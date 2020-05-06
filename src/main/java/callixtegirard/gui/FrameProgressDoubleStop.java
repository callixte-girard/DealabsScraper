package callixtegirard.gui;

import callixtegirard.model.WebsiteScraper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class FrameProgressDoubleStop extends FrameProgressDouble
{
    private JButton buttonStop = new JButton("Stop");

    public FrameProgressDoubleStop(String title, ActionListener actionListener) {
        super(title);

        this.setLayout(new GridLayout(4, 1)); // adapt layout to new size

        this.buttonStop.setBorder(new EmptyBorder(0, paddingSize, 0, paddingSize));
        this.buttonStop.addActionListener(actionListener);
        this.add(buttonStop);
    }

}
