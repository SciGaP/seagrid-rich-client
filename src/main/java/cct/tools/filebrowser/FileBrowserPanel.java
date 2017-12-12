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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import cct.interfaces.FileBrowserGUIInterface;
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
public class FileBrowserPanel
    extends JPanel implements FileBrowserGUIInterface {

   FileBrowserInterface fileBrowser = null;
   boolean browserIsBusy = false;
   Object[] titles = {
       "Name", "Size", "Modified", "Attributes"};
   Object[][] data = {
       {
       "", "", "", ""}
   };
   JTable table = new JTable(data, titles);

   BorderLayout borderLayout1 = new BorderLayout();
   JScrollPane fileScrollPane = new JScrollPane();
   JPanel controlsPanel = new JPanel();
   JButton upButton = new JButton();
   JComboBox jComboBox1 = new JComboBox();
   JButton refreshButton = new JButton();
   Component currentComponent = null;

   ImageIcon up = new ImageIcon(cct.resources.Resources.class.
                                getResource("cct/images/icons16x16/up.png"));
   ImageIcon refresh = new ImageIcon(cct.resources.Resources.class.
                                     getResource(
                                             "cct/images/icons16x16/refresh-2.png"));
   ImageIcon createFolder = new ImageIcon(cct.resources.Resources.class.
                                          getResource(
                                                  "cct/images/icons16x16/addEntry.png"));
   ImageIcon delete = new ImageIcon(cct.resources.Resources.class.
                                    getResource("cct/images/icons16x16/delete2.png"));
   ImageIcon help = new ImageIcon(cct.resources.Resources.class.
                                  getResource("cct/images/icons16x16/help.png"));
   ImageIcon arrowUp = new ImageIcon(cct.resources.Resources.class.
                                     getResource(
                                             "cct/images/icons16x16/blue-arrow-up.png"));
   ImageIcon arrowDown = new ImageIcon(cct.resources.Resources.class.
                                       getResource(
                                               "cct/images/icons16x16/blue-arrow-down.png"));

   FlowLayout flowLayout1 = new FlowLayout();
   JButton mkdirButton = new JButton();
   JButton deleteButton = new JButton();
   JButton helpButton = new JButton();
   JButton downloadButton = new JButton();
   JButton uploadButton = new JButton();
   public FileBrowserPanel() {
      this(null);
   }

   public FileBrowserPanel(FileBrowserInterface file_browser) {
      fileBrowser = file_browser;
      setFileBrowser(file_browser);
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      upButton.setMaximumSize(new Dimension(23, 23));
      upButton.setMinimumSize(new Dimension(23, 23));
      upButton.setPreferredSize(new Dimension(23, 23));
      upButton.setToolTipText("Up a Directory");
      upButton.setHorizontalTextPosition(SwingConstants.CENTER);
      upButton.setIcon(up);
      upButton.addActionListener(new FileBrowserPanel_upButton_actionAdapter(this));
      refreshButton.setMaximumSize(new Dimension(23, 23));
      refreshButton.setMinimumSize(new Dimension(23, 23));
      refreshButton.setPreferredSize(new Dimension(23, 23));
      refreshButton.setToolTipText("Refresh Folder");
      refreshButton.setHorizontalTextPosition(SwingConstants.CENTER);
      refreshButton.setIcon(refresh);
      refreshButton.addActionListener(new
                                      FileBrowserPanel_refreshButton_actionAdapter(this));
      controlsPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      mkdirButton.setMaximumSize(new Dimension(23, 23));
      mkdirButton.setMinimumSize(new Dimension(23, 23));
      mkdirButton.setPreferredSize(new Dimension(23, 23));
      mkdirButton.setToolTipText("New Folder");
      mkdirButton.setHorizontalTextPosition(SwingConstants.CENTER);
      mkdirButton.setIcon(createFolder);
      mkdirButton.addActionListener(new
                                    FileBrowserPanel_mkdirButton_actionAdapter(this));
      deleteButton.setMaximumSize(new Dimension(23, 23));
      deleteButton.setMinimumSize(new Dimension(23, 23));
      deleteButton.setPreferredSize(new Dimension(23, 23));
      deleteButton.setToolTipText("Delete Selected");
      deleteButton.setHorizontalTextPosition(SwingConstants.CENTER);
      deleteButton.setIcon(delete);
      deleteButton.addActionListener(new
                                     FileBrowserPanel_deleteButton_actionAdapter(this));
      helpButton.setEnabled(false);
      helpButton.setMaximumSize(new Dimension(23, 23));
      helpButton.setMinimumSize(new Dimension(23, 23));
      helpButton.setPreferredSize(new Dimension(23, 23));
      helpButton.setToolTipText("Help");
      helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
      helpButton.setIcon(help);
      jComboBox1.setToolTipText(
          "Select visited folders or type and press Enter for a new one");
      jComboBox1.setEditable(true);
      jComboBox1.addActionListener(new
                                   FileBrowserPanel_jComboBox1_actionAdapter(this));
      downloadButton.setMaximumSize(new Dimension(23, 23));
      downloadButton.setMinimumSize(new Dimension(23, 23));
      downloadButton.setPreferredSize(new Dimension(23, 23));
      downloadButton.setToolTipText("Download Selected File(s)/Folder(s)");
      downloadButton.setHorizontalTextPosition(SwingConstants.CENTER);
      downloadButton.setIcon(arrowDown);
      downloadButton.addActionListener(new
                                       FileBrowserPanel_downloadButton_actionAdapter(this));
      uploadButton.setMaximumSize(new Dimension(23, 23));
      uploadButton.setMinimumSize(new Dimension(23, 23));
      uploadButton.setPreferredSize(new Dimension(23, 23));
      uploadButton.setToolTipText("Open Upload Dialog");
      uploadButton.setHorizontalTextPosition(SwingConstants.CENTER);
      uploadButton.setIcon(arrowUp);
      this.add(fileScrollPane, BorderLayout.CENTER);
      controlsPanel.add(jComboBox1);
      controlsPanel.add(upButton);
      controlsPanel.add(refreshButton);
      controlsPanel.add(deleteButton);
      controlsPanel.add(mkdirButton);
      controlsPanel.add(downloadButton);
      controlsPanel.add(uploadButton);
      controlsPanel.add(helpButton);
      this.add(controlsPanel, BorderLayout.NORTH);
      if (fileBrowser != null) {
         fileScrollPane.add(fileBrowser.getComponent());
      }
      else {

         fileScrollPane.getViewport().add(table);
      }
   }

   public String[] getSelectedFiles() {
      return fileBrowser.getSelectedFiles();
   }

   public String[] getSelectedFolders() {
      return fileBrowser.getSelectedFiles();
   }

   public String getPWD() {
      return fileBrowser.pwd();
   }

   public void setFileBrowser(FileBrowserInterface file_browser) {
      fileBrowser = file_browser;
      if (fileBrowser != null) {
         if (currentComponent != null) {
            fileScrollPane.getViewport().remove(currentComponent);
         }
         fileBrowser.setTopGUI(this);
         Component comp = fileBrowser.getComponent();
         fileScrollPane.getViewport().add(comp);
         fileScrollPane.setForeground(comp.getForeground());
      }
   }

   public void switchFileBrowser(FileBrowserInterface file_browser) {
      fileBrowser = file_browser;
      if (fileBrowser != null) {
         //fileBrowser.setTopGUI(this);
         if (currentComponent != null) {
            fileScrollPane.getViewport().remove(currentComponent);
         }
         fileScrollPane.getViewport().add(fileBrowser.getComponent());
      }
   }

   /**
    * Client informs whether it busy or not. If it's busy all controls are inactive
    * @param busy boolean - "true" if busy, "false" otherwise
    */
   @Override
  public void setBusy(boolean busy) {
      if (busy) {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         upButton.setEnabled(false);
         jComboBox1.setEnabled(false);
         refreshButton.setEnabled(false);
         mkdirButton.setEnabled(false);
         deleteButton.setEnabled(false);
         //helpButton = new JButton();
         downloadButton.setEnabled(false);
         uploadButton.setEnabled(false);
      }
      else {
         setCursor(Cursor.getDefaultCursor());
         upButton.setEnabled(true);
         jComboBox1.setEnabled(true);
         refreshButton.setEnabled(true);
         mkdirButton.setEnabled(true);
         deleteButton.setEnabled(true);
         //helpButton = new JButton();
         downloadButton.setEnabled(true);
         uploadButton.setEnabled(true);

      }
   }

   @Override
  public void updateCWD(String cwd) {
      jComboBox1.setEnabled(false);
      jComboBox1.addItem(cwd);
      jComboBox1.setSelectedIndex(jComboBox1.getItemCount() - 1);
      jComboBox1.setEnabled(true);
   }

   @Override
  public void updateFieView(Component fileView) {
      fileScrollPane.getViewport().add(fileView);
   }

   public void refreshButton_actionPerformed(ActionEvent e) {
      if (!refreshButton.isEnabled()) {
         return;
      }
      refreshButton.setEnabled(false);
      if ( fileBrowser != null ) fileBrowser.refresh();
      refreshButton.setEnabled(true);
   }

   public void upButton_actionPerformed(ActionEvent e) {
      if (!upButton.isEnabled()) {
         return;
      }
      upButton.setEnabled(false);
      fileBrowser.upDirectory();
      upButton.setEnabled(true);
   }

   public void mkdirButton_actionPerformed(ActionEvent e) {
      if (!mkdirButton.isEnabled()) {
         return;
      }
      mkdirButton.setEnabled(false);
      if ( fileBrowser != null ) fileBrowser.mkdir();
      mkdirButton.setEnabled(true);
   }

   public void jComboBox1_itemStateChanged(ItemEvent e) {
      if (!jComboBox1.isEnabled()) {
         return;
      }
      jComboBox1.setEnabled(false);
      fileBrowser.cd(jComboBox1.getSelectedItem().toString());
      jComboBox1.setEnabled(true);

   }

   public void jComboBox1_actionPerformed(ActionEvent e) {
      if (!jComboBox1.isEnabled()) {
         return;
      }
      jComboBox1.setEnabled(false);
      if ( fileBrowser != null ) fileBrowser.cd(jComboBox1.getSelectedItem().toString());
      jComboBox1.setEnabled(true);
   }

   public void deleteButton_actionPerformed(ActionEvent e) {
      if (!deleteButton.isEnabled()) {
         return;
      }
      deleteButton.setEnabled(false);
      if ( fileBrowser != null ) fileBrowser.removeSelectedPaths();
      deleteButton.setEnabled(true);
   }

   public void downloadButton_actionPerformed(ActionEvent e) {
      if (!downloadButton.isEnabled()) {
         return;
      }
      downloadButton.setEnabled(false);
      if ( fileBrowser != null ) fileBrowser.downloadSelected();
      downloadButton.setEnabled(true);
   }
}

class FileBrowserPanel_downloadButton_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_downloadButton_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.downloadButton_actionPerformed(e);
   }
}

class FileBrowserPanel_deleteButton_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_deleteButton_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.deleteButton_actionPerformed(e);
   }
}

class FileBrowserPanel_jComboBox1_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_jComboBox1_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.jComboBox1_actionPerformed(e);
   }
}

class FileBrowserPanel_jComboBox1_itemAdapter
    implements ItemListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_jComboBox1_itemAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void itemStateChanged(ItemEvent e) {
      adaptee.jComboBox1_itemStateChanged(e);
   }
}

class FileBrowserPanel_mkdirButton_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_mkdirButton_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.mkdirButton_actionPerformed(e);
   }
}

class FileBrowserPanel_upButton_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_upButton_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.upButton_actionPerformed(e);
   }
}

class FileBrowserPanel_refreshButton_actionAdapter
    implements ActionListener {
   private FileBrowserPanel adaptee;
   FileBrowserPanel_refreshButton_actionAdapter(FileBrowserPanel adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.refreshButton_actionPerformed(e);
   }
}
