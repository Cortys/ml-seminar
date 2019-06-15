(ns user
  "REPL startup namespace that contains various helper methods to interact with the application."
  (:require [clojure.tools.namespace.repl :as ctnr]
            [clojure.tools.logging :as log]
            [clojure.stacktrace :refer :all]
            [clojure.repl :refer :all :exclude [root-cause]]
            [clojure.pprint :refer :all]
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

(defmacro save
  [type & args]
  `(dot (~type ~@args) ~(str "../paper/data/generated/" type ".dot")))

(save small-two-cluster)
