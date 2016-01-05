package com.future.jonassen.pump;

import android.util.Log;

/**
 * Created by Jonassen on 16/1/5.
 */
public class StatusVal {
    private static final String TAG = "StatusVal";

    private static boolean Status_isStop;
    private static boolean Status_isStart;
    private static boolean Status_isPause;

    private Object lock = new Object();

    public StatusVal() {
        Status_isStop = false;
        Status_isStart = false;
        Status_isPause = true;
    }

    public boolean isStart() {
//        OutputMessage();
        return Status_isStart;
    }

    public void Start() {
        synchronized (lock) {
            Status_isStart = true;
            if (isStop())
                Status_isStop = false;
            if (isPause())
                Status_isPause = false;
            OutputMessage();
        }
    }

    public boolean isPause() {
//        OutputMessage();
        return Status_isPause;
    }

    public void PauseSetter(boolean val) {
        synchronized (lock) {
            if (isStart() && !isStop())
                Status_isPause = val;
            else
                Log.i(TAG, "Set Failed");
            OutputMessage();
        }
    }

    public boolean isStop() {
//        OutputMessage();
        return Status_isStop;
    }

    public void Stop() {
        synchronized (lock) {
            if (isStart()) {
                Status_isStop = true;
                Status_isStart = false;
                Status_isPause = true;
            } else {
                Status_isPause = false;
                Status_isStop = true;
                Status_isStart = false;
            }
            OutputMessage();
        }
    }


    private void OutputMessage() {
        String s = "Start: " + String.valueOf(Status_isStart) +
                ". Pause: " + String.valueOf(Status_isPause) +
                ". Stop: " + String.valueOf(Status_isStop);

        Log.i(TAG, s);
    }
}
