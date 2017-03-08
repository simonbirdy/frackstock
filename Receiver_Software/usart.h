#ifndef _USART_H
#define _USART_H

#include <stdint.h>

void usart_initialize(void);
void usart_cycle(void);
uint8_t usart_waiting(void);
void usart_checkNewCommand(void);
#endif 