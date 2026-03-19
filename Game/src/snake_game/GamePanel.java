package snake_game;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

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
    boolean paused = false;

    int score = 0;
    int foodsEaten = 0;

    int speed = 10;
    final int SPEED_INCREASE_INTERVAL = 5;

    Timer timer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setDoubleBuffered(true);

        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {

                    // Movement
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

                    // Pause
                    case KeyEvent.VK_P:
                        paused = !paused;
                        break;

                    // Restart
                    case KeyEvent.VK_R:
                        restartGame();
                        break;
                }
            }
        });
        startGameLoop();
    }

    private void initGame() {
        snakeX = 250;
        snakeY = 100;

        direction = 'R';

        snakeBody = new LinkedList<>();
        snakeBody.add(new Point(250, 100));
        snakeBody.add(new Point(225, 100));
        snakeBody.add(new Point(200, 100));

        random = new Random();
        spawnFood();

        grow = false;
        running = true;
        paused = false;

        score = 0;
        foodsEaten = 0;
        speed = 10;
    }

    public void startGameLoop() {
        int delay = 1000 / speed;

        timer = new Timer(delay, e -> {
            if (running && !paused) {
                move();
                checkFood();
                checkCollisions();

                // Implemented: Adjust speed dynamically
                ((Timer) e.getSource()).setDelay(1000 / speed);
            }
            repaint();
        });
        timer.start();
    }

    public void restartGame() { initGame(); }

    public void spawnFood() {
        while (true) {
            int x = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            int y = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;

            Point newFood = new Point(x, y);

            if (!snakeBody.contains(newFood)) {
                food = newFood;
                break;
            }
        }
    }

    public void checkFood() {
        if (snakeBody.getFirst().equals(food)) {
            grow = true;
            spawnFood();

            score += 10;
            foodsEaten++;

            if (foodsEaten % SPEED_INCREASE_INTERVAL == 0) {
                speed++;
            }
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

        if (!grow) {
            snakeBody.removeLast();
        } else {
            grow = false;
        }
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

            // Implemented: Draw score
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(20f));
            String scoreText = "Score: " + score;
            FontMetrics metrics = g.getFontMetrics();
            int scoreX = WIDTH - metrics.stringWidth(scoreText) - 10;
            int scoreY = metrics.getHeight();
            g.drawString(scoreText, scoreX, scoreY);

            // Implemented: Draw pause text centered
            if (paused) {
                g.setColor(Color.YELLOW);
                g.setFont(g.getFont().deriveFont(30f));

                String pausedText = "PAUSED";
                metrics = g.getFontMetrics();
                int x = (WIDTH - metrics.stringWidth(pausedText)) / 2;
                int y = (HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();

                g.drawString(pausedText, x, y);
            }
        } else {
            drawGameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(g.getFont().deriveFont(40f));

        String gameOverText = "GAME OVER";
        FontMetrics metrics = g.getFontMetrics();
        int x = (WIDTH - metrics.stringWidth(gameOverText)) / 2;
        int y = HEIGHT / 2;
        g.drawString(gameOverText, x, y);

        // Implemented: Draw final score centered
        g.setFont(g.getFont().deriveFont(20f));
        metrics = g.getFontMetrics();
        String scoreText = "Final Score: " + score;
        int scoreX = (WIDTH - metrics.stringWidth(scoreText)) / 2;
        int scoreY = y + metrics.getHeight() + 10; // 10 px spacing
        g.drawString(scoreText, scoreX, scoreY);

        // Implemented: Draw restart text centered
        String restartText = "Press R to Restart";
        int restartX = (WIDTH - metrics.stringWidth(restartText)) / 2;
        int restartY = scoreY + metrics.getHeight() + 10;
        g.drawString(restartText, restartX, restartY);
    }
}
