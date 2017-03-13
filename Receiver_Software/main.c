/*----------------------------------------------------------------------------
 * Name:    Blinky.c
 * Purpose: LED Flasher
 *----------------------------------------------------------------------------
 * This file is part of the uVision/ARM development tools.
 * This software may only be used under the terms of a valid, current,
 * end user licence from KEIL for a compatible version of KEIL software
 * development tools. Nothing else gives you the right to use this software.
 *
 * This software is supplied "AS IS" without warranties of any kind.
 *
 * Copyright (c) 2015 Keil - An ARM Company. All rights reserved.
 *----------------------------------------------------------------------------*/


#include "stm32f0xx.h"                  // Device header
#include "led.h"
#include "sc.h"
#include "usart.h"
#include "rfm.h"
#include "animation.h"
#include "info.h"

/*----------------------------------------------------------------------------
 * SystemCoreClockConfigure: configure SystemCoreClock using HSI
                             (HSE is not populated on Discovery board)
 *----------------------------------------------------------------------------*/
void SystemCoreClockConfigure(void) {

  RCC->CR |= ((uint32_t)RCC_CR_HSION);                     // Enable HSI
  while ((RCC->CR & RCC_CR_HSIRDY) == 0);                  // Wait for HSI Ready

  RCC->CFGR = RCC_CFGR_SW_HSI;                             // HSI is system clock
  while ((RCC->CFGR & RCC_CFGR_SWS) != RCC_CFGR_SWS_HSI);  // Wait for HSI used as system clock

  FLASH->ACR  = FLASH_ACR_PRFTBE;                          // Enable Prefetch Buffer
  FLASH->ACR |= FLASH_ACR_LATENCY;                         // Flash 1 wait state

  RCC->CFGR |= RCC_CFGR_HPRE_DIV1;                         // HCLK = SYSCLK
  RCC->CFGR |= RCC_CFGR_PPRE_DIV1;                         // PCLK = HCLK

  RCC->CR &= ~RCC_CR_PLLON;                                // Disable PLL

  //  PLL configuration:  = HSI/2 * 12 = 48 MHz
  RCC->CFGR &= ~(RCC_CFGR_PLLSRC | RCC_CFGR_PLLXTPRE | RCC_CFGR_PLLMULL);
  RCC->CFGR |=  (RCC_CFGR_PLLSRC_HSI_Div2 | RCC_CFGR_PLLMULL12);

  RCC->CR |= RCC_CR_PLLON;                                 // Enable PLL
  while((RCC->CR & RCC_CR_PLLRDY) == 0) __NOP();           // Wait till PLL is ready

  RCC->CFGR &= ~RCC_CFGR_SW;                               // Select PLL as system clock source
  RCC->CFGR |=  RCC_CFGR_SW_PLL;
  while ((RCC->CFGR & RCC_CFGR_SWS) != RCC_CFGR_SWS_PLL);  // Wait till PLL is system clock src
}


uint8_t device_adress = DEV_ADRESS;

/*----------------------------------------------------------------------------
 * main: initialize and start the system
 *----------------------------------------------------------------------------*/
int main (void) {
	uint32_t i = 0;
	
			//Colors
		
		// initialize peripherals
		SystemCoreClockConfigure();               // configure System Clock
		SystemCoreClockUpdate();
		RCC->AHBENR |= RCC_AHBENR_GPIOBEN | RCC_AHBENR_GPIOAEN;

		//Init Functions
		led_initialize();
		sc_initialize();
		usart_initialize();
		rfm_initialize();
		animation_initialize();

		while(1){
			GPIOA->BSRR = GPIO_BSRR_BS_12;
			animation_cycle();
			led_cycle();
			usart_cycle();
			GPIOA->BSRR = GPIO_BSRR_BR_12;

		
			
			while(sc_waitForCycle()==0 && usart_waiting()){
				usart_checkNewCommand();
			}				

	}
}
