(ns framed.std.io
  "I/O utility functions to complement clojure.java.io"
  (:require [clojure.java.io :as io])
  (:import (java.io File DataInputStream DataOutputStream)
           (org.apache.commons.io IOUtils)
           (java.nio.file Files StandardCopyOption))
  (:refer-clojure :exclude [spit]))

(def tmpdir
  "The system temporary directory"
  (System/getProperty "java.io.tmpdir"))

(defn data-input-stream
  "Coerce argument to an open java.io.DataInputStream"
  [istream-like]
  (DataInputStream. (io/input-stream istream-like)))

(defn data-output-stream
  "Coerce argument to an open java.io.DataOutputStream"
  [ostream-like]
  (DataOutputStream. (io/output-stream ostream-like)))

(defn stream-copy
  "Copy from input stream to output stream.
   Does *not* close streams"
  [input-stream output-stream]
  (IOUtils/copyLarge
    ^java.io.InputStream input-stream
    ^java.io.OutputStream output-stream
    (byte-array 64000000)))

(defn spit
  "Like clojure.core/spit, but returns f"
  [f content & options]
  (apply clojure.core/spit f content options)
  f)

(defn tempfile
  "Create a tempfile that deletes on VM exit"
  ([] (tempfile "std" ".tmp"))
  ([filename] (tempfile filename ".tmp"))
  ([filename suffix]
   (let [file (File/createTempFile filename suffix)]
     (.deleteOnExit file)
     file)))

(defn temp-path
  "Generate a random filename in the system temp directory
   Does *not* create a file, only generates a path"
  []
  (str tmpdir "/" (java.util.UUID/randomUUID) ".tmp"))

(defn nonempty-file?
  "Does file-like x exist and contain more than zero bytes?"
  [x]
  (when x
    (let [f (io/file x)]
      (and (.exists f) (> (.length f) 0)))))

(defn ->Path
  "Construct a java.nio.file.Path from file-like x"
  [x]
  {:pre [x]}
  (.toPath (io/file x)))

(defn create-link
  "Create a hard-link for file-like link to file-like existing
   Returns link"
  [link existing]
  (Files/createLink (->Path link) (->Path existing))
  link)

(defn move
  "Atomically move file-like src to file-like dest and return dest.
   Throws error if dest already exists"
  [src dest]
  (if (.exists (io/file dest))
    (throw (IllegalArgumentException. "dest already exists"))
    (let [copy-opts (into-array [StandardCopyOption/ATOMIC_MOVE])]
      (Files/move (->Path src) (->Path dest) copy-opts)
      dest)))
