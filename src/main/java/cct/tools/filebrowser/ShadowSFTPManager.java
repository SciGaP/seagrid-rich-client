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

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import cct.interfaces.FTPManagerInterface;
import cct.interfaces.FileBrowserInterface;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 * Shadow Manager stands behind GUIs and manages SSH connections. Could be run as a SFTP Browser standalone application
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
enum FTP_OBJECT {
  
  FTP_FRAME, FTP_FILE_CHOOSER, FTP_CLIENT_INTERFACE
}

public class ShadowSFTPManager
        implements ShadowManagerInterface {
  
  private static final int maxStroredHosts = 10;
  static final String NEW_HOST = "New host";
  static final String NUMBER_OF_HOSTS_KEY = "numberOfHosts";
  static final String HOST_KEY = "host";
  String defaultProtocol = "sftp";
  private boolean exitOnAllClose = true;
  private Preferences prefs;
  private EditHostsDialog editHostsDialog = null;
  private java.util.List<String> knownHosts = new ArrayList<String>();
  private Map<String, UserHostInfo> authenticatedHosts = new HashMap<String, UserHostInfo>();
  private Map<Component, SFTPObject> activeBrowsers = new HashMap<Component, SFTPObject>();
  private FTPManagerInterface manager = null;
  
  public ShadowSFTPManager() {
    
    try {
      manager = new FTPManager();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    
    int numHosts = 0;
    try {
      prefs = Preferences.userNodeForPackage(this.getClass());
      numHosts = prefs.getInt(NUMBER_OF_HOSTS_KEY, 0);
      if (numHosts < 0) {
        numHosts = 0;
      } else if (numHosts > maxStroredHosts) {
        numHosts = maxStroredHosts;
      }
      
      for (int i = 0; i < numHosts; i++) {
        String host = prefs.get(HOST_KEY + String.valueOf(i), null);
        if (host != null) {
          knownHosts.add(host);
        }
      }
    } catch (Exception ex) {
      System.err.println("Error retrieving known host list: " + ex.getMessage() + " Ignored...");
    }
    
  }
  
  public void setExitOnAllClose(boolean enable) {
    exitOnAllClose = enable;
  }
  
  public static void main(String[] args) {
    ShadowSFTPManager shadowsftpmanager = new ShadowSFTPManager();
    //shadowsftpmanager.start();
    shadowsftpmanager.startFileChooser();
  }

  /**
   * Starts file browser
   */
  public void startFileBrowser() {

    // --- Check for at least one active file browser
    Set set = activeBrowsers.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      SFTPObject sft_object = (SFTPObject) me.getValue();
      if (sft_object.objectSwitch == FTP_OBJECT.FTP_FRAME) {
        return; // One borwser is already active...
      }
    }
    
    SFTPObject sft_object = new SFTPObject(this, FTP_OBJECT.FTP_FRAME);
    activeBrowsers.put(sft_object.getComponent(), sft_object);
    sft_object.setKnownHosts(getKnownHosts());
    sft_object.setVisible(true);
  }
  
  public void startFileChooser() {
    SFTPObject sft_object = new SFTPObject(this, FTP_OBJECT.FTP_FILE_CHOOSER);
    activeBrowsers.put(sft_object.getComponent(), sft_object);
    //sft_object.setKnownHosts(getKnownHosts());
    sft_object.setVisible(true);
  }
  
  @Override
  public void processEditHosts(Component component) {
    if (knownHosts.size() < 1) {
      return;
    }
    
    String[] hosts = new String[knownHosts.size()];
    knownHosts.toArray(hosts);
    
    if (editHostsDialog == null) {
      editHostsDialog = new EditHostsDialog(null, "Edit hosts list", true);
      editHostsDialog.setAlwaysOnTop(true);
      editHostsDialog.setLocationByPlatform(true);
    }
    
    editHostsDialog.setListData(hosts);
    
    if (component != null) {
      editHostsDialog.setLocationRelativeTo(component);
    }
    
    editHostsDialog.setVisible(true);
    
    if (!editHostsDialog.isOKPressed()) {
      return;
    }
    
    int[] indices = editHostsDialog.getSelectedIndices();
    
    if (indices == null || indices.length < 1) {
      return;
    }
    
    for (int i = indices.length - 1; i >= 0; i--) {
      knownHosts.remove(indices[i]);
    }
    
    updateKnownHosts();
  }
  
  @Override
  public String[] getKnownHosts() {
    String[] hosts = new String[knownHosts.size() + 1];
    hosts[0] = NEW_HOST;
    for (int i = 0; i < knownHosts.size(); i++) {
      String hostKey = knownHosts.get(i);
      hosts[i + 1] = hostKey.substring(hostKey.indexOf("@") + 1) + " as " + hostKey.substring(0, hostKey.indexOf("@"));
    }
    return hosts;
  }
  
  public void saveKnownHosts() {
    // --- Save known hosts
    if (knownHosts.size() > 0) {
      try {
        //prefs = Preferences.userNodeForPackage(this.getClass());
        prefs.putInt(NUMBER_OF_HOSTS_KEY, knownHosts.size());
        System.out.println("Saving sftp host(s) before exit\n" + NUMBER_OF_HOSTS_KEY + "=" + knownHosts.size());
        
        for (int i = 0; i < knownHosts.size(); i++) {
          prefs.put(HOST_KEY + String.valueOf(i), knownHosts.get(i));
          System.out.println(HOST_KEY + String.valueOf(i) + "=" + knownHosts.get(i));
        }
        for (int i = knownHosts.size(); i < maxStroredHosts; i++) {
          prefs.remove(HOST_KEY + String.valueOf(i));
        }
      } catch (Exception ex) {
        System.err.println("Error retrieving known host list: " + ex.getMessage() + " Ignored...");
      }
    }
  }
  
  @Override
  public void processExit(Component component) {
    SFTPObject sft_object = activeBrowsers.get(component);
    activeBrowsers.remove(component);
    sft_object.dispose();

    // --- Save known hosts
    saveKnownHosts();

    // --- Exit
    if (activeBrowsers.size() == 0 && exitOnAllClose) {
      System.exit(0);
    }
  }
  
  @Override
  public void processConnection(String host_info, Component component) throws Exception {
    String hostKey = null;
    String host = null, username = null;
    char[] password = null;
    int port = 22; // !!!
    boolean ask_for_password = false;
    
    if (host_info.equals(NEW_HOST)) {
      ConnectHostDialog connectDialog = new ConnectHostDialog(null, "Connect to Host", true);
      connectDialog.setAlwaysOnTop(true);
      connectDialog.setLocationRelativeTo(component);
      int n = connectDialog.showDialog();
      if (n == ConnectHostDialog.CANCEL) {
        return;
      }
      host = connectDialog.getHost().toLowerCase();
      if (host.length() < 1) {
        throw new Exception("Host name is not specified");
      }
      username = connectDialog.getUsername();
      if (username.length() < 1) {
        throw new Exception("User name is not specified");
      }
      password = connectDialog.getPassword();
      port = connectDialog.getPort();
      if (port < 1) {
        throw new Exception("Wrong port number: " + port);
      }
      
      hostKey = username + "@" + host;
      
    } else if (host_info.contains(" as ")) {
      host = host_info.substring(0, host_info.indexOf(" "));
      username = host_info.substring(host_info.lastIndexOf(" ") + 1);
      hostKey = username + "@" + host;
      ask_for_password = true;
    } else {
      throw new Exception("Shadow Manager Error: Unknown host request: " + host_info);
    }

    // --- make connection and authentication...
    SFTPObject sftpObject = activeBrowsers.get(component);
    FileBrowserInterface browser = null;
    //if ((sftpObject == null || !sftpObject.connected) ) {

    browser = manager.getFTPBrowser(defaultProtocol);
    if (browser == null) {
      throw new Exception("Cannot get File Browser for the protocol: " + defaultProtocol);
    }
    
    if (authenticatedHosts.containsKey(hostKey)) {
      UserHostInfo hostInfo = authenticatedHosts.get(hostKey);
      try {
        browser.connect(hostInfo.Host, hostInfo.User, hostInfo.Password, hostInfo.Port);
      } catch (Exception ex) {
        throw ex;
      }
      
    } else {
      
      if (ask_for_password) {
        ConnectHostDialog connectDialog = new ConnectHostDialog(null, "Connect to " + host, true);
        connectDialog.hostTextField.setText(host);
        connectDialog.hostTextField.setEditable(false);
        connectDialog.userTextField.setText(username);
        connectDialog.userTextField.setEditable(false);
        connectDialog.passwordTextField.grabFocus();
        connectDialog.portNumber = port;
        connectDialog.portTextField.setText(String.valueOf(port));
        connectDialog.setAlwaysOnTop(true);
        connectDialog.setLocationRelativeTo(component);
        connectDialog.setVisible(true);
        int n = connectDialog.getKeyPressed();
        if (n == ConnectHostDialog.CANCEL) {
          return;
        }
        
        password = connectDialog.getPassword();
        port = connectDialog.getPort();
        if (port < 1) {
          throw new Exception("Wrong port number: " + port);
        }
      }
      
      try {
        browser.connect(host, username, password, port);
      } catch (Exception ex) {
        throw ex;
      }
      UserHostInfo hostInfo = new UserHostInfo(host, username, password, port);
      authenticatedHosts.put(hostKey, hostInfo);
    }
    //}

    // ---it;s already authenticated component
    //else {
    //}
// --- So, authentication is successful
    addKnownHost(hostKey);
    
    if (sftpObject == null) {
      createNewBrowser(host, username, browser);
    } else if (!sftpObject.connected) {
      sftpObject.setFileBrowser(host, username, browser);
    } else if (sftpObject.connected && sftpObject.browserFrame != null) {
      createNewBrowser(host, username, browser);
    } else if (sftpObject.connected) {
      sftpObject.setFileBrowser(host, username, browser);
      //createNewBrowser(host, username, browser);
    }
    
    updateKnownHosts();
  }
  
  void addKnownHost(String host) {
    if (knownHosts.contains(host)) {
      return;
    }
    
    if (knownHosts.size() >= maxStroredHosts) {
      for (int i = knownHosts.size() - 1; i >= maxStroredHosts - 1; i--) {
        knownHosts.remove(i);
      }
    }
    
    knownHosts.add(0, host);
  }
  
  void createNewBrowser(String host, String username, FileBrowserInterface browser) {
    SFTPObject sft_object = new SFTPObject(this, FTP_OBJECT.FTP_FRAME);
    activeBrowsers.put(sft_object.getComponent(), sft_object);
    sft_object.setKnownHosts(getKnownHosts());
    sft_object.setFileBrowser(host, username, browser);
    sft_object.setVisible(true);
  }
  
  void updateKnownHosts() {
    String[] hosts = getKnownHosts();
    Set set = activeBrowsers.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      SFTPObject sft_object = (SFTPObject) me.getValue();
      sft_object.setKnownHosts(hosts);
    }
    
    saveKnownHosts();
  }
  
  public void addClient(ShadowClientInterface client) {
    SFTPObject sft_object = new SFTPObject(this, client);
    activeBrowsers.put(sft_object.getComponent(), sft_object);
    sft_object.setKnownHosts(getKnownHosts());
    sft_object.setVisible(true);
  }
}

