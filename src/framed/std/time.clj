(ns framed.std.time
  "Date/time utilities on top of JodaTime/clj-time"
  (:require (clj-time
              [core :as tcore]
              [coerce :as tcoerce]))
  (:import org.joda.time.DateTime))

(defn days-in
  "Return a sequence of all days in an interval (left-inclusive, right exclusive)
   Ex:
     (std.time/days-in
       (clj-time.core/interval (clj-time.core/date-time 2015 8 20)
                               (clj-time.core/date-time 2015 8 23)))
     ; => [#<DateTime 2015-08-20> #<DateTime 2015-08-21> #<DateTime 2015-08-23>]"
  [interval]
  (let [start (tcore/start interval)
        end (tcore/end interval)]
    (take-while
      (fn [t] (tcore/before? t end))
      (iterate
        (fn [t] (tcore/plus t (tcore/days 1)))
        start))))

(defn periods-from
  "Given a starting date, return the date that is N periods from it
   N - positive or negative integer

   Ex:
     (def start (clj-time.core/date-time 2015 2 20))
     (periods-from start (clj-time.core/days 2) -3)
     ; => #<DateTime 2015-02-14> ; Three two-day periods earlier

     (periods-from start (clj-time.core/days 2) 3)
     ; => #<DateTime 2015-02-26> ; Three two-day periods later"
  [date period n]
  (let [f (fn [n] (if (< n 0) tcore/minus tcore/plus))]
    (last (take (inc (Math/abs n))
                (iterate #((f n) % period) date)))))

(defn period-interval
  "Given a period (e.g. 7 days), return the interval that is N
   periods from date. Period must be joda.time.ReadablePeriod,
   ex: (clj-time.core/days 7)

   Zero denotes the interval starting from `date` and ending
   one period later.

   Ex:
     ; One 7-day period earlier than date
     (period-interval
       (clj-time.core/date-time 2014 11 13)
       (clj-time.core/days 7)
       -1)
     ; => #<Interval 2014-11-06 / 2014-11-13>"
  ([date period n]
   (period-interval date period n (inc n)))
  ([date period n0 n1]
   {:pre [date period n0 n1 (< n0 n1)]}
   (tcore/interval
     (periods-from date period n0)
     (periods-from date period n1))))

(defn at-midnight
  "Return the DateTime at midnight of a given date"
  [^DateTime date]
  (.toDateMidnight date))

(defn unix->datetime
  "Return a DateTime from a Unix timestamp (in seconds)"
  [t]
  (->> (long t)
       (* 1000)
       tcoerce/from-long))

(defn datetime->unix
  "Convert a DateTime to a Unix timestamp (in seconds)"
  [date]
  (-> (tcoerce/to-long date)
      (/ 1000)
      long))

(def str->inst
  "Parse a string timestamp into a java.util.Date"
  (comp tcoerce/to-date tcoerce/from-string))
