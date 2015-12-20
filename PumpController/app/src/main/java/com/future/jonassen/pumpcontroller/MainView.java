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

import com.bjw.gpio.GPIOJNI;
import com.friendlyarm.AndroidSDK.HardwareControler;
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


    private Button btnSet;
    private Button btnStart;
    private Button btnPause;
    private Button btnFf;
    private Button btnStop;
    private Button btnEdit;

    private Button btn_test_gpio;
    private Button btn_test_led;
    private Button btn_test_pwm;

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

    private Flux[] Time;
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
    private Flux remainTime = new Flux(0, 0);
    private int remainId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Log.i("-------------------", "START----------------------");

        Time = new Flux[5];
        for (int i = 0; i < 5; i++)
        {
            Time[i] = new Flux();
        }
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
        btnFf = (Button) findViewById(R.id.btn_ff);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnEdit = (Button) findViewById(R.id.btn_edit);


        btn_test_gpio = (Button) findViewById(R.id.gpio1);
        btn_test_led = (Button) findViewById(R.id.led1);
        btn_test_pwm = (Button) findViewById(R.id.pwm1);


        tv_rt_usr.setText("Admin");
        tv_status.setText(PauseStatus);


        btn_test_pwm.setOnClickListener(buttonListener);
        btn_test_gpio.setOnClickListener(buttonListener);
        btn_test_led.setOnClickListener(buttonListener);
        btnSet.setOnClickListener(buttonListener);
        btnStart.setOnClickListener(buttonListener);
        btnPause.setOnClickListener(buttonListener);
        btnFf.setOnClickListener(buttonListener);
        btnStop.setOnClickListener(buttonListener);
        btnEdit.setOnClickListener(buttonListener);


        Thread th = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    if (isRun)
                    {
                        rt_way = Way[rt_rand];
                        try
                        {
                            if (!isPause)
                            {
                                int mlTime = remainTime.iFluxML * 5;
                                int mlTimeR = remainTime.iFluxML;
                                int i;
                                for (i = 0; i < mlTime && !isPause; i++)
                                {
                                    Thread.sleep(200);
                                }
                                if (isPause)
                                {
                                    remainTime.iFluxML = mlTimeR - i / 5;
                                    remainId = rt_rand;
                                    Log.i("Remain", String.valueOf(
                                            remainTime.iFluxML) + "ml" + String.valueOf(
                                            remainTime.iFluxUL) + " ul");
                                }
                                else
                                {
                                }
                                MyMessageSender(ML_TASK, remainTime.iFluxML, rt_rand, rt_cycle,
                                                rt_way);

                            }
                        }
                        catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }

                        try
                        {
                            if (!isPause)
                            {
                                int ulTime = remainTime.iFluxUL * 5;
                                int ulTimeR = remainTime.iFluxUL;
                                int i;
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
                                else
                                {
                                    if (++rt_rand == 4)
                                    {
                                        rt_rand = 0;
                                        rt_cycle += 1;
                                        if (setok == 1)
                                        {
                                            if ((rt_cycle - 1) == all_cycle)
                                            {
                                                isRun = false;
                                                tv_status.setText("Finished");
                                            }
                                        }
                                        else
                                        {
                                            if (rt_cycle == 5)
                                            {
                                                isRun = false;
                                            }
                                        }
                                    }
                                }
                            }
                            MyMessageSender(UL_TASK, Time[rt_rand].iFluxUL, rt_rand, rt_cycle,
                                            rt_way);
                        }
                        catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }


                        remainId = rt_rand;
                        remainTime.iFluxML = Time[rt_rand].iFluxML;
                        remainTime.iFluxUL = Time[rt_rand].iFluxUL;
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

//                        Pump.PumpValveSel(m.arg2);

//                        Pump.PumpEnbSetting(true);
                        Pump.PumpFluxUL();
                        break;

                    default:
                        break;
                }

                super.handleMessage(m);
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
                Time[i] = bean.flux;
                Way[i] = bean.way;
            }

            all_cycle = data.getIntExtra("cyc", 1);

            for (int i = 0; i < ansdata.keySet().size(); i++)
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


            remainTime = Time[0];
            rt_rand = 0;
            remainId = rt_rand;
            isRun = false;
            isPause = false;
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
                    Pump.PumpEnbSetting(true);

                    Toast.makeText(MainView.this, "start button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_pause:
                {
                    if (isStart)
                    {
                        if (!isRun)
                        {
                            isRun = true;
                            isPause = true;
                            btnPause.setText("Pause");
                            tv_status.setText(RunningStatus);
                            Pump.PumpEnbSetting(true);
                        }
                        else
                        {
                            isRun = false;
                            isPause = false;
                            btnPause.setText("Resume");
                            Pump.PumpEnbSetting(false);
                            tv_status.setText(PauseStatus);
                        }
                        Log.i("isRun", "-->false");
                        Toast.makeText(MainView.this, "pause button has been pressed",
                                       Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                case R.id.btn_ff:
                {
                    Toast.makeText(MainView.this, "ff button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_stop:
                {
                    isRun = false;
                    isStart = false;
                    rt_rand = 0;
                    rt_cycle = 0;

                    tv_rt_time.setText(String.valueOf(Time[rt_rand].iFluxML) + "ml" +
                                               String.valueOf(Time[rt_rand].iFluxUL) + "ul");

                    tv_rt_cycle.setText(Integer.toString(rt_cycle));
                    tv_rt_step.setText("未设置");

                    Pump.PumpEnbSetting(false);

                    tv_status.setText("停止");
                    Log.i("isRun", "-->false");
                    Toast.makeText(MainView.this, "stop button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_edit:
                {
                    Toast.makeText(MainView.this, "edit button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.gpio1:
                {
                    Log.i("aa", "gpio1");
                    if (GPIOJNI.ReadGPIO("GPH3", 3) == 1)
                    {
                        GPIOJNI.WriteGPIO("GPH3", 3, 0);
                    }
                    else
                    {
                        GPIOJNI.WriteGPIO("GPH3", 3, 1);
                    }

                    break;
                }
                case R.id.led1:
                {
                    Log.i("aa", "led1");
                    if (GPIOJNI.ReadGPIO("GPJ2", 0) == 1)
                    {
                        HardwareControler.setLedState(0, 1);
                    }
                    else
                    {
                        HardwareControler.setLedState(0, 0);
                    }
                    break;
                }

                case R.id.pwm1:
                {
                    Log.i("aaa", "pwm");
                    HardwareControler.PWMPlay((int) (2000 * 1.02));
                    break;
                }

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
