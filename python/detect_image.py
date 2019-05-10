import pyautogui
from pyscreeze import ImageNotFoundException


def detect_image(data):
    print("Detect image")
    try:
        file = data['file']
        x, y = pyautogui.locateCenterOnScreen(file, confidence=0.9)
        data['xy'] = int(x), int(y)
        pyautogui.moveTo(x, y)
    except ImageNotFoundException:
        data['error'] = "Не нашел такое изображение на экране"
    except OSError:
        data['error'] = "Не нашел ваше изображение в файловой системе. " \
                        "Проверьте название файла."
    except KeyError as e:
        data['error'] = f"Требуется аргумент '{e.args[0]}'"
    except Exception as e:
        data['error'] = str(e)
    return data

detect_image(data)
