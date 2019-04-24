import uuid

from pymongo import MongoClient


commands = {}

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
    },
    'code': """
import psutil
import pyautogui

def choose_shape(data):
    print("Choose shape")
    try:
        num = int(data['shape_num'])
        data['xy'] = data['shapes'][num]
        pyautogui.moveTo(*data['xy'])
        del data['shape_num']
        try:
            pid = data['img_pid']
            p = psutil.Process(pid)
            p.kill()
            del data['img_pid']
        except KeyError:
            pass
    except Exception as e:
        data['error'] = str(e)
    return data

choose_shape(data)"""
}


commands['click'] = {
    'name': 'click',
    'phrase': ['кликни', 'сделай клик'],
    'paramType': {
        'xy': '(int, int)',
        'double_click': 'boolean'
    },
    'returnType': {
        'xy': '(int, int)'
    },
    'code': """
import pyautogui

def click(data):
    try:
        if 'xy' in data:
            x, y = data['xy']
        else:
            x, y = pyautogui.position()
            data['xy'] = x, y
        if pyautogui.onScreen(x, y):
            if data.get('double_click', False):
                print("Выполняю двойной клик мышкой в", x, y)
                pyautogui.doubleClick(x, y)
                del data['doubleClick']
            else:
                print("Выполняю клик мышкой в", x, y)
                pyautogui.click(x, y)
        else:
            data['error'] = "Эти координаты находятся вне вашего экрана", *pyautogui.size()
    except Exception as e:
        data['error'] = str(e)
    return data

click(data)"""
}

