#include "animation.h"
#include "stm32f0xx.h"  
#include "led.h"
#include "accel.h"


RGB white = {0xFF,0xFF,0xFF};
RGB red = {0xFF,0x00,0x00};
RGB green = {0x00,0xFF,0x00};
RGB blue = {0x00,0x00,0xFF};
RGB black = {0x00,0x00,0x00};
RGB orange = {255,69,0};
RGB yellow = {255,255,0};
RGB cyan = {0,255,255};
		
		uint32_t counter;

static uint8_t bgBuffer[LED_NR*3];
static uint8_t fgBuffer[LED_NR*3];
static uint8_t buffer[LED_NR*3];
static uint8_t opacity[LED_NR];

static uint8_t animationPointer;
uint8_t bgAnimation;
uint8_t fgAnimation;


static void blinkCycle(void);
static void blinkTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void constColorCycle(void);
static void constColorTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static uint16_t speed;
static uint16_t ColorCounter;

ANIMATION_TYPE FGanimationList[] = 
{
	{blinkCycle, blinkTrigger} // 0
};

ANIMATION_TYPE BGanimationList[] = 
{
	{constColorCycle, constColorTrigger}
};

#define NR_ANIMATIONS_FG sizeof(FGanimationList)/sizeof(ANIMATION_TYPE)
#define NR_ANIMATIONS_BG sizeof(BGanimationList)/sizeof(ANIMATION_TYPE)

void animation_initialize(void){
		GPIOA->MODER  &=  ~(GPIO_MODER_MODER12);
		GPIOA->MODER  |=  (GPIO_MODER_MODER12_0);
		led_setLEDBuffer(buffer);
		speed = 10;
}


void animation_cycle(void){
	int i,j;
	FGanimationList[fgAnimation].animationCycle();
	BGanimationList[bgAnimation].animationCycle();
	
	for(i=0;i<LED_NR;i++){
		j = 3*i;
		buffer[j] 	= ((uint16_t)(255-opacity[i])*(uint16_t)bgBuffer[j] + (uint16_t)opacity[i]*(uint16_t)fgBuffer[j])>>8;
		buffer[j+1] = ((uint16_t)(255-opacity[i])*(uint16_t)bgBuffer[j+1] + (uint16_t)opacity[i]*(uint16_t)fgBuffer[j+1])>>8;
		buffer[j+2] = ((uint16_t)(255-opacity[i])*(uint16_t)bgBuffer[j+2] + (uint16_t)opacity[i]*(uint16_t)fgBuffer[j+2])>>8;
	}
}

void animation_triggerFG(uint8_t nr, uint8_t v1, uint8_t v2, uint8_t v3){
	FGanimationList[nr].animationTrigger(v1,v2,v3);
	fgAnimation = nr;
}

void animation_triggerBG(uint8_t nr, uint8_t v1, uint8_t v2, uint8_t v3){
	BGanimationList[nr].animationTrigger(v1,v2,v3);
	bgAnimation = nr;
}



uint8_t dec;


static void blinkCycle(void){

	if(counter>0){

		if(counter > dec){
			counter -= dec;
		}
		else{
			counter = 0;
		}
		//animation_setFGRange(0,LED_NR-1, RGBbrightness(tmp,counter));
		animation_setOpacityRange(0,LED_NR-1,counter);
	}
	else{
		animation_setOpacityRange(0,LED_NR-1,0);

	}
}

static void blinkTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
		RGB tmp;
		tmp.r = (v2&0xF8);
		tmp.g = (v2&0x7)<<5 | (v3&0xC)>>3;
		tmp.b = (v3&0x3E)<<2;
	
		animation_setFGRange(0,LED_NR-1,tmp);

		counter = 200;
		dec = v1;
}

RGB constColor = {0xFF,0x00,0x00};


static void constColorCycle(void){
	int i;
	RGB color;
	uint16_t s;
	uint16_t c;
	ACCEL_TYPE accelData = accel_getData();
	ColorCounter+=speed;
	if(ColorCounter >= 2400)ColorCounter-=2400;
	
	/*for(i=0;i<52;i++){
		s = ((ColorCounter+i*25)%2400)/400;
		c = ((ColorCounter+i*25)%2400)-s*400;
		color.r = 0;
		color.g = 0;
		color.b = 0;
		
		switch(s){
			case 4:
				color.r = c/2;
				color.b = 200;
				break;
			case 0:
				color.r = 200;
				color.g = c/2;
				break;
			case	1:
				color.g = 200;
				color.r = 200-c/2;
				break;
			case 2:
				color.b = c/2;
				color.g = 200;
				break;
			case 3:
				color.b = 200;
				color.g = 200-c/2;
				break;
			case 5:
				color.b = 200-c/2;
				color.r = 200;
				break;
			default:
				break;
		}*/
		color.r = (uint8_t)(accelData.x+32);
		color.g = (uint8_t)(accelData.y+32);
		color.b = (uint8_t)(accelData.z+32);
		
		animation_setBGRange(0,101,color);
		//animation_setBG(i,RGBbrightness(color,64));
		//animation_setBG(104-i,RGBbrightness(color,64));
}

static void constColorTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	constColor.r = v1;
	constColor.g = v2;
	constColor.b = v3;
	ColorCounter = 0;
	speed = v1;
}


__inline void animation_setFG(uint16_t led_nr, RGB color){
	fgBuffer[led_nr*3] = color.g;
	fgBuffer[led_nr*3+1] = color.r;
	fgBuffer[led_nr*3+2] = color.b;
}

__inline void animation_setBG(uint16_t led_nr, RGB color){
	bgBuffer[led_nr*3] = color.g;
	bgBuffer[led_nr*3+1] = color.r;
	bgBuffer[led_nr*3+2] = color.b;
}

__inline void animation_setFGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		animation_setFG(i,color);
	}
}

__inline void animation_setBGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		animation_setBG(i,color);
	}
}

__inline void animation_setOpacity(uint16_t led_nr, uint8_t pOpacity){
	opacity[led_nr] = pOpacity;
}

__inline void animation_setOpacityRange(uint16_t led_nr_start,uint16_t led_nr_end, uint8_t pOpacity){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		opacity[i]= pOpacity;
	}
}

__inline RGB RGBbrightness(RGB color, uint8_t brightness){
	RGB tmp;
	tmp.r = ((uint16_t)color.r*(uint16_t)brightness)>>8;
	tmp.g = ((uint16_t)color.g*(uint16_t)brightness)>>8;
	tmp.b = ((uint16_t)color.b*(uint16_t)brightness)>>8;
	return tmp;
}
