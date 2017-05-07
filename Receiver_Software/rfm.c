#include "rfm.h"
#include "stm32f0xx.h"  


uint16_t rfm_buffer[100];

uint16_t rfm_send[] = {
		0x4201,
		0x1544,
		0x000D,
		0x0000,
		0xA900,
		0x0000,
		0x1800,
		0x0000,
		0x5600,
		0x282F,
		0x9F00,
		0x0000,
		0x0001,
		0x0400,
		0x0100,
		0x8110,
		0x0000,
		0x8000,
		0x0E03,
		0x8030,
		0xAEAC,
		0xD453,
		0x4940,
		0x2905,
		0x0318,
		0xFA10,
		0x0000,
		0xC000,
		0x0000,
		0xCA00,
		0x0000,
		0xCC22,
		0x0000,
		0x001F
};

static void rfm_delay(void);
static void rfm_delay2(void);



static void set_sclk(int level);
static void set_sda(int level);
static void set_cso(int level);
static int get_sda(void);
static uint8_t rfm_readByte(void);
static void rfm_writeByte(uint8_t dat);
static void rfm_write(uint16_t dat);
static uint8_t rfm_read(uint8_t addr);

void rfm_initialize(void){
		int i;
	uint16_t adress;
	uint16_t data;
		RCC->AHBENR |=  RCC_AHBENR_GPIOFEN | RCC_AHBENR_GPIOBEN;
		GPIOB->MODER |= GPIO_MODER_MODER1_0;
		GPIOF->MODER |= GPIO_MODER_MODER0_0;
	
		for(i=0;i<sizeof(rfm_send)/2;i++){
			adress = (2*i)<<8;
			data = rfm_send[i]&0x00FF;
			rfm_write(adress | data);
			rfm_delay2();
			adress = (2*i+1)<<8;
			data = rfm_send[i]>>8&0x00FF;
			rfm_write(adress | data);
			rfm_delay2();
	}	
}


void rfm_delay(void){
	int i;
	for(i=0;i<1000;i++){
	}
}

void rfm_delay2(void){
	int i;
	for(i=0;i<100000;i++){
	}
}

static void set_sclk(int level){
	switch(level){
		case 0:
			GPIOF->BSRR = GPIO_BSRR_BR_0;
			break;
		case 1:
			GPIOF->BSRR = GPIO_BSRR_BS_0;
			break;
		default:
			break;
	}
}

static void set_sda(int level){
		switch(level){
		case 0:
			GPIOF->MODER |= GPIO_MODER_MODER1_0;
			GPIOF->BSRR = GPIO_BSRR_BR_1;
			break;
		case 1:
			GPIOF->MODER |= GPIO_MODER_MODER1_0;
			GPIOF->BSRR = GPIO_BSRR_BS_1;
			break;
		case 2:
			GPIOF->MODER &= ~GPIO_MODER_MODER1_0;
			break;
		default:
			break;
	}
}

static void set_cso(int level){
		switch(level){
		case 0:
			GPIOB->BSRR = GPIO_BSRR_BR_1;
			break;
		case 1:
			GPIOB->BSRR = GPIO_BSRR_BS_1;
			break;
		default:
			break;
	}
}

static int get_sda(void){
	if(GPIOF->IDR & GPIO_IDR_1){
		return 1;
	}
	else{
		return 0;
	}
}

static uint8_t rfm_readByte(void)
{
	uint8_t RdPara = 0;
 	uint8_t bitcnt;
  
	set_cso(0);
	set_sda(2);
 	
 	for(bitcnt=8; bitcnt!=0; bitcnt--)
 		{
 		set_sclk(0);
 		RdPara <<= 1;
		rfm_delay();
 		set_sclk(1);
 		if(get_sda())
 			RdPara |= 0x01;
 		else
 			RdPara |= 0x00;
		rfm_delay();		
	}
		set_sclk(0);
		set_sda(1);
	set_cso(1);
rfm_delay();	
 	return(RdPara);	
}

static void rfm_writeByte(uint8_t dat)
{

 	uint8_t bitcnt;	
 
 			set_sda(1);

 
	set_cso(0);
 	for(bitcnt=8; bitcnt!=0; bitcnt--)
 		{
 		set_sclk(0);
 		if(dat&0x80)
		set_sda(1);
 		else
		set_sda(0);
				rfm_delay();
 		set_sclk(1);
 		dat <<= 1; 
		rfm_delay();
		
 		}
		set_sclk(0);
		set_sda(1);
	rfm_delay();
}

/**********************************************************
**Name:	 	vSpi3Write
**Func: 	SPI Write One word
**Input: 	Write word
**Output:	none
**********************************************************/
static void rfm_write(uint16_t dat)
{
 	rfm_writeByte((uint8_t)(dat>>8)&0x7F);
 	rfm_writeByte((uint8_t)dat);
 	set_cso(1);
}

/**********************************************************
**Name:	 	bSpi3Read
**Func: 	SPI-3 Read One byte
**Input: 	readout addresss
**Output:	readout byte
**********************************************************/
static uint8_t rfm_read(uint8_t addr)
{
  	rfm_writeByte(addr|0x80);
 	return(rfm_readByte());
}	
	
