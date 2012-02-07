(ns ring.server.test.standalone
  (:require [clj-http.client :as http])
  (:use clojure.test
        ring.server.standalone
        ring.util.response))

(defmacro with-server [server & body]
  `(let [server# ~server]
     (try
       ~@body
       (finally (.stop server#)))))

(deftest serve-test
  (let [handler (constantly (response "Hello World"))]
    (with-server (serve handler {:join? false})
      (let [resp (http/get "http://localhost:5000")]
        (is (= (:status resp) 200))
        (is (= (:body resp) "Hello World"))))))
