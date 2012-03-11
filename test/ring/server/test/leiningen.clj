(ns ring.server.test.leiningen
  (:use clojure.test
        ring.server.leiningen
        ring.server.test.utils))

(def basic-project-clj
  `{:ring {:handler default-handler
           :adapter {:join? false}
           :open-browser? false}})

(deftest serve-test
  (testing "basic project.clj"
    (with-server (serve basic-project-clj)
      (is-server-running-on-port 3000))))
