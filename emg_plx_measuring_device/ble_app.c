#include "ble_app.h"
#include "gatt_db.h"
#include "sl_status.h"
#include "em_common.h"
#include "app_assert.h"
#include "sl_bluetooth.h"

static uint8_t advertising_set_handle = 0xff;

uint8_t timestampedValue[8] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
uint8_t timeInMs[4] = {0x00, 0x00, 0x00, 0x00,};



void refreshPulseCharacteristicValue(uint32_t currPulseValue){

  sl_status_t sc;
  size_t data_len;

  dataAndTimeToByteArray(currPulseValue);

  sc = sl_bt_gatt_server_write_attribute_value(gattdb_pulse_continous_measurement,
                                               0,
                                               sizeof(timestampedValue),
                                               timestampedValue);

  sc = sl_bt_gatt_server_read_attribute_value(gattdb_pulse_continous_measurement,
                                               0,
                                               sizeof(timestampedValue),
                                               &data_len,
                                               &timestampedValue);

  sl_bt_gatt_server_notify_all(gattdb_pulse_continous_measurement, sizeof(timestampedValue), timestampedValue);

  app_assert_status(sc);
}



void refreshSa02CharacteristicValue(uint32_t currSaO2Value){

  sl_status_t sc;
  size_t data_len;

  dataAndTimeToByteArray(currSaO2Value);
  sc = sl_bt_gatt_server_write_attribute_value(gattdb_sa02_continous_measurement,
                                               0,
                                               sizeof(timestampedValue),
                                               timestampedValue);

  sc = sl_bt_gatt_server_read_attribute_value(gattdb_sa02_continous_measurement,
                                               0,
                                               sizeof(timestampedValue),
                                               &data_len,
                                               &timestampedValue);


  sl_bt_gatt_server_notify_all(gattdb_sa02_continous_measurement, sizeof(timestampedValue), timestampedValue);

  app_assert_status(sc);
}


void dataAndTimeToByteArray(uint32_t value){

  timestampedValue[0] = (uint8_t)((value >> 24) & 0xFF);
  timestampedValue[1] = (uint8_t)((value >> 16) & 0xFF);
  timestampedValue[2] = (uint8_t)((value >> 8) & 0xFF);
  timestampedValue[3] = (uint8_t)(value & 0xFF);
  timestampedValue[4] =   timeInMs[0];
  timestampedValue[5] =   timeInMs[1];
  timestampedValue[6] =   timeInMs[2];
  timestampedValue[7] =   timeInMs[3];
}

void refreshTimestampCharacteristicValue(uint32_t currTimeInMs){

  sl_status_t sc;
  size_t data_len;

  timeInMs[0] = (uint8_t)((currTimeInMs >> 24) & 0xFF);
  timeInMs[1] = (uint8_t)((currTimeInMs >> 16) & 0xFF);
  timeInMs[2] = (uint8_t)((currTimeInMs >> 8) & 0xFF);
  timeInMs[3] = (uint8_t)(currTimeInMs & 0xFF);

  sc = sl_bt_gatt_server_write_attribute_value(gattdb_timestamp,
                                               0,
                                               sizeof(timeInMs),
                                               timeInMs);


  if (currTimeInMs%1000 == 0){


      sc = sl_bt_gatt_server_read_attribute_value(gattdb_timestamp,
                                                  0,
                                                  sizeof(timeInMs),
                                                  &data_len,
                                                  &timeInMs);

      sl_bt_gatt_server_notify_all(gattdb_timestamp, sizeof(timeInMs), timeInMs);

      app_assert_status(sc);
  }
}


void sl_bt_on_event(sl_bt_msg_t *evt)
{
  sl_status_t sc;
  bd_addr address;
  uint8_t address_type;
  uint8_t system_id[8];

  switch (SL_BT_MSG_ID(evt->header)) {
    // -------------------------------
    // This event indicates the device has started and the radio is ready.
    // Do not call any stack command before receiving this boot event!
    case sl_bt_evt_system_boot_id:

      // Extract unique ID from BT Address.
      sc = sl_bt_system_get_identity_address(&address, &address_type);
      app_assert_status(sc);

      // Pad and reverse unique ID to get System ID.
      system_id[0] = address.addr[5];
      system_id[1] = address.addr[4];
      system_id[2] = address.addr[3];
      system_id[3] = 0xFF;
      system_id[4] = 0xFE;
      system_id[5] = address.addr[2];
      system_id[6] = address.addr[1];
      system_id[7] = address.addr[0];

      sc = sl_bt_gatt_server_write_attribute_value(gattdb_system_id,
                                                   0,
                                                   sizeof(system_id),
                                                   system_id);
      app_assert_status(sc);

      // Create an advertising set.
      sc = sl_bt_advertiser_create_set(&advertising_set_handle);
      app_assert_status(sc);

      // Generate data for advertising
      sc = sl_bt_legacy_advertiser_generate_data(advertising_set_handle,
                                                 sl_bt_advertiser_general_discoverable);
      app_assert_status(sc);

      // Set advertising interval to 100ms.
      sc = sl_bt_advertiser_set_timing(
        advertising_set_handle,
        160, // min. adv. interval (milliseconds * 1.6)
        160, // max. adv. interval (milliseconds * 1.6)
        0,   // adv. duration
        0);  // max. num. adv. events
      app_assert_status(sc);
      // Start advertising and enable connections.
      sc = sl_bt_legacy_advertiser_start(advertising_set_handle,
                                         sl_bt_advertiser_connectable_scannable);
      app_assert_status(sc);
      break;

    // -------------------------------
    // This event indicates that a new connection was opened.
    case sl_bt_evt_connection_opened_id:
      break;

    // -------------------------------
    // This event indicates that a connection was closed.
    case sl_bt_evt_connection_closed_id:
      // Generate data for advertising
      sc = sl_bt_legacy_advertiser_generate_data(advertising_set_handle,
                                                 sl_bt_advertiser_general_discoverable);
      app_assert_status(sc);

      // Restart advertising after client has disconnected.
      sc = sl_bt_legacy_advertiser_start(advertising_set_handle,
                                         sl_bt_advertiser_connectable_scannable);
      app_assert_status(sc);
      break;

    ///////////////////////////////////////////////////////////////////////////
    // Add additional event handlers here as your application requires!      //
    ///////////////////////////////////////////////////////////////////////////

    // -------------------------------
    // Default event handler.
    default:
      break;
  }
}
