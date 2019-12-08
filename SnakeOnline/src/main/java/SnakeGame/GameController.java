package SnakeGame;

import Global.GlobalController;
import me.ippolitov.fit.snakes.SnakesProto;

public class GameController {
    private GameLogic logic;
    private GameField field;
    private int delay;
    private int pingDelay;
    private int nodeTimeout;
    private GlobalController controller;
    public GameController(int width, int height, int foodStatic, float foodPerPlayer,
                          int delay, float deadFoodProb, int pingDelay, int nodeTimeout,
                          GlobalController controller) {
        logic = new GameLogic(width,height,foodStatic,foodPerPlayer,deadFoodProb);
        field = new GameField(width,height);
        this.delay = delay;
        this.pingDelay = pingDelay;
        this.nodeTimeout = nodeTimeout;
        this.controller = controller;
    }

    public SnakesProto.GameState makeNextState(){

        logic.makeNextStep();
        SnakesProto.GameState state = logic.createNewState(delay,pingDelay,nodeTimeout);

        /*
        отправка стейта всем
         */
        return state;
    }
}
