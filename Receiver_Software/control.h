/**
 * @file usart_hal.c
 * @author Simon Vogel
 * @author Ueli Schön
 */
 
 
#ifndef _CONTROL_H
#define _CONTROL_H

#include <stdint.h>

#define ANGLE_TOLERANCE     5   
#define DETECTION_INTERVAL  (1*20)  // Time position needts to be up upright
#define TIME_TO_CHOOSE      10      // Time you spend in color choosing mode in Seconds
#define SWITCH_LEVEL        -7      // Quantifies intensity of mode switch shake
#define MIN_BAT_mV          3100    // Min Batterie Voltage over 3 cells

void control_init(void);
void control_cycle(void);

#endif
