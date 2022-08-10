(ns fr.jeremyschoffen.mbt2.git
  (:require
    [clojure.string :as string]
    [clojure.tools.build.api :as b]))

(def default-min-sha-length 10)

(def status-args ["status" "--porcelain"])

(defn status [& {:as arg}]
  (-> arg
      (assoc :git-args status-args)
      b/git-process))

(defn clean-repo? [& {:as arg}]
  (if-let [st (status arg)]
    (-> st
        string/split-lines
        count
        (= 0))
    true))

(defn make-describe-args [{:keys [prefix min-sha-length]
                           :or {min-sha-length default-min-sha-length}}]
  ["describe" "--long"
   "--match" (str prefix "*.*")
   (format "--abbrev=%d" min-sha-length)
   "--dirty=-DIRTY"
   "--always"])


(defn describe [& {:as arg}]
  (-> arg
      (assoc :git-args (make-describe-args arg))
      b/git-process))


(defn add! [& {:keys [git-add-paths] :as arg}]
  (-> arg
      (assoc :git-args (into ["add"] (map str) git-add-paths))
      b/git-process))


(defn add-all! [& {:as arg}]
  (-> arg
      (assoc :git-args ["add" "-A"])
      b/git-process))


(defn commit! [& {:keys [commit-msg] :as arg}]
  (-> arg
      (assoc :git-args ["commit" "-m" commit-msg])
      (b/git-process)))


(defn tag! [& {:keys [tag-name tag-msg] :as arg}]
  (let [tag-cmd (if tag-msg
                  ["tag" "-a" tag-name "-m" tag-msg]
                  ["tag" tag-name])]
    (-> arg
        (assoc :git-args tag-cmd)
        b/git-process)))



(comment
  (clean-repo?)
  (make-describe-args {})
  (describe)
  (require '[clojure.repl])
  (clojure.repl/doc b/git-count-revs))
