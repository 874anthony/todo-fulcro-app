(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.dom :as dom]))

(defsc TodoAppTitle
  [this props]
  (dom/div {:style {:display "flex"
                    :justifyContent "center"
                    :alignItems "center"
                    :flexDirection "column"
                    :gap "8px"}}
           (dom/h1 {:style {
                            :fontFamily "sans-serif"
                            :fontSize "36px"
                            :fontWeight "bold"
                            :textTransform "uppercase"
                            :letterSpacing "-.5px"
                            }} "Todo App" )
           (dom/p {:style {
                           :fontFamily "sans-serif"
                           :fontSize "24px"
                           :fontWeight "normal"
                           }} "This is a simple todo app built with Fulcro, made by Anthony Acosta M. (@874anthony)")))


(def ui-todo-app-title (comp/factory TodoAppTitle))

(defmutation delete-todo [{:keys [list-id todo-id]}]
  (action
    [{:keys [state]}]
    (let [current-todos (get-in @state [:root/todo-list :todo-list/items])]
      (println "Before:" current-todos)
      (swap! state update-in [:root/todo-list :todo-list/items]
             #(vec (remove (fn [item] (prn "Item" (:todo-item/id item) todo-id) (= (:todo-item/id item) todo-id)) %)) )
      (println "After:" (get-in @state [:root/todo-list :todo-list/items]))))
  (remote [env] (m/with-params env {:todo-list/id list-id, :todo-item/id todo-id})))

(defmutation edit-todo [{:keys [id new-value]}]
  (action [{:keys [state]}]
          (swap! state assoc-in [:root/todo-list :todo-list/items (dec id) :todo-item/value] new-value)))

(defsc TodoItem
  [this {:todo-item/keys [id value]}]
  {:query [:todo-item/id
           :todo-item/value]
   :ident :todo-item/id
   :initLocalState (fn [this {:todo-item/keys [id value]}]
                    {
                     :id id
                     :edit-value value
                     :editing? false
                     :on-edit-change (fn [e]
                                       (comp/set-state! this (assoc (comp/get-state this) :edit-value (.. e -target -value))))
                     :on-edit-ok     (fn [_]
                                       (comp/transact! this [(edit-todo {:id id :new-value (comp/get-state this :edit-value)})])
                                       (comp/set-state! this (assoc (comp/get-state this) :editing? false)))
                     :on-edit-click #(comp/set-state! this (assoc (comp/get-state this) :editing? true))
                     :on-delete     #(comp/transact! this [(delete-todo {:list-id 1, :todo-id id})])
                     })
  }
  (let [{:keys [editing? on-edit-click edit-value on-edit-change on-edit-ok on-delete]} (comp/get-state this)]
    (if editing?
     (dom/li
         (dom/input {:type "text"
                     :value edit-value
                     :onChange on-edit-change})
         (dom/button {:onClick on-edit-ok} "OK")
         (dom/button  "Cancel"))
     (dom/li {:style {:display "flex"
                :flexDirection "row"
                :justifyContent "center"
                :alignItems "center"
                :gap "8px"}}
       (dom/p {:style {
                       :fontFamily "sans-serif"
                       :fontSize "16px"
                       :fontWeight "normal"
                       }} edit-value)
       (dom/button {:style {:border "none" :padding "4px 8px" :backgroundColor "cyan" :borderRadius "10px"} :onClick on-edit-click} "Edit")
       (dom/button {:style {:border "none" :padding "4px 8px" :backgroundColor "red" :borderRadius "10px"} :onClick on-delete} "Delete")
     )))
  )

(def ui-todo-item (comp/factory TodoItem {:keyfn :todo-item/id}))

(defmutation add-todo [{:keys [list-id value]}]
  (action
    [{:keys [state]}]
    (let [new-id (inc (get-in @state [:root/todo-list :todo-list/item-count]))
          new-item {:todo-item/id new-id :todo-item/value value}]
      (swap! state update-in [:root/todo-list :todo-list/items] conj new-item)
      (swap! state update-in [:root/todo-list :todo-list/item-count] inc)
      ))
   (remote [env] (m/with-params env {:todo-list/id list-id, :todo-item/value value}))
  )

(defsc TodoInput
  [this {:todo-input/keys [value]}]
  {:query [:todo-input/value]
   :initial-state {:todo-input/value ""}
   :initLocalState (fn [this {:todo-input/keys [value]}]
                     {:input-value value
                      :on-change (fn [e]
                                   (comp/set-state! this (assoc (comp/get-state this) :input-value (.. e -target -value))))
                      :on-add (fn [e]
                                (comp/transact! this [(add-todo {:list-id 1 :value (comp/get-state this :input-value)})])
                                (comp/set-state! this (assoc (comp/get-state this) :input-value "")))
                      })
   }
  (let [{:keys [on-change on-add input-value]} (comp/get-state this)]
    (dom/div {:style {:display "flex"
                      :flexDirection "row"
                      :gap "8px"}}
             (dom/input {:type "text"
                         :value input-value
                         :onChange on-change
                         :onKeyDown (fn [e]
                                      (when (= (.-keyCode e) 13)
                                        (on-add e)
                                        ))
                         :placeholder "Add a new todo item"
                         :style {:padding "8px"
                                 :margin "0 auto"
                                 :border "1px solid #ccc"
                                 :borderRadius "4px"
                                 :fontFamily "sans-serif"
                                 :fontSize "16px"
                                 :fontWeight "normal"}}))))

(def ui-todo-input (comp/factory TodoInput))

(defsc TodoList
  [this {:todo-list/keys [items]}]
  {:query [:todo-list/id
           :todo-list/item-count
           {:todo-list/items (comp/get-query TodoItem)}]
   :initial-state {}
  }
  (dom/div {:style {:display "flex"
                    :alignItems "center"
                    :flexDirection "column"
                    :gap "8px"}}
  (when (not-empty items)
   (dom/ul (map ui-todo-item items))
  )))

(def ui-todo-list (comp/factory TodoList))

(defsc Root
  [this {:root/keys [todo-list]}]
  {:query [{:root/todo-list (comp/get-query TodoList)}]
   :initial-state {}
   :componentDidMount (fn [this]
                        (df/load! this :root/todo-list TodoList))
  }
  (dom/div {:style {:margin "0 auto"
                    :backgroundColor "#f5f5f5"
                    :textAlign "center"
                    :padding "20px"}}
           (ui-todo-app-title)
           (ui-todo-input)
           (ui-todo-list todo-list)
       ))