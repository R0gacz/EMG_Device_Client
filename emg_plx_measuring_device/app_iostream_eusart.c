#include "max30100.h"
#include "app_log.h"
#include "ble_app.h"

#include "sl_iostream.h"
#include "sl_iostream_init_instances.h"
#include "sl_iostream_handles.h"


uint32_t irACValueSqSum = 0;
uint32_t redACValueSqSum = 0;
uint16_t samplesRecorded = 0;
uint16_t pulsesDetected = 0;
uint32_t currentSaO2Value = 0;


Filter_Data irf = {0};
Filter_Data redf = {0};

void app_iostream_eusart_init(void)
{


#if !defined(__CROSSWORKS_ARM) && defined(__GNUC__)
  setvbuf(stdout, NULL, _IONBF, 0);
  setvbuf(stdin, NULL, _IONBF, 0);
#endif


  const char str1[] = "IOstream EUSART example\r\n\r\n";
//  sl_iostream_write(sl_iostream_vcom_handle, str1, strlen(str1));
  app_log_info(str1);

  sl_iostream_set_default(sl_iostream_vcom_handle);
  const char str2[] = "This is output on the default stream\r\n";
//  sl_iostream_write(SL_IOSTREAM_STDOUT, str2, strlen(str2));
  app_log_info(str2);


  init_i2c();
//   Read device ID
  uint8_t device_id;
  read_register(0xFF, &device_id, 1);
  printf("Device ID: 0x%02x\n", device_id);

  // Configure MAX30100

  setupTimer();
  configure_max30100();


}

void app_iostream_eusart_process_action(void) {

  if (max30100_read_data()){

  //int32_t ORG_IR = IR;
  //int32_t ORG_RED = RED;

  int32_t IRac = DCRemove(IR, &IRcw);
  int32_t REDac = DCRemove(RED, &REDcw);

  IRac = MeanDiff(IRac);
  REDac = MeanDiff(REDac);


  IRac = LowPassButterworthFilter(IRac, & irf);
  REDac = LowPassButterworthFilter(REDac, & redf);


  irACValueSqSum += IRac * IRac;
  redACValueSqSum += REDac * REDac;
  samplesRecorded++;

  if (IRac_plot == 1){
      //printf("IRac %u \n", (uint32_t)(IRac*IRac));
      //printf("irACValueSqSum %u \n", (uint32_t)(IRac*IRac));
      printf("IRac %d \n", IRac);
      //printf("REDac %d \n", REDac);
      printf("milis %u \n", milis);

  }
  if (detectPulse((uint32_t)(IRac*IRac))) {
    pulsesDetected++;

    float red_log_rms = logf(sqrtf(redACValueSqSum / samplesRecorded));
    float ir_log_rms = logf(sqrtf(irACValueSqSum / samplesRecorded));
    float ratioRMS = 0.0f;
    if (red_log_rms != 0.0f && ir_log_rms != 0.0f) {
      ratioRMS = red_log_rms / ir_log_rms;
    }
    currentSaO2Value = 110.0f - 13.0f * ratioRMS; // SPo2 value by pulse-rate
    refreshSa02CharacteristicValue(currentSaO2Value);

    if (debug == 1){
    printf("Current SaO2 value: %lu \n", currentSaO2Value);
    }



    if (pulsesDetected % RESET_SPO2_EVERY_N_PULSES == 0) {
      irACValueSqSum = 0;
      redACValueSqSum = 0;
      samplesRecorded = 0;
      //resetFIFO();
      if (debug == 1){
      printf("RESET_SPO2_EVERY_N_PULSES \n");
      }
    }
  }
  }

  BalanceIntestines();

}
