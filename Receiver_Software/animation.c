#include "animation.h"
#include "stm32f0xx.h"  
#include "led.h"
#include "accel.h"
#include "adc.h"
#include "stdlib.h"
#include "sc.h"
#include "info.h"

#define BRIGHNESS_LIMIT 180


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

static void idleCycle(void);
static void idleTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static uint8_t animationPointer;
uint8_t bgAnimation;
uint8_t fgAnimation;


static void blinkCycle(void);
static void blinkTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void bounceCycle(void);
static void bounceTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void stroboCycle(void);
static void stroboTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void rainbowCycle(void);
static void rainbowTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void rainCycle(void);
static void rainTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void stripeCycle(void);
static void stripeTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void stripe2Cycle(void);
static void stripe2Trigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void constColorCycle(void);
static void constColorTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void twoFaceCycle(void);
static void twoFaceTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static void sparkCycle(void);
static void sparkTrigger(uint8_t v1, uint8_t v2, uint8_t v3);

static uint16_t speed;
static uint16_t ColorCounter;

ANIMATION_TYPE FGanimationList[] = 
{
	{idleCycle,idleTrigger},
	{blinkCycle, blinkTrigger}, // 0
	{stroboCycle,stroboTrigger},
	{rainCycle,rainTrigger}
};

ANIMATION_TYPE BGanimationList[] = 
{
	{rainbowCycle, rainbowTrigger},
	{stripeCycle,stripeTrigger},
	{constColorCycle,constColorTrigger},
	{stripe2Cycle,stripe2Trigger},
	{twoFaceCycle,twoFaceTrigger},
	{sparkCycle,sparkTrigger}
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


static RGB rainBuffer[70];
static uint8_t rainOpacity[70]={0};
static uint8_t rainSpeed;

static void rainCycle(void){
	static uint8_t rainCounter;
	uint8_t i;
	
	if(rainSpeed==0){
		rainCounter = 0;
			for(i=69;i>0;i--){	// Shift Buffer for 1 position
				if(i>1){
					rainBuffer[i] = rainBuffer[i-2];
					rainOpacity[i] = rainOpacity[i-2];
				}
			}
	}
	else{
		if(rainCounter >= rainSpeed){ // Shift pending
			rainCounter = 0;
			for(i=69;i>0;i--){	// Shift Buffer for 1 position
				if(i!=0){
					rainBuffer[i] = rainBuffer[i-1];
					rainOpacity[i] = rainOpacity[i-1];
				}
			}
		}
	}
	
	
	for(i=0;i<50;i++){
		animation_setFG(i, rainBuffer[i+20]);
		animation_setFG(99-i, rainBuffer[i+20]);
		animation_setOpacity(i, rainOpacity[i+20]);
		animation_setOpacity(99-i, rainOpacity[i+20]);
	}
	rainCounter++;
}

/**
Parameter: v1: HighNibble: Speed, LowNibble: Rain-Drop-Length
**/
static void rainTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
		RGB tmp;
		uint8_t i;
		uint8_t rainDropLength = ((v1&0x0F)<<2)/3;
		uint32_t intensity=1000;
		uint8_t tmpOpacity = 255;
		tmp.r = (v2&0xF8);
		tmp.g = (v2&0x7)<<5 | (v3&0xC)>>3;
		tmp.b = (v3&0x3E)<<2;
		rainSpeed = (v1>>4);
	
		//animation_setOpacityRange(0,LED_NR-1,128);
	
		for(i = 0; i < rainDropLength; i++)
		{
			/*tmp.r = (tmp.r*intensity)/1000;
			tmp.g = (tmp.g*intensity)/1000;
			tmp.b = (tmp.b*intensity)/1000;*/
			rainBuffer[20-i]=tmp;
			rainOpacity[20-i]=tmpOpacity;
			tmpOpacity -= (255/(rainDropLength+1));
			intensity -= (1000/(rainDropLength+1));
		}
		
}



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

int16_t energy;
int8_t length;
int8_t arm;

