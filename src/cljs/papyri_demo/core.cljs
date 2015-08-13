(ns papyri-demo.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Test data

(def test-data {:thought "Lorem ipsum dolor sit amet"
                :category "Latin"
                :date "Aug. 13"})

;; -------------------------
;; Components

(defn table-row [data]
  [:tr
   [:td.mdl-data-table__cell--non-numeric
    (:thought data)]])

(defn table [name]
  [:table.mdl-data-table.mdl-js-data-table.mdl-shadow--2dp.papyri-table
   [:caption.table-caption [:h6 name]]
   [:tbody
    (repeat 10 (table-row test-data))]])

(defn table-container [table]
  [:div.table-container.mdl-grid
   [:div.mdl-cell.mdl-cell--12-col
    [:div.paper-backdrop.mdl-card.mdl-shadow--2dp
     table]]])

(defn search-bar []
  [:form
   {:action "#"}
   [:div.mdl-textfield.mdl-js-textfield.mdl-textfield--expandable
    [:label.mdl-button.mdl-js-button.mdl-button--icon
     {:for "search"}
     [:i.material-icons
      "search"]]
    [:div.mdl-textfield__expandable-holder
     [:input#search.mdl-textfield__input
      {:type "text"}]
     [:label.mdl-textfield__label
      {:for "sample-expandable"}
      "Expandable Input"]]]])

(comment
  (defn fab []
    [:a#view-source.mdl-button.mdl-js-button.mdl-button--raised.mdl-js-ripple-effect.mdl-color--accent.mdl-color-text--accent-contrast
     {:href "https://github.com/google/material-design-lite/blob/master/templates/text-only/", :target "_blank"}
     "View Source"]))

;; -------------------------
;; Views

(defn home-page []
  [:div.layout-transparent.mdl-layout.mdl-js-layout.mdl-layout--fixed-header.has-drawer
   [:header.mdl-layout__header.mdl-layout__header--transparent
    [:div.mdl-layout__header-row
     [:span.mdl-layout-title "Papyri"]
     [:div.mdl-layout-spacer]
     (search-bar)]]
   [:div.mdl-layout__drawer
    [:span.mdl-layout-title "Papyri"]
    [:nav.mdl-navigation
     [:a.mdl-navigation__link "About"]
     [:a.mdl-navigation__link "Settings"]
     [:a.mdl-navigation__link "Latin"]
     [:a.mdl-navigation__link "Ea"]]]
   [:main.mdl-layout__content
    [:div
     (table-container (table "Latin"))
     (table-container (table "Ea"))]]])

(defn about-page []
  [:div [:h2 "About papyri-demo"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
