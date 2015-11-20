package legacy.editors.nanocad;

import javax.swing.JFrame;

public class nanocadMain
{
    public static JFrame nano;

    public static void main(String args[])
    {
        showNanocad();
    }

    public static void showNanocad() {
        nano = new nanocadFrame2();
        nano.setSize(700, 600);
        nano.setLocationRelativeTo(null);
        nano.setVisible(true);
    }
}


