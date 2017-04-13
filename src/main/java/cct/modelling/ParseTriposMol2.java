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

package cct.modelling;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.MonomerInterface;

/**
 *
 * <p>Title: ParseTriposMol2</p>
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
public class ParseTriposMol2
    implements AtomProperties {
   private ParseTriposMol2() {
   }

   static final Logger logger = Logger.getLogger(ParseTriposMol2.class.getCanonicalName());


   public static void parseMol2File(String filename, MoleculeInterface mol) {
      //Molecule mol = null;
      AtomInterface atom;
      BondInterface bond;
      String line;
      StringTokenizer st;
      Integer natoms = new Integer(0), nbonds = new Integer(0),
          nresidues = new Integer(0);
      Float cartes = new Float(0.0f);

      try {
         BufferedReader in = new BufferedReader(new FileReader(filename));
         //mol = new Molecule();
         while ( (line = in.readLine()) != null) {
            //line.trim();

            if (line.compareToIgnoreCase("@<TRIPOS>MOLECULE") == 0) {
               // Read Molecule name
               line = in.readLine();
               //line.trim();
               mol.setName(line);

               // read number of atoms & bonds
               line = in.readLine();
               st = new StringTokenizer(line, " ");

               try {
                  natoms = new Integer(st.nextToken());
                  nbonds = new Integer(st.nextToken());
                  nresidues = new Integer(st.nextToken());
               }
               catch (NumberFormatException e) {
                  break;
               }

            }

            else if (line.compareToIgnoreCase("@<TRIPOS>ATOM") == 0) {
               //@<TRIPOS>ATOM
               //  1 P           5.9733   -6.8476   27.4300 P.3       1 A1          0.0000 BACKBONE|DICT|DIRECT
               //  2 O1P         6.2573   -8.2786   27.2100 O.co2     1 A1          0.0000 BACKBONE|DICT
               int current_subst_id = 0;

               for (int i = 0; i < natoms.intValue(); i++) {
                  if ( (line = in.readLine()) == null) {
                     break;
                  }
                  st = new StringTokenizer(line, " ");

                  boolean new_residue = false;
                  String substr = null;

                  st.nextToken(); // skip first token

                  atom = mol.getNewAtomInstance(); // gAtom();
                  atom.setProperty(AtomInterface.NAME, st.nextToken());

                  // --- Read in coordinates
                  if (st.countTokens() < 3) {
                     break; // Actually it's an error
                  }

                  try {
                     atom.setX(Float.parseFloat(st.nextToken()));
                     atom.setY(Float.parseFloat(st.nextToken()));
                     atom.setZ(Float.parseFloat(st.nextToken()));

                  }
                  catch (NumberFormatException e) {
                     logger.info(
                         "Error converting cartesin coordinates\n");
                     break;
                  }

                  if (st.countTokens() > 0) { // Parse Tripos Atom Type
                     String triposName = st.nextToken();
                     atom.setProperty(AtomInterface.SYBYL_TYPE,
                                      triposName);
                     atom.setAtomicNumber(triposAtomTypeToElement(
                         triposName));
                  }
                  // ---
                  int subst_id = current_subst_id;
                  if (st.countTokens() > 0) { // Parse the ID number of the substructure containing the atom
                     try {
                        subst_id = Integer.parseInt(st.nextToken());
                        --subst_id;
                     }
                     catch (NumberFormatException e) {
                        logger.info(
                            "Error converting ID number of the substructure containing the atom");
                        break;
                     }

                     if (subst_id < 0) {
                        logger.info(
                            "Error in MOL2 file: substructure ID is negative: " + subst_id);
                        break;
                     }

                     if (subst_id > current_subst_id) {
                        new_residue = true;
                        current_subst_id = subst_id;
                     }
                     //a.setSubstructureNumber(Integer.parseInt(st.
                     //        nextToken()));
                  }

                  if (st.countTokens() > 0) { // Parse the name of the substructure containing the atom
                     //if ( new_residue ) {
                     StringTokenizer res = new StringTokenizer(st.nextToken(), "0123456789");
                     substr = res.nextToken();
                     //}
                     //else
                     //     st.nextToken(); // Skip token
                  }

                  if (st.countTokens() > 0) {
                     atom.setProperty(AtomInterface.ATOMIC_CHARGE,
                                      st.nextToken());
                  }

                  logger.info(atom.getAtomicNumber() + " XYZ: " +
                                     atom.getX() + " " + atom.getY() + " " +
                                     atom.getZ());
                  if (substr == null) {
                     substr = "UNK";
                  }
                  mol.addAtom(atom, subst_id, substr);
               }

               logger.info("Number of atoms in " + filename + " : " +
                                  mol.getNumberOfAtoms());
               for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
                  atom = mol.getAtomInterface(i);
                  logger.info(i + " " + atom.getName() + " " + atom.getSubstructureNumber());
               }
               for (int i = 0; i < mol.getNumberOfMonomers(); i++) {
                  MonomerInterface m = mol.getMonomerInterface(i);
                  logger.info("Res: " + i + " " + m.getName() + " atoms:" + m.getNumberOfAtoms());
               }

            }

            else if (line.compareToIgnoreCase("@<TRIPOS>BOND") == 0) {

               for (int i = 0; i < nbonds.intValue(); i++) {
                  if ( (line = in.readLine()) == null) {
                     break;
                  }
                  st = new StringTokenizer(line, " ");

                  // Inform about error and continue....
                  if (st.countTokens() < 3) {
                     logger.info(
                         "Uncomplete BOND record... Continuing...\n");
                     continue;
                  }

                  st.nextToken(); // skip first token

                  int a1, a2;
                  try {
                     a1 = Integer.parseInt(st.nextToken()) - 1;
                     a2 = Integer.parseInt(st.nextToken()) - 1;
                  }
                  catch (NumberFormatException e) {
                     logger.info(
                         "Error converting bonded atoms\n");
                     break;
                  }

                  AtomInterface a_i = mol.getAtomInterface(a1);
                  AtomInterface a_j = mol.getAtomInterface(a2);
                  bond = mol.getNewBondInstance(a_i, a_j);
                  mol.addBond(bond);
                  //mol.createNewBond(a_i, a_j);

               }

               logger.info("Number of bonds: " +
                                  mol.getNumberOfBonds());

            }

         } // --- End of while

      }
      catch (IOException e) {
         logger.info("parseMol2File: " + e.getMessage() + "\n");
         return;
      }
      /*
               try {
       StreamTokenizer tok = new StreamTokenizer( new FileReader( filename ));
          tok.resetSyntax();
          tok.wordChars(1,255);
          tok.whitespaceChars( ' ', ' ');
          //tok.eolIsSignificant( true );

          mol = new Molecule();

          while( tok.nextToken() != tok.TT_EOF ) {

          }
               } catch ( IOException e ) {

               }
       */

      return ;
   }

   //**************************************************************************
    public static void saveTriposMol2File(MoleculeInterface m, String filename) {

       FileOutputStream out;
       try {
          out = new FileOutputStream(filename);
       }
       catch (java.io.FileNotFoundException e) {
          System.err.println(filename + " not found\n");
          return;
       }
       catch (SecurityException e) {
          System.err.println(filename + ": " + e.getMessage());
          return;
       }
       catch (IOException e) {
          logger.info(filename + ": " + e.getMessage());
          return;
       }

       try {
          out.write( ("@<TRIPOS>MOLECULE\n").getBytes());
          out.write( (m.getName() + "\n").getBytes());
          out.write( (m.getNumberOfAtoms() + "\n").getBytes());
          out.write( ("@<TRIPOS>ATOM\n").getBytes());
          for (int i = 0; i < m.getNumberOfAtoms(); i++) {
             AtomInterface a = m.getAtomInterface(i);
             out.write( ( (i + 1) + " " + a.getName()).getBytes());
             out.write( (" " + a.getX() + " " + a.getY() + " " + a.getZ()).
                       getBytes());
             out.write( (" " + a.getProperty(AtomInterface.SYBYL_TYPE)).
                       getBytes());
             out.write( (" " + a.getSubstructureNumber()).getBytes());
             out.write( (" " + "XXX").getBytes());
             out.write( (" " + a.getProperty(AtomInterface.ATOMIC_CHARGE)).
                       getBytes());
             out.write( ("\n").getBytes());
          }
          out.close();
       }
       catch (IOException e) {
          logger.info("Error writing into " + filename);
          return;
       }

    }

   //**************************************************************************
    public static int triposAtomTypeToElement(String atomType) {
       StringTokenizer tok = new StringTokenizer(atomType, " .");
       return ChemicalElements.getAtomicNumber(tok.nextToken());
    }
}
