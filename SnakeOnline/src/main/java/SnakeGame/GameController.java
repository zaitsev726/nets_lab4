package SnakeGame;

import me.ippolitov.fit.snakes.SnakesProto;
import me.ippolitov.fit.snakes.SnakesProto.GameMessage;
import me.ippolitov.fit.snakes.SnakesProto.GamePlayer;
import me.ippolitov.fit.snakes.SnakesProto.GameState;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    private static int width;
    private static int height;
    private int foodStatic;
    private float foodPerPlayer;
    private int delay;
    private float deadFoodProb;
    private int pingDelay;
    private int nodeTimeout;

    private static int[][] gameField;
    private int stateOrder;

    private static boolean owner = false;
    private static String host_IP = "";

    private List<Event> crashHeads = new ArrayList<>();
    private List<Event> appleHeads = new ArrayList<>();
    private List<Integer> deadSnakes = new ArrayList<>();
    private List<Event> apples = new ArrayList<>();


    public GameController(GameMessage.AnnouncementMsg connect, InetAddress IP) {
        /*connection*/

        Players.getInstance().setPlayers((ArrayList<GamePlayer>) connect.getPlayers().getPlayersList());
        width = connect.getConfig().getWidth();
        height = connect.getConfig().getHeight();
        owner = false;
        host_IP = IP.toString();

    }

    public GameController(int width, int height, int foodStatic, float foodPerPlayer,
                          int delay, float deadFoodProb, int pingDelay, int nodeTimeout) {
        /*create your game*/

        GameController.width = width;
        GameController.height = height;
        this.foodStatic = foodStatic;
        this.foodPerPlayer = foodPerPlayer;
        this.delay = delay;
        this.deadFoodProb = deadFoodProb;
        this.pingDelay = pingDelay;
        this.nodeTimeout = nodeTimeout;

        gameField = new int[width][height];
        stateOrder = 1;
        crashHeads = new ArrayList<>();
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

    /*возващает координаты для 1 змеи*/
    public static int[] randomCoord() {
        int[] a = new int[2];
        a[0] = (int) (Math.random() * width);
        a[1] = (int) (Math.random() * height);
        return a;
    }

    /*возвращает координаты куда можно вставить змею или пустой массив*/
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

    /*рисует змею на экране*/
    public static void paintSnake(GameState.Snake snake) {
        List coords = snake.getPointsList();
        int ID = snake.getPlayerId();

        int tail = ID++;
        int head = 0 - tail;


        if (snake.getPlayerId() == ID) {
            head += 2;
            tail += 2;
        }

        GameState.Coord coord1 = (GameState.Coord) coords.get(0);
        gameField[coord1.getX()][coord1.getY()] = head;
        for (int i = 1; i < coords.size(); i++) {
            /*координаты относительные должны быть!*/
            GameState.Coord coord2 = (GameState.Coord) coords.get(i);
            int x1 = coord1.getX();
            int y1 = coord1.getY();
            int x2 = coord2.getX();
            int y2 = coord2.getY();
            while (x1 != x2 && y1 != y2) {
                if (x1 == x2) {
                    if (y1 > y2) {
                        y1--;
                    } else {
                        y1++;
                    }
                }
                if (y1 == y2) {
                    if (x1 > x2) {
                        x1--;
                    } else {
                        x1++;
                    }
                }
                gameField[x1][y1] = tail;

            }
            coord1 = coord2;
        }
    }

    public void makeNextStep() {
        List snakes = Players.getInstance().getSnakes();
        ArrayList<GamePlayer> players = Players.getInstance().getPlayers();
        GameState.Snake snake = null;

        for (int k = 0; k < snakes.size(); k++) {
            snake = (GameState.Snake) snakes.get(k);
            if (snake == null)
                continue;
            int tail = snake.getPlayerId() + 1;
            int head = 0 - tail;

            int i = snake.getPoints(0).getX();
            int j = snake.getPoints(0).getY();

            gameField[i][j] = tail;
            SnakesProto.Direction direction = snake.getHeadDirection();
            switch (direction) {
                case UP:
                    j--;
                    if (j < 0)
                        j = height - 1;
                case LEFT:
                    i--;
                    if (i < 0)
                        i = width - 1;
                case DOWN:
                    j++;
                    if (j >= height)
                        j = 0;
                case RIGHT:
                    i++;
                    if (i >= width)
                        i = 0;
            }

            if (gameField[i][j] > 1) {
                crashHeads.add(new Event(i, j, head));
            }
            if (gameField[i][j] == 1) {
                appleHeads.add(new Event(i, j, head));
                Iterator<Event> iterator = apples.iterator();
                while (iterator.hasNext()){
                    Event e = iterator.next();
                    if(e.getX() == i && e.getY() == j)
                        apples.remove(e);
                }

                gameField[i][j] = head;         //добавить голову?
            }
            if (gameField[i][j] == 0) {
                GameState.Snake.Builder builder = snake.toBuilder();
                builder.setPoints(0,GameState.Coord.newBuilder().setX(i).setY(j).build());
                snake = builder.build();
                gameField[i][j] = head;
            }

        }

        checkDeadHeadToHead();
        checkDeadHeadToTail();
        moveTail(snakes);
        checkLastDead();
        removeDeadSnakes(snakes, players);
        addApples(players);
        createNewState();
        setCoords(snakes);
    }

    private void checkDeadHeadToHead(){
        Iterator<Event> iterator = crashHeads.iterator();
        while (iterator.hasNext()){
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();

            if(gameField[i][j] < 0 && gameField[i][j] != head){
                int head2 = gameField[i][j];
                deadSnakes.add(head);
                if(!deadSnakes.contains(head2))
                    deadSnakes.add(head2);
                crashHeads.remove(e);
            }
        }
    }

    private void checkDeadHeadToTail(){
        Iterator<Event> iterator = crashHeads.iterator();
        while(iterator.hasNext()){
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();

            if(gameField[i][j] > 1){
                int tail2 = gameField[i][j];
                if(deadSnakes.contains(-tail2)){
                    deadSnakes.add(head);
                    crashHeads.remove(e);
                }
            }
        }
    }

    private void moveTail(List<GameState.Snake> snakes){
        for (int k = 0; k < snakes.size(); k++) {
            GameState.Snake snake = snakes.get(k);
            if (snake == null)
                continue;
            int tail = snake.getPlayerId() + 1;
            int head = 0 - tail;
            boolean appleHead = false;
            if(!deadSnakes.contains(head)){

                for(int i = 0; i < appleHeads.size(); i++){
                    if(head == appleHeads.get(i).getHost_head()) {
                        appleHead = true;
                        continue;
                    }//выйдет ли из цикла???
                }
                if(!appleHead) {

                    List<GameState.Coord> coords = snake.getPointsList();
                    GameState.Coord c = coords.get(coords.size()-1);
                    int x = c.getX();
                    int y = c.getY();

                    coords.remove(c);

                    gameField[x][y] = 0;

                    if(gameField[x-1][y] == tail){
                        x--;
                    }else if(gameField[x+1][y] == tail){
                        x++;
                    }else if (gameField[x][y-1] == tail){
                        y--;
                    }else if (gameField[x][y+1] == tail){
                        y++;
                    }

                    GameState.Coord a = GameState.Coord.newBuilder()
                            .setX(x)
                            .setY(y)
                            .build();

                    GameState.Coord prev = coords.get(coords.size() -2 );

                    if(prev.getX() > 0){
                        if(prev.getX() > x){
                            coords.remove(prev);
                            prev = null;
                        }
                    }
                    if(prev.getX() < 0){
                        if(prev.getX() < x){
                            coords.remove(prev);
                            prev = null;
                        }
                    }
                    if(prev.getY() > 0){
                        if(prev.getY() > y){
                            coords.remove(prev);
                            prev = null;
                        }
                    }

                    if(prev.getY() < 0){
                        if(prev.getY() < y){
                            coords.remove(prev);
                            prev = null;
                        }
                    }

                    if(!a.equals(prev))
                        coords.add(a);

                    gameField[x][y] = tail;         //там уже лежит tail вроде бы
                }
            }
        }
    }

    private void checkLastDead(){
        Iterator<Event> iterator = crashHeads.iterator();
        while (iterator.hasNext()){
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();

            if(gameField[i][j] > 1){
                deadSnakes.add(head);
                crashHeads.remove(e);
            }
        }
    }

    private void removeDeadSnakes(List<GameState.Snake> snakes,  ArrayList<GamePlayer> players ) {

        //восстановление яблок
        for(int i = 0; i < appleHeads.size(); i ++){
            if(deadSnakes.contains(appleHeads.get(i).getHost_head())){
                gameField[appleHeads.get(i).getX()][appleHeads.get(i).getY()] = 1;
                apples.add(new Event(appleHeads.get(i).getX(), appleHeads.get(i).getY(), 0));
            }
        }

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                for(int k = 0; k < deadSnakes.size(); k ++){
                    if(gameField[i][j] == deadSnakes.get(k)){
                        if(Math.random() > deadFoodProb){
                            gameField[i][j] = 1;
                            apples.add(new Event(i,j,0));
                        }
                        else
                            gameField[i][j] = 0;
                    }
                }
            }
        }

            Iterator<GameState.Snake> iterator = snakes.iterator();
        while(iterator.hasNext()){
            GameState.Snake snake = iterator.next();
            int ID = snake.getPlayerId()+1;
            for(int i = 0; i < deadSnakes.size(); i ++){
                if(ID == deadSnakes.get(i))
                    snakes.remove(snake);
                    Iterator<GamePlayer> iter = players.iterator();
                    while(iter.hasNext()){
                        GamePlayer player = iter.next();
                        if((player.getId() + 1) == ID){
                            players.remove(player);
                        }
                    }
            }
        }
    }

    private void addApples(List<GamePlayer> players){
        while(apples.size() < (players.size()*foodPerPlayer + foodStatic )){
            int x = (int) (Math.random()*width), y = (int) (Math.random()*height);
            while (gameField[x][y] != 0){
            x = (int) (Math.random()*width);
            y = (int) (Math.random()*height);
            }
            gameField[x][y] = 1;
            apples.add(new Event(x,y,0));
        }
    }



    private void setCoords(List<GameState.Snake> snakes){
        for(int k = 0; k < snakes.size(); k++){
            GameState.Snake.Builder snake = snakes.get(k).toBuilder();
            int tail = snake.getPlayerId()+1;
            int head = 0 - tail;
            int score = 0;
            boolean nearby = true;
            int i = snake.getPoints(0).getX();
            int j = snake.getPoints(0).getY();

            SnakesProto.Direction direction = snake.getHeadDirection();
            snake.addPoints(GameState.Coord.newBuilder().setX(i).setY(j).build());

            /*
            поменять направление на противоположное
             */
            while (nearby){
                //чекнуть лист смены направлений
            }
        }

    }

    public GameState createNewState() {
        SnakesProto.GameConfig config = SnakesProto.GameConfig.newBuilder()
                .setWidth(width)
                .setHeight(height)
                .setFoodStatic(foodStatic)
                .setFoodPerPlayer(foodPerPlayer)
                .setStateDelayMs(delay)
                .setDeadFoodProb(deadFoodProb)
                .setPingDelayMs(pingDelay)
                .setNodeTimeoutMs(nodeTimeout)
                .build();

        List<GamePlayer> list = Players.getInstance().getPlayers();
        SnakesProto.GamePlayers.Builder playersBuilder = SnakesProto.GamePlayers.newBuilder();
        for(int i = 0; i < list.size(); i ++){
            playersBuilder.addPlayers(list.get(i));
        }

        List<GameState.Snake> list2 = Players.getInstance().getSnakes();
        GameState.Builder stateBuilder = GameState.newBuilder();

        for(int i = 0; i < list2.size(); i++){
            stateBuilder.addSnakes(list2.get(i));
        }

        stateBuilder.setStateOrder(stateOrder)
                    .setConfig(config)
                    .setPlayers(playersBuilder.build());

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
