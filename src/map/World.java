package map;

import animation.Movement;
import animation.MovingImage;
import display.Display;
import test.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import static java.lang.StrictMath.abs;

/**
 * digit keys: players
 * Uppercase letter: Tiles
 * Lower case letter: power up like coins, ...
 * other symbols: enemies
 *
 *
 * TODO: Map cannot get pass the size the of the array. when is at end player can move to the right of the screen.
 *       Without going off of it.
 */

public class World {

    private static  MovingImage[][] elements;           //The objects on the map
    private static  MovingImage player;
    private static  String[] elementsFilePaths;         // The file paths to where the images of the elements are
                                                        // allocated

    private static  ArrayList<String> mapCode;
    private static  int count = 0;                      //Keeps a count on the maps
    private static  String mapFiles;
    private static  Point offset;                    //The location of the map

    private static int mapHeight;
    private static int mapWidth;
    private static int elementWidthAvg;
    private static int elementHeightAvg;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;

    private boolean isGravity = true;

    private int jumpOffset = 0;
    private int jumpHeight = 155;
    private boolean startJump = false;

    private boolean tileMoved = false;

    private Movement movement;

    /**
     * Constructor
     * @param maps The filepath for the maps. The map should have the same name but
     *             numbered from 0  up to the number of maps for example map0, map1, map2 etc.
     *             Path should looks like e.g resources/maps/name where name is the name of the map without the
     *             number. The display will be adjusted to the map height.
     */

    public World(String maps, Hashtable<Character,MovingImage> items) throws IOException {

        //Create file
        File file = new File(maps);
        File parentFile = file.getParentFile();

        if (parentFile.exists()) {

            mapFiles = maps;

            mapCode     = getFileContent(maps+count+ ".txt");
            int cols    = getLongestLine(mapCode);
            int rows    = mapCode.size();

            elements = new MovingImage[rows][cols];
        } else  {
            throw new IOException("File doesn't exist");
        }

        addElements(items);
        Display.height = mapHeight;

        offset = new Point();
        System.out.println("Map Height: " + mapHeight);
        System.out.println("Map Width: " +  mapWidth);

        System.out.println("element height: " +  elementHeightAvg);
        System.out.println("element width: " +  elementWidthAvg);
    }

    //MARK:INPUTS
    //--------------------------------------------------------------------------------------------------------------------------//
    public void processInput(){

       moveLeft  = Game.keyboard.keyDown(KeyEvent.VK_LEFT);
       moveRight = Game.keyboard.keyDown(KeyEvent.VK_RIGHT);

       if(!moveUp)
            moveUp = Game.keyboard.keyDownOnce(KeyEvent.VK_UP);

       player.movements[0] = moveLeft;
       player.movements[1] = moveRight;
       player.movements[2] = moveDown;
       player.movements[3] = moveUp;

    }

    //MARK: Updates
    //--------------------------------------------------------------------------------------------------------------------------//
    public void update(){

        //System.out.println(player);

        //Represent right, left, up and down.
        Pair result[] = new  Pair[4];
        Pair temp1;

        System.out.println(offset);

        player.x += abs(offset.x);
        player.y += abs(offset.y);

        //Update elements.
        for(int row = 0; row<elements.length; ++row) {
            for(int col = 0; col<elements[row].length; ++col) {

                if (!isEmpty(row,col)) {

                    if(!isPlayer(row,col)) {

                         if(isGravity) {
                           temp1  = isBottomCollision(row, col);
                           if (!temp1.isEmpty()) result[Movement.DOWN] = temp1;
                         }

                        if(moveRight) {
                             temp1 = isRightCollision(row,col);
                             if(!temp1.isEmpty()) result[Movement.RIGHT] = temp1;
                             System.out.println(temp1);
                        }

                        if(moveLeft ) {
                            temp1 = isLeftCollision(row,col);
                            if(!temp1.isEmpty()) result[Movement.LEFT] = temp1;
                        }

                        if(moveUp) {
                            temp1 = isTopCollision(row,col);
                            if(!temp1.isEmpty()) result[Movement.UP] = temp1;
                        }
                    }
                    elements[row][col].update();
                }
            }
        }

        player.x -= abs(offset.x);
        player.y -= abs(offset.y);

        updatePlayer(result);
        updateTiles(result);
    }

