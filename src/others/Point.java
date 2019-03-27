package others;

public class Point {
    public float x;
    public float y;

   public Point(){
        x = 0;
        y = 0;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

   public Point(Point other){
        x = other.x;
        y = other.y;
    }

    public void setLocation(float x, float y){
        this.x = x;
        this.y = y;
    }

   public void setLocation(Point other){
        x = other.x;
        y = other.y;
    }

   public void translate(float dx, float dy){
        x += dx;
        y += dy;
    }

   public void moveLeft(float dx){
        this.x -= dx;
    }

   public void moveRight(float dx){
        this.x += dx;
    }

    public float distance(Point other){
        return  (float)(Math.pow(other.x - x,2) + Math.pow(other.y-y,2));
    }
    public float distance(float x2, float y2){
        return  (float)(Math.pow(x2 - x,2) + Math.pow(y2-y,2));
    }

    @Override
    public boolean equals(Object obj) {
        Point other = (Point) obj;
        return other.x == x && y == other.y;
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }
}
