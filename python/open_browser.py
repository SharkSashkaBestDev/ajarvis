import webbrowser


def open_browser(data):
    print("Open browser")
    webbrowser.open_new("http://google.com")
    return data


open_browser(data)