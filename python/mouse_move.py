import pyautogui


def mouse_move(data):
    print("Mouse move")
    if 'xy' in data:
        x, y = data['xy']
        if pyautogui.onScreen(x, y):
            pyautogui.moveTo(*data['xy'])
        else:
            size = pyautogui.size()
            data['error'] = f"Эти координаты ({x}; {y}) " \
                f"находятся вне вашего экрана ({size.width}; {size.height})"
    else:
        data['error'] = "Требуется аргумент 'xy'"
    return data

mouse_move(data)