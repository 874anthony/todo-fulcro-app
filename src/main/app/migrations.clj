(ns app.migrations
  (:require [datomic.api :as d]))

(def todo-schema [
  {:db/ident :todo/value}
  {:db/valueType :db.type/string}
  {:db/cardinality :db.cardinality/one}
  {:db/doc "The value of the todo item"}
])

(defn apply-schema
  [connection]
  (d/transact connection todo-schema))
