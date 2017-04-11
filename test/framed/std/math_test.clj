(ns framed.std.math-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [framed.std.math :as std.math]))

(deftest test-nonzero?
  (is (std.math/nonzero? -1))
  (is (std.math/nonzero? 1))
  (is (not (std.math/nonzero? 0)))
  (is (not (std.math/nonzero? nil))))

(deftest test-divide-some
  (is (nil? (std.math/divide-some 1 0)))
  (is (= 1/2 (std.math/divide-some 1 2)))
  (is (thrown? AssertionError (std.math/divide-some nil 2)))
  (is (thrown? AssertionError (std.math/divide-some 10 nil))))

(defspec test-divide-some-generative
  (prop/for-all [numer gen/pos-int
                 denom gen/s-pos-int]
    (is (= (/ numer denom)
           (std.math/divide-some numer denom)))))

(deftest test-round-bigdecimal
  (is (= 0.33M (std.math/round-bigdecimal 2 0.3333333333M)))
  (is (= 0.3M (std.math/round-bigdecimal 1 0.3333333333M))))

(deftest test-round-places
  (is (= 3.14 (std.math/round-places 2 3.14159))))

(deftest test-mean
  (is (= 4.5 (double (std.math/mean (range 10)))))
  (is (nil? (std.math/mean []))))

(deftest test-median
  (is (= 4.5 (double (std.math/median (range 10)))))
  (is (= 5 (std.math/median (range 11))))
  (is (nil? (std.math/median []))))

(deftest test-mode
  (is (= 2 (std.math/mode [1 2 3 2 3 2])))
  (is (= [2 3] (std.math/mode [1 2 2 2 3 3 3])))
  (is (nil? (std.math/mode [1 2 3 4 5]))))

(deftest test-variance
  (let [vs '(2 4 4 4 5 5 7 9)]
    (testing "1 ddof (default unbiased estimate)"
      (is (= 4.57143 (->> (std.math/variance vs) (std.math/round-places 5))))
      (is (= 4.57143 (->> (std.math/variance vs 1) (std.math/round-places 5)))))
    (testing "0 ddof (biased estimate)"
      (is (= 4.0 (std.math/variance vs 0))))
    (is (= (std.math/variance vs) (std.math/variance (vec vs))) "It returns equal results for lists/vectors")
    (is (nil? (std.math/variance [])) "It returns nil given an empty seq")
    (is (nil? (std.math/variance nil)) "It returns nil given nil")
    (is (thrown? AssertionError (std.math/variance vs nil)) "ddof must be integer")
    (is (thrown? AssertionError (std.math/variance vs -1)) "ddof must be integer > 0")))

(deftest test-std-dev
  (let [vs '(2 4 4 4 5 5 7 9)]
    (testing "1 ddof (default unbiased estimate)"
      (is (= 2.13809 (->> (std.math/std-dev vs) (std.math/round-places 5))))
      (is (= 2.13809 (->> (std.math/std-dev vs 1) (std.math/round-places 5)))))
    (testing "0 ddof (biased estimate)"
      (is (= 2.0 (std.math/std-dev vs 0))))
    (is (= (std.math/std-dev vs) (std.math/std-dev (vec vs))) "It returns equal results for lists/vectors")
    (is (nil? (std.math/std-dev [])) "It returns nil given an empty seq")
    (is (nil? (std.math/std-dev nil)) "It returns nil given nil")
    (is (thrown? AssertionError (std.math/std-dev vs nil)) "ddof must be integer")
    (is (thrown? AssertionError (std.math/std-dev vs -1)) "ddof must be integer > 0")))
