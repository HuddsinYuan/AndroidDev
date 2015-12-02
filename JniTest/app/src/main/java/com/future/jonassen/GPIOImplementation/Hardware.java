
package com.future.jonassen.GPIOImplementation;

import android.util.Log;

/**
 * Created by Jonassen on 15/12/2.
 */
public class Hardware
{
    static public native int initGPIO(int pin);
    static public native int uninitGPIO(int pin);

    static public native int GPIOSetDir(int pin, int dir);
    static public native int GPIOGetDir(int pin);

    static public native int GPIOSetVal(int pin, int val);
    static public native int GPIOGetVal(int pin);

    static public native String getClanguageString();


    static {
    System.loadLibrary("HardwareSupport");
}
}



