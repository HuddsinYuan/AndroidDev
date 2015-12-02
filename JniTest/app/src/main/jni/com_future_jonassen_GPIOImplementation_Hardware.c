#include "com_future_jonassen_GPIOImplementation_Hardware.h"

JNIEXPORT jstring JNICALL Java_com_future_jonassen_GPIOImplementation_Hardware_getClanguageString
  (JNIEnv *env, jobject obj) {
    return (*env)->NewStringUTF(env, "THIS IS A TEST");
  }

