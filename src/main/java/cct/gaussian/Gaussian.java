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
package cct.gaussian;

import cct.GlobalSettings;
import cct.cprocessor.CommandInterface;
import cct.cprocessor.Variable;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.GraphicsRendererInterface;
import cct.interfaces.MoleculeEventObject;
import cct.interfaces.MoleculeInterface;
import cct.modelling.CCTAtomTypes;
import cct.modelling.ChemicalElements;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.Molecule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Gaussian extends GeneralMolecularDataParser implements GJFParserInterface, CommandInterface {

  static boolean Debug = true;
  public static final String MEMORY_TEMPLATE = "@@MEMORY@@";
  public static final String NCPUS_TEMPLATE = "@@NCPUS@@";
  public static final String OPT_TEMPLATE = "@@OPT@@";
  public static final String FREQ_TEMPLATE = "@@FREQ@@";
  public static final String METHOD_TEMPLATE = "@@METHOD@@";
  public static final String BASIS_TEMPLATE = "@@BASIS@@";
  public static final String COORDINATES_TEMPLATE = "@@COORDINATES@@";
  public static final String CHARGE_TEMPLATE = "@@CHARGE@@";
  public static final String MULTIPLICITY_TEMPLATE = "@@MULTIPLICITY@@";
  public static final String TITLE_TEMPLATE = "@@TITLE@@";
  //
  public static final String ATOM_FIXED_PROPERTY = "GAUSSIAN_ATOM_FIXED";
  public static final String FRAGMENT_PROPERTY = "GAUSSIAN_FRAGMENT";
  //
  public final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);
  protected List percentSection = new ArrayList();
  protected List<GaussianMolecule> Molecules = new ArrayList<GaussianMolecule>();
  private String Tokens[] = new String[10];
  String Message = null;
  GraphicsRendererInterface renderer = null;
  static final Logger logger = Logger.getLogger(Gaussian.class.getCanonicalName());
  public final static String LINK_ZERO_COMMANDS_KEY = "GaussianLinkZeroCommand";
  public final static String ROUTE_COMMANDS_KEY = "GaussianRouteCommand";
  public final static String MOLEC_SPECS_KEY = "GaussianMoleculeSpec";
  public final static String NUMBER_OF_ATOMS_KEY = "GaussianNumberOfAtoms";
  final static String link0_MEM = "%MEM";
  final static String link0_CHK = "%CHK";
  final static String link0_RWF = "%RWF";
  final static String link0_INT = "%INT";
  final static String link0_D2E = "%D2E";
  final static String link0_NPROC = "%NPROC";
  final static String link0_KJOB = "%KJOB"; // %KJob LN [M]
  final static String link0_NPROCSHARED = "%NPROCSHARED";
  final static String link0_SAVE = "%SAVE";
  final static String link0_NOSAVE = "%NOSAVE";
  final static String link0_SUBST = "%SUBST";
  final static String link0_NPROCLINDA = "%NPROCLINDA";
  final static String link0_LINDAWORKERS = "%LINDAWORKERS";
  // Integer - number of arguments
  static final Map<String, Integer> link0Commands = new HashMap<String, Integer>();

  static {
    link0Commands.put(link0_MEM, new Integer(1));
    link0Commands.put(link0_CHK, new Integer(1));
    link0Commands.put(link0_RWF, new Integer(1));
    link0Commands.put(link0_INT, new Integer(1));
    link0Commands.put(link0_D2E, new Integer(1));
    link0Commands.put(link0_KJOB, new Integer(-2)); // Up to 2 parameters
    link0Commands.put(link0_NPROCSHARED, new Integer(1));
    link0Commands.put(link0_NPROC, new Integer(1));
    link0Commands.put(link0_SAVE, new Integer(0));
    link0Commands.put(link0_NOSAVE, new Integer(0));
    link0Commands.put(link0_SUBST, new Integer(2));
    link0Commands.put(link0_NPROCLINDA, new Integer(1));
    link0Commands.put(link0_LINDAWORKERS, new Integer(1));
  }

  public Gaussian() {
  }

  static public void setLoggerLevel(Level level) {
    logger.setLevel(level);
    GaussianMolecule.setLoggerLevel(level);
  }

  private void addGaussianMolecule(GaussianMolecule gmol) {
    Molecules.add(gmol);

    MoleculeInterface mol = getMoleculeInterface();
    gmol.getMolecule(mol);
    if (mol.getNumberOfAtoms() < 1) {
      return;
    }
    Molecule.guessCovalentBonds(mol);
    Molecule.guessAtomTypes(mol);
    this.addMolecule(mol);
  }

  public int getNumberOfMolecules() {
    return Molecules.size();
  }

  @Override
  public int getNumberOfSteps() {
    return getNumberOfMolecules();
  }

  public void setGraphicsRenderer(GraphicsRendererInterface r) {
    renderer = r;
  }

  void resetTokens() {
    for (int i = 0; i < Tokens.length; i++) {
      Tokens[i] = null;
    }
  }

  int numberOfTokens() {
    int i;
    for (i = 0; i < Tokens.length && Tokens[i] != null; i++) {
    }
    return i;
  }

  public String getErrorMessage() {
    return Message;
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    Gaussian g = new Gaussian();
    try {
      g.parseData(in);
    } catch (Exception ex) {
      return 0;
    }
    if (g.getMolecule() == null || g.getMolecule().getNumberOfAtoms() < 1) {
      return 0;
    }
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {
    String line;
    GaussianMolecule Atoms = new GaussianMolecule();
    boolean z_matrix = false;

    try {

      while ((line = in.readLine()) != null) {
        logger.info("Parsing: " + line + "\n");
        line = line.trim();

        if (line.startsWith("#") || line.startsWith("%") || line.startsWith("--Link1--")) {
          Atoms = new GaussianMolecule();

          // --- Skip line if it's a Link 1 section
          if (line.startsWith("--Link1--")) {
            if ((line = in.readLine()) == null) {
              String msg = "Unexpected end-of-file after reading Link 1 section";
              logger.severe(msg);
              //throw new Exception(msg);
              break;
            }
            line = line.trim();
          }

          // --- Read Link 0 Commands (% lines)
          if (line.startsWith("%")) {
            try {
              Atoms.addLinkZeroCommands(line);
            } catch (Exception ex) {
              Message += ex.getMessage() + "\n";
            }
            while ((line = in.readLine()) != null) {
              logger.info("Parsing %%% section: " + line);
              line = line.trim();
              if (line.startsWith("#")) {
                break;
              }
              try {
                Atoms.addLinkZeroCommands(line);
              } catch (Exception ex) {
                Message += ex.getMessage() + "\n";
              }

            }
          }

          // --- Read Route section (# lines)
          if (line.startsWith("#")) {
            logger.info("Parsing route: " + line);
            Atoms.addRouteOptions(line);
            while ((line = in.readLine()) != null) {
              logger.info("Parsing route: " + line);
              line = line.trim();
              if (line.length() == 0) {
                break;
              }
              Atoms.addRouteOptions(line);
            }
            Atoms.parseRouteOptions();
          }

          // --- Read in title section
          if (Atoms.routeSection.isSkipTitleSection()) {
            continue;
          }

          boolean not_first_comment_line = false;
          while ((line = in.readLine()) != null) {
            logger.info("Parsing title: " + line + "\n");
            line = line.trim();
            if (line.length() == 0 && not_first_comment_line) {
              break;
            }
            not_first_comment_line = true;
            Atoms.titleSection.add(line);
          }

          // --- Read in charge and spin multiplicity section
          if (Atoms.routeSection.isSkipChargeSection()) {
            continue;
          }

          if ((line = in.readLine()) != null) {
            logger.info("Parsing: " + line + "\n");
            int[] ch = null;
            try {
              ch = Atoms.parseChargeSection(line);
              Atoms.setChargeAndMultiplicity(ch);
            } catch (Exception ex) {
              String msg = "Error parsing line: " + line + " : " + ex.getMessage();
              logger.severe(msg);
              //throw new Exception(msg);
              return;
            }
            Atoms.chargeSection.add(line);
          } else {
            String msg = "Unexpected end of file while reading charge section";
            logger.severe(msg);
            //return Molecules.size(); !!!
            return;
          }

          if (Atoms.routeSection.isSkipMoleculeSpecification()) {
            continue;
          }

          // Read molecule
          z_matrix = false;
          //Atoms = new AtomParamArray();

          while ((line = in.readLine()) != null) {
            logger.info("Reading line: " + line);
            if (line.equalsIgnoreCase("--Link1--")) {
              break;
            }
            Atoms.addMoleculeSpecsItem(line);
          } // End of while

          try {
            Atoms.parseMoleculeGeometry();
          } catch (Exception ex) {
            logger.warning("Error Parsing Molecular Specifications: " + ex.getMessage());
          }

          logger.info("Number of Atoms: " + Atoms.size() + "\n");

        }

        if (Atoms.size() > 0) {
          //if (z_matrix) {
          //  Atoms.cartesians = fromInternalToCartesians(Atoms);
          //}

          //Molecules.add(Atoms);
          addGaussianMolecule(Atoms);
        }

      } // --- End of while

      //in.close();
    } catch (Exception e) {
      logger.warning("Error: " + e.getMessage());
      //return Molecules.size();
      return;
    }

    logger.info("parseGJF: Number of molecules: " + Molecules.size() + "\n");
    //return Molecules.size();
    return;
  }

  /**
   * Parses Gaussian Job File (gjf).
   *
   * @param filename String File name or String bufer (see fileType argument)
   * @param fileType int File type, 0 - read from the file <tt>filename</tt>, 1 - <tt>filename</tt> is a String bufer
   * @return int Returns number of molecules. -1 otherwise (use getErrorMessage() to get error description)
   */
  @Override
  public int parseGJF(String filename, int fileType) {
    String line;
    GaussianMolecule Atoms = new GaussianMolecule();
    boolean z_matrix = false;

    BufferedReader in = null;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(filename));
      } catch (java.io.FileNotFoundException e) {
        Message = e.getMessage();
        return -1;
      } catch (IOException ex) {
        Logger.getLogger(Gaussian.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(filename));
    } else {
      Message = "Unknown file type";
      return -1; // Error
    }

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));

      while ((line = in.readLine()) != null) {
        if (Debug) {
          logger.info("Parsing: " + line + "\n");
        }
        line = line.trim();

        if (line.startsWith("#") || line.startsWith("%")
            || line.startsWith("--Link1--")) {
          Atoms = new GaussianMolecule();

          // --- Skip line if it's a Link 1 section
          if (line.startsWith("--Link1--")) {
            if ((line = in.readLine()) == null) {
              System.err.println(
                  "Unexpected end-of-file after reading Link 1 section");
              break;
            }
            line = line.trim();
          }

          // --- Read Link 0 Commands (% lines)
          if (line.startsWith("%")) {
            try {
              Atoms.addLinkZeroCommands(line);
            } catch (Exception ex) {
              Message += ex.getMessage() + "\n";
            }
            while ((line = in.readLine()) != null) {
              if (Debug) {
                logger.info("Parsing %%% section: " + line);
              }
              line = line.trim();
              if (line.startsWith("#")) {
                break;
              }
              try {
                Atoms.addLinkZeroCommands(line);
              } catch (Exception ex) {
                Message += ex.getMessage() + "\n";
              }

            }
          }

          // --- Read Route section (# lines)
          if (line.startsWith("#")) {
            if (Debug) {
              logger.info("Parsing route: " + line);
            }
            Atoms.addRouteOptions(line);
            while ((line = in.readLine()) != null) {
              if (Debug) {
                logger.info("Parsing route: " + line);
              }
              line = line.trim();
              if (line.length() == 0) {
                break;
              }
              Atoms.addRouteOptions(line);
            }
            Atoms.parseRouteOptions();
          }

          // --- Read in title section
          if (Atoms.routeSection.isSkipTitleSection()) {
            continue;
          }

          boolean not_first_comment_line = false;
          while ((line = in.readLine()) != null) {
            if (Debug) {
              logger.info("Parsing title: " + line + "\n");
            }
            line = line.trim();
            if (line.length() == 0 && not_first_comment_line) {
              break;
            }
            not_first_comment_line = true;
            Atoms.titleSection.add(line);
          }

          // --- Read in charge and spin multiplicity section
          if (Atoms.routeSection.isSkipChargeSection()) {
            continue;
          }

          if ((line = in.readLine()) != null) {
            if (Debug) {
              logger.info("Parsing: " + line + "\n");
            }
            int[] ch = null;
            try {
              ch = Atoms.parseChargeSection(line);
              Atoms.setChargeAndMultiplicity(ch);
            } catch (Exception ex) {
              System.err.println("ERROR: parseGJF " + ex.getMessage());
              return -1;
            }
            Atoms.chargeSection.add(line);
          } else {
            System.err.println(
                "parseGJF: Unexpected end of file while reading charge section\n");
            return Molecules.size();
          }

          if (Atoms.routeSection.isSkipMoleculeSpecification()) {
            continue;
          }

          // Read molecule
          z_matrix = false;
          //Atoms = new AtomParamArray();

          while ((line = in.readLine()) != null) {
            if (Debug) {
              logger.info("Reading line: " + line);
            }
            if (line.equalsIgnoreCase("--Link1--")) {
              break;
            }
            //line = line.trim(); //.toUpperCase();
            Atoms.addMoleculeSpecsItem(line);
          } // End of while

          try {
            Atoms.parseMoleculeGeometry();
          } catch (Exception ex) {
            System.err.println(
                "Error Parsing Molecular Specifications: " + ex.getMessage());
          }

          if (Debug) {
            logger.info("Number of Atoms: " + Atoms.size() + "\n");
          }

        }

        if (Atoms.size() > 0) {
          //if (z_matrix) {
          //  Atoms.cartesians = fromInternalToCartesians(Atoms);
          //}

          //Molecules.add(Atoms);
          addGaussianMolecule(Atoms);
        }

      } // --- End of while

      in.close();

    } catch (IOException e) {
      System.err.println("parseGJF: " + e.getMessage() + "\n");
      return Molecules.size();
    }

    if (Debug) {
      logger.info("parseGJF: Number of molecules: " + Molecules.size() + "\n");
    }
    return Molecules.size();
  }

  public MoleculeInterface getMolecule(MoleculeInterface mol, int n) {

    if (n < 0 || n >= Molecules.size()) {
      return null;
    }

    //Molecule mol = new Molecule();
    GaussianMolecule molecule = Molecules.get(n);
    molecule.getMolecule(mol);
    return mol;
  }

  public GaussianMolecule getGaussianMolecule(int n) {

    if (n < 0 || n >= Molecules.size()) {
      return null;
    }

    return Molecules.get(n);
  }

  public boolean setMolecule(MoleculeInterface mol, int index) {
    if (index < 0 || index > Molecules.size()) {
      System.err.println("setMolecule: index < 0 || index > Molecules.size()");
      return false; // unable to add molecule
    } else if (mol == null || mol.getNumberOfAtoms() == 0) {
      System.err.println("mol == null || mol.getNumberOfAtoms() == 0");
      return false; // unable to add molecule
    }

    GaussianMolecule Atoms;
    if (index == Molecules.size()) { // Add new molecule at the end
      Atoms = new GaussianMolecule();
      //Molecules.add(Atoms);
      addGaussianMolecule(Atoms);
      percentSection.add(null);
    } else {
      Atoms = Molecules.get(index);
      Atoms.clear();
    }
    Atoms.ensureCapacity(mol.getNumberOfAtoms());

    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface at = mol.getAtomInterface(i);
      GaussianAtom gat = new GaussianAtom(at);
      Atoms.add(gat);
    }
    return true;
  }

  @Override
  public List getLinkZeroCommands(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return null;
    }
    GaussianMolecule molecule = Molecules.get(step);
    return molecule.getLinkZeroCommands();
  }

  public String getRouteSection(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return "";
    }
    GaussianMolecule molecule = Molecules.get(step);
    GaussianKeywords gk = molecule.getRouteSection();
    return gk.getOptions();
  }

  public String getTitleSection(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return "";
    }
    GaussianMolecule molecule = Molecules.get(step);
    List buff = molecule.getTitleSection();
    String title = "";
    for (int i = 0; i < buff.size(); i++) {
      title += (String) buff.get(i);
      if (!title.endsWith("\n")) {
        title += "\n";
      }
    }
    return title;
  }

  public String getChargeSection(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return "";
    }
    GaussianMolecule molecule = Molecules.get(step);
    List buff = molecule.getChargeSection();
    String title = "";
    for (int i = 0; i < buff.size(); i++) {
      title += (String) buff.get(i);
      //if ( !title.endsWith("\n") ) title += "\n";
    }
    return title;
  }

  public String getMoleculeSpecsSection(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return "";
    }
    GaussianMolecule molecule = Molecules.get(step);
    /*
     ArrayList buff = molecule.getMoleculeSpecsSection();
     String title = "";
     for (int i = 0; i < buff.size(); i++) {
     title += (String) buff.get(i);
     if (!title.endsWith("\n")) {
     title += "\n";
     }
     }
     */
    //return title;
    return molecule.getMoleculeSpecsSectionAsString();
  }

  public List getMoleculeSpecs(int step) {
    if (step < 0 || step >= Molecules.size()) {
      return null;
    }
    GaussianMolecule molecule = Molecules.get(step);
    /*
     ArrayList buff = molecule.getMoleculeSpecsSection();
     String title = "";
     for (int i = 0; i < buff.size(); i++) {
     title += (String) buff.get(i);
     if (!title.endsWith("\n")) {
     title += "\n";
     }
     }
     */
    //return title;
    return molecule.getMoleculeSpecsSection();
  }

  public void removeAllEntries() {
    Molecules.clear();
  }

  public List<GaussianAtom> validateMolecularGeometry(String molSpecs) throws
      Exception {
    List atoms = null;
    try {
      GaussianMolecule gmol = new GaussianMolecule();
      atoms = gmol.parseMoleculeGeometry(molSpecs);
    } catch (Exception ex) {
      throw new Exception("Validating Molecular Geometry: " + ex.getMessage());
    }

    if (atoms.size() > 0 && renderer != null) {
      MoleculeInterface m = new Molecule();
      for (int i = 0; i < atoms.size(); i++) {
        AtomInterface atom = m.getNewAtomInstance();
        GaussianAtom a = (GaussianAtom) atoms.get(i);
        atom.setAtomicNumber(a.element);
        atom.setName(a.name);
        atom.setX(a.xyz[0]);
        atom.setY(a.xyz[1]);
        atom.setZ(a.xyz[2]);
        atom.setSubstructureNumber(0);
        m.addAtom(atom);

      }
      Molecule.guessCovalentBonds(m);
      renderer.renderMolecule(m);
    }
    return atoms;
  }

  /**
   * Returns molecule specifications (coordinates) as a String
   *
   * @param molecule MoleculeInterface
   * @return String
   * @throws Exception
   */
  public static String getMoleculeSpecsAsString(MoleculeInterface molecule) throws
      Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    if (molecule == null || molecule.getNumberOfAtoms() == 0) {
      return "";
    }
    try {
      writeMoleculeSpecs(molecule, out);
    } catch (Exception ex) {
      throw ex;
    }
    return out.toString();
  }

  /**
   * Writes coordinates of atoms into output stream
   *
   * @param molecule MoleculeInterface
   * @param out OutputStream
   * @throws Exception
   */
  public static void writeMoleculeSpecs(MoleculeInterface molecule, OutputStream out) throws
      Exception {

    try {
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface atom = molecule.getAtomInterface(i);
        /*
         String name = atom.getName();
         String elSymbol = ChemicalElements.getElementSymbol( atom.getAtomicNumber() );
         String theRest = "";
         if ( name.length() > elSymbol.length() ) {
         theRest = name.substring(elSymbol.length());
         }
        
         if ( !theRest.matches("\\d.") ) {
         name = name.substring(0,elSymbol.length());
         }
         */

        String aName = atom.getName();
        int element = atom.getAtomicNumber();
        String symbol = ChemicalElements.getElementSymbol(element);

        String element_label = String.valueOf(element);

        if (symbol.length() == 2) {
          if (aName.startsWith(symbol)) {
            element_label = aName;
          } else {
            element_label = symbol;
          }
        } else if (aName.length() > 1) {
          int el = ChemicalElements.getAtomicNumber(aName.substring(0, 2));
          if (el == 0) {
            element_label = aName;
          }
        } else {
          int el = ChemicalElements.getAtomicNumber(aName);
          if (el == element) {
            element_label = aName;
          }
        }

        // --- Process dummy atom
        if (element == 0 && element_label.toUpperCase().startsWith("DU")) {
          if (element_label.length() == 2) {
            element_label = "X";
          } else {
            element_label = "X" + element_label.substring(2);
          }
        }

        String s = String.format("%6s %10.6f  %10.6f  %10.6f\n",
            element_label, atom.getX(),
            atom.getY(), atom.getZ());
        out.write(s.getBytes());
        //out.write( (atom.getAtomicNumber() + " ").getBytes());

        //out.write( (" " + atom.getX() + " " + atom.getY() + " " + atom.getZ()).
        //          getBytes());
        //out.write( ("\n").getBytes());
      }

      out.write(("\n").getBytes());
    } catch (IOException e) {
      System.err.println("writeCartesianCoordinates: Error: " + e.getMessage());
      throw e;
    }

  }

  /**
   * Defines element of an atom
   *
   * @param aname String - Atom label 9name)
   * @return int - Element (0 for dummy atom), -1 for "Tv"
   * @throws Exception
   */
  public static int getAtomNumber(String aname) throws Exception {

    if (aname == null || aname.length() < 1) {
      throw new IllegalArgumentException("Atom name is empty");
    }

    if (aname.indexOf("-") != -1) {
      aname = aname.substring(0, aname.indexOf("-"));
    }

    if (aname.equalsIgnoreCase("Tv")) {
      return -1;
    }

    // --- remove leading zeros (if any)
    while (aname.length() > 0 && aname.startsWith("0")) {
      aname = aname.substring(1);
    }

    if (aname.length() < 1) {
      return 0;
    }

    int element = 0;
    try { // --- Is it number?
      element = Integer.parseInt(aname);
      if (element < 0 || element > ChemicalElements.getNumberOfElements()) {
        throw new Exception("Wrong element: " + element);
      }
      return element;
    } catch (NumberFormatException e) {
      // Do nothing
    }

    // --- Parse non-numeric atom label of two letters
    if (aname.length() > 1) {
      try {
        element = ChemicalElements.checkAtomicSymbol(aname.substring(0, 2));
        return element;
      } catch (Exception ex) {
        //throw ex; --- Do nothing
      }
    }

    // --- Parse non-numeric atom label of one letter
    try {
      element = ChemicalElements.checkAtomicSymbol(aname.substring(0, 1));
      return element;
    } catch (Exception ex) {
      throw ex;
    }
  }

  public static void main(String[] args) {
    Gaussian gamess = new Gaussian();

    try {
      gamess.parseGJF(args[0], 0);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }
    List ms = gamess.getMoleculeSpecs(0);
    logger.info(ms.toString());
    /*
     Set set = controls.entrySet();
     Iterator iter = set.iterator();
     while (iter.hasNext()) {
     Map.Entry me = (Map.Entry) iter.next();
     logger.info(me.getKey().toString() + "=" +
     me.getValue().toString());
     }
     */
  }

  public void validateLink0Section(String link0Specs) throws Exception {
    boolean waitingOption = true;
    boolean waitingNextOption = false;
    boolean waitingValue = false;
    boolean variableNumber = false;
    int nValues = 0;
    String Errors = "";
    String option = "";

    StringTokenizer st = new StringTokenizer(link0Specs, " =\t\n");
    while (st.hasMoreTokens()) {

      String token = st.nextToken();

      if (waitingNextOption) {
        if (token.startsWith("%")) {
          waitingOption = true;
          waitingNextOption = false;
        } else {
          continue;
        }
      }

      if (variableNumber && waitingValue && token.startsWith("%")) {
        waitingOption = true;
        waitingValue = false;
        variableNumber = false;
      }

      // --- Parsing option
      if (waitingOption) {
        option = token.toUpperCase();
        if (!link0Commands.containsKey(option)) {
          waitingOption = false;
          waitingNextOption = true;
          Errors += "Unknown Link0 option: " + option + "\n";
          continue;
        }

        Integer n = link0Commands.get(option);
        if (n == 0) {
          continue;
        }

        waitingOption = false;
        waitingValue = true;
        nValues = n > 0 ? n : -n;
        variableNumber = n < 0;
        continue;
      }

      // --- Parsing value
      if (waitingValue) {
        if (option.equals(link0_MEM)) {
          try {
            parseLink0MemValue(token);
          } catch (Exception ex) {
            Errors += "Wrong %Mem value: " + token + " : "
                + ex.getMessage() + "\n";
          }
        } else if (option.equals(link0_NPROCSHARED)
            || option.equals(link0_NPROC)
            || option.equals(link0_NPROCLINDA)) {
          int n = 0;
          try {
            n = Integer.parseInt(token);
            if (n < 1) {
              Errors += "Wrong value for " + option + " : " + token
                  + " : should be > 0\n";
            }
          } catch (Exception ex) {
            Errors += "Wrong value for " + option + " : " + token + " : "
                + ex.getMessage() + "\n";
          }

        }

        --nValues;
        if (nValues == 0) {
          waitingOption = true;
          waitingValue = false;
        }
      }
    }

    if (Errors.length() > 0) {
      throw new Exception(Errors);
    }
  }

  /**
   * Returns number in bytes
   *
   * @param value String
   * @return long
   * @throws Exception
   */
  static public long parseLink0MemValue(String value) throws Exception {
    // %Mem=N
    // Sets the amount of dynamic memory used to N words (8N bytes).
    // The default is 6MW. N may be optionally followed by a units designation: KB, MB, GB, KW, MB or GW.

    // long - max 9223372036854775807, inclusive
    String Value = value.toUpperCase();
    String number;
    long factor = 0;

    if (Value.endsWith("KB")) {
      factor = 1024L;
      number = value.substring(0, value.length() - 2);
    } else if (Value.endsWith("MW")) {
      factor = 8L * 1024L * 1024L;
      number = value.substring(0, value.length() - 2);
    } else if (Value.endsWith("MB")) {
      factor = 1024L * 1024L;
      number = value.substring(0, value.length() - 2);
    } else if (Value.endsWith("GB")) {
      factor = 1024L * 1024L * 1024L;
      number = value.substring(0, value.length() - 2);
    } else if (Value.endsWith("KW")) {
      factor = 8L * 1024L;
      number = value.substring(0, value.length() - 2);
    } else if (Value.endsWith("GW")) {
      factor = 8L * 1024L * 1024L * 1024L;
      number = value.substring(0, value.length() - 2);
    } else { // in MW
      factor = 8L * 1024L * 1024L;
      number = value;
    }

    long valueInBytes = 0;

    try {
      valueInBytes = Long.parseLong(number);
    } catch (Exception ex) {
      throw ex;
    }

    if (valueInBytes <= 0) {
      throw new Exception("Value should be positive");
    }

    return valueInBytes * factor;
  }

  /**
   * Returns the maximum number of processors from all molecules
   *
   * @return int
   */
  public int getNumberOfProcessors() {
    int nprocs = 1;
    for (int i = 0; i < Molecules.size(); i++) {
      GaussianMolecule mol = Molecules.get(i);
      nprocs = Math.max(nprocs, mol.getNumberOfProcessors());
    }
    return nprocs;
  }

  /**
   * Returns memory in KB
   *
   * @return int
   */
  public int getMemory() {
    int memory = 0;
    for (int i = 0; i < Molecules.size(); i++) {
      GaussianMolecule mol = Molecules.get(i);
      if (i == 0) {
        memory = mol.getMemory();
        continue;
      }

      memory = Math.max(memory, mol.getMemory());
    }
    return memory;
  }

  public static void saveGJFUsingTemplate(MoleculeInterface mol, String template, String fileName) throws Exception {
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      throw new Exception("Molecule has no atoms");
    }
    if (template == null || template.trim().length() < 1) {
      throw new Exception("Template is empty");
    }
    BufferedWriter bw;
    try {
      bw = new BufferedWriter(new FileWriter(fileName));
    } catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " for writing: " + ex.getLocalizedMessage());
    }
    saveGJFUsingTemplate(mol, template, bw);
    bw.close();
  }

  public static void saveGJFUsingTemplate(MoleculeInterface mol, String template, Writer writer) throws Exception {
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      throw new Exception("Molecule has no atoms");
    }
    if (template == null || template.trim().length() < 1) {
      throw new Exception("Template is empty");
    }

    if (template.contains(TITLE_TEMPLATE)) {
      String title = "Some molecule";
      if (mol.getName() != null && mol.getName().trim().length() > 0) {
        title = mol.getName().trim();
      }
      template = template.replaceAll(TITLE_TEMPLATE, title);
    }

    if (template.contains(CHARGE_TEMPLATE)) {
      double charge = 0;
      Object obj = mol.getProperty(MoleculeInterface.ChargeProperty);
      if (obj != null && obj instanceof Number) {
        charge = ((Number) obj).doubleValue();
        double ch = (double) ((int) charge);
        if (Math.abs(ch - charge) > 0.01) {
          logger.warning("Total charge is a bit far from integer number: " + charge);
        }
        if (charge != ch) {
          if (charge - ch > 0.5) {
            ch += 1;
          } else if (charge - ch < -0.5) {
            ch -= 1;
          }
          //ch = charge > ch ? ch + 1 : ch -1;
        }
        System.out.println("Sum of partial charges: " + charge + " Attributed xharge: " + ((int) ch));
        template = template.replaceAll(CHARGE_TEMPLATE, String.valueOf((int) ch));
      } else {
        logger.warning("Total charge is not defined or is not a number: " + (obj == null ? obj : obj.toString()));
      }
    }

    if (!template.contains(COORDINATES_TEMPLATE)) {
      throw new Exception("GJF Template must contain " + COORDINATES_TEMPLATE);
    }
    StringBuilder sb = new StringBuilder(mol.getNumberOfAtoms() * 40);
    boolean yesFixed = false;
    String fragment;
    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      fragment = "";
      AtomInterface atom = mol.getAtomInterface(i);
      if ((!yesFixed) && atom.getProperty(ATOM_FIXED_PROPERTY) != null) {
        yesFixed = true;
      }
      Object obj = atom.getProperty(FRAGMENT_PROPERTY);
      if (obj != null && obj instanceof Number) {
        int frag = ((Number) obj).intValue();
        fragment = "(Fragment=" + String.valueOf(frag) + ")";
      }
      sb.append(atom.getAtomicNumber() + fragment + " "
          + String.format("%10.4f %10.4f %10.4f", atom.getX(), atom.getY(), atom.getZ()) + "\n");
    }

    if (yesFixed) {
      sb.append("\n");
      for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
        AtomInterface atom = mol.getAtomInterface(i);
        if (atom.getProperty(ATOM_FIXED_PROPERTY) != null) {
          sb.append("X " + String.valueOf(i + 1) + " F\n");
        }
      }
    }

    template = template.replaceAll(COORDINATES_TEMPLATE, sb.toString());

    writer.write(template);
  }

  public CommandInterface ciInstance() {
    return new Gaussian();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    String fileName = null;
    if (args[0] instanceof Variable) {
      fileName = ((Variable) args[0]).getValue().toString();
    } else {
      fileName = args[0].toString();
    }
    int n = parseGJF(fileName, 0);
    int sel = 1;
    if (args.length > 1) {
      sel = ((Integer) args[1]).intValue();
    }
    if (sel > n) {
      throw new Exception("Load Gaussian input command: input file contains " + n
          + " structures. Requested: " + sel);
    }
    MoleculeInterface m = Molecule.getNewInstance();
    m = getMolecule(m, sel - 1);
    if (m == null || m.getNumberOfAtoms() < 1) {
      throw new Exception("Load Gaussian input command: Didn't find atoms in file");
    }
    Molecule.guessCovalentBonds(m);
    Molecule.guessAtomTypes(m, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
    return m;
  }

  /**
   * Parses command parameters.
   *
   * @param tokens - arguments. The first argument is a command name, so it's ignored
   * @return parsed and parameters
   * @throws Exception
   */
  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens.length < 2) {
      throw new Exception("Load Gaussian input command requires at least one argument (file name)");
    }

    if (tokens[1] == null) {
      throw new Exception("Load Gaussian input command: file name is empty");
    }
    if (tokens.length >= 2) {
      File file = new File(tokens[1]);
      if (!file.exists()) {
        throw new Exception("Load Gaussian input command: file " + tokens[1] + " does not exist");
      }
    }

    Object[] parsedArgs = new Object[tokens.length > 2 ? 2 : 1];
    parsedArgs[0] = tokens[1];

    // ---
    if (tokens.length > 2) {
      Integer n = null;
      try {
        n = Integer.parseInt(tokens[2]);
      } catch (Exception ex) {
        throw new Exception("Load Gaussian input command: expected a structure number as the second argument, got: "
            + tokens[2]);
      }
      if (n < 1) {
        throw new Exception("Load Gaussian input command: the second argument should be > 0, got: "
            + tokens[2]);
      }
      parsedArgs[1] = n;
    }

    return parsedArgs;
  }
}
