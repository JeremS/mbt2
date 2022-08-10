(ns fr.jeremyschoffen.mbt2.git-test
  (:require
    [clojure.string :as string]
    [clojure.test :refer [deftest is testing use-fixtures]]
    [clojure.tools.build.api :as b]
    [fr.jeremyschoffen.java.nio.alpha.file :as fs]
    [fr.jeremyschoffen.mbt2.git :as git]))

;; ------------------------------------------------------------------------------
;; Fixtures
;; ------------------------------------------------------------------------------
(def test-resources-dir (-> "."
                            fs/canonical-path
                            (fs/path "test-resources")))

(def test-repo-path (fs/path test-resources-dir "repo1"))


(def basic-git-process-arg {:dir (str test-repo-path)})


(defn git-init!
  "use :dir for the dir to init"
  [& {:as arg}]
  (-> arg
      (assoc :git-args ["init"])
      (b/git-process)))


(defn setup-test-repo! []
  (fs/create-directory! test-repo-path)
  (git-init! basic-git-process-arg))


(defn tear-test-repo-down! []
  (b/delete {:path (str test-repo-path)}))


(defn test-repo! [f]
  (setup-test-repo!)
  (f)
  (tear-test-repo-down!))

(use-fixtures :each test-repo!)


;; ------------------------------------------------------------------------------
;; Utils
;; ------------------------------------------------------------------------------
(defn create-example-file! [dir name content]
  (let [p (fs/path dir name)]
    (fs/create-file! p)
    (spit p content)))


(def status-line-regex #"(\S+)\s+(\S+)")


(defn parse-status [line]
  (let [[_ st n] (re-matches status-line-regex line)]
    {n st}))


(defn repo-status []
  (if-let [st (git/status basic-git-process-arg)]
    (->> st
         (string/split-lines)
       (map parse-status)
       set)
    #{}))


(defn clean-repo? []
  (git/clean-repo? basic-git-process-arg))


(defn commit! [msg]
  (-> basic-git-process-arg
      (assoc :commit-msg msg)
      git/commit!))



;; ------------------------------------------------------------------------------
;; Tests
;; ------------------------------------------------------------------------------
(deftest git-add-commit
  (testing "At init"
    (is (clean-repo?)))

  (testing "status after creating files"
    (create-example-file! test-repo-path "f1.txt" "f1")
    (create-example-file! test-repo-path "f2.txt" "f2")

    (is (not (clean-repo?)))
    (is (= (repo-status)
           #{{"f1.txt" "??"}
             {"f2.txt" "??"}})))

  (testing "status after adding files"
    (git/add! (assoc basic-git-process-arg :git-add-paths ["f1.txt" "f2.txt"]))

    (is (not (clean-repo?)))
    (is (= (repo-status)
           #{{"f1.txt" "A"} {"f2.txt" "A"}})))

  (testing "status after adding files"
    (commit! "Added f1 & f2.")

    (is (clean-repo?))
    (is (= (repo-status) #{})))

  (testing "testing add add-all"
    (create-example-file! test-repo-path "f3.txt" "f3")
    (create-example-file! test-repo-path "f4.txt" "f4")
    (git/add-all! basic-git-process-arg)


    (is (not (clean-repo?)))
    (is (= (repo-status)
           #{{"f3.txt" "A"} {"f4.txt" "A"}}))


    (commit! "Added f1 & f2.")

    (is (clean-repo?))
    (is (= (repo-status) #{}))))


;; ------------------------------------------------------------------------------
(defn tag! [& {:as args}]
  (-> basic-git-process-arg
      (merge args)
      (git/tag!)))

(defn show [v]
  (-> basic-git-process-arg
      (assoc :git-args ["show" v])
      b/git-process))


(deftest git-tags
  (testing "first tag"
    (create-example-file! test-repo-path "f1.txt" "f1")
    (git/add-all! basic-git-process-arg)
    (commit! "Added f1.")
    (tag! :tag-name "0.1")
    (is (string/starts-with? (show "0.1") "commit")))

  (testing "second tag"
    (create-example-file! test-repo-path "f2.txt" "f2")
    (git/add-all! basic-git-process-arg)
    (commit! "Added f2.")
    (tag! :tag-name "0.2" :tag-msg "version 0.2")
    (is (string/starts-with? (show "0.2") "tag"))))


(comment
  (clojure.test/run-tests)

  (git-tags)
  (setup-test-repo!)
  (tear-test-repo-down!)

  (repo-status))

