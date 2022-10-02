package GameObjects;

import java.awt.*;

public class Obstacle extends GameObject {
    public Obstacle(double x, double y, double w, double h, Color c) {
        super(x, y, w, h);
        setColor(c);
    }

    public Obstacle(double x, double y, double w, double h) {
        super(x, y, w, h);
    }
}
