package org.dt

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.common.CommonUtils
import org.ingestion.IngestionValidator
import org.jobHistory.JobHistory
import org.readerwriter.DataFrameReaderWriter
import org.s3.S3Handler

object DataTransformer {
  val LOGGER = Logger.getLogger(getClass.getClass)

  def main(args: Array[String]): Unit = {

    LOGGER.info("Initiating spark job")
    val spark = SparkSession.builder().appName("Data-Transformer").getOrCreate()

    LOGGER.info("Spark job initiated for " + args(0))
    spark.conf.set("ticker", args(0))

    LOGGER.info("Loading configuration.properties")
    val prop = CommonUtils.loadPropertiesFile()

    try{
    LOGGER.info("Saving S3 configuration in spark configuration")
    S3Handler.configureS3InHadoop(prop)

    LOGGER.info("Checking for new ingested data")
    val ingested_df = IngestionValidator.findLatestLoad(prop)

    if(ingested_df.isEmpty) {
      LOGGER.info("No data to be processed. Please check if new data has been ingested")
      JobHistory.write("SUCCESS", prop)
    } else{
      ingested_df.foreach(row => {
        LOGGER.debug("Storing start-time in spark conf")
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:ss")
        spark.conf.set("start-time", LocalDateTime.now().format(fmt))
        val transformed_df = Transformations.transformations(row.get(2).toString, prop)
        DataFrameReaderWriter.write(prop.getProperty("output.destination.type"), transformed_df,
          prop, prop.getProperty("output.destination.tablename"))
      })
      LOGGER.info("Inserting record in JOB_HISTORY table")
      JobHistory.write("SUCCESS", prop)
      LOGGER.info("Job successfully executed!")
    }
  }
    catch {
      case e => {
        LOGGER.error("Error while transforming data. " + e.getMessage)
        JobHistory.write("FAILURE", prop)
        throw e
      }
    }
  }
}
