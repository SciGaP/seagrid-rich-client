package cct.modelling.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cct.interfaces.AtomInterface;
import cct.interfaces.CoordinateParserInterface;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import cct.modelling.CCTAtomTypes;
import cct.modelling.FormatManager;
import cct.modelling.Molecule;
import cct.tools.IOUtils;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EditAndPasteCoordFrame
    extends JFrame implements ItemListener {
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JPanel formatsPanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JTextArea coordTextArea = new JTextArea();
  private JButton cancelButton = new JButton();
  private JRadioButton jRadioButton1 = new JRadioButton();
  private FlowLayout flowLayout1 = new FlowLayout();
  private ButtonGroup buttonGroup = new ButtonGroup();
  private JButton okButton = new JButton();

  private Java3dUniverse java3dUniverse = null;
  private String selectedBuilder = "";
  private String defaultFileName = "text.txt";
  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu jMenu1 = new JMenu();
  private JMenuItem jMenuItem1 = new JMenuItem();
  private JMenuItem jMenuItem2 = new JMenuItem();
  private JButton validateButton = new JButton();

  private Map<String, JRadioButton> refTable = new HashMap<String, JRadioButton> ();

  public EditAndPasteCoordFrame() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    coordTextArea.setText("jTextArea1");
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    jRadioButton1.setText("jRadioButton1");
    formatsPanel.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    okButton.setText("  OK  ");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    this.setJMenuBar(jMenuBar1);
    jMenu1.setText("File");
    jMenuItem1.setText("Save As");
    jMenuItem1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    jMenuItem2.setText("Exit");
    jMenuItem2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jMenuItem2_actionPerformed(e);
      }
    });
    validateButton.setText("Validate Geometry");
    validateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        validateButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(okButton);
    jPanel2.add(validateButton);
    jPanel2.add(cancelButton);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(coordTextArea);
    jPanel1.add(formatsPanel, BorderLayout.SOUTH);
    formatsPanel.add(jRadioButton1);
    buttonGroup.add(jRadioButton1);
    jMenuBar1.add(jMenu1);
    jMenu1.add(jMenuItem1);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem2);
    Font currentFont = coordTextArea.getFont();
    int size = currentFont.getSize();
    int style = currentFont.getStyle();
    String fontName = currentFont.getFontName();

    if (size < 12) {
      Font newFont = new Font(fontName, style, 12);
      coordTextArea.setFont(newFont);
    }

    setFormatManager();

  }

  private void setFormatManager() throws Exception {
    String[] parsers = FormatManager.getCoordinateParsers();

    formatsPanel.removeAll();
    refTable.clear();

    if (parsers == null) {
      throw new Exception("Cannot get list of coordinate parsers: ");
    }

    coordTextArea.setText("");
    for (int i = 0; i < parsers.length; i++) {
      JRadioButton jRadioButton = new JRadioButton();
      jRadioButton.setText(parsers[i]);
      jRadioButton.setActionCommand(parsers[i]);
      formatsPanel.add(jRadioButton);
      buttonGroup.add(jRadioButton);
      refTable.put(parsers[i], jRadioButton);
      jRadioButton.addItemListener(this);
      if (i == 0) {
        jRadioButton.setSelected(true);
      }
      else {
        jRadioButton.setSelected(false);
      }
    }
    okButton.grabFocus();

    this.pack();
  }

  public void setDefaultFileName(String file_name) {
    defaultFileName = file_name;
  }

  private void saveTextIntoFile() {
    String text = coordTextArea.getText();
    if (text == null || text.length() < 1) {
      JOptionPane.showMessageDialog(this, "Nothing to save", "Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }

    FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
    fd.setFile(defaultFileName);
    fd.setVisible(true);
    if (fd.getFile() != null) {
      String fileName = fd.getFile();
      String workingDirectory = fd.getDirectory();
      try {
        IOUtils.saveStringIntoFile(text, workingDirectory + fileName);
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error Saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void setJava3dUniverse(Java3dUniverse j3d) {
    java3dUniverse = j3d;
  }

  public static void main(String[] args) {
    EditAndPasteCoordFrame editandpastecoordframe = new EditAndPasteCoordFrame();
  }

  public void jMenuItem2_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  public void jMenuItem1_actionPerformed(ActionEvent e) {
    saveTextIntoFile();
  }

  public void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }

  @Override
  public void itemStateChanged(ItemEvent e) {

    if (e.getStateChange() == ItemEvent.SELECTED) {
      selectedBuilder = ( (JRadioButton) e.getSource()).getActionCommand();

    }
    okButton.grabFocus();
  }

  private void parseCoordinates() throws Exception {

    if (coordTextArea.getText().trim().length() < 7) {
      JOptionPane.showMessageDialog(this, "Text area does not have enough data", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      BufferedReader in = new BufferedReader(new StringReader(coordTextArea.getText()));
      MoleculeInterface molecule = new Molecule();

      CoordinateParserInterface cpi = FormatManager.getCoordinateParser(selectedBuilder);
      cpi.parseCoordinates(in, molecule);

      if (molecule == null || molecule.getNumberOfAtoms() < 1) {
        JOptionPane.showMessageDialog(this, "Didn't find coordinates", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      Molecule.guessCovalentBonds(molecule);
      Molecule.guessAtomTypes(molecule, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
      java3dUniverse.setMolecule(molecule);

    }
    catch (Exception ex) {
      throw ex;
    }

  }

  public void setCoordinates(String text) {
    coordTextArea.setText(text);
    String parser = FormatManager.guessSimpleCoordinateParser(text);
    if (parser != null) {
      JRadioButton jRadioButton = refTable.get(parser);
      jRadioButton.setSelected(true);
    }
  }

  public void validateButton_actionPerformed(ActionEvent e) {
    try {
      parseCoordinates();
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error while parsing coordinates: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

  public void okButton_actionPerformed(ActionEvent e) {
    try {
      parseCoordinates();
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Error while parsing coordinates: " + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }

    setVisible(false);
  }

}
