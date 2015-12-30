package com.future.jonassen.pumpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

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

public class MainView extends Activity
{
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "Main Activity:";
    private static final String RunningStatus = "Running";
    private static final String PauseStatus = "Pause";

    private static final int ML_TASK = 10001;
    private static final int UL_TASK = 10002;
    private static final int END_TASK = 10003;

    private Button btnSet;
    private Button btnStart;
    private Button btnPause;
    private Button btnStop;

//    private Button btn_test_gpio;
//    private Button btn_test_led;
//    private Button btn_test_pwm;

    public TextView tv_status;

    private static int rt_rand = 0;
    private static int rt_cycle = 1;
    private static int rt_time = 0;
    private static boolean rt_way = true;

    private TextView tv_rt_usr; // 默认是admin
    private TextView tv_rt_step; //第几个瓶子 + 方向
    private TextView tv_rt_cycle; //对应第几次循环，不大于总数的循环
    private TextView tv_rt_time; //当前步骤所需要的时间

    private static int all_total_time = 0;
    private static int all_cycle = 0;
    private static int all_single_time = 0;
    private static int all_water_time = 0;

    private TextView tv_all_total_time;
    private TextView tv_all_cycle;
    private TextView tv_all_single_time;
    private TextView tv_all_water_time;

    private static Flux[] Time;
    private boolean[] Way = new boolean[]{true, false, true, false, true};
    private int[] BottleSel = new int[]{1, 2, 3, 4, 5};

    private LinkedHashMap<Integer, Bean> ansdata = new LinkedHashMap<>();
    private PumpControl Pump = new PumpControl();
    private int setok = 0;


    private Handler mhandler;

    private boolean isRun = false;
    private boolean isStart = false;

    /*
        是否暂停，暂停的时候记录还需要多少时间完成

     */
    private boolean isPause = false;
    private Flux remainTime = new Flux(100, 100);
    private int remainId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Log.i("-------------------", "START----------------------");

        Time = new Flux[4];
        for (int i = 0; i < 4; i++)
        {
            Time[i] = new Flux();
            Time[i].iFluxML = 1 + i;
            Time[i].iFluxUL = 5 - i;
        }

        remainTime.iFluxML = Time[0].iFluxML;
        remainTime.iFluxUL = Time[0].iFluxUL;
        rt_rand = 0;
        rt_cycle = 0;
        remainId = rt_rand;
        setok = 0;
        isRun = false;
        isPause = true;
        isStart = false;
        printMessage();

        Pump.PumpIOInit();
        Pump.PumpEnbSetting(false);

        tv_status = (TextView) findViewById(R.id.status);
        tv_rt_usr = (TextView) findViewById(R.id.data_ans_usr);
        tv_rt_step = (TextView) findViewById(R.id.data_ans_step);
        tv_rt_cycle = (TextView) findViewById(R.id.data_ans_cycle);
        tv_rt_time = (TextView) findViewById(R.id.data_ans_all_time);

        tv_all_total_time = (TextView) findViewById(R.id.rt_ans_total);
        tv_all_single_time = (TextView) findViewById(R.id.rt_ans_each_time);
        tv_all_water_time = (TextView) findViewById(R.id.rt_ans_water_time);
        tv_all_cycle = (TextView) findViewById(R.id.rt_ans_cycle);

        btnSet = (Button) findViewById(R.id.btn_set);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnStop = (Button) findViewById(R.id.btn_stop);

//        btn_test_gpio = (Button) findViewById(R.id.gpio1);
//        btn_test_led = (Button) findViewById(R.id.led1);
//        btn_test_pwm = (Button) findViewById(R.id.pwm1);

        tv_rt_usr.setText("Admin");
        tv_status.setText(PauseStatus);

//        btn_test_pwm.setOnClickListener(buttonListener);
//        btn_test_gpio.setOnClickListener(buttonListener);
//        btn_test_led.setOnClickListener(buttonListener);
        btnSet.setOnClickListener(buttonListener);
        btnStart.setOnClickListener(buttonListener);
        btnPause.setOnClickListener(buttonListener);
        btnStop.setOnClickListener(buttonListener);


