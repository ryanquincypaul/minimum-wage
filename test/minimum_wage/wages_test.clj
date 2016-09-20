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
    (is (= response {:years [{:year "2015" :get-states-by-year-uri "/2015"}
                             {:year "2016" :get-states-by-year-uri "/2016"} 
                             {:year "2017" :get-states-by-year-uri "/2017"}
                             {:year "2018" :get-states-by-year-uri "/2018"}]}))))

(def ^{:private true} common-year
  2016) 


(deftest get-year-states-info-test
  ;Use year that all states have a value for
  (let [response (get-states-by-year common-year parsed-state-data parsed-federal-data)]
    (is (= response {:year 2016
                     :states [{:state "Alabama" :postalcode "AL" :get-state-wage-info-for-year-uri "/2016/AL"}
                              {:state "Massachusetts" :postalcode "MA" :get-state-wage-info-for-year-uri "/2016/MA"}
                              {:state "Michigan" :postalcode "MI" :get-state-wage-info-for-year-uri "/2016/MI"}]
                     :federal "7.25"}))))

;to prevent responses of states with null or no values for a year
(def ^{:private true} some-missing-year
  2018)

(deftest get-year-wage-info-missing-test
  (let [response (get-states-by-year some-missing-year parsed-state-data parsed-federal-data)]
    (is (= response {:year 2018
                     :states [{:state "Massachusetts" :postalcode "MA" :get-state-wage-info-for-year-uri "/2018/MA"}
                              {:state "Michigan" :postalcode "MI" :get-state-wage-info-for-year-uri "/2018/MI"}]
                     :federal "7.25"}))))

(def ^{:private true} michigan-postal-code
  "mi")

(deftest get-state-wage-info-test
  (let [response (get-state-wage-info-for-year common-year michigan-postal-code parsed-state-data parsed-federal-data)]
    (is (= response {:Michigan {:state "Michigan" :postalcode "MI" :wage "8.50"}
                     :federal "7.25"}))))
