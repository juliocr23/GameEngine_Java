package others;

public class Rectangle
{

  public Point  coordinate;
  public float w;
  public float h;

    /**
     * Constructor
     * @param x      The y value of the rectangle
     * @param y      The x value of the rectangle
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     */
   public Rectangle(float x, float y, float width, float height)
   {
      coordinate.x = x;
      coordinate.y = y;
      this.w = width;
      this.h = height;
   }

    /**
     * No- args Constructors
     * Initialize
     * x = 0, y = 0 and
     * width = 0
     * height = 0
     */
   public Rectangle(){
       w = 0;
       h = 0;
       coordinate = new Point();
   }

    /**
     * The setLocation method set the location of the
     * x and y coordinate of the rectangle
     * @param x The x coordinate of the rectangle
     * @param y The y coordinate of the rectangle
     */
   public void setLocation(float x, float y)
   {
     coordinate.x = x;
     coordinate.y = y;
   }


    /**
     * The overlaps method check if whether a two
     * rectangles overlaps
     * @param rect The rectangle to check if it is overlaping with
     * @return True if rectangles overlaps false otherwise
     */
   public boolean overlaps(Rectangle rect)
   {
      return (getX()      < rect.getX() + rect.w)   &&
             (getX() + w  > rect.getX()         )   &&
             (getY()      < rect.getY() + rect.h)   &&
             (getY() + h  > rect.getY()         );
   }

    /**
     * The contains method check if a point is contain within
     * the rectangle
     * @param mx The x coordinate of the point
     * @param my The y coordinate of the point
     * @return True if point is within the rectangle
     * otherwise return False
     */
   public boolean contains(double mx, double my)
   {
      return (mx > getX()) && (mx < getX()+ w) && (my > getY()) && (my < getY()+ h);
   }

   public void moveRightBy(float dx) {
       coordinate.x += dx;
   }

   public void moveLeft(float dx) {
       coordinate.x -= dx;
   }

   public void moveUp(float dy) {
       coordinate.y -= dy;
   }

   public void moveDown(float dy) {
       coordinate.y += dy;
   }


   //MARK: Getters and Setters
   //--------------------------------------------------------------------------------------------------------------//
    public float getX() {
        return coordinate.x;
    }

    public void setX(float x) {
        coordinate.x = x;
    }

    public float getY() {
        return coordinate.y;
    }

    public void setY(float y) {
        this.coordinate.y = y;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }
}