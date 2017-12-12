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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import cct.tools.MemoryMonitor;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class MemoryMonitorFrame extends JFrame {

   final MemoryMonitor monitor = new MemoryMonitor();
   BorderLayout borderLayout1 = new BorderLayout();

   public MemoryMonitorFrame() {
      super("Memory Monitor");
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      getContentPane().setLayout(borderLayout1);

      monitor.surf.start();
      this.addWindowListener(new MemoryMonitorFrame_this_windowAdapter(this));
      this.getContentPane().add(monitor, BorderLayout.CENTER);

      //WindowListener l = new WindowAdapter() {
      //public void windowClosing(WindowEvent e) { }
      //public void windowDeiconified(WindowEvent e) { monitor.surf.start(); }
      //public void windowIconified(WindowEvent e) { monitor.surf.stop(); }
      //};

   }

   public static void main(String s[]) {
      MemoryMonitorFrame demo = new MemoryMonitorFrame();
      WindowListener l = new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
         //public void windowDeiconified(WindowEvent e) { monitor.surf.start(); }
         //public void windowIconified(WindowEvent e) { monitor.surf.stop(); }
      };
      demo.addWindowListener(l);
      demo.pack();
      demo.setSize(new Dimension(200, 200));
      demo.setVisible(true);
   }

   public void this_windowClosing(WindowEvent e) {
      this.setVisible(false);
   }

   public void this_windowIconified(WindowEvent e) {
      monitor.surf.stop();
   }

   public void this_windowDeiconified(WindowEvent e) {
      monitor.surf.start();
   }

}

class MemoryMonitorFrame_this_windowAdapter
    extends WindowAdapter {
   private MemoryMonitorFrame adaptee;
   MemoryMonitorFrame_this_windowAdapter(MemoryMonitorFrame adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void windowClosing(WindowEvent e) {
      adaptee.this_windowClosing(e);
   }
}
