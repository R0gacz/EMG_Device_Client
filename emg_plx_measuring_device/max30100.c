#include "max30100.h"
#include "gatt_db.h"
#include "sl_status.h"
#include "ble_app.h"

sl_status_t sc;

int32_t IRcw = 0;
int32_t REDcw = 0;

uint32_t IR = 0;
uint32_t RED = 0;

uint8_t redLEDCurrent = DefaultCurrent;

int32_t msum = 0;
int32_t mvalues[MEAN_FILTER_SIZE];
int32_t mindex = 0;
int32_t mcount = 0;

volatile uint32_t lastREDLedCurrentCheck = 0;

volatile uint32_t milis = 0;

uint8_t currentPulseDetectorState = PULSE_IDLE;
uint32_t lastBeatThreshold = 0;
uint32_t currentBPM;
uint32_t valuesBPM[PULSE_BPM_SAMPLE_SIZE] = {0};
uint32_t valuesBPMSum = 0;
uint8_t valuesBPMCount = 0;
uint8_t bpmIndex = 0;

I2C_TypeDef *i2c = I2C1; // Use I2C0 interface



void TIMER0_IRQHandler(void) {
    if (TIMER_IntGet(TIMER0) & TIMER_IF_OF) {
        milis++;
        TIMER_IntClear(TIMER0, TIMER_IF_OF);
        refreshTimestampCharacteristicValue(milis);
    }
}

void initMilisTimer(void) {
    // Set the timer period for 1 microsecond, this is too quick timer.
    uint32_t timerPeriod = CMU_ClockFreqGet(cmuClock_TIMER0) /1000 ; // timer for 1 second corrected to mili seconds
    CMU_ClockFreqGet(cmuClock_TIMER0);

    TIMER_TopSet(TIMER0, timerPeriod);

    TIMER_IntEnable(TIMER0, TIMER_IF_OF);
}

void setupTimer(void){
  CMU_ClockEnable(cmuClock_GPIO, true);
  CMU_ClockEnable(cmuClock_TIMER0, true);

  TIMER_Init_TypeDef timerInit = TIMER_INIT_DEFAULT;
  timerInit.prescale = timerPrescale1;
  TIMER_Init(TIMER0, &timerInit);

  TIMER_IntEnable(TIMER0, TIMER_IF_OF);

  NVIC_SetPriority(TIMER0_IRQn, 1);  // Set the interrupt priority (if needed)
  NVIC_EnableIRQ(TIMER0_IRQn);       // Enable the interrupt

  // Start the timer
  TIMER_Enable(TIMER0, true);
  initMilisTimer();
}

void init_i2c(void) {
  CMU_ClockEnable(cmuClock_I2C1, true);

  GPIO_PinModeSet(gpioPortD, 2, gpioModeWiredAndPullUpFilter, 1); // SDA
  GPIO_PinModeSet(gpioPortD, 3, gpioModeWiredAndPullUpFilter, 1); // SCL

  I2C_Init_TypeDef i2cInit = I2C_INIT_DEFAULT;
  i2cInit.refFreq = 0;
  i2cInit.freq = I2C_FREQ_STANDARD_MAX;
  i2cInit.clhr = i2cClockHLRStandard;

  I2C_Init(i2c, &i2cInit);
  I2C_SlaveAddressSet(i2c, I2C_ADDRESS);
}

void read_register(uint8_t reg, uint8_t *data, uint8_t len) {
  // Write register address to device
  I2C_TransferSeq_TypeDef seq;
  I2C_TransferReturn_TypeDef ret;
  uint8_t reg_addr[1] = { reg };
  seq.addr = I2C_ADDRESS << 1;
  seq.flags = I2C_FLAG_WRITE_READ;
  seq.buf[0].data = reg_addr;
  seq.buf[0].len = 1;
  seq.buf[1].data = data;
  seq.buf[1].len = len;
  ret = I2C_TransferInit(i2c, &seq);
  while (ret == i2cTransferInProgress) {
    ret = I2C_Transfer(i2c);
  }
}

