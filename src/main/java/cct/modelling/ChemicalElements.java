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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <p>
 * Title: ChemicalElements</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class ChemicalElements {

  static final int numberOfElements = 110;
  static final String elementSymbol[] = new String[numberOfElements];
  static final String elementName[] = new String[numberOfElements];
  static final float covalentRadii[] = new float[numberOfElements];
  static final float atomicWeight[] = new float[numberOfElements];
  static final float vanderwaalsRadii[] = new float[numberOfElements];
  static final float uffVDWRadii[] = new float[numberOfElements];
  static final float uffD[] = new float[numberOfElements];
  static final private Map<String, Integer> elementSymbolToAtomicNumber = new HashMap<String, Integer>(numberOfElements);
  static final private Map<String, Float> elementSymbolToCovalentRadius = new HashMap<String, Float>(numberOfElements);

  protected ChemicalElements() {

  }

  static {

    elementSymbol[0] = "Du";
    elementSymbol[1] = "H";
    elementSymbol[2] = "He";
    elementSymbol[3] = "Li";
    elementSymbol[4] = "Be";
    elementSymbol[5] = "B";
    elementSymbol[6] = "C";
    elementSymbol[7] = "N";
    elementSymbol[8] = "O";
    elementSymbol[9] = "F";
    elementSymbol[10] = "Ne";
    elementSymbol[11] = "Na";
    elementSymbol[12] = "Mg";
    elementSymbol[13] = "Al";
    elementSymbol[14] = "Si";
    elementSymbol[15] = "P";
    elementSymbol[16] = "S";
    elementSymbol[17] = "Cl";
    elementSymbol[18] = "Ar";
    elementSymbol[19] = "K";
    elementSymbol[20] = "Ca";
    elementSymbol[21] = "Sc";
    elementSymbol[22] = "Ti";
    elementSymbol[23] = "V";
    elementSymbol[24] = "Cr";
    elementSymbol[25] = "Mn";
    elementSymbol[26] = "Fe";
    elementSymbol[27] = "Co";
    elementSymbol[28] = "Ni";
    elementSymbol[29] = "Cu";
    elementSymbol[30] = "Zn";
    elementSymbol[31] = "Ga";
    elementSymbol[32] = "Ge";
    elementSymbol[33] = "As";
    elementSymbol[34] = "Se";
    elementSymbol[35] = "Br";
    elementSymbol[36] = "Kr";
    elementSymbol[37] = "Rb";
    elementSymbol[38] = "Sr";
    elementSymbol[39] = "Y";
    elementSymbol[40] = "Zr";
    elementSymbol[41] = "Nb";
    elementSymbol[42] = "Mo";
    elementSymbol[43] = "Tc";
    elementSymbol[44] = "Ru";
    elementSymbol[45] = "Rh";
    elementSymbol[46] = "Pd";
    elementSymbol[47] = "Ag";
    elementSymbol[48] = "Cd";
    elementSymbol[49] = "In";
    elementSymbol[50] = "Sn";
    elementSymbol[51] = "Sb";
    elementSymbol[52] = "Te";
    elementSymbol[53] = "I";
    elementSymbol[54] = "Xe";
    elementSymbol[55] = "Cs";
    elementSymbol[56] = "Ba";
    elementSymbol[57] = "La";
    elementSymbol[58] = "Ce";
    elementSymbol[59] = "Pr";
    elementSymbol[60] = "Nd";
    elementSymbol[61] = "Pm";
    elementSymbol[62] = "Sm";
    elementSymbol[63] = "Eu";
    elementSymbol[64] = "Gd";
    elementSymbol[65] = "Tb";
    elementSymbol[66] = "Dy";
    elementSymbol[67] = "Ho";
    elementSymbol[68] = "Er";
    elementSymbol[69] = "Tm";
    elementSymbol[70] = "Yb";
    elementSymbol[71] = "Lu";
    elementSymbol[72] = "Hf";
    elementSymbol[73] = "Ta";
    elementSymbol[74] = "W";
    elementSymbol[75] = "Re";
    elementSymbol[76] = "Os";
    elementSymbol[77] = "Ir";
    elementSymbol[78] = "Pt";
    elementSymbol[79] = "Au";
    elementSymbol[80] = "Hg";
    elementSymbol[81] = "Tl";
    elementSymbol[82] = "Pb";
    elementSymbol[83] = "Bi";
    elementSymbol[84] = "Po";
    elementSymbol[85] = "At";
    elementSymbol[86] = "Rn";
    elementSymbol[87] = "Fr";
    elementSymbol[88] = "Ra";
    elementSymbol[89] = "Ac";
    elementSymbol[90] = "Th";
    elementSymbol[91] = "Pa";
    elementSymbol[92] = "U";
    elementSymbol[93] = "Np";
    elementSymbol[94] = "Pu";
    elementSymbol[95] = "Am";
    elementSymbol[96] = "Cm";
    elementSymbol[97] = "Bk";
    elementSymbol[98] = "Cf";
    elementSymbol[99] = "Es";
    elementSymbol[100] = "Fm";
    elementSymbol[101] = "Md";
    elementSymbol[102] = "No";
    elementSymbol[103] = "Lr";
    elementSymbol[104] = "Rf";
    elementSymbol[105] = "Db";
    elementSymbol[106] = "Sg";
    elementSymbol[107] = "Bh";
    elementSymbol[108] = "Hn";
    elementSymbol[109] = "Mt";

    elementName[0] = "Dummy";
    elementName[1] = "Hydrogen";
    elementName[2] = "Helium";
    elementName[3] = "Lithium";
    elementName[4] = "Beryllium";
    elementName[5] = "Boron";
    elementName[6] = "Carbon";
    elementName[7] = "Nitrogen";
    elementName[8] = "Oxygen";
    elementName[9] = "Fluorine";
    elementName[10] = "Neon";
    elementName[11] = "Sodium";
    elementName[12] = "Magnesium";
    elementName[13] = "Aluminum";
    elementName[14] = "Silicon";
    elementName[15] = "Phosphorus";
    elementName[16] = "Sulfur";
    elementName[17] = "Chlorine";
    elementName[18] = "Argon";
    elementName[19] = "Potassium";
    elementName[20] = "Calcium";
    elementName[21] = "Scandium";
    elementName[22] = "Titanium";
    elementName[23] = "Vanadium";
    elementName[24] = "Chromium";
    elementName[25] = "Manganese";
    elementName[26] = "Iron";
    elementName[27] = "Cobalt";
    elementName[28] = "Nickel";
    elementName[29] = "Copper";
    elementName[30] = "Zinc";
    elementName[31] = "Gallium";
    elementName[32] = "Germanium";
    elementName[33] = "Arsenic";
    elementName[34] = "Selenium";
    elementName[35] = "Bromine";
    elementName[36] = "Krypton";
    elementName[37] = "Rubidium";
    elementName[38] = "Strontium";
    elementName[39] = "Yttrium";
    elementName[40] = "Zirconium";
    elementName[41] = "Niobium";
    elementName[42] = "Molybdenum";
    elementName[43] = "Technetium";
    elementName[44] = "Ruthenium";
    elementName[45] = "Rhodium";
    elementName[46] = "Palladium";
    elementName[47] = "Silver";
    elementName[48] = "Cadmium";
    elementName[49] = "Indium";
    elementName[50] = "Tin";
    elementName[51] = "Antimony";
    elementName[52] = "Tellurium";
    elementName[53] = "Iodine";
    elementName[54] = "Xenon";
    elementName[55] = "Cesium";
    elementName[56] = "Barium";
    elementName[57] = "Lanthanum";
    elementName[58] = "Cerium";
    elementName[59] = "Praseodymium";
    elementName[60] = "Neodymium";
    elementName[61] = "Promethium";
    elementName[62] = "Samarium";
    elementName[63] = "Europium";
    elementName[64] = "Gadolinium";
    elementName[65] = "Terbium";
    elementName[66] = "Dysprosium";
    elementName[67] = "Holmium";
    elementName[68] = "Erbium";
    elementName[69] = "Thulium";
    elementName[70] = "Ytterbium";
    elementName[71] = "Lutetium";
    elementName[72] = "Hafnium";
    elementName[73] = "Tantalum";
    elementName[74] = "Tungsten";
    elementName[75] = "Rhenium";
    elementName[76] = "Osmium";
    elementName[77] = "Iridium";
    elementName[78] = "Platinum";
    elementName[79] = "Gold";
    elementName[80] = "Mercury";
    elementName[81] = "Thallium";
    elementName[82] = "Lead";
    elementName[83] = "Bismuth";
    elementName[84] = "Polonium";
    elementName[85] = "Astatine";
    elementName[86] = "Radon";
    elementName[87] = "Francium";
    elementName[88] = "Radium";
    elementName[89] = "Actinium";
    elementName[90] = "Thorium";
    elementName[91] = "Protactinium";
    elementName[92] = "Uranium";
    elementName[93] = "Neptunium";
    elementName[94] = "Plutonium";
    elementName[95] = "Americium";
    elementName[96] = "Curium";
    elementName[97] = "Berkelium";
    elementName[98] = "Californium";
    elementName[99] = "Einsteinium";
    elementName[100] = "Fermium";
    elementName[101] = "Mendelevium";
    elementName[102] = "Nobelium";
    elementName[103] = "Lawrencium";
    elementName[104] = "Rutherfordium";
    elementName[105] = "Dubnium";
    elementName[106] = "Seaborgium";
    elementName[107] = "Bohrium";
    elementName[108] = "Hahnium";
    elementName[109] = "Meitnerium";

    covalentRadii[0] = 0.0f;
    covalentRadii[1] = 0.55f; // H
    covalentRadii[2] = 1.00f; // He
    covalentRadii[3] = 1.56f; // Li
    covalentRadii[4] = 1.13f; // Be
    covalentRadii[5] = 0.95f; // B
    covalentRadii[6] = 0.86f; // C
    covalentRadii[7] = 0.80f; // N
    covalentRadii[8] = 0.66f; // O
    covalentRadii[9] = 0.64f; // F
    covalentRadii[10] = 1.00f; // Ne
    covalentRadii[11] = 1.91f; // Na
    covalentRadii[12] = 1.60f; // Mg - Original value 1.6
    covalentRadii[13] = 1.43f; // Al
    covalentRadii[14] = 1.34f; // Si
    covalentRadii[15] = 1.30f; // P
    covalentRadii[16] = 1.3f; // S - original: 1.04f
    covalentRadii[17] = 1.62f; // Cl
    covalentRadii[18] = 1.00f; // Ar
    covalentRadii[19] = 1.9f; // K - Original value 2.34
    covalentRadii[20] = 1.97f; // Ca
    covalentRadii[21] = 1.64f; // Sc
    covalentRadii[22] = 1.45f; // Ti
    covalentRadii[23] = 1.35f; // V
    covalentRadii[24] = 1.27f; // Cr
    covalentRadii[25] = 1.32f; // Mn
    covalentRadii[26] = 1.27f; // Fe - original value 1.27f
    covalentRadii[27] = 1.26f; // Co
    covalentRadii[28] = 1.24f; // Ni
    covalentRadii[29] = 1.28f; // Cu
    covalentRadii[30] = 1.39f; // Zn
    covalentRadii[31] = 1.40f; // Ga
    covalentRadii[32] = 1.40f; // Ge
    covalentRadii[33] = 1.50f; // As
    covalentRadii[34] = 1.60f; // Se
    covalentRadii[35] = 1.11f; // Br
    covalentRadii[36] = 2.34f; // Kr
    covalentRadii[37] = 2.50f; // Rb
    covalentRadii[38] = 2.15f; // Sr
    covalentRadii[39] = 1.80f; // Y
    covalentRadii[40] = 1.60f; // Zr
    covalentRadii[41] = 1.48f; // Nb
    covalentRadii[42] = 1.40f; // Mo
    covalentRadii[43] = 1.35f; // Tc
    covalentRadii[44] = 1.32f; // Ru
    covalentRadii[45] = 1.34f; // Rh
    covalentRadii[46] = 1.37f; // Pd
    covalentRadii[47] = 1.44f; // Ag
    covalentRadii[48] = 1.57f; // Cd
    covalentRadii[49] = 1.66f; // In
    covalentRadii[50] = 1.58f; // Sn
    covalentRadii[51] = 1.60f; // Sb
    covalentRadii[52] = 1.70f; // Te
    covalentRadii[53] = 1.95f; // I
    covalentRadii[54] = 0.00f; // Xe
    covalentRadii[55] = 2.71f; // Cs
    covalentRadii[56] = 2.24f; // Ba
    covalentRadii[57] = 1.87f; // La
    covalentRadii[58] = 1.82f; // Ce
    covalentRadii[59] = 1.83f; // Pr
    covalentRadii[60] = 1.82f; // Nd
    covalentRadii[61] = 0.00f; // Pm
    covalentRadii[62] = 1.80f; // Sm
    covalentRadii[63] = 2.04f; // Eu
    covalentRadii[64] = 1.80f; // Gd
    covalentRadii[65] = 1.78f; // Tb
    covalentRadii[66] = 1.77f; // Dy
    covalentRadii[67] = 1.77f; // Ho
    covalentRadii[68] = 1.76f; // Er
    covalentRadii[69] = 1.75f; // Tm
    covalentRadii[70] = 1.94f; // Yb
    covalentRadii[71] = 1.73f; // Lu
    covalentRadii[72] = 1.59f; // Hf
    covalentRadii[73] = 1.48f; // Ta
    covalentRadii[74] = 1.41f; // W
    covalentRadii[75] = 1.46f; // Re
    covalentRadii[76] = 1.34f; // Os
    covalentRadii[77] = 1.36f; // Ir
    covalentRadii[78] = 1.39f; // Pt
    covalentRadii[79] = 1.44f; // Au
    covalentRadii[80] = 1.62f; // Hg
    covalentRadii[81] = 1.73f; // Tl
    covalentRadii[82] = 1.75f; // Pb
    covalentRadii[83] = 1.70f; // Bi
    covalentRadii[84] = 1.70f; // Po
    covalentRadii[85] = 0.00f; // At
    covalentRadii[86] = 0.00f; // Rn
    covalentRadii[87] = 0.00f; // Fr
    covalentRadii[88] = 0.00f; // Ra
    covalentRadii[89] = 1.88f; // Ac
    covalentRadii[90] = 1.80f; // Th
    covalentRadii[91] = 1.61f; // Pa
    covalentRadii[92] = 1.55f; // U
    covalentRadii[93] = 1.58f; // Np
    covalentRadii[94] = 1.64f; // Pu
    covalentRadii[95] = 1.73f; // Am
    covalentRadii[96] = 0.00f; // Cm
    covalentRadii[97] = 0.00f; // Bk
    covalentRadii[98] = 0.00f; // Cf
    covalentRadii[99] = 0.00f; // Es
    covalentRadii[100] = 0.00f; // Fm
    covalentRadii[101] = 0.00f; // Md
    covalentRadii[102] = 0.00f; // No
    covalentRadii[103] = 0.00f; // Lr
    covalentRadii[104] = 0.00f; // Db
    covalentRadii[105] = 0.00f; // Jl
    covalentRadii[106] = 0.00f; // Rf
    covalentRadii[107] = 0.00f; // Bh
    covalentRadii[108] = 0.00f; // Hn
    covalentRadii[109] = 0.00f; // Mt

    // --- Taken from Bondi, J.Phys.Chem., 68, 441, 1964. Other elements are assigned van der Waals radii of 2.0A.
    vanderwaalsRadii[0] = 0.0f;
    vanderwaalsRadii[1] = 1.2f; // H
    vanderwaalsRadii[2] = 1.4f; // He
    vanderwaalsRadii[3] = 1.82f; // Li
    vanderwaalsRadii[4] = 2.f; // Be
    vanderwaalsRadii[5] = 2.f; // B
    vanderwaalsRadii[6] = 1.7f; // C
    vanderwaalsRadii[7] = 1.55f; // N
    vanderwaalsRadii[8] = 1.52f; // O
    vanderwaalsRadii[9] = 1.47f; // F
    vanderwaalsRadii[10] = 1.54f; // Ne
    vanderwaalsRadii[11] = 2.27f; // Na
    vanderwaalsRadii[12] = 1.73f; // Mg
    vanderwaalsRadii[13] = 2.f; // Al
    vanderwaalsRadii[14] = 2.1f; // Si
    vanderwaalsRadii[15] = 1.8f; // P
    vanderwaalsRadii[16] = 1.8f; // S
    vanderwaalsRadii[17] = 1.75f; // Cl
    vanderwaalsRadii[18] = 1.88f; // Ar
    vanderwaalsRadii[19] = 2.75f; // K
    vanderwaalsRadii[20] = 2.f; // Ca
    vanderwaalsRadii[21] = 2.f; // Sc
    vanderwaalsRadii[22] = 2.f; // Ti
    vanderwaalsRadii[23] = 2.f; // V
    vanderwaalsRadii[24] = 2.f; // Cr
    vanderwaalsRadii[25] = 2.f; // Mn
    vanderwaalsRadii[26] = 2.f; // Fe
    vanderwaalsRadii[27] = 2.f; // Co
    vanderwaalsRadii[28] = 1.63f; // Ni
    vanderwaalsRadii[29] = 1.40f; // Cu
    vanderwaalsRadii[30] = 1.49f; // Zn
    vanderwaalsRadii[31] = 1.87f; // Ga
    vanderwaalsRadii[32] = 2.f; // Ge
    vanderwaalsRadii[33] = 1.85f; // As
    vanderwaalsRadii[34] = 1.9f; // Se
    vanderwaalsRadii[35] = 1.85f; // Br
    vanderwaalsRadii[36] = 2.02f; // Kr
    vanderwaalsRadii[37] = 2.f; // Rb
    vanderwaalsRadii[38] = 2.f; // Sr
    vanderwaalsRadii[39] = 2.f; // Y
    vanderwaalsRadii[40] = 2.f; // Zr
    vanderwaalsRadii[41] = 2.f; // Nb
    vanderwaalsRadii[42] = 2.0f; // Mo
    vanderwaalsRadii[43] = 2.f; // Tc
    vanderwaalsRadii[44] = 2.f; // Ru
    vanderwaalsRadii[45] = 2.f; // Rh
    vanderwaalsRadii[46] = 1.63f; // Pd
    vanderwaalsRadii[47] = 1.72f; // Ag
    vanderwaalsRadii[48] = 1.58f; // Cd
    vanderwaalsRadii[49] = 1.93f; // In
    vanderwaalsRadii[50] = 2.17f; // Sn
    vanderwaalsRadii[51] = 2.f; // Sb
    vanderwaalsRadii[52] = 2.06f; // Te
    vanderwaalsRadii[53] = 1.98f; // I
    vanderwaalsRadii[54] = 2.16f; // Xe
    vanderwaalsRadii[55] = 2.f; // Cs
    vanderwaalsRadii[56] = 2.f; // Ba
    vanderwaalsRadii[57] = 2.f; // La
    vanderwaalsRadii[58] = 2.f; // Ce
    vanderwaalsRadii[59] = 2.f; // Pr
    vanderwaalsRadii[60] = 2.f; // Nd
    vanderwaalsRadii[61] = 2.00f; // Pm
    vanderwaalsRadii[62] = 2.0f; // Sm
    vanderwaalsRadii[63] = 2.f; // Eu
    vanderwaalsRadii[64] = 2.0f; // Gd
    vanderwaalsRadii[65] = 2.f; // Tb
    vanderwaalsRadii[66] = 2.f; // Dy
    vanderwaalsRadii[67] = 2.f; // Ho
    vanderwaalsRadii[68] = 2.f; // Er
    vanderwaalsRadii[69] = 2.f; // Tm
    vanderwaalsRadii[70] = 2.f; // Yb
    vanderwaalsRadii[71] = 2.f; // Lu
    vanderwaalsRadii[72] = 2.f; // Hf
    vanderwaalsRadii[73] = 2.f; // Ta
    vanderwaalsRadii[74] = 2.f; // W
    vanderwaalsRadii[75] = 2.f; // Re
    vanderwaalsRadii[76] = 2.f; // Os
    vanderwaalsRadii[77] = 2.f; // Ir
    vanderwaalsRadii[78] = 1.72f; // Pt
    vanderwaalsRadii[79] = 1.66f; // Au
    vanderwaalsRadii[80] = 1.55f; // Hg
    vanderwaalsRadii[81] = 1.96f; // Tl
    vanderwaalsRadii[82] = 2.02f; // Pb
    vanderwaalsRadii[83] = 2.0f; // Bi
    vanderwaalsRadii[84] = 2.0f; // Po
    vanderwaalsRadii[85] = 2.00f; // At
    vanderwaalsRadii[86] = 2.00f; // Rn
    vanderwaalsRadii[87] = 2.00f; // Fr
    vanderwaalsRadii[88] = 2.00f; // Ra
    vanderwaalsRadii[89] = 2.f; // Ac
    vanderwaalsRadii[90] = 2.0f; // Th
    vanderwaalsRadii[91] = 2.f; // Pa
    vanderwaalsRadii[92] = 1.86f; // U
    vanderwaalsRadii[93] = 2.f; // Np
    vanderwaalsRadii[94] = 2.f; // Pu
    vanderwaalsRadii[95] = 2.f; // Am
    vanderwaalsRadii[96] = 2.00f; // Cm
    vanderwaalsRadii[97] = 2.00f; // Bk
    vanderwaalsRadii[98] = 2.00f; // Cf
    vanderwaalsRadii[99] = 2.00f; // Es
    vanderwaalsRadii[100] = 2.00f; // Fm
    vanderwaalsRadii[101] = 2.00f; // Md
    vanderwaalsRadii[102] = 2.00f; // No
    vanderwaalsRadii[103] = 2.00f; // Lr
    vanderwaalsRadii[104] = 2.00f; // Db
    vanderwaalsRadii[105] = 2.00f; // Jl
    vanderwaalsRadii[106] = 2.00f; // Rf
    vanderwaalsRadii[107] = 2.00f; // Bh
    vanderwaalsRadii[108] = 2.00f; // Hn
    vanderwaalsRadii[109] = 2.00f; // Mt

    atomicWeight[0] = 0.0f;
    atomicWeight[1] = 1.00079f; // H
    atomicWeight[2] = 4.00260f; // He
    atomicWeight[3] = 6.94f; // Li
    atomicWeight[4] = 9.01218f; // Be
    atomicWeight[5] = 10.81f; // B
    atomicWeight[6] = 12.011f; // C
    atomicWeight[7] = 14.0067f; // N
    atomicWeight[8] = 15.9994f; // O
    atomicWeight[9] = 18.998403f; // F
    atomicWeight[10] = 20.179f; // Ne
    atomicWeight[11] = 22.98977f; // Na
    atomicWeight[12] = 24.305f; // Mg
    atomicWeight[13] = 26.98154f; // Al
    atomicWeight[14] = 28.0855f; // Si
    atomicWeight[15] = 30.97376f; // P
    atomicWeight[16] = 32.06f; // S
    atomicWeight[17] = 35.453f; // Cl
    atomicWeight[18] = 39.948f; // Ar
    atomicWeight[19] = 39.0983f; // K
    atomicWeight[20] = 40.08f; // Ca
    atomicWeight[21] = 44.9559f; // Sc
    atomicWeight[22] = 47.90f; // Ti
    atomicWeight[23] = 50.9415f; // V
    atomicWeight[24] = 51.996f; // Cr
    atomicWeight[25] = 54.9380f; // Mn
    atomicWeight[26] = 55.847f; // Fe
    atomicWeight[27] = 58.9332f; // Co
    atomicWeight[28] = 58.71f; // Ni
    atomicWeight[29] = 63.546f; // Cu
    atomicWeight[30] = 65.38f; // Zn
    atomicWeight[31] = 65.735f; // Ga
    atomicWeight[32] = 72.59f; // Ge
    atomicWeight[33] = 74.9216f; // As
    atomicWeight[34] = 78.96f; // Se
    atomicWeight[35] = 79.904f; // Br
    atomicWeight[36] = 83.80f; // Kr
    atomicWeight[37] = 85.467f; // Rb
    atomicWeight[38] = 87.62f; // Sr
    atomicWeight[39] = 88.9059f; // Y
    atomicWeight[40] = 91.22f; // Zr
    atomicWeight[41] = 92.9064f; // Nb
    atomicWeight[42] = 95.94f; // Mo
    atomicWeight[43] = 98.9062f; // Tc
    atomicWeight[44] = 101.07f; // Ru
    atomicWeight[45] = 102.9055f; // Rh
    atomicWeight[46] = 106.4f; // Pd
    atomicWeight[47] = 107.868f; // Ag
    atomicWeight[48] = 112.41f; // Cd
    atomicWeight[49] = 114.82f; // In
    atomicWeight[50] = 118.69f; // Sn
    atomicWeight[51] = 121.75f; // Sb
    atomicWeight[52] = 127.60f; // Te
    atomicWeight[53] = 126.9045f; // I
    atomicWeight[54] = 131.30f; // Xe
    atomicWeight[55] = 132.9054f; // Cs
    atomicWeight[56] = 137.33f; // Ba
    atomicWeight[57] = 138.9055f; // La
    atomicWeight[58] = 140.12f; // Ce
    atomicWeight[59] = 140.9077f; // Pr
    atomicWeight[60] = 144.24f; // Nd
    atomicWeight[61] = 145.00f; // Pm
    atomicWeight[62] = 150.4f; // Sm
    atomicWeight[63] = 151.96f; // Eu
    atomicWeight[64] = 157.25f; // Gd
    atomicWeight[65] = 158.9254f; // Tb
    atomicWeight[66] = 162.50f; // Dy
    atomicWeight[67] = 164.9304f; // Ho
    atomicWeight[68] = 167.26f; // Er
    atomicWeight[69] = 168.9342f; // Tm
    atomicWeight[70] = 173.04f; // Yb
    atomicWeight[71] = 174.967f; // Lu
    atomicWeight[72] = 178.49f; // Hf
    atomicWeight[73] = 180.9479f; // Ta
    atomicWeight[74] = 183.85f; // W
    atomicWeight[75] = 186.207f; // Re
    atomicWeight[76] = 190.2f; // Os
    atomicWeight[77] = 192.22f; // Ir
    atomicWeight[78] = 195.09f; // Pt
    atomicWeight[79] = 196.9665f; // Au
    atomicWeight[80] = 200.59f; // Hg
    atomicWeight[81] = 204.37f; // Tl
    atomicWeight[82] = 207.2f; // Pb
    atomicWeight[83] = 208.9804f; // Bi
    atomicWeight[84] = 209.0f; // Po
    atomicWeight[85] = 210.0f; // At
    atomicWeight[86] = 222.0f; // Rn
    atomicWeight[87] = 223.0f; // Fr
    atomicWeight[88] = 226.0254f; // Ra
    atomicWeight[89] = 227.0f; // Ac
    atomicWeight[90] = 232.0381f; // Th
    atomicWeight[91] = 231.0359f; // Pa
    atomicWeight[92] = 238.029f; // U
    atomicWeight[93] = 237.0482f; // Np
    atomicWeight[94] = 244.0f; // Pu
    atomicWeight[95] = 243.0f; // Am
    atomicWeight[96] = 247.0f; // Cm
    atomicWeight[97] = 247.0f; // Bk
    atomicWeight[98] = 251.0f; // Cf
    atomicWeight[99] = 254.0f; // Es
    atomicWeight[100] = 257.0f; // Fm
    atomicWeight[101] = 258.0f; // Md
    atomicWeight[102] = 259.0f; // No
    atomicWeight[103] = 260.0f; // Lr
    atomicWeight[104] = 260.0f; // Db
    atomicWeight[105] = 260.0f; // Jl
    atomicWeight[106] = 266f; // Rf
    atomicWeight[107] = 261f; // Bh
    atomicWeight[108] = 264f; // Hn
    atomicWeight[109] = 266f; // Mt

    // --- Taken from JACS, 114(1992), 10026
    uffVDWRadii[0] = 0.0f;
    uffVDWRadii[1] = 2.886f; // H
    uffVDWRadii[2] = 2.362f; // He
    uffVDWRadii[3] = 2.454f; // Li
    uffVDWRadii[4] = 2.745f; // Be
    uffVDWRadii[5] = 4.083f; // B
    uffVDWRadii[6] = 3.851f; // C
    uffVDWRadii[7] = 3.66f; // N
    uffVDWRadii[8] = 3.5f; // O
    uffVDWRadii[9] = 3.364f; // F
    uffVDWRadii[10] = 3.243f; // Ne
    uffVDWRadii[11] = 2.983f; // Na
    uffVDWRadii[12] = 3.021f; // Mg
    uffVDWRadii[13] = 4.499f; // Al
    uffVDWRadii[14] = 4.295f; // Si
    uffVDWRadii[15] = 4.147f; // P
    uffVDWRadii[16] = 4.035f; // S
    uffVDWRadii[17] = 3.947f; // Cl
    uffVDWRadii[18] = 3.868f; // Ar
    uffVDWRadii[19] = 3.812f; // K
    uffVDWRadii[20] = 3.399f; // Ca
    uffVDWRadii[21] = 3.295f; // Sc
    uffVDWRadii[22] = 3.175f; // Ti
    uffVDWRadii[23] = 3.144f; // V
    uffVDWRadii[24] = 3.023f; // Cr
    uffVDWRadii[25] = 2.961f; // Mn
    uffVDWRadii[26] = 2.912f; // Fe
    uffVDWRadii[27] = 2.872f; // Co
    uffVDWRadii[28] = 2.834f; // Ni
    uffVDWRadii[29] = 3.495f; // Cu
    uffVDWRadii[30] = 2.763f; // Zn
    uffVDWRadii[31] = 4.383f; // Ga
    uffVDWRadii[32] = 4.280f; // Ge
    uffVDWRadii[33] = 4.230f; // As
    uffVDWRadii[34] = 4.205f; // Se
    uffVDWRadii[35] = 4.189f; // Br
    uffVDWRadii[36] = 4.141f; // Kr
    uffVDWRadii[37] = 4.114f; // Rb
    uffVDWRadii[38] = 3.641f; // Sr
    uffVDWRadii[39] = 3.345f; // Y
    uffVDWRadii[40] = 3.124f; // Zr
    uffVDWRadii[41] = 3.165f; // Nb
    uffVDWRadii[42] = 3.052f; // Mo
    uffVDWRadii[43] = 2.998f; // Tc
    uffVDWRadii[44] = 2.963f; // Ru
    uffVDWRadii[45] = 2.929f; // Rh
    uffVDWRadii[46] = 2.899f; // Pd
    uffVDWRadii[47] = 3.148f; // Ag
    uffVDWRadii[48] = 2.848f; // Cd
    uffVDWRadii[49] = 4.463f; // In
    uffVDWRadii[50] = 4.392f; // Sn
    uffVDWRadii[51] = 4.420f; // Sb
    uffVDWRadii[52] = 4.470f; // Te
    uffVDWRadii[53] = 4.500f; // I
    uffVDWRadii[54] = 4.404f; // Xe
    uffVDWRadii[55] = 4.517f; // Cs
    uffVDWRadii[56] = 3.703f; // Ba
    uffVDWRadii[57] = 3.522f; // La
    uffVDWRadii[58] = 3.556f; // Ce
    uffVDWRadii[59] = 3.606f; // Pr
    uffVDWRadii[60] = 3.575f; // Nd
    uffVDWRadii[61] = 3.547f; // Pm
    uffVDWRadii[62] = 3.520f; // Sm
    uffVDWRadii[63] = 3.493f; // Eu
    uffVDWRadii[64] = 3.368f; // Gd
    uffVDWRadii[65] = 3.451f; // Tb
    uffVDWRadii[66] = 3.428f; // Dy
    uffVDWRadii[67] = 3.409f; // Ho
    uffVDWRadii[68] = 3.391f; // Er
    uffVDWRadii[69] = 3.374f; // Tm
    uffVDWRadii[70] = 3.355f; // Yb
    uffVDWRadii[71] = 3.640f; // Lu
    uffVDWRadii[72] = 3.141f; // Hf
    uffVDWRadii[73] = 3.170f; // Ta
    uffVDWRadii[74] = 3.069f; // W
    uffVDWRadii[75] = 2.954f; // Re
    uffVDWRadii[76] = 3.120f; // Os
    uffVDWRadii[77] = 2.840f; // Ir
    uffVDWRadii[78] = 2.754f; // Pt
    uffVDWRadii[79] = 3.293f; // Au
    uffVDWRadii[80] = 2.705f; // Hg
    uffVDWRadii[81] = 4.347f; // Tl
    uffVDWRadii[82] = 4.297f; // Pb
    uffVDWRadii[83] = 4.370f; // Bi
    uffVDWRadii[84] = 4.709f; // Po
    uffVDWRadii[85] = 4.750f; // At
    uffVDWRadii[86] = 4.765f; // Rn
    uffVDWRadii[87] = 4.900f; // Fr
    uffVDWRadii[88] = 3.677f; // Ra
    uffVDWRadii[89] = 3.478f; // Ac
    uffVDWRadii[90] = 3.396f; // Th
    uffVDWRadii[91] = 3.424f; // Pa
    uffVDWRadii[92] = 3.395f; // U
    uffVDWRadii[93] = 3.424f; // Np
    uffVDWRadii[94] = 3.424f; // Pu
    uffVDWRadii[95] = 3.381f; // Am
    uffVDWRadii[96] = 3.326f; // Cm
    uffVDWRadii[97] = 3.339f; // Bk
    uffVDWRadii[98] = 3.313f; // Cf
    uffVDWRadii[99] = 3.299f; // Es
    uffVDWRadii[100] = 3.286f; // Fm
    uffVDWRadii[101] = 3.274f; // Md
    uffVDWRadii[102] = 3.248f; // No
    uffVDWRadii[103] = 3.236f; // Lr --- Original table is up to here...
    uffVDWRadii[104] = 2.00f; // Db
    uffVDWRadii[105] = 2.00f; // Jl
    uffVDWRadii[106] = 2.00f; // Rf
    uffVDWRadii[107] = 2.00f; // Bh
    uffVDWRadii[108] = 2.00f; // Hn
    uffVDWRadii[109] = 2.00f; // Mt

    // --- Taken from JACS, 114(1992), 10026. In the case of several values the first was taken
    uffD[0] = 0.0f;
    uffD[1] = 0.044f; // H
    uffD[2] = 0.056f; // He
    uffD[3] = 0.025f; // Li
    uffD[4] = 0.085f; // Be
    uffD[5] = 0.180f; // B
    uffD[6] = 0.105f; // C
    uffD[7] = 0.069f; // N
    uffD[8] = 0.060f; // O
    uffD[9] = 0.050f; // F
    uffD[10] = 0.042f; // Ne
    uffD[11] = 0.030f; // Na
    uffD[12] = 0.111f; // Mg
    uffD[13] = 0.505f; // Al
    uffD[14] = 0.402f; // Si
    uffD[15] = 0.305f; // P
    uffD[16] = 0.274f; // S
    uffD[17] = 0.227f; // Cl
    uffD[18] = 0.185f; // Ar
    uffD[19] = 0.035f; // K
    uffD[20] = 0.238f; // Ca
    uffD[21] = 0.019f; // Sc
    uffD[22] = 0.017f; // Ti
    uffD[23] = 0.016f; // V
    uffD[24] = 0.015f; // Cr
    uffD[25] = 0.013f; // Mn
    uffD[26] = 0.013f; // Fe
    uffD[27] = 0.014f; // Co
    uffD[28] = 0.015f; // Ni
    uffD[29] = 0.005f; // Cu
    uffD[30] = 0.124f; // Zn
    uffD[31] = 0.405f; // Ga
    uffD[32] = 0.379f; // Ge
    uffD[33] = 0.309f; // As
    uffD[34] = 0.291f; // Se
    uffD[35] = 0.251f; // Br
    uffD[36] = 0.220f; // Kr
    uffD[37] = 0.040f; // Rb
    uffD[38] = 0.235f; // Sr
    uffD[39] = 0.072f; // Y
    uffD[40] = 0.069f; // Zr
    uffD[41] = 0.059f; // Nb
    uffD[42] = 0.056f; // Mo
    uffD[43] = 0.048f; // Tc
    uffD[44] = 0.056f; // Ru
    uffD[45] = 0.053f; // Rh
    uffD[46] = 0.048f; // Pd
    uffD[47] = 0.036f; // Ag
    uffD[48] = 0.228f; // Cd
    uffD[49] = 0.599f; // In
    uffD[50] = 0.567f; // Sn
    uffD[51] = 0.449f; // Sb
    uffD[52] = 0.398f; // Te
    uffD[53] = 0.339f; // I
    uffD[54] = 0.332f; // Xe
    uffD[55] = 0.045f; // Cs
    uffD[56] = 0.364f; // Ba
    uffD[57] = 0.017f; // La
    uffD[58] = 0.013f; // Ce
    uffD[59] = 0.010f; // Pr
    uffD[60] = 0.010f; // Nd
    uffD[61] = 0.009f; // Pm
    uffD[62] = 0.008f; // Sm
    uffD[63] = 0.008f; // Eu
    uffD[64] = 0.009f; // Gd
    uffD[65] = 0.007f; // Tb
    uffD[66] = 0.007f; // Dy
    uffD[67] = 0.007f; // Ho
    uffD[68] = 0.007f; // Er
    uffD[69] = 0.006f; // Tm
    uffD[70] = 0.228f; // Yb
    uffD[71] = 0.041f; // Lu
    uffD[72] = 0.072f; // Hf
    uffD[73] = 0.081f; // Ta
    uffD[74] = 0.067f; // W
    uffD[75] = 0.066f; // Re
    uffD[76] = 0.037f; // Os
    uffD[77] = 0.073f; // Ir
    uffD[78] = 0.080f; // Pt
    uffD[79] = 0.039f; // Au
    uffD[80] = 0.385f; // Hg
    uffD[81] = 0.680f; // Tl
    uffD[82] = 0.663f; // Pb
    uffD[83] = 0.518f; // Bi
    uffD[84] = 0.325f; // Po
    uffD[85] = 0.284f; // At
    uffD[86] = 0.248f; // Rn
    uffD[87] = 0.050f; // Fr
    uffD[88] = 0.404f; // Ra
    uffD[89] = 0.033f; // Ac
    uffD[90] = 0.026f; // Th
    uffD[91] = 0.022f; // Pa
    uffD[92] = 0.022f; // U
    uffD[93] = 0.019f; // Np
    uffD[94] = 0.016f; // Pu
    uffD[95] = 0.014f; // Am
    uffD[96] = 0.013f; // Cm
    uffD[97] = 0.013f; // Bk
    uffD[98] = 0.013f; // Cf
    uffD[99] = 0.012f; // Es
    uffD[100] = 0.012f; // Fm
    uffD[101] = 0.011f; // Md
    uffD[102] = 0.011f; // No
    uffD[103] = 0.011f; // Lr --- Original table is up to here...
    uffD[104] = 0.011f; // Db
    uffD[105] = 0.011f; // Jl
    uffD[106] = 0.011f; // Rf
    uffD[107] = 0.011f; // Bh
    uffD[108] = 0.011f; // Hn
    uffD[109] = 0.011f; // Mt

    for (int i = 0; i < numberOfElements; i++) {
      elementSymbolToAtomicNumber.put(elementSymbol[i].toUpperCase(), i);
      elementSymbolToCovalentRadius.put(elementSymbol[i].toUpperCase(), covalentRadii[i]);
    }
  }

  // Methods
  public static int getAtomicNumber(String elem) {
    if (elem.length() == 0) {
      return 0;
    }
    Integer atn = elementSymbolToAtomicNumber.get(elem.toUpperCase());
    if (atn == null) {
      return 0;
    }
    return atn;
    //for (int i = 1; i < elementSymbol.length; i++) { /// !!! To do more elegant
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return i;
    //  }
    //}
    //return 0;
  }

  public static int checkAtomicSymbol(String elem) throws IllegalArgumentException {
    if (elem.length() == 0) {
      throw new IllegalArgumentException("checkAtomicSymbol: atom symbol is empty");
    }
    Integer anumber = elementSymbolToAtomicNumber.get(elem.toUpperCase());
    if (anumber == null) {
      throw new IllegalArgumentException("Unknown atom symbol: " + elem);
    }
    return anumber;
    //for (int i = 1; i < elementSymbol.length; i++) {
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return i;
    //  }
    //}
    //throw new IllegalArgumentException("Unknown atom symbol: " + elem);
  }

  /**
   *
   * @param elem String
   * @return float
   */
  public static float getAtomicWeight(String elem) {
    if (elem.length() == 0) {
      return 0;
    }
    Integer anumber = elementSymbolToAtomicNumber.get(elem.toUpperCase());
    if (anumber == null) {
      return 0.0f;
    }
    //for (int i = 1; i < elementSymbol.length; i++) { /// !!! To do more elegant
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return atomicWeight[i];
    //  }
    //}
    return atomicWeight[anumber];
  }

  /**
   *
   * @param element int
   * @return float
   */
  public static float getAtomicWeight(int element) {
    if (element < 1 || element >= elementSymbol.length) {
      return 0;
    } else {
      return atomicWeight[element];
    }
  }

  public static String getElementSymbol(int element) {
    if (element < 1 || element >= elementSymbol.length) {
      return elementSymbol[0];
    } else {
      return elementSymbol[element];
    }
  }

  public static String getElementName(int element) {
    if (element < 1 || element >= elementName.length) {
      return elementName[0];
    } else {
      return elementName[element];
    }
  }

  public static float getCovalentRadius(int elem) {
    if (elem < 0 || elem > covalentRadii.length - 1) {
      return 0.0f;
    }
    return covalentRadii[elem];
  }

  /**
   * Returns van-der-Waals radius of element (from Bondi, J.Phys.Chem., 68, 441, 1964)
   *
   * @param elem int
   * @return float
   */
  public static float getVanDerWaalsRadius(int elem) {
    if (elem < 0 || elem > vanderwaalsRadii.length - 1) {
      return 0.0f;
    }
    return vanderwaalsRadii[elem];
  }

  public static float getCovalentRadius(String elem) {
    if (elem.length() == 0) {
      return 0.0f;
    }
    elem = elem.toUpperCase();
    Float radius = elementSymbolToCovalentRadius.get(elem);
    if (radius == null) {
      return 0.0f;
    }
    //for (int i = 1; i < elementSymbol.length; i++) { /// !!! To do more elegant
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return covalentRadii[i];
    // }
    //}
    return radius;
  }

  /**
   * Returns van-der-Waals radius of element (from Bondi, J.Phys.Chem., 68, 441, 1964)
   *
   * @param elem String
   * @return float
   */
  public static float getVanDerWaalsRadius(String elem) {
    if (elem.length() == 0) {
      return 0.0f;
    }
    Integer anumber = elementSymbolToAtomicNumber.get(elem.toUpperCase());
    if (anumber == null) {
      return 0.0f;
    }
    //for (int i = 1; i < elementSymbol.length; i++) { /// !!! To do more elegant
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return vanderwaalsRadii[i];
    //  }
    //}
    return vanderwaalsRadii[anumber];
  }

  public static float getVDWWellDepth(String elem) {
    if (elem.length() == 0) {
      return 0.0f;
    }
    Integer anumber = elementSymbolToAtomicNumber.get(elem.toUpperCase());
    if (anumber == null) {
      return 0.0f;
    }
    //for (int i = 1; i < elementSymbol.length; i++) { /// !!! To do more elegant
    //  if (elem.compareToIgnoreCase(elementSymbol[i]) == 0) {
    //    return uffD[i];
    //  }
    //}
    return uffD[anumber];
  }

  public static float getUFFWellDepth(int elem) {
    if (elem < 0 || elem > uffD.length - 1) {
      return 0.0f;
    }
    return uffD[elem];
  }

  public static float getUFFRadius(int elem) {
    if (elem < 0 || elem > uffVDWRadii.length - 1) {
      return 0.0f;
    }
    return uffVDWRadii[elem];
  }

  public static String[] getAllElements() {
    return elementSymbol;
  }

  public static int getNumberOfElements() {
    return numberOfElements;
  }

  public static float guessCovalentBondLength(int elem1, int elem2) {
    if (elem1 < 1 || elem2 < 1) {
      return 0;
    }
    return (float) Math.sqrt(getCovalentRadius(elem1) * getCovalentRadius(elem2));
  }

  public static void main(String args[]) {
    for (int i = 0; i < elementSymbol.length; i++) {
      System.out.println("('" + elementName[i] + "','" + elementSymbol[i] + "'," + (i) + "," + atomicWeight[i] + "),");
    }

  }
}
