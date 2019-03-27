package animation;

import java.awt.*;

//TODO: Comment what each method is supposed to do.

public class Background {

    private ImageLayer[] background;  //the image layers of the background
    private int width;                //The width of the image
    private int height;               //The height of the image


    /**
     * Constructor
     * @param width  The width of the images
     * @param height The height of the images
     * @param file   The file path of the images
     * @param numberOfImages The number of images
     */
    public Background(int width, int height, String file,int numberOfImages){
        this.width = width;
        this.height = height;
        setBackground(file,numberOfImages);
    }

    /**
     * The setBackground method set the
     * @param images The layered images used for the background
     * @param numberOfImages The number of images located at the path
     */
    private void setBackground(String images, int numberOfImages){
        try {
            background = new ImageLayer[numberOfImages];
            for(int i = 1; i<=numberOfImages; i++) {
                background[i - 1] = new ImageLayer(images + i + ".png",
                                                    0, 0,i ,
                                                     width, height);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * The draw method draws the background
     * @param g The graphics used to draw to the background
     */
    public void draw(Graphics g){
            for (int i = background.length-1; i >=0; i--)
                background[i].draw(g);
    }

    /**
     * The moveLeftBy method move the background to the left
     * by the specify dx
     * @param dx The distance to move the background left by in pixels.
     */
    public void moveLeftBy(int dx){
        for(int i = 0; i<background.length; i++)
            background[i].moveLeftBy(dx);
    }

    /**
     * The moveRightBy method move the background to the right
     * by the specify dx
     * @param dx The distance to move the background right by in pixels.
     */
    public void moveRightBy(int dx){
        for(int i = 0; i<background.length; i++)
            background[i].moveRightBy(dx);
    }

    /**
     * The moveUpBy method move the background up
     * by the specify dy
     * @param dy The distance to move the background up by in pixels.
     */
    public void moveUpBy(int dy){
        for(int i = 0; i<background.length; i++)
            background[i].moveUpBy(dy);
    }

    /**
     * The moveDownBy method move the background down
     * by the specify dy
     * @param dy The distance to move the background down  by in pixels.
     */
    public void moveDownBy(int dy){
        for(int i = 0; i<background.length; i++)
            background[i].moveDownBy(dy);
    }

    /**
     * The reset method reset the animation of the background
     */
    public void reset(){
        for(int i = 0; i<background.length; i++)
             background[i].reset();
    }

    public int getX(int i){
        return background[i].getX();
    }

    public int getY(int i){
        return background[i].getY();
    }

    public int getSize(){
        return background.length;
    }

    public boolean isNotLeftEnd(){
        boolean flag = true;

        for(int i = 0; i<background.length; i++) {
            if (background[i].getX() > 0){
                flag = false;
                break;
            }
        }
        return flag;
    }
}
