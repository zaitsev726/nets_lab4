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
        if(state!= null) {
            interfaceController.repaintField(GameField.updateField(state));
        }
        try {
            while (true) {
                //delay
                Thread.sleep(1000);
                SnakesProto.GameState state = controller.getNextState();
                if (state != null) {
                    interfaceController.repaintField(GameField.updateField(state));
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
