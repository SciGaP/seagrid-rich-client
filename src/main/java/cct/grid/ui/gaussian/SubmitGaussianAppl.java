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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cct.gaussian.Gaussian;
import cct.gaussian.ui.GaussianInputEditorFrame;
import cct.globus.ui.GridProxyInitDialog;
import cct.grid.ui.CheckPointStatus;
import cct.grid.ui.JobOutputInterface;
import cct.grid.ui.JobStatusDialog;
import cct.j3d.SimpleRenderer;
import cct.tools.IOUtils;

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
public class SubmitGaussianAppl implements ActionListener {
    static boolean                   useGraphics    = true;
    GridProxyInitDialog              gpiFrame       = null;
    IOFrame                          jio            = null;
    JobStatusDialog                  jstatusd       = null;
    boolean                          packFrame      = false;
    private SubmitGaussianDialog     sgd            = null;
    JMenuItem                        jobStatusItem  = new JMenuItem("Job Status");
    JMenuItem                        gridSubmitItem = new JMenuItem("Submit to Grid");
    JMenuItem                        getProxyItem   = new JMenuItem("Get Proxy");
    private GaussianInputEditorFrame frame;

    /**
     * Construct and show the application.
     */
    public SubmitGaussianAppl() {

        // SubmitGaussianFrame frame = new SubmitGaussianFrame();
        Gaussian g = new Gaussian();

        if (useGraphics) {
            SimpleRenderer r = new SimpleRenderer();

            g.setGraphicsRenderer(r);
        }

        frame = new GaussianInputEditorFrame(g);
        gridSubmitItem.addActionListener(this);
        getProxyItem.addActionListener(this);
        jobStatusItem.addActionListener(this);

        JMenu fileMenu = frame.getFileMenu();
        int   n        = fileMenu.getItemCount();

        if (n <= 2) {
            n = 0;
        } else {
            n -= 2;
        }

        fileMenu.insertSeparator(n);
        fileMenu.insert(getProxyItem, n + 1);
        fileMenu.insert(gridSubmitItem, n + 1);
        fileMenu.insert(jobStatusItem, n + 1);

        JToolBar toolBar = frame.getToolBar();

        // jio = new IOFrame();
        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize  = frame.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-nographics")) {
                useGraphics = false;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                new SubmitGaussianAppl();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gridSubmitItem) {

            // --- Save gjf file
            FileDialog fd = new FileDialog(new Frame(), "Save Gaussian Job File", FileDialog.SAVE);

            fd.setFile("*.gjf;*.com;*.g03");

            // fd.setDirectory(directory);
            // fd.setFile(file_name);
            fd.setVisible(true);

            if (fd.getFile() == null) {
                return;
            }

            String newFileName         = new String(fd.getFile());
            String newWorkingDirectory = new String(fd.getDirectory());

            try {
                IOUtils.saveStringIntoFile(frame.toString(), newWorkingDirectory + newFileName);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            // --- Open job submit dialog
            if (sgd == null) {
                if (jio != null) {
                    jio.setVisible(true);
                }

                sgd = new SubmitGaussianDialog(new Frame(), jio, "Submit Gaussian Job", false);
                sgd.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                sgd.enableLocalDir(false);
                sgd.enableInputFile(false);

                String[] gk = { "ng1.apac.edu.au" };

                sgd.setGatekeepers(gk);

                String[] hosts = { "lc.apac.edu.au", "ac.apac.edu.au" };

                sgd.setHosts(hosts);
            }

            sgd.setInputFile(newFileName);
            sgd.setLocalDir(newWorkingDirectory);
            sgd.setVisible(true);
        } else if (e.getSource() == getProxyItem) {
            if (gpiFrame == null) {
                gpiFrame = new GridProxyInitDialog(new Frame(), true, false);
            }

            gpiFrame.setVisible(true);
        } else if (e.getSource() == jobStatusItem) {
            if (jstatusd == null) {
                CheckPointStatus chk = new CheckPointStatus();

                jstatusd = new JobStatusDialog(new Frame(), "Job Status", false, chk);
            }

            jstatusd.queryJobStatus();
            jstatusd.setVisible(true);
        }
    }

    class IOFrame extends JFrame implements JobOutputInterface {
        JScrollPane jScrollPane1 = new JScrollPane();
        JTextArea   jTextArea1   = new JTextArea();
        JPanel      contentPane;

        public IOFrame() {
            contentPane = (JPanel) getContentPane();
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            setSize(new Dimension(400, 300));
            setTitle("Job Input/Output");
            jTextArea1.setToolTipText("");
            jTextArea1.setEditable(false);
            contentPane.add(jScrollPane1, java.awt.BorderLayout.CENTER);
            jScrollPane1.getViewport().add(jTextArea1);
        }

        @Override
        public void appendOutput(String output) {
            jTextArea1.append(output);
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
