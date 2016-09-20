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

(defn get-federal-wage-by-year
  "Get federam minimum-wage from federal wage csv by year"
  [year federal-wages]
  ((keyword (str year)) (first federal-wages)))

(defn form-year-item
  ;create a map containing year and uri to get wage info
  [year]
  (let [year-string (name year)] {:year year-string :get-states-by-year-uri (str "/" year-string)})
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


(defn form-get-states-by-year-item
  "create a map containing state, postalcode, and uri to get state wage"
  [year state-wage-map]
  (assoc 
   (select-keys state-wage-map [:state :postalcode]) 
   :get-state-wage-info-for-year-uri (str "/" year "/" (:postalcode state-wage-map)))
)

(defn form-get-states-by-year-response
  "Returns filtered states into response"
  ([year state-wages]
    (form-get-states-by-year-response year state-wages []))
  ([year state-wages output-vector]
    (if (empty? state-wages)
      {:year year :states  output-vector}
      (recur year (rest state-wages) (conj output-vector (form-get-states-by-year-item year (first state-wages)))))))

(defn get-states-by-year
  "Returns the states available and the federal minimum wage for given year"
  [year state-wages federal-wages]
  (assoc 
   (form-get-states-by-year-response 
    year 
    (filter #(not (nil? ((keyword (str year)) %))) state-wages))
   :federal (get-federal-wage-by-year year federal-wages))
)

(defn get-state-wage-info-for-year-response
  "Creates map with state, postal code, and wage"
  [year state-info] 
  (let [[state postal-code wage] [(:state state-info) (:postalcode state-info) ((keyword (str year)) state-info)]]
   {(keyword state) {:state state :postalcode postal-code :wage wage}})
 )

(defn get-state-wage-info-for-year
  "Get info for specific state and year"
  [year postal-code state-wages federal-wages]
  (assoc 
   (get-state-wage-info-for-year-response 
    year 
    (first (filter #(= (:postalcode %) (clojure.string/upper-case postal-code)) state-wages))) 
   :federal (get-federal-wage-by-year year federal-wages))
)

