package UserInterface.GamePage;

import java.awt.event.KeyAdapter;

class FieldKeyListener extends KeyAdapter {
    GameFieldPanel panel;
    FieldKeyListener(GameFieldPanel p){
        panel = p;
    }

}