package dte.masteriot.mdp.smartplantguardian;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class LineChartView extends View {

    private Paint linePaint;
    private Paint gridPaint;
    private Paint textPaint;
    private Path linePath;
    private float[] dataPoints;
    private int maxDataPoints = 7; // Should match maxPoints in MainActivity

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the data line
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#2196F3")); // Blue line
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(8f);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        // Paint for the grid lines
        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);

        // Paint for axis labels
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        linePath = new Path();
        dataPoints = new float[maxDataPoints];
    }

    public void setData(float[] data) {
        if (data != null) {
            if(data.length != this.maxDataPoints) {
                this.maxDataPoints = data.length;
            }
            this.dataPoints = data;
            invalidate(); // Request redraw
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.length == 0) {
            return;
        }

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float padding = 40f; // Padding for labels

        // Draw horizontal grid lines and labels
        for (int i = 0; i <= 4; i++) {
            float y = padding + i * (viewHeight - 2 * padding) / 4f;
            canvas.drawLine(padding, y, viewWidth - padding, y, gridPaint);
            String label = String.valueOf(100 - i * 25);
            canvas.drawText(label, padding - 20, y + (textPaint.getTextSize() / 3), textPaint);
        }

        // Draw the data line
        linePath.reset();
        float xStep = (viewWidth - 2 * padding) / (float) (maxDataPoints - 1);

        for (int i = 0; i < maxDataPoints; i++) {
            float x = padding + i * xStep;
            float y = padding + (100 - dataPoints[i]) / 100f * (viewHeight - 2 * padding);

            if (i == 0) {
                linePath.moveTo(x, y);
            } else {
                linePath.lineTo(x, y);
            }
        }

        canvas.drawPath(linePath, linePaint);
    }
}
