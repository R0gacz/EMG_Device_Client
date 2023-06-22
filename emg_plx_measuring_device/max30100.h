/*
 * max30100.h
 *
 *      Author: TL
 *
 */

#ifndef MAX30100_H_
#define MAX30100_H_


#include "em_device.h"
#include "em_chip.h"
#include "em_emu.h"
#include "em_core.h"
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <stdbool.h>
#include "em_i2c.h"
#include "em_timer.h"
#include "em_gpio.h"
#include "em_cmu.h"


#define debug 0
#define IRac_plot 0

#define I2C_ADDRESS 0x57 // MAX30100 default address
#define MAX30100_REG_FIFO_WRITE_POINTER         0x02
#define MAX30100_REG_FIFO_OVERFLOW_COUNTER      0x03
#define MAX30100_REG_FIFO_READ_POINTER          0x04
#define MAX30100_REG_FIFO_DATA                  0x05
#define FIFO_DEPTH                              0x10
#define REG_MODE_CONFIG     0x06
#define REG_PULSE_WIDTH     0x07
#define REG_IR_LED_CURRENT 0x09
//#define REG_SAMPLING_RATE            0x09
#define SAMPLING_RATE 0x01
#define MODE_CONFIG_VALUE   0x03
#define SPO2_CONFIG_VALUE   0x07
#define HIGH_RES_EN (1 << 6)
#define DefaultCurrent 0x06
#define MAGIC_ACCEPTABLE_INTENSITY_DIFF         70000
#define RED_LED_CURRENT_ADJUSTMENT_NS           100

#define RESET_SPO2_EVERY_N_PULSES     15
#define PULSE_BPM_SAMPLE_SIZE       15 //Moving average size
typedef enum PulseStateMachine{
   PULSE_IDLE,
   PULSE_TRACE_UP,
   PULSE_TRACE_DOWN
}PulseStateMachine;

#define MEAN_FILTER_SIZE        10 // Mean difference filter size
typedef struct{
   int32_t value[2];
}Filter_Data;


extern int32_t IRcw;
extern int32_t REDcw;
extern uint32_t IR;
extern uint32_t RED;



extern uint8_t redLEDCurrent;

extern volatile uint32_t milis;
#define PULSE_MIN_THRESHOLD         6000  //300 is good for finger, but for wrist you need like 20, and there is shitloads of noise
#define PULSE_MAX_THRESHOLD         100000
// uint32_t PULSE_MIN_THRESHOLD  =       100; 9000 80000 (good option for losely finger on sensor) 20000 200000 OK for trible rubber band

void setupTimer(void);
void resetFIFO(void);
bool max30100_read_data(void);
void configure_max30100(void);
void init_i2c(void);
void read_register(uint8_t reg, uint8_t *data, uint8_t len);
int32_t DCRemove(int32_t value,int32_t *cw);
int32_t MeanDiff(int32_t M);
int32_t LowPassButterworthFilter(int32_t value,Filter_Data *filter_data);
void BalanceIntestines(void);
bool detectPulse(uint32_t sensor_value);

#endif /* MAX30100_H_ */
