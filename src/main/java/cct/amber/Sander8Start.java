/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.amber;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * <p>Title: Preparation of input file for Sander 8 program</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Sander8Start {
   boolean packFrame = false;
   static final String VERSION_8 = "8";
   static final String VERSION_9 = "9";
   static private String Version = VERSION_9;

   /**
    * Construct and show the application.
    */
   public Sander8Start(String sanderVersion) {
      JFrame frame = null;
      Sander8Frame s8 = null;
      Sander9Frame s9 = null;

      if (sanderVersion.equalsIgnoreCase(VERSION_8)) {
         s8 = new Sander8Frame(new Sander8JobControl());
         frame = s8;
      }
      else if (sanderVersion.equalsIgnoreCase(VERSION_9)) {
         s9 = new Sander9Frame(new Sander9JobControl());
         frame = s9;
      }
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
      if (args.length > 0 && args[0].equalsIgnoreCase(VERSION_8)) {
         Version = VERSION_8;
      }
      else if (args.length > 0 && args[0].equalsIgnoreCase(VERSION_9)) {
         Version = VERSION_9;
      }
      else if (args.length > 0) {
         System.err.println("Unknown version: " + args[0] + " Using default version " + Version);
      }

      SwingUtilities.invokeLater(new Runnable() {
         @Override
        public void run() {
            try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
               exception.printStackTrace();
            }

            new Sander8Start(Version);
         }
      });
   }
}
