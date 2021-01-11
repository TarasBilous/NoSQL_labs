# Prerequisites
* Акаунт на azure
* IDE with Java/Maven
* Postman  

# Azure Part
1. Створюємо resource group.
2. Створюємо Redis Cache Instance і чекаємо поки він повністю не створиться.
3. Паралельно створюємо Event Hub Namespace.
4. Створюємо Event Hub Instance в попередньо створеному Event Hub Namespace.
5. Створюємо Shared access policy(Send) в нашому Event Hub Instance.

# Project Configuration
1. В файлі ```SendDataEventHubImpl.java``` вставляємо наші назви Event Hub Namespace, Event Hub Instance та Primary key & Connection string-primary key з Shared access policy.
2. В файлі ```SendDataConsoleImpl.java``` вставляємо в CACHE_KEY свій Primary з Redis Cache Instance Access keys, а в CACHE_HOSTNAME частину Secondary connection string.
3. Компілюємо і запускаємо проєкт.

# Result
1. В Postman посилаємо POST запит на ```localhost:9000/url``` з вибором стратегії (redis/eventHub) та лінкою на дані.
2. Перевіряємо чи дані записались в Event Hub (Event Hub -> Features –> Proccess Data –> Explore).
3. Перевіряємо чи дані записались в Redis (Redis Chache -> Console > команда hgetAll ім'я каталогу, який вказаний в MAP_NAME у файлі SendDataConsoleImpl.java).
