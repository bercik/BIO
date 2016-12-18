import math
import sys

def isPrime(num):
    found = True
    for i in range(2, int(math.sqrt(num) + 1)):
        if (num % i == 0):
            found = False
            break

    return found

MAX_I = 10000
if len(sys.argv) > 1:
    MAX_I = int(sys.argv[1])
    primes = 0

for i in range(2, MAX_I):
    if isPrime(i):
        primes += 1

print(primes)
