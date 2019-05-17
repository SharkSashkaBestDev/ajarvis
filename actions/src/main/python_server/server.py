from flask import Flask, jsonify, request
from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError

import mongo_config


app = Flask(__name__)

host = mongo_config.host
port = mongo_config.port
db_name = mongo_config.database
collection_name = mongo_config.collection

mongo = MongoClient(f'mongodb://{host}:{port}/')
db = mongo[db_name]
collection = db[collection_name]

EXCEPTION = "Ошибка в {}, сообщение: {}"
data = {}


def temp(data):
    exec(data['code'])

@app.route('/execute', methods=['POST'])
def exec_command():
    global data
    status_code = 200
    data.pop('error', None)
    try:
        json = request.get_json()
        data.update(json['kwargs'])
        for id_ in json['ids']:
            id = str(id_)
            command = collection.find_one({"_id": id})
            if command:
                exec(command['code'], {'data': data, 'temp': temp})
            else:
                data['error'] = EXCEPTION.format(
                    "Python-сервер", 
                    "не знаю команды с id = " + id)
                status_code = 404
                break
            if 'error' in data:
                data['error'] = EXCEPTION.format(id, data['error'])
                del data['error']
                status_code = 400
                break
    except ServerSelectionTimeoutError:
        data['error'] = EXCEPTION.format(
            "Python-сервер", 
            "проверьте работу MongoDB, соединение не было установлено.")
        status_code = 500     
    except KeyError as e:
        data['error'] = EXCEPTION.format("Python-сервер", f"требуется аргумент '{e.args[0]}'")
        status_code = 400
    except Exception as ex:
        data['error'] = EXCEPTION.format("Python-сервер", str(ex))
        status_code = 500
    return jsonify(data), status_code


@app.route('/clean', methods=['GET'])
def clean():
    data.clear()
    return jsonify(data), 200

