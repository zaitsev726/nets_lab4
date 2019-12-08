package SnakeGame;

import NetworkPart.NetSocket.SteerMsgQueue;
import me.ippolitov.fit.snakes.SnakesProto;
import me.ippolitov.fit.snakes.SnakesProto.GamePlayer;
import me.ippolitov.fit.snakes.SnakesProto.GameState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameLogic {
    /*
    геймлоджик отвечает за логику игры ( возможно надо переименовать)
    > делает новый шаг
    > добавляет яблоки
    > убивает змей и превращает их в яблоки
    ресурсы: списко игроков, список змейк, геймфилд
     */
    private static int width;
    private static int height;
    private int foodStatic;
    private float foodPerPlayer;
    private float deadFoodProb;

    private static int[][] gameField;
    private int stateOrder = 1;

    private List<Event> crashHeads = new ArrayList<>();
    private List<Event> appleHeads = new ArrayList<>();
    private List<Integer> deadSnakes = new ArrayList<>();
    private List<Event> apples = new ArrayList<>();

    public GameLogic(int width, int height, int foodStatic, float foodPerPlayer,
                      float deadFoodProb) {
        /*create your game*/

        GameLogic.width = width;
        GameLogic.height = height;
        this.foodStatic = foodStatic;
        this.foodPerPlayer = foodPerPlayer;
        this.deadFoodProb = deadFoodProb;

        gameField = new int[width][height];
        stateOrder = 1;
        crashHeads = new ArrayList<>();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                gameField[i][j] = 0;
    }

    //новый шаг
    public void makeNextStep() {
        Players.getInstance().updatePlayers();
        ArrayList<GamePlayer> players = Players.getInstance().getPlayers();
        List<GameState.Snake> snakes = Players.getInstance().getSnakes();
        List<GameState.Snake> updatedSnakes = new ArrayList<>();
        Map<Integer, SnakesProto.Direction> newDirections = SteerMsgQueue.getInstance().getMap();

        GameState.Snake snake = null;

        for (int k = 0; k < snakes.size(); k++) {
            snake = snakes.get(k);
            if (snake == null)
                continue;
            int tail = snake.getPlayerId() + 1;
            int head = -tail;

            int i = snake.getPoints(0).getX();
            int j = snake.getPoints(0).getY();

            gameField[i][j] = tail;
            SnakesProto.Direction direction;
            if (newDirections.containsKey(snake.getPlayerId())) {
                //если повернули то надо добавить "угол"
                GameState.Snake.Builder builder = snake.toBuilder();
                List<GameState.Coord> coords = builder.getPointsList();
                direction = newDirections.get(snake.getPlayerId());
                switch (direction) {
                    case UP:
                        coords.add(1, GameState.Coord.newBuilder().setX(0).setY(1).build());
                    case DOWN:
                        coords.add(1, GameState.Coord.newBuilder().setX(0).setY(-1).build());
                    case LEFT:
                        coords.add(1, GameState.Coord.newBuilder().setX(1).setY(0).build());
                    case RIGHT:
                        coords.add(1, GameState.Coord.newBuilder().setX(-1).setY(0).build());
                }
                builder.clearPoints();
                for (int p = 0; p < coords.size(); p++) {
                    builder.addPoints(coords.get(p));
                }
                snake = builder.build();
            } else {
                direction = snake.getHeadDirection();
            }
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

            if (gameField[i][j] > 1 || gameField[i][j] < -1) {
                crashHeads.add(new Event(i, j, head));
            }
            if (gameField[i][j] == 1) {
                appleHeads.add(new Event(i, j, head));
                Iterator<Event> iterator = apples.iterator();
                while (iterator.hasNext()) {
                    Event e = iterator.next();
                    if (e.getX() == i && e.getY() == j)
                        iterator.remove();
                }

                GameState.Snake.Builder builder = snake.toBuilder();
                builder.setPoints(0, GameState.Coord.newBuilder().setX(i).setY(j).build());
                snake = builder.build();
                gameField[i][j] = head;
            }
            if (gameField[i][j] == 0) {
                GameState.Snake.Builder builder = snake.toBuilder();
                builder.setPoints(0, GameState.Coord.newBuilder().setX(i).setY(j).build());
                snake = builder.build();
                gameField[i][j] = head;
            }
            updatedSnakes.add(snake);
            //добавлять снейки в новый лист !!
        }
    for(int i = 0; i < width; i ++){
        for(int j = 0; j < height; j ++){
            System.out.print(gameField[i][j] + " ");
        }
        System.out.println("");
    }
        checkDeadHeadToHead();
        checkDeadHeadToTail();
        moveTail( updatedSnakes, newDirections);
        checkLastDead(snakes);
        removeDeadSnakes(updatedSnakes, players);
        addApples(players);
        // setCoords(updatedSnakes);
        Players.getInstance().setSnakes(updatedSnakes);
    }


    //проверяем голову с головой
    private void checkDeadHeadToHead() {
        Iterator<Event> iterator = crashHeads.iterator();
        while (iterator.hasNext()) {
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();

            if (gameField[i][j] < 0 && gameField[i][j] != head) {
                int head2 = gameField[i][j];
                deadSnakes.add(head);
                if (!deadSnakes.contains(head2))
                    deadSnakes.add(head2);
                iterator.remove();
            }
        }
    }

    //проверяем голову с туловищем
    private void checkDeadHeadToTail() {
        Iterator<Event> iterator = crashHeads.iterator();
        while (iterator.hasNext()) {
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();

            if (gameField[i][j] > 1) {
                int tail2 = gameField[i][j];
                if (deadSnakes.contains(-tail2)) {
                    deadSnakes.add(head);
                    iterator.remove();
                }
            }
        }
    }

    //перемещаем тела
    private void moveTail(List<GameState.Snake> snakes, Map<Integer, SnakesProto.Direction> map) {
        for (int k = 0; k < snakes.size(); k++) {
            GameState.Snake snake = snakes.get(k);
            if (snake == null)
                continue;
            int tail = snake.getPlayerId() + 1;
            int head = 0 - tail;
            boolean appleHead = false;
            if (!deadSnakes.contains(head)) {

                for (int i = 0; i < appleHeads.size(); i++) {
                    if (head == appleHeads.get(i).getHost_head()) {
                        //если скушали яблоко
                        appleHead = true;
                        SnakesProto.Direction direction = snake.getHeadDirection();
                        if (!map.containsKey(snake.getPlayerId())){
                            //хвост остается на месте после съедения яблока
                            switch (direction) {
                                case UP:
                                    snake = snake.toBuilder().setPoints(1, GameState.Coord.newBuilder()
                                            .setX(0)
                                            .setY(snake.getPoints(1).getY() + 1).build()).build();
                                case DOWN:
                                    snake = snake.toBuilder().setPoints(1, GameState.Coord.newBuilder()
                                            .setX(0)
                                            .setY(snake.getPoints(1).getY() - 1).build()).build();
                                case RIGHT:
                                    snake = snake.toBuilder().setPoints(1, GameState.Coord.newBuilder()
                                            .setX(snake.getPoints(1).getX() - 1)
                                            .setY(0).build()).build();
                                case LEFT:
                                    snake = snake.toBuilder().setPoints(1, GameState.Coord.newBuilder()
                                            .setX(snake.getPoints(1).getX() + 1)
                                            .setY(0).build()).build();
                            }
                        }
                    }
                }
                if (!appleHead) {
                    //если не скушали яблоко
                    List<GameState.Coord> coords = snake.getPointsList();

                    GameState.Coord lastPointOfTail = coords.get(coords.size() - 1);
                    int x = lastPointOfTail.getX();
                    int y = lastPointOfTail.getY();
                    //!(x == 0 && y == 0)
                    if (x > 0)
                        x--;
                    if (x < 0)
                        x++;
                    if (y > 0)
                        y--;
                    if (y < 0)
                        y++;
                    int l = coords.size()-1;
                    coords.remove(l);
                    if (!(x == 0 && y == 0))
                        coords.add(GameState.Coord.newBuilder().setX(x).setY(y).build());

                }
            }
        }
    }

    //еще раз проверяем мертвые головы
    private void checkLastDead(List<GameState.Snake> snakes) {
        Iterator<Event> iterator = crashHeads.iterator();
        while (iterator.hasNext()) {
            Event e = iterator.next();
            int i = e.getX();
            int j = e.getY();
            int head = e.getHost_head();
            if (gameField[i][j] > 1 || gameField[i][j] < -1){
                deadSnakes.add(head);
                iterator.remove();
            }
            else{
            /*
            добавить постановку головы на поле
             */
                for (GameState.Snake snake : snakes) {
                    if (snake.getPlayerId() == ((-head) - 1)) {
                        snake.toBuilder().setPoints(0, GameState.Coord.newBuilder()
                                .setX(i)
                                .setY(j)
                                .build()).build();
                    }
                }
            }
        }
    }

    //удаляем мертвых змей
    private void removeDeadSnakes(List<GameState.Snake> snakes, ArrayList<GamePlayer> players) {

        //восстановление яблок
        for (Event appleHead : appleHeads) {
            if (deadSnakes.contains(appleHead.getHost_head())) {
                gameField[appleHead.getX()][appleHead.getY()] = 1;
                apples.add(new Event(appleHead.getX(), appleHead.getY(), 0));
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < deadSnakes.size(); k++) {
                    if (gameField[i][j] == deadSnakes.get(k)) {
                        if (Math.random() > deadFoodProb) {
                            gameField[i][j] = 1;
                            apples.add(new Event(i, j, 0));
                        } else
                            gameField[i][j] = 0;
                    }
                }
            }
        }

        Iterator<GameState.Snake> iterator = snakes.iterator();
        while (iterator.hasNext()) {
            GameState.Snake snake = iterator.next();
            int ID = snake.getPlayerId() + 1;
            for (int i = 0; i < deadSnakes.size(); i++) {
                if (ID == deadSnakes.get(i))
                    iterator.remove();
                Iterator<GamePlayer> iter = players.iterator();
                while (iter.hasNext()) {
                    GamePlayer player = iter.next();
                    if ((player.getId() + 1) == ID) {
                        iter.remove();
                    }
                }
            }
        }
        Players.getInstance().setPlayers(players);
    }

    //добавляем яблоки
    private void addApples(List<GamePlayer> players) {
        while (apples.size() < (players.size() * foodPerPlayer + foodStatic)) {
            int x = (int) (Math.random() * width), y = (int) (Math.random() * height);
            while (gameField[x][y] != 0) {
                x = (int) (Math.random() * width);
                y = (int) (Math.random() * height);
            }
            gameField[x][y] = 1;
            apples.add(new Event(x, y, 0));
        }
    }


    //создаем новый стейт
    public GameState createNewState(int delay, int pingDelay, int nodeTimeout) {
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
        for (int i = 0; i < list.size(); i++) {
            playersBuilder.addPlayers(list.get(i));
        }

        List<GameState.Snake> list2 = Players.getInstance().getSnakes();
        GameState.Builder stateBuilder = GameState.newBuilder();

        for (int i = 0; i < list2.size(); i++) {
            stateBuilder.addSnakes(list2.get(i));
        }

        stateBuilder.setStateOrder(stateOrder)
                .setConfig(config)
                .setPlayers(playersBuilder.build());
        stateOrder++;
        for (int i = 0; i < apples.size(); i++) {
            stateBuilder.addFoods(GameState.Coord.newBuilder()
                    .setX(apples.get(i).getX())
                    .setY(apples.get(i).getY()));
        }

        return stateBuilder.build();
    }
}