        Thread th = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    if (isRun)
                    {
                        rt_way = Way[remainId];


                        try //开始处理ML
                        {
                            if (!isPause && remainTime.iFluxML != 0)
                            {
                                Log.i("Thread", "Process ML");
                                printMessage();
                                int mlTime = remainTime.iFluxML * 5;
                                int mlTimeR = remainTime.iFluxML;
                                int i;

                                //发送电机控制信号和阀门选择信号，进行ML级别的液体抽取
                                MyMessageSender(ML_TASK, remainTime.iFluxML, rt_rand, rt_cycle,
                                                rt_way);
                                Log.i("MLTask",
                                      "send + " + String.valueOf(remainTime.iFluxML) + "ml " +
                                              String.valueOf(remainTime.iFluxUL) + "ul");

                                //等待预设定的时间
                                for (i = 0; i < mlTime && !isPause; i++)
                                {
                                    Thread.sleep(200);
                                }
                                if (isPause)//按下PAUSE的瞬间，监听器会掐断电机使能
                                {
                                    //暂停，记录当前还剩余的ML时间和全部的UL时间，同时关闭电机
                                    remainTime.iFluxML = mlTimeR - i / 5;
                                    remainId = rt_rand;
                                    Log.i("Remain", String.valueOf(
                                            remainTime.iFluxML) + "ml" + String.valueOf(
                                            remainTime.iFluxUL) + " ul");
                                }
                                else
                                {
                                    remainTime.iFluxML = 0;
                                }


                            }
                        }
                        catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }


                        try //开始处理UL
                        {
                            if (!isPause)
                            {
                                Log.i("Thread", "Process UL");
                                printMessage();
                                int ulTime = remainTime.iFluxUL * 5;
                                int ulTimeR = remainTime.iFluxUL;
                                int i;

                                //然后进行UL级别的抽取，进行电机的速率控制和抽取时间及阀门选择等
                                MyMessageSender(UL_TASK, remainTime.iFluxUL, rt_rand, rt_cycle,
                                                rt_way);
                                Log.i("ULTask",
                                      "send + " + String.valueOf(remainTime.iFluxML) + "ml " +
                                              String.valueOf(remainTime.iFluxUL) + "ul");

                                for (i = 0; i < ulTime && !isPause; i++)
                                {
                                    Thread.sleep(200);
                                }
                                if (isPause)
                                {
                                    remainTime.iFluxML = 0;
                                    remainTime.iFluxUL = ulTimeR - i / 5;
                                    remainId = rt_rand;
                                    Log.i("Remain", String.valueOf(
                                            remainTime.iFluxML) + "ml" + String.valueOf(
                                            remainTime.iFluxUL) + " ul");
                                }
                                else if (!isPause && isRun)
                                {
                                    /*
                                    如果没暂停的话现在进行数据的更新

                                     rt_rand ++  --> 阀门的递进
                                     rt_cycle --> 循环增加，并且判定是否到了结束时间

                                    */

                                    Log.i("Thread", "Process Done. shift rt_rand");
                                    printMessage();
                                    if (++rt_rand == 4)
                                    {
                                        rt_rand = 0;
                                        rt_cycle++;

                                        Log.i("Task", "finished, wait for another");

//                                        if (setok == 1)
//                                        {
//                                            if ((rt_cycle - 1) == all_cycle)
//                                            {
//                                                MyMessageSender(END_TASK, 0, 0, 0, false);
//                                            }
//                                        }
//                                        else
//                                        {
//                                            if (rt_cycle == 5)
//                                            {
//                                                MyMessageSender(END_TASK, 0, 0, 0, false);
//                                            }
//                                        }
                                    }
                                    remainId = rt_rand;
                                    remainTime.iFluxML = Time[rt_rand].iFluxML;
                                    remainTime.iFluxUL = Time[rt_rand].iFluxUL;

                                    Log.i("Thread",
                                          "Shift Done. Temp Cycle is " + String.valueOf(rt_cycle));
                                    printMessage();
                                    if (setok == 1)
                                    {
                                        if (rt_cycle == all_cycle)
                                        {
                                            MyMessageSender(END_TASK, 0, 0, 0, false);
                                        }
                                    }
                                    else
                                    {
                                        if (rt_cycle == 99)
                                        {
                                            MyMessageSender(END_TASK, 0, 0, 0, false);
                                        }
                                    }
                                }
                            }

                        }
                        catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }

                        //结束，刷新下一个时间
