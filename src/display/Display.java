package display;

import input.KeyboardInput;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

public abstract class Display extends JFrame implements  Runnable{

    private static volatile boolean running;
    private static Thread gameThread;
    private static BufferStrategy bufferStrategy;

    private   static GraphicsDevice graphicsDevice;
    protected static DisplayMode displayMode;

    public static int width;
    public static int height;

    protected String title;
    protected Color background;
    public Canvas canvas;

    public static KeyboardInput keyboard;


    public void start(){
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {

        running = true;

        int fps = 60;
        double timePerTick= 1.0E9/fps;
        double delta = 0;

        long currentTime = System.nanoTime();
        long lastTime    = currentTime;
        double nsPerFrame;

        long timer = 0;
        long ticks = 0;

        while (running) {
            currentTime = System.nanoTime();
            nsPerFrame = currentTime-lastTime;

            delta += nsPerFrame/timePerTick;    //Calculate target 60 fps
            timer += nsPerFrame;
            lastTime = currentTime;


            //Make sure to run the program 60 fps
            if(delta >= 1) {
                processInput();
                updateObject();
                renderFrame();
                ticks++;
                delta--;
            }

            if(timer >= 1.0E9 ){
                System.out.println("Ticks: " + ticks);
                ticks = 0;
                timer = 0;
            }
        }
    }

    protected abstract void processInput();

    protected abstract void updateObject();

    protected void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bufferStrategy.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    draw(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
    }

    protected abstract void draw(Graphics g);


    protected void createSizeWindow(){

        //Setup the canvas
        canvas = new Canvas();
        canvas.setSize(width,height);
        canvas.setBackground(background);
        canvas.setIgnoreRepaint(true);

        //Add keyboard listener to the canvas
        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);

        getContentPane().add(canvas);
        setTitle(title);
        setIgnoreRepaint(true);
        //setResizable(false);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        canvas.requestFocus();
    }

    protected void createFullWindow(){
        loadFullScreen();

        keyboard = new KeyboardInput();
        addKeyListener(keyboard);

        //Allow page flipping or double buffering
        createBufferStrategy( 2 );
        bufferStrategy = getBufferStrategy();

        onExit(); //Finished program if user pressed the escape button
    }


    private void loadFullScreen(){
        setIgnoreRepaint(true);
        setUndecorated(true);
        setBackground(background);

        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        graphicsDevice = ge.getDefaultScreenDevice();
        displayMode = getAvailableDisplays()[0];


        if( !graphicsDevice.isFullScreenSupported() ) {
            System.err.println( "ERROR: Not Supported!!!" );
            System.exit( 0 );
        }

        graphicsDevice.setFullScreenWindow(this);

        graphicsDevice.setDisplayMode(displayMode);
    }

    private void onExit(){
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    shutDown();
                }
            }
        });
    }

    protected void shutDown() {
        try {
            running = false;
            gameThread.join();

            graphicsDevice.setDisplayMode( getCurrentDisplayMode() );
            graphicsDevice.setFullScreenWindow( null );
        } catch( InterruptedException e ) {
            System.out.println(e.getMessage());
        }
        System.exit( 0 );
    }

    protected void exitSizedScreen(){
        try {
            running = false;
            gameThread.join();
        } catch( InterruptedException e ) {
            System.out.println(e.getMessage());
        }
        System.exit( 0 );
    }

    public DisplayMode getCurrentDisplayMode() {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        return ge.getDefaultScreenDevice().getDisplayMode();
    }

    public DisplayMode[] getAvailableDisplays(){
        return graphicsDevice.getDisplayModes();
    }

    public void setDisplayMode(DisplayMode mode){
        displayMode = mode;
    }

}
