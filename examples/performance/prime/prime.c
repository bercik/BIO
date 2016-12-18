#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#define TRUE 1
#define FALSE 0

int isPrime(int num)
{
   if (num == 2)
   {
      return TRUE;
   }

   int found = TRUE;
   for (int i = 2; i <= sqrt(num) + 1; ++i)
   {
      if (num % i == 0)
      {
         found = FALSE;
         break;
      }
   }

   return found;
}

int main(int argc, char **argv)
{
   int MAX_I = 100;
   int primes = 0;

   if (argc > 1)
   {
      MAX_I = atoi(argv[1]);
   }

   for (int i = 2; i < MAX_I; ++i)
   {
      if (isPrime(i))
      {
         ++primes;
      }
   }

   printf("%d\n", primes);
}
