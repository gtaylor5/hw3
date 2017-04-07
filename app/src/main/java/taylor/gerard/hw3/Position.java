package taylor.gerard.hw3;

/**
 * Created by Gerard on 3/12/2017.
 */

public class Position {

    //hold position in array.

    private int x = 0;
    private int y = 0;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
