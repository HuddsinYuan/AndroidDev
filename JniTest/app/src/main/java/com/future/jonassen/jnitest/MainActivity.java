package com.future.jonassen.jnitest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.future.jonassen.GPIOImplementation.Hardware;

public class MainActivity extends Activity
{
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         tv = (TextView)findViewById(R.id.tv1);
        Button btn = (Button)findViewById(R.id.bt1);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tv.setText(Hardware.getClanguageString());
            }
        });
    }

}
