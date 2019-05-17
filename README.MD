# AJarvis Project
Для запуска проекта необходимо:

1. Запустить AjarvisApplication.java
2. В cmd прописать: cd actions/src/main/python_server export FLASK_APP=server.py export FLASK_ENV=development flask run
3. В новой cmd прописать: python3 recognition/src/main/python/main.py
4. Команда для теста: "Открой браузер"

## Java part

Для работы  требуется установка :
- [MongoDb](https://www.mongodb.com/)

Для конфигурации нужно заполнить в  файле [application.properties](./actions/src/main/java/com/asoft/ajarvis/actions/AjarvisApplication.java) такие поля : <br>
- `server.port`порт основного сервера (java) по умолчанию стоит 8091
- `spring.data.mongodb.host`  адресс приложения по умолчанию стоит 127.0.0.1
- `spring.data.mongodb.database` название базы для приложения

Для конфигурации доступных языков и серверов нужно заполнить   файл [servers.properties](./actions/src/main/resources/servers.properties) таким образом : <br>
`PYTHON=http://127.0.0.1:5000/execute` ,где `PYTHON` переменная из [Language.java](./actions/src/main/java/com/asoft/ajarvis/actions/enities/Language.java) <br>
а `http://127.0.0.1:5000/execute` адрес сервера куда нужно отправлять запросы

Для заполнения базы данных командами нужно выполнить запросы из файла [commands.http](./actions/src/main/java/com/asoft/ajarvis/actions/enities/commands.http)  <br>
пердварительно изменив поля `"phrase"` написав после равно то что нужно будет сказать для выполнения этой команды <br>


Запуск сервера осуществляется из класа [AjarvisApplication](./actions/src/main/java/com/asoft/ajarvis/actions/AjarvisApplication.java)

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
        

## Python part

Использовался **Python** версии >= **3.7.3**

[Сервер](./actions/src/main/python_server/) <br>
[Команды](./j-1/src/main/python_commands/)


Файлы конфигурации MongoDB базы: 
- Для сервера - [mongo_config.py](./actions/src/main/python_server/mongo_config.py)
- Для команд - [mongo_config.py](./j-1/src/main/python_commands/mongo_config.py)

Для работы некоторых команд требуется установка дополнительных библиотек:
- [Tesseract](https://github.com/tesseract-ocr/tesseract/wiki)
- [OpenCV](https://docs.opencv.org/3.4/df/d65/tutorial_table_of_content_introduction.html)

Для скачивания требуемых библиотек Python'а перейти в папку сервера или команд с файлом requirements.txt и выполнить: <br>
`pip install -r requirements.txt`

Для считывания команд из файлов, записи их в БД и генерации json-файла перейти в папку [python_commands](./j-1/src/main/python_commands/) и выполнить: <br>
`python commands_to_json.py`

Запуск сервера из cmd:
1. Перейти в папку [python_server](./actions/src/main/python_server/)
2. 
    - Windows:
        - `SET FLASK_APP=server.py`
        - `SET FLASK_ENV=development`
    - Mac OS, Linux:
        - `export FLASK_APP=server.py`
        - `export FLASK_ENV=development`
3. `flask run`

Адреса:
1. `POST /execute`<br>
В теле запроса:
    - **ids** - масссив, состоящий из id'шников каждой команды для последовательного выполнения;
    - **kwargs** - объект, где ключи - это названия нужных аргементов, значения - значения аргументов. **Требуемые аргументы** для каждой команды смотреть в базе данных или в файле [commands.json](./j-1/src/main/python_commands/commands.json).<br>
    Запрос вернет объект, где будут содержаться результаты выполнения всех команд. **Возвращаемые значения** для каждой команды смотреть в базе данных или в файле [commands.json](./j-1/src/main/python_commands/commands.json). Статус код ответа - 200.<br><br>
Если возникнут **исключения**, то в возвращаемом объекте будет содержаться ключ **`error`**, значение - строка с ошибкой
2. `GET /clean`<br>
Очистит внутренний словарь сервера с некоторыми запомненными аргументами и результатами выполнения.

