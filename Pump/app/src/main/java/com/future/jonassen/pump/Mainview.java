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

    private static final String STRING_STAIN_STEP = "正在染色";
    private static final String STRING_STAIN_FINISH = "染色结束";
    private static final String STRING_WASH_STEP = "正在水洗";
    private static final String STRING_DESTAIN_STEP = "正在脱色";
    private static final String STRING_DESTAIN_DONE = "倒入废液";

    private static final int TASK_MAIN_WORK = 10001;
    private static final int TASK_SECOND_WORK = 10002;
    private static final int TASK_END_WORK = 10003;
    private static final int TASK_SETTING_DONE = 10004;
    private static final int TASK_BUTTON_CLICK = 10005;
    private static final int TASK_TEST_WORK = 10006;
    private static final int TASK_STATUS_REFRESH = 10007;

    private static final int BUTTON_START = 20001;
    private static final int BUTTON_PAUSE = 20002;
    private static final int BUTTON_STOP = 20003;
    private static final int BUTTON_FF = 20004;

    private static final int STATE_PAUSE = 30001;
    private static final int STATE_RESUME = 30002;

    private static final int STEP_STAIN = 40002;
    private static final int STEP_STAIN_REVERSE = 40003;
    private static final int STEP_WASH = 40004;
    private static final int STEP_WASH_BACK = 40005;
    private static final int STEP_DESTAIN = 40006;
    private static final int STEP_DESTAIN_BACK = 40007;

    private static int TempStep = 0;
    private static int TempTime = 0;

    private static boolean IsSetting = false;


    /*
        需要从View页面中抽取以下几个值
     */

    /*
        按键按下给出的信号
     */
    private Button btnStart;
    private Button btnPause;
    private Button btnStop;

    /*
        需要设置的六个参数
     */
    private EditText etStainFlux;
    private EditText etStainTime;
    private EditText etWaterTime;
    private EditText etDestainFlux;
    private EditText etDestainTime;
    private EditText etDestainCycle;

    /*
        当前的运行状态
     */
    private TextView tvStatus;

    /*
        当前的状态的运行数据
     */
    private TextView tvStep;
    private TextView tvCycle;
    private TextView tvRemainTime;


    private boolean isRestarted = false;
    private boolean run_for_once = false;
    private int multry_for_pumpdir = 0;
    private static int MULTRY_FOR_PUMPDIR = 3;
    /*
        默认的厂家设置的数据

        厂家设定的参数为
            1、进染色液100ml --> 100s
            2、染色液静置120mins --> 120*60s
            3、染色液回抽100ml+10ml --> 110s
            4、水洗凝胶流量 100ml --> 100s
            5、水洗凝胶静置 5s --> 5s
            6、重复过程 3次
            7、进脱色液200ml --> 200s
            8、脱色液静置10mins --> 10*60s
            9、回抽脱色液200ml --> 200s
            10、脱色循环n次
            11、脱色液保持

        目前为了测试设置为染色120s, 水洗时间为10s, 脱色时间为10s
     */
    private static final int iMauStainSecond = 100; // 可以设定
    private static final int iMauStainStandMinute = 120; // 可以设定
    private static final int iMauStainReverseSecond = (int)(iMauStainSecond * 1.1);
    private static final int iMauWaterSecond = iMauStainSecond;
    private static final int iMauWaterStand = 5;
    private static final int iMauWaterCycle = 3; // 可以设定
    private static final int iMauDestainSecond = iMauStainSecond * 2; // 可以设定
    private static final int iMauDestainStandMinute = 10; // 可以设定
    private static final int iMauDestainReverseSecond = iMauDestainSecond;
    private static final int iMauDestainCycle = 3; // 可以设定


    /*
        获取到用户设置的数据
     */
    private int iUserStainSecond;
    private int iUserStainStandMinute;
    private int iUserWaterCycle;
    private int iUserDestainSecond;
    private int iUserDestainStandMinute;
    private int iUserDestainCycle;

    private int iUserStainReverseSecond;
    private int iUserWaterSecond;
    private int iUserWaterStand = iMauWaterStand;
    private int iUserDestainReverseSecond;

    private void GetParaFromSetting() {
        iUserStainSecond = Integer.parseInt(etStainFlux.getText().toString());
        iUserStainStandMinute = Integer.parseInt(etStainTime.getText().toString());
        iUserWaterCycle = Integer.parseInt(etWaterTime.getText().toString());
        iUserDestainSecond = Integer.parseInt(etDestainFlux.getText().toString());
        iUserDestainStandMinute = Integer.parseInt(etDestainTime.getText().toString());
        iUserDestainCycle = Integer.parseInt(etDestainCycle.getText().toString());
        iUserStainReverseSecond = (int) (1.1 * iUserStainSecond);
        iUserWaterSecond = iUserStainSecond;
        iUserWaterStand = iMauWaterStand;
        iUserDestainReverseSecond = iUserDestainSecond;
        printSettingInfo();
    }

    private void InitParaFromMauSetting() {
        iUserStainSecond = iMauStainSecond;
        iUserStainStandMinute = iMauStainStandMinute;
        iUserWaterCycle = iMauWaterCycle;
        iUserDestainSecond = iMauDestainSecond;
        iUserDestainStandMinute = iMauDestainStandMinute;
        iUserDestainCycle = iMauDestainCycle;
        iUserStainReverseSecond = iMauStainReverseSecond;
        iUserWaterSecond = iMauWaterSecond;
        iUserWaterStand = iMauWaterStand;
        iUserDestainReverseSecond = iMauDestainReverseSecond;
    }

    private void printSettingInfo() {
        Log.i("抽取的染色液流量", String.valueOf(iUserStainSecond) + " ml.");
        Log.i("染色液静置时间", String.valueOf(iUserStainStandMinute) + " min.");
        Log.i("染色液回抽时间", String.valueOf(iUserStainReverseSecond) + " ml.");
        Log.i("用户水洗次数", String.valueOf(iUserWaterCycle) + " 次.");
        Log.i("水洗时间", String.valueOf(iUserWaterSecond) + " s.");
        Log.i("水静置时间", String.valueOf(iUserWaterStand) + " s.");
        Log.i("抽取的脱色液流量", String.valueOf(iUserDestainSecond) + " ml.");
        Log.i("脱色液静置时间", String.valueOf(iUserDestainStandMinute) + " min.");
        Log.i("脱色次数", String.valueOf(iUserDestainCycle) + " 次.");
        Log.i("脱色液回抽时间", String.valueOf(iUserDestainReverseSecond) + " s.");
    }

    /*
        每一个步骤需要的参数
     */

    StepValueControl svc = new StepValueControl();
    StepValueControl svc_init = new StepValueControl();

    /*
        1秒时间到, 推动主线程的进步
     */
    private int count_time = 0;

    /*
        为了测试添加
     */

    private Class<StatusVal> cls; //statusVal的反射类
    private StatusVal statusVal;

    private tPumpControl pump = new tPumpControl();
    private StepControl Step = new StepControl(iMauDestainCycle+1, iMauWaterCycle+1);

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
                    tvRemainTime.setText(String.format("%d 分 %d 秒", min, sec));
                    break;

                case TASK_SECOND_WORK:
                    all_time = msg.arg1;
                    min = all_time / 60;
                    sec = all_time % 60;
                    tvRemainTime.setText(String.format("%d 分 %d 秒", min, sec));

                    break;

                case TASK_SETTING_DONE:
                    Toast.makeText(Mainview.this, "SettingDone", Toast.LENGTH_SHORT).show();
                    Step = new StepControl(iUserDestainCycle, iUserWaterCycle);
                    break;

                case TASK_END_WORK:
                    Log.i("Handler", "End work");
                    tvStatus.setText("结束");
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

                case TASK_STATUS_REFRESH:
                    if (statusVal.isStop()) {
                        tvStatus.setText("停止");
                    } else if (statusVal.isPause()) {
                        tvStatus.setText("暂停");
                    } else if (statusVal.isStart()) {
                        tvStatus.setText("运行");
                    }
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
        InitParaFromMauSetting();

        svc_init.s = STRING_STAIN_STEP;
        svc_init.cycle = 1;
        svc_init.wait_time = IsSetting ? iUserStainStandMinute : iMauStainStandMinute;
        /*
            为了测试将静置时间设置成sec，实际测试为min
         */
