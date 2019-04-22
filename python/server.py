from importlib import import_module

from flask import Flask, jsonify, request
from pymongo import MongoClient

from commands_to_json import commands # for testing


app = Flask(__name__)
# app.config["MONGO_URI"] = "mongodb://localhost:27017/commands"
host = '127.0.0.1'
port = 27017
mongo = MongoClient(f'mongodb://{host}:{port}/')
db = mongo.ajarvis


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
        command = commands[id] # for testing
        # command = db['command'].find_one({"_id": id}) # for production with database
        exec(command['code'], {'data': data, 'import_module': import_module, 'temp': temp})
    return jsonify(data)
