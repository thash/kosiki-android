(ns com.memerelics.kosiki.main
  (:use [com.memerelics.kosiki.layout :as l]
        [neko.activity :only [defactivity set-content-view!]]
        [neko.resource :as r] ;; r/get-string :keyword, etc
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.listeners.adapter-view :only [on-item-click]]
        [neko.ui.adapters :only [ref-adapter]])
  (:require [cheshire.core :as json]
            [clojure.string :as s])
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
(def word-list-items (atom []))

(defn get-element [base id] (id (.getTag base)))

(defn get-json [url]
  (let [client (DefaultHttpClient.)
        res (.. client (execute (HttpGet. url)))
        body (slurp (.. res getEntity getContent))]
    (json/decode body true)))

;; TODO: handle multiple params
(defn api-get [path] (get-json (str "http://" domain "/" path "?api_key=" api_key)))

(defn update-word-list [words]
  (on-ui (reset! word-list-items words)))

(defactivity com.memerelics.kosiki.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! a (make-ui l/main)) ;; NOTE: change a -> this for release build
     ;; NOTE: cause error when use ref-adapter at toplevel.
     (.setAdapter
      (get-element l/main-layout ::l/word-list)
      (ref-adapter
       (fn [] l/word-row)
       (fn [position view _ word] ;; called when word-list-items is updated
         (.setText (get-element (get-element view ::l/problem-block) ::l/word-text) (:text word))
         (.setText (get-element (get-element view ::l/problem-block) ::l/word-created-at)
                   (s/replace (:created_at word) #"^(\d+)-(\d+)-(\d+).*" "$1/$2/$3"))
         (.setText (get-element (get-element view ::l/answer-block) ::l/word-ans)  (:ans word)))
       word-list-items)))
    (future (update-word-list (api-get "words")))))
