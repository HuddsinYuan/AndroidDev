package com.future.jonassen.calculater;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText Adder1 = (EditText)findViewById(R.id.add_f);
        final EditText Adder2 = (EditText)findViewById(R.id.add_s);
        final TextView Ans = (TextView)findViewById(R.id.final_ans);

        Button getAns = (Button)findViewById(R.id.get);
        getAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n1,n2,ans;
                Ans.setText("");
                n1 = Integer.parseInt(Adder1.getText().toString());
                n2 = Integer.parseInt(Adder2.getText().toString());
                ans = n1 + n2;
                Ans.setText(Integer.toString(ans));
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
