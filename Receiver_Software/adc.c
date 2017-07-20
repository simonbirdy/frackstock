#include "adc.h"
#include "stm32f0xx.h"  

static uint8_t pointer;
static uint8_t id_device;
static uint16_t battery_buffer[32];
static uint32_t battery_sum;

static uint8_t get_value(uint32_t value){
	if(value < 300){
		return 0;
	}
	else if(value < 800){
		return 1;
	}
	else if(value < 1400){
		return 2;
	}
	else if(value < 2000){
		return 3;
	}
	else if(value < 2600){
		return 4;
	}
	else if(value < 3200){
		return 5;
	}
	else if(value < 3700){
		return 6;
	}
	else{
		return 7;
	}
	
}

void adc_initialize(void){
	uint32_t data_0,data_1;
	uint8_t i;
	    RCC->APB2ENR  	|=   RCC_APB2ENR_ADC1EN;
			RCC->AHBENR |= RCC_AHBENR_GPIOAEN;
	
			GPIOA->MODER |= GPIO_MODER_MODER0 | GPIO_MODER_MODER1 | GPIO_MODER_MODER2;
	
			ADC1->CFGR2 = ADC_CFGR2_CKMODE_1;
			ADC1->SMPR = ADC_SMPR_SMP;
	
	
			ADC1->CR |= ADC_CR_ADCAL;
			while((ADC1->CR & ADC_CR_ADCAL) != 0);
	
	
	
			ADC1->CHSELR = 1;
			ADC1->CR |= ADC_CR_ADEN;
	
	
	
			data_0 = 0;
			for(i=0;i<32;i++){
					ADC1->ISR &= ~(ADC_ISR_EOC);
					ADC1->CR |= ADC_CR_ADSTART;
					while((ADC1->ISR & ADC_ISR_EOC) == 0);
					data_0 += ADC1->DR;
			}

			data_0 = data_0>>5;

			ADC1->CR &= ~(ADC_CR_ADEN);
			ADC1->CHSELR = 2;
			ADC1->CR |= ADC_CR_ADEN;

			
			data_1 = 0;
			for(i=0;i<32;i++){
					ADC1->ISR &= ~(ADC_ISR_EOC);
					ADC1->CHSELR = 2;
					ADC1->CR |= ADC_CR_ADSTART;
					while((ADC1->ISR & ADC_ISR_EOC) == 0);
					data_1 += ADC1->DR;
			}
			
			data_1 = data_1>>5;
			
			id_device = (get_value(data_0)<<3) + get_value(data_1);
			
			ADC1->CR &= ~(ADC_CR_ADEN);
			ADC1->CHSELR = 4;
			ADC1->CR |= ADC_CR_ADEN;
			ADC1->CR |= ADC_CR_ADSTART;
			
			for(i = 0; i<32;i++){
				battery_buffer[i] = 3000;
			}
			battery_sum = 32*3000;
}

uint8_t adc_getID(void){
	return id_device;
}


void adc_cycle(void){
		uint16_t tmp_data;
		tmp_data = ADC1->DR;
		ADC1->CR |= ADC_CR_ADSTART;
		battery_sum -= battery_buffer[pointer];
		battery_buffer[pointer] = tmp_data;
		battery_sum += tmp_data;
		if(++pointer==32)pointer=0;
}

uint16_t adc_getBattery(void){
	uint32_t tmp;
	tmp = battery_sum/32;
	tmp = (tmp*4800)>>12;
	return tmp;
}


