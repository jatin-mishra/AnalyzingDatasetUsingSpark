import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ StreamingContext ,  Seconds}
import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder

object KafkaandSparkStreaming{

    def main(args:Array[String]){
        val conf = new SparkConf().setAppName("appname").setMaster(args(0))
        val ssc = new StreamingContext(conf,Seconds(30))

        /*
            you can pass all config type things in this kafkaConfig map.
        */
        val topicSet = Set("topicOne")
        val kafkaConfig = Map("metadata.broker.list" -> "hostname1:port1,hostname2:port2")
        val stream = new KafkaUtils.createDirectStream[String,String,StringDecoder, StringDecoder](ssc, kafkaConfig, topicSet)
        val messages = stream.map( s => s._2 )



        val departmentMessages = messages.filter(
            msg => {
            val endPoint = msg.split(" ")(6)
            endPoint.split("/")(1) == "department"
            }
        )

        val departments = departmentMessages.map(
            rec => {
                val endPoint = rec.Split(" ")(6)
                (endPoint.Split("/")(2), 1)
            }
        )

        val departmentTraffic = departments.reduceByKey(_ + _)

        departmentTraffic.saveAsTextFiles(args(3))

        ssc.start()
        ssc.awaitTermination()        

    }
}


/*
    Always choose compatible vesions of jars like : here kafka version and spark-streaming-kafka version

*/