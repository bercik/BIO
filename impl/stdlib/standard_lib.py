from binder_utils import *
from parser_utils import *
from error import InterpreterError
from stdlib.arithmetic import *

class StdLib:
    def bindStandardFunctions(binder):
        binder.bindStandard('CALL', (ParameterType.Call,), StdLib.callFun)
        binder.bindStandard('PRINT', (ParameterType.Value,), StdLib.printFun)
        binder.bindStandard('ASSIGN_LOCAL', (ParameterType.VarName, \
            ParameterType.Value), StdLib.assignLocal)
        binder.bindStandard('ASSIGN_GLOBAL', (ParameterType.VarName, \
            ParameterType.Value), StdLib.assignGlobal)
        binder.bindStandard('RETURN', (ParameterType.Value,), \
            StdLib.returnFun)
        binder.bindUser('ADD', (ParameterType.Value, ParameterType.Value), \
            Arithmetic.add)

    # funkcja rekursywnie wywołująca wywołanie funkcji
    def callFun(interpreter, localVars, call):
        fn = call.funName
        standardFun = userFun = False
        if fn in interpreter.standardFuns:
            fun = interpreter.standardFuns[fn]
            standardFun = True
        elif fn in interpreter.userFuns:
            fun = interpreter.userFuns[fn]
            userFun = True
        else:
            fun = interpreter.scriptFuns[fn]

        if standardFun or userFun:
            return StdLib.callStandardUserFun(interpreter, localVars, \
                call, fun, standardFun)
        else:
            return StdLib.callScriptFun(interpreter, \
                localVars, call, fun)

    def callScriptFun(interpreter, localVars, call, fun):
        # słownik string:value parametrów funkcji
        params = {}
        for cp, fp in zip(call.callParams, fun.funParams):
            if fp.funParType == FunParType.VarName:
                if cp.callParType == CallParamType.VarName:
                    value = StdLib.getVar(cp.value, localVars, \
                        interpreter.globalVars, cp.line)
                    varName = fp.value
                elif cp.callParType == CallParamType.StructName:
                    # TODO
                    pass
                elif cp.callParType == CallParamType.Call:
                    print(cp.funName)
                    returnVal = interpreter.runFun(cp.funName)
                    pass
                # zwykład wartość
                else:
                    fieldValue = cp.value
                    fieldType = \
                        StdLib.assignCallParamTypeToFieldType(cp.callParType)
                    varName = fp.value
                    value = Field('', fieldValue, fieldType)

                params[varName] = value
        return interpreter.runFun(call.funName, **params)

    def callStandardUserFun(interpreter, localVars, call, fun, isStandard):
        # lista parametrów funkcji
        params = []
        for cp, fpt in zip(call.callParams, \
            fun.parTypes):
            cpt = cp.callParType
            # print(fpt)
            # print(cpt)
            
            parType = fpt
            # special case
            if fpt == ParameterType.Value:
                # special case
                if cpt == CallParamType.VarName:
                    value = StdLib.getVar(cp.value, localVars, \
                        interpreter.globalVars, cp.line)
                elif cpt == CallParamType.Call:
                    #print(cp.value)
                    returnVal, localVars = interpreter.runFunCall(cp.value, \
                        **localVars)
                    value = returnVal
                else:
                    fieldType = StdLib.assignCallParamTypeToFieldType(cpt)
                    fieldValue = cp.value
                    value = Field('', fieldValue, fieldType)
            else:
                value = cp.value

            params.append(Parameter(parType, value))

        # call function with proper parameters
        if isStandard:
            returnVal = fun.func(interpreter, localVars, *params)
        else:
            returnVal = fun.func(*params)
        return returnVal
        # test print
        # i = 1
        # for p in params:
        #     print(str(i) + ': ' + str(p))
        #     i += 1

    def getVar(varName, localVars, globalVars, line):
        if varName in localVars:
            return localVars[varName]
        elif varName in globalVars:
            return globalVars[varName]
        else:
            msg = 'Variable ' + varName + ' is not defined' + \
                ' in line ' + str(line)
            raise InterpreterError(msg)

    def assignCallParamTypeToFieldType(cpt):
        if cpt == CallParamType.Int:
            return FieldType.Int
        elif cpt == CallParamType.Float:
            return FieldType.Float
        elif cpt == CallParamType.String:
            return FieldType.String
        elif cpt == CallParamType.Boolean:
            return FieldType.Boolean
        elif cpt == CallParamType.NoneVal:
            return FieldType.NoneVal

    def printFun(interpreter, localVars, par):
        print(par.value.value)
        return Field.NONE

    def assignLocal(interpreter, localVars, varName, value):
        localVars[varName.value] = value.value
        return Field.NONE

    def returnFun(interpreter, localVars, par):
        return par.value

    def assignGlobal(interpreter, localVars, varName, value):
        interpreter.globalVars[varName.value] = value.value
        return Field.NONE

    def add(par1, par2):
        return par1.value.value + par2.value.value

    def attachToEvent(interpreter, localVars, eventName, funName):
        # TODO
        return Field.NONE