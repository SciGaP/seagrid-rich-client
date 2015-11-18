package nanocad;

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
        nano.setBounds(10, 10, 700, 600);
        nano.setVisible(true);
    }
}


