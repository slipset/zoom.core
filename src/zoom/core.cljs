(ns ^:figwheel-hooks zoom.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [clojure.string :as str]))


;; http://www.petercollingridge.co.uk/tutorials/svg/interactive/dragging/
;; http://www.petercollingridge.co.uk/tutorials/svg/interactive/pan-and-zoom/


(println "This text is printed from src/zoom/core.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))


;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom {:text "Hello world!"
         :matrix [1 0 0 1 0 0]
         :view-box [0 0 250 150]}))

(defn center [view-box]
  [(/ (nth view-box 2) 2)
   (/ (nth view-box 3) 2)])

(defn get-app-element []
  (gdom/getElement "app"))

(defn pan [dx dy matrix]
  (js/console.log "panning" dx dy)
  (-> matrix
      (update 4 + dx)
      (update 5 + dy)))

;; transformMatrix[4] += (1 - scale) * centerX;
;;transformMatrix[5] += (1 - scale) * centerY;

(defn zoom [view-box scale matrix]
  (let [scaled (mapv (partial * scale) matrix)
        [center-x center-y ] (center view-box)
        scaled (-> scaled
                   (update 4 + (* (- 1 scale) center-x))
                   (update 5 + (* (- 1 scale) center-y)))]
    scaled))

(defn handle-pan [dx dy]
  (swap! app-state update :matrix
         (partial pan dx dy)))

(defn handle-zoom [scale]
  (let [view-box (:view-box @app-state)]
    (swap! app-state update :matrix (partial zoom view-box scale))))

(defn start-panning [_]
  (swap! app-state assoc :panning true))

(defn stop-panning [_]
  (swap! app-state assoc :panning false))

(defn panning [e]
  (if (:panning @app-state)
    (let [svg (gdom/getElement "matrix-group")
          ctm (.getScreenCTM svg)
          view-box (:view-box @app-state)
          offset-x (/ (- (.-pageX e) (.-e ctm)) (.-a ctm))
          offset-y (/ (- (.-pageY e) (.-f ctm)) (.-d ctm))
          view-box (:view-box @app-state)]
      (js/console.log "panning" offset-x offset-y)
      (swap! app-state update :matrix
             (partial
              pan (- offset-x (nth view-box 2)) (- offset-y (nth view-box 3)))))))

