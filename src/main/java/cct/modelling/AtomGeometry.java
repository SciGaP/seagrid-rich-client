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

import cct.resources.images.ImageResources;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

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
/*
 # Element Fragment Table
 # Fragment Format: index pixmap fragment hotx hoty name
 # Indexes = 0 ... n-1
 Fragment nItems = 20
  0 atom.xpm     atom.frg     24 18 "%1 Atom"
  1 sing.xpm     sing.frg     24 24 "%1 Terminal (S)"
  2 dit.xpm      dit.frg      24 24 "%1 Terminal (T)"
  3 lin.xpm      lin.frg      24 24 "%1 Linear (S-S)"
  4 dist.xpm     dist.frg     24 24 "%1 Divalent (S-T)"
  5 didd.xpm     didd.frg     24 24 "%1 Divalent (D-D)"
  6 trid.xpm     trid.frg     24 24 "%1 Trivalent (D-LP-LP)"
  7 trisd.xpm    trisd.frg    24 24 "%1 Trivalent (S-D-LP)"
  8 triaa.xpm    triaa.frg    24 24 "%1 Trivalent (A-A-LP)"
  9 tripla.xpm   tripla.frg   24 24 "%1 Trigonal Planar"
 10 tshap.xpm    tshap.frg    24 24 "%1 T-shaped"
 11 trissd.xpm   trissd.frg   24 24 "%1 Trivalent (S-S-D)"
 12 trisaa.xpm   trisaa.frg   24 24 "%1 Trivalent (S-A-A)"
 13 bent.xpm     bent.frg     24 24 "%1 Tetravalent (S-S-LP-LP)"
 14 tetsss.xpm   tetsss.frg   24 24 "%1 Tetravalent (S-S-S-LP)"
 15 tet.xpm      tet.frg      24 24 "%1 Tetrahedral"
 16 sqpla.xpm    sqpla.frg    24 24 "%1 Square Planar"
 17 seesaw.xpm   seesaw.frg   24 24 "%1 Seesaw"
 18 tribipyr.xpm tribipyr.frg 24 24 "%1 Trigonal Bipyramid"
 19 oct.xpm      oct.frg      24 24 "%1 Octahedral"
 # Fragment Format: atnum def_index frag_index0 frag_index1 ...
 Element nItems = 111
        1       1       0       1
        2       19      0       19
        3       1       0       1
        4       3       0       3
        5       9       0       9
        6       15      0       4       5       11      12      15
        7       14      0       2       7       8       14
        8       13      0       6       13
        9       1       0       1
        10      19      0       19
        11      1       0       1
        12      3       0       3
        13      9       0       9
        14      15      0       4       5       11      12      15      18
        15      14      0       7       9       14      15      18      19
        16      13      0       6       11      13      15      18      19
        17      1       0       1       3       9       15      18      19
        18      19      0       19
        19      1       0       1
        20      3       0       3
        21      19      0       3       9       10      15      16      17      18      19
        22      19      0       3       9       10      15      16      17      18      19
        23      19      0       3       9       10      15      16      17      18      19
        24      19      0       3       9       10      15      16      17      18      19
        25      19      0       3       9       10      15      16      17      18      19
        26      19      0       3       9       10      15      16      17      18      19
        27      19      0       3       9       10      15      16      17      18      19
        28      19      0       3       9       10      15      16      17      18      19
        29      19      0       3       9       10      15      16      17      18      19
        30      19      0       3       9       10      15      16      17      18      19
        31      19      0       3       9       10      15      16      17      18      19
        32      19      0       3       9       10      15      16      17      18      19
        33      19      0       3       9       10      15      16      17      18      19
        34      19      0       3       9       10      15      16      17      18      19
        35      1       0       1       3       9       15      16      17      18      19
        36      19      0       19
        37      1       0       1
        38      3       0       3
        39      19      0       3       9       10      15      16      17      18      19
        40      19      0       3       9       10      15      16      17      18      19
        41      19      0       3       9       10      15      16      17      18      19
        42      19      0       3       9       10      15      16      17      18      19
        43      19      0       3       9       10      15      16      17      18      19
        44      19      0       3       9       10      15      16      17      18      19
        45      19      0       3       9       10      15      16      17      18      19
        46      19      0       3       9       10      15      16      17      18      19
        47      19      0       3       9       10      15      16      17      18      19
        48      19      0       3       9       10      15      16      17      18      19
        49      19      0       3       9       10      15      16      17      18      19
        50      19      0       3       9       10      15      16      17      18      19
        51      19      0       3       9       10      15      16      17      18      19
        52      19      0       3       9       10      15      16      17      18      19
        53      19      0       1       3       9       15      16      17      18      19
        54      19      0       19
        55      1       0       1
        56      3       0       3
        57      19      0       3       9       10      15      16      17      18      19
        58      19      0       3       9       10      15      16      17      18      19
        59      19      0       3       9       10      15      16      17      18      19
        60      19      0       3       9       10      15      16      17      18      19
        61      19      0       3       9       10      15      16      17      18      19
        62      19      0       3       9       10      15      16      17      18      19
        63      19      0       3       9       10      15      16      17      18      19
        64      19      0       3       9       10      15      16      17      18      19
        65      19      0       3       9       10      15      16      17      18      19
        66      19      0       3       9       10      15      16      17      18      19
        67      19      0       3       9       10      15      16      17      18      19
        68      19      0       3       9       10      15      16      17      18      19
        69      19      0       3       9       10      15      16      17      18      19
        70      19      0       3       9       10      15      16      17      18      19
        71      19      0       3       9       10      15      16      17      18      19
        72      19      0       3       9       10      15      16      17      18      19
        73      19      0       3       9       10      15      16      17      18      19
        74      19      0       3       9       10      15      16      17      18      19
        75      19      0       3       9       10      15      16      17      18      19
        76      19      0       3       9       10      15      16      17      18      19
        77      19      0       3       9       10      15      16      17      18      19
        78      19      0       3       9       10      15      16      17      18      19
        79      19      0       3       9       10      15      16      17      18      19
        80      19      0       3       9       10      15      16      17      18      19
        81      19      0       3       9       10      15      16      17      18      19
        82      19      0       3       9       10      15      16      17      18      19
        83      19      0       3       9       10      15      16      17      18      19
        84      19      0       3       9       10      15      16      17      18      19
        85      19      0       1       3       9       15      16      17      18      19
        86      19      0       19
        87      1       0       1
        88      3       0       3
        89      19      0       3       9       10      15      16      17      18      19
        90      19      0       3       9       10      15      16      17      18      19
        91      19      0       3       9       10      15      16      17      18      19
        92      19      0       3       9       10      15      16      17      18      19
        93      19      0       3       9       10      15      16      17      18      19
        94      19      0       3       9       10      15      16      17      18      19
        95      19      0       3       9       10      15      16      17      18      19
        96      19      0       3       9       10      15      16      17      18      19
        97      19      0       3       9       10      15      16      17      18      19
        98      19      0       3       9       10      15      16      17      18      19
        99      19      0       3       9       10      15      16      17      18      19
        100     19      0       3       9       10      15      16      17      18      19
        101     19      0       3       9       10      15      16      17      18      19
        102     19      0       3       9       10      15      16      17      18      19
        103     19      0       3       9       10      15      16      17      18      19
        104     19      0       3       9       10      15      16      17      18      19
        105     19      0       3       9       10      15      16      17      18      19
        106     19      0       3       9       10      15      16      17      18      19
        107     19      0       3       9       10      15      16      17      18      19
        108     19      0       3       9       10      15      16      17      18      19
        109     19      0       3       9       10      15      16      17      18      19
        110     1       0       1
        111     1       0       1
 */
