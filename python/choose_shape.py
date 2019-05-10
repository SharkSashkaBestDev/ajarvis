import psutil
import pyautogui


def choose_shape(data):
    print("Choose shape")
    try:
        num = int(data['shape_num'])
        data['xy'] = data['shapes'][num]     
        pyautogui.moveTo(*data['xy'])
        del data['shape_num']
        if 'img_pid' in data:
            pid = data['img_pid']
            p = psutil.Process(pid)
            p.kill()
            del data['img_pid']
    except ValueError:
        data['error'] = "Аргумент 'shape_num' должен быть целым положительным числом"
    except KeyError as e:
        data['error'] = f"Требуется аргумент '{e.args[0]}'"
    except IndexError:
        data['error'] = f"Объекта с индексом {num} не существует"
    except Exception as e:
        data['error'] = str(e)
    return data

choose_shape(data)
