#include "accel.h"
#include "stm32f0xx.h"  



ACCEL_TYPE currentData;

void accel_init(void){
		RCC->APB1ENR |= RCC_APB1ENR_I2C1EN;

		GPIOA->MODER  &=  ~(GPIO_MODER_MODER9 | GPIO_MODER_MODER10);
		GPIOA->MODER  |=  (GPIO_MODER_MODER9_1 | GPIO_MODER_MODER10_1);
		GPIOA->AFR[1] |= (4<<1*4) | (4<<2*4);
	
	
	
	
	
		I2C1->TIMINGR = 0x20B;
		I2C1->CR1 |= I2C_CR1_PE;
	
	I2C1->CR2 = (0x4C<<1) | (0x02<<16) | I2C_CR2_AUTOEND;
	I2C1->CR2 |= I2C_CR2_START;
	
	while((I2C1->ISR & (I2C_ISR_TXIS)) == 0);
	I2C1->TXDR = 0x07;
	while((I2C1->ISR & (I2C_ISR_TXIS)) == 0);
	I2C1->TXDR = 0x01;
		
}

void accel_cycle(void){
	uint8_t tmp;
	I2C1->CR2 = (0x4C<<1) | (0x01<<16);
	I2C1->CR2 |= I2C_CR2_START;
	
	while((I2C1->ISR & (I2C_ISR_TXIS)) == 0);
	I2C1->TXDR = 0;
	
	while((I2C1->ISR & I2C_ISR_TC) == 0);

	I2C1->CR2 = (0x4C<<1) | (0x03<<16) | I2C_CR2_AUTOEND | I2C_CR2_RD_WRN;
	I2C1->CR2 |= I2C_CR2_START;	
	
	
	while((I2C1->ISR & (I2C_ISR_RXNE)) == 0);
	tmp = 0x3F&I2C1->RXDR;
	if((tmp & 0x20) != 0){
		currentData.x = (int8_t)(tmp | 0xC0);
	}
	else{
				currentData.x = (int8_t)(tmp);
	}
	while((I2C1->ISR & (I2C_ISR_RXNE)) == 0);
		tmp = 0x3F&I2C1->RXDR;
	if((tmp & 0x20) != 0){
		currentData.y = (int8_t)(tmp | 0xC0);
	}
	else{
				currentData.y = (int8_t)(tmp);
	}
	while((I2C1->ISR & (I2C_ISR_RXNE)) == 0);
	tmp = 0x3F&I2C1->RXDR;
	if((tmp & 0x20) != 0){
		currentData.z = (int8_t)(tmp | 0xC0);
	}
	else{
				currentData.z = (int8_t)(tmp);
	}


	
	

}

ACCEL_TYPE accel_getData(void){
	return currentData;
}