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

package cct.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.AtomicSet;
import cct.modelling.AtomicSets;
import cct.modelling.Molecule;

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
public class CCTParser
    extends DefaultHandler {

  static boolean Debug = false;
  static private String NO_PROPERTY_SIGN = "&!";
  static final String INTERNAL_PROPERTY = "_";

  List molecules = new ArrayList();
  List atoms = new ArrayList();
  List bonds = new ArrayList();
  static final Logger logger = Logger.getLogger(CCTParser.class.getCanonicalName());

  AtomicSets atomicSets = new AtomicSets();
  String atomSetName = "ATOM_SET";

  boolean finish_parsing = false;
  boolean yes_cct = false;
  boolean yes_many_molecules = false;
  boolean yes_many_monomers = false;
  boolean yes_many_atoms = false;
  boolean yes_molecule = false;
  boolean unrecoverableError = false;
  int parseLevel = -1;
  String currentTag = null;
  String fullString = "";
  MoleculeInterface currentMolecule = null;

  StringReader sReader = null;
  StringWriter sWriter = null;

  private String propertyID = "";
  private String propertyType = "";
  private String propertyFormat = "";
  private String propertyClass = "";
  private String propertyValue = "";

  ResourceBundle resources;

  static private Writer out;

  public CCTParser(MoleculeInterface molecule) {
    currentMolecule = molecule;
    try {
      resources = ResourceBundle.getBundle("cct.cct");
      String text =
          resources.getString(this.getClass().getName() + ".debug");
      if (text.equalsIgnoreCase("true")) {
        Debug = true;
      }
      else if (text.equalsIgnoreCase("false")) {
        Debug = false;
      }
      else {
        logger.info("Warning: wrong value for " +
                           this.getClass().getName() + ".debug" + " : " +
                           text);
      }
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources for class " + this.getClass().getName() +
                           " not found");
      }
    }
  }

  /**
   *
   * @param file_name String
   * @param molecule MoleculeInterface
   * @return List
   */
  public List parseCCTFile(Object inputSource, MoleculeInterface molecule) {
    DefaultHandler handler = new CCTParser(molecule);
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      // Set up output stream
      out = new OutputStreamWriter(System.out, "UTF8");

      // Parse the input
      SAXParser saxParser = factory.newSAXParser();

      if (inputSource instanceof String) {
        saxParser.parse(new File( (String) inputSource), handler);
      }
      else if (inputSource instanceof InputStream) {
        saxParser.parse( (InputStream) inputSource, handler);
      }
      else {
        System.err.println("parseCCTFile: Unknown input source: " +
                           inputSource.getClass().getCanonicalName());
        return null;
      }

    }
    catch (Throwable t) {
      //logger.error("Parsing error " + t.getMessage());
      t.printStackTrace();
      return null;
    }

    CCTParser parser = (CCTParser) handler;
    List mols = parser.getMolecules();
    return mols;
  }

  public static void saveCCTFile(MoleculeInterface molec, String fileName) throws
      Exception {
    List mols = new ArrayList();
    mols.add(molec);
    try {
      saveCCTFile(mols, fileName);
    }
    catch (Exception ex) {
      throw ex;
    }
  }

  public static void saveCCTFile(List mols, String fileName) throws
      Exception {
    //Message = "";

    FileOutputStream out;
    try {
      out = new FileOutputStream(fileName);
    }
    catch (Exception ex) {
      System.err.println("Error opening FileOutputStream: " + ex.getMessage());
      throw ex;
    }

    //StringWriter sWriter = new StringWriter();

    StreamResult streamResult = new StreamResult(out);
    SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.
        newInstance();

    // SAX2.0 ContentHandler.
    TransformerHandler hd = null;
    try {
      hd = tf.newTransformerHandler();
    }
    catch (TransformerConfigurationException e) {
      System.err.println("Error geting newTransformerHandler: " +
                         e.getMessage());
      throw e;
    }

    Transformer serializer = hd.getTransformer();
    //serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
    //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "users.dtd");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    hd.setResult(streamResult);
    try {
      hd.startDocument();
      AttributesImpl atts = new AttributesImpl();
      atts.addAttribute("", "", "version", "CDATA", "0.1");
      //atts.addAttribute("","","TYPE","CDATA",type[i]);

      // CCT tag.
      hd.startElement("", "", CCTParserEnum.CCT_TAG.toString(), atts);

      if (mols.size() > 1) {
        atts.clear();
        atts.addAttribute("", "", "number", "CDATA",
                          String.valueOf(mols.size()));
        hd.startElement("", "", CCTParserEnum.MANY_MOLECULES_TAG.toString(),
                        atts);
      }

      for (int i = 0; i < mols.size(); i++) {
        MoleculeInterface molec = (MoleculeInterface) mols.get(i);
        atts.clear();
        atts.addAttribute("", "", "name", "CDATA", molec.getName());
        atts.addAttribute("", "", "atoms", "CDATA",
                          String.valueOf(molec.getNumberOfAtoms()));
        atts.addAttribute("", "", "bonds", "CDATA",
                          String.valueOf(molec.getNumberOfBonds()));
        hd.startElement("", "", CCTParserEnum.MOLECULE_TAG.toString(), atts);

        // --- Monomers *******************
        List substr = molec.getMolecularSubstructure();

        atts.clear();
        atts.addAttribute("", "", "number", "CDATA",
                          String.valueOf(substr.size()));
        hd.startElement("", "", CCTParserEnum.MANY_MONOMERS_TAG.toString(),
                        atts);
        //hd.startCDATA();

        hd.characters("\n".toCharArray(), 0, 1);

        Iterator iter = substr.iterator();
        while (iter.hasNext()) {
          String data = (String) iter.next();
          if (data == null) {
            data = "NONAME\n";
          }
          else {
            data += "\n";
          }
          hd.characters(data.toCharArray(), 0, data.length());
          //hd.startEntity(data);
          //out.write(data.getBytes());
          //hd.endEntity("");
        }

        //hd.endCDATA();
        hd.endElement("", "", CCTParserEnum.MANY_MONOMERS_TAG.toString());
        // --- End of monomers ********************

        // --- Atoms *******************

        writeAtoms(hd, molec);

        // --- end of atoms *************

        // --- Bonds ********************

        writeBonds(hd, molec);

        // --- end of Bonds *************

        // --- Start to print Properties

        writeProperties(hd, molec);

        // --- End of Properties

        hd.endElement("", "", CCTParserEnum.MOLECULE_TAG.toString());
      }

      if (mols.size() > 1) {
        hd.endElement("", "", CCTParserEnum.MANY_MOLECULES_TAG.toString());
      }

      hd.endElement("", "", CCTParserEnum.CCT_TAG.toString());
      hd.endDocument();

    }
    catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println(e.getMessage());
      throw e;
      //return null;
    }

    try {
      out.close();
    }
    catch (Exception ex) {
      throw ex;
    }

  }

  public static void writeBonds(TransformerHandler hd,
                                MoleculeInterface molec) throws
      SAXException {
    AttributesImpl atts = new AttributesImpl();
    try {
      atts.clear();
      atts.addAttribute("", "", "number", "CDATA",
                        String.valueOf(molec.getNumberOfBonds()));
      hd.startElement("", "", CCTParserEnum.MANY_BONDS_TAG.toString(),
                      atts);
      //hd.startCDATA();

      hd.characters("\n".toCharArray(), 0, 1);

      for (int j = 0; j < molec.getNumberOfBonds(); j++) {
        BondInterface bond = molec.getBondInterface(j);
        AtomInterface a_i = bond.getIAtomInterface();
        AtomInterface a_j = bond.getJAtomInterface();

        String data = molec.getAtomIndex(a_i) + "," +
            molec.getAtomIndex(a_j) + "\n";
        hd.characters(data.toCharArray(), 0, data.length());
      }

      //hd.endCDATA();
      hd.endElement("", "", CCTParserEnum.MANY_BONDS_TAG.toString());

    }
    catch (SAXException e) {
      System.err.println("Error Writing Bonds : " + e.getMessage());
      throw e;
    }

  }

  public static void writeAtoms(TransformerHandler hd,
                                MoleculeInterface molec) throws
      SAXException {
    AttributesImpl atts = new AttributesImpl();
    try {
      atts.clear();
      atts.addAttribute("", "", "number", "CDATA",
                        String.valueOf(molec.getNumberOfAtoms()));
      hd.startElement("", "", CCTParserEnum.MANY_ATOMS_TAG.toString(),
                      atts);
      //hd.startCDATA();

      hd.characters("\n".toCharArray(), 0, 1);

      for (int j = 0; j < molec.getNumberOfAtoms(); j++) {
        AtomInterface atom = molec.getAtomInterface(j);
        String data = atom.getAtomicNumber() + "," + atom.getName() +
            "," + atom.getX() + "," + atom.getY() + "," + atom.getZ() +
            "," + atom.getSubstructureNumber() + "\n";
        hd.characters(data.toCharArray(), 0, data.length());
      }

      //hd.endCDATA();
      hd.endElement("", "", CCTParserEnum.MANY_ATOMS_TAG.toString());

    }
    catch (SAXException e) {
      System.err.println("Error Writing Atoms: " + e.getMessage());
      throw e;
    }

  }

  public static void writeAtomSets(TransformerHandler hd, MoleculeInterface molec, AtomicSets sets) throws SAXException {

    AttributesImpl atts = new AttributesImpl();
    Molecule.validateSets(molec);

    Set set2 = sets.entrySet();
    Iterator iter = set2.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String setName = me.getKey().toString();
      Object obj = me.getValue();
      if (! (obj instanceof AtomicSet)) {
        System.err.println("! (obj instanceof AtomicSet). Ignored...");
        continue;
      }
      AtomicSet aset = (AtomicSet) obj;

      atts.clear();
      atts.addAttribute("", "", "name", "CDATA", aset.getName());
      atts.addAttribute("", "", "type", "CDATA", "atoms");
      atts.addAttribute("", "", "number", "CDATA", String.valueOf(aset.size()));
      atts.addAttribute("", "", "comment", "CDATA", "type attribute is not currently used");

      hd.startElement("", "", CCTParserEnum.ATOM_SET_TAG.toString(), atts);

      hd.characters("\n".toCharArray(), 0, 1);

      for (int j = 0; j < aset.size(); j++) {
        AtomInterface atom = aset.get(j);
        int index = molec.getAtomIndex(atom);
        if (j != 0) {
          hd.characters(",".toCharArray(), 0, 1);
        }
        String data = String.valueOf(index);
        hd.characters(data.toCharArray(), 0, data.length());
      }
      hd.characters("\n".toCharArray(), 0, 1);

      hd.endElement("", "", CCTParserEnum.ATOM_SET_TAG.toString());
    }

  }

  public static void writeProperties(TransformerHandler hd, MoleculeInterface molec) throws SAXException {

    AttributesImpl atts = new AttributesImpl();

    Map molProps = molec.getProperties();

    Set set = molProps.entrySet();
    Iterator iter = set.iterator();
    try {
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String propName = me.getKey().toString();
        if (Debug) {
          logger.info("Writing molecular property: " + propName);
        }

        Object prop = molec.getProperty(propName);

        // --- If there are many properties
        if (propName.equals(MoleculeInterface.AtomicSets)) { // Atomic sets
          if (! (prop instanceof AtomicSets)) {
            System.err.println("!(prop instanceof AtomicSets). Ignored...");
            continue;
          }
          writeAtomSets(hd, molec, (AtomicSets) prop);
        }

        else if (prop instanceof List) {
          List properties = (List) prop;
          for (int i = 0; i < properties.size(); i++) {
            Object obj = properties.get(i);
            String propFormat = obj.getClass().getSimpleName();

            atts.clear();
            atts.addAttribute("", "", "id", "CDATA", propName);
            atts.addAttribute("", "", "type", "CDATA", "molecule");
            atts.addAttribute("", "", "class", "CDATA", propFormat);

            String data = getObjectAsString(obj);

            atts.addAttribute("", "", "value", "CDATA", data);
            hd.startElement("", "", CCTParserEnum.PROPERTY_TAG.toString(),
                            atts);
            hd.endElement("", "", CCTParserEnum.PROPERTY_TAG.toString());
          }
        }

        // --- if there is only one property entry
        else {

          atts.clear();
          atts.addAttribute("", "", "id", "CDATA", propName);
          atts.addAttribute("", "", "type", "CDATA", "molecule");
          //Object prop = molec.getProperty(propName);
          String propFormat = prop.getClass().getSimpleName();
          atts.addAttribute("", "", "class", "CDATA", propFormat);

          //String data = prop.toString();
          String data = getObjectAsString(prop);
          atts.addAttribute("", "", "value", "CDATA", data);

          hd.startElement("", "", CCTParserEnum.PROPERTY_TAG.toString(),
                          atts);

          //hd.characters("\n".toCharArray(), 0, 1);

          //String data = prop.toString() + "\n";
          //hd.characters(data.toCharArray(), 0, data.length());

          hd.endElement("", "", CCTParserEnum.PROPERTY_TAG.toString());
        }

      }
    }
    catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println("Error Writing properties: " + e.getMessage());
      throw e;
      //return null;
    }

    // --- Write Atomic properties

    // --- First count number of unique atom properties


    Map<String, Integer> props = new HashMap<String, Integer> ();
    Map propsFormat = new HashMap();
    for (int j = 0; j < molec.getNumberOfAtoms(); j++) {
      AtomInterface atom = molec.getAtomInterface(j);
      Map p = atom.getProperties();
      set = p.entrySet();
      Iterator iter2 = set.iterator();
      while (iter2.hasNext()) {
        Map.Entry me = (Map.Entry) iter2.next();
        String propName = me.getKey().toString();

        // --- We don't save properties which starts from INTERNAL_PROPERTY symbol
        if (propName.startsWith(INTERNAL_PROPERTY)) {
          continue;
        }

        Object obj = me.getValue();
        if (props.containsKey(propName)) {
          int count = props.get(propName).intValue();
          ++count;
          props.put(propName, new Integer(count));
        }
        else { // Found new property
          Integer count = new Integer(1);
          props.put(propName, count);
          propsFormat.put(propName, obj.getClass().getSimpleName());
        }
      }
    }

    logger.info("There are " + props.size() +
                       " unique atomic properties");

    // --- Printing

    set = props.entrySet();
    iter = set.iterator();
    try {
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String propName = me.getKey().toString();
        Integer count = (Integer) me.getValue();
        if (Debug) {
          logger.info("Writing property: " + propName +
                             " number: " + count.intValue());
        }

        atts.clear();
        atts.addAttribute("", "", "id", "CDATA", propName);
        atts.addAttribute("", "", "type", "CDATA", "atom");
        AtomInterface atom = molec.getAtomInterface(0);
        Object prop = atom.getProperty(propName);
        String propFormat = (String) propsFormat.get(propName);
        atts.addAttribute("", "", "class", "CDATA", propFormat);
        boolean indexed = false;
        if (count.intValue() > molec.getNumberOfAtoms() / 2) {
          atts.addAttribute("", "", "format", "CDATA", "all");
          indexed = false;
        }
        else {
          atts.addAttribute("", "", "format", "CDATA", "indexed");
          indexed = true;
        }
        hd.startElement("", "", CCTParserEnum.PROPERTY_TAG.toString(),
                        atts);
        hd.characters("\n".toCharArray(), 0, 1);

        for (int j = 0; j < molec.getNumberOfAtoms(); j++) {
          atom = molec.getAtomInterface(j);
          prop = atom.getProperty(propName);

          if (indexed) {
            if (prop == null) {
              continue;
            }
            String data = j + "," + getObjectAsString(prop) + "\n";
            hd.characters(data.toCharArray(), 0, data.length());
          }
          else {
            String data;
            if (prop == null) {
              data = NO_PROPERTY_SIGN + "\n";
            }
            else {
              data = getObjectAsString(prop) + "\n";
            }
            hd.characters(data.toCharArray(), 0, data.length());
          }
        }

        hd.endElement("", "", CCTParserEnum.PROPERTY_TAG.toString());
      }
    }
    catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println("Error Writing properties: " + e.getMessage());
      throw e;
      //return null;
    }

    // --- Write Bond properties

    // --- First count number of unique bond properties

    props.clear();
    propsFormat.clear();
    for (int j = 0; j < molec.getNumberOfBonds(); j++) {
      BondInterface bond = molec.getBondInterface(j);
      Map p = bond.getProperties();
      set = p.entrySet();
      Iterator iter2 = set.iterator();
      while (iter2.hasNext()) {
        Map.Entry me = (Map.Entry) iter2.next();
        String propName = me.getKey().toString();

        // --- We don't save properties which starts from INTERNAL_PROPERTY symbol
        if (propName.startsWith(INTERNAL_PROPERTY)) {
          continue;
        }

        Object obj = me.getValue();
        if (props.containsKey(propName)) {
          Integer count = props.get(propName);
          ++count;
          props.put(propName, count);
          //int count = ( (Integer) props.get(propName)).intValue();
          //++count;
          //props.put(propName, new Integer(count));
        }
        else { // Found new property
          Integer count = new Integer(1);
          props.put(propName, count);
          propsFormat.put(propName, obj.getClass().getSimpleName());
        }
      }
    }

    logger.info("There are " + props.size() +
                       " unique bond properties");

    // --- Printing

    set = props.entrySet();
    iter = set.iterator();
    try {
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String propName = me.getKey().toString();
        Integer count = (Integer) me.getValue();
        if (Debug) {
          logger.info("Writing property: " + propName +
                             " number: " + count.intValue());
        }

        atts.clear();
        atts.addAttribute("", "", "id", "CDATA", propName);
        atts.addAttribute("", "", "type", "CDATA", "bond");
        //BondInterface bond = molec.getBondInterface(0);
        //Object prop = bond.getProperty(propName);
        String propFormat = (String) propsFormat.get(propName);
        atts.addAttribute("", "", "class", "CDATA", propFormat);
        boolean indexed = false;
        if (count.intValue() > molec.getNumberOfBonds() / 2) {
          atts.addAttribute("", "", "format", "CDATA", "all");
          indexed = false;
        }
        else {
          atts.addAttribute("", "", "format", "CDATA", "indexed");
          indexed = true;
        }
        hd.startElement("", "", CCTParserEnum.PROPERTY_TAG.toString(),
                        atts);
        hd.characters("\n".toCharArray(), 0, 1);

        for (int j = 0; j < molec.getNumberOfBonds(); j++) {
          BondInterface bond = molec.getBondInterface(j);
          Object prop = bond.getProperty(propName);

          if (indexed) {
            if (prop == null) {
              continue;
            }
            String data = j + "," + getObjectAsString(prop) + "\n";
            hd.characters(data.toCharArray(), 0, data.length());
          }
          else {
            String data;
            if (prop == null) {
              data = NO_PROPERTY_SIGN + "\n";
            }
            else {
              data = getObjectAsString(prop) + "\n";
            }
            hd.characters(data.toCharArray(), 0, data.length());
          }
        }

        hd.endElement("", "", CCTParserEnum.PROPERTY_TAG.toString());
      }
    }
    catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println("Error Writing properties: " + e.getMessage());
      throw e;
      //return null;
    }

  }

  public int getMoleculesCount() {
    return molecules.size();
  }

  public List getMolecules() {
    return molecules;
  }

  public void startDocument() throws SAXException {
    if (Debug) {
      emit("<?xml version='1.0' encoding='UTF-8'?>");
      nl();
    }
  }

  public void endDocument() throws SAXException {
    try {
      nl();
      out.flush();

    }
    catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
  }

  public void startElement(String namespaceURI,
                           String lName, // local name
                           String qName, // qualified name
                           Attributes attrs) throws SAXException {

    if (unrecoverableError) {
      throw new SAXException("Unrecoverable error");
    }

    if (finish_parsing) {
      return;
    }

    String eName = lName; // element name
    if ("".equals(eName)) {
      eName = qName; // namespaceAware = false
    }
    if (Debug) {
      emit("<" + eName);
    }
    if (attrs != null) {
      for (int i = 0; i < attrs.getLength(); i++) {
        String aName = attrs.getLocalName(i); // Attr name
        if ("".equals(aName)) {
          aName = attrs.getQName(i);
        }
        if (Debug) {
          emit(" ");
          emit(aName + "=\"" + attrs.getValue(i) + "\"");
        }
      }
    }
    if (Debug) {
      emit(">");
    }
    // Parse elements...

    currentTag = eName;

    if (CCTParserEnum.CCT_TAG.equalsIgnoreCase(eName)) {
      yes_cct = true;
      parseLevel = CCTParserEnum.CCT_TAG.getEnum();
      return;
    }
    if (!yes_cct) {
      return; // Don't find <cct> tag yet...
    }

    if (CCTParserEnum.MANY_MOLECULES_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing molecules...");
      }
      parseLevel = CCTParserEnum.MANY_MOLECULES_TAG.getEnum();
      yes_many_molecules = true;
      return;
    }

    if (CCTParserEnum.MOLECULE_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing molecule...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();
      yes_molecule = true;

      // --- Init new molecule
      if (molecules.size() != 0) {
        currentMolecule = currentMolecule.getInstance();
      }
      atoms.clear();
      bonds.clear();
      return;
    }

    if (!yes_many_molecules && !yes_molecule) {
      return; // Don't find <molecules> or <molecule> tag yet...
    }

    if (CCTParserEnum.MANY_MONOMERS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing monomers...");
      }
      parseLevel = CCTParserEnum.MANY_MONOMERS_TAG.getEnum();
      yes_many_monomers = true;
      sWriter = new StringWriter();
      return;
    }

    if (CCTParserEnum.MANY_ATOMS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing atoms...");
      }
      parseLevel = CCTParserEnum.MANY_ATOMS_TAG.getEnum();
      yes_many_atoms = true;
      sWriter = new StringWriter();
      return;
    }

    if (CCTParserEnum.MANY_BONDS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing bonds...");
      }
      parseLevel = CCTParserEnum.MANY_BONDS_TAG.getEnum();
      sWriter = new StringWriter();
      return;
    }

    if (CCTParserEnum.ATOM_SET_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing atom set...");
      }
      parseLevel = CCTParserEnum.ATOM_SET_TAG.getEnum();
      atomSetName = attrs.getValue("name");
      sWriter = new StringWriter();
      return;
    }

    if (CCTParserEnum.PROPERTY_TAG.equalsIgnoreCase(eName)) {
      if (attrs == null) {
        System.err.println("Error: no attributes for property");
        unrecoverableError = true;
        return;
      }
      // --- Reset property attributes
      propertyID = "";
      propertyType = "atom";
      propertyFormat = "all";
      propertyClass = "String";

      for (int i = 0; i < attrs.getLength(); i++) {
        String aName = attrs.getLocalName(i); // Attr name
        if ("".equals(aName)) {
          aName = attrs.getQName(i);
        }

        if (aName.equalsIgnoreCase("id")) {
          propertyID = attrs.getValue(i);
        }
        else if (aName.equalsIgnoreCase("type")) {
          propertyType = attrs.getValue(i);
        }
        else if (aName.equalsIgnoreCase("format")) {
          propertyFormat = attrs.getValue(i);
        }
        else if (aName.equalsIgnoreCase("class")) {
          propertyClass = attrs.getValue(i);
        }
        else if (aName.equalsIgnoreCase("value")) {
          propertyValue = attrs.getValue(i);
        }
        else {
          System.err.println("Warning: unknown attribute name: " + aName +
                             " Ignored...");
        }

        if (propertyID.length() == 0) {
          System.err.println(
              "Error: No property ID attribute in property tag");
          unrecoverableError = true;
          return;
        }

      }

      if (Debug) {
        logger.info("Starting parsing property...");
      }
      parseLevel = CCTParserEnum.PROPERTY_TAG.getEnum();
      sWriter = new StringWriter();
      return;
    }

  }

  public void endElement(String namespaceURI,
                         String sName, // simple name
                         String qName // qualified name
      ) throws SAXException {

    if (unrecoverableError) {
      throw new SAXException("Unrecoverable error");
    }

    if (finish_parsing) {
      return;
    }

    String eName = sName; // element name
    if ("".equals(eName)) {
      eName = qName; // namespaceAware = false
    }

    if (Debug) {
      emit("</" + eName + ">");
    }
    // --- Parsing

    if (CCTParserEnum.CCT_TAG.equalsIgnoreCase(eName)) {
      finish_parsing = true;
      yes_cct = false;
      return;
    }

    if (CCTParserEnum.MANY_MOLECULES_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing molecules...");
      }
      parseLevel = CCTParserEnum.CCT_TAG.getEnum();
      yes_many_molecules = false;
      yes_molecule = false;
      return;
    }

    if (CCTParserEnum.MOLECULE_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing molecule: monomers: " + currentMolecule.getMolecularSubstructure().size() + " atoms: " +
                           currentMolecule.getNumberOfAtoms() + " bonds: " + currentMolecule.getNumberOfBonds());
      }
      if (yes_many_molecules) {
        parseLevel = CCTParserEnum.MANY_MOLECULES_TAG.getEnum();
      }
      else {
        parseLevel = CCTParserEnum.CCT_TAG.getEnum();
      }

      if (atomicSets.size() > 0) {
        Map prop = currentMolecule.getProperties();
        prop.put(MoleculeInterface.AtomicSets, atomicSets);
      }
      molecules.add(currentMolecule);
      yes_molecule = false;
      return;
    }

    if (CCTParserEnum.MANY_MONOMERS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing monomers...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();
      yes_many_monomers = false;
      try {
        readMonomers(sWriter.toString(), currentMolecule);
        sWriter.close();
      }
      catch (Exception ex) {
        logger.info("Finish parsing monomers: " + ex.getMessage());
      }

      return ;
    }

    if (CCTParserEnum.MANY_ATOMS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing atoms...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();
      yes_many_atoms = false;
      try {
        atoms = readAtoms(sWriter.toString(), currentMolecule);
        sWriter.close();
      }
      catch (Exception ex) {
        logger.info("Finish parsing atoms: " + ex.getMessage());
      }
      return ;
    }

    if (CCTParserEnum.MANY_BONDS_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing bonds...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();
      //yes_many_atoms = false;
      try {
        bonds = readBonds(sWriter.toString(), currentMolecule);
        sWriter.close();
      }
      catch (Exception ex) {
        System.err.println("Error: parsing bonds: " + ex.getMessage());
      }
      return ;
    }

    if (CCTParserEnum.ATOM_SET_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing atom set...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();
      try {
        AtomicSet aset = readAtomSet(sWriter.toString(), currentMolecule);
        sWriter.close();
        if (aset.size() > 0) {
          this.atomicSets.put(aset.getName(), aset);
        }
      }
      catch (Exception ex) {
        System.err.println("Error: parsing atom set: " + ex.getMessage());
      }
      return ;
    }

    if (CCTParserEnum.PROPERTY_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing property...");
      }
      parseLevel = CCTParserEnum.MOLECULE_TAG.getEnum();

      if (propertyType.equalsIgnoreCase("molecule")) {
        try {
          Object objType = getObjectClass(propertyValue, propertyClass);
          currentMolecule.addProperty(propertyID, objType);
        }
        catch (Exception ex) {
          System.err.println("Error parsing molecule property: " + propertyID + " : " + ex.getMessage());
          System.err.println("  Property value: " + propertyValue + " Ignored... ");
        }

        return ;
      }

      try {
        bonds = readProperty(sWriter.toString(), propertyID, propertyType, propertyFormat, propertyClass, currentMolecule);
        sWriter.close();
      }
      catch (Exception ex) {
        System.err.println("Error: parsing property: " + ex.getMessage());
      }
      return ;
    }

  }

  public void characters(char buf[], int offset, int len) throws
      SAXException {

    if (unrecoverableError) {
      throw new SAXException("Unrecoverable error");
    }
    String s = null;
    //logger.info("BUF: " + offset+" "+len);
    if (Debug) {
      s = new String(buf, offset, len);
      emit(s);
    }

    // --- Parsing atom info

    if (yes_many_atoms) {
      if (parseLevel == CCTParserEnum.MANY_ATOMS_TAG.getEnum()) {
        sWriter.write(buf, offset, len);
      }
    }

    else if (parseLevel == CCTParserEnum.PROPERTY_TAG.getEnum()) {
      sWriter.write(buf, offset, len);
      if (Debug) {
        logger.info("Adding property: " + s);
      }
    }

    else if (parseLevel == CCTParserEnum.MANY_MONOMERS_TAG.getEnum()) {
      sWriter.write(buf, offset, len);
      if (Debug) {
        logger.info("Adding monomer: " + s);
      }
      //currentMolecule.addMonomer(fullString.substring(0,
      //    fullString.indexOf(",")).trim());
    }

    else if (parseLevel == CCTParserEnum.MANY_BONDS_TAG.getEnum()) {
      sWriter.write(buf, offset, len);
      if (Debug) {
        logger.info("Adding bond: " + s);
      }
    }

    else if (parseLevel == CCTParserEnum.ATOM_SET_TAG.getEnum()) {
      sWriter.write(buf, offset, len);
      if (Debug) {
        logger.info("Adding atom set member: " + s);
      }
    }

    //fullString = "";
  }

  //===========================================================
  // Utility Methods ...
  //===========================================================

  // Wrap I/O exceptions in SAX exceptions, to
  // suit handler signature requirements
  private void emit(String s) throws SAXException {
    try {
      //logger.info(s);
      //if ( true ) return;
      out.write("CHARS:" + s);
      out.flush();
    }
    catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
  }

  /*
       private void emit(String s) throws SAXException {
    try {
      out.write(s);
      out.flush();
    }
    catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
       }
   */
  // Start a new line
  private void nl() throws SAXException {
    String lineEnd = System.getProperty("line.separator");
    try {
      out.write(lineEnd);
    }
    catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
  }

  /*
       class tasksEndException
      extends Exception {

       }
   */

  public static List readBonds(String bondsAsString,
                               MoleculeInterface molec) throws
      Exception {
    List bonds = new ArrayList();

    BufferedReader in = null;
    in = new BufferedReader(new StringReader(bondsAsString));

    String line;
    try {
      while ( (line = in.readLine()) != null) {
        if (line.trim().length() == 0) {
          continue; // Ignore empty lines
        }
        //logger.info("Adding bond: " + line);
        try {
          String tokens[] = line.trim().split(",");
          if (tokens.length < 2) {
            System.err.println("Error: uncomplete data for " +
                               bonds.size() +
                               " bond: " + line);
          }

          int a_index = Integer.parseInt(tokens[0]);
          int b_index = Integer.parseInt(tokens[1]);

          AtomInterface a_i = molec.getAtomInterface(a_index);
          AtomInterface a_j = molec.getAtomInterface(b_index);

          BondInterface b = molec.getNewBondInstance(a_i, a_j);
          molec.addBond(b);
        }
        catch (Exception ex) {
          System.err.println("Error: " + ex.getMessage());
          throw new Exception(ex.getMessage());
        }

      }

      in.close();
    }
    catch (Exception ex) {
      throw new Exception(ex.getMessage());
    }

    return bonds;
  }

  public AtomicSet readAtomSet(String atomSetAsString, MoleculeInterface molec) throws
      Exception {
    AtomicSet aset = new AtomicSet(atomSetName);

    BufferedReader in = null;
    in = new BufferedReader(new StringReader(atomSetAsString));

    String line;
    String tokens[];
    try {
      while ( (line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() == 0) {
          continue; // Ignore empty lines
        }
        //logger.info("Adding bond: " + line);
        try {
          tokens = line.split("[, ]");
          if (tokens.length < 1) {
            continue;
          }
        }
        catch (Exception ex) {
          System.err.println("readAtomSet: Error: " + ex.getMessage());
          throw new Exception(ex.getMessage());
        }

        for (int i = 0; i < tokens.length; i++) {
          try {
            int index = Integer.parseInt(tokens[i]);
            if (index < 0 || index >= molec.getNumberOfAtoms()) {
              System.err.println("readAtomSet: index < 0 || index >= molec.getNumberOfAtoms(): Ignored...");
              continue;
            }
            AtomInterface atom = molec.getAtomInterface(index);
            aset.add(atom);
          }
          catch (Exception ex) {
            System.err.println("readAtomSet: Error while parsing atom index: " + tokens[i] + " Ignored...");
          }
        }
      }

      in.close();
    }
    catch (Exception ex) {
      throw new Exception(ex.getMessage());
    }

    return aset;
  }

  public static List readProperty(String propertyAsString, String ID,
                                  String Type,
                                  String Format, String Class,
                                  MoleculeInterface molec) throws
      Exception {
    List props = new ArrayList();

    BufferedReader in = null;
    in = new BufferedReader(new StringReader(propertyAsString));

    boolean indexed = Format.equalsIgnoreCase("indexed");
    boolean atomProp = Type.equalsIgnoreCase("atom");
    boolean bondProp = Type.equalsIgnoreCase("bond");
    boolean moleculeProp = Type.equalsIgnoreCase("molecule");

    if (Debug) {
      logger.info("Parsing property: " + ID);
    }

    String line;
    try {

      if (indexed) { // Indexed property
        while ( (line = in.readLine()) != null) {
          if (line.trim().length() == 0) {
            continue; // Ignore empty lines
          }
          logger.info("Parsing line: " + line);

          String tokens[] = line.trim().split(",");
          if (tokens.length < 2) {
            System.err.println("Error: uncomplete data for " +
                               props.size() + " indexed property: " +
                               line);
            throw new Exception("Error: uncomplete data for " +
                                props.size() + " indexed property: " +
                                line);
          }

          int index = Integer.parseInt(tokens[0]);
          String token = line.substring(line.indexOf(",") + 1, line.length());

          Object objType = getObjectClass(token, Class);
          logger.info("Index: " + index + " property: " + token +
                             " class: " + objType.getClass().getSimpleName());

          if (atomProp) {
            AtomInterface atom = molec.getAtomInterface(index);
            atom.setProperty(ID, objType);
          }
          else if (bondProp) {
            BondInterface bond = molec.getBondInterface(index);
            bond.setProperty(ID, objType);
          }
          else if (moleculeProp) {
            molec.addProperty(ID, objType);
          }
          else {
            throw new Exception("ERROR: Read property: Unknown type: " +
                                Type);
          }

          //props.add(line.trim());
        }

      }

      else { // Property for all entities
        int count = 0;
        while ( (line = in.readLine()) != null) {
          if (line.trim().length() == 0) {
            continue; // Ignore empty lines
          }

          String token = line.trim();
          if (token.equals(NO_PROPERTY_SIGN)) {
            ++count;
            continue;
          }

          Object objType = getObjectClass(token, Class);
          if (Debug) {
            logger.info("Obj type: " +
                               objType.getClass().getSimpleName() +
                               " value: " + objType);
          }

          if (atomProp) {
            AtomInterface atom = molec.getAtomInterface(count);
            atom.setProperty(ID, objType);
          }
          else if (bondProp) {
            BondInterface bond = molec.getBondInterface(count);
            bond.setProperty(ID, objType);
          }
          else if (moleculeProp) {
            molec.addProperty(ID, objType);
          }
          else {
            throw new Exception("ERROR: Read property: Unknown type: " +
                                Type);
          }
          ++count;
        }

        //props.add(line.trim());
      }

      in.close();
    }
    catch (Exception ex) {
      throw new Exception("Parsing property: " + ID + " : " + ex.getMessage());
    }

    return props;
  }

  public static String getObjectAsString(Object data) {
    // String
    if (data instanceof String) {
      return (String) data;
    }
    // --- String[]
    else if (data instanceof String[]) {
      String str[] = (String[]) data;

      String Str = "";
      for (int i = 0; i < str.length; i++) {
        if (i > 0) {
          Str += ",";
        }
        Str += str[i];
      }
      return Str;

    }
    // --- Integer
    else if (data instanceof Integer) {
      Integer i = (Integer) data;
      return i.toString();
    }
    // --- Integer[]
    else if (data instanceof Integer[]) {
      Integer ia[] = (Integer[]) data;

      String str = "";
      for (int i = 0; i < ia.length; i++) {
        if (i > 0) {
          str += ",";
        }
        str += ia[i].toString();
      }
      return str;
    }
    // --- Float
    else if (data instanceof Float) {
      Float f = (Float) data;
      return f.toString();
    }
    // --- Float[]
    else if (data instanceof Float[]) {
      Float ia[] = (Float[]) data;
      String str = "";
      for (int i = 0; i < ia.length; i++) {
        if (i > 0) {
          str += ",";
        }
        str += ia[i].toString();
      }
      return str;
    }
    // --- Double
    else if (data instanceof Double) {
      Double f = (Double) data;
      return f.toString();
    }
    // --- Double[]
    else if (data instanceof Double[]) {
      Double ia[] = (Double[]) data;
      String str = "";
      for (int i = 0; i < ia.length; i++) {
        if (i > 0) {
          str += ",";
        }
        str += ia[i].toString();
      }
      return str;
    }

    // --- Boolean
    else if (data instanceof Boolean) {
      Boolean f = (Boolean) data;
      return f.toString();
    }
    // --- Boolean[]
    else if (data instanceof Boolean[]) {
      Boolean ia[] = (Boolean[]) data;
      String str = "";
      for (int i = 0; i < ia.length; i++) {
        if (i > 0) {
          str += ",";
        }
        str += ia[i].toString();
      }
      return str;
    }

    return data.toString();
  }

  public static Object getObjectClass(String data, String dataClass) throws
      Exception {
    if (dataClass.equalsIgnoreCase("String") ||
        dataClass.endsWith(".String")) {
      return data;
    }
    // --- Integer values
    else if (dataClass.equalsIgnoreCase("Integer") ||
             dataClass.endsWith(".Integer")) {
      try {
        int n = Integer.parseInt(data);
        Integer i = new Integer(n);
        return i;
      }
      catch (Exception ex) {
        System.err.println("ERROR: Wrong integer value: " + data);
        throw ex;
      }
    }
    // --- Integer array values
    else if (dataClass.equalsIgnoreCase("Integer[]") ||
             dataClass.endsWith(".Integer[]")) {
      String tokens[] = data.split(",");
      Integer ia[] = new Integer[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
        try {
          ia[i] = new Integer(tokens[i]);
        }
        catch (Exception ex) {
          System.err.println("ERROR: Wrong integer value: " + tokens[i]);
          throw ex;
        }
      }
      return ia;
    }
    // --- Boolean values
    else if (dataClass.equalsIgnoreCase("Boolean") ||
             dataClass.endsWith(".Boolean")) {
      try {
        Boolean i = new Boolean(data);
        return i;
      }
      catch (Exception ex) {
        System.err.println("ERROR: Wrong Boolean value: " + data);
        throw ex;
      }
    }
    // --- Boolean array values
    else if (dataClass.equalsIgnoreCase("Boolean[]") ||
             dataClass.endsWith(".Boolean[]")) {
      String tokens[] = data.split(",");
      Boolean ia[] = new Boolean[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
        try {
          ia[i] = new Boolean(tokens[i]);
        }
        catch (Exception ex) {
          System.err.println("ERROR: Wrong Boolean value: " + tokens[i]);
          throw ex;
        }
      }
      return ia;
    }

    // --- Float value
    else if (dataClass.equalsIgnoreCase("Float") ||
             dataClass.endsWith(".Float")) {
      try {
        Float f = new Float(data);
        return f;
      }
      catch (Exception ex) {
        System.err.println("ERROR: Wrong float value: " + data);
        throw ex;
      }
    }
    // --- Float array values
    else if (dataClass.equalsIgnoreCase("Float[]") ||
             dataClass.endsWith(".Float[]")) {
      String tokens[] = data.split(",");
      Float ia[] = new Float[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
        try {
          ia[i] = new Float(tokens[i]);
        }
        catch (Exception ex) {
          System.err.println("ERROR: Wrong float value: " + tokens[i]);
          throw ex;
        }
      }
      return ia;
    }
    // --- Double value
    else if (dataClass.equalsIgnoreCase("Double") ||
             dataClass.endsWith(".Double")) {
      try {
        Double f = new Double(data);
        return f;
      }
      catch (Exception ex) {
        System.err.println("ERROR: Wrong double value: " + data);
        throw ex;
      }
    }
    // --- Double array values
    else if (dataClass.equalsIgnoreCase("Double[]") ||
             dataClass.endsWith(".Double[]")) {
      String tokens[] = data.split(",");
      Double ia[] = new Double[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
        try {
          ia[i] = new Double(tokens[i]);
        }
        catch (Exception ex) {
          System.err.println("ERROR: Wrong double value: " + tokens[i]);
          throw ex;
        }
      }
      return ia;
    }

    return data;
  }

  public static List readAtoms(String bondsAsString, MoleculeInterface molec) throws
      Exception {
    List at = new ArrayList();
    BufferedReader in = null;
    in = new BufferedReader(new StringReader(bondsAsString));

    String line;
    try {
      while ( (line = in.readLine()) != null) {
        if (line.trim().length() == 0) {
          continue;
        }
        //logger.info("Adding atom: " + line);
        try {
          String tokens[] = line.trim().split(",");
          if (tokens.length < 5) {
            System.err.println("Error: uncomplete data for " + at.size() + " atom: " + line);
          }
          AtomInterface atom = molec.getNewAtomInstance();
          atom.setAtomicNumber(Integer.parseInt(tokens[0]));
          atom.setName(tokens[1]);
          atom.setXYZ(Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]));
          if (tokens.length >= 6) {
            atom.setSubstructureNumber(Integer.parseInt(tokens[5]));
          }
          if (tokens.length >= 7) {
            atom.setProperty(AtomInterface.ATOMIC_CHARGE, new Float(tokens[6]));
          }
          at.add(atom);
        }
        catch (Exception ex) {
          System.err.println("Error: " + ex.getMessage());
          throw ex;
        }

      }

      in.close();
    }
    catch (Exception ex) {
      throw ex;
    }

    for (int i = 0; i < at.size(); i++) {
      AtomInterface atom = (AtomInterface) at.get(i);
      molec.addAtom(atom, atom.getSubstructureNumber());
    }

    return at;
  }

  /**
   * Reads list of monomers
   * @param monomersAsString String
   * @param molec MoleculeInterface
   * @return List
   * @throws Exception
   */
  public static List readMonomers(String monomersAsString, MoleculeInterface molec) throws
      Exception {
    List monomers = new ArrayList();
    BufferedReader in = null;
    in = new BufferedReader(new StringReader(monomersAsString));

    String line;
    try {
      while ( (line = in.readLine()) != null) {
        if (line.trim().length() == 0) {
          continue;
        }
        //logger.info("Adding monomer: " + line);
        molec.addMonomer(line);
        monomers.add(line);
      }

      in.close();
    }
    catch (Exception ex) {
      throw ex;
    }

    return monomers;
  }

}
