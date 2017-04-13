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

package cct.tools.filebrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import cct.interfaces.FileBrowserInterface;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 * It's a marionette class which is used by a class implementing ShadowManagerInterface interface
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SFTPBrowserFrame
    extends JFrame implements ActionListener {
   BorderLayout borderLayout1 = new BorderLayout();
   JFileBrowserPanel jFileBrowserPanel1 = new JFileBrowserPanel();
   JMenuBar jMenuBar1 = new JMenuBar();
   JMenu fileMenu = new JMenu();
   JMenu jMenu1 = new JMenu();
   JMenu connectMenu = new JMenu();
   JMenuItem exitMenuItem = new JMenuItem();
   JToolBar jToolBar1 = new JToolBar();

   ShadowManagerInterface shadowManager = null;
   JMenuItem editHostsMenuItem = new JMenuItem();
   public SFTPBrowserFrame(ShadowManagerInterface shadow_manager) {
      setShadowManager(shadow_manager);
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void setShadowManager(ShadowManagerInterface shadow_manager) {
      shadowManager = shadow_manager;
   }

   private void jbInit() throws Exception {
      getContentPane().setLayout(borderLayout1);
      jMenu1.setText("File");
      connectMenu.setText("Connect to");
      exitMenuItem.setText("Exit");
      exitMenuItem.addActionListener(this);
      this.setJMenuBar(jMenuBar1);
      editHostsMenuItem.setText("Edit Host List");
      editHostsMenuItem.addActionListener(this);
      jMenuBar1.add(jMenu1);
      this.getContentPane().add(jFileBrowserPanel1, BorderLayout.CENTER);
      this.getContentPane().add(jToolBar1, BorderLayout.NORTH);
      jMenu1.add(connectMenu);
      jMenu1.add(editHostsMenuItem);
      jMenu1.addSeparator();
      jMenu1.add(exitMenuItem);
      jMenuBar1.add(jMenu1);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            shadowManager.processExit(we.getWindow());
         }
      });

   }

   public void setKnownHosts(String[] hosts) {
      connectMenu.removeAll();
      for (int i = 0; i < hosts.length; i++) {
         JMenuItem item = new JMenuItem(hosts[i]);
         item.setActionCommand(hosts[i]);
         item.addActionListener(this);
         connectMenu.add(item);
      }
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (shadowManager == null) {
         System.err.println("No Shadow Manager is set...");
         return;
      }

      if (e.getSource() == exitMenuItem) {
         shadowManager.processExit(this);
         return;
      }
      else if (e.getSource() == editHostsMenuItem) {
         shadowManager.processEditHosts(this);
         return;
      }

      String command = e.getActionCommand();
      if (shadowManager == null) {
         System.err.println("No Shadow Manager is set...");
         return;
      }
      try {
         shadowManager.processConnection(command, this);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
      }
   }

   public void setFileBrowser(FileBrowserInterface fileBrowser) {
      jFileBrowserPanel1.setFileBrowser(fileBrowser);
      jFileBrowserPanel1.repaint();
      pack();
   }

   public static void main(String[] args) {
      SFTPBrowserFrame sftpbrowserframe = new SFTPBrowserFrame(null);
   }
}
