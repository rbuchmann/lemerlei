(ns lemerlei.authentication
  (:require [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows   :as workflows]
            [ring.util.http-response     :refer :all]
            [clj-time.core               :refer [minutes]]
            [cemerick.friend             :as friend]
            [sourcewerk.friend-jwt.core  :as friend-jwt]))

(def users {"friend" {:username "friend"
                      :password (creds/hash-bcrypt "clojure")
                      :roles #{::user}}
            "greg" {:username "greg"
                    :password (creds/hash-bcrypt "kaktus")
                    :roles #{::admin}}}) ; only needed for login resource

(derive ::admin ::user) ; admins are considered to be also users, this
                        ; is an example of a nice clojure concept,
                        ; ad hoc hierarchies:
                        ; http://clojure.org/reference/multimethods

(def jwt-service-config
  {:algorithm :HS256
   :private-key "secret" ; FIXME never put a plain text secret in the source code!
   :token-time-to-live (minutes 2)})

(def jwt-client-config
  {:algorithm :HS256
   :public-key "secret"})

(defn wrap [app]
  (friend/authenticate
   app
   {:allow-anon? true
    :unauthorized-handler (fn [& _] (unauthorized))
    :unauthenticated-handler friend-jwt/workflow-deny
    :login-uri "/login"
    :workflows [(friend-jwt/workflow
                 :token-header "JWT"
                 :service-config jwt-service-config
                 :client-config jwt-client-config
                 :credential-fn (partial creds/bcrypt-credential-fn users)
                 :get-user-fn users)]}))

;; Macro just to show some possibilities of abstraction, should use
;; friends builtin exception mappers or a simple middleware for this

(defmacro with-authorizing-only [roles & body]
  ;; The backtick can be thought of as a code template, where ~
  ;; inserts the corresponding expression, and ~@ inserts a sequence
  ;; of expressions without the surrounding parens. Variables of the
  ;; form foo# generate new symbols to avoid name capture
  ;; http://clojure.org/reference/macros
  `(try (friend/authorize ~roles
                          (ok ~@body))
        (catch clojure.lang.ExceptionInfo e#
          (let [data# (:object (.getData e#))]
            (if (= :unauthorized (:cemerick.friend/type data#))
              (unauthorized
               {:error "Unauthorized!"
                :roles-required (-> data#
                                    ::cemerick.friend/required-roles
                                    vec)})
              (internal-server-error))))))
