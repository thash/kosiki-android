(ns com.memerelics.kosiki.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading]
        [neko.ui])
  (:import (org.apache.http HttpResponse)
           (org.apache.http.client HttpClient)
           (org.apache.http.client.methods HttpGet)
           (org.apache.http.impl.client DefaultHttpClient)))

(def domain "localhost:8080")
(def api_key "de6zEHyY1sKLACgz-Tkg")

(declare android.widget.LinearLayout main-layout)
(def main [:linear-layout {:orientation :vertical,
                                  :id-holder true, :def `main-layout}
                  [:text-view {:text "wei from Clojure!"}]
                  [:text-view {:text "-- default --" :id ::words}]])

(defn http-get [url]
  (let [client (DefaultHttpClient.)
        res (.. client (execute (HttpGet. url)))
        body (slurp (.. res getEntity getContent))]
    body))

(defactivity com.memerelics.kosiki.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui (set-content-view! a (make-ui main)))
    (future (let [json (http-get (str "http://" domain "/words?api_key=" api_key))]
              (on-ui (.setText (::words (.getTag main-layout)) json))))))
