(ns app.db
  (:require [datomic.api :as d]))

(def db-uri "datomic:mem://todos")

(defn init-datomic [db-uri]
  (d/create-database db-uri)
  (d/connect db-uri))

(defonce conn (atom nil))

(defn start []
  (reset! conn (init-datomic db-uri)))

(defn stop []
  ;; Your code to stop the database connection, if needed
  )
