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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import cct.interfaces.FileBrowserInterface;
import cct.tools.FileFilterImpl;

import com.sshtools.j2ssh.FileTransferProgress;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SFTPFileChooserDialog
    extends JDialog implements ActionListener, ShadowClientInterface, SelectionInfoInterface {
   final int MAX = 10;
   boolean jobCancelled = false;
   long start_time;
   double totalBytes;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JFileBrowserPanel fileBrowserPanel1 = new JFileBrowserPanel();
   JPanel jPanel1 = new JPanel();
   JButton cancelButton = new JButton();
   JButton loadButton = new JButton();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel2 = new JLabel();
   JTextField fileTextField = new JTextField();
   JComboBox filterComboBox = new JComboBox();
   GridBagLayout gridBagLayout1 = new GridBagLayout();

   ImageIcon help = new ImageIcon(cct.resources.Resources.class.
                                  getResource("cct/images/icons16x16/help.png"));

   FileFilterImpl[] fileFilters = null;
   boolean loadPressed = false;
   File temporaryLocalFile = null;
   FileBrowserInterface browser;

   JButton loadHelpButton = new JButton();
   JButton fileTypesHelpButton = new JButton();

   public SFTPFileChooserDialog(Frame owner, String title, boolean modal) {
      super(owner, title, modal);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
  public void setSelectedFileName(String file_name) {
      fileTextField.setText(file_name);
   }

   public SFTPFileChooserDialog() {
      this(new Frame(), "SFTP File Chooser Dialog", false);
   }

   @Override
  public Component getComponent() {
      return this;
   }

   @Override
  public void setKnownHosts(String[] hosts) {

   }

   @Override
  public void setFileBrowser(String host, String username, FileBrowserInterface file_browser) {
      browser = file_browser;
      fileBrowserPanel1.setFileBrowser(browser);
      if (browser != null) {
         browser.setSelectionInfoInterface(this);
         Component comp = browser.getComponent();
         fileBrowserPanel1.setBackground(comp.getForeground());
         panel1.setBackground(comp.getForeground());
      }

      fileBrowserPanel1.repaint();
      pack();

   }

   public void setFileBrowser(FileBrowserInterface fileBrowser) {
      browser = fileBrowser;
      fileBrowserPanel1.setFileBrowser(fileBrowser);
      if (browser != null) {
         browser.setSelectionInfoInterface(this);
         Component comp = browser.getComponent();
         fileBrowserPanel1.setBackground(comp.getForeground());
         panel1.setBackground(comp.getForeground());
      }
      fileBrowserPanel1.repaint();
      pack();
   }

   public void setFileFilters(FileFilterImpl[] filters) {
      fileFilters = filters;
      filterComboBox.removeAllItems();
      filterComboBox.setEnabled(false);
      //try {
      for (int i = 0; i < fileFilters.length; i++) {
         filterComboBox.addItem(fileFilters[i].getDescription());
      }
      //} catch ( Exception ex ) {
      //   ex.printStackTrace();
      //}
      filterComboBox.setEnabled(true);
      if (filterComboBox.getItemCount() > 0) {
         filterComboBox.setSelectedIndex(0);
         if (browser != null) {
            browser.setFileFilter(fileFilters[filterComboBox.getSelectedIndex()]);
         }
      }
   }

   public int getSeletedFilterIndex() {
      return filterComboBox.getSelectedIndex();
   }

   public String getSeletedFilterDescription() {
      return filterComboBox.getSelectedItem().toString();
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new SFTPFileChooserDialog_cancelButton_actionAdapter(this));
      loadButton.setToolTipText("");
      loadButton.setText("Load");
      loadButton.addActionListener(new SFTPFileChooserDialog_loadButton_actionAdapter(this));
      jLabel1.setToolTipText("");
      jLabel1.setText("Files of type:");
      jLabel2.setToolTipText("");
      jLabel2.setText("File name:");
      fileTextField.setToolTipText("Type file name or file template and press Enter");
      fileTextField.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            fileTextField_actionPerformed(e);
         }
      });
      jPanel1.setLayout(gridBagLayout1);
      filterComboBox.addItemListener(new ItemListener() {
         @Override
        public void itemStateChanged(ItemEvent e) {
            filterComboBox_itemStateChanged(e);
         }
      });
      loadHelpButton.setMaximumSize(new Dimension(23, 23));
      loadHelpButton.setMinimumSize(new Dimension(23, 23));
      loadHelpButton.setPreferredSize(new Dimension(23, 23));
      loadHelpButton.setToolTipText("Help on loading files");
      loadHelpButton.setHorizontalTextPosition(SwingConstants.CENTER);
      loadHelpButton.setIcon(help);
      loadHelpButton.setText("");
      loadHelpButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            loadHelpButton_actionPerformed(e);
         }
      });

      fileTypesHelpButton.setMaximumSize(new Dimension(23, 23));
      fileTypesHelpButton.setMinimumSize(new Dimension(23, 23));
      fileTypesHelpButton.setPreferredSize(new Dimension(23, 23));
      fileTypesHelpButton.setToolTipText("Help on file types");
      fileTypesHelpButton.setHorizontalTextPosition(SwingConstants.CENTER);
      fileTypesHelpButton.setIcon(help);
      fileTypesHelpButton.setText("");
      fileTypesHelpButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            fileTypesHelpButton_actionPerformed(e);
         }
      });
      filterComboBox.setToolTipText("Select File format");

      getContentPane().add(panel1);
      panel1.add(fileBrowserPanel1);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(filterComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(fileTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
          , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(loadHelpButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(loadButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(cancelButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(fileTypesHelpButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            loadPressed = false;
            setVisible(false);
         }
      });

   }

   @Override
  public void actionPerformed(ActionEvent e) {

   }

   public boolean isLoadPressed() {
      return loadPressed;
   }

   public void cancelButton_actionPerformed(ActionEvent e) {
      setVisible(false);
      loadPressed = false;
   }

   /**
    * Sets table selection mode. Valid values: ListSelectionModel.SINGLE_SELECTION,
    * ListSelectionModel.SINGLE_INTERVAL_SELECTION, and ListSelectionModel.MULTIPLE_INTERVAL_SELECTION.
    * Otherwise sets ListSelectionModel.SINGLE_SELECTION
    * @param mode int
    */
   public void setSelectionMode(int mode) {
      switch (mode) {
         case ListSelectionModel.SINGLE_SELECTION:
         case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
         case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
            fileBrowserPanel1.setSelectionMode(mode);
            return;
      }
      System.err.println(this.getClass().getCanonicalName() + " : unknown selection mode. Ignored...");
      fileBrowserPanel1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
   }

   public void loadButton_actionPerformed(ActionEvent e) {
      loadPressed = true;
      String fileName = fileTextField.getText().trim();
      if (fileName.length() < 1) {
         JOptionPane.showMessageDialog(this, "Select file in the files panel or type its name in the textfield", "Warning",
                                       JOptionPane.WARNING_MESSAGE);
         return;
      }

      //=================================
      /*
             String[] file = fileBrowserPanel1.getSelectedFiles();
             if (file == null || file.length < 1) {
         JOptionPane.showMessageDialog(this, "Select file first for download", "Warning",
                                       JOptionPane.WARNING_MESSAGE);
         return;
             }
             else if (file == null || file.length > 1) {
         JOptionPane.showMessageDialog(this, "INTERNAL ERROR: Only one file can be selected. Got " + file.length, "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return;
             }
       */
      //================================
      /*
             browser = fileBrowserPanel1.getFileBrowserInterface();

             // ===============================
             try {
         // Create temp file.
         temporaryLocalFile = File.createTempFile("cct", null);

         // Delete temp file when program exits.
         temporaryLocalFile.deleteOnExit();

             }
             catch (Exception ex) {
         JOptionPane.showMessageDialog(this, "Cannot create temporary file: " + ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         temporaryLocalFile = null;
         return;
             }

             // --- Transferring file

             try {
         jobCancelled = false;
         FileTranferWorker downloader = new FileTranferWorker(this, fileName, temporaryLocalFile.getAbsolutePath());
         downloader.start();
         //browser.get(file[0], temporaryLocalFile.getAbsolutePath(), downloader);
             }
             catch (Exception ex) {
         JOptionPane.showMessageDialog(this,
                                       "Cannot transfer " + fileName + " to " + temporaryLocalFile.getAbsolutePath() + " : " +
                                       ex.getMessage(),
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
         temporaryLocalFile = null;
         return;
             }
       */
      try {
         transferFile(fileName);
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
         temporaryLocalFile = null;
         loadPressed = false;
      }

   }

   public File getLocalFile() {
      return temporaryLocalFile;
   }

   public void transferFile(String remoteFile, String localFile, FileTransferProgress progress) throws Exception {
      browser.get(remoteFile, localFile, progress);
   }

   public void filterComboBox_itemStateChanged(ItemEvent e) {
      if (!filterComboBox.isEnabled()) {
         return;
      }
      if (e.getStateChange() == ItemEvent.DESELECTED) {
         return;
      }
      browser.setFileFilter(fileFilters[filterComboBox.getSelectedIndex()]);
   }

   String makeValidPattern(String initialPattern) {
      String finalPattern = "";
      boolean charsStarted = false;
      for (int i = 0; i < initialPattern.length(); i++) {
         if (initialPattern.substring(i, i + 1).equals("*")) {
            if (charsStarted) {
               charsStarted = false;
               finalPattern += "]";
            }

            finalPattern += ".*";
         }
         else if (initialPattern.substring(i, i + 1).equals("?")) {
            if (charsStarted) {
               charsStarted = false;
               finalPattern += "]";
            }

            finalPattern += ".";
         }
         else if (initialPattern.substring(i, i + 1).equals(".")) {
            if (charsStarted) {
               charsStarted = false;
               finalPattern += "]";
            }

            finalPattern += "[.]";
         }

         else {
            if (!charsStarted) {
               charsStarted = true;
               finalPattern += "[";
            }
            finalPattern += initialPattern.substring(i, i + 1);
         }
      }
      if (charsStarted) {
         charsStarted = false;
         finalPattern += "]";
      }

      return finalPattern;
   }

   public void fileTextField_actionPerformed(ActionEvent e) {
      String fileName = fileTextField.getText().trim();
      if (browser == null) {
         System.err.println(this.getClass().getCanonicalName() + " : SFTP Browser is not set");
         return;
      }
      try {
         //String pattern = makeValidPattern(fileName);
         String pattern = fileName.replaceAll("[.]", "[.]");
         pattern = pattern.replaceAll("[*]", ".*");
         pattern = pattern.replaceAll("[?]", ".");
         browser.listUsingFilePattern(pattern);
         int nfiles = browser.getFileCount();
         if (nfiles == 1) {
            String[] fileNames = browser.getFileNames();
            loadPressed = true;
            transferFile(fileNames[0]);
         }
      }
      catch (Exception ex) {
         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         loadPressed = false;
         return;
      }
   }

   void transferFile(String fileName) throws Exception {

      // --- Creates temporary file
      try {
         // Create temp file.
         temporaryLocalFile = File.createTempFile("cct", null);

         // Delete temp file when program exits.
         temporaryLocalFile.deleteOnExit();

      }
      catch (Exception ex) {
         temporaryLocalFile = null;
         throw new Exception("Cannot create temporary file: " + ex.getMessage());
      }

      // --- Transferring file

      try {
         jobCancelled = false;
         FileTranferWorker downloader = new FileTranferWorker(this, fileName, temporaryLocalFile.getAbsolutePath());
         downloader.start();
         //browser.get(file[0], temporaryLocalFile.getAbsolutePath(), downloader);
      }
      catch (Exception ex) {
         temporaryLocalFile = null;
         throw new Exception("Cannot transfer " + fileName + " to " + temporaryLocalFile.getAbsolutePath() + " : " +
                             ex.getMessage());
      }

   }

   public void loadHelpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this, "Left-mouse-click on the file or type file name explicitly in the text field\n" +
                                    "  and press \"Load\" button\n" +
                                    "Use file template, for example, *.com for the non-standard file extension", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);
   }

   public void fileTypesHelpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this, "Use combobox to select file format and file template", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);

   }

   private class SFTPFileChooserDialog_cancelButton_actionAdapter
       implements ActionListener {
      private SFTPFileChooserDialog adaptee;
      SFTPFileChooserDialog_cancelButton_actionAdapter(SFTPFileChooserDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.cancelButton_actionPerformed(e);
      }
   }

   private class SFTPFileChooserDialog_loadButton_actionAdapter
       implements ActionListener {
      private SFTPFileChooserDialog adaptee;
      SFTPFileChooserDialog_loadButton_actionAdapter(SFTPFileChooserDialog adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.loadButton_actionPerformed(e);
      }
   }

   /**
    *
    * <p>Title: Computational Chemistry Tookit</p>
    *
    * <p>Description: </p>
    *
    * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
    *
    * <p>Company: ANU</p>
    *
    * @author Dr. Vladislav Vassiliev
    * @version 1.0
    */
   class FileTranferWorker
       extends cct.tools.SwingWorker implements FileTransferProgress {

      TransferProgressDialog transferProgressDialog;
      private SFTPFileChooserDialog adaptee;
      String remoteFile, localFile;

      public FileTranferWorker(SFTPFileChooserDialog adaptee, String remote_file, String local_file) {
         this.adaptee = adaptee;
         remoteFile = remote_file;
         localFile = local_file;

         JFrame frame = null;
         if (getOwner() instanceof JFrame) {
            frame = (JFrame) getOwner();
         }

         transferProgressDialog = new TransferProgressDialog(frame, "File transfer progress", false);
         transferProgressDialog.setLocationRelativeTo(adaptee);
         transferProgressDialog.setAlwaysOnTop(true);
         transferProgressDialog.setProgress(0);
         transferProgressDialog.setMaximum(MAX);
         transferProgressDialog.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               cancelTranfer();
            }
         }
         );

      }

      public void cancelTranfer() {
         jobCancelled = true;
         loadPressed = false;
         adaptee.setVisible(false);
         interrupt();
      }

      /*
             public void actionPerformed(ActionEvent e) {
        jobCancelled = true;
         //adaptee.loadButton_actionPerformed(e);
             }
       */

      @Override
      public Object construct() {

         try {
            adaptee.transferFile(remoteFile, localFile, this);
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
            temporaryLocalFile = null;

         }
         adaptee.setVisible(false);

         return null;
      }

      @Override
      public void start() {

         super.start();
      }

      @Override
      public void completed() {
         transferProgressDialog.setVisible(false);
      }

      @Override
      public boolean isCancelled() {
         return jobCancelled;
      }

      @Override
      public void progressed(long bytesSoFar) {
         if (jobCancelled) {
            return;
         }

         // Get elapsed time in milliseconds
         long elapsedTimeMillis = System.currentTimeMillis() - start_time;

         // Get elapsed time in seconds
         double elapsedTimeSec = elapsedTimeMillis / 1000.0;

         double partSoFar = bytesSoFar / totalBytes;

         if (elapsedTimeSec > 5.0) { // If less than 5 sec - ignore
            double timeLeft = elapsedTimeSec * (1.0 / partSoFar - 1.0);
            if (timeLeft > 86400.0) { // Number of seconds in a day
               int days = (int) (timeLeft / 86400.0);
               int hours = (int) (timeLeft - days * 86400.0);
               transferProgressDialog.setTime(days + " days and " + hours + " hours left");
            }
            else if (timeLeft > 3600.0) { // Number of seconds in hour
               int hours = (int) (timeLeft / 3600.0);
               int minutes = (int) (timeLeft - hours * 3600.0);
               transferProgressDialog.setTime(hours + " hours and " + minutes +
                                              " minutes left");
            }
            else if (timeLeft > 60.0) { // Number of seconds in minute
               int minutes = (int) (timeLeft / 60.0);
               int seconds = (int) (timeLeft - minutes * 60.0);
               transferProgressDialog.setTime(minutes + " minutes and " + seconds +
                                              " seconds left");
            }
            else {
               transferProgressDialog.setTime( ( (int) timeLeft) + " seconds left");
            }

         }

         //if (DEBUG) {
         //   logger.info("SO far: " + bytesSoFar);
         //}

         transferProgressDialog.setProgress( (int) (partSoFar * MAX));
         //transferProgressDialog.pack();
      }

      @Override
      public void started(long bytesTotal, String remoteFile) {
         // Get current time
         start_time = System.currentTimeMillis();
         totalBytes = bytesTotal;
         String size;
         long n1024 = 1024;
         if (bytesTotal / (1024 * 1024) > 0) {
            size = String.valueOf(bytesTotal / (1024 * 1024)) + " Mb";
         }
         else if (bytesTotal / (1024) > 0) {
            size = String.valueOf(bytesTotal / (1024)) + " Kb";
         }
         else {
            size = String.valueOf(bytesTotal) + " b";
         }

         // --- Download
         //if (filesToDownload != null) {
         transferProgressDialog.setFrom("From: " + remoteFile);
         transferProgressDialog.setTo("To: " + temporaryLocalFile.getAbsolutePath());
         /*
                }

                // Upload
                else {
            dialog.setFrom("From: " + currentLocalFile);
            dialog.setTo("To: " + remoteFile);
                }
          */

         transferProgressDialog.setProgress(0);
         transferProgressDialog.setTime("");
         transferProgressDialog.validate();
         transferProgressDialog.pack();
         transferProgressDialog.setVisible(true);
      }

   }
}
