LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gpiodriver
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	/Users/Jonassen/Project/AndroidDev/Pump/gpiodriver/src/main/jni/Android.mk \
	/Users/Jonassen/Project/AndroidDev/Pump/gpiodriver/src/main/jni/bjwgpio.c \

LOCAL_C_INCLUDES += /Users/Jonassen/Project/AndroidDev/Pump/gpiodriver/src/main/jni
LOCAL_C_INCLUDES += /Users/Jonassen/Project/AndroidDev/Pump/gpiodriver/src/release/jni

include $(BUILD_SHARED_LIBRARY)
