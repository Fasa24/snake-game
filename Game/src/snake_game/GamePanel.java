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

	int snakeX;
	int snakeY;

	char direction;

	LinkedList<Point> snakeBody;

	Point food;
	Random random;

	boolean grow = false;

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
		startGameLoop(); // Implemented
	}

	// Implemented: Game loop made to fix stutter issues
	public void startGameLoop() {
		new Thread(() -> {
			final int FPS = 10;
			final long frameTime = 1000 / FPS;

			while (true) {
				long startTime = System.currentTimeMillis();

				move();
				checkFood();
				repaint();

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
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Implemented: Draw food
		g.setColor(Color.RED);
		g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

		// Implemented: Draw snake
		for (int i = 0; i < snakeBody.size(); i++) {

			if (i == 0) {
				g.setColor(Color.GREEN);
			} else {
				g.setColor(Color.BLUE);
			}

			g.fillRoundRect(
				snakeBody.get(i).x,
				snakeBody.get(i).y,
				UNIT_SIZE,
				UNIT_SIZE,
				5,
				5
			);
		}

		// Implemented: Recommended for better rendering
		Toolkit.getDefaultToolkit().sync();
	}

	public void move() {
		switch (direction) {
		case 'U':
			snakeY -= UNIT_SIZE;
			break;

		case 'D':
			snakeY += UNIT_SIZE;
			break;

		case 'L':
			snakeX -= UNIT_SIZE;
			break;

		case 'R':
			snakeX += UNIT_SIZE;
			break;
		}

		snakeBody.addFirst(new Point(snakeX, snakeY));
		if (!grow) { snakeBody.removeLast(); } else { grow = false; }
	}
}
