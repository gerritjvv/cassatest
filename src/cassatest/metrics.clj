(ns cassatest.metrics
  (:require [metrics.core :refer [new-registry]]
            [metrics.meters :as meter]
            [metrics.counters :as counter]
            [metrics.reporters.console :as console]))


(defn- start-reporting! [reg reporting-type]
  (case reporting-type
    :stdout (console/start (console/reporter reg {}) 5)
    (RuntimeException. (str "reporter " reporting-type " not supported"))))


(defn start
  "Start the metrics and return a context object that should be used when calling mark-query! etc"
  [{:keys [reporting] :or {reporting :stdout}}]
  (let [reg (new-registry)]
    (start-reporting! reg reporting)
    {:queries-meter (meter/meter reg "queries-meter") :queries-counter (counter/counter reg "queries-counter")}))

(defn mark-query!
  "Mark the query metrics: meter and counter by one"
  [{:keys [queries-meter queries-counter]}]
  (meter/mark! queries-meter)
  (counter/inc! queries-counter))

;(defn queries-meter
;  "Returns a map {1 number 5 number 15 number}"
;  [{:keys [queries-meter]}]
;  (meter/rates queries-meter))

(defn queries-counter
  "Returns a number"
  [{:keys [queries-counter]}]
  (counter/value queries-counter))

(defn metrics-f
  "Helper function to chain a function f with a function that calls mark-query! each time f is called
  metrics-ctx the context returned from start
  f this function must have (fn [state] (fn [] do something) ) structure
  returns (fn [state] (fn [] ) )"
  [metrics-ctx f]
  (fn [state]
    (let [f2 (f state)]
      (fn []
        (f2)
        (mark-query! metrics-ctx)))))