int8_t thres1,thres2;
int8_t old;
static void bounceCycle(void){
	ACCEL_TYPE tmp;
		int8_t diff;

	tmp = accel_getData();
	diff = old - tmp.z;
	
	length += energy/4;
	
	if(energy > 0){
		energy = energy/4;
		if(energy==0){
			energy = -1;
		}
	}
	else if(energy < 0){
		energy *= 2;
		if(length < 0){
			energy = 0;
			length = 0;
		}
	}
	
		if(diff > -thres2){
			bounceTrigger(100,thres1,thres2);
			arm=0;
		}
	
	
	animation_setOpacityRange(0,LED_NR-1,0);
	animation_setOpacityRange(49-length,49,255);
	animation_setFGRange(0,LED_NR-1,black);
	animation_setFGRange(49-length,49,RGBbrightness(white,64));
}

static void bounceTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	energy = v1;
	thres1 = v2;
	thres2 = v3;
	animation_setOpacityRange(0,LED_NR-1,255);
}


uint8_t stroboLength;
uint8_t stroboPause;
uint8_t stroboOn;

enum STROBO{
	STROBO_SINGLE = 0,
	STROBO_CONT_SYNC = 1,
	STROBO_CONT_ASYNC = 2,
	STROBO_RAND = 3
} stroboMode;

static void stroboCycle(void){
	if(stroboOn == 0){
		switch(stroboMode){
			case STROBO_SINGLE:
				if(counter == 1){
					animation_setOpacityRange(0,LED_NR-1,255);
					stroboOn = 1;
					counter = stroboLength;
				}
				break;
			case STROBO_CONT_SYNC:
				if(--counter == 0){
					animation_setOpacityRange(0,LED_NR-1,255);
					stroboOn = 1;
					counter = stroboLength;
				}
				break;
			case STROBO_CONT_ASYNC:
				if(--counter == 0){
					animation_setOpacityRange(0,LED_NR-1,255);
					stroboOn = 1;
					counter = stroboLength;
				}
				break;
			case STROBO_RAND:
				if(--counter == 0){
					animation_setOpacityRange(0,LED_NR-1,255);
					stroboOn = 1;
					counter = stroboLength;
				}
			break;
			default:
				break;
		}
	}
	else{
		switch(stroboMode){
			case STROBO_SINGLE:
				if(counter > 0){
					counter--;
					if(counter == 0){
							animation_setOpacityRange(0,LED_NR-1,0);
					}
				}
				break;
			case STROBO_CONT_SYNC:
				if(--counter == 0){
					animation_setOpacityRange(0,LED_NR-1,0);
					counter = stroboPause;
					stroboOn = 0;
				}
				break;
			case STROBO_CONT_ASYNC:
				if(counter > 0){
					counter--;
					if(counter == 0){
						animation_setOpacityRange(0,LED_NR-1,0);
						if(stroboPause > 0){
							counter = 35;
							stroboOn = 0;
						}
					}
				}
				break;
			case STROBO_RAND:
				if(counter > 0){
					counter--;
					if(counter == 0){
						animation_setOpacityRange(0,LED_NR-1,0);
						if(stroboPause > 0){
							counter = rand()%stroboPause+1;
							stroboOn = 0;
						}
					}
				}

			break;
			default:
				break;
		}
		
	}
}

static void stroboTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
		RGB tmp;
		tmp.r = (v2&0xF8);
		tmp.g = (v2&0x7)<<5 | (v3&0xC)>>3;
		tmp.b = (v3&0x3E)<<2;
	
		animation_setFGRange(0,LED_NR-1,tmp);
		animation_setOpacityRange(0,LED_NR-1,0);
	
		stroboMode = (enum STROBO)(v1&0x3);
		stroboLength = (v1&0x1C)>>2;
		stroboPause = (v1&0xE0)>>5;
		
	srand(sc_getTimeCounter());

	stroboOn = 0;
	
	switch(stroboMode){
		case STROBO_SINGLE:
			counter = 1;
			break;
		case STROBO_CONT_SYNC:
			counter = stroboPause;
			break;
		case STROBO_CONT_ASYNC:
			counter = device_adress;
			break;
		case STROBO_RAND:
			counter = (rand()%stroboPause);
		break;
		default:
			break;
	}
		
}



