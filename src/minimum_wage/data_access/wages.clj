(ns minimum-wage.data-access.wages
  (:use [keyval-collection-parse.parse])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn read-file
  "helper method to call read-csv on a filename"
  [filename]
  (with-open [in-file (io/reader filename)]
    (doall
      (csv/read-csv in-file))))

(def state-csv-filename "wage_data/state_minimum_wage.csv")
(def state-wages-collection (parse (read-file state-csv-filename)))

(def federal-csv-filename "wage_data/federal_minimum_wage.csv")
(def federal-wages-collection (parse (read-file federal-csv-filename)))

(defn form-year-item
  ;create a map containing year and uri to get wage info
  [year]
  (let [year-string (name year)] {:year year-string :get-year-wage-info-uri (str "/" year-string)})
)

(defn form-get-years-response
  "organize year keys into a vector of maps"
  ([years]
    (form-get-years-response years []))
  ([years output-vector]
   (if (empty? years)
     {:years output-vector}
     (recur (rest years) (conj output-vector (form-year-item (first years))))))
)

(defn get-years
  "Returns the years available through the api"
  [state-wages]
  (let [year-keys (keys (dissoc (first state-wages) :state :postalcode))]
    (form-get-years-response year-keys))
)

(defn get-year-wage-info
  "Returns the states available and the federal minimum wage for given year"
  [year state-wages federal-wages]
  "yolo"
)

