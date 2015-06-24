(ns cassatest.generators-test
  (:require [clojure.test :refer :all]
            [cassatest.generators :as gens])
  (:import (java.util UUID)))


(deftest test-int-range []
                        (let [f (gens/parse-generator {:type :int-range :from 2 :to 5})]
                          (dotimes [i 100]
                            (is (< 1 (f) 5)))))

(deftest test-constant []
                        (let [f (gens/parse-generator {:type :constant :v 10})]
                          (dotimes [i 100]
                            (is (= (f) 10)))))



(deftest test-constant []
                       (let [f (gens/parse-generator {:type :uuid})]
                         (dotimes [i 100]
                           (is (instance? UUID (f))))))


(deftest test-constant []
                       (let [f (gens/parse-generator {:type :rand-chars :length 4})]
                         (dotimes [i 100]
                           (is (string? (f)))
                           (is (= (count (f)) 4)))))