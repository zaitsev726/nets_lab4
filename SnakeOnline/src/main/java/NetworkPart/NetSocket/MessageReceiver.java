package NetworkPart.NetSocket;

import MessageProcessing.MessageHandler;
import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class MessageReceiver extends Thread{
    private static final int BUF_SIZE = 4096;
    private DatagramSocket socket;
    private MessageHandler handler;
    public MessageReceiver(DatagramSocket socket){
        this.socket = socket;
        this.start();
        handler = new MessageHandler();
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[BUF_SIZE];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            while (true) {
                socket.receive(dp);
                byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());

                SnakesProto.GameMessage message = SnakesProto.GameMessage.parseFrom(a1);
                handler.handlingMessage(message, dp.getAddress(), dp.getPort(),message.getMsgSeq());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
