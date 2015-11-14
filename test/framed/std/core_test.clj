(ns framed.std.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.string :as string]
            [framed.std.core :as s]))

(defspec test-mapcat
  (prop/for-all [vs (gen/vector gen/string-alphanumeric)]
    (is (= (clojure.core/mapcat identity vs) (s/mapcat identity vs)))
    (is (= (clojure.core/mapcat seq vs) (s/mapcat seq vs)))))

(deftest test-shuffle
  (let [rng (java.util.Random. 1)]
    (is (= [7 10 8 9 5 3 1 4 2 6]
           (s/shuffle (java.util.Random. 1) [1 2 3 4 5 6 7 8 9 10])))))

(deftest test-map-from-keys
  (let [foo 1
        bar 2]
    (is (= {:foo 1 :bar 2} (s/map-from-keys foo bar)))))

(deftest test-zip
  (is (= [[1 3] [2 4]] (s/zip [1 2] [3 4])))
  (is (= [[1 3 "a"] [2 4 "b"]] (s/zip [1 2] [3 4] ["a" "b"]))))

(deftest test-zipmap-seq
  (let [coll [1 2 3]]
    (is (= {2 3, 4 6, 6 9}
           (s/zipmap-seq #(* 2 %) #(* 3 %) [1 2 3])))))

(deftest test-map-tup
  (let [m {"foo" 1 "bar" 2}]
    (is (= [["foo" 2] ["bar" 4]] (s/map-tup #(* % 2) m)))
    (is (= [["oof" 2] ["rab" 4]] (s/map-tup string/reverse #(* % 2) m)))))

(deftest test-map-kv
  (let [m {"foo" 1 "bar" 2}]
    (is (= {"foo" 2 "bar" 4} (s/map-kv #(* % 2) m)))
    (is (= {"oof" 2 "rab" 4} (s/map-kv string/reverse #(* % 2) m)))))

(deftest test-when-assoc-in
  (let [m {:foo 1}]
    (is (= {:foo 1} (s/when-assoc-in m [:bar] nil)))
    (is (= {:foo 1 :bar 2} (s/when-assoc-in m [:bar] 2)))
    (is (= {:foo 1 :bar {:norf 3}} (s/when-assoc-in m [:bar :norf] 3)))))

(deftest test-coll-wrap
  (is (= [2] (s/coll-wrap 2)))
  (is (= [1 2 3] (s/coll-wrap [1 2 3])))
  (is (= '(1 2 3) (s/coll-wrap '(1 2 3))))
  (is (= [{:foo 1}] (s/coll-wrap {:foo 1}))))

(deftest test-flip
  (let [m {:foo 1 :bar 2}
        flipped-dissoc (s/flip dissoc)]
    (is (= {:bar 2} (flipped-dissoc :foo m)))
    (is (= {:bar 2} (s/flip dissoc :foo m)))))
