package com.moxin.videoline.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.moxin.videoline.R;

/**
 * @author yuqirong
 */

public class NewRoundImageView extends AppCompatImageView {

    private Path mPath;
    private RectF mRectF;
    private float[] rids = new float[8];
    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    public NewRoundImageView(Context context) {
        this(context, null);
    }

    public NewRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        float mRadius = array.getDimension(R.styleable.RoundImageView_radius, 10);
        rids[0] = mRadius;
        rids[1] = mRadius;
        rids[2] = mRadius;
        rids[3] = mRadius;
        rids[4] = 0f;
        rids[5] = 0f;
        rids[6] = 0f;
        rids[7] = 0f;
        array.recycle();
        mPath = new Path();
        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mPath.addRoundRect(mRectF, rids, Path.Direction.CW);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        canvas.save();
        canvas.clipPath(mPath);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF = new RectF(0, 0, w, h);
    }

}
