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
    private static final int TASK_MAIN_WORK = 10001;
    private static final int TASK_SECOND_WORK = 10002;
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
    private static int TempTime;

    /*
        目前在染色阶段，水洗阶段，脱色阶段，需要抽取的染色液，水，脱色液的数量并不清楚，暂定 10ml 15ml 20ml

     */
    // TODO: 16/1/6 这个具体参数需要和厂家确认
    private static final Flux iColor = new Flux(10, 0);
    private static final Flux iWater = new Flux(15, 0);
    private static final Flux iDecolor = new Flux(20, 0);

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


    private TextView tvStep;
    private TextView tvCycle;
    private TextView tvTime;


    /*
        默认的厂家设置的数据

        厂家默认的染色时间为120min, 水洗时间为10s, 脱色时间为10min

        目前为了测试设置为染色120s, 水洗时间为10s, 脱色时间为10s
     */
    private static final int iMauTimeColor = 120;
    private static final int iMauTimeColorReverse = iMauTimeColor + 2;
    private static final int iMauTimeWater = 10;
    private static final int iMauTimeDecolor = 10;

    /*
        获取到用户设置的数据

        iColorTime --> 传送给iTimeColor
        iWaterTime --> 传送给iTimeWater
        iCycle --> 传送给StepControl来作为步骤控制器
        iEachTime --> 传送给iTimeDecolor
     */
    private int iUserTimeColor;
    private int iUserTimeColorReverse;
    private int iUserTimeWater;
    private int iUserCycle = 4;
    private int iUserTimeDecolor;

    /*
        每一个步骤需要的参数
     */

    StepValueControl svc = new StepValueControl();

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
    private StepControl Step;

    private Thread mainThread;
    private Thread timeThread;
    private Handler mainHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case TASK_MAIN_WORK:
                    tvCycle.setText(String.valueOf(msg.arg1));
                    tvStep.setText(msg.getData().getString("string"));
                    int all_time = msg.arg2;
                    int min = all_time / 60;
                    int sec = all_time % 60;
                    tvTime.setText(String.format("%d 分 %d 秒", min, sec));
                    break;

                case TASK_SECOND_WORK:
                    break;

                case TASK_SETTING_DONE:
                    Toast.makeText(Mainview.this, "SettingDone", Toast.LENGTH_SHORT).show();
                    Step = new StepControl(iUserCycle);
                    break;

                case TASK_END_WORK:
                    Log.i("Handler", "End work");
                    statusVal.PauseSetter(true);

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
        timeThread = new Thread(SecondControl);
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
        tvStep = (TextView) findViewById(R.id.data_ans_step);
        tvCycle = (TextView) findViewById(R.id.data_ans_cycle);
        tvTime = (TextView) findViewById(R.id.data_ans_all_time);


        etColorTime.setText(String.valueOf(iMauTimeColor));
        etWaterTime.setText(String.valueOf(iMauTimeWater));
        etCycle.setText(String.valueOf(allowSetting ? 5 : iUserCycle));
        etEachTime.setText(String.valueOf(iMauTimeDecolor));

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
                        /*
                            每个步骤需要的参数，然后统一进行线程的Sleep管理

                            需要配置的参数有：
                            步骤： 染色 水洗 脱色
                            循环：按照厂家要求定制，只有在第二次循环时可以定制2~4循环
                            时间：每一个步骤的时间
                            阀门：每一个步骤打开的阀门
                            方向：每一个步骤所需求的方向
                         */

                        switch (Step.getStep()) {
                            case STEP_COLOR:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.ColorStep);
                                    svc.cycle = 1;
                                    svc.time = allowSetting ? iMauTimeColor : iUserTimeColor;
                                    svc.valve = 4;
                                    svc.dir = true;
                                }
                                break;

                            case STEP_COLOR_BACK:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.ColorDone);
                                    svc.cycle = 0;
                                    svc.time = allowSetting ? iMauTimeColorReverse : iUserTimeColorReverse;
                                    svc.valve = 4;
                                    svc.dir = false;
                                }
                                break;

                            case STEP_WASH:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.WashStep);
                                    svc.cycle = 1;
                                    svc.time = allowSetting ? iMauTimeWater : iUserTimeWater;
                                    svc.valve = 3;
                                    svc.dir = true;
                                }
                                break;

                            case STEP_WASH_BACK:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.WashStep);
                                    svc.cycle = Step.isStepCycle() ? 1 : Step.getInnerCycle();
                                    svc.time = allowSetting ? iMauTimeWater : iUserTimeWater;
                                    svc.valve = 1;
                                    svc.dir = false;
                                }
                                break;

                            case STEP_DECOLOR:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.Decolor);
                                    svc.cycle = Step.isStepCycle() ? 1 : Step.getInnerCycle();
                                    svc.time = allowSetting ? iMauTimeDecolor : iUserTimeDecolor;
                                    svc.valve = 2;
                                    svc.dir = true;
                                }
                                break;

                            case STEP_DECOLOR_BACK:
                                synchronized (svc.lock) {
                                    svc.s = String.valueOf(R.string.Decolor);
                                    svc.cycle = 1;
                                    svc.time = allowSetting ? iMauTimeDecolor : iUserTimeDecolor;
                                    svc.valve = 1;
                                    svc.dir = true;
                                }
                                break;

                            default:
                                break;

                        }

                        /*
                            配置完毕后，发给UI线程刷新界面
                         */
                        synchronized (svc.lock) {
                            PumpMessageSender(svc);
                        }

                        /*
                            svc中
                                time, cycle, s 用来更新UI
                                time,valve, dir用来控制阀门
                            接下来控制阀门
                         */

                        



                    }
                }
            }
        }
    };

    /*
        处理倒计时操作
     */
    Runnable SecondControl = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                ThreadSleep(1000);
                MessageSender(TASK_SECOND_WORK, svc.time, 0);
                svc.time -- ;
                if (svc.time == 0) {
                    Step.NextStep();
                }
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
                    btnSet.setClickable(false);
                    break;

                case R.id.btn_set:
                    if (allowSetting) {
                        iUserTimeColor = Integer.parseInt(etColorTime.getText().toString());
                        iUserTimeColorReverse = iUserTimeColor + 2;
                        iUserTimeWater = Integer.parseInt(etWaterTime.getText().toString());
                        iUserCycle = Integer.parseInt(etCycle.getText().toString());
                        iUserTimeDecolor = Integer.parseInt(etEachTime.getText().toString());

                        Log.i("iColorTime", String.valueOf(iUserTimeColor));
                        Log.i("iWaterTime", String.valueOf(iUserTimeWater));
                        Log.i("iCycle", String.valueOf(iUserCycle));
                        Log.i("iEachTime", String.valueOf(iUserTimeDecolor));

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
                    btnSet.setClickable(true);
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

    private void PumpMessageSender(StepValueControl svc) {
        Message m = mainHanlder.obtainMessage();
        Bundle bundle = new Bundle();
        m.what = TASK_MAIN_WORK;
        m.arg1 = svc.cycle;
        m.arg2 = svc.time;
        bundle.putInt("valve", svc.valve);
        bundle.putBoolean("dir", svc.dir);
        bundle.putString("string", svc.s);
        m.setData(bundle);
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
