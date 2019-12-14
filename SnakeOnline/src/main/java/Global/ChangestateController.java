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
            interfaceController.repaintField(GameField.updateField(state),
                    state.getConfig().getWidth(),
                    state.getConfig().getHeight(),
                    controller.getID());
        }
        try {
            while (true) {
                //delay
                System.out.println("IDIDIDIDIIDID" + controller.getID());
                Thread.sleep(1000);
                state = controller.getNextState();
                if (state != null) {
                    System.out.println();
                    interfaceController.repaintField(GameField.updateField(state),
                            state.getConfig().getWidth(),
                            state.getConfig().getHeight(),
                            controller.getID());
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
