;; The only requirement of the project.clj file is that it includes a
;; defproject form. It can have other code in it as well, including
;; loading other task definitions.

;; !!! If you touch src/mavenant/LeinDependenciesTask.clj or get an
;; !!! exception like 'duplicate entry'...
;; Using lein 1.4 clean everything and remove files in src/mavenant
;; except LeinDependenciesTask.clj. AOT-compile then move the compiled
;; files in classes/mavenant to src/mavenant (so classes/mavenant
;; becomes empty).
;; Then you can use the latest lein to perform normal tasks.

(defproject lein47ri "1.5.1-RC1"
  :description "Lein for Sevenri"
;  :url "https://github.com/technomancy/leiningen"
  :license {:name "Eclipse Public License"}
  :dependencies [[lancet "1.0.0"]
                 [robert/hooke "1.1.0"]
                 [org.apache.maven/maven-ant-tasks "2.0.10" :exclusions [ant]]]
  :dev-dependencies [[org.clojure/clojure "1.2.1"]
                     [org.clojure/clojure-contrib "1.2.0"]]
  :aot [mavenant.LeinDependenciesTask]
;  :eval-in-leiningen true
  :disable-implicit-clean true)
