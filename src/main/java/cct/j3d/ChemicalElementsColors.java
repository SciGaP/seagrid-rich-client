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

package cct.j3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.scijava.java3d.Material;
import org.scijava.vecmath.Color3f;

import cct.modelling.ChemicalElements;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ChemicalElementsColors
    extends ChemicalElements {

  static final String DEFAULT_ATOM_COLOR_SCHEME = "Sybyl Scheme";
  static final String ATOM_COLOR_SCHEME_2 = "GaussView Scheme";
  static final String ATOM_COLOR_SCHEME_JMOL = "Jmol Scheme";
  static final String ATOM_COLOR_SCHEME_RASMOL = "Rasmol Scheme";
  static final String ATOM_COLOR_SCHEME_RASMOL_NEW = "Rasmol CPKnew Scheme";

  private static String currentColorSchemeName = DEFAULT_ATOM_COLOR_SCHEME;
  private static List currentColorScheme;
  private static String atomColorSchemeKey = "atomColorScheme";
  private static Preferences prefs; // = Preferences.userNodeForPackage(getClass());
  static final Logger logger = Logger.getLogger(ChemicalElementsColors.class.getCanonicalName());

  static TreeMap atomColours = new TreeMap();
  static List elementMaterial = new ArrayList();
  static List elementColors = new ArrayList();
  static List gaussianElementColors = new ArrayList();
  static List rasmolElementColors = new ArrayList();
  static List rasmolNewElementColors = new ArrayList();
  static List jmolElementColors = new ArrayList();
  static Map colorScheme = new LinkedHashMap();

  static Material highlightMaterial = new Material(
      new Color3f(0.333f, 0.777f, 0.222f),
      new Color3f(0.777f, 0.222f, 0.555f), new Color3f(0.777f, 0.222f, 0.555f),
      new Color3f(1.0f, 1.0f, 1.0f), 15.0f);

  static Color3f highlightColor = new Color3f(Color.MAGENTA);

  static {
    elementColors.add(new Color3f(Color.DARK_GRAY)); // 0 - Dummy
    elementColors.add(new Color3f(Color.CYAN)); //   1 - H
    elementColors.add(new Color3f(Color.MAGENTA)); //   2 - He
    elementColors.add(new Color3f(Color.MAGENTA)); //   3 - Li
    elementColors.add(new Color3f(Color.MAGENTA)); //   4 - Be
    elementColors.add(new Color3f(Color.MAGENTA)); //   5 - B
    elementColors.add(new Color3f(Color.WHITE)); //   6 - C
    elementColors.add(new Color3f(Color.BLUE)); //   7 - N
    elementColors.add(new Color3f(Color.RED)); //   8 - O
    elementColors.add(new Color3f(Color.GREEN)); //   9 - F
    elementColors.add(new Color3f(Color.MAGENTA)); //  10 - Ne
    elementColors.add(new Color3f(Color.MAGENTA)); //  11 - Na
    elementColors.add(new Color3f(Color.MAGENTA)); //  12 - Mg
    elementColors.add(new Color3f(Color.CYAN)); //  13 - Al
    elementColors.add(new Color3f(Color.YELLOW)); //  14 - Si
    elementColors.add(new Color3f(Color.ORANGE)); //  15 - P
    elementColors.add(new Color3f(Color.YELLOW)); //  16 - S
    elementColors.add(new Color3f(Color.GREEN)); //  17 - Cl
    elementColors.add(new Color3f(Color.MAGENTA)); //  18 - Ar
    elementColors.add(new Color3f(Color.MAGENTA)); //  19 - K
    elementColors.add(new Color3f(Color.MAGENTA)); //  20 - Ca
    elementColors.add(new Color3f(Color.MAGENTA)); //  21 - Sc
    elementColors.add(new Color3f(Color.MAGENTA)); //  22 - Ti
    elementColors.add(new Color3f(Color.MAGENTA)); //  23 - V
    elementColors.add(new Color3f(Color.MAGENTA)); //  24 - Cr
    elementColors.add(new Color3f(Color.MAGENTA)); //  25 - Mn
    elementColors.add(new Color3f(Color.MAGENTA)); //  26 - Fe
    elementColors.add(new Color3f(Color.MAGENTA)); //  27 - Co
    elementColors.add(new Color3f(Color.MAGENTA)); //  28 - Ni
    elementColors.add(new Color3f(Color.MAGENTA)); //  29 - Cu
    elementColors.add(new Color3f(Color.MAGENTA)); //  30 - Zn
    elementColors.add(new Color3f(Color.MAGENTA)); //  31 - Ga
    elementColors.add(new Color3f(Color.MAGENTA)); //  32 - Ge
    elementColors.add(new Color3f(Color.MAGENTA)); //  33 - As
    elementColors.add(new Color3f(Color.MAGENTA)); //  34 - Se
    elementColors.add(new Color3f(Color.GREEN)); //  35 - Br
    elementColors.add(new Color3f(Color.MAGENTA)); //  36 - Kr
    elementColors.add(new Color3f(Color.MAGENTA)); //  37 - Rb
    elementColors.add(new Color3f(Color.MAGENTA)); //  38 - Sr
    elementColors.add(new Color3f(Color.MAGENTA)); //  39 - Y
    elementColors.add(new Color3f(Color.MAGENTA)); //  40 - Zr
    elementColors.add(new Color3f(Color.MAGENTA)); //  41 - Nb
    elementColors.add(new Color3f(Color.MAGENTA)); //  42 - Mo
    elementColors.add(new Color3f(Color.MAGENTA)); //  43 - Tc
    elementColors.add(new Color3f(Color.MAGENTA)); //  44 - Ru
    elementColors.add(new Color3f(Color.MAGENTA)); //  45 - Rh
    elementColors.add(new Color3f(Color.MAGENTA)); //  46 - Pd
    elementColors.add(new Color3f(Color.MAGENTA)); //  47 - Ag
    elementColors.add(new Color3f(Color.MAGENTA)); //  48 - Cd
    elementColors.add(new Color3f(Color.MAGENTA)); //  49 - In
    elementColors.add(new Color3f(Color.MAGENTA)); //  50 - Sn
    elementColors.add(new Color3f(Color.MAGENTA)); //  51 - Sb
    elementColors.add(new Color3f(Color.MAGENTA)); //  52 - Te
    elementColors.add(new Color3f(Color.PINK)); //  53 - I
    elementColors.add(new Color3f(Color.MAGENTA)); //  54 - Xe
    elementColors.add(new Color3f(Color.MAGENTA)); //  55 - Cs
    elementColors.add(new Color3f(Color.MAGENTA)); //  56 - Ba
    elementColors.add(new Color3f(Color.MAGENTA)); //  57 - La
    elementColors.add(new Color3f(Color.MAGENTA)); //  58 - Ce
    elementColors.add(new Color3f(Color.MAGENTA)); //  59 - Pr
    elementColors.add(new Color3f(Color.MAGENTA)); //  60 - Nd
    elementColors.add(new Color3f(Color.MAGENTA)); //  61 - Pm
    elementColors.add(new Color3f(Color.MAGENTA)); //  62 - Sm
    elementColors.add(new Color3f(Color.MAGENTA)); //  63 - Eu
    elementColors.add(new Color3f(Color.MAGENTA)); //  64 - Gd
    elementColors.add(new Color3f(Color.MAGENTA)); //  65 - Tb
    elementColors.add(new Color3f(Color.MAGENTA)); //  66 - Dy
    elementColors.add(new Color3f(Color.MAGENTA)); //  67 - Ho
    elementColors.add(new Color3f(Color.MAGENTA)); //  68 - Er
    elementColors.add(new Color3f(Color.MAGENTA)); //  69 - Tm
    elementColors.add(new Color3f(Color.MAGENTA)); //  70 - Yb
    elementColors.add(new Color3f(Color.MAGENTA)); //  71 - Lu
    elementColors.add(new Color3f(Color.MAGENTA)); //  72 - Hf
    elementColors.add(new Color3f(Color.MAGENTA)); //  73 - Ta
    elementColors.add(new Color3f(Color.MAGENTA)); //  74 - W
    elementColors.add(new Color3f(Color.MAGENTA)); //  75 - Re
    elementColors.add(new Color3f(Color.MAGENTA)); //  76 - Os
    elementColors.add(new Color3f(Color.MAGENTA)); //  77 - Ir
    elementColors.add(new Color3f(Color.MAGENTA)); //  78 - Pt
    elementColors.add(new Color3f(Color.MAGENTA)); //  79 - Au
    elementColors.add(new Color3f(Color.MAGENTA)); //  80 - Hg
    elementColors.add(new Color3f(Color.MAGENTA)); //  81 - Tl
    elementColors.add(new Color3f(Color.MAGENTA)); //  82 - Pb
    elementColors.add(new Color3f(Color.MAGENTA)); //  83 - Bi
    elementColors.add(new Color3f(Color.MAGENTA)); //  84 - Po
    elementColors.add(new Color3f(Color.MAGENTA)); //  85 - At
    elementColors.add(new Color3f(Color.MAGENTA)); //  86 - Rn
    elementColors.add(new Color3f(Color.MAGENTA)); //  87 - Fr
    elementColors.add(new Color3f(Color.MAGENTA)); //  88 - Ra
    elementColors.add(new Color3f(Color.MAGENTA)); //  89 - Ac
    elementColors.add(new Color3f(Color.MAGENTA)); //  90 - Th
    elementColors.add(new Color3f(Color.MAGENTA)); //  91 - Pa
    elementColors.add(new Color3f(Color.MAGENTA)); //  92 - U
    elementColors.add(new Color3f(Color.MAGENTA)); //  93 - Np
    elementColors.add(new Color3f(Color.MAGENTA)); //  94 - Pu
    elementColors.add(new Color3f(Color.MAGENTA)); //  95 - Am
    elementColors.add(new Color3f(Color.MAGENTA)); //  96 - Cm
    elementColors.add(new Color3f(Color.MAGENTA)); //  97 - Bk
    elementColors.add(new Color3f(Color.MAGENTA)); //  98 - Cf
    elementColors.add(new Color3f(Color.MAGENTA)); //  99 - Es
    elementColors.add(new Color3f(Color.MAGENTA)); // 100 - Fm
    elementColors.add(new Color3f(Color.MAGENTA)); // 101 - Md
    elementColors.add(new Color3f(Color.MAGENTA)); // 102 - No
    elementColors.add(new Color3f(Color.MAGENTA)); // 103 - Lr
    elementColors.add(new Color3f(Color.MAGENTA)); // 104 - Db
    elementColors.add(new Color3f(Color.MAGENTA)); // 105 - Jl
    elementColors.add(new Color3f(Color.MAGENTA)); // 106 - Rf
    elementColors.add(new Color3f(Color.MAGENTA)); // 107 - Bh
    elementColors.add(new Color3f(Color.MAGENTA)); // 108 - Hn
    elementColors.add(new Color3f(Color.MAGENTA)); // 109 - Mt

    gaussianElementColors.add(new Color3f(Color.DARK_GRAY)); // 0 - Dummy
    gaussianElementColors.add(new Color3f(0.80f, 0.80f, 0.80f)); //   1 - H
    gaussianElementColors.add(new Color3f(0.85f, 1.00f, 1.00f)); //   2 - He
    gaussianElementColors.add(new Color3f(0.80f, 0.49f, 1.00f)); //   3 - Li
    gaussianElementColors.add(new Color3f(0.80f, 1.00f, 0.00f)); //   4 - Be
    gaussianElementColors.add(new Color3f(1.00f, 0.71f, 0.71f)); //   5 - B
    gaussianElementColors.add(new Color3f(0.56f, 0.56f, 0.56f)); //   6 - C
    gaussianElementColors.add(new Color3f(0.10f, 0.10f, 0.90f)); //   7 - N
    gaussianElementColors.add(new Color3f(0.90f, 0.00f, 0.00f)); //   8 - O
    gaussianElementColors.add(new Color3f(0.70f, 1.00f, 1.00f)); //   9 - F
    gaussianElementColors.add(new Color3f(0.69f, 0.89f, 0.96f)); //  10 - Ne
    gaussianElementColors.add(new Color3f(0.67f, 0.36f, 0.95f)); //  11 - Na
    gaussianElementColors.add(new Color3f(0.70f, 0.80f, 0.00f)); //  12 - Mg
    gaussianElementColors.add(new Color3f(0.82f, 0.65f, 0.65f)); //  13 - Al
    gaussianElementColors.add(new Color3f(0.50f, 0.60f, 0.60f)); //  14 - Si
    gaussianElementColors.add(new Color3f(1.00f, 0.50f, 0.00f)); //  15 - P
    gaussianElementColors.add(new Color3f(1.00f, 0.78f, 0.16f)); //  16 - S
    gaussianElementColors.add(new Color3f(0.10f, 0.94f, 0.10f)); //  17 - Cl
    gaussianElementColors.add(new Color3f(0.50f, 0.82f, 0.89f)); //  18 - Ar
    gaussianElementColors.add(new Color3f(0.56f, 0.25f, 0.83f)); //  19 - K
    gaussianElementColors.add(new Color3f(0.60f, 0.60f, 0.00f)); //  20 - Ca
    gaussianElementColors.add(new Color3f(0.90f, 0.90f, 0.89f)); //  21 - Sc
    gaussianElementColors.add(new Color3f(0.75f, 0.76f, 0.78f)); //  22 - Ti
    gaussianElementColors.add(new Color3f(0.65f, 0.65f, 0.67f)); //  23 - V
    gaussianElementColors.add(new Color3f(0.54f, 0.60f, 0.78f)); //  24 - Cr
    gaussianElementColors.add(new Color3f(0.61f, 0.48f, 0.78f)); //  25 - Mn
    gaussianElementColors.add(new Color3f(0.50f, 0.48f, 0.78f)); //  26 - Fe
    gaussianElementColors.add(new Color3f(0.36f, 0.43f, 1.00f)); //  27 - Co
    gaussianElementColors.add(new Color3f(0.36f, 0.48f, 0.76f)); //  28 - Ni
    gaussianElementColors.add(new Color3f(1.00f, 0.48f, 0.38f)); //  29 - Cu
    gaussianElementColors.add(new Color3f(0.49f, 0.50f, 0.69f)); //  30 - Zn
    gaussianElementColors.add(new Color3f(0.76f, 0.56f, 0.56f)); //  31 - Ga
    gaussianElementColors.add(new Color3f(0.40f, 0.56f, 0.56f)); //  32 - Ge
    gaussianElementColors.add(new Color3f(0.74f, 0.50f, 0.89f)); //  33 - As
    gaussianElementColors.add(new Color3f(1.00f, 0.63f, 0.00f)); //  34 - Se
    gaussianElementColors.add(new Color3f(0.65f, 0.13f, 0.13f)); //  35 - Br
    gaussianElementColors.add(new Color3f(0.36f, 0.73f, 0.82f)); //  36 - Kr
    gaussianElementColors.add(new Color3f(0.44f, 0.18f, 0.69f)); //  37 - Rb
    gaussianElementColors.add(new Color3f(0.50f, 0.40f, 0.00f)); //  38 - Sr
    gaussianElementColors.add(new Color3f(0.58f, 0.99f, 1.00f)); //  39 - Y
    gaussianElementColors.add(new Color3f(0.58f, 0.88f, 0.88f)); //  40 - Zr
    gaussianElementColors.add(new Color3f(0.45f, 0.76f, 0.79f)); //  41 - Nb
    gaussianElementColors.add(new Color3f(0.33f, 0.71f, 0.71f)); //  42 - Mo
    gaussianElementColors.add(new Color3f(0.23f, 0.62f, 0.66f)); //  43 - Tc
    gaussianElementColors.add(new Color3f(0.14f, 0.56f, 0.59f)); //  44 - Ru
    gaussianElementColors.add(new Color3f(0.04f, 0.49f, 0.55f)); //  45 - Rh
    gaussianElementColors.add(new Color3f(0.00f, 0.41f, 0.52f)); //  46 - Pd
    gaussianElementColors.add(new Color3f(0.60f, 0.78f, 1.00f)); //  47 - Ag
    gaussianElementColors.add(new Color3f(1.00f, 0.85f, 0.56f)); //  48 - Cd
    gaussianElementColors.add(new Color3f(0.65f, 0.46f, 0.45f)); //  49 - In
    gaussianElementColors.add(new Color3f(0.40f, 0.50f, 0.50f)); //  50 - Sn
    gaussianElementColors.add(new Color3f(0.62f, 0.39f, 0.71f)); //  51 - Sb
    gaussianElementColors.add(new Color3f(0.83f, 0.48f, 0.00f)); //  52 - Te
    gaussianElementColors.add(new Color3f(0.58f, 0.00f, 0.58f)); //  53 - I
    gaussianElementColors.add(new Color3f(0.26f, 0.62f, 0.69f)); //  54 - Xe
    gaussianElementColors.add(new Color3f(0.34f, 0.09f, 0.56f)); //  55 - Cs
    gaussianElementColors.add(new Color3f(0.40f, 0.20f, 0.00f)); //  56 - Ba
    gaussianElementColors.add(new Color3f(0.44f, 0.87f, 1.00f)); //  57 - La
    gaussianElementColors.add(new Color3f(1.00f, 1.00f, 0.78f)); //  58 - Ce
    gaussianElementColors.add(new Color3f(0.85f, 1.00f, 0.78f)); //  59 - Pr
    gaussianElementColors.add(new Color3f(0.78f, 1.00f, 0.78f)); //  60 - Nd
    gaussianElementColors.add(new Color3f(0.64f, 1.00f, 0.78f)); //  61 - Pm
    gaussianElementColors.add(new Color3f(0.56f, 1.00f, 0.78f)); //  62 - Sm
    gaussianElementColors.add(new Color3f(0.38f, 1.00f, 0.78f)); //  63 - Eu
    gaussianElementColors.add(new Color3f(0.27f, 1.00f, 0.78f)); //  64 - Gd
    gaussianElementColors.add(new Color3f(0.19f, 1.00f, 0.78f)); //  65 - Tb
    gaussianElementColors.add(new Color3f(0.12f, 1.00f, 0.71f)); //  66 - Dy
    gaussianElementColors.add(new Color3f(0.00f, 1.00f, 0.71f)); //  67 - Ho
    gaussianElementColors.add(new Color3f(0.00f, 0.90f, 0.46f)); //  68 - Er
    gaussianElementColors.add(new Color3f(0.00f, 0.83f, 0.32f)); //  69 - Tm
    gaussianElementColors.add(new Color3f(0.00f, 0.75f, 0.22f)); //  70 - Yb
    gaussianElementColors.add(new Color3f(0.00f, 0.67f, 0.14f)); //  71 - Lu
    gaussianElementColors.add(new Color3f(0.30f, 0.76f, 1.00f)); //  72 - Hf
    gaussianElementColors.add(new Color3f(0.30f, 0.65f, 1.00f)); //  73 - Ta
    gaussianElementColors.add(new Color3f(0.15f, 0.58f, 0.84f)); //  74 - W
    gaussianElementColors.add(new Color3f(0.15f, 0.49f, 0.67f)); //  75 - Re
    gaussianElementColors.add(new Color3f(0.15f, 0.40f, 0.59f)); //  76 - Os
    gaussianElementColors.add(new Color3f(0.09f, 0.33f, 0.53f)); //  77 - Ir
    gaussianElementColors.add(new Color3f(0.09f, 0.36f, 0.56f)); //  78 - Pt
    gaussianElementColors.add(new Color3f(1.00f, 0.82f, 0.14f)); //  79 - Au
    gaussianElementColors.add(new Color3f(0.71f, 0.71f, 0.76f)); //  80 - Hg
    gaussianElementColors.add(new Color3f(0.65f, 0.33f, 0.30f)); //  81 - Tl
    gaussianElementColors.add(new Color3f(0.34f, 0.35f, 0.38f)); //  82 - Pb
    gaussianElementColors.add(new Color3f(0.62f, 0.31f, 0.71f)); //  83 - Bi
    gaussianElementColors.add(new Color3f(0.67f, 0.36f, 0.00f)); //  84 - Po
    gaussianElementColors.add(new Color3f(0.46f, 0.31f, 0.27f)); //  85 - At
    gaussianElementColors.add(new Color3f(0.26f, 0.51f, 0.59f)); //  86 - Rn
    gaussianElementColors.add(new Color3f(0.26f, 0.00f, 0.40f)); //  87 - Fr
    gaussianElementColors.add(new Color3f(0.30f, 0.10f, 0.00f)); //  88 - Ra
    gaussianElementColors.add(new Color3f(0.44f, 0.67f, 0.98f)); //  89 - Ac
    gaussianElementColors.add(new Color3f(0.00f, 0.73f, 1.00f)); //  90 - Th
    gaussianElementColors.add(new Color3f(0.00f, 0.63f, 1.00f)); //  91 - Pa
    gaussianElementColors.add(new Color3f(0.00f, 0.56f, 1.00f)); //  92 - U
    gaussianElementColors.add(new Color3f(0.00f, 0.50f, 0.95f)); //  93 - Np
    gaussianElementColors.add(new Color3f(0.00f, 0.42f, 0.95f)); //  94 - Pu
    gaussianElementColors.add(new Color3f(0.33f, 0.36f, 0.95f)); //  95 - Am
    gaussianElementColors.add(new Color3f(0.47f, 0.36f, 0.89f)); //  96 - Cm
    gaussianElementColors.add(new Color3f(0.54f, 0.37f, 0.89f)); //  97 - Bk
    gaussianElementColors.add(new Color3f(0.63f, 0.21f, 0.83f)); //  98 - Cf
    gaussianElementColors.add(new Color3f(0.66f, 0.17f, 0.78f)); //  99 - Es
    gaussianElementColors.add(new Color3f(0.70f, 0.12f, 0.73f)); // 100 - Fm
    gaussianElementColors.add(new Color3f(0.70f, 0.05f, 0.65f)); // 101 - Md
    gaussianElementColors.add(new Color3f(0.74f, 0.05f, 0.53f)); // 102 - No
    gaussianElementColors.add(new Color3f(0.78f, 0.00f, 0.40f)); // 103 - Lr
    gaussianElementColors.add(new Color3f(1.00f, 0.50f, 0.50f)); // 104 - Db
    gaussianElementColors.add(new Color3f(0.90f, 0.40f, 0.40f)); // 105 - Jl
    gaussianElementColors.add(new Color3f(0.80f, 0.30f, 0.30f)); // 106 - Rf
    gaussianElementColors.add(new Color3f(0.70f, 0.20f, 0.20f)); // 107 - Bh
    gaussianElementColors.add(new Color3f(0.60f, 0.10f, 0.10f)); // 108 - Hn
    gaussianElementColors.add(new Color3f(0.50f, 0.00f, 0.00f)); // 109 - Mt

    jmolElementColors.add(new Color3f(new Color(0xFA1691))); // 0 - Dummy
    jmolElementColors.add(new Color3f(new Color(255, 255, 255))); //   1 - H
    jmolElementColors.add(new Color3f(new Color(217, 255, 255))); //   2 - He
    jmolElementColors.add(new Color3f(new Color(204, 128, 255))); //   3 - Li
    jmolElementColors.add(new Color3f(new Color(194, 255, 0))); //   4 - Be
    jmolElementColors.add(new Color3f(new Color(255, 181, 181))); //   5 - B
    jmolElementColors.add(new Color3f(new Color(144, 144, 144))); //   6 - C
    jmolElementColors.add(new Color3f(new Color(48, 80, 248))); //   7 - N
    jmolElementColors.add(new Color3f(new Color(255, 13, 13))); //   8 - O
    jmolElementColors.add(new Color3f(new Color(144, 224, 80))); //   9 - F
    jmolElementColors.add(new Color3f(new Color(179, 227, 245))); //  10 - Ne
    jmolElementColors.add(new Color3f(new Color(171, 92, 242))); //  11 - Na
    jmolElementColors.add(new Color3f(new Color(138, 255, 0))); //  12 - Mg
    jmolElementColors.add(new Color3f(new Color(191, 166, 166))); //  13 - Al
    jmolElementColors.add(new Color3f(new Color(240, 200, 160))); //  14 - Si
    jmolElementColors.add(new Color3f(new Color(255, 128, 0))); //  15 - P
    jmolElementColors.add(new Color3f(new Color(255, 255, 48))); //  16 - S
    jmolElementColors.add(new Color3f(new Color(31, 240, 31))); //  17 - Cl
    jmolElementColors.add(new Color3f(new Color(128, 209, 227))); //  18 - Ar
    jmolElementColors.add(new Color3f(new Color(143, 64, 212))); //  19 - K
    jmolElementColors.add(new Color3f(new Color(61, 255, 0))); //  20 - Ca
    jmolElementColors.add(new Color3f(new Color(230, 230, 230))); //  21 - Sc
    jmolElementColors.add(new Color3f(new Color(191, 194, 199))); //  22 - Ti
    jmolElementColors.add(new Color3f(new Color(166, 166, 171))); //  23 - V
    jmolElementColors.add(new Color3f(new Color(138, 153, 199))); //  24 - Cr
    jmolElementColors.add(new Color3f(new Color(156, 122, 199))); //  25 - Mn
    jmolElementColors.add(new Color3f(new Color(224, 102, 51))); //  26 - Fe
    jmolElementColors.add(new Color3f(new Color(240, 144, 160))); //  27 - Co
    jmolElementColors.add(new Color3f(new Color(80, 208, 80))); //  28 - Ni
    jmolElementColors.add(new Color3f(new Color(200, 128, 51))); //  29 - Cu
    jmolElementColors.add(new Color3f(new Color(125, 128, 176))); //  30 - Zn
    jmolElementColors.add(new Color3f(new Color(194, 143, 143))); //  31 - Ga
    jmolElementColors.add(new Color3f(new Color(102, 143, 143))); //  32 - Ge
    jmolElementColors.add(new Color3f(new Color(189, 128, 227))); //  33 - As
    jmolElementColors.add(new Color3f(new Color(255, 161, 0))); //  34 - Se
    jmolElementColors.add(new Color3f(new Color(166, 41, 41))); //  35 - Br
    jmolElementColors.add(new Color3f(new Color(92, 184, 209))); //  36 - Kr
    jmolElementColors.add(new Color3f(new Color(112, 46, 176))); //  37 - Rb
    jmolElementColors.add(new Color3f(new Color(0, 255, 0))); //  38 - Sr
    jmolElementColors.add(new Color3f(new Color(148, 255, 255))); //  39 - Y
    jmolElementColors.add(new Color3f(new Color(148, 224, 224))); //  40 - Zr
    jmolElementColors.add(new Color3f(new Color(115, 194, 201))); //  41 - Nb
    jmolElementColors.add(new Color3f(new Color(84, 181, 181))); //  42 - Mo
    jmolElementColors.add(new Color3f(new Color(59, 158, 158))); //  43 - Tc
    jmolElementColors.add(new Color3f(new Color(36, 143, 143))); //  44 - Ru
    jmolElementColors.add(new Color3f(new Color(10, 125, 140))); //  45 - Rh
    jmolElementColors.add(new Color3f(new Color(0, 105, 133))); //  46 - Pd
    jmolElementColors.add(new Color3f(new Color(192, 192, 192))); //  47 - Ag
    jmolElementColors.add(new Color3f(new Color(255, 217, 143))); //  48 - Cd
    jmolElementColors.add(new Color3f(new Color(166, 117, 115))); //  49 - In
    jmolElementColors.add(new Color3f(new Color(102, 128, 128))); //  50 - Sn
    jmolElementColors.add(new Color3f(new Color(158, 99, 181))); //  51 - Sb
    jmolElementColors.add(new Color3f(new Color(212, 122, 0))); //  52 - Te
    jmolElementColors.add(new Color3f(new Color(148, 0, 148))); //  53 - I
    jmolElementColors.add(new Color3f(new Color(66, 158, 176))); //  54 - Xe
    jmolElementColors.add(new Color3f(new Color(87, 23, 143))); //  55 - Cs
    jmolElementColors.add(new Color3f(new Color(0, 201, 0))); //  56 - Ba
    jmolElementColors.add(new Color3f(new Color(112, 212, 255))); //  57 - La
    jmolElementColors.add(new Color3f(new Color(255, 255, 199))); //  58 - Ce
    jmolElementColors.add(new Color3f(new Color(217, 255, 199))); //  59 - Pr
    jmolElementColors.add(new Color3f(new Color(199, 255, 199))); //  60 - Nd
    jmolElementColors.add(new Color3f(new Color(163, 255, 199))); //  61 - Pm
    jmolElementColors.add(new Color3f(new Color(143, 255, 199))); //  62 - Sm
    jmolElementColors.add(new Color3f(new Color(97, 255, 199))); //  63 - Eu
    jmolElementColors.add(new Color3f(new Color(69, 255, 199))); //  64 - Gd
    jmolElementColors.add(new Color3f(new Color(48, 255, 199))); //  65 - Tb
    jmolElementColors.add(new Color3f(new Color(31, 255, 199))); //  66 - Dy
    jmolElementColors.add(new Color3f(new Color(0, 255, 156))); //  67 - Ho
    jmolElementColors.add(new Color3f(new Color(0, 230, 117))); //  68 - Er
    jmolElementColors.add(new Color3f(new Color(0, 212, 82))); //  69 - Tm
    jmolElementColors.add(new Color3f(new Color(0, 191, 56))); //  70 - Yb
    jmolElementColors.add(new Color3f(new Color(0, 171, 36))); //  71 - Lu
    jmolElementColors.add(new Color3f(new Color(77, 194, 255))); //  72 - Hf
    jmolElementColors.add(new Color3f(new Color(77, 166, 255))); //  73 - Ta
    jmolElementColors.add(new Color3f(new Color(33, 148, 214))); //  74 - W
    jmolElementColors.add(new Color3f(new Color(38, 125, 171))); //  75 - Re
    jmolElementColors.add(new Color3f(new Color(38, 102, 150))); //  76 - Os
    jmolElementColors.add(new Color3f(new Color(23, 84, 135))); //  77 - Ir
    jmolElementColors.add(new Color3f(new Color(208, 208, 224))); //  78 - Pt
    jmolElementColors.add(new Color3f(new Color(255, 209, 35))); //  79 - Au
    jmolElementColors.add(new Color3f(new Color(184, 184, 208))); //  80 - Hg
    jmolElementColors.add(new Color3f(new Color(166, 84, 77))); //  81 - Tl
    jmolElementColors.add(new Color3f(new Color(87, 89, 97))); //  82 - Pb
    jmolElementColors.add(new Color3f(new Color(158, 79, 181))); //  83 - Bi
    jmolElementColors.add(new Color3f(new Color(171, 92, 0))); //  84 - Po
    jmolElementColors.add(new Color3f(new Color(117, 79, 69))); //  85 - At
    jmolElementColors.add(new Color3f(new Color(66, 130, 150))); //  86 - Rn
    jmolElementColors.add(new Color3f(new Color(66, 0, 102))); //  87 - Fr
    jmolElementColors.add(new Color3f(new Color(0, 125, 0))); //  88 - Ra
    jmolElementColors.add(new Color3f(new Color(112, 171, 250))); //  89 - Ac
    jmolElementColors.add(new Color3f(new Color(0, 186, 255))); //  90 - Th
    jmolElementColors.add(new Color3f(new Color(0, 161, 255))); //  91 - Pa
    jmolElementColors.add(new Color3f(new Color(0, 143, 255))); //  92 - U
    jmolElementColors.add(new Color3f(new Color(0, 128, 255))); //  93 - Np
    jmolElementColors.add(new Color3f(new Color(0, 107, 255))); //  94 - Pu
    jmolElementColors.add(new Color3f(new Color(84, 92, 242))); //  95 - Am
    jmolElementColors.add(new Color3f(new Color(120, 92, 227))); //  96 - Cm
    jmolElementColors.add(new Color3f(new Color(138, 79, 227))); //  97 - Bk
    jmolElementColors.add(new Color3f(new Color(161, 54, 212))); //  98 - Cf
    jmolElementColors.add(new Color3f(new Color(179, 31, 212))); //  99 - Es
    jmolElementColors.add(new Color3f(new Color(179, 31, 186))); // 100 - Fm
    jmolElementColors.add(new Color3f(new Color(179, 13, 166))); // 101 - Md
    jmolElementColors.add(new Color3f(new Color(189, 13, 135))); // 102 - No
    jmolElementColors.add(new Color3f(new Color(199, 0, 102))); // 103 - Lr
    jmolElementColors.add(new Color3f(new Color(204, 0, 89))); // 104 - Db
    jmolElementColors.add(new Color3f(new Color(209, 0, 79))); // 105 - Jl
    jmolElementColors.add(new Color3f(new Color(217, 0, 69))); // 106 - Rf
    jmolElementColors.add(new Color3f(new Color(224, 0, 56))); // 107 - Bh
    jmolElementColors.add(new Color3f(new Color(230, 0, 46))); // 108 - Hn
    jmolElementColors.add(new Color3f(new Color(235, 0, 38))); // 109 - Mt

    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //   0 - Dummy
    rasmolElementColors.add(new Color3f(new Color(0xFFFFFF))); //   1 - H
    rasmolElementColors.add(new Color3f(new Color(0xFFC0CB))); //   2 - He
    rasmolElementColors.add(new Color3f(new Color(0xB22222))); //   3 - Li
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //   4 - Be
    rasmolElementColors.add(new Color3f(new Color(0x00FF00))); //   5 - B
    rasmolElementColors.add(new Color3f(new Color(0xC8C8C8))); //   6 - C
    rasmolElementColors.add(new Color3f(new Color(0x8F8FFF))); //   7 - N
    rasmolElementColors.add(new Color3f(new Color(0xF00000))); //   8 - O
    rasmolElementColors.add(new Color3f(new Color(0xDAA520))); //   9 - F
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  10 - Ne
    rasmolElementColors.add(new Color3f(new Color(0x0000FF))); //  11 - Na
    rasmolElementColors.add(new Color3f(new Color(0x228B22))); //  12 - Mg
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  13 - Al
    rasmolElementColors.add(new Color3f(new Color(0xDAA520))); //  14 - Si
    rasmolElementColors.add(new Color3f(new Color(0xFFA500))); //  15 - P
    rasmolElementColors.add(new Color3f(new Color(0xFFC832))); //  16 - S
    rasmolElementColors.add(new Color3f(new Color(0x00FF00))); //  17 - Cl
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  18 - Ar
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  19 - K
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  20 - Ca
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  21 - Sc
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  22 - Ti
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  23 - V
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  24 - Cr
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  25 - Mn
    rasmolElementColors.add(new Color3f(new Color(0xFFA500))); //  26 - Fe
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  27 - Co
    rasmolElementColors.add(new Color3f(new Color(0xA52A2A))); //  28 - Ni
    rasmolElementColors.add(new Color3f(new Color(0xA52A2A))); //  29 - Cu
    rasmolElementColors.add(new Color3f(new Color(0xA52A2A))); //  30 - Zn
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  31 - Ga
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  32 - Ge
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  33 - As
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  34 - Se
    rasmolElementColors.add(new Color3f(new Color(0xA52A2A))); //  35 - Br
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  36 - Kr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  37 - Rb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  38 - Sr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  39 - Y
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  40 - Zr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  41 - Nb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  42 - Mo
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  43 - Tc
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  44 - Ru
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  45 - Rh
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  46 - Pd
    rasmolElementColors.add(new Color3f(new Color(0x808090))); //  47 - Ag
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  48 - Cd
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  49 - In
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  50 - Sn
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  51 - Sb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  52 - Te
    rasmolElementColors.add(new Color3f(new Color(0xA020F0))); //  53 - I
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  54 - Xe
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  55 - Cs
    rasmolElementColors.add(new Color3f(new Color(0xFFA500))); //  56 - Ba
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  57 - La
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  58 - Ce
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  59 - Pr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  60 - Nd
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  61 - Pm
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  62 - Sm
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  63 - Eu
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  64 - Gd
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  65 - Tb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  66 - Dy
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  67 - Ho
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  68 - Er
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  69 - Tm
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  70 - Yb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  71 - Lu
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  72 - Hf
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  73 - Ta
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  74 - W
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  75 - Re
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  76 - Os
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  77 - Ir
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  78 - Pt
    rasmolElementColors.add(new Color3f(new Color(0xDAA520))); //  79 - Au
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  80 - Hg
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  81 - Tl
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  82 - Pb
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  83 - Bi
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  84 - Po
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  85 - At
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  86 - Rn
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  87 - Fr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  88 - Ra
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  89 - Ac
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  90 - Th
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  91 - Pa
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  92 - U
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  93 - Np
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  94 - Pu
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  95 - Am
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  96 - Cm
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  97 - Bk
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  98 - Cf
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); //  99 - Es
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 100 - Fm
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 101 - Md
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 102 - No
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 103 - Lr
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 104 - Db
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 105 - Jl
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 106 - Rf
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 107 - Bh
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 108 - Hn
    rasmolElementColors.add(new Color3f(new Color(0xFF1493))); // 109 - Mt

    rasmolNewElementColors.addAll(rasmolElementColors);
    rasmolNewElementColors.set(0, new Color3f(new Color(0xFA1691))); // 0 - Unknown
    rasmolNewElementColors.set(3, new Color3f(new Color(0xB22121))); // 3 - Li
    rasmolNewElementColors.set(6, new Color3f(new Color(0xD3D3D3))); // 6 - C
    rasmolNewElementColors.set(7, new Color3f(new Color(0x87CEE6))); // 7 - N
    rasmolNewElementColors.set(8, new Color3f(new Color(0xFF0000))); // 8 - O
    rasmolNewElementColors.set(13, new Color3f(new Color(0x696969))); // 13 - Al
    rasmolNewElementColors.set(15, new Color3f(new Color(0xFFAA00))); // 15 - P
    rasmolNewElementColors.set(16, new Color3f(new Color(0xFFFF00))); // 16 - S
    rasmolNewElementColors.set(20, new Color3f(new Color(0x696969))); // 20 - Ca
    rasmolNewElementColors.set(22, new Color3f(new Color(0x696969))); // 22 - Ti
    rasmolNewElementColors.set(24, new Color3f(new Color(0x696969))); // 24 - Cr
    rasmolNewElementColors.set(25, new Color3f(new Color(0x696969))); // 25 - Mn
    rasmolNewElementColors.set(26, new Color3f(new Color(0xFFAA00))); // 26 - Fe
    rasmolNewElementColors.set(28, new Color3f(new Color(0x802828))); // 28 - Ni
    rasmolNewElementColors.set(29, new Color3f(new Color(0x802828))); // 29 - Cu
    rasmolNewElementColors.set(30, new Color3f(new Color(0x802828))); // 30 - Zn
    rasmolNewElementColors.set(35, new Color3f(new Color(0x802828))); // 35 - Br
    rasmolNewElementColors.set(47, new Color3f(new Color(0x696969))); // 47 - Ag
    rasmolNewElementColors.set(56, new Color3f(new Color(0xFFAA00))); // 56 - Ba

    colorScheme.put(DEFAULT_ATOM_COLOR_SCHEME, elementColors);
    colorScheme.put(ATOM_COLOR_SCHEME_2, gaussianElementColors);
    colorScheme.put(ATOM_COLOR_SCHEME_JMOL, jmolElementColors);
    colorScheme.put(ATOM_COLOR_SCHEME_RASMOL, rasmolElementColors);
    colorScheme.put(ATOM_COLOR_SCHEME_RASMOL_NEW, rasmolNewElementColors);

    currentColorScheme = elementColors;

  }

  public static Color3f getElementColor(int atomNumber) {
    Color3f color = new Color3f(Color.LIGHT_GRAY);
    if (atomNumber < 0 || atomNumber >= elementColors.size()) {
      return color;
    }
    try {
      //color.set( (Color3f) elementColors.get(atomNumber));
      color.set( (Color3f) currentColorScheme.get(atomNumber));
    }
    catch (IndexOutOfBoundsException e) {
      //
    }
    return color;
  }

  public static Material createMaterial(Color3f color) {
    if (color == null) {
      return null;
    }
    Material material = new Material();
    // --- Diffuse Color
    material.setDiffuseColor(color);

    // --- Ambient Color
    material.setAmbientColor(0.2f * color.x, 0.2f * color.y, 0.2f * color.z);

    // --- Emissive Color
    material.setEmissiveColor(0.0f, 0.0f, 0.0f);

    // --- Specular Color
    material.setSpecularColor(1.0f, 1.0f, 1.0f);

    // --- Set Shininess
    material.setShininess(15.0f);

    material.setCapability(Material.ALLOW_COMPONENT_READ);
    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
    return material;

  }

  public static Material getElementMaterial(int atomNumber) {
    Material material = new Material();
    if (atomNumber < 0 || atomNumber >= elementColors.size()) {
      return material;
    }

    Color3f color = new Color3f();
    try {
      color.set( (Color3f) currentColorScheme.get(atomNumber));
      //color.set( (Color3f) elementColors.get(atomNumber));
    }
    catch (IndexOutOfBoundsException e) {
      //
    }

    return createMaterial(color);
  }

  /**
   * Returns number of available atom color schemes
   * @return int
   */
  public static int getAtomColorSchemeNumber() {
    return colorScheme.size();
  }

  public static String getCurrentAtomColorScheme() {
    return currentColorSchemeName;
  }

  public static void setCurrentAtomColorScheme(String scheme) {
    if (colorScheme.containsKey(scheme)) {
      currentColorSchemeName = scheme;
      currentColorScheme = (List) colorScheme.get(scheme);
      return;
    }
    System.err.println("No such atom color scheme: " + scheme + " Ignored...");
  }

  public static String[] getAtomColorSchemeNames() {

    if (colorScheme == null || colorScheme.size() < 1) {
      return null;
    }

    String[] schemes = new String[colorScheme.size()];
    Set set = colorScheme.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      schemes[count] = me.getKey().toString();
      ++count;
    }
    return schemes;
  }

  public static void retrieveAtomColorSchemePrefs(Class c) {
    try {
      prefs = Preferences.userNodeForPackage(c);
    }
    catch (Exception ex) {
      System.err.println("Error retrieving Atom Color Scheme Preferences: " +
                         ex.getMessage() + " Ignored...");
      return;
    }

    String scheme = prefs.get(atomColorSchemeKey, currentColorSchemeName);
    if (colorScheme.containsKey(scheme)) {
      currentColorSchemeName = scheme;
      currentColorScheme = (List) colorScheme.get(scheme);
      return;
    }
    else {
      logger.info("Retrieving Preferences: There is no such Atom Color Scheme: " + scheme);
      currentColorSchemeName = ATOM_COLOR_SCHEME_2;
      currentColorScheme = (List) colorScheme.get(currentColorSchemeName);
    }
  }

  public static void saveAtomColorSchemePrefs(Class c) {
    try {
      prefs = Preferences.userNodeForPackage(c);
    }
    catch (Exception ex) {
      System.err.println("Error saving Atom Color Scheme Preferences: " +
                         ex.getMessage());
      return;
    }

    try {
      prefs.put(atomColorSchemeKey, currentColorSchemeName);
    }
    catch (Exception ex) {
      System.err.println("Cannot save Atom Color Scheme Preferences: " +
                         ex.getMessage());
    }

  }

  public static Material getHighlightMaterial() {
    return highlightMaterial;
  }

  public static Color3f getHighlightColor3f() {
    return highlightColor;
  }

}
