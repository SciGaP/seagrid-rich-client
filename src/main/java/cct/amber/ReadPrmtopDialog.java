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

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cct.interfaces.MoleculeInterface;
import cct.modelling.MolecularFileFormats;
import cct.modelling.Molecule;
import cct.tools.FileFilterImpl;

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
public class ReadPrmtopDialog
        extends JDialog {

    private JPanel panel1 = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JButton helpButton = new JButton();
    private JLabel jLabel1 = new JLabel();
    private JPanel filesPanel = new JPanel();
    private JPanel buttonsPanel = new JPanel();
    private JLabel jLabel2 = new JLabel();
    private JButton cancelButton = new JButton();
    private JButton loadButton = new JButton();
    private JButton coordButton = new JButton();
    private JButton browsePrmtopButton = new JButton();
    private JTextField inpcrdTextField = new JTextField();
    private JTextField prmtopTextField = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JFileChooser chooser = null;
    private AmberPrmtop prmtop = null;
    private MoleculeInterface molecule = null;
    private static String lastPWDKey = "lastPWD";
    private Preferences prefs = Preferences.userNodeForPackage(getClass());
    private File currentWorkingDirectory = null;
    private boolean OKPressed = false;
    static final Logger logger = Logger.getLogger(ReadPrmtopDialog.class.getCanonicalName());

    public ReadPrmtopDialog(Frame owner, String title, boolean modal,
            MoleculeInterface mol) {
        super(owner, title, modal);
        molecule = mol;
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jbInit();
            pack();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void setMolecule(MoleculeInterface mol) {
        molecule = mol;
    }

    public MoleculeInterface getMolecule() {
        return molecule;
    }

    private ReadPrmtopDialog() {
        this(new Frame(), "ReadPrmtopDialog", false, null);
    }

    private void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        helpButton.setToolTipText("");
        helpButton.setText("Help");
        helpButton.addActionListener(new ReadPrmtopDialog_helpButton_actionAdapter(this));
        jLabel1.setToolTipText("");
        jLabel1.setText("Coordinate File:");
        jLabel2.setToolTipText("");
        jLabel2.setText("Topology file:");
        cancelButton.setToolTipText("");
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ReadPrmtopDialog_cancelButton_actionAdapter(this));
        loadButton.setEnabled(false);
        loadButton.setToolTipText("");
        loadButton.setText("Load");
        loadButton.addActionListener(new ReadPrmtopDialog_loadButton_actionAdapter(this));
        coordButton.setEnabled(false);
        coordButton.setToolTipText("");
        coordButton.setText("Browse");
        coordButton.addActionListener(new ReadPrmtopDialog_coordButton_actionAdapter(this));
        browsePrmtopButton.setToolTipText("");
        browsePrmtopButton.setText("Browse");
        browsePrmtopButton.addActionListener(new ReadPrmtopDialog_browsePrmtopButton_actionAdapter(this));
        inpcrdTextField.setEnabled(false);
        inpcrdTextField.setEditable(false);
        inpcrdTextField.setText("");
        inpcrdTextField.setColumns(30);
        prmtopTextField.setToolTipText("");
        prmtopTextField.setEditable(false);
        prmtopTextField.setText("");
        prmtopTextField.setColumns(30);
        filesPanel.setLayout(gridBagLayout1);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().add(panel1);
        buttonsPanel.add(loadButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(helpButton);
        panel1.add(buttonsPanel, BorderLayout.SOUTH);
        panel1.add(filesPanel, BorderLayout.CENTER);
        filesPanel.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        filesPanel.add(prmtopTextField,
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        filesPanel.add(browsePrmtopButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        filesPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        filesPanel.add(inpcrdTextField,
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        filesPanel.add(coordButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
    }

    public boolean isOKPressed() {
        return OKPressed;
    }

    public void coordButton_actionPerformed(ActionEvent e) {
        if (chooser == null) {
            javax.swing.filechooser.FileFilter[] filters = FileFilterImpl.getFileFilters(
                    MolecularFileFormats.getUpdateCoordFormats());
            chooser = new JFileChooser();
            for (int i = 0; i < filters.length; i++) {
                chooser.addChoosableFileFilter(filters[i]);
            }

            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle("Open File with coordinates");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        }

        if (currentWorkingDirectory != null) {
            chooser.setCurrentDirectory(currentWorkingDirectory);
        } else {
            String lastPWD = prefs.get(lastPWDKey, "");
            if (lastPWD.length() > 0) {
                currentWorkingDirectory = new File(lastPWD);
                if (currentWorkingDirectory.isDirectory()
                        && currentWorkingDirectory.exists()) {
                    chooser.setCurrentDirectory(currentWorkingDirectory);
                }
            }
        }

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getPath();
            currentWorkingDirectory = chooser.getCurrentDirectory();

            try {
                prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("Cannot save cwd: " + ex.getMessage()
                        + " Ignored...");
            }
            logger.info("You chose to open this file: "
                    + fileName);

            try {
                Molecule.updateCoordinates(molecule,
                        chooser.getFileFilter().
                        getDescription(),
                        fileName);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.loadButton.setEnabled(true);
            inpcrdTextField.setText(fileName);
            //return fileName;
        }

    }

    public void browsePrmtopButton_actionPerformed(ActionEvent e) {
        FileDialog fd = new FileDialog(this, "Open Amber Topology File (prmtop)",
                FileDialog.LOAD);
        fd.setFile("*.top;prmtop");
        fd.setVisible(true);
        if (fd.getFile() == null) {
            return;
        }

        String fileName = fd.getFile();
        String workingDirectory = fd.getDirectory();

        prmtop = new AmberPrmtop();
        try {
            prmtop.parsePrmtop(molecule, workingDirectory + fileName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (prmtop.getNumberOfAtoms() < 1) {
            JOptionPane.showMessageDialog(this,
                    "Didn't find atoms in file",
                    "Didn't find atoms in file",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Found " + prmtop.getNumberOfAtoms()
                + " atoms in file", "Info",
                JOptionPane.INFORMATION_MESSAGE);

        inpcrdTextField.setEnabled(true);
        coordButton.setEnabled(true);

        prmtopTextField.setEnabled(false);
        prmtopTextField.setText(workingDirectory + fileName);
        prmtopTextField.setEnabled(true);
    }

    public void loadButton_actionPerformed(ActionEvent e) {
        this.OKPressed = true;
        prmtopTextField.setText("");
        inpcrdTextField.setText("");
        this.loadButton.setEnabled(false);
        coordButton.setEnabled(false);
        this.setVisible(false);
    }

    public void cancelButton_actionPerformed(ActionEvent e) {
        this.OKPressed = false;
        prmtopTextField.setText("");
        inpcrdTextField.setText("");
        this.loadButton.setEnabled(false);
        coordButton.setEnabled(false);
        this.setVisible(false);
    }

    public void helpButton_actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
                "Download topology file first. Then download coordinates and press Load button",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

class ReadPrmtopDialog_helpButton_actionAdapter
        implements ActionListener {

    private ReadPrmtopDialog adaptee;

    ReadPrmtopDialog_helpButton_actionAdapter(ReadPrmtopDialog adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.helpButton_actionPerformed(e);
    }
}

class ReadPrmtopDialog_cancelButton_actionAdapter
        implements ActionListener {

    private ReadPrmtopDialog adaptee;

    ReadPrmtopDialog_cancelButton_actionAdapter(ReadPrmtopDialog adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.cancelButton_actionPerformed(e);
    }
}

class ReadPrmtopDialog_loadButton_actionAdapter
        implements ActionListener {

    private ReadPrmtopDialog adaptee;

    ReadPrmtopDialog_loadButton_actionAdapter(ReadPrmtopDialog adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.loadButton_actionPerformed(e);
    }
}

class ReadPrmtopDialog_browsePrmtopButton_actionAdapter
        implements ActionListener {

    private ReadPrmtopDialog adaptee;

    ReadPrmtopDialog_browsePrmtopButton_actionAdapter(ReadPrmtopDialog adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.browsePrmtopButton_actionPerformed(e);
    }
}

class ReadPrmtopDialog_coordButton_actionAdapter
        implements ActionListener {

    private ReadPrmtopDialog adaptee;

    ReadPrmtopDialog_coordButton_actionAdapter(ReadPrmtopDialog adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.coordButton_actionPerformed(e);
    }
}
