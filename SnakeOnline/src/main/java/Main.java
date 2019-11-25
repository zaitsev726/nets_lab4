import NetworkPart.Broadcast.MulticastController;
import UserInterface.InteraceController;

public class Main {

    public static void main(String[] args) throws Exception {

        InteraceController i = InteraceController.getInstance();

        MulticastController controller = new MulticastController();
        controller.start();
       /* SnakesProto.GameMessage.AnnouncementMsg.Builder s = SnakesProto.GameMessage.AnnouncementMsg.newBuilder();

        SnakesProto.GameConfig.Builder game =SnakesProto.GameConfig.newBuilder();


        SnakesProto.GameMessage.AnnouncementMsg.Builder s = SnakesProto.GameMessage.AnnouncementMsg.newBuilder();
        SnakesProto.GamePlayer.Builder s = SnakesProto.GamePlayer.newBuilder();

        SnakesProto.GameMessage.JoinMsg.Builder j = SnakesProto.GameMessage.JoinMsg.newBuilder();
        //j.setField(SnakesProto.PlayerType, SnakesProto.PlayerType.HUMAN_VALUE);
        j.setPlayerType(SnakesProto.PlayerType.HUMAN);

        j.setOnlyView(true);

        SnakesProto.GameMessage.JoinMsg l = j.build();


        CodedOutputStream c = CodedOutputStream.newInstance(System.out);
        CodedInputStream d = CodedInputStream.newInstance(System.in);

        SnakesProto.GameMessage.Builder aa = SnakesProto.GameMessage.newBuilder();

        aa.getTypeCase()
        SnakesProto.GameMessage bb = aa.build();

        bb = SnakesProto.GameMessage.parseFrom(System.in);
        SnakesProto.GameMessage.AnnouncementMsg g = bb.getAnnouncement();
        */
    }

}
