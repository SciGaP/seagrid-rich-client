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
package cct.mdl;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Logger;

import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class SDF {

  static final Logger logger = Logger.getLogger(SDF.class.getCanonicalName());
  private RandomAccessFile sdFile = null;
  private java.util.List<SDCompound> Compounds = null;
  private MoleculeInterface molecule;

  public SDF() {
  }

  public MoleculeInterface getMolecule() {
    return molecule;
  }

  public void setMolecule(MoleculeInterface molecule) {
    this.molecule = molecule;
  }

  public int getNumberCompounds() {
    if (Compounds == null) {
      return 0;
    }
    return Compounds.size();
  }

  public MoleculeInterface loadCompound(int index) throws Exception {
    SDCompound sdc = Compounds.get(index);
    if (sdc.isLoaded()) {
      return sdc.getMolecule();
    }

    sdc.loadCompound();
    return sdc.getMolecule();
  }
  
  public MoleculeInterface loadCompound(int index, MoleculeInterface mol) throws Exception {
    SDCompound sdc = Compounds.get(index);
    if (sdc.isLoaded()) {
      return sdc.getMolecule();
    }

    sdc.loadCompound(mol);
    return sdc.getMolecule();
  }

  public void parseSDFile(String file_name, MoleculeInterface molec) throws Exception {

    // ---- Open file
    try {
      sdFile = new RandomAccessFile(file_name, "r");
    } catch (Exception ex) {
      throw new Exception("Cannot open file " + file_name + " : " + ex.getMessage());
    }

    // --- Start to read file
    String line;
    long fileSize = sdFile.getChannel().size();
    int estimatedComps = (int) (fileSize / 3072l) + 1;
    Compounds = new ArrayList<SDCompound>();
    if (Compounds instanceof ArrayList) {
      ((ArrayList) Compounds).ensureCapacity(estimatedComps);
    }
    logger.info("Allocated memory for estimated " + estimatedComps + " compounds");

    try {
      boolean newCompound = false;
      long position = sdFile.getFilePointer();
      if ((line = sdFile.readLine()) == null) {
        throw new Exception("Unexpected end-of-file while reading the first compound..");
      }
      SDCompound compound = new SDCompound(sdFile, position, molec != null ? molec.getInstance() : molec);
      compound.setMoleculename(line.trim());
      Compounds.add(compound);

      while ((line = sdFile.readLine()) != null) {
        if (line.equals("$$$$")) {
          position = sdFile.getFilePointer();
          if ((line = sdFile.readLine()) == null) {
            System.err.println("Unexpected end-of-file while reading the " + Compounds.size() + " compound. Ignoring...");
            break;
          }

          compound = new SDCompound(sdFile, position, molec != null ? molec.getInstance() : molec);
          compound.setMoleculename(line.trim());
          Compounds.add(compound);
          if (Compounds.size() % 10000 == 0) {
            double bytes_per_comp = (double) position / (double) Compounds.size();
            double estim_comps = (double) fileSize / bytes_per_comp;
            System.out.println("..." + Compounds.size() + " ~"
                + String.format("%6.2f", ((double) Compounds.size() / estim_comps * 100.0)) + "%");
          }
        }
      }

      logger.info("Found compounds " + Compounds.size());
    } catch (Exception ex) {
      throw new Exception(" : " + ex.getMessage());
    }

  }

  public static void main(String[] args) {
    FileDialog fd = new FileDialog(new Frame(), "Open SDF File", FileDialog.LOAD);
    fd.setFile("*.sdf");
    fd.setVisible(true);
    if (fd.getFile() == null) {
      System.exit(0);
    }
    String fileName = fd.getFile();
    String workingDirectory = fd.getDirectory();

    SDF sdf = new SDF();

    MoleculeInterface molec = new Molecule();
    try {
      sdf.parseSDFile(workingDirectory + fileName, molec);
      for (int i = 0; i < sdf.getNumberCompounds(); i++) {
        MoleculeInterface mol = sdf.loadCompound(i);
        System.out.println("Loaded " + mol.getName() + " Numat: " + mol.getNumberOfAtoms());
        Map map = mol.getProperties();
        for (Object obj : map.keySet()) {
          System.out.print(obj.toString() + ": ");
          Object val = map.get(obj);
          if (val instanceof Object[]) {
            Object[] array = (Object[]) val;

            for (Object iter : array) {
              System.out.print("\n ---> " + iter.toString() + " ");
            }
            System.out.println();
          } else {
            System.out.println(val.toString());
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.exit(0);
  }
}
