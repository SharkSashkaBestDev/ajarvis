import json
import uuid

from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError

import mongo_config


commands = {}

commands['make_click'] = {
    'name': 'click',
    'phrase': "сделай клик",
    'paramType': {
        'xy': 'int[2',
        'russian': {
            'xy': "координаты"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['make_double_click'] = {
    'name': 'double_click',
    'phrase': "сделай двойной клик",
    'paramType': {
        'xy': 'int[2',
        'russian': {
            'xy': "координаты"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['open_browser'] = {
    'name': 'open_browser',
    'phrase': 'открой браузер'
}

commands['detect_image'] = {
    'name': 'detect_image',
    'phrase': 'найди изображение',
    'paramType': {
        'file': 'path',
        'russian': {
            'file': "путь к изображению"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['mouse_move'] = {
    'name': 'mouse_move',
    'phrase': 'перемести мышку',
    'paramType': {
        'xy': 'int[2',
        'russian': {
            'xy': "координаты"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['press_enter'] = {
    'name': 'press_enter',
    'phrase': 'нажми enter'
}

commands['write_phrase'] = {
    'name': 'write_phrase',
    'phrase': 'напиши',
    'paramType': {
        'text': 'String',
        'russian': {
            'text': "текст"
        }
    }
}

commands['detect_shapes'] = {
    'name': 'detect_shapes',
    'phrase': 'найди объекты',
    'paramType': {
        'color': 'String',
        'shape': 'enum[линия, прямоугольник, всё',
        'width_low': 'int',
        'width_up': 'int',
        'height_low': 'int',
        'height_up': 'int',
        'russian': {
            'color': 'цвет',
            'shape': 'форма',
            'width_low': 'минимальная ширина',
            'width_up': 'максимальная ширина',
            'height_low': 'минимальная высота',
            'height_up': 'максимальная высота',
        }
    },
    'returnType': {
        'shapes': 'int[][2',
        'img': 'path'
    }
}

commands['detect_text'] = {
    'name': 'detect_text',
    'phrase': 'найди текст',
    'paramType': {
        'text': 'String',
        'russian': {
            'text': "текст"
        }
    },
    'returnType': {
        'shapes': 'int[][2',
        'img': 'path'
    }
}

commands['show_shapes'] = {
    'name': 'show_shapes',
    'phrase': 'покажи найденное',
    'historyArgs': {
        'img': 'path'
    },
    'returnType': {
        'img_pid': 'int'
    }
}

commands['choose_shape'] = {
    'name': 'choose_shape',
    'phrase': 'выбираю объект',
    'paramType': {
        'shape_num': 'int',
        'russian': {
            'shape_num': "номер объекта"
        }
    },
    'historyArgs': {
        'shapes': 'int[][2',
        'img_pid': 'int'
    },
    'returnType': {
        'xy': 'int[2'
    }
}


if __name__ == '__main__':
    host = mongo_config.host
    port = mongo_config.port
    db_name = mongo_config.database
    collection_name = mongo_config.collection

    mongo = MongoClient(f'mongodb://{host}:{port}/')
    db = mongo[db_name]
    collection = db[collection_name]

    conn = True
    for key, command in commands.items():
        try:
            file_name = key + ".py"
            with open(file_name, 'r', encoding='utf-8') as file:
                command['code'] = file.read()
        except FileNotFoundError:
            pass
        command['_id'] = str(uuid.uuid4())
        try:
            if conn and not collection.count_documents({"name": command['name']}):
                collection.insert_one(command)
        except ServerSelectionTimeoutError:
            conn = False

    with open('commands.json', 'w', encoding='utf-8') as outfile:
        json.dump(commands, outfile, ensure_ascii=False)

    if not conn:
        print("Проверьте работу MongoDB. Соединение не было установлено.")
