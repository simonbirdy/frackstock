package com.vos.myapplication;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.vos.myapplication.BezierSplineUtil;

/**
 *
 * The ColorWheel displays a HSV colorwheel to select a color.
 *
 * It supports 2 different modes:
 * - select one color
 * - select two colors
 *
 *
 * Copyright 2013 Piotr Adamus
 *
 * extended 2016 by Simon Vogel
 *
 * @author Beat Käfer, Simon Vogel
 */
public class ColorWheel extends View {
    /**
     * Customizable display parameters (in percents)
     */
    private final int paramOuterPadding = 2; // outer padding of the whole color picker view
    private final int paramInnerPadding = 5; // distance between value slider wheel and inner color wheel
    private final int paramValueSliderWidth = 10; // width of the value slider
    private final int paramArrowPointerSize = 4; // size of the arrow pointer; set to 0 to hide the pointer

    private final float valueSliderSize = 90;
    private final float valueSliderMargin = 22;


    private int dirMode;

    public final static int DIR_MODE_CW = 0;
    public final static int DIR_MODE_CCW = 1;



    private int contMode;

    public final static int CONT_MODE_SINGLE = 0;
    public final static int CONT_MODE_CONTINOUS = 1;



    private int mode;

    public final static int MODE_ONE_COLOR = 0;
    public final static int MODE_TWO_COLOR = 1;

    private Paint colorWheelPaint;
    private Paint valueSliderPaint;

    private Paint colorViewPaint;

    private Paint colorPointerPaint;
    private RectF colorPointerCoords;

    private Paint valuePointerPaint;
    private Paint valuePointerArrowPaint;

    private RectF outerWheelRect;
    private RectF innerWheelRect;

    private Path colorViewPath;
    private Path colorViewPath1;
    private Path colorViewPath2;
    private Path valueSliderPath;
    private Path arrowPointerPath;
    private Path connectPathCW;
    private Path connectPathCCW;
    private Path circlePath;

    private Bitmap colorWheelBitmap;

    private int valueSliderWidth;
    private int innerPadding;
    private int outerPadding;

    private int arrowPointerSize;
    private int outerWheelRadius;
    private int innerWheelRadius;
    private int colorWheelRadius;


    private int currentControl;
    private final int VALUE_CONTROL = 1;
    private final int COLOR_CONTROL = 2;
    private final int NO_CONTROL = 0;
    private final int SELECT_COL_1_CONTROL =3;
    private final int SELECT_COL_2_CONTROL = 4;

    private int width;
    private int height;

    public void setListener(ColorWheelListener listener) {
        this.listener = listener;
    }

    private ColorWheelListener listener;

    private Matrix gradientRotationMatrix;

    /** Currently selected color */
    private int currentColor;
    private float[][] colorsHSV = new float[2][3];
    private int[][] colorPoints = new  int[2][2];


    public ColorWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ColorWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorWheel(Context context) {
        super(context);
        init();
    }

    private void init() {

        currentControl = NO_CONTROL;
        currentColor = 0;
        mode = MODE_ONE_COLOR;

        colorsHSV[0][0] = 0;
        colorsHSV[0][1] = 0;
        colorsHSV[0][2] = 0;

        colorsHSV[1][0] = 0;
        colorsHSV[1][1] = 0;
        colorsHSV[1][2] = 0;

        colorPointerPaint = new Paint();
        colorPointerPaint.setStyle(Paint.Style.STROKE);
        colorPointerPaint.setStrokeWidth(5f);
        colorPointerPaint.setColor(Color.WHITE);

        valuePointerPaint = new Paint();
        valuePointerPaint.setStyle(Paint.Style.STROKE);
        valuePointerPaint.setStrokeWidth(2f);

        valuePointerArrowPaint = new Paint();

        colorWheelPaint = new Paint();
        colorWheelPaint.setAntiAlias(true);
        colorWheelPaint.setDither(true);

        valueSliderPaint = new Paint();
        valueSliderPaint.setAntiAlias(true);
        valueSliderPaint.setDither(true);

        colorViewPaint = new Paint();
        colorViewPaint.setAntiAlias(true);

        colorViewPath = new Path();
        colorViewPath1 = new Path();
        colorViewPath2 = new Path();
        valueSliderPath = new Path();
        arrowPointerPath = new Path();
        connectPathCW = new Path();
        connectPathCCW = new Path();

        outerWheelRect = new RectF();
        innerWheelRect = new RectF();

        colorPointerCoords = new RectF();
        calculateColorPoints();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float[] colorHSV;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);
        calculateColorPoints();

