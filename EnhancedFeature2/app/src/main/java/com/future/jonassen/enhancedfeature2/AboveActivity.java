package com.future.jonassen.enhancedfeature2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class AboveActivity extends AppCompatActivity {
    private SeekBar SBar;
    //final NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_above);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final TextView result= (TextView)findViewById(R.id.seekbartext);
        SBar =(SeekBar)findViewById(R.id.seekbar1);
        SBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("SeekBar: ", "-->" + progress);
                result.setText("temp:" + Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(AboveActivity.this, "开始滑动", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(AboveActivity.this, "结束滑动", Toast.LENGTH_SHORT).show();
            }
        });

        final Button btnAlert1 = (Button)findViewById(R.id.three);
        btnAlert1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(AboveActivity.this).create();
                alert.setIcon(R.drawable.b);
                alert.setTitle("Sytem:");
                alert.setMessage("带取消，中立和确定按钮的对话框");

                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboveActivity.this, "按了取消按钮", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboveActivity.this, "按了确定按钮", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setButton(DialogInterface.BUTTON_NEUTRAL, "中立", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboveActivity.this, "按了中立按钮", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }

        });

        Button btnAlert2 = (Button)findViewById(R.id.two);
        btnAlert2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[] {"a", "b", "c", "d"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AboveActivity.this);
                builder.setIcon(R.drawable.b);
                builder.setTitle("favourite alphlet:");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboveActivity.this, "choose" + items[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });

        Button btnAlert3 = (Button)findViewById(R.id.one);
        btnAlert3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] item2s = new String[] {"Slient", "Meeting", "Outdoor"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AboveActivity.this);
                builder.setIcon(R.drawable.b);
                builder.setTitle("choose situation:");
                builder.setSingleChoiceItems(item2s, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboveActivity.this, "choose " + item2s[which], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("OK", null);
                builder.create().show();
            }
        });

        Button btn4Alert4 = (Button)findViewById(R.id.kkk);
        btn4Alert4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Boolean[] checkedItems = new Boolean[] {false, true, false, true, false};
                final boolean[] checkedItems = new boolean[] {false, true, false, true, false};
                final String[] itemgames = new String[] {"Game1", "Game2", "Game3", "Game4", "Game5" };
                AlertDialog.Builder builder = new AlertDialog.Builder(AboveActivity.this);
                builder.setIcon(R.drawable.b);
                builder.setTitle("favourite Game:");
                builder.setMultiChoiceItems(itemgames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result_of_game = "";
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i] == true) {
                                result_of_game += itemgames[i] + "、";
                            }
                        }
                        if (!"".equals(result_of_game)) {
                            result_of_game = result_of_game.substring(0, result_of_game.length() - 1);
                            Toast.makeText(AboveActivity.this, "您选择了[" + result_of_game + "]", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.create().show();
            }
        });



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_above, menu);
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
