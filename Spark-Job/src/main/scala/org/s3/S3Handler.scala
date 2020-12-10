package org.s3

import java.util.Properties

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

import scala.util.{Failure, Success, Try}

object S3Handler {
  val LOGGER = Logger.getLogger(getClass.getClass)

  val spark = SparkSession.active

  /*This function is used to establish connection with S3.*/
  def checkS3Connection(prop: Properties): AmazonS3 = {
    Try(new AWSStaticCredentialsProvider(new BasicAWSCredentials(prop.getProperty("aws.s3.accesskey"), prop.getProperty("aws.s3.secretkey")))) match {
      case Success(aws_credentials) => {
        Try(AmazonS3ClientBuilder.standard().withCredentials(aws_credentials).withRegion(prop.getProperty("aws.s3.region")).build()) match {
          case Success(client) => client
          case Failure(_) => throw new Exception("Error creating client for S3")
        }
      }
      case Failure(_) => throw new Exception("Error reading AWS credentials")
    }
  }

  def createBucket(bucketName: String, client: AmazonS3): Unit = {
    client.doesBucketExistV2(bucketName) match {
      case true => LOGGER.info(s"Bucket by the name {} already exists".format(bucketName))
      case false => client.createBucket(bucketName)
    }
  }

  def configureS3InHadoop(prop: Properties): Unit = {
    val hadoopConf = spark.sparkContext.hadoopConfiguration
    LOGGER.info(prop.getProperty("aws.s3.accesskey"))
    hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    hadoopConf.set("fs.s3.awsAccessKeyId", prop.getProperty("aws.s3.accesskey"))
    hadoopConf.set("fs.s3.awsSecretAccessKey", prop.getProperty("aws.s3.secretkey"))
  }
}