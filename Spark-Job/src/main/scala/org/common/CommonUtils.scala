package org.common

import java.util.Properties

import scala.io.Source
import scala.util.{Failure, Success, Try}

object CommonUtils {

  def loadPropertiesFile():Properties = {
    val prop = new Properties()
    Try(getClass().getClassLoader.getResource("configuration.properties")) match {
      case Success(url) => {
        val source = Source.fromURL(url)
        prop.load(source.bufferedReader())
        prop
      }
      case Failure(_) => throw new Exception("Error reading configuration.properties")
    }
  }

}
