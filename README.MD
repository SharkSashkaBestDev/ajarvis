# AJarvis Project

Запуск сервера из cmd:
1. `cd python`
2. 
    - Windows:
        - `SET FLASK_APP=server.py`
        - `SET FLASK_ENV=development`
    - Mac OS, Linux:
        - `export FLASK_APP=server.py`
        - `export FLASK_ENV=development`
3. `flask run`

Для считывания команд из файлов, записи их в БД и генерации json-файла с ними выполнить в cmd файл commands_to_json.py: <br>
`python commands_to_json.py`

Для работы некоторых команд требуется установка дополнительных библиотек:
- [Tesseract](https://github.com/tesseract-ocr/tesseract/wiki)
- [OpenCV](https://docs.opencv.org/3.4/df/d65/tutorial_table_of_content_introduction.html)
