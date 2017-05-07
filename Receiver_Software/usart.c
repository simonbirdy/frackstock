#include "stm32f0xx.h"  
#include "info.h"
#include "usart.h"
#include "animation.h"

//#define VCOMPORT
#define BUFFER_SIZE 100
#define DELAY_LENGTH 2
#define COMMAND_LENGTH 7
uint8_t rec_buffer[BUFFER_SIZE];

static void handleCharacter(void);
static void handleCommand(void);
static uint16_t tailPointer;
uint8_t newData(void);
uint16_t getPointer(void);
uint16_t delayCounter;
uint8_t commandBuffer[COMMAND_LENGTH];
uint8_t waiting;

enum{
	IDLE,
	RECEIVE
}state;

void usart_initialize(void){
	// Set USART Registers
		RCC->APB2ENR |= RCC_APB2ENR_USART1EN;
		RCC->AHBENR |= RCC_AHBENR_DMA1EN | RCC_AHBENR_GPIOAEN;
	
		tailPointer = 0;
	

		GPIOA->MODER  &=  ~(GPIO_MODER_MODER3);
		GPIOA->MODER  |=  (GPIO_MODER_MODER3_1);
		GPIOA->AFR[0] |= (1<<3*4);

	
		DMA1_Channel3->CCR = DMA_CCR_MINC | DMA_CCR_CIRC;
		DMA1_Channel3->CNDTR = BUFFER_SIZE;
		DMA1_Channel3->CMAR = (uint32_t)&rec_buffer;
		DMA1_Channel3->CPAR = (uint32_t)&USART1->RDR;
    USART1->BRR  = 5000;
	
		USART1->CR3 = USART_CR3_DMAR;
		USART1->CR1  = USART_CR1_UE | USART_CR1_TE;

		DMA1_Channel3->CCR |= DMA_CCR_EN;
	
		USART1->CR1  |=  USART_CR1_RE;
		USART1->TDR = 0xAB;

}


void usart_cycle(void){
	waiting = 1;
	if(delayCounter > 0){
		delayCounter--;
		if(delayCounter==0)
			state = IDLE;
	}
}

void usart_checkNewCommand(void){
	while(newData()){
			handleCharacter();
	}
}


uint8_t lastSession;
uint8_t session;

static void handleCharacter(void){
	static uint8_t commandCounter;
	uint8_t c = rec_buffer[getPointer()];
	delayCounter = DELAY_LENGTH;
	switch(state){
		case IDLE:
			commandCounter = 0;
			if(c == 0xAA){
				state = RECEIVE;
			}
			break;
		case RECEIVE:
				commandBuffer[commandCounter] = c;
				if(++commandCounter == COMMAND_LENGTH){
					handleCommand();
					state = IDLE;
				}
			break;			
		default:
			break;
	}
}

#define SESSION 	0
#define TARGET	 	1
#define COMMAND 	2
#define PAR1 			3	
#define PAR2 			4
#define PAR3 			5
#define CHECKSUM 	6

#define MODE_ADRESS 0x00
#define MODE_EXCLUDE 0x40
#define MODE_MODULO 0x80

static void handleCommand(void){
	static uint8_t oldSession;
	uint8_t offset,modulo;
	uint8_t session = commandBuffer[SESSION];
	uint8_t command = commandBuffer[COMMAND];
	uint8_t target = commandBuffer[TARGET];
	uint8_t par1 = commandBuffer[PAR1];
	uint8_t par2 = commandBuffer[PAR2];
	uint8_t par3 = commandBuffer[PAR3];
	uint8_t checksum = commandBuffer[CHECKSUM];
	uint8_t checksum_calc = target+session+command+par1+par2+par3;
	
	
	if(checksum == checksum_calc){
		if(oldSession==session) return;
		switch(target&0xC0){
			case MODE_MODULO:
					modulo = (target&0x07);
					offset = (target&0x38)>>3;
					if((device_adress)%modulo != offset)return;
				break;
			case MODE_EXCLUDE:
					if(device_adress == (target&0x3F))return;
				break;
			case MODE_ADRESS:
					if(device_adress != (target&0x3F) && (target&0x3F) != 0x3F)return;
			default:
					break;				
		}
		
		if(command&0x80){	//Init Animation Command
			animation_triggerBG(command&0x7F,par1,par2,par3);
		}
		else{							//Trigger Command
			animation_triggerFG(command&0x7F,par1,par2,par3);
		}
		waiting = 0;
	}
	
	
}

uint8_t usart_waiting(void){
		return waiting;
}

uint16_t getPointer(void){
	uint16_t oldPointer = tailPointer;
	tailPointer = (tailPointer+1)%BUFFER_SIZE;
	return oldPointer;
}


uint8_t newData(void){
	uint16_t pointer = BUFFER_SIZE-DMA1_Channel3->CNDTR;
	if(pointer != tailPointer){
		return 1;
	}
	else{
		return 0;
	}
}