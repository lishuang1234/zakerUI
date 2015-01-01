package com.ls.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.main.ls.zakerui.R;

/**
 * Created by ls on 2014/12/31 0031.
 */
public class PullDoorView extends RelativeLayout {
    private Context context;
    private Scroller scroller;
    private int mScreenWidth;
    private int mScreenHeight;
    private ImageView imageView;
    private int mLastDownY;
    private int mCurrY;
    private int mDistenY;
    private boolean closeFlag;

    public PullDoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setViews();

    }


    public PullDoorView(Context context) {
        super(context);
        this.context = context;
        setViews();
    }


    private void setViews() {

        BounceInterpolator polator = new BounceInterpolator();
        scroller = new Scroller(context, polator);
        //获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics display = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(display);
        mScreenWidth = display.widthPixels;
        mScreenHeight = display.heightPixels;

        //设置背景透明
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
        //添加上层显示的ImageView
        imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.back2);
        addView(imageView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = (int) event.getY();//相对于父视图坐标
                return true;//拦截触摸事件
            case MotionEvent.ACTION_MOVE:
                mCurrY = (int) event.getY();
                mDistenY = mCurrY - mLastDownY;
                if (mDistenY < 0) {//向上滑动执行操作
                    scrollTo(0, -mDistenY);//原先为（0.0）、将整个父视图的左上角定为(0,0)，
                    // 再移动这个屏幕的左上角到父视图的点(x,y)处，注意此处的x和y是根据父视图的坐标系来定的。
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mDistenY < 0) {
                    if (Math.abs(mDistenY) > mScreenHeight / 2) {
                        startBounceAnim(this.getScrollY(), mScreenHeight, 450);
                        closeFlag = true;
                    } else {
                        closeFlag = false;
                        startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startBounceAnim(int scrollY, int mScreenHeight, int i) {
        scroller.startScroll(0, scrollY, 0, mScreenHeight, i);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
           System.out.println("scroller.getCurrX(): " + scroller.getCurrX() + "  scroller.getCurrY()" + scroller.getCurrY());
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();

        } else {
            if (closeFlag) {
                this.setVisibility(View.GONE);
            }

        }
    }
}
