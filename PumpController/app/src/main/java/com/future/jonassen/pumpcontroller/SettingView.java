package com.future.jonassen.pumpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

public class SettingView extends Activity
{
    private static final int ADDITEM = 10001;
    private static final int BACK = 10002;
    private static final int SURE = 10003;
    private int mItemCounter = 0;
    private static final String TAG = "MainActivity";

    private ListView list;
    private Button btn_add;
    private Button btn_back;

    private LayoutInflater mInflater;

    private MyAdapter adapter;
    public LinkedHashMap<Integer, Bean> listdata;
    public LinkedHashMap<Integer, Bean> intentdata;
    private Bean singlelist;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_view);

        mInflater = LayoutInflater.from(this);

        list = (ListView) findViewById(R.id.setting_menu);
        btn_add = (Button) findViewById(R.id.add_action);
        btn_back = (Button) findViewById(R.id.back_action);
        mItemCounter = 0;

        btn_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "ADD Button clicked");
                singlelist = new Bean(mItemCounter++);
                sendMessageToMainThread(ADDITEM, mItemCounter);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "BACK Button clicked");
                sendMessageToMainThread(BACK, 0);
            }
        });

        listdata = new LinkedHashMap<Integer, Bean>();


        adapter = new MyAdapter();
        list.setAdapter(adapter);


    }


    private void updataView(int idx)
    {
        listdata.put(idx, singlelist);
        adapter.notifyDataSetChanged();
    }


    public void DataTransfer()
    {
        Intent intent = new Intent();

        intent.setClass(SettingView.this, MainView.class);

        intentdata = new LinkedHashMap<>(listdata);

        intent.putExtra(TAG, new Gson().toJson(intentdata));

        Log.i(TAG, new Gson().toJson(intentdata));

        startActivity(intent);

    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.arg1)
            {
                case ADDITEM:
                {
                    updataView(msg.what);
                    break;
                }
                case BACK:
                {
                    DataTransfer();
                    break;
                }
                case SURE:
                {
                    int c = msg.what;
                    Bean bean = new Bean();
                    bean = intentdata.get(c);
//                    bean.setSure(true);
                }

                default:
                {
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public void sendMessageToMainThread(int id, int what)
    {
        Message msg = mHandler.obtainMessage();

        msg.arg1 = id;
        msg.what = what;
        msg.sendToTarget();
    }


    public class MyAdapter extends BaseAdapter
    {
        private LinkedHashMap<Integer, Bean> intentinfo = new LinkedHashMap<>();

        @Override
        public int getCount()
        {
            return listdata.keySet().size();
        }

        @Override
        public Object getItem(int position)
        {
            Object[] c = listdata.keySet().toArray();
            int d = (Integer) c[position];
            return listdata.get(d);
        }

        @Override
        public long getItemId(int position)
        {
            return ((Bean) this.getItem(position)).id;
        }


        @Override
        public View getView(
                int position, View convertView, ViewGroup parent)
        {
            ViewHolder mViewholder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.listview_item, null);
                mViewholder = new ViewHolder();
                mViewholder.mSpinner = (Spinner) convertView.findViewById(R.id.line);
                mViewholder.mEdittext = (EditText) convertView.findViewById(R.id.flux);
                mViewholder.mCheckbox = (CheckBox) convertView.findViewById(R.id.way);
                mViewholder.mButton = (Button) convertView.findViewById(R.id.sure);
                mViewholder.listener = new MakeSureListener();
                mViewholder.listener.setid(position);

                convertView.setTag(mViewholder);
            }
            else
            {
                mViewholder = (ViewHolder) convertView.getTag();
            }

            Bean mBean = (Bean) getItem(position);

            mViewholder.mButton.setOnClickListener(mViewholder.listener);
//            if (mBean.isSure())
//            {
//                int idtp = position;
//                mBean.index = idtp;
//                mBean.setId(mViewholder.mSpinner.getSelectedItemId());
//                mBean.setSflux(mViewholder.mEdittext.getText().toString());
//                mBean.setWay(mViewholder.mCheckbox.isChecked());
//                intentdata.put(mBean.index, mBean);
//            }
//            else
//            {
//                if (intentdata.get(position) != null)
//                {
//                    intentdata.remove(position);
//                }
//            }

            return convertView;

        }

        class MakeSureListener implements View.OnClickListener
        {
            private int idx;

            public void setid(int id)
            {
                this.idx = id;
            }

            public int getIdx()
            {
                return idx;
            }

            @Override
            public void onClick(View v)
            {

                sendMessageToMainThread(SURE, idx);

            }
        }

        class ViewHolder
        {
            Spinner mSpinner;
            EditText mEdittext;
            CheckBox mCheckbox;
            Button mButton;
            MakeSureListener listener;
//
//            public int id;
//            public String flux;
//            public boolean way;
        }

        public void LinkedMessageDisplay(LinkedHashMap<Integer, Bean> data)
        {
            int i = data.keySet().size();
            for (; i > 0; i--)
            {
//                Log.i("MyAdapter: ", data.get(i).OutputMessage());
            }
        }

    }

}
