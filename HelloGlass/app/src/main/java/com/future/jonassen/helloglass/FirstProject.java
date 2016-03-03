package com.future.jonassen.helloglass;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.zxing.BarcodeFormat;

import java.util.EnumSet;

/*
    wiki要求继承 FragmentActivity，并且有要求实现IScanResultHandler
 */
public class FirstProject extends FragmentActivity implements IScanResultHandler {

    private BarcodeFragment fragment;


    private CardScrollView mCardScroller;
    private View mView;
    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        /*
            FLAG_KEEP_SCREEN_ON
            在运行期间屏幕常亮,基础的android的操作
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*
            使用允许 ok glass 命令 调出二级菜单。
         */
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        mView = buildView();
        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Object getItem(int position) {
                return mView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mView;
            }

            @Override
            public int getPosition(Object item) {
                if (mView.equals(item)) {
                    return 0;
                }
                return AdapterView.INVALID_POSITION;
            }
        });

        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOptionsMenu();
            }
        });

        mGestureDetector = createGestureDetector(this);
        setContentView(mCardScroller);

    }

    /*
        在libv2中有个Interface 需要实现scanResult的处理。
    */
    @Override
    public void scanResult(ScanResult scanResult) {
        String result = scanResult.getRawResult().getText(); // 结果

        /*
            设置显示的位置啊什么的
         */
        Toast toast = Toast.makeText(getApplicationContext(), "QRCode : " + result, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        /*
            按照wiki,在显示成功后调用restart，交给fm来实现新的fragment的生命周期

            You don't have to call the start and stop camera actions in your activity for activity state changes as the library takes care of state change by itself.
         */
        fragment.restart();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.main, menu);
//            fragment.restart();
            return true;
        }

        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId == Window.FEATURE_OPTIONS_PANEL) {
            /*
                一旦条目被选中就进入这里，启动service view的fragment

                进行相应的取景分析等
             */
            setContentView(R.layout.scanlayout);
            /*
                将需要的fragment交给 fragmentManager进行管理
             */
            fragment = (BarcodeFragment) getSupportFragmentManager().findFragmentById(R.id.viewDetecter);

            /*
                根据选定的条目进行相应的解析设置
             */
            switch (item.getItemId()) {
                case R.id.QRCode:
                    fragment.setDecodeFor(EnumSet.of(BarcodeFormat.QR_CODE));
                    break;

                case R.id.BarCode:
                    fragment.setDecodeFor(EnumSet.of(BarcodeFormat.UPC_A));
                    break;
            }

            /*
            libv2的要求，利用这个函数可以将扫描结果直接传送给当前的class --> FirstProject

            原文：Once registered with Fragment as setScanResultHandler the result will be forwarded to the registered class.

         */
            fragment.setScanResultHandler(this);
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public View buildView() {

        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.COLUMNS);
        card.addImage(R.drawable.back);
        card.setText("Welcome");
        return card.getView();
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    openOptionsMenu();
                    return true;
//                } else if (gesture == Gesture.TWO_TAP) {
//                    // do something on two finger tap
//                    return true;
//                } else if (gesture == Gesture.SWIPE_RIGHT) {
//                    // do something on right (forward) swipe
//                    return true;
//                } else if (gesture == Gesture.SWIPE_LEFT) {
//                    // do something on left (backwards) swipe
//                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN) {
                    finish();
                }
                return false;
            }
        });

//        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
//            @Override
//            public void onFingerCountChanged(int previousCount, int currentCount) {
//                // do something on finger count changes
//            }
//        });
//
//        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
//            @Override
//            public boolean onScroll(float displacement, float delta, float velocity) {
//                // do something on scrolling
//                return true;
//            }
//        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

}
