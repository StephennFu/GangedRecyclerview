package com.fatchao.gangedrecyclerview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;


public class AnimView extends AppCompatImageView {


    private final static String TAG = "AnimView";

    boolean isBezierBackDone = false;

    private int mWidth;
    private int mHeight;
    private int PULL_WIDTH;
    private int PULL_DELTA;

    private long mStart;
    private long mStop;
    private int mBezierDeta;

    private long bezierBackDur;

    private Paint mBackPaint;
    private Path mPath;

    private AnimatorStatus mAniStatus = AnimatorStatus.PULL_LEFT;

    enum AnimatorStatus{
        PULL_LEFT,
        DRAG_LEFT,
        RELEASE,
    }

    public AnimView(Context context) {
        this(context, null, 0);
    }

    public AnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        PULL_WIDTH = (int) TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
        PULL_DELTA = (int) TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources().getDisplayMetrics());

        mPath = new Path();
        mBackPaint = new Paint();
        //平滑化(anti-alias)
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 测量长宽
     * @param widthMeasureSpec fill_parent , wrap_content
     * @param heightMeasureSpec fill_parent , wrap_content
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: widthMeasureSpec = " + widthMeasureSpec + ", heightMeasureSpec = " + heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Log.d(TAG, "onMeasure: width = " + width);
        if (width > PULL_DELTA + PULL_WIDTH) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(PULL_DELTA + PULL_WIDTH, MeasureSpec.getMode(widthMeasureSpec));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //鉴定是pull（<PULL_WIDTH） 还是 drag (>=PULL_WIDTH)
        Log.d(TAG, "onLayout: changed = " + changed);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();

            Log.d(TAG, "onLayout: mWidth = " + mWidth);
            Log.d(TAG, "onLayout: mHeight = " + mHeight);

            if (mWidth < PULL_WIDTH) {
                mAniStatus = AnimatorStatus.PULL_LEFT;
            }

            switch (mAniStatus) {
                case PULL_LEFT:
                    if (mWidth >= PULL_WIDTH) {
                        mAniStatus = AnimatorStatus.DRAG_LEFT;
                    }
                    break;
            }
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: into onDraw");
        super.onDraw(canvas);
        switch (mAniStatus){
            case PULL_LEFT:
                Log.d(TAG, "onDraw: mWidth = " + mWidth + ", mHeight = " + mHeight);
                canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
                break;
            case DRAG_LEFT:
                drawDrag(canvas);
                break;
            case RELEASE:
                drawBack(canvas, getBezierDelta());
                break;
        }
    }

    private void drawDrag(Canvas canvas) {

        Log.d(TAG, "drawDrag: mWidth = " + mWidth);
        Log.d(TAG, "drawDrag: PULL_WIDTH = " + PULL_WIDTH);
        canvas.drawRect(mWidth - PULL_WIDTH, 0, mWidth, mHeight, mBackPaint);

//        mPath.reset();
//        mPath.moveTo(mWidth - PULL_WIDTH, 0);
//        mPath.quadTo(0, mHeight / 2, mWidth - PULL_WIDTH, mHeight);
//        canvas.drawPath(mPath, mBackPaint);

        mPath.reset();
        mPath.moveTo(mWidth - PULL_WIDTH, 0);
        mPath.rLineTo(10, 0);
        canvas.drawPath(mPath, mBackPaint);
    }

    private void drawBack(Canvas canvas, int delta){
        mPath.reset();
        mPath.moveTo(mWidth, 0);
        mPath.lineTo(mWidth - PULL_WIDTH, 0);
        mPath.quadTo(delta, mHeight / 2,  mWidth - PULL_WIDTH, mHeight);
        mPath.lineTo(mWidth, mHeight);
        canvas.drawPath(mPath, mBackPaint);

        invalidate();

        if(bezierBackRatio == 1){
            isBezierBackDone = true;
        }

        if(isBezierBackDone && mWidth <= PULL_WIDTH){
            drawFooterBack(canvas);
        }
    }

    private void drawFooterBack(Canvas canvas){
        Log.d(TAG, "drawFooterBack: ");
        canvas.drawRect(0, 0, mWidth, mHeight, mBackPaint);
    }

    public void releaseDrag(){
        Log.d(TAG, "releaseDrag: into releaseDrag");
        mAniStatus = AnimatorStatus.RELEASE;
        mStart = System.currentTimeMillis();
        mStop = mStart + bezierBackDur;
        mBezierDeta = mWidth - PULL_WIDTH;
        isBezierBackDone = false;
        requestLayout();
    }

    public void setBezierBackDur(long bezierBackDur) {
        this.bezierBackDur = bezierBackDur;
    }

    private float bezierBackRatio;

    private int getBezierDelta() {
        bezierBackRatio = getBezierBackRatio();
        return (int) (mBezierDeta * bezierBackRatio);
    }

    private float getBezierBackRatio() {
        if (System.currentTimeMillis() >= mStop) {
            return 1;
        }
        float ratio = (System.currentTimeMillis() - mStart) / (float) bezierBackDur;
        return Math.min(1, ratio);
    }

    public void setBgColor(int color){
        mBackPaint.setColor(color);
    }
}
