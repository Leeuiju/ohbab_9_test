package com.example.ohbab_9_test.view.toucheffect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchEffectRelativeLayout extends RelativeLayout {

    public TouchEffectRelativeLayout(Context context) {
        super(context);
    }
    public TouchEffectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        tc.parseMe(this, context, attrs);
    }
    public TouchEffectRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        tc.parseMe(this, context, attrs);
    }
    public TouchEffectCore tc = new TouchEffectCore();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int states[] = getDrawableState();
        for(int state : states)
        {
            if(state == android.R.attr.state_pressed)
            {
                tc.touchState(this);
                invalidate();
                return ;
            }
        }

        tc.notTouchState();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        tc.onTouchEvent(ev, getWidth(), getHeight());
        return super.onTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        tc.dispatchDraw(canvas);
        if(tc.isTouchAnimating)
            invalidate();
    }

}
