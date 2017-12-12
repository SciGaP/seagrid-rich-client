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

package cct.awtdialogs;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GaussianCalcSetupDialog
    extends Dialog implements ActionListener, ItemListener {
   Choice JobType, Method, Charge, Spin, BasisSet;
   Checkbox drawBond;
   boolean OK = false;
   static final Logger logger = Logger.getLogger(GaussianCalcSetupDialog.class.getCanonicalName());
   public GaussianCalcSetupDialog(Frame parent, String Title, boolean modal) {
      super(parent, Title, modal);

      //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER);
      //GridLayout sizer = new  GridLayout( 0, 2, 5 ,5 );
      GridLayout sizer = new GridLayout(0, 1, 3, 3);
      setLayout(sizer);

      Panel P = new Panel();
      P.setLayout(new FlowLayout());

      Label JobT = new Label("Job Type:", Label.RIGHT);
      JobType = new Choice();
      JobType.addItemListener(this);

      Label method = new Label("Method:", Label.RIGHT);
      Method = new Choice();
      Method.addItemListener(this);

      drawBond = new Checkbox("Draw Bond", true);
      drawBond.addItemListener(this);

      Button OK = new Button("OK");
      Button Cancel = new Button("Cancel");

      // --- Adding controls to a dialog

      P.add(JobT);
      P.add(JobType);

      P.add(method);
      P.add(Method);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

      P.add(drawBond);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout());
      P.add(OK);
      P.add(Cancel);
      add(P);

      OK.addActionListener(this);
      Cancel.addActionListener(this);

      setSize(300, 200);
   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("OK")) {
         OK = true;
         //dispose();
         setVisible(false);
      }
      else if (arg.equals("Cancel")) {
         OK = false;
         //dispose();
         setVisible(false);
      }

      else {
         logger.info("Event: " + arg);
      }
   }

   @Override
  public void itemStateChanged(ItemEvent ie) {
      repaint();
   }

   public boolean pressedOK() {
      return OK;
   }

}