static void idleCycle(void){

}
static void idleTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
		RGB tmp;
		//tmp = white;
    
		if(v1 == 0){
			    animation_setOpacityRange(0,LED_NR-1,255);
		}
		else{
		    animation_setOpacityRange(0,LED_NR-1,0);
		}
		//animation_setFGRange(0,LED_NR-1,tmp);
        //animation_setOpacityRange(0,LED_NR-1,150);

}



RGB constColor = {0xFF,0x00,0x00};


static void rainbowCycle(void){
	int i;
	RGB color;
	uint16_t s;
	uint16_t c;
	ColorCounter+=speed;
	if(ColorCounter >= 2400)ColorCounter-=2400;
	
	for(i=0;i<50;i++){
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
		}
		animation_setBG(i,RGBbrightness(color,64));
		animation_setBG(99-i,RGBbrightness(color,64));
}

}

static void rainbowTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	constColor.r = v1;
	constColor.g = v2;
	constColor.b = v3;
	ColorCounter = 0;
	speed = v1;
}


static void constColorCycle(void){


}

static void constColorTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	RGB tmp_color;
	tmp_color.r = v1;
	tmp_color.g = v2;
	tmp_color.b = v3;
	
	
	
	animation_setBGRange(0,99,tmp_color);
}


static void twoFaceCycle(void){
	
}

static void twoFaceTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	RGB tmp_color1,tmp_color2;
	uint8_t bright = (v1&0xFC);
	tmp_color1.r =  ((0x3&v1)<<6) | ((v2&0x80)>>2);
	tmp_color1.g = (v2&0x70)<<1;
	tmp_color1.b = (v2&0xE)<<4;
	
	tmp_color2.r = ((v2&0x01)<<7) | ((v3&0xC0)>>1);
	tmp_color2.g = (v3&0x38)<<2;
	tmp_color2.b = (v3&0x7)<<5;
	
	animation_setBGRange(0,49,RGBbrightness(tmp_color1,bright));
	animation_setBGRange(50,99,RGBbrightness(tmp_color2,bright));
}


uint8_t sparkModulo;
uint8_t sparkSpeed;
uint8_t sparkDelay;
RGB sparkColor;
static void sparkCycle(void){
	uint8_t i;
	RGB tmp;
	if(--sparkDelay == 0){
		sparkDelay = sparkSpeed;
		for(i=0;i<100;i++){
			if((rand()%sparkModulo)==0){
				animation_setBG(i,sparkColor);
			}
			else{
				animation_setBG(i,black);
			}
		}
	}
}

static void sparkTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	sparkColor.r = (v2&0xF8);
	sparkColor.g = (v2&0x7)<<5 | (v3&0xC)>>3;
	sparkColor.b = (v3&0x3E)<<2;
	
	srand(sc_getTimeCounter());
	
	sparkModulo = v1&0x0F;
	sparkSpeed = (v1&0xF0)>>4;
	sparkDelay = sparkSpeed;
}

static uint16_t stripeCounter = 0;
static uint8_t stripeSpeed = 1;
static uint8_t stripeLength = 1;
static uint8_t stripeDelay = 0;
static RGB color1;
static RGB color2;

static void stripeCycle(void){
	uint8_t i;
	for(i = 0;i<50;i++){
		if(i<stripeCounter){
			if((i-stripeCounter+2*stripeLength)%(2*stripeLength)<stripeLength){
						animation_setBG(i,color1);
						animation_setBG(99-i,color1);
			}
			else{
						animation_setBG(i,color2);
						animation_setBG(99-i,color2);
			}
		}
		else{
			if((i-stripeCounter)%(2*stripeLength)<stripeLength){
						animation_setBG(i,color1);
						animation_setBG(99-i,color1);
			}
			else{
						animation_setBG(i,color2);
						animation_setBG(99-i,color2);
			}
		}
	}
	if(++stripeDelay == stripeSpeed){
			stripeDelay = 0;
			stripeCounter++;
			if(stripeCounter == 2*stripeLength){
				stripeCounter = 0;
			}
	}
	
}

