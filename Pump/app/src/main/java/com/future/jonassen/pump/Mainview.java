package com.future.jonassen.pump;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("NullArgumentToVariableArgMethod")
public class Mainview extends Activity {
    private static final int TASK_ML_WORK = 10001;
    private static final int TASK_UL_WORK = 10002;
    private static final int TASK_END_WORK = 10003;
    private static final int TASK_SETTING_DONE = 10004;
    private static final int TASK_BUTTON_CLICK = 10005;


    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnSet;


    private Class<StatusVal> cls;
//    private StatusVal st;


    private StatusVal statusVal;

    private tPumpControl pump = new tPumpControl();

    private Thread mainThread;
    private Handler mainHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);

            switch (msg.what) {
                case TASK_ML_WORK:
                    break;

                case TASK_UL_WORK:
                    break;

                case TASK_SETTING_DONE:
                    break;

                case TASK_END_WORK:
                    break;

                case TASK_BUTTON_CLICK:
                    if (msg.arg2 == 1) {
                        btnPause.setText("Resume");
                    } else if (msg.arg2 == 0) {
                        btnPause.setText("Pause");
                    }
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);
        pump.PumpIOInit();
        mainThread = new Thread(PumpControl);

        /*
            开始资源定位
         */
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnSet = (Button) findViewById(R.id.btn_set);


        /*
            设置监听器
         */
        btnStart.setOnClickListener(btnListener);
        btnPause.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnSet.setOnClickListener(btnListener);

//        st.getClass();

        statusVal = new StatusVal();
        cls = (Class<StatusVal>) statusVal.getClass();

        Method method = null;
        try {
            //反射类获取方法
            method = cls.getDeclaredMethod("OutputMessage", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        try {
            //反射类调用原类的私有方法
            method.invoke(statusVal, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 若调用私有方法，必须抑制java对权限的检查
    }

    Runnable PumpControl = new Runnable() {
        @Override
        public void run() {
            while (statusVal.isStart()) {

            }
        }
    };

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button c = (Button) v;

            switch (c.getId()) {
                case R.id.btn_start:
                    statusVal.Start();
                    break;
                case R.id.btn_set:
                    Toast.makeText(Mainview.this, "SET BUTTON", Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn_pause:
                    if (statusVal.isPause()) {
                        statusVal.PauseSetter(false);
                        MessageSender(TASK_BUTTON_CLICK, 0, 0);
                    } else {
                        statusVal.PauseSetter(true);
                        MessageSender(TASK_BUTTON_CLICK, 0, 1);
                    }

                    break;
                case R.id.btn_stop:
                    statusVal.Stop();
                    break;
                default:
                    break;
            }
        }
    };

    private void MessageSender(int TaskId, int arg1, int arg2) {
        Message m = mainHanlder.obtainMessage();
        m.what = TaskId;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.sendToTarget();
    }

    public Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {
        Class ownerClass = owner.getClass();

        Class[] argsClass = new Class[args.length];

        for (int i = 0, j = args.length; i < j; i++) {

            argsClass[i] = args[i].getClass();

        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(owner, args);

    }

}
