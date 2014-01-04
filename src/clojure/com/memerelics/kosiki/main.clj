(ns com.memerelics.kosiki.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading]
        [neko.ui])
  (:require [cheshire.core :as json])
  (:import (org.apache.http HttpResponse)
           (org.apache.http.client HttpClient)
           (org.apache.http.client.methods HttpGet)
           (org.apache.http.impl.client DefaultHttpClient)
           (android.widget ArrayAdapter)))

(def domain "localhost:8080")
(def api_key "de6zEHyY1sKLACgz-Tkg")

(declare android.widget.LinearLayout main-layout)
(def main [:linear-layout {:orientation :vertical,
                                  :id-holder true, :def `main-layout}
                  [:text-view {:text "wei from Clojure!"}]
                  [:list-view {:id ::word-list}]])

(defn get-json [url]
  "repl> (first (get-json http://domain/words?api_key=.....))
         { :id 1, :text \"Something is out of whack\", :ans \"何か調子が悪い\",
           :pun \"\", :count 1, :done false,
           :updated_at \"2013-12-24T00:58:19.000+09:00\",:created_at \"2011-01-10T20:24:23.000+09:00\" }"
  (let [client (DefaultHttpClient.)
        res (.. client (execute (HttpGet. url)))
        body (slurp (.. res getEntity getContent))]
    (json/decode body true)))

(defactivity com.memerelics.kosiki.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui (set-content-view! a (make-ui main)))
    (future (let [data (get-json (str "http://" domain "/words?api_key=" api_key))]
              (on-ui
               ;; (.setText (::words (.getTag main-layout)) (:text (first data)))
               (.. (::word-list (.getTag main-layout))
                   (setAdapter (ArrayAdapter. a android.R$layout/simple_list_item_1 (map #(:text %) data))))
               )))
    ))
