import json
import uuid

from pymongo import MongoClient


commands = {}

commands['make_click'] = {
    'name': 'click',
    'phrase': ['кликни', 'сделай клик'],
    'paramType': {
        'xy': '(int, int)',
        'double_click': 'boolean'
    },
    'returnType': {
        'xy': '(int, int)'
    }
}

commands['detect_image'] = {
    'name': 'detect_image',
    'phrase': ['найди картинку', 'найди изображение'],
    'paramType': {
        'file': 'path',
    },
    'returnType': {
        'xy': '(int, int)'
    }
}

commands['mouse_move'] = {
    'name': 'mouse_move',
    'phrase': ['перемести мышку', 'передвинь мышку'],
    'paramType': {
        'xy': '(int, int)',
    },
    'returnType': {
        'xy': '(int, int)'
    },
    'code': """
import pyautogui

def mouse_move(data):
    print("Mouse move")

    pyautogui.moveTo(*data['xy'])
    return data

mouse_move(data)"""
}

commands['press_enter'] = {
    'name': 'press_enter',
    'phrase': ['нажми enter'],
    'paramType': { },
    'returnType': { },
    'code': """
import pyautogui

def press_enter(data):
    print("Press Enter")

    pyautogui.press('enter')
    return data

press_enter(data)"""
}

commands['write_phrase'] = {
    'name': 'write_phrase',
    'phrase': ['напиши'],
    'paramType': {
        'text': 'str'
     },
    'returnType': { },
    'code': """
import pyautogui

def write_phrase(data):
    print("Write")

    for letter in data['text']:
        pyautogui.press(letter)
    del data['text']
    return data

write_phrase(data)"""
}

commands['detect_shapes'] = {
    'name': 'detect_shapes',
    'phrase': ['найди объекты', 'найди прямоугольники', 'найди линии'],
    'paramType': {
        'color': 'str',
        'shape': 'Enum(line, rectangle, all)',
        'width_low': 'int',
        'width_up': 'int',
        'height_low': 'int',
        'height_up': 'int',
     },
    'returnType': {
        'shapes': 'Array((int, int))',
        'img': 'path'
     }
}

commands['detect_text'] = {
    'name': 'detect_text',
    'phrase': ['найди фразу', 'найди текст'],
    'paramType': {
        'phrase': 'str'
     },
    'returnType': {
        'shapes': 'Array((int, int))',
        'img': 'path'
     }
}

commands['show_shapes'] = {
    'name': 'show_shapes',
    'phrase': ['покажи найденное'],
    'paramType': {
        'img': 'path',
     },
    'returnType': { }
}

commands['choose_shape'] = {
    'name': 'choose_shape',
    'phrase': ['выбираю объект', 'выбираю форму', 'выбираю'],
    'paramType': {
        'shape_num': 'int',
        'shapes': 'Array((int, int))',
        'img_pid': 'int'
    },
    'returnType': {
        'xy': '(int, int)',
        'shapes': 'Array((int, int))'
    }
}


if __name__ == '__main__':
    host = '127.0.0.1'
    port = 27017
    db_name = 'ajarvis'
    collection_name = 'command'

    mongo = MongoClient(f'mongodb://{host}:{port}/')
    db = mongo[db_name]
    collection = db[collection_name]
    for key, command in commands.items():
        try:
            file_name = key + ".py"
            with open(file_name, 'r', encoding='utf-8') as file:
                command['code'] = file.read()
        except FileNotFoundError:
            pass
        command['_id'] = str(uuid.uuid4())
        collection.insert_one(command)

    with open('commands.json', 'w', encoding='utf-8') as outfile:
        json.dump(commands, outfile, ensure_ascii=False)
