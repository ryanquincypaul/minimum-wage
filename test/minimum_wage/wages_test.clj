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
  (let [response (get-years parsed-state-data parsed-federal-data)]
    (is (= response {:years [{:year "2015" :url "/years/2015" :states_url "/years/2015/states" :federal_wage_info_url "/years/2015/federal"}
                             {:year "2016" :url "/years/2016" :states_url "/years/2016/states" :federal_wage_info_url "/years/2016/federal"}
                             {:year "2017" :url "/years/2017" :states_url "/years/2017/states" :federal_wage_info_url "/years/2017/federal"}
                             {:year "2018" :url "/years/2018" :states_url "/years/2018/states" :federal_wage_info_url "/years/2018/federal"}]
                     :url "/years"}))))

(def ^{:private true} common-year
  2016)

(deftest get-year-test
  (let [response (get-year common-year)]
    (is (= response {:year "2016"
                     :url "/years/2016"
                     :states_url "/years/2016/states"
                     :federal_wage_info_url "/years/2016/federal"
                     :all_years_url "/years"}))))

(deftest get-federal-wage-info-for-year-test
  (let [response (get-federal-wage-info-for-year common-year parsed-federal-data)]
    (is (= response {:year "2016"
                     :minimum-wage "7.25"
                     :url "/years/2016/federal"
                     :states_url "/years/2016/states"
                     :year_url "/years/2016"}))))

(deftest get-year-states-test
  ;Use year that all states have a value for
  (let [response (get-states-for-year common-year parsed-state-data)]
    (is (= response {:year "2016"
                     :states [{:state "Alabama" :postalcode "AL" :url "/years/2016/states/al"}
                              {:state "Massachusetts" :postalcode "MA" :url "/years/2016/states/ma"}
                              {:state "Michigan" :postalcode "MI" :url "/years/2016/states/mi"}]
                     :url "/years/2016/states"
                     :year_url "/years/2016"
                     :federal_wage_info_url "/years/2016/federal"}))))

;to prevent responses of states with null or no values for a year
(def ^{:private true} some-missing-year
  2018)

(deftest get-year-states-alabama-missing-test
  (let [response (get-states-for-year some-missing-year parsed-state-data)]
    (is (= response {:year "2018" 
                     :states [{:state "Massachusetts" :postalcode "MA" :url "/years/2018/states/ma"}
                              {:state "Michigan" :postalcode "MI" :url "/years/2018/states/mi"}]
                     :url "/years/2018/states"
                     :year_url "/years/2018"
                     :federal_wage_info_url "/years/2018/federal"}))))

(def ^{:private true} michigan-postal-code
  "mi")

(deftest get-state-wage-info-test
  (let [response (get-state-wage-info-for-year common-year michigan-postal-code parsed-state-data)]
    (is (= response {:year "2016" 
                     :state "Michigan"
                     :postalcode "MI"
                     :minimum-wage "8.50"
                     :url "/years/2016/states/mi"
                     :year_url "/years/2016"
                     :federal_wage_info_url "/years/2016/federal"}))))

(def ^{:private true} parsed-federal-data-extra-year
  [{:2015 "7.25" :2016 "7.25" :2017 "7.25" :2018 "7.25" :2019 "15.00"}])

(deftest get-years-with-only-federal-wage)
                                        ;make sure we also return years even when we only have federal data

(let [response (get-years parsed-state-data parsed-federal-data-extra-year)]
  (is (= response {:years [{:year "2015" :url "/years/2015" :states_url "/years/2015/states" :federal_wage_info_url "/years/2015/federal"}
                           {:year "2016" :url "/years/2016" :states_url "/years/2016/states" :federal_wage_info_url "/years/2016/federal"}
                           {:year "2017" :url "/years/2017" :states_url "/years/2017/states" :federal_wage_info_url "/years/2017/federal"}
                           {:year "2018" :url "/years/2018" :states_url "/years/2018/states" :federal_wage_info_url "/years/2018/federal"}
                           {:year "2019" :url "/years/2019" :states_url "/years/2019/states" :federal_wage_info_url "/years/2019/federal"}]
                   :url "/years"})))
