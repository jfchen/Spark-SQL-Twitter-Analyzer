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
        
This package has 5 Twitter queries implemented in Scala (and can be built into a standalone app which you can run via the spark-submit program). Below is the output from running this app, on a 16 million Twitter dataset:

Q1 ------ Total count by languages Lang, count(*) ---
[ArrayBuffer(en),5777222]
[null,3727441]
[ArrayBuffer(ja),2704153]
[ArrayBuffer(es),1711197]
[ArrayBuffer(pt),659012]
[ArrayBuffer(ar),603793]
[ArrayBuffer(ru),264605]
[ArrayBuffer(id),234660]
[ArrayBuffer(ko),203726]
[ArrayBuffer(tr),115678]
[ArrayBuffer(fr),103877]
[ArrayBuffer(th),85506]
[ArrayBuffer(en-gb),77942]
[ArrayBuffer(de),33596]
[ArrayBuffer(it),27663]
[ArrayBuffer(nl),19889]
[ArrayBuffer(pl),16548]
[ArrayBuffer(zh-tw),7632]
[ArrayBuffer(sv),6311]
[ArrayBuffer(zh-cn),5290]
[ArrayBuffer(en-GB),3671]
[ArrayBuffer(el),3095]
[ArrayBuffer(hi),2726]
[ArrayBuffer(uk),2602]
[ArrayBuffer(msa),2362]
Q1 completed.
query time: 5.786566397 sec

Q2 ------ earliest and latest tweet dates ---
[2015-02-12T07:59:48.308+00:00]
[2015-02-12T00:19:18.258+00:00]
Q2 completed.
query time: 1.059922054 sec

Q3 ------ Which time zones are the most active per day? ---
[Eastern Time (US & Canada),2015-02-1,727688]
[Central Time (US & Canada),2015-02-1,587851]
[Brasilia,2015-02-1,527198]
[Tokyo,2015-02-1,510255]
[Pacific Time (US & Canada),2015-02-1,397727]
[Irkutsk,2015-02-1,342943]
[Atlantic Time (Canada),2015-02-1,244621]
[Seoul,2015-02-1,241879]
[Hawaii,2015-02-1,230342]
[Buenos Aires,2015-02-1,225498]
[Quito,2015-02-1,192758]
[Arizona,2015-02-1,192275]
[Santiago,2015-02-1,186726]
[Jakarta,2015-02-1,163999]
[Bangkok,2015-02-1,152740]
Q3 completed.
query time: 3.966009242 sec

Q4 ------ Who is most influential?  ---
[.,null,5639925,7120]
[あ,null,5497037,1798]
[。,null,4649930,1349]
[Please Zayn ,Brasilia,3665806,32]
[Age of #Rownevieve,Central Time (US & Canada),3366534,5]
[Emily Massey,null,3365595,3]
[Nevil Paperman,null,3364886,3]
[、,null,3360013,934]
[tai,Brasilia,3327049,90]
[使ってません,null,3194538,802]
Q4 completed.
query time: 10.938257191 sec

Q5 ------ Top devices used among all Twitter users ---
[Twitter for iPhone,3532734]
[Twitter for Android,2454865]
[Twitter Web Client,1361441]
[twittbot.net,341854]
[TweetDeck,301340]
[IFTTT,289989]
[Twitter for iPad,183512]
[twitterfeed,141179]
[dlvr.it,128650]
[Facebook,124822]
[Twitter for Android Tablets,114122]
[تطبيق تغريد دعاء,111659]
[Mobile Web (M2),105706]
[Twitter for Windows Phone,82332]
[Twitter for BlackBerry®,75975]
[Instagram,73833]
[TweetAdder v4,72258]
[تطبيق قرآني,72168]
[knzmuslim كنز المسلم,53971]
[Hootsuite,46453]
Q5 completed.
query time: 2.950492016 sec

Usage

1. To build, lay out the sbt src tree and copy this package into it, then run 'bin/sbt package', for example:

% bin/sbt package
[info] Loading project definition from /TestAutomation/sbt/project
[info] Set current project to SparkSQLTwitterAnalyzer (in build file:/TestAutomation/sbt/)
[info] Updating {file:/TestAutomation/sbt/}sbt...
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] Done updating.
[info] Compiling 1 Scala source to /TestAutomation/sbt/target/scala-2.10/classes...
[info] Packaging /TestAutomation/sbt/target/scala-2.10/sparksqltwitteranalyzer_2.10-1.2.1.jar ...
[info] Done packaging.
[success] Total time: 20 s, completed Apr 3, 2015 10:08:42 AM

2. Copy sampletweets2015.dat to your distributed file system (accessible by Spark)

% hadoop fs -copyFromLocal /yourdir/sampletweets2015.dat /twitter/data/.

3. Run the app in Spark

% sudo runuser -l yarn -c "/usr/bin/spark-submit --master yarn-cluster  --name Analyze --executor-memory 4096m  --num-executors 100 --class com.ibm.apps.twitter_classifier.Analyze /TestAutomation/sbt/target/scala-2.10/sparksqltwitteranalyzer_2.10-1.2.1.jar /twitter/data  > /tmp/Analyze.out  2>&1 "

.end.