//        svc_init.wait_time = svc_init.wait_time * 60;
        svc_init.pump_time = IsSetting ? iUserStainSecond : iMauStainSecond;
        svc_init.valve = 4;
        svc_init.dir = true;
        if (svc_init.dir) {
            svc_init.s += "正向";
        } else {
            svc_init.s += "反向";
        }
        svc_init.isWaitTime = false;

        /*
            开始资源定位
         */
        /*
            按键资源的定位
         */
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnStop = (Button) findViewById(R.id.btn_stop);
//        btnSet = (Button) findViewById(R.id.btn_set);
//        btnFf = (Button) findViewById(R.id.btn_ff);

        /*
            设置资源的定位
         */
        etStainFlux = (EditText) findViewById(R.id.para_ans_stainflux);
        etStainTime = (EditText) findViewById(R.id.para_ans_staintime);
        etWaterTime = (EditText) findViewById(R.id.para_ans_watertime);
        etDestainFlux = (EditText) findViewById(R.id.para_ans_destainflux);
        etDestainTime = (EditText) findViewById(R.id.para_ans_destaintime);
        etDestainCycle = (EditText) findViewById(R.id.para_ans_destaincycle);

        etStainFlux.setText(String.valueOf(iMauStainSecond));
        etStainTime.setText(String.valueOf(iMauStainStandMinute));
        etWaterTime.setText(String.valueOf(iMauWaterCycle));
        etDestainFlux.setText(String.valueOf(iMauDestainSecond));
        etDestainTime.setText(String.valueOf(iMauDestainStandMinute));
        etDestainCycle.setText(String.valueOf(iMauDestainCycle));


        /*
            实时信息的资源框的定位
         */
        tvStep = (TextView) findViewById(R.id.realtime_ans_step);
        tvCycle = (TextView) findViewById(R.id.realtime_ans_cycle);
        tvRemainTime = (TextView) findViewById(R.id.realtime_ans_remaintime);
        tvStatus = (TextView) findViewById(R.id.realtime_status);


        tvStatus.setText("停止");



        /*
            设置监听器
         */
        btnStart.setOnClickListener(btnListener);
        btnPause.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
