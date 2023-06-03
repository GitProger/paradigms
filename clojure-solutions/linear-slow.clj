;; More detailed contracts

(defn vec? "Is a vector (seq of numbers)?" [v]
  (and (vector? v) (every? number? v)))

(defn matrix? "Is a matrix?" [m]
  (and (vector? m) (every? vec? m)
       (apply = (map count m))))

(defn matrix-n "Count of cols in a matrix" [m] {:pre [(matrix? m)]} (count (first m)))
(defn matrix-m "Count of rows in a matrix" [m] {:pre [(matrix? m)]} (count m))

(defn consistent? "Are matrices m1 and m2 consistent" [m1 m2]
  (= (matrix-n m1) (matrix-m m2)))

(defn same-sized "Are matrices m1 and m2 of the same dimensions" [m1 m2]
  (and (= (matrix-n m1) (matrix-n m2))
       (= (matrix-m m1) (matrix-m m2))))


(defn v-operator [f]
  (fn [& vs]
    {:pre  [(matrix? (vec vs))]
     :post [(apply = (map count vs))]}
    (apply (partial mapv f) vs)))

(def v+ "coord-by-coord +" (v-operator +))
(def v- "coord-by-coord -" (v-operator -))
(def v* "coord-by-coord *" (v-operator *))
(def vd "coord-by-coord /" (v-operator /))

(defn v*s "Vector multiplied by scalars" [v & cs]
  {:pre  [(vec? v)]
   :post [(= (count v) (count %))]}
  (let [c (apply * cs)]
    (mapv (partial * c) v)))

(defn scalar "dot product" [& vs] (apply + (apply v* vs)))

(defn vect "cross product"
  ([v]
   {:pre  [(vec? v)]
    :post [(= v %)]}
   v)
  ([v1 v2]
   {:pre  [(vec? v1) (vec? v2) (= 3 (count v1)) (= 3 (count v2))]
    :post [(= 3 (count %))]}
   [(- (* (nth v1 1) (nth v2 2)) (* (nth v1 2) (nth v2 1)))
    (- (* (nth v1 2) (nth v2 0)) (* (nth v1 0) (nth v2 2)))
    (- (* (nth v1 0) (nth v2 1)) (* (nth v1 1) (nth v2 0)))])
  ([v1 v2 & vs]
   (reduce vect (conj vs v2 v1))))



(defn m-operator [f]
  (fn [& ms]
    {:pre  [(every? #(same-sized (first ms) %) ms)]
     :post [(same-sized (first ms) %)]}
    (apply (partial mapv (v-operator f)) ms)))

(def m+ "element-by-element +" (m-operator +))
(def m- "element-by-element -" (m-operator -))
(def m* "element-by-element *" (m-operator *))
(def md "element-by-element /" (m-operator /))

;; [[]] -> []
(defn transpose "Transposed matrix" [m]
  {:pre [(matrix? m)]}
  ;:post [(= (matrix-m m) (matrix-n %))   ; Because of specific [] processing [[]] -> []
  ;       (= (matrix-n m) (matrix-m %))]} ; So matrix -> vector, in maths empty matrix is incorrect
  (apply mapv vector m))

(defn m*s "Matrix multiplied by scalars" [m & cs]
  {:pre  [(matrix? m) (vec? (vec cs))]
   :post [(same-sized m %)]}
  (let [c (apply * cs)]
    (mapv #(v*s % c) m)))

(defn m*v "Matrix multiplied by a vector" [m v]
  {:pre  [(matrix? m) (vec? v) (= (count v) (matrix-n m))]
   :post [(= (count %) (matrix-m m))]}
  (mapv #(scalar % v) m))

(defn m*m "Matrix multiplied by a matrix"
  ([m]
   {:pre [(matrix? m)] :post [(= m %)]}
   m)
  ([m1 m2]
   {:pre  [(consistent? m1 m2)]
    :post [(= (matrix-n %) (matrix-n m2))
           (= (matrix-m %) (matrix-m m1))]}
   (transpose (mapv #(m*v m1 %) (transpose m2))))
  ([m1 m2 & ms]
   (reduce m*m (conj ms m2 m1))))

;; Tensors

(defn tensor? [t]
  (or
    (number? t)
    (vec? t)
    (and
      (apply = (map count t))
      (every? tensor? t))))

(defn tensor-form [t]
  {:pre [(tensor? t)]}
  (cond
    (number? t) ()
    (empty? t) '(0)
    :else (cons
            (count t)
            (tensor-form (first t)))))

(defn forms-cons "form 1 can be expanded to form 2" [fa fb]
  (= fa (take (count fa) fb)))

(defn broadcast-1 "tensor with form (...f) to tensor with form (...f r)" [t r]
  {:pre [(tensor? t) (>= r 0)]}
  (if (number? t)
    (vec (repeat r t))
    (mapv #(broadcast-1 % r) t)))

(defn broadcast "Expand tensor t to form new-f" [t new-f]
  {:pre [(tensor? t) (forms-cons (tensor-form t) new-f)]}
  (let [f (tensor-form t)
        actions (take-last (- (count new-f) (count f)) new-f)]
    (reduce broadcast-1 t actions)))



(defn unify "Transforms all tensors to the same form" [& ts]
  {:pre [(every? tensor? ts)]
   :post [(apply = (map tensor-form %))]}
  (let [f (apply max-key count (map tensor-form ts))]
    (map #(broadcast % f) ts)))

(defn tb-operator [f]
  (letfn [(opr [& ts]
            {:pre [(every? tensor? ts)]}
            (if (vec? (vec ts))
              (apply f ts)
              (mapv
                (fn [i]
                  (apply opr (mapv #(nth % i) ts)))
                (range (count (first ts))))))]
    (fn [& ts] (apply opr (apply unify ts)))))              ; opr)) ; for same-formed tensors

(def tb+ "element-by-element +" (tb-operator +))
(def tb- "element-by-element -" (tb-operator -))
(def tb* "element-by-element *" (tb-operator *))
(def tbd "element-by-element /" (tb-operator /))

