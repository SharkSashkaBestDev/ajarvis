# AJarvis Project
## Java part

Для работы  требуется установка :
- [MongoDb](https://www.mongodb.com/)

Для конфигурации нужно заполнить в  файле [application.properties](https://github.com/SharkSashkaBestDev/ajarvis/blob/feature/Aj-33/actions/src/main/resources/application.properties) такие поля : <br>
- `server.port`порт основного сервера (java) по умолчанию стоит 8091
- `spring.data.mongodb.host`  адресс приложения по умолчанию стоит 127.0.0.1
- `spring.data.mongodb.database` название базы для приложения

Для конфигурации доступных языков и серверов нужно заполнить   файл [servers.properties](https://github.com/SharkSashkaBestDev/ajarvis/blob/feature/Aj-33/actions/src/main/resources/servers.properties) таким образом : <br>
`PYTHON=http://127.0.0.1:5000/execute` ,где `PYTHON` переменная из [Language.java](https://github.com/SharkSashkaBestDev/ajarvis/blob/feature/Aj-33/actions/src/main/java/com/asoft/ajarvis/actions/enities/Language.java) <br>
а `http://127.0.0.1:5000/execute` адрес сервера куда нужно отправлять запросы

Для заполнения базы данных командами нужно выполнить запросы из файла [commands.http](https://github.com/SharkSashkaBestDev/ajarvis/blob/feature/Aj-33/actions/src/main/java/com/asoft/ajarvis/actions/enities/commands.http)  <br>
пердварительно изменив поля `"phrase"` написав после равно то что нужно будет сказать для выполнения этой команды <br>


Запуск сервера осуществляется из класа [AjarvisApplication](https://github.com/SharkSashkaBestDev/ajarvis/blob/feature/Aj-33/actions/src/main/java/com/asoft/ajarvis/actions/AjarvisApplication.java)

Выполнение команд происходит через запросы на адреса 
 * для фильтрации строки и выделение фразы конкретной команды для выполнения нужно отправить 
    `POST` запрос на адресс `localhost:8091/ajarvis/commands/filter` с телом запроза подобным на  <br> 
 `{
   	"phrase":"sds as тип тест  go вы  Work аргументы путь 10  11.5 "
   }`
  
    
    - фраза для фильтрации :
        - "phrase"
    
* для получения всех команд  нужно отправить 
    `GET` запрос на адресс `localhost:8091/ajarvis/commands` 
    
* для  выполнения команды  нужно отправить 
    `POST` запрос на адресс `localhost:8091/ajarvis/commands/execute` с телом запроза подобным на (обьяснения после тела ) <br> 
 `{
      "begin": 0,
      "phrase": "exec histry",
      "end": 0
  }`
  
    
    - фраза команды:
        - "phrase"
    - аргументы:
        - "begin"
        - "end"
        - ...
       
* также есть запросы для разработчиков :<br>
    - что бы понять как будет выглядеть код определенной команды 
        нужно отправить `GET` запрос на адресс `localhost:8091/ajarvis/commands/giveCode/{id}` 
        на месте `{id}` нужно вставить идентификатор конкретной команды
        