    private void updatePlayer(Pair collision[]) {

       updateMoveRight(collision[Movement.RIGHT]);

       updateMoveLeft(collision[Movement.LEFT]);

       if(collision[Movement.DOWN] != null && moveUp) //If is on tile and want to jump, jump can start. Ps. Cannot jump on air.
            startJump = true;

       updateMoveUp(collision[Movement.UP]);

       updateMoveDown(collision[Movement.DOWN]);
    }

    private void updateMoveRight(Pair collision){

        if(moveRight)
            player.setFacingPosition(Movement.RIGHT);

        if(moveRight) {
           if(isPlayerEndScreen()) {
               player.x = Display.width - player.width - 2;
           }
           else if(isPlayerMidScreen() && !isEndOfMap()) {
               player.x =  (Display.width/2 - player.width) -1;
           }
           else if(collision !=  null) {
               int  r = collision.row;
               int  c = collision.col;
               player.x = abs(elements[r][c].x - player.width - 1);
           } else {
               player.moveRight();
           }
       }
    }

    private void updateMoveLeft(Pair collision){

        if(moveLeft)
            player.setFacingPosition(Movement.LEFT);

        if(moveLeft){

            if(collision != null) {
                int r = collision.row;
                int c = collision.col;
                player.x = abs(elements[r][c].x + elements[r][c].width + 1);
            }
            else if(isPlayerStartScreen()) {
                player.x = 0;
            }
            else if(offset.x > 0) {
                player.moveLeft();
            }
        }
    }

    private void updateMoveUp(Pair collision){
        if(startJump){

            if(jumpOffset <= jumpHeight && collision == null) {

                player.moveUp();
                jumpOffset += player.getVyi() + player.getAy();
                isGravity = false;
            }
            else {
                isGravity = true;
                startJump = false;
                moveUp = false;
            }
        }
    }

    private void updateMoveDown(Pair collision){
        if(isGravity) {
            if (collision == null) {
                player.moveDown();
            } else {
                int r = collision.row;
                int c = collision.col;
                player.y = abs(elements[r][c].y - player.height - 1);
                jumpOffset = 0;
                moveUp = false;
            }
        }
    }


    public void updateTiles(Pair collision[]){


        if(!isPlayerMidScreen())
            return;

        int r, c;
        if(moveRight) {
            if(collision[Movement.RIGHT] != null) { //There is collision can't move
                r = collision[Movement.RIGHT].row;
                c = collision[Movement.RIGHT].col;
                offset.x =  (elements[r][c].x -  (Display.width/2)) *-1;
            }
            else if(isEndOfMap()) {  //Is the end of the map can't move
                offset.x  = (getMapWidth()- Display.width)* -1;
            }
            else { //There is no collision map can move
                offset.x -= player.getXOffset();
            }
        }

        if(moveLeft) {

            if(collision[Movement.LEFT] != null) { //There is collision can't move
                r = collision[Movement.LEFT].row;
                c = collision[Movement.LEFT].col;
                offset.x = ((elements[r][c].x -  Display.width/2) + (elements[r][c].width + player.width) + 2) * -1;
            }
            else  if(offset.x < 0) {
                offset.x += player.getXOffset();
            }
            else {
                offset.x = 0;
            }
        }
    }

    //MARK: Rendering
    //--------------------------------------------------------------------------------------------------------------------------//
    public void draw(Graphics g) {

        int r = getStartingRow();
        int c = getStartingCol();

        player.draw(g);
        for(int i = r;  getRow(i) < Display.height; i++){
            for(int j = c; getCol(j) < Display.width; j++){
                if (!isEmpty(i,j)) {
                    if(!isPlayer(i,j)) {
                        elements[i][j].draw(g,offset);
                    }
                }
            }
        }
    }

    //MARK: Collision
    //--------------------------------------------------------------------------------------------------------------------------//
    //&*
    public Pair isRightCollision(int row, int col) {

        if(exist(row, col) && isLeftSide(row, col))
            return new Pair(row, col);

        else return new Pair();
    }

    //*&
    public Pair isLeftCollision(int row, int col) {
        if(exist(row,col) && isRightSide(row,col))
            return new Pair(row,col);
        else
            return new Pair();
    }

