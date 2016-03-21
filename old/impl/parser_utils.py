from enum import Enum
from token import TokenType

class Fun:
    def __init__(self, name, funParams, node, line):
        self.name = name
        self.funParams = funParams
        self.node = node
        self.line = line

    def __str__(self):
        result = self.name + ': '
        result += '['
        for fp in self.funParams:
            result += str(fp) + ', '
        if len(self.funParams) > 0:
            result = result[0:-2]
        result += ']\n'
        result += str(self.node)
        return result

    def __repr__(self):
        return self.__str__()

class FunPar:
    def __init__(self, funParType, value, line):
        self.funParType = funParType
        self.value = value
        self.line = line

    def __str__(self):
        return self.funParType.__str__() + ':' + self.value

    def __repr__(self):
        return self.__str__()

class FunParType(Enum):
    VarName = 0
    FunName = 1

    def __str__(self):
        return self.name[self.name.find('.')+1:]

    def simpleStr(self):
        if self.name == 'VarName':
            return 'Variable'
        elif self.name == 'FunName':
            return 'Function'

class Call:
    def __init__(self, funName, callParams, line):
        self.funName = funName
        self.callParams = callParams
        self.line = line

    def __str__(self):
        result = ' ' + self.funName + '\n'
        i = 1
        for cp in self.callParams:
            result += ' param ' + str(i) + ':\n'
            result += ' ' + str(cp) + '\n'
            i += 1
        return result

    def __repr__(self):
        return self.__str__()

class CallParam:
    def __init__(self, callParType, value, line):
        self.callParType = callParType
        self.value = value
        self.line = line

    def __str__(self):
        return self.callParType.__str__() + ":" + str(self.value)

    def __repr__(self):
        return self.__str__()

class CallParamType(Enum):
    VarName = 0
    FunName = 1
    StructName = 2
    Int = 3
    Float = 4
    String = 5
    Boolean = 6
    NoneVal = 7
    Call = 8

    def __str__(self):
        return self.name[self.name.find('.')+1:]

class Node:
    def __init__(self):
        self.calls = []

    def appendCall(self, call):
        self.calls.append(call)

    def __str__(self):
        result = ''
        i = 1
        for c in self.calls:
            result += ' Call ' + str(i) + ':\n'
            result += str(c) + '\n'
            i += 1
        return result

    def __repr__(self):
        return self.__str__()

def isKeyword(tok, val):
    return tok.tokType == TokenType.Keyword and tok.value == val

def isName(tok):
    return tok.tokType == TokenType.Name

def isOpenBracket(tok):
    return tok.tokType == TokenType.Bracket and tok.value == 'open'

def isCloseBracket(tok):
    return tok.tokType == TokenType.Bracket and tok.value == 'close'

def isFunName(tok):
    return tok.tokType == TokenType.FunName

def isComma(tok):
    return tok.tokType == TokenType.Comma

def isStruct(tok):
    return tok.tokType == TokenType.Struct

def isInt(tok):
    return tok.tokType == TokenType.Int

def isFloat(tok):
    return tok.tokType == TokenType.Float

def isString(tok):
    return tok.tokType == TokenType.String

def isBoolean(tok):
    return tok.tokType == TokenType.Boolean

def isNoneVal(tok):
    return tok.tokType == TokenType.NoneVal