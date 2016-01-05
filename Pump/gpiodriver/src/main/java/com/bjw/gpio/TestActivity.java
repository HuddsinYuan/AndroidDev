package com.bjw.gpio;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class TestActivity extends Activity {
	Button btnTestBack,btnStart;
	EditText editTestInfo;
	RadioButton radioBoard6410,radioBoard210;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        setTitle("Performance Test");
        btnStart=(Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new btnClickListener());
        btnTestBack=(Button)findViewById(R.id.btnTestBack);
        btnTestBack.setOnClickListener(new btnClickListener());
        editTestInfo=(EditText)findViewById(R.id.editTestInfo);
        radioBoard6410=(RadioButton)findViewById(R.id.radioBoard6410);
        radioBoard210=(RadioButton)findViewById(R.id.radioBoard210);
        editTestInfo.setText("��LED1~4��ͨ��JNI��ȡ10��Σ�����ƽ���ʱ:\n");
        if (android.os.Build.BOARD.contains("6410")) {
        	radioBoard6410.setChecked(true);
		} else if (android.os.Build.BOARD.contains("210"))
		{
			radioBoard210.setChecked(true);
		}
    }

    class btnClickListener implements View.OnClickListener{
		public void onClick(View v)
		{
			if (v==btnStart){
				String sInfo=editTestInfo.getText().toString();
				int mLoopCount = 100000;//ѭ��10���
				
				String sLED;
				int[] LEDn=new int[4];
				if (radioBoard6410.isChecked()){
					sLED="GPK";
					LEDn[0]=4;
					LEDn[0]=5;
					LEDn[0]=6;
					LEDn[0]=7;
				} else{
					sLED="GPJ2";
					LEDn[0]=0;
					LEDn[0]=1;
					LEDn[0]=2;
					LEDn[0]=3;
				}
				/* ��ȷ��Ϊ���뼶 */
				long startTime = System.nanoTime();
				for(int i = 0;i <= mLoopCount; ++ i)
				{
					for (int j : LEDn)
					{
						int ret = GPIOJNI.ReadGPIO(sLED, j);
					}
				}
				long endTime = System.nanoTime();
				double usetime=(endTime-startTime)/1000000;
				sInfo+="һ����ʱ:"+String.valueOf(usetime)+"ms\n";
				editTestInfo.setText(sInfo);
				usetime=usetime/mLoopCount;
				String timeStr_ms = String.valueOf(usetime);
				sInfo+="ÿ�ζ�ȡ��ʱ:"+timeStr_ms+"ms\n";
				editTestInfo.setText(sInfo);
			}
			else if (v==btnTestBack) {
				TestActivity.this.finish();
			}
		}
    }
    
}
