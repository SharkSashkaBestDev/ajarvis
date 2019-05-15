# AJarvis Project
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
