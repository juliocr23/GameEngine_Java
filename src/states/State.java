package states;

import java.awt.*;


public abstract class State {

    private static State currentState = null;

    public static void setState(State state){
        currentState = state;
    }

    /**
     * The getState method
     * @return return the current state of the game
     *         such as gameOver, menu, about etc.
     */

    public static State getState(){ return currentState; }

    public abstract void update();

    public abstract void processInput();

    public abstract void draw(Graphics g);
}
