(ns picture-gallery.routes.services
  (:require [picture-gallery.routes.services.auth :as auth]
            [picture-gallery.routes.services.upload :as upload]
            [compojure.api.upload :refer [wrap-multipart-params TempFileUpload]]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema UserRegistration
  {:id             String
   :pass           String
   :pass-confirm   String})

(s/defschema Result
  {:result                   s/Keyword
   (s/optional-key :message) String})

(declare service-routes)
(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Picture Gallery API"
                           :description "Public Services"}}}}
  (POST "/register" req
    :return Result
    :body [user UserRegistration]
    :summary "register a new user"
    (auth/register! req user))

  (POST "/login" req
    :header-params [authorization :- String]
    :summary "log in the user and create a session"
    :return Result
    (auth/login! req authorization))

  (POST "/logout" []
    :summary "remove user session"
    :return Result
    (auth/logout!)))

(declare restricted-service-routes)
(defapi restricted-service-routes
  {:swagger {:ui "/swagger-ui-private"
             :spec "/swagger-private.json"
             :data {:info {:version "1.0.0"
                           :title "Picture Gallery API"
                           :description "Private Services"}}}}
  (POST "/upload" req
    :multipart-params [file :- TempFileUpload]
    :middleware [wrap-multipart-params]
    :summary "handles image upload"
    :return Result
    (upload/save-image! (:identity req) file)))