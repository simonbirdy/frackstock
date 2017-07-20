#ifndef _ACCEL_H
#define _ACCEL_H

#include <stdint.h>


#define SIZE_OF_AVG  10

typedef struct{
	int8_t x;
	int8_t y;
	int8_t z;
    int8_t x_avg;
    int8_t y_avg;
    int8_t z_avg;
    uint8_t angle;
    uint8_t angle_xy;
}ACCEL_TYPE;

typedef int8_t tFloatAvgType;
typedef int16_t tTempSumType;
typedef struct
 {
	tFloatAvgType aData[SIZE_OF_AVG];
	uint8_t IndexNextValue;
 } tFloatAvgFilter;

void accel_init(void);
void accel_cycle(void);

ACCEL_TYPE accel_getData(void);

// Initialisiert das Filter mit einem Startwert.
void InitFloatAvg(tFloatAvgFilter * io_pFloatAvgFilter,
		  tFloatAvgType i_DefaultValue);

// Schreibt einen neuen Wert in das Filter.
void AddToFloatAvg(tFloatAvgFilter * io_pFloatAvgFilter,
		   tFloatAvgType i_ui16NewValue);
// Berechnet den Durchschnitt aus den letzten SIZE_OF_AVG eingetragenen Werten.
tFloatAvgType GetOutputValue(tFloatAvgFilter * io_pFloatAvgFilter);

void CalcAngle(void);

#endif