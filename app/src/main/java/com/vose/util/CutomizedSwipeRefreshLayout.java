package com.vose.util;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 *
 *   Extended SwipeRefreshLayout to fix conflicts with horizontal scrolling
 *
 *   code taken from:
 *
 *   http://stackoverflow.com/questions/23989910/horizontalscrollview-inside-swiperefreshlayout
 *
 *   This restricts the X movement to a narrow amount, so that horizontal scrolling works
 *   smoothly.
 *
 */
//it's out of use now
public class CutomizedSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;

    public CutomizedSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (xDiff > mTouchSlop) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}

