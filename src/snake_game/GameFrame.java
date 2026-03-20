package snake_game;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GamePanel panel;
    GameMenu panel2;

    public GameFrame() {
        //panel = new GamePanel();
    	panel2 = new GameMenu(this);

        add(panel2);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        ImageIcon icon = new ImageIcon("src/img/icon.png");
        setIconImage(icon.getImage());
    }
}
