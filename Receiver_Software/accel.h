#ifndef _ACCEL_H
#define _ACCEL_H

#include <stdint.h>

typedef struct{
	int8_t x;
	int8_t y;
	int8_t z;
}ACCEL_TYPE;

void accel_init(void);
void accel_cycle(void);

ACCEL_TYPE accel_getData(void);


#endif