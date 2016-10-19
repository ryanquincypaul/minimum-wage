(ns minimum-wage.data-access.wages
  (:use [keyval-collection-parse.parse]
        [markdown.core])
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn ^{:private true} read-resource
  "helper method to call read-csv on a resource from the resource dir"
  [resource]
  (with-open [in-file (io/reader (io/resource resource))]
    (doall
      (csv/read-csv in-file))))

(def api-reference-md-resource "public/minimum-wage-api-reference.md")

(defn load-api-reference
  []
  (md-to-html-string (slurp (io/resource api-reference-md-resource))))

(defn ^{:private true} get-wage-data
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

(defn ^{:private true} get-federal-wage-by-year
  "Get federam minimum-wage from federal wage csv by year."
  [year federal-wages]
  ((keyword (str year)) (first federal-wages)))

(defn ^{:private true} form-year-item
  "Create a map containing year and uri to get wage info."
  [year]
  (let [year-string (name year)] {:year year-string 
                                  :url (str "/years/" year-string) 
                                  :states_url (str "/years/" year-string "/states") 
                                  :federal_wage_info_url (str "/years/" year-string "/federal")}))

(defn ^{:private true} form-get-years-response
  ([years]
   (form-get-years-response years []))
  ([years output-vector]
   (if (empty? years)
     {:years output-vector :url "/years"}
     (recur (rest years) (conj output-vector (form-year-item (first years)))))))

(defn ^{:private true} get-available-year-keys
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
  [year]
  {:year (str year) 
   :url (str "/years/" year) 
   :states_url (str "/years/" year "/states") 
   :federal_wage_info_url (str "/years/" year "/federal")
   :all_years_url "/years"})

(defn get-federal-wage-info-for-year
  ([year]
    (get-federal-wage-info-for-year year federal-wages-collection))
  ([year federal-wages]
   {:year (str year) 
    :minimum_wage (get-federal-wage-by-year year federal-wages) 
    :url (str "/years/" year "/federal")
    :states_url (str "/years/" year "/states") 
    :year_url (str "/years/" year)}))

(defn ^{:private true} form-get-states-for-year-state-item
  "create a map containing state, postalcode, and uri to get state wage"
  [year state-wage-map]
  (assoc
   (select-keys state-wage-map [:state :postalcode])
   :url (str "/years/" year "/states/" (clojure.string/lower-case (:postalcode state-wage-map)))))

(defn ^{:private true} form-get-states-for-year-response
  "Returns filtered states into response"
  ([year state-wages]
   (form-get-states-for-year-response year state-wages []))
  ([year state-wages output-vector]
   (if (empty? state-wages)
     {:year (str year) 
      :states output-vector
      :url (str  "/years/" year "/states")
      :year_url (str "/years/" year) 
      :federal_wage_info_url (str "/years/" year "/federal")}
     (recur year (rest state-wages) (conj output-vector (form-get-states-for-year-state-item year (first state-wages)))))))

(defn get-states-for-year
  "Returns the states available and the federal minimum wage for given year. Allows for sample state and federal wage data for testing."
  ([year]
   (get-states-for-year year state-wages-collection))
  ([year state-wages]
   (form-get-states-for-year-response
    year
    (filter #(not (nil? ((keyword (str year)) %))) state-wages))))

(defn ^{:private true} get-state-wage-info-for-year-response
  "Creates map with state, postal code, and wage"
  [year state-info]
  (let [[state postal-code wage] [(:state state-info) (:postalcode state-info) ((keyword (str year)) state-info)]]
    {:year (str year) 
     :state state 
     :postalcode postal-code 
     :minimum_wage wage
     :url (str "/years/" year "/states/" (clojure.string/lower-case postal-code))
     :year_url (str "/years/" year)
     :federal_wage_info_url (str "/years/" year "/federal")}))

(defn get-state-wage-info-for-year
  "Get info for specific state and year. Allows for sample state and federal wage data for testing."
  ([year postal-code]
   (get-state-wage-info-for-year year postal-code state-wages-collection))
  ([year postal-code state-wages]
   (get-state-wage-info-for-year-response
    year
    (first (filter #(= (:postalcode %) (clojure.string/upper-case postal-code)) state-wages)))))

