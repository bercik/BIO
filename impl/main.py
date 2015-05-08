import sys

from lexer import Lexer
from parser import Parser
from error import *

def main(argv):
	if len(argv) < 2:
		print('Potrzebuję nazwę skryptu do działania')
		return
	
	lex = Lexer(argv[1])
	tokens = lex.getTokens()
	parser = Parser(tokens)

if __name__ == '__main__':
	try:
		main(sys.argv)
	except (LexicalError, SyntaxError) as ex:
		print('<Error>: ' + str(ex))