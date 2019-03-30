import speech_recognition as sr
from threading import Thread


def listen():
    with sr.Microphone() as source:
        global life_listen
        print("Я слушаю!")
        while life_listen:
            aud = r.listen(source)
            list_of_audio.append(aud)


def recognize():
    pause_flag = True
    global life_recognize
    while life_recognize:
        for i in range(len(list_of_audio)):
            try:
                current_phrase = r.recognize_google(list_of_audio[0], language="ru-RU")

                if "остановить" in current_phrase.split(" ") and pause_flag:
                    pause_flag = False
                    print("Распознавание приостановлено!")

                if pause_flag:
                    print("You said: " + current_phrase)

            except Exception as e:
                print(e)
            finally:
                del list_of_audio[0]


th_1 = Thread(target = listen)
th_2 = Thread(target = recognize)
global life_listen, life_recognize
life_listen = True
life_recognize = True


if __name__=="__main__":
    r = sr.Recognizer()
    list_of_audio = []

    th_1.start(), th_2.start()
    th_1.join(), th_2.join()