from flask import Flask, jsonify, request
from pymongo import MongoClient

from commands import commands

app = Flask(__name__)
# app.config["MONGO_URI"] = "mongodb://localhost:27017/commands"
host = ''
port = 27017
mongo = MongoClient(f'mongodb://{host}:{port}/')
db = mongo.ajarvis


data = {}


@app.route('/execute', methods=['POST'])
def exec_command():
    global data
    data.pop('error', None)
    json = request.get_json()
    data.update(json['kwargs'])
    for id in json['ids']:
        command = db['command'].find_one({"_id": id})
        exec(command['code'])
        # command = commands[id]
        # command(data)
    return jsonify(data)
