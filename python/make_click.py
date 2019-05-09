import pyautogui


def click(data):
    try:
        if 'xy' in data:
            x, y = data['xy']
        else:
            x, y = pyautogui.position()
            data['xy'] = x, y
        if pyautogui.onScreen(x, y):
            print("Выполняю клик мышкой в", x, y)
            pyautogui.click(x, y)
        else:
            data['error'] = "Эти координаты находятся вне вашего экрана", *pyautogui.size()
    except Exception as e:
        data['error'] = str(e)
    return data

click(data)
