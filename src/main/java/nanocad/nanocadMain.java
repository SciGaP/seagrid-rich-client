package nanocad;

import javax.swing.JFrame;

public class nanocadMain
{
    public static JFrame nano;
//    public static nanocadFrame2 nano;

    public static void main(String args[])
    {
        nano = new nanocadFrame2();
        //nano = new nanocadFrame();
        nano.setBounds(10, 10, 700, 600);
        nano.setVisible(true);
    }
}


