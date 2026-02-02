(ns expense-tracker.core
  (:require
   [cheshire.core :as json]
   [compojure.core :refer [defroutes GET POST PUT DELETE]]
   [compojure.route :refer [not-found]]
   [expense-tracker.sql :as sql]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]])
  (:gen-class))

(defn parse-int [number-string]
  (try (Integer/parseInt number-string)
       (catch Exception _e)))

(defn add-expense
  "Handler to add expense"
  [{:keys [body]}]
  (let [desciption (:description body)
        amount (:amount body)
        resp-id (sql/add-expense desciption amount)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:message "Expense Added successfully"
                                  :id resp-id})}))

(defn update-expense
  "Handler to update the expense"
  [{:keys [body] :as req}]
  (let [{:keys [id]} (:params req)]
    (sql/update-expense (parse-int id) body)
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:message "Expense updated"})}))

(defn modify-expenses
  [expenses]
  (mapv (fn [row]
          (into {} (map (fn [[k v]] [(keyword (name k)) v]) row)))
        expenses))

(defn get-all-expenses
  [_req]
  (let [all-expenses (sql/get-all-expenses)
        modified-data (modify-expenses all-expenses)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string modified-data)}))

(defn monthly-summary
  [expenses]
  (->> expenses
       (group-by (fn [expense]
                   (let [inst (.toInstant (:time expense))
                         ldt (java.time.LocalDateTime/ofInstant inst java.time.ZoneOffset/UTC)]
                     (str (.getMonthValue ldt) "-" (.getYear ldt)))))
       (reduce-kv (fn [m k vs]
                    (assoc m k (reduce + 0.0 (map :amount vs))))
                  {})))

(defn get-summery
  [{:keys [query-params]}]
  (let [query-month (get query-params "month")
        query-year (get query-params "year")
        all-expenses (sql/get-all-expenses)
        modified-data (modify-expenses all-expenses)
        summery (monthly-summary modified-data)
        con-month-year (str query-month "-" query-year)
        updated-summary (if (and query-month query-year)
                          (get summery con-month-year {con-month-year 0})
                          summery)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string updated-summary)}))

(defn delete-expense
  [id]
  (sql/delete-expense (parse-int id))
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string {:message "Expense deleted"})})

(defroutes routes
  (POST "/expense" _req add-expense)
  (PUT "/expense/:id" _req update-expense)
  (DELETE "/expense/:id" [id] (delete-expense id))
  (GET "/expenses" _ get-all-expenses)
  (GET "/summery" req (get-summery req))
  (not-found "Page Not found"))

(def app
  (-> routes
      (wrap-params)
      (wrap-keyword-params) 
      (wrap-json-body {:keywords? true})
      (wrap-json-response)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000 :join? true}))
