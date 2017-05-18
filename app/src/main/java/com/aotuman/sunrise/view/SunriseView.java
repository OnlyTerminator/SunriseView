package com.aotuman.sunrise.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.aotuman.sunrise.R;

/**
 * Created by aotuman on 2017/2/23.
 */

public class SunriseView extends View {
    private Paint mPathPaint;
    private Paint mAnmationPaint;
    private int mStartPointX;
    private int mStartPointY;
    private int mEndPointX;
    private int mEndPointY;

    private int mMovePointX;
    private int mMovePointY;

    private int mCirclePointX;
    private int mCirclePointY;

    private Bitmap mSunBitmap;
    private int mBitmapW;
    private int mBitmapH;
    private boolean mNotUp;
    private boolean isNeedSun;
    private boolean mHasDown;
    private int mRadius;
    private RectF mRectF;

    public SunriseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunriseView(Context context) {
        this(context, null);
    }

    public SunriseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isNeedSun) {
            //画曲线
            canvas.save();
            canvas.clipRect(mStartPointX, 0, mEndPointX, mStartPointY, Region.Op.INTERSECT);
            canvas.clipRect(mMovePointX - mBitmapW / 2, mMovePointY - mBitmapH / 2, mMovePointX + mBitmapW / 2, mMovePointY + mBitmapH / 2, Region.Op.DIFFERENCE);
            canvas.drawArc(mRectF, 200, 140, true, mPathPaint);
            //画透明背景用圆的角度来控制
            canvas.clipRect(mStartPointX, 0, mMovePointX, mStartPointY, Region.Op.INTERSECT);
            canvas.drawArc(mRectF, 200, 140, true, mAnmationPaint);
            canvas.restore();

            //画小太阳
            canvas.drawBitmap(mSunBitmap, mMovePointX - mBitmapW, mMovePointY - mBitmapH, null);

        } else if (mNotUp || mHasDown) {
            //画曲线
            canvas.save();
            canvas.clipRect(mMovePointX - mBitmapW / 2, mMovePointY - mBitmapH / 2, mMovePointX + mBitmapW / 2, mMovePointY + mBitmapH / 2, Region.Op.DIFFERENCE);
            canvas.clipRect(0, 0, getWidth(), mStartPointY, Region.Op.INTERSECT);
            canvas.drawCircle(mCirclePointX, mCirclePointY, mRadius, mPathPaint);
            canvas.restore();

            if (mNotUp) {
                //画小太阳
                canvas.drawBitmap(mSunBitmap, mStartPointX - mBitmapW, mStartPointY - mBitmapH, null);
            } else {
                canvas.drawBitmap(mSunBitmap, mEndPointX - mBitmapW, mEndPointY - mBitmapH, null);
            }
        } else {
            //这里不绘制小太阳，只有曲线
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), mStartPointY, Region.Op.INTERSECT);
            canvas.drawCircle(mCirclePointX, mCirclePointY, mRadius, mPathPaint);
            canvas.restore();
        }
    }

    public void sunAnim(final float a) {
        if (a == 0) {
            mSunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daily_deatil_sun_up);
            mBitmapW = mSunBitmap.getWidth() / 2;
            mBitmapH = mSunBitmap.getHeight();
            mNotUp = true;
            isNeedSun = false;
            mHasDown = false;
            invalidate();
        } else if (a == 1) {
            mSunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daily_deatil_sun_up);
            mBitmapW = mSunBitmap.getWidth() / 2;
            mBitmapH = mSunBitmap.getHeight();
            mNotUp = false;
            isNeedSun = false;
            mHasDown = true;
            mMovePointX = mEndPointX;
            mMovePointY = mEndPointY;
            invalidate();
        } else {
            mSunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daily_deatil_sun);
            mBitmapW = mSunBitmap.getWidth() / 2;
            mBitmapH = mSunBitmap.getHeight() / 2;
            mNotUp = false;
            isNeedSun = true;
            mHasDown = false;
            ValueAnimator progressAnimator = ValueAnimator.ofFloat(210, 330);
            progressAnimator.setDuration(3000);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**每次在初始值和结束值之间产生的一个平滑过渡的值，逐步去更新进度*/
                    float x = (float) animation.getAnimatedValue();
                    if ((x - 210) <= (120 * a)) {
                        mMovePointX = mCirclePointX + (int) (mRadius * (Math.cos(x * 3.14 / 180)));
                        mMovePointY = mCirclePointY + (int) (mRadius * (Math.sin(x * 3.14 / 180)));
                        invalidate();
                    } else {
                        return;
                    }
                }
            });
            progressAnimator.setInterpolator(new LinearInterpolator());
            progressAnimator.start();
        }
    }

    private void initData() {
        //曲线初始化
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(2);
        mPathPaint.setColor(Color.parseColor("#ffffff"));
        PathEffect effects = new DashPathEffect(new float[]{10, 10, 10, 10}, 0);
        mPathPaint.setPathEffect(effects);

        //日出动画阴影部分初始化
        mAnmationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnmationPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mAnmationPaint.setColor(Color.parseColor("#32ffffff"));

        //开始坐标的XY
        mStartPointX = dp2px(9);
        mStartPointY = dp2px(80);

        //结束坐标的XY
        mEndPointX = dp2px(139);
        mEndPointY = mStartPointY;

        //太阳的移动坐标的XY
        mMovePointX = mStartPointX;
        mMovePointY = mStartPointY;

        //圆的半径
        mRadius = dp2px(74);

        //圆心坐标
        mCirclePointX = dp2px(74);
        mCirclePointY = dp2px(115);

        //圆的初始化
        mRectF = new RectF(mCirclePointX - mRadius, mCirclePointY - mRadius, mCirclePointX + mRadius, mCirclePointY + mRadius);

    }

    /**
     * dp转px
     */
    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }
}
