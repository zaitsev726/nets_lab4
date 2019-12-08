package MessageProcessing;

import me.ippolitov.fit.snakes.SnakesProto;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageManagement extends Thread {
    private static final long PING_TIME = 1000;

    private static volatile Map<SnakesProto.GameMessage, Date> queue = new ConcurrentHashMap<>();
    public static volatile List<DatagramPacket> messages = new ArrayList<>();

    public MessageManagement() {
        start();
    }

    public synchronized static void addNewMessage(SnakesProto.GameMessage message) {
        if (queue.isEmpty()) {
            queue.put(message, new Date());
        } else {
            for (Iterator<Map.Entry<SnakesProto.GameMessage, Date>> it = queue.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<SnakesProto.GameMessage, Date> iterator = it.next();
                if (iterator.getKey().equals(message)) {
                    return;
                }
            }
            queue.put(message, new Date());
        }
    }


    @Override
    public void run() {
        while (true) {
            if (!queue.isEmpty()) {
                Date d = new Date();

                for (Iterator<Map.Entry<SnakesProto.GameMessage, Date>> it = queue.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<SnakesProto.GameMessage, Date> iterator = it.next();
                    switch (iterator.getKey().getTypeCase()) {
                        case PING:
                            if (d.getTime() - iterator.getValue().getTime() > PING_TIME) {
                                queue.put(iterator.getKey(), d);
                                createPingPacket(iterator.getKey());
                            }
                            break;
                        case STEER:
                            break;
                        case ACK:

                        case STATE:

                        case ANNOUNCEMENT:

                        case JOIN:
                        case ERROR:
                        case ROLE_CHANGE:
                        case TYPE_NOT_SET:

                    }

                }
            }
        }
    }


    private void createPingPacket(SnakesProto.GameMessage message) {
/*
        ArrayList players = Players.getInstance().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            SnakesProto.GamePlayer g = (SnakesProto.GamePlayer) players.get(i);
            try {
                if (!g.getIpAddress().equals("") && GameLogic.getOwner() == true) {

                    messages.add(new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                            InetAddress.getByName(g.getIpAddress().substring(1)), g.getPort()));

                } else if (g.getIpAddress().equals("") && GameLogic.getOwner() == false) {

                    messages.add(new DatagramPacket(message.toByteArray(), message.toByteArray().length,
                            InetAddress.getByName(GameLogic.getHost_IP().substring(1)), g.getPort()));
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }*/
    }
}
