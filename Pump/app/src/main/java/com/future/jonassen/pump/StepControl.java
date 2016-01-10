package com.future.jonassen.pump;

import android.util.Log;

/**
 * Created by Jonassen on 16/1/6.
 */
public class StepControl {

    private static final int STEP_COLOR = 40002;
    private static final int STEP_COLOR_BACK = 40003;
    private static final int STEP_WASH = 40004;
    private static final int STEP_WASH_BACK = 40005;
    private static final int STEP_DECOLOR = 40006;
    private static final int STEP_DECOLOR_BACK = 40007;
    private boolean STEP_CYCLE = false;
    private boolean RunningState = true;

    private int iStep = STEP_COLOR;
    private int innerCycle = 0;
    public int iTotal = 5;

    public int getInnerCycle() {
        return innerCycle;
    }

    public StepControl(int iTotal) {
        this.iTotal = iTotal;
    }

    public int getStep() {
        return iStep;
    }

    public String getStringStep() {
        String s = new String();

        switch (iStep) {
            case STEP_DECOLOR_BACK:
                s = "STEP_DECOLOR_BACK";
                break;
            case STEP_DECOLOR:
                s = "STEP_DECOLOR";
                break;
            case STEP_COLOR:
                s = "STEP_COLOR";
                break;
            case STEP_COLOR_BACK:
                s = "STEP_COLOR_BACK";
                break;
            case STEP_WASH:
                s = "STEP_WASH";
                break;
            case STEP_WASH_BACK:
                s = "STEP_WASH_BACK";
                break;
            default:
                s = "Error";
                break;
        }

        return s;
    }

    public boolean isStepCycle() {
        return STEP_CYCLE;
    }

    public void NextStep() {
        if (innerCycle < iTotal) {

            if (!STEP_CYCLE) {
                iStep++;
            } else {
                if (iStep == STEP_WASH_BACK) {
                    iStep = STEP_DECOLOR;
                } else if (iStep == STEP_DECOLOR) {
                    iStep = STEP_WASH_BACK;
                    innerCycle++;
                }
            }

            if (iStep == STEP_DECOLOR_BACK + 1 && !STEP_CYCLE) {
                STEP_CYCLE = true;
                iStep = STEP_WASH_BACK;
                innerCycle++;
            }

            Log.i("StepControl", "Next step is " + getStringStep());
        } else {
            RunningState = false;
            Log.i("StepControl", "Out of Cycle. Running State " + String.valueOf(RunningState));
        }

    }

    public boolean isRunningState() {
        return RunningState;
    }

    public void OutputMessage() {
        switch (iStep) {
            case STEP_DECOLOR_BACK:
                Log.i("StepControl", "STEP_DECOLOR_BACK. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            case STEP_DECOLOR:
                Log.i("StepControl", "STEP_DECOLOR. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            case STEP_COLOR:
                Log.i("StepControl", "STEP_COLOR. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            case STEP_COLOR_BACK:
                Log.i("StepControl", "STEP_COLOR_BACK. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            case STEP_WASH:
                Log.i("StepControl", "STEP_WASH. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            case STEP_WASH_BACK:
                Log.i("StepControl", "STEP_WASH_BACK. Running State: " + String.valueOf(STEP_CYCLE) + "Cycle: " + String.valueOf(innerCycle));
                break;
            default:
                Log.i("StepControl", "Error");
                break;
        }
    }
}
