(ns ru.nsu.fit.core (:gen-class))

(defn l-to-l-of-l [l]
    (reduce (fn [x y] (cons (list y) x)) () l)
)

(defn append [c ls]
    (map (fn [x] (concat x (list c))) (filter (fn [x] (not= c (last x))) ls))
)

(defn append-all [lc ls]
    (reduce (fn [x y] (concat x (append y ls))) () lc)
)

(defn cons-perms [n l]
    (reduce (fn [x y] (append-all l x)) (l-to-l-of-l l) (range (dec n)))
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (count(cons-perms 4 [ "a" "b" "c" "d"]))))

