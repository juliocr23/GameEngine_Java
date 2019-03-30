package map;

import animation.MovingImage;
import display.Display;
import others.Point;
import test.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * digit keys: players
 * Uppercase letter: Tiles
 * Lower case letter: power up like coins, ...
 * other symbols: enemies
 *
 * //NOTE: COllision to the right is not working! check update method.
 */

public class World {

    private static  MovingImage[][] elements;           //The objects on the map
    private static  MovingImage player;
    private static  String[] elementsFilePaths;         // The file paths to where the images of the elements are
                                                        // allocated

    private static  ArrayList<String> mapCode;
    private static  int count = 0;                      //Keeps a count on the maps
    private static  String mapFiles;
    private static  Point location;                    //The location of the map

    private static int mapHeight;
    private static int mapWidth;
    private static int elementWidthAvg;
    private static int elementHeightAvg;

    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;


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
       moveUp    = Game.keyboard.keyDown(KeyEvent.VK_UP);
       moveDown  = Game.keyboard.keyDown(KeyEvent.VK_DOWN);

       player.movements[0] = moveLeft;
       player.movements[1] = moveRight;
       player.movements[2] = moveDown;
       player.movements[3] = moveUp;

    }

    //MARK: Updates
    //--------------------------------------------------------------------------------------------------------------------------//
    public void update(){

      //  System.out.println("Hello World!");
        //Get player's row and col
        int row = getPlayerRow();
        int col = getPlayerCol();

        System.out.println("r:" + row + " " + "c:" + col);

        //TODO: When it goes off the screen collision doesn't work!
        if(moveRight) {

            if(!isRightCollision(row,col))
                    player.moveRight();
        }

       else if(moveLeft){
            if(!isLeftCollision(row,col))
                player.moveLeft();
        }

       else if(moveUp){
            if(!isTopCollision(row,col))
                player.moveUp();
        }

       else if(moveDown)
            if(!isBottomCollision(row,col))
                player.moveDown();
        

        int startR =0;
        int startC =0;

        //Is drawing the whole matrix.
        //TODO: Only draw the portion that needs to be display.
        for(row = startR; row<elements.length; ++row) {
            for(col = startC; col<elements[row].length; ++col) {

                if (!isEmpty(row,col)) {

                    //Check
                    elements[row][col].update();

                    //TODO: There is an issue with the map when updating it.
                 //   updateMap(row,col);
                }
            }
        }
    }

    private void updatePlayer(int row, int col) {


        int newRow = getNewRow(row,col);
        int newCol = getNewCol(row,col);

        //TODO: Check that is not the
        if (isValid(newRow, newCol) && elements[newRow][newCol] == null ) {
            elements[newRow][newCol] = elements[row][col];
            elements[row][col] = null;
        }
    }

    private void updateMap(int row, int col){

        //Check that row, col is not player's coordinate
        //To move maps objects.
        if (!isPlayer(row, col)) {

            if(player.isRightBoundary()) { //Check that player is on constraint and
              if(!moveLeft(row,col)) player.stopMoving = true;
              else player.stopMoving = false;
            }

            if(player.isMovingLeft()) {
              if(!moveRight(row,col)) player.stopMoving = true;
              else player.stopMoving = false;
            }
        }
    }

    //MARK: Collision
    //--------------------------------------------------------------------------------------------------------------------------//
   //NOTE: It doesn't work when going above a tile. -> *_
    public boolean isRightCollision(int row, int col) {

        var isRightCollision = exist(row,col+1) && isOverLaps(row,col+1);

        var isTopRightCollision = exist(row-1,col+1) && isOverLaps(row-1,col+1);

        var isBottomRightCollision = exist(row+1,col+1) && isOverLaps(row+1,col+1);

        return isRightCollision || isTopRightCollision || isBottomRightCollision;
    }


    public boolean isLeftCollision(int row, int col) {

        var isLeftCollision       = exist(row,col-1) && isOverLaps(row,col-1);
        var isLeftTopCollision    = exist(row-1,col-1) && isOverLaps(row-1,col-1);
        var isLefBottomCollision  = exist(row+1,col-1) && isOverLaps(row+1,col-1);

        return isLeftCollision || isLeftTopCollision || isLefBottomCollision;
    }

    public boolean isTopCollision(int row, int col) {

        var isTopCollision      = exist(row-1,col) && isOverLaps(row-1,col);
        var isTopLeftCollision  = exist(row-1,col+1) && isOverLaps(row-1,col+1);
        var isTopRightCollision = exist(row-1,col-1) && isOverLaps(row-1,col-1);

        return  isTopCollision || isTopLeftCollision || isTopRightCollision;
    }

    public boolean isBottomCollision(int row, int col) {

        var isBottomCollision  = exist(row+1,col) && isOverLaps(row+1,col);
        var isBottomLeftCollision  = exist(row+1,col+1) && isOverLaps(row+1,col+1);
        var isBottomRightCollision = exist(row+1,col-1) && isOverLaps(row+1,col-1);

        return  isBottomCollision || isBottomLeftCollision || isBottomRightCollision;
    }

    private boolean isOverLaps(int row, int col){
        var result = player.overlaps(elements[row][col]);
        return  result;
    }

    //MARK: Type Alia for elements[row][col]
    public float y(int row, int col){
       return  elements[row][col].getY();
    }

    public float x(int row, int col){
        return elements[row][col].getX();
    }

    public float h(int row,int col){
        return elements[row][col].getH();
    }

    public float w(int row, int col){
        return elements[row][col].getW();
    }


    private boolean moveLeft(int row,int col){

        int c = getPlayerCol();
        int r = getPlayerRow();

        if(player.isMovingRight() &&  isEmpty(r,c+1) ) {
            elements[row][col].coordinate.x -= player.getVxi();
            return true;
        }
        else if(player.isMovingLeft()) {
            player.leftBoundary = Display.width / 2 + player.w;
            return true;
        } else {
            //TODO: When moving up and down player position for r is not changing.
            System.out.println(r);
            System.out.println(elements[r][c+1]);
        }

        return false;
    }

    public boolean moveRight(int row, int col) {

        int c = getPlayerCol();
        int r = getPlayerRow();

        if(mapOriginal()) {
            player.leftBoundary = 0;
            return true;
        }
        else if(isEmpty(r,c-1)) {
            elements[row][col].coordinate.x += player.getVxi();
            return true;
        }
        return false;
    }



    //MARK: Rendering
    //--------------------------------------------------------------------------------------------------------------------------//
    public void draw(Graphics g) {
        for(int row = 0; row< elements.length; row++){
            for(int col = 0; col<elements[row].length; col++){
                if (!isEmpty(row,col)) {
                    elements[row][col].draw(g);
                }
            }
        }
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


    //MARK: Functionality
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
                    width  += sprite.w;
                    height += sprite.h;
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
                    elements[row][col].coordinate.x = getXFor(row,col);
                    elements[row][col].distance.x   =  getXFor(row,col);

                    elements[row][col].distance.y   = getYFor(row,col);
                    elements[row][col].coordinate.y = getYFor(row,col);

                    if(isPlayer(row,col)) {
                        float playerWidth = elements[row][col].w;
                        elements[row][col].rightBoundary = (Display.width/2-playerWidth);
                        elements[row][col].leftBoundary  = 0;
                        elements[row][col].upBoundary    = 0;
                        elements[row][col].downBoundary  = getYFor(row,col);
                    }
                }
            }
        }
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
        return isPlayer(elements[row][col].getId());
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
        if(elements[row][col].h > elementHeightAvg)
            hOffset = (int)elements[row][col].h-elementHeightAvg;

        return hOffset;
    }

    public float wOffset(int row, int col) {
        int wOffset = 0;
        if(elements[row][col].w > elementWidthAvg)
            wOffset = (int)elements[row][col].w-elementWidthAvg;

        return wOffset;
    }


    public int getNewCol(int row, int col){

        Point d = elements[row][col].distance();
        float totalX =d.x+wOffset(row,col);
        return  (int)totalX/elementWidthAvg;
    }
    public int getNewRow(int row, int col){

        Point d = elements[row][col].distance();
        float totalY =d.y+hOffset(row,col);
        return  (int)totalY/elementHeightAvg;
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

    public int getPlayerRow(){

        //Player's height could be > elementHeightAvg
        float hOffset = 0;
        if(player.h > elementHeightAvg)
            hOffset = (int)player.h-elementHeightAvg;

        return  Math.round((player.distance.y+hOffset)/elementHeightAvg);
    }

    public int getPlayerCol(){

        float wOffset = 0;
        if(player.w > elementWidthAvg)
            wOffset = (int)player.w-elementWidthAvg;


       return Math.round((player.distance.x+wOffset)/elementWidthAvg);
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
