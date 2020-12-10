package org.readerwriter

import java.util.Properties

import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.{Failure, Success, Try}

object DataFrameReaderWriter {

  val LOGGER = Logger.getLogger(getClass.getClass)

  val spark = SparkSession.active

  def read(fileType: String, tableName: String, prop: Properties): DataFrame = {
    fileType match {
      case "mysql" => {
          Try(spark.read.format("jdbc").option("url", prop.getProperty("jdbc.mysql.url")).
            option("user", prop.getProperty("jdbc.mysql.username")).option("password", prop.getProperty("jdbc.mysql.password")).
            option("dbtable", tableName).option("driver", prop.getProperty("jdbc.mysql.driver")).load()) match {
            case Success(x) => x
            case Failure(exception) => {LOGGER.error(exception.printStackTrace()) ;throw new Exception(exception.getMessage)}
          }
      }
      case "s3" => {
        LOGGER.info("reading ingestion file " + tableName)
        Try(spark.read.parquet(tableName)) match {
          case Success(x) => x
          case Failure(e) => {
            LOGGER.error("Error reading the parquet file" + e.getMessage)
            throw e
          }
        }
      }
    }
  }

  def write(destination: String, df: DataFrame, prop: Properties, tableName: String = null): Unit = {
    destination match {
      case "mysql" => df.write.mode("append").format("jdbc").option("url", prop.getProperty("jdbc.mysql.url")).
        option("user", prop.getProperty("jdbc.mysql.username")).option("password", prop.getProperty("jdbc.mysql.password")).
        option("dbtable", tableName).option("driver", prop.getProperty("jdbc.mysql.driver")).save()
      case "redshift" => df.write.mode("append").format("jdbc").option("url", prop.getProperty("jdbc.redshift.url")).
        option("user", prop.getProperty("jdbc.redshift.username")).option("password", prop.getProperty("jdbc.redshift.password")).
        option("dbtable", tableName).option("driver", prop.getProperty("jdbc.redshift.driver")).save()
      case "s3" => df.write.mode("append").parquet(tableName)
    }
  }

}
