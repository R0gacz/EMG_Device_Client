PROJECT_NAME := emg
PROJECT_PATH := $(abspath .)
PROJECT_BOARD := evb
export PROJECT_PATH PROJECT_BOARD
#CONFIG_TOOLPREFIX :=

-include ./proj_config.mk

ifeq ($(origin BL60X_SDK_PATH), undefined)
BL60X_SDK_PATH_GUESS ?= $(shell pwd)
BL60X_SDK_PATH ?= $(BL60X_SDK_PATH_GUESS)/../..
$(info ****** Please SET BL60X_SDK_PATH ******)
$(info ****** Trying SDK PATH [$(BL60X_SDK_PATH)])
endif

# NimBLE Porting Layer needed for mulitasking functions
COMPONENTS_NPL     := nimble-porting-layer 
COMPONENTS_BLSYS   := bltime blfdt blmtd bloop loopadc looprt loopset
COMPONENTS_VFS     := romfs
COMPONENTS_BLE     := 

INCLUDE_COMPONENTS += freertos_riscv_ram bl602 bl602_std hal_drv vfs yloop utils cli blog blog_testc
INCLUDE_COMPONENTS += easyflash4
INCLUDE_COMPONENTS += $(COMPONENTS_NETWORK)
INCLUDE_COMPONENTS += $(COMPONENTS_BLSYS)
INCLUDE_COMPONENTS += $(COMPONENTS_VFS)
INCLUDE_COMPONENTS += $(COMPONENTS_NPL)
INCLUDE_COMPONENTS += $(PROJECT_NAME)

include $(BL60X_SDK_PATH)/make_scripts_riscv/project.mk
