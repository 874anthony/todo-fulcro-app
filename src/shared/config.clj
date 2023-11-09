(ns config
  (:require
    [com.fulcrologic.fulcro.server.config :as fserver]
    [mount.core :refer [defstate args]]
    [taoensso.timbre :as log]))

(defstate config
    :start (let [{:keys [config overrides]
                  :or {config "config/defaults.edn"}} (args)
                  loaded-config (merge (fserver/load-config! {:config-path config}) overrides)]
              (log/info "Loading config" config)
             loaded-config))
