class SyntaxError(Exception):
    def __init__(self, message, token):
        # Call the base class constructor with the
        # parameters it needs
        print(token.ch)
        print(token.value)
        message += str(token) + ' in line ' + str(token.line) +\
			' character ' + str(token.ch - len(token.value))
        super(SyntaxError, self).__init__(message)

class LexicalError(Exception):
    def __init__(self, message):
        # Call the base class constructor with the
        # parameters it needs
        super(LexicalError, self).__init__(message)
