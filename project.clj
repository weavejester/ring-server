(defproject ring-server "0.2.6-SNAPSHOT"
  :description "Library for running Ring web servers"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/core.incubator "0.1.0"]
                 [ring "1.2.0-SNAPSHOT"]
                 [ring-refresh "0.1.1"]]
  :plugins [[codox "0.6.1"]]
  :profiles {:dev {:dependencies [[clj-http "0.4.1"]]}})
