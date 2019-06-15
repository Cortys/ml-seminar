(defproject ml-seminar "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [aysylu/loom "1.0.2"]]

  :profiles {:dev {:source-paths ["dev" "src" "test"]
                   :dependencies [[org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [proto-repl "0.3.1"]
                                  [proto-repl-charts "0.3.2"]
                                  [com.gfredericks/debug-repl "0.0.10"]]
                   :repl-options {:init-ns user
                                  :init (start)
                                  :nrepl-middleware
                                  [com.gfredericks.debug-repl/wrap-debug-repl]}}})
