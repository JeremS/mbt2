(ns fr.jeremyschoffen.mbt2.git
  (:require
    [clojure.string :as string]
    [clojure.tools.build.api :as b]))


(defn status
  "Exec \"git status --porcelain\".

  Same options as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (-> opts
      (assoc :git-args ["status" "--porcelain"])
      b/git-process))

(defn clean-repo?
  "Test is the repo is clean (Status blank).

  Same options as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (if-let [st (status opts)]
    (-> st
        string/split-lines
        count
        (= 0))
    true))


(defn add!
  "Exec \"git add\" on the vector of paths under the key `:git-add-paths`.

  Same options as [[clojure.tools.build/git-process]]."
  [& {:keys [git-add-paths] :as opts}]
  (-> opts
      (assoc :git-args (into ["add"] (map str) git-add-paths))
      b/git-process))


(defn add-all!
  "Exec \"git add -A\".

  Same options as [[clojure.tools.build/git-process]]."
  [& {:as opts}]
  (-> opts
      (assoc :git-args ["add" "-A"])
      b/git-process))


(defn commit!
  "Exec \"git commit -m\". The message is passed undef the key `:commit-msg`.

  Same options as [[clojure.tools.build/git-process]]."
  [& {:keys [commit-msg] :as opts}]
  (-> opts
      (assoc :git-args ["commit" "-m" commit-msg])
      (b/git-process)))


(defn tag!
  "Exec \"git tag\" using the name under `:tag-name`.
  An optional message can be passed under the key `:tag-msg`.

  Same options as [[clojure.tools.build/git-process]]"
  [& {:keys [tag-name tag-msg] :as opts}]
  (let [tag-cmd (if tag-msg
                  ["tag" "-a" tag-name "-m" tag-msg]
                  ["tag" tag-name])]
    (-> opts
        (assoc :git-args tag-cmd)
        b/git-process)))



(comment
  (clean-repo?)
  (require '[clojure.repl])
  (clojure.repl/doc b/git-count-revs))
