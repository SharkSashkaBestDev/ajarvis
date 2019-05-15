import pyautogui


def shift(data):
    try:
        SHIFT = 20
        xOffset = 0
        yOffset = 0
        if 'direction' in data:
            if data['direction'] == 'выше':
                yOffset = -SHIFT
            if data['direction'] == 'ниже':
                yOffset = SHIFT
            if data['direction'] == 'левее':
                xOffset = -SHIFT
            if data['direction'] == 'правее':
                xOffset = SHIFT
            pyautogui.move(xOffset, yOffset)
            data['xy'] = pyautogui.position()
        else:
            data['error'] = "Требуется аргумент 'direction'"
    except Exception as e:
        data['error'] = str(e)
    return data

shift(data)