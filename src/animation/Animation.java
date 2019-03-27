//
//  Animation.java
//  Game Engine
//
//  Created by Julio Rosario on 2/8/19.
//  Copyright © 2019 Julio Rosario. All rights reserved.
//


package animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Animation {

    private ArrayList<Drawing> image; //Contains the images that makeup the animation
    private int current = 0;
    private int delay;
    private int initialDelay;

    /**
     * no-args Constructor
     */
    public Animation(){

        image = new ArrayList<>();

        current = 0;
        delay   = 0;

        initialDelay = 0;
    }

    /**
     * Constructor
     * @param path The filepath to where the image files are located
     * @param format The format of the images
     * @param duration The duration for the animation
     */

    public Animation(String path, String format, int duration) throws IOException {

        this.initialDelay = duration;
        delay = duration;
        image = new ArrayList<>();

        File file = new File(path);
        File parentFile = file.getParentFile();

        if (parentFile.exists()) {

            int numberOfImages = parentFile.listFiles().length;
            for (int i = 0; i < numberOfImages; i++) {
                String fileName = path + i + format;
                image.add(new Drawing(fileName));
            }
        } else {
            throw new IOException("File doesn't exist");
        }
    }

    /**
     * The update method updates the animation
     * once the animation is over it reset the animation.
     */
    public void update(){
        if(isAnimationOver())
            reset();
    }

    /**
     * The setTime method set the duration for the animation
     * @param time The time to display the image for.
     */
    public void setDuration(int time){
        initialDelay = time;
        delay        = time;
    }

    public void setInitialDelay(int d){
        initialDelay += d;
        delay        += d;
    }

    /**
     * The nextImage method goes to the next image in the animation
     * @return The next image in the animation
     */
    public BufferedImage nextImage() {

        if(current != image.size()-1) {
            if (delay == 0) {
                current++;

                delay = initialDelay;
            } else
                delay--;
        }
        return image.get(current).image;
    }

    public void reset(){
        current = 0;
    }

    /**
     * The previousImage method goes to the previous image in the animation
     * @return The previous image in the animation
     */
    public BufferedImage previousImage() {

        if (current > 0){
            if (delay == 0) {
                current--;
                delay = initialDelay;
            } else
                delay--;
        }
        return image.get(current).image;
    }

    /**
     * The getImage method
     * @return The image that is supposed to be display
     */

    public BufferedImage getImage(){ return image.get(current).image;}

    public BufferedImage getMirrorImage(){
       return image.get(current).mirrorImage;
    }

    /**
     * The isAnimationOver method
     * @return True if animation is over otherwise it returns false
     */
    public boolean isAnimationOver(){ return current == image.size()-1; }

    /**
     * The getIndex method
     * @return The index of the image of the animation being display
     */
    public int getIndex(){
        return current;
    }

    /**
     * The getNumberOfImages method
     * @return The number of images in the animation
     */
    public int getNumberOfImages(){ return image.size(); }

    /**
     * The addImage method add an image to the animation
     * @param image The image to be added to the animation
     */
    public void addImage(BufferedImage image){ this.image.add(new Drawing(image));}

    /**
     * The addImage method adds an image from a file path to
     * the animation
     * @param file The file of the image to be add it to the
     *             animation
     */

    public void addImage(String file){
        image.add(new Drawing(file));
    }

    public int getCurrent() {
        return current;
    }


    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        initialDelay = delay;
    }
}
