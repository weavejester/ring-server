(ns ring.server.test.leiningen
  (:use clojure.test
        ring.server.leiningen
        ring.server.test.utils))

(def project-clj
  `{:ring {:handler default-handler}})

(deftest serve-test
  (testing "basic project.clj"
    (with-server (serve project-clj)
      (is-server-running-on-port 3000))))
