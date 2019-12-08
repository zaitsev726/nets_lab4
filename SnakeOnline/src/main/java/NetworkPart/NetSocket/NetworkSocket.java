package NetworkPart.NetSocket;

import NetworkPart.NetworkController;

import java.io.IOException;
import java.net.Socket;

public class NetworkSocket {
    private Socket socket;

    public NetworkSocket(){
        if(NetworkController.getInstance().getPort() != 0){
            try {
                socket = new Socket(NetworkController.getInstance().getIP(), NetworkController.getInstance().getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
