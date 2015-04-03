name := "SparkSQLTwitterAnalyzer"

version := "1.2.1"
		
scalaVersion := "2.10.4"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.1.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.1.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-hive_2.10" % "1.1.0" % "provided"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.2.0" % "provided"
libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"
libraryDependencies += "org.apache.spark" % "spark-streaming-twitter_2.10" % "1.2.0" % "provided"
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.5" % "provided"
