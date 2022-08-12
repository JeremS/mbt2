(ns fr.jeremyschoffen.mbt2.git
  (:require
    [clojure.string :as string]
    [clojure.tools.build.api :as b]))


(defn status [& {:as opts}]
  (-> opts
      (assoc :git-args ["status" "--porcelain"])
      b/git-process))

(defn clean-repo? [& {:as opts}]
  (if-let [st (status opts)]
    (-> st
        string/split-lines
        count
        (= 0))
    true))


(defn add! [& {:keys [git-add-paths] :as opts}]
  (-> opts
      (assoc :git-args (into ["add"] (map str) git-add-paths))
      b/git-process))


(defn add-all! [& {:as opts}]
  (-> opts
      (assoc :git-args ["add" "-A"])
      b/git-process))


(defn commit! [& {:keys [commit-msg] :as opts}]
  (-> opts
      (assoc :git-args ["commit" "-m" commit-msg])
      (b/git-process)))


(defn tag! [& {:keys [tag-name tag-msg] :as opts}]
  (let [tag-cmd (if tag-msg
                  ["tag" "-a" tag-name "-m" tag-msg]
                  ["tag" tag-name])]
    (-> opts
        (assoc :git-args tag-cmd)
        b/git-process)))



(def default-min-sha-length 10)

(def default-version-tag-prefix)

(defn make-describe-args [{:keys [prefix min-sha-length]
                           :or {min-sha-length default-min-sha-length
                                prefix default-version-tag-prefix}}]
  ["describe" "--long"
   "--match" (str prefix "*.*")
   (format "--abbrev=%d" min-sha-length)
   "--dirty=-DIRTY"
   "--always"])


(defn describe [& {:as opts}]
  (-> opts
      (assoc :git-args (make-describe-args opts))
      b/git-process))


(comment
  (clean-repo?)
  (make-describe-args {})
  (describe)
  (require '[clojure.repl])
  (clojure.repl/doc b/git-count-revs))
