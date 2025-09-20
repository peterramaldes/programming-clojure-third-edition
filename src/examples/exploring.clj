(ns examples.exploring
  (:require [clojure.string :as str])
  (:import (java.io File)))

(defn greeting
  "Returns a greeting of the form 'Hello, username.'
  Default username is 'world'."
  ([] (greeting "world"))
  ([username] (str "Hello, " username)))

(defn date [person-1 person-2 & chaperones]
  (println person-1 "and" person-2
           "went out with" (count chaperones) "chaperones."))

(defn indexable-world? [word]
  (> (count word) 2))

(filter indexable-world? (str/split "A fine day it is" #"\W+"))

(defn indexable-words [text]
  (let [indexable-word? (fn [w] (> (count w) 2))]
    (filter indexable-word? (str/split text #"\W+"))))

(defn indexable-words [text]
  (let [indexable-word? #(> (count %) 2)]
    (filter indexable-word? (str/split text #"\W+"))))

(defn make-greeter [greeting-prefix]
  (fn [username] (str greeting-prefix ", " username)))

(defn square-corners [bottom left size]
  (let [top (+ bottom size)
        right (+ left size)]
    [[bottom left] [top left] [top right] [bottom right]]))

(defn greet-author-1 [author]
  (println "Hello," (:first-name author)))

(defn greet-author-2 [{fname :first-name}]
  (println "Hello," fname))

(defn ellipsize [words]
  (let [[w1 w2 w3] (str/split words #"\s+")]
    (str/join " " [w1 w2 w3 "..."])))

(defn triple [number]
  #_(println "debug triple" number)
  (* 3 number))

(defn is-small? [number]
  (if (< number 100) "yes" "no"))

(defn is-small? [number]
  (if (< number 100)
    "yes"
    (do
      (println "Saw a big number" number)
      "no")))

(defn is-small? [number]
  (if (< number 100)
    "yes"
    (do
      (println "Saw a big number" number)
      "no")))

(loop [result [] x 10]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(defn indexed [coll] (map-indexed vector coll))

(defn index-filter [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))

(defn index-of-any [pred coll]
  (first (index-filter pred coll)))
