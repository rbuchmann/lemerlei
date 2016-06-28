(ns lemerlei.core-test
  (:require [cheshire.core     :as cheshire]
            [clojure.test      :refer :all]
            [lemerlei.core     :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest health-test
  (testing "Test GET request to health resource returns expected response"
    (let [response (app (-> (mock/request :get "/health")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (:status body) "OK")))))
