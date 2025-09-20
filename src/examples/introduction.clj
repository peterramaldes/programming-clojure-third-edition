(ns examples.introduction)

(def fibs (lazy-cat [0 1] (map + fibs (rest fibs))))

(def visitors (atom #{}))

(defn hello
  "Writes hello message to *out*. Calls you by username. Knows if your have been
  here before."
  [username]
  (swap! visitors conj username)
  (str "Hello, " username))
