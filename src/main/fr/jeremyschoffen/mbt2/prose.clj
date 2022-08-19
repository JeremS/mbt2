(ns fr.jeremyschoffen.mbt2.prose
  (:require
    [fr.jeremyschoffen.prose.alpha.document.clojure :as d]
    [fr.jeremyschoffen.prose.alpha.out.markdown.compiler :as c]))


(def eval-doc (d/make-evaluator))

(defn generate-md-doc
  "Execute a prose file generating markdown text.

  Args:
  - `src`: string path the the prose file (must be a resource in the classpath.)
  - `arg`: data to give the prose file as input."
  [src arg]
  (-> src
      (eval-doc arg)
      c/compile!))

(comment
  (-> "README.md.prose"
      (eval-doc {:lib-name 'toto/titi})
      c/compile!
      println))