class SFTPObject {
  
  ShadowSFTPManager shadowManager = null;
  SFTPBrowserFrame browserFrame = null;
  SFTPFileChooserDialog fileChooser = null;
  ShadowClientInterface clientInterface = null;
  FileBrowserInterface fileBrowser = null;
  Window guiObject = null;
  FTP_OBJECT objectSwitch = null;
  String remoteHost = null;
  boolean connected = false;
  
  public SFTPObject(ShadowSFTPManager manager, FTP_OBJECT object) {
    shadowManager = manager;
    objectSwitch = object;
    switch (object) {
      case FTP_FRAME:
        browserFrame = new SFTPBrowserFrame(manager);
        guiObject = browserFrame;
        break;
      case FTP_FILE_CHOOSER:
        fileChooser = new SFTPFileChooserDialog(null, "Choose file", true);
        guiObject = fileChooser;
        break;
      case FTP_CLIENT_INTERFACE:
        break;
    }

    //browserFrame.pack();
    guiObject.pack();
    //browserFrame.setLocationByPlatform(true);
    guiObject.setLocationByPlatform(true);
  }
  
  public SFTPObject(ShadowSFTPManager manager, ShadowClientInterface client) {
    shadowManager = manager;
    objectSwitch = FTP_OBJECT.FTP_CLIENT_INTERFACE;
    clientInterface = client;
  }
  
