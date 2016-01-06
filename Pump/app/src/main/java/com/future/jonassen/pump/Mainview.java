package com.future.jonassen.pump;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    private static final int BUTTON_FF = 20004;

    private static final int STATE_PAUSE = 30001;
    private static final int STATE_RESUME = 30002;

    private static final int STEP_COLOR = 40002;
    private static final int STEP_COLOR_BACK = 40003;
    private static final int STEP_WASH = 40004;
    private static final int STEP_WASH_BACK = 40005;
    private static final int STEP_DECOLOR = 40006;
    private static final int STEP_DECOLOR_BACK = 40007;

    private static int TempStep;

    /*
        目前在染色阶段，水洗阶段，脱色阶段，需要抽取的染色液，水，脱色液的数量并不清楚，暂定 10ml 15ml 20ml
     */
    private static final Flux iColor = new Flux(10, 0);
    private static final Flux iWater = new Flux(15, 0);
    private static final Flux iDecolor = new Flux(20, 0);

    /*
        厂家默认的染色时间为120min, 水洗时间为10s, 脱色时间为10min
     */
    private static final int iTimeColor = 120 * 60;
    private static final int iTimeColorReverse = iTimeColor + 2;
    private static final int iTimeWater = 10;
    private static final int iTimeDecolor = 10 * 60;

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnSet;
    private Button btnFf;

    private static boolean allowSetting = true;

    private EditText etColorTime;
    private EditText etWaterTime;
    private EditText etCycle;
    private EditText etEachTime;

    /*
        获取到用户设置的数据
     */
    private int iColorTime;
    private int iWaterTime;
    private int iCycle = 4;
    private int iEachTime;

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
    private StepControl Step ;

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
                    Toast.makeText(Mainview.this, "SettingDone", Toast.LENGTH_SHORT).show();
                    Step = new StepControl(iCycle);
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

                        case BUTTON_FF:
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
        btnFf = (Button) findViewById(R.id.btn_ff);
        tvTest1 = (TextView) findViewById(R.id.tvtest1);
        tvTest2 = (TextView) findViewById(R.id.tvtest2);
        etColorTime = (EditText) findViewById(R.id.rt_ans_total);
        etWaterTime = (EditText) findViewById(R.id.rt_ans_water_time);
        etCycle = (EditText) findViewById(R.id.rt_ans_cycle);
        etEachTime = (EditText) findViewById(R.id.rt_ans_each_time);

        etColorTime.setText(String.valueOf(iTimeColor));
        etWaterTime.setText(String.valueOf(iTimeWater));
        etCycle.setText(String.valueOf(0));
        etEachTime.setText(String.valueOf(iTimeDecolor));

        /*
            设置监听器
         */
        btnStart.setOnClickListener(btnListener);
        btnPause.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnSet.setOnClickListener(btnListener);
        btnFf.setOnClickListener(btnListener);

//        mainThread.start();
//        timeThread.start();
    }

    Runnable PumpControl = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (statusVal.isStart()) {
                    if (!statusVal.isPause()) {
    
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
                    if (allowSetting) {
                        iColorTime = Integer.parseInt(etColorTime.getText().toString());
                        iWaterTime = Integer.parseInt(etWaterTime.getText().toString());
                        iCycle = Integer.parseInt(etCycle.getText().toString());
                        iEachTime = Integer.parseInt(etEachTime.getText().toString());

                        Log.i("iColorTime", String.valueOf(iColorTime));
                        Log.i("iWaterTime", String.valueOf(iWaterTime));
                        Log.i("iCycle", String.valueOf(iCycle));
                        Log.i("iEachTime", String.valueOf(iEachTime));

                        allowSetting = false;
                        MessageSender(TASK_SETTING_DONE, 0, 0);
                    } else {
                        Toast.makeText(Mainview.this, "Setting Fail", Toast.LENGTH_SHORT).show();
                    }
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
                    break;

                case R.id.btn_ff:
                    Step.NextStep();
                    Step.OutputMessage();

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
