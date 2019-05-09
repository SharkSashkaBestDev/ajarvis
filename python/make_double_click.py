import pyautogui


def double_click(data):
    try:
        if 'xy' in data:
            x, y = data['xy']
        else:
            x, y = pyautogui.position()
            data['xy'] = x, y
        if pyautogui.onScreen(x, y):
            print("Выполняю двойной клик мышкой в", x, y)
            pyautogui.doubleClick(x, y)
        else:
            data['error'] = "Эти координаты находятся вне вашего экрана", *pyautogui.size()
    except Exception as e:
        data['error'] = str(e)
    return data

double_click(data)
