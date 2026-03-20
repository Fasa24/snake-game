package snake_game;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.awt.event.KeyEvent;

public class GameMenu extends JPanel implements KeyListener {
	GameFrame window;
	static final int WIDTH = 600;
    static final int HEIGHT = 600;
    static final int v = 25;
    int option = 0; // 0 = Jugar, 1 = Configuración, 2 = Salir
    Font font;
    

    String[] options = {"Jugar", "Configuración", "Salir"};

    public GameMenu(GameFrame window) {
    	this.window = window;
    	this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    	this.setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(40f);
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("Arial", Font.BOLD, 30); // fallback
            System.out.println("No");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(font);

        for (int i = 0; i < options.length; i++) {
            if (i == option) {
                g.setColor(Color.RED); // opción seleccionada
            } else {
                g.setColor(Color.WHITE);
            }
            
            if(i == 0) {
            	g.drawString(options[i], (WIDTH + 180) / 3, (HEIGHT - 250) + i * 50);
            }
            if(i == 1) {
            	g.drawString(options[i], (WIDTH + 15) / 3, (HEIGHT - 250) + i * 50);
            }
            if(i == 2) {
            	g.drawString(options[i], (WIDTH + 210) / 3, (HEIGHT - 250) + i * 50);
            }
            
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // mover selección
        if (key == KeyEvent.VK_W) {
            option--;
            if (option < 0) option = options.length - 1;
        }

        if (key == KeyEvent.VK_S) {
            option++;
            if (option >= options.length) option = 0;
        }

        // seleccionar con ENTER
        if (key == KeyEvent.VK_ENTER) {
            selectOption();
        }

        // seleccionar con números
        if (key == KeyEvent.VK_1) option = 0;
        if (key == KeyEvent.VK_2) option = 1;
        if (key == KeyEvent.VK_3) option = 2;

        repaint();
    }

    private void selectOption() {
        switch (option) {
            case 0:
            	GamePanel game = new GamePanel();
            	window.getContentPane().removeAll(); //limpiar menu
            	window.add(game);         // agregar juego
                window.revalidate();                 // actualizar
                window.repaint();
                game.requestFocusInWindow();
                break;
            case 1:
                System.out.println("Abrir configuración");
                break;
            case 2:
                System.exit(0);
                break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}