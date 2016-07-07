(ns lemerlei.github-test
  (:require [lemerlei.github                 :as gh]
            [lemerlei.github-generators      :as gg]
            [clojure.test                    :as t]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties   :as prop]))

;; Awesome generative testing, just describe how a commit should look
;; and check if our code pulls it apart correctly

(defspec summarizes-commits
  ;; try a hundred different samples. test.check is "smart", it tries
  ;; basic cases like e.g. 0, 1 and -1 for ints first and then expands
  ;; the range with more samples.
  100
  (prop/for-all
   [commit gg/commit-gen]
   (let [summary (gh/summarize-commit commit)]
     ;; We should get one result per file in the commit
     (= (count (:files commit))
        (count summary))
     ;; All results should have these keys
     (= (keys (first summary))
        [:author :date :message :entity :loc-added :loc-deleted :rev])
     ;; The date format should be like 1992-30-7
     (re-matches #"[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}" (:date (first summary))))))
