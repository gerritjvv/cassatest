# cassatest

A cassandra library to help with testing reads allowing to specify threads and rate limits 

## Usage


To usage run : ```lein uberjar```

Then:

```
java -jar -Xmx2048m <jar> -h 
```

```clojure
Usage: cassatest [options]

Options:
  -H, --hosts HOST                                       localhost    Comma separated string of remote hosts
  -q, --query query                                                   SQL Query, if params specified use as template e.g select a, b from table where a = {myvar} and b = {myvar2} then in params use {:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}
  -P, --params params                                    {}           {:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}
  -r, --thread-rate-limit limit                          2147483647   Integer that sets the rate at which each thread can query
  -i, --iterations n                                     1000         Number of iterations i.e queries a thread should do
  -n, --threads n                                        1            Number of threads to use
  -C, --consistency consistency                          :one         Cassandra consistency each-quorum,one,local-quorum,quorum,three,all,serial,two,any
  -R, --retry retry-policy                               :default     Cassandra retry-policy retry,downgrading-consistency,default,fallthrough
  -x, --read-attempts retry-read-attempts                1            Cassandra retry-policy==:retry read attempts
  -y, --write-attempts retry-write-attempts              1            Cassandra retry-policy==:retry write attempts
  -z, --unavailable-attempts retry-unavailable-attempts  1            Cassandra retry-policy==:retry unavailable attempts
  -T, --duration duration                                             If specified iterations are ignored and threads will run for this amount of time in seconds
  -h, --help
```

### Queries and parameters

The queries can either be hard coded or written as a template with variables being substituted on each query call via a generator.  

For supported generators please see ```cassatest.generators```.  

An example query where the parameter 'a' is replaced by a random number between [0 10) would be:

```
-q "select * from table where a = {a}"
--params="{:a {:type :int-range :from 0 :to 10}}"
```

### Generators

Random integer from rante [from to)  

```clojure
{:type :int-range :from 0 :to 10}
```

Constant  

```clojure
{:type :constant :v value}
```

Random chars  


```clojure
;will return 10 random chars from a-z
{:type :rand-chars :length 10}
```

Random UUID

```clojure
{:type :uuid}
```

Note that params must be written in edn notation.  

### Duration testing

If the attribute ```duration``` is specified the ```iterations``` option is ignored and  
threads will run for ```duration``` seconds.  

### Retry Policies

```
default downgrading-consistency fallthrough retry
```

If ```retry``` is specified as a retry-policy the properties ```read-attempts, write-attempts, and unavailable-attempts``` are used  
all of which default to 1 if not specified.  

## License

Copyright © 2015 gerritjvv

Distributed under the Eclipse Public License either version 1.0.