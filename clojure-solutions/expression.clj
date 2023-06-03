;; Functional Expressions

; Fixing https://clojure.atlassian.net/browse/CLJ-2244
(defn safe-div
  ([x] (/ 1.0 x))
  ([x & xs] (/ x (double (apply * xs)))))

(defn create-oper [f]
  (fn [& expressions]
    (fn [variable-map]
      (apply f (map #(% variable-map) expressions)))))      ; % - expression (function)

(defn variable [name]
  (fn [variable-map]
    (get variable-map name)))

(def constant constantly)
(def add (create-oper +))
(def subtract (create-oper -))
(def multiply (create-oper *))
(def divide (create-oper safe-div))
(def negate (create-oper -))

(defn meansq-base [& xs]
  (/ (apply + (map #(* % %) xs)) (count xs)))

(defn rms-base [& xs]
  (Math/sqrt (apply meansq-base xs)))

(def meansq (create-oper meansq-base))
(def rms (create-oper rms-base))

(def fun-map
  {'+      add,
   '-      subtract,
   '*      multiply,
   '/      divide,
   'negate negate,
   'meansq meansq,
   'rms    rms})

(defn gen-expr-parser [symbol-map gen-var gen-const]
  (letfn [(parse-expr [e]
            {:post [(some? e)]}
            (cond
              (symbol? e) (gen-var (name e))
              (number? e) (gen-const (num e))
              (list? e) (apply
                          (get symbol-map (first e))
                          (map parse-expr (rest e)))))]
    parse-expr))

(def parseFunction (comp (gen-expr-parser fun-map variable constant) read-string))

;; =====================================================================================================================
;; =====================================================================================================================
;; =====================================================================================================================
;; Object Expressions <My proto>
(def method-prefix "")                                      ; _
(def field-prefix "")                                       ; __

(defn proto-get
  "Returns object property respecting the prototype chain"
  ([obj key] (proto-get obj key nil))
  ([obj key default]
   (cond
     (contains? obj key) (obj key)
     (contains? obj :prototype) (proto-get (obj :prototype) key default)
     :else default)))

(defn proto-call
  "Calls object method respecting the prototype chain"
  [this key & args]
  (apply (proto-get this key) this args))

(defn field
  "Creates field"
  [key] (fn
          ([this] (proto-get this key))
          ([this def] (proto-get this key def))))

(defn method
  "Creates method"
  [key] (fn [this & args] (apply proto-call this key args)))

(defn constructor
  "Defines constructor"
  [ctor prototype]
  (fn [& args] (apply ctor {:prototype prototype} args)))


; Macros

(defn to-symbol [prefix name] (symbol (str prefix name)))

(defmacro deffield
  "Defines field"
  [name]
  `(def ~(to-symbol field-prefix name) (field ~(keyword name))))

(defmacro deffields
  "Defines multiple fields"
  [& names]
  `(do ~@(map (fn [name] `(deffield ~name)) names)))

(defmacro defmethod
  "Defines method"
  [name]
  `(def ~(to-symbol method-prefix name) (method ~(keyword name))))

(defmacro defmethods
  "Defines multiple methods"
  [& names]
  `(do ~@(map (fn [name] `(defmethod ~name)) names)))

(defmacro defconstructor
  "Defines constructor"
  [name fields prototype]
  `(do
     (deffields ~@(map symbol fields))
     (defn ~name ~fields
       (assoc {:prototype ~prototype}
         ~@(mapcat (fn [f] [(keyword f) f]) fields)))))

(defmacro defclass
  "Defines class"
  [name super fields & methods]
  (let [-name (fn [suffix] (fn [class] (symbol (str class "_" suffix))))
        proto-name (-name "proto")
        fields-name (-name "fields")
        method (fn [[name args body]] [(keyword name) `(fn [~'this ~@args] ~body)])
        base-proto (if (= '_ super) {} {:prototype (proto-name super)})
        prototype (apply assoc base-proto (mapcat method methods))
        public-prototype (proto-name name)
        public-fields (fields-name name)
        super-fields (if (= '_ super) [] (eval (fields-name super)))
        all-fields (into super-fields fields)]
    `(do
       (defmethods ~@(map (comp symbol first) methods))
       (deffields ~@(map symbol fields))
       (def ~public-prototype ~prototype)
       (def ~public-fields '~all-fields)
       (defconstructor ~name ~all-fields ~public-prototype))))
;; =====================================================================================================================
;; Object Expressions

(declare Constant Add Subtract Multiply Divide Negate)

(defclass OperationClass _
          [oper fun dif-rule exprs]
          (toString []
                    (str "(" (oper this) " " (clojure.string/join " " (map toString (exprs this))) ")"))
          (toStringInfix
            []
            (cond
              (= (count (exprs this)) 1) (str (oper this) " " (toStringInfix (first (exprs this)))) ; was (str (oper this) "(" (toStringInfix (first (exprs this))) ")")
              (= (count (exprs this)) 2) (str "("
                                              (toStringInfix (first (exprs this)))
                                              " " (oper this) " "
                                              (toStringInfix (second (exprs this)))
                                              ")")
              :else nil))
          (evaluate [var-map]
                    (apply (fun this) (map #(evaluate % var-map) (exprs this))))
          (diff [var] ((dif-rule this) this var)))


(defclass ConstantClass OperationClass [value]
          (toString [] (str (value this)))
          (toStringInfix [] (toString this))
          (evaluate [var-map] (value this))
          (diff [var] (Constant 0)))
(defn Constant [val] (ConstantClass "const" nil nil nil val))

(defn simplify-name [name]
  (str (Character/toLowerCase (first name))))

(defclass VariableClass OperationClass [var-name]
          (toString [] (str (var-name this)))
          (toStringInfix [] (toString this))
          (evaluate [var-map] (get var-map (simplify-name (var-name this))))
          (diff [var]
                (Constant (if (= var (var-name this)) 1 0))))

(defn Variable [name] (VariableClass "var" nil nil nil name))

(defn sumexp [& xs]
  (apply + (map #(Math/exp %) xs)))
(defn lse [& xs]
  (Math/log (apply sumexp xs)))


(declare Add-dif-rule Sub-dif-rule Mul-dif-rule Div-dif-rule Neg-dif-rule Sumexp-dif-rule Lse-dif-rule)

(defn Add [& exprs] (OperationClass "+" + Add-dif-rule exprs))
(defn Subtract [& exprs] (OperationClass "-" - Sub-dif-rule exprs))
(defn Multiply [& exprs] (OperationClass "*" * Mul-dif-rule exprs))
(defn Divide [& exprs] (OperationClass "/" safe-div Div-dif-rule exprs))
(defn Negate [& exprs] (OperationClass "negate" - Neg-dif-rule exprs))
(defn Sumexp [& exprs] (OperationClass "sumexp" sumexp Sumexp-dif-rule exprs))
(defn LSE [& exprs] (OperationClass "lse" lse Lse-dif-rule exprs))


(defn bool->int [b] (if b 1 0))
(defn int->bool [b] (if (> b 0) true false))

(defn And [& exprs] (OperationClass "&&"
                                    (fn [& es] (bool->int (every? true? (map int->bool es))))
                                    (constantly nil)
                                    exprs))
(defn Or [& exprs] (OperationClass "||"
                                   (fn [& es] (bool->int (some true? (map int->bool es))))
                                   (constantly nil)
                                   exprs))
(defn Xor [& exprs] (OperationClass "^^"
                                    (fn [& es] (apply bit-xor (map (comp bool->int int->bool) es)))
                                    (constantly nil)
                                    exprs))
(defn Not [e] (OperationClass "!"
                              (fn [e] (bool->int (not (int->bool e))))
                              (constantly nil)
                              (list e)))
(defn Impl [e1 e2] (OperationClass "->"
                                   (fn [e1 e2] (bool->int
                                                 (not (and
                                                        (int->bool e1)
                                                        (not (int->bool e2))))))
                                   (constantly nil)
                                   (list e1 e2)))
(defn Iff [& exprs] (OperationClass "<->"
                                    (fn [& es] (bool->int (apply = (map int->bool es))))
                                    (constantly nil)
                                    exprs))


(defn Add-dif-rule [this var]
  (apply Add (map #(diff % var) (exprs this))))
(defn Sub-dif-rule [this var]
  (apply Subtract (map #(diff % var) (exprs this))))

(defn Mul-dif-rule [this var]
  (let [e (exprs this)]
    (apply Add (map
                 (fn [i]
                   (apply Multiply
                          (concat (take i e)
                                  (cons
                                    (diff (nth e i) var)
                                    (drop (inc i) e)))))
                 (range (count e))))))

(defn Div-dif-rule [this var]
  (let [e (exprs this) n (count e)
        u (if (= n 1) (Constant 1) (first e))
        v (if (= n 1) (first e) (apply Multiply (rest e)))]
    (Divide
      (Subtract
        (Multiply (diff u var) v)
        (Multiply u (diff v var)))
      (Multiply v v))))

(def Neg-dif-rule Sub-dif-rule)

(defn Sumexp-dif-rule [this var]
  (let [e (exprs this)]
    (apply Add
           (map
             #(Multiply (Sumexp %) (diff % var))
             e))))

(defn Lse-dif-rule [this var]
  (let [e (exprs this) s (apply Sumexp e)]
    (Multiply
      (Divide (Constant 1) s)
      (diff s var))))

(def obj-map
  {'+             Add
   '-             Subtract
   '*             Multiply
   '/             Divide
   'negate        Negate
   'sumexp        Sumexp
   'lse           LSE

   '&&            And
   '||            Or
   (symbol "^^") Xor
   '!             Not
   '->            Impl
   '<->           Iff})

(def parseObject (comp (gen-expr-parser obj-map Variable Constant) read-string))

;; =====================================================================================================================
;; =====================================================================================================================
;; =====================================================================================================================
;; Parser Expressions
(load-file "parser.clj")

(defparser parseObjectInfix
           *all-chars (mapv char (range 0 128))
           (*chars [p] (+char (apply str (filter p *all-chars))))
           *letter (*chars #(Character/isLetter %))
           *digit (*chars #(Character/isDigit %))
           *space (*chars #(Character/isWhitespace %))
           *ws (+ignore (+star *space))
           *number (+map read-string (+str (+seq (+opt (+char "-"))
                                                 (+str (+plus *digit))
                                                 (+str (+opt (+seq
                                                               (+char ".")
                                                               (+str (+star *digit))))))))
           *const (+map Constant *number)
           *var (+map Variable (+str (+plus (+char "XxYyZz"))))


           (*op [operators]
                (+map obj-map
                      (apply +or (mapv
                                   (fn [id]
                                     (apply +seqf (constantly id)
                                            (map #(+char (str %)) (str id))))
                                   operators))))

           *argument (+or *const *var (+seqn 1 \( (delay *parseObjectInfix) \)) (delay *unary-op))
           *unary-op (+seqf #(% %2) (*op ['negate '!]) *ws *argument)

           (*wrap [order]
                  (fn [& operands]
                    (letfn [(wrap
                              ([x] x)
                              ([x op y & rest]
                               (apply wrap (apply op (order [x y])) rest)))]
                      (apply wrap (order (flatten operands))))))

           (*binary-op [order]
                       (fn [sub & cur-priotity-opers]
                         (+seqf (*wrap order)
                                sub
                                (+star
                                  (+seq *ws (*op cur-priotity-opers) *ws sub)))))

           *left-prior-op (*binary-op identity)
           *right-right-prior (*binary-op reverse)

           *muls (*left-prior-op *argument '* '/)
           *adds (*left-prior-op *muls '+ '-)
           *and (*left-prior-op *adds '&&)
           *or (*left-prior-op *and '||)
           *xor (*left-prior-op *or (symbol "^^"))
           *impl (*right-right-prior *xor '->)
           *iff (*left-prior-op *impl '<->)

           *parseObjectInfix (+seqn 0 *ws *iff *ws))
