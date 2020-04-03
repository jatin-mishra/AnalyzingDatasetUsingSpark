val url = ClassLoader.getSystemResource("path/To/Schema.json")
val schemaSource = Source.fromFile(url.getFile).getLines.mkString
val schemaFromJson = DataType.fromJson(schemaSource).asInstanceOf[StructType]
val df3 = spark.createDataFrame(spark.sparkContext.parallelize(strutureData),schemaFromJson)
df3.printSchema()	