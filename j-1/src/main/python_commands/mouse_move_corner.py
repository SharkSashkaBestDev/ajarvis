import pyautogui

pyautogui.FAILSAFE = False

def move_corner(data):
    try:
        width, height = pyautogui.size()
        x = width if data['horizontal'] == 'правый' else 0
        y = height if data['vertical'] == 'нижний' else 0
        pyautogui.moveTo(x, y)
        data['xy'] = pyautogui.position()
    except KeyError as e:
        data['error'] = f"Требуется аргумент '{e.args[0]}'"
    except Exception as e:
        data['error'] = str(e)
    return data

move_corner(data)