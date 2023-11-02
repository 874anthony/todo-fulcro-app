(ns app.resolvers
  (:require
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [datomic.api :as d]
    [app.db :as db]
  ))

;; Given :root/todo-list, this can generate a list item-count and the todo items
(pc/defresolver todo-list-resolver [env {:keys [:root/todo-list]}]
                {::pc/input  #{:root/todo-list}
                 ::pc/output [:todo-list/id :todo-list/item-count {:todo-list/items [:todo-item/id :todo-item/value]}]}

                (let [db (d/db @db/conn)
                      todo-list-eid (ffirst (d/q '[:find ?eid
                                           :in $ ?id
                                           :where
                                           [?eid :todo-list/id ?id]]
                                           db 1))
                      todo-items-eids (vec (map first  (d/q '[:find ?item
                                             :in $ ?list-id
                                             :where
                                             [?list-id :todo-list/items ?item]]
                                           db todo-list-eid)))
                      todo-items (mapv (fn [todo-item-eid]
                                         (let [result (d/q '[:find ?id ?value
                                                             :in $ ?eid
                                                             :where
                                                             [?eid :todo-item/id ?id]
                                                             [?eid :todo-item/value ?value]]
                                                           (d/db @app.db/conn) todo-item-eid)
                                               item (first result)
                                               id (first item)
                                               value (second item)]
                                           {:todo-item/id id
                                            :todo-item/value value}
                                           )) todo-items-eids)]
                    {:todo-list/item-count (count todo-items)
                     :todo-list/items todo-items}))


;; Root resolver to connect todo-list and todo-item to the root
(pc/defresolver root-resolver [env _]
                {::pc/output [:root/todo-list]}
                {:root/todo-list {:todo-list/id 1}})


(def resolvers [todo-list-resolver root-resolver])