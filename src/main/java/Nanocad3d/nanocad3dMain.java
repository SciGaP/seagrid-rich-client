package Nanocad3d;

import javax.swing.*;

public class nanocad3dMain
{
    public static JFrame nano;

    public static void main(String args[])
    {
        showNanocad3D();
    }

    public static void showNanocad3D() {
        if(nano == null || !nano.isShowing()) {
            nano = new Nanocad3D();
            nano.setSize(700, 600);
            nano.setResizable(true);
            nano.setLocationRelativeTo(null);
            nano.setVisible(true);
        }
        nano.toFront();
        nano.requestFocus();
    }
}