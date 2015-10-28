package com.future.jonassen.enhancedfeature1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    private final static int RequestCode = 1;
    private ProgressBar HPB;
    private ProgressBar VPB;
    private int iProgressStatus = 0;
    private Handler mHandler;
    private SeekBar SBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("ACTIVITY", "Main Activity --> OnCreate");

        Button btn1=(Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecActivity.class);
                intent.putExtra("str", "First Activity Transfer Val");
                startActivityForResult(intent, RequestCode);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        HPB = (ProgressBar)findViewById(R.id.progbarH);
        VPB = (ProgressBar)findViewById(R.id.progbarV);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                //super.handleMessage(msg);
                if(msg.what == 0x111) {
                    HPB.setProgress(iProgressStatus);
                }
                else {
                    Toast.makeText(MainActivity.this, "耗时操作已完成", Toast.LENGTH_SHORT).show();
                    HPB.setVisibility(View.GONE);
                    VPB.setVisibility(View.GONE);
                    msg.what = 0x111;
                    iProgressStatus = 0;
                }
            }
        };


        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        iProgressStatus = doWork();
                        Message m = new Message();
                        if (iProgressStatus < 100) {
                            m.what = 0x111;
                            mHandler.sendMessage(m);
                        } else {
                            m.what = 0x110;
                            mHandler.sendMessage(m);
                            break;
                        }
                    }
                }

                private int doWork() {
                    iProgressStatus += Math.random() * 10;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return iProgressStatus;
                }
            }).start();

        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RequestCode) {
            if(resultCode == SecActivity.ResultCode) {
                Bundle bundle=data.getExtras();
                String str = bundle.getString("back");
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("ACTIVITY", "Main Activity --> OnDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ACTIVITY", "Main Activity --> OnStart");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ACTIVITY", "Main Activity --> OnPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ACTIVITY", "Main Activity --> OnResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("ACTIVITY", "Main Activity --> OnRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ACTIVITY", "Main Activity --> OnStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
