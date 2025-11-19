(ns task-tracker.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [cheshire.core :as json])
  (:gen-class))

(def cli-options
  ;; An option with an argument
  [["-a" "--add" "Add Task"
    :default []
    :parse-fn conj]
   ["-u" "--update" "Update Task"
    :default []
    :parse-fn conj]

   ["-d" "--delete" "Delete Task"
    :parse-fn #(str %)]
   ["-l" "--list" "list all Tasks"
    :default [:all]
    :parse-fn #(str %)]
   ["-h" "--help"]])

(def task-file (atom nil))

(defn read-task
  "Returns all the task"
  ([]
   (read-task :all))
  ([status]
   (try
     (let [all-tasks (json/parse-string (slurp @task-file) true)
           filter-status (filter #(= status (:status %)) all-tasks)]
       (if (= :all status)
         all-tasks
         filter-status))
     (catch Exception _ []))))

(defn add-task
  "Add the Task"
  [description]
  (let [tasks (read-task)
        new-task {:id (java.util.UUID/randomUUID)
                  :description (str description)
                  :status "todo"
                  :createdAt (.toString (java.util.Date.))
                  :updatedAt (.toString (java.util.Date.))}
        updated-tasks (conj tasks new-task)]
    (spit @task-file (json/generate-string updated-tasks {:pretty true}))))

(defn update-task
  "Update the task status"
  [task-id status]
  (let [tasks (read-task)
        exp-task (into {} (filter #(= (:id %) task-id) tasks))
        updated-task (assoc exp-task
                            :status status
                            :updatedAt (.toString (java.util.Date.)))
        updated-task-map (conj (vec (remove #(not= % exp-task) tasks)) updated-task)]
    (spit @task-file (json/generate-string updated-task-map {:pretty true}))))

(defn delete-task
  [task-id]
  (let [tasks (read-task)
        updated-task-map (vec (remove #(not= (:id %) task-id) tasks))]
    (spit @task-file (json/generate-string updated-task-map {:pretty true}))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [options arguments]} (parse-opts args cli-options)]
    (reset! task-file (io/file "tasks.json"))
    (cond
      (true? (:add options)) (do (mapv add-task arguments)
                                 (print "Task Added successfully"))
      (true? (:update options)) (do (update-task (first arguments) (second arguments))
                                    (print "Task Updated successfully"))
      (:delete options) (do (delete-task arguments)
                            (print "Task Deleted successfully"))
      (:list options) (println (read-task (first arguments))))))
