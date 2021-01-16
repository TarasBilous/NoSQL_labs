# Prerequisites
* Акаунт на azure
* IDE with Java/Maven
* Postman  

# Azure Part
1. Створюємо resource group.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/resource-group.png)
2. Створюємо Redis Cache Instance і чекаємо поки він повністю не створиться.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/redis-cache-instance.png)
3. Паралельно створюємо Event Hub Namespace.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/event-hub-namespace.png)
4. Створюємо Event Hub Instance в попередньо створеному Event Hub Namespace.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/event-hub-instance.png)
5. Створюємо Shared access policy(Send) в нашому Event Hub Instance.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/shared-access-policy.png)

# Project Configuration
1. В файлі ```application.properties``` вставляємо наші назви Event Hub Namespace, Event Hub Instance та Primary key & Connection string-primary key з Shared access policy.
2. В файлі ```application.properties``` вставляємо в redis.cache.key свій Primary з Redis Cache Instance Access keys, а в redis.cache.hostname частину Secondary connection string.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/project-redis-configuration.png)
3. Компілюємо і запускаємо проєкт.

# Result
1. В Postman посилаємо POST запит на ```localhost:9000/lab``` з вибором стратегії (redis/eventHub) та лінкою на дані.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/postman.png)
2. Перевіряємо чи дані записались в Event Hub (Event Hub -> Features –> Proccess Data –> Explore).
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/event-hub-data.png)
3. Перевіряємо чи дані записались в Redis (Redis Chache -> Console > команда hgetAll ім'я каталогу, який вказаний в redis.map.name у файлі ```application.properties```).
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab5/redis-data.png)
