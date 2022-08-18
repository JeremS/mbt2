(ns fr.jeremyschoffen.mbt2.common-test
  (:require
    [clojure.string :as string]
    [clojure.tools.build.api :as b]
    [fr.jeremyschoffen.java.nio.alpha.file :as fs]
    [fr.jeremyschoffen.mbt2.git :as git]
    [fr.jeremyschoffen.mbt2.versioning :as v]))

;; ------------------------------------------------------------------------------
;; Fixtures
;; ------------------------------------------------------------------------------
(def test-resources-dir (-> "."
                            fs/canonical-path
                            (fs/path "test-resources")))


(def test-repo-path (fs/path test-resources-dir "test-repo"))


(def basic-git-process-arg {:dir (str test-repo-path)})

(defn ensure-dir! [path]
  (if-not (fs/exists? path)
    (fs/create-directory! path)))

(defn git-init!
  "use :dir for the dir to init"
  [& {:as arg}]
  (-> arg
      (assoc :git-args ["init"])
      (b/git-process)))


(defn setup-test-repo! []
  (ensure-dir! test-resources-dir)
  (fs/create-directory! test-repo-path)
  (git-init! basic-git-process-arg))


(defn tear-test-repo-down! []
  (b/delete {:path (str test-repo-path)}))


(defn test-repo! [f]
  (setup-test-repo!)
  (f)
  (tear-test-repo-down!))


;; ------------------------------------------------------------------------------
;; Utils
;; ------------------------------------------------------------------------------
(defn create-example-file! [name content]
  (let [p (fs/path test-repo-path name)]
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

(defn tag! [& {:as args}]
  (-> basic-git-process-arg
      (merge args)
      (git/tag!)))

(defn show [v]
  (-> basic-git-process-arg
      (assoc :git-args ["show" v])
      b/git-process))


(defn describe [& {:as opts}]
  (-> basic-git-process-arg
      (merge opts)
      v/describe))


(defn latest-release []
  (-> basic-git-process-arg
      v/get-latest-release))


