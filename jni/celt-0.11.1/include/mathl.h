#ifndef GST_MATHL_H
#define GST_MATHL_H

extern long double acosl(long double x);
extern long double asinl (long double x);
extern long double atanl (long double x);
extern long double ceill(long double x);
extern long double cosl(long double x);
extern long double expl (long double x);
extern long double floorl(long double x);
extern long double frexpl(long double x, int *exp);
extern long double ldexpl(long double x, int exp);
extern long double logl(long double x);
extern long double sinl (long double x);
extern long double sqrtl(long double x);
extern long double tanl (long double x);
extern long double truncl(long double x);

extern double trunc(double x);
extern float truncf(float x);

extern long lrintl(long double x);
extern long lrint(double x);
extern long lrintf(float x);

#endif