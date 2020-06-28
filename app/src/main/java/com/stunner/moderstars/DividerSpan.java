package com.stunner.moderstars;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class DividerSpan extends ReplacementSpan {


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Paint bak = paint;
        paint.setStrokeWidth(2);
        canvas.drawLine(x, y + 20, x + 9000, y + 20, paint);
        canvas.drawText(text, start, end, x, y, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(measureText(paint, text, start, end));
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }

}