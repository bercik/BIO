from binder_utils import *
from stdlib.error import BadParamsError

class Arithmetic:
    def add(par1, par2):
        field1 = par1.value
        field2 = par2.value
        par1type = field1.fieldType
        par2type = field2.fieldType
        if par1type == par2type:
            fieldType = par1type
            if par1type == FieldType.Int:
                fieldValue = int(field1.value) + int(field2.value)
            elif par1type == FieldType.Float:
                fieldValue = float(field1.value) + float(field2.value)
            elif par1type == FieldType.String:
                fieldValue = field1.value + field2.value
            else:
                msg = 'Can\' add variables of type ' + str(par1type)
                raise BadParamsError(msg)
        else:
            msg = 'Can\'t add two variables of diffrent types: ' + \
                str(par1type) + ' and ' + str(par2type)
            raise BadParamsError(msg)
        return Field('', fieldValue, fieldType)