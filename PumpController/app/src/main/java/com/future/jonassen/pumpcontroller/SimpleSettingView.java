package com.future.jonassen.pumpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Objects;


public class SimpleSettingView extends Activity
{

    private static final int ResultCode = 1;
    private static final String TAG = new String("SimpleSettingView");
    private EditText mlflux1;
    private EditText mlflux2;
    private EditText mlflux3;
    private EditText mlflux4;
    private EditText mlflux5;

    private EditText ulflux1;
    private EditText ulflux2;
    private EditText ulflux3;
    private EditText ulflux4;
    private EditText ulflux5;


    private CheckBox way1;
    private CheckBox way2;
    private CheckBox way3;
    private CheckBox way4;
    private CheckBox way5;

    private EditText cycle;
    private int cycletime;

    private Button setok;

    private Object lock = new Object();

    private LinkedHashMap<Integer, Bean> intentdata;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_setting_view);

        intentdata = new LinkedHashMap<>();

        InitAllBottle();
    }

    public void InitAllBottle()
    {
        ulflux1 = (EditText)findViewById(R.id.ulflux1);
        ulflux2 = (EditText)findViewById(R.id.ulflux2);
        ulflux3 = (EditText)findViewById(R.id.ulflux3);
        ulflux4 = (EditText)findViewById(R.id.ulflux4);
        ulflux5 = (EditText)findViewById(R.id.ulflux5);

        mlflux1 = (EditText)findViewById(R.id.mlflux1);
        mlflux2 = (EditText)findViewById(R.id.mlflux2);
        mlflux3 = (EditText)findViewById(R.id.mlflux3);
        mlflux4 = (EditText)findViewById(R.id.mlflux4);
        mlflux5 = (EditText)findViewById(R.id.mlflux5);

        way1 = (CheckBox) findViewById(R.id.way_1);
        way2 = (CheckBox) findViewById(R.id.way_2);
        way3 = (CheckBox) findViewById(R.id.way_3);
        way4 = (CheckBox) findViewById(R.id.way_4);
        way5 = (CheckBox) findViewById(R.id.way_5);

        cycle = (EditText) findViewById(R.id.cycle);

        setok = (Button) findViewById(R.id.setok);
        setok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                Bean bean1 = new Bean();
                Bean bean2 = new Bean();
                Bean bean3 = new Bean();
                Bean bean4 = new Bean();
                Bean bean5 = new Bean();


                synchronized (lock)
                {

                    bean1.id = 1;
                    bean1.flux.sFluxML = mlflux1.getText().toString();
                    bean1.flux.sFluxUL = ulflux1.getText().toString();
                    bean1.flux.convertVal();
                    bean1.way = way1.isChecked();
                    intentdata.put(1, bean1);

                    bean2.id = 2;
                    bean2.flux.sFluxML = mlflux2.getText().toString();
                    bean2.flux.sFluxUL = ulflux2.getText().toString();
                    bean2.flux.convertVal();
                    bean2.way = way2.isChecked();
                    intentdata.put(2, bean2);

                    bean3.id = 3;
                    bean3.flux.sFluxML = mlflux3.getText().toString();
                    bean3.flux.sFluxUL = ulflux3.getText().toString();
                    bean3.flux.convertVal();
                    bean3.way = way3.isChecked();
                    intentdata.put(3, bean3);

                    bean4.id = 4;
                    bean4.flux.sFluxML = mlflux4.getText().toString();
                    bean4.flux.sFluxUL = ulflux4.getText().toString();
                    bean4.flux.convertVal();
                    bean4.way = way4.isChecked();
                    intentdata.put(4, bean4);

                    bean5.id = 5;
                    bean5.flux.sFluxML = mlflux5.getText().toString();
                    bean5.flux.sFluxUL = ulflux5.getText().toString();
                    bean5.flux.convertVal();
                    bean5.way = way5.isChecked();
                    intentdata.put(5, bean5);

                    if (cycle.getText() == null)
                    {
                        cycletime = 5;
                    }
                    else
                    {
                        cycletime = Integer.parseInt(cycle.getText().toString());
                    }

                    intent.putExtra("val", new Gson().toJson(intentdata));
                    intent.putExtra("cyc", cycletime);

                    Log.i("aaaa", new Gson().toJson(intentdata));


                    setResult(ResultCode, intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i(TAG, "onPause");
    }
}
