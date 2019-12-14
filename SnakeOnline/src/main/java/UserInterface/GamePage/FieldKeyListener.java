package UserInterface.GamePage;

import NetworkPart.NetSocket.SteerMsgQueue;
import me.ippolitov.fit.snakes.SnakesProto;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class FieldKeyListener extends KeyAdapter {
    GameFieldPanel panel;
    public FieldKeyListener(GameFieldPanel p){
        panel = p;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("нажал");
        try {
            SnakesProto.GameMessage.SteerMsg.Builder steerMsg = SnakesProto.GameMessage.SteerMsg.newBuilder();
            super.keyPressed(e);
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) {
                steerMsg.setDirection(SnakesProto.Direction.LEFT);
            }
            else if (key == KeyEvent.VK_RIGHT) {
                steerMsg.setDirection(SnakesProto.Direction.RIGHT);
            }
            else if (key == KeyEvent.VK_DOWN) {
                steerMsg.setDirection(SnakesProto.Direction.DOWN);
            }
            else if (key == KeyEvent.VK_UP) {
                steerMsg.setDirection(SnakesProto.Direction.UP);
            }
            SteerMsgQueue.getInstance().addNewDirection(steerMsg.build(), InetAddress.getByName(""), 1);
        }catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
    }
}