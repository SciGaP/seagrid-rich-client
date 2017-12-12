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
package cct.ssh;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import cct.interfaces.FileBrowserInterface;
import cct.interfaces.FileChooserInterface;
import cct.tools.filebrowser.FileChooserDialog;
import cct.tools.filebrowser.SFTPBrowser;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class SSHPanel
    extends JPanel implements FileChooserInterface {

  static final String DIVIDER = "@";
  static final String DEFAULT_HOST = "defaultHost";
  static final String HOST_EDITABLE = "hostEditable";
  JLabel jLabel1 = new JLabel();
  JComboBox hostComboBox = new JComboBox();
  JLabel jLabel2 = new JLabel();
  JComboBox userComboBox = new JComboBox();
  JButton connectButton = new JButton();
  FileChooserDialog fileChooserDialog = null;
  private Preferences userPrefs = Preferences.userNodeForPackage(getClass());
  final static String SSHHostKey = "ssh-host";
  final static String SSHUserKey = "ssh-user";
  private Set<String> hosts = new HashSet<String>();
  private Set<String> users = new HashSet<String>();
  private Map sshClients = new HashMap();
  private JButton removeHostButton = new JButton();
  private JTextField jTextField1 = new JTextField();
  private JLabel jLabel3 = new JLabel();
  private JButton removeUserButton = new JButton();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  static final Logger logger = Logger.getLogger(SSHPanel.class.getCanonicalName());

  public SSHPanel() {
    try {
      jbInit();
      this.validate();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {

    this.setMinimumSize(new Dimension(500, 100));
    this.setPreferredSize(new Dimension(500, 100));

    this.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Remote Host: ");
    jLabel2.setToolTipText("");
    jLabel2.setText(" User Name:");
    connectButton.setToolTipText("Connect to Host");
    connectButton.setText("Connect");
    connectButton.addActionListener(new SSHPanel_connectButton_actionAdapter(this));
    hostComboBox.setPreferredSize(new Dimension(100, 19));
    hostComboBox.setToolTipText("Enter Host and Press Enter");
    hostComboBox.setEditable(true);
    hostComboBox.addActionListener(new SSHPanel_hostComboBox_actionAdapter(this));
    userComboBox.setPreferredSize(new Dimension(100, 19));
    userComboBox.setToolTipText("Enter username and Press Enter");
    userComboBox.setEditable(true);
    userComboBox.addActionListener(new SSHPanel_userComboBox_actionAdapter(this));
    removeHostButton.setToolTipText("Remove Host from the List");
    removeHostButton.setText("Remove");
    removeHostButton.addActionListener(new SSHPanel_removeHostButton_actionAdapter(this));
    jTextField1.setEnabled(false);
    jTextField1.setToolTipText("");
    jTextField1.setText("22");
    jTextField1.setColumns(6);
    jLabel3.setToolTipText("");
    jLabel3.setText("Port: ");
    removeUserButton.setToolTipText("Remove User from the List");
    removeUserButton.setText("Remove");
    removeUserButton.addActionListener(new SSHPanel_removeUserButton_actionAdapter(this));
    this.add(jLabel1,
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0),
        0, 0));
    this.add(hostComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(removeHostButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(jLabel3, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(jTextField1, new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(userComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(removeUserButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
    this.add(connectButton, new GridBagConstraints(3, 1, 2, 1, 0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    // --- Reading Hosts

    hostComboBox.setEnabled(false);
    hostComboBox.addItem("");
    for (int i = 0; i < 1000; i++) {
      String value = userPrefs.get(SSHHostKey + i, null);
      if (value == null) {
        break;
      }

      if (hosts.contains(value)) {
        continue;
      }
      hosts.add(value);

      //String tokens[] = value.split("@");

      //hostComboBox.addItem(tokens[1]);
      //userComboBox.addItem(tokens[0]);

      hostComboBox.addItem(value);
    }
    hostComboBox.setEnabled(true);

    // --- Reading users

    userComboBox.setEnabled(false);
    userComboBox.addItem("");
    for (int i = 0; i < 1000; i++) {
      String value = userPrefs.get(SSHUserKey + i, null);
      if (value == null) {
        break;
      }

      if (users.contains(value)) {
        continue;
      }
      users.add(value);

      userComboBox.addItem(value);
    }
    userComboBox.setEnabled(true);

    // ---

    String className = this.getClass().getCanonicalName();
    try {

      Properties props = cct.GlobalSettings.getCustomProperties();

      if (props != null) {


        //--- Default host

        String defHost = props.getProperty(className + DIVIDER + DEFAULT_HOST);
        if (defHost != null) {
          hostComboBox.setSelectedItem(defHost);
          if (!hosts.contains(defHost)) {
            hosts.add(defHost);
          }
        }

        String hostEditable = props.getProperty(className + DIVIDER + HOST_EDITABLE);
        if (hostEditable != null) {
          Boolean editable;
          try {
            editable = Boolean.parseBoolean(hostEditable);
            hostComboBox.setEditable(editable);
          } catch (Exception exx) {
          }
        }

      }

    } catch (Exception ex) {
      logger.warning("Cannot open custom properties file" + cct.GlobalSettings.getCustomPropertiesURL() + " : "
          + ex.getMessage());
    }
  }

  public String getHost() {
    return hostComboBox.getSelectedItem().toString();
  }

  public String getUserName() {
    return userComboBox.getSelectedItem().toString();
  }

  public int getPort() throws Exception {
    int port = 22;
    try {
      port = Integer.parseInt(jTextField1.getText().trim());
    } catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() + " : wrong port value: " + jTextField1.getText().trim()
          + " Using default value " + port);
    }
    return port;
  }

  @Override
  public String pwd() {
    if (fileChooserDialog == null || fileChooserDialog.getPressedKey() == FileChooserDialog.CANCEL) {
      return null;
    }
    return fileChooserDialog.pwd();
  }

  @Override
  public void setFileChooserVisible(boolean enable) throws Exception {
    FileBrowserInterface browser = null;
    if (fileChooserDialog == null) {
      try {
        checkConnection();
        Object obj = getSSHProvider();
        browser = (FileBrowserInterface) obj;
        browser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      } catch (Exception ex) {
        String host = hostComboBox.getSelectedItem().toString();
        String user = userComboBox.getSelectedItem().toString();
        //JOptionPane.showMessageDialog(null,
        //                              "Unable to connect Host " + host +
        //                              " as " + user +
        //                              " : " + ex.getMessage(),
        //                              "Error",JOptionPane.ERROR_MESSAGE);
        //return;
        throw new Exception("Unable to connect Host " + host + " as " + user
            + " : " + ex.getMessage());
      }

      fileChooserDialog = new FileChooserDialog(new Frame(),
          "Choose Remote File", true,
          browser);
      fileChooserDialog.setFileBrowser(browser);
      fileChooserDialog.setLocationRelativeTo(this.getParent());
      fileChooserDialog.setAlwaysOnTop(true);
      fileChooserDialog.setSize(-1, 450);
      fileChooserDialog.repaint();
      fileChooserDialog.pack();
    }

    // ---

    if (enable) {
      fileChooserDialog.setVisible(true);
    } else {
      fileChooserDialog.setVisible(false);
    }
  }

  @Override
  public String getDirectory() {
    if (fileChooserDialog == null || fileChooserDialog.getPressedKey() == FileChooserDialog.CANCEL) {
      return null;
    }

    String[] files = fileChooserDialog.getSelectedFolders();

    if (files == null) {
      return null;
    }

    if (files.length == 1) {
      return files[0];
    }

    JOptionPane.showMessageDialog(null,
        "INTERNAL ERROR: Multiple files are selected!",
        "Error",
        JOptionPane.ERROR_MESSAGE);

    return null;
  }

  @Override
  public String getFile() {
    if (fileChooserDialog == null || fileChooserDialog.getPressedKey() == FileChooserDialog.CANCEL) {
      return null;
    }

    String[] files = fileChooserDialog.getSelectedFiles();

    if (files == null) {
      return null;
    }

    if (files.length == 1) {
      return files[0];
    }

    JOptionPane.showMessageDialog(null,
        "INTERNAL ERROR: Multiple files are selected!",
        "Error",
        JOptionPane.ERROR_MESSAGE);

    return null;
  }

  @Override
  public String[] getFiles() {
    if (fileChooserDialog.getPressedKey() == FileChooserDialog.CANCEL) {
      return null;
    }

    String[] files = fileChooserDialog.getSelectedFiles();

    if (files == null) {
      return null;
    }

    return files;
  }

  @Override
  public String[] getDirectories() {
    if (fileChooserDialog.getPressedKey() == FileChooserDialog.CANCEL) {
      return null;
    }

    String[] files = fileChooserDialog.getSelectedFolders();

    if (files == null) {
      return null;
    }

    return files;
  }

  public void checkConnection() throws Exception {
    String host = hostComboBox.getSelectedItem().toString();

    if (host.length() < 0) {
      throw new Exception("Hostname is not set");
    }

    String user = userComboBox.getSelectedItem().toString();

    if (user.length() < 0) {
      throw new Exception("Username is not set");
    }

    Object obj = getSSHProvider();
    if (obj == null) {
      SFTPBrowser sftp = new SFTPBrowser();
      try {
        sftp.connect(host, user);
        sshClients.put(user + "@" + host, sftp);
        SSHServiceProvider.addHost(host, sftp);
        /*
        JOptionPane.showMessageDialog(null,
        "Connection to Host " + host +
        " as " + user +
        " is established successfully",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);
         */

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Unable to connect Host " + host + " as " + user
            + " : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }

    } else {
      SFTPBrowser sftp = (SFTPBrowser) obj;
      if (!sftp.isAuthenticated()) {
        try {
          sftp.reconnect();
        } catch (Exception ex) {
          throw new Exception("Unable to reconnect to Host " + host
              + "as " + user + " : " + ex.getMessage());
        }
      }
    }
  }

  public Object getSSHProvider() {
    String host = hostComboBox.getSelectedItem().toString();
    String user = userComboBox.getSelectedItem().toString();
    if (!sshClients.containsKey(user + "@" + host)) {
      System.err.println("no " + user + "@" + host + " in list");
      return null;
    }
    SFTPBrowser sftp = (SFTPBrowser) sshClients.get(user + "@" + host);
    if (!sftp.isAuthenticated()) {
      System.err.println(user + "@" + host + " is not autenticated");
      return null;
    }
    return sftp;
  }

  private void saveHosts(Set gk) {
    Iterator i = gk.iterator();
    String newValue = "";
    int count = 0;
    while (i.hasNext()) {
      newValue = (String) i.next();
      try {
        userPrefs.put(SSHHostKey + count, newValue);
      } catch (Exception ex) {
        System.err.println("Warning: unable to save host list: "
            + ex.getMessage());
      }
    }
  }

  private void saveUsers(Set gk) {
    Iterator i = gk.iterator();
    String newValue = "";
    int count = 0;
    while (i.hasNext()) {
      newValue = (String) i.next();
      try {
        userPrefs.put(SSHUserKey + count, newValue);
      } catch (Exception ex) {
        System.err.println("Warning: unable to save user list: "
            + ex.getMessage());
      }
    }
  }

  public void hostComboBox_actionPerformed(ActionEvent e) {
    if (!hostComboBox.isEnabled()) {
      return;
    }

    hostComboBox.setEnabled(false);

    int n = hostComboBox.getSelectedIndex();
    String host = null;
    Object obj = hostComboBox.getEditor().getItem();
    if (obj != null) {
      host = obj.toString();
    }

    if (n == -1 || n == 0) {
      if (obj == null) {
        hostComboBox.setEnabled(true);
        return;
      }
    } else {
      Object item = hostComboBox.getSelectedItem();
      host = item.toString().trim();
    }

    if (host.length() < 1) {
      hostComboBox.setEnabled(true);
      return;
    }

    if (hosts.contains(host)) {
      /*
      JOptionPane.showMessageDialog(null,
      "Host " + host + " is already in the list",
      "Warning",
      JOptionPane.WARNING_MESSAGE);
      GatekeeperComboBox.setEnabled(true);
       */
      hostComboBox.setEnabled(true);
      return;
    }

    hostComboBox.addItem(host);
    hostComboBox.setSelectedIndex(hostComboBox.getItemCount() - 1);
    hosts.add(host);

    saveHosts(hosts);

    hostComboBox.setEnabled(true);

  }

  public void userComboBox_actionPerformed(ActionEvent e) {
    if (!userComboBox.isEnabled()) {
      return;
    }

    userComboBox.setEnabled(false);

    String user = null;
    Object obj = userComboBox.getEditor().getItem();
    if (obj != null) {
      user = obj.toString();
    }
    int n = userComboBox.getSelectedIndex();

    if (n == -1 || n == 0) {
      if (obj == null) {
        userComboBox.setEnabled(true);
        return;
      }
    } else {
      Object item = userComboBox.getSelectedItem();
      user = item.toString().trim();
    }

    if (user.length() < 1) {
      userComboBox.setEnabled(true);
      return;
    }

    if (users.contains(user)) {
      /*
      JOptionPane.showMessageDialog(null,
      "Host " + host + " is already in the list",
      "Warning",
      JOptionPane.WARNING_MESSAGE);
      GatekeeperComboBox.setEnabled(true);
       */
      userComboBox.setEnabled(true);
      return;
    }

    userComboBox.addItem(user);
    userComboBox.setSelectedIndex(userComboBox.getItemCount() - 1);
    users.add(user);

    this.saveUsers(users);

    userComboBox.setEnabled(true);

  }

  public void connectButton_actionPerformed(ActionEvent e) {

    if (hostComboBox.getItemCount() < 1) {
      return;
    }

    if (!connectButton.isEnabled()) {
      return;
    }
    connectButton.setEnabled(false);
    hostComboBox.setEnabled(false);
    userComboBox.setEnabled(false);

    String host = hostComboBox.getSelectedItem().toString().trim();
    if (host.length() > 1) {
      String user = "";
      if (userComboBox.getItemCount() > 0) {
        user = userComboBox.getSelectedItem().toString().trim();
      }
      //SFTPBrowser
      if (user.length() < 1) { // Just ping a host
        try {
          //ConfigurationLoader.initialize(false);
          //SshConnectionProperties properties = new SshConnectionProperties();
          //properties.setHost("hostname");
          //properties.setPort(22);
          SshClient ssh = new SshClient();
          //ssh.connect(properties);
          ssh.connect(host, 22, new IgnoreHostKeyVerification());
          logger.info("System banner:\n" + ssh.getAuthenticationBanner(500));
          JOptionPane.showMessageDialog(null, host + " : Connection established successfully",
              "Success", JOptionPane.INFORMATION_MESSAGE);

          if (!hosts.contains(host)) {
            hosts.add(host);
            this.saveHosts(hosts);
          }
        } catch (java.io.IOException ex) {
          //System.err.println();
          //ex.printStackTrace();
          JOptionPane.showMessageDialog(null, "Unable to connect Host " + host + " : " + ex.getMessage(),
              "Error", JOptionPane.ERROR_MESSAGE);
        }
      } else if (sshClients.containsKey(user + "@" + host)) {
        SFTPBrowser sftp = (SFTPBrowser) sshClients.get(user + "@" + host);
        if (sftp.isAuthenticated()) {
          JOptionPane.showMessageDialog(null,
              "Connection to Host " + host + " as " + user + " is already authenticated",
              "Success", JOptionPane.INFORMATION_MESSAGE);
        } else { // !!! code here for reconnection
          try {
            sftp.reconnect();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to reconnect to Host "
                + host + "as " + user + " : " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      } else { // Make a connection
        SFTPBrowser sftp = new SFTPBrowser();

        try {
          sftp.connect(host, user);
          sshClients.put(user + "@" + host, sftp);
          SSHServiceProvider.addHost(host, sftp);
          JOptionPane.showMessageDialog(null,
              "Connection to Host " + host + " as " + user + " is established successfully",
              "Success", JOptionPane.INFORMATION_MESSAGE);

          if (!hosts.contains(host)) {
            hosts.add(host);
            this.saveHosts(hosts);
          }

          if (!users.contains(user)) {
            users.add(user);
            this.saveUsers(users);
          }

        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, "Unable to connect Host " + host
              + " as " + user + " : " + ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }

    }

    hostComboBox.setEnabled(true);
    userComboBox.setEnabled(true);
    connectButton.setEnabled(true);
  }

  public void removeHostButton_actionPerformed(ActionEvent e) {
    int n = hostComboBox.getSelectedIndex();
    Object item = hostComboBox.getSelectedItem();
    String host = item.toString().trim();

    if (host.length() < 1) {
      return;
    }

    hostComboBox.setEnabled(false);

    hosts.remove(host);
    hostComboBox.removeItemAt(n);
    if (n < hostComboBox.getItemCount()) {
      hostComboBox.setSelectedIndex(n);
    } else if (n > 0 && hostComboBox.getItemCount() > 0) {
      hostComboBox.setSelectedIndex(hostComboBox.getItemCount() - 1);
    }

    saveHosts(hosts);

    hostComboBox.setEnabled(true);
  }

  public void removeUserButton_actionPerformed(ActionEvent e) {
    int n = userComboBox.getSelectedIndex();
    Object item = userComboBox.getSelectedItem();
    String user = item.toString().trim();

    if (user.length() < 1) {
      return;
    }

    userComboBox.setEnabled(false);
    users.remove(user);
    userComboBox.removeItemAt(n);
    if (n < userComboBox.getItemCount()) {
      userComboBox.setSelectedIndex(n);
    } else if (n > 0 && userComboBox.getItemCount() > 0) {
      userComboBox.setSelectedIndex(userComboBox.getItemCount() - 1);
    }

    saveUsers(users);
    userComboBox.setEnabled(true);
  }

  private class SSHPanel_removeUserButton_actionAdapter
      implements ActionListener {

    private SSHPanel adaptee;

    SSHPanel_removeUserButton_actionAdapter(SSHPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.removeUserButton_actionPerformed(e);
    }
  }

  private class SSHPanel_removeHostButton_actionAdapter
      implements ActionListener {

    private SSHPanel adaptee;

    SSHPanel_removeHostButton_actionAdapter(SSHPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.removeHostButton_actionPerformed(e);
    }
  }

  private class SSHPanel_hostComboBox_actionAdapter
      implements ActionListener {

    private SSHPanel adaptee;

    SSHPanel_hostComboBox_actionAdapter(SSHPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.hostComboBox_actionPerformed(e);
    }
  }

  private class SSHPanel_userComboBox_actionAdapter
      implements ActionListener {

    private SSHPanel adaptee;

    SSHPanel_userComboBox_actionAdapter(SSHPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.userComboBox_actionPerformed(e);
    }
  }

  private class SSHPanel_connectButton_actionAdapter
      implements ActionListener {

    private SSHPanel adaptee;

    SSHPanel_connectButton_actionAdapter(SSHPanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      adaptee.connectButton_actionPerformed(e);
    }
  }

  public static void main(String[] args) {
    JDialog diag = new JDialog(new Frame(), "Test SSHPanel", true);
    SSHPanel panel = new SSHPanel();
    diag.add(panel);
    diag.setSize(640, 200);
    diag.pack();
    diag.setVisible(true);
    System.exit(0);
  }
}
