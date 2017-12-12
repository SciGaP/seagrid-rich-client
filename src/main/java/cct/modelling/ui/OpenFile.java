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
package cct.modelling.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cct.adf.ADF;
import cct.amber.ReadPrmtopDialog;
import cct.gamess.Gamess;
import cct.gamess.GamessOutput;
import cct.gaussian.Gaussian;
import cct.gaussian.GaussianCube;
import cct.gaussian.GaussianFragment;
import cct.gaussian.ui.GaussianInputEditorFrame;
import cct.gromacs.GromacsParserFactory;
import cct.interfaces.AtomInterface;
import cct.interfaces.GraphicsRendererInterface;
import cct.interfaces.MoleculeInterface;
import cct.j3d.UnitCellGraphics;
import cct.mdl.MDLMol;
import cct.modelling.CCTAtomTypes;
import cct.modelling.CHEMISTRY_FILE_FORMAT;
import cct.modelling.MolecularFileFormats;
import cct.modelling.Molecule;
import cct.mopac.Mopac;
import cct.mopac.MopacOutput;
import cct.pdb.PDB;
import cct.qchem.QChem;
import cct.qchem.QChemOutput;
import cct.tools.CCTParser;
import cct.tools.FileFilterImpl;
import cct.tools.SimpleParserFactory;
import cct.tools.XMolXYZ;
import cct.tools.ui.JShowText;
import cct.tripos.TriposParser;
import cct.vasp.ui.ReadPoscarDialog;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
enum OPEN_FILE_MODE {

  SIMPLE, ADVANCED
}

