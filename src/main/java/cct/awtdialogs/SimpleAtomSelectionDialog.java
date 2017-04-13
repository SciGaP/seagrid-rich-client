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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import cct.j3d.Java3dUniverse;

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
public class SimpleAtomSelectionDialog
    extends Dialog implements ActionListener, ItemListener {
   //SampleFrame daddy = null;
   Java3dUniverse daddy = null;
   Label message;
   Button OK, Cancel;
   boolean OKpressed = false;
   static final Logger logger = Logger.getLogger(SimpleAtomSelectionDialog.class.getCanonicalName());

   public SimpleAtomSelectionDialog(Java3dUniverse parent, String Title, boolean modal) {
      super(new Frame(), Title, modal);
      daddy = parent;

      //FlowLayout sizer = new  FlowLayout( FlowLayout.CENTER, 5, 5 );
      //GridLayout sizer = new  GridLayout( 0, 3, 5, 5 );
      //setLayout( sizer );

      message = new Label("Your message is here ", Label.CENTER);
      //add(message, FlowLayout.CENTER );
      add(message, BorderLayout.CENTER);

      Panel p = new Panel();
      p.setLayout(new FlowLayout());
      OK = new Button("OK");
      p.add(OK);
      OK.addActionListener(this);

      Cancel = new Button("Cancel");
      p.add(Cancel);
      Cancel.addActionListener(this);

      add(p, BorderLayout.SOUTH);

      daddy.enableMousePicking(true);

      validate();

      Dimension dim;

      dim = message.getMinimumSize();
      logger.info("dim min: " + dim);
      dim = message.getPreferredSize();
      logger.info("dim pref: " + dim);
      Rectangle r = message.getBounds();
      logger.info("bounds: " + r);
      setSize(400, 100);
      //repaint();

   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("OK")) {
         OKpressed = true;
         daddy.enableMousePicking(false);
         daddy.processSelectedAtoms();
         //dispose();
      }
      else if (arg.equals("Cancel")) {
         OKpressed = false;
         daddy.selectAllAtoms(false);
         daddy.enableMousePicking(false);
         daddy.processSelectedAtoms();
         //dispose();
      }
   }

   public boolean pressedOK() {
      return OKpressed;
   } // May be used in modal dialog

   @Override
  public void itemStateChanged(ItemEvent ie) {
      repaint();
   }

   public void setMessage(String mess) {
      message.setText(mess);
   }

   public void setOKVisible(boolean enable) {
      OK.setVisible(enable);
   }

}
