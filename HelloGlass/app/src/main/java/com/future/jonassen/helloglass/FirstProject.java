package com.future.jonassen.helloglass;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;

public class FirstProject extends FragmentActivity implements IScanResultHandler {

    private BarcodeFragment fragment;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.scanlayout);
        fragment = (BarcodeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        fragment.setScanResultHandler(this);
    }

    @Override
    public void scanResult(ScanResult scanResult) {
        String result = scanResult.getRawResult().getText();
        Toast toast = Toast.makeText(getApplicationContext(), "QRCode : " + result, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        fragment.restart();
    }
}
