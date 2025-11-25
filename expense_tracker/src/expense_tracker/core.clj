(ns expense-tracker.core
  (:require
   [cheshire.core :as json]
   [compojure.core :refer [defroutes GET POST]]
   [compojure.route :refer [not-found]]
   [expense-tracker.sql :as sql]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]])
  (:gen-class))

(defn add-expense-handler
  "Handler to add expense"
  [{:keys [body]}]
  (let [desciption (:description body)
        amount (:amount body)
        all-expense_resp (sql/add-expense desciption amount)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:message "Expense Added successfully"
                                  :id all-expense_resp})}))

(defroutes routes
  (POST "/expense" req (add-expense-handler req))
  (GET "/:name" [name] (str "Hello, " name))
  (not-found "Page Not found"))

(def app
  (-> routes
      (wrap-json-body {:keywords? true})
      (wrap-json-response)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (run-jetty app {:port 3000 :join? true}))
