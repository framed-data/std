(defproject io.framed/std "0.1.0"
  :description "A Clojure utility toolkit"
  :url "https://github.com/framed-data/std"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-json "0.5.3"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [com.cognitect/transit-clj "0.8.281"]
                 [com.damballa/abracad "0.4.12"]
                 [com.taoensso/encore "2.1.0"]
                 [com.taoensso/nippy "2.9.0"]
                 [commons-io/commons-io "2.4"]
                 [org.clojure/data.fressian "0.2.1"]
                 [clj-time "0.8.0"]
                 [org.clojure/test.check "0.8.0"]]
  :plugins [[codox "0.8.13"]]
  ;:codox {:src-dir-uri "http://github.com/framed-data/std/tree/master"}
  )
