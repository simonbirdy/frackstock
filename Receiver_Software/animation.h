#ifndef _ANIMATION_H
#define _ANIMATION_H

#include <stdint.h>
#include "led.h"

typedef struct {
	void (*animationCycle)(void);
	void (*animationTrigger)(uint8_t,uint8_t,uint8_t);
}ANIMATION_TYPE;



void animation_initialize(void);
void animation_cycle(void);
void animation_triggerFG(uint8_t nr, uint8_t v1, uint8_t v2, uint8_t v3);
void animation_triggerBG(uint8_t nr, uint8_t v1, uint8_t v2, uint8_t v3);

 void animation_setFG(uint16_t led_nr, RGB color);
 void animation_setBG(uint16_t led_nr, RGB color);


 void animation_setFGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color);
 void animation_setBGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color);

 void animation_setOpacity(uint16_t led_nr, uint8_t pOpacity);
 void animation_setOpacityRange(uint16_t led_nr_start,uint16_t led_nr_end, uint8_t pOpacity);
 RGB RGBbrightness(RGB color, uint8_t brightness);

extern uint8_t bgAnimation;
extern uint8_t fgAnimation;
#endif