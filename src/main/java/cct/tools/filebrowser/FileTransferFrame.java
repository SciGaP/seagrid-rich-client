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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ComboBoxEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;

import cct.interfaces.FTPManagerInterface;
import cct.interfaces.FileBrowserInterface;

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
public class FileTransferFrame
    extends JFrame {
  Map browsers = new HashMap();
  FTPManagerInterface ftpManager = null;
  ConnectHostDialog connectDialog = new ConnectHostDialog(this,
      "Connect to Host", true);
  BorderLayout borderLayout1 = new BorderLayout();
  FileTransferPanel fileTransferPanel1 = new FileTransferPanel();

  ImageIcon connect = new ImageIcon(cct.resources.Resources.class.
                                    getResource(
                                            "cct/images/icons16x16/client_network.png"));

  ImageIcon disconnect = new ImageIcon(cct.resources.Resources.class.
                                       getResource(
                                               "cct/images/icons16x16/delete2.png"));

  ImageIcon serverOK = new ImageIcon(cct.resources.Resources.class.
                                     getResource("cct/images/icons16x16/server.png"));
  ImageIcon serverDead = new ImageIcon(cct.resources.Resources.class.
                                       getResource(
                                               "cct/images/icons16x16/server_error.png"));

  JToolBar jToolBar1 = new JToolBar();
  JComboBox hostsComboBox = new JComboBox();
  JButton disconnectButton = new JButton();
  JButton connectButton = new JButton();
  JComboBox protocolComboBox = new JComboBox();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  static final Logger logger = Logger.getLogger(FileTransferFrame.class.getCanonicalName());

  public FileTransferFrame(FTPManagerInterface ftp_manager) {
    ftpManager = ftp_manager;
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    disconnectButton.setMaximumSize(new Dimension(23, 23));
    disconnectButton.setMinimumSize(new Dimension(23, 23));
    disconnectButton.setPreferredSize(new Dimension(23, 23));
    disconnectButton.setToolTipText("Disconnect");
    disconnectButton.setIcon(disconnect);
    jLabel1.setToolTipText("");
    jLabel1.setText("   Host:");
    protocolComboBox.setMaximumSize(new Dimension(64, 49));
    jLabel2.setToolTipText("");
    jLabel2.setText("Protocol:");
    hostsComboBox.addActionListener(new
                                    FileTransferFrame_hostsComboBox_actionAdapter(this));
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hostsComboBox.setEditable(false);
    connectButton.addActionListener(new
                                    FileTransferFrame_connectButton_actionAdapter(this));
    jToolBar1.add(jLabel2);
    jToolBar1.add(protocolComboBox);
    jToolBar1.add(jLabel1);

    jToolBar1.add(hostsComboBox);
    jToolBar1.add(connectButton);
    jToolBar1.add(disconnectButton);
    this.getContentPane().add(jToolBar1, BorderLayout.NORTH);
    this.getContentPane().add(fileTransferPanel1, BorderLayout.CENTER);

    connectButton.setMinimumSize(new Dimension(23, 23));
    connectButton.setMaximumSize(new Dimension(23, 23));
    connectButton.setPreferredSize(new Dimension(23, 23));
    connectButton.setIcon(connect);
    connectButton.setToolTipText("Connect");

    String[] protocols = ftpManager.getAvailableFTPProtocols();
    for (int i = 0; i < protocols.length; i++) {
      protocolComboBox.addItem(protocols[i]);
    }

    HostComboBoxRenderer renderer = new HostComboBoxRenderer();
    hostsComboBox.setRenderer(renderer);
  }

  public static void main(String[] args) {
    //FileBrowserInterface fileBrowser = new LocalFileBrowser();
    Runtime rt = Runtime.getRuntime();
    logger.info("Processors: " + rt.availableProcessors() +
                       " Memory: " + rt.freeMemory());
    FTPManagerInterface manager = new FTPManager();
    FileTransferFrame filetransferframe = new FileTransferFrame(manager);
    filetransferframe.setSize(640, 480);
    filetransferframe.setVisible(true);
    //System.exit(0);
  }

  public void hostsComboBox_actionPerformed(ActionEvent e) {
    if (!hostsComboBox.isEnabled()) {
      return;
    }

    hostsComboBox.setEnabled(false);
    Object item = hostsComboBox.getSelectedItem();

    String host = item.toString();

    FileBrowserInterface browser = (FileBrowserInterface) browsers.get(host);

    fileTransferPanel1.switchFileBrowser(browser);
    fileTransferPanel1.repaint();

    hostsComboBox.setEnabled(true);
  }

  public void connectButton_actionPerformed(ActionEvent e) {
    if (!connectButton.isEnabled()) {
      return;
    }
    connectButton.setEnabled(false);

    String protocol = protocolComboBox.getSelectedItem().toString();

    String host = "Unknown host";

    String username = "";
    char[] password = "".toCharArray();
    int port = 22;

    if (protocol.equalsIgnoreCase("sftp")) {
      connectDialog.setAlwaysOnTop(true);
      connectDialog.setLocationRelativeTo(this);
      int n = connectDialog.showDialog();
      if (n == ConnectHostDialog.CANCEL) {
        return;
      }
      host = connectDialog.getHost().toLowerCase();
      username = connectDialog.getUsername();
      password = connectDialog.getPassword();
      port = connectDialog.getPort();
    }

    FileBrowserInterface browser = null;

    if (browsers.containsKey(host)) {

    }

    else {
      browser = ftpManager.getFTPBrowser(protocol);

      try {
        if (protocol.equalsIgnoreCase("sftp")) {
          this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          browser.connect(host, username, password, port);

        }
        browsers.put(host, browser);

        hostsComboBox.setEnabled(false);
        hostsComboBox.addItem(host);
        hostsComboBox.setSelectedIndex(hostsComboBox.getItemCount() - 1);
        hostsComboBox.setEnabled(true);

        fileTransferPanel1.setFileBrowser(browser);
        fileTransferPanel1.repaint();
        pack();
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                                      ex.getMessage(), "Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
    this.setCursor(Cursor.getDefaultCursor());
    connectButton.setEnabled(true);

  }

  class HostComboBoxRenderer
      extends JLabel implements ListCellRenderer {

    public HostComboBoxRenderer() {
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */

    @Override
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {

      //Get the selected index. (The index param isn't
      //always valid, so just use the value.)

      if (value == null) {
        return this;
      }

      //int selectedIndex = ( (Integer) value).intValue();

      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      }
      else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }

      //Set the icon and text.  If icon was null, say so.

      String host = value.toString();
      //logger.info("Value: " + value.toString() + " Class: " +
      //                   value.getClass().getCanonicalName());
      setText(host);

      if (!browsers.containsKey(host)) {
        logger.info(host + " is not found in browsers: " +
                           browsers.toString());
      }
      FileBrowserInterface browser = (FileBrowserInterface) browsers.get(
          host);

      if (browser == null) {
        logger.info("Internal error: Server - null; browser: " +
                           browsers.toString());
        setIcon(serverDead);
      }
      else if (browser.isAuthenticated()) {
        //logger.info("Server - OK");
        setIcon(serverOK);
      }
      else {
        //logger.info("Server - dead");
        setIcon(serverDead);
      }

      return this;
    }
  }

  class HostEditor
      extends JTextField implements ComboBoxEditor, ActionListener {

    @Override
    public Component getEditorComponent() {
      return null;
    }

    @Override
    public Object getItem() {
      return this.getText();
    }

    @Override
    public void removeActionListener(ActionListener l) {
      this.removeActionListener(l);
    }

    @Override
    public void selectAll() {
      this.selectAll();
    }

    @Override
    public void addActionListener(ActionListener l) {
      this.addActionListener(l);
    }

    @Override
    public void setItem(Object anObject) {
      this.setText(anObject.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
  }
}

class FileTransferFrame_connectButton_actionAdapter
    implements ActionListener {
  private FileTransferFrame adaptee;
  FileTransferFrame_connectButton_actionAdapter(FileTransferFrame adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.connectButton_actionPerformed(e);
  }
}

class FileTransferFrame_hostsComboBox_actionAdapter
    implements ActionListener {
  private FileTransferFrame adaptee;
  FileTransferFrame_hostsComboBox_actionAdapter(FileTransferFrame adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.hostsComboBox_actionPerformed(e);
  }
}
