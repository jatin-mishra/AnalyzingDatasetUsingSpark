import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ streamingContext, Seconds}

object streamingWorldCount{
    def main(args:Array[String]){
        val conf = new SparkConf().setAppName("streamingWorldCount").setMaster(args(0))

        val ssc = new StreamingContext(conf,Seconds(30))

        val messages = ssc.socketTextStream(args(1),args(2).toInt)

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