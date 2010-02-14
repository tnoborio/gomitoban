(ns gomitoban.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.http compojure.html
	gomitoban.bot)
  (:import
    (com.google.appengine.api.users UserServiceFactory)
    (com.google.appengine.api.datastore Query))
  (:require [appengine-clj.datastore :as ds]))

(defroutes bomitoban-app
  (GET "/cron/tweet"
       (setup)
       (html (update "test")))
  (GET "/cron/follow"
       (setup)
       (let [fllws (followers)
	     frnds (friends)
	     uf (unfollowers fllws frnds)]
	 (follow! uf)
	 (html "ok")))
  (GET "/cron/mentions"
       (setup)
       (html (parse-mentions (mentions)))))

(defservice bomitoban-app)
