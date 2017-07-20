#include "accel.h"
#include "stm32f0xx.h"  
#include <math.h>




ACCEL_TYPE currentData;


 
tFloatAvgFilter FilterAccelX;
tFloatAvgFilter FilterAccelY;
tFloatAvgFilter FilterAccelZ;
 
int32_t xy_square;




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
    
    //Init of floatingAvg
    InitFloatAvg(&FilterAccelX, 0);
    InitFloatAvg(&FilterAccelY, 0);
    InitFloatAvg(&FilterAccelZ, 0);
		
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

    //Add to floatAvrage
    AddToFloatAvg(&FilterAccelX, currentData.x);
    AddToFloatAvg(&FilterAccelY, currentData.y);
    AddToFloatAvg(&FilterAccelZ, currentData.z);
	
	currentData.x_avg = GetOutputValue(&FilterAccelX);
    currentData.y_avg = GetOutputValue(&FilterAccelY);
    currentData.z_avg = GetOutputValue(&FilterAccelZ);
    
    CalcAngle();
}


void CalcAngle(void){
    int32_t z_sqare = 0;
    xy_square = pow((int32_t)currentData.x_avg,2) + pow((int32_t)currentData.y_avg,2);
    if (currentData.z_avg > 0){
        z_sqare =  pow((int32_t)currentData.z_avg,2);
    }else{
        z_sqare =  -(pow((int32_t)currentData.z_avg,2));
    }

    currentData.angle = (uint8_t)((atan2(xy_square, z_sqare))* 81.169020977);

    currentData.angle_xy = (uint8_t)((atan2(fabs((float)currentData.x_avg), currentData.y_avg))* 57.17);
}

ACCEL_TYPE accel_getData(void){
	return currentData;
}


void InitFloatAvg(tFloatAvgFilter * io_pFloatAvgFilter, 
		  tFloatAvgType i_DefaultValue)
{   uint8_t i;
 	// Den Buffer mit dem Initialisierungswert fuellen:
    for (i=0; i<SIZE_OF_AVG;i++)
	{
		io_pFloatAvgFilter->aData[i] = i_DefaultValue;
	}
	// Der naechste Wert soll an den Anfang des Buffers geschrieben werden:
	io_pFloatAvgFilter->IndexNextValue = 0;
} 


void AddToFloatAvg(tFloatAvgFilter * io_pFloatAvgFilter,
		   tFloatAvgType i_NewValue)
{ 
	// Neuen Wert an die dafuer vorgesehene Position im Buffer schreiben.
	io_pFloatAvgFilter->aData[io_pFloatAvgFilter->IndexNextValue] =
		i_NewValue;
	// Der naechste Wert wird dann an die Position dahinter geschrieben.
	io_pFloatAvgFilter->IndexNextValue++;
	// Wenn man hinten angekommen ist, vorne wieder anfangen.
	io_pFloatAvgFilter->IndexNextValue = io_pFloatAvgFilter->IndexNextValue %SIZE_OF_AVG;
}  


tFloatAvgType GetOutputValue(tFloatAvgFilter * io_pFloatAvgFilter)
{
	tTempSumType TempSum = 0;
    uint8_t i;
    tFloatAvgType o_Result;
	// Durchschnitt berechnen
	for (i = 0; i < SIZE_OF_AVG; ++i)
	{
		TempSum += io_pFloatAvgFilter->aData[i];
	}
	// Der cast is OK, wenn tFloatAvgType und tTempSumType korrekt gewaehlt wurden.
	o_Result = (tFloatAvgType) (TempSum / SIZE_OF_AVG);
	return o_Result;
}