package com.future.jonassen.helloandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Jonassen on 15/10/26.
 */
public class UserView extends View {
    public UserView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.testpic);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if(bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
}