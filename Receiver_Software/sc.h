#ifndef _SC_H
#define _SC_H

#include "stdint.h"

#define CYCLE_DURATION 40000u


void sc_initialize(void);
uint8_t sc_waitForCycle(void);

#endif
