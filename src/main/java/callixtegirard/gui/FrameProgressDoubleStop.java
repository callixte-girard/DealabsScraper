package callixtegirard.gui;

import callixtegirard.model.WebsiteScraper;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class FrameProgressDoubleStop extends FrameProgressDouble
{
    private JButton buttonStop = new JButton("Stop");

    public FrameProgressDoubleStop(String title, ActionListener actionListener) {
        super(title, 1000, 160, 4, 1);

//        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // cool but not working :(
        // redundant !
//        this.setLayout(new GridLayout(4, 1)); // adapt layout to new size
//        this.setSize(1000, 160);

        // bon en fait un bouton dans une BoxLayout c'est toujours caca...
//        this.buttonStop.setSize(200, 100);
//        this.buttonStop.setBounds(50,100,95,30);
//        this.buttonStop.setBorder(LineBorder.createBlackLineBorder());
        this.buttonStop.setPreferredSize(new Dimension(100, 40));

        this.buttonStop.addActionListener(actionListener);
        this.add(buttonStop);
    }

    public void updateButton(String text) {
        SwingUtilities.invokeLater(() -> getButtonStop().setText(text));
    }

    public void updateButton(boolean enable) {
        SwingUtilities.invokeLater(() -> getButtonStop().setEnabled(enable));
    }


    public JButton getButtonStop() {
        return buttonStop;
    }
}
