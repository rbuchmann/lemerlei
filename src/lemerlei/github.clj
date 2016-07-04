(ns lemerlei.github
  (:require [tentacles.repos    :as repos]
            [environ.core       :refer [env]]
            [clojure.string     :as str]
            [clojure.core.async :as async :refer [go chan <!! <! >!]]
            [lemerlei.code-maat :as maat]))

;; reading from environment via environ. This gets filled by
;; environment variables and the .lein-env file.

(def username (env :gituser))
(def password (env :gitpassword))

(def opts {:auth (str username ":" password)})

;; One of Clojures mechanisms for polymorphism.  Protocols are like
;; interfaces, only much more useful, since you can implement them for
;; types you don't control. In this regard, they are more similar to
;; Haskells typeclasses

(defprotocol CommitSource
  (get-commits [this user repo])
  (get-commit [this user repo sha]))

;; simple connector for synchronous github api requests. A record is
;; like a simple, immutable value class in Java.  This one doesn't
;; have any fields, it only implements the CommitSource protocol.

(defrecord GithubConnector []
  CommitSource
  (get-commits [_ user repo]
    (repos/commits user repo opts))
  (get-commit [_ user repo sha]
    (repos/specific-commit user repo sha opts)))

(def github (GithubConnector.))

(defn summarize-commit [commit-info]
  (let [{:keys [commit files sha]} commit-info ; destructure the commit info
        {{:keys [name date]} :author           ; nested destructuring
         message :message} commit]
    ;; produce a vector of change infos for code-maat
    (mapv (fn [{:keys [additions deletions filename]}]
            {:author name
             :date (-> date (str/split #"T") first)
             :message (-> message str/split-lines first)
             :entity filename
             :loc-added additions
             :loc-deleted deletions
             :rev (->> sha (take 7) (apply str))})
          files)))

(defn download-changelist [source user repo]
  ;; Every commit added to the channel
  ;; will be reduced to its sha hash.
  ;; (See http://clojure.org/reference/transducers)
  (let [commit-chan (chan 1 (map :sha))
        out-chan (chan 500)] ; buffer generously for parallel processing

    ;; Start a go-routine to fill the commit-channel
    (go
      (let [commit-list (get-commits source user repo)]
        (doseq [commit commit-list]
          (>! commit-chan commit))
        (async/close! commit-chan)))
    ;; Pipe commit-chan to out-chan, subject to a transducer,
    ;; and with parallelism 10 (chosen arbitrarily, as an example)
    (async/pipeline-blocking 10
                             out-chan
                             ;; transducers can be composed
                             (comp
                              (map (partial get-commit source user repo))
                              ;; transducers can produce multiple outputs,
                              ;; this is similar to flatmap in rx
                              (mapcat summarize-commit))
                             commit-chan)
    out-chan))
