(ns leiningen.version
  "Print version for Leiningen and the current JVM.")

(defn version
  "Print version for Leiningen and the current JVM."
  []
  (println "Lein47ri 1.5.0-RC1"
           "on Java" (System/getProperty "java.version")
           (System/getProperty "java.vm.name")))
