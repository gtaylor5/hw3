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

public class Arrow extends Shape {

    private int strokeWidth;
    private int fillColor;
    private ColorStateList strokeColor;
    private Path path;

    private Paint fillPaint;
    private Paint strokePaint;

    public Arrow(int strokeWidth, int fillColor, ColorStateList strokeColor){

        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(this.strokeColor.getColorForState(new int[0],0));
        strokePaint.setStrokeWidth(this.strokeWidth);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(this.fillColor);

    }

    public void setState(int[] stateList){
        this.strokePaint.setColor(strokeColor.getColorForState(stateList,0));
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if(path == null){
            path = new Path();
        }
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, strokePaint);

    }

    //arrow logic

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
        path = new Path();
        path.moveTo(width/2, 0);
        path.lineTo(width/2, height);
        path.lineTo(0, height/2);
        path.lineTo(width, height/2);
        path.lineTo(width/2, height);
    }
}