  public void setVisible(boolean enable) {
    //browserFrame.setVisible(enable);
    if (guiObject != null) {
      guiObject.setVisible(true);
    }
  }
  
  public Component getComponent() {
    //return browserFrame;
    if (guiObject != null) {
      return guiObject;
    }
    return clientInterface.getComponent();
  }
  
  public void setKnownHosts(String[] hosts) {
    switch (objectSwitch) {
      case FTP_FRAME:
        browserFrame.setKnownHosts(hosts);
        break;
      case FTP_CLIENT_INTERFACE:
        clientInterface.setKnownHosts(hosts);
        break;
    }
  }
  
  public void setFileBrowser(String host, String username, FileBrowserInterface file_browser) {
    fileBrowser = file_browser;
    switch (objectSwitch) {
      case FTP_FRAME:
        browserFrame.setFileBrowser(fileBrowser);
        browserFrame.setTitle("sftp: " + host + " as " + username);
        break;
      case FTP_FILE_CHOOSER:
        fileChooser.setFileBrowser(fileBrowser);
        fileChooser.setTitle("sftp: " + host + " as " + username);
        break;
      case FTP_CLIENT_INTERFACE:
        clientInterface.setFileBrowser(host, username, file_browser);
        break;
      
    }
    
    connected = true;
  }
  
  public void dispose() {
    //browserFrame.setVisible(false);
    if (guiObject != null) {
      guiObject.setVisible(false);
    }
    if (fileBrowser != null) {
      fileBrowser.disconnect();
    }
    //browserFrame.dispose();
    if (guiObject != null) {
      guiObject.dispose();
    }
  }
}

class UserHostInfo {
  
  String Host, User;
  char[] Password = null;
  int Port;
  
  public UserHostInfo(String host, String user, char[] password, int port) {
    Host = host;
    User = user;
    Port = port;
    if (password == null || password.length < 1) {
      return;
    }
    Password = new char[password.length];
    for (int i = 0; i < Password.length; i++) {
      Password[i] = password[i];
    }
  }
}