public class AtomGeometry {

  public static final int LINEAR = 0;
  public static final int TRIGONAL = 1;
  public static final int SQUARE = 2;
  public static final int TETRAHEDRAL = 3;
  public static final int TRIGONAL_BIPYRAMID = 4;
  public static final int OCTAHEDRAL = 5;

  static final String ATOM = "Atom";

  static final String TERMINAL_SINGLE_ATOM = "Terminal single";
  static final String TERMINAL_DOUBLE_ATOM = "Terminal double";
  static final String TERMINAL_TRIPLE_ATOM = "Terminal triple";

  static final String LINEAR_SINGLE_SINGLE_ATOM = "Linear Single-Single";
  static final String LINEAR_DOUBLE_DOUBLE_ATOM = "Linear Double-Double";
  static final String LINEAR_SINGLE_TRIPLE_ATOM = "Linear Single-Triple";

  static final String BENDED_SINGLE_DOUBLE_ATOM = "Bended Single-Double";

  static final String TRIGONAL_PLANAR_SSS_ATOM = "Trigonal Planar (SSS)";
  static final String TRIGONAL_PLANAR_DSS_ATOM = "Trigonal Planar (DSS)";
  static final String TRIGONAL_PLANAR_SAA_ATOM = "Trigonal Planar (SAA)";

