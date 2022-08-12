(ns fr.jeremyschoffen.mbt2.core
  (:require
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.versioning :as v]))


(defn assert-clean-repo [& {:as arg}]
  (when-not (git/clean-repo? arg)
    (throw (ex-info "Dirty repo." {}))))


(defn tag-release! [& {:as opts}]
  (v/tag! opts))

