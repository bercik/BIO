#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#define TRUE 1
#define FALSE 0

void isPrime(int num)
{
   int found = TRUE;
   for (int i = 2; i <= sqrt(num) + 1; ++i)
   {
      if (num % i == 0)
      {
         found = FALSE;
         break;
      }
   }

   if (found)
   {
      printf("%d\n", num);
   }
}

int main(int argc, char **argv)
{
   int MAX_I = 100;

   if (argc > 1)
   {
      MAX_I = atoi(argv[1]);
   }

   for (int i = 2; i < MAX_I; ++i)
   {
      isPrime(i);
   }
}
