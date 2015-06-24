(defproject cassatest "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}


            :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
            :global-vars {*warn-on-reflection* true
                          *assert*             true}

            :jvm-opts ["-Xmx1g"]

            :prep-tasks ["javac" "compile"]
            :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                           [org.clojure/tools.cli "0.3.1"]
                           [org.clojure/tools.logging "0.3.1"]
                           [com.google.guava/guava "18.0"]
                           [clojurewerkz/cassaforte "2.0.0"]])
