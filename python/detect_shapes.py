import cv2
import numpy as np
import pyautogui

from colors import colors


def detect_shape(data):
    def get_mask(img):
        img = cv2.GaussianBlur(img, (3, 3), sigmaX=4, sigmaY=4)
        if 'color' in data:
            hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
            mask = np.zeros(hsv.shape[:-1], dtype=hsv.dtype)
            for low_c, up_c in colors[data['color']]:
                mask1 = cv2.inRange(hsv, low_c, up_c)
                mask = cv2.bitwise_or(mask, mask1)
            data.pop('color', None)
        else:
            gr = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            _, mask = cv2.threshold(gr, 220, 255, cv2.THRESH_BINARY)
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(3, 3))
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel) # noise reduction
        return mask

    def get_shapes(mask):
        screen_size = pyautogui.size()
        shapes = []
        min_area = 50
        max_area = np.prod(screen_size) / 2
        min_line_area = 30
        thickness = 5
        k = 3
        is_line = data['shape'] == 'line'

        mode = cv2.RETR_EXTERNAL
        cnts, _ = cv2.findContours(mask, mode, cv2.CHAIN_APPROX_SIMPLE)
        for c in cnts:
            if len(c) > 500: 
                continue
            if data['shape'] == 'rectangle':
                is_closed = True        
                peri = cv2.arcLength(c, is_closed)    
                approx = cv2.approxPolyDP(c, 0.01*peri, is_closed)
                if len(approx) == 4:
                    rect = cv2.minAreaRect(approx)
                else:
                    continue
            else:
                cnt = cv2.convexHull(c)
                rect = cv2.minAreaRect(cnt)
            width, height = rect[1][::-1] if np.abs(rect[2]) == 90 else rect[1]
            check_rect_size = True
            if 'width_low' in data:
                check_rect_size &= data['width_low'] < width
            if 'width_up' in data:
                check_rect_size &= width < data['width_up']
            if 'height_low' in data:
                check_rect_size &= data['height_low'] < height
            if 'height_up' in data:
                check_rect_size &= height < data['height_up']

            check_line_size = ((width < thickness and height > k*width) \
                or (height < thickness and width > k*height)) \
                and height*width > min_line_area
            check_rect_size &= width > thickness  \
                and height > thickness \
                and min_area < cv2.contourArea(c) \
                and width*height < max_area
            
            if (is_line and check_line_size) \
                    or (not is_line and check_rect_size):
                shapes.append(rect)
        shapes = sorted(shapes, key=lambda x: x[0][::-1])
        print("Найдено", len(shapes), "объект[а/ов]")

        data.pop('shape', None)
        data.pop('width_low', None)
        data.pop('width_up', None)
        data.pop('height_low', None)
        data.pop('height_up', None)
        return shapes

    def draw_shapes(shapes, img):
        centers = []
        color = (0, 255, 0)
        thickness = 2
        for i, shape in enumerate(shapes):
            box = np.int0(cv2.boxPoints(shape))
            cv2.drawContours(img, [box], -1, color, thickness)
            center = int(shape[0][0]), int(shape[0][1])

            cv2.putText(img, str(i), center, 
                cv2.FONT_HERSHEY_SIMPLEX, 0.6, 
                (255, 128, 0), thickness)
            centers.append(center) 
        return centers

    try:
        err = ""
        img = pyautogui.screenshot()
        img = np.array(img)
        img = img[:, :, ::-1] # conversion from RGB to BGR
        
        mask = get_mask(img)
        # cv2.imwrite("mask.png", mask)
        shapes = get_shapes(mask)
        if len(shapes):
            shapes = draw_shapes(shapes, img)
            file_name = 'detected_objects.png'
            cv2.imwrite(file_name, img)
            data['img'] = file_name
            data['shapes'] = shapes
        else:
            err = 'Ничего не нашел'
    except KeyError:
        err = "Такого цвета нет"
    except Exception as e:
        err = str(e)
    if err:
        data['error'] = err
    return data
