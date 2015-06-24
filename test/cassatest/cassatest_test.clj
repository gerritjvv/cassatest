(ns cassatest.cassatest-test
  (:require [clojure.test :refer :all]
            [cassatest.cassatest :as c]))


(deftest test-arguments []
                        (let [{{:keys [
                                       hosts
                                       threads
                                       thread-rate-limit
                                       iterations
                                       query
                                       params] :as opts} :options errors :errors}
                              (c/parse-options ["--hosts=a,b,c" "--threads=10" "--thread-rate-limit=100" "--query=select * from table where a = {myvar}" "--params={:myvar {:type :constant :v 1}}"
                                                "--iterations=10"])]
                          (is (not errors))
                          (prn opts)
                          (is (= hosts ["a" "b" "c"]))
                          (is (= iterations 10))
                          (is (= threads 10))
                          (is (= thread-rate-limit 100))
                          (is (= query "select * from table where a = {myvar}"))
                          (is (fn? (:myvar params)))))
