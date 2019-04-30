import speech_recognition as sr
from threading import Thread

from queue import Queue
import requests

LANG_RU = "ru-RU"
PHRASE = "phrase"
INT = "int"
STRING = "String"
BASE_URL = "http://127.0 0.1:8091/ajarvis/commands/"

def set_args(args):
    global args_flag
    cords = []
    dict_of_new_phrases = {}

    if args[PHRASE] is not None:
        dict_of_new_phrases = {PHRASE: args.pop(PHRASE)}

        for i in args:
            print("Введите аргумент " + i)
            args_flag = False
            if i == "xy":
                for _ in args[i]:
                    while True:
                        try:
                            arg = int(r.recognize_google(list_of_args.get(), language=LANG_RU).lower())
                            print(arg)
                            cords.append(arg)
                            break
                        except:
                            print("Повторите попытку ввода")
                dict_of_new_phrases.update({i: cords})
                print(dict_of_new_phrases)
            if args[i] == INT:
                while True:
                    try:
                        arg = int(r.recognize_google(list_of_args.get(), language=LANG_RU).lower())
                        print(arg)
                        dict_of_new_phrases.update({i: arg})
                        break
                    except:
                        print("Повторите попытку ввода")

            elif args[i] == STRING:
                while True:
                    arg = r.recognize_google(list_of_args.get(), language=LANG_RU).lower()
                    if arg != "":
                        print(arg)
                        dict_of_new_phrases.update({i: arg})
                        break
                    else:
                        print("Повторите попытку ввода")
    print("Аргументы успешно введены")
    args_flag = True
    return dict_of_new_phrases


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
            current_phrase = r.recognize_google(list_of_audio.get(), language=LANG_RU).lower()

            resoult = standard_phrases(current_phrase, )
            if resoult != " ":
                print(resoult)
                var = requests.post(url, json={PHRASE: resoult})
                send_args = var.json()

                print(send_args)

                new_args = set_args(send_args)

                print(new_args)

                var_redy = requests.post(url_param, json=new_args)
                get_args = var_redy.json()

                print(get_args)

        except Exception as e:
            print(e)
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
