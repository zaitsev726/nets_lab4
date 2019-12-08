package UserInterface.GamePage;

import UserInterface.Layouts.VerticalLayout;

import javax.swing.*;
import java.awt.*;
public class GamePanel extends JPanel {
    public GameFieldPanel gameField;
    public JButton backButton;

    private JPanel chatPanel;
    private JPanel scorePanel;
    private JPanel currentGamePanel;

    public GamePanel() {
        this.setLayout(null);

        gameField = new GameFieldPanel();
        backButton = new JButton("BACK");

        chatPanel = new JPanel();
        scorePanel = new JPanel();
        currentGamePanel = new JPanel();


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

        this.add(scorePane);
        this.add(currentPane);
        this.add(backButton);
        this.add(chatPane);

    }

    public void addGameField(int width, int height) {
        System.out.println("YES");
        int size = gameField.getDOT_SIZE();
        int x = (1000 - width*size)/2;
        int y = (1000 - height*size)/2;
        gameField.setBounds(x+5,y+5,width*size,height*size);
        gameField.setBackground(Color.BLACK);
        this.add(gameField);
        this.repaint();

    }
}
