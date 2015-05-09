from enum import Enum

class Parameter:
    def __init__(self, parType, value):
        self.parType = parType
        self.value = value

    def __str__(self):
        return str(self.parType) + ':' + str(self.value)

class ParameterType(Enum):
    Field = 0
    Call = 1
    FunName = 2

    def __str__(self):
        return self.name[self.name.find('.')+1:]

class Field:
    def __init__(self, name, value, fieldType):
        self.name = name
        self.value = value
        self.fieldType = fieldType

class FieldType(Enum):
    Int = 0
    Float = 1
    String = 2
    Boolean = 3
    NoneVal = 4
    Struct = 5

class Struct:
    def __init__(self):
        self.fields = []

    def appendField(self, f):
        self.fields.append(f)

class BindFunc:
    def __init__(self, name, parTypes, func, isStandard):
        # sprawdzamy czy funkcja przyjmuje 
        # odpowiednia liczbe argumentow
        args = func.__code__.co_argcount
        # jeżeli funkcja standardowa (czyli mająca dodatkowy parametr binder)
        if isStandard:
            args -= 1
        # jeżeli funkcja klasy
        if 'self' in func.__code__.co_varnames:
            args -= 1
        if (len(parTypes) != args):
            raise BindError('Too much or too less arguments ' +\
                'in binded function ' + func.__code__.co_name)
        
        self.name = name
        self.parTypes = parTypes
        self.func = func