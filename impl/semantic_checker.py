import operator

from error import SemanticError
from parser_utils import Call, CallParam, CallParamType

class SemanticChecker:
    def __init__(self, scriptFuns, standardFuns, userFuns):
        self.scriptFuns = scriptFuns
        self.standardFuns = standardFuns
        self.userFuns = userFuns

        self.checkForMultipleDefinitions()
        self.checkForSameParametersName()
        self.checkForReturnAtTheEndOfFunction()

    def checkForReturnAtTheEndOfFunction(self):
        for f in self.scriptFuns:
            calls = f.node.calls
            if len(calls) == 0 or calls[len(calls)-1].funName != 'RETURN':
                callParam = CallParam(CallParamType.NoneVal, '', -1)
                call = Call('RETURN', (callParam, ), -1)
                calls.append(call)

    def checkForSameParametersName(self):
        for f in self.scriptFuns:
            sortedParams = sorted(f.funParams, key = \
                operator.attrgetter('value'))
            for i in range(0, len(sortedParams)-1):
                pname1 = sortedParams[i].value
                pname2 = sortedParams[i+1].value
                if pname1 == pname2:
                    msg = 'Same parameters name ' + pname1 + \
                        ' in function ' + f.name + ' in line ' + \
                        str(f.line)
                    raise SemanticError(msg)

    def checkForMultipleDefinitions(self):
        # binder zapewnia, że userFuns i standardFuns są unikalne 
        # w swoich zbiorach. Trzeba sprawdzić, czy żadna z funkcji
        # scriptFuns nie powtarza się i nie jest taka sama jak
        # userFuns lub standardFuns
        self.scriptFuns.sort(key = operator.attrgetter('name'))
        # check if is in standard functions
        fname = self.scriptFuns[0].name
        if fname in self.standardFuns:
            msg = 'Function ' + fname + ' in line ' + \
                str(self.scriptFuns[0].line) + ' already' + \
                ' binded as standard function'
            raise SemanticError(msg)
        # check if is in user functions
        if fname in self.userFuns:
            msg = 'Function ' + fname + ' in line ' + \
                str(self.scriptFuns[0].line) + ' already' + \
                ' binded as user function'
            raise SemanticError(msg)

        for i in range(0, len(self.scriptFuns)-1):
            f1name = self.scriptFuns[i].name
            f2name = self.scriptFuns[i+1].name

            # check if is in standard functions
            if f2name in self.standardFuns:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(self.scriptFuns[i+1].line) + ' already' + \
                    ' binded as standard function'
                raise SemanticError(msg)
            # check if is in user functions
            if f2name in self.userFuns:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(self.scriptFuns[i+1].line) + ' already' + \
                    ' binded as user function'
                raise SemanticError(msg)
            # check if is in script functions   
            if f1name == f2name:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(self.scriptFuns[i+1].line) + ' already defined ' + \
                    'in line ' + str(self.scriptFuns[i].line)
                raise SemanticError(msg)