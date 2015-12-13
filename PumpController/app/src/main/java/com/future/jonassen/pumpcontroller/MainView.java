package com.future.jonassen.pumpcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

//import com.friendlyarm.AndroidSDK.HardwareControler;
//import com.bjw.gpio.GPIOJNI;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class MainView extends Activity
{
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "Main Activity:";
    private static final String RunningStatus = "Running";
    private static final String PauseStatus = "Pause";
//    private static

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

    private int[] Time = new int[]{1100, 1200, 1300, 1400, 1500};
    private boolean[] Way = new boolean[]{true, false, true, false, true};
    private int[] BottleSel = new int[]{1, 2, 3, 4, 5};

    private LinkedHashMap<Integer, Bean> ansdata = new LinkedHashMap<>();
    private Bean bean = new Bean();
    private int setok = 0;


    private Handler mhandler;

    private boolean isRun = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Log.i("-------------------", "START----------------------");

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

//        GPIOJNI.SetCfgpin("GPH2", 0, 1); //EINT16
//        GPIOJNI.SetCfgpin("GPH2", 1, 1); //EINT17
//        GPIOJNI.SetCfgpin("GPH2", 2, 1); //EINT18
//        GPIOJNI.SetCfgpin("GPH2", 3, 1); //EINT19
//
//        GPIOJNI.SetCfgpin("GPH3", 0, 1); //EINT24
//        GPIOJNI.SetCfgpin("GPH3", 1, 1); //EINT25
//        GPIOJNI.SetCfgpin("GPH3", 2, 1); //EINT26
//        GPIOJNI.SetCfgpin("GPH3", 3, 1); //EINT27
//

//        HardwareControler.PWMStop();


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
                        Message m = mhandler.obtainMessage();
                        // TODO: 15/11/5 Add 1ms Message Process.

                        rt_time = Time[rt_rand];
                        rt_way = Way[rt_rand];


                        Bundle bundle = new Bundle();

                        m.arg1 = rt_time;
                        m.arg2 = rt_rand;
                        bundle.putString("cycle", Integer.toString(rt_cycle));
                        bundle.putBoolean("way", rt_way);
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        try
                        {
                            Thread.sleep(Time[rt_rand]);
                        }
                        catch (InterruptedException ie)
                        {
                            ie.printStackTrace();
                        }

                        if (++rt_rand == 5)
                        {
                            rt_rand = 0;
                            rt_cycle += 1;
                            if (setok == 1)
                            {
                                if ((rt_cycle - 1) == all_cycle)
                                {
                                    isRun = false;
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
            }
        });


        mhandler = new Handler()
        {
            @Override
            public void handleMessage(Message m)
            {
                String s = new String();

                try
                {
                    if (m.getData().getBoolean("way", true) == true)
                    {
                        // TODO: 15/11/5 正向操作
                        s = "";
                        s = "抽";

                    }
                    else if (m.getData().getBoolean("way") == false)
                    {
                        // TODO: 15/11/5 反向操作
                        s = "";
                        s = "放";
                    }
                    else
                    {
                        // TODO: 15/11/5  throw mydefined exception
                    }
                }
                catch (NumberFormatException | NullPointerException nfe)
                {
                    nfe.printStackTrace();
                    Log.i("handleMessage-->", "Bundle getData(\"way\") failed");
                }

                tv_rt_cycle.setText(m.getData().getString("cycle"));
                tv_rt_time.setText(Integer.toString(m.arg1) + "ms");
                tv_rt_step.setText("从第" + Integer.toString(m.arg2 + 1) + "个瓶子" + s);

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
                Time[i] = Integer.parseInt(bean.sFlux);
                Way[i] = bean.way;
            }

            all_cycle = data.getIntExtra("cyc", 1);

            for (int i = 0; i < ansdata.keySet().size(); i++)
            {
                Log.i(String.valueOf(i) + " bottle",
                      "Time: " + Time[i] + "  Way: " + Way[i]);

                all_single_time = all_single_time + Time[i];

            }

            all_total_time = all_single_time * all_cycle;

            //设置染色脱色参数
            tv_all_total_time.setText(String.valueOf(all_total_time));
            tv_all_cycle.setText(String.valueOf(all_cycle));
            tv_all_single_time.setText(String.valueOf(all_single_time));
            tv_all_water_time.setText("不明");

            rt_rand = 0;
            isRun = false;
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
                    tv_status.setText("运行中");
                    isRun = true;

                    Toast.makeText(MainView.this, "start button has been pressed",
                                   Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_pause:
                {
                    if (isRun == false)
                    {
                        isRun = true;
                        btnPause.setText("Pause");
                        tv_status.setText(RunningStatus);
                    }
                    else
                    {
                        isRun = false;
                        btnPause.setText("Resume");
                        tv_status.setText(PauseStatus);
                    }
                    Log.i("isRun", "-->false");
                    Toast.makeText(MainView.this, "pause button has been pressed",
                                   Toast.LENGTH_SHORT).show();
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
                    rt_rand = 0;
                    rt_cycle = 0;

                    tv_rt_time.setText(Integer.toString(Time[rt_rand]) + "ms");
                    tv_rt_cycle.setText(Integer.toString(rt_cycle));
                    tv_rt_step.setText("未设置");

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
//                    if (GPIOJNI.ReadGPIO("GPH3", 3) == 1)
//                    {
//                        GPIOJNI.WriteGPIO("GPH3", 3, 0);
//                    }
//                    else
//                    {
//                        GPIOJNI.WriteGPIO("GPH3", 3, 1);
//                    }

                    break;
                }
                case R.id.led1:
                {
                    Log.i("aa", "led1");
//                    if (GPIOJNI.ReadGPIO("GPJ2", 0) == 1)
//                    {
//                        HardwareControler.setLedState(0, 1);
//                    }
//                    else
//                    {
//                        HardwareControler.setLedState(0, 0);
//                    }
                    break;
                }

                case R.id.pwm1:
                {
                    Log.i("aaa", "pwm");
//                    HardwareControler.PWMPlay((int)(2000*1.02));
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

}
