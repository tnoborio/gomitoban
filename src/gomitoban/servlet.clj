(ns gomitoban.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.http compojure.html
	gomitoban.bot)
  (:import
    (com.google.appengine.api.users UserServiceFactory)
    (com.google.appengine.api.datastore Query))
  (:require [appengine-clj.datastore :as ds]))

(defn load-config []
  (read-string (slurp "config.clj")))

(defn setup []
  (let [conf (load-config)]
    (setup-tw (:username conf) (:password conf))))

(defn create [content author]
  (ds/create {:kind "Greeting" :author author :content content :date (java.util.Date.)}))

(defroutes bomitoban-app
  (GET "/cron/tweet"
       (html (update "test")))
  (GET "/cron/follow"
       (setup)
       (let [fllws (followers)
	     frnds (friends)
	     uf (unfollowers fllws frnds)]
	 (follow! uf)
	 (html "ok"))))

(defservice bomitoban-app)
