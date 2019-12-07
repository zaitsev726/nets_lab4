package SnakeGame;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.List;

public class GameField {
    /*
    класс гейм филд отвечает за поле
    >отрисовка змейк
    >возвращение поля для отрисовки на экране
    >обнуление поля
    >смотрит есть ли свободное место для змеи
    ресуры: ничего извне
     */

    private static int[][] gameField = null;
    private static int width = 0;
    private static int height = 0;


    private GameField(int width, int height) {
        GameField.width = width;
        GameField.height = height;

        gameField = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gameField[i][j] = 0;
            }
        }
    }

    private static void paintSnake(SnakesProto.GameState.Snake snake) {
        List coords = snake.getPointsList();
        int ID = snake.getPlayerId();

        int tail = ID + 1;
        int head = 0 - tail;


        SnakesProto.GameState.Coord coord = (SnakesProto.GameState.Coord) coords.get(0);
        int currentX = coord.getX();
        int currentY = coord.getY();

        gameField[currentX][currentY] = head;

        for (int i = 1; i < coords.size(); i++) {
            coord = (SnakesProto.GameState.Coord) coords.get(i);
            int x = coord.getX();
            int y = coord.getY();
            //!(x == 0 && y == 0)
            while (x != 0 || y != 0) {
                if (x != 0) {
                    if (x > 0) {
                        currentX++;
                        x--;
                    }
                    if (x < 0) {
                        currentX--;
                        x++;
                    }
                }
                if (y != 0) {
                    if (y > 0) {
                        currentY++;
                        y--;
                    }
                    if (y < 0) {
                        currentY--;
                        y++;
                    }
                }
                gameField[currentX][currentY] = tail;
            }
        }


    }

    public static int[] randomCoord() {
        if (height == 0 || width == 0)
            return null;
        int[] a = new int[2];
        a[0] = (int) (Math.random() * width);
        a[1] = (int) (Math.random() * height);
        return a;
    }

    public static int[] getCoordForSpawn() {
        if (gameField != null) {
            synchronized (GameField.class) {
                if (gameField == null) {
                    return null;
                }

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
            }
        }
        return null;
    }

    public static int[][] updateField(SnakesProto.GameState state, boolean newGame) {
        if(gameField != null) {
            synchronized (GameField.class) {
                if (newGame || gameField == null) {
                    gameField = new int[state.getConfig().getWidth()][state.getConfig().getWidth()];
                    for (int i = 0; i < state.getConfig().getWidth(); i++) {
                        for (int j = 0; j < state.getConfig().getHeight(); j++) {
                            gameField[i][j] = 0;
                        }
                    }
                }
                List<SnakesProto.GameState.Snake> snakes = state.getSnakesList();

                for (int i = 0; i < snakes.size(); i++) {
                    SnakesProto.GameState.Snake snake = snakes.get(i);
                    paintSnake(snake);
                }
                for (int i = 0; i < state.getFoodsCount(); i++) {
                    SnakesProto.GameState.Coord foodCoord = state.getFoods(i);
                    if (gameField[foodCoord.getX()][foodCoord.getY()] != 0) {
                        //error
                        System.out.println("ERRROR");
                    } else
                        gameField[foodCoord.getX()][foodCoord.getY()] = 1;
                }

                return gameField;
            }
        }
        return null;
    }

    public static void paintNewSnake(SnakesProto.GameState.Snake snake){
        if(gameField != null) {
            synchronized (GameField.class) {
                paintSnake(snake);
            }
        }
    }
}
