(defproject ring-server "0.2.1"
  :description "Library for running Ring web servers"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/core.incubator "0.1.0"]
                 [ring "1.0.2"]]
  :profiles {:dev {:dependencies [[clj-http "0.3.1"]
                                  [codox "0.4.0"]]}})
