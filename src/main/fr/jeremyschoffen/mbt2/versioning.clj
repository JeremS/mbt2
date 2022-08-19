(ns fr.jeremyschoffen.mbt2.versioning
  (:require
    [clojure.tools.build.api :as b]
    [fr.jeremyschoffen.mbt2.git :as git]
    [lambdaisland.regal :as regal]))

(def default-min-sha-length 10)

(def default-version-tag-prefix "v")


(defn new-version-number
  "Alias to [[clojure.tools.build/git-count-revs]].

  Same options as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (b/git-count-revs opts))


(defn new-tag-name
  "Generate a new tag based on optional `:prefix` and [[new-version-number]].

  Options:
  - `:prefix`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:keys [prefix]
      :or {prefix default-version-tag-prefix}
      :as opts}]
  (str prefix (new-version-number opts)))


(defn tag!
  "Tag the current commit with a new vesion.

  Options:
  - `:prefix`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (-> opts
      (assoc :tag-name (new-tag-name opts))
      git/tag!))




(defn- make-describe-command [{:keys [prefix min-sha-length]
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
     [:+ :non-whitespace]]))


(defn parse-tag-name-from-description [desc opts]
  (let [[_ tag-name] (re-matches (make-desc-regex opts) desc)]
    tag-name))


(defn last-tag
  "Get the name of the tag created last.

  Options:
  - `:prefix`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (-> opts
      describe
      (parse-tag-name-from-description opts)))


(defn ref-for-tag [& {:keys [tag-name min-sha-length]
                      :or {min-sha-length default-min-sha-length}
                      :as opts}]
  (-> opts
      (assoc :git-args ["show-ref" (format "--abbrev=%d" min-sha-length) tag-name])
      b/git-process))


(def ref-regex
  (regal/regex
    [:cat
     [:capture [:+ :non-whitespace]]
     \space
     [:+ :non-whitespace]]))


(defn parse-commit-from-ref [ref]
  (let [[_ commit] (re-matches ref-regex ref)]
    commit))


(defn commit-from-tag
  "Get a commit from a tag name.

  Args:
  - `:tag-name`
  Options:
  - `:min-sha-length`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (-> opts
      ref-for-tag
      parse-commit-from-ref))


(defn release-for-tag
  "Get the map part of git coordinates of the lib for a given tag.

  Args:
  - `:tag-name`
  Options:
  - `:min-sha-length`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:keys [tag-name] :as opts}]
  {:git/tag tag-name
   :git/sha (commit-from-tag opts)})


(defn get-latest-release
  "Get the map part of git coordinates of the lib for the tag created last.

  Options:
  - `:prefix`
  - `:min-sha-length`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (when-let [tag-name (last-tag opts)]
    (-> opts
        (assoc :tag-name tag-name)
        release-for-tag)))


(defn latest-git-coord
  "Get the git coordinates of the lib for the latest tag.

  Args:
  - `:lib-name`
  Options:
  - `:prefix`
  - `:min-sha-length`
  - Same as [[clojure.tools.build/git-process]]."
  [& {:keys [lib-name] :as opts}]
  {lib-name (get-latest-release opts)})


