(ns gomitoban.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.http compojure.html
	gomitoban.bot)
  (:import
    (com.google.appengine.api.users UserServiceFactory)
    (com.google.appengine.api.datastore Query))
  (:require [appengine-clj.datastore :as ds]))

(defn create [content author]
  (ds/create {:kind "Greeting" :author author :content content :date (java.util.Date.)}))

(defroutes bomitoban-app
  (GET "/a"
       (html (parse-content "火=燃えるゴミ、金曜日= 燃えないゴミ, 水曜日＝ペットボトル  ")))
  (GET "/"
       (html [:h1 "Hello, World!"]))
  (GET "/test"
       (let [user-service (UserServiceFactory/getUserService)
	     user (.getCurrentUser user-service)]
	 (html
	  [:h1 "Hello, " (if user (.getNickname user) "World") "!"]
	  [:p (link-to (.createLoginURL user-service "/") "sign in")]
	  [:p (link-to (.createLogoutURL user-service "/") "sign out")])))
  (GET "/create"
       (html (create "programming clojure" "tnoborio")))
  (GET "/find"
       (html (find-all))))       

(defservice bomitoban-app)
