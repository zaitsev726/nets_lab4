package NetworkPart.Broadcast;


import NetworkPart.NetworkController;
import me.ippolitov.fit.snakes.SnakesProto;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class MulticastController extends Thread {
    private final int multicastPort = 9192;
    private InetAddress multicastAddress;

    private MulticastSocket multicastSocket;

    public MulticastController(){
        try {
            multicastAddress =  InetAddress.getByName("239.192.0.4");
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.setInterface(NetworkController.getInstance().getIP());
            multicastSocket.setSoTimeout(1000);
            multicastSocket.joinGroup(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        byte[] buf = new byte[4096];
        DatagramPacket dp = new DatagramPacket(buf,buf.length);
        SnakesProto.GameMessage.AnnouncementMsg message;
        SnakesProto.GameMessage msg;
        System.out.println("**Сокет начал работу**");
        while(true){
            try {

                multicastSocket.receive(dp);

              //  System.out.println(Arrays.toString(dp.getData()));
                System.out.println(dp.getLength());
                byte[] a1 = Arrays.copyOf(dp.getData(), dp.getLength());

                message = SnakesProto.GameMessage.parseFrom(a1).getAnnouncement();

                //System.out.println(SnakesProto.GameMessage.parseFrom(dp.getData()));
                System.out.println(message.getCanJoin());
                SnakesProto.GameConfig config = message.getConfig();
                System.out.println("ширина поля: " + config.getWidth());
                System.out.println("высота поля: " + config.getHeight());
                System.out.println("статичное количество еды: " + config.getFoodStatic());
                System.out.println("количество еды на каждого игрока :" + config.getFoodPerPlayer());
                System.out.println("задержка: " + config.getPingDelayMs());
                System.out.println("вероятность: " + config.getDeadFoodProb());
                //где еще 2???
                SnakesProto.GamePlayers g;
                g = message.getPlayers();
                List<SnakesProto.GamePlayer> list = g.getPlayersList();
                for(int i = 0; i < list.size(); i++){
                    System.out.println("Имя игрока" + (i+1) +" "+ list.get(i).getName());
                }

                //InterfaceController.getInstance().addNewConnectButton(message);

            }catch (SocketTimeoutException e){

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                System.out.println("Something going wrong...");
            }
            System.out.println("------------------------");
        }
    }
}
