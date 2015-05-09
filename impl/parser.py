from parser_utils import *
from token import *
from error import SyntaxError

class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        # create token counter
        self.tokenCounter = 0
        # assign first token
        self.tok = self.nextToken()

        # init functions variable (which contains
        # something like syntax tree)
        self.functions = []
        # create syntax tree
        self.createSyntaxTree()

    def nextToken(self):
        if (self.tokenCounter >= len(self.tokens)):
            return None
        tok = self.tokens[self.tokenCounter]
        self.tokenCounter += 1
        return tok

    def nextTokenWithoutMove(self):
        if (self.tokenCounter >= len(self.tokens)):
            return None
        tok = self.tokens[self.tokenCounter]
        return tok

    def createSyntaxTree(self):
        while not isKeyword(self.tok, '<EOF>'):
            self.function()
        i = 1

    def function(self):
        if (isKeyword(self.tok, 'def')):
            line = self.tok.line
            self.tok = self.nextToken()
            name, params = self.functionDefinition()
            node = self.functionBody()
            f = Fun(name, params, node, line)
            self.functions.append(f)
        else:
            raise SyntaxError('Expected def keyword got ', self.tok)

    def functionBody(self):
        n = Node()
        while not isKeyword(self.tok, 'end'):
            c = self.call()
            n.appendCall(c)

        self.tok = self.nextToken()
        return n

    def call(self):
        if isName(self.tok):
            funName = self.tok.value
            callLine = self.tok.line
            self.tok = self.nextToken()
            if isOpenBracket(self.tok):
                callParams = []
                self.tok = self.nextToken()
                line = self.tok.line
                while not isCloseBracket(self.tok):
                    value = self.tok.value

                    if isName(self.tok):
                        nextTok = self.nextTokenWithoutMove()
                        if isOpenBracket(nextTok):
                            value = self.call()
                            c = CallParam(CallParamType.Call, value, line)
                        else:
                            c = CallParam(CallParamType.VarName, value, line)
                            self.tok = self.nextToken()

                    elif isStruct(self.tok):
                        c = CallParam(CallParamType.StructName, value, line)
                        self.tok = self.nextToken()
                    elif isInt(self.tok):
                        c = CallParam(CallParamType.Int, value, line)
                        self.tok = self.nextToken()
                    elif isFloat(self.tok):
                        c = CallParam(CallParamType.Float, value, line)
                        self.tok = self.nextToken()
                    elif isString(self.tok):
                        c = CallParam(CallParamType.String, value, line)
                        self.tok = self.nextToken()
                    elif isBoolean(self.tok):
                        c = CallParam(CallParamType.Boolean, value, line)
                        self.tok = self.nextToken()
                    elif isNoneVal(self.tok):
                        c = CallParam(CallParamType.NoneVal, value, line)
                        self.tok = self.nextToken()
                    elif isFunName(self.tok):
                        c = CallParam(CallParamType.FunName, value, line)
                        self.tok = self.nextToken()
                    else:
                        raise SyntaxError('Expected variable ' + \
                            'name, struct name, value or ' + \
                            'function call got ', self.tok)

                    callParams.append(c)

                    if not isCloseBracket(self.tok) and \
                        not isComma(self.tok):
                        raise SyntaxError('Expected close bracket ' +\
                            'or comma got ', self.tok)
                    if isCloseBracket(self.tok):
                        break
                    self.tok = self.nextToken()
                self.tok = self.nextToken()

                return Call(funName, callParams, callLine)
            else:
                raise SyntaxError('Expected open bracket got ',\
                    self.tok)
        else:
            raise SyntaxError('Expected name got ', self.tok)

    def functionDefinition(self):
        if isName(self.tok):
            name = self.tok.value
            self.tok = self.nextToken()
            params = self.functionParams()
            return name, params
        else:
            raise SyntaxError('Expected function name got ', self.tok)

    def functionParams(self):
        if isOpenBracket(self.tok):
            params = []
            self.tok = self.nextToken()
            while not isCloseBracket(self.tok):
                params.append(self.functionParam())
                self.tok = self.nextToken()
                if not isComma(self.tok) and not isCloseBracket(self.tok):
                    raise SyntaxError('Expected comma or close bracket ' + \
                        'got ', self.tok)
                if isCloseBracket(self.tok):
                    break
                self.tok = self.nextToken()
            self.tok = self.nextToken()
            return params
        else:
            raise SyntaxError('Expected open bracket got ', self.tok)

    def functionParam(self):
        line = self.tok.line
        if isName(self.tok):
            funParType = FunParType.VarName
            return FunPar(funParType, self.tok.value, line)
        elif isFunName(self.tok):
            funParType = FunParType.FunName
            return FunPar(funParType, self.tok.value, line)
        else:
            raise SyntaxError('Expected name or function name got ', \
                self.tok)

    def getFunctions(self):
        return self.functions