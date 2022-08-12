(ns fr.jeremyschoffen.mbt2.git-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer [deftest is testing use-fixtures]]
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.common-test :as ct]))

;; ------------------------------------------------------------------------------
;; Fixtures
;; ------------------------------------------------------------------------------
(use-fixtures :each ct/test-repo!)
;; ------------------------------------------------------------------------------
;; Tests
;; ------------------------------------------------------------------------------
(deftest git-add-commit
  (testing "At init"
    (is (ct/clean-repo?)))

  (testing "status after creating files"
    (ct/create-example-file! "f1.txt" "f1")
    (ct/create-example-file! "f2.txt" "f2")

    (is (not (ct/clean-repo?)))
    (is (= (ct/repo-status)
           #{{"f1.txt" "??"}
             {"f2.txt" "??"}})))

  (testing "status after adding files"
    (git/add! (assoc ct/basic-git-process-arg :git-add-paths ["f1.txt" "f2.txt"]))

    (is (not (ct/clean-repo?)))
    (is (= (ct/repo-status)
           #{{"f1.txt" "A"} {"f2.txt" "A"}})))

  (testing "status after adding files"
    (ct/commit! "Added f1 & f2.")

    (is (ct/clean-repo?))
    (is (= (ct/repo-status) #{})))

  (testing "testing add add-all"
    (ct/create-example-file! "f3.txt" "f3")
    (ct/create-example-file! "f4.txt" "f4")
    (git/add-all! ct/basic-git-process-arg)


    (is (not (ct/clean-repo?)))
    (is (= (ct/repo-status)
           #{{"f3.txt" "A"} {"f4.txt" "A"}}))


    (ct/commit! "Added f1 & f2.")

    (is (ct/clean-repo?))
    (is (= (ct/repo-status) #{}))))


;; ------------------------------------------------------------------------------

(deftest git-tags
  (testing "first tag"
    (do
      (ct/create-example-file! "f1.txt" "f1")
      (git/add-all! ct/basic-git-process-arg)
      (ct/commit! "Added f1.")
      (ct/tag! :tag-name "0.1"))

    (is (string/starts-with? (ct/show "0.1") "commit")))

  (testing "second tag"
    (do
      (ct/create-example-file! "f2.txt" "f2")
      (git/add-all! ct/basic-git-process-arg)
      (ct/commit! "Added f2.")
      (ct/tag! :tag-name "0.2" :tag-msg "version 0.2"))

    (is (string/starts-with? (ct/show "0.2") "tag"))))


(comment
  (clojure.test/run-tests)

  (setup-test-repo!)
  (tear-test-repo-down!)

  (repo-status))

