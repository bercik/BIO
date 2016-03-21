from enum import Enum

from error import BindError

class Parameter:
    def __init__(self, parType, value):
        self.parType = parType
        self.value = value

    def __str__(self):
        return str(self.parType) + ':' + str(self.value)

class ParameterType(Enum):
    Value = 0 # wartość, typ Field
    Call = 1 # wywołanie funkcji np. PRINT('siems')
    FunName = 2 # nazwa funkcji z @ na początku
    RawFunName = 3 # czysta nazwa funkcji (bez @ na poczatku)
    VarName = 4 # nazwa zmiennej

    def __str__(self):
        return self.name[self.name.find('.')+1:]

    def simpleStr(self):
        if self.name == 'Value':
            return 'Value'
        elif self.name == 'Call':
            return 'Function'
        elif self.name == 'FunName':
            return 'Function name'
        elif self.name == 'VarName':
            return 'Variable name'
        elif self.name == 'RawFunName':
            return 'Raw function name'
        else:
            'DEFAULT VALUE IN PARAMETER TYPE SIMPLE STR'

class Field:
    def __init__(self, name, value, fieldType):
        self.name = name
        self.value = value
        self.fieldType = fieldType

    def __str__(self):
        return '[' + self.name + ']' + str(self.fieldType) + \
            ': ' + str(self.value)

class FieldType(Enum):
    Int = 0
    Float = 1
    String = 2
    Boolean = 3
    NoneVal = 4
    Struct = 5

    def __str__(self):
        return self.name[self.name.find('.')+1:]

Field.NONE = Field('', 'none', FieldType.NoneVal)

class Struct:
    def __init__(self):
        self.fields = {}

    def appendField(self, f):
        self.fields.append(f)

class BindFunc:
    def __init__(self, name, parTypes, func, isStandard):
        # sprawdzamy czy funkcja przyjmuje 
        # odpowiednia liczbe argumentow
        args = func.__code__.co_argcount
        # jeżeli funkcja standardowa to odejmujemy 2 argumenty
        if isStandard:
            args -= 2
        # jeżeli funkcja klasy
        if 'self' in func.__code__.co_varnames:
            args -= 1
        if (len(parTypes) != args):
            raise BindError('Too much or too less arguments ' +\
                'in binded function ' + func.__code__.co_name)
        
        self.name = name
        self.parTypes = parTypes
        self.func = func