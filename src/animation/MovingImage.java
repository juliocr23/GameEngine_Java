package animation;

import others.Rectangle;
import others.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//TODO: Match keyboard to actions: Consider using a hashMap? or a class structure.
//TODO: Moving image should know which animation is the idle or it should mandate that
//TODO: the path for the idle animation should come first, and then the one for walking
//TODO: Rectangle width for collision is Fixed!

public class MovingImage extends Rectangle {

    private ArrayList<Animation> animations = new ArrayList<>();
    private Point initialVelocity  = new Point(2,2);
    public Point finalVelocity = new Point(5,5);
    public Point acceleration  = new Point(0.5f,0.5f);

    private float scale = 1;
    private int current = 0;      //The index of the animation being shown
    private BufferedImage image;  //The image being display
    public  String imageFormat = "png";
    public String filePath[];

    private int lastHorizontalMove = 1;
    private int lastVerticalMove = 3;

    public boolean[] movements = new boolean[4]; //Track sprite movements
    private char id; //Sprite id

    public float leftBoundary   =0;
    public float rightBoundary  =0;
    public float downBoundary   =0;
    public float upBoundary     =0;

    public Point distance = new Point();

    public boolean stopMoving = false;


    /**
     * Constructor
     * @param filePaths  An array containing the file paths for the animation of the sprite, where [0] is
     *                   the path to when the animation is idle and [1] when animation is walking.
     * @param duration   An array containing the duration for each animation
     */

    public MovingImage(String[] filePaths, int[] duration, char id) {

        this.coordinate.setLocation(coordinate); //Set location of the sprite
        this.id = id;
        this.filePath =  filePaths;

        //Load animation of the sprite
        for(int i = 0; i<filePaths.length; i++){
            try {
                Animation newAnimation = new Animation(filePaths[i],"."+imageFormat, duration[i]);
                animations.add(newAnimation);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        update();

        w = (image.getWidth() * scale);
        h = (image.getHeight() * scale);
    }

    public MovingImage(String filePath, char id) {
        try {
            this.id = id;
            image = ImageIO.read(new File(filePath + "." + imageFormat));
            w = scale*image.getWidth();
            h = scale*image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public MovingImage(MovingImage other) {

        id = other.getId();

        initialVelocity = new Point(other.initialVelocity);
        finalVelocity   = new Point(other.finalVelocity);
        acceleration    = new Point(other.acceleration);

        scale   = other.scale;
        current = other.current;
        image   = other.image;
        w       = other.w;
        h       = other.h;

        lastVerticalMove = other.lastVerticalMove;
        lastHorizontalMove = other.lastHorizontalMove;

        for(int i = 0; i<movements.length; i++) {
            movements[i] = other.movements[i];
        }

        animations = other.animations;
    }

    //MARK: Updates
    //------------------------------------------------------------------------------------------------------------------//
    public void update(){

        updateVelocity();

        if(animations.size() >= 1) {
            updateImage();
            animations.get(current).update();
        }
    }

    public void updateImage(){

        animations.get(current).nextImage();

        if (lastMoveLeft()) {
            image = animations.get(current).getMirrorImage();
        } else {
            image = animations.get(current).getImage();
        }
    }

    public void updateVelocity(){

        if(isMoving()) {
            current = 1;
            if (initialVelocity.x + acceleration.x <= finalVelocity.x)
                initialVelocity.x += acceleration.x;

            if (initialVelocity.y + acceleration.y <= finalVelocity.y)
                initialVelocity.y += acceleration.y;
        } else {
            initialVelocity.x = 0;
            //initialVelocity.y = 0; TODO:
            current = 0;
        }
    }

    //MARK: Rendering
    //------------------------------------------------------------------------------------------------------------------//
    public void draw(Graphics g){

        var imgWidth =  (int) (image.getWidth() * scale);
        var imgHeight = (int) (image.getHeight() * scale);
        g.drawImage(image,(int)getX(),(int) getY(), imgWidth, imgHeight,null);

        g.setColor(Color.red);
        g.drawRect((int)getX(),(int) getY(), (int)w, (int)h);
    }

    //MARK: Movements
    //-----------------------------------------------------------------------------------------------------------------//
    public void moveLeft(){
        moveLeft(initialVelocity.x);
        float offset = distance.x - initialVelocity.x;

        if(!stopMoving && offset>=0)  distance.x -= initialVelocity.x;
        lastHorizontalMove = 0;

    }

    public void moveRight(){

        moveRightBy(initialVelocity.x);
        if(!stopMoving)  distance.x += initialVelocity.x;
        lastHorizontalMove = 1;
    }

    public void moveDown(){

        moveDown(initialVelocity.y);
        System.out.println(initialVelocity.y);
        if(!stopMoving)  distance.y += initialVelocity.y;

        lastVerticalMove = 2;
    }

    public void moveUp(){

        moveUp(initialVelocity.y);

        //If object is not moving, then is at boundary
        //therefore we must record distance move.
        if(!stopMoving) distance.y -= initialVelocity.y;

        lastVerticalMove = 3;
    }

    public boolean isMoving(){

        for(int i = 0; i<movements.length; i++) {
            if (movements[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean lastMoveLeft(){ return lastHorizontalMove == 0; }
    public boolean lastMoveRight(){
        return lastHorizontalMove == 1;
    }
    public boolean lastMoveUp(){
        return lastVerticalMove == 3;
    }
    public boolean lastMoveDown(){ return lastVerticalMove  == 2; }

    public boolean isMovingLeft(){ return movements[0]; }
    public boolean isMovingRight(){ return movements[1]; }
    public boolean isMovingDown(){ return movements[2]; }
    public boolean isMovingUp(){ return movements[3]; }


    //MARK: Getters and Setters
    //-----------------------------------------------------------------------------------------------------------------//

    /**
     * The getCurrent method
     * @return The index of the animation being shown
     */
    public int getCurrent(){
        return current;
    }

    public char getId(){
        return id;
    }

    public MovingImage getCopy() {
        MovingImage newSprite = new MovingImage(this);
        return newSprite;
    }

    public void setScale(float scale) {
        this.scale = scale;
        w *= scale;
        h *= scale;
    }

    public float getVxi(){
        return initialVelocity.x;
    }

    public float getVyi(){
        return initialVelocity.y;
    }

    public float getVxf(){
        return finalVelocity.x;
    }

    public float getVyf(){
        return finalVelocity.y;
    }

    public float getAx(){
        return acceleration.x;
    }

    public float getAy(){
        return acceleration.y;
    }

    //TODO: Not used.

    public boolean isLeftBoundary(){
        float xOffset = coordinate.x - initialVelocity.x;
        return leftBoundary >= xOffset;
    }
    public boolean isRightBoundary(){
        float xOffset = coordinate.x + initialVelocity.x;
        return rightBoundary <= xOffset;
    }

    public boolean isDownBoundary(){
        float yOffset = coordinate.y + initialVelocity.y;
        return yOffset-1 >= downBoundary;
    }

    public boolean isUpBoundary(){
        float yOffset = coordinate.y - initialVelocity.y;
        return upBoundary>= yOffset;
    }

    public Point distance(){
        return new Point(distance);
    }
}


