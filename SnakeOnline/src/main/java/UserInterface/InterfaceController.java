package UserInterface;

import Global.GlobalController;
import NetworkPart.NetworkController;
import UserInterface.ConnectionPage.ConnectionPanel;
import UserInterface.Frames.Window;
import UserInterface.GamePage.GamePanel;
import UserInterface.MenuPage.MenuPanel;
import UserInterface.NetInfoEntryPage.NetInfoEntryPanel;
import UserInterface.NewGamePage.NewGamePanel;
import me.ippolitov.fit.snakes.SnakesProto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InterfaceController {
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

    private GlobalController controller;

    private ArrayList<SnakesProto.GameMessage.AnnouncementMsg> multicastMessages;

    public InterfaceController(GlobalController controller){
        multicastMessages = new ArrayList<>();
        window = new Window(sizeWidth,sizeHeight,locationX,locationY);
        menuPanel = new MenuPanel(sizeWidth,sizeHeight);
        newGamePanel = new NewGamePanel();
        gamePanel = new GamePanel();
        connectionPanel = new ConnectionPanel();
        netInfoEntryPanel = new NetInfoEntryPanel();
        this.controller = controller;

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
                        NetworkController.getInstance().setPort(w);

                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры для port!");
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
        connectionPanel.a.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              //  window.remove(connectionPanel);
              //  window.add(connectionPanel);
                window.revalidate();
                window.repaint();
            }
        });
    }

    private void initializationGameListeners() {
        gamePanel.backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.removeGame();
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
                        controller.setWidth(w);

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
                        controller.setHeight(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.foodStaticField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(newGamePanel.foodStaticField.getText());
                    if (w > 100 || w < 0) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 0 до 100, а не " + w);
                    }
                    else
                        controller.setFoodStatic(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.foodPerPlayerField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    float w = Float.parseFloat(newGamePanel.foodPerPlayerField.getText());
                    if (w > 100 || w < 0) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 0 до 100, а не " + w);
                    }
                    else
                        controller.setFoodPerPlayer(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели вещественное число!");
                }
            }
        });

        newGamePanel.stateDelayField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(newGamePanel.stateDelayField.getText());
                    if (w > 10000 || w < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 1 до 10000, а не " + w);
                    }
                    else
                        controller.setStateDelay(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.deadFoodProbField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    float w = Float.parseFloat(newGamePanel.deadFoodProbField.getText());
                    if (w > 1 || w < 0) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 0 до 1, а не " + w);
                    }
                    else
                        controller.setDeadFoodProb(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели вещественное число!");
                }
            }
        });

        newGamePanel.pingDelayField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(newGamePanel.pingDelayField.getText());
                    if (w > 10000 || w < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 1 до 10000, а не " + w);
                    }
                    else
                        controller.setPingDelay(w);
                } catch (NumberFormatException r) {
                    JOptionPane.showMessageDialog(window, "Вы некорректно ввели цифры!");
                }
            }
        });

        newGamePanel.nodeTimeoutField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(newGamePanel.nodeTimeoutField.getText());
                    if (w > 10000 || w < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 1 до 10000, а не " + w);
                    }
                    else
                        controller.setNodeTimeout(w);
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
                    window.remove(newGamePanel);
                    window.add(gamePanel);
                    controller.initilizationGame();
                    System.out.println(controller.getHeight());
                    System.out.println(controller.getWidth());
                    gamePanel.addGameField(controller.getWidth(),controller.getHeight());
                    gamePanel.setFocusable(true);
                    gamePanel.requestFocus();
                    window.revalidate();
                    window.repaint();
                }
            //}
        });
    }

    public synchronized void addNewConnectButton(SnakesProto.GameMessage.AnnouncementMsg message){
        if(message == null)
            return;

        if(multicastMessages.contains(message))
            return;

        multicastMessages.add(message);

        List<SnakesProto.GamePlayer> players = message.getPlayers().getPlayersList();
        String hostName ="unknown";
        for(int i = 0; i < players.size(); i ++){
            if(players.get(i).getId() == 0)                 //переделать по IP
                hostName = players.get(i).getName();
        }

        JButton button = new JButton("Ширина: " + message.getConfig().getWidth() + " " +
                "Длина: " + message.getConfig().getHeight() + " " +
                "StaticFood: " + message.getConfig().getFoodStatic() + " " +
                "FoodPerPlayer: " + message.getConfig().getFoodPerPlayer() + " " +
                "Delay: " + message.getConfig().getPingDelayMs() + " " +
                "Prob: " + message.getConfig().getDeadFoodProb() + " " +
                "Host: " + hostName + " " +
                "Can join: " + message.getCanJoin());
        /*
        актион листенеры
        !
         */

        connectionPanel.panel.add(button);
        window.revalidate();
        window.repaint();
    }

    public void repaintField(int[][] a){
        gamePanel.gameField.repaintField(a,controller.getWidth(),controller.getHeight(),1);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
    }
}
