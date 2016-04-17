package com.future.jonassen.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {

    private Button btnclick;
    private Button btnsend;
    private EditText txWordSend;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService;

    public static int REQUEST_CONNECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*
            设定一些界面的属性
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        /*
            设定按键的属性
         */
        btnclick = (Button) findViewById(R.id.btnconnect);
        btnclick.setOnClickListener(stateChangeListener);

        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setOnClickListener(stateChangeListener);

        txWordSend = (EditText) findViewById(R.id.txWord);

        /*
            注册蓝牙服务的监听器
         */

//        IntentFilter connectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//        IntentFilter disconnectreqFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//        IntentFilter disconnectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//
//        this.registerReceiver(mReceiver, connectFilter);
//        this.registerReceiver(mReceiver, disconnectFilter);
//        this.registerReceiver(mReceiver, disconnectreqFilter);

        /*
            蓝牙的服务打开。
         */
        mBluetoothService = new BluetoothService(MainActivity.this, this.mHandler);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    private View.OnClickListener stateChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnconnect) {
            /*
                检测蓝牙服务是否打开
             */
                mBluetoothService.start();
            /*
                如果什么都还没开始做的话就在按键按下之后进入DeviceList中去选择配对等
             */
                if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                    Intent searchService = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(searchService, REQUEST_CONNECT);
                } else if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                /*
                    如果连上了就关闭吧
                 */
                    mBluetoothService.stop();
                    btnclick.setText("Connect");
                    Toast.makeText(MainActivity.this, "stop state", Toast.LENGTH_SHORT).show();
                }
            }
            else if (v.getId() == R.id.btnsend) {
                String line  = txWordSend.getText().toString();
                byte[] bline = line.getBytes();
                mBluetoothService.write(bline, -1, bline.length);

            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE: {
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            btnclick.setText("Disconnect");
                            Log.i("mHandler", "STATE_CONNECTED");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.i("mHandler", "STATE_CONNECTING");
                            break;
                        case BluetoothService.STATE_NONE:
                            Log.i("mHandler", "STATE_NONE");

                            break;
                    }

                    break;
                }

                case BluetoothService.MESSAGE_DEVICE_NAME:
                    Log.i("mHandler", "Device Name is " + msg.obj.toString());
                    break;


            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            选择好了之后得到另外一台手机的配对地址后进行配对。 //完成连接。
         */
        if (requestCode == REQUEST_CONNECT) {
            if (resultCode == Activity.RESULT_OK) {
                String addr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
                mBluetoothService.connect(device);
            }
        }
    }

    /*
        一旦确定了连接状态之后， 设定 readhandler之后就可以进行传送了。
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    mBluetoothService.setReadHandler(mHandler);
                    Log.i("Bt", "ACTION_ACL_CONNECTED");
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    Log.i("Bt", "ACTION_ACL_DISCONNECT_REQUESTED");
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.i("Bt", "ACTION_ACL_DISCONNECTED");
                    break;
            }
        }
    };


}
