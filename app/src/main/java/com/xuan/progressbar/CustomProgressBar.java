package com.xuan.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;

/**
 * com.xuan.progressbar
 *
 * @author by xuan on 2018/5/18
 * @version [版本号, 2018/5/18]
 * @update by xuan on 2018/5/18
 * @descript
 */
public class CustomProgressBar extends View{
    private int innerBgColor= ContextCompat.getColor(getContext(),R.color.colorAccent);
    private int outerBgColor= ContextCompat.getColor(getContext(),R.color.colorPrimary);
    private int fontColor= ContextCompat.getColor(getContext(),R.color.colorAccent);

    private int fontSize= 15;
    private int borderWidth= 5;
    private String progressMiddleText;

    private Paint innerPaint;
    private Paint outerPaint;
    private Paint textPaint;

    private float progressValue=40;
    private float progressValueMax=100;

    public CustomProgressBar(Context context) {
        this(context,null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context,attrs);

        innerPaint=new Paint();
        initArcPaint(innerPaint,innerBgColor);
        outerPaint=new Paint();
        initArcPaint(outerPaint,outerBgColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(fontColor);
        textPaint.setTextSize(fontSize);
    }

    private void initArcPaint(Paint paint,int bgColor) {
        paint.setAntiAlias(true);
        paint.setColor(bgColor);
        // 防抖动

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomProgressBar);

        innerBgColor=typedArray.getColor(R.styleable.CustomProgressBar_innerBgColor,innerBgColor);
        outerBgColor=typedArray.getColor(R.styleable.CustomProgressBar_outerBgColor,outerBgColor);
        fontColor=typedArray.getColor(R.styleable.CustomProgressBar_fontColor,fontColor);

        fontSize=typedArray.getDimensionPixelSize(R.styleable.CustomProgressBar_fontSize,fontSize);
        borderWidth=typedArray.getDimensionPixelSize(R.styleable.CustomProgressBar_borderWidth,dipToPx(borderWidth));
        progressMiddleText=typedArray.getString(R.styleable.CustomProgressBar_progressMiddleText);

        progressValue=typedArray.getFloat(R.styleable.CustomProgressBar_progressValue,progressValue);
        progressValueMax=typedArray.getFloat(R.styleable.CustomProgressBar_progressValueMax,progressValueMax);

        typedArray.recycle();
    }

    private int dipToPx(float dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,dip,getResources().getDisplayMetrics());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(Math.min(width,height),Math.min(width,height));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        /* 画一个圆
        drawCircle(float cx, float cy, float radius, @NonNull Paint paint)
        cx:圆心的x坐标。
        cy:圆心的y坐标。
        radius:圆的半径。
        paint:绘制时所使用的画笔。
        */
        int center=getWidth()/2;
        // 减去边框作为半径 否则宽度不够显示不全
        canvas.drawCircle(center,center,center-borderWidth/2,innerPaint);

        /*
            center-borderWidth/2
            如果画圆的时候是 -borderWidth  那么圆弧也是 -borderWidth
            如果画圆的时候是 -borderWidth/2 那么圆弧也是 -borderWidth/2
        */

        //     画第一个圆弧
        //     public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter,
        //            @NonNull Paint paint) {
        //     startAngle开始的角度 sweepAngle-弧线顺时针旋转的角度 useCenter 如果为true，绘制的起点和终点会和圆心相连
        //     public RectF(float left, float top, float right, float bottom)
        //     矩形的宽width = right - left ，高height = bottom - top
        RectF oval=new RectF(borderWidth/2,borderWidth/2,getWidth()-borderWidth/2,getHeight()-borderWidth/2);
        canvas.drawArc(oval,0, progressValue/progressValueMax *360,false,outerPaint);


        ////////////////////////画文字////////////////////////

        DecimalFormat df = new DecimalFormat("##.00%");
        String text=df.format(progressValue/progressValueMax);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        //计算宽度 字体的长度有关
        Rect bounds=new Rect();//矩形
        //给矩形设置边界
        textPaint.getTextBounds(text,0,text.length(),bounds);

        int dx=getWidth()/2-bounds.width()/2;
        int dy= (int) ((fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom);
        int baseLine=getHeight()/2+dy;
        canvas.drawText(text,getPaddingLeft()+dx,baseLine,textPaint);
    }

    public float getProgressValue() {
        return progressValue;
    }

    public synchronized void updateProgress(float value) {
        if(value<0) return;

        this.progressValue=value;
        invalidate();
    }
}
