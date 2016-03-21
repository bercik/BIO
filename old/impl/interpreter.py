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
    def runFun(self, funName, **params):
        localVars = params
        for call in self.scriptFuns[funName].node.calls:
            returnVal, localVars = self.runFunCall(call, **localVars)
            if call.funName == 'RETURN':
                return returnVal

    def runFunCall(self, call, **params):
        # słownik string:Field
        localVars = params
        returnVal = self.standardFuns['CALL'].func(self, localVars, call)
        return returnVal, localVars

    def start(self):
        self.runFun('onSTART')