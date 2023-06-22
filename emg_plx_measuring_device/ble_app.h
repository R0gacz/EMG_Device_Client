#ifndef BLE_APP_H_
#define BLE_APP_H_

#include <stdio.h>

void refreshPulseCharacteristicValue(uint32_t currPulseValue);
void refreshSa02CharacteristicValue(uint32_t currSaO2Value);
void refreshTimestampCharacteristicValue(uint32_t currTimeInMsValue);

#endif /* BLE_H_ */
