(ns framed.std.time-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as tcore]
            [framed.std.time :as std.time]))

(deftest test-days-in
  (is (= [(tcore/date-time 2014 10 30)
          (tcore/date-time 2014 10 31)
          (tcore/date-time 2014 11 1)
          (tcore/date-time 2014 11 2)
          (tcore/date-time 2014 11 3)
          (tcore/date-time 2014 11 4)
          (tcore/date-time 2014 11 5)]
         (std.time/days-in
           (tcore/interval
             (tcore/date-time 2014 10 30)
             (tcore/date-time 2014 11 6))))
      "It calculates the time point sequence correctly"))

(deftest test-periods-from
  (is (= (tcore/date-time 2015 2 13)
         (std.time/periods-from (tcore/date-time 2015 2 20) (tcore/days 7) -1))
      "It calculates the last interval (one period back) correctly.")
  (is (= (tcore/date-time 2015 2 27)
         (std.time/periods-from (tcore/date-time 2015 2 20) (tcore/days 7) 1))
      "It calculates the last interval (one period back) correctly.")
  (is (= (tcore/date-time 2015 2 14)
         (std.time/periods-from (tcore/date-time 2015 2 20) (tcore/days 3) -2))
      "It calculates multiple periods back correctly.")
  (is (= (tcore/date-time 2015 2 26)
         (std.time/periods-from (tcore/date-time 2015 2 20) (tcore/days 3) 2))
      "It calculates multiple periods forward correctly."))

(deftest test-period-interval
  (is (thrown? AssertionError
               (std.time/period-interval (tcore/date-time 2015 1 1) nil -1 0)))
  (is (= (tcore/interval
           (tcore/date-time 2014 11 6)
           (tcore/date-time 2014 11 13))
         (std.time/period-interval
           (tcore/date-time 2014 11 13)
           (tcore/days 7)
           -1))
      "It calculates the last interval (one period back) correctly.")
  (is (= (tcore/interval
           (tcore/date-time 2014 11 13)
           (tcore/date-time 2014 11 20))
         (std.time/period-interval
           (tcore/date-time 2014 11 13)
           (tcore/days 7)
           0))
      "It calculates the current interval (one period forward) correctly.")
  (is (= (tcore/interval
           (tcore/date-time 2014 10 30)
           (tcore/date-time 2014 11 6))
         (std.time/period-interval
           (tcore/date-time 2014 11 13)
           (tcore/days 7)
           -2))
      "It calculates multiple periods back correctly.")
  (is (= (tcore/interval
           (tcore/date-time 2014 11 20)
           (tcore/date-time 2014 11 27))
         (std.time/period-interval
           (tcore/date-time 2014 11 13)
           (tcore/days 7)
           1))
      "It calculates multiple periods forward correctly.")
  (testing "Period ranges"
    (is (= (tcore/interval
             (tcore/date-time 2014 11 6)
             (tcore/date-time 2014 11 20))
           (std.time/period-interval
             (tcore/date-time 2014 11 20)
             (tcore/days 7)
             -2
             0))
        "It calculates multiple negative periods correctly.")
    (is (= (tcore/interval
             (tcore/date-time 2014 11 6)
             (tcore/date-time 2014 11 20))
           (std.time/period-interval
             (tcore/date-time 2014 11 13)
             (tcore/days 7)
             -1
             1))
        "It calculates multiple negative to positive periods forward.")
    (is (= (tcore/interval
             (tcore/date-time 2014 11 20)
             (tcore/date-time 2014 11 27))
           (std.time/period-interval
             (tcore/date-time 2014 11 13)
             (tcore/days 7)
             1
             2))
        "It calculates multiple positive periods forward")
    (is (= (tcore/interval
           (tcore/date-time 2014 11 20)
           (tcore/date-time 2014 11 27))
         (std.time/period-interval
           (tcore/date-time 2014 11 13)
           (tcore/days 7)
           1
           2))
      "It calculates multiple positive periods forward")))

(deftest test-unix->datetime
  (is (= (tcore/date-time 2015 8 23 22 24 26)
         (std.time/unix->datetime 1440368666))))

(deftest test-datetime->unix
  (is (= 1440368666
         (std.time/datetime->unix (tcore/date-time 2015 8 23 22 24 26)))))
