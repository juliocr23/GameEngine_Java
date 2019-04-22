package animation;

public enum Movement {

    RIGHT(0),LEFT(1),UP(2),DOWN(3);

    public final int index;
    Movement(int index) {
        this.index = index;
    }
}
