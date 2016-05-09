package com.future.jonassen.fragmenttry;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SecondActivity extends Activity {

    private EditText etUsername;
    private EditText etPassname;
    private Button btnsubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_second);

        etUsername = (EditText) findViewById(R.id.etusername);
        etPassname = (EditText) findViewById(R.id.etpassword);
        btnsubmit = (Button) findViewById(R.id.btnclick);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassname.getText().toString();
                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                intent.putExtra("user", username);
                intent.putExtra("pass", password);
                setResult(1, intent);
                finish();

            }
        });
    }

}
