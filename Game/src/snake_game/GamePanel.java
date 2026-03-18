package snake_game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GamePanel extends JPanel {

    static final int WIDTH = 600;
    static final int HEIGHT = 600;
    static final int UNIT_SIZE = 25;

    int snakeX, snakeY;
    char direction;

    LinkedList<Point> snakeBody;

    Point food;
    Random random;

    boolean grow = false;
    boolean running = true;

    int score = 0;
    int foodsEaten = 0;

    int speed = 10;
    final int SPEED_INCREASE_INTERVAL = 5;

    public GamePanel() {
        snakeX = 250;
        snakeY = 100;

        direction = 'R';

        snakeBody = new LinkedList<>();
        snakeBody.add(new Point(250, 100));
        snakeBody.add(new Point(225, 100));
        snakeBody.add(new Point(200, 100));

        random = new Random();
        spawnFood();

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setDoubleBuffered(true);

        setFocusTraversalKeysEnabled(false);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_D:
                        if (direction != 'L') direction = 'R';
                        break;
                    case KeyEvent.VK_W:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_S:
                        if (direction != 'U') direction = 'D';
                        break;
                }
            }
        });
        startGameLoop();
    }

    public void startGameLoop() {
        new Thread(() -> {
            while (running) {
                long startTime = System.currentTimeMillis();

                move();
                checkFood();
                checkCollisions();
                repaint();

                long frameTime = 1000 / speed;
                long elapsed = System.currentTimeMillis() - startTime;
                long sleepTime = frameTime - elapsed;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void spawnFood() {
        int x = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        food = new Point(x, y);
    }

    public void checkFood() {
        if (snakeBody.getFirst().equals(food)) {
            grow = true;
            spawnFood();

            score += 10;
            foodsEaten++;

            if (foodsEaten % SPEED_INCREASE_INTERVAL == 0) { speed++; }
        }
    }

    public void checkCollisions() {
        Point head = snakeBody.getFirst();

        // Implemented: Wall collision
        if (head.x < 0 || head.x >= WIDTH ||
            head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        // Implemented: Self collision
        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                running = false;
                break;
            }
        }
    }

    public void move() {
        switch (direction) {
            case 'U': snakeY -= UNIT_SIZE; break;
            case 'D': snakeY += UNIT_SIZE; break;
            case 'L': snakeX -= UNIT_SIZE; break;
            case 'R': snakeX += UNIT_SIZE; break;
        }
        snakeBody.addFirst(new Point(snakeX, snakeY));

        if (!grow) { snakeBody.removeLast(); }
        else { grow = false; }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {

            // Implemented: Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            // Implemented: Draw snake
            for (int i = 0; i < snakeBody.size(); i++) {
                if (i == 0) g.setColor(Color.GREEN);
                else g.setColor(Color.BLUE);

                g.fillRoundRect(
                        snakeBody.get(i).x,
                        snakeBody.get(i).y,
                        UNIT_SIZE,
                        UNIT_SIZE,
                        5,
                        5
                );
            }

            // Implemented: Draw score (top-right)
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, WIDTH - 100, 20);

        } else { drawGameOver(g); }

        Toolkit.getDefaultToolkit().sync();
    }

    public void drawGameOver(Graphics g) {
        String gameOverText = "GAME OVER";
        String scoreText = "Final Score: " + score;

        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(40f));

        var metrics = g.getFontMetrics();
        int x = (WIDTH - metrics.stringWidth(gameOverText)) / 2;
        int y = HEIGHT / 2;

        g.drawString(gameOverText, x, y);

        g.setFont(g.getFont().deriveFont(20f));
        metrics = g.getFontMetrics();

        int scoreX = (WIDTH - metrics.stringWidth(scoreText)) / 2;
        int scoreY = y + 40;

        g.drawString(scoreText, scoreX, scoreY);
    }
}
