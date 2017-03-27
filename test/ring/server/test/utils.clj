(ns ring.server.test.utils
  "Utility functions for running unit tests"
  (:require [clj-http.client :as http])
  (:use clojure.test
        ring.util.response))

(defmacro with-server [server & body]
  `(let [server# ~server]
     (try
       ~@body
       (finally (.stop server#)))))

(defn default-handler [req]
  (response "Hello World"))

(defn http-get [port uri & {:keys [timeout] :or {timeout 1000}}]
  (http/get (str "http://localhost:" port uri)
            {:conn-timeout timeout
             :throw-exceptions false}))

(defn is-server-running-on-port [port] 
  (let [resp (http-get port "")]
    (is (= (:status resp) 200))))

(defn is-server-not-running-on-port [port]
  (is (thrown? Exception (http-get port "" :timeout 10))))
