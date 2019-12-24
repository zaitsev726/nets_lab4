package MessageProcessing;

import Global.GlobalController;
import NetworkPart.NetSocket.SendPart.ResendQueue;
import NetworkPart.NetSocket.SteerMsgQueue;
import SnakeGame.Players;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetAddress;
import java.util.ArrayList;

public class MessageHandler {
    private GlobalController controller;
    private ResendQueue resendQueue;
    public MessageHandler(GlobalController controller, ResendQueue resendQueue){
        this.controller = controller;
        this.resendQueue = resendQueue;
    }

    public void handlingMessage(SnakesProto.GameMessage message, InetAddress address, int port, long msg_seq){
       //если пришло любое сообщение обновляем информацию о пользователе

       // System.out.println("приняли месседж");
        switch (message.getTypeCase()) {
            case PING:
                //ничего не делаем т.к. уже добавили в мапу
                break;
            case STEER:
                SteerMsgQueue.getInstance().addNewDirection(message.getSteer(),address,port);
                break;
            case ACK:
                //удаление сообщения из спам очереди
                resendQueue.deleteMessage(message);
                break;
            case STATE:
                //обновление стейта
                if(!controller.getMaster()) {
                    SnakesProto.GameState state = message.getState().getState();
                    Players.getInstance().setPlayers(new ArrayList<SnakesProto.GamePlayer>(state.getPlayers().getPlayersList()));
                    Players.getInstance().setSnakes(state.getSnakesList());
                    controller.sendAck(message.getMsgSeq(), Players.getInstance().getHostID());
                    controller.setHostID(state.getPlayers().getPlayersList());
                    controller.setHostIP(address.toString());
                    controller.setHostPort(port);
               //     System.out.println("State order" + state.getStateOrder());

                    controller.setState(state);
                }
                break;
            case ANNOUNCEMENT:
                break;
            case JOIN:
                Players.getInstance().addNewPlayerInQueue(message, address,port,msg_seq);
                break;
            case ERROR:
                controller.errorMessage(message.getError());
                break;
            case ROLE_CHANGE:
                controller.sendAck(message.getMsgSeq(), message.getSenderId());
                //если получили извне сообщение о том что кто то выходит
                if(message.getRoleChange().getReceiverRole().equals(SnakesProto.NodeRole.MASTER))
                    Players.getInstance().updateRole(address,port);
                //если получает сообщение не мастер
                if(message.getRoleChange().getSenderRole().equals(SnakesProto.NodeRole.VIEWER)
                    && message.getRoleChange().getReceiverRole().equals(SnakesProto.NodeRole.MASTER))
                    Players.getInstance().updateRole(address, port);

                if(message.getRoleChange().getReceiverRole().equals(SnakesProto.NodeRole.DEPUTY)
                && message.getRoleChange().getSenderRole().equals(SnakesProto.NodeRole.VIEWER)){
                    controller.updateGame( null,true);
                }
                if(message.getRoleChange().getReceiverRole().equals(SnakesProto.NodeRole.DEPUTY)
                && message.getRoleChange().getSenderRole().equals(SnakesProto.NodeRole.NORMAL)){
                    controller.updateGame( null,false);
                }

                //если будет кнопка join то получать сообщение о смерти от мастера
                //пока что они игнорятся
                break;
            case TYPE_NOT_SET:
                break;

        }

    }

    /*
          case 2: return PING;
          case 3: return STEER;
          case 4: return ACK;
          case 5: return STATE;
          case 6: return ANNOUNCEMENT;
          case 7: return JOIN;
          case 8: return ERROR;
          case 9: return ROLE_CHANGE;
          case 0: return TYPE_NOT_SET;
     */
}
