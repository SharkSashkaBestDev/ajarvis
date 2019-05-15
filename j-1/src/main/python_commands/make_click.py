import pyautogui


def click(data):
    try:
        if 'xy' in data:
            x, y = data['xy']
        else:
            x, y = pyautogui.position()
            data['xy'] = x, y
        if pyautogui.onScreen(x, y):
            if data['type'] == 'двойной':
                pyautogui.doubleClick(x, y)
            else:
                pyautogui.click(x, y)
        else:
            size = pyautogui.size()
            data['error'] = f"Эти координаты ({x}; {y}) " \
                f"находятся вне вашего экрана ({size.width}; {size.height})"
    except KeyError as e:
        data['error'] = f"Требуется аргумент '{e.args[0]}'"
    except Exception as e:
        data['error'] = str(e)
    return data

click(data)
