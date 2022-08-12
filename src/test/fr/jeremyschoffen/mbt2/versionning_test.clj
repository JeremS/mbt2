(ns fr.jeremyschoffen.mbt2.versionning-test
  (:require
    [clojure.test :refer [deftest is testing use-fixtures]]
    [clojure.tools.build.api :as b]
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.versioning :as v]
    [fr.jeremyschoffen.mbt2.common-test :as ct]))



(use-fixtures :each ct/test-repo!)

(deftest git-tags
  (testing "first tag"
    (do
      (ct/create-example-file! "f1.txt" "f1")
      (git/add-all! ct/basic-git-process-arg)
      (ct/commit! "Added f1.")
      (v/tag! ct/basic-git-process-arg))

    (is (= (:git/tag (ct/latest-release)) "v1")))

  (testing "second tag"
    (do
      (ct/create-example-file! "f2.txt" "f2")
      (git/add-all! ct/basic-git-process-arg)
      (ct/commit! "Added f2.")

      (ct/create-example-file! "f3.txt" "f3")
      (git/add-all! ct/basic-git-process-arg)
      (ct/commit! "Added f3.")
      (v/tag! ct/basic-git-process-arg))

    (is (= (:git/tag (ct/latest-release)) "v3"))))


(comment
  (clojure.test/run-tests)
  (ct/describe)
  (ct/latest-release)

  (git-tags)
  (ct/setup-test-repo!)
  (ct/tear-test-repo-down!))