void write_register(uint8_t reg, uint8_t data)

  {
  uint8_t written;
  read_register(reg, &written, 2);
  uint8_t tx_data[2] = { reg, data };
  I2C_TransferSeq_TypeDef seq;
  I2C_TransferReturn_TypeDef ret;
  seq.addr = I2C_ADDRESS << 1;
  seq.flags = I2C_FLAG_WRITE;

  seq.buf[0].data = tx_data;
  seq.buf[0].len = 2;


  ret = I2C_TransferInit(i2c, &seq);

  while (ret == i2cTransferInProgress) {
    ret = I2C_Transfer(i2c);
  }

  uint8_t written2;
  read_register(reg, &written2, 1);
}

void setLedsPulseWidth(void){
  uint8_t previous;
  read_register(SPO2_CONFIG_VALUE, &previous, 1);
  write_register(SPO2_CONFIG_VALUE, (previous & 0xfc) | 0x02);
};

void setSamplingRate(void){
  uint8_t previous;
  read_register(SPO2_CONFIG_VALUE, &previous, 1);
  write_register(SPO2_CONFIG_VALUE, (previous & 0xe3) | (SAMPLING_RATE << 2));
}

void setLedCurrent(void){
  write_register(REG_IR_LED_CURRENT, (0x08 << 4) | DefaultCurrent);
}

void setHighRes(void){
  uint8_t previous;
  read_register(SPO2_CONFIG_VALUE, &previous, 1);
  write_register(SPO2_CONFIG_VALUE, previous |HIGH_RES_EN);
}

void resetFIFO(void) {

  write_register(MAX30100_REG_FIFO_WRITE_POINTER, 0);
  write_register(MAX30100_REG_FIFO_READ_POINTER, 0);
  write_register(MAX30100_REG_FIFO_OVERFLOW_COUNTER, 0);

}

void configure_max30100(void){
write_register(REG_MODE_CONFIG, MODE_CONFIG_VALUE);
//write_register(REG_IR_LED_CURRENT, DefaultCurrent << 4 | DefaultCurrent);
setLedsPulseWidth();
setSamplingRate();
setLedCurrent();
//write_register(SPO2_CONFIG_VALUE, (1<<2) | 3);
setHighRes();
resetFIFO();
}

bool max30100_read_data(void) {
  // Read the pointers registers to check if new data is available
  uint8_t toRead;
  uint8_t toRead1;
  uint8_t toRead2;
  read_register(MAX30100_REG_FIFO_WRITE_POINTER, &toRead1, 1);
  read_register(MAX30100_REG_FIFO_READ_POINTER, &toRead2, 1);
  toRead = toRead1 - toRead2;

  if (toRead != 0) {
      uint8_t fifo_data[4];
     read_register(MAX30100_REG_FIFO_DATA, fifo_data, 4);
     IR = ( fifo_data[0] << 8) | fifo_data[1];
     RED = ( fifo_data[2] << 8) | fifo_data[3];
     return true;
  }

  return false;

  }

int32_t DCRemove(int32_t value,int32_t *cw)
{
   int32_t oldcw = *cw;
   *cw = value + 0.94 * *cw;
   return *cw - oldcw;
}

int32_t MeanDiff(int32_t M)
{
   int32_t avg = 0;

   msum -= mvalues[mindex];
   mvalues[mindex] = M;
   msum += mvalues[mindex];

   mindex++;
   mindex = mindex % MEAN_FILTER_SIZE;

   if(mcount < MEAN_FILTER_SIZE){
      mcount++;
   }

   avg = msum / mcount;
   return avg - M;
}

