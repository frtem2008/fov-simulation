package GameObjects;

import Graphics.Display;
import Graphics.Main;
import Input.Keyboard;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Player extends GameObject {

    private static final boolean radarMode = false;

    private final double radius;
    private final double viewRadius = 600;
    private final Line2D.Double fovA = new Line2D.Double();
    private final Line2D.Double fovB = new Line2D.Double();
    private final ArrayList<Line2D> directionsToTarget = new ArrayList<>();
    private final ArrayList<Target> visibleTargets = new ArrayList<>();
    private double rotation;
    private double viewAngle = 90;

    public Player(int x, int y, int radius, int rotation) {
        super(x, y, radius);
        this.radius = radius;
        this.rotation = rotation;
        move();
    }

    public double angleBetweenRotationAndDirectionToTarget(Line2D directionToTarget) {
        Line2D line1 = new Line2D.Double(x, y, x + Math.cos(Math.toRadians(rotation)), y + Math.sin(Math.toRadians(rotation)));

        double angle1 = Math.atan2(line1.getY1() - line1.getY2(),
                line1.getX1() - line1.getX2());
        double angle2 = Math.atan2(directionToTarget.getY1() - directionToTarget.getY2(),
                directionToTarget.getX1() - directionToTarget.getX2());

        return Math.toDegrees(angle1 - angle2);
    }

    public double normalizeAngle(double angle) {
        if (angle > 180)
            angle -= 360;
        else if (angle <= -180)
            angle += 360;

        return angle;
    }

    public double getRadius() {
        return radius;
    }

    public double getRotation() {
        return rotation;
    }

    public Line2D.Double getRotationLine() {
        return new Line2D.Double(
                x + radius, y + radius,
                x + radius + Math.cos(Math.toRadians(rotation)) * radius,
                y + radius + Math.sin(Math.toRadians(rotation)) * radius
        );
    }

    public ArrayList<Target> getVisibleTargets() {
        return visibleTargets;
    }

    public void drawFov(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawLine((int) fovA.x1, (int) fovA.y1, (int) fovA.x2, (int) fovA.y2);
        g.drawLine((int) fovB.x1, (int) fovB.y1, (int) fovB.x2, (int) fovB.y2);
        g.drawArc((int) (x - viewRadius + radius), (int) (y - viewRadius + radius), (int) viewRadius * 2, (int) viewRadius * 2, (int) -(rotation + viewAngle / 2), (int) viewAngle);

        for (Line2D line2D : directionsToTarget) {
            drawLine2D(g, line2D, Color.blue);
        }
    }

    private Obstacle getLineIntersectionObstacle(Line2D line) {
        for (int i = 0; i < Main.obstacles.size(); i++) {
            if (line.intersects(Main.obstacles.get(i).hitbox.getRectangle()))
                return (Obstacle) Main.obstacles.get(i);
        }
        return null;
    }

    private void drawLine2D(Graphics g, Line2D line2D, Color c) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setColor(c);
        graphics2D.draw(line2D);
    }

    private double distToTarget(Target target) {
        return Math.sqrt((x - target.x) * (x - target.x) + (y - target.y) * (y - target.y));
    }

    private Line2D[] fillLinesToTargetBounds(Target target) {
        Line2D[] dirToTar = new Line2D[(int) (target.w * 4)];

        for (int j = 0; j < (int) (target.w * 4); j++) {
            if (j < target.w)
                dirToTar[j] = new Line2D.Double(x + radius, y + radius, target.x + j, target.y);
            else if (j < target.w * 2)
                dirToTar[j] = new Line2D.Double(x + radius, y + radius, target.x, target.y - target.h + j);
            else if (j < target.w * 3)
                dirToTar[j] = new Line2D.Double(x + radius, y + radius, target.x + target.w, target.y - target.h * 2 + j);
            else
                dirToTar[j] = new Line2D.Double(x + radius, y + radius, target.x - target.w * 3 + j, target.y + target.h);
        }
        return dirToTar;
    }

    private void findVisibleTargets() {
        visibleTargets.clear();
        directionsToTarget.clear();

        for (int i = 0; i < Main.targets.size(); i++) {
            Target target = (Target) Main.targets.get(i);

            Line2D[] dirToTar = fillLinesToTargetBounds(target);
            //directionsToTarget.addAll(Arrays.asList(dirToTar));

            for (int j = 0; j < dirToTar.length - 1; j++) {
                Line2D directionToTarget = dirToTar[j];

                double angle = angleBetweenRotationAndDirectionToTarget(directionToTarget);

                if (distToTarget(target) <= viewRadius) {
                    if (Math.abs(normalizeAngle(angle)) < viewAngle / 2 &&
                            Math.cos(Math.toRadians(angle)) >= 0 //для сонаправленного вектора направления и объекта
                    ) {
                        if (getLineIntersectionObstacle(directionToTarget) == null) {
                            visibleTargets.add(target);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void move() {
        if (radarMode) {
            viewAngle = 2;
            if (Keyboard.getD() && x < Display.w)
                x += 2;
            if (Keyboard.getA() && x > 0)
                x -= 2;
            if (Keyboard.getW() && y > 0)
                y -= 2;
            if (Keyboard.getS() && y < Display.h)
                y += 2;

            rotation += 4;
            rotation = rotation % 360;
        } else {
            if (x >= 0 && x + w <= Main.frameSize.width && y >= 0 && y + h <= Main.frameSize.height) {
                if (Keyboard.getUp()) {
                    x += Math.cos(Math.toRadians(rotation)) * 2;
                    y += Math.sin(Math.toRadians(rotation)) * 2;
                    hitbox.x = x;
                    hitbox.y = y;
                    if (x < 0) x = 0;
                    if (x + w > Main.frameSize.width) x = Main.frameSize.width - w;
                    if (y < 0) y = 0;
                    if (y + h > Main.frameSize.height) y = Main.frameSize.height - h;
                }

                if (Keyboard.getDown()) {
                    x -= Math.cos(Math.toRadians(rotation)) * 2;
                    y -= Math.sin(Math.toRadians(rotation)) * 2;
                }
            }

            if (Keyboard.getLeft()) {
                rotation -= 2;
                if (rotation < 0)
                    rotation = 360 - rotation;
            }
            if (Keyboard.getRight()) {
                rotation += 2;
                rotation = rotation % 360;
            }

            if (Keyboard.getA())
                viewAngle = viewAngle < 4 ? 2 : viewAngle - 2;
            if (Keyboard.getD())
                viewAngle = viewAngle > 176 ? 180 : viewAngle + 2;
        }
        hitbox.x = x;
        hitbox.y = y;

        changeFovLines();
        findVisibleTargets();
    }

    //some stupid math made in half an hour lol
    private void changeFovLines() {
        fovA.x1 = x + radius;
        fovA.y1 = y + radius;
        fovB.x1 = x + radius;
        fovB.y1 = y + radius;

        fovA.x2 = x + radius + Math.cos(Math.toRadians(viewAngle / 2 + rotation)) * viewRadius;
        fovA.y2 = y + radius + Math.sin(Math.toRadians(viewAngle / 2 + rotation)) * viewRadius;
        fovB.x2 = x + radius + Math.cos(Math.toRadians(viewAngle / 2 + rotation - viewAngle)) * viewRadius;
        fovB.y2 = y + radius + Math.sin(Math.toRadians(viewAngle / 2 + rotation - viewAngle)) * viewRadius;
    }
}
