# TwitterStreamProject
Small project to experiment big data tools. The twitter stream api is connected to Kafka, all the data is stored in Redis and processed by Spark.


# How to run

The project is build in scala language so you need to have scala version 2.11.x on your machine

When using the Twitter streaming API to access tweets via HTTP requests, you must supply your Twitter username and password and to use the twitter4j api, you also must provide authentication details:

1. Go to https://dev.twitter.com/apps and click on the button that says “Create a new application” 
2. Fill in the name, description and website fields.
3. Click on the button at the bottom that says “Create my access token”.
4. Click on the “OAuth tool” tab and you’ll see four fields for authentication which you need in order to use twitter4j to access tweets and other information from Twitter: Consumer key, Consumer secret, Access token, and Access token secret.

After you get your authentication details, go to the main file and change, respectively, the fields:

```scala

	/** TwitterStream configuration **/
    val config = new ConfigurationBuilder()
    .setOAuthConsumerKey("[your consumer key here]")
    .setOAuthConsumerSecret("[your consumer secret here]")
    .setOAuthAccessToken("[your access token here]")
    .setOAuthAccessTokenSecret("[your access token secret here]")
    .setJSONStoreEnabled(true)
    .build

```

5. Install kafka with the version 0.8.2.1

6. Install spark with the version 1.4.1, http://spark.apache.org/downloads.html

7. Go to the kafka folder and run the zookeeper server with these command: bin/zookeeper-server-start.sh config/zookeeper.properties

8. Same thing, but to run the kafka server: bin/kafka-server-start.sh config/server.properties

9. Go to the Twitter Stream Project and run the command sbt assembly, to create a fat JAR of your project with all of its dependencies

10. The, go to the browser http://localhost:8080, and copy the Spark Master at address of your spark cluster spark://????

11. Go to the spark folder and create a worker: ./sbin/start-slave.sh spark://????

12. In the same folder, run the ./bin/spark-submit --class "Main.Main" --master spark://??? [.jar path]