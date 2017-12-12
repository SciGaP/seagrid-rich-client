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

package cct.grid.ui;

import java.awt.BorderLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import cct.grid.CheckPoint;
import cct.grid.FileViewerInterface;
import cct.grid.JobStatusInterface;

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
public class JobStatusPanel
    extends JPanel implements MouseMotionListener, ActionListener,
    MouseListener {
   BorderLayout borderLayout1 = new BorderLayout();

   JScrollPane jScrollPane1 = new JScrollPane();

   JobStatusTableModel dataSource = null;
   JobStatusInterface jsi = null;
   javax.swing.Timer popupTimer = null;
   PopupFactory factory = null;
   Popup popup;
   Point mousePoint;
   FileViewerInterface fileViewer = null;

   public static String downloadPrefix = "Download ";

   JPopupMenu popupMenu = new JPopupMenu("File download");
   //JMenu downloadMenu = new JMenu("Download");
   PopupListener popupListener = new PopupListener();
   CheckPoint currentCheckPoint = null;

   JobStatusPopupPanel popupPanel = new JobStatusPopupPanel();

   public JTable jobStatusTable = null;

   boolean DEBUG = false;

   final static String helpText =
       "Use cntrl or shfit keys and mouse to select/unselect the rows";
   JLabel jLabel1 = new JLabel();

   public JobStatusPanel() {
      jobStatusTable = new JTable();

      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public JobStatusPanel(JobStatusTableModel data) {
      dataSource = data;
      jsi = dataSource.getJobStatusInterface();
      jobStatusTable = new JTable(data);

      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void updateTable(JobStatusTableModel data) {
      dataSource = data;
      jsi = dataSource.getJobStatusInterface();
      jobStatusTable.setModel(data);
   }

   private void jbInit() throws Exception {
      this.setLayout(borderLayout1);
      jobStatusTable.addMouseMotionListener(this);
      jobStatusTable.addMouseListener(this);
      jLabel1.setText(" ");
      jScrollPane1.getViewport().add(jobStatusTable);
      this.add(jScrollPane1, BorderLayout.NORTH);
      this.add(jLabel1, BorderLayout.CENTER);
      //generalPopup.add(this.downloadMenu);
   }

   public String getHelp() {
      return helpText;
   }

   /**
    * Invoked when a mouse button is pressed on a component and then dragged.
    * @param e MouseEvent
    */
   @Override
  public void mouseDragged(MouseEvent e) {
      doWork(e);
   }

   /**
    * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
    * @param e MouseEvent
    */
   @Override
  public void mouseMoved(MouseEvent e) {
      doWork(e);
   }

   private void doWork(MouseEvent e) {

      if (popupMenu.isShowing()) {
         return;
      }

      if (popupMenu.isVisible()) {
         return;
      }

      if (popupTimer != null && popupTimer.isRunning()) {
         popupTimer.stop();
         if (popup != null) {
            popup.hide();
            popup = null;
         }
         //logger.info("Stop old timer");
      }
      else if (popupTimer != null) {
         mousePoint = e.getPoint();
         popupTimer.restart();
         //logger.info("Restart timer");
         return;
      }

      mousePoint = e.getPoint();
      popupTimer = new javax.swing.Timer(1000, this);
      popupTimer.setRepeats(false);
      popupTimer.start();
      //logger.info("Start new timer");

      //logger.info("x="+e.getX()+" y="+e.getY());
      //daddy.findClosestAtom(e.getX(),e.getY());

   }

   @Override
  public void actionPerformed(ActionEvent e) {

      if (dataSource == null) {
         return;
      }

      if (popup != null) {
         popup.hide();
         popup = null;
      }
      else {
         if (factory == null) {
            factory = PopupFactory.getSharedInstance();
            //factory = new PopupFactory();
         }
         Point point;
         try {
            point = this.getLocationOnScreen();
         }
         catch (IllegalComponentStateException ex) {
            return;
         }
         int row = jobStatusTable.rowAtPoint(mousePoint);
         CheckPoint chk = jsi.getCheckPoint(row);
         popupPanel.jobNameLabel.setText(chk.getJobName());
         popupPanel.taskProviderLabel.setText(chk.getTaskProvider());
         popupPanel.hostLabel.setText(chk.getComputer());
         popupPanel.softLabel.setText(chk.getProgram());
         popupPanel.submitTimeLabel.setText(chk.getJobSubmitTime());
         popupPanel.localDirLabel.setText(chk.getLocalDirectory());
         popupPanel.remoteDirLabel.setText(chk.getRemoteDirectory());
         popupPanel.executableLabel.setText(chk.getExecutable());
         popupPanel.setupOutputFiles(chk.getOutputFilesEntry());
         popupPanel.setupResourcesUsed(chk.getResourcesUsed());
         popup = factory.getPopup(this, popupPanel,
                                  point.x + mousePoint.x + 15,
                                  point.y + mousePoint.y + 30);
         popup.show();

      }

   }

   /**
    * Invoked when the mouse button has been clicked (pressed and released) on a component.
    * @param e MouseEvent
    */
   @Override
  public void mouseClicked(MouseEvent e) {

   }

   /**
    * Invoked when the mouse enters a component.
    * @param e MouseEvent
    */
   @Override
  public void mouseEntered(MouseEvent e) {

   }

   /**
    * Invoked when the mouse exits a component.
    * @param e MouseEvent
    */
   @Override
  public void mouseExited(MouseEvent e) {
      if (popup != null) {
         popup.hide();
         popup = null;
      }
   }

   /**
    * Invoked when a mouse button has been pressed on a component.
    * @param e MouseEvent
    */
   @Override
  public void mousePressed(MouseEvent e) {
      if (popup != null) {
         popup.hide();
         popup = null;
      }

      maybeShowPopup(e);
   }

   @Override
  public void mouseReleased(MouseEvent e) {
      if (popup != null) {
         popup.hide();
         popup = null;
      }

      maybeShowPopup(e);
   }

   public void jobStatusTable_mouseExited(MouseEvent e) {
      if (popup != null) {
         popup.hide();
         popup = null;
      }
   }

   private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {

         int row = jobStatusTable.rowAtPoint(e.getPoint());
         CheckPoint chk = jsi.getCheckPoint(row);
         Map output = chk.getOutputFilesEntry();
         if (output == null || output.size() < 1) {
            return;
         }

         //JMenu submenu = new JMenu("Download files");
         //downloadMenu.add(popupPanel);

         this.currentCheckPoint = chk;
         //downloadMenu.removeAll();
         popupMenu.removeAll();
         Set set = output.entrySet();
         Iterator iter = set.iterator();
         while (iter.hasNext()) {
            Map.Entry me = (Map.Entry) iter.next();
            String tag = me.getKey().toString();
            if (tag.startsWith("local")) {
               continue;
            }
            Object obj = me.getValue();
            String fileName = obj.toString();

            JMenuItem menuItem = new JMenuItem(downloadPrefix + fileName);
            menuItem.addActionListener(this.popupListener);
            //submenu.add(menuItem);
            //downloadMenu.add(menuItem);
            popupMenu.add(menuItem);
         }
         //downloadMenu.add(submenu);

         if (popup != null) {
            popup.hide();
            popup = null;
         }

         popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
   }

   public void setFileViewer(FileViewerInterface fileviewer) {
      fileViewer = fileviewer;
   }

   private class PopupListener
       implements ActionListener {
      PopupListener() {}

      @Override
      public void actionPerformed(ActionEvent e) {
         String arg = e.getActionCommand();
         arg = arg.substring(downloadPrefix.length());
         fileViewer.setFileInfo(currentCheckPoint, arg);
      }

   }

}
