package Global;

import MessageProcessing.MessageCreator;
import NetworkPart.Broadcast.MulticastController;
import NetworkPart.NetSocket.SteerMsgQueue;
import NetworkPart.NetworkController;
import SnakeGame.CurrentLobby;
import SnakeGame.GameController;
import SnakeGame.Players;
import UserInterface.InterfaceController;
import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalController {
    private InterfaceController interfaceController;
    private GameController gameController;
    private ChangestateController stateController = null;
    private MulticastController multicastController;
    private NetworkController networkController;
    private CurrentLobby lobby;

    private int width = 0;
    private int height = 0;
    private int foodStatic = 0;
    private float foodPerPlayer = (float) 0.0;
    private int stateDelay = 0;
    private float deadFoodProb = (float) 0.0;
    private int pingDelay = 0;
    private int nodeTimeout = 0;
    private volatile SnakesProto.GameState state = null;
    private String name = "default";

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setFoodStatic(int foodStatic) {
        this.foodStatic = foodStatic;
    }

    public void setFoodPerPlayer(float foodPerPlayer) {
        this.foodPerPlayer = foodPerPlayer;
    }

    public void setStateDelay(int stateDelay) {
        this.stateDelay = stateDelay;
    }

    public void setDeadFoodProb(float deadFoodProb) {
        this.deadFoodProb = deadFoodProb;
    }

    public void setPingDelay(int pingDelay) {
        this.pingDelay = pingDelay;
    }

    public void setNodeTimeout(int nodeTimeout) {
        this.nodeTimeout = nodeTimeout;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getMaster() {
        return lobby.isMaster();
    }

    public String getHostIP() {
        return lobby.getHost_IP();
    }

    public int getHostPort() {
        return lobby.getHost_port();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GlobalController() {
        Players.getInstance().setController(this);
        interfaceController = new InterfaceController(this);
        gameController = null;
        networkController = new NetworkController(this);
        multicastController = new MulticastController(networkController.getIP(), interfaceController);
        multicastController.start();
    }

    public void removeGame() {

        Players.getInstance().clearPlayers();
        stateController.interrupt();
        // stateController.stop();
        if (gameController != null) {
            gameController.clearField();
        }
        gameController = null;
    }
    public void masterDead(SnakesProto.GamePlayer deputy){
        //gameController.clearField();
        lobby = new CurrentLobby(deputy.getIpAddress(), deputy.getPort(), false);
        stateController.interrupt();
        stateController = new ChangestateController(this,
                this.state,
                interfaceController, stateDelay);

    }
    public void initializationGame() {

        lobby = new CurrentLobby(true);
        Players.getInstance().addNewPlayerInQueue();
        gameController = new GameController(width, height, foodStatic, foodPerPlayer,
                stateDelay, deadFoodProb, pingDelay, nodeTimeout, this, 1);

        stateController = new ChangestateController(this,
                gameController.makeNextState(),
                interfaceController, stateDelay);

        stateController.start();
    }

    public void initializationConnect(DatagramPacket dp) {
        byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());
        SnakesProto.GameMessage.AnnouncementMsg message = null;
        try {
            message = SnakesProto.GameMessage.parseFrom(a1).getAnnouncement();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        width = message.getConfig().getWidth();
        height = message.getConfig().getHeight();
        foodStatic = message.getConfig().getFoodStatic();
        foodPerPlayer = message.getConfig().getFoodPerPlayer();
        stateDelay = message.getConfig().getStateDelayMs();
        deadFoodProb = message.getConfig().getDeadFoodProb();
        pingDelay = message.getConfig().getPingDelayMs();
        nodeTimeout = message.getConfig().getNodeTimeoutMs();

        lobby = new CurrentLobby(dp.getAddress().toString(), dp.getPort(), false);
        networkController.sendNewMessage(MessageCreator.createNewJoinMsg(name));
        stateController = new ChangestateController(this, getNextState(),
                interfaceController, message.getConfig().getStateDelayMs());

        stateController.start();
    }

    public synchronized SnakesProto.GameState getNextState() {
        //если мы хост
        // if(lobby != null) {
        if (lobby.isMaster()) {
            state = gameController.makeNextState();
            networkController.deleteAnnouncementMsg();
            networkController.sendNewMessage(MessageCreator.createNewAnnouncementMsg(
                    state.getPlayers(),
                    state.getConfig(),
                    Players.getInstance().canJoin()
            ));
        }
        if (!lobby.isMaster())
            System.out.println("yes");
        //если мы не хост
        return state;
        // }
        //   return null;
    }

    public void setPort(int port) {
        networkController.setPort(port);
    }

    public void errorMessage(SnakesProto.GameMessage.ErrorMsg error) {
        interfaceController.showMessage(error.getErrorMessage());
    }

    public synchronized void setState(SnakesProto.GameState state) {
        if (this.state == null)
            this.state = state;

        if (this.state.getStateOrder() < state.getStateOrder()) {
            this.state = state;
        }
    }

    public void sendState(SnakesProto.GameState state) {
        networkController.deleteOldStates();
        ArrayList<SnakesProto.GamePlayer> players = Players.getInstance().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            //как то узнаем то что это не мы
            if (players.get(i).getId() != lobby.getOur_ID())
                networkController.sendNewMessage(MessageCreator.createNewStateMsg(state, players.get(i).getId()));
        }
    }

    public void sendAck(long msg_seq, int receiver_ID) {
        networkController.sendNewMessage(MessageCreator.createNewAckMsg(msg_seq, receiver_ID));
    }

    public void sendSteer(SnakesProto.Direction direction) {
        if (lobby.isMaster()) {
            try {
                SteerMsgQueue.getInstance().addNewDirection(
                        SnakesProto.GameMessage.SteerMsg.newBuilder().setDirection(direction)
                                .build(),
                        InetAddress.getByName(""),
                        1);//1 - временно
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            networkController.sendNewMessage(MessageCreator.createNewSteerMsg(direction));
        }
    }

    public void setOurId(int receiverId) {
        lobby.setOur_ID(receiverId);
    }

    public int getID() {
        return lobby.getOur_ID();
    }

    public void sendRoleChange() {
        if (lobby.isMaster()) {
            for (SnakesProto.GamePlayer player : Players.getInstance().getPlayers()) {
                if (player.getRole().equals(SnakesProto.NodeRole.DEPUTY))
                    networkController.sendNewMessage(MessageCreator.createNewRoleChangeMsg(
                            SnakesProto.NodeRole.VIEWER,
                            SnakesProto.NodeRole.DEPUTY,
                            lobby.getOur_ID(),
                            player.getId()));
            }
        } else
            networkController.sendNewMessage(MessageCreator.createNewRoleChangeMsg(
                    SnakesProto.NodeRole.VIEWER,
                    SnakesProto.NodeRole.MASTER,
                    lobby.getOur_ID(),
                    lobby.getHost_ID()));
    }

    public void sendRoleChange(SnakesProto.NodeRole receiverRole,
                               SnakesProto.NodeRole senderRole,
                               int receiverId) {
        networkController.sendNewMessage(MessageCreator.createNewRoleChangeMsg(
                senderRole,
                receiverRole,
                lobby.getOur_ID(),
                receiverId));

    }

    public void setHostID(List<SnakesProto.GamePlayer> playersList) {
        for (SnakesProto.GamePlayer player : playersList) {
            if (player.getRole().equals(SnakesProto.NodeRole.MASTER)) {
                if (lobby != null) {
                    lobby.setHost_ID(player.getId());
                }
            }
        }
    }

    public void setHostIP(String IP) {
        lobby.setHost_IP(IP);
    }

    public void setHostPort(int port) {
        lobby.setHost_port(port);
    }

    public int getHostID() {
        return lobby.getHost_ID();
    }

    public void updateGame(SnakesProto.GameState state) {
        SnakesProto.GamePlayer player = null;
        if(state == null){
            state = this.state;
        }

        for (SnakesProto.GamePlayer p : Players.getInstance().getPlayers()) {
            if (p.getId() == lobby.getOur_ID()) {
                player = p;
            }
        }
        //System.out.println(player.getRole());
        if (player.getRole().equals(SnakesProto.NodeRole.DEPUTY)) {
            ArrayList<SnakesProto.GamePlayer> players = new ArrayList<>();
            ArrayList<SnakesProto.GameState.Snake> snakes = new ArrayList<>(state.getSnakesList());
            for (SnakesProto.GamePlayer gamePlayer : state.getPlayers().getPlayersList()) {
                if (gamePlayer.getRole().equals(SnakesProto.NodeRole.DEPUTY)) {
                    try {
                        players.add(gamePlayer.toBuilder()
                                .setRole(SnakesProto.NodeRole.MASTER)
                                .setPort(1)
                                .setIpAddress(InetAddress.getByName("").toString())
                                .build());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                } else if (gamePlayer.getRole().equals(SnakesProto.NodeRole.MASTER)) {
                    players.add(gamePlayer.toBuilder()
                            .setRole(SnakesProto.NodeRole.VIEWER)
                            .setIpAddress(lobby.getHost_IP())
                            .setPort(lobby.getHost_port())
                            .build());
                    for (SnakesProto.GameState.Snake snake : state.getSnakesList()) {
                        if (snake.getPlayerId() == gamePlayer.getId()) {
                            snakes.remove(snake);
                            snakes.add(snake.toBuilder().setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE).build());
                        }
                    }
                } else
                    players.add(gamePlayer);

            }
            Players.getInstance().setPlayers(players);
            Players.getInstance().setSnakes(snakes);
            lobby = new CurrentLobby(true);
            lobby.setOur_ID(player.getId());
            lobby.setHost_ID(player.getId());
            gameController = new GameController(width, height, foodStatic, foodPerPlayer,
                    stateDelay, deadFoodProb, pingDelay, nodeTimeout, this, state.getStateOrder());

            stateController = new ChangestateController(this,
                    state,
                    interfaceController, stateDelay);

        }
    }
}
