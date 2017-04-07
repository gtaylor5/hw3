package taylor.gerard.hw3;

import android.graphics.Rect;

/**
 * Created by Gerard on 3/11/2017.
 */
public class Jewel {

    //type class to handle all of the jewel types

    public enum Type {
        Circle, Square, Star, Arrow;
    }

    private Type type;
    private Rect bounds;
    private Position position;

    public Jewel(Rect bounds, Type type) {
        this.bounds = bounds;
        this.type = type;
    }

    public Jewel(Type type){
        this.type = type;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
