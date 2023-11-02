(ns app.mutations
  (:require
    [app.resolvers :refer [list-table todos-table]]
    [app.db :as db :refer [conn]]
    [datomic.api :as d]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(pc/defmutation delete-todo [env {list-id :todo-list/id todo-id :todo-item/id}]
                {::pc/sym `app.ui/delete-todo}
                (log/info "Deleting todo" todo-id "from list" list-id)

                ;; Remove the todo item from the todos-table
                (swap! todos-table dissoc todo-id)

                ;; Remove the todo-id from the :todo-list/items in list-table
                (swap! list-table update :todo-list/items #(remove #{todo-id} %))

                ;; Update the item count
                (swap! list-table update :todo-list/item-count dec))

(pc/defmutation add-todo [env {list-id :todo-list/id todo-value :todo-item/value}]
                {::pc/sym `app.ui/add-todo}
                (log/info "Adding todo" todo-value "to list" list-id)

                ;; Add the todo item to the todos-table
                (let [new-id (inc (count @todos-table))]
                  (swap! todos-table assoc new-id {:todo-item/id new-id :todo-item/value todo-value})

                ;; Add the todo-id to the :todo-list/items in list-table
                  (swap! list-table update :todo-list/items conj new-id))

                ;; Update the item count
                (swap! list-table update :todo-list/item-count inc))

(pc/defmutation edit-todo [env {todo-id :todo-item/id new-value :todo-item/value}]
                {::pc/sym `app.ui/edit-todo}
                (log/info "Editing todo" todo-id "to" new-value)

                ;; Update the todo item in the todos-table
                (swap! todos-table update todo-id assoc :todo-item/value new-value))

(def mutations [delete-todo add-todo edit-todo])