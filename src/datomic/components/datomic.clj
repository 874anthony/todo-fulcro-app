(ns datomic
  (:require
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [datomic.client.api :as d]
    [mount.core :refer [defstate]]))
