(ns build
  (:require
    [fr.jeremyschoffen.mbt2.core :as mbt]))


(def lib-name 'io.github.jerems/mbt2)


(defn latest-git-coord []
  (mbt/latest-git-coord :lib-name lib-name))


(defn make-readme []
  (mbt/generate-md-doc "README.md.prose" {:git-coord (latest-git-coord)}))


(defn generate-readme! []
  (spit "README.md" (make-readme)))


(defn generate-docs! []
  ;; generate stuff
  (generate-readme!)
  (mbt/git-add-all!)
  (mbt/git-commit! :commit-msg "Generated docs."))



(defn release! []
  (mbt/assert-clean-repo)
  (mbt/tag-release!)
  (generate-docs!))


(comment
  (println (make-readme))
  (generate-readme!)
  (release!))
