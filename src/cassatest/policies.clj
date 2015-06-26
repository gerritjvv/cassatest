(ns cassatest.policies
  ^{:doc "Contains custom cassandra policy implementations
          create is a multimethod that creates the different policy instances using [type conf]"}
  (:require
    [clojurewerkz.cassaforte.policies :as cp])
  (:import (com.datastax.driver.core.policies RetryPolicy RetryPolicy$RetryDecision)
           (com.datastax.driver.core ConsistencyLevel)))

(declare retry-policy)
(set! *unchecked-math* true)

(defonce RETRY-POLICIES #{:default :downgrading-consistency :fallthrough :retry})

(defmulti create (fn [type conf] type))

;;create the default and all except :retry
(defmethod create :default [type _]
  (cp/retry-policy type))

;;create the retry custom policy
(defmethod create :retry [_ {:keys [read-attempts write-attempts unavailable-attempts]}]
  (retry-policy read-attempts write-attempts unavailable-attempts))

(defn retry-policy
  "Creates a retry policy that will
   onReadTimeout: retry read-attempts times
   onWriteTimeout: retry write-attempts times
   onUnavailable: retry unavailable-attempts times"
  [read-attempts write-attempts unavailable-attempts]
  {:pre [(number? read-attempts) (number? write-attempts) (number? unavailable-attempts)]}
  (reify RetryPolicy

    (onReadTimeout [_ stmnt cl required-responses received-responses dataReceived rtime]
      (cond
        dataReceived                          (RetryPolicy$RetryDecision/ignore)
        (< rtime (long read-attempts)) (RetryPolicy$RetryDecision/retry ^ConsistencyLevel cl)
        :default                              (RetryPolicy$RetryDecision/rethrow)))

    (onWriteTimeout [_ stmnt cl wt required-responses received-responses wtime]
      (if (< wtime (long write-attempts))
        (RetryPolicy$RetryDecision/retry ^ConsistencyLevel cl)
        (RetryPolicy$RetryDecision/rethrow)))

    (onUnavailable [_ stmnt cl required-responses received-responses utime]
      (if (< utime (long unavailable-attempts))
        (RetryPolicy$RetryDecision/retry ^ConsistencyLevel cl)
        (RetryPolicy$RetryDecision/rethrow)))))
