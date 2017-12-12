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

import static cct.gaussian.Gaussian.logger;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.Geometry3d;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
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
enum Link0_Commands {

  MEM, CHK, RWF, INT, D2E, NPROC, KJOB, NPROCSHARED, SAVE, NOSAVE, SUBST, NPROCLINDA, LINDAWORKERS
}

public class GaussianMolecule
        extends ArrayList {

  static boolean Debug = true;
  public static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);
  int jobType = GaussianKeywords.JOB_UNKNOWN;
  int Charge = 0, Multiplicity = 1;
  int[] chargeAndMultiplicity = null;
  List LinkZeroCommands = new ArrayList();
  Map<Link0_Commands, Object> LinkZeroObjects = new HashMap<Link0_Commands, Object>();
  //ArrayList routeSection = new ArrayList();
  GaussianKeywords routeSection = new GaussianKeywords();
  List titleSection = new ArrayList();
  List chargeSection = new ArrayList();
  List moleculeSpecsSection = new ArrayList();
  //String moleculeSpecsSection = "";
  List cartesians = null;
  private double distanceUnits = 1.0;
  public Map Parameters = new HashMap();
  static final Logger logger = Logger.getLogger(GaussianMolecule.class.getCanonicalName());
  private static String ATOM_TYPE_REGEX = "^[a-zA-Z0-9]{1,2}[(].*[)].*";
  private static Pattern ATOM_TYPE_MATCHER;

  static {
    try {
      ATOM_TYPE_MATCHER = Pattern.compile(ATOM_TYPE_REGEX);
    } catch (Exception ex) {
      logger.severe("Wrong " + ATOM_TYPE_REGEX + " " + ex.getMessage() + " Ignoring & continuing...");
    }
  }

  public GaussianMolecule() {
    //if (ATOM_TYPE_MATCHER == null) {
    //  try {
    //    ATOM_TYPE_MATCHER = Pattern.compile(ATOM_TYPE_REGEX);
    //  } catch (Exception ex) {
    //   logger.severe("Wrong " + ATOM_TYPE_REGEX + " " + ex.getMessage() + " Ignoring & continuing...");
    // }
    //}
  }
  
   static public void setLoggerLevel(Level level) {
    logger.setLevel(level);
    GaussianKeywords.logger.setLevel(level);
  }

  public void addRouteOptions(String opt) {
    routeSection.addOptions(opt);
  }

  public void parseRouteOptions() {
    routeSection.parseOptions();
    distanceUnits = routeSection.getDistanceUnits();
  }

  public void addLinkZeroCommands(String command) throws Exception {
    parseLink0Command(command);
    LinkZeroCommands.add(command);
  }

  public void parseLink0Command(String command) throws Exception {
    command = command.trim();
    if (!command.startsWith("%")) {
      throw new Exception("Link0 command should start with %, got " + command);
    }

    StringTokenizer st = new StringTokenizer(command, "= ,", false);

    String token = st.nextToken();

    if (token.length() < 2) {
      throw new Exception("Too short Link0 command: " + command);
    }

    Link0_Commands link0 = getLinkZeroCommandbject(token.substring(1));

    //MEM, CHK, RWF, INT, D2E, NPROC, KJOB, NPROCSHARED, SAVE, NOSAVE, SUBST, NPROCLINDA, LINDAWORKERS;
    switch (link0) {
      case MEM:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting memory value for %" + link0.toString() + " command");
        }
        token = st.nextToken();
        MemoryObject memory = null;
        try {
          memory = new MemoryObject(token);
        } catch (Exception ex) {
          throw new Exception("Cannot parse memory value: " + ex.getMessage());
        }

        LinkZeroObjects.put(link0, memory);
        break;

      case CHK:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting checkfile file name");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case RWF:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting a single, unified Read-Write file name");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case INT:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting the two-electron integral file name(s) in %" + link0.toString()
                  + " command");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case D2E:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting the two-electron integral derivative file name(s) in %" + link0.toString()
                  + " command");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case NPROC:
      case NPROCSHARED:
      case NPROCLINDA:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting number of processors in %" + link0.toString()
                  + " command");
        }

        Integer nproc = 0;
        try {
          nproc = Integer.parseInt(st.nextToken());
        } catch (Exception ex) {
          throw new Exception("Cannot parse number of processors in %" + link0.toString()
                  + " command, got " + command);
        }

        LinkZeroObjects.put(link0, nproc);
        break;

      case KJOB:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting link number in %" + link0.toString()
                  + " command");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case SAVE:
      case NOSAVE:
        LinkZeroObjects.put(link0, null);
        break;

      case SUBST:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting a link from an alternate directory in %" + link0.toString()
                  + " command");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;

      case LINDAWORKERS:
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting the list of the TCP node nams in %" + link0.toString()
                  + " command");
        }
        token = st.nextToken();

        LinkZeroObjects.put(link0, token);
        break;
    }
  }

  /**
   * Returns memory in KB. The default is 6MW
   *
   * @return int
   */
  public int getMemory() {
    if (!LinkZeroObjects.containsKey(Link0_Commands.MEM)) {
      return 6 * 8 * 1024;
    }

    MemoryObject memory = (MemoryObject) LinkZeroObjects.get(Link0_Commands.MEM);
    return memory.memoryInKB;
  }

  public int getNumberOfProcessors() {
    if (!LinkZeroObjects.containsKey(Link0_Commands.NPROC)
            && !LinkZeroObjects.containsKey(Link0_Commands.NPROCSHARED)
            && !LinkZeroObjects.containsKey(Link0_Commands.NPROCLINDA)) {
      return 1;
    }

    Integer nproc = (Integer) LinkZeroObjects.get(Link0_Commands.NPROC);
    if (nproc == null) {
      nproc = 1;
    }
    Integer nproc_shared = (Integer) LinkZeroObjects.get(Link0_Commands.NPROCSHARED);
    if (nproc_shared == null) {
      nproc_shared = 1;
    }
    Integer nproc_linda = (Integer) LinkZeroObjects.get(Link0_Commands.NPROCLINDA);
    if (nproc_linda == null) {
      nproc_linda = 1;
    }

    int nprocs = 1;
    nprocs = Math.max(nproc, nproc_shared);
    nprocs = Math.max(nprocs, nproc_linda);

    return nprocs;
  }

  /**
   * Command should be without initial "%"
   *
   * @param command String
   * @return Link0_Commands
   * @throws Exception
   */
  Link0_Commands getLinkZeroCommandbject(String command) throws Exception {
    for (Link0_Commands c : Link0_Commands.values()) {
      if (command.equalsIgnoreCase(c.toString())) {
        return c;
      }
    }
    throw new Exception("No such Link0 command: " + command);
  }

  public List getLinkZeroCommands() {
    return LinkZeroCommands;
  }

  public List getTitleSection() {
    return titleSection;
  }

  public List getChargeSection() {
    return chargeSection;
  }

  public void setTotalCharge(int charge) {
    Charge = charge;
  }

  public int[] getChargeAndMultiplicity() {
    return this.chargeAndMultiplicity;
  }

  public void setTotalMultiplicity(int mult) throws Exception {
    if (mult < 1) {
      throw new Exception("Wrong multiplicity : " + mult
              + " Should be positive");
    }
    Multiplicity = mult;
  }

  public void setChargeAndMultiplicity(int[] ch) throws Exception {
    if (ch.length < 1 || (ch.length % 2) != 0) {
      throw new Exception(
              "Charge section does not consists of CHARGE MULTIPLICITY pair(s): "
              + ch.length);
    }

    chargeAndMultiplicity = ch;
    Charge = ch[0];
    Multiplicity = ch[1];
  }

