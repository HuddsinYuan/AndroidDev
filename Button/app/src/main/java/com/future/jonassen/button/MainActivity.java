package com.future.jonassen.button;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button bt1=(Button)findViewById(R.id.Button1);
        Button bt2=(Button)findViewById(R.id.Button2);
        Button bt3=(Button)findViewById(R.id.Button3);
        bt1.setOnClickListener(listener);
        bt2.setOnClickListener(listener);

        ToggleButton tgbtn=(ToggleButton)findViewById(R.id.ToggleButton1);
        tgbtn.setOnClickListener(listener);

        final RadioGroup sex=(RadioGroup)findViewById(R.id.radioGroup);
        sex.setOnCheckedChangeListener(RadioListener);

        bt3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < sex.getChildCount(); i++) {
                    RadioButton q = (RadioButton)sex.getChildAt(i);
                    if(q.isChecked()) {
                        Log.i("单选按钮", "性别:" + q.getText());
                        break;
                    }
                }
            }
        });

        final CheckBox like1=(CheckBox)findViewById(R.id.CB1);
        final CheckBox like2=(CheckBox)findViewById(R.id.CB2);
        final CheckBox like3=(CheckBox)findViewById(R.id.CB3);

        like1.setOnCheckedChangeListener(checkBox_listener);
        like2.setOnCheckedChangeListener(checkBox_listener);
        like3.setOnCheckedChangeListener(checkBox_listener);

        Button btn4 =(Button)findViewById(R.id.CheckBoxQ);
        btn4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String like = "";
                if(like1.isChecked()) {
                    like += like1.getText().toString()+ " ";
                }

                if(like2.isChecked()) {
                    like += like2.getText().toString()+ " ";
                }

                if(like3.isChecked()) {
                    like += like3.getText().toString()+ " ";
                }

                Log.i("复选了：",like);
                Toast.makeText(MainActivity.this, like, Toast.LENGTH_SHORT).show();

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
    }


    private CheckBox.OnCheckedChangeListener checkBox_listener=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                Log.i("复选按钮","选中了[" + buttonView.getText().toString() + "]");
            }
            else {
                Log.i("复选按钮","取消了[" + buttonView.getText().toString() + "]");
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener RadioListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton q=(RadioButton)findViewById(checkedId);
            Log.i("单选按钮","您的选择是:" + q.getText());
        }
    };

    private OnClickListener listener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnButton=(Button) v;


            switch (btnButton.getId()) {
                case R.id.Button1:
                    Toast.makeText(MainActivity.this, "按钮1单机", Toast.LENGTH_LONG).show();
                    break;
                case R.id.Button2:
                    Toast.makeText(MainActivity.this, "按钮2单机", Toast.LENGTH_LONG).show();
                    break;

                default:
                    ToggleButton tgb=(ToggleButton) v;
                    Toast.makeText(MainActivity.this, tgb.getText(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


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
