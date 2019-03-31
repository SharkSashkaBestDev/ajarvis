from flask import Flask, jsonify, request
from pymongo import MongoClient

# from commands import commands

app = Flask(__name__)
# app.config["MONGO_URI"] = "mongodb://localhost:27017/commands"
mongo = MongoClient('mongodb://localhost:27017/')
db = mongo.commands # db name


data = {}


@app.route('/execute', methods=['POST'])
def exec_command():
    json = request.get_json()
    command = db.commands.find_one({"_id": json['id']})
    # global data
    # data.update(json['kwargs'])
    data = json['kwargs']
    exec(command['code'])
    return jsonify(data)


@app.route('/chain', methods=['POST'])
def chain_exec_command():
    json = request.get_json()    
    # global data
    # data.update(json['kwargs'])
    data = json['kwargs']
    # data['commands'] = [commands[id] for id in json['ids']]
    # command = commands[json['id']]
    # command(data)
    data['commands'] = []
    for id in json['ids']:
        comm = db.commands.find_one({"_id": id})
        data.append(comm['code'])
    command = db.commands.find_one({"_id": json['id']})
    exec(command)
    return jsonify(data)


# @app.route("/exec", methods=['POST'])
# def execute():
#     global data
#     json = request.get_json()
#     if json['id'] in commands:
#         data.update(json['kwargs'])
#         command = commands[json['id']]
#         command(data)
#     return jsonify(data)
