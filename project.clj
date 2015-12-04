(defproject cassatest "0.1.0-SNAPSHOT"
            :description "A cassandra library to help with testing reads allowing to specify threads and rate limits — Edit"
            :url "https://github.com/gerritjvv/cassatest"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}


            :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
            :global-vars {*warn-on-reflection* true
                          *assert*             true}

            :jvm-opts ["-Xmx1g"]

            :main cassatest.cassatest
            :aot [cassatest.cassatest]

            :prep-tasks ["javac" "compile"]
            :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                           [org.clojure/tools.cli "0.3.1"]
                           [org.clojure/tools.logging "0.3.1"]
                           [com.google.guava/guava "18.0"]
			   [com.datastax.cassandra:cassandra-driver-core "3.0.0-alpha5"]
                           [clojurewerkz/cassaforte "2.0.0" :exclude com.datastax.cassandra:cassandra-driver-core]
                           [metrics-clojure "2.4.0"]])
