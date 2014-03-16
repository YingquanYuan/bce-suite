#ifndef BCE_UTILS_H_INCLUDED
#define BCE_UTILS_H_INCLUDED

#include <stdio.h>
#include <stdlib.h>

typedef unsigned char byte;

void print_hex(char c);

void itoa(char *string, int number);

void int_to_bytes(int src, byte *dest);

int bytes_to_int(byte *src);

#endif // BCE_UTILS_H_INCLUDED
