package com.thexfactorlabs.dots;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Harry Anuszewski on 8/4/2017.
 * Copyright TheXFactorLabs
 */

public class Draw extends View {
    private static final String TAG = "CirclesDrawingView";
    boolean flag = false;
    int screenX;
    int screenY;
    Context appContext;

    /* Main bitmap */
    //private Bitmap mBitmap = null;

    //private Rect mMeasuredRect;

    /** Stores data about single circle */
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;
        int direction = 0; //0 left, 1 right, 2 up, 3 down
        int speed = 8;
        Context mContext;

        private Paint mCirclePaint;
        Paint strokePaint;

        CircleArea(int centerX, int centerY, int radius, Context context) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;

            mContext = context;

            Random r = new Random();
            direction = r.nextInt(4);

            setupPaint();
        }

        private void setupPaint(){
            mCirclePaint = new Paint();
            mCirclePaint.setColor(getMatColor("500"));
            mCirclePaint.setStrokeWidth(40);
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setMaskFilter(new BlurMaskFilter(150, BlurMaskFilter.Blur.INNER));

            strokePaint = new Paint();
            strokePaint.setColor(getMatColor("500"));
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(3);
        }

        public Paint getCirclePaint(){
            return  mCirclePaint;
        }

        public Paint getStrokePaint(){
            return  strokePaint;
        }

        public int getDirection(){
            return direction;
        }

        public int getSpeed(){
            return speed;
        }

        private int getMatColor(String typeColor)
        {
            int returnColor = Color.BLACK;
            int arrayId = mContext.getResources().getIdentifier("mdcolor_" + typeColor, "array", mContext.getApplicationContext().getPackageName());

            if (arrayId != 0)
            {
                TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
                int index = (int) (Math.random() * colors.length());
                returnColor = colors.getColor(index, Color.BLACK);
                colors.recycle();
            }
            return returnColor;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    /** Paint to draw circles */
    private Paint mCirclePaint;
    Paint strokePaint;

    private static final int CIRCLES_LIMIT = 10;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public Draw(final Context ct) {
        super(ct);
        init(ct);
    }

    private void init(final Context ct) {
        // Generate bitmap used for background
        //mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.abc_ic_menu_cut_mtrl_alpha);
        appContext = ct;

//        mCirclePaint = new Paint();
//        mCirclePaint.setColor(Color.argb(100,50,205,50));
//        mCirclePaint.setStrokeWidth(40);
//        mCirclePaint.setStyle(Paint.Style.FILL);
//        mCirclePaint.setMaskFilter(new BlurMaskFilter(150, BlurMaskFilter.Blur.INNER));
//
//        strokePaint = new Paint();
//        strokePaint.setColor(Color.argb(100,0,100,0));
//        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setStrokeWidth(3);

        Display display = ((Activity)ct).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenX = size.x;
        screenY = size.y;
    }

    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
        //canv.drawBitmap(mBitmap, null, mMeasuredRect, null);
        invalidate();
        for (CircleArea circle : mCircles) {
            if(circle.getDirection() == 0) {
                if(circle.centerX + circle.radius < 0){
                    circle.centerX = screenX;
                }else{
                    circle.centerX = circle.centerX - circle.getSpeed();
                }
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getCirclePaint());
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getStrokePaint());
            }else if(circle.getDirection() == 1){
                if(circle.centerX - circle.radius > screenX){
                    circle.centerX = 0;
                }else{
                    circle.centerX= circle.centerX + circle.getSpeed();
                }
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getCirclePaint());
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getStrokePaint());
            }else if(circle.getDirection() == 2){
                if(circle.centerY - circle.radius > screenY){
                    circle.centerY = 0;
                }else{
                    circle.centerY = circle.centerY + circle.getSpeed();
                }
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getCirclePaint());
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getStrokePaint());
            }else{ //3
                if(circle.centerY + circle.radius < 0){
                    circle.centerY = screenY;
                }else{
                    circle.centerY = circle.centerY - circle.getSpeed();
                }
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getCirclePaint());
                canv.drawCircle(circle.centerX, circle.centerY, circle.radius, circle.getStrokePaint());
            }
        }
    }

    @Override
    public boolean onTouchEvent(@Nullable final MotionEvent event) {
        boolean handled = false;
        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
//                final int pointerCount = event.getPointerCount();
//
//                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
//                    // Some pointer has moved, search it by pointer id
//                    pointerId = event.getPointerId(actionIndex);
//
//                    xTouch = (int) event.getX(actionIndex);
//                    yTouch = (int) event.getY(actionIndex);
//
//                    touchedCircle = mCirclePointer.get(pointerId);
//
//                    if (null != touchedCircle) {
//                        touchedCircle.centerX = xTouch;
//                        touchedCircle.centerY = yTouch;
//                    }
//                }
//                invalidate();
//                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, 40, appContext/*mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT*/);

            if (mCircles.size() == CIRCLES_LIMIT) {
            }
            //if (flag == false)
             else{
                mCircles.add(touchedCircle);
                flag = true;
            }

        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }

        return touched;
    }
}
