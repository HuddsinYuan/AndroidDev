package com.future.jonassen.pump;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("NullArgumentToVariableArgMethod")
public class Mainview extends Activity {
    private static final int TASK_ML_WORK = 10001;
    private static final int TASK_UL_WORK = 10002;
    private static final int TASK_END_WORK = 10003;
    private static final int TASK_SETTING_DONE = 10004;
    private static final int TASK_BUTTON_CLICK = 10005;
    private static final int TASK_TEST_WORK = 10006;

    private static final int BUTTON_START = 20001;
    private static final int BUTTON_PAUSE = 20002;
    private static final int BUTTON_STOP = 20003;

    private static final int STATE_PAUSE = 30001;
    private static final int STATE_RESUME = 30002;


    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnSet;

    /*
        为了测试添加
     */
    private TextView tvTest1;
    private TextView tvTest2;
    private boolean tsSwitcher = false;
    private int mlcounter = 0;
    private int ulcounter = 0;

    private Class<StatusVal> cls; //statusVal的反射类
    private StatusVal statusVal;

    private tPumpControl pump = new tPumpControl();

    private Thread mainThread;
    private Thread timeThread;
    private Handler mainHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case TASK_ML_WORK:
                    break;

                case TASK_UL_WORK:
                    break;

                case TASK_SETTING_DONE:
                    break;

                case TASK_END_WORK:
                    Log.i("Handler", "End work");
                    statusVal.PauseSetter(true);
                    MessageSender(TASK_BUTTON_CLICK, BUTTON_PAUSE, STATE_PAUSE);
                    MessageSender(TASK_TEST_WORK, 0, TASK_ML_WORK);
                    MessageSender(TASK_TEST_WORK, 0, TASK_UL_WORK);
                    mlcounter = 0;
                    ulcounter = 0;

                    break;

                case TASK_BUTTON_CLICK:
                    switch (msg.arg1) {
                        case BUTTON_START:
                            break;
                        case BUTTON_PAUSE:
                            if (msg.arg2 == STATE_PAUSE) {
                                btnPause.setText(R.string.btn_pause);
                            } else if (msg.arg2 == STATE_RESUME) {
                                btnPause.setText(R.string.btn_resume);
                            }
                            break;
                        case BUTTON_STOP:
                            break;
                    }
                    break;

                case TASK_TEST_WORK:
                    if (msg.arg2 == TASK_ML_WORK)
                        tvTest1.setText("ML: " + String.valueOf(msg.arg1));
                    else if (msg.arg2 == TASK_UL_WORK)
                        tvTest2.setText("UL: " + String.valueOf(msg.arg1));
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);
        pump.PumpIOInit();
        mainThread = new Thread(PumpControl);
        timeThread = new Thread(TimeControl);
        statusVal = new StatusVal();

        /*
            开始资源定位
         */
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnSet = (Button) findViewById(R.id.btn_set);
        tvTest1 = (TextView) findViewById(R.id.tvtest1);
        tvTest2 = (TextView) findViewById(R.id.tvtest2);
        /*
            设置监听器
         */
        btnStart.setOnClickListener(btnListener);
        btnPause.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnSet.setOnClickListener(btnListener);


        mainThread.start();
        timeThread.start();
    }

    Runnable PumpControl = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (statusVal.isStart()) {
                    if (!statusVal.isPause()) {
                        if (tsSwitcher) {
                            ThreadSleep(240);
                            mlcounter++;
                            MessageSender(TASK_TEST_WORK, mlcounter, TASK_ML_WORK);
                        } else {
                            ThreadSleep(500);
                            ulcounter++;
                            MessageSender(TASK_TEST_WORK, ulcounter, TASK_UL_WORK);
                        }
                    }
                }
            }
        }
    };

    Runnable TimeControl = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ThreadSleep(1000);

                tsSwitcher = !tsSwitcher;
            }
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button c = (Button) v;

            switch (c.getId()) {
                case R.id.btn_start:
                    statusVal.Start();
                    break;
                case R.id.btn_set:
                    Toast.makeText(Mainview.this, "SET BUTTON", Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn_pause:
                    if (statusVal.isPause()) {
                        statusVal.PauseSetter(false);
                        MessageSender(TASK_BUTTON_CLICK, BUTTON_PAUSE, STATE_PAUSE);
                    } else {
                        statusVal.PauseSetter(true);
                        MessageSender(TASK_BUTTON_CLICK, BUTTON_PAUSE, STATE_RESUME);
                    }
                    break;
                case R.id.btn_stop:
                    statusVal.Stop();
                    MessageSender(TASK_END_WORK, 0, 0);
//                    MessageSender();
                    break;
                default:
                    break;
            }
        }
    };

    private void MessageSender(int TaskId, int arg1, int arg2) {
        Message m = mainHanlder.obtainMessage();
        m.what = TaskId;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.sendToTarget();
    }

    private void Invoke_OutputMethod() {
        Log.i("Invoke", "This function is invoked");

        cls = (Class<StatusVal>) statusVal.getClass();

        Method method = null;
        try {
            //反射类获取方法
            method = cls.getDeclaredMethod("OutputMessage", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        try {
            //反射类调用原类的私有方法
            method.invoke(statusVal, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void ThreadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
