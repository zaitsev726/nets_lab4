package Global;

import SnakeGame.GameController;
import SnakeGame.Players;
import UserInterface.InterfaceController;
import me.ippolitov.fit.snakes.SnakesProto;

public class GlobalController {
    private InterfaceController interfaceController;
    private GameController gameController;
    private ChangestateController stateController = null;
    private int width = 0;
    private int height = 0;
    private int foodStatic = 0;
    private float foodPerPlayer = (float) 0.0;
    private int stateDelay = 0;
    private float deadFoodProb = (float) 0.0;
    private int pingDelay = 0;
    private int nodeTimeout = 0;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setFoodStatic(int foodStatic) { this.foodStatic = foodStatic; }
    public void setFoodPerPlayer(float foodPerPlayer) { this.foodPerPlayer = foodPerPlayer; }
    public void setStateDelay(int stateDelay) { this.stateDelay = stateDelay; }
    public void setDeadFoodProb(float deadFoodProb) { this.deadFoodProb = deadFoodProb; }
    public void setPingDelay(int pingDelay) { this.pingDelay = pingDelay; }
    public void setNodeTimeout(int nodeTimeout) { this.nodeTimeout = nodeTimeout; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public GlobalController(){
        interfaceController = new InterfaceController(this);
        gameController = null;

       // MulticastController controller = new MulticastController();
        //controller.start();
    }

    public void removeGame() {
        gameController = null;
    }

    public void initilizationGame(){

        Players.getInstance().addNewPlayerInQueue();
        gameController = new GameController(width,height,foodStatic,foodPerPlayer,
                stateDelay,deadFoodProb,pingDelay,nodeTimeout,this);

        stateController = new ChangestateController(this,
                gameController.makeNextState(),
                interfaceController, stateDelay);
        stateController.start();
    }

    public SnakesProto.GameState getNextState(){
        return gameController.makeNextState();
    }
}
