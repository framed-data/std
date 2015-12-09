(ns framed.std.serialization-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.java.io :as io]
            [taoensso.nippy :as nippy]
            (framed.std
              [io :as std.io]
              [serialization :refer :all])))

(deftest test-fressian-coll
  (let [coll [:a "b" 3]]
    (is (= (seq (coll->FressianSeq coll)) coll)))
  (let [coll [false]]
    (is (= (seq (coll->FressianSeq coll)) coll)))
  (let [coll [[false]]]
    (is (= (seq (coll->FressianSeq coll)) coll)))
  (let [coll ['false]]
    (is (= (seq (coll->FressianSeq coll)) coll)))
  (let [coll [['false]]]
    (is (= (seq (coll->FressianSeq coll)) coll)))
  (is (= nil (seq (coll->FressianSeq [])))))

(defspec test-fressian-coll-generative
  100
  (prop/for-all [vs (gen/vector gen/simple-type-printable)]
    (= (seq (coll->FressianSeq vs)) (seq vs))))

(deftest test-read-csv
  (let [f (std.io/tempfile)]
    (write-csv f
               ["name","age"]
               [["John" 27]
                ["Mary" 28]])
    (is (= [["name","age"] ["John" "27"] ["Mary" "28"]]
           (read-csv f))
        "It reads labels and rows")
    (is (= [["John" "27"] ["Mary" "28"]]
           (read-csv true f))
        "It drops headers")))

(deftest test-write-csv
  (let [w (java.io.StringWriter.)]
    (write-csv w
               []
               [["foo" "bar"]
                ["baz" "quux"]])
    (is (= (.toString w)
           "foo,bar\nbaz,quux\n")
        "It writes a CSV with no headers"))
  (let [w (java.io.StringWriter.)]
    (write-csv w
               ["heads" "tails"]
               [["foo" "bar"]
                ["baz" "quux"]])
    (is (= (.toString w)
           "heads,tails\nfoo,bar\nbaz,quux\n")
        "It writes a CSV with headers"))
  (let [w (java.io.StringWriter.)]
    (write-csv w
               []
               [[0.333M 0.14M]
                [3.1415M 0.492M]])
    (is (= (.toString w)
           "0.333,0.14\n3.1415,0.492\n")
        "It writes decimals")))

(deftest test-write-jsonl
  (let [data [{:foo :bar}
              {:baz :quux}]
        tempfile (std.io/tempfile)
        result (write-jsonl tempfile data)]
    (is (= (read-jsonl tempfile)
           [{"foo" "bar"}
            {"baz" "quux"}]))))

(deftest test-file->NippySeq
  (let [colls [[1 2] [3 4] [5 6]]
        paths (map (fn [_] (std.io/tempfile)) colls)]
    (->> (map vector paths colls)
         (map (fn [[path coll]] (write-nippy path coll)))
         doall)
    (is (= colls
           (->> (map file->NippySeq paths)
                (map seq)
                (map vec)))
        "It roundtrips collections of collections")))

(defspec gentest-read-avro
  100
  (prop/for-all [v (gen/vector gen/string)]
    (let [tempfile (std.io/tempfile)
          schema (avro-schema "string")]
      (= v (vec (read-avro (write-avro tempfile schema v)))))))

(deftest test-read-avro
  (testing "with InputStreams"
    (let [vs ["one" "two"]
          schema (avro-schema "string")
          f (write-avro (std.io/tempfile) schema vs)]
      (is (= vs (read-avro (io/input-stream f)))))))
