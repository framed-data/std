(ns framed.std.math-test
  (:require [clojure.test :refer :all]
            [framed.std.math :as m]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(deftest test-nonzero?
  (is (m/nonzero? -1))
  (is (m/nonzero? 1))
  (is (not (m/nonzero? 0)))
  (is (not (m/nonzero? nil))))

(deftest test-divide-some
  (is (nil? (m/divide-some 1 0)))
  (is (= 1/2 (m/divide-some 1 2)))
  (is (thrown? AssertionError (m/divide-some nil 2)))
  (is (thrown? AssertionError (m/divide-some 10 nil))))

(defspec test-divide-some-generative
  (prop/for-all [numer gen/pos-int
                 denom gen/s-pos-int]
    (is (= (/ numer denom)
           (m/divide-some numer denom)))))

(deftest test-round-bigdecimal
  (is (= 0.33M (m/round-bigdecimal 2 0.3333333333M)))
  (is (= 0.3M (m/round-bigdecimal 1 0.3333333333M))))

(deftest test-round-places
  (is (= 3.14 (m/round-places 2 3.14159))))

(deftest test-mean
  (is (= 4.5 (double (m/mean (range 10)))))
  (is (nil? (m/mean []))))

(deftest test-median
  (is (= 4.5 (double (m/median (range 10)))))
  (is (= 5 (m/median (range 11))))
  (is (nil? (m/median []))))

(deftest test-mode
  (is (= 2 (m/mode [1 2 3 2 3 2])))
  (is (= [2 3] (m/mode [1 2 2 2 3 3 3])))
  (is (nil? (m/mode [1 2 3 4 5]))))
