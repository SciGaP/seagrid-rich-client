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

import cct.interfaces.FileBrowserGUIInterface;
import cct.interfaces.FileBrowserInterface;
import cct.tools.FileFilterImpl;
import cct.tools.FileUtilities;
import cct.tools.UserPasswordDialog;
import com.sshtools.j2ssh.DirectoryOperation;
import com.sshtools.j2ssh.FileTransferProgress;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.*;
import com.sshtools.j2ssh.io.UnsignedInteger32;
import com.sshtools.j2ssh.io.UnsignedInteger64;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 * It's a workhorse which performs all SFTP Browser functions
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SFTPBrowser
    extends MouseAdapter implements FileBrowserInterface, ActionListener, ListSelectionListener {

  static final String upDirectoryItem = "Up a Directory";
  static final String RefreshItem = "Refresh";
  static final String NewForlderItem = "New Folder";
  static final String GoToForlderItem = "Go to Folder...";
  static final String DeleteItem = "Delete";
  static final String UploadDialogItem = "Upload Dialog";
  static final String SelectAllItem = "Select All";
  static final String DownloadItem = "Download";
  static final String PropertiesItem = "Properties";
  FileFilterImpl Filter = null;
  boolean Busy = false;
  boolean tableInitiated = false;
  int tableSelectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  FileBrowserGUIInterface topGUI = null;
  SelectionInfoInterface selectionInfo = null;
  Map directories = new HashMap();
  JTable table = new JTable(5, 5);
  SshClient ssh = null;
  String hostName = null;
  String userName = null;
  char[] password = null;
  String currentPWD = null;
  java.util.List currentFileList = null;
  java.util.List authenticationMethods = null;
  int AuthenticationResult = AuthenticationProtocolState.FAILED;
  int portNumber = 22;
  SftpClient sftpClient = null;
  boolean DEBUG = true;
  String[] columnNames = {
    "Remote Name", "Size", "Type", "Modified", "Attributes"};
  int[] sortStatus = null;
  TableSorter sorter = null;
  Object[][] data = null;
  NumberFormat numberFormat = NumberFormat.getInstance();
  JPopupMenu generalPopup, filePopup;
  FileBrowserTableModel tModel = null;
  JFileChooser localFileChooser = null;
  FilePropertiesDialog fileProps = null;
  static final Logger logger = Logger.getLogger(SFTPBrowser.class.getCanonicalName());

  public SFTPBrowser() {
    try {
      tModel = new FileBrowserTableModel();
      localFileChooser = new JFileChooser();
      fileProps = new FilePropertiesDialog(new Frame(), "File Properties");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public int getFileCount() {
    if (currentFileList == null) {
      return 0;
    }
    int count = 0;
    for (int i = 0; i < currentFileList.size(); i++) {
      SftpFile file = (SftpFile) currentFileList.get(i);
      if (file.isDirectory()) {
        continue;
      }
      ++count;
    }
    return count;
  }

  public String[] getFileNames() {
    if (currentFileList == null) {
      return null;
    }
    java.util.List<String> files = new ArrayList<String>();
    for (int i = 0; i < currentFileList.size(); i++) {
      SftpFile file = (SftpFile) currentFileList.get(i);
      if (file.isDirectory()) {
        continue;
      }
      files.add(file.getFilename());
    }
    if (files.size() < 1) {
      return null;
    }
    String[] fileNames = new String[files.size()];
    files.toArray(fileNames);
    return fileNames;
  }

  public void setSelectionInfoInterface(SelectionInfoInterface selection_info) {
    table.getSelectionModel().removeListSelectionListener(this);
    selectionInfo = selection_info;
    if (selectionInfo != null) {
      table.getSelectionModel().addListSelectionListener(this);
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    if (selectionInfo == null) {
      return;
    }
    if (table.getSelectedRowCount() == 0 || table.getSelectedRowCount() > 1) {
      return;
    }
    int n = table.getSelectedRow();

    //int firstIndex = e.getFirstIndex();
    //int lastIndex = e.getLastIndex();
    //if (firstIndex == lastIndex) {
    String type = (String) table.getValueAt(n, 2); // Get type of item
    if (type.equalsIgnoreCase("Folder")) {
      selectionInfo.setSelectedFileName("");
      return;
    }
    try {
      String item = this.getFileName(table.getValueAt(n, 0));
      selectionInfo.setSelectedFileName(item);
    } catch (Exception ex) {
      System.err.println(this.getClass().getCanonicalName() + " : cannot get file name");
    }
    //}
  }

  public void listUsingFilePattern(String filePattern) throws Exception {
    java.util.List fileList = null;
    String newDir;

    try {
      fileList = sftpClient.ls();
      newDir = sftpClient.pwd();
    } catch (Exception ex) {
      //ex.printStackTrace();
      throw ex;
    }

    for (int i = 0; i < fileList.size(); i++) {
      SftpFile file = (SftpFile) fileList.get(i);
      if (file.getFilename().equals(".") && file.isDirectory()) {
        fileList.remove(i);
        break;
      }
    }

    if (filePattern != null && filePattern.length() > 0) {

      //Pattern pattern = Pattern.compile(filePattern);

      // --- Apply file pattern

      for (int i = fileList.size() - 1; i >= 0; i--) {
        SftpFile file = (SftpFile) fileList.get(i);
        if (file.isDirectory()) {
          continue;
        }

        if (file.getFilename().matches(filePattern)) {
          continue;
        }

        /*
        Matcher matcher = pattern.matcher(file.getFilename());
        if (matcher.find()) {
        continue;
        }
         */

        fileList.remove(i);
      }
    }

    this.currentFileList = fileList;
    currentPWD = newDir;

    setupTable();
    directories.put(currentPWD, currentFileList);
    topGUI.updateFieView(table);
    topGUI.updateCWD(currentPWD);

  }

  public void setFileFilter(FileFilterImpl filter) {
    Filter = filter;
    refresh();
  }

  public String getHost() {
    return hostName;
  }

  public String getUsername() {
    return userName;
  }

  public char[] getPassword() {
    return password;
  }

  public int getPort() {
    return portNumber;
  }

  public void get(String remote, String local) throws Exception {
    try {
      sftpClient.get(currentPWD + "/" + remote, local);
    } catch (Exception ex) {
      throw new Exception("Cannot transfer " + currentPWD + "/" + remote + " to " + local + " : " + ex.getMessage());
    }

  }

  public void get(String remote, String local, FileTransferProgress progress) throws Exception {
    try {
      sftpClient.get(currentPWD + "/" + remote, local, progress);
    } catch (Exception ex) {
      throw new Exception("Cannot transfer " + currentPWD + "/" + remote + " to " + local + " : " + ex.getMessage());
    }

  }

  public String pwd() {
    if (ssh == null) {
      return null;
    }
    if (sftpClient == null) {
      return null;
    }
    return sftpClient.pwd();
  }

  public SftpClient getSftpClient() throws Exception {
    if (sftpClient == null) {
      try {
        sftpClient = ssh.openSftpClient();
      } catch (Exception ex) {
        throw ex;
      }
    }
    return sftpClient;
  }

  public SshClient getSshClient() {
    return ssh;
  }

  /**
   * Sets selection mode: The following selectionMode values are allowed:
   * ListSelectionModel.SINGLE_SELECTION Only one list index can be selected at a time.
   * ListSelectionModel.SINGLE_INTERVAL_SELECTION One contiguous index interval can be selected at a time.
   * ListSelectionModel.MULTIPLE_INTERVAL_SELECTION In this mode, there's no restriction on what can be selected. This is the default.
   * @param mode int - an integer specifying the type of selections that are permissible
   */
  public void setSelectionMode(int mode) throws Exception {
    switch (mode) {
      case ListSelectionModel.SINGLE_SELECTION:
      case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
      case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
        tableSelectionMode = mode;
        table.setSelectionMode(mode);
        return;
    }
    throw new Exception("Unknown table selection mode: " + mode
        + " Available modes: "
        + ListSelectionModel.SINGLE_SELECTION + ","
        + ListSelectionModel.SINGLE_INTERVAL_SELECTION + ","
        + ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        + "(default)");
  }

  /**
   * For processing popup menu items
   * @param e ActionEvent
   */
  public void actionPerformed(ActionEvent e) {
    String arg = e.getActionCommand();
    if (arg.equals(upDirectoryItem)) {
      this.upDirectory();
    } else if (arg.equals(RefreshItem)) {
      this.refresh();
    } else if (arg.equals(NewForlderItem)) {
      mkdir();
    } else if (arg.equals(GoToForlderItem)) {
      goToFolderDialog();
    } else if (arg.equals(DeleteItem)) {
      this.removeSelectedPaths();
    } else if (arg.equals(UploadDialogItem)) {
      openUploadDialog();
    } else if (arg.equals(SelectAllItem)) {
      table.selectAll();
    } else if (arg.equals(DownloadItem)) {
      this.downloadSelected();
    } else if (arg.equals(PropertiesItem)) {
      openFilePropertiesDialog();
    }
  }

  void openFilePropertiesDialog() {
    if (table.getSelectedRowCount() < 1) {
      JOptionPane.showMessageDialog(null,
          "Select File(s)/Folder(s) first!",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (table.getSelectedRowCount() == 1) {
      int n = table.getSelectedRow();
      SftpFile file = null;
      Object obj = table.getValueAt(n, 0);
      if (obj instanceof FileName) {
        FileName fname = (FileName) obj;
        String filename = fname.getFileName();
        for (int i = 0; i < this.currentFileList.size(); i++) { // Redo this
          SftpFile f = (SftpFile) currentFileList.get(i);
          if (filename.equals(f.getFilename())) {
            file = f;
            break;
          }
        }
      } else {
        file = (SftpFile) table.getValueAt(n, 0);
      }

      fileProps.setTitle(file.getFilename() + " Properties");
      FileAttributes attr = file.getAttributes();

      if (file.isDirectory()) {
        fileProps.setFileName(file.getFilename());
        fileProps.setFileType("Folder");
        fileProps.setFileSize("0 bytes");
        fileProps.setFileIcon(FileUtilities.getFolderIcon32x32());
      } else {
        fileProps.setFileName(file.getFilename());
        fileProps.setFileType("File");
        UnsignedInteger64 size2 = attr.getSize();
        fileProps.setFileSize(numberFormat.format(size2.doubleValue())
            + " bytes");
        fileProps.setFileIcon(FileUtilities.getIcon32x32(file.getFilename()));
      }

      String location = file.getAbsolutePath().substring(0,
          file.getAbsolutePath().lastIndexOf(file.getFilename()));
      fileProps.setFileLocation(location);

      fileProps.setFileModified(attr.getModTimeString());

      fileProps.setPermissions(attr.getPermissionsString());

      fileProps.setAlwaysOnTop(true);
      fileProps.setLocationRelativeTo(null);
      int key = fileProps.showDialog();

      if (key == FilePropertiesDialog.SET) {
        int permissions = fileProps.getPermissions();
        try {
          Busy = true;
          topGUI.setBusy(true);
          //table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          sftpClient.chmod(permissions, file.getAbsolutePath());
          refresh();
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null,
              "Cannot change permissions for "
              + file.getAbsolutePath() + " : "
              + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
        Busy = false;
        topGUI.setBusy(false);
      }
    }
  }

  public boolean isAuthenticated() {
    if (ssh == null) {
      return false;
    }
    return ssh.isAuthenticated();
  }

  /**
   * Downloads selected items
   */
  public void downloadSelected() {
    if (table.getSelectedRowCount() < 1) {
      JOptionPane.showMessageDialog(null,
          "Select File(s)/Folder(s) first!",
          "Warning",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Setup list of remote files to transfer
    int[] rows = table.getSelectedRows();
    java.util.List finalList = new ArrayList(rows.length);

    for (int i = 0; i < rows.length; i++) {
      Object obj = table.getValueAt(rows[i], 0);
      String path = null;
      try {
        path = getFileName(obj);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
            "Internal Error: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        continue;
      }

      SftpFile file = null;

      for (int j = 0; j < currentFileList.size(); j++) { // Stupid thing
        file = (SftpFile) currentFileList.get(j);
        if (path.equals(file.getFilename())) {
          path = file.getAbsolutePath();
          break;
        }
      }

      if (file == null) {
        System.err.println("INTERNAL ERROR: no file " + path
            + " in file list\n");
        //errorMess += "INTERNAL ERROR: no file " + path + " in file list\n";
        continue;
      }

      finalList.add(file);
    }

    localFileChooser.setDialogTitle("Select Folder for Download");
    localFileChooser.setControlButtonsAreShown(true);
    localFileChooser.setDragEnabled(true);
    localFileChooser.setMultiSelectionEnabled(false);
    localFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    Component parent = null;
    if (this.topGUI != null && topGUI instanceof Component) {
      parent = (Component) topGUI;
    }
    //int returnVal = localFileChooser.showOpenDialog(parent);
    int returnVal = localFileChooser.showDialog(parent, "Start Download");
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File dir = localFileChooser.getSelectedFile();

      localFileChooser.setCurrentDirectory(dir);
      FileTranferWorker downloader = new FileTranferWorker(finalList,
          dir.getAbsolutePath());
      downloader.start();
    } else {
    }

  }

  public String getFileName(Object fileObject) throws Exception {
    String path = null;
    if (fileObject instanceof String) {
      path = (String) fileObject;
    } else if (fileObject instanceof SftpFile) {
      SftpFile file = (SftpFile) fileObject;
      path = file.getFilename(); // Get selected value
    } else if (fileObject instanceof FileName) {
      FileName fn = (FileName) fileObject;
      path = fn.getFileName();
    } else {
      throw new Exception("Unknown class for file name: "
          + fileObject.getClass().getCanonicalName());
    }
    return path;
  }

  /**
   * Opens upload dialog
   */
  public void openUploadDialog() {
    //localFileChooser.setMinimumSize(new Dimension(325, 345));
    localFileChooser.setDialogTitle("Select File(s) for Upload");
    localFileChooser.setControlButtonsAreShown(true);
    localFileChooser.setDragEnabled(true);
    localFileChooser.setMultiSelectionEnabled(true);
    localFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    Component parent = null;
    if (this.topGUI != null && topGUI instanceof Component) {
      parent = (Component) topGUI;
    }
    //int returnVal = localFileChooser.showOpenDialog(parent);
    int returnVal = localFileChooser.showDialog(parent,
        "Upload Selected File(s)");
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File[] files = localFileChooser.getSelectedFiles();
      //File file = localFileChooser.getSelectedFile();
      //This is where a real application would open the file.

      FileTranferWorker uploader = new FileTranferWorker(files,
          currentPWD);
      uploader.start();
    } else {
    }

  }

  /**
   * Upload a file to the remote computer.
   * @param file String - the path/name of the local file
   * @throws Exception -  if an IO error occurs or the file does not exist
   */
  public void uploadFile(String file) throws Exception {
    try {
      sftpClient.put(file);
    } catch (java.io.IOException ex) {
      throw ex;
    }
  }

  /**
   * Remove a file or directory from the remote computer
   * @param path String - the path of the remote file/directory
   */
  public void rm(String path) {

    try {
      sftpClient.rm(path);
    } catch (java.io.IOException ex) {
      JOptionPane.showMessageDialog(null,
          "Error removing " + path + " : "
          + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    refresh();
  }

  /**
   * Removes selected file(s)/folder(s)
   */
  public void removeSelectedPaths() {
    if (table.getSelectedRowCount() == 0) {
      JOptionPane.showMessageDialog(null,
          "Select file(s)/folder(s) first!",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int[] rows = table.getSelectedRows();
    int removed = 0;
    String errorMess = "";

    for (int i = 0; i < rows.length; i++) {

      String path = null;
      Object obj = null;
      try {
        obj = table.getValueAt(rows[i], 0);
        path = getFileName(obj);
      } catch (Exception ex) {
        errorMess += "INTERNAL ERROR: cannot get file name as a String: " + obj.getClass().getCanonicalName() + "\n";
        continue;
      }
      SftpFile file = null;

      for (int j = 0; j < currentFileList.size(); j++) { // Stupid thing
        file = (SftpFile) currentFileList.get(j);
        if (path.equals(file.getFilename())) {
          break;
        }
      }

      if (file == null) {
        errorMess += "INTERNAL ERROR: no file " + path + " in file list\n";
        continue;
      }

      try {
        if (DEBUG) {
          logger.info("Removing: " + file.getAbsolutePath());
        }
        sftpClient.rm(file.getAbsolutePath());
        ++removed;
      } catch (java.io.IOException ex) {
        errorMess += "Error removing " + file.getAbsolutePath() + " : " + ex.getMessage() + "\n";
      }
    }

    if (errorMess.length() > 0) {
      JOptionPane.showMessageDialog(null, errorMess, "Error",
          JOptionPane.ERROR_MESSAGE);
    }

    if (removed == 0) {
      return;
    }

    refresh();

  }

  public void goToFolderDialog() {
    Component parent = null;
    if (topGUI != null && topGUI instanceof Component) {
      parent = (Component) topGUI;
    }
    Object folder = JOptionPane.showInputDialog(parent, "Enter Folder Name:", "Go to Remote Folder", JOptionPane.QUESTION_MESSAGE, null, null,
        currentPWD);

    if (folder == null || folder.toString().trim().length() == 0) {
      return;
    }

    String folderName = folder.toString();
    if (DEBUG) {
      logger.info("goToFolderDialog: folder: " + folderName);
    }
    cd(folderName);
  }

  /**
   * Changes the working directory on the remote server
   * @param dir String - the new working directory
   */
  public void cd(String dir) {
    try {
      changeAbsolutePath(dir);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    topGUI.updateFieView(table);
    topGUI.updateCWD(currentPWD);
    if (DEBUG) {
      logger.info("cd: changed to " + dir + " PWD: " + currentPWD);
    }
  }

  public void mkdir(String dir) {
    // Relative or Absolute path?

    String newDir;
    if (!dir.startsWith("/")) { // Relative path
      newDir = currentPWD + "/" + dir;
    } else {
      newDir = dir;
    }

    try {
      makeDirectory(newDir);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
          ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    refresh();
  }

  public void mkdir() {
    Component parent = null;
    if (topGUI != null && topGUI instanceof Component) {
      parent = (Component) topGUI;
    }
    Object folder = JOptionPane.showInputDialog(parent,
        "Enter folder name",
        "Create New Forlder",
        JOptionPane.QUESTION_MESSAGE, null, null,
        "");

    if (folder == null || folder.toString().trim().length() == 0) {
      return;
    }

    String folderName = folder.toString();

    mkdir(folderName);
  }

  /**
   * Creates a new directory on the remote server
   * @param dir String - the name of the new directory
   * @throws Exception
   */
  private void makeDirectory(String dir) throws Exception {
    try {
      sftpClient.mkdir(dir);
    } catch (java.io.IOException ex) {
      throw new Exception("Error creating directory " + dir + " : "
          + ex.getMessage());
    }
  }

  private void listCurrentDirectory() throws Exception {
    java.util.List fileList = null;
    String newDir;

    try {
      fileList = sftpClient.ls();
      newDir = sftpClient.pwd();
    } catch (Exception ex) {
      //ex.printStackTrace();
      throw ex;
    }

    for (int i = 0; i < fileList.size(); i++) {
      SftpFile file = (SftpFile) fileList.get(i);
      if (file.getFilename().equals(".") && file.isDirectory()) {
        fileList.remove(i);
        break;
      }
    }

    // --- Apply file filter
    for (int i = fileList.size() - 1; i >= 0; i--) {
      SftpFile file = (SftpFile) fileList.get(i);
      if (file.isDirectory()) {
        continue;
      }

      if (Filter == null) {
        continue;
        //System.err.println("File Filter is not set");
      } else if (Filter.accept(file.getFilename(), false)) {
        continue;
      }

      fileList.remove(i);
    }

    this.currentFileList = fileList;
    currentPWD = newDir;
  }

  private void setupTable() {

    /*
    Object[][] data = new Object[currentFileList.size()][columnNames.length];

    for (int i = 0; i < currentFileList.size(); i++) {
    SftpFile file = (SftpFile) currentFileList.get(i);
    FileAttributes attr = file.getAttributes();

    data[i][0] = file.getFilename();

    UnsignedInteger64 size = attr.getSize();
    //data[i][1] = size.toString();
    data[i][1] = size;

    //data[i][2] = attr.getModTimeString();
    if (attr.isDirectory()) {
    data[i][2] = "Folder";
    }
    else if (attr.isLink()) {
    data[i][2] = "Link";
    }
    else {
    data[i][2] = "File";
    }

    //data[i][2] = attr.getModTimeString();
    data[i][3] = attr.getModifiedTime();

    data[i][4] = attr.getPermissionsString();
    //data[i][3] = attr.getPermissions();

    }
     */

    //FileBrowserTableModel tModel = new FileBrowserTableModel(columnNames,
    //    data);
    //FileBrowserTableModel tModel = new FileBrowserTableModel();

    // --- Remember previous sorting status

    if (sorter != null) {
      for (int i = 0; i < sortStatus.length; i++) {
        sortStatus[i] = sorter.getSortingStatus(i);
      }
    }

    sorter = new TableSorter(tModel);

    if (sortStatus == null) {
      sorter.setSortingStatus(0, TableSorter.ASCENDING);
      sortStatus = new int[columnNames.length];
      for (int i = 0; i < sortStatus.length; i++) {
        sortStatus[i] = sorter.getSortingStatus(i);
      }
    } else {
      for (int i = 0; i < sortStatus.length; i++) {
        sorter.setSortingStatus(i, sortStatus[i]);
      }

    }

    table.setModel(sorter);
    sorter.setTableHeader(table.getTableHeader());

  }

  public Component getComponent() {

    if (tableInitiated) {
      return table;
    }

    currentFileList = null;

    try {
      if (sftpClient == null) {
        sftpClient = ssh.openSftpClient();
      }
      currentFileList = sftpClient.ls();
      currentPWD = sftpClient.pwd();
      for (int i = 0; i < currentFileList.size(); i++) {
        SftpFile file = (SftpFile) currentFileList.get(i);
        if (file.getFilename().equals(".") && file.isDirectory()) {
          currentFileList.remove(i);
          break;
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

    table.setModel(tModel);
    table.setShowGrid(false);
    Dimension dim = table.getIntercellSpacing();
    dim.width += 2;
    table.setIntercellSpacing(dim);
    initColumnSizes(table);
    table.setSelectionMode(tableSelectionMode);

    TableSorter sorter = new TableSorter(tModel);
    table.setModel(sorter);
    sorter.setTableHeader(table.getTableHeader());

    // --- Create popup menues
    createPopups();

    topGUI.updateCWD(currentPWD);

    // Store all data....

    //ArrayList bunch = new ArrayList();
    directories.put(currentPWD, currentFileList);

    //table.setDefaultRenderer(SftpFile.class, new FileNameRenderer(true));
    table.setDefaultRenderer(FileName.class, new FileNameRenderer(true));

    //table.setDefaultRenderer(UnsignedInteger64.class,
    //                         new UnsignedInteger64Renderer());
    table.setDefaultRenderer(FileSize.class,
        new FileSizeRenderer());

    table.getTableHeader().setToolTipText(
        "Click to sort; Shift-Click to sort in reverse order");
    tableInitiated = true;

    return table;
  }

  /**
   * Creates popup menues
   */
  private void createPopups() {

    //Create General popup menu.
    generalPopup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem(upDirectoryItem);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);
    menuItem = new JMenuItem(RefreshItem);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);

    menuItem = new JMenuItem(SelectAllItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);

    menuItem = new JMenuItem(UploadDialogItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);

    menuItem = new JMenuItem(GoToForlderItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);

    menuItem = new JMenuItem(NewForlderItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    generalPopup.add(menuItem);

    //Create File popup menu.
    filePopup = new JPopupMenu();
    menuItem = new JMenuItem("Open");
    menuItem.setEnabled(false);
    //menuItem.addActionListener(this);
    filePopup.add(menuItem);
    menuItem = new JMenuItem(DownloadItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    filePopup.add(menuItem);

    menuItem = new JMenuItem(DeleteItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    filePopup.add(menuItem);

    menuItem = new JMenuItem("Rename");
    menuItem.setEnabled(false);
    //menuItem.addActionListener(this);
    filePopup.add(menuItem);

    menuItem = new JMenuItem(PropertiesItem);
    menuItem.setEnabled(true);
    menuItem.addActionListener(this);
    filePopup.add(menuItem);

    //menuItem.addActionListener(this);
    filePopup.add(menuItem);

    //Add listener to components that can bring up popup menus.
    table.addMouseListener(this);

  }

  public void setTopGUI(FileBrowserGUIInterface guiInterface) {
    topGUI = guiInterface;
  }

  /*
   * This method picks good column sizes.
   * If all column heads are wider than the column's cells'
   * contents, then you can just use column.sizeWidthToFit().
   */
  private void initColumnSizes(JTable table) {
    FileBrowserTableModel model = (FileBrowserTableModel) table.getModel();
    TableColumn column = null;
    Component comp = null;
    int headerWidth = 0;
    int cellWidth = 0;
    Object[] longValues = getLongestValues(table);
    TableCellRenderer headerRenderer =
        table.getTableHeader().getDefaultRenderer();

    for (int i = 0; i < table.getColumnCount(); i++) {
      column = table.getColumnModel().getColumn(i);

      comp = headerRenderer.getTableCellRendererComponent(
          null, column.getHeaderValue(),
          false, false, 0, 0);
      headerWidth = comp.getPreferredSize().width;

      comp = table.getDefaultRenderer(model.getColumnClass(i)).
          getTableCellRendererComponent(
          table, longValues[i],
          false, false, 0, i);
      cellWidth = comp.getPreferredSize().width;

      if (DEBUG) {
        logger.info("Initializing width of column "
            + i + ". "
            + "headerWidth = " + headerWidth
            + "; cellWidth = " + cellWidth);
      }

      //XXX: Before Swing 1.1 Beta 2, use setMinWidth instead.
      column.setPreferredWidth(Math.max(headerWidth, cellWidth));
    }
  }

  private Object[] getLongestValues(JTable table) {

    Object[] longValues = new Object[table.getColumnCount()];
    int[] widths = new int[table.getColumnCount()];
    for (int j = 0; j < table.getColumnCount(); j++) {
      widths[j] = 0;
    }
    Component comp = null;
    int cellWidth = 0;
    FileBrowserTableModel model = (FileBrowserTableModel) table.getModel();

    for (int i = 0; i < table.getRowCount(); i++) {
      for (int j = 0; j < table.getColumnCount(); j++) {
        Object value = table.getValueAt(i, j);

        comp = table.getDefaultRenderer(model.getColumnClass(j)).
            getTableCellRendererComponent(
            table, value,
            false, false, 0, j);
        cellWidth = comp.getPreferredSize().width;

        if (cellWidth > widths[j]) {
          widths[j] = cellWidth;
          longValues[j] = value;
        }
      }
    }
    return longValues;
  }

  /**
   * Tries to connect to the host
   * @param hostname String
   * @param port int
   * @throws Exception
   */
  public void connectToHost(String hostname, int port) throws Exception {
    try {
      //ConfigurationLoader.initialize(false);
      //SshConnectionProperties properties = new SshConnectionProperties();
      //properties.setHost("hostname");
      //properties.setPort(22);
      ssh = new SshClient();
      //ssh.connect(properties);
      logger.info("Connecting to " + hostname + ":" + port + "...");
      ssh.connect(hostname, port, new IgnoreHostKeyVerification());
      logger.info("System banner:\n" + ssh.getAuthenticationBanner(500));
    } catch (java.io.IOException ex) {
      logger.severe("Cannot connect to " + hostname + ":" + port + ": " + ex.getMessage());
      throw ex;
    }

    if (!ssh.isConnected()) {
      logger.info("Cannot connect to " + hostname);
      throw new Exception("Cannot connect to " + hostname);
    } else {
      logger.info("Connected to " + hostname + ":" + port);
    }
  }

  public void disconnect() {
    ssh.disconnect();
  }

  /**
   * Connect the client to the server using default connection properties.
   * This call attempts to connect to the hostname specified on the standard SSH port of 22 and uses all the default connection properties.
   * @param hostname String - The hostname of the server to connect
   * @throws Exception -  If an IO error occurs during the connect operation
   */
  public void connect(String hostname) throws Exception {

    try {
      connectToHost(hostname, portNumber);
    } catch (Exception ex) {
      throw new Exception("Cannot connect to host " + hostname + " " + ex.getMessage());
    }

    UserPasswordDialog dialog = new UserPasswordDialog();
    dialog.setLocationRelativeTo(null);
    dialog.setAlwaysOnTop(true);
    dialog.setMessage("Connecting to " + hostname);
    dialog.setMessageVisible(true);
    dialog.pack();
    dialog.setVisible(true);

    if (!dialog.isOKPressed()) {
      throw new Exception("Authentification cancelation");
    }

    userName = dialog.getUsername();
    password = dialog.getPassword();

    // Retrieving the available authentication Methods

    try {

      authenticationMethods = getAvailableAuthMethods(userName);

      logger.info("Available Authentication Methods:");
      for (int i = 0; i < authenticationMethods.size(); i++) {
        logger.info(authenticationMethods.get(i).toString());
      }

    } catch (Exception ex) {
      throw new Exception(
          "Unable to get available Authentification Methods for user "
          + userName + " " + ex.getMessage());
    }

    AuthenticationResult = AuthenticationProtocolState.FAILED;

    try {
      AuthenticationResult = authenticate(userName, password);
    } catch (Exception ex) {
      throw ex;
    }

    if (AuthenticationResult == AuthenticationProtocolState.FAILED) {
      System.err.println("The authentication failed");
      throw new Exception("The authentication failed");
    }

    if (AuthenticationResult == AuthenticationProtocolState.PARTIAL) {
      logger.info("The authentication succeeded but another"
          + "authentication is required");
      throw new Exception("The authentication succeeded but another"
          + "authentication is required");
    }

    sftpClient = ssh.openSftpClient();
  }

  public void connect(String hostname, String username) throws Exception {

    try {
      connectToHost(hostname, portNumber);
    } catch (Exception ex) {
      throw new Exception("Cannot connect to host " + hostname + " "
          + ex.getMessage());
    }

    UserPasswordDialog dialog = new UserPasswordDialog();
    dialog.setUsername(username);
    if (username.trim().length() > 0) {
      dialog.setUsernameEnabled(false);
    } else {
      dialog.setUsernameEnabled(true);
    }
    dialog.setLocationRelativeTo(null);
    dialog.setAlwaysOnTop(true);
    dialog.setVisible(true);

    if (!dialog.isOKPressed()) {
      throw new Exception("Authentification cancelation");
    }

    userName = dialog.getUsername();
    password = dialog.getPassword();

    // Retrieving the available authentication Methods

    try {

      authenticationMethods = getAvailableAuthMethods(userName);

      logger.info("Available Authentication Methods:");
      for (int i = 0; i < authenticationMethods.size(); i++) {
        logger.info(authenticationMethods.get(i).toString());
      }

    } catch (Exception ex) {
      throw new Exception(
          "Unable to get available Authentification Methods for user "
          + userName + " " + ex.getMessage());
    }

    AuthenticationResult = AuthenticationProtocolState.FAILED;

    try {
      AuthenticationResult = authenticate(userName, password);
    } catch (Exception ex) {
      throw ex;
    }

    if (AuthenticationResult == AuthenticationProtocolState.FAILED) {
      System.err.println("The authentication failed");
      throw new Exception("The authentication failed");
    }

    if (AuthenticationResult == AuthenticationProtocolState.PARTIAL) {
      logger.info("The authentication succeeded but another"
          + "authentication is required");
      throw new Exception("The authentication succeeded but another"
          + "authentication is required");
    }

  }

  private int authenticateUser(String username, char[] pass) throws Exception {

    // --- Get authentication methods

    try {

      this.authenticationMethods = getAvailableAuthMethods(username);

      if (DEBUG) {
        logger.info("Available Authentication Methods:");
        for (int i = 0; i < authenticationMethods.size(); i++) {
          //logger.info(authenticationMethods.get(i).toString());
          logger.info((i + 1) + " : " + authenticationMethods.get(i).toString());
        }
      }

    } catch (Exception ex) {
      throw new Exception(
          "Unable to get available Authentification Methods for user "
          + username + " " + ex.getMessage());
    }

    // --- Athenticate user

    int AuthResult = AuthenticationProtocolState.FAILED;

    try {
      AuthResult = authenticate(username, pass);
    } catch (Exception ex) {
      throw ex;
    }

    if (AuthResult == AuthenticationProtocolState.FAILED) {
      System.err.println("The authentication failed");
      throw new Exception("The authentication failed");
    }

    if (AuthResult == AuthenticationProtocolState.PARTIAL) {
      System.err.println("The authentication succeeded but another"
          + "authentication is required");
      throw new Exception("The authentication succeeded but another"
          + "authentication is required");
    }
    return AuthResult;
  }

  /**
   * Tries to reconnect
   * @throws Exception
   */
  public void reconnect() throws Exception {
    try {
      connect(getHost(), getUsername(), getPassword(), getPort());
    } catch (Exception ex) {
      logger.severe("Cannot reconnect to the host " + getHost() + ":" + getPort() + " as " + getUsername() + ": " + ex.getMessage());
      throw ex;
    }
  }

  /**
   * Connect the client to the server using default connection properties.
   * This call attempts to connect to the hostname specified on the port of 22
   * and uses all the default connection properties.
   * @param hostname String
   * @param username String
   * @param password char[]
   * @param port int
   * @throws Exception
   */
  public void connect(String hostname, String username, char[] pass, int port) throws Exception {
    try {
      logger.info("Connecting to " + hostname + ":" + port);
      connectToHost(hostname, port);
      logger.info("Connection to " + hostname + ":" + port + " established");
      portNumber = port;
    } catch (Exception ex) {
      logger.severe("Cannot connect to " + hostname + ":" + port + ": " + ex.getMessage());
      throw new Exception("Cannot connect to host " + hostname + " " + ex.getMessage());
    }

    try {
      logger.info("Authenticating " + username + " on " + hostname + ":" + port + "...");
      AuthenticationResult = authenticateUser(username, pass);
    } catch (Exception ex) {
      throw new Exception("Cannot authenticate user " + username + " " + ex.getMessage());
    }

    sftpClient = ssh.openSftpClient();
  }

  /**
   * Changes the working directory on the remote server.
   * @param directory String - the new working directory
   * @throws Exception
   */
  public void setRemoteDirectory(String directory) throws Exception {
    try {
      this.sftpClient.cd(directory);
    } catch (Exception ex) {
      //ex.printStackTrace();
      throw ex;
    }
  }

  public static void main(String[] args) {
    SFTPBrowser localfilebrowser = new SFTPBrowser();
  }

  /**
   * Returns the list of available authentication methods for a given user.
   * @param username String The name of the account for which you require the available authentication methods
   * @return List - A list of Strings, for example "password", "publickey" & "keyboard-interactive"
   * @throws Exception If an IO error occurs during the operation
   */
  public java.util.List getAvailableAuthMethods(String username) throws
      Exception {
    java.util.List available = null;
    // Retrieving the available authentication Methods

    // It is possible at any time after the connection has been established
    // and before authentication has been completed to request a list of authentication methods that can be used.
    // The getAvailableAuthMethods method returns a list of authentication method names.

    try {
      available = ssh.getAvailableAuthMethods(username);
    } catch (java.io.IOException ex) {
      //System.err.println();
      ex.printStackTrace();
      throw ex;
    }
    return available;
  }

  /**
   * Authenticate the user on the remote host.
   * @return int - The authentication result
   */
  public int authenticate(String username, char[] pass) throws Exception {

    int result = AuthenticationProtocolState.FAILED;

    try {
      if (authenticationMethods.contains("password")) {
        logger.info("Athenticating " + username + " using Password Authentication Client");
        PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();

        //PasswordAuthenticationDialog dialog = new PasswordAuthenticationDialog(new
        //    Frame());
        //dialog.setVisible(true);

        //pwd.setAuthenticationPrompt(dialog); // ???

        pwd.setUsername(username);

        pwd.setPassword(String.valueOf(pass));

        result = ssh.authenticate(pwd);

      } else if (authenticationMethods.contains("keyboard-interactive")) {
        logger.info("Athenticating " + username + " using keyboard interactive Client");
        /*
         * Create the keyboard-interactive instance
         */
        KBIAuthenticationClient kbi = new KBIAuthenticationClient();
        // Get the users name
        //System.out.print("Username? ");
        // Read the password
        //username = reader.readLine();

        kbi.setUsername(username);
        password = pass;

        // Set the callback interface
        kbi.setKBIRequestHandler(new KBIRequestHandler() {

          public void showPrompts(String name, String instructions,
              KBIPrompt[] prompts) {
            logger.info("Name: " + name);
            logger.info("Instructions: " + instructions);
            String response;
            if (prompts != null) {
              for (int i = 0; i < prompts.length; i++) {
                //if (DEBUG) {
                logger.info("Prompt " + (i + 1) + ": " + prompts[i].getPrompt());
                //}

                if (prompts[i].getPrompt().toLowerCase().indexOf("password") != -1
                    || prompts[i].getPrompt().toLowerCase().indexOf("pass") != -1) {
                  response = String.valueOf(password);
                } else {
                  Object obj = JOptionPane.showInputDialog(null,
                      prompts[i].getPrompt(), "Response prompt",
                      JOptionPane.QUESTION_MESSAGE, null, null, "");
                  response = obj.toString();
                }
                //try {
                //response = reader.readLine();

                prompts[i].setResponse(response);

                //}
                //catch (IOException ex) {
                //  prompts[i].setResponse("");
                //  ex.printStackTrace();
                //}
              }
            }
          }
        });

        // Try the authentication
        result = ssh.authenticate(kbi);
      }
    } catch (java.io.IOException ex) {
      throw new Exception("Cannot authnenticate user " + username + " : " + ex.getMessage());
    }

    return result;
  }

  public void refresh() {
    if (DEBUG) {
      logger.info("Refreshing directory: " + currentPWD);
    }

    try {
      listCurrentDirectory();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
          "Unable to list directory "
          + currentPWD + " : " + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    setupTable();
    directories.put(currentPWD, currentFileList);
    topGUI.updateFieView(table);
    topGUI.updateCWD(currentPWD);
  }

  public void upDirectory() {
    try {
      changeRelativePath("..");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    topGUI.updateFieView(table);
    topGUI.updateCWD(currentPWD);

  }

  private void changeAbsolutePath(String newDir) throws Exception {
    try {
      setRemoteDirectory(newDir);
    } catch (Exception ex) {
      throw new Exception("Unable to change directory to "
          + newDir + " : " + ex.getMessage());
    }

    if (directories.containsKey(newDir)) {
      currentFileList = (java.util.List) directories.get(newDir);
      currentPWD = newDir;
      logger.info("Retrieving old file list for " + currentPWD);
    } else {

      try {
        listCurrentDirectory();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
            "Unable to list directory "
            + newDir + " : " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      logger.info("Adding directory listing for " + currentPWD);
      directories.put(currentPWD, currentFileList);
    }

    setupTable();

  }

  private void changeRelativePath(String Dir) throws Exception {
    if (Dir.equalsIgnoreCase(".")) {
      return; // The same directory
    }
    String newDir;
    if (Dir.equalsIgnoreCase("..")) {
      newDir = "/";
      if (currentPWD.lastIndexOf("/") != -1) {
        newDir = currentPWD.substring(0, currentPWD.lastIndexOf("/"));
      }

      if (newDir.length() == 0) {
        newDir = "/";
      }

    } else {
      newDir = currentPWD + "/" + Dir;
    }

    try {
      changeAbsolutePath(newDir);
    } catch (Exception ex) {
      throw ex;
    }

  }

  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
      logger.info(" double click");
      if (table.getSelectedRowCount() == 0
          || table.getSelectedRowCount() > 1) {
        return;
      }
      int n = table.getSelectedRow();
      String type = (String) table.getValueAt(n, 2); // Get type of item

      if (type.equalsIgnoreCase("Folder")) {
        String item = null;
        try {
          item = this.getFileName(table.getValueAt(n, 0));
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        try {
          changeRelativePath(item);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        topGUI.updateFieView(table);
        topGUI.updateCWD(currentPWD);
      }
    }
  }

  /**
   * Returns absolute paths of all selected items
   * @return String[]
   */
  public String[] getSelectedItems() {
    if (table.getSelectedRowCount() == 0) {
      return null;
    }

    int[] rows = table.getSelectedRows();
    String[] items = new String[rows.length];
    for (int i = 0; i < rows.length; i++) {
      int n = rows[i];
      try {
        items[i] = getFileName(table.getValueAt(n, 0));
      } catch (Exception ex) {
        items[i] = null;
        System.err.println("INTERNAL ERROR: getSelectedItems: " + ex.getMessage());
      }
    }
    return items;
  }

  /**
   * Returns selected files (ignores selected directories, if any)
   * @return String[]
   */
  public String[] getSelectedFiles() {
    if (table.getSelectedRowCount() == 0) {
      return null;
    }

    int[] rows = table.getSelectedRows();
    String[] items = new String[rows.length];
    for (int i = 0; i < rows.length; i++) {
      int n = rows[i];
      String type = (String) table.getValueAt(n, 2); // Get type of item

      if (type.equalsIgnoreCase("Folder")) { // Ignore directories
        continue;
      }

      try {
        items[i] = getFileName(table.getValueAt(n, 0));
      } catch (Exception ex) {
        items[i] = null;
        System.err.println("INTERNAL ERROR: getSelectedItems: " + ex.getMessage());
      }
    }
    return items;
  }

  /**
   * Returns selected directories (ignores selected files, if any)
   * @return String[]
   */
  public String[] getSelectedFolders() {
    if (table.getSelectedRowCount() == 0) {
      return null;
    }

    int[] rows = table.getSelectedRows();
    String[] items = new String[rows.length];
    for (int i = 0; i < rows.length; i++) {
      int n = rows[i];
      String type = (String) table.getValueAt(n, 2); // Get type of item

      if (!type.equalsIgnoreCase("Folder")) { // Ignore directories
        continue;
      }

      try {
        items[i] = getFileName(table.getValueAt(n, 0));
      } catch (Exception ex) {
        items[i] = null;
        System.err.println("INTERNAL ERROR: getSelectedItems: " + ex.getMessage());
      }
    }
    return items;
  }

  private void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      if (table.getSelectedRowCount() == 0) {
        generalPopup.show(e.getComponent(), e.getX(), e.getY());
      } else {
        filePopup.show(e.getComponent(),
            e.getX(), e.getY());
      }
    }
  }

  /**
   *
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
  private class FileBrowserTableModel
      extends AbstractTableModel {
    //String[] columnNames = null;

    boolean DEBUG = true;
    //Object[][] data = null;

    public FileBrowserTableModel() {
    }

    /*
    public FileBrowserTableModel(String[] column_names, Object[][] new_data) {
    columnNames = column_names;
    data = new_data;
    }
     */
    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      /*
      if (data == null) {
      return 0;
      }
      return data.length;
       */
      if (currentFileList == null) {
        return 0;
      }
      return currentFileList.size();
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      //return data[row][col];
      SftpFile file = (SftpFile) currentFileList.get(row);
      if (col == 0) { // File name
        //return file.getFilename();
        int type = FileName.FILE;
        if (file.isDirectory()) {
          type = FileName.FOLDER;
        }
        FileName fname = null;
        try {
          fname = new FileName(file.getFilename(), type);
        } catch (Exception ex) {
        }
        return fname;
      }

      FileAttributes attr = file.getAttributes();

      if (col == 1) { // Size
        if (file.isDirectory()) {
          return new FileSize("0");
        } else {
          //UnsignedInteger64 size = attr.getSize();
          return new FileSize(attr.getSize().bigIntValue());
        }
      }

      if (col == 2) { // Type
        if (attr.isDirectory()) {
          return "Folder";
        } else {
          return "File";
        }

      }

      if (col == 3) { // Modified time
        return attr.getModTimeString();
      }

      if (col == 4) {
        return attr.getPermissionsString();
      }

      System.err.println("INTERNAL ERROR: Out of columns");
      return "";
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.

      //if (col < 2) {
      return false;
      //}
      //else {
      // return true;
      //}
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
      if (DEBUG) {
        logger.info("Setting value at " + row + "," + col
            + " to " + value
            + " (an instance of "
            + value.getClass() + ")");
      }

      data[row][col] = value;
      fireTableCellUpdated(row, col);

      if (DEBUG) {
        logger.info("New value of data:");
        printDebugData();
      }
    }

    private void printDebugData() {
      int numRows = getRowCount();
      int numCols = getColumnCount();

      for (int i = 0; i < numRows; i++) {
        System.out.print("    row " + i + ":");
        for (int j = 0; j < numCols; j++) {
          System.out.print("  " + data[i][j]);
        }
        System.out.print("\n");
      }
      logger.info("--------------------------");
    }
  }

  class FileTranferWorker
      extends cct.tools.SwingWorker implements FileTransferProgress {

    File[] filesToUpload = null;
    java.util.List filesToDownload = null;
    final int MAX = 10;
    //JLabel msgLabel = null;
    double totalBytes;
    TransferProgressDialog dialog = null;
    String pwd;
    String localDirectory;
    String currentLocalFile;
    boolean jobCancelled = false;
    Object[] options = {
      "Continue", "Cancel Transfer", "Ignore Errors"};
    ConfirmFileOverwriteDialog overdial = null;
    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
    NumberFormat nf = NumberFormat.getInstance();
    long start_time;

    FileTranferWorker(File[] files, String PWD) {
      super();
      filesToUpload = files;
      pwd = PWD;
    }

    FileTranferWorker(File file, String PWD) {
      super();
      filesToUpload = new File[1];
      filesToUpload[0] = file;
      pwd = PWD;
    }

    FileTranferWorker(java.util.List file_list, String localDir) {
      super();
      filesToDownload = file_list;
      localDirectory = localDir;
    }

    public Object construct() {

      String result = "done";
      dialog = new TransferProgressDialog(new Frame(), "File transfer progress", false);
      dialog.setAlwaysOnTop(true);
      dialog.setLocationRelativeTo(null);
      dialog.setProgress(0);
      dialog.setMaximum(MAX);
      dialog.cancelButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          cancelTranfer();
        }
      });

      /*
      Object[] comp = {
      msgLabel, progressBar};
      Object[] options = {
      "Cancel"};


      JOptionPane pane = new JOptionPane(comp,
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.INFORMATION_MESSAGE,
      null,
      options,
      options[0]);

      dialog = pane.createDialog(null, "File Transfer Progress");
       */

      // --- Download
      if (filesToDownload != null) {
        result = downloadFiles();
      } // Upload
      else {
        result = this.uploadFiles();
      }

      dialog.setVisible(false);
      return "done";
    }

    /**
     * Downloads files
     * @return String
     */
    private String downloadFiles() {
      boolean ignoreErrors = false;
      boolean yes_to_all = false;
      boolean no_to_all = false;

      for (int i = 0; i < filesToDownload.size(); i++) {
        if (jobCancelled) {
          return "cancelled";
        }

        SftpFile file = (SftpFile) filesToDownload.get(i);

        //currentLocalFile = localDirectory + File.separator +
        currentLocalFile = localDirectory + "/" + file.getFilename();
        try {
          if (file.isFile()) {
            if (DEBUG) {
              logger.info("Transfering: "
                  + file.getAbsolutePath() + " to "
                  + currentLocalFile);
            }

            if (!yes_to_all) {
              File newFile = new File(currentLocalFile);
              if (newFile.exists()) {
                if (no_to_all) {
                  continue;
                }
                int n = showConfirmOverwrite(newFile, file);
                if (n == ConfirmFileOverwriteDialog.YES) {
                } else if (n == ConfirmFileOverwriteDialog.YES_TO_ALL) {
                  yes_to_all = true;
                } else if (n == ConfirmFileOverwriteDialog.NO) {
                  continue;
                } else if (n == ConfirmFileOverwriteDialog.NO_TO_ALL) {
                  no_to_all = true;
                  continue;
                } else { // Cancel
                  this.cancelTranfer();
                  return "cancelled";
                }
              }
            }

            FileAttributes attr = sftpClient.get(file.getAbsolutePath(),
                localDirectory
                + //File.separator +
                "/"
                + file.getFilename(), this);
          } else if (file.isDirectory()) {
            if (DEBUG) {
              logger.info("Transfering: "
                  + file.getAbsolutePath() + " to "
                  + localDirectory);
            }
            boolean recurse = true;
            boolean sync = true;
            boolean commit = true;
            DirectoryOperation dirop = sftpClient.copyRemoteDirectory(
                file.getAbsolutePath(), localDirectory, recurse, sync, commit,
                this);
          }

          if (jobCancelled) {
            return "cancelled";
          }
        } catch (Exception ex) {
          if (jobCancelled) {
            return "cancelled";
          }
          if (ignoreErrors) {
            continue;
          }
          int n = JOptionPane.showOptionDialog(null,
              "Error: " + ex.getMessage(),
              "File Transfer Error",
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.ERROR_MESSAGE, null,
              options, options[1]);
          if (n == JOptionPane.YES_OPTION) {
            continue;
          } else if (n == JOptionPane.NO_OPTION) {
            cancelTranfer();
            return "cancelled";
          } else if (n == JOptionPane.CANCEL_OPTION) {
            ignoreErrors = true;
            continue;
          }
        }
      }
      return "done";
    }

    /**
     * Uploads files
     * @return String
     */
    private String uploadFiles() {
      boolean ignoreErrors = false;
      boolean yes_to_all = false;
      boolean no_to_all = false;
      for (int i = 0; i < filesToUpload.length; i++) {
        if (jobCancelled) {
          return "cancelled";
        }

        currentLocalFile = filesToUpload[i].getAbsolutePath();
        if (DEBUG) {
          logger.info("Transfering: " + currentLocalFile);
        }

        try {
          // --- Copy file
          if (filesToUpload[i].isFile()) {
            String remoteFile = pwd + "/" + filesToUpload[i].getName();
            try {
              FileAttributes fa = sftpClient.stat(remoteFile);
              if (fa.isDirectory()) {
                JOptionPane.showMessageDialog(null,
                    "Folder " + remoteFile
                    + " already exists on remote host\n"
                    + "Local file "
                    + currentLocalFile
                    + " is not uploaded",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                continue;
              } else if (fa.isFile()) {
                int n = showConfirmOverwrite(remoteFile, fa,
                    filesToUpload[i]);
                if (n == ConfirmFileOverwriteDialog.YES) {
                } else if (n == ConfirmFileOverwriteDialog.YES_TO_ALL) {
                  yes_to_all = true;
                } else if (n == ConfirmFileOverwriteDialog.NO) {
                  continue;
                } else if (n == ConfirmFileOverwriteDialog.NO_TO_ALL) {
                  no_to_all = true;
                  continue;
                } else { // Cancel
                  this.cancelTranfer();
                  return "cancelled";
                }

              }
            } catch (Exception ex) { // if an IO error occurs or the file does not exist
              // Ignore it for now
            }
            sftpClient.put(currentLocalFile, this);
          } // --- Copy directory
          else if (filesToUpload[i].isDirectory()) {
            boolean recurse = true;
            boolean sync = true;
            boolean commit = true;
            sftpClient.copyLocalDirectory(currentLocalFile, pwd, recurse,
                sync, commit, this);
          }

          if (jobCancelled) {
            return "cancelled";
          }
        } catch (Exception ex) {
          if (jobCancelled) {
            return "cancelled";
          }
          if (ignoreErrors) {
            continue;
          }
          int n = JOptionPane.showOptionDialog(null,
              "Error: " + ex.getMessage(),
              "File Transfer Error",
              JOptionPane.YES_NO_CANCEL_OPTION,
              JOptionPane.ERROR_MESSAGE, null,
              options, options[1]);
          if (n == JOptionPane.YES_OPTION) {
            continue;
          } else if (n == JOptionPane.NO_OPTION) {
            cancelTranfer();
            return "cancelled";
          } else if (n == JOptionPane.CANCEL_OPTION) {
            ignoreErrors = true;
            continue;
          }
        }
      }
      return "done";
    }

    /**
     *  Cancels file transfer
     */
    public void cancelTranfer() {
      if (DEBUG) {
        logger.info("Transfer cancelled...");
      }
      jobCancelled = true;
      dialog.setVisible(false);
      interrupt();
    }

    /**
     * Start the worker thread.
     */
    public void start() {

      super.start();
    }

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
      if (pwd != null && pwd.equals(currentPWD)) {
        refresh();
      }
    }

    public void completed() {
      dialog.setVisible(false);
    }

    public boolean isCancelled() {
      return jobCancelled;
    }

    public void progressed(long bytesSoFar) {
      if (jobCancelled) {
        return;
      }

      // Get elapsed time in milliseconds
      long elapsedTimeMillis = System.currentTimeMillis() - start_time;

      // Get elapsed time in seconds
      double elapsedTimeSec = (double) elapsedTimeMillis / 1000.0;

      double partSoFar = (double) bytesSoFar / totalBytes;

      if (elapsedTimeSec > 5.0) { // If less than 5 sec - ignore
        double timeLeft = elapsedTimeSec * (1.0 / partSoFar - 1.0);
        if (timeLeft > 86400.0) { // Number of seconds in a day
          int days = (int) (timeLeft / 86400.0);
          int hours = (int) (timeLeft - (double) days * 86400.0);
          dialog.setTime(days + " days and " + hours + " hours left");
        } else if (timeLeft > 3600.0) { // Number of seconds in hour
          int hours = (int) (timeLeft / 3600.0);
          int minutes = (int) (timeLeft - (double) hours * 3600.0);
          dialog.setTime(hours + " hours and " + minutes
              + " minutes left");
        } else if (timeLeft > 60.0) { // Number of seconds in minute
          int minutes = (int) (timeLeft / 60.0);
          int seconds = (int) (timeLeft - (double) minutes * 60.0);
          dialog.setTime(minutes + " minutes and " + seconds
              + " seconds left");
        } else {
          dialog.setTime(((int) timeLeft) + " seconds left");
        }

      }

      //if (DEBUG) {
      //   logger.info("SO far: " + bytesSoFar);
      //}

      dialog.setProgress((int) (partSoFar * (double) MAX));
    }

    public void started(long bytesTotal, String remoteFile) {
      // Get current time
      start_time = System.currentTimeMillis();
      totalBytes = (double) bytesTotal;
      String size;
      long n1024 = 1024;
      if (bytesTotal / (1024 * 1024) > 0) {
        size = String.valueOf(bytesTotal / (1024 * 1024)) + " Mb";
      } else if (bytesTotal / (1024) > 0) {
        size = String.valueOf(bytesTotal / (1024)) + " Kb";
      } else {
        size = String.valueOf(bytesTotal) + " b";
      }

      // --- Download
      if (filesToDownload != null) {
        dialog.setFrom("From: " + remoteFile);
        dialog.setTo("To: " + currentLocalFile);
      } // Upload
      else {
        dialog.setFrom("From: " + currentLocalFile);
        dialog.setTo("To: " + remoteFile);
      }

      dialog.setProgress(0);
      dialog.setTime("");

      dialog.setVisible(true);
    }

    /**
     * Dialog for download
     * @param existingFile File
     * @param fileToDownload SftpFile
     * @return int
     */
    private int showConfirmOverwrite(File existingFile,
        SftpFile fileToDownload) {
      if (overdial == null) {
        overdial = new ConfirmFileOverwriteDialog();
        overdial.setLocationRelativeTo(null);
        overdial.setAlwaysOnTop(true);
      }

      Date date = new Date(existingFile.lastModified());

      FileAttributes attr = fileToDownload.getAttributes();
      UnsignedInteger32 modtime = attr.getModifiedTime();
      UnsignedInteger64 size2 = attr.getSize();
      Date date2 = new Date(modtime.longValue());

      return overdial.showDialog(existingFile.getAbsolutePath(),
          existingFile.getName(),
          nf.format(existingFile.length())
          + " bytes",
          df.format(date),
          nf.format(size2.doubleValue())
          + " bytes",
          attr.getModTimeString());
      //df.format(date2));
    }

    private int showConfirmOverwrite(String existingFile, FileAttributes attr, File fileToUpload) {
      if (overdial == null) {
        overdial = new ConfirmFileOverwriteDialog();
        overdial.setLocationRelativeTo(null);
        overdial.setAlwaysOnTop(true);
      }

      Date date = new Date(fileToUpload.lastModified());

      UnsignedInteger32 modtime = attr.getModifiedTime();
      UnsignedInteger64 size2 = attr.getSize();
      Date date2 = new Date(modtime.longValue());

      String fileName = existingFile.lastIndexOf("/") != -1
          ? existingFile.substring(existingFile.lastIndexOf("/") + 1,
          existingFile.length())
          : existingFile;

      return overdial.showDialog(existingFile, fileName, attr.getModTimeString(), nf.format(size2.doubleValue()) + " bytes",
          df.format(date), nf.format(fileToUpload.length()) + " bytes");

      //df.format(date2));
    }
  }
}
