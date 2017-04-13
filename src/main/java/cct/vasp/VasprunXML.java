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

package cct.vasp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.StructureManagerInterface;

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

enum WhatToParse {
  INITIAL_STRUCTURE_ONLY, FINAL_STRUCTURE_ONLY, PARSE_ALL
}

public class VasprunXML
    extends DefaultHandler implements StructureManagerInterface {

  public boolean debug = true;
  public static final String TOTAL_ENERGY_KEY = "total";

  private WhatToParse whatToParse = WhatToParse.FINAL_STRUCTURE_ONLY;

  private int numberAtoms = 0;
  private int nTypes = 0;
  private VasprunTags currentTag = null;
  private VasprunTags validTags = VasprunTags.MODELING;
  private String currentEnergyLabel = null;
  private boolean yes_modeling = false;
  private boolean parse_atominfo = false;
  private boolean parse_atoms = false;
  private boolean parse_types = false;
  private boolean parse_atominfo_array_atoms = false;
  private boolean parse_crystal = false;
  private boolean parse_structure = false;
  private boolean parse_varray = false;
  private boolean parse_varray_basis = false;
  private boolean parse_varray_positions = false;
  private boolean parse_calculation = false;
  private boolean parse_scstep = false;
  private boolean parse_energy = false;
  private boolean parse_v = false;
  private boolean parse_c = false;

  private VaspSnapshot vaspSnapshot = null;
  private List<VaspSnapshot> snapshots = new ArrayList<VaspSnapshot> ();

  private Set<String> energyDecomp = new HashSet<String> ();

  private Map<String, List> termVsStrReference = new HashMap<String, List> ();

  private VaspAtom vaspAtom = null;
  private int vaspAtomIndex = 0;

  private double latticeVectors[][] = null;
  private int latticeVectorsIndex = 0;

  private List<VaspAtom> initialStructure = new ArrayList<VaspAtom> ();
  private boolean initialStrIsSet = false;
  private List<VaspAtom> finalStructure = new ArrayList<VaspAtom> ();
  private int atomIndex = 0;

  private StringWriter sWriter = null;
  static final Logger logger = Logger.getLogger(VasprunXML.class.getCanonicalName());

  public VasprunXML() {

  }

  public String[] getEnergyTerms() {
    if (energyDecomp.size() < 1) {
      return null;
    }
    String[] sa = new String[energyDecomp.size()];
    energyDecomp.toArray(sa);
    return sa;
  }

  /**
   * Returns all values for (energy) term "term"
   * @param term String - Energy term ("total", etc.)
   * @return double[] - (energy) term values
   */
  public double[] getAllTerms(String term) {
    if (!energyDecomp.contains(term) || snapshots.size() < 1) {
      return null;
    }

    List<Integer> references = new ArrayList<Integer> (snapshots.size());
    List<Double> values = new ArrayList<Double> (snapshots.size());
    for (int i = 0; i < snapshots.size(); i++) {
      VaspSnapshot snapshot = snapshots.get(i);
      try {
        Double value = snapshot.getEnergyTerm(term);
        values.add(value);
        references.add(new Integer(i));
      }
      catch (Exception ex) {}
    }
    if (values.size() < 1) {
      return null;
    }
    termVsStrReference.put(term, references);
    double[] energies = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      energies[i] = values.get(i);
    }
    return energies;
  }

  /**
   * Returns all total energies ("total" key)
   * It's the same as  getAllTerms(TOTAL_ENERGY_KEY)
   * @return double[] -  values
   */
  public double[] getTotalEnergies() {
    return getAllTerms(TOTAL_ENERGY_KEY);
  }

  /**
   * Takes the last snapshot from the bunch
   * @param molec MoleculeInterface
   * @throws Exception - if no snapshots/atoms
   */
  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : molec == null");
    }

    if (snapshots == null || snapshots.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no structures...");
    }

    VaspSnapshot snapshot = snapshots.get(snapshots.size() - 1);

    List<VaspAtom> atoms = snapshot.getAtoms();

    if (atoms == null || atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() + " : no atoms in selected snapshot");
    }

    //molec.addProperty(MoleculeInterface.ChargeProperty, new Integer(netCharge));
    //molec.addProperty(MoleculeInterface.MultiplicityProperty, new Integer(spinMultiplicity));

    molec.addMonomer("VASP");

    for (int i = 0; i < atoms.size(); i++) {
      VaspAtom va = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(va.getName());
      atom.setAtomicNumber(va.getAtomicNumber());
      atom.setXYZ(va.getX(), va.getY(), va.getZ());
      molec.addAtom(atom);
    }
  }

  public double[][] getLatticeVectors() throws Exception {

    if (snapshots == null || snapshots.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName() +
                          " : no structures...");
    }

    VaspSnapshot snapshot = snapshots.get(snapshots.size() - 1);

    return snapshot.getLatticeVectors();
  }

  public void parseVasprunXML(String fileName) throws Exception {
    File file = null;
    try {
      file = new File(fileName);
    }
    catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " : " + ex.getMessage());
    }
    parseVasprunXML(file);
  }

  public void parseVasprun(String fileName) throws Exception {
    String line, token;
    BufferedReader in = null;

    try {
      in = new BufferedReader(new FileReader(fileName));
    }
    catch (Exception ex) {
      throw new Exception("Cannot open file " + fileName + " : " + ex.getMessage());
    }

    boolean start_parse = false;
    try {

      while ( (line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) { // Blank line
          continue;
        }

        if (line.equalsIgnoreCase("<modeling>")) {
          start_parse = true;
          continue;
        }
        else if (line.equalsIgnoreCase("</modeling>")) {
          start_parse = false;
          continue;
        }

        if (!start_parse) {
          continue;
        }

        //  --- atominfo

        if (line.equalsIgnoreCase("<atominfo>")) {
          line = line.trim().toUpperCase();
          //  <atoms>      23</atoms>

        }

        StringTokenizer st = new StringTokenizer(line.toUpperCase(), " =,");
        token = st.nextToken();
      }
    }
    catch (Exception ex) {
      throw ex;
    }
  }

  public void parseVasprunXML(Object inputSource) throws Exception {

    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      // Parse the input
      SAXParser saxParser = factory.newSAXParser();

      if (inputSource instanceof File) {
        saxParser.parse( (File) inputSource, this);
      }
      else if (inputSource instanceof InputStream) {
        saxParser.parse( (InputStream) inputSource, this);
      }
      else {
        throw new Exception("parseVasprunXML: Internal error: Unknown input source: " +
                            inputSource.getClass().getCanonicalName());
      }
    }
    catch (Exception ex) {
      if (snapshots == null || snapshots.size() < 1) {
        ex.printStackTrace();
        throw new Exception("Cannot parse file : " + ex.getMessage());
      }
      System.err.println("It was an error while reading XML file, however several snashots have been salvaged...");
    }
  }

  @Override
  public void startDocument() throws SAXException {
    if (debug) {
      logger.info("Start Document");
    }
  }

  @Override
  public void endDocument() throws SAXException {
    if (debug) {
      logger.info("End Document");
    }

  }

  @Override
  public void startElement(String namespaceURI,
                           String lName, // local name
                           String qName, // qualified name
                           Attributes attrs) throws SAXException {

    String eName = lName; // element name
    if ("".equals(eName)) {
      eName = qName; // namespaceAware = false
    }
    if (debug) {
      System.out.print("<" + eName);
    }
    if (attrs != null) {
      for (int i = 0; i < attrs.getLength(); i++) {
        String aName = attrs.getLocalName(i); // Attr name
        if ("".equals(aName)) {
          aName = attrs.getQName(i);
        }
        if (debug) {
          System.out.print(" " + aName + "=\"" + attrs.getValue(i) + "\"");
        }
      }
    }
    if (debug) {
      logger.info(">");
    }
    // Parse elements...

    currentTag = validTags.getCommandObject(eName);

    if (currentTag == null) {
      logger.info("Unknown tag " + eName + " Ignored...");
      return;
    }

    if (currentTag == VasprunTags.MODELING) {
      yes_modeling = true;
      return;
    }

    if (!yes_modeling) {
      return;
    }

    // --- Tags
    // <generator>
    // <incar>
    // <kpoints>
    // <parameters>
    // <atominfo>
    //   <atoms>      23</atoms>
    //   <types>
    //   <array name="atoms" >
    //      <dimension dim="1">ion</dimension>
    //      <field type="string">element</field>
    //      <field type="int">atomtype</field>
    //      <set>
    //         <rc><c>Fe</c><c>   1</c></rc>
    //   <array name="atomtypes" >
    //      <dimension dim="1">type</dimension>
    //      <field type="int">atomspertype</field>
    //      <field type="string">element</field>
    //      <field>mass</field>
    //      <field>valence</field>
    //      <field type="string">pseudopotential</field>
    //      <set>
    //         <rc><c>  20</c><c>Fe</c><c>     55.84700000</c><c>      8.00000000</c><c>  US Fe                                 </c></rc>
    // <structure name="initialpos" >
    //   <crystal>
    //      <varray name="basis" >
    //         <v>       4.94500000      0.00000000      0.00000000</v>
    //         <v>      -1.64833333      4.66218754      0.00000000</v>
    //         <v>       0.00000000      0.00000000     23.77520000</v>
    //      <i name="volume">    548.12576206</i>
    //      <varray name="rec_basis" >
    //   <varray name="positions" >
    //   <varray name="selective"  type="logical" >
    //   <varray name="velocities" >
    //   <nose>
    // <calculation>



    switch (currentTag) {
      case ATOMINFO:
        parse_atominfo = true;
        break;
      case ATOMS:
        if (parse_atominfo) {
          parse_atoms = true;
          sWriter = new StringWriter();
        }
        break;
      case TYPES:
        parse_types = true;
        if (parse_types) {
          sWriter = new StringWriter();
        }
        break;

      case ARRAY:
        if (attrs.getValue("name") != null && attrs.getValue("name").equalsIgnoreCase("atoms")) {
          parse_atominfo_array_atoms = true;
        }
        break;

      case SET:
        if (parse_atominfo_array_atoms) {
        }
        break;

      case CALCULATION:
        parse_calculation = true;
        vaspSnapshot = new VaspSnapshot();
        break;

      case STRUCTURE:
        parse_structure = true;
        atomIndex = 0;
        if (attrs != null) {
          if (attrs.getValue("name") != null && attrs.getValue("name").equalsIgnoreCase("initialpos")) {
            vaspSnapshot = new VaspSnapshot();
          }
          else if (attrs.getValue("name") != null && attrs.getValue("name").equalsIgnoreCase("finalpos")) {
            vaspSnapshot = new VaspSnapshot();
          }
        }
        break;

      case CRYSTAL:
        parse_crystal = true;
        break;

      case VARRAY:
        parse_varray = true;
        if (attrs != null) {
          if (attrs.getValue("name") != null && attrs.getValue("name").equalsIgnoreCase("basis") && parse_crystal) {
            parse_varray_basis = true;
          }
          else if (attrs.getValue("name") != null && attrs.getValue("name").equalsIgnoreCase("positions")) {
            parse_varray_positions = true;
          }
        }
        break;

      case SCSTEP:
        parse_scstep = true;
        break;

      case ENERGY:
        parse_energy = true;
        break;

      case V:
        parse_v = true;
        if (parse_varray_basis || parse_varray_positions) {
          sWriter = new StringWriter();
        }
        break;

      case C:
        parse_c = true;
        if (parse_atominfo_array_atoms) {
          sWriter = new StringWriter();
        }
        break;

      case I:
        if (parse_calculation && parse_energy && ! (parse_scstep)) {
          sWriter = new StringWriter();
          if (attrs != null && attrs.getValue("name") != null) {
            currentEnergyLabel = attrs.getValue("name");
          }
        }

        break;
      default:
        break;
    }

  }

  @Override
  public void endElement(String namespaceURI,
                         String sName, // simple name
                         String qName // qualified name
      ) throws SAXException {

    String eName = sName; // element name
    if ("".equals(eName)) {
      eName = qName; // namespaceAware = false
    }
    if (debug) {
      logger.info("</" + eName + ">");
    }
    // Parse elements...

    currentTag = validTags.getCommandObject(eName);

    if (currentTag == null) {
      return;
    }

    if (currentTag == VasprunTags.MODELING) {
      yes_modeling = false;
      return;
    }

    String s = null;
    if (sWriter != null) {
      s = sWriter.toString().trim();
      sWriter = null;
    }

    switch (currentTag) {
      case ATOMINFO:
        parse_atominfo = false;
        break;
      case ATOMS:
        if (parse_atominfo) {
          if (parse_atoms) {
            try {
              numberAtoms = Integer.parseInt(s);
            }
            catch (Exception ex) {
              throw new SAXException("Cannot parse number of atoms: \"" + s + "\" : " + ex.getMessage());
            }

            if (initialStructure.size() > 0 && initialStructure.size() != numberAtoms) {
              throw new SAXException("initialStructure.size() > 0 && initialStructure.size() != numberAtoms");
            }
          }
        }
        parse_atoms = false;
        break;
      case TYPES:
        if (parse_types) {
          try {
            nTypes = Integer.parseInt(s);
          }
          catch (Exception ex) {
            throw new SAXException("Cannot parse number of atom types: " + s + " : " + ex.getMessage());
          }
        }
        parse_types = false;
        break;

      case ARRAY:
        if (parse_atominfo_array_atoms) {
          parse_atominfo_array_atoms = false;
        }
        break;

      case SET:
        if (parse_atominfo_array_atoms) {
          initialStrIsSet = true;
        }
        break;

      case CALCULATION:
        parse_calculation = false;
        snapshots.add(vaspSnapshot);
        atomIndex = 0;
        break;

      case STRUCTURE:
        if (!parse_calculation) {
          snapshots.add(vaspSnapshot);
        }
        parse_structure = false;
        atomIndex = 0;
        break;

      case CRYSTAL:
        parse_crystal = false;
        break;

      case VARRAY:

        if (parse_varray_positions) {
          vaspSnapshot.setAtoms(this.initialStructure);
        }

        parse_varray = false;
        parse_varray_basis = false;
        parse_varray_positions = false;
        break;

      case SCSTEP:
        parse_scstep = false;
        break;

      case ENERGY:
        parse_energy = false;
        break;

      case V:
        if (! (parse_varray_basis || parse_varray_positions)) {
          break;
        }

        double[] vec = new double[3];
        StringTokenizer tokens = new StringTokenizer(s, " ");
        for (int i = 0; i < 3; i++) {
          if (!tokens.hasMoreTokens()) {
            throw new SAXException("Cannot parse vector data: " + s + " : Unexpected end of data");
          }
          try {
            vec[i] = Double.parseDouble(tokens.nextToken());
          }
          catch (Exception ex) {
            throw new SAXException("Cannot parse vector data: " + s + " : " + ex.getMessage());
          }
        }

        if (parse_varray_basis) {
          if (latticeVectorsIndex >= 3) {
            latticeVectorsIndex = 0;
          }
          if (latticeVectorsIndex == 0) {
            latticeVectors = new double[3][3];
          }
          latticeVectors[latticeVectorsIndex][0] = vec[0];
          latticeVectors[latticeVectorsIndex][1] = vec[1];
          latticeVectors[latticeVectorsIndex][2] = vec[2];
          ++latticeVectorsIndex;

          if (latticeVectorsIndex >= 3) {
            vaspSnapshot.setLatticeVectors(latticeVectors);
            latticeVectorsIndex = 0;
            break;
          }
        }

        else if (parse_varray_positions) {
          if (atomIndex >= initialStructure.size()) {
            throw new SAXException("atomIndex >= initialStructure.size()");
          }

          vaspAtom = initialStructure.get(atomIndex);
          vaspAtom.setCoordinates(vec[0], vec[1], vec[2]);
          ++atomIndex;
        }
        parse_v = false;
        break;

      case C:
        if (parse_atominfo_array_atoms) {
          if (vaspAtomIndex == 0) {
            vaspAtom = new VaspAtom();
            vaspAtom.setElement(s);
            ++vaspAtomIndex;
          }
          else if (vaspAtomIndex == 1) {
            vaspAtomIndex = 0;
            initialStructure.add(vaspAtom);
          }
        }

        parse_c = false;
        break;

      case I:
        if (parse_calculation && parse_energy && ! (parse_scstep)) {
          double value = 0;
          try {
            value = Double.parseDouble(s);
            vaspSnapshot.addEnergyTerm(currentEnergyLabel, value);
          }
          catch (Exception ex) {
            throw new SAXException("Cannot parse value for energy: " + s + " : " + ex.getMessage());
          }

          if (currentEnergyLabel != null && (!energyDecomp.contains(currentEnergyLabel))) {
            energyDecomp.add(currentEnergyLabel);
          }
        }

        break;

      default:
        break;
    }

  }

  @Override
  public void characters(char buf[], int offset, int len) throws
      SAXException {

    if (len < 1) {
      return;
    }

    if (currentTag == null) {
      return;
    }

    String s = null;

    if (sWriter != null) {
      sWriter.write(buf, offset, len);
    }

    switch (currentTag) {
      case ATOMS:

        /*
                     if (parse_atoms) {
           try {
              s = new String(buf, offset, len).trim();
              numberAtoms = Integer.parseInt(s);
           }
           catch (Exception ex) {
              throw new SAXException("Cannot parse number of atoms: \"" + s + "\" : " + ex.getMessage());
           }

           if (initialStructure.size() > 0 && initialStructure.size() != numberAtoms) {
              throw new SAXException("initialStructure.size() > 0 && initialStructure.size() != numberAtoms");
           }
                     }
         */
        break;

      case TYPES:

        /*
                     if (parse_types) {
           try {
              s = new String(buf, offset, len).trim();
              nTypes = Integer.parseInt(s);
           }
           catch (Exception ex) {
              throw new SAXException("Cannot parse number of atom types: " + s + " : " + ex.getMessage());
           }
                     }
         */
        break;

      case C:

        /*
                     if (!parse_c) {
           break;
                     }
                     if (parse_atominfo_array_atoms) {
           if (vaspAtomIndex == 0) {
              vaspAtom = new VaspAtom();
              vaspAtom.setElement(new String(buf, offset, len).trim());
              ++vaspAtomIndex;
           }
           else if (vaspAtomIndex == 1) {
              vaspAtomIndex = 0;
              initialStructure.add(vaspAtom);
           }

                     }
         */
        break;

      case V:

        /*
                     if (!parse_v) {
           break;
                     }
                     if (! (parse_varray_basis || parse_varray_positions)) {
           break;
                     }

                     double[] vec = new double[3];
                     s = new String(buf, offset, len).trim();
                     StringTokenizer tokens = new StringTokenizer(s, " ");
                     for (int i = 0; i < 3; i++) {
           if (!tokens.hasMoreTokens()) {
              throw new SAXException("Cannot parse vector data: " + s + " : Unexpected end of data");
           }
           try {
              vec[i] = Double.parseDouble(tokens.nextToken());
           }
           catch (Exception ex) {
              throw new SAXException("Cannot parse vector data: " + s + " : " + ex.getMessage());
           }
                     }

                     if (parse_varray_basis) {
           if (latticeVectorsIndex == 0) {
              latticeVectors = new double[3][3];
           }
           else if (latticeVectorsIndex >= 3) {
              vaspSnapshot.setLatticeVectors(latticeVectors);
              latticeVectorsIndex = 0;
              break;
           }
           latticeVectors[latticeVectorsIndex][0] = vec[0];
           latticeVectors[latticeVectorsIndex][1] = vec[1];
           latticeVectors[latticeVectorsIndex][2] = vec[2];
           ++latticeVectorsIndex;
                     }

                     else if (parse_varray_positions) {
           if (atomIndex >= initialStructure.size()) {
              throw new SAXException("atomIndex >= initialStructure.size()");
           }

           vaspAtom = initialStructure.get(atomIndex);
           vaspAtom.setCoordinates(vec[0], vec[1], vec[2]);
                     }
         */
        break;

    }

  }

  /**
   * Not implemented yet
   * @param number int
   * @throws Exception
   */
  @Override
  public void selectStructure(int number) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number) is not implemented yet");
  }

  /**
   * Not implemented yet...
   * @param number int
   * @param term String
   * @throws Exception
   */
  @Override
  public void selectStructure(int number, String term) throws Exception {
    throw new Exception(this.getClass().getCanonicalName() + ": selectStructure(int number, String term) is not implemented yet");
  }

  @Override
  public float[][] getStructure(int n) {
    return getStructure(n, TOTAL_ENERGY_KEY);
  }

  @Override
  public float[][] getStructure(int n, String term) {
    if (!energyDecomp.contains(term) || snapshots.size() < 1) {
      return null;
    }

    if (snapshots == null || snapshots.size() < 1) {
      return null;
    }

    List<Integer> references = termVsStrReference.get(term);
    if (references == null) {
      System.err.println("references == null");
      return null;
    }
    else if (n >= references.size()) {
      System.err.println("n >= references.size()");
      return null;
    }

    if (debug) {
      logger.info("getStructure: # " + n);
    }

    VaspSnapshot snapshot = snapshots.get(references.get(n));

    List<VaspAtom> atoms = snapshot.getAtoms();

    if (atoms == null || atoms.size() < 1) {
      System.err.println(this.getClass().getCanonicalName() + " : no atoms in selected snapshot");
      return null;
    }

    float[][] coord = new float[atoms.size()][3];
    for (int i = 0; i < atoms.size(); i++) {
      VaspAtom va = atoms.get(i);
      coord[i][0] = va.getX();
      coord[i][1] = va.getY();
      coord[i][2] = va.getZ();
      if (debug && i == atoms.size() - 1) {
        logger.info(i + " : x=" + coord[i][0] + " y=" + coord[i][1] + " z=" + coord[i][2]);
      }

    }
    return coord;
  }

  //===========================================================
  // Utility Methods ...
  //===========================================================


}
