package callixtegirard.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrameProgressDouble extends FrameProgress
{
    protected JProgressBar bar2 = new JProgressBar();

    public FrameProgressDouble(String title) {
        super(title);

        this.setLayout(new GridLayout(3, 1)); // adapt layout to new size

//        this.bar.setMaximum(total);
        this.bar2.setBorder(new EmptyBorder(0, paddingSize, 0, paddingSize));
        this.add(bar2);
    }

    public void update(String msg, int progress, int progress2)
    {
        SwingUtilities.invokeLater(() -> {
            this.label.setText(msg);
            this.bar.setValue(progress);
            this.bar2.setValue(progress2);
        });
    }

    public JProgressBar getBar2() {
        return bar2;
    }

}
