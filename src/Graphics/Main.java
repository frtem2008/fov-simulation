package Graphics;//основной игровой класс

import GameObjects.GameObject;
import GameObjects.Obstacle;
import GameObjects.Player;
import GameObjects.Target;
import Input.Keyboard;
import Input.Mouse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static final double cameraY = 400;
    private static final GameObjects.Player player = new Player(100, 100, 1, 0);
    //коодинаты игрока, который находится в центре экрана
    public static Dimension frameSize = new Dimension(Display.w, Display.h);
    public static ArrayList<GameObject> obstacles = new ArrayList<>();
    public static ArrayList<GameObject> targets = new ArrayList<>();
    //смещение камеры (положения игрока) относительно левого верхнего угла экрана
    private static double cameraX = 500;
    //миникарта
    //изображения
    private static Image Wall, Player, Bot, Bullet, MapImage, Background;
    //клавиатура + мышь
    public final Mouse mouse = new Mouse();
    private final Keyboard keyboard = new Keyboard(10);

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения и прогружаем игру
            loadImages();

            player.setColor(new Color(32, 28, 96));

            //привязываем слушатели
            frame.addKeyListener(keyboard);
            frame.addMouseListener(mouse);
            frame.addMouseMotionListener(mouse);

            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для использования tab, alt и т.д
            frame.setFocusTraversalKeysEnabled(false);

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;
            int frames = 0;

            //размер JFrame на самом деле


            for (int i = 0; i < 200; i++) {
                if (Math.random() > 0.2) {
                    Obstacle a = (Obstacle) GameObject.generate(Obstacle.class, Math.random() * 1600, Math.random() * 900, Math.random() * 120 + 10, Math.random() * 120 + 10);
                    if (a != null)
                        obstacles.add(a);
                } else {
                    Target a = (Target) GameObject.generate(Target.class, Math.random() * 1600, Math.random() * 900, Math.random() * 43 + 10, Math.random() * 43 + 10);
                    if (a != null)
                        targets.add(a);
                }
            }
            //главный игровой цикл
            int posX, posY;

            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();
                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                frameImage.getGraphics().drawImage(Background, 0, 0, null);
                //рисование на предварительном изображении
                this.draw(frameImage.getGraphics());
                //отрисовка миникарты

                //рисование на итоговом окне
                frameGraphics.drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);

                //очистка мусора
                frameImage.getGraphics().dispose();
                frameGraphics.dispose();

                //показ буфера на холсте
                bs.show();

                //разворот на полный экран
                if (Keyboard.getF11()) {
                    while (Keyboard.getF11()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    frame.dispose();
                    if (Display.isFullScreen) {
                        frame.setUndecorated(false);
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setBounds(Display.x, Display.y, Display.w, Display.h);
                        cameraX = 500;
                    } else {
                        cameraX = frameSize.getWidth() / 1.2;
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                if (Keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }

                //перезагрузка игры
                if (Keyboard.getR()) {
                    System.out.println("Reloading...");
                    loadImages();
                    System.out.println("Reloading finished");
                }

                //обновления игрока
                frames++;
                for (int i = 0; i < Main.frameSize.width; i++) {
                    for (int j = 0; j < Main.frameSize.height; j++) {
                        player.move();
                    }
                }

                //замер времени, ушедшего на отрисовку кадра
                end = System.currentTimeMillis();
                len = end - start;

                //стабилизация фпс
                if (len < frameLength) {
                    try {
                        Thread.sleep((long) (frameLength - len));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void draw(Graphics g) {
        player.draw(g);
        player.drawFov(g);
        g.setColor(new Color(123, 13, 31));
        g.drawLine((int) player.getRotationLine().x1, (int) player.getRotationLine().y1, (int) player.getRotationLine().x2, (int) player.getRotationLine().y2);
        //g.drawString("Rotation: " + line2String(player.getRotationLine()), 100, 100);
        g.drawString("Rotation: " + player.getRotation(), 100, 100);

        for (GameObject obstacle : obstacles) {
            obstacle.draw(g);
        }

        for (Target target : player.getVisibleTargets()) {
            target.draw(g);
        }
    }

    //функция загрузки изображений (путь к папке: src/Resources/Images/)
    public void loadImages() {
        System.out.println("Loading images");
        try {
            Player = ImageIO.read(new File("src/Images/player.png"));
            //Background = ImageIO.read(new File("src/Images/background.jpg"));
        } catch (IOException e) {
            System.out.println("Failed loading images");
            e.printStackTrace();
            return;
        }
        System.out.println("Finished loading images");
    }
}