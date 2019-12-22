package UserInterface;

import Global.GlobalController;
import UserInterface.ConnectionPage.ConnectionPanel;
import UserInterface.Frames.Window;
import UserInterface.GamePage.GamePanel;
import UserInterface.MenuPage.MenuPanel;
import UserInterface.NetInfoEntryPage.NetInfoEntryPanel;
import UserInterface.NewGamePage.NewGamePanel;
import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto.GameMessage;
import me.ippolitov.fit.snakes.SnakesProto.GamePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.util.List;
import java.util.*;

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

    private HashMap<GameMessage.AnnouncementMsg, DatagramPacket> multicastMessages;
    private HashMap<GameMessage.AnnouncementMsg, JButton> connectButtons;
    private HashMap<GameMessage.AnnouncementMsg, Date> lastDate;
    private HashMap<JButton, DatagramPacket> hosts;

    public InterfaceController(GlobalController controller) {
        multicastMessages = new HashMap<>();
        connectButtons = new HashMap<>();
        lastDate = new HashMap<>();
        hosts = new HashMap<>();

        window = new Window(sizeWidth, sizeHeight, locationX, locationY);
        menuPanel = new MenuPanel(sizeWidth, sizeHeight);
        newGamePanel = new NewGamePanel();
        gamePanel = new GamePanel(controller);
        connectionPanel = new ConnectionPanel();
        netInfoEntryPanel = new NetInfoEntryPanel();
        this.controller = controller;

        window.add(menuPanel);
        initializationListeners();
        window.setVisible(true);
    }

    private void initializationListeners() {
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
                    if (w > 49151 || w < 1024) {
                        JOptionPane.showMessageDialog(window,
                                "Введите число в диапазоне от 1024 до 49151, а не " + w);
                    } else
                        controller.setPort(w);

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
                controller.sendRoleChange();
                controller.removeGame();
                window.remove(gamePanel);
                window.add(connectionPanel);
                window.revalidate();
                window.repaint();
            }
        });
        gamePanel.viewModButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.masterExit();    //заменить на любой чел выходит!
                controller.setOurId(0);
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
                    } else {
                        controller.setWidth(w);
                    }
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
                    } else
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
                    } else
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
                    } else
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
                    } else
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
                    } else
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
                    } else
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
                    } else
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
                controller.initializationGame();
                System.out.println(controller.getHeight());
                System.out.println(controller.getWidth());
                gamePanel.addGameField(controller.getWidth(), controller.getHeight());
                gamePanel.setFocusable(true);
                gamePanel.requestFocus();
                window.revalidate();
                window.repaint();
            }
            //}
        });
    }

    //вот тут какой то велосипед изобретаю
    public synchronized void addNewConnectButton(DatagramPacket dp) {

        byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());
        GameMessage.AnnouncementMsg message = null;
        try {
            message = GameMessage.parseFrom(a1).getAnnouncement();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        if (message == null)
            return;

        if (multicastMessages.containsKey(message)) {
            lastDate.put(message,new Date());
            return;
        }

        Iterator<Map.Entry<GameMessage.AnnouncementMsg, DatagramPacket>> iterator
                = multicastMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<GameMessage.AnnouncementMsg, DatagramPacket> next = iterator.next();
            if (next.getValue().getAddress().equals(dp.getAddress()) &&
                    next.getValue().getPort() == dp.getPort()) {
                iterator.remove();

                hosts.remove(connectButtons.get(next.getKey()));
                connectionPanel.panel.remove(connectButtons.get(next.getKey()));
                connectButtons.remove(next.getKey());
                lastDate.remove(next.getKey());
            }
        }

        multicastMessages.put(message, dp);
        lastDate.put(message,new Date());

        List<GamePlayer> players = message.getPlayers().getPlayersList();
        String hostName = "unknown";
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == 0)                 //переделать по IP
                hostName = players.get(i).getName();
        }

        final JButton button = new JButton("Ширина: " + message.getConfig().getWidth() + " " +
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

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatagramPacket dp = hosts.get(button);
                byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());
                GameMessage.AnnouncementMsg message = null;
                try {
                    message = GameMessage.parseFrom(a1).getAnnouncement();
                } catch (InvalidProtocolBufferException m) {
                    m.printStackTrace();
                }
                controller.initializationConnect(dp);
                window.remove(connectionPanel);
                window.add(gamePanel);

                gamePanel.addGameField(message.getConfig().getWidth(),message.getConfig().getHeight());
                gamePanel.setFocusable(true);
                gamePanel.requestFocus();
                window.revalidate();
                window.repaint();
            }
        });

        connectButtons.put(message, button);
        hosts.put(button,dp);

        connectionPanel.panel.add(button);
        window.revalidate();
        window.repaint();
    }

    public synchronized void removeButton() {
        Iterator<Map.Entry<GameMessage.AnnouncementMsg, Date>> iterator = lastDate.entrySet().iterator();
        Date d = new Date();
        while (iterator.hasNext()) {
            Map.Entry<GameMessage.AnnouncementMsg, Date> next = iterator.next();
            if (d.getTime() - next.getValue().getTime() > 5000) {
                connectionPanel.panel.remove(connectButtons.get(next.getKey()));
                hosts.remove(connectButtons.get(next.getKey()));
                connectButtons.remove(next.getKey());
                multicastMessages.remove(next.getKey());
                iterator.remove();
                window.revalidate();
                window.repaint();
            }
        }
    }

    public void repaintField(int[][] a, int width, int height, int ID, List<GamePlayer> players) {
      //  gamePanel.repaintScore(players);
        gamePanel.repaintScore(players);
        gamePanel.gameField.repaintField(a, width, height, ID);

        gamePanel.repaint();
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(window, message);
    }
}
