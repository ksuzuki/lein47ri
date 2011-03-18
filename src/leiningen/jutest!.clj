(ns leiningen.jutest!
  "Run a project's tests after cleaning and fetching dependencies using jutest."
  (:refer-clojure :exclude [test])
  (:use [leiningen.clean :only [clean]]
        [leiningen.deps :only [deps]]
        [leiningen.jutest :only [jutest]]))

(defn jutest!
  "Run a project's tests after cleaning and fetching dependencies using jutest."
  [project & nses]
  (apply jutest (doto project clean deps) nses))
