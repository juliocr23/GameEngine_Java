
package test;
import states.*;
import display.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



public class Game extends Display {

   public static State gameState;

   public void start(){

       width = 640;
       height = 512;

       //Create states
       gameState = new GameState();

       //Set state
       State.setState(gameState);

       //Create a size display
       createSizeWindow();
       canvas.setBackground(Color.black);
       super.start();
   }

    @Override
    protected void processInput() {
        keyboard.poll();
        if(State.getState() != null){
            State.getState().processInput();
        }
    }

    @Override
    protected void updateObject() {
        if(State.getState() != null){
            State.getState().update();
        }
    }

    @Override
    protected void draw(Graphics g) {
        if(State.getState() != null){
            State.getState().draw(g);
        }
    }

    public static void main(String[]args){
        final Game app = new Game();

       SwingUtilities.invokeLater(() -> app.start());

        app.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               app.exitSizedScreen();
            }
        });
    }
}
