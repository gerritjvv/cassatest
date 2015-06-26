(ns cassatest.cassatest-test
  (:require [clojure.test :refer :all]
            [cassatest.cassatest :as c]
            [clojurewerkz.cassaforte.policies :as cp])
  (:import (com.datastax.driver.core.policies RetryPolicy)))


(deftest test-arguments []
                        (let [{{:keys [
                                       hosts
                                       threads
                                       thread-rate-limit
                                       iterations
                                       query
                                       consistency
                                       duration
                                       retry
                                       params] :as opts} :options errors :errors}
                              (c/parse-options ["--hosts=a,b,c" "--threads=10" "--thread-rate-limit=100" "--query=select * from table where a = {myvar}" "--params={:myvar {:type :constant :v 1}}"
                                                "--iterations=10" "--consistency=all"
                                                "--duration=10" "--retry=default"
                                                "--retry=retry"])]
                          (is (not errors))
                          (prn opts)
                          (is (= hosts ["a" "b" "c"]))
                          (is (= iterations 10))
                          (is (= threads 10))
                          (is (= thread-rate-limit 100))
                          (is (= query "select * from table where a = {myvar}"))
                          (is (= consistency :all))
                          (is (= duration 10))
                          (is (fn? retry))
                          (is (instance? RetryPolicy (retry {:read-attempts 1 :write-attempts 1 :unavailable-attempts 1})))
                          (is (fn? (:myvar params)))))
