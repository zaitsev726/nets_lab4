package MessageProcessing;

import me.ippolitov.fit.snakes.SnakesProto;

public class MessageCreator {
    private static volatile long message_ID = 1;

    /*
    PingMsg ping = 2;
        SteerMsg steer = 3;
        AckMsg ack = 4;
        StateMsg state = 5;
        AnnouncementMsg announcement = 6;
        JoinMsg join = 7;
        ErrorMsg error = 8;
        RoleChangeMsg role_change = 9;

     */

    /**
     * готово
     */
    public static void createNewPing(){
        SnakesProto.GameMessage message = SnakesProto.GameMessage.newBuilder()
                .setMsgSeq(message_ID)
                .setPing(SnakesProto.GameMessage.PingMsg.newBuilder().build())
                .build();
        message_ID++;
        MessageManagement.addNewMessage(message);
    }

    /**
     * готово
     */
    public static void createNewAckMsg(long msg_seq, int ID){
        SnakesProto.GameMessage message = SnakesProto.GameMessage.newBuilder()
                .setMsgSeq(msg_seq)
                .setReceiverId(ID)
                .build();
        MessageManagement.addNewMessage(message);
    }

    public static void createNewSteerMsg(){

    }

    public static void createNewAnnouncementMsg(){

    }
}
