/**
 * @file usart_hal.c
 * @author Simon Vogel
 */
 
 
#ifndef _LED_H
#define _LED_H

#include <stdint.h>

#define LED_NR 120


typedef struct rgb{
	uint8_t r;
	uint8_t g;
	uint8_t b;
}RGB;

/**
 * @brief This function initializes the USART module and the other hardware parts, which are used for the USART transmission and reception.
 */
void led_initialize(void);
void led_setLEDBuffer(uint8_t * pBuffer);
void led_cycle(void);


void led_setLed(uint16_t led_nr, RGB color);
#endif
