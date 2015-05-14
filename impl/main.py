import sys
import builtins

from lexer import Lexer
from parser import Parser
from binder import Binder
from semantic_checker import SemanticChecker
from binder_utils import *
from error import *
from performance import Performance
from standard_lib import StdLib
from interpreter import Interpreter

def main(argv):
    if len(argv) < 2:
        print('Potrzebuję nazwę skryptu do działania')
        return

    perf = Performance()
    builtins.perf = perf
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
    # print(tokens)
    perf.start('Parser')
    parser = Parser(tokens)
    perf.end('Parser')
    perf.start('Binder')
    binder = Binder()

    # bind some example function
    binder.bindStandard('CALL', (ParameterType.Call,), StdLib.callFun)
    binder.bindStandard('PRINT', (ParameterType.Value,), StdLib.printFun)
    binder.bindStandard('ASSIGN_LOCAL', (ParameterType.VarName, \
        ParameterType.Value), StdLib.assignLocal)
    binder.bindStandard('RETURN', (ParameterType.Value,), StdLib.returnFun)
    # end of binding some example functions
    perf.end('Binder')

    perf.start('Semantic checker')
    semanticChecker = SemanticChecker(parser.getFunctions(),\
        *binder.getFunctions())
    perf.end('Semantic checker')

    # initialize and run interpreter
    perf.start('Interpreter')
    interpreter = Interpreter(*semanticChecker.getFunctions())
    interpreter.start()
    perf.end('Interpreter')

    perf.end()
    perf.printSummary()

    # test print
    # i = 1
    # scriptFuns, standardFuns, userFuns = semanticChecker.getFunctions()
    # for fname, f in scriptFuns.items():
    #         print('-------------')
    #         print('Function ' + str(i) + ':')
    #         print(f)
    #         i += 1

if __name__ == '__main__':
	try:
		main(sys.argv)
	except (LexicalError, SyntaxError, SemanticError) as ex:
		print('<Error>: ' + str(ex))