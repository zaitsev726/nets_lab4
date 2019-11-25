package UserInterface.NewGamePage;

import javax.swing.*;
import java.awt.*;

public class NewGamePanel extends JPanel {

    public JTextField widthField;
    public JTextField heightField;

    public JButton continueButton;
    public JButton backButton;
    public JCheckBox checkBox;

    public NewGamePanel() {
        this.setLayout(null);

        continueButton = new JButton("CONTINUE");
        backButton = new JButton("BACK");
        checkBox = new JCheckBox("SINGLEPLAYER");

        checkBox.setSelected(false);

        backButton.setVerticalTextPosition(AbstractButton.CENTER);
        backButton.setHorizontalTextPosition(AbstractButton.CENTER);

        widthField = new JTextField();
        heightField = new JTextField();

        widthField.setBounds(350, 100, 100, 30);
        widthField.setFont(new Font("Dialog", Font.PLAIN, 26));

        heightField.setBounds(350, 200, 100, 30);
        heightField.setFont(new Font("Dialog", Font.PLAIN, 26));

        checkBox.setFont(new Font("Dialog", Font.PLAIN, 26));

        JLabel label = new JLabel("Введите ширину поля: ");
        label.setFont(new Font("Dialog", Font.PLAIN, 26));

        JLabel label1 = new JLabel("Введите длину поля: ");
        label1.setFont(new Font("Dialog", Font.PLAIN, 26));

        label.setBounds(50, 100, 300, 25);

        label1.setBounds(70, 200, 300, 25);

        checkBox.setBounds(90, 300, 300, 25);

        continueButton.setBounds(668, 500, 200, 100);
        backButton.setBounds(100, 500, 200, 100);

        this.add(checkBox);
        this.add(backButton);
        this.add(continueButton);
        this.add(label);
        this.add(widthField);
        this.add(heightField);
        this.add(label1);


        setVisible(true);
    }
}

