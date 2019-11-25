import NetworkPart.Broadcast.MulticastController;
import UserInterface.InteraceController;
import me.ippolitov.fit.snakes.SnakesProto;

public class Main {

    public static void main(String[] args) {
        InteraceController i = new InteraceController();

        MulticastController controller = new MulticastController();
        controller.start();
        SnakesProto.GameConfig.Builder game =SnakesProto.GameConfig.newBuilder();
    }

}
