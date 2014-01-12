(ns com.memerelics.kosiki.layout
  (:use [neko.notify :only [toast]])
  (:import (android.view Gravity View)))

(declare android.widget.LinearLayout main-layout)
(def main
  [:linear-layout {:orientation :vertical
                   :id-holder true :def `main-layout}
   [:list-view {:id ::word-list}]])

(def word-row
  [:relative-layout {:layout-width :fill :layout-height 120 :padding 10 :id-holder true
                     :on-click (fn [v]
                                 (if (= View/VISIBLE (.getVisibility (::problem-block (.getTag v))))
                                   (do (.setVisibility (::problem-block (.getTag v)) View/INVISIBLE)
                                       (.setVisibility (::answer-block (.getTag v)) View/VISIBLE))
                                   (do (.setVisibility (::problem-block (.getTag v)) View/VISIBLE)
                                       (.setVisibility (::answer-block (.getTag v)) View/INVISIBLE))))}
   [:relative-layout {:id ::problem-block
                      :layout-height :fill :id-holder true}
    [:linear-layout {:id ::spacer
                     :layout-width 50 :layout-height :fill}]
    [:text-view {:id ::word-text :layout-to-right-of ::spacer
                 :text-size 24 :text ""}]
    [:text-view {:id ::word-created-at :text "" :layout-align-parent-right true}]]
   [:relative-layout {:id ::answer-block :visibility View/INVISIBLE
                      :layout-height :fill :id-holder true}
    [:linear-layout {:id ::left-arrow
                     :layout-width 50 :layout-height :fill}]
    [:text-view {:id ::word-ans :layout-to-right-of ::left-arrow
                 :text-size 18 :text ""}]
    [:linear-layout {:id ::right-arrow :layout-align-parent-right true
                     :layout-width 50 :layout-height :fill}]]])