@Deprecated
public class OpenFile
        implements ActionListener {

  static private Preferences prefs = Preferences.userNodeForPackage(OpenFile.class.getClass());
  private static String lastPWDKey = "lastPWD";
  Frame Parent = null;
  ReadPoscarDialog vaspDialog = null;
  ReadPrmtopDialog prmtopDialog = null;
  GaussianInputEditorFrame gaussianInputEditorFrame = null;
  GraphicsRendererInterface Renderer = null;
  Gaussian gaussianData;
  private String fileName = null;
  static final Logger logger = Logger.getLogger(OpenFile.class.getCanonicalName());

  public OpenFile() {
  }

  public OpenFile(Frame parent) {
    Parent = parent;
  }

  public static void main(String[] args) {
    OpenFile openfile = new OpenFile();
  }

  public void setGraphicsRenderer(GraphicsRendererInterface renderer) {
    Renderer = renderer;
  }

  public Object[] loadMolecule(Frame parent, File currentWorkingDirectory) throws Exception {
    Map<CHEMISTRY_FILE_FORMAT, String> readFormats = MolecularFileFormats.commonReadFormats;
    //Map<CHEMISTRY_FILE_FORMAT, String> readFormatsDescr = MolecularFileFormats.commonReadFormatDescription;
    int count = 0;
    FileFilterImpl[] filter = new FileFilterImpl[readFormats.size()];

    Map<String, CHEMISTRY_FILE_FORMAT> reference = new HashMap<String, CHEMISTRY_FILE_FORMAT>();

    JFileChooser chooser = new JFileChooser();

    Set set = readFormats.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      CHEMISTRY_FILE_FORMAT format = (CHEMISTRY_FILE_FORMAT) me.getKey();
      String extensions = readFormats.get(format);
      String temp[] = extensions.split(";");
      filter[count] = new FileFilterImpl();
      String descr = MolecularFileFormats.getFileFormatDescription(format);

      for (int i = 0; i < temp.length; i++) {
        if (i == 0) {
          descr += " (";
        } else {
          descr += ";";
        }
        filter[count].addExtension(temp[i]);
        descr += "*." + temp[i];
        if (i == temp.length - 1) {
          descr += ")";
        }
      }
      filter[count].setDescription(descr);
      chooser.addChoosableFileFilter(filter[count]);
      reference.put(descr, format);
      ++count;
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setAcceptAllFileFilterUsed(false);

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

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = chooser.showOpenDialog(parent);
    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return null;
    }
    FileFilter ff = chooser.getFileFilter();
    CHEMISTRY_FILE_FORMAT format = reference.get(ff.getDescription());
    fileName = chooser.getSelectedFile().getPath();

    return openFile(fileName, format, OPEN_FILE_MODE.SIMPLE);
  }

  public String getFileName() {
    return fileName;
  }

  public Object[] openFile(String fileName, CHEMISTRY_FILE_FORMAT fileType, OPEN_FILE_MODE mode) throws Exception {

    if (fileName == null) {
      throw new Exception(OpenFile.class.getCanonicalName() + ": empty file name");
    }

    MoleculeInterface m = null;
    JShowText showResume;
    Object[] obj = null;

    switch (fileType) {
      case ALL_FORMATS:
        break;
      /*
      case G03_GJF:
      if (gaussianInputEditorFrame != null &&
      gaussianInputEditorFrame.isVisible()) {
      gaussianInputEditorFrame.setVisible(false);
      }
      
      gaussianData = new Gaussian();
      int n = gaussianData.parseGJF(fileName, 0);
      logger.info("Number of molecules: " + n);
      if (n < 1) {
      throw new Exception("Didn't find atoms in file");
      }
      
      // --- In a SIMPLE mode just take the first structure
      if (mode == OPEN_FILE_MODE.SIMPLE) {
      m = Molecule.getNewInstance();
      m = gaussianData.getMolecule(m, 0);
      if (m == null || m.getNumberOfAtoms() < 1) {
      throw new Exception("Didn't find atoms in file");
      }
      Molecule.guessCovalentBonds(m);
      Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
      CCTAtomTypes.getElementMapping());
      break;
      }
      
      if (n > 1) {
      if (gaussianInputEditorFrame == null) {
      gaussianData.setGraphicsRenderer(Renderer);
      gaussianInputEditorFrame = new GaussianInputEditorFrame(gaussianData);
      gaussianInputEditorFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      try {
      gaussianInputEditorFrame.removeMenuItem("File", "Exit");
      }
      catch (Exception ex) {
      
      }
      JMenuItem item = new JMenuItem("Select current step & exit");
      //GaussianInputEditorFrame_selectStep_actionAdapter al = new
      //    GaussianInputEditorFrame_selectStep_actionAdapter();
      
      item.addActionListener(this);
      gaussianInputEditorFrame.addMenuItem("File", item);
      gaussianInputEditorFrame.returnBackButton.setVisible(true);
      gaussianInputEditorFrame.returnBackButton.addActionListener(this);
      
      }
      gaussianInputEditorFrame.setupEditor(gaussianData);
      JOptionPane.showMessageDialog(null,
      "Gaussian Input file has more than 1 step\n" +
      "Select step in the next dialog",
      "Info",
      JOptionPane.INFORMATION_MESSAGE);
      
      gaussianInputEditorFrame.setVisible(true);
      //logger.info("Loading 1st Molecule...");
      }
      else {
      m = Molecule.getNewInstance();
      m = gaussianData.getMolecule(m, 0);
      if (m == null || m.getNumberOfAtoms() < 1) {
      throw new Exception("Didn't find atoms in file");
      }
      Molecule.guessCovalentBonds(m);
      Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
      CCTAtomTypes.getElementMapping());
      //MoleculeInterface m2 = Molecule.divideIntoMolecules(m);
      }
      
      break;
       */
      /*
      case G03_OUTPUT:
      if (fileName == null) {
      fileName = chooseFileDialog(MolecularFileFormats.gaussian03Output,
      "Gaussian G03 Output Files (*.log;*.out)",
      JFileChooser.OPEN_DIALOG);
      }
      if (fileName != null) {
      j3d.openMolecularModelingFile(MolecularFileFormats.gaussian03Output, fileName);
      }
      
      break;
       */
      case G03_CUBE:
        try {
          m = new Molecule();
          GaussianCube.extractGeometry(fileName, m);
          Molecule.guessCovalentBonds(m);
          Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                  CCTAtomTypes.getElementMapping());
          //MoleculeInterface m2 = Molecule.divideIntoMolecules(mol);
        } catch (Exception ex) {
          throw new Exception("Error Loading Gaussian Cube file : " + ex.getMessage());
        }
        break;

      case G03_FRAGMENT:
        m = new Molecule();
        m = GaussianFragment.parseGaussianFragmentFile(m, fileName);
        break;

      case GAMESS_INPUT:
        Gamess gamess = new Gamess();
        gamess.parseGamessInput(fileName, 0);
        m = new Molecule();
        gamess.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());
        break;

      case GAMESS_OUTPUT:
        GamessOutput gamess_out = new GamessOutput();
        gamess_out.parseGamessOutputFile(fileName);
        m = new Molecule();
        gamess_out.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      case MOPAC2002_LOG:
        MopacOutput mopac = new MopacOutput();
        mopac.parseMopacLogFile(fileName);
        m = new Molecule();
        mopac.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());

        showResume = new JShowText("MOPAC Log File Resume");
        showResume.setSize(600, 640);
        showResume.setTitle("MOPAC Log File Resume: " + fileName);
        showResume.setLocationByPlatform(true);
        showResume.setText(mopac.getOutputResume());
        showResume.setVisible(true);
        break;

      case MOPAC_INPUT:
        Mopac mopac_in = new Mopac();
        mopac_in.parseMopacInput(fileName, 0);
        m = new Molecule();
        mopac_in.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      case MOPAC_OUTPUT:
        MopacOutput mopac_out = new MopacOutput();
        mopac_out.parseMopacOutputFile(fileName);
        m = new Molecule();
        mopac_out.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());

        showResume = new JShowText("MOPAC Output File Resume");
        showResume.setSize(600, 640);
        showResume.setTitle("MOPAC Output File Resume: " + fileName);
        showResume.setLocationByPlatform(true);
        showResume.setText(mopac_out.getOutputResume());
        showResume.setVisible(true);

        break;
      case PDB:
        m = new Molecule();
        PDB.parsePDBFile(fileName, m);
        break;

      case ADF_INPUT:
        ADF adf_input = new ADF();
        adf_input.parseInput(fileName, 0);
        m = new Molecule();
        adf_input.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());
        break;

      case MOL2:
        m = new Molecule();
        TriposParser triposParser = new TriposParser();
        triposParser.parseMol2File(fileName, m);
        break;

      case MDL_MOLFILE:
        m = new Molecule();
        MDLMol mol = new MDLMol();
        m = mol.parseFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      case GROMACS_GRO:
        m = new Molecule();
        m = GromacsParserFactory.parseGromacsCoordFile(fileName, m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      case AMBER_PRMTOP:
        if (prmtopDialog == null) {
          prmtopDialog = new ReadPrmtopDialog(Parent, "Open Amber Topology File (prmtop)", true, null);
          prmtopDialog.setLocationRelativeTo(Parent);
        }
        m = new Molecule();
        prmtopDialog.setMolecule(m);

        prmtopDialog.setVisible(true);

        if (prmtopDialog.isOKPressed()
                && prmtopDialog.getMolecule().getNumberOfAtoms() > 0) {
          //this.java3dUniverse.addMolecule(prmtopDialog.getMolecule());
        } else {
          return null;
        }

        break;

      case CCT:
        m = new Molecule();
        CCTParser cctParser = new CCTParser(m);
        List mols = cctParser.parseCCTFile(fileName, m);
        m = (Molecule) mols.get(0);
        break;

      case XMOL_XYZ:
        m = new Molecule();
        XMolXYZ xMolXYZ = new XMolXYZ();
        m = xMolXYZ.parseXMolXYZ(fileName, m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE,
                CCTAtomTypes.getElementMapping());

        break;
      case VASP_POSCAR:
        if (vaspDialog == null) {
          vaspDialog = new ReadPoscarDialog(Parent, "Open VASP Poscar File (poscar)", true, null);
          vaspDialog.setLocationRelativeTo(Parent);
        }
        m = new Molecule();
        vaspDialog.setMolecule(m);

        vaspDialog.setVisible(true);

        if (vaspDialog.isOKPressed()
                && vaspDialog.getMolecule().getNumberOfAtoms() > 0) {
          m = vaspDialog.getMolecule();
          Molecule.guessCovalentBonds(m);
          UnitCellGraphics poscarUC = new UnitCellGraphics("VASP Unit Cell");
          obj = new Object[2];
          obj[0] = m;
          obj[1] = poscarUC.getCellGraphicsObject(vaspDialog.getLatticeVectors());
        }
        return obj;
      //break;

      case QCHEM_INPUT:
        QChem qchem = new QChem();
        qchem.parseQChemInput(fileName, 0);
        m = new Molecule();
        qchem.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
        break;

      case QCHEM_OUTPUT:
        QChemOutput qchem_out = new QChemOutput();
        qchem_out.parseQChemOutputFile(fileName, false);
        m = new Molecule();
        qchem_out.getMolecularInterface(m);
        Molecule.guessCovalentBonds(m);
        Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());

        showResume = new JShowText("Q-Chem Output File Resume");
        showResume.setSize(600, 640);
        showResume.setTitle("Q-Chem Calculation Summary: " + fileName);
        showResume.setLocationByPlatform(true);
        showResume.setText(qchem_out.getOutputResume());
        showResume.setVisible(true);

        break;

    }
    logger.info("Number of atoms: " + m.getNumberOfAtoms());
    obj = new Object[1];
    obj[0] = m;
    return obj;
  }

  /*
  String chooseFileDialog(String fileType, String formatDescr, int dialogType) {
  JFileChooser chooser = new JFileChooser();
  FileFilterImpl filter = new FileFilterImpl();
  String extensions = MolecularFileFormats.readFormats.get(fileType).toString();
  String temp[] = extensions.split(";");
  for (int i = 0; i < temp.length; i++) {
  filter.addExtension(temp[i]);
  }
  filter.setDescription(formatDescr);
  chooser.setFileFilter(filter);
  chooser.setDialogType(dialogType);
  if (dialogType == JFileChooser.OPEN_DIALOG) {
  chooser.setDialogTitle("Open File");
  chooser.setDialogType(JFileChooser.OPEN_DIALOG);
  }
  else if (dialogType == JFileChooser.SAVE_DIALOG) {
  chooser.setDialogTitle("Save File");
  chooser.setDialogType(JFileChooser.SAVE_DIALOG);
  }
  
  if (currentWorkingDirectory != null) {
  chooser.setCurrentDirectory(currentWorkingDirectory);
  }
  else {
  String lastPWD = prefs.get(lastPWDKey, "");
  if (lastPWD.length() > 0) {
  currentWorkingDirectory = new File(lastPWD);
  if (currentWorkingDirectory.isDirectory() &&
  currentWorkingDirectory.exists()) {
  chooser.setCurrentDirectory(currentWorkingDirectory);
  }
  }
  }
  
  int returnVal = JFileChooser.CANCEL_OPTION;
  
  if (dialogType == JFileChooser.OPEN_DIALOG) {
  returnVal = chooser.showOpenDialog(this);
  }
  else if (dialogType == JFileChooser.SAVE_DIALOG) {
  returnVal = chooser.showSaveDialog(this);
  }
  
  if (returnVal == JFileChooser.APPROVE_OPTION) {
  String fileName = chooser.getSelectedFile().getPath();
  //String separator = chooser.getSelectedFile().pathSeparator;
  //String fileName = chooser.getSelectedFile().getName();
  currentWorkingDirectory = chooser.getCurrentDirectory();
  try {
  prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
  }
  catch (Exception ex) {
  System.err.println("Cannot save cwd: " + ex.getMessage() +
  " Ignored...");
  }
  logger.info("You chose to open this file: " +
  fileName);
  return fileName;
  }
  return null;
  }
   */
  @Override
  public void actionPerformed(ActionEvent e) { /// !!! WRONG
    gaussianInputEditorFrame.setVisible(false);
    int n = gaussianInputEditorFrame.getSelectedStep();
    MoleculeInterface m = Molecule.getNewInstance();
    m = gaussianData.getMolecule(m, n);
    Molecule.guessCovalentBonds(m);

    //if (this.processingSelected == SELECTED_ADD_MOLECULE) {
    //   enableMousePicking(true);
    //}
  }
}
