(ns fr.jeremyschoffen.mbt2.core
  (:require
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.prose :as p]
    [fr.jeremyschoffen.mbt2.versioning :as v]))


(defn assert-clean-repo [& {:as arg}]
  (when-not (git/clean-repo? arg)
    (throw (ex-info "Dirty repo." {}))))


(defn git-add! [& {:as opts}]
  (git/add! opts))


(defn git-add-all! [& {:as opts}]
  (git/add-all! opts))


(defn git-commit! [& {:as opts}]
  (git/commit! opts))


(defn tag-release! [& {:as opts}]
  (v/tag! opts))


(defn latest-git-coord [& {:as opts}]
  (v/latest-git-coord opts))


(defn generate-md-doc [src inputs]
  (p/generate-md-doc src inputs))
