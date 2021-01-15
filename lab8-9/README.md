# Access preparation
1. Створюємо новий сторедж аккаунт. В advanced конфігурації ставимо Enabled для Hierarchical namespace
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/storage.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/storage_adv.png)
2. Створюємо новий контейнер GEN2 з Container access level
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/container.png)
3. Створюємо нову директорію всередині контейнера
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/directory.png)
4. Переходимо в Azure Active Directory -> App registrations та створюємо нову registration
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/registration.png)
5. Переходимо в створену registration в Certificates & secrets та створюємо новий client secret
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/client_secret.png)

# Azure Databricks
1. Створюємо та запускаємо Azure Databricks Service
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/databricks.png)
2. Переходимо в clusters і створюємо кластер. В Claster mode обираємо Single node, в Databricks Runtime Version - 6.4
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/cluster.png)
3. В кластері обираємо Libraries –> Install New –> Maven. Coordinates: ```com.microsoft.azure:azure-eventhubs-spark_2.12:2.3.18```
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/maven.png)
4. На сторінці Home вибираємо Create Notebook, створюємо Notebook на Python
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/python_net.png)
5. В Python Notebook вставляємо код. Відповідні поля копіюємо з Azure Active Directory -> App registration -> Створений попередньо registartion.
Копіюємо Application (client) ID та встаялємо в fs.azure.account.oauth2.client.id
З Certificates & secrets копіюємо ID раніше створеного Client secret та вставляємо в fs.azure.account.oauth2.client.secret
В "fs.azure.account.oauth2.client.endpoint" після https://login.microsoftonline.com/ вставляємо Directory (tenant) ID з вкладки Overview.
В source вставляємо відповідно ім'я Storage account і Container 

```
# Databricks notebook source
configs = {"fs.azure.account.auth.type": "OAuth",
         "fs.azure.account.oauth.provider.type": "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider",
         "fs.azure.account.oauth2.client.id": "539d7db3-e2e8-4194-b6af-1c5999fd2f52",
         "fs.azure.account.oauth2.client.secret": "2~q-~S_B-.1ifafXmb72i2tBk_04Z2L5W8",
         "fs.azure.account.oauth2.client.endpoint": "https://login.microsoftonline.com/435e6fcb-7831-4054-8094-8d7806d0af00/oauth2/token",
         "fs.azure.createRemoteFileSystemDuringInitialization": "true"}

dbutils.fs.mount(
        source = "abfss://bilous-container@bilousacccount.dfs.core.windows.net",
        mount_point = "/mnt/bilous-dir",
        extra_configs = configs)

display(dbutils.fs.ls('/mnt/bilous-dir'))
```  

6. На сторінці Home вибираємо Create Notebook, створюємо Notebook на Scala
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/scala_net.png)
7. В Scala Notebook вставляємо код. Знову вставляємо відповідні дані + дані для з'єднання з EventHub. Також витягуємо (і за потреби конвертуємо) відповідні поля з датасету

```
import org.apache.spark.eventhubs.{ ConnectionStringBuilder, EventHubsConf, EventPosition }
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

// To connect to an Event Hub, EntityPath is required as part of the connection string.
// Here, we assume that the connection string from the Azure portal does not have the EntityPath part.
val appID = "539d7db3-e2e8-4194-b6af-1c5999fd2f52"
val password = "2~q-~S_B-.1ifafXmb72i2tBk_04Z2L5W8"
val tenantID = "435e6fcb-7831-4054-8094-8d7806d0af00"
val fileSystemName = "bilous-container";
var storageAccountName = "bilousacccount";
val connectionString = ConnectionStringBuilder("Endpoint=sb://bilouseventhubnamespace.servicebus.windows.net/;SharedAccessKeyName=BilousPolicy;SharedAccessKey=vnpXyLyWcNHEzNjaCjE6xzs+FEoiA2DDoZQgWVUXmX0=;EntityPath=bilouseventhub")
  .setEventHubName("bilouseventhub")
  .build
val eventHubsConf = EventHubsConf(connectionString)
  .setStartingPosition(EventPosition.fromEndOfStream)

var dataset = 
  spark.readStream
    .format("eventhubs")
    .options(eventHubsConf.toMap)
    .load()
      
val filtered = dataset.select(
    from_unixtime(col("enqueuedTime").cast(LongType)).alias("enqueuedTime")
      , get_json_object(col("body").cast(StringType), "$.map.case_count").alias("case_count").cast(DoubleType)
      , get_json_object(col("body").cast(StringType), "$.map.death_count").alias("death_count").cast(DoubleType)
        , get_json_object(col("body").cast(StringType), "$.map.case_count_7day_avg").alias("case_count_7day_avg").cast(DoubleType)
        , get_json_object(col("body").cast(StringType), "$.map.death_count_7day_avg").alias("death_count_7day_avg").cast(DoubleType)
        , get_json_object(col("body").cast(StringType), "$.map.hospitalized_count").alias("hospitalized_count")
        , get_json_object(col("body").cast(StringType), "$.map.incomplete").alias("incomplete").cast(DoubleType)
  )
  
filtered.writeStream
  .format("com.databricks.spark.csv")
  .outputMode("append")
  .option("checkpointLocation", "/mnt/bilous-dir/bilous-dir")
  .start("/mnt/bilous-dir/bilous-dir")
  ```

# Result
1. Запускаємо Python notebook. Відкриваємо завантажений Microsoft Azure Storage Explorer -> subscription -> storage -> container -> directory. В директорії мають з'явитись метадані
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/python_start.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/metadata.png)
2. Запускаємо Scala notebook і паралельно посилаємо запит на запис даних в Eventhub. Через кілька секунд дані повинні з'явитись в директорії
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/scala_start.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/data.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab8-9/excel.png)

