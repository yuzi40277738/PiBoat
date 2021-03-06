package com.qianmi.boat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qianmi.boat.R;
import com.qianmi.boat.utils.L;

/**
 * Created by Chen Haitao on 2015/8/21.
 */
public class Controller extends View {

    private Context mContext;
    private Paint mPaint;
    private Bitmap mBg;
    private Bitmap mReadyPoint;
    private Bitmap mTouchPoint;
    private int[] mPosInit;
    private int[] mPosCurrent;
    private int mWidth;
    private int mHeight;
    private boolean validate = false;
    private boolean actionValidate = true;

    private int status;

    private static final int STATUS_INIT = 1;
    private static final int STATUS_DOWN = 2;
    private static final int STATUS_MOVE = 3;
    private static final int STATUS_UP = 4;
    private static final int STATUS_END = 5;

    public static final int DIRECTION_LEFT = 100;
    public static final int DIRECTION_RIGHT = 101;
    public static final int DIRECTION_UP = 102;
    public static final int DIRECTION_DOWN = 103;

    private Trigger mTrigger;

    public Controller(Context context) {
        this(context, null);

    }

    public Controller(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mPosInit = new int[2];
        mPosCurrent = new int[2];
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBg = BitmapFactory.decodeResource(getResources(), R.drawable.bg_m);
        L.d("(bg)width:" + mBg.getWidth() + ", height: " + mBg.getHeight());
        mReadyPoint = BitmapFactory.decodeResource(getResources(), R.drawable.ready_point);
        L.d("(readyPoint)width:" + mReadyPoint.getWidth() + ", height: " + mReadyPoint.getHeight());
        mTouchPoint = BitmapFactory.decodeResource(getResources(), R.drawable.touch_point);
        L.d("(touchPoint)width:" + mTouchPoint.getWidth() + ", height: " + mTouchPoint.getHeight());

        status = STATUS_INIT;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        L.d("width:" + mWidth + ", height: " + mHeight);
    }

       @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                status = STATUS_DOWN;
                if (event.getX() > mWidth * 2 / 5 && event.getAction() < mWidth * 3 / 5 && event.getY() > mHeight * 2 / 5 && event.getY() < mHeight * 3 / 5) {
                    mPosInit[0] = (int)event.getX();
                    mPosInit[1] = (int) event.getY();
                    postInvalidate();
                    validate = true;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (validate) {
                    status = STATUS_MOVE;
                    mPosCurrent[0] = (int)event.getX();
                    mPosCurrent[1] = (int) event.getY();
                    postInvalidate();

                    if (mPosCurrent[0] < mWidth / 5) {
                        if (actionValidate && mTrigger != null) {
                            mTrigger.onTrigger(DIRECTION_LEFT);
                            actionValidate = false;
                        }
                    }else if (mPosCurrent[0] > mWidth * 4 / 5) {
                        if (actionValidate && mTrigger != null) {
                            mTrigger.onTrigger(DIRECTION_RIGHT);
                            actionValidate = false;
                        }
                    }else if (mPosCurrent[1] < mHeight / 5) {
                        if (actionValidate && mTrigger != null) {
                            mTrigger.onTrigger(DIRECTION_UP);
                            actionValidate = false;
                        }
                    }else if (mPosCurrent[1] > mHeight * 4 / 5) {
                        if (actionValidate && mTrigger != null) {
                            mTrigger.onTrigger(DIRECTION_DOWN);
                            actionValidate = false;
                        }
                    }

                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                validate = false;
                status = STATUS_UP;
                mPosCurrent[0] = (int)event.getX();
                mPosCurrent[1] = (int) event.getY();
                postInvalidate();
                actionValidate = true;

                break;
        }

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (status) {
            case STATUS_INIT:
                canvas.drawBitmap(mBg, 20, 20, mPaint);
                canvas.drawBitmap(mReadyPoint, mWidth / 2 - mReadyPoint.getWidth() /2 , mHeight / 2  -  mReadyPoint.getHeight() /2 , mPaint);
                break;

            case STATUS_DOWN:
                canvas.drawBitmap(mBg, 20, 20, mPaint);
                canvas.drawBitmap(mTouchPoint, mWidth / 2 - mTouchPoint.getWidth() /2 , mHeight / 2  -  mTouchPoint.getHeight() /2 , mPaint);

                break;

            case STATUS_MOVE:
                canvas.drawBitmap(mBg, 20, 20, mPaint);
                canvas.drawBitmap(mTouchPoint, mPosCurrent[0] - mTouchPoint.getWidth() /2 , mPosCurrent[1]  -  mTouchPoint.getHeight() /2 , mPaint);
                break;

            case STATUS_UP:
                canvas.drawBitmap(mBg, 20, 20, mPaint);
                canvas.drawBitmap(mBg, 20, 20, mPaint);
                canvas.drawBitmap(mReadyPoint, mWidth / 2 - mReadyPoint.getWidth() /2 , mHeight / 2  -  mReadyPoint.getHeight() /2 , mPaint);
                break;

            case STATUS_END:

                break;


        }
    }

    public void setTrigger(Trigger trigger) {
        this.mTrigger = trigger;
    }

    public void removeTrigger() {
        this.mTrigger = null;
    }

    public interface Trigger {
        void onTrigger(int direction);
    }
}
