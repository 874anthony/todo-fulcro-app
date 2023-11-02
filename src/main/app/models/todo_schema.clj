(ns app.models.todo-schema)

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