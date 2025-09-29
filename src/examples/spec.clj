(ns examples.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(defn scale-ingredient [ingredient factor]
  (update ingredient :quantity * factor))

;; Specs describing an ingredient
(s/def ::ingredient (s/keys :req [::name ::quantity ::unit]))
(s/def ::name string?)
(s/def ::quantity number?)
(s/def ::unit keyword?)

;; Function spec for scale-ingredient
(s/fdef scale-ingredient
        :args (s/cat :ingredient ::ingredient :factor number?)
        :ret ::ingredient)


;; Enumerates
(s/def ::bowling-roll #{0 1 2 3 4 5 6 7 8 9 10})

;; Range Specs
(s/def ::bowling-ranged-roll (s/int-in 0 11))

;; Handling Nil
(s/def :my.app/company-name-2 (s/nilable string?))

(s/def ::nilable-boolean (s/nilable boolean?))

;; Logical Specs
(s/def ::odd-int (s/and int? odd?))
(comment
  (s/valid? ::odd-int 5)
  (s/valid? ::odd-int 10)
  (s/valid? ::odd-int 5.2)
  )

(s/def ::odd-or-42 (s/or :odd ::odd-int :42 #{42}))

(comment
  (s/valid? ::odd-or-42 "foo")
  (s/valid? ::odd-or-42 "42")
  (s/valid? ::odd-or-42 :odd-int)
  (s/valid? ::odd-or-42 42)
  (s/valid? ::odd-or-42 5)
  (s/valid? ::odd-or-42 4)
  (s/conform ::odd-or-42 42)
  (s/conform ::odd-or-42 19)
  (s/explain ::odd-or-42 0)
  (s/explain-str ::odd-or-42 0)
  (s/explain-data ::odd-or-42 0)
  )


;; Collection Specs
(s/def ::names (s/coll-of string?))
(comment
  (s/valid? ::names ["Alex" "Stu"])
  (s/valid? ::names #{"Alex" "Stu"})
  (s/valid? ::names '("Alex" "Stu"))
  (s/valid? ::names '("Alex" 2 "Stu"))
  )

(s/def ::my-set (s/coll-of int? :kind set? :min-count 2))
(comment
  (s/valid? ::my-set #{10 20})
  )

(s/def ::scores (s/map-of string? int?))
(comment
  (s/valid? ::scores {"Stu" 100, "Alex" 200})
  (s/valid? ::scores {"Stu" 100, "Alex" 200, "Foo" [1 2 3]})
  (s/conform ::scores {"Stu" 100, "Alex" 200, "Foo" [1 2 3]})
  (s/explain ::scores {"Stu" 100, "Alex" 200, "Foo" [1 2 3]})
  (s/explain-data ::scores {"Stu" 100, "Alex" 200, "Foo" [1 2 3]})
  )

;; Collection Sampling

;; Tuples
(s/def ::point (s/tuple float? float?))
(comment
  (s/conform ::point [1.3 2.7])
  (s/valid? ::point [1.3 2.7])
  )

;; Information Maps
(s/def :music/id uuid?)
(s/def :music/artist string?)
(s/def :music/title string?)
(s/def :music/date inst?)

(s/def :music/release
  (s/keys :req [:music/id]
          :opt [:music/artist
                :music/title
                :music/date]))

(s/def :music/release-unqualified
  (s/keys :req-un [:music/id]
          :opt-un [:music/artist
                   :music/title
                   :music/date]))

;; Sequences with Structure
(s/def ::cat-example (s/cat :s string? :i int?))

(comment
  (s/valid? ::cat-example ["abc" 100])
  (s/valid? ::cat-example [100 "abc"])
  (s/valid? ::cat-example ["abc" 100])
  (s/conform ::cat-example ["abc" 100])
  )

(s/def ::alt-example (s/alt :i int? :k keyword?))

(comment
  (s/valid? ::alt-example [100])
  (s/valid? ::alt-example [:foo])
  (s/conform ::alt-example [:foo])
  )

;; Repetition Operators
(s/def ::oe (s/cat :odds (s/+ odd?)
                   :even (s/? even?)))

(comment
  (s/conform ::oe [1 3 5 100])
  )

(s/def ::odds (s/+ odd?))
(s/def ::optional-even (s/? even?))

(s/def ::oe2 (s/cat :odds ::odds
                    :even ::optional-even))

(comment
  (s/conform ::oe2 [1 3 5 100])
  )

;; Variable Argument Lists
(s/def ::println-args (s/* any?))

(comment
  (s/valid? ::println-args 1)
  (s/valid? ::println-args [] [])
  )

(clojure.set/intersection #{1 2} #{2 3} #{2 5})

;; Spec the args of intersection
(s/def ::intersection-args
  (s/cat :s1 set?
         :sets (s/* set?)))

(comment
  (s/conform ::intersection-args '[#{1 2} #{2 3} #{2 5}])
  (s/conform ::intersection-args '[#{1 :a} #{2 :a} #{2 :a}])
  )

(s/def ::intersection-args-2 (s/+ set?))

(comment
  (s/conform ::intersection-args-2 '[#{1 2} #{2 3} #{2 5}])
  (s/conform ::intersection-args-2 '[])
  )


(s/def ::meta map?)
(s/def ::validator ifn?)
(s/def ::atom-args
  (s/cat :x any? :options (s/keys* :opt-un [::meta ::validator])))

(comment
  (s/conform ::atom-args [100 :meta {:foo 1} :validator int?])
  )

;; Multi-arity Argument Lists
(s/def ::repeat-args
  (s/cat :n (s/? int?) :x any?))

(comment
  (s/conform ::repeat-args [100 "foo"])
  (s/conform ::repeat-args ["foo"])
  (s/conform ::repeat-args [])
  )

;; Specifying Functions
(s/def ::rand-args (s/cat :n (s/? number?)))
(s/def ::rand-ret double?)
(s/def ::rand-fn
  (fn [{:keys [args ret]}]
    (let [n (or (:n args) 1)]
      (cond (zero? n) (zero? ret)
            (pos? n) (and (>= ret 0) (< ret n))
            (neg? n) (and (<= ret 0) (> ret n))))))

(s/fdef clojure.core/rand
        :args ::rand-args
        :ret ::rand-ret
        :fn ::rand-fn)

;; Anonymous Functions
(defn opposite [pred]
  (comp not pred))

(s/def ::pred
  (s/fspec :args (s/cat :x any?)
           :ret boolean?))

(s/fdef opposite
        :args (s/cat :pred ::pred)
        :ret ::pred)

;; Instrumenting Functions
(stest/instrument 'clojure.core/rand)
(stest/instrument (stest/enumerate-namespace 'clojure.core))

(rand :boom)

;; Generative Function Testing

(s/fdef clojure.core/symbol
        :args (s/cat :ns (s/? string?) :name string?)
        :ret symbol?
        :fn (fn [{:keys [args ret]}]
              (and (= (name ret) (:name args))
                   (= (namespace ret) (:ns args)))))

(stest/check 'clojure.core/symbol)
















































