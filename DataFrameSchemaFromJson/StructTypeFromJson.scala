val url = ClassLoader.getSystemResource("path/To/Schema.json")
val schemaSource = Source.fromFile(url.getFile).getLines.mkString
val schemaFromJson = DataType.fromJson(schemaSource).asInstanceOf[StructType]
val df3 = spark.createDataFrame(spark.sparkContext.parallelize(strutureData),schemaFromJson)
df3.printSchema()	





df4.printSchema()

// root
//  |-- firstname: string (nullable = true)
//  |-- middlename: string (nullable = true)
//  |-- lastname: string (nullable = true)
//  |-- id: string (nullable = true)
//  |-- gender: string (nullable = true)
//  |-- salary: integer (nullable = true)


//       changing and adding struct of the schema of df4
val updateDF = df4.withColumn("OtherInfo",
	struct(
		col("id").as("identifier"),
		col("gender").as("gender"),
		col("salary").as("salary"),
		when(col("salary").cast(IntegerType) < 2000 , "Low")
			.when(col("salary").cast(IntegerType) < 4000 , "Medium")
			.otherwise("High").alias("salary_grade")

	)).drop("id","gender","salary")


updateDF.printSchema()
updateDF.show(false)

// root
//  |-- name: struct (nullable = true)
//  |    |-- firstname: string (nullable = true)
//  |    |-- middlename: string (nullable = true)
//  |    |-- lastname: string (nullable = true)
//  |-- OtherInfo: struct (nullable = false)
//  |    |-- identifier: string (nullable = true)
//  |    |-- gender: string (nullable = true)
//  |    |-- salary: integer (nullable = true)
//  |    |-- Salary_Grade: string (nullable = false)
