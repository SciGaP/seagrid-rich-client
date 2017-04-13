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

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
public class SubmitGaussianFrame_AboutBox extends JDialog implements ActionListener {
    String       comments      = "Computational Chemistry Toolkit";
    String       copyright     = "Copyright (c) 2006";
    JPanel       panel1        = new JPanel();
    JPanel       panel2        = new JPanel();
    JLabel       label4        = new JLabel();
    JLabel       label3        = new JLabel();
    JLabel       label2        = new JLabel();
    JLabel       label1        = new JLabel();
    JPanel       insetsPanel3  = new JPanel();
    JPanel       insetsPanel2  = new JPanel();
    JPanel       insetsPanel1  = new JPanel();
    JLabel       imageLabel    = new JLabel();
    ImageIcon    image1        = new ImageIcon();
    GridLayout   gridLayout1   = new GridLayout();
    FlowLayout   flowLayout1   = new FlowLayout();
    JButton      button1       = new JButton();
    BorderLayout borderLayout2 = new BorderLayout();
    BorderLayout borderLayout1 = new BorderLayout();
    String       product       = "Molecular Structure Viewer/Editor";
    String       version       = "1.0";
    JLabel       jLabel1       = new JLabel();

    public SubmitGaussianFrame_AboutBox() {
        this(null);
    }

    public SubmitGaussianFrame_AboutBox(Frame parent) {
        super(parent);

        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Component initialization.
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        image1 = new ImageIcon(SubmitGaussianFrame.class.getResource("about.png"));
        imageLabel.setIcon(image1);
        setTitle("About");
        panel1.setLayout(borderLayout1);
        panel2.setLayout(borderLayout2);
        insetsPanel1.setLayout(flowLayout1);
        insetsPanel2.setLayout(flowLayout1);
        insetsPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridLayout1.setRows(5);
        gridLayout1.setColumns(1);
        label1.setToolTipText("");
        label1.setText("Gaussian Job Submitter");
        label2.setToolTipText("");
        label2.setText("Version 1.0 beta");
        label3.setText("Copyright (c) ANU 2006");
        label4.setToolTipText("");
        label4.setText("Written by Dr. V. Vasilyev");
        insetsPanel3.setLayout(gridLayout1);
        insetsPanel3.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 10));
        button1.setText("OK");
        button1.addActionListener(this);
        jLabel1.setToolTipText("");
        jLabel1.setText("e-mail:vvv900@gmail.com");
        insetsPanel2.add(imageLabel, null);
        panel2.add(insetsPanel2, BorderLayout.WEST);
        getContentPane().add(panel1, null);
        insetsPanel3.add(label1, null);
        insetsPanel3.add(label2, null);
        insetsPanel3.add(label3, null);
        insetsPanel3.add(label4, null);
        insetsPanel3.add(jLabel1);
        panel2.add(insetsPanel3, BorderLayout.CENTER);
        insetsPanel1.add(button1, null);
        panel1.add(insetsPanel1, BorderLayout.SOUTH);
        panel1.add(panel2, BorderLayout.NORTH);
        setResizable(true);
    }

    /**
     * Close the dialog on a button event.
     *
     * @param actionEvent ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == button1) {
            dispose();
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
