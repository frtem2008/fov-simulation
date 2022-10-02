package GameObjects;

import Graphics.Main;

import java.awt.*;
import java.util.ArrayList;

public abstract class GameObject {
    public static final int maxGeneratorAttempts = 100;
    private final boolean round;
    public double x, y, w, h;
    public Rect hitbox;
    private Color color;

    public GameObject(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.hitbox = new Rect(x, y, w, h);
        this.round = false;
    }

    public GameObject(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.w = r * 2;
        this.h = r * 2;
        this.hitbox = new Rect(x, y, w, w);
        this.round = true;
    }

    public static GameObject generate(Class cl, double x, double y, double w, double h) {
        int counter = 0;
        GameObject a = null;

        if (cl.equals(Obstacle.class)) {
            a = new Obstacle(x, y, w, h,
                    new Color(
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255)
                    )
            );
            while (a.hitbox.intersectsList(Main.obstacles) || a.hitbox.intersectsList(Main.targets)) {
                a = new Obstacle(x, y, w, h,
                        new Color(
                                (int) (Math.random() * 255),
                                (int) (Math.random() * 255),
                                (int) (Math.random() * 255)
                        )
                );
                if (counter++ > maxGeneratorAttempts)
                    return null;
            }
        }
        if (cl.equals(Target.class)) {
            a = new Target(x, y, w,
                    new Color(
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255)
                    )
            );
            while (a.hitbox.intersectsList(Main.obstacles) || a.hitbox.intersectsList(Main.targets)) {
                a = new Target(x, y, w,
                        new Color(
                                (int) (Math.random() * 255),
                                (int) (Math.random() * 255),
                                (int) (Math.random() * 255)
                        )
                );
                if (counter++ > maxGeneratorAttempts)
                    return null;
            }
            return a;
        }
        return a;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawHitBox(Graphics g) {
        g.setColor(Color.red);
        g.drawRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.w, (int) hitbox.h);
    }

    public void draw(Graphics g) {
        drawHitBox(g);
        g.setColor(color);

        if (round)
            g.fillOval((int) x, (int) y, (int) w, (int) w);
        else
            g.fillRect((int) x, (int) y, (int) w, (int) h);
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

class Rect {
    public double x, y, w, h;

    public Rect(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Rectangle getRectangle() {
        return new Rectangle((int) x, (int) y, (int) w, (int) h);
    }

    public boolean intersectsList(ArrayList<GameObject> list) {
        for (GameObject gameObject : list) {
            if (intersects(gameObject.hitbox)) {
                return true;
            }
        }

        return false;
    }

    public boolean intersects(Rect r) {
        double tw = this.w;
        double th = this.h;
        double rw = r.w;
        double rh = r.h;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.x;
        double ty = this.y;
        double rx = r.x;
        double ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }
}
