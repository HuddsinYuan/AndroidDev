package com.future.jonassen.pump;

import android.util.Log;
//import com.bjw.gpio.GPIOJNI;
//import com.friendlyarm.AndroidSDK.HardwareControler;

/**
 * Created by Jonassen on 16/1/5.
 */

public class tPumpControl {
    private static final String TAG = "PumpControl";

    private static final int OPEN_STATE = 1;
    private static final int CLOSE_STATE = 0;

    private static final String CONTROL_GROUP = "GPH2";
    private static final int DIR_P = 0;
    private static final int DIR_N = 1;
    private static final int ENB_P = 2;
    private static final int ENB_N = 3;

    private static final String VALVE_GROUP = "GPH3";
    private static final int VALVE1 = 0;
    private static final int VALVE2 = 1;
    private static final int VALVE3 = 2;
    private static final int VALVE4 = 3;


    private boolean enablestate;
    private boolean waystate;
    private int valvesel;

    private static final int MLFreq_Inc1 = 5000;
    private static final int MLFreq_Inc2 = 10000;
    private static final int MLFreq_Inc3 = 15000;
    private static final int MLFreq_Inc4 = 20000;
    private static final int MLFreq = (int) (29538 * 1.02);
    private static final int ULFreq = (int) (31 * 1.02);

    public void PumpIOInit() {
//        GPIOJNI.SetCfgpin(CONTROL_GROUP, DIR_P, 1);
//        GPIOJNI.SetCfgpin(CONTROL_GROUP, DIR_N, 1);
//        GPIOJNI.SetCfgpin(CONTROL_GROUP, ENB_P, 1);
//        GPIOJNI.SetCfgpin(CONTROL_GROUP, ENB_N, 1);
//
//        GPIOJNI.SetCfgpin(VALVE_GROUP, VALVE1, 1);
//        GPIOJNI.SetCfgpin(VALVE_GROUP, VALVE2, 1);
//        GPIOJNI.SetCfgpin(VALVE_GROUP, VALVE3, 1);
//        GPIOJNI.SetCfgpin(VALVE_GROUP, VALVE4, 1);



/*
    EINT16  -- GPH2_0 -- DIR+
    EINT17  -- GPH2_1 -- DIR-
    EINT18  -- GPH2_2 -- ENB+
    EINT19  -- GPH2_3 -- ENB-

    EINT24  -- GPH3_0 -- VALVE1
    EINT25  -- GPH3_1 -- VALVE2
    EINT26  -- GPH3_2 -- VALVE3
    EINT27  -- GPH3_3 -- VALVE4

 */
//        HardwareControler.PWMStop();

        Log.i(TAG, "水泵初始化完成.");
    }

    /*  DIR+ -->5V
        DIR- -->GND  顺时针

        ELSE  逆时针
     */
    public void PumpDirSetting(boolean way) {
        waystate = way;
        if (way) {
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, DIR_P, OPEN_STATE);
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, DIR_N, CLOSE_STATE);
            Log.i(TAG, "水泵方向：正向[对着人]。 +Way = " + String.valueOf(way));
        } else {
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, DIR_P, CLOSE_STATE);
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, DIR_N, OPEN_STATE);
            Log.i(TAG, "水泵方向：反向[背着人] +Way = " + String.valueOf(way));
        }

    }

    /* ENA+ ---> 5V
    *  ENA- ---> GND   停止
    *
    *
    *  ELSE   转
    */
    public void PumpEnbSetting(boolean enb) {
        if (enb) {
            enablestate = enb;
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, ENB_P, CLOSE_STATE);
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, ENB_N, OPEN_STATE);
            Log.i(TAG, "Pump Enabled");
        } else {
            enablestate = enb;
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, ENB_P, OPEN_STATE);
//            GPIOJNI.WriteGPIO(CONTROL_GROUP, ENB_N, CLOSE_STATE);
//            HardwareControler.PWMStop();

            Log.i(TAG, "Pump Disenabled");
        }

    }

    public boolean getPumpEnbState() {
        return enablestate;
    }

    public void PumpValveSel(int num) {
        valvesel = num;
        switch (num) {
            case VALVE1:
                CloseAllValve();
//                GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE1, OPEN_STATE);
                Log.i(TAG, "VALVE 1 OPEN.");
                break;
            case VALVE2:
                CloseAllValve();
//                GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE2, OPEN_STATE);
                Log.i(TAG, "VALVE 2 OPEN.");
                break;
            case VALVE3:
                CloseAllValve();
//                GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE3, OPEN_STATE);
                Log.i(TAG, "VALVE 3 OPEN.");
                break;
            case VALVE4:
                CloseAllValve();
//                GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE4, OPEN_STATE);
                Log.i(TAG, "VALVE 4 OPEN.");
                break;
        }

    }

//    public void PumpFluxML()
//    {
//        HardwareControler.PWMStop();
//
//        HardwareControler.PWMPlay(2000);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(4000);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(6000);
//        Log.i(TAG, "PumpFluxML");
//    }


    public void PumpFluxML() {
//        HardwareControler.PWMStop();

//        HardwareControler.PWMPlay(MLFreq_Inc1);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(MLFreq_Inc2);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(MLFreq_Inc3);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(MLFreq_Inc4);
//        GPIOJNI.uPause(1000);
//        HardwareControler.PWMPlay(MLFreq);
        Log.i(TAG, "PumpFluxML");
        Log.i(TAG, "Frequeny is " +String.valueOf(MLFreq));

    }

    public void PumpFluxUL() {
//        HardwareControler.PWMStop();
//        HardwareControler.PWMPlay(ULFreq);
        Log.i(TAG, "PumpFluxUL");
        Log.i(TAG, "Frequency is " + String.valueOf(ULFreq));
    }

    public void CloseAllValve() {
//        PumpEnbSetting(false);
//        GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE1, CLOSE_STATE);
//        GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE2, CLOSE_STATE);
//        GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE3, CLOSE_STATE);
//        GPIOJNI.WriteGPIO(VALVE_GROUP, VALVE4, CLOSE_STATE);
        Log.i(TAG, "Close All Valve.");
//        PumpEnbSetting(true);
    }

    public void OutputInfo() {
        String s  = "当前" + String.valueOf(valvesel) + "号阀门打开，水泵运行状态为" +
                String.valueOf(enablestate) + " 抽水方向为" + String.valueOf(waystate);
        Log.i("SecondDetect", s);
    }
}
