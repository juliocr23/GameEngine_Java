package states;

import animation.MovingImage;
import map.World;
import others.Point;
import test.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Hashtable;


public class GameState extends State  {

    private MovingImage player;
    private World world;
    private String fileMap = "resources/maps/map";

    public GameState(){
        createPlayer();

        try {
            System.out.println("player h " + player.h);
            System.out.println("player w " + player.w);
            Hashtable<Character,MovingImage> items = new Hashtable<>();
            getTiles(items);
            items.put('0',player);

            world = new World(fileMap,items);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
       world.update();
    }

    @Override
    public void processInput() {
        player.moveDown(Game.keyboard.keyDown(KeyEvent.VK_DOWN));
        player.moveUp(Game.keyboard.keyDown(KeyEvent.VK_UP));
        player.moveLeft(Game.keyboard.keyDown(KeyEvent.VK_LEFT));
        player.moveRight(Game.keyboard.keyDown(KeyEvent.VK_RIGHT));
    }

    @Override
    public void draw(Graphics graphics) {
        world.draw(graphics);
    }

    private void createPlayer(){
        String filePath[] = {"resources/player/idle/Idle", "resources/player/run/Run"};
        int duration[] = {3,3};
        player = new MovingImage(filePath,duration,'0');
        player.setScale(0.2f);
    }

    private void getTiles(Hashtable<Character,MovingImage> map){

       for(char i = 'A'; i<='O'; i++) {
            MovingImage newSprite = new MovingImage("resources/tile/Tile" + i, i);
            map.put(i,newSprite);
       }

    }
}
