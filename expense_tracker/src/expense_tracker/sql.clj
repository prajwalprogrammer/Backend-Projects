(ns expense-tracker.sql
  (:require [next.jdbc :as jdbc]))


(def db-config
  {:dbtype "postgresql"
   :dbname "expense_tracker"
   :host "localhost"
   :user "postgres"
   :password "your_password"})

(def db (jdbc/get-datasource db-config))

(defn execute-query
  [query]
  (jdbc/execute! db query))

(defn add-expense
  [desc amount]
  (let [id (java.util.UUID/randomUUID)
        curr_time (.toString (java.util.Date.))]
    (execute-query
     ["INSERT INTO expenses 
       values
       (?, ?, ?, ?)" id curr_time desc amount])
    id))