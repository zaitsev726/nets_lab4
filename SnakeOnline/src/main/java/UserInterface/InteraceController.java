package UserInterface;

import NetworkPart.GlobalController;
import UserInterface.ConnectionPage.ConnectionPanel;
import UserInterface.Frames.Window;
import UserInterface.GamePage.GamePanel;
import UserInterface.MenuPage.MenuPanel;
import UserInterface.NetInfoEntryPage.NetInfoEntryPanel;
import UserInterface.NewGamePage.NewGamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InteraceController {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int sizeWidth = 1520;
    private int sizeHeight = 1045;
    private int locationX = (screenSize.width - sizeWidth) / 2;
    private int locationY = (screenSize.height - sizeHeight) / 2 - 20;

    private UserInterface.Frames.Window window;
    private MenuPanel menuPanel;
    private NewGamePanel newGamePanel;
    private GamePanel gamePanel;
    private ConnectionPanel connectionPanel;
    private NetInfoEntryPanel netInfoEntryPanel;

    private int heightSnakeField;
    private int widthSnakeField;


    public InteraceController(){
        window = new Window(sizeWidth,sizeHeight,locationX,locationY);
        menuPanel = new MenuPanel(sizeWidth,sizeHeight);
        newGamePanel = new NewGamePanel();
        gamePanel = new GamePanel();
        connectionPanel = new ConnectionPanel();
        netInfoEntryPanel = new NetInfoEntryPanel();


        window.add(menuPanel);
        initializationListeners();
        window.setVisible(true);
    }

    private void initializationListeners(){
        initializationMenuListeners();
        initializationNewGamePanelListeners();
        initializationGameListeners();
        initializationConnectionListeners();
        initializationNetInfoEntryListeners();

    }

    private void initializationNetInfoEntryListeners() {
        netInfoEntryPanel.continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(netInfoEntryPanel);
                window.add(menuPanel);
                window.revalidate();
                window.repaint();
            }
        });

        netInfoEntryPanel.portField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Отображение введенного текста
                try {
                    int w = Integer.parseInt(netInfoEntryPanel.portField.getText());
                    if (w > 6000 || w < 2000) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 2000 до 6000, а не " + w);
                    }
                    else
                        GlobalController.getInstance().setPort(w);

                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры для port!");
                }
            }
        });

        netInfoEntryPanel.IPField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Отображение введенного текста
                try {
                    String hostname = netInfoEntryPanel.IPField.getText();
                    InetAddress adress = InetAddress.getByName(hostname);
                    GlobalController.getInstance().setIP(adress);
                    System.out.println(adress);
                    System.out.println(hostname);
                } catch (UnknownHostException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели IP!");
                }
            }
        });
    }

    private void initializationConnectionListeners() {
        connectionPanel.backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(connectionPanel);
                window.add(menuPanel);
                window.revalidate();
                window.repaint();
            }
        });
    }

    private void initializationGameListeners() {
        gamePanel.backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(gamePanel.gameField.timer != null)
                    gamePanel.gameField.timer.stop();
                window.remove(gamePanel);
                window.add(newGamePanel);
                window.revalidate();
                window.repaint();
            }
        });

    }

    private void initializationMenuListeners() {
        menuPanel.newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(menuPanel);
                window.add(newGamePanel);
                window.revalidate();
                window.repaint();
            }
        });

        menuPanel.connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(menuPanel);
                window.add(connectionPanel);
                window.revalidate();
                window.repaint();
            }
        });

        menuPanel.netInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(menuPanel);
                window.add(netInfoEntryPanel);
                window.revalidate();
                window.repaint();
            }
        });
    }

    private void initializationNewGamePanelListeners() {

        newGamePanel.widthField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Отображение введенного текста
                try {
                    int w = Integer.parseInt(newGamePanel.widthField.getText());
                    if (w > 100 || w < 10) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 10 до 100, а не " + w);
                    }
                    else
                        setWidthSnakeField(w);

                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.heightField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(newGamePanel.heightField.getText());
                    if (w > 100 || w < 10) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 10 до 100, а не " + w);
                    }
                    else
                        setHeightSnakeField(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.remove(newGamePanel);
                window.add(menuPanel);
                window.revalidate();
                window.repaint();
            }
        });

        newGamePanel.continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              /*  if(heightSnakeField < 10 || heightSnakeField > 100 ||
                widthSnakeField < 10 || widthSnakeField > 100)                                     // раскоментить в случае тестов!
                    JOptionPane.showMessageDialog(window, "Вы ввели неправильный размер поля!");
                else {*/
                    window.remove(newGamePanel);
                    window.add(gamePanel);

                    widthSnakeField = 20;
                    heightSnakeField = 20;
                    gamePanel.addGameField(widthSnakeField, heightSnakeField);
                    gamePanel.gameField.initGame(widthSnakeField, heightSnakeField);
                    gamePanel.setFocusable(true);
                    gamePanel.requestFocus();
                    window.revalidate();
                    window.repaint();
                }
            //}
        });
    }

    private void setHeightSnakeField(int value){
        if(value > 100 || value < 10)
            heightSnakeField = 0;
        else
            heightSnakeField = value;
    }
    private void setWidthSnakeField(int value){
        if(value > 100 || value < 10)
            widthSnakeField = 0;
        else
            widthSnakeField = value;
    }

}
