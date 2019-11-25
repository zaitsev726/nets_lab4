package UserInterface.NetInfoEntryPage;

import javax.swing.*;
import java.awt.*;

public class NetInfoEntryPanel extends JPanel {
    public JTextField portField;
    public JTextField IPField;

    public JButton continueButton;

    public NetInfoEntryPanel(){
        this.setLayout(null);
        portField = new JTextField();
        IPField = new JTextField();
        continueButton = new JButton("CONTINUE");
        continueButton.setBounds(668, 500, 200, 100);

        JLabel label = new JLabel("Введите порт в диапазоне от 2000 до 6000: ");
        label.setFont(new Font("Dialog", Font.PLAIN, 26));

       /* JLabel label1 = new JLabel("Введите IP (для пользователей WINDOWS) : ");
        label1.setFont(new Font("Dialog", Font.PLAIN, 26));*/

        label.setBounds(5,10,680,30);
      //  label1.setBounds(815,10,680,30);

        portField.setBounds(5,50,680,30);
       // IPField.setBounds(815,50,680,30);

        this.add(label);
        //this.add(label1);
        this.add(portField);
       // this.add(IPField);

        this.add(continueButton);
    }
}
