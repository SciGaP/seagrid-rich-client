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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

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
public class TransferProgressDialog
    extends JDialog {
   JPanel panel1 = new JPanel();
   public JLabel fromLabel = new JLabel();
   public JProgressBar ProgressBar = new JProgressBar();
   JPanel jPanel1 = new JPanel();
   public JButton cancelButton = new JButton();
   JPanel jPanel2 = new JPanel();
   GridLayout gridLayout1 = new GridLayout();
   BorderLayout borderLayout1 = new BorderLayout();
   JLabel timeLeftLabel = new JLabel();
   JLabel toLabel = new JLabel();
   public TransferProgressDialog(Frame owner, String title, boolean modal) {
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

   public TransferProgressDialog() {
      this(new Frame(), "TransferProgressDialog", false);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(gridLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      fromLabel.setToolTipText("");
      fromLabel.setText(
          "From                                                             " +
          "                   ");
      cancelButton.setText("Cancel");
      gridLayout1.setColumns(1);
      gridLayout1.setRows(5);
      jPanel1.setLayout(borderLayout1);
      borderLayout1.setHgap(5);
      ProgressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
      ProgressBar.setToolTipText("File Transfer Progress");
      ProgressBar.setStringPainted(true);
      panel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
          createBevelBorder(BevelBorder.RAISED, Color.white, Color.white,
                            new Color(115, 114, 105), new Color(165, 163, 151)),
          BorderFactory.createEmptyBorder(0, 10, 0, 10)));
      panel1.setPreferredSize(new Dimension(200, 169));
      timeLeftLabel.setToolTipText("");
      timeLeftLabel.setText("XX Seconds Left");
      toLabel.setToolTipText("");
      toLabel.setText("To:");
      getContentPane().add(panel1);
      jPanel2.add(cancelButton);
      panel1.add(fromLabel, null);
      panel1.add(toLabel);
      panel1.add(jPanel1, null);
      jPanel1.add(ProgressBar, BorderLayout.NORTH);
      panel1.add(timeLeftLabel);
      panel1.add(jPanel2, null);
   }

   public void setProgress(int value) {
      ProgressBar.setValue(value);
   }

   public void setMaximum(int max) {
      ProgressBar.setMaximum(max);
   }

   public void setFrom(String from) {
      fromLabel.setText(from);
   }

   public void setTo(String to) {
      toLabel.setText(to);
   }

   public void setTime(String time) {
      timeLeftLabel.setText(time);
   }

}
