package SnakeGame;

import MessageProcessing.MessageCreator;
import NetworkPart.GlobalController;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Players {
    /*
    плейрс отвечает за хранение змей и игроков
    > должен добавлять новых игроков в спискок
    > должен добавлять новых змей в список, и запрашивать их отрисовку
    > назначает роли игрокам
     */
    private static Players instance;
    private ArrayList<SnakesProto.GamePlayer> players = new ArrayList<>();
    private List<SnakesProto.GameState.Snake> snakes = new ArrayList<>();
    private int ID = 1;
    private boolean hasDeputy = false;

    public static Players getInstance(){
        Players localInstance = instance;
        if(localInstance == null){
            synchronized (Players.class){
                localInstance = instance;
                if(localInstance == null)
                    instance = localInstance = new Players();
            }
        }
        return localInstance;
    }

    public void addNewPlayer(){
        players.add(SnakesProto.GamePlayer.newBuilder()
                .setName("как то получить имя")
                .setId(ID)
                .setIpAddress("")
                .setPort(GlobalController.getInstance().getPort())
                .setRole(SnakesProto.NodeRole.MASTER)
                .setScore(2)
                .build());

        int[] randomCoord = GameField.randomCoord();
        createNewSnake(randomCoord);

        ID++;
    }

    public void addNewPlayer(SnakesProto.GameMessage.JoinMsg join, InetAddress address, int port, long msg_seq) {
        if (GameController.getOwner()) {
            SnakesProto.NodeRole role;
            if (join.getOnlyView())
                role = SnakesProto.NodeRole.VIEWER;
            else if (hasDeputy)
                role = SnakesProto.NodeRole.NORMAL;
            else {
                role = SnakesProto.NodeRole.DEPUTY;
                hasDeputy = true;
            }

            players.add(SnakesProto.GamePlayer.newBuilder()
                    .setName(join.getName())
                    .setId(ID)
                    .setIpAddress(address.toString())
                    .setPort(port)
                    .setRole(role)
                    .setType(join.getPlayerType())
                    .setScore(2)
                    .build());

            int [] a = GameField.getCoordForSpawn();
            createNewSnake(a);

            MessageCreator.createNewAckMsg(msg_seq,ID);
            ID++;
        }
    }

    private void createNewSnake(int[] a) {
        SnakesProto.Direction direction = randomDirection();
        int [] b = tailCoord(a, direction);
        if(b == null)
            return;
        SnakesProto.GameState.Snake snake = SnakesProto.GameState.Snake.newBuilder()
                .setPlayerId(ID)
                .setHeadDirection(direction)
                .setState(SnakesProto.GameState.Snake.SnakeState.ALIVE)
                .addPoints(coord(a[0],a[1]))
                .addPoints(coord(b[0],b[1]))
                .build();

        snakes.add(snake);

        GameField.paintNewSnake(snake);
    }

    public void setPlayers(ArrayList<SnakesProto.GamePlayer> p){
        players = p;
    }
    public void setSnakes(List<SnakesProto.GameState.Snake> p){snakes = p;}
    public ArrayList<SnakesProto.GamePlayer> getPlayers() {
        return players;
    }
    public List<SnakesProto.GameState.Snake> getSnakes() { return snakes; }
    private SnakesProto.GameState.Coord coord(int x, int y){ return SnakesProto.GameState.Coord.newBuilder().setX(x).setY(y).build(); }

    private SnakesProto.Direction randomDirection(){
        int random = (int) (Math.random() * 4);
        SnakesProto.Direction d;
        switch (random) {
            case 0:
                d = SnakesProto.Direction.UP;
                break;
            case 1:
                d = SnakesProto.Direction.DOWN;
                break;
            case 2:
                d = SnakesProto.Direction.LEFT;
                break;
            case 3:
                d = SnakesProto.Direction.RIGHT;
                break;
            default:
                d = SnakesProto.Direction.RIGHT;
        }
        return d;
    }

    private int[] tailCoord(int[] head, SnakesProto.Direction d){
        int[] a = new int[2];
        switch (d){
            case UP:
                a[1] ++;
                return a;
            case DOWN:
                a[1] --;
                return a;
            case LEFT:
                a[0] ++;
                return a;
            case RIGHT:
                a[0] --;
                return a;
        }
        return null;
    }
}
