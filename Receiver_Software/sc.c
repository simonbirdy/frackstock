#include "sc.h"
#include "stm32f0xx.h"  

uint16_t old_value;
void sc_initialize(void){
	RCC->APB2ENR |= RCC_APB2ENR_TIM16EN;
	
	TIM16->PSC = 47;
	TIM16->CR1 = TIM_CR1_CEN;
}

uint16_t sc_getTimeCounter(void){
	return ((uint16_t)TIM16->CNT);
}

uint8_t sc_waitForCycle(void){ 
	uint16_t value;
	value = ((uint16_t)TIM16->CNT)-old_value;
	
	if(value<CYCLE_DURATION){
		return 0;
	}
	else{
			old_value += CYCLE_DURATION;
			return 1;
	}
}