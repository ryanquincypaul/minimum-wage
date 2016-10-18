(defproject minimum-wage "0.1.0-SNAPSHOT"
  :description "A Clojure powered RESTful web service providing State and Federal minimum wage data."
  :url "https://github.com/ryanquincypaul/minimum-wage"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [ring-cors "0.1.8"]
                 [org.clojure/data.csv "0.1.3"]
                 [keyval-collection-parse "0.1.0-SNAPSHOT"]
                 [markdown-clj "0.9.89"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler minimum-wage.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}}
  :resources "resources/")
