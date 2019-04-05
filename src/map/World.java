package map;

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
 * ->use the height and width of the element to determine the row and col it should check.
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

        //TODO: Add offset to collision where player don't overlap. is set 1 pixel away from object.
        updatePlayer();

        int startR =0;
        int startC =0;

        //Is drawing the whole matrix.
        //TODO: Only draw the portion that needs to be display.
        for(int row = startR; row<elements.length; ++row) {
            for(int col = startC; col<elements[row].length; ++col) {

                if (!isEmpty(row,col)) {

                    //Check
                    elements[row][col].update();

                    //TODO: There is an issue with the map when updating it.
                 //   updateMap(row,col);
                }
            }
        }
    }

    private void updatePlayer() {

        //Get player's row and col
        int row = getPlayerRow();
        int col = getPlayerCol();

        if(isValid(row,col) && elements[row][col] != player) {
            System.out.println("Can't get player row,col");
            return;
        }

        Pair result;
        if(moveRight) {

            result = isRightCollision(row,col);
            if(result.isEmpty())
               player.moveRight();
            else {
                player.x = abs(elements[result.row][result.col].x - elements[result.row][result.col].width - 1);
                System.out.println("r:" + result.row + " c: " + result.col);
            }
        }

        if(moveLeft){

            result = isLeftCollision(row,col);
            if(result.isEmpty())
                player.moveLeft();
            else
                player.x =  abs(elements[result.row][result.col].x + elements[result.row][result.col].width + 1);
        }

        if(moveUp){

            result = isTopCollision(row,col);
            if(result.isEmpty())
                player.moveUp();
            else
                player.y =  abs(elements[result.row][result.col].y + elements[result.row][result.col].height + 1);
        }

        if(moveDown) {

            result = isBottomCollision(row,col);
            if (result.isEmpty())
                player.moveDown();
            else
                player.y = abs(elements[result.row][result.col].y - elements[result.row][result.col].height - 1);
        }

        var newRow = getPlayerRow();
        var newCol = getPlayerCol();

        if(isValid(newRow,newCol) && isEmpty(newRow,newCol)) {
                elements[newRow][newCol] = elements[row][col];
                elements[row][col] = null;
        }

          printElements();
//        System.out.println(elementHeightAvg);
//        System.out.println(player.height);
    }

    //MARK: Collision
    //--------------------------------------------------------------------------------------------------------------------------//


    //&*
    public Pair isRightCollision(int row, int col) {

        var isRightCollision = exist(row,col+1) && isLeftSide(row,col+1);
        var isTopRightCollision = exist(row-1,col+1) && isLeftSide(row-1,col+1);
        var isBottomRightCollision = exist(row+1,col+1) && isLeftSide(row+1,col+1);

        if(isRightCollision) return new Pair(row,col+1);

        else if(isTopRightCollision) return new Pair(row-1,col+1);

        else if(isBottomRightCollision) return new Pair(row+1,col+1);

        else return new Pair();
    }

    //*&
    public Pair isLeftCollision(int row, int col) {

        var isLeftCollision       = exist(row,col-1) && isRightSide(row,col-1);
        var isLeftTopCollision    = exist(row-1,col-1) && isRightSide(row-1,col-1);
        var isLefBottomCollision  = exist(row+1,col-1) && isRightSide(row+1,col-1);

        if(isLeftCollision)   return new Pair(row,col-1);

        else if(isLeftTopCollision) return new Pair(row-1,col-1);

        else if(isLefBottomCollision) return new Pair(row+1,col-1);

        else return  new Pair();
    }

    //  *
    //  &
    public Pair isTopCollision(int row, int col) {

        int elementsToCheck = 1;
        if(player.height > elementHeightAvg)
             elementsToCheck +=  Math.round((player.height-elementHeightAvg + 0.0)/elementHeightAvg);

        boolean isTopCollision,
                isTopLeftCollision,
                isTopRightCollision;

        for(int i = 1; i<=elementsToCheck; ++i) {

            isTopCollision = exist(row - i, col) && isBottomSide(row - i, col);
            isTopLeftCollision = exist(row - i, col + 1) && isBottomSide(row - i, col + 1);
            isTopRightCollision = exist(row - i, col - 1) && isBottomSide(row - i, col - 1);

            if (isTopCollision) return new Pair(row - i, col);

            else if (isTopLeftCollision) return new Pair(row - i, col + 1);

            else if (isTopRightCollision) return new Pair(row - i, col - 1);
        }

       return new Pair();
    }

    //  &
    //  *
    public Pair isBottomCollision(int row, int col) {

        int elementsToCheck = 1;
        if(player.height > elementHeightAvg)
            elementsToCheck +=  Math.round((player.height-elementHeightAvg + 0.0)/elementHeightAvg);

        boolean isBottomCollision,
                isBottomLeftCollision,
                isBottomRightCollision;

//        for(int i = 1; i<=elementsToCheck; ++i) {

             isBottomCollision  = exist(row + 1,col) && isTopSide(row + 1,col);
             isBottomLeftCollision  = exist(row + 1,col+1) && isTopSide(row + 1,col+1);
             isBottomRightCollision = exist(row + 1,col-1) && isTopSide(row + 1,col-1);

//            System.out.println(row + i);
//
//            System.out.println(isBottomCollision);

             if(isBottomCollision) return  new Pair(row + 1,col);

             else if(isBottomLeftCollision) return new Pair(row + 1,col+1);

             else if(isBottomRightCollision) return new Pair(row + 1,col-1);
//        }

        return new Pair();
    }

    public boolean isLeftSide(int row, int col) {

        float xOffset = player.getVxi() + player.getAx();
        boolean pConner1 = player.contains(leftTopConner(row,col, -xOffset, 0));
        boolean pConner2 = player.contains(leftBottomConner(row,col, -xOffset, 0));

        return pConner1 || pConner2;
    }

    public boolean isRightSide(int row, int col) {

        float xOffset = player.getVxi() + player.getAx();
        boolean conner1 = player.contains(rightTopConner(row,col, xOffset, 0));
        boolean conner2 = player.contains(rightBottomConner(row,col, xOffset, 0));

        return conner1 || conner2;
    }

    public boolean isTopSide(int row, int col) {

        float yOffset = player.getVyi() +  player.getAy();

        boolean pConner1 = player.contains(leftTopConner(row,col, 0, -yOffset));
        boolean pConner2 = player.contains(rightTopConner(row,col, 0, -yOffset));

        //Player could be bigger than object.
//        int pRow = getPlayerRow();
//        int pCol = getPlayerCol();
//        boolean eConner1 = elements[row][col].contains(leftBottomConner(pRow,pCol,0,yOffset));
//        boolean eConner2 =  elements[row][col].contains(rightBottomConner(pRow,pCol,0,yOffset));


        return pConner1 || pConner2;
    }

    public boolean isBottomSide(int row, int col) {

        float yOffset = player.getVyi() +  player.getAy();

        //Object could be bigger than player.
        boolean pConner1 = player.contains(rightBottomConner(row,col, 0, yOffset));
        boolean pConner2 = player.contains(leftBottomConner(row,col, 0,yOffset));

        //Player could be bigger than object.
        int pRow = getPlayerRow();
        int pCol = getPlayerCol();
        boolean eConner1 = elements[row][col].contains(leftTopConner(pRow,pCol,0,-yOffset));
        boolean eConner2 =  elements[row][col].contains(rightTopConner(pRow,pCol,0,-yOffset));

        return pConner1 || pConner2 || eConner1 || eConner2;
    }


    public Point leftTopConner(int row, int col, float xOffset, float yOffset) {

        int x =(int) (elements[row][col].x + xOffset);
        int y =(int) (elements[row][col].y + yOffset);
        return  new Point(x,y);
    }

    public Point rightTopConner(int row, int col, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int) (elements[row][col].x + elements[row][col].width + xOffset);
        conner.y = (int) (elements[row][col].y + yOffset);

        return conner;
    }

    public Point leftBottomConner(int row, int col, float xOffset, float yOffset) {
        Point conner = new Point();
        conner.x = (int) (elements[row][col].x + xOffset);
        conner.y = (int) (elements[row][col].y + elements[row][col].height + yOffset);

        return conner;
    }

    public Point rightBottomConner(int row, int col, float xOffset, float yOffset) {

        Point conner = new Point();
        conner.x = (int)(elements[row][col].x + elements[row][col].width + xOffset);
        conner.y = (int) (elements[row][col].y + elements[row][col].height + yOffset);

        return conner;
    }



    //MARK: Type Alia for elements[row][col]
    private boolean moveLeft(int row,int col){

        int c = getPlayerCol();
        int r = getPlayerRow();

        if(player.isMovingRight() &&  isEmpty(r,c+1) ) {
            elements[row][col].x -= player.getVxi();
            return true;
        }
        else if(player.isMovingLeft()) {
            player.leftBoundary = Display.width / 2 + player.width;
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
            elements[row][col].x += player.getVxi();
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
                    elements[row][col].distance.x   = (int) getXFor(row,col);

                    elements[row][col].distance.y   = (int) getYFor(row,col);
                    elements[row][col].y = (int)getYFor(row,col);

                    if(isPlayer(row,col)) {
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


    public int getNewCol(int row, int col){

        Point d = elements[row][col].distance();
        float totalX =d.x+wOffset(row,col);
        return  (int)totalX/elementWidthAvg;
    }
    public int getNewRow(int row, int col){

        Point d = elements[row][col].distance();
        float totalY = d.y + hOffset(row,col);
        return (int)totalY/elementHeightAvg;
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
        if(player.height > elementHeightAvg)
            hOffset = player.height-elementHeightAvg;

        return  Math.round((player.distance.y+hOffset)/elementHeightAvg);
    }

    public int getPlayerCol(){

        float wOffset = 0;
        if(player.width > elementWidthAvg)
            wOffset = (int)player.width-elementWidthAvg;


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

   public boolean isEmpty(){
        return row == -1 && col == -1;
    }

    @Override
    public String toString() {
       return "r: " + row + " c: " + col + "\n";
    }
}



