(ns cassatest.exec
  (:require [clojure.tools.logging :refer [error info]])
  (:import (java.util.concurrent Executors CountDownLatch ExecutorService)
           (com.google.common.util.concurrent RateLimiter)))


(defn rate-limited
  "f must be (fn [state] (fn [] do things here))
   Calls f's return function iterations times and limit the rate at which its called via rate-limit
   Returns (fn [state] (fn [] ))"
  [rate-limit iterations f]
  {:pre [(number? rate-limit) (number? iterations) (fn? f)]}
  (fn [state]
    (let [^RateLimiter limiter (RateLimiter/create (double rate-limit))
          f2 (f state)]
      (fn []
        (dotimes [_ iterations]
          (.acquire limiter)
          (f2))))))

(defn submit
  "Runs f in a try catch"
  [^ExecutorService exec f]
  (.submit exec ^Runnable (fn [] (try (f) (catch Exception e (do (.printStackTrace e) (error e e)))))))

(defn via-threads!
  "Run f in thread-count threads (fn [state ] (fn [] run code ))
   f when called with state must return a function that can be called without arguments
   returns a delay which can be used to wait on all of the threads to complete"
  [state thread-count f]
  {:pre [(associative? state) (number? thread-count) (fn? f)]}

  (let [exec (Executors/newCachedThreadPool)
        ^CountDownLatch latch (CountDownLatch. thread-count)]
    (dotimes [_ thread-count]
      (let [f2 (f state)]
        (submit exec #(try (f2) (finally (.countDown latch))))))

    (delay (.await latch))))