bool detectPulse(uint32_t sensor_value)
{
  static uint32_t prev_sensor_value = 0;
  static uint8_t values_went_down = 0;
  static uint32_t currentBeat = 0;
  static uint32_t lastBeat = 0;

   if(sensor_value > PULSE_MAX_THRESHOLD)
   {
      currentPulseDetectorState = PULSE_IDLE;
      prev_sensor_value = 0;
      lastBeat = 0;
      currentBeat = 0;
      values_went_down = 0;
      lastBeatThreshold = 0;

      return false;
   }

   switch(currentPulseDetectorState)
   {
      case PULSE_IDLE:
   if(sensor_value >= PULSE_MIN_THRESHOLD ) {
      currentPulseDetectorState = PULSE_TRACE_UP;
      values_went_down = 0;
   }
   break;
      case PULSE_TRACE_DOWN:
   if(sensor_value < prev_sensor_value)
   {
      values_went_down++;
   }


   if(sensor_value < PULSE_MIN_THRESHOLD)
   {
      currentPulseDetectorState = PULSE_IDLE;
   }
   break;


      case PULSE_TRACE_UP:
   if(sensor_value > prev_sensor_value)
   {
      currentBeat = milis;
      lastBeatThreshold = sensor_value;
   }
   else
   {
      uint32_t beatDuration = currentBeat - lastBeat;
      lastBeat = currentBeat;

      uint32_t rawBPM = 0;
      if(beatDuration > 0){
         rawBPM = 60000 / beatDuration;

      valuesBPM[bpmIndex] = rawBPM;
      valuesBPMSum = 0;
      for(int i=0; i<PULSE_BPM_SAMPLE_SIZE; i++)
      {
         valuesBPMSum += valuesBPM[i];
      }
//      if (debug == 1){
//      printf("rawBPM: %u \n", rawBPM);
//      }
      bpmIndex++;
      bpmIndex = bpmIndex % PULSE_BPM_SAMPLE_SIZE;

      if(valuesBPMCount < PULSE_BPM_SAMPLE_SIZE)
         valuesBPMCount++;

      currentBPM = valuesBPMSum / valuesBPMCount;


      currentPulseDetectorState = PULSE_TRACE_DOWN;

      refreshPulseCharacteristicValue(currentBPM);
      if (debug == 1){
      printf(" currentBPM %lu time \n", currentBPM);
//      printf(" currentTime %lu time \n", milis);
      }
      return true;
      }
      else{
          return false;
      }
   }
   break;
   }


   prev_sensor_value = sensor_value;
   return false;
}

int32_t LowPassButterworthFilter(int32_t value,Filter_Data *filter_data)
{
   filter_data->value[0] = filter_data->value[1];
   //filter_data->value[1] = filter_data->value[2];
   //Fs = 100Hz and Fc = 10Hz
   filter_data->value[1] = (2.452372752527856026e-1 * value) + (0.50952544949442879485 * filter_data->value[0]);

   //Fs = 100Hz and Fc = 4Hz
   //filter_data->value[1] = (1.367287359973195227e-1 * value) + (0.72654252800536101020 * filter_data->value[0]); //Very precise butterworth filter
   //filter_data->value[2] = (1.367287359973195227e-1 * value) + (0.3 * filter_data->value[0]) + (0.3 * filter_data->value[1]);
   return filter_data->value[0] + filter_data->value[1];
}

void BalanceIntestines(void){
  if( milis - lastREDLedCurrentCheck >= RED_LED_CURRENT_ADJUSTMENT_NS) {
        if( IRcw - REDcw > MAGIC_ACCEPTABLE_INTENSITY_DIFF && redLEDCurrent < 0x0F) {
     redLEDCurrent++;
     write_register(REG_IR_LED_CURRENT, (redLEDCurrent << 4) | DefaultCurrent);
   if (debug == 1){
     printf("balance intestines %u \n", redLEDCurrent);
     }
        }
        else if(REDcw - IRcw > MAGIC_ACCEPTABLE_INTENSITY_DIFF && redLEDCurrent > 0) {
     redLEDCurrent--;
     write_register(REG_IR_LED_CURRENT, (redLEDCurrent << 4) | DefaultCurrent);
     if (debug == 1){
     printf("balance intestines %u \n", redLEDCurrent);
     }
     }
       lastREDLedCurrentCheck = milis;
}
}



