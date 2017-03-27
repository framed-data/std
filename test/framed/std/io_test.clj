(ns framed.std.io-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [framed.std.io :as std.io]))

(deftest test-path-get
  (is (= "foo/bar/quux.txt" (str (std.io/path-get "foo" "bar" "quux.txt")))))

(deftest test-path-join
  (let [expected "foo/bar/quux.txt"]
    (is (= expected (std.io/path-join "foo" "bar" "quux.txt")))
    (is (= expected (std.io/path-join "foo/" "/bar" "quux.txt")))))

(deftest test-stream-copy
  (let [contents "hello"
        f1 (std.io/spit (std.io/tempfile) contents)
        f2 (std.io/tempfile)]
    (is (empty? (slurp f2)))
    (with-open [istream (io/input-stream f1)
                ostream (io/output-stream f2)]
      (std.io/stream-copy istream ostream))
    (is (= contents (slurp f2)))))
