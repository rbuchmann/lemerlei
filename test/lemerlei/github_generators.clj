(ns lemerlei.github-generators
  (:require [clojure.test.check.generators :as gen]))

(def date-gen
  (gen/let [year   (gen/choose 1900 2200)
            month  (gen/choose 1 12)
            day    (gen/choose 1 28)
            minute (gen/choose 0 60)
            second (gen/choose 0 60)]
    (str year "-" month "-" day "T" minute ":" second "+0200")))

(def message-gen
  (gen/hash-map
   :additions gen/pos-int
   :deletions gen/pos-int
   :filename gen/string-alphanumeric))

(def commit-gen
  (gen/hash-map
   :sha gen/string-alphanumeric
   :commit (gen/hash-map
            :message gen/string
            :author (gen/hash-map :name gen/string-alphanumeric
                                  :date date-gen))
   :files (gen/such-that not-empty
                         (gen/vector message-gen))))

(def commit-list-gen
  (gen/vector commit-gen))
