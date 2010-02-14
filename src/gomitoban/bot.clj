(ns gomitoban.bot
  (:use
   clojure.set
   clojure.contrib.str-utils)
  (:require
   [appengine-clj.datastore :as ds])
  (:import
   (com.google.appengine.api.datastore Query)
   (twitter4j Twitter)
   (twitter4j Paging)))

(defn setup-tw
  [username password]
  (def tw (Twitter. username password)))

(defn- match? [re str]
  (re-matches re str))

(defn- seq-with-idx [seq]
  (map list seq
       (map #(keyword (str %)) (range (count seq)))))
; (seq-with-idx '(a b c))

(def weeks
     (map #(.toString %) "日月火水木金土"))

(def week-matches
     (map #(java.util.regex.Pattern/compile
	    (str "^" (.toString %) ".*"))
	  weeks))

(defn week-key [str]
  (some #(if (match? (first %) str)
	   (keyword (nth % 1))
	   nil)
	(seq-with-idx week-matches)))

(defn- trim [str]
  (if (= (class str) String)
    (.trim str)
    ""))

(defn- split [field]
  (let [[k v] (re-split #"=|＝" field)]
    (list (week-key (trim k)) (trim v))))
; (split "月曜=ほげ")
; (split "火=燃えるゴミ")

(defn- to-week-map [body]
  (reduce #(assoc %1 (first %2) (nth %2 1)) {}
	  (map split (re-seq #"[^,、]+" body))))
; (to-week-map "火=燃えるゴミ、金曜日= 燃えないゴミ, 水曜日＝ペットボトル  ")

(defn parse-content [content]
  (let [week-map (to-week-map content)]
    (map #(% week-map) (map #(keyword (str %)) (range 7)))))
; (parse-content "火=燃えるゴミ、金曜日= 燃えないゴミ, 水曜日＝ペットボトル  ")

(defn create-schedule [userid content]
  (ds/create {:kind "Schedule" :userid userid
	      :content (str-join "\t" (parse-content content)) :date (java.util.Date.)}))

(defn find-all []
  (ds/find-all (doto (Query. "Greeting") (.addSort "date"))))

(defn followers []
  (seq (.. tw getFollowersIDs getIDs)))

(defn friends []
  (seq (.. tw getFriendsIDs getIDs)))

(defn unfollowers [fllws frnds]
  (seq (difference (set fllws) (set frnds))))

(defn follow! [ids]
  (dorun (map #(.createFriendship tw %) ids)))

(defn unfollow! [ids]
  (doall (map #(.createFriendship tw %) ids)))
    
(defn mentions []
  (map #(hash-map :id (.getId %) :text (.getText %) :user_id (.. % getUser getId))
       (seq (.getMentions tw ))))

(defn update [status]
  (.updateStatus tw status))

(defn main []
  (let [fllws (followers)
	frnds (friends)
	uf (unfollowers fllws frnds)]
    (println "un followers: " uf)
    (println "friends: " frnds)
    (follow! uf)))

