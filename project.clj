(defproject ring-server "0.3.1"
  :description "Library for running Ring web servers"
  :url "https://github.com/weavejester/ring-server"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/core.incubator "0.1.0"]
                 [ring "1.2.1"]
                 [ring-refresh "0.1.2"]]
  :plugins [[codox "0.6.6"]]
  :profiles {:dev {:dependencies [[clj-http "0.4.1"]]}})