  static final String T_SHAPED_ATOM = "T-shaped";

  static final String SEESAW_SHAPED_ATOM = "Seesaw";

  static final String TETRAHEDRAL_SSSS_ATOM = "Tetrahedral (SSSS)";
  static final String TETRAHEDRAL_SSS_ATOM = "Tetrahedral (SSSLp)";
  static final String TETRAHEDRAL_SS_ATOM = "Tetrahedral (SSLpLp)";
  static final String TETRAHEDRAL_SSSD_ATOM = "Tetrahedral (SSSD)"; // H3PO4
  static final String TETRAHEDRAL_SSDD_ATOM = "Tetrahedral (SSDD)"; // H2SO4

  static final String SQUARE_PLANAR_ATOM = "Square planar";

  static final String TRIGONAL_BIPYRAMID_ATOM = "Trigonal bipyramid";

  static final String OCTAHEDRAL_ATOM = "Octahedral";

  // --- icons

  public static ImageIcon ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/emptyTransparent.png"));
  static ImageIcon TERMINAL_SINGLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/terminal_single_atom.png"));

  static ImageIcon TERMINAL_DOUBLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/terminal_double_atom.png"));

  static ImageIcon TERMINAL_TRIPLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/terminal_triple_atom.png"));

  static ImageIcon LINEAR_SINGLE_SINGLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/linear_single_single_atom.png"));

  static ImageIcon LINEAR_SINGLE_TRIPLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/linear_single_triple_atom.png"));

  static ImageIcon LINEAR_DOUBLE_DOUBLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/linear_double_double_atom.png"));

  static ImageIcon BENDED_SINGLE_DOUBLE_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/bended_single_double_atom.png"));

  static ImageIcon TRIGONAL_PLANAR_SSS_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/trigonal-3.png"));

  static ImageIcon TRIGONAL_PLANAR_DSS_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/trigonal_planar_dss_atom.png"));

  static ImageIcon TRIGONAL_PLANAR_SAA_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/trigonal_planar_saa_atom.png"));

  static ImageIcon TRIGONAL_BIPYRAMID_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/trigonal_bipyramid_atom.png"));

  static ImageIcon T_SHAPED_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/t_shaped_atom.png"));

  static ImageIcon SQUARE_PLANAR_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/square_planar_atom.png"));

  static ImageIcon SEESAW_SHAPED_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/seesaw_shaped_atom.png"));

  static ImageIcon OCTAHEDRAL_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/octahedral_atom.png"));

  static ImageIcon TETRAHEDRAL_SSSS_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-4.png"));

  static ImageIcon TETRAHEDRAL_SSS_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-3.png"));

  static ImageIcon TETRAHEDRAL_SS_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-2.png"));

  static ImageIcon TETRAHEDRAL_SSSD_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral_sssd_atom.png"));

