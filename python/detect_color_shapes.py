from argparse import ArgumentParser

import cv2
import numpy as np
import pyautogui

from colors import colors
from command import Command


class DetectColorShapeCommand(Command):
    def execute(self):
        try:
            # img = pyautogui.screenshot("screen.png")
            # img = np.array(img)
            # img = img[:, :, ::-1].copy() # convertion from RGB to BGR

            file = 'scr.png'
            img = cv2.imread(file)
            color = colors[self.kwargs['color']]

            mask = self.get_color_mask(img, color)
            res = cv2.bitwise_and(img, img, mask=mask)
            cnts = self.get_top_contours(mask, n=self.kwargs['contours_num'])
            self.draw_contours(cnts, img)
            self.draw_contours(cnts, res)
            cv2.imwrite('mask_detected.png', res)
            cv2.imwrite('result_image.png', img)
            cv2.imwrite('mask.png', mask)
        except KeyError:
            print("Такого цвета нет")


    def get_color_mask(self, img, color):
        img = cv2.GaussianBlur(img, (3, 3), sigmaX=4, sigmaY=4)
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        if self.kwargs['color'] == "красный":
            mask1 = cv2.inRange(hsv, color[0][0], color[0][1])
            mask2 = cv2.inRange(hsv, color[1][0], color[1][1])
            mask = cv2.bitwise_or(mask1, mask2)
        else:
            mask = cv2.inRange(hsv, color[0], color[1])
        kernel = cv2.getStructuringElement(cv2.MORPH_RECT,(3, 3))
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel) # noise reduction
        return mask

    def get_top_contours(self, mask, n=10):
        cnts, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        # cnts = [self._prepare_contour(c) for c in cnts]
        cnts = sorted(cnts, key=lambda c: -cv2.contourArea(c))
        print("Найдено", len(cnts), self.kwargs['color'][:-1] + "х объектов")
        return cnts[:n]

    def draw_contours(self, cnts, img):
        for c in cnts:
            # color = [int(v) for v in np.random.randint(0, 255, 3)]
            color = (0, 255, 0)
            cv2.drawContours(img, [c], -1, color, 2)

    def _prepare_contour(self, contour):
        contour = cv2.convexHull(contour)
        perimeter = cv2.arcLength(contour, True)
        contour = cv2.approxPolyDP(contour, 0.005*perimeter, True)
        return contour


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('color', metavar='color', type=str)
    parser.add_argument('contours_num', metavar='contours_num', type=int)

    args = parser.parse_args()
    com = DetectColorShapeCommand(
        dict(
            color=args.color, 
            contours_num=args.contours_num
        ))
    com.execute()
