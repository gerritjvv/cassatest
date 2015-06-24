# cassatest

A Clojure library designed to ... well, that part is up to you.

## Usage


To usage run : ```lein uberjar```

Then:

```
java -jar -Xmx2048m <jar> -h 
```

```clojure
Usage: cassatest [options]

Options:
  -H, --hosts HOST               localhost   Comma separated string of remote hosts
  -q, --query query                          SQL Query, if params specified use as template e.g select a, b from table where a = {myvar} and b = {myvar2} then in params use {:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}
  -P, --params params            {}          {:myvar {:type :int-range :from 0 :to 10} :myvar2 {:type :constant :v 10}}
  -r, --thread-rate-limit limit  2147483647  Integer that sets the rate at which each thread can query
  -i, --iterations n             1000        Number of iterations i.e queries a thread should do
  -n, --threads n                1           Number of threads to use
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

Note that params must be written in edn notation.  

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
