(ns com.memerelics.kosiki.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.resource :as r] ;; r/get-string :keyword, etc
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.listeners.adapter-view :only [on-item-click]]
        [neko.ui.adapters :only [ref-adapter]])
  (:require [cheshire.core :as json])
  (:import (org.apache.http HttpResponse)
           (org.apache.http.client HttpClient)
           (org.apache.http.client.methods HttpGet)
           (org.apache.http.impl.client DefaultHttpClient)
           (android.widget ArrayAdapter)))

;; (in-ns 'com.memerelics.kosiki.main)

;; Forwarding Android device's localhost to alm's localhost using Chrome Remote debug,
;; then nginx proxy_pass it to DebianVM:3000 in which Rails running.
(def domain "localhost:8080")
(def api_key "de6zEHyY1sKLACgz-Tkg")

;; using ref instead of atom is better?
(def word-list-items (atom ["Loading..."]))

(declare android.widget.LinearLayout main-layout)
(def main [:linear-layout {:orientation :vertical,
                           :id-holder true, :def `main-layout}
           [:text-view {:id ::title :text "wei from Clojure!"}]
           [:list-view {:id ::word-list}]])

(defn get-element [id] (id (.getTag main-layout)))

(defn get-json [url]
  (let [client (DefaultHttpClient.)
        res (.. client (execute (HttpGet. url)))
        body (slurp (.. res getEntity getContent))]
    (json/decode body true)))

;; TODO: handle multiple params
(defn api-get [path] (get-json (str "http://" domain "/" path "?api_key=" api_key)))

(defn update-word-list [words]
  (on-ui
   (.setText (get-element ::title) (:text (rand-nth words))) ;; make sure working
   (reset! word-list-items (map #(:text %) words))))

(defactivity com.memerelics.kosiki.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! a (make-ui main))
     ;; NOTE: cause error when use ref-adapter at toplevel.
     (.. (get-element ::word-list)
         (setAdapter (ref-adapter (fn [] [:text-view {:text "-- created --"}]) ;; not shown
                                  (fn [position view _ data] (.setText view (str position ": " data)))
                                  word-list-items))))
    (future (update-word-list (api-get "words")))
    ))
