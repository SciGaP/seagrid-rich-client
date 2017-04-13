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



package cct.grid.ui.gaussian;

//~--- non-JDK imports --------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import cct.globus.ui.GatekeeperPanel;
import cct.grid.ui.PBS_Panel;

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
public class SubmitGaussPanel extends JPanel {
    BorderLayout        borderLayout1        = new BorderLayout();
    GatekeeperPanel     gatekeeperPanel1     = new GatekeeperPanel();
    PBS_Panel           pBS_Panel1           = new PBS_Panel();
    SubmitGaussianPanel submitGaussianPanel1 = new SubmitGaussianPanel();

    public SubmitGaussPanel() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        gatekeeperPanel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
                "Gatekeeper Options"));
        pBS_Panel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray, 1), "PBS Options"));
        submitGaussianPanel1.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
                "Job Specifications"));
        this.add(gatekeeperPanel1, BorderLayout.NORTH);
        this.add(submitGaussianPanel1, BorderLayout.SOUTH);
        this.add(pBS_Panel1, BorderLayout.CENTER);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
