#include <float.h>

#include "mathl.h"

/* To compute the integer part of X, sum a big enough
   integer so that the precision of the floating point
   number is exactly 1.  */

long
lrintf(float x)
{
  float y;
  if (x < 0.0L)
    {
      y = -(1.0L / FLT_EPSILON - x - 1.0 / FLT_EPSILON);
      if (y < x)
        y = y + 1.0L;
    }
  else
    {
      y = 1.0L / FLT_EPSILON + x - 1.0 / FLT_EPSILON;
      if (y > x)
        y = y - 1.0L;
    }

  return (long) y;
}