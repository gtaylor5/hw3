package taylor.gerard.hw3;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by Gerard on 3/12/2017.
 */

public class Star extends Shape {

    private Path path;
    private Paint strokePaint;
    private Paint fillPaint;

    private ColorStateList strokeColor;
    private int strokeWidth;
    private int fillColor;

    public Star(int strokeWidth, ColorStateList strokeColor, int fillColor) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(this.strokeWidth);
        strokePaint.setColor(strokeColor.getColorForState(new int[0],0));

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(this.fillColor);

    }

    public void setState(int[] stateList){
        this.strokePaint.setColor(strokeColor.getColorForState(stateList,0));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
        path = new Path();
        path.moveTo(0,0);
        path.lineTo(width,height);
        path.moveTo(width/2, 0);
        path.lineTo(width/2,height);
        path.moveTo(width,0);
        path.lineTo(0,height);
        path.moveTo(0,height/2);
        path.lineTo(width,height/2);
        path.addCircle(width/2,height/2,Math.min(height, width)/3, Path.Direction.CW);
    }



}