        // drawing color view
        if(mode == MODE_ONE_COLOR) {

            if(contMode == CONT_MODE_CONTINOUS){
                redrawCirclePath();
                Paint tempPaint = new Paint();
                tempPaint.setStyle(Paint.Style.STROKE);
                tempPaint.setColor(Color.WHITE);
                tempPaint.setStrokeWidth(10);
                tempPaint.setAntiAlias(true);
                canvas.drawPath(circlePath,tempPaint);
            }

            colorHSV = colorsHSV[0];
            colorViewPaint.setStyle(Paint.Style.FILL);
            colorViewPaint.setColor(Color.HSVToColor(colorHSV));
            canvas.drawPath(colorViewPath, colorViewPaint);
        }
        else{
            colorHSV = colorsHSV[0];
            colorViewPaint.setStyle(Paint.Style.FILL);
            colorViewPaint.setColor(Color.HSVToColor(colorHSV));
            canvas.drawPath(colorViewPath1, colorViewPaint);

            colorHSV = colorsHSV[1];
            colorViewPaint.setStyle(Paint.Style.FILL);
            colorViewPaint.setColor(Color.HSVToColor(colorHSV));
            canvas.drawPath(colorViewPath2, colorViewPaint);

            if(currentColor == 0){
                colorViewPaint.setStyle(Paint.Style.STROKE);
                colorViewPaint.setColor(Color.WHITE);
                colorViewPaint.setStrokeWidth(10);
                canvas.drawPath(colorViewPath1, colorViewPaint);
            }
            else{
                colorViewPaint.setStyle(Paint.Style.STROKE);
                colorViewPaint.setColor(Color.WHITE);
                colorViewPaint.setStrokeWidth(10);
                canvas.drawPath(colorViewPath2, colorViewPaint);
            }

            Paint tempPaint = new Paint();
            tempPaint.setStyle(Paint.Style.STROKE);
            tempPaint.setColor(Color.WHITE);
            tempPaint.setStrokeWidth(10);
            tempPaint.setAntiAlias(true);

            redrawConnectionPath();
            if(dirMode == DIR_MODE_CW){
                canvas.drawPath(connectPathCW, tempPaint);
            }
            else{
                canvas.drawPath(connectPathCCW, tempPaint);
            }

        }

