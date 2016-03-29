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
        if(nano == null || !nano.isShowing()) {
            nano = new nanocadFrame2();
            nano.setSize(700, 600);
            nano.setResizable(true);
            nano.setLocationRelativeTo(null);
            nano.setVisible(true);
        }
        nano.toFront();
        nano.requestFocus();
    }
}