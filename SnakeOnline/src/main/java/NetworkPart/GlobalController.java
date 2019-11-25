package NetworkPart;

import NetworkPart.NetSocket.NetworkSocket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class GlobalController {
    private static volatile GlobalController instance;
    private int port;
    private InetAddress IP;
    private NetworkSocket networkSocket;

    private GlobalController(){

        try {
            IP = getLocalAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println(IP);

    }

    public static GlobalController getInstance(){
        GlobalController localInstance = instance;
        if(localInstance == null){
            synchronized (GlobalController.class){
                localInstance = instance;
                if(localInstance == null)
                    instance = localInstance = new GlobalController();
            }
        }
        return localInstance;
    }

    public void setPort(int port){
        if(port >= 2000 && port <= 6000) {
            this.port = port;
            networkSocket = new NetworkSocket();
        }
    }
    public int getPort(){
        return port;
    }
    public void setIP(InetAddress IP){
        this.IP = IP;
    }
    public InetAddress getIP(){return  IP;}

    private InetAddress getLocalAddress() throws UnknownHostException, SocketException {
        List<NetworkInterface> netInts = Collections.list(NetworkInterface.getNetworkInterfaces());

        // there is a simple method, but it works sometimes
        // incorrectly when there are several network interfaces
        if (netInts.size() == 1) {
            return InetAddress.getLocalHost();
        }

        for (NetworkInterface net : netInts) {
            if (!net.isLoopback() && !net.isVirtual() && net.isUp()) {
                Enumeration<InetAddress> addrEnum = net.getInetAddresses();
                while (addrEnum.hasMoreElements()) {
                    InetAddress addr = addrEnum.nextElement();
                    // filter out addresses, which cannot be considered as the main address
                    // and return the first suitable address
                    if ( !addr.isLoopbackAddress() && !addr.isAnyLocalAddress()
                            && !addr.isLinkLocalAddress() && !addr.isMulticastAddress()
                    ) {
                        return addr;
                    }
                }
            }
        }
        // we can fall here if there are no suitable addresses/interfaces
        // or we don't have enough permissions
        return null;
    }
}
