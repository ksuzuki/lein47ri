(ns leiningen.jutest
  "Run the project's tests and report the results in the junit compatible XML format."
  (:refer-clojure :exclude [test])
  (:use [leiningen.core :only [*exit* eval-in-lein *test-summary*]]
        [leiningen.test :exclude [form-for-testing-namespaces test]]
        [leiningen.compile :only [eval-in-project]])
  (:import (java.io File)))

(defn form-for-testing-namespaces
  "Return a form that when eval'd in the context of the project will test
each namespace and print an overall summary."
  ([namespaces result-file & [selectors]]
     `(do
        (doseq [n# '~namespaces]
          (require n# :reload))
        ~(form-for-hook-selectors selectors)
        (let [summary# (binding [clojure.test/*test-out* *out*]
                         (~'clojure.test.junit/with-junit-output
                           (apply ~'clojure.test/run-tests '~namespaces)))]
          (when-not (= "1.5" (System/getProperty "java.specification.version"))
            (shutdown-agents))
          ;; Stupid ant won't let us return anything, so write results to disk
          (with-open [w# (-> (java.io.File. ~result-file)
                             (java.io.FileOutputStream.)
                             (java.io.OutputStreamWriter.))]
            (.write w# (pr-str summary#)))
          (when (and ~*exit-after-tests* ~*exit*)
            (System/exit 0))))))

(defn jutest
  "Run the project's tests. Accepts either a list of test namespaces to run or
a list of test selectors. With no arguments, runs all tests. Report the results
in the junit compatible XML format."
  [project & tests]
  (when (eval-in-lein (:eval-in-leiningen project))
    (require '[clojure walk template stacktrace])
    (require 'clojure.test)
    (require 'clojure.test.junit))
  (let [[nses selectors] (read-args tests project)
        result (File/createTempFile "lein" "result")]
    (eval-in-project project (form-for-testing-namespaces
                              nses (.getAbsolutePath result) (vec selectors))
                     nil nil `(do (require '~'clojure.test)
                                  (require '~'clojure.test.junit)
                                  ~(when (seq selectors)
                                     '(require 'robert.hooke))))
    (if (and (.exists result) (pos? (.length result)))
      (let [summary (read-string (slurp (.getAbsolutePath result)))
            success? (zero? (+ (:error summary) (:fail summary)))]
        (.delete result)
        (when *test-summary*
          (reset! *test-summary* summary))
        (if success? 0 1))
      1)))
