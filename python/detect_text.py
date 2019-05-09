from collections import namedtuple
from functools import lru_cache
from itertools import product
import re

import cv2
import pyautogui
import pytesseract
import numpy as np


def detect_text(data):
    SCREEN_X, SCREEN_Y = pyautogui.size()
    img = pyautogui.screenshot()
    found = pytesseract.image_to_boxes(img, lang='rus', output_type=pytesseract.Output.DICT)

    phrase = data['phrase']

    text = "".join(found['char'])

    red = re.compile('[,.@|:;—<>=+-^"`!?()*&%$#_»«\n~…®©\'“”№{}›‘°„]')
    re_space = re.compile('\s+')

    phrase = red.sub(' ', phrase.lower())
    phrase = re_space.sub(' ' , phrase)

    for m in sorted(red.finditer(text), key=lambda x: x.span()[0], reverse=True):
        i = m.span()[0]
        del found['char'][i]
        del found['left'][i]
        del found['right'][i]
        del found['bottom'][i]
        del found['top'][i]


    text = "".join(found['char']).lower()

    words = tuple(phrase.split())
    n_words = len(words)
    matches = []
    n_min = (2 if n_words > 4 else (2 if n_words > 1 else 1)) - 1
    Match = namedtuple('m', ['l', 'r'])

    @lru_cache(maxsize=128)
    def find(words):
        res = []
        ms = list(re.finditer("".join(words), text))
        if ms:
            for m in ms:
                mat = Match(*m.span())
                if all([mat.l > v.r or mat.r < v.l or (mat.l <= v.l and v.r <= mat.r) for v in matches]):
                    res.append(mat)
            matches.extend(res)
        return res

    result = []

    def f(words, mss, n):
        if not words: 
            return None
        for j in range(len(words), n_min, -1):
            ws = words[:j]
            n += len(ws)
            ms = find(ws)
            if ms:
                mss.append(ms)
                if n == n_words:
                    result.append(mss.copy())
                    mss.pop()
                    n -= len(ws)
                    continue
                f(words[j:], mss, n)
                mss.pop()
            n -= len(ws)

    f(words, [], 0)

    rects = []
    for res in result:
        for ms in product(*res):
            r_pre = ms[0].l
            right_max = top_max = 0
            bottom_min = left_min = SCREEN_X
            for m in ms:
                diff = m.l - r_pre
                if diff > 300 or diff < 0:
                    break
                for j in range(m.l, m.r):
                    if found['left'][j] < left_min: left_min = found['left'][j]
                    if found['right'][j] > right_max: right_max = found['right'][j]
                    if found['bottom'][j] < bottom_min: bottom_min = found['bottom'][j]
                    if found['top'][j] > top_max: top_max = found['top'][j]
                r_pre = m.r
            else:
                rects.append(((left_min, SCREEN_Y-top_max), (right_max, SCREEN_Y-bottom_min)))

    print("Найдено", len(rects))

    centers = []
    rects = sorted(rects, key=lambda x: x[0][::-1])
    img = np.array(img)
    for i, rect in enumerate(rects):
        cv2.rectangle(img, *rect, (0, 255, 0), 2)
        center = rect[0][0] + (rect[1][0] - rect[0][0]) // 2, rect[0][1] + (rect[1][1] - rect[0][1]) // 2 
        cv2.putText(img, str(i), center, 
            cv2.FONT_HERSHEY_SIMPLEX, 1, 
            (0, 128, 255), 2)
        centers.append(center)

    file_name = 'detected_text.png'
    cv2.imwrite(file_name, cv2.cvtColor(img, cv2.COLOR_RGB2BGR))
    data['shapes'] = centers
    data['img'] = file_name
    return data


detect_text(data)
