(ns framed.std.math
  (:import (java.math BigDecimal MathContext RoundingMode)))

(defn nonzero?
  "Is x non-nil and non-zero?"
  [x]
  (and x (not= 0 x)))

(defn divide-some
  "Zero-safe division (returns value or nil)"
  [n div]
  {:pre [n div]}
  (when (nonzero? div)
    (/ n div)))

(defn ^BigDecimal bigdecimal
  "Convert x to a java.math.BigDecimal, representing ratios as doubles"
  [x]
  (if (ratio? x)
    (bigdec (double x))
    (bigdec x)))

(defn ^BigDecimal round-bigdecimal
  "Round BigDecimal `v` to specified number of significant digits"
  [precision ^BigDecimal v]
  (.round v (MathContext. precision RoundingMode/HALF_UP)))

(defn round-places
  "Round double `v` to specified number of decimal places"
  [places v]
  (let [factor (Math/pow 10 places)]
    (/ (Math/round (* v factor)) factor)))

(defn mean [vs]
  (when (seq vs)
    (/ (apply + vs) (count vs))))

(defn median [vs]
  (when (seq vs)
    (let [sorted (sort vs)
          len (count vs)
          mid (int (/ len 2))]
      (if (odd? len)
        (nth sorted mid)
        (/ (+ (nth sorted mid) (nth sorted (dec mid))) 2)))))

(defn mode
  "Return the mode of vs, or a seq of values if vs are multi-modal
   Returns nil if no mode present (all frequencies equal)"
  [vs]
  (let [freqs (sort-by second (frequencies vs))
        singular? (fn [coll] (= 1 (count coll)))]
    (when-not (singular? (distinct (map second freqs)))
      (let [[_ mode-count] (last freqs)
            modes (->> freqs
                       (drop-while #(not= mode-count (last %)))
                       (map first))]
        (if (singular? modes)
          (first modes)
          modes)))))

(defn percentage
  "What percent of total is v?
   Can optionally specify rounding decimal places; defaults to 2"
  ([v total]
   (percentage v total 2))
  ([v total places]
   (let [round (partial round-places places)]
     (round (* 100 (double (/ v total)))))))

(defn clamp
  "Return v, or `max-val` if v exceeds max-val"
  [max-val v]
  (if (> v max-val)
    max-val
    v))
