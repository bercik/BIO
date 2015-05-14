from token import Token
from token import TokenType
from error import SyntaxError
from error import LexicalError

import re
import sys
import builtins

def static_vars(**kwargs):
    def decorate(func):
        for k in kwargs:
            setattr(func, k, kwargs[k])
        return func
    return decorate

class Lexer:
	ignoreCharacters = ('\r', '\n', '\t', ' ')
	comment = ('%')
	brackets = ('(', ')')
	comma = (',')
	keywords = ('def', 'end', '<EOF>')
	booleanValues = ('true', 'false')
	funPrefix = '@'

	def __init__(self, data):
		# init
		self.tokens = []
		self.data = data
		self.line = 1
		self.ch = 1

		# attach <EOF> keyword to the end of file
		self.data += '\n<EOF> '

		# analyze data
		self.analyze()

	def analyze(self):
		tok = ''
		isStr = False
		isComment = False
		specialChar = False
		self.line = 1
		self.ch = 1

		for ch in self.data:
			if ch != '\n':
				self.ch += 1
			
			if isComment:
				if ch == '\n':
					isComment = False
			elif specialChar:
				if ch == 'n':
					tok += '\n'
				elif ch == 'r':
					tok += '\r'
				elif ch == 't':
					tok += '\t'
				elif ch == '\\':
					tok += '\\'
				elif ch == '\"':
					tok += '\"'
				elif ch == '\'':
					tok += '\''
				else:
					msg = 'Urecognized special character ' + \
						'\\' + ch + ' in line ' + str(self.line) + \
						' character ' + str(self.ch)
					raise LexicalError(msg)
				specialChar = False
			elif isStr and ch == '\\':
				specialChar = True
			elif isStr and ch != '\"':
				tok += ch
			elif ch in Lexer.comma:
				self.recognizeToken(tok)
				tok = ''
				self.addCommaToken()
			elif ch in Lexer.comment:
				isComment = True
				self.recognizeToken(tok)
				tok = ''
			elif ch in Lexer.brackets:
				self.recognizeToken(tok)
				tok = ''
				self.recognizeBracket(ch)
			elif ch in Lexer.ignoreCharacters:
				self.recognizeToken(tok)
				tok = ''
			elif ch == '\"':
				if isStr:
					self.addStringToken(tok)
					isStr = False
					tok = ''
				else:
					isStr = True
			else:
				tok += ch

			if ch == '\n':
				self.ch = 1
				self.line += 1
		if isStr:
			msg = 'EOF while scanning string literal ' + \
				'(propably you don\'t put closing quote)'
			raise LexicalError(msg)

	def addStringToken(self, string):
		self.tokens.append(Token(TokenType.String, string, \
			self.line, self.ch))

	def addCommaToken(self):
		self.tokens.append(Token(TokenType.Comma, "", self.line, self.ch))

	def recognizeToken(self, tok):
		if tok != '':
			if tok in Lexer.keywords:
				t = Token(TokenType.Keyword, tok, self.line, self.ch)
				self.tokens.append(t)
			elif tok in Lexer.booleanValues:
				t = Token(TokenType.Boolean, tok, self.line, self.ch)
				self.tokens.append(t)
			elif self.recognizeNone(tok):
				pass
			elif self.recognizeInt(tok):
				pass
			elif self.recognizeFloat(tok):
				pass
			elif self.recognizeFunName(tok):
				pass
			elif self.recognizeName(tok):
				pass
			elif self.recognizeStruct(tok):
				pass
			else:
				msg = 'Nie udało rozpoznać się ' + tok + \
					' w lini ' + str(self.line) + \
					' znak ' + str(self.ch - len(tok))
				raise LexicalError(msg)

	@static_vars(p = re.compile('^[\-]?[1-9][0-9]*$|^[0]*$'))
	def recognizeInt(self, tok):
		m = self.recognizeInt.p.match(tok)
		if m != None:
			t = Token(TokenType.Int, tok, self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	@static_vars(p = re.compile('^[\-]?([0]|[1-9][0-9]*)[\.][0-9]+$'))
	def recognizeFloat(self, tok):
		m = self.recognizeFloat.p.match(tok)
		if m != None:
			t = Token(TokenType.Float, tok, self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	def recognizeNone(self, tok):
		if tok == 'None':
			t = Token(TokenType.NoneVal, '', self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	@static_vars(p = re.compile('^(([a-zA-Z_][a-zA-Z_0-9]*)\.)+' + \
			'([a-zA-Z_][a-zA-Z_0-9]*)$'))
	def recognizeStruct(self, tok):
		m = self.recognizeStruct.p.match(tok)
		if m != None:
			t = Token(TokenType.Struct, tok, self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	def recognizeFunName(self, tok):
		if tok.startswith(Lexer.funPrefix) \
			and Lexer.isName(tok[1:]):
			t = Token(TokenType.FunName, tok[1:], self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	def recognizeName(self, tok):
		if Lexer.isName(tok):
			t = Token(TokenType.Name, tok, self.line, self.ch)
			self.tokens.append(t)
			return True
		else:
			return False

	@static_vars(p = re.compile('^[a-zA-Z_][a-zA-Z_0-9]*$'))
	def isName(tok):
		m = Lexer.isName.p.match(tok)
		return m != None

	def recognizeBracket(self, brack):
		if brack == '(':
			tokenValue = 'open'
		elif brack == ')':
			tokenValue = 'close'

		t = Token(TokenType.Bracket, tokenValue, self.line, self.ch)
		self.tokens.append(t)

	def getTokens(self):
		return self.tokens

if __name__ == '__main__':
	if len(sys.argv) < 2:
		print('Potrzebuję nazwę skryptu do działania')
	else:
		lex = Lexer(sys.argv[1])
		print(lex.getTokens())