class Interpreter:
    def __init__(self, scriptFuns, standardFuns, userFuns):
        self.scriptFuns = scriptFuns
        self.standardFuns = standardFuns
        self.userFuns = userFuns

        # słownik string:Field
        self.globalVars = {}
        # słownik string:string list
        self.events = {}

    # run function in script
    def runFun(self, funName):
        # słownik string:Field
        localVars = {}
        for call in self.scriptFuns[funName].node.calls:
            if self.standardFuns['CALL'].func(self, localVars, call):
                break

    def start(self):
        self.runFun('onSTART')