package com.example.ohbab_9_test.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class LeftMenuView extends FrameLayout {

    static final float MODAL_ALPHA = 170.0f;
    ViewGroup uiMenu;
    ViewGroup viewModal;
    int uiStatus = 0;
    Activity activity;

    public LeftMenuView(Context context) {
        this(context, null);
    }

    public LeftMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setContentView(Activity activity, View menuView, int menuWidth) throws Exception {
        this.activity = activity;
        uiMenu.addView(menuView);
        uiMenu.setBackgroundColor(Color.WHITE);
        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
        l.leftMargin = -menuWidth;
        l.width = menuWidth;
        uiMenu.setLayoutParams(l);
    }

    void init(Context context) {
        viewModal = new FrameLayout(context);
        viewModal.setBackgroundColor(Color.TRANSPARENT);
        addView(viewModal, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

        uiMenu = new FrameLayout(context);
        addView(uiMenu, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));

        setOnTouchListener(new MyOnTouchListener());
    }

    public void uiSet(float x) {
        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();

        float alpha = (float) (l.width + l.leftMargin) / (float) l.width;
        int intAlpha = (int) (alpha * MODAL_ALPHA);
        viewModal.setBackgroundColor(Color.argb(intAlpha, 0, 0, 0));

        l.leftMargin = (int) x;
        uiMenu.setLayoutParams(l);
    }

    public void uiUp() {
        uiStatus = 1;
        viewModal.setVisibility(View.VISIBLE);
        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
        move(uiMenu, -l.width, 0);
    }

    public void uiDown() {
        uiStatus = 0;
        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
        move(uiMenu, 0, -l.width);

    }


    class MyRunnable implements Runnable {
        float cvel = 300;
        View movingView;
        int from;
        int to;

        public MyRunnable(View movingView, int from, int to) {
            this.movingView = movingView;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            LayoutParams l = (LayoutParams) movingView.getLayoutParams();
            float direction = 1;
            if (from > to)
                direction = -1;

            float distance = cvel / 100.0f * 2;
            cvel -= 0.3f;

            if (distance < 3f)
                distance = 3f;

            float now = (l.leftMargin + distance * direction);

            if (
                    (direction >= 0 && now >= to)
                            || (direction < 0 && now <= to)
            ) {
                if (mTimer2 != null) {
                    mTimer2.cancel();
                    mTimer2.purge();
                }
                mTimer2 = null;
                now = to;
            }
            if (to <= 0)
                uiSet(now);
        }
    }

    Timer mTimer2;

    public void stopMove() {
        if (mTimer2 != null) {
            mTimer2.purge();
            mTimer2.cancel();
            mTimer2 = null;
        }
    }

    public void move(final View movingView, final int from, final int to) {
        if (mTimer2 != null)
            return;

        TimerTask mTask = new TimerTask() {
            MyRunnable run = null;

            @Override
            public void run() {
                if (run == null)
                    run = new MyRunnable(movingView, from, to);
                activity.runOnUiThread(run);
            }
        };

        mTimer2 = new Timer();
        mTimer2.scheduleAtFixedRate(mTask, 0, 1);
    }

    public boolean isOpen() {
        return uiStatus == 1;
    }


    class MyOnTouchListener implements OnTouchListener {
        int pointerId;
        float touchStartX;
        long touchTime;
        float uiMenuStartX;
        float touchStartXOrigin;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (uiStatus == 1) {
                if (pointerId != -1 && event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getX(pointerId);
                    LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
                    float newUiMenuX = uiMenuStartX - (touchStartX - x);
                    if (newUiMenuX <= 0 && newUiMenuX >= -l.width) {
                        uiSet(newUiMenuX);
                        stopMove();
                    } else if (newUiMenuX > 0) {
                        touchStartX = event.getX(pointerId);
                        uiMenuStartX = l.leftMargin;
                        uiSet(0);
                        stopMove();
                    } else {
                        touchStartX = event.getX(pointerId);
                        uiMenuStartX = l.leftMargin;
                        uiSet(-l.width);
                        stopMove();
                    }
                } else if (pointerId != -1 && event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    long nowTouchTime = System.currentTimeMillis();
                    float x = event.getX(pointerId);
                    if (nowTouchTime - touchTime < 500.0f && Math.abs(touchStartXOrigin - x) < 10.0f) {
                        uiDown();
                        pointerId = -1;
                    } else if (nowTouchTime - touchTime < 500.0f && touchStartXOrigin - x > 0) {
                        uiDown();
                        pointerId = -1;
                    } else {
                        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
                        float newUiMenuX = uiMenuStartX - (touchStartX - x);
                        if (newUiMenuX >= -(float) l.width / 2.0f) {
                            uiUp();
                            pointerId = -1;
                        } else {
                            uiDown();
                            pointerId = -1;
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    pointerId = event.getPointerId(0);
                    try {
                        touchStartXOrigin = touchStartX = event.getX(pointerId);
                        LayoutParams l = (LayoutParams) uiMenu.getLayoutParams();
                        uiMenuStartX = l.leftMargin;
                        touchTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        pointerId = -1;
                    }
                }
                return true;
            }

            return false;
        }
    }
}
