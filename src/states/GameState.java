package states;

import animation.MovingImage;
import map.World;
import java.awt.*;
import java.util.Hashtable;


public class GameState extends State  {

    private MovingImage player;
    private World world;
    private String fileMap = "resources/maps/map";

    public GameState(){
        createPlayer();

        try {
            System.out.println("player h " + player.height);
            System.out.println("player w " + player.width);
            Hashtable<Character,MovingImage> items = new Hashtable<>();
            getTiles(items);
            items.put('0',player);

            world = new World(fileMap,items);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processInput() {
        world.processInput();
    }

    @Override
    public void update() {
       world.update();
    }


    @Override
    public void draw(Graphics graphics) {
        world.draw(graphics);
    }

    private void createPlayer(){
        String filePath[] = {"resources/player/idle/Idle", "resources/player/run/Run"};
        int duration[] = {3,3};
        player = new MovingImage(filePath,duration,'0');
        player.setScale(0.22f, 0.13f);
    }

    private void getTiles(Hashtable<Character,MovingImage> map){

       for(char i = 'A'; i<='O'; i++) {
            MovingImage newSprite = new MovingImage("resources/tile/Tile" + i, i);
            map.put(i,newSprite);
       }

    }
}
