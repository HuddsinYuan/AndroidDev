package com.future.jonassen.pump;

/**
 * Created by Jonassen on 16/1/6.
 */
/*
   每个步骤需要的参数，然后统一进行线程的Sleep管理

   需要配置的参数有：
   步骤： 染色 水洗 脱色
   循环：按照厂家要求定制，只有在第二次循环时可以定制2~4循环
   时间：每一个步骤的时间
   阀门：每一个步骤打开的阀门
   方向：每一个步骤所需求的方向
*/

public class StepValueControl {

//    private static final int STEP_COLOR = 40002;
//    private static final int STEP_COLOR_BACK = 40003;
//    private static final int STEP_WASH = 40004;
//    private static final int STEP_WASH_BACK = 40005;
//    private static final int STEP_DECOLOR = 40006;
//    private static final int STEP_DECOLOR_BACK = 40007;


    int cycle;
    int wait_time;
    int pump_ml_time;
    int pump_ul_time;
    int valve;
    boolean isMLTime;
    boolean isWaitTime;
    boolean dir;
    boolean set_ok;
    String s;

    Object lock = new Object();

    public StepValueControl() {
        s = "";
        cycle = 0;
        wait_time = 0;
        pump_ml_time = 0;
        pump_ul_time = 0;
        isMLTime = true;
        isWaitTime = false;
        valve = 0;
        dir = false;
        set_ok = false;
    }

//    public void getCurrentStepParam(int currentStep) {
////        StepValueControl svc = new StepValueControl();
//        switch (currentStep) {
//
//            case STEP_COLOR:
//                this.s = String.valueOf(R.string.ColorStep);
//                this.cycle =1;
//                this.time = 120;
//                this.valve = 4;
//                this.dir = true;
//                break;
//
//            case STEP_COLOR_BACK:
//                this.s = String.valueOf(R.string.ColorDone);
//                this.cycle = 0;
//                this.time = 0;
//                this.valve = 4;
//                this.dir = false;
//                break;
//
//            case STEP_WASH:
//                s = "STEP_WASH";
//                break;
//
//            case STEP_WASH_BACK:
//                s = "STEP_WASH_BACK";
//                break;
//
//            case STEP_DECOLOR:
//                s = "STEP_DECOLOR";
//                break;
//
//            case STEP_DECOLOR_BACK:
//                s = "STEP_DECOLOR_BACK";
//                break;
//
//            default:
//                s = "Error";
//                break;
//        }

//        svc = this;
//        return svc;
//    }
}
