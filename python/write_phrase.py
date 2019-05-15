import platform

import pyautogui
import pyperclip


def write_phrase(data):
    print("Write text")
    if 'text' in data:
        pyperclip.copy(data['text'])
        command = "command" if platform.system() == "Darwin" else "ctrl"
        pyautogui.hotkey(command, "v")
        del data['text']
    else:
        data['error'] = "Требуется аргумент 'text"
    return data

write_phrase(data)
