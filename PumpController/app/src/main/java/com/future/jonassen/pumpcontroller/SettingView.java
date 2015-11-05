package com.future.jonassen.pumpcontroller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingView extends AppCompatActivity {
    protected Button btnAdd ;
    protected int ShowListVal = 0;
    protected List<HashMap<String, Object>> mData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAdd = (Button)findViewById(R.id.add_action);


        mData = getData();
        btnAdd.setOnClickListener(AddAction);
        MyAdapter adapter = new MyAdapter(this);
        ListView setting_menu = (ListView)findViewById(R.id.setting_menu);
        setting_menu.setAdapter(adapter);

    }


    private View.OnClickListener AddAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button button = (Button) v;
            switch (button.getId()) {
                case R.id.add_action:
                    ShowListVal = ShowListVal + 1;
                    break;
            }
        }
    };


    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            convertView = mInflater.inflate(R.layout.listview_item, null, false);
            EditText fluid_quantity = (EditText) convertView.findViewById(R.id.fluid_quantity);
            CheckBox way_sel = (CheckBox) convertView.findViewById(R.id.way_sel);
            TextView test_point = (TextView) convertView.findViewById(R.id.test_point);

            try {
                int PulseTime = new Integer(fluid_quantity.getText().toString()).intValue();
                //TODO: Fix this shit about How to prase String to int /  There is other problem.
                //TODO: Not the parse problem, There is other problem.
            }
            catch (NumberFormatException nfe) {
                Log.i("Important: ","1");
            }

            try {
                String Test_Val = new String();
                Test_Val = test_point.getText().toString();
            }
            catch (NumberFormatException nfe) {
                Log.i("Important: ","2");
            }


            /*TODO
                Transform PulseTime & way_ans to MainView
            */

            return convertView;
        }
    }

    private List<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        HashMap<String, Object> map = null;

        for(int idx = 0; idx <= 20; idx ++) {
            map = new HashMap<>();
            map.put("num", "No." + idx);
            list.add(map);
        }

        return list;
    }

    public void showInfo(int position) {
        getData();
    }



}