  static ImageIcon TETRAHEDRAL_SSDD_ATOM_ICON = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral_ssdd_atom.png"));

  static Map<String, AtomGeometry> atomTypes = new LinkedHashMap<String, AtomGeometry> ();

  static {
    atomTypes.put(ATOM,
                  new AtomGeometry(ATOM, 0, 0, 0, 0, 0, LINEAR, ATOM_ICON));
    atomTypes.put(TERMINAL_SINGLE_ATOM,
                  new AtomGeometry(TERMINAL_SINGLE_ATOM, 1, 1, 0, 0, 0,
                                   LINEAR, TERMINAL_SINGLE_ATOM_ICON));
    atomTypes.put(TERMINAL_DOUBLE_ATOM,
                  new AtomGeometry(TERMINAL_DOUBLE_ATOM, 1, 0, 1, 0, 0,
                                   LINEAR, TERMINAL_DOUBLE_ATOM_ICON));
    atomTypes.put(TERMINAL_TRIPLE_ATOM,
                  new AtomGeometry(TERMINAL_TRIPLE_ATOM, 1, 0, 0, 0, 1,
                                   LINEAR, TERMINAL_TRIPLE_ATOM_ICON));
    atomTypes.put(LINEAR_SINGLE_SINGLE_ATOM,
                  new AtomGeometry(LINEAR_SINGLE_SINGLE_ATOM, 2, 2, 0, 0, 0,
                                   LINEAR, LINEAR_SINGLE_SINGLE_ATOM_ICON));
    atomTypes.put(LINEAR_DOUBLE_DOUBLE_ATOM,
                  new AtomGeometry(LINEAR_DOUBLE_DOUBLE_ATOM, 2, 0, 2, 0, 0,
                                   LINEAR, LINEAR_DOUBLE_DOUBLE_ATOM_ICON));
    atomTypes.put(LINEAR_SINGLE_TRIPLE_ATOM,
                  new AtomGeometry(LINEAR_SINGLE_TRIPLE_ATOM, 2, 1, 0, 0, 1,
                                   LINEAR, LINEAR_SINGLE_TRIPLE_ATOM_ICON));
    atomTypes.put(BENDED_SINGLE_DOUBLE_ATOM,
                  new AtomGeometry(BENDED_SINGLE_DOUBLE_ATOM, 2, 1, 1, 0, 0,
                                   TRIGONAL, BENDED_SINGLE_DOUBLE_ATOM_ICON));
    atomTypes.put(TRIGONAL_PLANAR_SSS_ATOM,
                  new AtomGeometry(TRIGONAL_PLANAR_SSS_ATOM, 3, 3, 0, 0, 0,
                                   TRIGONAL, TRIGONAL_PLANAR_SSS_ATOM_ICON));
    atomTypes.put(TRIGONAL_PLANAR_DSS_ATOM,
                  new AtomGeometry(TRIGONAL_PLANAR_DSS_ATOM, 3, 2, 1, 0, 0,
                                   TRIGONAL, TRIGONAL_PLANAR_DSS_ATOM_ICON));
    atomTypes.put(TRIGONAL_PLANAR_SAA_ATOM,
                  new AtomGeometry(TRIGONAL_PLANAR_SAA_ATOM, 3, 1, 0, 2, 0,
                                   TRIGONAL, TRIGONAL_PLANAR_SAA_ATOM_ICON));
    atomTypes.put(T_SHAPED_ATOM,
                  new AtomGeometry(T_SHAPED_ATOM, 3, 3, 0, 0, 0,
                                   SQUARE, T_SHAPED_ATOM_ICON));
    atomTypes.put(SEESAW_SHAPED_ATOM,
                  new AtomGeometry(SEESAW_SHAPED_ATOM, 4, 4, 0, 0, 0,
                                   SQUARE, SEESAW_SHAPED_ATOM_ICON));
    atomTypes.put(TETRAHEDRAL_SSSS_ATOM,
                  new AtomGeometry(TETRAHEDRAL_SSSS_ATOM, 4, 4, 0, 0, 0,
                                   TETRAHEDRAL, TETRAHEDRAL_SSSS_ATOM_ICON));
    atomTypes.put(TETRAHEDRAL_SSS_ATOM,
                  new AtomGeometry(TETRAHEDRAL_SSS_ATOM, 3, 3, 0, 0, 0,
                                   TETRAHEDRAL, TETRAHEDRAL_SSS_ATOM_ICON));
    atomTypes.put(TETRAHEDRAL_SS_ATOM,
                  new AtomGeometry(TETRAHEDRAL_SS_ATOM, 2, 2, 0, 0, 0,
                                   TETRAHEDRAL, TETRAHEDRAL_SS_ATOM_ICON));
    atomTypes.put(TETRAHEDRAL_SSSD_ATOM,
                  new AtomGeometry(TETRAHEDRAL_SSSD_ATOM, 4, 3, 1, 0, 0,
                                   TETRAHEDRAL, TETRAHEDRAL_SSSD_ATOM_ICON));
    atomTypes.put(TETRAHEDRAL_SSDD_ATOM,
                  new AtomGeometry(TETRAHEDRAL_SSDD_ATOM, 4, 2, 2, 0, 0,
                                   TETRAHEDRAL, TETRAHEDRAL_SSDD_ATOM_ICON));
    atomTypes.put(SQUARE_PLANAR_ATOM,
                  new AtomGeometry(SQUARE_PLANAR_ATOM, 4, 4, 0, 0, 0,
                                   SQUARE, SQUARE_PLANAR_ATOM_ICON));
    atomTypes.put(TRIGONAL_BIPYRAMID_ATOM,
                  new AtomGeometry(TRIGONAL_BIPYRAMID_ATOM, 5, 5, 0, 0, 0,
                                   TRIGONAL_BIPYRAMID,
                                   TRIGONAL_BIPYRAMID_ATOM_ICON));
    atomTypes.put(OCTAHEDRAL_ATOM,
                  new AtomGeometry(OCTAHEDRAL_ATOM, 6, 6, 0, 0, 0,
                                   OCTAHEDRAL, OCTAHEDRAL_ATOM_ICON));
  }

