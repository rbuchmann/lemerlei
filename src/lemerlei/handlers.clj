(ns lemerlei.handlers
  (:require [lemerlei.github    :as gh]
            [lemerlei.code-maat :as maat]
            [clojure.core.async :as async :refer [go chan <!! <! >!]]))

(defn run-analysis [analysis source user repo]
  (->> (gh/download-changelist source user repo)
       (async/into [])
       <!!
       analysis))

(def authors (partial run-analysis maat/get-authors))

(def coupling (partial run-analysis maat/get-coupling))