static void stripeTrigger(uint8_t v1, uint8_t v2, uint8_t v3){
	uint8_t shift = v3&0x0F;
	color1.r =  (v2&0xC0);
	color1.g = ((v2&0x30)<<2);
	color1.b = ((v2&0x0C)<<4);
	color2.r = ((v2&0x03))<<6;
	color2.g = (v3&0xC0);
	color2.b = ((v3&0x30)<<2);
	color1 = RGBbrightness(color1,v3<<4);
	color2 = RGBbrightness(color2,v3<<4);

	stripeCounter = 0;
	stripeSpeed = (v1&0xE0)>>5;
	stripeLength = v1&0x1F;
}



static void stripe2Cycle(void){
	uint8_t i;
	for(i = 0;i<25;i++){
		if(i<stripeCounter){
			if((i-stripeCounter+2*stripeLength)%(2*stripeLength)<stripeLength){
						animation_setBG(i,color1);
						animation_setBG(49-i,color1);
						animation_setBG(50+i,color1);
						animation_setBG(99-i,color1);
			}
			else{
						animation_setBG(i,color2);
						animation_setBG(49-i,color2);
						animation_setBG(50+i,color2);
						animation_setBG(99-i,color2);
			}
		}
		else{
			if((i-stripeCounter)%(2*stripeLength)<stripeLength){
						animation_setBG(i,color1);
						animation_setBG(49-i,color1);
						animation_setBG(50+i,color1);
						animation_setBG(99-i,color1);
			}
			else{
						animation_setBG(i,color2);
						animation_setBG(49-i,color2);
						animation_setBG(50+i,color2);
						animation_setBG(99-i,color2);
			}
		}
	}
	if(++stripeDelay == stripeSpeed){
			stripeDelay = 0;
			stripeCounter++;
			if(stripeCounter == 2*stripeLength){
				stripeCounter = 0;
			}
	}
	
}

static void stripe2Trigger(uint8_t v1, uint8_t v2, uint8_t v3){
	uint8_t shift = v3&0x0F;
	color1.r =  (v2&0xC0);
	color1.g = ((v2&0x30)<<2);
	color1.b = ((v2&0x0C)<<4);
	color2.r = ((v2&0x03))<<6;
	color2.g = (v3&0xC0);
	color2.b = ((v3&0x30)<<2);
	color1 = RGBbrightness(color1,v3<<4);
	color2 = RGBbrightness(color2,v3<<4);
	stripeCounter = 0;
	stripeSpeed = (v1&0xE0)>>5;
	stripeLength = v1&0x1F;
}


 void animation_setFG(uint16_t led_nr, RGB color){
	fgBuffer[led_nr*3] = color.g;
	fgBuffer[led_nr*3+1] = color.r;
	fgBuffer[led_nr*3+2] = color.b;
}

 void animation_setBG(uint16_t led_nr, RGB color){
	bgBuffer[led_nr*3] = color.g;
	bgBuffer[led_nr*3+1] = color.r;
	bgBuffer[led_nr*3+2] = color.b;
}

 void animation_setFGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		animation_setFG(i,color);
	}
}

 void animation_setBGRange(uint16_t led_nr_start,uint16_t led_nr_end, RGB color){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		animation_setBG(i,color);
	}
}

 void animation_setOpacity(uint16_t led_nr, uint8_t pOpacity){
	opacity[led_nr] = pOpacity;
}

 void animation_setOpacityRange(uint16_t led_nr_start,uint16_t led_nr_end, uint8_t pOpacity){
	int i;
	for(i=led_nr_start;i<=led_nr_end;i++){
		opacity[i]= pOpacity;
	}
}

 RGB RGBbrightness(RGB color, uint8_t brightness){
	RGB tmp;
    if (brightness > BRIGHNESS_LIMIT){
        brightness = BRIGHNESS_LIMIT;
    }
	tmp.r = ((uint16_t)color.r*(uint16_t)brightness)>>8;
	tmp.g = ((uint16_t)color.g*(uint16_t)brightness)>>8;
	tmp.b = ((uint16_t)color.b*(uint16_t)brightness)>>8;
	return tmp;
}
