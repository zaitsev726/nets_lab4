package Global;

import MessageProcessing.MessageCreator;
import NetworkPart.Broadcast.MulticastController;
import NetworkPart.NetworkController;
import SnakeGame.GameController;
import SnakeGame.Lobby;
import SnakeGame.Players;
import UserInterface.InterfaceController;
import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;

public class GlobalController {
    private InterfaceController interfaceController;
    private GameController gameController;
    private ChangestateController stateController = null;
    private MulticastController multicastController;
    private NetworkController networkController;
    private Lobby lobby;

    private int width = 0;
    private int height = 0;
    private int foodStatic = 0;
    private float foodPerPlayer = (float) 0.0;
    private int stateDelay = 0;
    private float deadFoodProb = (float) 0.0;
    private int pingDelay = 0;
    private int nodeTimeout = 0;
    private SnakesProto.GameState state = null;
    private String name = "default";

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setFoodStatic(int foodStatic) { this.foodStatic = foodStatic; }
    public void setFoodPerPlayer(float foodPerPlayer) { this.foodPerPlayer = foodPerPlayer; }
    public void setStateDelay(int stateDelay) { this.stateDelay = stateDelay; }
    public void setDeadFoodProb(float deadFoodProb) { this.deadFoodProb = deadFoodProb; }
    public void setPingDelay(int pingDelay) { this.pingDelay = pingDelay; }
    public void setNodeTimeout(int nodeTimeout) { this.nodeTimeout = nodeTimeout; }
    public void setName(String name){this.name = name;}

    public boolean getMaster(){return lobby.isMaster();}
    public String getHostIP(){return lobby.getHost_IP();}
    public int getHostPort(){return lobby.getHost_port();}
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public GlobalController(){
        interfaceController = new InterfaceController(this);
        gameController = null;
        networkController = new NetworkController(this);
        multicastController = new MulticastController(networkController.getIP(), interfaceController);
        multicastController.start();
    }

    public void removeGame() {
        lobby = null;
        gameController.clearField();
        gameController = null;
    }

    public void initializationGame(){

        lobby = new Lobby(true);
        Players.getInstance().addNewPlayerInQueue();
        gameController = new GameController(width,height,foodStatic,foodPerPlayer,
                stateDelay,deadFoodProb,pingDelay,nodeTimeout,this);

        stateController = new ChangestateController(this,
                gameController.makeNextState(),
                interfaceController, stateDelay);

        stateController.start();
    }
    public void initializationConnect(DatagramPacket dp){
        byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());
        SnakesProto.GameMessage.AnnouncementMsg message = null;
        try {
            message = SnakesProto.GameMessage.parseFrom(a1).getAnnouncement();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        lobby = new Lobby(dp.getAddress().toString(),dp.getPort(),false);
        networkController.sendNewMessage(MessageCreator.createNewJoinMsg(name));
        stateController = new ChangestateController(this, getNextState(),
                interfaceController, message.getConfig().getStateDelayMs());

        stateController.start();
    }

    public SnakesProto.GameState getNextState(){
        //если мы хост
        if(lobby.isMaster()) {
            state = gameController.makeNextState();
            networkController.deleteAnnouncementMsg();
            networkController.sendNewMessage(MessageCreator.createNewAnnouncementMsg(
                    state.getPlayers(),
                    state.getConfig(),
                    Players.getInstance().canJoin()
            ));
        }
        //если мы не хост
        return state;
    }
    public void setPort(int port){
        networkController.setPort(port);
    }

    public void errorMessage(SnakesProto.GameMessage.ErrorMsg error) {
        interfaceController.showMessage(error.getErrorMessage());
    }

    public void setState(SnakesProto.GameState state){
        if(this.state == null)
            this.state = state;

        if(this.state.getStateOrder() < state.getStateOrder()){
            this.state = state;
        }
    }

    public void sendState(SnakesProto.GameState state){
        ArrayList<SnakesProto.GamePlayer> players = Players.getInstance().getPlayers();
        for(int i = 0; i < players.size(); i++){
            //как то узнаем то что это не мы
            if (players.get(i).getId() != 1)
            networkController.sendNewMessage(MessageCreator.createNewStateMsg(state,players.get(i).getId()));
        }
    }

}
