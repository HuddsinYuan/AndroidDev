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
    int pump_time;
    int valve;
//    boolean isMLTime;
    boolean isWaitTime;
    boolean dir;
    boolean set_ok;
    String s;

    Object lock = new Object();

    public StepValueControl(StepValueControl stepValueControl) {
        this.dir = stepValueControl.dir;
        this.wait_time = stepValueControl.wait_time;
        this.pump_time = stepValueControl.pump_time;
        this.valve = stepValueControl.valve;
        this.isWaitTime = stepValueControl.isWaitTime;
//        this.isMLTime = stepValueControl.isMLTime;
        this.set_ok = stepValueControl.set_ok;
        this.s = stepValueControl.s;
        this.lock = stepValueControl.lock;
        this.cycle = stepValueControl.cycle;
    }


    public StepValueControl() {
        s = "";
        cycle = 0;
        wait_time = 0;
        pump_time = 0;
        isWaitTime = false;
        valve = 0;
        dir = false;
        set_ok = false;
    }

}
