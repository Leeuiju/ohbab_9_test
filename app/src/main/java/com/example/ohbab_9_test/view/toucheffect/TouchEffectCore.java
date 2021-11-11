package com.example.ohbab_9_test.view.toucheffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchEffectCore {

    boolean isTouching = false;
    public boolean isTouchAnimating = false;
    long touchStartTime = -1;
    long animationStartTime = -1;
    int touchStartX, touchStartY;
    Paint paint = new Paint();
    Paint circlePaint = new Paint();
    Rect thisRect = new Rect();
    boolean rounded = false;
    int roundedPathWidth = 0;
    Path roundedPath;
    View father;
    boolean outerLine = false;
    int outerLineColor = 0;
    Paint outerLinePaint = null;
    String appLink = null;

    public boolean isCalcDispatchDrawRect = false;
    public boolean isEffectOn = true;

    MotionEvent ev;
    int width;
    int height;

    void parseMe(View v, Context context, AttributeSet attrs) {
        father = v;
    }

    public void onTouchEvent(MotionEvent ev, int width, int height) {
        //Log.i(TAG, "onTouchEvent "+ ev.getAction() + " " + ev.getX() + "," + ev.getY());
        this.ev = ev;
        this.width = width;
        this.height = height;
    }

    public void notTouchState() {
        if(isTouching)
        {
            isTouching = false;
            animationStartTime = System.currentTimeMillis();
        }
    }

    final int xy[] = new int[2];
    public void touchState(View v) {
        if(!isEffectOn)
            return ;
        //Log.i(TAG, "touchState");
        isTouching = true;
        touchStartTime = System.currentTimeMillis();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        circlePaint.setStyle(Paint.Style.FILL);

        if (ev != null) {
            touchStartX = (int) ev.getX();
            touchStartY = (int) ev.getY();
            v.getLocationInWindow(xy);
            //Log.i(TAG, "touchState setted " + touchStartX + "," + touchStartY + " / " + xy[0] +","+ xy[1]);
        }
        thisRect.top = 0;
        thisRect.left = 0;
        thisRect.right = width;
        thisRect.bottom = height;
        isTouchAnimating = true;
    }

    public void dispatchDraw(Canvas canvas) {
        if(father != null && isCalcDispatchDrawRect) {
            thisRect.top = 0;
            thisRect.left = 0;
            thisRect.right = father.getWidth();
            thisRect.bottom = father.getHeight();
        }

        if (outerLine && father != null) {
            canvas.drawRect(0, 0, father.getWidth(), father.getHeight(), outerLinePaint);
        }

        if (isTouching) {
            long now = System.currentTimeMillis();
//			float rate = (now - touchStartTime) / 300.0f; // +(int)(0x44*rate)
            paint.setColor(Color.argb(0x20, 0x00, 0x00, 0x00));
            canvas.drawRect(thisRect, paint);
//			circlePaint.setColor(Color.argb(0x15, 0x00, 0x00, 0x00));
//			canvas.drawCircle(touchStartX, touchStartY, (int)( (500)/2*rate), circlePaint);
            //Log.i(TAG, "isTouching      - " + (touchStartX) + " " + (touchStartY) + " " + (int)( (500)/2*rate));
        }
        else if(isTouchAnimating)
        {
            long now = System.currentTimeMillis();
            float rate = (now - touchStartTime) / 800.0f; // +(int)(0x44*rate)
            float animationRate = (now - animationStartTime) / 800.0f; // +(int)(0x44*rate)

            if(animationRate >= 1)
            {
                isTouchAnimating = false;
                return ;
            }
            float rectAlphaRate = 1.0f-animationRate;
            if(rectAlphaRate > 0 )
            {
                paint.setColor(Color.argb( (int)(0x20 * rectAlphaRate/2.0f), 0x00, 0x00, 0x00));
                canvas.drawRect(thisRect, paint);
            }

            circlePaint.setColor(Color.argb((int)(0x15 * rectAlphaRate), 0x00, 0x00, 0x00));
            canvas.drawCircle(touchStartX, touchStartY, (int)( (width)*animationRate), circlePaint);

            //Log.i(TAG, "isTouchAnimating- " + (touchStartX) + " " + (touchStartY) + " " + (int)( (500)/2*rate));
            if(father != null)
                father.invalidate();
        }
    }
}
