package Input;

import GameObjects.GameObject;
import GameObjects.Obstacle;
import GameObjects.Target;
import Graphicks.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
    //позиция + кол-во прокрученных тиков
    public static boolean mouseClicked;
    public static int x, y;

    @Override
    public void mouseClicked(MouseEvent e) {
        new Thread(() -> {

            if (e.getButton() == MouseEvent.BUTTON1) {
                Obstacle gen = (Obstacle) GameObject.generate(Obstacle.class, e.getX(), e.getY(), Math.random() * 120 + 50, Math.random() * 120 + 50);
                if (gen != null)
                    Main.obstacles.add(gen);
                else
                    System.out.println("Unable to generate an obstacle");
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                int rad = (int) (Math.random() * 35 + 15);
                Target gen = (Target) GameObject.generate(Target.class, e.getX() - rad, e.getY() - rad, rad, rad);
                if (gen != null)
                    Main.targets.add(gen);
                else
                    System.out.println("Unable to generate a target");
            }
        }).start();
        System.out.println("X: " + e.getX() + " Y: " + e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseClicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }
}
