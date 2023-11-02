(ns app.db
  (:require
  [datomic.api :as d]
  ))

(def db-uri "datomic:mem://todos")

(def todo-schema [
  {:db/ident :todo/value}
  {:db/valueType :db.type/string}
  {:db/cardinality :db.cardinality/one}
  {:db/doc "The value of the todo item"}
])

(def first-todos [
  {:todo/value "Ir a cenar"}
  {:todo/value "Barrer la casa"}
  {:todo/value "Tomar unas cervezas"}
])

(defn init-datomic [db-uri]
  (d/create-database db-uri)
  (d/connect db-uri))

(defonce conn (atom nil))

(defn start []
  (println "Starting database")
  (reset! conn (init-datomic db-uri))

  ;; Apply the schema
  (d/transact @conn todo-schema)

  ;; Insert the first todos
  (d/transact @conn first-todos)
)

(defn stop []
  ;; Your code to stop the database connection, if needed
  )