//        btnSet.setOnClickListener(btnListener);
//        btnFf.setOnClickListener(btnListener);

//        statusVal.Start();
        timeThread.start();
        mainThread.start();
    }
    /*

     */
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
                        if (TempStep != Step.getStep() || isRestarted) {
                            isRestarted = false;
                            Log.i("PumpControl", "Enter Setting Part");
                            switch (Step.getStep()) {
                                case STEP_STAIN:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_STAIN_STEP;
                                        svc.cycle = 1;
                                        svc.wait_time = iUserStainStandMinute;
                                        svc.pump_time = iUserStainSecond;
                                        svc.valve = 4;
                                        svc.dir = true;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        TempStep = STEP_STAIN;
                                        TempTime = svc.pump_time;
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                case STEP_STAIN_REVERSE:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_STAIN_FINISH;
                                        svc.cycle = 0;
                                        svc.wait_time = 0;
                                        svc.pump_time = iUserStainReverseSecond;
                                        svc.valve = 4;
                                        svc.dir = false;
                                        TempStep = STEP_STAIN_REVERSE;
                                        TempTime = svc.pump_time;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                case STEP_WASH:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_WASH_STEP;
                                        svc.cycle = Step.getWaterCycle();
                                        svc.wait_time = iUserWaterStand;
                                        svc.pump_time = iUserWaterSecond;
                                        svc.valve = 3;
                                        svc.dir = true;
                                        TempStep = STEP_WASH;
                                        TempTime = svc.pump_time;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                case STEP_WASH_BACK:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_WASH_STEP;
                                        svc.cycle = Step.getWaterCycle();
                                        svc.wait_time = iUserWaterStand;
                                        svc.pump_time = iUserWaterSecond;
                                        svc.valve = 1;
                                        svc.dir = false;
                                        TempStep = STEP_WASH_BACK;
                                        TempTime = svc.pump_time;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                case STEP_DESTAIN:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_DESTAIN_STEP;
                                        svc.cycle = Step.getDestainCycle();
                                        svc.wait_time = iUserDestainStandMinute;
                                        svc.pump_time = iUserDestainSecond; /* 脱色液的数量 */
                                        svc.valve = 2;
                                        svc.dir = true;
                                        TempStep = STEP_DESTAIN;
                                        TempTime = svc.pump_time;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                case STEP_DESTAIN_BACK:
                                    synchronized (svc.lock) {
                                        svc.s = STRING_DESTAIN_DONE;
                                        svc.cycle = 1;
                                        svc.wait_time = 0;
                                        svc.pump_time = iUserDestainReverseSecond; /* 脱色液的数量 */
                                        svc.valve = 1;
                                        svc.dir = false;
                                        TempStep = STEP_DESTAIN_BACK;
                                        TempTime = svc.pump_time;
                                        if (svc.dir) {
                                            svc.s += "正向";
                                        } else {
                                            svc.s += "反向";
                                        }
                                        svc.isWaitTime = false;
                                    }
                                    break;

                                default:
                                    break;

                            }
                        /*
                            配置完毕后，发给UI线程刷新界面
                         */
                            synchronized (svc.lock) {

                                multry_for_pumpdir = 0;

                                PumpMessageSender(svc);
//                                set_ok = true;


                                Log.i("PumpControl", "Software Setting Ok");


                        /*
                            svc中
                                time, cycle, s 用来更新UI
                                time,valve, dir用来控制阀门
                            接下来控制阀门
                         */

                                pump.PumpValveSel(svc.valve - 1);
                                pump.PumpDirSetting(svc.dir);
                                pump.PumpFluxML();
                                pump.PumpEnbSetting(true);
                            }

                            Log.i("PumpControl", "Hardware Setting Ok");

                        }
                    }
                    /*
                        如果暂停了，在这里做处理
                     */
                    else {

                    }
                }
            }
        }
    };

    /*
        处理倒计时操作,只负责处理倒计时问题
     */
    Runnable SecondControl = new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (!statusVal.isPause() && statusVal.isStart()) {
//                    Log.i("SecondControl", "set_ok value is " +String.valueOf(set_ok));
//                    if (set_ok) {
//                        Log.i("SecondControl", "Enter SecondControl Thread");


                    /*
                        前三秒尝试多次设置旋转方向，共尝试三次
                     */
                    if (multry_for_pumpdir < MULTRY_FOR_PUMPDIR) {
                        pump.PumpDirSetting(svc.dir);
                        multry_for_pumpdir++;
                    }

                    /*
                        判断当前的运行状态
                     */

//                    if (svc.isWaitTime || statusVal.isStop() || statusVal.isPause()) {
//                        if (pump.getPumpEnbState()) {
//                            pump.PumpEnbSetting(false);
//                        }
//                    } else {
//                        if (pump.getPumpEnbState() && run_for_once) {
//                            run_for_once = false;
//                            pump.PumpValveSel(svc.valve - 1);
//                            pump.PumpDirSetting(svc.dir);
//                            pump.PumpFluxML();
//                            Log.i("SecondControl", "Get Here");
//                        }
//                    }

                    ThreadSleep(1000);
                    /*
                        检查是否是运行状态，只检查isWaitTime对于Pump的控制
                     */


//                    if (svc.isWaitTime && pump.getPumpEnbState()) {
//                        pump.PumpEnbSetting(false);
//                    } else if (!svc.isWaitTime && !pump.getPumpEnbState()) {
//                        pump.PumpEnbSetting(true);
//                    }

                    /*
                        原来的 ML 抽水的过程
                     */

                    if (!svc.isWaitTime) {
                        if (TempTime > 0) {
                            TempTime--;
                        } else if (TempTime == 0) {
                            TempTime = svc.wait_time;
                            svc.isWaitTime = true;
                        }
                        count_time = svc.wait_time + TempTime;
                    }

                    /*
                        原来的 UL 抽水的过程
                     */

//                    if (!svc.isMLTime && !svc.isWaitTime) {
//                        if (TempTime > 0) {
//                            TempTime--;
////                            Log.i("SecondControl", "Pump UL Work. Pump Dir = " + String.valueOf(svc.dir));
//                        } else if (TempTime == 0) {
//                            TempTime = svc.wait_time - svc.pump_ul_time - svc.pump_ml_time;
//                            svc.isWaitTime = true;
//                            pump.CloseAllValve();
//                        }
//
//                        count_time = svc.wait_time - svc.pump_ml_time + TempTime - svc.pump_ul_time;
//
//                    }

                    /*
                        静置等待的过程
                     */
                    if (svc.isWaitTime) {
                        if (TempTime > 0) {
                            TempTime--;
                        } else if (TempTime == 0) {
                            Step.NextStep();
                            Step.OutputMessage();
                            if (!Step.isRunningState()) {
                                statusVal.Stop();
                                pump.PumpEnbSetting(false);
                                pump.CloseAllValve();
                                RefreshDevice2InitState();
                                MessageSender(TASK_END_WORK, 0, 0);
                                MessageSender(TASK_STATUS_REFRESH, 0, 0);
                            }
                        }
                        count_time = TempTime;
                    }


                    MessageSender(TASK_SECOND_WORK, count_time, 0);
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
                    pump.PumpEnbSetting(true);
                    MessageSender(TASK_STATUS_REFRESH, 0, 0);
                    GetParaFromSetting();
                    break;

//                case R.id.btn_set:
//                    if (IsSetting) {
//                        iUserTimeColor = Integer.parseInt(etColorTime.getText().toString());
//                        iUserTimeColorReverse = iUserTimeColor + 2;
//                        iWater.iFluxML = Integer.parseInt(etWaterTime.getText().toString());
//                        iUserCycle = Integer.parseInt(etCycle.getText().toString());
//                        iUserTimeDecolor = Integer.parseInt(etEachTime.getText().toString());
//
//                        Log.i("iColorTime", String.valueOf(iUserTimeColor));
//                        Log.i("iWaterTime", String.valueOf(iWater.iFluxML));
//                        Log.i("iCycle", String.valueOf(iUserCycle));
//                        Log.i("iEachTime", String.valueOf(iUserTimeDecolor));
//
//                        IsSetting = true;
//                        MessageSender(TASK_SETTING_DONE, 0, 0);
//                    } else {
//                        Toast.makeText(Mainview.this, "Setting Fail", Toast.LENGTH_SHORT).show();
//                    }
//                    break;

                case R.id.btn_pause:
                    if (statusVal.isPause()) {
                        statusVal.PauseSetter(false);
                        pump.PumpEnbSetting(true);
                        run_for_once = true;
                        MessageSender(TASK_BUTTON_CLICK, BUTTON_PAUSE, STATE_PAUSE);
                    } else {
                        statusVal.PauseSetter(true);
                        pump.PumpEnbSetting(false);
                        pump.CloseAllValve();
                        MessageSender(TASK_BUTTON_CLICK, BUTTON_PAUSE, STATE_RESUME);
                    }

                    MessageSender(TASK_STATUS_REFRESH, 0, 0);
                    break;

                case R.id.btn_stop:
                    statusVal.Stop();
                    pump.PumpEnbSetting(false);
                    pump.CloseAllValve();
                    RefreshDevice2InitState();
                    MessageSender(TASK_END_WORK, 0, 0);
                    MessageSender(TASK_STATUS_REFRESH, 0, 0);
                    break;

//                case R.id.btn_ff:
//                    Step.NextStep();
//                    Step.OutputMessage();
//                    if (Step.isRunningState() == false) {
//                        RefreshDevice2InitState();
//                    }

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
        m.arg2 = svc.wait_time;
        bundle.putInt("valve", svc.valve);
        bundle.putBoolean("dir", svc.dir);
        bundle.putString("string", svc.s);
        m.setData(bundle);
        m.sendToTarget();
    }

    private void RefreshDevice2InitState() {
        isRestarted = true;
        statusVal.PauseSetter(true);

        Step = new StepControl(iUserDestainCycle, iUserWaterCycle);


//        svc = new StepValueControl(svc_init);
    }

//    private void Invoke_OutputMethod() {
//        Log.i("Invoke", "This function is invoked");
//
//        cls = (Class<StatusVal>) statusVal.getClass();
//
//        Method method = null;
//        try {
//            //反射类获取方法
//            method = cls.getDeclaredMethod("OutputMessage", null);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        method.setAccessible(true);
//        try {
//            //反射类调用原类的私有方法
//            method.invoke(statusVal, null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    private void ThreadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void SecondDetect() {
        String s;
        s = "当前步骤：" + Step.getStringStep(Step.getStep()-1) + " " +
                "水泵状态：" + String.valueOf(pump.getPumpEnbState()) + " " +
                "水泵抽水方向：" + String.valueOf(svc.dir) + " " +
                "阀门那个开：" + String.valueOf(svc.valve);
        Log.i("SecondDetect", s);


    }
}
