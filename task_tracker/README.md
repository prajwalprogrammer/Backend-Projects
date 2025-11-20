# task_tracker
Sample solution for the [task-tracker](https://roadmap.sh/projects/task-tracker) challenge from roadmap.sh.

## Task_Tracker is a command-line tool for managing tasks, written in Clojure. It allows users to easily create, list, update, and delete tasks.

## Installation

```` 
git clone https://github.com/prajwalprogrammer/Backend-Projects/tree/main/task_tracker
cd Task_Tracker
lein run
````

## Usage

#### Add Task 
`` lein run -a "Buy Vegitable" ``

#### Add Multiple Tasks as Single CMD
`` lein run -a "Go To Gym" "Office Task" ``

#### Update Task
`` lein run -u "c7869787-b60f-46d3-8521-c4f7bff4b8c6" "in-progress" ``

#### Delete Task
`` lein run -d "c7869787-b60f-46d3-8521-c4f7bff4b8c6" ``

#### List All Task
`` lein run -l ``

#### List Task as per status
`` lein run -l "todo" ``
