package models.DAO

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object DbConfiguration {
  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.default")
}
