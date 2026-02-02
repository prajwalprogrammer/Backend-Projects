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
  (first (jdbc/execute! db query)))

(defn execute!
  [query]
  (jdbc/execute! db query))

(defn add-expense
  [desc amount]
  (let [curr_time  (.getTime (java.util.Date.))
        ts (java.sql.Timestamp. curr_time)
        result (execute-query
                ["INSERT INTO expenses
                  (description , amount, time)
                  VALUES (?, ?, ?) RETURNING id"
                 desc amount ts])]
    (get result :expenses/id)))

(defn update-expense
  [id body]
  (let [{:keys [description amount]} body]
    (execute-query
     ["UPDATE expenses
       SET description = ?, amount = ?
       WHERE id = ?"
      description amount id])))

(defn delete-expense
  [id]
  (execute-query
   ["DELETE FROM expenses
     WHERE
     id = ?" id]))

(defn get-all-expenses
  []
  (execute!
   ["SELECT * FROM expenses"]))