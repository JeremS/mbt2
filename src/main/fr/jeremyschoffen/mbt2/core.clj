(ns fr.jeremyschoffen.mbt2.core
  (:require
    [fr.jeremyschoffen.mbt2.git :as git]))


(defn assert-clean-repo [& {:as arg}]
  (when-not (git/clean-repo? arg)
    (throw (ex-info "Dirty repo." {}))))


(defn tag-release! [{:keys [version] :as arg}]
  (-> arg
      (assoc :tag-name version)
      git/tag!)
  arg)

