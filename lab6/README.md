# Prerequisites
Акаунт на azure  
Акаунт на google cloud platform

# GCP Part

1. Створюємо Compute Engine instance з базовими налаштуваннями.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/compute-engine-part1.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/compute-engine-part2.png)

2. Створюємо правила фаєрволу, щоб мати змогу доступатись до Compute Engine інстансу по зовнішній IP-адресі, а також для коректного читання Kiban'ою каталогів ElasticSearch'а. Налаштовуємо рейндж IP-адрес для TCP-портів 9200(ElasticSearch) та 5601(Kibana).
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/elastic-rule.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/kibana-rule.png)

3. Під'єднуємось за допомогою ssh-з'єднання до машини і встановлюємо ElasticSearch і Kibana.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/ssh-connection.png)

&nbsp;&nbsp;&nbsp;&nbsp;Встановлення Java:
```
$ sudo apt-get install default-jre
```
&nbsp;&nbsp;&nbsp;&nbsp;Встановлення ElasticSearch
```
$ wget -qO - https://packages.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
$ sudo apt-get install elasticsearch
$ sudo vi /etc/elasticsearch/elasticsearch.yml 
# розкоментовуємо рядок network.host і встановлюємо для нього значення “0.0.0.0” та добавляємо discovery.type: single-node
$ sudo service elasticsearch restart
```
&nbsp;&nbsp;&nbsp;&nbsp;Встановлення Logstash
```
$ sudo apt-get install apt-transport-https
$ echo "deb https://artifacts.elastic.co/packages/5.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-5.x.list
$ sudo apt-get update
$ sudo apt-get install logstash
$ sudo service logstash start
```
&nbsp;&nbsp;&nbsp;&nbsp;Встановлення Kibana
```
$ echo "deb http://packages.elastic.co/kibana/5.3/debian stable main" | sudo tee -a /etc/apt/sources.list.d/kibana-5.3.x.list
$ sudo apt-get update
$ sudo apt-get install kibana
$ sudo vi /etc/kibana/kibana.yml
# розкоментовуємо та вказуємо server.port: 5601 та server.host: “0.0.0.0”
$ sudo service kibana start
```

4. Переходимо по http://\<compute-engine-ip\>:5601 та http://\<compute-engine-ip\>:9200, щоб перевірити чи сервіси в робочому стані.  
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/elastic-test.png)
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/kibana-test.png)

# Azure Part
1. Створюємо Logic App. Resource group використовуємо з 5 лабораторної роботи.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/logic-app.png)

2. Створюємо тригер для Event Hub.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/logic-app-trigger1.png)

3. Добавляємо крок для надсилання HTTP-запиту до ElasticSearch після отримання даних.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/logic-app-trigger2.png)


# Result
Надсилаємо дані в Event Hub і після виконання Logic App перевіряємо їх наявність в Kibana.
![alt text](https://github.com/TarasBilous/NoSQL_labs/blob/master/images/lab6/kibana-data.png)
