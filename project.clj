(defproject io.framed/std "0.2.1"
  :description "A Clojure utility toolkit"
  :url "https://github.com/framed-data/std"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48"]
                 [clj-json "0.5.3"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [com.cognitect/transit-clj "0.8.281"]
                 [com.damballa/abracad "0.4.12"]
                 [com.taoensso/encore "2.1.0"]
                 [com.taoensso/nippy "2.9.0"]
                 [commons-io/commons-io "2.4"]
                 [org.clojure/data.fressian "0.2.1"]
                 [clj-time "0.8.0"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [org.clojure/test.check "0.8.0"]]
  :plugins [[codox "0.8.13"]
            [lein-cljsbuild "1.0.6"]]
  :cljsbuild {:builds []})
