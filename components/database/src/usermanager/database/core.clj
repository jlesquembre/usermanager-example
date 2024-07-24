(ns usermanager.database.core
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]))

(defrecord Database [db-spec     ; configuration
                     init-fn     ; callback to initialize the database
                     datasource] ; state

  component/Lifecycle
  (start [this]
    (if datasource
      this ; already initialized
      (let [this+ds (assoc this :datasource (jdbc/get-datasource db-spec))]
        ;; set up database if necessary
        (init-fn this+ds (:dbtype db-spec))
        this+ds)))
  (stop [this]
    (assoc this :datasource nil))

  ;; allow the Database component to be "called" with no arguments
  ;; to produce the underlying datasource object
  clojure.lang.IFn
  (invoke [_] datasource))

(defn create
  "Given a database spec (hash map) and an initialization
  function (that accepts a database component and a string
  specifying the database type), return an initialized
  database component.

  The component can be invoked with no arguments to return
  the underlying data source (javax.sql.DataSource).

  The init-fn should be able to initialize a freshly-created
  database but also gracefully handle an existing database."
  [db-spec init-fn]
  (map->Database {:db-spec db-spec :init-fn init-fn}))

(defn auto-increment-key
  "Given a database type (e.g., sqlite), return a string
  that can be used to declare a column as an auto-increment
  primary key in a CREATE TABLE statement."
  [db-type]
  (if (= "sqlite" db-type)
    "primary key autoincrement"
    (str "generated always as identity"
         " (start with 1 increment by 1)"
         " primary key")))
