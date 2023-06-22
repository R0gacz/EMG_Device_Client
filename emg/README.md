# Budowanie

Budowanie wymaga SDK BL602, którego ścieżka powinna być ustawiona zmiennej
środowiskowej `BL602_SDK_PATH`.

```sh
export BL60X_SDK_PATH="$(realpath ../bl_iot_sdk)"
git clone https://github.com/pine64/bl_iot_sdk "${BL602_SDK_PATH}"
make CONFIG_CHIP_NAME=BL602 CONFIG_LINK_ROM=1 -j4
```

# Flashowanie

Po zbudowaniu i instalacji [blflash](https://github.com/spacemeowx2/blflash):

```sh
blflash --port /dev/ttyUSB3 build_out/emg.bin
```
