package Global;

import SnakeGame.GameField;
import UserInterface.InterfaceController;
import me.ippolitov.fit.snakes.SnakesProto;

public class ChangestateController extends Thread{
    private GlobalController controller;
    private SnakesProto.GameState state;
    private InterfaceController interfaceController;
    private int delay;

    public ChangestateController(GlobalController controller, SnakesProto.GameState state,
                                 InterfaceController interfaceController, int delay){
        this.controller = controller;
        this.state = state;
        this.interfaceController = interfaceController;
        this.delay = delay;

    }
    @Override
    public void run() {
        interfaceController.repaintField(GameField.updateField(state, false));
        try {
            //delay
            Thread.sleep(2000);
            SnakesProto.GameState state = controller.getNextState();
            interfaceController.repaintField(GameField.updateField(state,false));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
