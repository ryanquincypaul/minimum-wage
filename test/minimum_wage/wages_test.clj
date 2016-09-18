(ns minimum-wage.wages-test
  (:require [clojure.test :refer :all]
            [minimum-wage.data-access.wages :refer :all]))

;mocked parsed data from csv files found in wage_data folder
(def ^{:private true} parsed-state-data
  [{:state "Alabama", :postalcode "AL", :2015 "7.25", :2016 "7.25", :2017 "7.25" :2018 nil}
   {:state "Massachusetts", :postalcode "MA" :2015 "9.00", :2016 "10.00", :2017 "11.00", :2018 "11.00"}
   {:state "Michigan", :postalcode "MI", :2015 "8.15", :2016 "8.50", :2017 "8.90", :2018 "9.25"}])

(def ^{:private true} parsed-federal-data
  [{:2015 "7.25" :2016 "7.25" :2017 "7.25" :2018 "7.25"}])

(deftest get-years-test
  (let [response (get-years parsed-state-data)]
    (is (= response {:years [{:year "2015" :get-year-wage-info-uri "/2015"}
                             {:year "2016" :get-year-wage-info-uri "/2016"} 
                             {:year "2017" :get-year-wage-info-uri "/2017"}
                             {:year "2018" :get-year-wage-info-uri "/2018"}]}))))

(def ^{:private true} common-year
  2016) 


(deftest get-year-wage-info-test
  ;Use year that all states have a value for
  (let [response (get-year-wage-info common-year parsed-state-data parsed-federal-data)]
    (is (= response {:states [{:state "Alabama" :postalcode "AL" :wage "7.25"}
                              {:state "Massachusetts" :postalcode "MA" :wage "9.00"}
                              {:state "Michigan" :postalcode "MI" :wage "8.15"}]
                     :federal 7.25}))))

;to prevent responses of states with null or no values for a year
(def ^{:private true} some-missing-year
  2018)

(deftest get-year-wage-info-missing-test
  (let [response (get-year-wage-info some-missing-year parsed-state-data parsed-federal-data)]
    (is (= response {:states [{:state "Massachusetts" :postalcode "MA" :wage "11.00"}
                              {:state "Michigan" :postalcode "MI" :wage "9.25"}]
                     :federal 7.25}))))

