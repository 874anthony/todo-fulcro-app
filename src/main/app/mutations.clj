(ns app.mutations
  (:require
    [app.resolvers :refer [list-table todos-table]]
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

(def mutations [delete-todo])