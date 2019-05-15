import pyautogui


def double_click(data):
    try:
        if 'xy' in data:
            x, y = data['xy']
        else:
            x, y = pyautogui.position()
            data['xy'] = x, y
        if pyautogui.onScreen(x, y):
            print("Double click", x, y)
            pyautogui.doubleClick(x, y)
        else:
            size = pyautogui.size()
            data['error'] = f"Эти координаты ({x}; {y}) " \
                f"находятся вне вашего экрана ({size.width}; {size.height})"
    except Exception as e:
        data['error'] = str(e)
    return data

double_click(data)
