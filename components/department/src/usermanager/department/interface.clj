(ns usermanager.department.interface
  (:require [usermanager.department.core :as department]))

(defn get-by-id
  "Given a department ID, return the department record."
  [db id]
  (department/get-by-id db id))

(defn get-all
  "Return all available department records (in order)."
  [db]
  (department/get-all db))
