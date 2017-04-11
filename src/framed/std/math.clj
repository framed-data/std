(ns framed.std.math
  (:import (java.math BigDecimal MathContext RoundingMode)))

(defn nonzero?
  "Is v non-nil and non-zero?"
  [v]
  (and v (not= 0 v)))

(defn divide-some
  "Zero-safe division (returns value or nil)"
  [n div]
  {:pre [n div]}
  (when (nonzero? div)
    (/ n div)))

(defn ^BigDecimal bigdecimal
  "Convert v to a java.math.BigDecimal, representing ratios as doubles"
  [v]
  (if (ratio? v)
    (bigdec (double v))
    (bigdec v)))

(defn ^BigDecimal round-bigdecimal
  "Round BigDecimal `v` to specified number of significant digits"
  [precision ^BigDecimal v]
  (.round v (MathContext. precision RoundingMode/HALF_UP)))

(defn round-places
  "Round double `v` to specified number of decimal places"
  [places v]
  (let [factor (Math/pow 10 places)]
    (/ (Math/round (* v factor)) factor)))

(defn mean
  "Return the arithmetic mean of vs, or nil if empty"
  [vs]
  (when (seq vs)
    (/ (reduce + vs) (count vs))))

(defn median
  "Return the median value of vs, or nil if empty"
  [vs]
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

(defn- squared-deviations
  "Return the set of squared deviations from items in `vs` to their mean"
  [mean vs]
  (map #(Math/pow (- % mean) 2) vs))

(defn variance
  "Compute the variance of `vs`. Divisor used is `n - ddof` where `ddof` represents
   'delta degrees of freedom'. By default returns an unbiased estimate (ddof = 1)
   For a biased estimate, set ddof = 0

   See https://en.wikipedia.org/wiki/Bessel%27s_correction
       https://en.wikipedia.org/wiki/Degrees_of_freedom_(statistics)"
  ([vs]
   (variance vs 1))
  ([vs ddof]
   {:pre [(number? ddof) (>= ddof 0)]}
   (when (seq vs)
     (/ (reduce + (squared-deviations (mean vs) vs))
        (- (count vs) ddof)))))

(defn std-dev
  "Compute the standard deviation of `vs`. Divisor used is `n - ddof` where `ddof`
   represents 'delta degrees of freedom'. By default returns an unbiased
   estimate (ddof = 1). For a biased estimate, set ddof = 0

   See https://en.wikipedia.org/wiki/Bessel%27s_correction
       https://en.wikipedia.org/wiki/Degrees_of_freedom_(statistics)"
  ([vs]
   (std-dev vs 1))
  ([vs ddof]
   (some-> (variance vs ddof) Math/sqrt)))
