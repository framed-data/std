(ns framed.std.io-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [framed.std.io :as sio]))

(deftest test-stream-copy
  (let [contents "hello"
        f1 (sio/spit (sio/tempfile) contents)
        f2 (sio/tempfile)]
    (is (empty? (slurp f2)))
    (with-open [istream (io/input-stream f1)
                ostream (io/output-stream f2)]
      (sio/stream-copy istream ostream))
    (is (= contents (slurp f2)))))
