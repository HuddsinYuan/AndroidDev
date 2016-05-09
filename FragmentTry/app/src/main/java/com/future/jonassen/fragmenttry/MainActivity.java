package com.future.jonassen.fragmenttry;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoginFragment.DataInteraction{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int REQUEST_DATA = 1;

    private Toolbar toolbar;

    private LinearLayout llusername;
    private LinearLayout llpassword;

    private TextView tvUsername;
    private TextView tvPassword;

    private LoginFragment mloginFragment;

//    private TextView btnfriend;
//    private TextView btncontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        tvUsername = (TextView) findViewById(R.id.tvusername);
        tvPassword = (TextView) findViewById(R.id.tvpassword);
        llusername = (LinearLayout) findViewById(R.id.ll1);
        llpassword = (LinearLayout) findViewById(R.id.ll2);
        llusername.setVisibility(View.INVISIBLE);
        llpassword.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_log) {
            Toast.makeText(MainActivity.this, "edit option", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivityForResult(intent, REQUEST_DATA);
            return true;
        } else if (id == R.id.action_home) {
            // Fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (mloginFragment == null) {
                mloginFragment = new LoginFragment();
                mloginFragment.setmDataInteraction(this);
            }
            ft.addToBackStack(null);
            mloginFragment.show(ft,"dialog");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "get here");
        if (requestCode == REQUEST_DATA) {
            llusername.setVisibility(View.VISIBLE);
            llpassword.setVisibility(View.VISIBLE);

            Log.i(TAG, data.getStringExtra("user"));
            Log.i(TAG, data.getStringExtra("pass"));
            tvUsername.setText(data.getStringExtra("user"));
            tvPassword.setText(data.getStringExtra("pass"));
        }
    }

    @Override
    public void GetUserInfo(Bundle bundle) {
        llusername.setVisibility(View.VISIBLE);
        llpassword.setVisibility(View.VISIBLE);
        tvUsername.setText(bundle.getString("user") + " + frag");
        tvPassword.setText(bundle.getString("pass") + " + frag");
    }
}
