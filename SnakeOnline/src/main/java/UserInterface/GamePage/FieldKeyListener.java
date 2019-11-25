package UserInterface.GamePage;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class FieldKeyListener extends KeyAdapter {
    GameField panel;
    FieldKeyListener(GameField p){
        panel = p;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT && !panel.isRight()){
            panel.setLeft(true);
            panel.setUp(false);
            panel.setDown( false);
        }
        if(key == KeyEvent.VK_RIGHT && !panel.isLeft()){
            panel.setRight(true);
            panel.setUp( false);
            panel.setDown( false);
        }

        if(key == KeyEvent.VK_UP && !panel.isDown()){
            panel.setRight(false);
            panel.setUp(true);
            panel.setLeft(false);
        }
        if(key == KeyEvent.VK_DOWN && !panel.isUp()){
            panel.setDown( true);
            panel.setRight(false);
            panel.setLeft( false);
        }
    }

}