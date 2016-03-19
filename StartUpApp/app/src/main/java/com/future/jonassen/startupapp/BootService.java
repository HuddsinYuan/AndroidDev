package com.future.jonassen.startupapp;

/**
 * Created by Jonassen on 16/3/19.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootService extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
        Log.i("aaa", "bbbbbbb");
        mainActivityIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mainActivityIntent);

    }
}