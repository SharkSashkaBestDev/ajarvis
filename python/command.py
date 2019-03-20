class Command:    
    def __init__(self, kwargs):
        self.data = kwargs

    def execute(self):
        raise NotImplementedError("You should override this method")
    