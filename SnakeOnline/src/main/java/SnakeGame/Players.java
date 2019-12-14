package SnakeGame;

import MessageProcessing.MessageCreator;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Players {
    /*
    плейрс отвечает за хранение змей и игроков
    > должен добавлять новых игроков в спискок
    > должен добавлять новых змей в список, и запрашивать их отрисовку
    > назначает роли игрокам
     */
    private static Players instance;
    private List<SnakesProto.GamePlayer> queuePlayers = new ArrayList<>();
    private List<SnakesProto.GamePlayer> roleChangePlayers = new ArrayList<>();
    private List<SnakesProto.GameState.Snake> snakes = new ArrayList<>();
    private ArrayList<SnakesProto.GamePlayer> players = new ArrayList<>();
    private int ID = 1;
    private boolean hasDeputy = false;

    public static Players getInstance() {
        Players localInstance = instance;
        if (localInstance == null) {
            synchronized (Players.class) {
                localInstance = instance;
                if (localInstance == null)
                    instance = localInstance = new Players();
            }
        }
        return localInstance;
    }

    public void addNewPlayerInQueue() {
        try {
            queuePlayers.add(SnakesProto.GamePlayer.newBuilder()
                    .setName("как то получить имя")
                    .setId(0)
                    .setIpAddress(InetAddress.getByName("").toString())
                    .setPort(1)
                    .setRole(SnakesProto.NodeRole.MASTER)
                    .setScore(2)
                    .build());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public void addNewPlayerInQueue(SnakesProto.GameMessage.JoinMsg join, InetAddress address, int port, long msg_seq) {

        SnakesProto.NodeRole role;
        if (join.getOnlyView())
            role = SnakesProto.NodeRole.VIEWER;
        else if (hasDeputy)
            role = SnakesProto.NodeRole.NORMAL;
        else {
            role = SnakesProto.NodeRole.DEPUTY;
            hasDeputy = true;
        }

        queuePlayers.add(SnakesProto.GamePlayer.newBuilder()
                .setName(join.getName())
                .setId(0)
                .setIpAddress(address.toString())
                .setPort(port)
                .setRole(role)
                .setType(join.getPlayerType())
                .setScore(2)
                .build());

        //  MessageCreator.createNewAckMsg(msg_seq,ID); ???
    }

    public boolean canJoin(){
        return GameField.getCoordForSpawn() != null;
    }
    public void updatePlayers() {
        Iterator<SnakesProto.GamePlayer> iterator = queuePlayers.iterator();
        while (iterator.hasNext()) {
            SnakesProto.GamePlayer player = iterator.next();
            int[] a = GameField.getCoordForSpawn();
            // int [] a = randomCoord
            //System.out.println(a[0] + " " + a[1]);
            if (a == null) {
                //отправка сообщений об ошибке
                //пофиксить трабл с заместителем
                queuePlayers.clear();
                return;
            } else {
                player = player.toBuilder().setId(ID).build();
                players.add(player);
                createNewSnake(a);

                ID++;
                iterator.remove();
            }
        }
    }

    private void createNewSnake(int[] a) {
        SnakesProto.Direction direction = randomDirection();
        int[] b = tailCoord(direction);
        if (b == null)
            return;
        SnakesProto.GameState.Snake snake = SnakesProto.GameState.Snake.newBuilder()
                .setPlayerId(ID)
                .setHeadDirection(direction)
                .setState(SnakesProto.GameState.Snake.SnakeState.ALIVE)
                .addPoints(coord(a[0], a[1]))
                .addPoints(coord(b[0], b[1]))
                .build();

        GameField.paintNewSnake(snake);
        snakes.add(snake);
    }

    public void updateRole(InetAddress address, int port){
        //вопрос норм ли если челик получил список, а мы такие оп и удалили чела из него
        //а там лежит старый м м м м ?
        Iterator<SnakesProto.GamePlayer> iterator = players.iterator();
        while (iterator.hasNext()){
            SnakesProto.GamePlayer player = iterator.next();
            if(player.getIpAddress().equals(address.toString()) &&
                player.getPort() == port){
                if(player.getRole().equals(SnakesProto.NodeRole.DEPUTY)) {
                    //отправляем ему что он Viewer теперь
                    MessageCreator.createNewRoleChangeMsg(SnakesProto.NodeRole.MASTER, SnakesProto.NodeRole.VIEWER,
                            player.getIpAddress(), player.getPort(),1,player.getId());//инфа об айдишнике?
                    hasDeputy = false;
                }
                //как выбрать нового заместителя?
                roleChangePlayers.add(player);
            }
        }
    }

    public void updateAllRoles(){
        Iterator<SnakesProto.GamePlayer> iterator = roleChangePlayers.iterator();
        while (iterator.hasNext()){
            SnakesProto.GamePlayer player = iterator.next();

            for (SnakesProto.GamePlayer gamePlayer : players) {
                if (gamePlayer.getPort() == player.getPort() &&
                        gamePlayer.getIpAddress().equals(player.getIpAddress())) {
                    if(Collections.replaceAll(
                            players,
                            player,
                            gamePlayer.toBuilder()
                                    .setRole(SnakesProto.NodeRole.VIEWER)
                                    .build())//ИЗМЕНИТЬ ИМЕННО ВОТ ЭТУ СКОБКУ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    ) {
                        int ID_player = gamePlayer.getId();
                        for (SnakesProto.GameState.Snake snake : snakes) {
                            if(snake.getPlayerId() == ID_player)
                                Collections.replaceAll(snakes,
                                        snake,
                                        snake.toBuilder()
                                                .setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE)
                                                .build());
                        }
                    }
                }
            }
        }
    }

    public void setPlayers(ArrayList<SnakesProto.GamePlayer> p) {
        players = p;
    }

    public void setSnakes(List<SnakesProto.GameState.Snake> p) {
        snakes = p;
    }

    public ArrayList<SnakesProto.GamePlayer> getPlayers() {
        return players;
    }

    public List<SnakesProto.GameState.Snake> getSnakes() {
        return snakes;
    }

    private SnakesProto.GameState.Coord coord(int x, int y) {
        return SnakesProto.GameState.Coord.newBuilder().setX(x).setY(y).build();
    }

    private SnakesProto.Direction randomDirection() {
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

    private int[] tailCoord(SnakesProto.Direction d) {
        int[] a = new int[2];
        switch (d) {
            case UP:
                a[1]++;
                return a;
            case DOWN:
                a[1]--;
                return a;
            case LEFT:
                a[0]++;
                return a;
            case RIGHT:
                a[0]--;
                return a;
        }
        return null;
    }
}
