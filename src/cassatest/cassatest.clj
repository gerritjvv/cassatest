(ns cassatest.cassatest
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [cassatest.config :as config]
            [cassatest.cassandra :as cassandra]
            [cassatest.exec :as exec])
  (:gen-class)
  (:import (java.net InetAddress)))

(def cli-options
  [["-H" "--hosts HOST" "Comma separated string of remote hosts"
    :default config/DEFAULT-HOSTS
    :default-desc "localhost"
    :parse-fn config/parse-hosts-arg]

   ["-q" "--query query" "SQL Query, if params specified use as template e.g select a, b from table where a = {myvar} and b = {myvar2} then in params use {:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}"
    :validate [#(not-empty %)]]

   ["-P" "--params params" "{:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}"
    :parse-fn config/parse-parmams-arg
    :default config/DEFAULT-PARAMS]

   ["-r" "--thread-rate-limit limit" "Integer that sets the rate at which each thread can query"
    :parse-fn #(Integer/parseInt %)
    :default config/DEFAULT-RATE]

   ["-i" "--iterations n" "Number of iterations i.e queries a thread should do"
    :parse-fn #(Integer/parseInt %)
    :default config/DEFAULT-ITERATIONS]


   ["-n" "--threads n" "Number of threads to use"
    :parse-fn #(Integer/parseInt %)
    :default config/DEFAULT-THREADS]

   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Cassandra test queries"
        ""
        "Usage: cassatest [options]"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn run-queries! [{:keys [threads iterations] :as conf}]
  (let [start (System/currentTimeMillis)
        latch (cassandra/run-queries! conf)
        cnt (* threads iterations)]
    @latch
    (println cnt " queries in " (- (System/currentTimeMillis) start) "ms")))

(defn parse-options [args]
  (parse-opts args cli-options))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-options args)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      :default (run-queries! options))

    (System/exit 0)))
