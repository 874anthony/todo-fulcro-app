(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]
    [com.fulcrologic.fulcro.dom :as dom]))

(defmutation set-input-value! [[new-value]]
             (action [{:keys [state]}]
                     (swap! state assoc :input-value new-value)))

(defsc TodoAppTitle
  [this props]
    (dom/div {:style {:display "flex"
                      :justify-content "center"
                      :align-items "center"
                      :flex-direction "column"
                      :gap "8px"}}
      (dom/h1 {:style {
        :font-family "sans-serif"
        :font-size "36px"
        :font-weight "bold"
        :text-transform "uppercase"
        :letter-spacing "-.5px"
      }} "Todo App" )
      (dom/p {:style {
        :font-family "sans-serif"
        :font-size "24px"
        :font-weight "normal"
      }} "This is a simple todo app built with Fulcro, made by Anthony Acosta M. (@874anthony)")))


(def ui-todo-app-title (comp/factory TodoAppTitle))

(defsc AddTodoItem
  [this {:keys [input-value]}]
  (dom/div {:style {:display "flex"
                    :flex-direction "row"
                    :gap "8px"}}
    (dom/input {:type "text"
                :value input-value
                :onChange #(comp/transact! this [(:set-input-value! [(.. % -target -value)])])
                :placeholder "Add a new todo item"
                :style {:padding "8px"
                        :border "1px solid #ccc"
                        :border-radius "4px"
                        :font-family "sans-serif"
                        :font-size "16px"
                        :font-weight "normal"}})))

(def ui-add-todo-item (comp/factory AddTodoItem))

(defsc TodoItem
  [this props]
  (dom/div {:style {:display "flex"
                    :flex-direction "row"
                    :gap "8px"}}
           (dom/input {:type "checkbox"})
           (dom/p {:style {
                           :font-family "sans-serif"
                           :font-size "16px"
                           :font-weight "normal"
                           }} "Todo item 1")))

(def ui-todo-item (comp/factory TodoItem))

(defsc TodoItemList
  [this props]
  (dom/div {:style {:display "flex"
                    :align-items "center"
                    :flex-direction "column"
                    :gap "8px"}}
    (ui-add-todo-item)
    (ui-todo-item)))

(def ui-todo-item-list (comp/factory TodoItemList))

(defsc Root
  [this props]
   (dom/div {:style {:margin "0 auto"
                     :background-color "#f5f5f5"
                     :text-align "center"
                     :padding "20px"}}
     (ui-todo-app-title)
     (ui-todo-item-list)
     ))