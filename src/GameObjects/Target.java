package GameObjects;

import java.awt.*;

public class Target extends GameObject {
    public Target(double x, double y, double r) {
        super(x, y, r);
        setColor(Color.white);
    }

    public Target(double x, double y, double r, Color c) {
        super(x, y, r);
        setColor(c);
    }
}
