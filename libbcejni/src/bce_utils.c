#include "bce_utils.h"

void print_hex(char c)
{
    byte b = c;
    printf("%02x ", b);
}

void itoa(char *string, int number)
{
    sprintf(string, "%d", number);
}

//int 转为 byte[4]
void int_to_bytes(int src, byte *dest)
{
    dest[0] = (byte) (0xff & src);
    dest[1] = (byte) ((0xff00 & src) >> 8);
    dest[2] = (byte) ((0xff0000 & src) >> 16);
    dest[3] = (byte) ((0xff000000 & src) >> 24);
}

//byte[4] 转为 int
int bytes_to_int(byte *src)
{
    int dest = src[0] & 0xff;
    dest |= ((src[1] << 8) & 0xff00);
    dest |= ((src[2] << 16) & 0xff0000);
    dest |= ((src[3] << 24) & 0xff000000);
    return dest;
}
