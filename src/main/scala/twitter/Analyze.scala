// This model uses both jsonRDD and schemaRDD to process global twitter data.
// Each query answers a analytical question on the twitter data set.
// Some queries will have UDFs; queries that don't work yet are commented out.
// Tweets are downloaded using the decahose app that retrieves feeds to a local HDFS.

package com.ibm.apps.twitter_classifier

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.google.gson.{GsonBuilder, JsonParser}
import org.apache.spark.sql.SQLContext


/**
 * Pulls tweets from input files and runs a set of analytics queries and measure time 
 */

object Analyze {
  val jsonParser = new JsonParser()
  val gson = new GsonBuilder().setPrettyPrinting().create()

  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("Usage: " + this.getClass.getSimpleName + " <tweetInputDirectory>")
      System.exit(1)
    }

    val Array(tweetInput) = args

    println("Initializing Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // build tweets table using json in spark sql
    // the jsonFile RDD does not deal with empty lines well
    // val tweetTable = sqlContext.jsonFile(tweetInput).filter(r => r.length != 0)
    // instead, using textFile, rid the faulty lines, then create jsonRDD explicitly

    val texts = sc.textFile(tweetInput).filter(l => l.trim !="")
    val tweetTable = sqlContext.jsonRDD(texts)

    // register and cache it since we are re-using throughput
    tweetTable.registerTempTable("tweetTable")
    sqlContext.cacheTable("tweetTable")

    println("------Tweet table Schema---")
    tweetTable.printSchema()

    // println("----Sample Tweet Text-----")
    // sqlContext.sql("SELECT body FROM tweetTable LIMIT 100").collect().foreach(println)

    // read all tweets from input files 
    val tweets = sqlContext.sql("SELECT body FROM tweetTable WHERE body <> '' ").map(r => r.getString(0))
    val allcount = tweets.count()


    // timer wrapper to report query times; wrap around each sql call
    def time[A](f: => A) = {
        val s = System.nanoTime
        val ret = f
        println("query time: "+(System.nanoTime-s)/1e9+" sec")
        ret
    }

    // Q1 count the most active languages
    println("Q1 ------ Total count by languages Lang, count(*) ---")
    time {
       sqlContext.sql("SELECT actor.languages, COUNT(*) as cnt FROM tweetTable GROUP BY actor.languages ORDER BY cnt DESC LIMIT 25").collect.foreach(println)
       println("Q1 completed.") }
    
    // Q2 earliest and latest tweet dates 
    println("Q2 ------ earliest and latest tweet dates ---")
    time {
       sqlContext.sql("SELECT timestampMs as ts FROM tweetTable WHERE timestampMs <> '' ORDER BY ts DESC LIMIT 1").collect.foreach(println)
       sqlContext.sql("SELECT timestampMs as ts FROM tweetTable WHERE timestampMs <> '' ORDER BY ts ASC LIMIT 1").collect.foreach(println)
       println("Q2 completed.") }

    // Q3 Which time zones are the most active per day? 
    println("Q3 ------ Which time zones are the most active per day? ---")
    time {
       sqlContext.sql("""
  	SELECT
	 actor.twitterTimeZone,
	 SUBSTR(postedTime, 0, 9),
	 COUNT(*) AS total_count
	FROM tweetTable 
	WHERE actor.twitterTimeZone IS NOT NULL
	GROUP BY
	 actor.twitterTimeZone,
	 SUBSTR(postedTime, 0, 9)
	ORDER BY total_count DESC
	LIMIT 15 """).collect.foreach(println)
       println("Q3 completed.") }

    // Q4 Who is most influential?
    println("Q4 ------ Who is most influential?  ---")
    time {
       sqlContext.sql("""
	SELECT
	 t.retweeted_screen_name,
         t.tz,
	 sum(retweets) AS total_retweets,
	 count(*) AS tweet_count
	FROM (SELECT
	        actor.displayName as retweeted_screen_name,
	        body,
		actor.twitterTimeZone as tz,
	        max(retweetCount) as retweets
	      FROM tweetTable WHERE body <> ''
	      GROUP BY actor.displayName, actor.twitterTimeZone,
	               body) t
	GROUP BY t.retweeted_screen_name, t.tz
	ORDER BY total_retweets DESC
	LIMIT 10 """).collect.foreach(println)
       println("Q4 completed.") }

    // Q5 Top devices used among all Twitter users
    println("Q5 ------ Top devices used among all Twitter users ---")
    time {
       sqlContext.sql("""
        SELECT
         generator.displayName,
         COUNT(*) AS total_count
        FROM tweetTable
        WHERE  generator.displayName IS NOT NULL
        GROUP BY generator.displayName
        ORDER BY total_count DESC
        LIMIT 20 """).collect.foreach(println)
       println("Q5 completed.") }


    sc.stop()

  }
}
