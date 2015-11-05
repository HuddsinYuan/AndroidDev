package com.future.jonassen.pumpcontroller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;


public class MainView extends AppCompatActivity {
    private int[] TestTime = new int[] {2000, 1800, 3200, 4000, 700};
    private int[] TestWay = new int[] {1, 0, 1, 1, 0};
    private int[] TestBottleSel = new int[] {1, 2, 4, 3, 5};
    private Handler mhandler;
    private static int rand = 0;
    private boolean isRun = false;
    private static int cycle = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView usrInstance = (TextView)findViewById(R.id.data_ans_usr);
        usrInstance.setText("Admin");

        Button btnSet = (Button)findViewById(R.id.btn_set);
        Button btnStart = (Button)findViewById(R.id.btn_start);
        Button btnPause = (Button)findViewById(R.id.btn_pause);
        Button btnFf = (Button)findViewById(R.id.btn_ff);
        Button btnStop = (Button)findViewById(R.id.btn_stop);
        Button btnEdit = (Button)findViewById(R.id.btn_edit);
        btnSet.setOnClickListener(buttonListener);
        btnStart.setOnClickListener(buttonListener);
        btnPause.setOnClickListener(buttonListener);
        btnFf.setOnClickListener(buttonListener);
        btnStop.setOnClickListener(buttonListener);
        btnEdit.setOnClickListener(buttonListener);

        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    if(isRun) {
                        Message m = mhandler.obtainMessage();

                        Bundle bundle = new Bundle();
                        m.arg1 = TestTime[rand];
                        m.arg2 = TestBottleSel[rand];
                        bundle.putString("cycle", Integer.toString(cycle));
                        bundle.putString("way", Integer.toString(TestWay[rand]));
                        m.setData(bundle);
                        mhandler.sendMessage(m);
                        try {
                            Thread.sleep(TestTime[rand]);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }

                        if (++rand == 5) {
                            rand = 0;
                            cycle += 1;
                        }
                    }
                }
            }
        });
        th.start();


        mhandler = new Handler(){
            @Override
            public void handleMessage(Message m) {
                String s = new String();
                TextView data_ans_step  = (TextView)findViewById(R.id.data_ans_step);
                TextView data_ans_cycle = (TextView)findViewById(R.id.data_ans_cycle);
                TextView data_ans_all_time = (TextView)findViewById(R.id.data_ans_all_time);

                try {
                    if (m.getData().getString("way") == "1") {
                        // TODO: 15/11/5 正向操作
                        s = "";
                        s = "抽";

                    }
                    else if(m.getData().getString("way") == "0") {
                        // TODO: 15/11/5 反向操作
                        s = "";
                        s = "放";
                    }
                    else {
                        // TODO: 15/11/5  throw mydefined exception
                    }
                }
                catch (NumberFormatException | NullPointerException nfe) {
                    nfe.printStackTrace();
                    Log.i("handleMessage-->", "Bundle getData(\"way\") failed");
                }

                data_ans_cycle.setText(m.getData().getString("cycle"));
                data_ans_all_time.setText(Integer.toString(m.arg1) + "ms");
                data_ans_step.setText("从第" + Integer.toString(m.arg2) + "个瓶子" + s);

                super.handleMessage(m);
            }
        };

    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            TextView Status = (TextView)findViewById(R.id.status);

            switch (button.getId()) {
                case R.id.btn_set: {
                    Intent intent = new Intent();
                    intent.setClass(MainView.this, SettingView.class);
                    startActivity(intent);
                    break;
                }

                case R.id.btn_start: {
                    isRun = true;
                    Status.setText("运行中");
                    Toast.makeText(MainView.this, "start button has been pressed", Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_pause: {
                    isRun = false;
                    Status.setText("暂停");
                    Log.i("isRun", "-->false");
                    Toast.makeText(MainView.this, "pause button has been pressed", Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_ff: {
                    Toast.makeText(MainView.this, "ff button has been pressed", Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_stop: {
                    TextView data_ans_step  = (TextView)findViewById(R.id.data_ans_step);
                    TextView data_ans_cycle = (TextView)findViewById(R.id.data_ans_cycle);
                    TextView data_ans_all_time = (TextView)findViewById(R.id.data_ans_all_time);

                    isRun = false;
                    rand = 0;
                    cycle = 0;

                    data_ans_all_time.setText(Integer.toString(TestTime[rand]) + "ms");
                    data_ans_cycle.setText(Integer.toString(cycle));
                    data_ans_step.setText("从第1个瓶子");

                    Status.setText("停止");
                    Log.i("isRun", "-->false");
                    Toast.makeText(MainView.this, "stop button has been pressed", Toast.LENGTH_SHORT).show();
                    break;
                }

                case R.id.btn_edit: {
                    Toast.makeText(MainView.this, "edit button has been pressed", Toast.LENGTH_SHORT).show();
                    break;
                }

                default: {
                    Toast.makeText(MainView.this, "default: shouldn't be here", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
