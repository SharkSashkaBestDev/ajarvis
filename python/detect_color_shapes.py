from argparse import ArgumentParser
from subprocess import Popen

import cv2
import numpy as np
import pyautogui

from colors import colors
from command import Command


class DetectColorShapeCommand(Command):
    def execute(self):
        try:
            img = pyautogui.screenshot()
            img = np.array(img)
            img = img[:, :, ::-1].copy() # conversion from RGB to BGR

            color = colors[self.data['color']]

            mask = self.get_color_mask(img, color)
            cnts = self.get_top_contours(mask)
            cnts = self.draw_contours(cnts, img)
            if cnts:
                file_name = 'detected_objects.png'
                cv2.imwrite(file_name, img)
                self.data['img_process'] = self.show_img(file_name)
                self.data['shapes'] = cnts
        except KeyError:
            print("Такого цвета нет")
        except cv2.error as e:
            print("Такого файла нет")
        return self.data

    def get_color_mask(self, img, color):
        img = cv2.GaussianBlur(img, (3, 3), sigmaX=4, sigmaY=4)
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        if self.data['color'] == "красный":
            mask1 = cv2.inRange(hsv, color[0][0], color[0][1])
            mask2 = cv2.inRange(hsv, color[1][0], color[1][1])
            mask = cv2.bitwise_or(mask1, mask2)
        else:
            mask = cv2.inRange(hsv, color[0], color[1])
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(3, 3))
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel) # noise reduction
        return mask

    def get_top_contours(self, mask, n=10):
        mode = cv2.RETR_EXTERNAL
        cnts, _ = cv2.findContours(mask, mode, cv2.CHAIN_APPROX_SIMPLE)
        cs = []
        for c in cnts:
            if len(c) > 500: 
                continue
            cnt = cv2.convexHull(c)
            rect = cv2.minAreaRect(cnt)
            width, height = rect[1]

            min_area = 60
            max_area = np.prod(pyautogui.size()) / 2
            min_line_area = 30
            thickness = 5
            k = 3
            if self.data['line']:
                if ((width < thickness and height > k*width) \
                        or (height < thickness and width > k*height)) \
                        and height*width > min_line_area:
                    cs.append(rect)
            elif (width > thickness and height > thickness) \
                    and min_area < cv2.contourArea(c) < max_area \
                    and min_area < width*height < max_area:
                cs.append(rect)
        print("Найдено", len(cs), self.data['color'][:-1] + "х объект[а/ов]")
        cs = sorted(cs, key=lambda x: x[0][::-1])
        return cs

    def draw_contours(self, cnts, img):
        new_cnts = []
        for i, c in enumerate(cnts):
            box = np.int0(cv2.boxPoints(c))
            cv2.drawContours(img, [box], -1, (0, 255, 0), 2)
            cx, cy = np.int0(c[0])
            cv2.putText(img, str(i), (cx, cy), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 128, 0), 2)
            new_cnts.append((cx, cy)) 
        return new_cnts           

    def show_img(self, img):
        process = Popen(['python', 'open_img.py', img])
        return process


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('color', type=str)
    parser.add_argument('--line', type=bool, default=False)

    args = parser.parse_args()
    com = DetectColorShapeCommand({'color': args.color, 'line': args.line})
    com.execute()
