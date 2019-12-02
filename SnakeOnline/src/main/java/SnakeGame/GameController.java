package SnakeGame;

import NetworkPart.GlobalController;
import me.ippolitov.fit.snakes.SnakesProto.GameMessage;
import me.ippolitov.fit.snakes.SnakesProto.GamePlayer;
import me.ippolitov.fit.snakes.SnakesProto.GameState;

import java.net.InetAddress;
import java.util.ArrayList;

public class GameController {
    private static int width;
    private static int height;
    private int foodStatic;
    private float foodPerPlayer;
    private int delay;
    private float deadFoodProb;

    private static int[][] gameField;


    private static boolean owner = false;
    private static String host_IP = "";

    public GameController(GameMessage.AnnouncementMsg connect, InetAddress IP) {
        /*connection*/

        Players.getInstance().setPlayers((ArrayList<GamePlayer>) connect.getPlayers().getPlayersList());
        width = connect.getConfig().getWidth();
        height = connect.getConfig().getHeight();
        owner = false;
        host_IP = IP.toString();

    }

    public GameController(int width, int height, int foodStatic,
                          float foodPerPlayer, int delay, float deadFoodProb) {
        /*create your game*/
        GameController.width = width;
        GameController.height = height;
        this.foodStatic = foodStatic;
        this.foodPerPlayer = foodPerPlayer;
        this.delay = delay;
        this.deadFoodProb = deadFoodProb;
        gameField = new int[width][height];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                gameField[i][j] = 0;

        owner = true;
        host_IP = "";

    }

    public static boolean getOwner() {
        return owner;
    }

    public static String getHost_IP() {
        return host_IP;
    }

    public static int[] randomCoord() {
        int[] a = new int[2];
        a[0] = (int) (Math.random() * width);
        a[1] = (int) (Math.random() * height);
        return a;
    }

    public static int[] getCoord() {
        //Map<int[], Integer> map = new HashMap<>();
        int[] a = new int[2];

        boolean canSpawn = false;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (gameField[i][j] == 0) {
                    for (int k = 0; k < 5; k++) {
                        for (int m = 0; m < 5; m++) {
                            if (gameField[k][m] != 0)
                                canSpawn = true;
                        }
                    }
                    if (canSpawn) {
                        a[0] = i + 3;
                        a[1] = j + 2;
                        return a;
                    }
                }
            }
        }
        return null;
    }

    public static void addSnake(GameState.Snake snake) {
        ArrayList coords = (ArrayList) snake.getPointsList();
        int ID = 0;
        for (int i = 0; i < Players.getInstance().getPlayers().size(); i++) {
            if (Players.getInstance().getPlayers().get(i).getIpAddress().equals(GlobalController.getInstance().getIP().toString()) &&
                    Players.getInstance().getPlayers().get(i).getPort() == GlobalController.getInstance().getPort())
                ID = Players.getInstance().getPlayers().get(i).getId();
            /*костыльный метод */
        }
        int head = 1;
        int tail = 2;

        if (snake.getPlayerId() == ID) {
            head += 2;
            tail += 2;
        }

        GameState.Coord coord1 = (GameState.Coord) coords.get(0);
        gameField[coord1.getX()][coord1.getY()] = head;
        for (int i = 1; i < coords.size(); i++) {
            GameState.Coord coord2 = (GameState.Coord) coords.get(i);
            int x1 = coord1.getX();
            int y1 = coord1.getY();
            int x2 = coord2.getX();
            int y2 = coord2.getY();
            while (x1 != x2 && y1 != y2) {
                if (x1 == x2) {
                    if (y1 > y2) {
                        y1--;
                    }
                    else {
                        y1++;
                    }
                }
                if (y1 == y2) {
                    if (x1 > x2) {
                        x1--;
                    }
                    else {
                        x1++;
                    }
                }
                gameField[x1][y1] = tail;

            }
            coord1 = coord2;
        }
    }

    /*
        1-голова обычной змеи
        2-тело обычной змеи
        3-голова нашей змеи
        4-телонашейзмеи
     */

    public GameState createNewState() {

        return null;
    }
    /*
    System.out.println("ширина поля: " + config.getWidth());
                System.out.println("высота поля: " + config.getHeight());
                System.out.println("статичное количество еды: " + config.getFoodStatic());
                System.out.println("количество еды на каждого игрока :" + config.getFoodPerPlayer());
                System.out.println("задержка: " + config.getDelayMs());
                System.out.println("вероятность: " + config.getDeadFoodProb());*/
}
