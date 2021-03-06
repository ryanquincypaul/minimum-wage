(ns minimum-wage.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.coercions :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [minimum-wage.data-access.wages :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]))

(defroutes app-routes
  (GET "/" [] (load-api-reference))
  (GET "/years" [] (response (get-years)))
  (GET "/years/:year" [year] (response (get-year (str year))))
  (GET "/years/:year/federal" [year] (response (get-federal-wage-info-for-year (str year))))
  (GET "/years/:year/states" [year] (response (get-states-for-year (str year))))
  (GET "/years/:year/states/:postal-code" [year postal-code] (response (get-state-wage-info-for-year year postal-code)))
  (route/not-found (response {:message "Not Found"
                              :documentation_url "https://github.com/ryanquincypaul/minimum-wage/wiki"})))

(def app
  (-> (wrap-defaults app-routes site-defaults)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response app-routes)
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods [:get])))
