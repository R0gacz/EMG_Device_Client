//#include "em_rtcc.h"
//#include "gatt_db.h"
//#include "app_log.h"
//#include "rtc_driver.h"
//
//
//#define RTCC_CLOCK              cmuSelect_LFXO    // RTCC clock source
//#define RTCC_PRS_CH             0                 // RTCC PRS output channel
//#define WAKEUP_INTERVAL_MS      500               // Wake up interval in ms
//
//static uint8_t currDataTime[10] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,};

//
//
//void rtcc_init(){
//  RTCC_Init_TypeDef rtccInit = RTCC_INIT_DEFAULT;
//  RTCC_CCChConf_TypeDef rtccInitCompareChannel = RTCC_CH_INIT_COMPARE_DEFAULT;
//
//  RTCC_Init(&rtccInit);
//  RTCC_ChannelInit(1, &rtccInitCompareChannel);
//}
//
//
//void update_current_time_characteristic(void)
//{
//  sl_status_t sc;
////
////
////  uint32_t rtccCounter = RTCC_CombinedCounterGet();
//////  app_log_info("");
//  sc = sl_bt_gatt_server_write_attribute_value(gattdb_current_time,
//                                               0,
//                                               sizeof(currDataTime),
//                                               currDataTime);
////
//  sl_bt_gatt_server_notify_all(gattdb_current_time, sizeof(currDataTime), currDataTime);
////
////  currDataTime[0] = i;
////  currDataTime[1] = j++;
////  currDataTime[2] = 4;
////  currDataTime[3] = 23;
////  currDataTime[4] = 22;
////  currDataTime[5] = 11;
////  currDataTime[6] = i++;
////  currDataTime[7] = i++;
////  currDataTime[8] = i
////  currDataTime[9] = i;
//
////
////
//  if (sc == SL_STATUS_OK){
//    app_log_info("Attribute Current Time written: 0x%02x", (int)rtccCounter);
//  }
//}
//
//
//void setupRtcc(CMU_Select_TypeDef rtccClock)
//{
//  RTCC_Init_TypeDef rtccInit = RTCC_INIT_DEFAULT;
//  RTCC_CCChConf_TypeDef rtccInitCompareChannel = RTCC_CH_INIT_COMPARE_DEFAULT;
//
//  // Check RTCC clock source
//  if (rtccClock == cmuSelect_LFXO) {
//    // Initialize LFXO with specific parameters
//    CMU_LFXOInit_TypeDef lfxoInit = CMU_LFXOINIT_DEFAULT;
//    CMU_LFXOInit(&lfxoInit);
//  }
//
//  // Setting RTCC clock source
//  CMU_ClockSelectSet(cmuClock_RTCCCLK, rtccClock);
//
//  // Enable RTCC bus clock
//  CMU_ClockEnable(cmuClock_RTCC, true);
//
//  // Initialize Capture Compare Channel 1 to toggle PRS output on compare match
//  rtccInitCompareChannel.compMatchOutAction = rtccCompMatchOutActionToggle;
//  RTCC_ChannelInit(1, &rtccInitCompareChannel);
//
//  /**********************************************************************//**
//   * Set RTCC Capture Compare Value to the frequency of the
//   * LFXO/ULFRCO(minus 1).
//   *
//   * When the pre-counter (RTCC_PRECNT) matches this value, the counter
//   * (RTCC_CNT) increments by one.
//   *************************************************************************/
//  if (rtccClock == cmuSelect_ULFRCO) {
//    //Setting the CC1 compare value of the RTCC
//    RTCC_ChannelCCVSet(1, (1000 * WAKEUP_INTERVAL_MS) / 1000 - 1);
//  } else {
//    //Setting the CC1 compare value of the RTCC
//    RTCC_ChannelCCVSet(1, (32768 * WAKEUP_INTERVAL_MS) / 1000 - 1);
//  }
//
//  // Initialize the RTCC
//  rtccInit.cntWrapOnCCV1 = true;        // Clear counter on CC1 compare match
//  rtccInit.presc = rtccCntPresc_1;      // Prescaler 1
//
//  // Initialize and start counting
//  RTCC_Init(&rtccInit);
//
//  // Enabling Interrupt from RTCC CC1
//  RTCC_IntEnable(RTCC_IEN_CC1);
//  NVIC_ClearPendingIRQ(RTCC_IRQn);
//  NVIC_EnableIRQ(RTCC_IRQn);
//}

