package taylor.gerard.hw3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Gerard on 3/13/2017.
 */

public class ScoreDrawable extends View {

    private Paint paint = new Paint();
    private String score = "";

    public ScoreDrawable(Context context) {
        super(context);
    }

    public void setScore(String string){
        this.score = "Score: "+ string;
    }

    public ScoreDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScoreDrawable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setShadowLayer(6f, 0, 0, Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.score,getWidth()/2,getHeight() - getHeight()/6, paint);
    }
}
