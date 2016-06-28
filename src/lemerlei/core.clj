(ns lemerlei.core
  (:require [lemerlei.handlers       :as handlers]
            [lemerlei.github         :refer [github]]
            [compojure.api.sweet     :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core             :as s]))

(s/defschema Authors
  {:entity s/Str
   :n-authors s/Int
   :n-revs s/Int})

(s/defschema Coupling
  {:entity s/Str
   :coupled s/Str
   :degree s/Int
   :average-revs s/Int})

(defn make-app [source]
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Lemerlei"
                   :description "Compojure Api example"}
            :tags [{:name "api"
                    :description "Some code-maat analysis apis"}]}}}
   (GET "/health" []
        :return {:status s/Str}
        :summary "Always ok, because why wouldn't it!"
        (ok {:status "OK"}))

   (GET "/authors/:user/:repository" []
        :return [Authors]
        :path-params [user       :- String
                      repository :- String]
        :summary "Retrieves the number of authors and revisions per file"
        (ok (handlers/authors source user repository)))

   (GET "/coupling/:user/:repository" []
        :return [Coupling]
        :path-params [user       :- String
                      repository :- String]
        :summary "Calculates the coupling degree per file"
        (ok (handlers/coupling source user repository)))))

(def app (make-app github))
