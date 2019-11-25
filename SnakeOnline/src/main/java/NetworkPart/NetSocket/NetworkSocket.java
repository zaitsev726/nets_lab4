package NetworkPart.NetSocket;

import NetworkPart.GlobalController;

import java.io.IOException;
import java.net.Socket;

public class NetworkSocket {
    private Socket socket;

    public NetworkSocket(){
        if(GlobalController.getInstance().getPort() != 0){
            try {
                socket = new Socket(GlobalController.getInstance().getIP(), GlobalController.getInstance().getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
