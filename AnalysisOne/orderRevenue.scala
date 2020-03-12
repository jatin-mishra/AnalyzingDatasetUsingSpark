// command for spark2-shell
// spark2-shell --master yarn --num-executors 1 --executor-memory 512M

// read data and creation of RDDs
val order_items = sc.textFile("retail_db/order_items")
val order = sc.textFile("retail_db/orders")
order.take(100).foreach(println)
order_items.take(100).foreach(println)

// analyse available status
order.map(line => line.split(",")(3)).distinct.collect.foreach(println)

// filter for given status
val clsOrCmpOrders = order.filter(line => (line.split(",")(3) == "CLOSED") || (line.split(",")(3) == "COMPLETE"))

// CONVERT ORDER INTO KEY-VALUE PAIR
val keyValOrder = order.map(line => (line.split(",")(0).toInt,line.split(",")(1).substring(0,10))) 

// checking the variacity
keyValOrder.take(100).foreach(println)

// CONVERT ORDER ITEM INTO KEY-VALUE PAIR
val keyValOrderItem = order_items.map(line => ( ( line.split(",")(1).toInt ) , (( line.split(",")(2).toInt ) , (line.split(",")(4).toDouble ))))

// analyze ORDERITEM
keyValOrderItem.take(100).foreach(println)

// join on key = orderId
val KeyValJoined = keyValOrder.join(keyValOrderItem)

// analyzing RDD
KeyValJoined.take(100).foreach(println)


// now we have : (orderId , ( orderDate , ( productId , subtotal ) ) )
// we want	is  : ((orderDate , productId) , subTotal )
// get daily revenue per product Id
val daily = KeyValJoined.map( k => ((k._2._1 , k._2._2._1) , k._2._2._2)) 
val dailyRevenuePerProduct = daily.reduceByKey(_ + _ )

// analyze revenue  
dailyRevenuePerProduct.take(100).foreach(println)
dailyRevenuePerProduct.count


// LOAD PRODUCTS FROM LOCAL FILE SYSTEM
import scala.io.Source
val product = Source.fromFile("filePath").getLines.toList
val products = sc.parallelize(product)
val productsMap = products.map( line => (line.split(",")(0).toInt , line.split(",")(2)))

// analysis 
productsMap.take(10).foreach(println)

// change daily revenue schema
dailyRevenuePerProduct.map( line => (line._1._2 , (line._1._1 , line._2)))
dailyRevenuePerProductMap.take(10).foreach(println)

// join with productId
val dailyRevenuePerProductJoin = dailyRevenuePerProductMap.join(productsMap)
dailyRevenuePerProductJoin.take(10).foreach(println)

// SORT DATA ASC_DATE AND DESC_REVENUE
val dailyRevenuePerProductSorted = dailyRevenuePerProductJoin.map( line => ((line._2._1._1, -line._2._1._2) , (line._2._1._1 , line._2._1._2, line._2._2))).sortByKey()

// finalProduct
val output = dailyRevenuePerProductSorted.map(line => line._2)

// test data
output.take(100).foreach(println)

// save as textfile
output.saveAsTextFile("pathToTheOutputFile")








