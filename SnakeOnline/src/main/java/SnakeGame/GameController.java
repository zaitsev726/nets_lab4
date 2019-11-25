package SnakeGame;

import me.ippolitov.fit.snakes.SnakesProto;

import java.util.ArrayList;

public class GameController {
    private ArrayList<SnakesProto.GamePlayer> players;
    private int width;
    private int height;
    private int foodStatic;
    private float foodPerPlayer;
    private int delay;
    private float deadFoodProb;
    private boolean owner;

    public GameController(SnakesProto.GameMessage.AnnouncementMsg connect){
        /*connection*/
        players = (ArrayList<SnakesProto.GamePlayer>) connect.getPlayers().getPlayersList();

        this.width = connect.getConfig().getWidth();
        this.height = connect.getConfig().getHeight();
        this.owner = false;

    }

    public GameController(int width, int height, int foodStatic,
                          float foodPerPlayer ,int delay, float deadFoodProb){
        /*create your game*/
        this.width = width;
        this.height = height;
        this.foodStatic = foodStatic;
        this.foodPerPlayer = foodPerPlayer;
        this.delay = delay;
        this.deadFoodProb = deadFoodProb;
        this.owner = true;
    }




    /*
    System.out.println("ширина поля: " + config.getWidth());
                System.out.println("высота поля: " + config.getHeight());
                System.out.println("статичное количество еды: " + config.getFoodStatic());
                System.out.println("количество еды на каждого игрока :" + config.getFoodPerPlayer());
                System.out.println("задержка: " + config.getDelayMs());
                System.out.println("вероятность: " + config.getDeadFoodProb());*/
}
