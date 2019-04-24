from flask import Flask, jsonify, request
from pymongo import MongoClient


app = Flask(__name__)

host = '127.0.0.1'
port = 27017
db_name = 'ajarvis'
collection_name = 'command'

mongo = MongoClient(f'mongodb://{host}:{port}/')
db = mongo[db_name]
collection = db[collection_name]

data = {}


def temp(data):
    exec(data['code'])
    data.pop('code', None)

@app.route('/execute', methods=['POST'])
def exec_command():
    global data
    data.pop('error', None)
    json = request.get_json()
    data.update(json['kwargs'])
    for id in json['ids']:
        command = collection.find_one({"_id": id})
        exec(command['code'], {'data': data, 'temp': temp})
    return jsonify(data)
