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

import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GaussianAtom {

  static public String PDB_NAME_KEYWORD = "PDBName".toUpperCase();
  static public String RES_NAME_KEYWORD = "ResName".toUpperCase();
  static public String RES_NUM_KEYWORD = "ResNum".toUpperCase();
  public String name = null;
  public String atomType = null;
  public String extraParam = null;
  private String PDBName, ResName;
  private int ResNum = -01;
  public String parxyz[] = new String[3];
  public String parijk[] = new String[3];
  public int element = 0;
  public float charge = 0.0f;
  public float xyz[] = new float[3];
  public int ijk[] = new int[3];
  public int alternateZmatrixFormat = 0; // Regular Z-matrix, +1 or -1 otherwise
  boolean frozen = false;
  String Layer = null;
  ONIOM_LAYER ONIOM_Layer = ONIOM_LAYER.HIGH;
  String link_atom = null;
  int Bonded_to = -1;
  float scale_fac1, scale_fac2, scale_fac3;
  private boolean dummy = false;
  static final Logger logger = Logger.getLogger(GaussianAtom.class.getCanonicalName());

  public GaussianAtom() {
    parxyz[0] = parxyz[1] = parxyz[2] = null;
    parijk[0] = parijk[1] = parijk[2] = null;
    element = 0;
    xyz[0] = xyz[1] = xyz[2] = 0.0f;
    ijk[0] = ijk[1] = ijk[2] = 0;
  }

  public GaussianAtom(AtomInterface a) {
    super();
    element = a.getAtomicNumber();
    xyz[0] = a.getX();
    xyz[1] = a.getY();
    xyz[2] = a.getZ();
    name = a.getName();
  }

  public int getElement() {
    return element;
  }

  public boolean isDummy() {
    return dummy;
  }

  public void setDummy(boolean is_dummy) {
    dummy = is_dummy;
  }

  public float getX() {
    return xyz[0];
  }

  public float getY() {
    return xyz[1];
  }

  public float getZ() {
    return xyz[2];
  }

  public void parseAtomName(String aname, boolean complex_name) throws Exception {
    String token, atomName = aname;
    StringTokenizer st = null;
    boolean minusCharge = atomName.regionMatches(0, "--", 0, 2);

    if (complex_name) {
      // C(PDBName=C3,ResName=NH3,ResNum=1)
      name = atomName.substring(0, atomName.indexOf("("));
      name = name.trim();

    } else {
      st = new StringTokenizer(atomName, "-");

      // --- Parse element
      if (st.hasMoreTokens()) {
        token = st.nextToken();
      } else {
        token = "-";
      }
      name = token;
    }

    if (name.equalsIgnoreCase("X")) {
      setDummy(true);
      element = 0;
    } else {
      try {
        element = Gaussian.getAtomNumber(name);
      } catch (Exception e) {
        element = 0;
        //throw new Exception(e.getMessage() + " Set atom to dummy");
        System.err.println(e.getMessage() + " Set atom to dummy");
      }
    }

    if (complex_name) {
      // C(PDBName=C3,ResName=NH3,ResNum=1)
      String temp;
      atomName = atomName.toUpperCase();
      if (atomName.contains(PDB_NAME_KEYWORD)) {
        try {
          int start_index = atomName.indexOf(PDB_NAME_KEYWORD) + PDB_NAME_KEYWORD.length() + 1;
          int end_index = start_index + atomName.substring(start_index).indexOf(",");
          temp = atomName.substring(start_index, end_index);
          name = temp;
          this.setPDBName(temp);
        } catch (Exception ex) {
          logger.severe("Cannot properly parse atom name " + atomName + " : " + ex.getMessage() + " Ignored & continuing...");
        }
      }

      if (atomName.contains(RES_NAME_KEYWORD)) {
        try {
          int start_index = atomName.indexOf(RES_NAME_KEYWORD) + RES_NAME_KEYWORD.length() + 1;
          int end_index = start_index + atomName.substring(start_index).indexOf(",");
          temp = atomName.substring(start_index, end_index);
          this.setResName(temp);
        } catch (Exception ex) {
          logger.severe("Cannot properly parse residue name " + atomName + " : " + ex.getMessage() + " Ignored & continuing...");
        }
      }

      if (atomName.contains(RES_NUM_KEYWORD)) {
        try {
          int start_index = atomName.indexOf(RES_NUM_KEYWORD) + RES_NUM_KEYWORD.length() + 1;
          int end_index = start_index + atomName.substring(start_index).indexOf(")");
          temp = atomName.substring(start_index, end_index);
          this.setResNum(temp);
        } catch (Exception ex) {
          logger.severe("Cannot properly parse residue number " + atomName + " : " + ex.getMessage() + " Ignored & continuing...");
        }
      }

    }

    //
    if (st == null) {
      return;
    }

    if (!st.hasMoreTokens()) {
      return;
    }

    token = st.nextToken();

    if (token.startsWith("(")) {
      extraParam = token;
      return;
    }

    // --- MM Atom Type

    atomType = token;

    if (!st.hasMoreTokens()) {
      return;
    }

    token = st.nextToken();

    if (token.startsWith("(")) {
      extraParam = token;
      return;
    }

    // --- MM charge

    try {
      charge = Float.parseFloat(token);
      if (minusCharge) {
        charge = -charge;
      }
    } catch (NumberFormatException e) {
      logger.info("ERROR: expecting MM charge. Got " + token);
    }

    if (!st.hasMoreTokens()) {
      return;
    }

    extraParam = st.nextToken();
  }

  /**
   * @deprecated Use parseAtomName(String aname, boolean complex_name) instead
   */
  @Deprecated
  public void parseAtomName(String aname) throws Exception {
    parseAtomName(aname, false);
  }

  public void setONIOMLayer(String layer) throws Exception {
    Layer = layer;
    if (layer.equalsIgnoreCase("H") || layer.equalsIgnoreCase("High")) {
      ONIOM_Layer = ONIOM_LAYER.HIGH;
    } else if (layer.equalsIgnoreCase("M") || layer.equalsIgnoreCase("Medium")) {
      ONIOM_Layer = ONIOM_LAYER.MEDIUM;
    } else if (layer.equalsIgnoreCase("L") || layer.equalsIgnoreCase("Low")) {
      ONIOM_Layer = ONIOM_LAYER.LOW;
    } else {
      throw new Exception("Unknown ONIOM layer: " + layer);
    }
  }

  public String getPDBName() {
    return PDBName;
  }

  public void setPDBName(String PDBName) {
    this.PDBName = PDBName;
  }

  public String getResName() {
    return ResName;
  }

  public void setResName(String ResName) {
    this.ResName = ResName;
  }

  public int getResNum() {
    return ResNum;
  }

  public void setResNum(int ResNum) {
    this.ResNum = ResNum;
  }

  public void setResNum(String resNum) throws Exception {
    this.ResNum = Integer.parseInt(resNum);
  }
}
