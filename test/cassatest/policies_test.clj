(ns cassatest.policies-test
  (:require [clojure.test :refer :all]
            [cassatest.policies :as policies])
  (:import (com.datastax.driver.core.policies RetryPolicy)))


(deftest test-create-policies []
                              (is (instance? RetryPolicy (policies/create :default nil)))
                              (is (instance? RetryPolicy (policies/create :retry {:read-attempts 1 :write-attempts 1 :unavailable-attempts 1}))))
