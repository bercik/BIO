from binder_utils import *
from error import BindError

class Binder:
    def __init__(self):
        self.standardFunctions = {}
        self.userFunctions = {}


    def bindStandard(self, name, parTypes, func):
        if self.isAlreadyBinded(name):
            raise BindError('Standard function ' + name + \
                ' is already binded')

        bf = BindFunc(name, parTypes, func, True)
        self.standardFunctions[name] = bf

    def bindUser(self, name, parTypes, func):
        if self.isAlreadyBinded(name):
            raise BindError('User function ' + name + \
                ' is already binded')

        bf = BindFunc(name, parTypes, func, False)
        self.userFunctions[name] = bf

    def isAlreadyBinded(self, name):
        return name in self.standardFunctions or name in self.userFunctions

    def getFunctions(self):
        return self.standardFunctions, self.userFunctions