;; The only requirement of the project.clj file is that it includes a
;; defproject form. It can have other code in it as well, including
;; loading other task definitions.

(defproject lein47ri "1.5.0-RC1"
  :description "Lein for Sevenri"
;  :url "https://github.com/technomancy/leiningen"
  :license {:name "Eclipse Public License"}
  :dependencies [[lancet "1.0.0"]
                 [robert/hooke "1.1.0"]
                 [org.apache.maven/maven-ant-tasks "2.0.10" :exclusions [ant]]]
  :dev-dependencies [[org.clojure/clojure "1.2.1"]
                     [org.clojure/clojure-contrib "1.2.0"]]
  :disable-implicit-clean true
  :eval-in-leiningen true)
