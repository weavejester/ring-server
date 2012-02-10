(ns ring.server.test.standalone
  (:require [clj-http.client :as http])
  (:use clojure.test
        ring.server.standalone
        ring.util.environment
        ring.util.response))

(defmacro with-server [server & body]
  `(let [server# ~server]
     (try
       ~@body
       (finally (.stop server#)))))

(defn test-server [& [{:as options}]]
  (let [handler (constantly (response "Hello World"))]
    (serve handler (merge {:join? false} options))))

(defn is-server-running-on-port [port] 
  (let [resp (http/get (str "http://localhost:" port)
                       {:conn-timeout 1000})]
    (is (= (:status resp) 200))))

(deftest serve-test
  (testing "default port"
    (with-server (test-server)
      (is-server-running-on-port 3000)))
  (testing "fallback default ports"
    (with-server (test-server)
      (with-server (test-server)
        (is-server-running-on-port 3000)
        (is-server-running-on-port 3001))))
  (testing "PORT environment variable"
    (with-env {"PORT" "4563"}
      (with-server (test-server) 
        (is-server-running-on-port 4563))))
  (testing ":port option"
    (with-server (test-server {:port 5463})
      (is-server-running-on-port 5463))))
