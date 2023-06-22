//#include "gatt_db.h"
//#include "app_log.h"
//#include "battery_level.h"
//
//#include "em_common.h"
//#include "app_assert.h"
//#include "sl_bluetooth.h"
//#include "sl_bt_api.h"
//#include "sl_sleeptimer.h"
//#include "em_device.h"
//#include "em_cmu.h"
//#include "em_adc.h"
//#include "em_iadc.h"
//
//#define VINATT(ATT_FACTOR) ATT_FACTOR << _IADC_Single
//#define VREFATT(ATT_FACTOR)ATT_FACTOR << _ADC_SINGLECTRLX_VREFATT_SHIFT
//
//#define MICROVOLTS_PER_STEP    1221
//#define TICKS_PER_SECOND       32768
//#define ADC_READ               2
//#define BATTERY_READ_INTERVAL  1 * TICKS_PER_SECOND
//#define REPEATING              0
//
//#define SLEEP_TIMER_TRIGGER    0x01
//
//sl_sleeptimer_timer_handle_t battery_voltage_sleep_timer;
//
//static uint32_t battery_voltage = 0;
//static uint8_t battery_voltage_percentage = 0;
//
//
//void battery_app_init(){
//
//}
//
//static void init_adc_for_supply_measurement(void)
//{
//  CMU_ClockEnable(cmuClock_IADC0, true);
//  IADC_Init_t IADC_Defaults = IADC_INIT_DEFAULT;
//
//  IADC_InitSingle_t init_single = IADC_INITSINGLE_DEFAULT;
//  init_single.negSel = iadcNegSelVSS;
//  init_single.posSel = iadcPosSelAVDD;
//  init_single.reference = iadcRef5V;
//
//  // Start with defaults
//  IADC_init(IADC0, &IADC_Defaults);
//
//  IADC_initSingle(IADC0, &init_single);
//  IADC0->SINGLECTRLX = VINATT(12) | VREFATT(6);
//}
//
///**
// * @brief Make one ACD conversion
// * @return Single ADC reading
// */
//static uint32_t read_adc(void)
//{
//  IADC_Start(IADC0, iadcCmdStartSingle);
//  while ((IADC0->STATUS & IADC_STATUS_SINGLEQEN) == 0) {}
//  return IADC_DataSingleGet(IADC0);
//}
//
///**
// * @brief Read supply voltage raw reading from ADC and return reading in
// *   millivolts.
// * @return Supply Voltage in millivolts.
// * VFS = 2*VREF*VREFATTF/VINATTF, where
// * VREF is selected in the VREFSEL bitfield, and
// * VREFATTF (VREF attenuation factor) = (VREFATT+6)/24 when VREFATT is less than
// *   13, and (VREFATT-3)/12 when VREFATT is
// * greater than or equal to 13, and
// * VINATTF (VIN attenuation factor) = VINATT/12, illegal settings: 0, 1, 2
// * VREFATTF = (6+6)/24 = 0.5
// * VINATTF = 12/12
// * VFS = 2*5.0*0.5 = 5.0
// * 1.221 mV/step
// */
//static uint32_t read_supply_voltage(void)
//{
//  uint32_t raw_reading = read_adc();
//  uint32_t supply_voltage_mV = raw_reading * MICROVOLTS_PER_STEP / 1000UL;
//  return supply_voltage_mV;
//}
//
///**
// *  function: sleep_timer_callback
// *  will be called when the sleep timer expire
// */
//void sleep_timer_callback(sl_sleeptimer_timer_handle_t *handle,
//                          void __attribute__((unused)) *data)
//{
//  if (handle == &battery_voltage_sleep_timer) {
//    // Send the event to bluetooth stack and leave the processing for the event
//    //   loop
//    sl_bt_external_signal(SLEEP_TIMER_TRIGGER);
//  }
//}
//
///**
// * @brief Get the battery voltage status,
// *  including battery capacity and battery percent
// *
// */
//static void read_supply_status(void)
//{
//  battery_voltage = read_supply_voltage();
//  battery_voltage_percentage = (uint8_t) (battery_voltage / 33);
//  app_log("Battery voltage: %d (mV)\n", (int)battery_voltage);
//  app_log("Battery voltage percentage: %d %%\n",
//          (int)battery_voltage_percentage);
//}
//
///**
// * @brief Initialize for application.
// *
// */
//static void reporting_battery_app_init(void)
//{
//  sl_status_t sc;
//
//  /* init adc for reading battery voltage */
//  init_adc_for_supply_measurement();
//
//  /* start sleep timer for periodic reading battery voltage */
//  sc = sl_sleeptimer_start_periodic_timer_ms(&battery_voltage_sleep_timer,
//                                             1000,
//                                             sleep_timer_callback,
//                                             (void *)NULL,
//                                             0,
//                                             0);
//  app_assert(sc == SL_STATUS_OK,
//             "[E: 0x%04x] Failed to start sleep timer \r\n",
//             (int)sc);
//}
