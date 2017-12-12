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

package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import cct.interfaces.OutputResultsInterface;
import cct.interfaces.VibrationsRendererProvider;

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
public class VibrationsControlFrame
    extends JFrame {
   private BorderLayout borderLayout1 = new BorderLayout();
   private JPanel jPanel1 = new JPanel();
   private VibrationsControlPanel vibrationsControlPanel1 = new VibrationsControlPanel();
   private JButton helpButton = new JButton();
   private JButton hideButton = new JButton();

   public VibrationsControlFrame() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      getContentPane().setLayout(borderLayout1);
      hideButton.setToolTipText("");
      hideButton.addActionListener(new ActionListener() {
         @Override
        public void actionPerformed(ActionEvent e) {
            hideButton_actionPerformed(e);
         }
      });
      helpButton.setToolTipText("");
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.getContentPane().add(vibrationsControlPanel1, BorderLayout.CENTER);
      helpButton.setText("  Help  ");
      this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
      hideButton.setText("  Hide  ");
      jPanel1.add(hideButton);
      jPanel1.add(helpButton);

      addWindowListener(new WindowAdapter() {
         @Override
        public void windowClosing(WindowEvent we) {
            vibrationsControlPanel1.cleanupMess();
            we.getWindow().setVisible(false);
         }
      });

   }

   public void setSpectraProvider(OutputResultsInterface provider) {
      vibrationsControlPanel1.setSpectraProvider(provider);
   }

   public void setVibrationsRendererProvider(VibrationsRendererProvider provider) {
      vibrationsControlPanel1.setVibrationsRendererProvider(provider);
   }

   public static void main(String[] args) {
      VibrationsControlFrame vibrationscontrolframe = new VibrationsControlFrame();
   }

   public void hideButton_actionPerformed(ActionEvent e) {
      vibrationsControlPanel1.cleanupMess();
      setVisible(false);
   }
}
