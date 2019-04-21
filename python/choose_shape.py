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
    except Exception as e:
        data['error'] = str(e)
    return data
