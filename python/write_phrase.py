import platform

import pyautogui
import pyperclip


def write_phrase(data):
    pyperclip.copy(data['text'])
    command = "command" if platform.system() == "Darwin" else "ctrl"
    pyautogui.hotkey(command, "v")
    return data

write_phrase(data)
