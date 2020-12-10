package org.jobHistory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.readerwriter.DataFrameReaderWriter

object JobHistory {

  val LOGGER = Logger.getLogger(getClass.getClass)

  val spark = SparkSession.active

  private def readJobHistory(prop: Properties): DataFrame = {
    DataFrameReaderWriter.read("mysql", "JOB_HISTORY", prop)
  }

  def findLatestSuccessfulJobTime(prop: Properties) = {
    val read_df = readJobHistory(prop)
    val df = read_df.filter(read_df("STATUS") === "SUCCESS" and read_df("TICKER") === spark.conf.get("ticker"))
    df.show()
    LOGGER.info("Successfully read the JOB_HISTORY")
    val row = df.orderBy(col("END_TIME").desc)
    if(!row.isEmpty){
      val first = row.first()
      val index = first.fieldIndex("END_TIME")
      LOGGER.info("Got first")
      first.get(index)
    }else{
      "1970-01-01 00:00:00"
    }
  }

  def write(status: String, prop: Properties) = {
    val spark = SparkSession.active
    import spark.implicits._
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:ss")
    val row = Seq((spark.sparkContext.applicationId, spark.conf.get("ticker"), status, spark.conf.get("start-time", LocalDateTime.now().format(fmt))))
      .toDF("APPLICATION_ID", "TICKER", "STATUS", "START_TIME")
    DataFrameReaderWriter.write("mysql", row, prop, "JOB_HISTORY")
  }
}
