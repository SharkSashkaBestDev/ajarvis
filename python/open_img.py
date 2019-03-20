import sys

import cv2, pyautogui

img = cv2.imread(sys.argv[1])
img = cv2.resize(img, (int(img.shape[1]/1.1), int(img.shape[0]/1.1)))
cv2.imshow("image", img)
cv2.waitKey(0)
cv2.destroyAllWindows()