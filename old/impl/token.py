from enum import Enum

class Token:
	def __init__(self, tokType, value, line, ch):
		self.tokType = tokType
		self.value = value
		self.line = line
		self.ch = ch

	def __str__(self):
		return self.tokType.__str__() + ":" + self.value

	def __repr__(self):
		return self.__str__()

class TokenType(Enum):
	Comma = 0 # przecinek
	Bracket = 1 # nawias
	Name = 2 # nazwa (funkcji, zmiennej)
	FunName = 3 # nazwa funkcyjna (zaczyna się od @)
	String = 4 # łańcuch
	Int = 5 # liczba całkowita
	Float = 6 # liczba rzeczywista
	Boolean = 7 # wartość logiczna
	NoneVal = 8 # wartość None
	Struct = 9 # struktura
	Keyword = 10 # słowo kluczowe (np. def, end)

	def __str__(self):
		return self.name[self.name.find('.')+1:]