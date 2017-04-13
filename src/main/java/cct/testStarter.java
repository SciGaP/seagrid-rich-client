package cct;

import cct.dialogs.Frame1;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class testStarter {
   boolean packFrame = false;

   /**
    * Construct and show the application.
    */
   public testStarter() {
      JFrame.setDefaultLookAndFeelDecorated(true);
      Frame1 frame = new Frame1();

      //frame.addMenuItem("File", new JMenuItem("Get Geometry"));

      // Validate frames that have preset sizes
      // Pack frames that have useful preferred size info, e.g. from their layout
      if (packFrame) {
         frame.pack();
      }
      else {
         frame.validate();
      }

      // Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if (frameSize.height > screenSize.height) {
         frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
         frameSize.width = screenSize.width;
      }
      frame.setLocation( (screenSize.width - frameSize.width) / 2,
                        (screenSize.height - frameSize.height) / 2);
      frame.setVisible(true);
   }

   /**
    * Application entry point.
    *
    * @param args String[]
    */
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         @Override
        public void run() {
            try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
               exception.printStackTrace();
            }

            new testStarter();

         }
      });
   }
}
