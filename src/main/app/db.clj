(ns app.db
  (:require
    [datomic.api :as d]
  ))

(def db-uri "datomic:mem://todos")

(def todo-item-schema
  [{:db/ident       :todo-item/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Unique identifier for the todo item."}
   {:db/ident       :todo-item/value
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "The value or description of the todo item."}])

(def todo-list-schema
  [{:db/ident       :todo-list/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Unique identifier for the todo list."}
   {:db/ident       :todo-list/item-count
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "Count of items in the todo list."}
   {:db/ident       :todo-list/items
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "References to the todo items in the list."}])

(def schema (concat todo-item-schema todo-list-schema))

(def first-list [{:db/id (d/tempid :db.part/user)
                  :todo-list/id 1
                  :todo-list/item-count 0
                  :todo-list/items []}
])

(def first-todos [
    {:db/id (d/tempid :db.part/user) :todo-item/id 1 :todo-item/value "Ir a cenar"}
    {:db/id (d/tempid :db.part/user) :todo-item/id 2 :todo-item/value "Barrer la casa"}
    {:db/id (d/tempid :db.part/user) :todo-item/id 3 :todo-item/value "Tomar unas cervezas"}
])

(defn init-datomic [db-uri]
  (d/create-database db-uri)
  (d/connect db-uri))

(defonce conn (atom nil))

(defn start []
  (println "Starting database")
  (reset! conn (init-datomic db-uri))

  ;; Apply the schema
  (d/transact @conn schema)

  ;; Add the first list
  (d/transact @conn first-list)
)

(defn stop []
  ;; Your code to stop the database connection, if needed
  )
