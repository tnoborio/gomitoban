(ns gomitoban.servlet
  (:gen-class
   :extends javax.servlet.http.HttpServlet
   :init init)
  (:use compojure.http compojure.html
	gomitoban.bot)
  (:import
    (com.google.appengine.api.users UserServiceFactory)
    (com.google.appengine.api.datastore Query))
  (:require [appengine-clj.datastore :as ds]))

(defn -init [s]
  (setup))

(defroutes bomitoban-app
  (GET "/cron/tweet"
       (html (update "test")))
  (GET "/cron/follow"
       (let [fllws (followers)
	     frnds (friends)
	     uf (unfollowers fllws frnds)]
	 (follow! uf)
	 (html "ok")))
  (GET "/cron/mentions"
       (html (parse-mentions (mentions)))))

(defservice bomitoban-app)
