package NetworkPart.NetSocket.SendPart;

import Global.GlobalController;
import SnakeGame.Players;
import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageSender extends Thread {
    private DatagramSocket socket;
    private GlobalController controller;
    private ResendQueue resend;
    private ImmediateQueue queue;
    private int minToResend;        //минимальное время до отправки пакета из resend очереди
    private int ping_delay_ms;      //повторное отправление

    private ArrayList<SnakesProto.GamePlayer> currentPlayers;

    public MessageSender(DatagramSocket socket, GlobalController controller, ImmediateQueue queue, ResendQueue resendQueue) {
        this.socket = socket;
        this.queue = queue;
        this.ping_delay_ms = 100; //default value
        this.controller = controller;

        resend = resendQueue;

        minToResend = 1000; //хз какое число тут брать
    }

    public void setPing_delay_ms(int ping_delay_ms) {
        this.ping_delay_ms = ping_delay_ms;
    }

    @Override
    public void run() {
        while (true) {
            currentPlayers = Players.getInstance().getPlayers();
            Date date = new Date();
            LinkedBlockingQueue q = queue.getQ();
            
            try {
                SnakesProto.GameMessage message = (SnakesProto.GameMessage) q.poll(minToResend, TimeUnit.MILLISECONDS);

                if (message != null) {
                    switch (message.getTypeCase()) {
                        case PING:

                            break;
                        case STEER:

                            break;
                        case ACK:
                            sendAck(message);
                            return;
                        case STATE:
                            //если мастер то
                            sendState(message);
                            resend.addNewResendMessage(message);
                            break;
                        case ANNOUNCEMENT:
                            sendAnnouncementMsg(message);
                            resend.addNewResendMessage(message);
                            break;
                        case JOIN:
                            sendJoin(message);
                            resend.addNewResendMessage(message);
                            break;
                        case ERROR:
                            resend.addNewResendMessage(message);
                            break;
                        case ROLE_CHANGE:
                            resend.addNewResendMessage(message);
                            break;
                    }


                    minToResend = ping_delay_ms - (int) resend.getMinToResend();
                }


            } catch (InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }

            synchronized (ResendQueue.class) {//или конкурент хеш мап?
                ConcurrentHashMap<SnakesProto.GameMessage, Date> map = resend.getResendQueue();
                for (Map.Entry<SnakesProto.GameMessage, Date> next : map.entrySet()) {
                    if ((date.getTime() - next.getValue().getTime()) > ping_delay_ms) {
                        switch (next.getKey().getTypeCase()) {
                            case ANNOUNCEMENT:
                                sendAnnouncementMsg(next.getKey());
                                break;
                            case JOIN:
                                sendJoin(next.getKey());
                                break;
                            case STATE:
                                sendState(next.getKey());
                            default:
                        }
                        resend.addNewResendMessage(next.getKey());
                    }
                }
            }
        }
    }

    public void sendPing(SnakesProto.GameMessage message) {
        //проверка мастер мы или нет
        SnakesProto.GamePlayer player = null;
        for (int i = 0; i < currentPlayers.size(); i++) {
            if (currentPlayers.get(i).getId() == message.getReceiverId()) {
                player = currentPlayers.get(i);
            }
        }
        if (player != null) {
            try {
                DatagramPacket dp = new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                        InetAddress.getByName((player.getIpAddress()).substring(1)), player.getPort());
                socket.send(dp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //если мастер то отправляем всем, если не мастер то тока мастеру

    }

    public void sendSteer(SnakesProto.GameMessage message) {
        //отправляем мастеру смс о повроте узла

    }

    public void sendAck(SnakesProto.GameMessage message) {
        //отправляем мастеру подтверждение его сообщения
        DatagramPacket dp = null;
        try {
            for (SnakesProto.GamePlayer currentPlayer : currentPlayers) {
                //как то узнаем что мы хост
                if (currentPlayer.getId() == message.getReceiverId()) {
                    dp = new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                            InetAddress.getByName(currentPlayer.getIpAddress().substring(1)),
                            currentPlayer.getPort());
                    socket.send(dp);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendState(SnakesProto.GameMessage message) {
        DatagramPacket dp = null;
        try {
            for (SnakesProto.GamePlayer currentPlayer : currentPlayers) {
                //как то узнаем что мы хост
                if (currentPlayer.getId() != 1) {
                    dp = new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                            InetAddress.getByName(currentPlayer.getIpAddress().substring(1)),
                            currentPlayer.getPort());
                    System.out.println("отправляем стейт челууууууууцу");
                    socket.send(dp);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendJoin(SnakesProto.GameMessage message) {
        DatagramPacket dp = null;
        try {
            dp = new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                    InetAddress.getByName(controller.getHostIP().substring(1)), controller.getHostPort());

            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPing1(SnakesProto.GameMessage message) {

    }

    public void sendAnnouncementMsg(SnakesProto.GameMessage message) {
        //System.out.println("ОТПРАВЛЯЕМ МУЛЬТИКАСТ");
        try {
            DatagramPacket dp = new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                    InetAddress.getByName("239.192.0.4"), 9192);
            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}