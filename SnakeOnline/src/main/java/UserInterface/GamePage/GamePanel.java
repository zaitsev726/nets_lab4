package UserInterface.GamePage;

import UserInterface.Layouts.VerticalLayout;

import javax.swing.*;
import java.awt.*;
public class GamePanel extends JPanel {
    public GameField gameField;
    public JButton backButton;

    private JPanel snakePanel;
    private JPanel chatPanel;
    private JPanel scorePanel;
    private JPanel currentGamePanel;

    public GamePanel() {
        this.setLayout(null);

        gameField = new GameField();
        backButton = new JButton("BACK");

        snakePanel = new JPanel();
        chatPanel = new JPanel();
        scorePanel = new JPanel();
        currentGamePanel = new JPanel();

        snakePanel.setBounds(5, 5, 1000, 1000);

        JScrollPane scorePane = new JScrollPane(scorePanel);
        JScrollPane chatPane = new JScrollPane(chatPanel);
        JScrollPane currentPane = new JScrollPane(currentGamePanel);

        scorePane.setBounds(1010, 5, 490, 225);
        currentPane.setBounds(1010,235, 245,115);
        backButton.setBounds(1260, 235, 240, 115);
        chatPane.setBounds(1010, 355, 490, 650);


        scorePanel.setBackground(Color.green);
        gameField.setBackground(Color.black);
        chatPanel.setBackground(Color.CYAN);
        snakePanel.setBackground(Color.DARK_GRAY);

        scorePanel.setLayout(new VerticalLayout(465, 25));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));
        scorePanel.add(new JLabel("123123"));

        currentGamePanel.setLayout(new VerticalLayout(225,25));
        currentGamePanel.add(new JLabel("Ведущий "));
        currentGamePanel.add(new JLabel("Размер поля"));
        currentGamePanel.add(new JLabel("Еда"));

        chatPanel.setLayout(new VerticalLayout(465, 25));
        chatPanel.add(new JLabel("123123"));
        chatPanel.add(new JLabel("123123"));
        chatPanel.add(new JLabel("123123"));
        chatPanel.add(new JLabel("123123"));
        chatPanel.add(new JLabel("123123"));
        chatPanel.add(new JLabel("123123"));

        this.setFocusable(true);
        this.requestFocus();
        addKeyListener(new FieldKeyListener(gameField));

       // this.add(gameField);
        this.add(snakePanel);
        this.add(scorePane);
        this.add(currentPane);
        this.add(backButton);
        this.add(chatPane);

    }

    public void addGameField(int width, int height) {
        int size = gameField.getDOT_SIZE();
        int x = (1000 - width*size)/2;
        int y = (1000 - height*size)/2;
        gameField.setBounds(x,y,width*size,height*16);
        this.add(gameField);
        this.repaint();

    }
}
