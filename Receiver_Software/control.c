/**
 * @file usart_hal.c
 * @author Simon Vogel
 * @author Ueli Schön
 */
 
#include "control.h"
#include "animation.h"
#include "led.h"
#include "info.h"
#include "stm32f0xx.h"  
#include "accel.h"
#include "math.h"
#include "adc.h"

typedef struct {
	double H;
	double S;
	double V;
}HSV;

RGB red_cont = {0xFF,0x00,0x00};
RGB white_cont = {0xFF,0xFF,0xFF};
RGB green_cont = {0x00,0xFF,0x00};
RGB blue_cont = {0x00,0x00,0xFF};

typedef enum { IDLE, UPRIGHT, MODE, COLORCHOOSE} state_t ;

state_t control_state = IDLE;

static uint32_t control_mode_count;
static uint8_t next_mode_count;

static uint32_t mode_count, old_mode_count;
static ACCEL_TYPE currentData;

static RGB RGBround(RGB in, uint8_t bits);

uint8_t brightness;
HSV hsv_i;
RGB rgb_i;
RGB currentUserColor;
uint8_t currentUserBrightness;
static int8_t z_axis;
static uint8_t startup;
RGB HSVtoRGB(HSV hsv);

void triggerMode(void);

void nextMode(){
    if (next_mode_count){
        mode_count ++;
        next_mode_count = 6;
    } else{
        next_mode_count --;
    }
}

		
void control_init(void){
	startup = 60;
    mode_count = 0;
    old_mode_count = 0;
    next_mode_count = 0;
}


void stateMachine()
{
	
  switch( control_state ) {
      case IDLE:
          if (currentData.angle >= (255-ANGLE_TOLERANCE)){
                if (control_mode_count == DETECTION_INTERVAL){
                    control_state = UPRIGHT;
                }
           
          }else
                if( control_mode_count < DETECTION_INTERVAL){
                control_mode_count = 0;
                }
      break;
    case UPRIGHT:
        if (control_mode_count == (DETECTION_INTERVAL + 10)){
            animation_setFGRange(0, LED_NR-1, RGBbrightness(white_cont,0));
            //animation_setOpacityRange(0,LED_NR-1,0);
        }
        
        if (control_mode_count == (DETECTION_INTERVAL + 5)){
            //set forground animation to idle
            animation_triggerFG(0, 0,0,0);
            animation_setFGRange(0, LED_NR-1, RGBbrightness(red_cont,255));
        }
        if (control_mode_count == (DETECTION_INTERVAL + 10 + 30)){
            control_state = COLORCHOOSE;
            control_mode_count = 0;
        }
        
        
        if (z_axis > SWITCH_LEVEL){
            control_state = MODE;
            animation_setOpacityRange(0,LED_NR-1,0);
        } 
        break;
    case MODE:
        if (currentData.angle <= (255-ANGLE_TOLERANCE)){
            control_state = IDLE;
            control_mode_count = 0;
        }
        if (z_axis > SWITCH_LEVEL){
            nextMode();
        } 
        if (mode_count != old_mode_count){
            triggerMode();
            old_mode_count = mode_count;
        }
        break;
    case COLORCHOOSE:
        if (control_mode_count == (TIME_TO_CHOOSE*20)+5){
            animation_setFGRange(0, LED_NR-1, currentUserColor);
            animation_setOpacityRange(0,LED_NR-1,0);
            //animation_triggerBG(2,currentUserColor.r,currentUserColor.g,currentUserColor.b);
            
            
            triggerMode();
            control_state = IDLE;
            control_mode_count = 0;
        }
        
        if (control_mode_count == (TIME_TO_CHOOSE*20)){
            animation_setFGRange(0, LED_NR-1, RGBbrightness(red_cont,255));
            currentUserColor = rgb_i;
            currentUserBrightness = brightness;
        }
        
        if ((control_mode_count > (20)) && (control_mode_count <(TIME_TO_CHOOSE*20))){
            hsv_i.H = (currentData.angle*1.7117647059)-10; //1.4117647059
						if(hsv_i.H>359.0f){
							hsv_i.H = 359.0f;
						}
						if(hsv_i.H<0.0f){
							hsv_i.H = 0.0f;
						}
            hsv_i.V = 0.9;//log_value[(int)(currentData.angle_xy/255.0)];
            hsv_i.S = 1;
			brightness = currentData.angle_xy;
            rgb_i = HSVtoRGB(hsv_i);
            animation_setFGRange(0, LED_NR-1, RGBbrightness(rgb_i,brightness));
            //reset control mode counter        
        }
        
        if (control_mode_count == (20)){
            animation_setFGRange(0, LED_NR-1, RGBbrightness(white_cont,0));
            animation_setOpacityRange(0,LED_NR-1,255);
        }
        
        if (control_mode_count == (15)){
            animation_setFGRange(0, LED_NR-1, RGBbrightness(red_cont,255));
        }
        
        if (control_mode_count == (10)){
            animation_setFGRange(0, LED_NR-1, RGBbrightness(white_cont,0));
        }
        
        if (control_mode_count == (5)){
            animation_triggerFG(0, 0,0,0);
            animation_setFGRange(0, LED_NR-1, RGBbrightness(red_cont,255));
        }
        break;
                
    default:
        break;
  }
}