(defn hello-world []
  [:div
   {:on-mouse-move panning
    :on-mouse-down start-panning
    :on-mouse-up stop-panning
    }
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/zoom/core.cljs and watch it lol"]
   [:svg {:viewBox (str/join " "(:view-box @app-state))}
    [:g {:id "matrix-group"
         :transform (str "matrix(" (str/join " " (:matrix @app-state)) ")")}
     [:path {:id "WA" :class "territory" :d "m 38.3, 168.2 c -1.9,-0.6 -3.6,-1.1 -3.8,-1.3 -0.2,-0.2 0.8,-2.5 2.25,-5.2 1.4,-2.7 2.6,-5.5 2.6,-6.2 0,-0.8 -1.8,-4.25 -4,-7.7 -2.2,-3.5 -4,-7.1 -4,-8.1 l 0,-1.8 -6.4,-10.3 -6.4,-10.3 1.8,0 -7,-14.8 0.2,-7.3 c 0.1,-4 0.1,-8.9 0.1,-10.9 l -0.1,-3.5 5.5,-6 c 3,-3.3 5.5,-6.3 5.5,-6.7 0,-0.4 0.9,-0.8 1.9,-0.8 l 1.9,0 9.8,-6.1 13.8,-2.2 4.1,-2.6 8.4,-15 -1.2,-3 3.3,-5 1.8,1.1 4,-2.5 0,-3.1 4.6,-4.3 8.5,0 3.4,3.8 c 1.9,2.1 3.4,4.3 3.4,5 l 0,1.3 2.5,-0.6 L 97.5,33.2 c 0,38.3 0,77 0,113.8 l -4.6,1.6 -4.5,2.2 -2.3,5.6 -1.6,0.5 c -0.9,0.3 -4.8,1 -8.6,1.6 l -7,1.1 -8.2,4.9 -8.2,4.9 -5.3,0 c -2.9,0 -6.9,-0.5 -8.8,-1.1 z"}]
     [:path {:id "QLD" :class "territory" :d "m 200.3,123.9 c -11.8,-0.7 -14.8,-0.8 -22.8,-1.3 0,-8.5 0,-21 0,-21 l -16,0 c 0,0 0,-39.3 0,-60 l 6,3.5 5.2,3.5 4.4,0 6,-9.8 3.4,-24.6 2.8,-5.7 c 1.6,-3.1 3.3,-6 3.9,-6.3 l 1,-0.6 1.8,4.3 c 1,2.4 2.2,6.6 2.6,9.3 0.4,2.8 1.2,7.1 1.7,9.8 l 1,4.8 4.8,0 3.2,5 2.8,11 c 1.5,6.1 3.3,11.7 4,12.5 0.6,0.8 3.4,2.9 6.2,4.6 l 5,3.2 5,10 3.8,1 1.2,5.2 11.2,14.8 1.2,7.1 c 0.7,3.9 1.5,8.7 1.9,10.8 l 0.6,3.7 -1.3,5.1 -4.6,-1.1 -1.3,1.6 c -1.6,2 -3.7,2 -4.4,0.1 l -0.6,-1.5 -2.4,0 c -1.3,0 -4.3,0.7 -6.7,1.5 -2.3,0.8 -5.4,1.4 -6.9,1.3 -1.4,-0.1 -12.3,-0.7 -24.1,-1.4 z"}]
     [:path {:id "VIC" :class "territory" :d "m 219.5,177.3 0.8,1 c 0,0.6 2.3,2.7 5,4.8 l 5,3.8 -11.4,3.4 -5.7,5.4 -6.9,-3.7 -2.8,1.4 c -1.5,0.8 -3,1.5 -3.3,1.6 -0.3,0.1 -2.3,-0.9 -4.5,-2.2 c -2.2,-1.3 -6.3,-3.1 -9,-4 l -5,-1.6 -2.3,-2 -2.1,-2.1 c 0,-9 0,-17.9 0,-26.6 l 3.9,-1.3 3.1,0 1.6,3.4 1.9,0.5 2.4,0.5 1.5,0.9 4.7,6 3.2,3.9 c 3.2,0.9 6.5,1.7 9.2,2.4 3,0.8 4.7,0.9 6.2,1 l 2.9,2.6"}]
     [:path {:id "NSW" :class "territory" :d "m 220.5,176.4 -2.4,-2.7 -2.4,-1.5 c -2.1,-0.3 -6.2,-1.1 -9.1,-1.8 l -5.4,-1.4 -8.2,-10.4 -2.8,-0.7 -2.8,-0.7 -1,-1.9 -1,-1.9 -3.9,0 -4,1.5 0,-30.9 c 5.8,0.4 22.5,1.4 25.3,1.6 l 23.5,1.4 4.8,-1.2 c 4.6,-1.1 5.7,-2.1 8.3,-2 0,0 0.9,2.3 1.9,2.8 1.1,0.5 2.5,0.1 3.7,-0.3 1.1,-0.4 2,-2 3.1,-1.8 l 2.8,0.5 -1.3,5.1 c -0.7,2.7 -2.1,7.5 -2.9,10.9 -1.8,8.3 -3.1,11.6 -5.3,13.4 l -1.8,1.5 -4.1,12.5 c -2.2,6.9 -4.2,13.5 -4.4,14.7 l -0.3,2.2 -5.3,-4 -4.4,-4.2"} ]
     [:path {:id "SA" :class "territory" :d "m 176,181 -6.8,-9.4 0.7,-3.7 0.7,-3.7 -5.6,-6 -2.2,0.8 0.7,-7.8 -1.1,0 c -1.1,0 -2.2,1.1 -4.6,4.5 l -1.4,2 1,-4.8 c 0.6,-2.7 0.8,-5 0.6,-5.3 -0.8,-0.8 -6.1,2.5 -7.9,4.8 -0.9,1.2 -1.9,2 -2.1,1.8 -0.2,-0.3 -1.6,-2.6 -3.1,-5.2 l -2.7,-4.7 -11.4,-2.6 -19.2,0.2 -5.8,2.6 C 102.7,146.2 99,147.2 99,147 l 0,-44 77,0 z"}]
     [:g {:id "NT" :class "territory"}
      [:path {:d "m 99,33 3.9,0.4 c 2,0.2 4.4,-0.5 5.5,-1 l 1.9,-1 0,-2.5 0,-2.5 -2,0 0,-4 1.9,-1.7 c 1,-0.9 1.7,-2 1.5,-2.4 l -0.5,-0.7 6.8,-4.2 11.5,-1.4 0.8,-2.4 -5.6,-3 1.7,0 c 0.9,0 2,0.4 2.3,1 0.3,0.6 1.2,0.9 1.8,0.8 0.6,-0.1 3.4,0.8 6.2,2.1 l 5,2.4 7.5,0 0,3.7 -4.2,4 1.2,3.2 -3,5.8 17,11.2 0,60.8 -61,0 z"}]
      [:path {:d "m 115,8.7 -2.8,-0.4 0.8,-2.4 4.2,0 c 2.3,0 4.2,0.2 4.2,0.5 0,0.8 -2.3,3 -2.9,2.8 -0.3,-0 -1.8,-0.3 -3.4,-0.6 z"}]]
     [:g {:id "TAS" :class "territory"}
      [:path {:d "m 202.7,223.9 -2.7,-3.6 -0.1,-3.4 c -0.1,-1.9 -0.4,-3.7 -0.8,-4.1 -0.4,-0.4 -0.7,-1.8 -0.7,-3.1 l 0,-2.3 1,0 c 0.6,0 2.4,0.7 4.2,1.6 l 3.1,1.6 9.6,-1.2 0,7.2 -3.4,5.8 -2.6,-1 -1.7,3 c -0.9,1.6 -2,3 -2.4,3 -0.4,0 -1.9,-1.6 -3.4,-3.6 z"}]]
     [:g {:id "ACT" :class "territory"}
      [:path {:stroke-width "1.5" :stroke "#ffffff" :d "m 222,165.7 0.1,2.8 0.4,1.4 1,1 0.4,-0.9 0.2,2.9 2.6,1.6 1,-2.2 0,-3.6 c 0,0 -0.3,-0.5 -0.6,-1.1 0.8,-0.3 1.2,0.1 1.2,0.1 l 0.1,-3.4 1.5,-2 2.4,0.3 0.5,-1 -2.7,-1.3 c 0,0 -0.7,-1.8 -1.8,-2.4 -1.6,1.1 -4,2 -5.2,3.9 -0.4,0.7 -0.5,2.3 -0.5,2.3 z"}]]]
    [:circle {:cx "25" :cy "25" :r "21" :fill "white" :opacity "0.75"}]
    [:path {:class "button" :on-click #(handle-pan 0 25) :d "M25 5 l6 10 a20 35 0 0 0 -12 0z"} ]
    [:path {:class "button" :on-click #(handle-pan 25 0) :d "M5 25 l10 -6 a35 20 0 0 0 0 12z"} ]
    [:path {:class "button" :on-click #(handle-pan 0,-25) :d"M25 45 l6 -10 a20, 35 0 0,1 -12,0z"} ]
    [:path {:class "button" :on-click #(handle-pan -25, 0) :d "M45 25 l-10 -6 a35 20 0 0 1 0 12z"} ]
    [:circle {:class "compass" :cx "25" :cy "25" :r "10"}]
    [:circle {:class "button" :cx "25" :cy "20.5" :r "4" :on-click #(handle-zoom 0.8)}]
    [:circle {:class "button" :cx "25" :cy "29.5" :r "4" :on-click #(handle-zoom 1.25)}]
    [:rect {:class "plus-minus" :x "23" :y "20" :width "4" :height "1"}]
    [:rect {:class "plus-minus" :x "23" :y "29" :width "4" :height "1"}]
    [:rect {:class "plus-minus" :x "24.5" :y "27.5" :width "1" :height "4"}]]])


(defn mount [el]
  (reagent/render-component [hello-world] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
