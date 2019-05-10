import pyautogui


def press_enter(data):
    print("Press Enter")
    pyautogui.press('enter')
    return data

press_enter(data)