void control_cycle(void){
    currentData = accel_getData();
    control_mode_count ++;
    z_axis = currentData.z;
    
    stateMachine();
	
	// Startup ID 
		if(startup > 1){
			startup--;
			animation_triggerFG(0, 0,0,0);
			if(device_adress < 9){
				animation_setFGRange(0,device_adress,red_cont);
			}else{
				animation_setFGRange(0,9,red_cont);
				if(device_adress < 19){
						animation_setFGRange(10,device_adress,blue_cont);
				}
				else{
						animation_setFGRange(10,19,blue_cont);
						if(device_adress < 29){
						animation_setFGRange(20,device_adress,green_cont);
				}
				else{
						animation_setFGRange(20,29,green_cont);
						animation_setFGRange(30,device_adress,white_cont);
					}
				}
			}
		}else if(startup == 1){
					animation_triggerFG(0, 1,0,0);
					startup--;
		}
    //End Startup    
    
    // Batterie warning
        
    if (adc_getBattery() < MIN_BAT_mV){
        animation_setFGRange(0,50,red_cont);
    } 
}


void triggerMode(void){
    RGB tmp_rgb;
    uint8_t tmp1,tmp2,tmp3;
    switch(mode_count){
				case 1:
										//stripeCycle
										tmp_rgb = RGBround(currentUserColor,2);
                    tmp3 = currentUserBrightness>>4;
                    tmp3 |= 0xF0;
                    tmp2 = 0x03 |((tmp_rgb.b&0xC0)>>4) | ((tmp_rgb.g&0xC0)>>2) | (tmp_rgb.r&0xC0);
                    tmp1 = 0x2A;
                    animation_triggerBG(1, tmp1, tmp2, tmp3);
                    animation_setOpacityRange(0,99,0);
                    break;
                case 2:
                    //constColorCycle
                    tmp_rgb = RGBbrightness(currentUserColor,currentUserBrightness);
                    animation_triggerBG(2,tmp_rgb.r,tmp_rgb.g,tmp_rgb.b);
                    break;
                case 3:
										tmp_rgb = RGBround(currentUserColor,2);
                    tmp3 = currentUserBrightness>>4;
										tmp3 |= 0xF0;
                    tmp2 = 0x03 |((tmp_rgb.b&0xC0)>>4) | ((tmp_rgb.g&0xC0)>>2) | (tmp_rgb.r&0xC0);
                    tmp1 = 0x2A;
                    animation_triggerBG(3, tmp1, tmp2, tmp3);
                    animation_setOpacityRange(0,99,0);
                    //animation_triggerBG(3, currentUserColor.r, currentUserColor.g, currentUserColor.b);
                    break;
				case 4:
										tmp_rgb = RGBround(currentUserColor,3);
                    tmp1 = currentUserBrightness&0xFC;
                    tmp1 |= 0x3;
                    tmp2 = 0xFE | (tmp_rgb.r&0x80)>>7;
                    tmp3 = ((tmp_rgb.r&0x60)<<1) | ((tmp_rgb.g&0xE0)>>2) | (tmp_rgb.b&0xE0)>>5;
                    animation_triggerBG(4, tmp1, tmp2, tmp3);
                    animation_setOpacityRange(0,99,0);
                    break;
								
				case 5:									
                    tmp1 = 0x14;
                    tmp_rgb = RGBbrightness(currentUserColor,currentUserBrightness);
                    tmp_rgb = RGBround(tmp_rgb,3);
				
                    tmp2 = (tmp_rgb.r&0xF8) | (tmp_rgb.g&0xE0)>>5;
                    tmp3 = (tmp_rgb.g&0x18)<<3 | (tmp_rgb.b&0xF8)>>2;
                    
                    animation_triggerBG(5, tmp1, tmp2, tmp3);
                    animation_setOpacityRange(0,99,0);
                    //animation_triggerBG(3, currentUserColor.r, currentUserColor.g, currentUserColor.b);
                    break;
					
                case 6:		
                    //Rainbow
                    animation_triggerBG(0, 10, 0, 0);
                    animation_setOpacityRange(0,99,0);
                    break;
                
                case 7:		
                    mode_count =0;
                    break;
                default:
                    break;
            }
}

RGB HSVtoRGB(HSV hsv)
{   
    RGB rgb;
    float h,s,v,r,g,b;
    int i;
    float f, p, q, t;
    h = (float)hsv.H;
    s = (float)hsv.S;
    v = (float)hsv.V;
    
    h /= 60;            // sector 0 to 5
    i = floor( h );
    f = h - i;          // factorial part of h
    p = v * ( 1 - s );
    q = v * ( 1 - s * f );
    t = v * ( 1 - s * ( 1 - f ) );
    switch( i ) {
    case 0:
        r = v;
        g = t;
        b = p;
        break;
    case 1:
        r = q;
        g = v;
        b = p;
        break;
    case 2:
        r = p;
        g = v;
        b = t;
        break;
    case 3:
        r = p;
        g = q;
        b = v;
        break;
    case 4:
        r = t;
        g = p;
        b = v;
        break;
    default:        // case 5:
        r = v;
        g = p;
        b = q;
        break;
    }
    rgb.r = r*255;
    rgb.g = g*255;
    rgb.b = b*255;
    
    return rgb;
}


static RGB RGBround(RGB in,uint8_t bits){
	RGB tmp;
	uint8_t pattern;
	pattern = ((1<<bits)-1)<<(8-bits);
	
	tmp.r = in.r&pattern;
	if(in.r&(1<<(7-bits))){
		if(tmp.r != pattern){
				tmp.r += 1<<(8-bits);
		}
	}
	tmp.g = in.g&pattern;
	if(in.g&(1<<(7-bits))){
		if(tmp.g != pattern){
				tmp.g += 1<<(8-bits);
		}
	}
	
	tmp.b = in.b&pattern;
	if(in.b&(1<<(7-bits))){
		if(tmp.b != pattern){
				tmp.b += 1<<(8-bits);
		}
	}
	
	return tmp;
}