    //  *
    //  &
    public Pair isTopCollision(int row, int col) {
        if( exist(row , col) && isBottomSide(row, col))
            return new Pair(row,col);

        else return new Pair();
    }

    public Pair isBottomCollision(int row, int col) {
        if(exist(row,col) && isTopSide(row,col))
            return new Pair(row,col);
        else
            return new Pair();
    }

    public boolean isLeftSide(int row, int col) {

        float xOffset = player.getVxi() + player.getAx();
        boolean pConner1 = player.contains(leftTopConner(row,col, -xOffset, 0));
        boolean pConner2 = player.contains(leftBottomConner(row,col, -xOffset, 0));

        boolean eConner1 = elements[row][col].contains(rightTopConner(player,xOffset,0));
        boolean eConner2 = elements[row][col].contains(rightBottomConner(player,xOffset,0));

        return pConner1 || pConner2 || eConner1 || eConner2;
    }

    public boolean isRightSide(int row, int col) {

        float xOffset = player.getVxi() + player.getAx();
        boolean conner1 = player.contains(rightTopConner(row,col, xOffset, 0));
        boolean conner2 = player.contains(rightBottomConner(row,col, xOffset, 0));

        boolean eConner1 = elements[row][col].contains(leftTopConner(player,-xOffset,0));
        boolean eConner2 = elements[row][col].contains(leftBottomConner(player,-xOffset,0));

        return conner1 || conner2 || eConner1 || eConner2;
    }

    public boolean isTopSide(int row, int col) {

        float yOffset = player.getVyi() +  player.getAy();

        boolean pConner1 = player.contains(leftTopConner(row,col, 0, -yOffset));
        boolean pConner2 = player.contains(rightTopConner(row,col, 0, -yOffset));

        boolean eConner1 = elements[row][col].contains(leftBottomConner(player,0,yOffset));
        boolean eConner2 =  elements[row][col].contains(rightBottomConner(player,0,yOffset));

        return pConner1 || pConner2 || eConner1 || eConner2;
    }

    public boolean isBottomSide(int row, int col) {

        float yOffset = player.getVyi() +  player.getAy();

        //Object could be bigger than player.
        boolean pConner1 = player.contains(rightBottomConner(row,col, 0, yOffset));
        boolean pConner2 = player.contains(leftBottomConner(row,col, 0,yOffset));

        boolean eConner1 = elements[row][col].contains(rightTopConner(player,0,-yOffset));
        boolean eConner2 =  elements[row][col].contains(leftTopConner(player,0,-yOffset));

        return pConner1 || pConner2 || eConner1 || eConner2;
    }

    public Point leftTopConner(int row, int col, float xOffset, float yOffset) {

        int x =(int) (elements[row][col].x + xOffset);
        int y =(int) (elements[row][col].y + yOffset);
        return  new Point(x,y);
    }

    public Point leftTopConner(MovingImage sprite, float xOffset, float yOffset) {

        int x =(int) (sprite.x + xOffset);
        int y =(int) (sprite.y + yOffset);
        return  new Point(x,y);
    }

    public Point rightTopConner(int row, int col, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int) (elements[row][col].x + elements[row][col].width + xOffset);
        conner.y = (int) (elements[row][col].y + yOffset);