Output of the program will be in stdout of YARN job file (via historyserver

http://yourhost.com:19888/jobhistory/logs/datanode2.com:45454/container_1427990857060_0016_01_000001/container_1427990857060_0016_01_000001/yarn

Sample output:

------Tweet table Schema---
root
 |-- actor: struct (nullable = true)
 |    |-- displayName: string (nullable = true)
 |    |-- favoritesCount: integer (nullable = true)
 |    |-- followersCount: integer (nullable = true)
 |    |-- friendsCount: integer (nullable = true)
 |    |-- id: string (nullable = true)
 |    |-- image: string (nullable = true)
 |    |-- languages: array (nullable = true)
 |    |    |-- element: string (containsNull = false)
 |    |-- link: string (nullable = true)
 |    |-- links: array (nullable = true)
 |    |    |-- element: struct (containsNull = false)
 |    |    |    |-- href: string (nullable = true)
 |    |    |    |-- rel: string (nullable = true)
 |    |-- listedCount: integer (nullable = true)
 |    |-- location: struct (nullable = true)
 |    |    |-- displayName: string (nullable = true)
 |    |    |-- objectType: string (nullable = true)
 |    |-- objectType: string (nullable = true)
 |    |-- postedTime: string (nullable = true)
 |    |-- preferredUsername: string (nullable = true)
 |    |-- statusesCount: integer (nullable = true)
 |    |-- summary: string (nullable = true)
 |    |-- twitterTimeZone: string (nullable = true)
 |    |-- utcOffset: string (nullable = true)
 |    |-- verified: boolean (nullable = true)
 |-- body: string (nullable = true)
 |-- contributors: string (nullable = true)
 |-- coordinates: struct (nullable = true)
 |    |-- coordinates: array (nullable = true)
 |    |    |-- element: double (containsNull = false)
 |    |-- type: string (nullable = true)
 |-- created_at: string (nullable = true)
....

Q1 ------ Total count by languages Lang, count(*) ---
[ArrayBuffer(en),5777222]
[null,3727441]
[ArrayBuffer(ja),2704153]
[ArrayBuffer(es),1711197]
[ArrayBuffer(pt),659012]
[ArrayBuffer(ar),603793]
[ArrayBuffer(ru),264605]
[ArrayBuffer(id),234660]
[ArrayBuffer(ko),203726]
[ArrayBuffer(tr),115678]
[ArrayBuffer(fr),103877]
[ArrayBuffer(th),85506]
[ArrayBuffer(en-gb),77942]
[ArrayBuffer(de),33596]
[ArrayBuffer(it),27663]
[ArrayBuffer(nl),19889]
[ArrayBuffer(pl),16548]
[ArrayBuffer(zh-tw),7632]
[ArrayBuffer(sv),6311]
[ArrayBuffer(zh-cn),5290]
[ArrayBuffer(en-GB),3671]
[ArrayBuffer(el),3095]
[ArrayBuffer(hi),2726]
[ArrayBuffer(uk),2602]
[ArrayBuffer(msa),2362]
Q1 completed.
query time: 5.786566397 sec
Q2 ------ earliest and latest tweet dates ---
[2015-02-12T07:59:48.308+00:00]
[2015-02-12T00:19:18.258+00:00]
Q2 completed.
query time: 1.059922054 sec
Q3 ------ Which time zones are the most active per day? ---
[Eastern Time (US & Canada),2015-02-1,727688]
[Central Time (US & Canada),2015-02-1,587851]
[Brasilia,2015-02-1,527198]
[Tokyo,2015-02-1,510255]
[Pacific Time (US & Canada),2015-02-1,397727]
[Irkutsk,2015-02-1,342943]
[Atlantic Time (Canada),2015-02-1,244621]
[Seoul,2015-02-1,241879]
[Hawaii,2015-02-1,230342]
[Buenos Aires,2015-02-1,225498]
[Quito,2015-02-1,192758]
[Arizona,2015-02-1,192275]
[Santiago,2015-02-1,186726]
[Jakarta,2015-02-1,163999]
[Bangkok,2015-02-1,152740]
Q3 completed.
query time: 3.966009242 sec
Q4 ------ Who is most influential?  ---
[.,null,5639925,7120]
[あ,null,5497037,1798]
[。,null,4649930,1349]
[Please Zayn ,Brasilia,3665806,32]
[Age of #Rownevieve,Central Time (US & Canada),3366534,5]
[Emily Massey,null,3365595,3]
[Nevil Paperman,null,3364886,3]
[、,null,3360013,934]
[tai,Brasilia,3327049,90]
[使ってません,null,3194538,802]
Q4 completed.
query time: 10.938257191 sec
Q5 ------ Top devices used among all Twitter users ---
[Twitter for iPhone,3532734]
[Twitter for Android,2454865]
[Twitter Web Client,1361441]
[twittbot.net,341854]
[TweetDeck,301340]
[IFTTT,289989]
[Twitter for iPad,183512]
[twitterfeed,141179]
[dlvr.it,128650]
[Facebook,124822]
[Twitter for Android Tablets,114122]
[تطبيق تغريد دعاء,111659]
[Mobile Web (M2),105706]
[Twitter for Windows Phone,82332]
[Twitter for BlackBerry®,75975]
[Instagram,73833]
[TweetAdder v4,72258]
[تطبيق قرآني,72168]
[knzmuslim كنز المسلم,53971]
[Hootsuite,46453]
Q5 completed.
query time: 2.950492016 sec

 



 









2. 
