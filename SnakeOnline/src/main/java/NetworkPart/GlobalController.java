package NetworkPart;

import java.net.InetAddress;

public class GlobalController {
    private static volatile GlobalController instance;
    private int port;
    private InetAddress IP;
    private GlobalController(){
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
        if(port >= 2000 && port <= 6000)
            this.port = port;
    }
    public int getPort(){
        return port;
    }
    public void setIP(InetAddress IP){
        this.IP = IP;
    }
    public InetAddress getIP(){return  IP;}
}
