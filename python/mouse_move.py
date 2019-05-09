import pyautogui


def mouse_move(data):
    print("Mouse move")

    pyautogui.moveTo(*data['xy'])
    return data

mouse_move(data)