  private static AtomGeometry dummy = new AtomGeometry(null, 0, 0, 0, 0, 0, 0, null);

  protected String type = null;
  protected int geometry = LINEAR;
  protected int coordNumber = 0;
  protected int singleBonds = 0;
  protected int doubleBonds = 0;
  protected int aromaticBonds = 0;
  protected int tripleBonds = 0;
  protected ImageIcon icon = null;

  public AtomGeometry() {}

  public AtomGeometry(String _type, int coordnum, int singles, int doubles,
                      int aromatics, int triples, int geom, ImageIcon _icon) {
    type = _type;
    geometry = geom;
    coordNumber = coordnum;
    singleBonds = singles;
    doubleBonds = doubles;
    aromaticBonds = aromatics;
    tripleBonds = triples;
    icon = _icon;
  }

  public AtomGeometry(AtomGeometry aType) {
    setAtomGeometry(aType);
  }

  public ImageIcon getIcon() {
    return icon;
  }

  public void setAtomGeometry(AtomGeometry aType) {
    type = aType.type;
    geometry = aType.geometry;
    coordNumber = aType.coordNumber;
    singleBonds = aType.singleBonds;
    doubleBonds = aType.doubleBonds;
    aromaticBonds = aType.aromaticBonds;
    tripleBonds = aType.tripleBonds;
    icon = aType.icon;

  }

  public int getGeometry() {
    return geometry;
  }

  public static AtomGeometry getAtomGeometry(String gName) {
    return atomTypes.get(gName);
  }

  public void setCoordinationNumber(int n) {
    coordNumber = n;
  }

  public int getCoordinationNumber() {
    return coordNumber;
  }

  public int getMaxSingleBonds() {
    return singleBonds;
  }

  public void setMaxSingleBonds(int maxH) {
    singleBonds = maxH;
  }

}
