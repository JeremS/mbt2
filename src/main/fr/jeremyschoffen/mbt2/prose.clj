(ns fr.jeremyschoffen.mbt2.prose
  (:require
    [fr.jeremyschoffen.prose.alpha.document.clojure :as d]
    [fr.jeremyschoffen.prose.alpha.out.markdown.compiler :as c]))


(def eval-doc (d/make-evaluator))

(defn generate-md-doc [src arg]
  (-> src
      (eval-doc arg)
      c/compile!))

(comment
  (-> "README.md.prose"
      (eval-doc {:lib-name 'toto/titi})
      c/compile!
      println))

