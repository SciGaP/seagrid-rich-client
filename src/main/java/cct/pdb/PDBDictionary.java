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

package cct.pdb;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDBDictionary {

  static final String ROOT = "PDBDictionary";
  static final String RESIDUE_ELEMENT = "Residue";
  static final String ATOM_ELEMENT = "Atom";
  static final String BOND_ELEMENT = "Bond";

  static final String DICTIONARY_NAME_ATTRIBUTE = "name";

  static final String RESIDUE_NAME_ATTRIBUTE = "name";

  static final String ATOM_NAME_ATTRIBUTE = "name";
  static final String ATOM_ELEMENT_ATTRIBUTE = "element";
  static final String ATOM_CCTTYPE_ATTRIBUTE = "cct-type";

  static final String BOND_A1_ATTRIBUTE = "a1";
  static final String BOND_A2_ATTRIBUTE = "a2";

  static PDBDictionary defaultDictionary = null;

  String dicName;
  private Map<String, PDBResidue> Residues = new LinkedHashMap<String, PDBResidue> ();

  public PDBDictionary(String name) {
    dicName = name;
    if (defaultDictionary == null) {

    }
  }

  public void addResidue(PDBResidue res) throws Exception {
    if (Residues.containsKey(res.residueName)) {
      throw new Exception("Dcitionary " + dicName + " already has residue named " + res.residueName);
    }
    Residues.put(res.residueName, res);
  }

  private PDBDictionary() throws Exception {
    dicName = "default";
    PDBResidue res;
    PDBAtom atom;
    // --- Setup Glycine
    /*
     ATOM    154  N   GLY A 702      10.474   9.811  66.187  1.00 29.99           N
     ATOM    155  CA  GLY A 702       9.179  10.419  65.925  1.00 32.21           C
     ATOM    156  C   GLY A 702       8.818  10.120  64.473  1.00 39.13           C
     ATOM    157  O   GLY A 702       8.145  10.902  63.803  1.00 40.76           O

     */

    res = new PDBResidue("GLY", MONOMER_TYPE.AMINOACID);
    atom = new PDBAtom("N", 7, "N.3");
    res.addAtom(atom);
    atom = new PDBAtom("CA", 6, "C.4");
    res.addAtom(atom);
    atom = new PDBAtom("C", 6, "C.3");
    res.addAtom(atom);
    atom = new PDBAtom("O", 8, "O.2");
    res.addAtom(atom);

    atom = new PDBAtom("H", 1, "H");
    res.addAtom(atom);
    atom = new PDBAtom("HA1", 1, "H");
    res.addAtom(atom);
    atom = new PDBAtom("HA2", 1, "H");
    res.addAtom(atom);

    res.addBond("N", "CA");
    res.addBond("CA", "C");
    res.addBond("C", "O");

    res.addBond("N", "H");
    res.addBond("CA", "HA1");
    res.addBond("CA", "HA2");

    this.addResidue(res);

    // --- Setup ALA
    /*
     ATOM    149  N   ALA A 701      12.755   8.724  67.096  1.00 23.60           N
     ATOM    150  CA  ALA A 701      12.882   9.578  65.929  1.00 26.37           C
     ATOM    151  C   ALA A 701      11.576  10.323  65.649  1.00 29.47           C
     ATOM    152  O   ALA A 701      11.579  11.354  64.970  1.00 28.14           O
     ATOM    153  CB  ALA A 701      13.272   8.762  64.709  1.00 27.79           C

     */

  }

  public static PDBDictionary setupDefaultDictionary(PDBDictionary dic) {
    try {
      dic = new PDBDictionary();
    }
    catch (Exception ex) {
      System.err.println("INTERNAL ERROR: Cannot setup default PDB dictionary");
    }
    return dic;
  }

  public void saveDictionary(String file_name) throws Exception {

    File file = new File(file_name);

    //Create instance of DocumentBuilderFactory
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //Get the DocumentBuilder
    DocumentBuilder parser = factory.newDocumentBuilder();
    //Create blank DOM Document
    Document doc = parser.newDocument();

    //create the root element
    Element root = doc.createElement(ROOT);
    //all it to the xml tree
    doc.appendChild(root);

    root.setAttribute(DICTIONARY_NAME_ATTRIBUTE, dicName);

    // create a comment
    Comment comment = doc.createComment("Dictionary of PDB residues");
    //add in the root element
    root.appendChild(comment);

    // --- Start to add residues

    Set set = Residues.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String resName = me.getKey().toString();
      PDBResidue res = (PDBResidue) me.getValue();

      // create residue element
      Element residueElement = doc.createElement(RESIDUE_ELEMENT);
      //Add the atribute to the child
      residueElement.setAttribute(RESIDUE_NAME_ATTRIBUTE, resName);

      // --- create atom elements

      for (int i = 0; i < res.countAtoms(); i++) {
        PDBAtom atom = res.getAtom(i);

        // create atom element
        Element atomElement = doc.createElement(ATOM_ELEMENT);
        //Add the atribute to the child
        atomElement.setAttribute(ATOM_NAME_ATTRIBUTE, atom.name);
        atomElement.setAttribute(ATOM_ELEMENT_ATTRIBUTE, String.valueOf(atom.element));
        atomElement.setAttribute(ATOM_CCTTYPE_ATTRIBUTE, atom.cctAtomType);

        residueElement.appendChild(atomElement);
      }

      // --- create bond elements

      for (int i = 0; i < res.countBonds(); i++) {
        PDBBond bond = res.getBond(i);

        // create bond element
        Element bondElement = doc.createElement(ATOM_ELEMENT);
        //Add the atribute to the child
        bondElement.setAttribute(BOND_A1_ATTRIBUTE, bond.i_name);
        bondElement.setAttribute(BOND_A2_ATTRIBUTE, bond.j_name);

        residueElement.appendChild(bondElement);
      }

      root.appendChild(residueElement);
    }

    // An finally we will print the DOM tree

    TransformerFactory tranFactory = TransformerFactory.newInstance();
    Transformer aTransformer = tranFactory.newTransformer();

    Source src = new DOMSource(doc);
    //Result dest = new StreamResult(System.out);
    Result dest = new StreamResult(file);
    aTransformer.transform(src, dest);

  }

  public static void main(String[] args) {
    PDBDictionary dic = new PDBDictionary("Test");
    dic = PDBDictionary.setupDefaultDictionary(dic);
    try {
      dic.saveDictionary("dic-test.xml");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
