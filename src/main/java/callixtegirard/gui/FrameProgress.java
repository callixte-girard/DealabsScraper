package callixtegirard.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class FrameProgress extends JFrame
{
    private static final int paddingSize = 15;

    private JLabel label = new JLabel();
    private JProgressBar bar = new JProgressBar();


    public FrameProgress(String title)
    {
        this.setTitle(title);
        this.setSize(1000, 100);
        this.setLocationRelativeTo(null); //Nous demandons maintenant à notre objet de se positionner au centre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Termine le processus lorsqu'on clique sur la croix rouge
        this.setResizable(true);
        this.setLayout(new GridLayout(2, 1));

        this.label.setText("Initialisation...");
        this.label.setBorder(new EmptyBorder(0, paddingSize, 0, paddingSize));
        this.add(label);

//        this.bar.setMaximum(total);
        this.bar.setBorder(new EmptyBorder(0, paddingSize, 0, paddingSize));
        this.add(bar);

        // autres méthodes utiles
        // - JFrame
//        this.setGlassPane(); // ajoute une couche pour intercepter les actions de l'utilisateur
//        this.setLocation(int x, int y);
//        this.setAlwaysOnTop(boolean b);
//        this.setUndecorated(boolean b); // retire les bordes et boutons de contrôle
        // - JLabel
//        this.setBounds(int x, int y, int width, int height); // ????

        this.setVisible(true);
    }


    public void update(String msg, int progress)
    {
        SwingUtilities.invokeLater(() -> {
            this.label.setText(msg);
            this.bar.setValue(progress);
        });
    }


    public JLabel getLabel() {
        return label;
    }

    public JProgressBar getBar() {
        return bar;
    }
}