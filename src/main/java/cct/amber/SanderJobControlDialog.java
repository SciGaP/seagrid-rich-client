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

package cct.amber;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SanderJobControlDialog
    extends Dialog implements ActionListener, ItemListener {
   TextField natoms, nsteps, cutoff, memory_req, time_req;
   AmberSubmitDialog daddy = null;
   //BorderLayout borderLayout1 = new BorderLayout();

   public SanderJobControlDialog(AmberSubmitDialog owner, String title,
                                 boolean modal) {
      super(owner, title, modal);
      daddy = owner;

      GridLayout sizer = new GridLayout(0, 1, 3, 3);
      setLayout(sizer);

      Panel P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label nat = new Label("Number of Atoms:", Label.RIGHT);
      natoms = new TextField("       0");
      natoms.setEditable(false);
      natoms.addActionListener(this);

      P.add(nat);
      P.add(natoms);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label nst = new Label("Number of Energy/Grad Evaluations:", Label.RIGHT);
      nsteps = new TextField("        ");
      nsteps.setEditable(false);
      nsteps.addActionListener(this);

      P.add(nst);
      P.add(nsteps);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label cut = new Label("Nonbonded Cutoff", Label.RIGHT);
      cutoff = new TextField("        ");
      cutoff.setEditable(false);
      cutoff.addActionListener(this);

      P.add(cut);
      P.add(cutoff);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label memory = new Label("Memory requirement (Mb):", Label.RIGHT);
      memory_req = new TextField("      ");
      memory_req.setEditable(false);
      //memory_req.addActionListener(this);

      P.add(memory);
      P.add(memory_req);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label time = new Label("Time requirement (seconds):", Label.RIGHT);
      time_req = new TextField("      ");
      time_req.setEditable(false);
      //time_req.addActionListener(this);

      P.add(time);
      P.add(time_req);
      add(P);

      setSize(350, 230);
   }

   public void setCutoff(float cut) {
      cutoff.setText(Float.toString(cut));
   }

   public void setMemoryRequirement(float m_req) {
      memory_req.setText(Float.toString(m_req));
   }

   public void setNumberOfAtoms(int nat) {
      natoms.setText(Integer.toString(nat));
   }

   public void setNumberOfSteps(int n) {
      nsteps.setText(Integer.toString(n));
   }

   public void setTimeRequirement(float t_req) {
      time_req.setText(Float.toString(t_req));
   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("OK")) {

      }
   }

   @Override
  public void itemStateChanged(ItemEvent ie) {
      repaint();
   }

}