//  public ArrayList getMoleculeSpecsSection() {
//    return moleculeSpecsSection;
//  }
  public List getMoleculeSpecsSection() {
    return moleculeSpecsSection;
  }

  public MoleculeInterface getMolecule(MoleculeInterface mol) {
    int natoms = size();
    if (natoms < 1) {
      return mol;
    }
    mol.addMonomer("Gaussian");

    if (cartesians != null) {
      for (int i = 0; i < size(); i++) {
        float xyz[];
        AtomInterface atom = mol.getNewAtomInstance();
        GaussianAtom a = getAtom(i);
        atom.setAtomicNumber(a.element);
        atom.setName(a.name);
        xyz = (float[]) cartesians.get(i);
        atom.setX(xyz[0]);
        atom.setY(xyz[1]);
        atom.setZ(xyz[2]);
        mol.addAtom(atom);
      }
    } else {
      for (int i = 0; i < size(); i++) {
        AtomInterface atom = mol.getNewAtomInstance();
        GaussianAtom a = getAtom(i);
        atom.setAtomicNumber(a.element);
        atom.setName(a.name);
        atom.setX(a.xyz[0]);
        atom.setY(a.xyz[1]);
        atom.setZ(a.xyz[2]);
        mol.addAtom(atom);
      }
    }

    List props = null;

    // --- Add (if any) link zero commands

    props = getLinkZeroCommands();
    if (props != null) {
      for (int i = 0; i < props.size(); i++) {
        mol.addProperty(Gaussian.LINK_ZERO_COMMANDS_KEY, props.get(i).toString());
      }
    }

    // --- Add route section

    GaussianKeywords gk = getRouteSection();
    if (gk != null && gk.getOptions() != null) {
      mol.addProperty(Gaussian.ROUTE_COMMANDS_KEY, gk.getOptions());
    }

    // --- Get title

    props = getTitleSection();
    if (props != null) {
      String title = "";
      for (int i = 0; i < props.size(); i++) {
        title += props.get(i).toString() + " ";
      }
      mol.setName(title);
    }

    // --- Get charge and multiplicity

    int[] ch = getChargeAndMultiplicity();
    if (ch != null) {
      mol.addProperty(MoleculeInterface.ChargeProperty, new Integer(ch[0]));
      mol.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(ch[1]));
    }

    // --- Molecular specifications

    mol.addProperty(Gaussian.NUMBER_OF_ATOMS_KEY, new Integer(natoms));
    props = getMoleculeSpecsSection();
    for (int i = 0; i < props.size(); i++) {
      String line = props.get(i).toString();
      mol.addProperty(Gaussian.MOLEC_SPECS_KEY, line);
    }

    return mol;
  }

  public String getMoleculeSpecsSectionAsString() {
    if (moleculeSpecsSection == null || moleculeSpecsSection.size() < 1) {
      return null;
    }
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < moleculeSpecsSection.size(); i++) {
      buffer.append(moleculeSpecsSection.get(i).toString() + "\n");
    }
    return buffer.toString();
  }

  public List getCartesians() {
    return cartesians;
  }

  public GaussianKeywords getRouteSection() {
    return routeSection;
  }

  void addMoleculeSpecsItem(String item) {
    //if (item.length() == 0 || !item.endsWith("\n")) {
    //   item = item.concat("\n");
    // }
    //moleculeSpecsSection += item;
    moleculeSpecsSection.add(item);
  }

  public void addAtom(GaussianAtom a) {
    add(a);
  }

  public GaussianAtom getAtom(int n) {
    if (n < 0 || n >= size()) {
      return null;
    }
    return (GaussianAtom) get(n);
  }

  public boolean writeLinkZeroCommands(FileOutputStream out) {
    for (int i = 0; i < LinkZeroCommands.size(); i++) {
      String line = (String) LinkZeroCommands.get(i);
      try {
        out.write((line + "\n").getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Link Zero Commands");
        return false;
      }
    }

    // --- Final empty line

    if (LinkZeroCommands.size() > 0) {
      try {
        out.write("\n".getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Link Zero Commands");
        return false;
      }

    }
    return true;
  }

  public boolean writeRouteSection(FileOutputStream out) {
    String line;
    try {
      // --- First line
      /*
       * out.write(("#p ").getBytes()); line = routeSection.getMethod(); if (line != null) { out.write((line + " ").getBytes()); }
       * line = routeSection.getJobType(); if (line != null) { out.write((line + " ").getBytes()); } line =
       * routeSection.getBasisSet(); if (line != null) { out.write((line + " ").getBytes()); } line = routeSection.getOptions(); if
       * (line != null) { out.write((line + " ").getBytes()); } out.write(("\n").getBytes());
       */
      // --- Second line (if any)
      line = routeSection.getOptions();
      if (line != null) {
        out.write((line + "\n").getBytes());
      }
      // --- Empty line
      out.write(("\n").getBytes());
    } catch (IOException e) {
      System.err.println("saveGJF: Error writing Route Section");
      return false;
    }

    return true;
  }

  public boolean writeTitleSection(FileOutputStream out) {
    for (int i = 0; i < titleSection.size(); i++) {
      String line = (String) titleSection.get(i);
      try {
        out.write((line + "\n").getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Title Section");
        return false;
      }
    }

    // --- Final empty line

    if (titleSection.size() > 0) {
      try {
        out.write("\n".getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Title Section");
        return false;
      }

    }
    return true;
  }

  public boolean writeChargeSection(FileOutputStream out) {
    for (int i = 0; i < chargeSection.size(); i++) {
      String line = (String) chargeSection.get(i);
      try {
        out.write((line + " ").getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Charge Section");
        return false;
      }
    }

    try {
      out.write("\n".getBytes());
    } catch (IOException e) {
      System.err.println("saveGJF: Error writing Charge Section");
      return false;
    }

    return true;
  }

  public boolean writeAtoms(FileOutputStream out) {
    for (int i = 0; i < this.size(); i++) {
      GaussianAtom atom = this.getAtom(i);
      try {
        out.write((atom.getElement() + " ").getBytes());
        out.write((" " + atom.getX() + " " + atom.getY() + " " + atom.getZ()).getBytes());
        out.write(("\n").getBytes());
      } catch (IOException e) {
        System.err.println("saveGJF: Error writing Atoms Section");
        return false;
      }
    }

    try {
      out.write(("\n").getBytes());
    } catch (IOException e) {
      System.err.println("saveGJF: Error writing Atoms Section");
      return false;
    }

    return true;
  }

  public int[] parseChargeSection(String line) throws Exception {
    int[] ch_and_mult = null;

    if (line == null) {
      throw new Exception(
              "Charge section has zero length");
    }

    StringTokenizer st = new StringTokenizer(line, " ,", false);
    int nTokens = st.countTokens();
    if (nTokens < 1 || (nTokens % 2) != 0) {
      throw new Exception(
              "Charge section does not consists of CHARGE MULTIPLICITY pair(s)");
    }

    ch_and_mult = new int[nTokens];

    // --- parse the first pair

    String ch = "";
    /*
     * try { ch = st.nextToken(); Charge = Integer.parseInt(ch); ch_and_mult[0] = Charge; } catch (Exception ex) { throw new
     * Exception("Error while parsing the total system charge: " + ch + " : " + ex.getMessage()); }
     *
     * try { ch = st.nextToken(); Multiplicity = Integer.parseInt(ch); ch_and_mult[1] = Multiplicity; } catch (Exception ex) { throw
     * new Exception( "Error while parsing the total system multiplicity: " + ch + " : " + ex.getMessage()); }
     */

    // --- parse the rest

    for (int i = 0; i < nTokens; i++) {
      try {
        ch = st.nextToken();
        ch_and_mult[i] = Integer.parseInt(ch);
      } catch (Exception ex) {
        throw new Exception(
                "Error while parsing the charge or multiplicity: "
                + ch + " : " + ex.getMessage());
      }
    }

    return ch_and_mult;
  }

  /**
   *
   * @return ArrayList
   * @throws IOException
   */
  public List parseMoleculeGeometry() throws Exception {

    String specs = this.getMoleculeSpecsSectionAsString();

    BufferedReader in = new BufferedReader(new StringReader(specs));
    //moleculeSpecsSection));
    List atoms = null;
    try {
      atoms = parseMoleculeGeometry(in);
      if (Debug) {
        logger.info("Atoms: " + atoms.size());
      }
      this.clear();
      this.addAll(atoms);
    } catch (IOException ex) {
      throw ex;
    }
    return atoms;
  }

  /**
   *
   * @param stringBuffer String
   * @return ArrayList
   * @throws IOException
   */
  public List parseMoleculeGeometry(String stringBuffer) throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(stringBuffer));
    List atoms = null;
    try {
      atoms = parseMoleculeGeometry(in);
    } catch (IOException ex) {
      throw ex;
    }
    return atoms;
  }

  /**
   *
   * @param in BufferedReader
   * @return ArrayList
   */
  public List parseMoleculeGeometry(BufferedReader in) throws Exception {
    List atoms = new ArrayList();
    String line;
    String message = "";
    boolean z_matrix = false;
    String atomName = null;

    try {

      // Read molecule
      int natoms = 0;
      z_matrix = false;
      String token = null;
      StringTokenizer st = null;
      String tokens[] = new String[10];
      GaussianAtom a;
      Map parameters = new HashMap();
      Map<String, Integer> atref = new HashMap<String, Integer>();
      boolean connectivityParam = false;
      Matcher m = null;
      //Atoms = new AtomParamArray();

      while ((line = in.readLine()) != null) {
        if (Debug) {
          logger.info("Parsing: " + line);
        }
        //Atoms.addMoleculeSpecsItem(line);
        line = line.trim().toUpperCase();
        //if (Debug) {
        //   logger.info("Trimmed line: " + line);
        //}
        if (line.length() == 0 || line.startsWith("VARIABLES:")) {
          break;
        }
        resetTokens(tokens);
        //Tokens = line.split(" ,", Tokens.length);
        //Tokens = line.split(",");
        boolean complex_atom_type = false;
        if (ATOM_TYPE_MATCHER != null) {
          try {
            m = ATOM_TYPE_MATCHER.matcher(line);
            complex_atom_type = m.matches();
          } catch (Exception ex) {
            logger.severe("Error while matching a string: " + line + " : " + ex.getMessage() + " Ignoring & continuing...");
          }
        }

        if (complex_atom_type) {
          int index = line.indexOf(")") + 1;
          atomName = line.substring(0, index);
          line = line.substring(index);
          st = new StringTokenizer(line, " ,");
        } else {
          st = new StringTokenizer(line, " ,");
          atomName = st.nextToken();
        }


        ++natoms;

        a = new GaussianAtom();

        // ---- Parse Atom Number

        try {
          a.parseAtomName(atomName, complex_atom_type);
        } catch (Exception ex) {
          message += ex.getMessage() + "\n";
        }
        atref.put(a.name, natoms);
        /*
         * a.name = st.nextToken(); a.element = ChemicalElements.getAtomicNumber(a.name);
         */
        if (Debug) {
          logger.info("   Atom: " + natoms + " " + a.name + " " + a.element + "\n");
        }

        // --- Start to parse atom parameters

        //int number_of_tokens = numberOfTokens();
        int number_of_tokens = st.countTokens();
        if (Debug) {
          logger.info("   Number of tokens: " + (number_of_tokens + 1) + "\n");
        }

        // --- Parse 1st Atom

        if (natoms == 1 && number_of_tokens >= 3) { // It looks like it's Cartesian
          try {
            parseCartesianCoord(a, st);
          } catch (Exception ex) {
            throw new Exception("Cannot parse line " + line);
          }
        } else if (natoms == 1 && number_of_tokens == 0) { // It looks like it's Z-matrix
          z_matrix = true;

        } // --- Parse 2nd Atom
        else if (natoms == 2 && number_of_tokens >= 3 && (!z_matrix)) { // Cartesian
          try {
            parseCartesianCoord(a, st);
          } catch (Exception ex) {
            throw new Exception("Cannot parse line " + line);
          }
        } else if (natoms == 2 && number_of_tokens == 2) { // It looks like it's Z-matrix
          // Get i
          token = st.nextToken();
          try {
            a.ijk[0] = Integer.parseInt(token);
          } catch (NumberFormatException e) {
            // It's a parameter
            a.parijk[0] = token;
            connectivityParam = true;
          }

          // Get bond length
          token = st.nextToken();
          try {
            a.xyz[0] = Float.parseFloat(token) * (float) distanceUnits;
          } catch (NumberFormatException e) {
            a.parxyz[0] = token;
          }

          z_matrix = true;

        } // --- Parse 3d Atom
        else if (natoms == 3 && number_of_tokens >= 3 && (!z_matrix)) { // Cartesian
          try {
            parseCartesianCoord(a, st);
          } catch (Exception ex) {
            throw new Exception("Cannot parse line " + line);
          }
        } else if (natoms == 3 && number_of_tokens >= 4 && z_matrix) { // It looks like it's Z-matrix

          for (int i = 0; i < 2; i++) {
            // --- Parse i & j
            token = st.nextToken();
            try {
              a.ijk[i] = Integer.parseInt(token);
            } catch (NumberFormatException e) {
              a.parijk[i] = token;
              connectivityParam = true;
            }

            // Get bond length and angle
            token = st.nextToken();
            try {
              a.xyz[i] = Float.parseFloat(token);
              if (i == 0) {
                a.xyz[i] *= (float) distanceUnits;
              }
            } catch (NumberFormatException e) {
              a.parxyz[i] = token;
            }
          }

          z_matrix = true;
        } // --- Parse atoms > 3
        else if (natoms > 3 && number_of_tokens >= 3 && (!z_matrix)) { // Cartesian
          try {
            parseCartesianCoord(a, st);
          } catch (Exception ex) {
            throw new Exception("Cannot parse line " + line);
          }
        } else if (natoms > 3 && number_of_tokens >= 6 && z_matrix) { // It looks like it's Z-matrix

          for (int i = 0; i < 3; i++) {
            // --- Parse i, j, & k
            token = st.nextToken();
            try {
              a.ijk[i] = Integer.parseInt(token);
            } catch (NumberFormatException e) {
              a.parijk[i] = token;
              connectivityParam = true;
            }

            // Get bond length, angle, and dihedral
            token = st.nextToken();
            try {
              a.xyz[i] = Float.parseFloat(token);
              if (i == 0) {
                a.xyz[i] *= (float) distanceUnits;
              }
            } catch (NumberFormatException e) {
              a.parxyz[i] = token;
            }
          }

          if (st.hasMoreTokens()) { // Check for the Alternate Z-matrix Format
            token = st.nextToken();
            try {
              a.alternateZmatrixFormat = Integer.parseInt(token);
            } catch (NumberFormatException e) {
              throw new Exception("Expected an additional (integer) field for the Alternate Z-matrix Format. got " + token);
            }
          }

          z_matrix = true;
        } else {
          System.err.println("Unable to parse line: " + line + "\n");
        }

        atoms.add(a);

      } // End of while

      logger.info("Number of Atoms: " + atoms.size() + "\n");

      // -- Now we check whether geometry contain parameters to resolve to...

      int numParams = 0;
      for (int i = 0; i < atoms.size(); i++) {
        a = (GaussianAtom) atoms.get(i);
        if (a.parxyz[0] != null || a.parxyz[1] != null || a.parxyz[2] != null) {
          ++numParams;
        }
      }

      logger.info("Number of parameters to be resolved: " + numParams);

      // --- Parse parameters (if any)

      if (numParams > 0) {
        // read parameters
        while ((line = in.readLine()) != null) {
          logger.info("Parsing: " + line);
          //Atoms.addMoleculeSpecsItem(line);
          line = line.trim().toUpperCase();
          if (line.length() == 0) {
            continue; // Instead of break...
            //break;
          }
          resetTokens(tokens);
          //Tokens = line.split(" ,=", Tokens.length);
          st = new StringTokenizer(line, " ,=");
          //Atoms.Parameters.put(Tokens[0], Tokens[1]);
          if (st.countTokens() < 2) {
          } else {
            token = st.nextToken();
            parameters.put(token, st.nextToken());
          }
        }
        logger.info("Number of parameters: " + parameters.size() + "\n");
        // Assign parameters

        String param, key;
        boolean change_sign = false;
        for (int j = 0; j < parameters.size(); j++) {
          for (int i = 0; i < atoms.size(); i++) {
            a = (GaussianAtom) atoms.get(i);
            for (int k = 0; k < 3; k++) {
              change_sign = false;

              if (a.parxyz[k] != null) {
                key = a.parxyz[k];
                if (a.parxyz[k].startsWith("-")) {
                  key = a.parxyz[k].substring(1);
                  change_sign = true;
                }

                param = (String) parameters.get(key);

                try {
                  a.xyz[k] = Float.parseFloat(param);
                  if (k == 0) {
                    a.xyz[k] *= (float) distanceUnits;
                  }
                  if (change_sign) {
                    a.xyz[k] = -a.xyz[k];
                  }
                } catch (NumberFormatException e) {
                  System.err.println("Cannot convert to float value: " + param + "\n");
                }
              }
            }
          }
        }

        // Resolve connectivity paramaters, if any
        boolean errors = false;
        if (connectivityParam) {
          for (int i = 0; i < atoms.size(); i++) {
            a = (GaussianAtom) atoms.get(i);
            for (int k = 0; k < 3; k++) {

              if (a.parijk[k] != null) {
                if (!atref.containsKey(a.parijk[k])) {
                  throw new Exception("Atom " + (i + 1) + ": connectivity parameter refers to atom " + a.parijk[k] + " but no atom with such name");
                }
                a.ijk[k] = atref.get(a.parijk[k]);
              }
            }
          }
        }

        // print out atoms with resolved parameters

        if (Debug) {
          logger.info("\nAtoms with resolved parameters\n");
          for (int i = 0; i < atoms.size(); i++) {
            a = (GaussianAtom) atoms.get(i);
            System.out.print((i + 1) + " " + a.name + " " + a.element + " ");
            if (z_matrix) {
              System.out.print(a.ijk[0] + " ");
            }
            System.out.print(a.xyz[0] + " ");
            if (z_matrix) {
              System.out.print(a.ijk[1] + " ");
            }
            System.out.print(a.xyz[1] + " ");
            if (z_matrix) {
              System.out.print(a.ijk[2] + " ");
            }
            System.out.println(String.valueOf(a.xyz[2]));
          }
        }
      }

      if (atoms.size() > 0) {
        if (z_matrix) {
          List cartesians = fromInternalToCartesians(atoms);
          for (int i = 0; i < atoms.size(); i++) {
            a = (GaussianAtom) atoms.get(i);
            float xyz[] = (float[]) cartesians.get(i);
            a.xyz[0] = xyz[0];
            a.xyz[1] = xyz[1];
            a.xyz[2] = xyz[2];
          }
        }
      }
    } catch (IOException e) {
      System.err.println("parseMoleculeGeometry: " + e.getMessage());
      throw e;
    }

    if (message.length() > 0) {
      JOptionPane.showMessageDialog(null,
              "Error(s) while parsing molecule specifications\n\n"
              + message, "Error",
              JOptionPane.ERROR_MESSAGE);
    }

    return atoms;
  }

  void parseCartesianCoord(GaussianAtom atom, StringTokenizer st) throws Exception {
    if (st.countTokens() < 3) {
      throw new Exception("Expecting x,y,z coordinates. Got only " + st.countTokens() + " tokens");
    }
    String token = null;

    // --- Simple case

    if (st.countTokens() == 3) {
      for (int i = 0; i < 3; i++) {
        // Get X, Y, and Z
        token = st.nextToken();
        try {
          atom.xyz[i] = Float.parseFloat(token) * (float) distanceUnits;
        } catch (NumberFormatException e) {
          atom.parxyz[i] = token;
        }
      }
      return;
    }

    // --- Check for optimization/frozen flag

    token = st.nextToken();
    int i_start = 0;
    try {
      int flag = Integer.parseInt(token);
      if (flag != 0 && flag != -1) {
        atom.xyz[0] = flag;
        i_start = 1;
      } else {
        atom.frozen = flag == -1;
      }
    } catch (NumberFormatException e) {
      i_start = 1;
      try {
        atom.xyz[0] = Float.parseFloat(token) * (float) distanceUnits;
      } catch (NumberFormatException ex) {
        atom.parxyz[0] = token;
      }
    }

    for (int i = i_start; i < 3; i++) {
      // Get X, Y, and Z
      token = st.nextToken();
      try {
        atom.xyz[i] = Float.parseFloat(token) * (float) distanceUnits;
      } catch (NumberFormatException e) {
        atom.parxyz[i] = token;
      }
    }

    // --- Check for ONIOM options (if any)

    // atom coordinate-spec layer [link-atom [bonded-to [scale-fac1 [scale-fac2 [scale-fac3]]]]]
    if (routeSection.isONIOMCalculation()) {
      // --- Get Layer
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      try {
        atom.setONIOMLayer(token);
      } catch (Exception ex) {
        System.err.println(ex.getMessage());
      }

      // --- Get link atom
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      atom.link_atom = token;

      // --- Get bonded-to
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      try {
        atom.Bonded_to = Integer.parseInt(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse bonded-to atom number: " + token + " : " + ex.getMessage());
      }

      // --- Get scale-fac1
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      try {
        atom.scale_fac1 = Float.parseFloat(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse scale-fac1: " + token + " : " + ex.getMessage());
      }

      // --- Get scale-fac2
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      try {
        atom.scale_fac2 = Float.parseFloat(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse scale-fac2: " + token + " : " + ex.getMessage());
      }

      // --- Get scale-fac3
      if (!st.hasMoreTokens()) {
        return;
      }
      token = st.nextToken();
      try {
        atom.scale_fac3 = Float.parseFloat(token);
      } catch (Exception ex) {
        System.err.println("Cannot parse scale-fac3: " + token + " : " + ex.getMessage());
      }

    }

  }

  /**
   *
   * @param params ArrayList
   * @return ArrayList
   */
  public static List fromInternalToCartesians(List params) throws Exception {
    int i, ii, jj, kk, ll;
    int natoms = params.size();
    GaussianAtom a, a1, a2, a3;
    //Float xyz[] = new Float[3];
    float xyz[], x1[], x2[], x3[];

    double SIN2, COS2, SIN3, COS3,
            VT[] = new double[3], V1[] = new double[3],
            V2[] = new double[3];
    double R2,
            V3[] = new double[3], VA1, VB1, VC1, R3, V[] = new double[3];

    if (natoms < 1) {
      return null;
    }

    List cartesians = new ArrayList(natoms);

    // --- The first atom. Put it into the origin

    xyz = new float[3];
    xyz[0] = 0.0f;
    xyz[1] = 0.0f;
    xyz[2] = 0.0f;
    cartesians.add(xyz);

    if (natoms < 2) {
      return cartesians;
    }

    // --- ther second atom: put it along the X-axis

    a = (GaussianAtom) params.get(1);

    xyz = new float[3];
    xyz[0] = a.xyz[0]; // So, we put it along X-axis
    xyz[1] = 0.0f;
    xyz[2] = 0.0f;
    cartesians.add(xyz);

    if (natoms < 3) {
      return cartesians;
    }

    // --- Third atom (put it into the XOY plane

    a = (GaussianAtom) params.get(2);

    x1 = (float[]) cartesians.get(a.ijk[0] - 1);

    xyz = new float[3];
    xyz[2] = 0.0f; // Z-coordinate

    if (a.ijk[0] == 1) { // Connected to the first atom
      //logger.info("To the 1st");
      xyz[0] = a.xyz[0]
              * (float) Math.cos((double) (a.xyz[1] * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = a.xyz[0]
              * (float) Math.sin((double) (a.xyz[1] * DEGREES_TO_RADIANS));
    } else {
      //logger.info("To the 2nd");
      xyz[0] = x1[0]
              - a.xyz[0]
              * (float) Math.cos((double) (a.xyz[1] * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = -a.xyz[0] * (float) Math.sin((double) (a.xyz[1] * DEGREES_TO_RADIANS));
    }
    /*
     * a = (GaussianAtom) params.get(2);
     *
     * xyz = new float[3]; xyz[0] = xyz[0] + a.xyz[0] * (float) Math.cos( (double) (a.xyz[1] * 1.74532925199e-2f)); // So, we put it
     * along X-axis xyz[1] = xyz[1] + a.xyz[0] * (float) Math.sin( (double) (a.xyz[1] * 1.74532925199e-2f)); xyz[2] = 0.0f;
     */

    cartesians.add(xyz);

    for (i = 3; i < natoms; i++) {

      a = (GaussianAtom) params.get(i);

      x1 = (float[]) cartesians.get(a.ijk[0] - 1);
      x2 = (float[]) cartesians.get(a.ijk[1] - 1);
      x3 = (float[]) cartesians.get(a.ijk[2] - 1);

      xyz = new float[3];

      if (a.alternateZmatrixFormat != 0) {
        float[] unit = Geometry3d.getForTwoBondAngles(
                new float[]{x2[0] - x1[0], x2[1] - x1[1], x2[2] - x1[2]},
                new float[]{x3[0] - x1[0], x3[1] - x1[1], x3[2] - x1[2]},
                a.xyz[1] * DEGREES_TO_RADIANS, a.xyz[2] * DEGREES_TO_RADIANS, a.alternateZmatrixFormat);
        xyz[0] = x1[0] + a.xyz[0] * unit[0];
        xyz[1] = x1[1] + a.xyz[0] * unit[1];
        xyz[2] = x1[2] + a.xyz[0] * unit[2];
        cartesians.add(xyz);
        continue;
      }

      SIN2 = Math.sin((double) (a.xyz[1] * DEGREES_TO_RADIANS));
      COS2 = Math.cos((double) (a.xyz[1] * DEGREES_TO_RADIANS));
      SIN3 = Math.sin((double) (a.xyz[2] * DEGREES_TO_RADIANS));
      COS3 = Math.cos((double) (a.xyz[2] * DEGREES_TO_RADIANS));

      VT[0] = a.xyz[0] * COS2;
      VT[1] = a.xyz[0] * SIN2 * SIN3;
      VT[2] = a.xyz[0] * SIN2 * COS3;

      V1[0] = x3[0] - x2[0];
      V1[1] = x3[1] - x2[1];
      V1[2] = x3[2] - x2[2];

      V2[0] = x2[0] - x1[0];
      V2[1] = x2[1] - x1[1];
      V2[2] = x2[2] - x1[2];

      R2 = Math.sqrt(V2[0] * V2[0] + V2[1] * V2[1] + V2[2] * V2[2]);

      V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
      V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
      V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

      R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);

      V2[0] = V2[0] / R2;
      V2[1] = V2[1] / R2;
      V2[2] = V2[2] / R2;

      V3[0] = V3[0] / R3;
      V3[1] = V3[1] / R3;
      V3[2] = V3[2] / R3;

      V[0] = V2[1] * V3[2] - V2[2] * V3[1];
      V[1] = V2[2] * V3[0] - V2[0] * V3[2];
      V[2] = V2[0] * V3[1] - V2[1] * V3[0];

      VA1 = V2[0] * VT[0] + V3[0] * VT[1] + V[0] * VT[2];
      VB1 = V2[1] * VT[0] + V3[1] * VT[1] + V[1] * VT[2];
      VC1 = V2[2] * VT[0] + V3[2] * VT[1] + V[2] * VT[2];

      xyz[0] = x1[0] + (float) VA1;
      xyz[1] = x1[1] + (float) VB1;
      xyz[2] = x1[2] + (float) VC1;

      cartesians.add(xyz);
    }
    return cartesians;
  }

  static void resetTokens(String tokens[]) {
    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = null;
    }
  }

  private class MemoryObject {

    int memoryInKB = 0;
    String Memory;

    public MemoryObject(String memory) throws Exception {
      // Sets the amount of dynamic memory used to N words (8N bytes).
      // The default is 6MW. N may be optionally followed by a units designation: KB, MB, GB, KW, MB or GW.
      Memory = memory;
      memory = memory.toUpperCase();
      if (memory.matches(".+KB")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2));
      } else if (memory.matches(".+MB")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2)) * 1024;
      } else if (memory.matches(".+GB")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2)) * 1024 * 1024;
      } else if (memory.matches(".+KW")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2)) * 8;
      } else if (memory.matches(".+MW")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2)) * 8 * 1024;
      } else if (memory.matches(".+GW")) {
        memoryInKB = Integer.parseInt(memory.substring(0, memory.length() - 2)) * 8 * 1024 * 1024;
      } else {
        memoryInKB = Integer.parseInt(memory) * 8;
        memoryInKB = (int) ((float) memoryInKB / 1024.0f);
      }
    }
  }
}
