(ns lemerlei.code-maat
  (:require [code-maat.analysis.logical-coupling :as coupling]
            [code-maat.analysis.authors          :as authors]
            [incanter.core                       :as incanter]))

(def default-opts {:min-revs 5
                   :min-shared-revs 5
                   :min-coupling 30
                   :max-coupling 100
                   :max-changeset-size 30})

(defn to-maps [data]
  (let [col-names (incanter/col-names data)]
    (->> data
         incanter/to-list
         (map #(zipmap col-names %)))))

(defn run-analysis [analysis data]
  (-> (-> data
          incanter/to-dataset
          (analysis default-opts)
          to-maps)))

(defn get-coupling [data]
  (run-analysis coupling/by-degree data))

(defn get-authors [data]
  (run-analysis authors/by-count data))