commands['detect_image'] = {
    'name': 'detect_image',
    'phrase': ['найди картинку', 'найди изображение'],
    'paramType': {
        'file': 'path',
    },
    'returnType': {
        'xy': '(int, int)'
    },
    'code': """
import pyautogui
from pyscreeze import ImageNotFoundException

def detect_image(data):
    print("Detect image")

    try:
        file = data['file']
        x, y = pyautogui.locateCenterOnScreen(file)
        data['xy'] = int(x), int(y)
        pyautogui.moveTo(x, y)
    except ImageNotFoundException:
        data['error'] = "Не нашел такое изображение на экране"
    except OSError:
        data['error'] = f"Не нашел вашу картинку {file}"
    except Exception as e:
        data['error'] = str(e)
    del data['file']
    return data 

detect_image(data)"""
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

commands['detect_shape'] = {
    'name': 'detect_shape',
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
     },
    'code': """
import cv2
import numpy as np
import pyautogui

colors = {
    'белый': (
        (
            (0.0, 0.0, 242.24999999999997),
            (178.50277777777777, 63.74999999999999, 254.99999999999997)
        ),
    ),
    'бирюзовый': (
        (
            (79.55555555555556, 127.49999999999999, 63.74999999999999),
            (94.47222222222223, 254.99999999999997, 254.99999999999997)
        ),
        (
            (79.55555555555556, 63.74999999999999, 127.49999999999999),
            (94.47222222222223, 127.49999999999999, 254.99999999999997)
        )
    ),
    'жёлтый': (
        (
            (19.88888888888889, 127.49999999999999, 63.74999999999999),
            (37.291666666666664, 254.99999999999997, 254.99999999999997)
        ),
        (
            (19.88888888888889, 63.74999999999999, 127.49999999999999),
            (37.291666666666664, 127.49999999999999, 254.99999999999997)
        )
    ),
    'желтый': (
        (
            (19.88888888888889, 127.49999999999999, 63.74999999999999),
            (37.291666666666664, 254.99999999999997, 254.99999999999997)
        ),
        (
            (19.88888888888889, 63.74999999999999, 127.49999999999999),
            (37.291666666666664, 127.49999999999999, 254.99999999999997)
        )
    ),
    'зелёный': (
        (
            (37.291666666666664, 127.49999999999999, 63.74999999999999),
            (79.55555555555556, 254.99999999999997, 254.99999999999997)
        ),
        (
            (37.291666666666664, 63.74999999999999, 127.49999999999999),
            (79.55555555555556, 127.49999999999999, 254.99999999999997)
        )
    ),
    'зеленый': (
        (
            (37.291666666666664, 127.49999999999999, 63.74999999999999),
            (79.55555555555556, 254.99999999999997, 254.99999999999997)
        ),
        (
            (37.291666666666664, 63.74999999999999, 127.49999999999999),
            (79.55555555555556, 127.49999999999999, 254.99999999999997)
        )
    ),
    'красный': (
        (
            (169.05555555555554, 127.49999999999999, 63.74999999999999),
            (178.50277777777777, 254.99999999999997, 254.99999999999997)
        ),
        (
            (169.05555555555554, 63.74999999999999, 127.49999999999999),
            (178.50277777777777, 127.49999999999999, 254.99999999999997)
        ),
        (
            (0.0, 127.49999999999999, 63.74999999999999),
            (7.458333333333333, 254.99999999999997, 254.99999999999997)
        ),
        (
            (0.0, 63.74999999999999, 127.49999999999999),
            (7.458333333333333, 127.49999999999999, 254.99999999999997)
        )
    ),
    'оранжевый': (((7.458333333333333, 127.49999999999999, 63.74999999999999),
                (19.88888888888889, 254.99999999999997, 254.99999999999997)),
                ((7.458333333333333, 63.74999999999999, 127.49999999999999),
                (19.88888888888889, 127.49999999999999, 254.99999999999997))),
    'серый': (
        (
            (0.0, 0.0, 25.5),
            (178.50277777777777, 254.99999999999997, 63.74999999999999)
        ),
        
    ),
    'светло-серый': (
        (
            (0.0, 0.0, 63.74999999999999),
            (178.50277777777777, 63.74999999999999, 242.24999999999997)
        ), 
    ),
    'синий': (((94.47222222222223, 127.49999999999999, 63.74999999999999),
            (129.27777777777777, 254.99999999999997, 254.99999999999997)),
            ((94.47222222222223, 63.74999999999999, 127.49999999999999),
            (129.27777777777777, 127.49999999999999, 254.99999999999997))),
    'сиреневый': (((146.68055555555554, 127.49999999999999, 63.74999999999999),
                (169.05555555555554, 254.99999999999997, 254.99999999999997)),
                ((146.68055555555554, 63.74999999999999, 127.49999999999999),
                (169.05555555555554, 127.49999999999999, 254.99999999999997))),
    'фиолетовый': (((129.27777777777777, 127.49999999999999, 63.74999999999999),
                    (146.68055555555554, 254.99999999999997, 254.99999999999997)),
                ((129.27777777777777, 63.74999999999999, 127.49999999999999),
                    (146.68055555555554, 127.49999999999999, 254.99999999999997))),
    'чёрный': (
        (
            (0.0, 0.0, 0.0), (178.50277777777777, 254.99999999999997, 25.5)
        ),
    ),
    'черный': (
        (
            (0.0, 0.0, 0.0), (178.50277777777777, 254.99999999999997, 25.5)
        ),
    )
}

def detect_shape(data):
    def get_mask(img):
        img = cv2.GaussianBlur(img, (3, 3), sigmaX=4, sigmaY=4)
        if 'color' in data:
            hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
            mask = np.zeros(hsv.shape[:-1], dtype=hsv.dtype)
            for low_c, up_c in colors[data['color']]:
                mask1 = cv2.inRange(hsv, low_c, up_c)
                mask = cv2.bitwise_or(mask, mask1)
            data.pop('color', None)
        else:
            gr = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            _, mask = cv2.threshold(gr, 220, 255, cv2.THRESH_BINARY)
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(3, 3))
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel) # noise reduction
        return mask

    def get_shapes(mask):
        screen_size = pyautogui.size()
        shapes = []
        min_area = 50
        max_area = np.prod(screen_size) / 2
        min_line_area = 30
        thickness = 5
        k = 3
        is_line = data['shape'] == 'line'

        mode = cv2.RETR_EXTERNAL
        cnts, _ = cv2.findContours(mask, mode, cv2.CHAIN_APPROX_SIMPLE)
        for c in cnts:
            if len(c) > 500: 
                continue
            if data['shape'] == 'rectangle':
                is_closed = True        
                peri = cv2.arcLength(c, is_closed)    
                approx = cv2.approxPolyDP(c, 0.01*peri, is_closed)
                if len(approx) == 4:
                    rect = cv2.minAreaRect(approx)
                else:
                    continue
            else:
                cnt = cv2.convexHull(c)
                rect = cv2.minAreaRect(cnt)
            width, height = rect[1][::-1] if np.abs(rect[2]) == 90 else rect[1]
            check_rect_size = True
            if 'width_low' in data:
                check_rect_size &= data['width_low'] < width
            if 'width_up' in data:
                check_rect_size &= width < data['width_up']
            if 'height_low' in data:
                check_rect_size &= data['height_low'] < height
            if 'height_up' in data:
                check_rect_size &= height < data['height_up']

            check_line_size = ((width < thickness and height > k*width) \
                or (height < thickness and width > k*height)) \
                and height*width > min_line_area
            check_rect_size &= width > thickness  \
                and height > thickness \
                and min_area < cv2.contourArea(c) \
                and width*height < max_area
            
            if (is_line and check_line_size) \
                    or (not is_line and check_rect_size):
                shapes.append(rect)
        shapes = sorted(shapes, key=lambda x: x[0][::-1])
        print("Найдено", len(shapes), "объект[а/ов]")

        data.pop('shape', None)
        data.pop('width_low', None)
        data.pop('width_up', None)
        data.pop('height_low', None)
        data.pop('height_up', None)
        return shapes

    def draw_shapes(shapes, img):
        centers = []
        color = (0, 255, 0)
        thickness = 2
        for i, shape in enumerate(shapes):
            box = np.int0(cv2.boxPoints(shape))
            cv2.drawContours(img, [box], -1, color, thickness)
            center = int(shape[0][0]), int(shape[0][1])

            cv2.putText(img, str(i), center, 
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, 
                (255, 128, 0), thickness)
            centers.append(center) 
        return centers           

    try:
        err = ""
        img = pyautogui.screenshot()
        img = np.array(img)
        img = img[:, :, ::-1].copy() # conversion from RGB to BGR
        # img = cv2.imread("scr.png")
        

        mask = get_mask(img)
        shapes = get_shapes(mask)
        if len(shapes):
            shapes = draw_shapes(shapes, img)
            file_name = 'detected_objects.png'
            cv2.imwrite(file_name, img)
            data['img'] = file_name
            data['shapes'] = shapes
        else:
            err = 'Ничего не нашел'
    except KeyError:
        err = "Такого цвета нет"
    # except cv2.error:
    #     err = "Такого файла нет"
    except Exception as e:
        err = str(e)
    if err:
        data['error'] = err
    return data

detect_shape(data)"""
}

commands['show_shapes'] = {
    'name': 'show_shapes',
    'phrase': ['покажи найденное'],
    'paramType': {
        'img': 'path',
     },
    'returnType': { },
    'code': """
from multiprocessing import Process
print("Show shapes")

if 'img' in data:
    data['code'] = '''
import cv2;
import matplotlib.pyplot as plt;
img = cv2.imread(data['img']);
img = cv2.resize(img, (int(img.shape[1]/1.1), int(img.shape[0]/1.1)));
img = img[:, :, ::-1];
dpi = 120;
height, width, _ = img.shape;

figsize = width / float(dpi), height / float(dpi);

fig = plt.figure(figsize=figsize);
ax = fig.add_axes([0, 0, 1, 1]);

ax.axis('off');

ax.imshow(img);
figManager = plt.get_current_fig_manager();
figManager.full_screen_toggle();
plt.show()
'''
    process = Process(target=temp, args=(data,))
    process.start()
    data['img_pid'] = process.pid
else:
    data['error'] = 'Ничего не нашел, поэтому нечего показывать'
"""
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
