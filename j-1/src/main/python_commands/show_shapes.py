from multiprocessing import Process

import cv2
import matplotlib.pyplot as plt


def show_shapes(data):
    if 'img' in data:
        if cv2.imread(data['img']) is None:
            data['error'] = "Такого изображения с найденными объектами не существует. " \
                            "Попробуйте снова выполнить команду поиска нужных объектов."
        else:
            data['code'] = '''
import cv2
import matplotlib.pyplot as plt
img = cv2.imread(data['img'])
img = cv2.resize(img, (int(img.shape[1]/1.1), int(img.shape[0]/1.1)))
img = img[:, :, ::-1]
dpi = 120
height, width, _ = img.shape

figsize = width / float(dpi), height / float(dpi)

fig = plt.figure(figsize=figsize)
ax = fig.add_axes([0, 0, 1, 1])

ax.axis('off')

ax.imshow(img)
figManager = plt.get_current_fig_manager()
figManager.full_screen_toggle()
plt.show()'''
            process = Process(target=temp, args=(data,))
            process.start()
            data['img_pid'] = process.pid
            del data['code']
    else:
        data['error'] = "Требуется аргумент 'img'. " \
                        "Попробуйте снова выполнить команду поиска нужных объектов."
    return data

show_shapes(data)
