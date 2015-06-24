(ns cassatest.generators-test
  (:require [clojure.test :refer :all]
            [cassatest.generators :as gens]))


(deftest test-int-range []
                        (let [f (gens/parse-generator {:type :int-range :from 2 :to 5})]
                          (dotimes [i 100]
                            (is (< 1 (f) 5)))))

(deftest test-constant []
                        (let [f (gens/parse-generator {:type :constant :v 10})]
                          (dotimes [i 100]
                            (is (= (f) 10)))))
