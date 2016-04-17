package com.future.jonassen.bluetooth;

/**
 * Created by Jonassen on 16/4/14.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothService {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Context context;
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private static ConnectThread mConnectThread;
    //private ConnectedThread mConnectedThread;
    private static ConnectedThread mConnectedThread;
    private BroadcastReceiver mReceiver;
    private static int mState = 0x00;

    private AlertDialog.Builder builder;
    private static BluetoothSocket mySocket;

    private static final int REQUEST_ENABLE_BT = 1;

    public static final int MESSAGE_READ = 0x02;
    public static final int MESSAGE_WRITE = 0x03;
    public static final int MESSAGE_STATE_CHANGE = 0x04;
    public static final int MESSAGE_DEVICE_NAME = 0x05;

    public static final int STATE_NONE = 0x10;
    public static final int STATE_CONNECTING = 0x11;
    public static final int STATE_CONNECTED = 0x12;

    public BluetoothService(Context context, Handler handler) {
        this.context = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mAdapter == null) {
            builder = new AlertDialog.Builder(this.context);
            builder.setTitle(R.string.alert_title);
            builder.setMessage(R.string.alert_message);
            builder.create().show();
        }
        mHandler = handler;
        if (mState == 0x00) {
            mState = STATE_NONE;
        }
    }

    public synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    //step 1
    public void start() {
        if (mAdapter != null) {
            enableBluetooth();
        }
    }

    //step 2
    public synchronized void connect(BluetoothDevice bluetoothDevice) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    //step 3
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        String name = device.getName();
        mHandler.obtainMessage(MESSAGE_DEVICE_NAME, name).sendToTarget();//°ÑdevicenameµÄÏûÏ¢ËÍµ½handlerÄÇ±ßÈ¥

        setState(STATE_CONNECTED);
    }

    //step 4
    public void write(byte[] out, int offset, int counts) {
        ConnectedThread r;

        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            //if(mConnectedThread == null)
            //System.out.println("mConnectedThread is null @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            r = mConnectedThread;
            //if(r == null)
            //System.out.println("r is null @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }

        if (offset == -1) {
            if (r == null)
                System.out.println("r is null @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            else
                r.write(out);
        } else if (offset >= 0 && counts > 0) {
            if (r == null)
                System.out.println("r is null @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            else
                r.write(out, offset, counts);
        } else {
            return;
        }
    }

    //step 5
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }


    private void enableBluetooth() {
        if (!mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else
            return;
    }

    //discover the bluetooth devices nearby and return a list of devices;
    public ArrayList<String> discoveringDevices() {
        mAdapter.startDiscovery();

        mReceiver = new DiscoveringReceiver();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        return ((DiscoveringReceiver) mReceiver).getArrayAdapter();
    }

    private class DiscoveringReceiver extends BroadcastReceiver {
        private ArrayList<String> arrayAdapter = new ArrayList<String>();

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        public ArrayList<String> getArrayAdapter() {
            return arrayAdapter;
        }

    }

    public void setReadHandler(Handler handler) {
        if (mConnectedThread != null) {
            mConnectedThread.setHandler(handler);
        }
    }

    //Don't forget to unregister during onDestory;
    public void unRegisterReceiver() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
    }

    //connecting to the remote device with the given UUID
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            mmDevice = bluetoothDevice;
            BluetoothSocket tmp = null;

            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                // TODO: handle exception
            }
            mmSocket = tmp;
            mySocket = mmSocket;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            mAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (Exception e) {
                // TODO: handle exception
                try {
                    mmSocket.close();
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }

            //manage the connection//
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }

    //This thread runs during a connection with a remote device.
    //It handles all incoming and outgoing transmissions.
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Handler readHandler;
        //private final BufferedInputStream bInputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            //bInputStream = new BufferedInputStream(mmInStream);
        }

        public void setHandler(Handler readHandler) {
            if (this.readHandler == null) {
                this.readHandler = readHandler;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            byte[] buffer = new byte[1024];
            int bytes = 0;
            int offset = 0;
            System.out.println("Connected Thread running");

            while (true) {
                try {
//                    System.out.println("before read");
                    //bytes = mmInStream.available();
//                    System.out.println(bytes);
                    //bytes = mmInStream.read(buffer);
                    bytes = mmInStream.read(buffer, offset, 8);//Ö»¶ÁÒ»´Î
                    //System.out.println(new String(buffer,offset,bytes,"ASCII"));
                    //System.out.println(bytes);
                    //if(mmInStream != null)
                    //System.out.println("mmInStream isn't null");
                    //mmInStream.read(buffer);
                    //bytes = mmInStream.read();
                    //System.out.println(bytes);
//                    System.out.println("after read");
                    if (readHandler != null) {
                        System.out.println("readHandler != null+++++++++++++++++++++++++++++");
                        readHandler.obtainMessage(MESSAGE_READ, bytes, offset, buffer).sendToTarget();
                        System.out.println("Message_read sent ++++++++++++++++++++++");
                    }
                    //System.out.println(new String(buffer,offset,bytes,"ASCII") + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    offset += bytes;
                    if (offset >= 1016) {
                        offset = 0;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    //break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, bytes).sendToTarget();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void write(byte[] bytes, int offset, int counts) {
            try {
                mmOutStream.write(bytes, offset, counts);
                mHandler.obtainMessage(MESSAGE_WRITE, offset, counts, bytes).sendToTarget();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void cancel() {
            try {
                mmInStream.close();//¹Ø±ÕÊäÈëÊä³öÁ÷ºÍÌ×½Ó×Ö
                mmOutStream.close();
                mmSocket.close();
            } catch (Exception e) {
                // TO-5432O: handle exception
            }
        }


    }
    ////////////////////////test///////////////////////////////////


}

