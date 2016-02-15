(ns framed.std.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.string :as string]
            [framed.std.core :as std]))

(deftest test-find
  (is (= 4 (std/find even? [1 1 1 1 3 3 3 3 4 5 5 6])))
  (is (nil? (std/find even? [1 1 1 1 3 3 3]))))

(defspec test-mapcat
  (prop/for-all [vs (gen/vector gen/string-alphanumeric)]
    (is (= (clojure.core/mapcat identity vs) (std/mapcat identity vs)))
    (is (= (clojure.core/mapcat seq vs) (std/mapcat seq vs)))))

(deftest test-shuffle
  (let [rng (java.util.Random. 1)]
    (is (= [7 10 8 9 5 3 1 4 2 6]
           (std/shuffle (java.util.Random. 1) [1 2 3 4 5 6 7 8 9 10])))))

(deftest test-map-from-keys
  (let [foo 1
        bar 2]
    (is (= {:foo 1 :bar 2} (std/map-from-keys foo bar)))))

(deftest test-zip
  (is (= [[1 3] [2 4]] (std/zip [1 2] [3 4])))
  (is (= [[1 3 "a"] [2 4 "b"]] (std/zip [1 2] [3 4] ["a" "b"]))))

(deftest test-zipmap-seq
  (let [coll [1 2 3]]
    (is (= {2 3, 4 6, 6 9}
           (std/zipmap-seq #(* 2 %) #(* 3 %) [1 2 3])))))

(deftest test-map-tup
  (let [m {"foo" 1 "bar" 2}]
    (is (= [["foo" 2] ["bar" 4]] (std/map-tup #(* % 2) m)))
    (is (= [["oof" 2] ["rab" 4]] (std/map-tup string/reverse #(* % 2) m)))))

(deftest test-map-kv
  (let [m {"foo" 1 "bar" 2}]
    (is (= {"foo" 2 "bar" 4} (std/map-kv #(* % 2) m)))
    (is (= {"oof" 2 "rab" 4} (std/map-kv string/reverse #(* % 2) m)))))

(deftest test-when-assoc-in
  (let [m {:foo 1}]
    (is (= {:foo 1} (std/when-assoc-in m [:bar] nil)))
    (is (= {:foo 1 :bar 2} (std/when-assoc-in m [:bar] 2)))
    (is (= {:foo 1 :bar {:norf 3}} (std/when-assoc-in m [:bar :norf] 3)))))

(deftest test-when-assoc
  (let [m {:foo 1}]
    (is (= {:foo 1} (std/when-assoc m :bar nil)))
    (is (= {:foo 1 :bar 2} (std/when-assoc m :bar 2)))))

(deftest test-coll-wrap
  (is (= [2] (std/coll-wrap 2)))
  (is (= [1 2 3] (std/coll-wrap [1 2 3])))
  (is (= '(1 2 3) (std/coll-wrap '(1 2 3))))
  (is (= [{:foo 1}] (std/coll-wrap {:foo 1}))))

(deftest test-flip
  (let [m {:foo 1 :bar 2}
        flipped-dissoc (std/flip dissoc)]
    (is (= {:bar 2} (flipped-dissoc :foo m)))
    (is (= {:bar 2} (std/flip dissoc :foo m)))))

(deftest test-to-edn
  (is (= "{:hello \"world\"}" (std/to-edn {:hello "world"})))
  (is (= nil (std/to-edn nil))))

(deftest test-from-edn
  (is (= {:hello "world"} (std/from-edn "{:hello \"world\"}")))
  (is (= nil (std/from-edn nil))))
