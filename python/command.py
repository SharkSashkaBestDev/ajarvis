class Command:    
    def __init__(self, kwargs):
        self.kwargs = kwargs

    def execute(self):
        raise NotImplementedError("You should override this method")
    