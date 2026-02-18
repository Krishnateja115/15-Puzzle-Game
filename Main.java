package fifteen_divideandconq;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("15 Puzzle - Divide & Conquer");

            GamePanel panel = new GamePanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 700);
            frame.setLocationRelativeTo(null);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
}
