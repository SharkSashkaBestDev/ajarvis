from collections import namedtuple
from itertools import permutations
import re

import cv2
import pyautogui
import pytesseract


def detect_text(data):
    SCREEN_X, SCREEN_Y = pyautogui.size()
    img = pyautogui.screenshot()
    found = pytesseract.image_to_boxes(img, lang='rus', output_type=pytesseract.Output.DICT)

    phrase = data['phrase']

    text = "".join(found['char'])

    red = re.compile('[,.@|:;—<>=+-^"`!?()*&%$#_»«\n~…®©\'“”№{}›‘]')
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

    words = phrase.split()
    n_words = len(words)
    matches = []
    n_min = (2 if n_words > 4 else (2 if n_words > 1 else 1)) - 1
    Match = namedtuple('m', ['l', 'r'])

    for i in range(n_words, n_min, -1):
        combs = n_words - i + 1
        for j in range(combs):
            ws = words[j:j+i]
            matchs = list(re.finditer("".join(ws), text))
            if matchs:
                for m in matchs:
                    mat = Match(*m.span())
                    cond = all([mat.l > v.r or mat.r < v.l for v in matches])
                    if cond: 
                        matches.append(mat)

    rects = []
    new_ph = "".join(words)
    ph_len = len(new_ph)

    for i in range(1, min(n_words, len(matches))+1):
        min_combs = []
        for ms in permutations(matches, i):
            size = sum([m.r - m.l for m in ms])
            if ph_len == size:
                sentence = "".join([text[m.l : m.r] for m in ms])
                max_dist = max([abs(ms[j+1].l - ms[j].r) for j in range(len(ms) - 1)], default=0)
                if sentence == new_ph and max_dist < 400:
                    right_max = top_max = 0
                    bottom_min = left_min = SCREEN_X
                    for m in ms:
                        for j in range(m.l, m.r):
                            if found['left'][j] < left_min: left_min = found['left'][j]
                            if found['right'][j] > right_max: right_max = found['right'][j]
                            if found['bottom'][j] < bottom_min: bottom_min = found['bottom'][j]
                            if found['top'][j] > top_max: top_max = found['top'][j]
                    rects.append(((left_min, SCREEN_Y-top_max), (right_max, SCREEN_Y-bottom_min)))


    centers = []
    rects = sorted(rects, key=lambda x: x[0][::-1])
    for i, rect in enumerate(rects):
        cv2.rectangle(img, *rect, (0, 255, 0), 2)
        center = rect[0][0] + (rect[1][0] - rect[0][0]) // 2, rect[0][1] + (rect[1][1] - rect[0][1]) // 2 
        cv2.putText(img, str(i), center, 
            cv2.FONT_HERSHEY_SIMPLEX, 1, 
            (255, 128, 0), 2)
        centers.append(center)

    cv2.imwrite('detected_objects.png', img)
    data['shapes'] = centers
    return data

detect_text(data)
