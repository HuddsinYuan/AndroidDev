package com.future.jonassen.helloandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tView1 = (TextView)findViewById(R.id.tv3);
        tView1.setText(Html.fromHtml("大家好<font color=red>改变局部颜色</font>!"));

        String str="根据段落来改变颜色！";
        TextView tView2 = (TextView)findViewById(R.id.tv4);
        SpannableStringBuilder styleBuilder=new SpannableStringBuilder(str);
        styleBuilder.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styleBuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styleBuilder.setSpan(new ForegroundColorSpan(Color.RED), 4, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tView2.setText(styleBuilder);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
//        FrameLayout frameLayout=(FrameLayout)findViewById(R.id.mylayout);
//        final UserView view = new UserView(MainActivity.this);
//
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                view.invalidate();
//                return true;
//            }
//        });
//        frameLayout.addView(view);



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


