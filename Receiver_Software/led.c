/**
 * @file usart_hal.c
 * @author Simon Vogel
 */
 
#include "led.h"
#include "stm32f0xx.h"  



 
 #define HIGH_TIME 38
 #define LOW_TIME 19


static uint8_t * buffer;

void led_initialize(void)
{
	int i=0;
	RGB black = {0,0,0};
    // Enable used clocks
    RCC->AHBENR  	|=   RCC_AHBENR_GPIOAEN;
    
    /* Configure PA3 to USART2_RX, PA2 to USART2_TX */
		GPIOA->AFR[1] |= (1<<2*4)|(1<<4) | 2;
    GPIOA->MODER  &=  ~(GPIO_MODER_MODER8);
    GPIOA->MODER  |=  (GPIO_MODER_MODER8_0);
		GPIOA->OTYPER |= GPIO_OTYPER_OT_8;
		GPIOA->BSRR = GPIO_BSRR_BR_8;

       
	
	
	
		//Timer1
		RCC->APB2ENR = RCC_APB2ENR_TIM1EN;
		//TIM1->CR1 = TIM_CR1_ARPE;
		TIM1->CCMR1 = TIM_CCMR1_OC1M_2 | TIM_CCMR1_OC1M_1 | TIM_CCMR1_OC1PE;
		TIM1->CCR1 = 0;
		TIM1->ARR = 60;
		TIM1->CCER = TIM_CCER_CC1E;

		TIM1->BDTR = TIM_BDTR_MOE;
		for(i=0;i<10000;i++);
}

void led_cycle(void){
	uint32_t i,j,temp_moder;
	uint8_t tmp;
	
	TIM1->CCR1 = 0;
	TIM1->CNT = 0;
	
	TIM1->CR1 = TIM_CR1_CEN;
	TIM1->SR &= ~(TIM_SR_UIF);
	
	temp_moder = 	 GPIOA->MODER;
  temp_moder&=  ~(GPIO_MODER_MODER8);
  temp_moder  |=  (GPIO_MODER_MODER8_1);	
	GPIOA->MODER = temp_moder;

	while((TIM1->SR & TIM_SR_UIF) == 0);
	TIM1->SR &= ~(TIM_SR_UIF);
	
	for(i=0;i<3*LED_NR; i++){
		tmp = buffer[i];
		for(j=0; j<8;j++){
			while((TIM1->SR & TIM_SR_UIF) == 0);
			TIM1->SR &= ~(TIM_SR_UIF);

			if(tmp&0x80){
				TIM1->CCR1 = HIGH_TIME;
			}
			else{
				TIM1->CCR1 = LOW_TIME;
			}
			tmp = tmp<<1;
		}
	}
	while((TIM1->SR & TIM_SR_UIF) == 0);
			TIM1->SR &= ~(TIM_SR_UIF);

	TIM1->CCR1 = 0;

	while((TIM1->SR & TIM_SR_UIF) == 0);
	TIM1->CR1 &= ~TIM_CR1_CEN;
	GPIOA->BSRR = GPIO_BSRR_BR_8;
	temp_moder = 	 GPIOA->MODER;
  temp_moder&=  ~(GPIO_MODER_MODER8);
  temp_moder  |=  (GPIO_MODER_MODER8_0);	
	GPIOA->MODER = temp_moder;
}



void led_setLEDBuffer(uint8_t * pBuffer){
	buffer = pBuffer;
}


