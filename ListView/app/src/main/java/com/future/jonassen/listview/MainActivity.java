package com.future.jonassen.listview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    private int[] imageId=new int[] {R.drawable.test1, R.drawable.test3, R.drawable.test4};
    private int index = 0;
    private ImageSwitcher imageSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    //ListView
//        ListView listView=(ListView)findViewById(R.id.ListViewGroup);
//        String[] ListArray=new String[] {"a", "b", "c", "d", "e"};
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, ListArray);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String result = parent.getItemAtPosition(position).toString();
//                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
//            }
//        });

        Spinner Spinnn=(Spinner)findViewById(R.id.SpinnerTitle);
        Spinnn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                Log.i("复选框", "选中了" + result);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("复选框", "没有选中");
            }
        });

        imageSwitcher=(ImageSwitcher)findViewById(R.id.ImageS);
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(480, 360));
                return imageView;
            }
        });

        imageSwitcher.setImageResource(imageId[index]);

        Button bp=(Button)findViewById(R.id.prew);
        Button bn=(Button)findViewById(R.id.next);
        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 0) {
                    index--;
                }
                else {
                    index = imageId.length -1 ;
                }
                Log.i("ImageSwitch:", "prew pic -->id=" + index);
                imageSwitcher.setImageResource(imageId[index]);
            }
        });

        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < imageId.length -1) {
                    index ++;
                }
                else {
                    index = 0;
                }
                Log.i("ImageSwitch:", "next pic -->id=" + index);
                imageSwitcher.setImageResource(imageId[index]);
            }
        });

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