//                        remainId = rt_rand;
//                        remainTime.iFluxML = Time[rt_rand].iFluxML;
//                        remainTime.iFluxUL = Time[rt_rand].iFluxUL;
                    }
                }
            }
        }

        );


        mhandler = new Handler()
        {
            @Override
            public void handleMessage(Message m)
            {
                String s = new String();

                switch (m.what)
                {
                    case ML_TASK:
                        if (m.getData().getBoolean("way", true) == true)
                        {
                            s = "";
                            s = " + 正向抽取";
                            Pump.PumpDirSetting(true);
                        }
                        else
                        {
                            s = "";
                            s = " + 反向抽取";
                            Pump.PumpDirSetting(false);
                        }

                        tv_rt_cycle.setText(m.getData().getString("cycle"));
                        tv_rt_time.setText(String.valueOf(m.arg1) + "ml");
                        tv_rt_step.setText("从第" + Integer.toString(m.arg2 + 1) + "个瓶子" + s);

                        Pump.PumpValveSel(m.arg2);
//                        if (Pump.getPumpEnbState() == false)
//                        {
//                            Pump.PumpEnbSetting(true);
//                        }
//                        Pump.PumpEnbSetting(true);
                        Pump.PumpFluxML();
                        break;

                    case UL_TASK:
                        if (m.getData().getBoolean("way", true) == true)
                        {
                            s = "";
                            s = " + 正向抽取";
//                            Pump.PumpDirSetting(true);
                        }
                        else
                        {
                            s = "";
                            s = " + 反向抽取";
//                            Pump.PumpDirSetting(false);
                        }

                        tv_rt_cycle.setText(m.getData().getString("cycle"));
                        tv_rt_time.setText(String.valueOf(m.arg1) + "ul");
                        tv_rt_step.setText("从第" + Integer.toString(m.arg2 + 1) + "个瓶子" + s);

//                        if (Pump.getPumpEnbState() == false)
//                        {
//                            Pump.PumpEnbSetting(true);
//                        }
                        Pump.PumpFluxUL();
                        break;

                    case END_TASK:
                        rt_rand = 0;
                        isRun = false;
                        isPause = true;
                        isStart = false;

                        Pump.PumpEnbSetting(false);

                        tv_status.setText("停止");
                        break;

                    default:
                        break;
                }

//                super.handleMessage(m);
            }
        };

        th.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == resultCode &&
                data != null)
        {

            String line = data.getStringExtra("val");

            resetSettingValue();

            Log.i(TAG, line);

            Gson gson = new Gson();

            Type entitytype = new TypeToken<LinkedHashMap<Integer, Bean>>()
            {
            }.getType();

            ansdata = gson.fromJson(line, entitytype);

            Log.i(TAG, ansdata.toString());

            for (int i = 0; i < ansdata.keySet().size(); i++)
            {
                Bean bean = new Bean(ansdata.get(i + 1));
                Time[i].iFluxML = bean.flux.iFluxML;
                Time[i].iFluxUL = bean.flux.iFluxUL;
                Way[i] = bean.way;
            }

            all_cycle = data.getIntExtra("cyc", 1);

            for (int i = 0; i < ansdata.keySet().size() ; i++)
            {
                Log.i(String.valueOf(i) + " bottle",
                      "Time: " + Time[i].iFluxML + " ml " + Time[i].iFluxUL + "ul " + "  Way: " + Way[i]);

                all_single_time = all_single_time + Time[i].iFluxUL + Time[i].iFluxML;

            }

            all_total_time = all_single_time * all_cycle;

            //设置染色脱色参数
            tv_all_total_time.setText(String.valueOf(all_total_time) + "秒");
            tv_all_cycle.setText(String.valueOf(all_cycle) + "次");
            tv_all_single_time.setText(String.valueOf(all_single_time) + "秒");
            tv_all_water_time.setText("不明");

            remainTime.iFluxML = Time[0].iFluxML;
            remainTime.iFluxUL = Time[0].iFluxUL;
            rt_rand = 0;
            remainId = rt_rand;
            isRun = false;
            isPause = true;
            isStart = false;
            setok = 1;
        }

    }

    public View.OnClickListener buttonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Button button = (Button) v;

            switch (button.getId())
            {
                case R.id.btn_set:
                {
                    Intent intent = new Intent();
                    intent.setClass(MainView.this, SimpleSettingView.class);
                    startActivityForResult(intent, REQUEST_CODE);
                    break;
                }

                case R.id.btn_start:
                {
                    tv_status.setText(RunningStatus);
                    isRun = true;
                    isStart = true;
                    isPause = false;
                    Pump.PumpEnbSetting(true);

                    Toast.makeText(MainView.this, "start button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_pause:
                {
                    if (isStart)
                    {
                        if (isPause) //如果暂停了，那就继续
                        {   //运行的操作
//                            isRun = true;
                            isPause = false;
                            btnPause.setText("Pause");
                            tv_status.setText(RunningStatus);
                            Pump.PumpEnbSetting(true);
                            Log.i("Running State", "++" + String.valueOf(isPause));
                        }
                        else //不然就暂停
                        {   // 暂停的操作
//                            isRun = false;
                            isPause = true;
                            btnPause.setText("Resume");
                            Pump.PumpEnbSetting(false);
                            tv_status.setText(PauseStatus);
                            Log.i("Running State", "++" + String.valueOf(isPause));
                        }
                        Toast.makeText(MainView.this, "pause button has been pressed",
                                       Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainView.this, "the program hasn't start",
                                       Toast.LENGTH_LONG).show();
                    }
                    break;
                }

//                case R.id.btn_ff:
//                {
//                    Toast.makeText(MainView.this, "ff button has been pressed",
//                                   Toast.LENGTH_SHORT).show();
//                    break;
//                }

                case R.id.btn_stop:
                {
//                    isRun = false;
//                    isPause = true;
//                    isStart = false;
//                    rt_rand = 0;
//                    rt_cycle = 0;
//                    remainId = rt_rand;
//                    remainTime.iFluxML = Time[0].iFluxML;
//                    remainTime.iFluxUL = Time[0].iFluxUL;

                    remainTime.iFluxML = Time[0].iFluxML;
                    remainTime.iFluxUL = Time[0].iFluxUL;
                    rt_rand = 0;
                    rt_cycle = 0;
                    remainId = rt_rand;
                    isRun = false;
                    isPause = false;
                    isStart = false;

//                    setok = 1;

                    tv_rt_time.setText(String.valueOf(Time[rt_rand].iFluxML) + "ml" +
                                               String.valueOf(Time[rt_rand].iFluxUL) + "ul");

                    tv_rt_cycle.setText(Integer.toString(rt_cycle));

                    Pump.PumpEnbSetting(false);

                    tv_status.setText("停止");

                    printMessage();
                    Toast.makeText(MainView.this, "stop button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

//                case R.id.btn_edit:
//                {
//                    Toast.makeText(MainView.this, "edit button has been pressed",
//                                   Toast.LENGTH_SHORT).show();
//                    break;
//                }

//                case R.id.gpio1:
//                {
//                    Log.i("aa", "gpio1");
//                    if (GPIOJNI.ReadGPIO("GPH3", 3) == 1)
//                    {
//                        GPIOJNI.WriteGPIO("GPH3", 3, 0);
//                    }
//                    else
//                    {
//                        GPIOJNI.WriteGPIO("GPH3", 3, 1);
//                    }
//
//                    break;
//                }
//                case R.id.led1:
//                {
//                    Log.i("aa", "led1");
//                    if (GPIOJNI.ReadGPIO("GPJ2", 0) == 1)
//                    {
//                        HardwareControler.setLedState(0, 1);
//                    }
//                    else
//                    {
//                        HardwareControler.setLedState(0, 0);
//                    }
//                    break;
//                }
//
//                case R.id.pwm1:
//                {
//                    Log.i("aaa", "pwm");
//                    HardwareControler.PWMPlay((int) (2000 * 1.02));
//                    break;
//                }

                default:
                {
                    Toast.makeText(MainView.this, "default: shouldn't be here",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void MyMessageSender(int TaskId, int time, int bottle, int cycle, boolean way)
    {
        Message m = mhandler.obtainMessage();
        Bundle b = new Bundle();
        m.arg1 = time;
        m.arg2 = bottle;
        m.what = TaskId;
        b.putBoolean("way", way);
        b.putString("cycle", String.valueOf(cycle));
        m.setData(b);
        m.sendToTarget();
    }

    private void printMessage()
    {
        Log.i("Debug->Time", "Time[" + String.valueOf(rt_rand) + "] is " +
                String.valueOf(Time[rt_rand].iFluxML) + "ml " + String.valueOf(
                Time[rt_rand].iFluxUL) + "ul " +
                "and RemainTime[" + String.valueOf(remainId) + "] is " +
                String.valueOf(remainTime.iFluxML) + "ml " + String.valueOf(
                remainTime.iFluxUL) + "ul");
    }

    private void resetSettingValue()
    {
        for (int i = 0; i < Time.length; i++)
        {
            Time[i].iFluxML = 0;
            Time[i].iFluxUL = 0;
        }

//        all_cycle = 0;
        all_single_time = 0;
        all_total_time = 0;
        all_water_time = 0;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Thread.currentThread().destroy();
    }
}
