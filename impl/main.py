import sys

from lexer import Lexer
from parser import Parser
from binder import Binder
from semantic_checker import SemanticChecker
from binder_utils import *
from error import *
from performance import Performance

# some test functions
def start():
    print('start function')
def add(par1, par2, binder):
    print('add function')
    print(par1)
    print(par2)
def assignLocal(par1, par2, binder):
    print('assign local function')
    print(par1)
    print(par2)
def attachToEvent(par1, par2, binder):
    print('attachToEvent function')
    print(par1)
    print(par2)
def returnFun(par1, binder):
    print('return fun')
    print(par1)
class Foos:
    def printFoo(self, par):
        print(par)
    def call(self, par, binder):
        print(par)

def main(argv):
    if len(argv) < 2:
        print('Potrzebuję nazwę skryptu do działania')
        return

    perf = Performance()
    perf.start()
    perf.start('Read data')
    # read data
    with open(sys.argv[1]) as f:
        data = f.read()
    perf.end('Read data')

    perf.start('Lexer')
    lex = Lexer(data)
    perf.end('Lexer')
    tokens = lex.getTokens()
    perf.start('Parser')
    parser = Parser(tokens)
    perf.end('Parser')
    perf.start('Binder')
    binder = Binder()

    # bind some example function
    foos = Foos()
    binder.bindUser('START', (), start)
    binder.bindStandard('ADD', (ParameterType.Value, \
        ParameterType.Value), add)
    binder.bindUser('PRINT', (ParameterType.Value,), foos.printFoo)
    binder.bindStandard('CALL', (ParameterType.FunName,), foos.call)
    binder.bindStandard('ATTACH_TO_EVENT', (ParameterType.RawFunName, \
        ParameterType.RawFunName), attachToEvent)
    binder.bindStandard('RETURN', (ParameterType.Value, ), returnFun)
    binder.bindStandard('ASSIGN_LOCAL', (ParameterType.VarName, \
        ParameterType.Value), assignLocal)
    # end of binding some example functions
    perf.end('Binder')

    perf.start('Semantic checker')
    semanticChecker = SemanticChecker(parser.getFunctions(),\
        *binder.getFunctions())
    perf.end('Semantic checker')
    perf.end()
    perf.printSummary()

    # test print
    i = 1
    scriptFuns, standardFuns, userFuns = semanticChecker.getFunctions()
    for fname, f in scriptFuns.items():
            print('-------------')
            print('Function ' + str(i) + ':')
            print(f)
            i += 1

if __name__ == '__main__':
	try:
		main(sys.argv)
	except (LexicalError, SyntaxError, SemanticError) as ex:
		print('<Error>: ' + str(ex))