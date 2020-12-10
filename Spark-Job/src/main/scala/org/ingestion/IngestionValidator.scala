package org.ingestion

import java.util.Properties

import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.jobHistory.JobHistory
import org.readerwriter.DataFrameReaderWriter

object IngestionValidator {

  val LOGGER = Logger.getLogger(getClass.getClass)

  val spark = SparkSession.active

  private def readIngestionData(prop: Properties): DataFrame = {
    DataFrameReaderWriter.read("mysql", "CONTROL_INFO", prop)
  }

  def findLatestLoad(prop: Properties): Array[Row] = {
    val time = JobHistory.findLatestSuccessfulJobTime(prop)
    LOGGER.info("Latest time is: " + time)
    val control_info_df = readIngestionData(prop)
    control_info_df.filter(control_info_df("INGESTION_TIME") > time and
      control_info_df("TICKER") === spark.conf.get("ticker")).toDF().collect()
  }
}
