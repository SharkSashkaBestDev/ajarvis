# AJarvis Project

Запуск сервера из cmd:
1. cd python
2. 
    - Windows:
        - SET FLASK_APP=server.py
        - SET FLASK_ENV=development
    - Mac OS, Linux:
        - export FLASK_APP=server.py
        - export FLASK_ENV=development
3. flask run

Для считывания команд из файлов, записи их в БД и генерации json-файла с ними выполнить в cmd файл commands_to_json.py: <br>
python commands_to_json.py