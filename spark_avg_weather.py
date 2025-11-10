
# Description: PySpark program to compute daily average temperature and dew point temperature
#              Works on Windows without Hadoop setup.

from pyspark.sql import SparkSession
from pyspark.sql.functions import col, avg, substring, concat_ws, round

spark = SparkSession.builder \
    .appName("DailyAverageWeather") \
    .master("local[*]") \
    .getOrCreate()

spark.sparkContext.setSystemProperty("hadoop.home.dir", "C:/tmp")

# Make sure Asheville_weather.csv is in the same folder as this script
df = spark.read.csv("Asheville_weather.csv", header=True, inferSchema=True)

df = df.withColumn("date", substring(col("datetime"), 1, 8))

daily_avg = df.groupBy("date").agg(
    round(avg("temperature"), 2).alias("avg_temp"),
    round(avg("dewpoint"), 2).alias("avg_dewpoint")
)

result = daily_avg.select(
    col("date"),
    concat_ws(", ", col("avg_temp"), col("avg_dewpoint")).alias("combined_result")
)

result.write.mode("overwrite").option("header", "false").csv("daily_avg_output")

result.show(truncate=False)

print("\nDaily average weather data saved successfully to 'daily_avg_output' folder.")

spark.stop()
