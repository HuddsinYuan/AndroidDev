package com.future.jonassen.pump;

import android.util.Log;

/**
 * Created by Jonassen on 16/1/6.
 */
public class StepControl {

    private static final int STEP_STAIN = 40002;
    private static final int STEP_STAIN_REVERSE = 40003;
    private static final int STEP_WASH = 40004;
    private static final int STEP_WASH_BACK = 40005;
    private static final int STEP_DESTAIN = 40006;
    private static final int STEP_DESTAIN_REVERSE = 40007;
    private boolean STEP_CYCLE = false;
    private boolean RunningState = true;

    private int iStep = STEP_STAIN;
    private int destainCycle = 0;
    private int waterCycle = 0;

    public int DestainTime;
    public int WaterTime;

    private boolean run_for_once = true;

    public int getDestainCycle() {
        return destainCycle;
    }

    public int getWaterCycle() {
        return waterCycle;
    }

    public StepControl(int DestainTime, int WaterTime) {
        this.DestainTime = DestainTime;
        this.WaterTime = WaterTime;
        this.iStep = STEP_STAIN;
        Log.i("StepControl", "水循环进行" + String.valueOf(WaterTime) + "次，脱色循环进行" + String.valueOf(DestainTime) + "次");
    }

    public int getStep() {
        return iStep;
    }

    public String getStringStep(int Step) {
        String s = new String();

        switch (Step) {
            case STEP_DESTAIN_REVERSE:
                s = "抽回脱色液";
                break;
            case STEP_DESTAIN:
                s = "倒入脱色液";
                break;
            case STEP_STAIN:
                s = "倒入染色液";
                break;
            case STEP_STAIN_REVERSE:
                s = "抽回染色液";
                break;
            case STEP_WASH:
                s = "倒入清水";
                break;
            case STEP_WASH_BACK:
                s = "抽回清水";
                break;
            default:
                s = "结束";
                break;
        }

        return s;
    }

    public boolean isStepCycle() {
        return STEP_CYCLE;
    }

    public boolean isRunningState() {
        return RunningState;
    }

    public void NextStep() {
        if (RunningState) {
            /*
                水洗的次数判断
             */
            if (iStep == STEP_DESTAIN && destainCycle == DestainTime - 1) {
                RunningState = false;
            } else if (iStep == STEP_WASH_BACK && waterCycle < WaterTime - 1) {
                iStep = STEP_WASH;
                waterCycle++;
            }
            /*
                脱色的次数判断，最后要停留在脱色阶段
             */
            else if (iStep == STEP_DESTAIN && destainCycle < DestainTime - 1) {
                iStep = STEP_DESTAIN_REVERSE;
            } else if (iStep == STEP_DESTAIN_REVERSE && destainCycle < DestainTime - 1) {
                iStep = STEP_DESTAIN;
                destainCycle++;
            }

            /*
                如果既不是水洗阶段 也不是脱色阶段
             */
            else {
                iStep++;
            }
        }

    }

    public void OutputMessage() {
        Log.i("StepControl", "下一个步骤：" + this.getStringStep(iStep) + " " + "destainCycle: " + String.valueOf(destainCycle) + " waterCycle: " + String.valueOf(waterCycle)
                + "Running State: " + String.valueOf(RunningState));
    }
}
