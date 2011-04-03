;; Hack to print messages to *out* on 'lein deps'. There must be a better way,
;; but...
;;
;; 'lein deps' uses maven-ant's DependenciesTask (DT) to install depencendies.
;; DT instantiates AntDownloadMonitor (ADM) which spits messages to System/out
;; in the execute method just before calling the doExecute method so that we
;; don't have a chance to replace it with *out* suvvy one before doExecute is
;; called.
;;
;; In order to workaround the issue, here we create a subclass of DT and
;; replace the ADM instance with *out* suvvy one in the overriding doExecute
;; method, which delegates message printing to the Project's log method which,
;; in turn, should print them to *out*.
;;
;; Also, a part of DT tasks is updating local repos when correpsonding remote
;; repos are updated. When updating, RepositoryMetadataManager (RMM) is used
;; to print status messages. Again, RMM spits messages to System/out by
;; default. We create an instance of RMM with *out* suvvy logger before the
;; updater requests RMM (so the one we created is used).

(ns mavenant.LeinDependenciesTask
  (:gen-class
   :extends org.apache.maven.artifact.ant.DependenciesTask
   :exposes-methods {getProject superGetProject doExecute superDoExecute}
   :main false))

(defn lein-adm ;; adm := AntDownloadMonitor
  [project]
  (let [adm (proxy [org.apache.maven.artifact.ant.AntDownloadMonitor] [])]
    (.setProject adm project)
    adm))

(defmacro pmti ;;= 'print message and throwable if'
  [prd pfx msg trb]
  `(when (proxy-super ~prd)
     (println (str ~pfx ~msg))
     (when-not (nil? ~trb)
       (.printStackTrace ~trb *out*))))

(defn lein-rmm-logger ;; rmm := RepositoryMetadataManager
  []
  (let [threshold org.codehaus.plexus.logging.Logger/LEVEL_INFO
        name "LeinRMMLogger"]
    (proxy [org.codehaus.plexus.logging.AbstractLogger] [threshold name]
      (debug
        ([m] (.debug this m nil))
        ([m t] (binding [*out* *err*] (pmti isDebugEnabled "[DEBUG] " m t))))
      (info
        ([m] (.info this m nil))
        ([m t] (pmti isInfoEnabled nil m t)))
      (warn
        ([m] (.warn this m nil))
        ([m t] (binding [*out* *err*] (pmti isWarnEnabled "[WARNING] " m t))))
      (error
        ([m] (.error this m nil))
        ([m t] (binding [*out* *err*] (pmti isErrorEnabled "[ERROR] " m t))))
      (fatalError
        ([m] (.fatalError this m nil))
        ([m t] (binding [*out* *err*] (pmti isFatalErrorEnabled "[FATAL ERROR] " m t))))
      (getChildLogger [n] this))))

(defn -doExecute
  [this]
  (let [wm-name org.apache.maven.artifact.manager.WagonManager/ROLE
        wagon-manager (.lookup this wm-name)
        rmm-name (.getName org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager)
        repo-metadata-manager (.lookup this rmm-name)]
    (.setDownloadMonitor wagon-manager (lein-adm (.superGetProject this)))
    (.enableLogging repo-metadata-manager (lein-rmm-logger))
    ;;
    (.superDoExecute this)))
