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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
public class SubmitGaussianFrame extends JFrame {
    BorderLayout     borderLayout1     = new BorderLayout();
    JMenuBar         jMenuBar1         = new JMenuBar();
    JMenu            jMenuFile         = new JMenu();
    JMenuItem        jMenuFileExit     = new JMenuItem();
    JMenu            jMenuHelp         = new JMenu();
    JMenuItem        jMenuHelpAbout    = new JMenuItem();
    JToolBar         jToolBar          = new JToolBar();
    JButton          jButton3          = new JButton();
    JButton          jButton2          = new JButton();
    JButton          jButton1          = new JButton();
    ImageIcon        image3            =
        new ImageIcon(SubmitGaussianFrame.class.getResource("help.png"));
    ImageIcon        image2            =
        new ImageIcon(SubmitGaussianFrame.class.getResource("closeFile.png"));
    ImageIcon        image1            =
        new ImageIcon(SubmitGaussianFrame.class.getResource("openFile.png"));
    JLabel           statusBar         = new JLabel();
    JPanel           mainPanel         = new JPanel();
    BorderLayout     borderLayout2     = new BorderLayout();
    SubmitGaussPanel submitGaussPanel1 = new SubmitGaussPanel();
    JButton          submitButton      = new JButton();
    JPanel           jPanel1           = new JPanel();
    JPanel           contentPane;

    public SubmitGaussianFrame() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);
        setSize(new Dimension(471, 467));
        setTitle("Submit Gaussian Job");
        statusBar.setText(" ");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new SubmitGaussianFrame_jMenuFileExit_ActionAdapter(this));
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new SubmitGaussianFrame_jMenuHelpAbout_ActionAdapter(this));
        mainPanel.setLayout(borderLayout2);
        submitButton.setToolTipText("Submit Job");
        submitButton.setText("Submit");
        jMenuBar1.add(jMenuFile);
        jMenuFile.add(jMenuFileExit);
        jMenuBar1.add(jMenuHelp);
        jMenuHelp.add(jMenuHelpAbout);
        setJMenuBar(jMenuBar1);
        jButton1.setIcon(image1);
        jButton1.setToolTipText("Open File");
        jButton2.setIcon(image2);
        jButton2.setToolTipText("Close File");
        jButton3.setIcon(image3);
        jButton3.setToolTipText("Help");
        jToolBar.add(jButton1);
        jToolBar.add(jButton2);
        jToolBar.add(jButton3);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        mainPanel.add(submitGaussPanel1, BorderLayout.CENTER);
        mainPanel.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(submitButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                SubmitGaussianFrame frame = new SubmitGaussianFrame();

                frame.validate();
                frame.setVisible(true);
            }
        });
    }

    /**
     * File | Exit action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuFileExit_actionPerformed(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Help | About action performed.
     *
     * @param actionEvent ActionEvent
     */
    void jMenuHelpAbout_actionPerformed(ActionEvent actionEvent) {
        SubmitGaussianFrame_AboutBox dlg     = new SubmitGaussianFrame_AboutBox(this);
        Dimension                    dlgSize = dlg.getPreferredSize();
        Dimension                    frmSize = getSize();
        Point                        loc     = getLocation();

        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.setVisible(true);
    }
}


class SubmitGaussianFrame_jMenuFileExit_ActionAdapter implements ActionListener {
    SubmitGaussianFrame adaptee;

    SubmitGaussianFrame_jMenuFileExit_ActionAdapter(SubmitGaussianFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuFileExit_actionPerformed(actionEvent);
    }
}


class SubmitGaussianFrame_jMenuHelpAbout_ActionAdapter implements ActionListener {
    SubmitGaussianFrame adaptee;

    SubmitGaussianFrame_jMenuHelpAbout_ActionAdapter(SubmitGaussianFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        adaptee.jMenuHelpAbout_actionPerformed(actionEvent);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
