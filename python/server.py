from flask import Flask, jsonify, request
from pymongo import MongoClient

import mongo_config

app = Flask(__name__)

host = mongo_config.host
port = mongo_config.port
db_name = mongo_config.database
collection_name = mongo_config.collection

mongo = MongoClient(f'mongodb://{host}:{port}/')
db = mongo[db_name]
collection = db[collection_name]

data = {}


def temp(data):
    exec(data['code'])

@app.route('/execute', methods=['POST'])
def exec_command():
    global data
    data.pop('error', None)
    json = request.get_json()
    data.update(json['kwargs'])
    status_code = 200
    try:
        for id in json['ids']:
            command = collection.find_one({"_id": id})
            if command:
                exec(command['code'], {'data': data, 'temp': temp})
            else:
                data['error'] = "Не знаю команды с таким id " + id
                status_code = 404
                break
    except Exception as ex:
        data['error'] = str(ex)
        status_code = 500
    return jsonify(data), status_code
