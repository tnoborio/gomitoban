(ns gomitoban.bot-test
  (:use
   clojure.contrib.test-is
   appengine-clj.test-utils)
  (:require
   [gomitoban.bot :as bot]
   [appengine-clj.datastore :as ds])
  (:import
   (com.google.appengine.api.datastore Query)))

(deftest parse-content
  (let [result (bot/parse-content
		"火=燃えるゴミ、金曜日= 燃えないゴミ, 水曜日＝ペットボトル  ")]
    (is (= '(nil, nil, "燃えるゴミ", "ペットボトル",
		  nil, "燃えないゴミ", nil) result))))

(dstest can-create-schedule
  (bot/create-schedule 1234 "火=燃えるゴミ、金曜日= 燃えないゴミ, 水曜日＝ペットボトル  ")
  (let [all-schedules (ds/find-all (Query. "Schedule"))
	my-schedule (first all-schedules)]
    (is (= 1 (count all-schedules)))
    (is (= 1234 (:userid my-schedule)))
    (is (= "\t\t燃えるゴミ\tペットボトル\t\t燃えないゴミ\t" (:content my-schedule)))))
	  
(dstest can-parse-mentions
  (bot/setup "../war/config.clj")
  (println (bot/parse-mentions (bot/mentions))))

