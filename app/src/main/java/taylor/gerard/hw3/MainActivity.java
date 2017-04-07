package taylor.gerard.hw3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Drawables

    private Drawable circleDrawable; // circle
    private Drawable squareDrawable; //square
    private ShapeDrawable starDrawable; //star
    private ShapeDrawable arrowDrawable; // arrow
    private ScoreDrawable scoreDrawable; //score

    private int shapeSize; //size of each shape
    private int strokeWidth; // stroke width for each shape
    private GameBoard gameBoard; //gameboard
    private int score = 0; //score

    private Jewel tappedJewel = null; // jewel that is tapped
    private Jewel selectedJewel = null; //jewel that is selected
    private Rect selectedJewelBounds = null; // bounds of selected jewel
    private Jewel swapJewel = null; // jewel to be swapped

    public boolean refreshed = false; // boolean to check if board has been shuffled.

    GestureDetector gestureDetector; // gesture detector to sense genestures outside board

    private ColorStateList colorStateList; // color state list for star
    private ColorStateList arrowStrokeList; // color state list for arrow
    private AlertDialog alertDialog; // alert dialog field

    private Jewel[][] jewels = new Jewel[8][8]; // array of jewels used to comprise board.

    private LinearLayout mainLayout; // layout that holds the baord.

    //blinker runnable used to temporarily change color of shapes.
    //taken from lecture.

    private volatile boolean blink = false;
    private Runnable blinker = new Runnable() {
        @Override
        public void run() {
            try {
                blink = true;
                gameBoard.postInvalidate();
                Thread.sleep(250);
                blink = false;
                gameBoard.postInvalidate();
                Thread.sleep(250);
                blink = true;
                gameBoard.postInvalidate();
                Thread.sleep(250);
                blink = false;
                gameBoard.postInvalidate();
                tappedJewel = null;
                selectedJewel = null;
                gameBoard.isTap = false;
                gameBoard.isDrag = false;
                gameBoard.matchPositions.clear();
                gameBoard.postInvalidate();
            }catch (InterruptedException e){
                blink = false;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout screen = (LinearLayout) findViewById(R.id.activity_main); // get screens layout
        //after screen is loaded set the size of the attributes.
        screen.post(new Runnable() {
            @Override
            public void run() {
                colorStateList = getResources().getColorStateList(R.color.starstroke); //set color state list for star
                arrowStrokeList = getResources().getColorStateList(R.color.arrowstroke); // set arrow color stroke list
                shapeSize = Math.min(screen.getWidth(), screen.getHeight())/8; // sets the shape size based on the smallest dimensio of screen

                strokeWidth = (int)getResources().getDimension(R.dimen.strokeWidth); //set stroke width

                circleDrawable = getResources().getDrawable(R.drawable.circle); //set circle drawable
                squareDrawable = getResources().getDrawable(R.drawable.square); //set square drawable
                starDrawable = generateStarDrawable(); //set star drawable
                arrowDrawable = generateArrowDrawable(); // set arrow drawable

                //set score drawable.

                scoreDrawable = (ScoreDrawable) findViewById(R.id.scoreText);
                scoreDrawable.setScore(score+"");
                scoreDrawable.invalidate();

                //set gameboard / initialize

                gameBoard = new GameBoard(MainActivity.this);
                mainLayout = (LinearLayout)findViewById(R.id.boardHolder);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(shapeSize*8, shapeSize*8,1);
                gameBoard.setLayoutParams(layoutParams);
                gameBoard.fillJewels();
                mainLayout.addView(gameBoard);

                //set main screen gesture recognizer.

                gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        refreshed = false;
                        Rect r = new Rect(gameBoard.getLeft(), gameBoard.getTop(), gameBoard.getRight(), gameBoard.getBottom());
                        if(r.contains((int)e.getX(), (int)e.getY())){
                            return false;
                        }
                        if(alertDialog == null){
                            alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        }
                        alertDialog.setTitle("Paused");
                        alertDialog.setMessage("The game is paused");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Resume", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        refreshed = false;
                        return true;
                    }
                });
            }
        });
    }

    //save score on screen rotation.

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("score", score);
    }

    //get score on screen restore.

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        score = savedInstanceState.getInt("score");
        if(scoreDrawable!= null) {
            scoreDrawable.setScore(score + "");
            scoreDrawable.invalidate();
        }
    }

    //set touch event listener

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event); // show pause alert if tap.

        //shuffle gameboard if swiped.
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(!refreshed){
                    gameBoard.shuffleBoard();
                    refreshed = true;
                    score-=10;
                    scoreDrawable.setScore(score+"");
                    scoreDrawable.invalidate();
                }
        }
        return super.onTouchEvent(event);
    }

    //creates arrow drawable

    private ShapeDrawable generateArrowDrawable() {

        final Arrow arrow = generateArrow();
        ShapeDrawable arrowDrawable = new ShapeDrawable(arrow){
            @Override
            protected boolean onStateChange(int[] stateSet) {
                arrow.setState(stateSet);
                return super.onStateChange(stateSet);
            }

            @Override
            public boolean isStateful() {
                return true;
            }
        };
        arrowDrawable.setIntrinsicHeight(this.shapeSize);
        arrowDrawable.setIntrinsicWidth(this.shapeSize);
        arrowDrawable.setBounds(0,0,this.shapeSize, this.shapeSize);
        return arrowDrawable;
    }

    private Arrow generateArrow() {
        int arrowFillColor = getResources().getColor(R.color.arrowFillColor);
        return new Arrow(this.strokeWidth, arrowFillColor, arrowStrokeList);
    }

    //creates star drawable

    public ShapeDrawable generateStarDrawable(){
        final Star star = generateStar();
        ShapeDrawable starDrawable = new ShapeDrawable(star){
            @Override
            protected boolean onStateChange(int[] stateSet) {
                star.setState(stateSet);
                return super.onStateChange(stateSet);
            }

            @Override
            public boolean isStateful() {
                return true;
            }
        };
        starDrawable.setIntrinsicHeight(this.shapeSize);
        starDrawable.setIntrinsicWidth(this.shapeSize);
        starDrawable.setBounds(0,0,this.shapeSize, this.shapeSize);
        return starDrawable;
    }

    public Star generateStar(){
        int starStrokeColor = getResources().getColor(R.color.starStrokeColor);
        int starFillColor = getResources().getColor(R.color.starFillColor);
        return new Star(strokeWidth, colorStateList, starFillColor);
    }

    //gameboard class

    private class GameBoard extends View {

        //touch values used to track if the potential swap was valid.
        private int initialTouchX = 0;
        private int initialTouchY = 0;
        private int updatedTouchX = 0;
        private int updatedTouchY = 0;

        //placeholders for the selected and swapped jewel types

        private Jewel.Type swapType = null;
        private Jewel.Type selectType = null;

        //match positions. Used to find all of the positions that constitute a match.
        //primarily used in highlighting and removing shapes.

        private ArrayList<Position> matchPositions = new ArrayList<>();

        //boolean to test if board was tapped.
        private boolean isTap = false;
        private boolean isDrag = false;

        //INITIALIZATION AND BOARD FILLING.

        //boolean to tell if there are matches present based on the last swap.
        private boolean matchesPresent = false;

        //highlights the matches.

        private Runnable highLightMatches = new Runnable() {
            @Override
            public void run() {
                try {
                    tappedJewel = null;
                    postInvalidate(); // sets colors.
                    Thread.sleep(500); // freezes for half a second
                    for(Position p :matchPositions){ // set match positions = null
                        jewels[p.getY()][p.getX()] = null;
                    }
                    score+=matchPositions.size(); //update the score
                    scoreDrawable.setScore(score+""); //set the score in the drawable
                    matchPositions.clear();//remove matched positions
                    postInvalidate(); // invalidate
                    shiftDown(); //move values down after match and removal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scoreDrawable.invalidate(); //update score on UI thread
                        }
                    });
                }catch (InterruptedException e){
                    blink = false;
                }
            }
        };

        //constructor
        public GameBoard(Context context){
            super(context);
        }

        //fill jewels initially.
        public void fillJewels(){
            for(int i = 0; i < jewels.length; i++){
                for(int j = 0; j < jewels[i].length; j++){
                    generateRandomShape(i, j);
                }
            }
            checkMatches();
        }

        //generates random shape
        public void generateRandomShape(int i, int j) {
            Random random = new Random();
            int val = random.nextInt(4); // random int used to set type.
            Jewel jewel = null;
            //bounds based on array location
            Rect rect = new Rect(j * shapeSize + shapeSize / 8, i * shapeSize + shapeSize / 8, j * shapeSize + shapeSize - shapeSize / 8, i * shapeSize + shapeSize - shapeSize / 8);
            switch(val){
                case 0: {
                    jewel = new Jewel(rect, Jewel.Type.Arrow);
                    jewel.setPosition(new Position(j, i));
                    jewels[i][j] = jewel;
                    break;
                }
                case 1: {
                    jewel = new Jewel(rect, Jewel.Type.Circle);
                    jewel.setPosition(new Position(j, i));
                    jewels[i][j] = jewel;
                    break;
                }
                case 2: {
                    jewel = new Jewel(rect, Jewel.Type.Star);
                    jewel.setPosition(new Position(j, i));
                    jewels[i][j] = jewel;
                    break;
                }
                case 3: {
                    jewel = new Jewel(rect, Jewel.Type.Square);
                    jewel.setPosition(new Position(j, i));
                    jewels[i][j] = jewel;
                    break;
                }
            }
        }
        //refill empty null parts of array.
        private void refillEmptyCells() {
            for(int i = 0; i < jewels.length; i++){
                for(int j = 0; j < jewels[i].length; j++){
                    if(jewels[i][j] == null){
                        generateRandomShape(i,j);
                    }
                }
            }
        }

        //shifts shapes in each column down and refills empty cells. repeats until no matches are found.

        public void shiftDown() {
            Queue<Jewel>  queue = new LinkedList<>();
            for(int j = 0; j < 8; j++){
                for(int i = 7; i >= 0; i--){
                    if(jewels[i][j] != null){
                        queue.add(jewels[i][j]);
                        jewels[i][j] = null;
                    }
                }
                int i = 7;
                while(!queue.isEmpty()){
                    Jewel temp = queue.poll();
                    if(temp == null){
                        i--;
                        continue;
                    }
                    Jewel jewel = null;
                    Rect rect = new Rect(j * shapeSize + shapeSize / 8, i * shapeSize + shapeSize / 8, j * shapeSize + shapeSize - shapeSize / 8, i * shapeSize + shapeSize - shapeSize / 8);
                    jewel = new Jewel(rect, temp.getType());
                    jewel.setPosition(new Position(j, i));
                    jewels[i][j] = jewel;
                    i--;
                }
            }
            refillEmptyCells();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(checkResidualMatches()){
                new Thread(highLightMatches).start();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }


        //checks matches to highlight
        public boolean checkMatchesToHighlight(int i, int j){
            if(matchPositions.size() == 0){
                return false;
            }else{
                for(Position p : matchPositions){
                    if(p.getY() == i && p.getX() == j){
                        return true;
                    }
                }
            }
            return false;
        }

        //OVERRIDE METHODS

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Drawable drawableToUse = null;
            for(int i = 0; i < jewels.length; i++){
                for(int j = 0; j < jewels[i].length; j++){
                    if(jewels[i][j] == null){
                        continue;
                    }
                    switch(jewels[i][j].getType()){
                        case Square:
                            drawableToUse = squareDrawable;
                            break;
                        case Circle:
                            drawableToUse = circleDrawable;
                            break;
                        case Arrow:
                            drawableToUse = arrowDrawable;
                            break;
                        case Star:
                            drawableToUse = starDrawable;
                            break;
                    }
                    //draw logic for highlighting
                    if(checkMatchesToHighlight(i, j)){
                        drawableToUse.setState(selectedState);
                    }else if(jewels[i][j] == selectedJewel &&(tappedJewel != null && jewels[i][j].getType() == tappedJewel.getType())){
                        drawableToUse.setState(selectedState);
                    }else if(isTap && tappedJewel != null && jewels[i][j].getType() == tappedJewel.getType()){
                        drawableToUse.setState(selectedState);
                    }else{
                        drawableToUse.setState(unSelectedState);
                    }
                    drawableToUse.setBounds(jewels[i][j].getBounds());
                    drawableToUse.draw(canvas);
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = determineMeasure(widthMeasureSpec, R.dimen.preferredWidth);
            int height = determineMeasure(heightMeasureSpec, R.dimen.preferredHeight);

            setMeasuredDimension(width, height);
        }

        private int determineMeasure(int measureSpec, int preferredId) {

            float preferredSize = getResources().getDimension(preferredId);
            int size = MeasureSpec.getSize(measureSpec);
            int mode = MeasureSpec.getMode(measureSpec);

            switch (mode){
                case MeasureSpec.AT_MOST:
                    return (int) Math.min(preferredSize, size);
                case MeasureSpec.EXACTLY:
                    return size;
                case MeasureSpec.UNSPECIFIED:
                    return (int)preferredSize;
                default:
                    throw new IllegalArgumentException("Unhandled measure mode.");
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = (int)event.getX();
                    initialTouchY = (int)event.getY();
                    isTap = true;
                    selectedJewel = findJewelAtLocation((int)event.getX(), (int)event.getY());
                    if(selectedJewel != null) {
                        selectedJewelBounds = selectedJewel.getBounds();
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE: // if there is a valid drag.
                    if(isTap){
                        isDrag = true;
                        isTap = false;
                    }
                    updatedTouchX = (int)event.getX();
                    updatedTouchY = (int) event.getY();
                    if(selectedJewel != null){
                        selectedJewel.setBounds(new Rect((int)event.getX() * shapeSize + shapeSize / 8, (int) event.getY() * shapeSize + shapeSize / 8, (int)event.getX()* shapeSize + shapeSize - shapeSize / 8, (int) event.getY() * shapeSize + shapeSize - shapeSize / 8));
                        invalidate();
                        if(selectedJewel.getBounds().centerX() > selectedJewelBounds.centerX()+3*selectedJewelBounds.width()/2){
                            selectedJewel.setBounds(selectedJewelBounds);
                        }
                        if(selectedJewel.getBounds().centerX() < selectedJewelBounds.centerX()-3*selectedJewelBounds.width()/2){
                            selectedJewel.setBounds(selectedJewelBounds);
                        }
                        if(selectedJewel.getBounds().centerY() < selectedJewelBounds.centerY()+3*selectedJewelBounds.height()/2){
                            selectedJewel.setBounds(selectedJewelBounds);
                        }
                        if(selectedJewel.getBounds().centerY() > selectedJewelBounds.centerY()-3*selectedJewelBounds.height()/2){
                            selectedJewel.setBounds(selectedJewelBounds);
                        }
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP: // action release logic
                    if(isTap){
                        tappedJewel = selectedJewel;
                        new Thread(blinker).start();
                        invalidate();
                        return true;
                    }else {
                        isTap = false;
                        isDrag = false;
                        updatedTouchX = (int) event.getX();
                        updatedTouchY = (int) event.getY();
                        if (selectedJewel != null) {
                            swapJewel = findJewelAtLocation((int) event.getX(), (int) event.getY());
                            if (swapJewel != null) {
                                if (validSwap()) {
                                    swap();
                                    matchesPresent = false;
                                    checkSelectedMatches();
                                    checkSwappedMatches();
                                    if (!matchesPresent) {
                                        swap();
                                        invalidate();
                                        return true;
                                    } else {
                                        new Thread(highLightMatches).start();
                                    }
                                }
                            }
                        }
                    }
                }
            return super.onTouchEvent(event);
        }

        public boolean checkResidualMatches(){
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    ArrayList<Position> positions = new ArrayList<>();
                    //check vertical
                    if(jewels[i][j] == null){
                        continue;
                    }
                    Jewel.Type type = jewels[i][j].getType();
                    Position p = new Position(j, i);
                    int y = p.getY();
                    while(y >= 0){
                        Jewel temp = jewels[y][p.getX()];
                        if(temp != null){
                            if(temp.getType() == type){
                                positions.add(new Position(p.getX(), y));
                                y--;
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    y = p.getY()+1;
                    while(y < 8){
                        Jewel temp = jewels[y][p.getX()];
                        if(temp != null){
                            if(temp.getType() == type){
                                positions.add(new Position(p.getX(), y));
                                y++;
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }

                    //check x
                    ArrayList<Position> lateralPositions = new ArrayList<>();
                    int x = p.getX();
                    while(x >= 0){
                        Jewel temp = jewels[p.getY()][x];
                        if(temp != null){
                            if(temp.getType() == type){
                                lateralPositions.add(new Position(x, p.getY()));
                                x--;
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }

                    x = p.getX()+1;
                    while(x < 8){
                        Jewel temp = jewels[p.getY()][x];
                        if(temp != null){
                            if(temp.getType() == type){
                                lateralPositions.add(new Position(x, p.getY()));
                                x++;
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }

                    if(positions.size() >= 3){
                        matchesPresent = true;
                        for(Position p1 : positions){
                            matchPositions.add(p1);
                        }
                    }

                    if(lateralPositions.size() >= 3){
                        matchesPresent = true;
                        for(Position p1 : lateralPositions){
                            matchPositions.add(p1);
                        }
                    }

                }
            }
            return matchesPresent;
        }

        //check matches of first jewel

        public boolean checkSwappedMatches() {
            ArrayList<Position> positions = new ArrayList<>();
            //check vertical

            Position p = swapJewel.getPosition();
            int y = p.getY();
            while(y >= 0){
                Jewel temp = jewels[y][p.getX()];
                if(temp != null){
                    if(temp.getType() == swapType){
                        positions.add(new Position(p.getX(), y));
                        y--;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            y = p.getY()+1;
            while(y < 8){
                Jewel temp = jewels[y][p.getX()];
                if(temp != null){
                    if(temp.getType() == swapType){
                        positions.add(new Position(p.getX(), y));
                        y++;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            //check x
            ArrayList<Position> lateralPositions = new ArrayList<>();
            int x = p.getX();
            while(x >= 0){
                Jewel temp = jewels[p.getY()][x];
                if(temp != null){
                    if(temp.getType() == swapType){
                        lateralPositions.add(new Position(x, p.getY()));
                        x--;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            x = p.getX()+1;
            while(x < 8){
                Jewel temp = jewels[p.getY()][x];
                if(temp != null){
                    if(temp.getType() == swapType){
                        lateralPositions.add(new Position(x, p.getY()));
                        x++;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            if(positions.size() >= 3){
                matchesPresent = true;
                for(Position p1 : positions){
                    matchPositions.add(p1);
                }
            }

            if(lateralPositions.size() >= 3){
                matchesPresent = true;
                for(Position p1 : lateralPositions){
                    matchPositions.add(p1);
                }
            }
            return matchesPresent;
        }


        //check matches of second jewel
        public boolean checkSelectedMatches(){
            ArrayList<Position> positions = new ArrayList<>();
            //check vertical

            Position p = selectedJewel.getPosition();
            int y = p.getY();
            while(y >= 0){
                Jewel temp = jewels[y][p.getX()];
                if(temp != null){
                    if(temp.getType() == selectType){
                        positions.add(new Position(p.getX(), y));
                        y--;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }
            y = p.getY()+1;
            while(y < 8){
                Jewel temp = jewels[y][p.getX()];
                if(temp != null){
                    if(temp.getType() == selectType){
                        positions.add(new Position(p.getX(), y));
                        y++;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            //check x
            ArrayList<Position> lateralPositions = new ArrayList<>();
            int x = p.getX();
            while(x >= 0){
                Jewel temp = jewels[p.getY()][x];
                if(temp != null){
                    if(temp.getType() == selectType){
                        lateralPositions.add(new Position(x, p.getY()));
                        x--;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            x = p.getX()+1;
            while(x < 8){
                Jewel temp = jewels[p.getY()][x];
                if(temp != null){
                    if(temp.getType() == selectType){
                        lateralPositions.add(new Position(x, p.getY()));
                        x++;
                        continue;
                    }else{
                        break;
                    }
                }else{
                    break;
                }
            }

            if(positions.size() >= 3){
                matchesPresent = true;
                for(Position p1 : positions){
                    matchPositions.add(p1);
                }
            }
            if(lateralPositions.size() >= 3){
                matchesPresent = true;
                for(Position p1 : lateralPositions){
                    matchPositions.add(p1);
                }
            }
            return matchesPresent;
        }

        //test to see if swap was valid

        public boolean validSwap() {
            if(Math.abs(updatedTouchX-initialTouchX) > Math.abs(updatedTouchY - initialTouchY)) {
                if (Math.abs(updatedTouchX-initialTouchX) <= shapeSize*1.5) {
                    return true;
                }
            }else if(Math.abs(updatedTouchX-initialTouchX) < Math.abs(updatedTouchY - initialTouchY)){
                if(Math.abs(updatedTouchY - initialTouchY) <= shapeSize*1.5){
                    return true;
                }
            }
            return false;
        }

        //execute swap

        public void swap() {
            Jewel.Type temp = selectedJewel.getType();
            selectedJewel.setType(swapJewel.getType());
            swapJewel.setType(temp);
            swapType = swapJewel.getType();
            selectType = selectedJewel.getType();
            invalidate();
        }

        //find jewel given x and y location

        private Jewel findJewelAtLocation(int x, int y){
            for(int i = 0; i < jewels.length; i++){
                for(int j = 0; j < jewels[i].length; j++){
                    if(jewels[i][j] != null) {
                        if (jewels[i][j].getBounds().contains(x, y)) {
                            return jewels[i][j];
                        }
                    }
                }
            }
            return null;
        }

        //reset board.

        public void shuffleBoard(){
            for(int i = 0; i < jewels.length; i++){
                Arrays.fill(jewels[i], null);
            }
            fillJewels();
        }

        //MATCH CHECKING LOGIC.

        public void checkMatches(){
            ArrayList<Position> matches = new ArrayList<>();
            for(int i = 0; i < jewels.length; i++){
                for(int j = 0; j < jewels[i].length; j++){
                    if(jewels[i][j] == null){
                        generateRandomShape(i, j);
                    }
                    Position p = new Position(j, i);
                    Jewel jewel = jewels[i][j];
                    ArrayList<Position> temp = checkMatches(p, jewel);
                    if(temp != null){
                        matches.addAll(temp);
                    }
                }
            }
            while(matches.size() > 0) {
                Position p = matches.get(0);
                ArrayList<Position> temp;
                while(true){
                    temp = checkMatches(p, jewels[p.getY()][p.getX()]);
                    if(temp == null){
                        matches.remove(0);
                        break;
                    }
                    generateRandomShape(p.getY(), p.getX());
                }
            }
            invalidate();
        }

        private ArrayList<Position> checkMatches(Position p, Jewel jewel) {
            ArrayList<Position> lateral = checkLateral(p, jewel);
            ArrayList<Position> vertical = checkVertical(p, jewel);
            ArrayList<Position> matches = new ArrayList<>();
            matches.add(p);
            if(vertical == null && lateral == null){
                return null;
            }
            if(lateral != null){
                matches.addAll(lateral);
            }
            if(vertical != null){
                matches.addAll(vertical);
            }
            return matches;
        }

        private ArrayList<Position> checkVertical(Position p, Jewel jewel) {
            ArrayList<Position> matches = new ArrayList<>();
            matches.addAll(checkUp(p, jewel));
            matches.addAll(checkDown(p, jewel));
            if(matches.size() + 1 >= 3){
                return matches;
            }
            return null;
        }

        private ArrayList<Position> checkDown(Position p, Jewel jewel) {
            int verticalLocation = p.getY()+1;
            ArrayList<Position> matches = new ArrayList<>();
            while(verticalLocation < 8 && jewels[verticalLocation][p.getX()] != null){
                if(jewel.getType() == jewels[verticalLocation][p.getX()].getType()) {
                    matches.add(new Position(p.getX(), verticalLocation));
                    verticalLocation++;
                }else{
                    break;
                }
            }
            return matches;
        }

        private ArrayList<Position> checkUp(Position p, Jewel jewel) {
            int verticalLocation = p.getY()-1;
            ArrayList<Position> matches = new ArrayList<>();
            while(verticalLocation >= 0 && jewels[verticalLocation][p.getX()] != null){
                if(jewels[verticalLocation][p.getX()] == null){
                    break;
                }
                if(jewel.getType() == jewels[verticalLocation][p.getX()].getType()) {
                    matches.add(new Position(p.getX(), verticalLocation));
                    verticalLocation--;
                }else{
                    break;
                }
            }
            return matches;
        }

        private ArrayList<Position> checkLateral(Position p, Jewel jewel) {

            ArrayList<Position> matches = new ArrayList<>();
            matches.addAll(checkLeft(p, jewel));
            matches.addAll(checkRight(p, jewel));
            if(matches.size() + 1 >= 3){
                return matches;
            }
            return null;
        }

        private ArrayList<Position> checkRight(Position p, Jewel jewel) {
            int lateralLocation = p.getX()+1;
            ArrayList<Position> matches = new ArrayList<>();
            while(lateralLocation < 8 && jewels[p.getY()][lateralLocation] != null){
                if(jewel.getType() == jewels[p.getY()][lateralLocation].getType()) {
                    matches.add(new Position(lateralLocation, p.getY()));
                    lateralLocation++;
                }else{
                    break;
                }
            }
            return matches;
        }

        private ArrayList<Position> checkLeft(Position p, Jewel jewel) {
            int lateralLocation = p.getX()-1;
            ArrayList<Position> matches = new ArrayList<>();
            while(lateralLocation >= 0 && jewels[p.getY()][lateralLocation] != null ){
                if(jewel.getType() == jewels[p.getY()][lateralLocation].getType()) {
                    matches.add(new Position(lateralLocation, p.getY()));
                    lateralLocation--;
                }else{
                    break;
                }
            }
            return matches;
        }

    }

    private static final int[] selectedState = {android.R.attr.state_selected};
    private static final int[] unSelectedState = {};

}
