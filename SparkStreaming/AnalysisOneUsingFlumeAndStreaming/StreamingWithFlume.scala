
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{ StreamingContext , Seconds }
import org.apache.spark.streaming.flume._

object StreamingWithFlume{

    def main(args:Array[String]){

        val conf = new SparkConf().setAppName("streaming With flume").setMaster(args(0))
        val ssc = new StreamingContext(conf, Seconds(30))

        val stream = FlumeUtils.createPollingStream(ssc,args(1),args(2).toInt)
        val messages = stream.map(
            msg => {
                new String(msg.event.getBody.array())
            }
        )


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

spark-submit                    \
    --class FlumeStreamingDepartmentCount   \
    --master yarn                           \
    --conf spark.ui.port=12896
    --jar "addstreamingFlumeSinkJar, addsparkstreamingflumejar, commonslang, addflume-ng-sdk"      \
    jarfile.jar yarn-client hostname port

*/


