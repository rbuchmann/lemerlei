(defproject lemerlei "0.1.0-SNAPSHOT"
  :description "My-Croservice!"
  :dependencies [[org.clojure/clojure        "1.8.0"]
                 [clj-time                   "0.11.0"] ; required due to bug in `lein-ring uberwar`
                 [metosin/compojure-api      "1.1.1"]
                 [com.taoensso/timbre        "4.4.0"]
                 [clj-http                   "2.2.0"]
                 [org.clojure/data.json      "0.2.6"]
                 [com.cemerick/friend        "0.2.3"]
                 [clj-jwt                    "0.1.1"]
                 [com.stuartsierra/component "0.3.1"]
                 [environ                    "1.0.3"]
                 [code-maat                  "1.0-SNAPSHOT"]
                 [tentacles                  "0.5.1"]
                 [org.clojure/core.async     "0.2.385"]]
  :ring {:handler lemerlei.core/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [cheshire "5.5.0"]
                                  [ring/ring-mock "0.3.0"]]
                   :plugins [[lein-ring "0.9.7"]]}})
