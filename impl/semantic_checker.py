import operator

from error import SemanticError
from parser_utils import Call, CallParam, CallParamType
from parser_utils import FunParType
from binder_utils import ParameterType

class SemanticChecker:
    def __init__(self, scriptFuns, standardFuns, userFuns):
        self.scriptFuns = scriptFuns
        self.standardFuns = standardFuns
        self.userFuns = userFuns

        self.checkForMultipleDefinitions()
        self.convertScriptFunctionsListToDictionary()
        self.checkForOnStartMethod()
        self.checkForSameParametersName()
        self.checkForReturnAtTheEndOfFunction()
        self.checkForProperFunctionsArguments()

    def checkForProperFunctionsArguments(self):
        for fname, f in self.scriptFuns.items():
            for call in f.node.calls:
                self.checkCall(call, f)

    def checkCall(self, call, fun):
        # check this call
        if self.checkCallScriptFuns(call):
            pass
        elif self.checkCallUserFuns(call, fun):
            pass
        elif self.checkCallStandardFuns(call, fun):
            pass
        else:
            msg = 'Call ' + call.funName + ' in line ' + \
                str(call.line) + '. Function is not defined'
            raise SemanticError(msg)

        # check another call in this call parameters
        for callParam in call.callParams:
            if callParam.callParType == CallParamType.Call:
                self.checkCall(callParam.value, fun)

    def checkCallScriptFuns(self, call):
        if call.funName in self.scriptFuns:
            scriptFun = self.scriptFuns[call.funName]
            # check if number of parameters agrees
            if len(call.callParams) != len(scriptFun.funParams):
                msg = 'Too much or too less arguments in ' + \
                    call.funName + ' call in line ' + str(call.line)
                raise SemanticError(msg)
            # check if type of parameters agrees
            i = 1
            for funParam, callParam in \
                zip(scriptFun.funParams, call.callParams):
                if not self.checkIfParamsAreTheSame(funParam, callParam):
                    msg = 'Parameter ' + str(i) + ' in call ' + \
                        call.funName + ' in line ' + str(call.line) + \
                        ' is of type ' + str(callParam.callParType) + \
                        ', but should be of type ' + \
                        funParam.funParType.simpleStr()
                    raise SemanticError(msg)
                i += 1
            return True
        return False

    def checkIfParamsAreTheSame(self, funParam, callParam):
        funParType = funParam.funParType
        callParType = callParam.callParType
        if funParType == FunParType.VarName:
            return callParType != CallParamType.FunName
        elif funParType == FunParType.FunName:
            return callParType == CallParamType.Call
        else:
            return True

    def checkCallUserFuns(self, call, fun):
        if call.funName in self.userFuns:
            self.checkCallBindFuns(call, self.userFuns, fun)
            return True
        else:
            return False

    def checkCallStandardFuns(self, call, fun):
        if call.funName in self.standardFuns:
            self.checkCallBindFuns(call, self.standardFuns, fun)
            return True
        else:
            return False

    def checkCallBindFuns(self, call, bindFuns, fun):
        bindFun = bindFuns[call.funName]
        # check if number of parameters agrees
        if len(call.callParams) != len(bindFun.parTypes):
            msg = 'Too much or too less arguments in ' + \
                call.funName + ' call in line ' + str(call.line)
            raise SemanticError(msg)
        # check if type of parameters agrees
        i = 1
        for funParam, callParam in \
            zip(bindFun.parTypes, call.callParams):
            if not self.checkIfParamsAreTheSame2(funParam, callParam,\
                call, fun):
                msg = 'Parameter ' + str(i) + ' in call ' + \
                    call.funName + ' in line ' + str(call.line) + \
                    ' is of type ' + str(callParam.callParType) + \
                    ', but should be of type ' + \
                    funParam.simpleStr()
                raise SemanticError(msg)
            i += 1
        return True

    def checkIfParamsAreTheSame2(self, funParam, callParam, call, fun):
        callParType = callParam.callParType
        if funParam == ParameterType.Value:
            return callParType != CallParamType.FunName
        elif funParam == ParameterType.VarName:
            return callParType == CallParamType.VarName or \
                callParType == CallParamType.StructName
        elif funParam == ParameterType.Call:
            return callParType == CallParamType.Call
        elif funParam == ParameterType.FunName:
            if callParType == CallParamType.FunName:
                # check if fun name is in the fun parameters
                funNames = []
                for fp in fun.funParams:
                    if fp.funParType == FunParType.FunName:
                        funNames.append(fp.value)
                callFunName = callParam.value
                if callFunName in funNames:
                    return True
                else:
                    msg = 'Function name @' + callFunName + \
                        ' in call ' + call.funName + ' in line ' + \
                        str(call.line) + ' doesn\'t exist in ' + \
                        'function parameters'
                    raise SemanticError(msg)
            else:
                return False
        elif funParam == ParameterType.RawFunName:
            if callParType == CallParamType.VarName:
                callFunName = callParam.value
                if callFunName in self.scriptFuns or \
                    callFunName in self.userFuns or \
                    callFunName in self.standardFuns:
                    return True
                else:
                    msg = 'Parameter raw function name in call ' + \
                        call.funName + ' in line ' + str(call.line) + \
                        '. Function ' + callFunName + ' is not defined'
                    raise(SemanticError(msg))
            else:
                return False

    def convertScriptFunctionsListToDictionary(self):
        scriptFunsDict = {}
        for f in self.scriptFuns:
            scriptFunsDict[f.name] = f
        self.scriptFuns = scriptFunsDict

    def checkForOnStartMethod(self):
        if not 'onSTART' in self.scriptFuns:
            msg = 'Couldn\'t find onSTART method. You should define one'
            raise SemanticError(msg)

    def checkForReturnAtTheEndOfFunction(self):
        for fname, f in self.scriptFuns.items():
            calls = f.node.calls
            if len(calls) == 0 or calls[len(calls)-1].funName != 'RETURN':
                callParam = CallParam(CallParamType.NoneVal, '', -1)
                call = Call('RETURN', (callParam, ), -1)
                calls.append(call)

    def checkForSameParametersName(self):
        for fname, f in self.scriptFuns.items():
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
        sortedScriptFuns = sorted(self.scriptFuns, \
            key = operator.attrgetter('name'))
        # check if is in standard functions
        fname = sortedScriptFuns[0].name
        if fname in self.standardFuns:
            msg = 'Function ' + fname + ' in line ' + \
                str(sortedScriptFuns[0].line) + ' already' + \
                ' binded as standard function'
            raise SemanticError(msg)
        # check if is in user functions
        if fname in self.userFuns:
            msg = 'Function ' + fname + ' in line ' + \
                str(sortedScriptFuns[0].line) + ' already' + \
                ' binded as user function'
            raise SemanticError(msg)

        for i in range(0, len(sortedScriptFuns)-1):
            f1name = sortedScriptFuns[i].name
            f2name = sortedScriptFuns[i+1].name

            # check if is in standard functions
            if f2name in self.standardFuns:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(sortedScriptFuns[i+1].line) + ' already' + \
                    ' binded as standard function'
                raise SemanticError(msg)
            # check if is in user functions
            if f2name in self.userFuns:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(sortedScriptFuns[i+1].line) + ' already' + \
                    ' binded as user function'
                raise SemanticError(msg)
            # check if is in script functions   
            if f1name == f2name:
                msg = 'Function ' + f2name + ' in line ' + \
                    str(sortedScriptFuns[i+1].line) + ' already defined ' + \
                    'in line ' + str(sortedScriptFuns[i].line)
                raise SemanticError(msg)

    def getFunctions(self):
        return self.scriptFuns, self.standardFuns, self.userFuns