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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
public class ConfirmFileOverwriteDialog
    extends JDialog implements ActionListener {

   public final static int YES = 0;
   public final static int YES_TO_ALL = 1;
   public final static int NO = 2;
   public final static int NO_TO_ALL = 3;
   public final static int CANCEL = 4;

   int buttonPressed = CANCEL;

   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JLabel topLabel = new JLabel();
   JPanel buttonsPanel = new JPanel();
   JButton cancelButton = new JButton();
   JButton notoallButton = new JButton();
   JButton noButton = new JButton();
   JButton yestoallButton = new JButton();
   JButton yesButton = new JButton();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JLabel existingSize = new JLabel();
   JLabel existingModification = new JLabel();
   JLabel jLabel4 = new JLabel();
   GridBagLayout gridBagLayout1 = new GridBagLayout();
   JPanel jPanel3 = new JPanel();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel5 = new JLabel();
   JLabel jLabel6 = new JLabel();
   GridBagLayout gridBagLayout2 = new GridBagLayout();
   GridBagLayout gridBagLayout3 = new GridBagLayout();
   JPanel jPanel4 = new JPanel();
   JLabel fileNameLabel = new JLabel();
   JLabel jLabel8 = new JLabel();
   JLabel folderLabel = new JLabel();
   FlowLayout flowLayout1 = new FlowLayout();
   JLabel candidateSize = new JLabel();
   JLabel candidateModification = new JLabel();
   public ConfirmFileOverwriteDialog(Frame owner, String title) {
      super(owner, title, true);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public ConfirmFileOverwriteDialog() {
      this(new Frame(), "Confirm File Overwrite");
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      topLabel.setToolTipText("");
      topLabel.setText("Folder ");
      cancelButton.setMinimumSize(new Dimension(73, 23));
      cancelButton.setPreferredSize(new Dimension(73, 23));
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      notoallButton.setToolTipText("");
      notoallButton.setText("No to All");
      noButton.setMinimumSize(new Dimension(73, 23));
      noButton.setPreferredSize(new Dimension(73, 23));
      noButton.setToolTipText("");
      noButton.setText("No");
      yestoallButton.setMinimumSize(new Dimension(79, 23));
      yestoallButton.setPreferredSize(new Dimension(79, 23));
      yestoallButton.setToolTipText("");
      yestoallButton.setText("Yes to All");
      yesButton.setMinimumSize(new Dimension(73, 23));
      yesButton.setPreferredSize(new Dimension(73, 23));
      yesButton.setToolTipText("");
      yesButton.setText("Yes");
      jLabel2.setToolTipText("");
      jLabel2.setText("          Modified: ");
      jPanel1.setLayout(gridBagLayout2);
      jPanel2.setLayout(gridBagLayout1);
      jLabel3.setToolTipText("");
      jLabel3.setText("          Size: ");
      jLabel4.setToolTipText("");
      jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
      jLabel4.setHorizontalTextPosition(SwingConstants.LEFT);
      jLabel4.setText("Would you like to replace existing file");
      jLabel1.setToolTipText("");
      jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
      jLabel1.setHorizontalTextPosition(SwingConstants.LEFT);
      jLabel1.setText("with this one?");
      jLabel5.setToolTipText("");
      jLabel5.setText("          Size: ");
      jLabel6.setToolTipText("");
      jLabel6.setText("          Modified: ");
      jPanel3.setLayout(gridBagLayout3);
      fileNameLabel.setToolTipText("");
      fileNameLabel.setText("FILE-NAME");
      jLabel8.setToolTipText("");
      jLabel8.setText("already contains file named ");
      folderLabel.setToolTipText("");
      folderLabel.setText("FOLDER-NAME");
      jPanel4.setLayout(flowLayout1);
      flowLayout1.setAlignment(FlowLayout.LEFT);
      candidateSize.setToolTipText("");
      candidateSize.setText("FILE-SIZE");
      candidateModification.setToolTipText("");
      candidateModification.setHorizontalAlignment(SwingConstants.LEFT);
      candidateModification.setHorizontalTextPosition(SwingConstants.LEFT);
      candidateModification.setText("MODIFICATION-DATE");
      existingSize.setToolTipText("");
      existingSize.setText("FILE-SIZE");
      existingModification.setToolTipText("");
      existingModification.setText("MODIFICATION-DATE");
      getContentPane().add(panel1);
      buttonsPanel.add(yesButton);
      buttonsPanel.add(yestoallButton);
      buttonsPanel.add(noButton);
      buttonsPanel.add(notoallButton);
      buttonsPanel.add(cancelButton);
      panel1.add(jPanel4, BorderLayout.NORTH);
      jPanel4.add(topLabel);
      jPanel4.add(folderLabel);
      jPanel4.add(jLabel8);
      jPanel4.add(fileNameLabel);
      panel1.add(jPanel1, BorderLayout.CENTER);
      panel1.add(buttonsPanel, BorderLayout.SOUTH);
      jPanel2.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(existingSize, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST,
          GridBagConstraints.NONE,
          new Insets(5, 5, 5, 5), 0, 0));

      jPanel2.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                                                  ,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel2.add(existingModification,
                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                         GridBagConstraints.NORTHWEST,
                                         GridBagConstraints.NONE,
                                         new Insets(5, 5, 5, 5), 0, 0));
      jPanel1.add(jPanel3, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                                                  ,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jPanel2, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                                                  , GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(0, 0, 0, 0), 270,
                                                  0));
      jPanel3.add(jLabel5, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel3.add(jLabel6, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0
                                                  ,
                                                  GridBagConstraints.NORTHWEST,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(5, 5, 5, 5), 0, 0));
      jPanel3.add(jLabel1, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(1, 5, 5, 5), 0, 0));
      jPanel3.add(candidateSize, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
          , GridBagConstraints.WEST,
          GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
      jPanel3.add(candidateModification,
                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                         , GridBagConstraints.WEST,
                                         GridBagConstraints.NONE,
                                         new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel4, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
                                                  , GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(5, 5, 5, 5), 0, 0));

      yesButton.addActionListener(this);
      yestoallButton.addActionListener(this);
      noButton.addActionListener(this);
      notoallButton.addActionListener(this);
      cancelButton.addActionListener(this);

   }

   public int showDialog(String folder, String fileName, String targetSize,
                         String targetMod, String originSize, String originMod) {
      folderLabel.setText(folder);
      fileNameLabel.setText(fileName);
      existingSize.setText(targetSize);
      existingModification.setText(targetMod);
      candidateSize.setText(originSize);
      candidateModification.setText(originMod);
      setVisible(true);
      return buttonPressed;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == yesButton) {
         buttonPressed = YES;
      }
      else if (e.getSource() == yestoallButton) {
         buttonPressed = YES_TO_ALL;
      }
      else if (e.getSource() == noButton) {
         buttonPressed = NO;
      }
      else if (e.getSource() == notoallButton) {
         buttonPressed = NO_TO_ALL;
      }
      else {
         buttonPressed = CANCEL;
      }
      setVisible(false);
   }

}
