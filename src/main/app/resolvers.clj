(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]))

(def todos-table
  (atom
    {1 {:todo-item/id 1 :todo-item/value "Ir a cenar"}
     2 {:todo-item/id 2 :todo-item/value "Barrer la casa"}
     3 {:todo-item/id 3 :todo-item/value "Tomar unas cervezas"}}))

(def list-table
  (atom
    {:todo-list/id 1
     :todo-list/item-count 3
     :todo-list/items [1 2 3]
   }))

;; Given :todo-item/id, this can generate the details of a todo item
(pc/defresolver todo-resolver [env {:todo-item/keys [id]}]
                {::pc/input  #{:todo-item/id}
                 ::pc/output [:todo-item/id :todo-item/value]}
                (get @todos-table id))

;; Given :root/todo-list, this can generate a list item-count and the todo items
(pc/defresolver todo-list-resolver [env {:keys [:root/todo-list]}]
                {::pc/input  #{:root/todo-list}
                 ::pc/output [:todo-list/id :todo-list/item-count {:todo-list/items [:todo-item/id :todo-item/value]}]}
                (let [items (mapv (fn [id] (get @todos-table id)) (:todo-list/items @list-table))]
                  {:todo-list/item-count (count items)
                   :todo-list/items items}))


;; Root resolver to connect todo-list and todo-item to the root
(pc/defresolver root-resolver [env _]
                {::pc/output [:root/todo-list]}
                {:root/todo-list {:todo-list/id 1}})


(def resolvers [todo-resolver todo-list-resolver root-resolver])