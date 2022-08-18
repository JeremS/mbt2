(ns fr.jeremyschoffen.mbt2.prose.tags
  (:require
    [fr.jeremyschoffen.prose.alpha.document.lib :as l]
    [fr.jeremyschoffen.prose.alpha.out.markdown.tags :as md]
    [fr.jeremyschoffen.mbt2.versioning :as v]))


(defn clojure-block [& args]
  (apply md/code-block {:content-type "clojure"}
    args))


(defn coord->str [coord]
  (binding [*print-namespace-maps* false]
    (str coord)))


(defn git-coordinates
  "Tag displaying the git coordinates of the library. coordinates are found under the key
  `:git-coord` of the document's inputs."
  []
  (clojure-block
    (-> (l/get-input)
        :git-coord
        coord->str)))

(comment
  (require '[fr.jeremyschoffen.prose.alpha.eval.common :as e])
  (e/bind-env {:prose.alpha.document/input {:git-coord {'toto/titi {:git/sha "uriopaurieop"}}}}
    (git-coordinates)))


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



