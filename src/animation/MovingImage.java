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
    public Point finalVelocity = new Point(5,5);
    public Point acceleration  = new Point(1,1);

    private float wScale = 1;
    private float hScale = 1;
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

        var imgWidth =  (int) (image.getWidth() * wScale);
        var imgHeight = (int) (image.getHeight() * hScale);
        g.drawImage(image,(int)getX(),(int) getY(), imgWidth, imgHeight,null);

        g.setColor(Color.red);
        g.drawRect((int)getX(),(int) getY(), width, height);
    }

    //MARK: Movements
    //-----------------------------------------------------------------------------------------------------------------//
    public void moveLeft(){

        x -=  initialVelocity.x;

        float offset = distance.x - initialVelocity.x;

        if(!stopMoving && offset>=0)  distance.x -= initialVelocity.x;
        lastHorizontalMove = 0;

    }

    public void moveRight(){

         x += initialVelocity.x;
        if(!stopMoving)  distance.x += initialVelocity.x;
        lastHorizontalMove = 1;
    }

    public void moveDown(){

        y += initialVelocity.y;
        System.out.println(initialVelocity.y);
        if(!stopMoving)  distance.y += initialVelocity.y;

        lastVerticalMove = 2;
    }

    public void moveUp(){

       y -= initialVelocity.y;

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

    public void setScale(float wScale, float hScale) {
        this.wScale = wScale;
        this.hScale = hScale;

        width  *= wScale;
        height *= hScale;
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

    public Point distance(){
        return new Point(distance);
    }
}


