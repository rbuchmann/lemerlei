(ns lemerlei.handlers
  (:require [lemerlei.github    :as gh]
            [lemerlei.code-maat :as maat]
            [clojure.core.async :as async :refer [go chan <!! <! >!]]))

(defn run-analysis [analysis source user repo]
  (->> (gh/download-changelist source user repo) ; returns a channel of changes
       (async/into []) ; returns a channel with a single result, all
                       ; changes in a single vector
       <!!             ; Takes the result from the channel
       analysis))      ; Runs the given analysis on it

;; Point-free function definition via partial application

(def authors (partial run-analysis maat/get-authors))

(def coupling (partial run-analysis maat/get-coupling))
