package com.defch.demonike.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.defch.demonike.R;
import com.defch.demonike.ifaces.MoveListener;

/**
 * Created by DiegoFranco on 3/9/15.
 */
public class IndicatorView extends View implements MoveListener {

    private static final String TAG = IndicatorView.class.getSimpleName();
    private static final float DEFAULT_MAX_MOVE = 10;
    private static final int COLOR_ON = R.color.nike_g;
    private static final int COLOR_OFF = R.color.nike_r;
    private static final int COLOR_SCALE = R.color.nike_y;

    private float mMaxSpeed;
    private float mCurrentSpeed;

    private String label;
    private Paint onMarkPaint;
    private Paint offMarkPaint;
    private Paint scalePaint;
    private Paint readingPaint;
    private Path onPath;
    private Path offPath;
    final RectF oval = new RectF();

    private int ON_COLOR = COLOR_ON;
    private int OFF_COLOR = COLOR_OFF;
    private int SCALE_COLOR = COLOR_SCALE;

    private float SCALE_SIZE = 15f;
    private float READING_SIZE = 24f;

    private float centerX;
    private float centerY;
    private float radius;

    public IndicatorView(Context context) {
        super(context);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Indicatorview, 0, 0);
        try {
            mMaxSpeed = a.getFloat(R.styleable.Indicatorview_maxMove, DEFAULT_MAX_MOVE);
            mCurrentSpeed = a.getFloat(R.styleable.Indicatorview_currentMove, 0);
            ON_COLOR = a.getColor(R.styleable.Indicatorview_colorOn, ON_COLOR);
            OFF_COLOR = a.getColor(R.styleable.Indicatorview_colorOff, OFF_COLOR);
            SCALE_COLOR = a.getColor(R.styleable.Indicatorview_scaleColor, SCALE_COLOR);
            SCALE_SIZE = a.getDimension(R.styleable.Indicatorview_textSizeScale, SCALE_SIZE);
            READING_SIZE = a.getDimension(R.styleable.Indicatorview_textSizeRead, READING_SIZE);
        } finally{
            a.recycle();
        }
        initDraw();
    }

    private void initDraw(){
        onMarkPaint = new Paint();
        onMarkPaint.setStyle(Paint.Style.STROKE);
        onMarkPaint.setColor(ON_COLOR);
        onMarkPaint.setStrokeWidth(35f);
        onMarkPaint.setShadowLayer(5f, 0f, 0f, ON_COLOR);
        onMarkPaint.setAntiAlias(true);

        offMarkPaint = new Paint(onMarkPaint);
        offMarkPaint.setColor(OFF_COLOR);
        offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);

        scalePaint = new Paint(offMarkPaint);
        scalePaint.setStrokeWidth(2f);
        scalePaint.setTextSize(SCALE_SIZE);
        scalePaint.setShadowLayer(5f, 0f, 0f, Color.RED);
        scalePaint.setColor(SCALE_COLOR);

        readingPaint = new Paint(scalePaint);
        readingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        offMarkPaint.setShadowLayer(3f, 0f, 0f, Color.WHITE);
        readingPaint.setTextSize(65f);
        readingPaint.setTypeface(Typeface.SANS_SERIF);
        readingPaint.setColor(Color.WHITE);

        onPath = new Path();
        offPath = new Path();
    }

    public float getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setCurrentSpeed(float mCurrentSpeed) {
        if(mCurrentSpeed > this.mMaxSpeed)
            this.mCurrentSpeed = mMaxSpeed;
        else if(mCurrentSpeed < 0)
            this.mCurrentSpeed = 0;
        else
            this.mCurrentSpeed = mCurrentSpeed;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + width + "x" + height);

        if (width > height){
            radius = height/4;
        }else{
            radius = width/4;
        }
        oval.set(centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension / 2;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else {
            return getPreferredSize();
        }
    }

    private int getPreferredSize() {
        return 10;
    }

    @Override
    public void onDraw(Canvas canvas){
        drawScaleBackground(canvas);
        drawScale(canvas);
        drawLegend(canvas);
        drawReading(canvas);
    }

    private void drawScaleBackground(Canvas canvas){
        canvas.drawARGB(0, 0, 0, 0);
        offPath.reset();
        for(int i = -180; i < 0; i+=4){
            offPath.addArc(oval, i, 2f);
        }
        canvas.drawPath(offPath, offMarkPaint);
    }

    private void drawScale(Canvas canvas){
        onPath.reset();
        for(int i = -180; i < (mCurrentSpeed/mMaxSpeed)*180 - 180; i+=4){
            onPath.addArc(oval, i, 2f);
        }
        canvas.drawPath(onPath, onMarkPaint);
    }

    private void drawLegend(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(-180, centerX,centerY);
        Path circle = new Path();
        double halfCircumference = radius * Math.PI;
        double increments = 20;
        for(int i = 0; i < this.mMaxSpeed; i += increments){
            circle.addCircle(centerX, centerY, radius, Path.Direction.CW);
            canvas.drawTextOnPath(String.format("%d", i),
                    circle,
                    (float) (i * halfCircumference / this.mMaxSpeed),
                    -30f,
                    scalePaint);
        }

        canvas.restore();
    }

    private void drawReading(Canvas canvas){
        Path path = new Path();
        String message = String.format( "%s - %d", label, (int)this.mCurrentSpeed);
        float[] widths = new float[message.length()];
        readingPaint.getTextWidths(message, widths);
        float advance = 0;
        for(double width:widths)
            advance += width;
        path.moveTo(centerX - advance / 2, centerY);
        path.lineTo(centerX + advance / 2, centerY);
        canvas.drawTextOnPath(message, path, 0f, 0f, readingPaint);
    }

    @Override
    public void moveChangedListener(float value) {
        this.setCurrentSpeed(value);
        this.invalidate();
    }
}
