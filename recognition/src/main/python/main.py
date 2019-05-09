import speech_recognition as sr
from threading import Thread

from queue import Queue
import requests

LANG_RU = "ru-RU"
PHRASE = "phrase"
INT = "int"
STRING = "String"
BASE_URL = "http://127.0.0.1:8091/ajarvis/commands/"


def args_check(args_type, raw_arg):
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
                # dict_of_new_phrases.update({key: arg})
                # break
            else:
                print("Повторите попытку ввода")
                raw_arg = recognize_exception(True)


def set_args(args_description):
    global args_flag
    dict_of_new_phrases = {}

    if args_description[PHRASE] is not None:
        dict_of_new_phrases = {PHRASE: args_description.pop(PHRASE)}

        for i in args_description:
            print("Введите аргумент " + i)
            args_flag = False
            args_struct = args_description[i].split("[")
            arg_type = len(args_struct)
            arg = recognize_exception(True)
            if arg_type == 1:
                true_arg = args_check(args_struct[0], arg)
                dict_of_new_phrases.update({i: true_arg})
            else:
                array_for_arguments = []
                j = 0
                while "стоп" not in arg.split(" ") or j < int(args_struct[1]):
                    true_arg = args_check(args_struct[0], arg)
                    array_for_arguments.append(true_arg)
                    j += 1
                    if j >= int(args_struct[1]):
                        print("вы ввели достаточно аргументов скажите 'стоп' для остановки")
                    arg = recognize_exception(True)

                dict_of_new_phrases.update({i: array_for_arguments})
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
        print("Я слушаю!")
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
                # print(send_args)
                new_args = set_args(send_args)
                print(new_args)
                var_redy = requests.post(url_param, json=new_args)
                #get_args = var_redy.json()
                # print(get_args)

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