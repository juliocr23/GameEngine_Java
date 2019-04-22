package animation;


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
    public Point finalVelocity = new Point(5,7);
    public Point acceleration  = new Point(1,1);

    private float wScale = 1;
    private float hScale = 1;
    private int current = 0;      //The index of the animation being shown
    private BufferedImage image;  //The image being display
    public  String imageFormat = "png";
    public String filePath[];

    private int lastHorizontalMove = Movement.RIGHT;
    private int lastVerticalMove = Movement.DOWN;

    public boolean[] movements = new boolean[4]; //Track sprite movements
    private char id; //Sprite id

    public float leftBoundary   =0;
    public float rightBoundary  =0;
    public float downBoundary   =0;
    public float upBoundary     =0;


    /**
     * Constructor
     * @param filePaths  An array containing the file paths for the animation of the sprite, where [0] is
     *                   the path to when the animation is idle and [1] when animation is walking.
     * @param duration   An array containing the duration for each animation
     */

    public MovingImage(String[] filePaths, int[] duration, char id) {

        //TODO: setLocation(0,0);       //Set location of the sprite
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

        width  = (int) (image.getWidth() * wScale);
        height = (int) (image.getHeight() * hScale);
    }

    public MovingImage(String filePath, char id) {
        try {
            this.id = id;
            image = ImageIO.read(new File(filePath + "." + imageFormat));
            width  = (int)(wScale *image.getWidth());
            height = (int) hScale *image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public MovingImage(MovingImage other) {

        id = other.getId();

        initialVelocity = new Point(other.initialVelocity);
        finalVelocity   = new Point(other.finalVelocity);
        acceleration    = new Point(other.acceleration);

        wScale = other.wScale;
        hScale = other.hScale;
        current = other.current;
        image   = other.image;
        width      = other.width;
        height     = other.height;

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
        paint(g,x,y);
    }

    public void draw(Graphics g, Point offset) {
        paint(g, x + offset.x,y + offset.y);
    }

    public void draw(Graphics g, int x, int y) {
       paint(g,x,y);
    }

    private void paint(Graphics g, int x, int y){
        var imgWidth =  (int) (image.getWidth() * wScale);
        var imgHeight = (int) (image.getHeight() * hScale);
        g.drawImage(image,x,y, imgWidth, imgHeight,null);

        g.setColor(Color.red);
        g.drawRect(x,y, width, height);
    }


    //MARK: Movements
    //-----------------------------------------------------------------------------------------------------------------//
    public void moveRight(){
        moveRightBy(initialVelocity.x);
        lastHorizontalMove = Movement.RIGHT;
    }

    public void moveLeft(){
        moveLeftBy(initialVelocity.x);
        lastHorizontalMove = Movement.LEFT;

    }

    public void moveUp(){

        moveUpBy(initialVelocity.y);
        lastVerticalMove = Movement.UP;
    }

    public void moveDown(){

        moveDownBy(initialVelocity.y);
        lastVerticalMove = Movement.DOWN;
    }

    public void moveLeftBy(float dx){
        x -= dx;
    }

    public void moveRightBy(float dx) {
        x += dx;
    }

    public void moveUpBy(float dy){
        y -= dy;
    }

    public void moveDownBy(float dy) {
        y += dy;
    }

    public boolean isMoving(){

        for(int i = 0; i<movements.length; i++) {
            if (movements[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean lastMoveRight(){
        return lastHorizontalMove == Movement.RIGHT;
    }
    public boolean lastMoveLeft(){ return lastHorizontalMove == Movement.LEFT; }
    public boolean lastMoveUp(){
        return lastVerticalMove == Movement.UP;
    }
    public boolean lastMoveDown(){ return lastVerticalMove  == Movement.DOWN; }

    public boolean isMovingRight(){ return movements[0]; }
    public boolean isMovingLeft(){ return movements[1]; }
    public boolean isMovingUp(){ return movements[2]; }
    public boolean isMovingDown(){ return movements[3]; }


    public void setFacingPosition(int position) {
        lastHorizontalMove = position;
    }

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

    public void setScale(float wScale, float hScale) {
        this.wScale = wScale;
        this.hScale = hScale;

        width  *= wScale;
        height *= hScale;
    }

    public  float getXOffset(){

        float tempVx  =  initialVelocity.x + acceleration.x <= finalVelocity.x ?
                         initialVelocity.x + acceleration.x : initialVelocity.x;

        return tempVx;
    }

    public float getYOffset(){

        float tempVy =   initialVelocity.y + acceleration.y <= finalVelocity.y ?
                         initialVelocity.y + acceleration.y : initialVelocity.y;

        return tempVy;
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
//
//    public Point distance(){
//        return new Point(distance);
//    }
}


