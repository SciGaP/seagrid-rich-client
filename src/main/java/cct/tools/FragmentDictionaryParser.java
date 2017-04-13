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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 * <fragmentDictionary name=dic_name>
 * <fragment name=fragment_name url=relative_url/>
 * </fragmentDictionary>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class FragmentDictionaryParser
    extends DefaultHandler {

  static boolean Debug = true;

  Map dictionaryTree = new LinkedHashMap();
  List dicStack = new ArrayList();

  static final String INPUT_SOURCE_TAG = "inputSource";
  static final String DICTIONARY_TAG = "fragmentDictionary";
  static final String VERSION_TAG = "version";
  static final String FRAGMENT_TAG = "fragment";

  boolean yes_dictionary = false;
  boolean finish_parsing = false;

  boolean unrecoverableError = false;
  String currentTag = null;

  StringReader sReader = null;
  StringWriter sWriter = null;

  static private Writer out;
  private InputStream is = null;
  String fragContext = null;
  URL fragmentDictionary = null;
  Map fragmentsReferences = new HashMap();
  static final Logger logger = Logger.getLogger(FragmentDictionaryParser.class.getCanonicalName());

  public FragmentDictionaryParser() {

  }

  public MoleculeInterface loadFragment(String specification) throws Exception {

    InputStream is = getInputStream(specification);

    MoleculeInterface m = new Molecule();
    CCTParser cctParser = new CCTParser(m);
    List mols = cctParser.parseCCTFile(is, m);
    m = (MoleculeInterface) mols.get(0);
    logger.info("Number of atoms: " + m.getNumberOfAtoms());

    return m;
  }

  public InputStream getInputStream(String specification) throws Exception {
    String urlAddress = fragContext + specification;
    URL url = null;
    try {
      url = new URL(urlAddress);
    }
    catch (java.net.MalformedURLException ex) {
      throw new Exception("Error Loading fragment: " + specification + " : " + ex.getMessage());
    }

    InputStream is = null;
    try {
      is = url.openStream();
    }
    catch (IOException ex) {
      throw new Exception("Error Loading fragment: " + specification + " : " + ex.getMessage());
    }
    return is;
  }

  /*
      public FragmentDictionaryParser(MoleculeInterface molecule) {
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
   */

  public static Map parseFragmentDictionary(Object inputSource) {
    DefaultHandler handler = new FragmentDictionaryParser();
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();

    try {
      // Set up output stream
      out = new OutputStreamWriter(System.out, "UTF8");

      // Parse the input
      SAXParser saxParser = factory.newSAXParser();

      if (inputSource instanceof URL) {
        URL file_name_location = (URL) inputSource;

        URI uri = new URI(file_name_location.getPath()); // file_name_location.toURI();
        uri.normalize();
        logger.info("URI: " + uri.toASCIIString());
        saxParser.parse(new File(uri), handler);
      }
      else if (inputSource instanceof InputStream) {
        InputStream is = (InputStream) inputSource;
        saxParser.parse(is, handler);
      }
    }
    catch (Throwable t) {
      //logger.error("Parsing error " + t.getMessage());
      t.printStackTrace();
    }

    FragmentDictionaryParser parser = (FragmentDictionaryParser) handler;
    parser.setInputSource(inputSource);

    return parser.getDictionaryTree();
  }

  private static void saveCCTFile(List mols, String fileName) throws
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

    /*
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
          ArrayList substr = molec.getMolecularSubstructure();

          atts.clear();
          atts.addAttribute("", "", "number", "CDATA",
                            String.valueOf(substr.size()));
          hd.startElement("", "", CCTParserEnum.MANY_MONOMERS_TAG.toString(),
                          atts);
          //hd.startCDATA();

          hd.characters("\n".toCharArray(), 0, 1);

          Iterator iter = substr.iterator();
          while (iter.hasNext()) {
             String data = (String) iter.next() + "\n";
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
           catch (org.xml.sax.SAXException e) {
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
     */

  }

  public Map getDictionaryTree() {
    return dictionaryTree;
  }

  public void setInputSource(Object inputSource) {
    dictionaryTree.put(INPUT_SOURCE_TAG, inputSource);
  }

  @Override
  public void startDocument() throws SAXException {
    if (Debug) {
      emit("<?xml version='1.0' encoding='UTF-8'?>");
      nl();
    }
  }

  @Override
  public void endDocument() throws SAXException {
    try {
      nl();
      out.flush();
    }
    catch (IOException e) {
      throw new SAXException("I/O error", e);
    }
  }

  @Override
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

    if (DICTIONARY_TAG.equalsIgnoreCase(eName)) {
      yes_dictionary = true;
      if (Debug) {
        logger.info("Starting parsing fragment dictionary...");
      }
      Map oneMoreDic = new LinkedHashMap();
      if (dicStack.size() == 0) { // Root
        dicStack.add(oneMoreDic);
      }
      else {
        Map parent = (Map) dicStack.get(dicStack.size() -
                                        1); // Get last dic
        String dicName = attrs.getValue("name");
        if (dicName == null) { // Error

        }
        parent.put(dicName, oneMoreDic);
        dicStack.add(oneMoreDic);
      }
      return;
    }

    if (!yes_dictionary) {
      return; // Don't find <fragmentDcitionary> tag yet...
    }

    if (FRAGMENT_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Starting parsing fragment info");
      }
      String fragName = attrs.getValue("name");
      String fragURL = attrs.getValue("url");

      if (fragName == null) { // Error

      }
      if (fragURL == null) { // Error

      }

      Map currentDic = (Map) dicStack.get(dicStack.size() - 1);
      currentDic.put(fragName, fragURL);
    }
  }

  @Override
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

    if (DICTIONARY_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing dictionary...");
      }
      if (dicStack.size() == 1) {
        finish_parsing = true;
        this.dictionaryTree = (Map) dicStack.get(0);
      }
      dicStack.remove(dicStack.size() - 1); // Remove last dictionary in stack
      return;
    }

    if (FRAGMENT_TAG.equalsIgnoreCase(eName)) {
      if (Debug) {
        logger.info("Finish parsing fragment...");
      }
      return;
    }

  }

  @Override
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

    /*
           if (yes_many_atoms) {
       if (parseLevel == CCTParserEnum.MANY_ATOMS_TAG.getEnum()) {
          sWriter.write(buf, offset, len);
       }
           }
     */

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

  public void setFragmentDictionary(URL fragmentDic) throws Exception {

    fragmentDictionary = fragmentDic;
    fragmentsReferences.clear();

    if (fragmentDic.getProtocol().equalsIgnoreCase("jar") || fragmentDic.getProtocol().equalsIgnoreCase("zip")) {
      fragContext = fragmentDic.toString();
      fragContext = fragContext.substring(0, fragContext.indexOf("!/") + 2);
    }
    else {
      throw new Exception("setFragmentsBaseDirectory: don't know how to work with protocol: " + fragmentDic.getProtocol());
    }

    logger.info("Protocol: " + fragmentDic.getProtocol() + " Context: " + fragContext);
  }

  public void saveFragmentDictionary(Map fragTree, String fileName) throws Exception {

    Object obj = fragTree.get(INPUT_SOURCE_TAG);
    if (obj instanceof InputStream) {
      is = (InputStream) obj;
    }
    else {
      throw new Exception("Cannot handle InputSource " + obj.getClass().getCanonicalName());
    }

    if (!fileName.endsWith(".dic") && !fileName.endsWith(".DIC")) {
      fileName += ".dic";
    }

    FileOutputStream fos = new FileOutputStream(fileName);
    ZipOutputStream zos = new ZipOutputStream(fos);

    // --- Create dictionary descriptor

    //Create instance of DocumentBuilderFactory
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //Get the DocumentBuilder
    DocumentBuilder parser = factory.newDocumentBuilder();
    //Create blank DOM Document
    Document doc = parser.newDocument();
    //create the root element
    Element root = doc.createElement(DICTIONARY_TAG);
    root.setAttribute("name", "root");
    root.setAttribute(VERSION_TAG, "1.0");
    //add it to the xml tree
    doc.appendChild(root);
    //create a comment
    Comment comment = doc.createComment("This is a main root element for dictionary");
    //add in the root element
    root.appendChild(comment);

    addBranch(fragTree, doc, root, zos);

    // --- Finally, save dictionary descriptor

    ZipEntry entry = new ZipEntry(cct.GlobalSettings.getDefaultFragmentDictionary());
    zos.putNextEntry(entry);
    // Prepare the DOM document for writing
    javax.xml.transform.Source source = new DOMSource(doc);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Result result = new StreamResult(baos);

    // Write the DOM document to the file
    Transformer xformer = TransformerFactory.newInstance().newTransformer();
    //xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    xformer.setOutputProperty(OutputKeys.INDENT, "yes");

    xformer.transform(source, result);
    zos.write(baos.toByteArray());
    zos.closeEntry();

    zos.close();
  }

  private void addBranch(Map fragTree, Document doc, Element root, ZipOutputStream zos) throws Exception {
    Iterator iter = fragTree.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String key = me.getKey().toString();
      Object obj = me.getValue();
      if (obj instanceof Map) {
        Element child = doc.createElement(DICTIONARY_TAG);
        child.setAttribute("name", key);
        root.appendChild(child);
        Map childTree = (Map) fragTree.get(key);
        addBranch(childTree, doc, child, zos);
      }
      else if (obj instanceof String) {

        Element child = doc.createElement(FRAGMENT_TAG);
        child.setAttribute("name", key);
        child.setAttribute("url", obj.toString());
        root.appendChild(child);

        InputStream is = getInputStream(obj.toString());
        ZipEntry entry = new ZipEntry(obj.toString());
        zos.putNextEntry(entry);
        byte[] c = new byte[1];
        while ( (c[0] = (byte) is.read()) != -1) {
          zos.write(c);
        }
        zos.closeEntry();
      }
      else {
        System.err.println("addBranch: unknown class" + obj.getClass().getCanonicalName());
      }

    }

  }

}
