(ns fr.jeremyschoffen.mbt2.core
  (:require
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.prose :as p]
    [fr.jeremyschoffen.mbt2.versioning :as v]

    [fr.jeremyschoffen.mbt2.utils :as u]))

(defn assert-clean-repo
  "Assert that a repo is clean (status empty) or throw.

  Same options as [[clojure.tools.build.api/git-process]]."
  [& {:as arg}]
  (when-not (git/clean-repo? arg)
    (throw (ex-info "Dirty repo." {}))))


(defn git-add! [& {:as opts}]
  (git/add! opts))

(u/transfer-doc 'git/add! 'git-add!)


(defn git-add-all! [& {:as opts}]
  (git/add-all! opts))

(u/transfer-doc 'git/add-all! 'git-add-all!)


(defn git-commit! [& {:as opts}]
  (git/commit! opts))

(u/transfer-doc 'git/commit! 'git-commit!)


(defn tag-release! [& {:as opts}]
  (v/tag! opts))

(u/transfer-doc 'v/tag! 'tag-release!)


(defn latest-git-coord [& {:as opts}]
  (v/latest-git-coord opts))

(u/transfer-doc 'v/latest-git-coord 'latest-git-coord)


(defn generate-md-doc [src inputs]
  (p/generate-md-doc src inputs))

(u/transfer-doc 'p/generate-md-doc 'generate-md-doc)

