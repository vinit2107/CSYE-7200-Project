package org.dt

import java.util.Properties

import org.apache.log4j.Logger
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.readerwriter.DataFrameReaderWriter

object Transformations {

  val LOGGER = Logger.getLogger(getClass.getClass)

  private def readIngestedData(path: String, prop: Properties): DataFrame ={
    DataFrameReaderWriter.read(prop.getProperty("ingestion.destination.type"), path, prop)
  }

  def transformations(path: String, prop: Properties): DataFrame = {
    val df = readIngestedData(path, prop)
    val sma3 = movingAverage3bar(df)
    val sma5 = movingAverage5bar(sma3)
    val sma13 = movingAverage13bar(sma5)
    val typicalValueDf = typicalValue(sma13)
    val bb3low = bollingerBandLow3(typicalValueDf)
    val bb3high = bollingerBandHigh3(bb3low)
    val bb5low = bollingerBandLow5(bb3high)
    val bb5high = bollingerBandHigh5(bb5low)
    val bb13high = bollingerBandHigh13(bb5high)
    val final_df1 = bollingerBandLow13(bb13high)
    val final_df = final_df1.withColumn("time", to_timestamp(col("time")))
    final_df.drop("typicalValue")
  }

  private def movingAverage3bar(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(year(df("time"))).rowsBetween(-2, 0)
    df.withColumn("3_sma", round(avg(col("close")).over(window), 3))
  }

  private def movingAverage5bar(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-4, 0)
    df.withColumn("5_sma", round(avg(col("close")).over(window), 3))
  }

  private def movingAverage13bar(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-12, 0)
    df.withColumn("13_sma", round(avg(col("close")).over(window), 3))
  }

  private def typicalValue(df: DataFrame): DataFrame = {
    df.withColumn("typicalValue", round((col("low") + col("high") + col("close"))/3, 3))
  }

  private def bollingerBandLow3(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-2, 0)
    val df1 = df.withColumn("bb_3_low_1", round(col("typicalValue") - stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_3_low_1.5", round(col("typicalValue") - lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_3_low_2.5", round(col("typicalValue") - lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }

  private def bollingerBandHigh3(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-2, 0)
    val df1 = df.withColumn("bb_3_high_1", round(col("typicalValue") + stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_3_high_1.5", round(col("typicalValue") + lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_3_high_2.5", round(col("typicalValue") + lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }

  private def bollingerBandLow5(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-4, 0)
    val df1 = df.withColumn("bb_5_low_1", round(col("typicalValue") - stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_5_low_1.5", round(col("typicalValue") - lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_5_low_2.5", round(col("typicalValue") - lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }

  private def bollingerBandHigh5(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-4, 0)
    val df1 = df.withColumn("bb_5_high_1", round(col("typicalValue") + stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_5_high_1.5", round(col("typicalValue") + lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_5_high_2.5", round(col("typicalValue") + lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }

  private def bollingerBandLow13(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-12, 0)
    val df1 = df.withColumn("bb_13_low_1", round(col("typicalValue") - stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_13_low_1.5", round(col("typicalValue") - lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_13_low_2.5", round(col("typicalValue") - lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }

  private def bollingerBandHigh13(df: DataFrame): DataFrame = {
    val window = Window.partitionBy(month(df("time"))).rowsBetween(-12, 0)
    val df1 = df.withColumn("bb_13_high_1", round(col("typicalValue") + stddev(col("typicalValue")).over(window), 3))
    val df2 = df1.withColumn("bb_13_high_1.5", round(col("typicalValue") + lit(1.5) * stddev(col("typicalValue")).over(window), 3))
    df2.withColumn("bb_13_high_2.5", round(col("typicalValue") + lit(2.5) * stddev(col("typicalValue")).over(window), 3))
  }
}
