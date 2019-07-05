(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [clojure.repl :refer :all :exclude [root-cause]]
            [clojure.pprint :refer :all]
            [clojure.string :as str]
            [proto-repl.saved-values]
            [loom.graph :as g]
            [loom.attr :as ga]
            [loom.io :as gio :refer [dot view]]))

(defn start
  []
  (log/info "REPL started."))

(defn refresh
  []
  (ctnr/refresh :after 'user/start))

(defn refresh-all
  []
  (ctnr/refresh-all :after 'user/start))

(defmacro save
  [type & args]
  `(let [res# (~type ~@args)]
     (cond
       (g/graph? res#)
       (dot res# ~(str "../paper/data/generated/" type ".dot"))
       (string? res#)
       (spit ~(str "../paper/data/generated/" type ".svg") res#))))

(defn sample
  [f start end step]
  (map f (range start end step)))

(defn svg-bars
  [vals]
  (let [spacing 5
        low (apply min 0 vals)
        high (apply max 0 vals)
        width (* spacing (count vals))
        height (- high low)
        start (str "<svg height=\"" height "\" width=\"" width "\">")
        end "</svg>"
        bars (map-indexed (fn [i v]
                            (let [x (+ (* spacing i) 2)]
                              (str "<line x1=\"" x "\" y1=\"" high "\" x2=\"" x "\" y2=\"" (- high v) "\""
                                   "style=\"stroke:rgb(0,0,0);stroke-width:3;\" />")))
                          vals)]
    (str start (str/join "\n" bars) end)))

;; Generated:

(defn two-cluster
  ([]
   (two-cluster 1000 3000 0.02))
  ([n e p]
   (let [nodes (range n)
         p1 (vec (range (/ n 2)))
         p2 (vec (range (/ n 2) n))]
     (as-> (g/graph) $
       (apply g/add-nodes $ nodes)
       (ga/add-attr-to-nodes $ :cluster 1 p1)
       (ga/add-attr-to-nodes $ :cluster 2 p2)
       (reduce (fn [g _]
                 (if (< (rand) p)
                   (g/add-edges g [(rand-nth p1) (rand-nth p2)])
                   (if (< (rand) 0.5)
                     (g/add-edges g [(rand-nth p1) (rand-nth p1)])
                     (g/add-edges g [(rand-nth p2) (rand-nth p2)]))))
               $ (range e))))))

(defn small-two-cluster
  []
  (two-cluster 50 150 0.02))

(defn save-graphs
  []
  (save small-two-cluster))

(defn cos-sample
  [n freq]
  (sample #(* 200 (- (Math/cos (+ (* 2 Math/PI freq %) 0)))) 0 1 (/ 1 n)))

(defn cos-bars-0
  []
  (svg-bars (map #(+ % 100) (cos-sample 150 0))))

(defn cos-bars-1
  []
  (svg-bars (map #(- % 10) (cos-sample 150 0.5))))

(defn cos-bars-10
  []
  (svg-bars (map #(- % 10) (cos-sample 150 5))))

(defn save-bars
  []
  (save cos-bars-0)
  (save cos-bars-1)
  (save cos-bars-10))

(save-bars)
