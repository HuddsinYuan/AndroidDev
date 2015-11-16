package com.future.jonassen.buttonlistview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener
{
    public int jobcounter = 0;
    private static final int ADDJOB = 1001;
    private static final int PROCESSADD = 1002;
    private static final int PROCESSDONE = 1003;
    private static final int ITEMBUTTON_CLICKED = 1004;

    private static final String TAG = "ListViewTest";

    protected static final long REFRESH_INTERVAL = 100;

    protected boolean mBusy = false;

    private long lastupdatatime = 0;

    Object lock = new Object();

    ListView listView;
    Button buttonadd;

    AddjavaListAdapter adapter;
    LayoutInflater inflater;

    LinkedHashMap<Integer, ListInfo> listdatas;

    LinkedHashMap<Integer, ListInfo> addlist = new LinkedHashMap<Integer, MainActivity.ListInfo>();
    List<Integer> delidlist = new ArrayList<Integer>();


    Handler mainHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            long v = System.currentTimeMillis() - lastupdatatime;

            switch (msg.what)
            {
                case ADDJOB:
                    updateListView();
                    lastupdatatime += REFRESH_INTERVAL;
                    break;
                case PROCESSADD:
                    if (!mBusy && v > REFRESH_INTERVAL)
                    {
                        updateListView();
                        lastupdatatime = System.currentTimeMillis();
                    }
                    break;

                case ITEMBUTTON_CLICKED:
                    if (!listdatas.get(msg.arg1).running)
                    {
                        synchronized (listdatas.get(msg.arg1).lock)
                        {
                            listdatas.get(msg.arg1).lock.notifyAll();
                        }
                    }
                    listdatas.get(msg.arg1).running = !listdatas.get(msg.arg1).running;

                    if (!mBusy)
                    {
                        updateListView();
                        lastupdatatime += REFRESH_INTERVAL;
                    }
                    break;
                case PROCESSDONE:
                    synchronized (lock)
                    {
                        delidlist.add(msg.arg1);
                    }
                    updateListView();
                    lastupdatatime += REFRESH_INTERVAL;
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        listdatas = new LinkedHashMap<Integer, ListInfo>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview001);
        adapter = new AddjavaListAdapter();
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                switch (scrollState)
                {
                    case SCROLL_STATE_IDLE:
                        mBusy = false;
                        break;

                    case SCROLL_STATE_TOUCH_SCROLL:
                        mBusy = true;
                        break;

                    case SCROLL_STATE_FLING:
                        mBusy = true;
                        break;
                }
            }

            @Override
            public void onScroll(
                    AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount)
            {

            }
        });

        buttonadd = (Button) findViewById(R.id.button_addjob);
        buttonadd.setOnClickListener(this);

        inflater = LayoutInflater.from(this);

    }

    private void updateListView()
    {
        synchronized (lock)
        {
            for (Integer delid : delidlist)
            {
                Log.i(TAG, "ProcessDone done!" + delid);
                listdatas.remove(delid);
            }
            listdatas.putAll(addlist);

            for (ListInfo info : addlist.values())
            {
                new WorkThread("workthread#" + jobcounter, info).start();
                jobcounter++;
            }

            delidlist.clear();
            addlist.clear();
        }
        adapter.notifyDataSetChanged();
    }

    class ListInfo
    {
        @Override
        public String toString()
        {
            return "ListInfo [Index" + index + ", id=" + id + ", running="
                    + running + ", title=" + ", progress=" + process
                    + "]";
        }

        int index;
        long id;
        boolean running = true;

        String title;
        int process;

        Object lock = new Object();
    }

    class ViewHolder
    {
        TextView text;
        ProgressBar bar;
        Button button;
        ItemClickListener listener;
    }

    class AddjavaListAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return listdatas.keySet().size();
        }

        @Override
        public Object getItem(int position)
        {
            Object[] d = listdatas.keySet().toArray();
            int c = (Integer) d[position];
            return listdatas.get(c);
        }

        @Override
        public long getItemId(int position)
        {
            return ((ListInfo)this.getItem(position)).id;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            ViewHolder holder;

            if(v == null) {
                v = inflater.inflate(R.layout.single_item, null);
                holder = new ViewHolder();

                holder.text = (TextView)v.findViewById(R.id.text_item);
                holder.bar = (ProgressBar)v.findViewById(R.id.progressbar_item);
                holder.button = (Button)v.findViewById(R.id.button_item);
                holder.listener = new ItemClickListener();

                v.setTag(holder);
            }
            else {
                holder = (ViewHolder)v.getTag();
            }

            final ListInfo data = (ListInfo)getItem(position);

            holder.listener.setId(data.index);
            holder.button.setOnClickListener(holder.listener);

            holder.text.setText(data.title);
            holder.bar.setProgress(data.process);

            if(data.running) {
                holder.button.setText("stop");
            }
            else {
                holder.button.setText("start");
            }

            return v;

        }
    }

    class ItemClickListener implements View.OnClickListener
    {
        private int id;

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v)
        {
            Log.i(TAG, "button clicked! " + id + "|" + listdatas.get(id).running + "-->" +!listdatas.get(id).running);
            sendMessageToHandler(ITEMBUTTON_CLICKED, id);

        }
    }

    class WorkThread extends Thread
    {

        ListInfo in;

        public WorkThread(String name, ListInfo in)
        {
            super(name);
            this.in = in;
        }

        @Override
        public void run()
        {
            ListInfo sdata = listdatas.get(in.index);

            while (sdata.process < 100)
            {
                if (!listdatas.get(in.index).running)
                {
                    synchronized (listdatas.get(in.index).lock)
                    {
                        try
                        {
                            listdatas.get(in.index).lock.wait();
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                try
                {
                    Thread.sleep((int) (3000 * new Random().nextDouble()));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                sdata.process += new Random().nextInt(15) + 1;
                sendMessageToHandler(PROCESSADD, in.index);
            }
            if (sdata.process >= 100)
            {
                sendMessageToHandler(PROCESSDONE, in.index);
            }
        }
    }


    public void sendMessageToHandler(int what, int arg1)
    {
        Message msg = mainHandler.obtainMessage();
        msg.what = what;
        msg.arg1 = arg1;
        msg.sendToTarget();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_addjob:
            {
                ListInfo info = new ListInfo();
                info.title = "file" + jobcounter;
                info.process = 0;
                info.id = jobcounter;
                info.index = jobcounter;

                synchronized (lock)
                {
                    addlist.put(jobcounter, info);
                }
                sendMessageToHandler(ADDJOB, jobcounter);
                break;
            }
            default:
                break;
        }
    }
}