        return conner;
    }

    public Point rightTopConner(MovingImage sprite, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int) (sprite.x + sprite.width + xOffset);
        conner.y = (int) (sprite.y + yOffset);

        return conner;
    }

    public Point leftBottomConner(int row, int col, float xOffset, float yOffset) {
        Point conner = new Point();
        conner.x = (int) (elements[row][col].x + xOffset);
        conner.y = (int) (elements[row][col].y + elements[row][col].height + yOffset);

        return conner;
    }

    public Point leftBottomConner(MovingImage sprite, float xOffset, float yOffset) {
        Point conner = new Point();
        conner.x = (int) (sprite.x + xOffset);
        conner.y = (int) (sprite.y + sprite.height + yOffset);

        return conner;
    }

    public Point rightBottomConner(int row, int col, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int)(elements[row][col].x + elements[row][col].width + xOffset);
        conner.y = (int) (elements[row][col].y + elements[row][col].height + yOffset);

        return conner;
    }

    public Point rightBottomConner(MovingImage sprite, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int)(sprite.x + sprite.width + xOffset);
        conner.y = (int) (sprite.y + sprite.height + yOffset);

        return conner;
    }

    //MARK: File Parsing
    //--------------------------------------------------------------------------------------------------------------------------//
    /**
     * The getFileContent method parse the map file
     * @param fileName A text file containing the path to file
     * @return An ArrayList  containing the context read from the txt file
     */

    private ArrayList<String> getFileContent(String fileName){

        ArrayList<String> context = new ArrayList<>();
        File file = new File(fileName);

        try {
            Scanner read = new Scanner(file);
            String line;
            while (read.hasNext()){

                line = read.nextLine();
                if(!line.startsWith("#")){
                    context.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return context;
    }

    /**
     * The getLongestLine method find the longest line
     * in an ArrayList<String>()
     * @param context An ArrayList type string
     * @return The length of the longest line in the ArrayList
     */

   private int getLongestLine(ArrayList<String> context){
        int longest = 0;
        int temp;
        for(int i = 0; i<context.size(); i++){
            temp = context.get(i).length();

            if(longest < temp) longest = temp;
        }
        return longest;
    }


    //MARK: Method Loaders
    //--------------------------------------------------------------------------------------------------------------------------//

    /**
     * The addElements method add elements to the current map.
     * @param items The elements to be added on the map.
     *              Each element on the array should be unique.
     *              copies will be made to match those on the file map.
     */
    private void addElements(Hashtable<Character,MovingImage> items) {

        int width = 0;
        int height = 0;

        String lineOfCode;
        char   obj;

        for(int row = 0; row< elements.length; row++){

            lineOfCode = mapCode.get(row);
            for(int col = 0; col<lineOfCode.length(); col++){

                obj = lineOfCode.charAt(col);
                MovingImage sprite = items.get(obj);

                if (sprite != null) {

                    if (isPlayer(obj))    {
                        elements[row][col] = sprite;
                        player = sprite;
                    }

                    else if (isElement(obj)) elements[row][col] = sprite.getCopy();
                    width  += sprite.width;
                    height += sprite.height;
                }else  {
                    System.out.println("Error " + obj + " Doesn't exist in Hashtable");
                }
            }
        }

        elementWidthAvg = width/elements[0].length;
        elementHeightAvg = height/elements[0].length;

        mapHeight =  elementWidthAvg*elements.length;
        mapWidth  =  elementHeightAvg*elements[0].length;

        addLocationToElements();
    }

    private void addLocationToElements(){
        for(int row = 0; row< elements.length; row++){
            for(int col = 0; col<elements[row].length; col++){

                if (!isEmpty(row,col)) {
                    elements[row][col].x = (int) getXFor(row,col);
                   // elements[row][col].distance.x   = (int) getXFor(row,col);

                  //  elements[row][col].distance.y   = (int) getYFor(row,col);
                    elements[row][col].y = (int)getYFor(row,col);

                    if(isPlayer(row,col)) {
                        //TODO: Not needed
                        float playerWidth = elements[row][col].width;
                        elements[row][col].rightBoundary = (Display.width/2-playerWidth);
                        elements[row][col].leftBoundary  = 0;
                        elements[row][col].upBoundary    = 0;
                        elements[row][col].downBoundary  = getYFor(row,col);
                    }
                }
            }
        }
    }

    //Map methods
    //----------------------------------------------------------------------------------------------------------------//

    private int getStartingCol(){
        return  Math.max(0,Math.abs(offset.x)/elementWidthAvg);
    }

    private int getStartingRow(){
        return  Math.max(0,Math.abs(offset.y)/elementHeightAvg);
    }

    private int getEndingRow(){
        return elements.length;
    }

    private int getEndingCol(){
        return elements[0].length;
    }

    private int getRow(int i){
        return (i*elementHeightAvg+offset.y);
    }

    private int getCol(int j){
        return (j*elementWidthAvg+offset.x);
    }

    private boolean isEndOfMap(){
        int mapWidth = elements[0].length*elementWidthAvg;
        int nowWidth =  (int)(abs(offset.x) + player.getXOffset() + Display.width);

        return nowWidth >= mapWidth;
    }

    private int getMapWidth(){
        return elements[0].length*elementWidthAvg;
    }

    //Player methods
    //----------------------------------------------------------------------------------------------------------------//
    private int getPlayerRow(){

        //Player's height could be > elementHeightAvg
        float hOffset = 0;
        if(player.height > elementHeightAvg)
            hOffset = player.height-elementHeightAvg;

        return  Math.round((player.y+hOffset)/elementHeightAvg);
    }

    private int getPlayerCol(){

        float wOffset = 0;
        if(player.width > elementWidthAvg)
            wOffset = player.width-elementWidthAvg;


        return Math.round((player.x+wOffset)/elementWidthAvg);
    }

    private boolean isPlayerMidScreen(){
        return (player.getX() + player.getXOffset()) >= (Display.width/2 - player.width);
    }

    private boolean isPlayerEndScreen(){
       return  (player.x + player.getXOffset()) >= (Display.width - player.width);
    }

    private boolean isPlayerStartScreen(){
       return  (player.getX()-player.getXOffset()) < 0;
    }

    //Helpers
    //----------------------------------------------------------------------------------------------------------------//
    private boolean isTile(char letter) {
        return Character.isAlphabetic(letter) && Character.isUpperCase(letter);
    }

    private boolean isTile(int row, int col) {
        return isTile(elements[row][col].getId());
    }

    private boolean isPlayer(char letter) {
       return Character.isDigit(letter);
    }

    private boolean isPlayer(int row, int col) {
        return isValid(row,col) && !isEmpty(row,col) && isPlayer(elements[row][col].getId());
    }

    private boolean isPowerUp(char letter) {
        return Character.isAlphabetic(letter) && Character.isLowerCase(letter);
    }

    private boolean isPowerUp(int row, int col) {
        return isPowerUp(elements[row][col].getId());
    }

    private boolean isEnemy(char letter) {
       return !isTile(letter) && !isPlayer(letter) && !isPowerUp(letter) && !Character.isSpaceChar(letter);
    }

    private boolean isEnemy(int row, int col) {
        return isEnemy(elements[row][col].getId());
    }

    private  boolean isElement(char letter) {
       return  isTile(letter) || isPowerUp(letter) || isPlayer(letter) || isEnemy(letter);
    }

    public int getMapHeight(){
        return mapHeight;
    }

    public float getYFor(int row, int col) {
        return   (row * elementHeightAvg) - hOffset(row,col);
    }

    public float getXFor(int row, int col) {
        return (col * elementWidthAvg)-wOffset(row,col);
    }

    public float hOffset(int row, int col){
        int hOffset = 0;
        if(elements[row][col].height > elementHeightAvg)
            hOffset = (int)elements[row][col].height-elementHeightAvg;

        return hOffset;
    }

    public float wOffset(int row, int col) {
        int wOffset = 0;
        if(elements[row][col].width > elementWidthAvg)
            wOffset = (int)elements[row][col].width-elementWidthAvg;

        return wOffset;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < elements.length && col>= 0 && col <elements[0].length;
    }

    public boolean isEmpty(int row, int col) {
        return elements[row][col] == null;
    }

    public  boolean exist(int row, int col) {
        return isValid(row,col) && !isEmpty(row,col);
    }

    public boolean mapOriginal(){

        for(int row = 0; row <elements.length; row++) {
            for(int col = 0; col<elements[0].length; col++){

                if(!isEmpty(row,col))
                    if(elements[row][col].getX() < 0 || elements[row][col].getY() < 0)
                        return false;
            }
        }
        return true;
    }

    //TODO: Testing methods
    private void printElements(){
        for(int i = 0; i<elements.length; i++) {
            for(int j = 0; j<elements[i].length; j++){
                if(elements[i][j] != null) {
                    if(elements[i][j] == player)
                        System.out.print("&");
                    else
                        System.out.print("*");

                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}

class Pair{
    int row;
    int col;

    Pair(){
        row = -1;
        col = -1;
    }

    Pair(int r, int c) {
        row = r;
        col = c;
    }

    public Pair(Pair other) {
        this.row = other.row;
        this.col = other.col;
    }
    
   public boolean isEmpty(){
        return row == -1 && col == -1;
    }

    @Override
    public String toString() {
       return "r: " + row + " c: " + col + "\n";
    }
}



