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

(defn read-resource
  "helper method to call read-csv on a resource from the resource dir"
  [resource]
  (with-open [in-file (io/reader (io/resource resource))]
    (doall
      (csv/read-csv in-file))))

(defn get-wage-data
  "retrieve a read csv file containing wage data from either from server or fallback to what is in war"
  [server-resource war-resource]
    (if (io/resource server-resource)
      (read-resource server-resource)
      (read-resource war-resource)))

;;dev resources are for testing locally or as a fallback in case the server doesn't have any csv files in resource folder
(def state-csv-resource "wage_data/state_minimum_wage.csv")
(def state-csv-resource-dev "wage_data/dev/state_minimum_wage.csv")
(def state-wages-collection (parse (get-wage-data state-csv-resource state-csv-resource-dev)))

(def federal-csv-resource "wage_data/federal_minimum_wage.csv")
(def federal-csv-resource-dev "wage_data/dev/federal_minimum_wage.csv")
(def federal-wages-collection (parse (get-wage-data federal-csv-resource federal-csv-resource-dev)))

(defn get-federal-wage-by-year
  "Get federam minimum-wage from federal wage csv by year."
  [year federal-wages]
  ((keyword (str year)) (first federal-wages)))

(defn href-map
  "create href map for link"
  [uri]
  {:href uri})

(defn link-map
  "take a key value pair and creates a link that will be returned in the links list"
  [kvp]
  {(key kvp) (href-map (val kvp))})

(defn form-links
  "form values for links section of responses"
  ([links]
    (form-links links []))
  ([links output-map]
    (if (empty? links)
      output-map 
      (recur (rest links) (conj output-map (link-map (first links)))))))

(defn form-year-item
  "Create a map containing year and uri to get wage info."
  [year]
  (let [year-string (name year)] {:year year-string 
                                  :url (str "/years/" year-string) 
                                  :states_url (str "/years/" year-string "/states") 
                                  :federal_wage_info_url (str "/years/" year-string "/federal")}))

(defn form-get-years-response
  ([years]
   (form-get-years-response years []))
  ([years output-vector]
   (if (empty? years)
     {:years output-vector :url "/years"}
     (recur (rest years) (conj output-vector (form-year-item (first years)))))))

(defn get-available-year-keys
  "Takes the first line of Federal and State wage data and grabs and sorts the years into a sorted-set"
  [state-wage-data-year-keys federal-wage-data-year-keys]
  (apply sorted-set (concat state-wage-data-year-keys federal-wage-data-year-keys)))

(defn get-years
  "Returns the years available through the api. Accepts sample state and federal wage data for testing."
  ([]
   (get-years state-wages-collection federal-wages-collection))
  ([state-wages federal-wages]
   ;TODO: Simplify this using some reduce or apply magic
   (let [year-keys (get-available-year-keys (keys (dissoc (first state-wages) :state :postalcode)) (keys (dissoc (first federal-wages))))]
     (form-get-years-response year-keys))))

(defn get-year
  []
  {})

(defn get-federal-wage-info-for-year
  []
  {})

(defn form-get-states-for-year-state-item
  "create a map containing state, postalcode, and uri to get state wage"
  [year state-wage-map]
  (assoc
   (select-keys state-wage-map [:state :postalcode])
   :get-state-wage-info-for-year-uri (str "/" year "/" (:postalcode state-wage-map))))

(defn form-get-states-for-year-response
  "Returns filtered states into response"
  ([year state-wages federal-wages]
   (form-get-states-for-year-response year state-wages [] federal-wages))
  ([year state-wages output-vector federal-wages]
   (if (empty? state-wages)
     {(keyword (str year)) {:states output-vector :federal (get-federal-wage-by-year year federal-wages)}}
     (recur year (rest state-wages) (conj output-vector (form-get-states-for-year-state-item year (first state-wages))) federal-wages))))

(defn get-states-for-year
  "Returns the states available and the federal minimum wage for given year. Allows for sample state and federal wage data for testing."
  ([year]
   (get-states-for-year year state-wages-collection federal-wages-collection))
  ([year state-wages federal-wages]
   (form-get-states-for-year-response
    year
    (filter #(not (nil? ((keyword (str year)) %))) state-wages)
    federal-wages)))

(defn get-state-wage-info-for-year-response
  "Creates map with state, postal code, and wage"
  [year state-info federal-wages]
  (let [[state postal-code wage] [(:state state-info) (:postalcode state-info) ((keyword (str year)) state-info)]]
    {(keyword (str year)) {(keyword state) {:state state :postalcode postal-code :wage wage} :federal (get-federal-wage-by-year year federal-wages)}}))

(defn get-state-wage-info-for-year
  "Get info for specific state and year. Allows for sample state and federal wage data for testing."
  ([year postal-code]
   (get-state-wage-info-for-year year postal-code state-wages-collection federal-wages-collection))
  ([year postal-code state-wages federal-wages]
   (get-state-wage-info-for-year-response
    year
    (first (filter #(= (:postalcode %) (clojure.string/upper-case postal-code)) state-wages)) federal-wages)))

