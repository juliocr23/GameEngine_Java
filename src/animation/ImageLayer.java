package animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class ImageLayer
{
  private Image image;

  private int x; // The variable used to move the image to the left and right
  private int y; // The variable used to move the image down and up
  private int z; // The variable used to give depth to the image

  //Used for resetting the animation
  private int originalX;
  private int originalY;
  private int originalZ;

  private int screenWidth;  //The width of the screen
  private int screenHeight; //The height of the screen


    /**
     * Constructor
     * @param file The filepath of the image
     * @param x    The x coordinate of the image
     * @param y    The y coordinate of the image
     * @param z    The depth of the image (z coordinate)
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     */
   public ImageLayer(String file, int x, int y, int z,int screenWidth, int screenHeight)
   {
      try {
         image = ImageIO.read(new File(file));
      }catch (Exception e){
         System.out.println(e.getMessage());
      }

      this.x = x;
      this.y = y;
      this.z = z;

      originalX = x;
      originalY = y;
      originalZ = z;

      this.screenHeight = screenHeight;
      this.screenWidth = screenWidth;

   }

    /**
     * The draw method draws the image layer
     * @param g The graphics used to draw the image layer
     */
    public void draw(Graphics g) {
        for(int i = 0; i < 10; i++)
            g.drawImage(image, x/z + screenWidth*i, y,screenWidth,screenHeight, null);
    }

    //MARK: Functionality
   //------------------------------------------------------------------------------------------------------------//
   public void moveLeftBy(int dx) { x -= dx; }
   public void moveRightBy(int dx) { x += dx; }
   public void moveUpBy(int dy){ y += dy;}
   public void moveDownBy(int dy){ y-= dy;}

   //MARK: Getters and Setters
   //------------------------------------------------------------------------------------------------------------//
   public void setX(int x){this.x = x;}
   public void setY(int y){this.y = y;}
   public int getX(){
        return x;
    }
   public int getY(){
        return y;
    }
   public void setScreenWidth(int w){
        screenWidth = w;
    }
   public void setScreenHeight(int h){
        screenHeight = h;
    }

    /**
     * The reset method
     * reset the image layer to the original
     * x,y and z
     */
    public void reset(){
       x = originalX;
       y = originalY;
       z = originalZ;
   }
}