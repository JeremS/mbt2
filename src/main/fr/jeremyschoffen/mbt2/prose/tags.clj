(ns fr.jeremyschoffen.mbt2.prose.tags
  (:require
    [fr.jeremyschoffen.prose.alpha.document.lib :as l]
    [fr.jeremyschoffen.prose.alpha.out.markdown.tags :as md]
    [fr.jeremyschoffen.mbt2.versioning :as v]))

(defn coord->str [coord]
  (binding [*print-namespace-maps* false]
    (str coord)))



(defn version []
  (md/code-block {:content-type "clojure"}
    (->> (l/get-input)
         :lib-name
         (v/latest-git-coord :lib-name)
         coord->str)))



(comment
  (require '[fr.jeremyschoffen.prose.alpha.eval.common :as e])
  (e/bind-env {:prose.alpha.document/input {:project-version "version 1"}}
    (version)))
