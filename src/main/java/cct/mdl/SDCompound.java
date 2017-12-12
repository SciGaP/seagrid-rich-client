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

import cct.interfaces.MoleculeInterface;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

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
public class SDCompound {

  private String MoleculeName;
  private MoleculeInterface Molecule = null;
  private RandomAccessFile sdFile = null;
  private long compoundPosition = 0;
  private boolean loaded = false;

  public SDCompound(RandomAccessFile raf, long position, MoleculeInterface molec) {
    sdFile = raf;
    compoundPosition = position;
    Molecule = molec;
  }

  public void setMoleculename(String name) {
    this.MoleculeName = name;
  }

  public String getMoleculeName() {
    return MoleculeName;
  }

  public MoleculeInterface getMolecule() {
    return Molecule;
  }

  public long getCompoundPosition() {
    return compoundPosition;
  }

  public boolean isLoaded() {
    return loaded;
  }

  public MoleculeInterface loadCompound() throws Exception {
    sdFile.seek(compoundPosition);
    MDLMol sdf = new MDLMol();
    sdf.parseFile(sdFile, Molecule);
    loaded = true;
    return Molecule;
  }

  public MoleculeInterface loadCompound(MoleculeInterface mol) throws Exception {
    sdFile.seek(compoundPosition);
    MDLMol sdf = new MDLMol();
    sdf.parseFile(sdFile, mol);
    Molecule = mol;
    return Molecule;
  }

}
