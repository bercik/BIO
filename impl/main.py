import sys

from lexer import Lexer
from parser import Parser
from binder import Binder
from semantic_checker import SemanticChecker
from binder_utils import *
from error import *

# some test functions
def start():
    print('start function')
def add(par1, par2, binder):
    print('add function')
    print(par1)
    print(par2)
def attachToEvent(par1, par2, binder):
    print('attachToEvent function')
    print(par1)
    print(par2)
class Foos:
    def printFoo(self, par):
        print(par)
    def call(self, par, binder):
        print(par)

def main(argv):
    if len(argv) < 2:
        print('Potrzebuję nazwę skryptu do działania')
        return

    lex = Lexer(argv[1])
    tokens = lex.getTokens()
    parser = Parser(tokens)
    binder = Binder()
    
    # bind some example function
    foos = Foos()
    binder.bindUser('START', (), start)
    binder.bindStandard('ADD', (ParameterType.Field, \
        ParameterType.Field), add)
    binder.bindUser('PRINT', (ParameterType.Field,), foos.printFoo)
    binder.bindStandard('CALL', (ParameterType.Call,), foos.call)
    binder.bindStandard('ATTACH_TO_EVENT', (ParameterType.FunName, \
        ParameterType.FunName), attachToEvent)
    # end of binding some example functions

    semanticChecker = SemanticChecker(parser.getFunctions(),\
        *binder.getFunctions())

    # test print
    i = 1
    for f in parser.getFunctions():
            print('Function ' + str(i) + ':')
            print(f)
            i += 1

if __name__ == '__main__':
	try:
		main(sys.argv)
	except (LexicalError, SyntaxError, SemanticError) as ex:
		print('<Error>: ' + str(ex))