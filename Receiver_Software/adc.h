#ifndef _ADC_H
#define _ADC_H

#include <stdint.h>


void adc_initialize(void);


void adc_cycle(void);

uint8_t adc_getID(void);

uint16_t adc_getBattery(void);

#endif