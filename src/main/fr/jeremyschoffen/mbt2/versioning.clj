(ns fr.jeremyschoffen.mbt2.versioning
  (:require
    [clojure.tools.build.api :as b]
    [fr.jeremyschoffen.mbt2.git :as git]
    [lambdaisland.regal :as regal]))

(def default-min-sha-length 10)

(def default-version-tag-prefix "v")


(defn new-version-number [& {:as opts}]
  (b/git-count-revs opts))


(defn new-tag-name [& {:keys [prefix]
                       :or {prefix default-version-tag-prefix}
                       :as opts}]
  (str prefix (new-version-number opts)))


(defn tag! [& {:as opts}]
  (-> opts
      (assoc :tag-name (new-tag-name opts))
      git/tag!))




(defn make-describe-command [{:keys [prefix min-sha-length]
                              :or {min-sha-length default-min-sha-length
                                   prefix default-version-tag-prefix}}]
  ["describe" "--long" "--tags"
   "--match" (str prefix "**")
   (format "--abbrev=%d" min-sha-length)
   "--always"])


(defn describe [& {:as opts}]
  (-> opts
      (assoc :git-args (make-describe-command opts))
      b/git-process))


(defn make-desc-regex [{:keys [prefix]
                        :or {prefix default-version-tag-prefix}}]
  (regal/regex
    [:cat
     [:capture prefix [:+ :digit]]
     "-"
     [:+ :digit]
     "-g"
     [:capture [:+ :non-whitespace]]]))


(defn parse-description [desc opts]
  (let [[_ tag-name sha] (re-matches (make-desc-regex opts) desc)]
    {:git/tag tag-name
     :git/sha sha}))


(defn get-latest-release [& {:as opts}]
  (-> opts
      describe
      (parse-description opts)))


(defn latest-git-coord [& {:keys [lib-name]}]
  {lib-name (get-latest-release)})


