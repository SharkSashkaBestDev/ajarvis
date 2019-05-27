import speech_recognition as sr
from threading import Thread

from queue import Queue
import requests

import os
import platform
import webbrowser


LANG_RU = "ru-RU"
PHRASE = "phrase"
INT = "int"
ENUM = "enum"
PATH = "path"
COMAND = "comand"
RUSSIAN = "russian"
STRING = "String"
BASE_URL = "http://127.0.0.1:8091/ajarvis/commands/"
simbol = "/"


def args_check(args_type, raw_arg, enum_filtr):
    url = BASE_URL + "filter"
    if args_type == INT:
        while True:
            try:
                arg = int(raw_arg)
                print(arg)
                return arg
            except:
                print("Повторите попытку ввода")
                raw_arg = recognize_exception(True)

    elif args_type == STRING:
        while True:
            arg = raw_arg
            if arg != "":
                print(arg)
                return arg
            else:
                print("Повторите попытку ввода")
                raw_arg = recognize_exception(True)
    elif args_type == COMAND:
        while True:
            com = requests.post(url, json={PHRASE: raw_arg})
            try:
                arg = com.json().pop(PHRASE)
                print(arg)
                return arg
            except:
                print("Команда не распознана или такой команды не существует, повторите")
                raw_arg = recognize_exception(True)
    elif args_type == ENUM:
        while True:
            print("Скажите словл из списка " + enum_filtr)
            arg = recognize_exception(True)
            list = enum_filtr.split(", ")
            if arg in list:
                print(arg)
                return arg
            else:
                print("Вы ввели слово не из списка")
    elif args_type == PATH:
        # webbrowser.open("http://127.0.0.1:5000/")
        arg = ""
        path = []
        error_args = False
        while "стоп" not in arg.split(" "):
            stre = simbol + simbol.join(path)
            if os.path.isfile(stre):
                break
            list = os.listdir(stre)
            [x.lower() for x in list].sort()

            for i in range(len(list) - 1, -1, -1):
                try:
                    if len(path) == 0:
                        if not os.path.isfile(stre + list[i]):
                            os.listdir(stre + list[i])
                    else:
                        if not os.path.isfile(stre + simbol + list[i]):
                            os.listdir(stre + simbol + list[i])
                except:
                    del list[i]
            html = '<!DOCTYPE html><html> <head><meta charset = "UTF-8"><title> путь </title></head><body><h1>Скажите номер папки или файла</h1><table>'
            for i in range(len(list)):
                html += '<tr><td>' + str(i+1) + '</ td><td>' + str(list[i]) + '</td></tr>'

            if error_args:
                html += '<h2>неправильный аргумент</h2></table></body></html>'
            else:
                html += '</table></body></html>'
            with open("path.html", 'w') as f:
                f.write(html)
                f.close()

            webbrowser.open("path.html")
            # webbrowser.refresh()
            # redirect(url_for('apiget', list=list, err = False))
            arg = recognize_exception(True)
            try:
                numb = int(arg) - 1
                path.append(list[numb])
            except:
                if arg != "стоп":
                    pass
                    # redirect(url_for('apiget', list=list, err=True))
        return stre


def array_russian_text(lim, j, russian_array):
    if lim == 1:
        print("Введите аргумент " + russian_array[0])
    elif j < lim:
        print("Введите аргумент " + russian_array[j])
    arg = recognize_exception(True)
    return arg


def set_args(args_description):
    global args_flag
    dict_of_new_phrases = {}

    if args_description[PHRASE] is not None:
        dict_of_new_phrases = {PHRASE: args_description.pop(PHRASE)}
        rusian = args_description.pop(RUSSIAN, None)

        if args_description:
            for i in args_description:

                args_flag = False
                args_struct = args_description[i].split("[")
                arg_type = len(args_struct)

                if arg_type == 1:
                    print("Введите аргумент " + rusian[i])
                    if args_struct[0] == PATH:
                        true_arg = args_check(args_struct[0], None, None)
                    else:
                        arg = recognize_exception(True)
                        true_arg = args_check(args_struct[0], arg, None)
                    dict_of_new_phrases.update({i: true_arg})
                else:
                    try:
                        russian_array = rusian[i].split(" ")
                        lim = int(args_struct[1])
                        array_for_arguments = []
                        j = 0
                        arg = array_russian_text(lim, j, russian_array)
                        while "стоп" not in arg.split(" ") or j < lim:
                            true_arg = args_check(args_struct[0], arg, None)
                            array_for_arguments.append(true_arg)
                            j += 1
                            if j >= int(args_struct[1]):
                                print("вы ввели достаточно аргументов скажите 'стоп' для остановки")
                            arg = array_russian_text(lim, j, russian_array)
                        dict_of_new_phrases.update({i: array_for_arguments})
                    except:
                        print("Введите аргумент " + rusian[i])
                        true_arg = args_check(args_struct[0], None, args_struct[1])
                        dict_of_new_phrases.update({i: true_arg})

    print("Аргументы успешно введены")
    args_flag = True
    return dict_of_new_phrases


def recognize_exception(flag):
    while True:
        try:
            if flag:
                arg = r.recognize_google(list_of_args.get(), language=LANG_RU).lower()
            else:
                arg = r.recognize_google(list_of_audio.get(), language=LANG_RU).lower()
            return arg
        except:
            arg = recognize_exception(flag)
            break
    return arg


def listen():
    global args_flag
    with sr.Microphone() as source:
        r.adjust_for_ambient_noise(source, duration=1)
        print("я слушаю!")
        while True:
            aud = r.listen(source, phrase_time_limit=5)
            if args_flag:
                list_of_audio.put(aud)
            else:
                list_of_args.put(aud)


def recognize():
    url = BASE_URL + "filter"
    url_param = BASE_URL + "execute"

    while True:
        try:
            current_phrase = recognize_exception(False)

            resoult = standard_phrases(current_phrase)
            if resoult != " ":
                print(resoult)
                var = requests.post(url, json={PHRASE: resoult})
                send_args = var.json()
                new_args = set_args(send_args)
                print(new_args)
                requests.post(url_param, json=new_args)
        except Exception as _:
            print("Аргументы не приняты")
        finally:
            list_of_audio.task_done()


def standard_phrases(current_phrase):
    global end, pause_flag
    if end:
        if "да" in current_phrase.split(" "):
            print("Пока!")
            exit(0)
        else:
            print("Завершение отменено")
            end = False
            return " "

    if "завершить" in current_phrase.split(" ") and pause_flag:
        print("Вы точно хотите выйти")
        end = True
        return " "
    if "остановить" in current_phrase.split(" ") and pause_flag:
        pause_flag = False
        print("Распознавание приостановлено!")
    elif "начать" in current_phrase.split(" ") and not pause_flag:
        pause_flag = True
        print("Распознавание возобновлено!")
        return " "
    if pause_flag:
        return current_phrase
    else:
        return " "


if __name__ == "__main__":
    global end, pause_flag, args_flag
    args_flag = True
    end = False
    pause_flag = True

    r = sr.Recognizer()
    r.energy_threshold = 3500  # уровень чувствительности
    current_phrase = ""

    list_of_audio = Queue()
    list_of_args = Queue()

    th_1 = Thread(target=listen)
    th_1.setDaemon(True)
    th_1.start()

    recognize()
