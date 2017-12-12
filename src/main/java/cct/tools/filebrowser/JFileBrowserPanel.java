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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import cct.interfaces.FileBrowserGUIInterface;
import cct.interfaces.FileBrowserInterface;
import cct.tools.FileFilterImpl;

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
public class JFileBrowserPanel
    extends JPanel implements FileBrowserGUIInterface {

   FileBrowserInterface fileBrowser = null;
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

   ImageIcon up = new ImageIcon(cct.resources.Resources.class.
                                getResource("cct/images/icons16x16/up.png"));
   ImageIcon refresh = new ImageIcon(cct.resources.Resources.class.
                                     getResource("cct/images/icons16x16/refresh-2.png"));
   ImageIcon createFolder = new ImageIcon(cct.resources.Resources.class.
                                          getResource(
                                                  "cct/images/icons16x16/addEntry.png"));
   ImageIcon delete = new ImageIcon(cct.resources.Resources.class.
                                    getResource("cct/images/icons16x16/delete2.png"));
   ImageIcon help = new ImageIcon(cct.resources.Resources.class.
                                  getResource("cct/images/icons16x16/help.png"));

   FlowLayout flowLayout1 = new FlowLayout();
   JButton mkdirButton = new JButton();
   JButton deleteButton = new JButton();
   JButton helpButton = new JButton();

   public JFileBrowserPanel() {
      this(null);
   }

   public JFileBrowserPanel(FileBrowserInterface file_browser) {
      fileBrowser = file_browser;
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public FileBrowserInterface getFileBrowserInterface() {
      return fileBrowser;
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      upButton.setMaximumSize(new Dimension(23, 23));
      upButton.setMinimumSize(new Dimension(23, 23));
      upButton.setPreferredSize(new Dimension(23, 23));
      upButton.setToolTipText("Up a Directory");
      upButton.setHorizontalTextPosition(SwingConstants.CENTER);
      upButton.setIcon(up);
      upButton.addActionListener(new JFileBrowserPanel_upButton_actionAdapter(this));
      refreshButton.setMaximumSize(new Dimension(23, 23));
      refreshButton.setMinimumSize(new Dimension(23, 23));
      refreshButton.setPreferredSize(new Dimension(23, 23));
      refreshButton.setToolTipText("Refresh Folder");
      refreshButton.setHorizontalTextPosition(SwingConstants.CENTER);
      refreshButton.setIcon(refresh);
      refreshButton.addActionListener(new
                                      JFileBrowserPanel_refreshButton_actionAdapter(this));
      controlsPanel.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      mkdirButton.setMaximumSize(new Dimension(23, 23));
      mkdirButton.setMinimumSize(new Dimension(23, 23));
      mkdirButton.setPreferredSize(new Dimension(23, 23));
      mkdirButton.setToolTipText("Create New Folder");
      mkdirButton.setHorizontalTextPosition(SwingConstants.CENTER);
      mkdirButton.setIcon(createFolder);
      mkdirButton.addActionListener(new
                                    JFileBrowserPanel_mkdirButton_actionAdapter(this));
      deleteButton.setMaximumSize(new Dimension(23, 23));
      deleteButton.setMinimumSize(new Dimension(23, 23));
      deleteButton.setPreferredSize(new Dimension(23, 23));
      deleteButton.setToolTipText("Delete Selected");
      deleteButton.setHorizontalTextPosition(SwingConstants.CENTER);
      deleteButton.setIcon(delete);
      deleteButton.addActionListener(new
                                     JFileBrowserPanel_deleteButton_actionAdapter(this));
      helpButton.setMaximumSize(new Dimension(23, 23));
      helpButton.setMinimumSize(new Dimension(23, 23));
      helpButton.setPreferredSize(new Dimension(23, 23));
      helpButton.setToolTipText("Help");
      helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
      helpButton.setIcon(help);
      helpButton.addActionListener(new JFileBrowserPanel_helpButton_actionAdapter(this));
      jComboBox1.setToolTipText(
          "Select visited folders or type and press Enter for a new one");
      jComboBox1.setEditable(true);
      jComboBox1.addActionListener(new
                                   JFileBrowserPanel_jComboBox1_actionAdapter(this));
      this.add(fileScrollPane, BorderLayout.CENTER);
      controlsPanel.add(jComboBox1);
      controlsPanel.add(upButton);
      controlsPanel.add(refreshButton);
      controlsPanel.add(deleteButton);
      controlsPanel.add(mkdirButton);
      controlsPanel.add(helpButton);
      this.add(controlsPanel, BorderLayout.NORTH);
      if (fileBrowser != null) {
         Component comp = fileBrowser.getComponent();
         fileScrollPane.add(comp);
         fileScrollPane.setBackground(comp.getForeground());
      }
      else {
         fileScrollPane.getViewport().add(table);
         fileScrollPane.setBackground(table.getForeground());
      }

      fileScrollPane.setBackground(table.getForeground());
      fileScrollPane.setForeground(table.getForeground());


   }

   public void setFileFilter(FileFilterImpl filter) {
      fileBrowser.setFileFilter(filter);
   }

   public String[] getSelectedFiles() {
      return fileBrowser.getSelectedFiles();
   }

   public void setFileBrowser(FileBrowserInterface file_browser) {
      fileBrowser = file_browser;
      if (fileBrowser != null) {
         fileBrowser.setTopGUI(this);
         Component comp = fileBrowser.getComponent();
         fileScrollPane.getViewport().add(comp);
         fileScrollPane.setBackground(comp.getForeground());
      }
   }

   @Override
  public void setBusy(boolean busy) {
      if (busy) {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         upButton.setEnabled(false);
         jComboBox1.setEnabled(false);
         refreshButton.setEnabled(false);
         mkdirButton.setEnabled(false);
         deleteButton.setEnabled(false);

      }
      else {
         setCursor(Cursor.getDefaultCursor());
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
      if (fileBrowser != null) {
         fileBrowser.refresh();
      }
      refreshButton.setEnabled(true);
   }

   public void upButton_actionPerformed(ActionEvent e) {
      if (!upButton.isEnabled()) {
         return;
      }
      upButton.setEnabled(false);
      if (fileBrowser != null) {
         fileBrowser.upDirectory();
      }
      upButton.setEnabled(true);
   }

   public void mkdirButton_actionPerformed(ActionEvent e) {
      if (!mkdirButton.isEnabled()) {
         return;
      }
      mkdirButton.setEnabled(false);
      if (fileBrowser != null) {
         fileBrowser.mkdir();
      }
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
      if (fileBrowser != null) {
         fileBrowser.cd(jComboBox1.getSelectedItem().toString());
      }
      jComboBox1.setEnabled(true);
   }

   public void deleteButton_actionPerformed(ActionEvent e) {
      if (!deleteButton.isEnabled()) {
         return;
      }
      deleteButton.setEnabled(false);
      if (fileBrowser != null) {
         fileBrowser.removeSelectedPaths();
      }
      deleteButton.setEnabled(true);
   }

   public void helpButton_actionPerformed(ActionEvent e) {
      JOptionPane.showMessageDialog(this, "Toolbar:\n" +
                                    "Use toolbar buttons to go up a directory, refresh current directory\n" +
                                    "to delete selected file(s) and directories (be careful!), to create a new directory\n" +
                                    "Use combobox to switch fast to previously visited directories\n" +
                                    "Files area:\n" +
                                    "Use mouse to select single files and directories\n" +
                                    "Use Shift key + mouse to select blocks of files/directories\n" +
                                    "Using Control key + mouse gives more control for selecting/deselecting\n" +
                                    "To initiate download of selected files and directories do right mouse button click\n" +
                                    "  to open the popup menu and select folder on local computer\n" +
                                    "To upload files do right mouse button click to open the popup menu and\n" +
                                    "  select \"Upload Dialog\" (no selection should be within the file area)", "Help",
                                    JOptionPane.INFORMATION_MESSAGE);
   }

   /**
    * Sets table selection mode. Valid values: ListSelectionModel.SINGLE_SELECTION,
    * ListSelectionModel.SINGLE_INTERVAL_SELECTION, and ListSelectionModel.MULTIPLE_INTERVAL_SELECTION.
    * Otherwise sets ListSelectionModel.SINGLE_SELECTION
    * @param mode int
    */
   public void setSelectionMode(int mode) {
      try {
         switch (mode) {
            case ListSelectionModel.SINGLE_SELECTION:
            case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
            case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
               fileBrowser.setSelectionMode(mode);
               return;
         }
         System.err.println(this.getClass().getCanonicalName() + " : unknown selection mode. Ignored...");
         fileBrowser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      }
      catch (Exception ex) {}
   }

   private class JFileBrowserPanel_helpButton_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_helpButton_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.helpButton_actionPerformed(e);
      }
   }

   private class JFileBrowserPanel_deleteButton_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_deleteButton_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.deleteButton_actionPerformed(e);
      }
   }

   private class JFileBrowserPanel_jComboBox1_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_jComboBox1_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.jComboBox1_actionPerformed(e);
      }
   }

   private class JFileBrowserPanel_jComboBox1_itemAdapter
       implements ItemListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_jComboBox1_itemAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void itemStateChanged(ItemEvent e) {
         adaptee.jComboBox1_itemStateChanged(e);
      }
   }

   private class JFileBrowserPanel_mkdirButton_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_mkdirButton_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.mkdirButton_actionPerformed(e);
      }
   }

   private class JFileBrowserPanel_upButton_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_upButton_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.upButton_actionPerformed(e);
      }
   }

   private class JFileBrowserPanel_refreshButton_actionAdapter
       implements ActionListener {
      private JFileBrowserPanel adaptee;
      JFileBrowserPanel_refreshButton_actionAdapter(JFileBrowserPanel adaptee) {
         this.adaptee = adaptee;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         adaptee.refreshButton_actionPerformed(e);
      }
   }

}
