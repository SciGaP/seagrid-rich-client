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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

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
public class DoubleSpinnerDialog
    extends JDialog implements ActionListener {
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JSpinner jSpinner1 = new JSpinner();
   JPanel jPanel1 = new JPanel();
   boolean okPressed = false;
   JButton okButton = new JButton();
   JButton cancelButton = new JButton();
   public DoubleSpinnerDialog(Frame owner, String title, boolean modal,
                              double value, double minimum, double maximum,
                              double stepSize) {
      super(owner, title, modal);
      try {
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         jbInit();
         jSpinner1.setModel(new SpinnerNumberModel(value, minimum, maximum,
             stepSize));
         pack();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private DoubleSpinnerDialog() {
      this(new Frame(), "Select Value", true, 10, 0, 100, 1);
   }

   private void jbInit() throws Exception {
      panel1.setLayout(borderLayout1);
      okButton.setToolTipText("Accept Value");
      okButton.setText("OK");
      cancelButton.setToolTipText("Cancel Input");
      cancelButton.setText("Cancel");
      this.setDefaultCloseOperation(javax.swing.WindowConstants.
                                    DO_NOTHING_ON_CLOSE);
      getContentPane().add(panel1);
      panel1.add(jSpinner1, BorderLayout.NORTH);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(okButton);
      jPanel1.add(cancelButton);

      okButton.addActionListener(this);
      cancelButton.addActionListener(this);
   }

   public boolean isOKPressed() {
      return okPressed;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      if (e.getSource() == okButton) {
         okPressed = true;
         setVisible(false);
      }
      else if (e.getSource() == cancelButton) {
         okPressed = false;
         setVisible(false);
      }

   }

   public double getValue() {
      double value = 0;
      try {
         String size = jSpinner1.getModel().getValue().toString();
         value = Double.parseDouble(size);
      }
      catch (NumberFormatException nfe) {
      }
      return value;
   }

    public static void main(String [] s) {
      DoubleSpinnerDialog dial = new DoubleSpinnerDialog();
      //dial.setSize(640, 480);
      dial.pack();
      dial.setVisible(true);
      if ( dial.isOKPressed()) {
        System.out.println("Selected: "+dial.getValue());
      }
      System.exit(0);
    }
}
