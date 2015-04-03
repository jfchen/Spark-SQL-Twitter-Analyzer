# Spark-SQL-Twitter-Analyzer
Process large amount of Twitter data using Spark SQL (and its JSON support). Answers questions like "What are the most popular languages?", "Who is most influential?", "Which time zones are most active during a day?" and more.

With Spark SQL support for JSON dataset, you are ready to analyze Twitter data in Spark using familiar SQL syntax.

For example, to answer the question "Which time zones are the most active per day?", you simply run this query in
Spark:

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
        LIMIT 15
        
This package has 5 Twitter queries implemented in Scala (and can be built into a standalone app which yo        
