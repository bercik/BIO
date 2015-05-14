from binder_utils import *
from parser_utils import *

class StdLib:
    # funkcja rekursywnie wywołująca wywołanie funkcji
    def callFun(interpreter, localVars, call):
        fn = call.funName
        # print('fn: ' + fn)
        if fn in interpreter.standardFuns:
            # lista parametrów funkcji
            params = []
            for cp, fpt in zip(call.callParams, \
                interpreter.standardFuns[fn].parTypes):
                cpt = cp.callParType
                # print(fpt)
                # print(cpt)
                
                parType = fpt
                # special case
                if fpt == ParameterType.Value:
                    # special case
                    if cpt == CallParamType.VarName:
                        value = localVars[cp.value]
                    else:
                        fieldType = StdLib.assignCallParamTypeToFieldType(cpt)
                        fieldValue = cp.value
                        value = Field('', fieldValue, fieldType)
                else:
                    value = cp.value

                params.append(Parameter(parType, value))
            # call function with proper parameters
            returnVal = interpreter.standardFuns[fn].func(interpreter, \
                localVars, *params)
            # jeżeli funkcja zwróciła wartość to zwracamy do góry
            if returnVal != None:
                return True
            else:
                return False
            # test print
            # i = 1
            # for p in params:
            #     print(str(i) + ': ' + str(p))
            #     i += 1

    def assignCallParamTypeToFieldType(cpt):
        if cpt == CallParamType.Int:
            return FieldType.Int

    def printFun(interpreter, localVars, par):
        print(par.value.value)
        return None

    def assignLocal(interpreter, localVars, varName, value):
        localVars[varName.value] = value.value
        return None

    def returnFun(interpreter, localVars, par):
        return par

    def attachToEvent(interpreter, localVars, eventName, funName):
        return None