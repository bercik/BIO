import math
import sys

def isPrime(num):
    found = True
    for i in range(2, int(math.sqrt(num) + 1)):
        if (num % i == 0):
            found = False
            break

    if found:
        print(num)

MAX_I = 100
if len(sys.argv) > 1:
    MAX_I = int(sys.argv[1])

for i in range(2, MAX_I):
    isPrime(i)

