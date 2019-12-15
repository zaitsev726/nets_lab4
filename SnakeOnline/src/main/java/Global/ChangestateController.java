package Global;

import SnakeGame.GameField;
import UserInterface.InterfaceController;
import me.ippolitov.fit.snakes.SnakesProto;

import java.util.Date;

public class ChangestateController extends Thread{
    private GlobalController controller;
    private SnakesProto.GameState state;
    private InterfaceController interfaceController;
    private int delay;
    private SnakesProto.GameState lastState;
    private Date lastDate;
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
            lastState = state;
            lastDate = new Date();
        }
        try {
            lastDate = new Date();
            while (true) {
                //delay
                System.out.println("IDIDIDIDIIDID" + controller.getID());
                Thread.sleep(100);
                state = controller.getNextState();

                if (state != null) {
                    System.out.println("Номер текущего стейта" + state.getStateOrder());
                    System.out.println();
                    interfaceController.repaintField(GameField.updateField(state),
                            state.getConfig().getWidth(),
                            state.getConfig().getHeight(),
                            controller.getID());

                        if (lastState == null || state.getStateOrder() > lastState.getStateOrder()) {
                            lastState = state;
                            lastDate = new Date();
                        }
//                    System.out.println("РАЗНИЦА " + ((new Date().getTime()) - lastDate.getTime()));
                        if ((new Date().getTime()) - lastDate.getTime() > 2000) {
                            controller.updateGame(lastState);
                        }
                    }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
