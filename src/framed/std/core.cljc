(ns framed.std.core
  "Utility functions to complement clojure.core"
  (:require #?(:clj [clojure.edn]
               :cljs [cljs.reader]))
  (:refer-clojure :exclude [find mapcat shuffle]))

(defn find
  "Return the first value of x in coll that is logically true for (pred x)
   Similar to clojure.core/some, but returns the item itself.

   Ex: (find even? [1 1 1 3 4 5 6]) ; => 4"
  [pred coll]
  (first (filter pred coll)))

(defn mapcat
  "Like clojure.core/mapcat over a single coll without object
   reachability memory issues. This is especially useful when
   large seqs are generated by f, but this is a general-purpose
   replacement for its clojure.core counterpart.

   See http://clojurian.blogspot.com/2012/11/beware-of-mapcat.html
       http://stackoverflow.com/questions/21943577/mapcat-breaking-the-lazyness"
  [f coll]
  (lazy-seq
    (when (seq coll)
      (concat (f (first coll))
              (mapcat f (rest coll))))))

#?(:clj
  (defn shuffle
    "Same as clojure.core/shuffle but accepts source of randomness
     for deterministic testing"
    [^java.util.Random rng ^java.util.Collection coll]
    (let [al (java.util.ArrayList. coll)]
      (java.util.Collections/shuffle al rng)
      (clojure.lang.RT/vector (.toArray al)))))

(defmacro map-from-keys
  "Given symbols, e.g. `(map-from-keys foo bar)`,
   return a map with those names as keyword keys, and those values:

   Ex:
     (map-from-keys foo bar)
     ; => {:foo foo
           :bar bar}"
  [& forms]
  (let [pairs (mapcat (fn [f] (vector (keyword (name f)) f)) forms)]
    `(hash-map ~@pairs)))

(defn rand-int-between
  "Generate a random int in the inclusive range of min-val to max-val"
  [min-val max-val]
  {:pre [(>= max-val min-val)]}
  (let [val-range (inc (- max-val min-val))]
    (+ min-val (rand-int val-range))))

(def ^{:private true} alphanum
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890")

(defn rand-alphanumeric
  "Generate a string of random letters/digits of a given length"
  [len]
  (apply str (repeatedly len #(rand-nth alphanum))))

(defn zip
  "Zip corresponding elements from two or more colls together
   Ex:
     (zip [1 2] [3 4])
     ; => [[1 3] [2 4]]"
  [& colls]
  (apply map vector colls))

(defn zipmap-seq
  "Given a collection `coll`, return a map where for all k in coll,
   key of entry is (f k) and value of entry is (g k)

   Ex:
     (zipmap-seq #(* 2 %) (* 3 %) [1 2 3])
     ;=> {2 3, 4 6, 6 9}"
  [key-fn val-fn coll]
  (zipmap
    (map key-fn coll)
    (map val-fn coll)))

(defn map-tup
  "For all k,v in coll, return a seq of [(key-fn k) (val-fn v)] tuples

   Ex:
     (map-tup #(* 2 %) #(* 3 %) {1 2, 3 4, 5 6})
     ; => ([2 6] [6 12] [10 18])"
  ([val-fn coll]
   (map-tup identity val-fn coll))
  ([key-fn val-fn coll]
   (map (fn [[k v]]
          [(key-fn k) (val-fn v)])
        coll)))

(def
  ^{:doc "Same as `map-tup` but returns results in a map"
    :arglists '([val-fn coll] [key-fn val-fn coll])}
  map-kv
  (comp (partial into {}) map-tup))

(defn when-assoc-in
 "When v is truthy, assoc it into coll at ks. Otherwise return coll"
  [coll ks v]
  (if v
    (assoc-in coll ks v)
    coll))

(defn when-assoc
 "When v is truthy, assoc it into coll at k. Otherwise return coll"
  [coll k v]
  (when-assoc-in coll [k] v))

(defn coll-wrap
  "Wrap value in a vector if it is not sequential already
   Ex:
     (coll-wrap 2)       ; => [2]
     (coll-wrap [1 2 3]) ; => [1 2 3]"
  [x-or-xs]
  (if (sequential? x-or-xs) x-or-xs [x-or-xs]))

(defn flip
  "Takes two arguments in the reverse order of f ('flips' a function
   of two arguments)
   If supplied a function with no args, returns a new function
   accepting the reversed args

   Ex:
     (flip dissoc :foo {:foo 1 :bar 2})
     ; => {:bar 2}

     (def flipped-dissoc (flip dissoc))
     (flipped-dissoc :foo {:foo 1 :bar 2})
     ; => {:bar 2}"
  ([f]
   (fn [y x] (f x y)))
  ([f y x]
   (f x y)))

#?(:clj
  (defmacro future-loop
    "Execute body repeatedly within a future, returning the future"
    [& body]
    `(future
       (loop []
         ~@body
         (recur)))))

(defn to-edn
  "pr-str x only if it is truthy, else return nil"
  [x]
  (when x
    (pr-str x)))

(defn from-edn
  "Attempt to parse x as EDN, or return nil on failure"
  [x]
  #?(:clj (try (clojure.edn/read-string x) (catch Exception ex nil))
     :cljs (try (cljs.reader/read-string x) (catch js/Error ex nil))))
