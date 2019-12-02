package MessageProcessing;

import me.ippolitov.fit.snakes.SnakesProto;

import java.net.InetAddress;

public class MessageHandler {

    public MessageHandler(){}

    public void handlingMessage(SnakesProto.GameMessage message, InetAddress address, int port, long msg_seq){

        switch (message.getTypeCase()) {
            case PING:
                break;
            case STEER:
                break;
            case ACK:
            case STATE:

            case ANNOUNCEMENT:
                /*Something going bad*/
                return;
            case JOIN:
            case ERROR:
            case ROLE_CHANGE:
            case TYPE_NOT_SET:

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