        // drawing value slider
        colorHSV = colorsHSV[currentColor];
        float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };

        SweepGradient sweepGradient = new SweepGradient(centerX, centerY, new int[] { Color.BLACK, Color.HSVToColor(hsv), Color.HSVToColor(hsv), Color.BLACK}, new float[] {0,  valueSliderSize/360 , valueSliderSize/360+(360-valueSliderSize)/2/360,valueSliderSize/360+(360-valueSliderSize)/360 });
        sweepGradient.setLocalMatrix(gradientRotationMatrix);
        valueSliderPaint.setShader(sweepGradient);

        canvas.drawPath(valueSliderPath, valueSliderPaint);

        // drawing color wheel pointer


        if(mode == MODE_ONE_COLOR) {
            colorHSV = colorsHSV[0];
            float hueAngle = (float) Math.toRadians(colorHSV[0]);
            int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
            int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

            Paint tempPaint = new Paint();
            tempPaint.setColor(Color.WHITE);
            tempPaint.setAntiAlias(true);
            canvas.drawCircle((float) colorPointX, (float) colorPointY, 30, tempPaint);
            tempPaint.setColor(Color.HSVToColor(new float[]{colorHSV[0], colorHSV[1], 1f}));
            canvas.drawCircle((float) colorPointX, (float) colorPointY, 20, tempPaint);
        }
        else{
            colorHSV = colorsHSV[0];

            Paint tempPaint = new Paint();
            tempPaint.setColor(Color.WHITE);
            tempPaint.setAntiAlias(true);
            canvas.drawCircle((float) colorPoints[0][0], (float) colorPoints[0][1], 30, tempPaint);
            tempPaint.setColor(Color.HSVToColor(new float[]{colorHSV[0], colorHSV[1], 1f}));
            canvas.drawCircle((float) colorPoints[0][0], (float) colorPoints[0][1], 20, tempPaint);

            colorHSV = colorsHSV[1];


            tempPaint = new Paint();
            tempPaint.setColor(Color.WHITE);
            tempPaint.setAntiAlias(true);
            canvas.drawCircle((float) colorPoints[1][0], (float) colorPoints[1][1], 30, tempPaint);
            tempPaint.setColor(Color.HSVToColor(new float[]{colorHSV[0], colorHSV[1], 1f}));
            canvas.drawCircle((float) colorPoints[1][0], (float) colorPoints[1][1], 20, tempPaint);

        }
        // drawing value pointer

        colorHSV = colorsHSV[currentColor];
        valuePointerPaint.setColor(Color.HSVToColor(new float[] { 0f, 0f, 1f - colorHSV[2] }));
        valuePointerPaint.setStrokeWidth(7);

        double valueAngle = ((0.0f + (180-valueSliderSize)/2) +(colorHSV[2]) *  valueSliderSize) * Math.PI / 180;
        float valueAngleX = (float) Math.cos(valueAngle);
        float valueAngleY = (float) Math.sin(valueAngle);

        canvas.drawLine(valueAngleX * innerWheelRadius + centerX, valueAngleY * innerWheelRadius + centerY, valueAngleX * outerWheelRadius + centerX,
                valueAngleY * outerWheelRadius + centerY, valuePointerPaint);

        // drawing pointer arrow

        if (arrowPointerSize > 0) {
            drawPointerArrow(canvas);
        }

    }

    /**
     *
     * Draws the Pointer (circle) on the value slider
     * @param canvas
     */
    private void drawPointerArrow(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        float[] colorHSV = colorsHSV[currentColor];
        double tipAngle = ((0.0f + (180-valueSliderSize)/2) +(colorHSV[2]) *  valueSliderSize) * Math.PI / 180;
        double tipAngleX = Math.cos(tipAngle) * (outerWheelRadius+15);
        double tipAngleY = Math.sin(tipAngle) * (outerWheelRadius+15);
        Paint tempPaint = new Paint();
        tempPaint.setColor(Color.WHITE);
        tempPaint.setAntiAlias(true);
        canvas.drawCircle((float)tipAngleX+centerX, (float)tipAngleY+centerY, 30, tempPaint);
        tempPaint.setColor(Color.HSVToColor(colorHSV));
        canvas.drawCircle((float)tipAngleX+centerX, (float)tipAngleY+centerY, 20, tempPaint);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        if(width <= 0 || height <= 0){
            return;
        }

        this.width = width;
        this.height = height;

        int centerX = width / 2;
        int centerY = height / 2;


        innerPadding = (int) (paramInnerPadding * width / 100);
        outerPadding = (int) (paramOuterPadding * width / 100);
        arrowPointerSize = (int) (paramArrowPointerSize * width / 100);
        valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

        outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
        innerWheelRadius = outerWheelRadius - valueSliderWidth;
        colorWheelRadius = innerWheelRadius - innerPadding;

        outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
        innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);

        gradientRotationMatrix = new Matrix();
        gradientRotationMatrix.preRotate(0+(180-valueSliderSize)/2, width / 2, height / 2);


        //ColorView Path
        colorViewPath = new Path();
        RectF tempRect = generateArcRect((180-valueSliderSize)/2-valueSliderMargin,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath.arcTo(tempRect,180+(180-valueSliderSize)/2-valueSliderMargin,-180);
        colorViewPath.arcTo(outerWheelRect, 0+(180-valueSliderSize)/2-valueSliderMargin,  -(360-valueSliderSize-2*valueSliderMargin));
        tempRect = generateArcRect(180-(180-valueSliderSize)/2+valueSliderMargin,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath.arcTo(tempRect,180-(180-valueSliderSize)/2+valueSliderMargin,-180);
        colorViewPath.arcTo(innerWheelRect, 180-(180-valueSliderSize)/2+valueSliderMargin, (360-valueSliderSize-2*valueSliderMargin));

        //ColorView Path1
        colorViewPath1 = new Path();
        tempRect = generateArcRect(270-valueSliderMargin/2,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath1.arcTo(tempRect,270-valueSliderMargin/2,180);
        colorViewPath1.arcTo(innerWheelRect, 270-valueSliderMargin/2,-(360-valueSliderSize-3*valueSliderMargin)/2);
        tempRect = generateArcRect(180-(180-valueSliderSize)/2+valueSliderMargin,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath1.arcTo(tempRect,-(180-valueSliderSize)/2+valueSliderMargin,180);
        colorViewPath1.arcTo(outerWheelRect, 180-(180-valueSliderSize)/2+valueSliderMargin, (360-valueSliderSize-3*valueSliderMargin)/2);

        //ColorView Path2
        colorViewPath2 = new Path();
        tempRect = generateArcRect(270+valueSliderMargin/2,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath2.arcTo(tempRect,270+valueSliderMargin/2,-180);
        colorViewPath2.arcTo(innerWheelRect, 270+valueSliderMargin/2,(360-valueSliderSize-3*valueSliderMargin)/2);
        tempRect = generateArcRect((180-valueSliderSize)/2-valueSliderMargin,centerX,centerY,innerWheelRadius,valueSliderWidth);
        colorViewPath2.arcTo(tempRect,180+(180-valueSliderSize)/2-valueSliderMargin,-180);
        colorViewPath2.arcTo(outerWheelRect, (180-valueSliderSize)/2-valueSliderMargin, -(360-valueSliderSize-3*valueSliderMargin)/2);

        //ValueSliderPath
        valueSliderPath = new Path();

        tempRect = generateArcRect((180-valueSliderSize)/2,centerX,centerY,innerWheelRadius,valueSliderWidth);
        valueSliderPath.arcTo(tempRect,180+(180-valueSliderSize)/2,180);
        valueSliderPath.arcTo(outerWheelRect, 0+(180-valueSliderSize)/2, valueSliderSize);
        tempRect = generateArcRect(180-(180-valueSliderSize)/2,centerX,centerY,innerWheelRadius,valueSliderWidth);
        valueSliderPath.arcTo(tempRect,180-(180-valueSliderSize)/2,180);
        valueSliderPath.arcTo(innerWheelRect, 180-(180-valueSliderSize)/2, -valueSliderSize);

    }

    /**
     * generates a Rect to draw an arc at the selected location in relation to the color wheel
     * @param angle
     * @param centerX
     * @param centerY
     * @param radius
     * @param sliderWidth
     * @return
     */
    private RectF generateArcRect(float angle, int centerX, int centerY, int radius, int sliderWidth){
        RectF tempRect = new RectF();
        int tempcenterY = centerX + (int) (((float)radius+(float)sliderWidth/2.0)*Math.sin((angle)/180*Math.PI));
        int tempcenterX = centerY + (int) (((float)radius+(float)sliderWidth/2.0)*Math.cos((angle)/180*Math.PI));
        tempRect.set(tempcenterX-(float)sliderWidth/2,tempcenterY-(float)sliderWidth/2,tempcenterX+(float)sliderWidth/2,tempcenterY+(float)sliderWidth/2);

        return tempRect;
    }

    /**
     * generate ColorWheel bitmap
     * @param width
     * @param height
     * @return
     */
    private Bitmap createColorWheelBitmap(int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int colorCount = 12;
        int colorAngleStep = 360 / 12;
        int colors[] = new int[colorCount + 1];
        float hsv[] = new float[] { 0f, 1f, 1f };
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = (i * colorAngleStep + 180) % 360;
            colors[i] = Color.HSVToColor(hsv);
        }
        colors[colorCount] = colors[0];

        SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
        RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);

        colorWheelPaint.setShader(composeShader);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);

        return bitmap;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if(listener != null) {
                    listener.onTouchEnded();
                }
                currentControl = NO_CONTROL;
                break;

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();
                int cx = x - getWidth() / 2;
                int cy = y - getHeight() / 2;
                double d = Math.sqrt(cx * cx + cy * cy);

                double angle = (Math.atan2(cy, cx) * 180 / Math.PI);

                if(currentControl == NO_CONTROL){
                    if(d <= outerWheelRadius * 1.1) {
                        if (d <= colorWheelRadius * 1.1) {
                            if(x > colorPoints[0][0]-60 &&  x < colorPoints[0][0]+60 && y > colorPoints[0][1]-60 && y < colorPoints[0][1]+60){
                                currentControl = COLOR_CONTROL;
                                currentColor = 0;
                                if (listener != null) {
                                    listener.onTouchStarted();
                                }
                            } else if(x > colorPoints[1][0]-60 &&  x < colorPoints[1][0]+60 && y > colorPoints[1][1]-60 && y < colorPoints[1][1]+60){
                                currentControl = COLOR_CONTROL;
                                currentColor = 1;
                                if (listener != null) {
                                    listener.onTouchStarted();
                                }
                            }
                        } else if (d >= innerWheelRadius && angle > (180 - valueSliderSize) / 2 && angle < 180-(180 - valueSliderSize) / 2) {
                            currentControl = VALUE_CONTROL;
                            if (listener != null) {
                                listener.onTouchStarted();
                            }
                        }else if(mode == MODE_TWO_COLOR){
                            if(d >= innerWheelRadius && (angle > (180-(180-valueSliderSize)/2+valueSliderMargin) || angle < -90-valueSliderMargin/2)){
                                currentControl = SELECT_COL_1_CONTROL;
                                currentColor = 0;
                                invalidate();
                            }
                            else if((d >= innerWheelRadius && (angle > -90+valueSliderMargin/2  || angle < (180-valueSliderSize)/2-valueSliderMargin))){
                                currentControl = SELECT_COL_2_CONTROL;
                                currentColor = 1;
                                invalidate();
                            }
                        }
                    }
                }

                switch(currentControl){
                    case COLOR_CONTROL:

                        colorsHSV[currentColor][0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
                        colorsHSV[currentColor][1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));
                        calculateColorPoints();
                        if(mode==MODE_TWO_COLOR) {
                            redrawConnectionPath();
                        }
                        invalidate();
                        break;
                    case VALUE_CONTROL:
                        colorsHSV[currentColor][2] = (float) Math.max(0, Math.min(1, ((Math.atan2(cy, cx) * 180 / Math.PI) - (180 - valueSliderSize) / 2) / valueSliderSize));
                        invalidate();
                        break;
                    default:
                }

                if(currentControl != NO_CONTROL) {
                    if (listener != null){
                        listener.valueChanged(colorsHSV,currentColor);
                    }
                }


                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * calculates the Path in the colorwheel between both selected colors
     */
    public void redrawConnectionPath(){
        connectPathCW = hsvPath(colorsHSV[0], colorsHSV[1]);
        connectPathCCW = hsvPath(colorsHSV[1], colorsHSV[0]);
    }

    /**
     * calculates the Path for the circle animation
     */
    private void redrawCirclePath(){
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        circlePath = new Path();
        circlePath.addCircle(centerX,centerY,colorWheelRadius*colorsHSV[0][1], Path.Direction.CW);
    }

    /**
     * Calculates the location of the color points
     */
    public void calculateColorPoints(){
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        float hueAngle = (float) Math.toRadians(colorsHSV[0][0]);
        int colorPointX = (int) (-Math.cos(hueAngle) * colorsHSV[0][1] * colorWheelRadius) + centerX;
        int colorPointY = (int) (-Math.sin(hueAngle) * colorsHSV[0][1] * colorWheelRadius) + centerY;

        colorPoints[0][0] = colorPointX;
        colorPoints[0][1] = colorPointY;

        hueAngle = (float) Math.toRadians(colorsHSV[1][0]);
        colorPointX = (int) (-Math.cos(hueAngle) * colorsHSV[1][1] * colorWheelRadius) + centerX;
        colorPointY = (int) (-Math.sin(hueAngle) * colorsHSV[1][1] * colorWheelRadius) + centerY;

        colorPoints[1][0] = colorPointX;
        colorPoints[1][1] = colorPointY;
    }

    /**
     * generate a path between two colors in the colorwheel
     * @param start
     * @param end
     * @return
     */
    Path hsvPath(float[] start, float[] end){
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        BezierSplineUtil.Point[] points = new BezierSplineUtil.Point[11];

        float hueDiff = end[0] - start[0];
        if(hueDiff <0){
            hueDiff = 360+hueDiff;
        }
        float satDiff = end[1] - start[1];

        for(int i = 0;i<11; i++){
            float hueAngle = (float) Math.toRadians(start[0]+hueDiff/10.0*i);
            int colorPointX = (int) (-Math.cos(hueAngle) * (start[1]+satDiff/10.0*i) * colorWheelRadius) + centerX;
            int colorPointY = (int) (-Math.sin(hueAngle) * (start[1]+satDiff/10.0*i) * colorWheelRadius) + centerY;
            points[i] = new BezierSplineUtil.Point(colorPointX,colorPointY);
        }

        BezierSplineUtil.Point[][] controlPoints = BezierSplineUtil.getCurveControlPoints(points);
        BezierSplineUtil.Point[] firstCP = controlPoints[0];
        BezierSplineUtil.Point[] secondCP = controlPoints[1];

        Path p = new Path();
        p.moveTo(points[0].x, points[0].y);

        for (int i = 0; i < firstCP.length; i++) {
            p.cubicTo(firstCP[i].x, firstCP[i].y,
                    secondCP[i].x, secondCP[i].y,
                    points[i + 1].x, points[i+1].y);
        }

        return p;

    }


    public int getDirMode() {
        return dirMode;
    }

    public void setDirMode(int dirMode) {
        this.dirMode = dirMode;
        invalidate();
    }

    public int getContMode() {
        return contMode;
    }

    public void setContMode(int contMode) {
        this.contMode = contMode;
        invalidate();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.onSizeChanged(width,height,width,height);
        invalidate();
    }

    /**
     * Set the selected colors of the ColorWheel
     * @param colors
     */
    public void setColor(float[][] colors) {
        colorsHSV = colors;
        invalidate();
    }

    /**
     * get the selected Colors
     * @return
     */
    public float[][] getColor(){
        return colorsHSV;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putFloatArray("color1", colorsHSV[0]);
        state.putFloatArray("color2",colorsHSV[1]);
        state.putParcelable("super", super.onSaveInstanceState());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            colorsHSV = new float[2][3];
            colorsHSV[0] = bundle.getFloatArray("color1");
            colorsHSV[1] = bundle.getFloatArray("color2");
            super.onRestoreInstanceState(bundle.getParcelable("super"));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * Listener for the ColorWheel
     */
    public interface ColorWheelListener{
        /**
         * Called when the color changes
         * @param colorValues
         * @param currentColor index of currently selected color
         */
        public void valueChanged(float colorValues[][], int currentColor);

        /**
         * Called when the touch started (DOWN)
         */
        public void onTouchStarted();

        /**
         * Called when the touch ended (UP)
         */
        public void onTouchEnded();
    }
}
