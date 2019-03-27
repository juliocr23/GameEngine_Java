package animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawing {

   public BufferedImage image;
   public BufferedImage mirrorImage;

   public Drawing(String fileName) {
       try {
           image = ImageIO.read(new File(fileName));
           mirrorImage = mirrorImg(image);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   public Drawing(BufferedImage image) {
      this.image = getNewImage(image);
      mirrorImage = mirrorImg(image);
   }

    /**
     * The getNewImage method copy an image
     * @param image The image to be copy
     * @return The new copy image
     */
    public static BufferedImage getNewImage(BufferedImage image){

        int height = (image.getHeight(null));
        int width =  (image.getWidth(null));

        BufferedImage newImg = new BufferedImage(width, height, ((BufferedImage)image).getType());
        Graphics2D g2 = newImg.createGraphics();

        g2.drawImage(image,0,0,width,height,null);
        g2.dispose();

        return newImg;
    }

    /**
     * The mirrorImg method creates a mirror image
     * @param image The image to be mirror
     * @return The mirror image
     */
    public static BufferedImage mirrorImg(BufferedImage image){
        int height = (image.getHeight(null));
        int width =  (image.getWidth(null));

        BufferedImage newImg = new BufferedImage(width, height, ((BufferedImage)image).getType());
        Graphics2D g2 = newImg.createGraphics();

        g2.drawImage(image,width,0,-width,height,null);
        g2.dispose();

        return newImg;
    }

    public int getWidth(){
        return image.getWidth();
    }

    public int getHeight(){
        return image.getHeight();
    }
}
