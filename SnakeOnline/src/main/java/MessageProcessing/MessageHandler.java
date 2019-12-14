package MessageProcessing;

import Global.GlobalController;
import NetworkPart.NetSocket.SendPart.ResendQueue;
import NetworkPart.NetSocket.SteerMsgQueue;
import SnakeGame.Players;
import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private GlobalController controller;
    private ResendQueue resendQueue;
    private ConcurrentHashMap<SnakesProto.GamePlayer, Date> lastMessage;
    public MessageHandler(GlobalController controller, ResendQueue resendQueue){
        this.controller = controller;
        this.resendQueue = resendQueue;
        lastMessage = new ConcurrentHashMap<>();
    }

    /*вопрос что использовать?
        есть структура плеер в котором можно заполнять два поля, но хорошо ли применять к ним иквелс?
        или создать свою структуру. переопределить метод иквелс и там уже флекс?
    * */
    public void handlingMessage(SnakesProto.GameMessage message, InetAddress address, int port, long msg_seq){
       //если пришло любое сообщение обновляем информацию о пользователе
        SnakesProto.GamePlayer player = SnakesProto.GamePlayer.newBuilder()
                                        .setName("")
                                        .setId(0)
                                        .setIpAddress(address.toString())
                                        .setPort(port)
                                        .setRole(SnakesProto.NodeRole.NORMAL)
                                        .setScore(0)
                                        .build();
        lastMessage.put(player,new Date());

        switch (message.getTypeCase()) {
            case PING:
                //ничего не делаем т.к. уже добавили в мапу
                break;
            case STEER:
                //не уверен что это так и оно работает
                SteerMsgQueue.getInstance().addNewDirection(message.getSteer(),address,port);
                break;
            case ACK:
                //удаление сообщения из спам очереди
                resendQueue.deleteMessage(message);
                break;
            case STATE:
                //обновление стейта
                SnakesProto.GameState state = message.getState().getState();
                Players.getInstance().setPlayers((ArrayList<SnakesProto.GamePlayer>) state.getPlayers().getPlayersList());
                Players.getInstance().setSnakes(state.getSnakesList());
                controller.setState(state);
                break;
            case ANNOUNCEMENT:
                //не должны такое ловить
                return;
            case JOIN:
                //передаем сообщение в players
                //System.out.println("***приняли ДЖОИН!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                Players.getInstance().addNewPlayerInQueue(message.getJoin(), address,port,msg_seq);
                break;
            case ERROR:
                controller.errorMessage(message.getError());
                break;
            case ROLE_CHANGE:
                //если получили извне сообщение о том что кто то выходит

                //если получает сообщение не мастер
                if(message.getRoleChange().getSenderRole().equals(SnakesProto.NodeRole.VIEWER))
                    Players.getInstance().updateRole(address, port);

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
