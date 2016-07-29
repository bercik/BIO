import math

def isPrime(num):
    found = True
    for i in range(2, int(math.sqrt(num) + 1)):
        if (num % i == 0):
            found = False
            break

    if found:
        print(num)

MAX_I = 100
for i in range(2, MAX_I):
    isPrime(i)


