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
package cct.gamess;

import cct.Constants;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.math.PointGroup;
import cct.modelling.CCTAtomTypes;
import cct.modelling.ChemicalElements;
import cct.modelling.GeneralMolecularDataParser;
import cct.modelling.Molecule;
import cct.vecmath.Geometry3d;
import java.util.logging.Level;

/**
 * <p>
 * Title: </p>
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
public class Gamess extends GeneralMolecularDataParser {

  static final double ONE_BOHR = Constants.ONE_BOHR; // In Angstrom
  static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0);
  static final int LOOKING_FOR_SECTION = 0;
  static final int LOOKING_FOR_SWITCH = 1;
  static final int LOOKING_FOR_SWITCH_VALUE = 2;
  static final int READING_COMMENT = 3;
  static final String endSection = "$END";
  static final String commentSection = "!";
  static final int UNIQUE = 0;
  static final int HINT = 1;
  static final int CART = 2;
  static final int ZMT = 3;
  static final int ZMTMPC = 4;
  static final String controlSection = "$CONTRL"; //  chemical control data             INPUTA:START
  static final String systemSection = "$SYSTEM"; //  computer related control data     INPUTA:START
  static final String basisSection = "$BASIS"; //   basis set                         INPUTB:BASISS
  static final String dataSection = "$DATA"; //    molecule, basis set               INPUTB:MOLE
  static final String fmoxyzSection = "$FMOXYZ"; //  atomic coordinates for FMO FMOIO :FMOXYZ  
  static final String zmatSection = "$ZMAT"; //    coded z-matrix                    ZMATRX:ZMATIN
  static final String libeSection = "$LIBE"; //    linear bend data                  ZMATRX:LIBE
  static final String scfSection = "$SCF"; //     HF-SCF wavefunction control       SCFLIB:SCFIN
  static final String scfmiSection = "$SCFMI"; //   SCF-MI input control data         SCFMI :MIINP
  static final String mp2Section = "$MP2"; //     2nd order Moller-Plesset          MP2   :MP2INP
  static final String guessSection = "$GUESS"; //   initial orbital selection         GUESS :GUESMO
  static final String vecSection = "$VEC"; //     orbitals              (formatted) GUESS :READMO
  // -- Potential energy surface options:
  static final String statptSection = "$STATPT"; //  geometry search control           STATPT:SETSIG
  static final String trudgeSection = "$TRUDGE"; //  nongradient optimization          TRUDGE:TRUINP
  static final String trurstSection = "$TRURST"; //  restart data for TRUDGE           TRUDGE:TRUDGX
  static final String forceSection = "$FORCE"; //   hessian, normal coordinates       HESS  :HESSX
  static final String cphfSection = "$CPHF"; //    coupled-Hartree-Fock options      CPHF  :CPINP
  static final String hessSection = "$HESS"; //    force constant matrix (formatted) HESS  :FCMIN
  static final String gradSection = "$GRAD"; //    gradient vector       (formatted) HESS  :EGIN
  static final String dipdrSection = "$DIPDR"; //   dipole deriv. matrix  (formatted) HESS  :DDMIN
  static final String vibSection = "$VIB"; //     HESSIAN restart data  (formatted) HESS  :HSSNUM
  static final String massSection = "$MASS"; //    isotope selection                 VIBANL:RAMS
  static final String ircSection = "$IRC"; //     intirisic reaction path           RXNCRD:IRCX
  static final String drcSection = "$DRC"; //     dynamic reaction path             DRC   :DRCDRV
  static final String surfSection = "$SURF"; //    potential surface scan            SURF  :SRFINP
  // --- Interpretation, properties:
  static final String localSection = "$LOCAL"; //   orbital localization control      LOCAL :LMOINP
  static final String twoeiSection = "$TWOEI"; //   J,K integrals         (formatted) LOCCD :TWEIIN
  static final String elmonSection = "$ELMOM"; //   electrostatic moments             PRPLIB:INPELM
  static final String elpotSection = "$ELPOT"; //   electrostatic potential           PRPLIB:INPELP
  static final String eldensSection = "$ELDENS"; //  electron density                  PRPLIB:INPELD
  static final String elfldgSection = "$ELFLDG"; //  electric field/gradient           PRPLIB:INPELF
  static final String pointsSection = "$POINTS"; //  property calculation points       PRPLIB:INPPGS
  static final String gridSection = "$GRID"; //    property calculation mesh         PRPLIB:INPPGS
  static final String pdcSection = "$PDC"; //     MEP fitting mesh                  PRPLIB:INPPDC
  static final String molgrfSection = "$MOLGRF"; //  orbital plots                     PARLEY:PLTMEM
  static final String stoneSection = "$STONE"; //   distributed multipole analysis    PRPPOP:STNRD
  static final String morokmSection = "$MOROKM"; //  Morokuma energy decomposition     MOROKM:MOROIN
  static final String ffcalcSection = "$FFCALC"; //  finite field polarizabilities     FFIELD:FFLDX
  static final String tdhfSection = "$TDHF"; //    time dependent HF NLO properties  TDHF  :TDHFX
  // --- Solvation models:
  static final String efragSection = "$EFRAG"; //   effective fragment potentials     EFINP :EFINP
  static final String fragnameSection = "$FRAGNAME"; // specific named fragment pot.     EFINP :RDSTFR
  static final String fgrrplSection = "$FRGRPL"; //  inter-fragment repulsion          EFINP :RDDFRL
  static final String pcmSection = "$PCM"; //     polarizable continuum model       PCM   :PCMINP
  static final String pcmcavSection = "$PCMCAV"; //  PCM cavity generation             PCM   :MAKCAV
  static final String newcavSection = "$NEWCAV"; //  PCM escaped charge cavity         PCM   :DISREP
  static final String disbsSection = "$DISBS"; //   PCM dispersion basis set          PCMDIS:ENLBS
  static final String disrepSection = "$DISREP"; //  PCM dispersion/repulsion          PCMVCH:MORETS
  static final String scrfSection = "$SCRF"; //    self consistent reaction field    SCRF  :ZRFINP
  // --- Integral and integral modification options:
  static final String ecpSection = "$ECP"; // effective core potentials ECPLIB : ECPPAR
  static final String efieldSection = "$EFIELD"; // external electric field PRPLIB : INPEF
  static final String intgrlSection = "$INTGRL"; // format for 2e-integrals INT2A : INTIN
  static final String transSection = "$TRANS"; // integral transformation TRANS : TRFIN
  // --- MCSCF and CI wavefunctions, and their properties :
  static final String ciinpSection = "$CIINP"; // control of CI process GAMESS : WFNCI
  static final String detSection = "$DET"; // determinantal full CI for MCSCF ALDECI : DETINP
  static final String cidetSection = "$CIDET"; // determinantal full CI ALDECI : DETINP
  static final String drtSection = "$DRT"; // distinct row table for MCSCF GUGDRT : ORDORB
  static final String cidrtSection = "$CIDRT"; // distinct row table for CI GUGDRT : ORDORB
  static final String mcscfSection = "$MCSCF"; // parameters for MCSCF MCSCF : MCSCF
  static final String mcqdptSection = "$MCQDPT"; // multireference pert.theory MCQDPT : MQREAD
  static final String cisortSection = "$CISORT"; // integral sorting GUGSRT : GUGSRT
  static final String gugemSection = "$GUGEM"; // Hamiltonian matrix formation GUGEM : GUGAEM
  static final String gugdiaSection = "$GUGDIA"; // Hamiltonian eigenvalues / vectors GUGDGA : GUGADG
  static final String gugdmSection = "$GUGDM"; // 1e-density matrix GUGDM : GUGADM
  static final String gugdm2Section = "$GUGDM2"; // 2e-density matrix GUGDM2 : GUG2DM
  static final String lagranSection = "$LAGRAN"; // CI lagrangian matrix LAGRAN : CILGRN
  static final String trfdm2Section = "$TRFDM2"; // 2e-density backtransformation TRFDM2 : TRF2DM
  static final String transtSection = "$TRANST"; // transition moments, spin - orbit TRNSTN : TRNSTX
  static final Set validSections = new HashSet();
  static Map<String, Map> sectionVarsReference = new HashMap<String, Map>();
  static Map<String, GamessSwitch> controlSwitches = null;
  static Map<String, GamessSwitch> basisSwitches = new HashMap<String, GamessSwitch>();
  static Map<String, GamessSwitch> systemSwitches = new HashMap<String, GamessSwitch>();
  static final Set validSymmetrySymbols = new HashSet();
  static final Logger logger = Logger.getLogger(Gamess.class.getCanonicalName());

  static {
    validSections.add(controlSection);
    validSections.add(systemSection);
    validSections.add(basisSection);
    validSections.add(dataSection);
    validSections.add(zmatSection);
    validSections.add(libeSection);
    validSections.add(scfSection);
    validSections.add(scfmiSection);
    validSections.add(mp2Section);
    validSections.add(guessSection);
    validSections.add(vecSection);

    validSections.add(statptSection);
    validSections.add(trudgeSection);
    validSections.add(trurstSection);
    validSections.add(forceSection);
    validSections.add(cphfSection);
    validSections.add(hessSection);
    validSections.add(gradSection);
    validSections.add(dipdrSection);
    validSections.add(vibSection);
    validSections.add(massSection);
    validSections.add(ircSection);
    validSections.add(drcSection);
    validSections.add(surfSection);

    validSections.add(localSection);
    validSections.add(twoeiSection);
    validSections.add(elmonSection);
    validSections.add(elpotSection);
    validSections.add(eldensSection);
    validSections.add(elfldgSection);
    validSections.add(pointsSection);
    validSections.add(gridSection);
    validSections.add(pdcSection);
    validSections.add(molgrfSection);
    validSections.add(stoneSection);
    validSections.add(morokmSection);
    validSections.add(ffcalcSection);
    validSections.add(tdhfSection);

    // --- Solvation models:
    validSections.add(efragSection);
    validSections.add(fragnameSection);
    validSections.add(fgrrplSection);
    validSections.add(pcmSection);
    validSections.add(pcmcavSection);
    validSections.add(newcavSection);
    validSections.add(disbsSection);
    validSections.add(disrepSection);
    validSections.add(scrfSection);

    // --- Integral and integral modification options:
    validSections.add(ecpSection);
    validSections.add(efieldSection);
    validSections.add(intgrlSection);
    validSections.add(transSection);

    // --- MCSCF and CI wavefunctions, and their properties :
    validSections.add(ciinpSection);
    validSections.add(detSection);
    validSections.add(cidetSection);
    validSections.add(drtSection);
    validSections.add(cidrtSection);
    validSections.add(mcscfSection);
    validSections.add(mcqdptSection);
    validSections.add(cisortSection);
    validSections.add(gugemSection);
    validSections.add(gugdiaSection);
    validSections.add(gugdmSection);
    validSections.add(gugdm2Section);
    validSections.add(lagranSection);
    validSections.add(trfdm2Section);
    validSections.add(transtSection);

    // --- Setup valid symmetry symbols
    validSymmetrySymbols.add("C1");
    validSymmetrySymbols.add("CS");
    validSymmetrySymbols.add("CI");
    validSymmetrySymbols.add("CN");
    validSymmetrySymbols.add("S2N");
    validSymmetrySymbols.add("CNH");
    validSymmetrySymbols.add("NH");
    validSymmetrySymbols.add("CNV");
    validSymmetrySymbols.add("DN");
    validSymmetrySymbols.add("DNH");
    validSymmetrySymbols.add("DND");
    validSymmetrySymbols.add("T");
    validSymmetrySymbols.add("TH");
    validSymmetrySymbols.add("TD");
    validSymmetrySymbols.add("O");
    validSymmetrySymbols.add("OH");
  }
  String Title = "No Title";
  String SymmetrySymbol = "c1";
  int AxisOrder = 1;
  Map<String, Map> currentVars = new HashMap<String, Map>();
  List<GamessAtom> atoms = new ArrayList<GamessAtom>();
  //HashMap controlGroup = new HashMap();
  Map basisGroup = new HashMap();
  private boolean ignoreDataSection = false;

  public Gamess() {
    initialize();
  }

  public void setLoggerLevel(Level level) {
    logger.setLevel(level);
  }

  private void initialize() {

    // --- Setup vars
    //currentVars.put(controlSection, controlGroup);
    //currentVars.put(basisSection, basisGroup);
    // --- Initialize control switches
    if (controlSwitches != null) {
      return;
    }

    controlSwitches = new HashMap<String, GamessSwitch>();

    GamessSwitch gSwitch = new GamessSwitch("SCFTYP",
        "together with MPLEVL or CITYP specifies the wavefunction.  You may choose from");
    gSwitch.addValue("RHF",
        "Restricted Hartree Fock calculation (default)", true);
    gSwitch.addValue("UHF", "Unrestricted Hartree Fock calculation");
    gSwitch.addValue("ROHF",
        "Restricted open shell Hartree-Fock. (high spin, see GVB for low spin)");
    gSwitch.addValue("GVB",
        "Generalized valence bond wavefunction or OCBSE type ROHF. (needs $SCF input)");
    gSwitch.addValue("MCSCF",
        "Multiconfigurational SCF wavefunction (this requires $DET or $DRT input)");
    gSwitch.addValue("NONE",
        "indicates a single point computation,  rereading a converged SCF function. "
        + "This option requires that you select CITYP=GUGA or ALDET, RUNTYP=ENERGY, "
        + "TRANSITN, or SPINORBT, and GUESS=MOREAD.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("ISPHER", "Spherical Harmonics option");
    gSwitch.setType(new Integer(-1));
    gSwitch.addValue("-1",
        "Use Cartesian basis functions to construct symmetry-adapted linear combination (SALC) "
        + "of basis functions. The SALC space is the linear variation space used. (default)", true);
    gSwitch.addValue("0",
        "Use spherical harmonic functions to create SALC functions, which are then expressed "
        + "in terms of Cartesian functions. The contaminants are not dropped, hence this "
        + "option has EXACTLY the same variational space as ISPHER=-1. The only benefit to "
        + "obtain from this is a population analysis in terms of pure s,p,d,f,g functions.");
    gSwitch.addValue("1",
        "Same as ISPHER=0, but the function space is truncated to eliminate all contaminant "
        + "Cartesian functions [3S(D), 3P(F), 4S(G), and 3D(G)] before constructing the SALC "
        + "functions. The computation corresponds to the use of a spherical harmonic basis.");

    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("MPLEVL",
        "chooses Moller-Plesset perturbation theory level, after the SCF. See $MP2 and $MCQDPT input groups.");
    gSwitch.addValue("0", "skips the MP computation (default)", true);
    gSwitch.addValue("2",
        "performs a second order energy correction.  MP2 is implemented only "
        + "for RHF, UHF, ROHF, and MCSCF wave functions.  Gradients are available "
        + "only for RHF, so for the others you may pick from RUNTYP=ENERGY, TRUDGE, SURFACE, or FFIELD only.");
    gSwitch.setType(new Integer(0));
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("CITYP",
        "chooses CI computation after the SCF, for any SCFTYP except UHF.");
    gSwitch.addValue("NONE", "skips the CI. (default)", true);
    gSwitch.addValue("GUGA",
        "runs the Unitary Group CI package, which requires $CIDRT input. Gradients are available only for RHF, "
        + "so for other SCFTYPs, you may choose only RUNTYP=ENERGY, TRUDGE, SURFACE, FFIELD, TRANSITN, or SPINORBT.");
    gSwitch.addValue("ALDET",
        "runs the Ames Laboratory determinant full CI package, requiring $CIDET input.  RUNTYP=ENERGY only.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("RUNTYP",
        "specifies the type of computation\n"
        + "* * * * * * * * * * * * * * * * * * * * * * * * *\n"
        + "Note that RUNTYPs involving the energy gradient,\n"
        + "which are GRADIENT, HESSIAN, OPTIMIZE, SADPOINT, \n"
        + "IRC, GRADEXTR, and DRC, cannot be used for any\n"
        + "CI or MP2 computation, except when SCFTYP=RHF.\n"
        + "* * * * * * * * * * * * * * * * * * * * * * * * *");
    gSwitch.addValue("ENERGY", "Molecular energy. (default)", true);
    gSwitch.addValue("GRADIENT", "Molecular energy plus gradient.");
    gSwitch.addValue("HESSIAN",
        "Molecular energy plus gradient plus second derivatives, including harmonic "
        + "harmonic vibrational analysis.  See the $FORCE and $CPHF input groups.");
    gSwitch.addValue("OPTIMIZE",
        "Optimize the molecular geometry using analytic energy gradients. See $STATPT.");
    gSwitch.addValue("TRUDGE",
        "Non-gradient total energy minimization. See groups $TRUDGE and $TRURST.");
    gSwitch.addValue("SADPOINT",
        "Locate saddle point (transition state). See the $STATPT group.");
    gSwitch.addValue("IRC",
        "Follow intrinsic reaction coordinate. See the $IRC group.");
    gSwitch.addValue("GRADEXTR",
        "Trace gradient extremal. See the $GRADEX group.");
    gSwitch.addValue("DRC",
        "Follow dynamic reaction coordinate. See the $DRC group.");
    gSwitch.addValue("SURFACE",
        "Scan linear cross sections of the potential energy surface.  See $SURF.");
    gSwitch.addValue("PROP", "Properties will be calculated.  A $DATA deck and converged $VEC group should be "
        + "input.  Optionally, orbital localization can be done.  See $ELPOT, etc.");
    gSwitch.addValue("MOROKUMA",
        "Performs monomer energy decomposition. See the $MOROKM group.");
    gSwitch.addValue("TRANSITN",
        "Compute radiative transition moment. See the $TRANST group.");
    gSwitch.addValue("SPINORBT",
        "Compute spin-orbit coupling. See the $TRANST group.");
    gSwitch.addValue("FFIELD",
        "applies finite electric fields, most commonly to extract polarizabilities. See the $FFCALC group.");
    gSwitch.addValue("TDHF",
        "analytic computation of time dependent polarizabilities.  See the $TDHF group.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("EXETYP", "");
    gSwitch.addValue("RUN", "Actually do the run. (default)", true);
    gSwitch.addValue("CHECK",
        "Wavefunction and energy will not be evaluated.  This lets you speedily check input and memory requirements. "
        + "See the overview section for details.");
    gSwitch.addValue("DEBUG",
        "Massive amounts of output are printed, useful only if you hate trees.");
    gSwitch.addValue("routine",
        "Maximum output is generated by the routine named.  Check the source for the routines this applies to."); // !!!
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("MAXIT",
        "Maximum number of SCF iteration cycles. Pertains only to RHF, UHF, ROHF, or "
        + "GVB runs.  See also MAXIT in $MCSCF. (default = 30)", true);
    gSwitch.addValue("30", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("ICHARG",
        "Molecular charge.  (default=0, neutral)", true);
    gSwitch.addValue("0", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("MULT",
        "Multiplicity of the electronic state. = 1 singlet (default)", true);
    gSwitch.addValue("1", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("ECP", "effective core potential control.");
    gSwitch.addValue("NONE", "all electron calculation (default).", true);
    gSwitch.addValue("READ", "read the potentials in $ECP group.");
    gSwitch.addValue("SBKJC", "use Stevens, Basch, Krauss, Jasien, Cundari potentials for all heavy atoms (Li-Rn are available).");
    gSwitch.addValue("HW",
        "use Hay, Wadt potentials for all the heavy atoms (Na-Xe are available).");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("COORD",
        "choice for molecular geometry in $DATA.\n\n"
        + "Note that the CART, ZMT, ZMTMPC choices require input of "
        + "all atoms in the molecule.  These three also orient the "
        + "molecule, and then determine which atoms are unique.  The "
        + "reorientation is very likely to change the order of the "
        + "atoms from what you input.  When the point group contains "
        + "a 3-fold or higher rotation axis, the degenerate moments "
        + "of inertia often cause problems choosing correct symmetry "
        + " unique axes, in which case you must use COORD=UNIQUE rather than Z-matrices.\n\n"
        + "Note that the CART, ZMT, ZMTMPC choices require input of "
        + "all atoms in the molecule.  These three also orient the "
        + "molecule, and then determine which atoms are unique.  The "
        + "reorientation is very likely to change the order of the "
        + "atoms from what you input.  When the point group contains "
        + "a 3-fold or higher rotation axis, the degenerate moments "
        + "of inertia often cause problems choosing correct symmetry "
        + "unique axes, in which case you must use COORD=UNIQUE "
        + "rather than Z-matrices.\n\n"
        + "Warning:  The reorientation into principal axes is done "
        + "only for atomic coordinates, and is not applied to the "
        + "axis dependent data in the following groups: $VEC, $HESS, "
        + "$GRAD, $DIPDR, $VIB, nor Cartesian coords of effective "
        + "fragments in $EFRAG.  COORD=UNIQUE avoids reorientation, "
        + "and thus is the safest way to read these.\n\n "
        + "Note that the choices CART, ZMT, ZMTMPC require the use "
        + "of a $BASIS group to define the basis set.  The first "
        + "two choices might or might not use $BASIS, as you wish.");

    gSwitch.addValue("UNIQUE",
        "only the symmetry unique atoms will be given, in Cartesian coords (default).", true);
    gSwitch.addValue("HINT",
        "only the symmetry unique atoms will be given, in Hilderbrandt style internals.");
    gSwitch.addValue("CART", "Cartesian coordinates will be input.");
    gSwitch.addValue("ZMT", "GAUSSIAN style internals will be input.");
    gSwitch.addValue("ZMTMPC", "MOPAC style internals will be input.");
    gSwitch.addValue("FRAGONLY",
        "means no part of the system is treated by ab initio means, hence $DATA is not "
        + "given.  The system is specified by $EFRAG.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("UNITS",
        "distance units, any angles must be in degrees.");
    gSwitch.addValue("ANGS", "Angstroms (default)", true);
    gSwitch.addValue("BOHR", "Bohr atomic units");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NZVAR",
        "NZVAR refers mainly to the coordinates used by OPTIMIZE "
        + "or SADPOINT runs, but may also print the internal's values for other run types.  You can use internals to "
        + "define the molecule, but Cartesians during optimizations! \n\n"
        + "= 0  Use Cartesian coordinates (default).\n"
        + "= M  If COORD=ZMT or ZMTMPC and a $ZMAT is not given: the internal coordinates will be those defining "
        + "the molecule in $DATA.  In this case, $DATA must not contain any dummy atoms.  M is usually 3N-6, or 3N-5 for linear.\n\n"
        + "= M  For other COORD choices, or if $ZMAT is given: the internal coordinates will be those defined "
        + "in $ZMAT.  This allows more sophisticated internal coordinate choices.  M is ordinarily 3N-6 (3N-5), unless $ZMAT has linear bends.", true);
    gSwitch.addValue("0", "Use Cartesian coordinates(default)", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("LOCAL", "controls orbital localization.");
    gSwitch.addValue("NONE", "Skip localization (default).", true);
    gSwitch.addValue("BOYS", "Do Foster-Boys localization.");
    gSwitch.addValue("RUEDNBRG", "Do Edmiston-Ruedenberg localization.");
    gSwitch.addValue("POP",
        "Do Pipek-Mezey population localization. See the $LOCAL group.   Localization "
        + "does not work for SCFTYP=GVB or CITYP.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("MOLPLT",
        "flag that produces an input deck for a molecule "
        + "drawing program distributed with GAMESS. (default is .FALSE.)", true);
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue("false", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("PLTORB",
        "flag that produces an input deck for an orbital "
        + "plotting program distributed with GAMESS. (default is .FALSE.)", true);
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue("false", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("AIMPAC",
        "flag to create an input deck for Bader's atoms "
        + "in molecules properties code. (default=.FALSE.)", true);
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue("false", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("RPAC",
        "flag to create the input files for Bouman and "
        + "Hansen 'sRPAC electronic excitation and NMR shieldings program.RPAC works only with "
        + "RHF wavefunctions. (inactive)", true);
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue("false", "", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("FRIEND",
        "string to prepare input to other quantum programs");
    gSwitch.addValue("HONDO", " for HONDO 8.2");
    gSwitch.addValue("MELDF", " for MELDF");
    gSwitch.addValue("GAMESSUK", " for GAMESS(UK Daresbury version)");
    gSwitch.addValue("GAUSSIAN", " for Gaussian 9x");
    gSwitch.addValue("ALL", " for all of the above");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NPRINT",
        "Print / punch control flag. See also EXETYP for debug info. "
        + "(options - 7to 5are primarily debug)");
    gSwitch.addValue("-7", "Extra printing from Boys localization.");
    gSwitch.addValue("-6", "debug for geometry searches");
    gSwitch.addValue("-5", "minimal output");
    gSwitch.addValue("-4", "print 2e-contribution to gradient.");
    gSwitch.addValue("-3", "print 1e-contribution to gradient.");
    gSwitch.addValue("-2", "normal printing, no punch file");
    gSwitch.addValue("1", "extra printing for basis, symmetry, ZMAT");
    gSwitch.addValue("2", "extra printing for MO guess routines");
    gSwitch.addValue("3", "print out property and 1e-integrals");
    gSwitch.addValue("4", "print out 2e-integrals");
    gSwitch.addValue("5",
        "print out SCF data for each cycle. (Fock and density matrices, current MOs");
    gSwitch.addValue("6",
        "same as 7, but wider 132columns output. This option isn't perfect.");
    gSwitch.addValue("7", "normal printing and punching(default)", true);
    gSwitch.addValue("8",
        "more printout than 7.The extra output is(AO) Mulliken and overlap population analysis, eigenvalues, Lagrangians, ...");
    gSwitch.addValue("9",
        "everything in 8plus Lowdin population analysis, final density matrix.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NOSYM", "");
    gSwitch.addValue("0", "the symmetry specified in $DATA is used as much as possible in integrals, SCF, "
        + "gradients, etc. (this is the default)", true);
    gSwitch.addValue("1",
        "the symmetry specified in the $DATA group is used to build the molecule, then "
        + "symmetry is not used again.Some GVB or MCSCF runs(those without a totally "
        + "symmetric charge density) require you request no symmetry.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("INTTYP", "");
    gSwitch.addValue("POPLE", "use fast Pople - Hehre routines for sp integral blocks, and HONDO Rys polynomial code for "
        + "all other integrals. (default)", true);
    gSwitch.addValue("HONDO", " use HONDO / Rys integrals for all integrals. This option produces slightly more accurate "
        + "integrals but is also slower.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NORMF", "");
    gSwitch.addValue("0", "normalize the basis functions(default)", true);
    gSwitch.addValue("1", "no normalization");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NORMP", "");
    gSwitch.addValue("0", "input contraction coefficients refer to normalized Gaussian primitives. (default)", true);
    gSwitch.addValue("1", "the opposite.");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("ITOL",
        "primitive cutoff factor(default = 20)\n"
        + "= n products of primitives whose exponential factor is less than 10**( -n) are skipped.", true);
    gSwitch.addValue("20", "", true);
    gSwitch.setType(new Integer(20));
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("ICUT",
        " = n integrals less than 10.0**(-n) are not  saved on disk. (default = 9)", true);
    gSwitch.addValue("9", "", true);
    gSwitch.setType(new Integer(9));
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("IREST",
        "restart control options (for OPTIMIZE run restarts, see $STATPT)\n"
        + "Note that this option is unreliable!");
    gSwitch.addValue("-1", "reuse dictionary file from previous run,  useful with GEOM = DAF and / or GUESS = MOSAVED. "
        + "Otherwise, this option is the same as 0.");
    gSwitch.addValue("0", "normal run(default)", true);
    gSwitch.addValue("1", "2e restart(1 - e integrals and MOs saved)");
    gSwitch.addValue("2",
        "SCF restart(1 - , 2 - e integrls and MOs saved)");
    gSwitch.addValue("3", "1e gradient restart ");
    gSwitch.addValue("4", "2e gradient restart");
    controlSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("GEOM",
        "select where to obtain molecular geometry");
    gSwitch.addValue("INPUT", " from $DATA input(default for IREST = 0)"); /// !!!
    gSwitch.addValue("DAF", "read from DICTNRY file(default otherwise)", true);
    controlSwitches.put(gSwitch.getName(), gSwitch);

    // --- Setting up basis swithces
    gSwitch = new GamessSwitch("GBASIS",
        "GBASIS requests various Gaussian basis sets.");
    gSwitch.addValue("MINI",
        "Huzinaga's 3 gaussian minimal basis set.\nAvailable H-Rn.");
    gSwitch.addValue("MIDI",
        "Huzinaga's 21 split valence basis set.\nAvailable H-Rn.");
    gSwitch.addValue("STO",
        "Pople's STO-NG minimal basis set.\nAvailable H-Xe, for NGAUSS=2,3,4,5,6.");
    gSwitch.addValue("N21",
        "Pople's N-21G split valence basis set.\nAvailable H-Xe, for NGAUSS=3.\nAvailable H-Ar, for NGAUSS=6.");
    gSwitch.addValue("N31",
        "Pople's N-31G split valence basis set.\nAvailable H-Ne,P-Cl for NGAUSS=4.\n"
        + "Available H-He,C-F for NGAUSS=5.\nAvailable H-Kr, for NGAUSS=6, note that the bases for K,Ca,Ga-Kr were changed 9/2006.");
    gSwitch.addValue("N311",
        "Pople's \"triple split\" N-311G basis set.\nAvailable H-Ne, for NGAUSS=6.\nSelecting N311 implies MC for Na-Ar.");
    gSwitch.addValue("DZV",
        "\"double zeta valence\" basis set. a synonym for DH for H,Li,Be-Ne,Al-Cl.\n"
        + "(14s,9p,3d)/[5s,3p,1d] for K-Ca.\n(14s,11p,5d/[6s,4p,1d] for Ga-Kr.");
    gSwitch.addValue("DH", "Dunning/Hay \"double zeta\" basis set.\n"
        + "(3s)/[2s] for H.\n"
        + "(9s,4p)/[3s,2p] for Li.\n"
        + "(9s,5p)/[3s,2p] for Be-Ne.\n"
        + "(11s,7p)/[6s,4p] for Al-Cl.");
    gSwitch.addValue("TZV", "\"triple zeta valence\" basis set.\n"
        + "(5s)/[3s] for H.\n"
        + "(10s,3p)/[4s,3p] for Li.\n"
        + "(10s,6p)/[5s,3p] for Be-Ne.\n"
        + "a synonym for MC for Na-Ar.\n"
        + "(14s,9p)/[8s,4p] for K-Ca."
        + "(14s,11p,6d)/[10s,8p,3d] for Sc-Zn.");

    gSwitch.addValue("MC", "McLean/Chandler \"triple split\" basis.\n"
        + "(12s,9p)/[6s,5p] for Na-Ar.\n"
        + "Selecting MC implies 6-311G for H-Ne.");
    gSwitch.addValue("CCD",
        "Dunning-type Correlation Consistent basis sets, officially called cc-pVnZ."
        + "Available for H-He, Li-Ne, Na-Ar, Ca, Ga-Kr");
    gSwitch.addValue("CCT",
        "Dunning-type Correlation Consistent basis sets, officially called cc-pVnZ."
        + "Available for H-He, Li-Ne, Na-Ar, Ca, Ga-Kr");
    gSwitch.addValue("CCQ",
        "Dunning-type Correlation Consistent basis sets, officially called cc-pVnZ."
        + "Available for H-He, Li-Ne, Na-Ar, Ca, Ga-Kr");
    gSwitch.addValue("CC5",
        "Dunning-type Correlation Consistent basis sets, officially called cc-pVnZ."
        + "Available for H-He, Li-Ne, Na-Ar, Ca, Ga-Kr");
    gSwitch.addValue("CC6",
        "Dunning-type Correlation Consistent basis sets, officially called cc-pVnZ."
        + "Available for H-He, Li-Ne, Na-Ar, Ca, Ga-Kr");
    //Use n = D,T,Q,5,6 to indicate the level of
    //polarization. These provide a hierachy of
    //basis sets suitable for recovering the
    //correlation energy.
    //Available for H - He, Li - Ne, Na - Ar, Ca, Ga - Kr

    gSwitch.addValue("ACCD",
        "As CCD, but augmented with a set of diffuse functions, e.g.aug - cc - pVDZ.");
    gSwitch.addValue("ACCT",
        "As CCT, but augmented with a set of diffuse functions, e.g.aug - cc - pVTZ.");
    gSwitch.addValue("ACCQ",
        "As CCQ, but augmented with a set of diffuse functions, e.g.aug - cc - pVQZ.");
    gSwitch.addValue("ACC5",
        "As CC5, but augmented with a set of diffuse functions, e.g.aug - cc - pV5Z.");
    gSwitch.addValue("ACC6",
        "As CC6, but augmented with a set of diffuse functions, e.g.aug - cc - pV6Z.");

    gSwitch.addValue("CCDC", "As CCD, but augmented with tight functions for recovering core and core - valence "
        + "correlation, e.g.cc - pCVDZ");
    gSwitch.addValue("CCTC", "As CCT, but augmented with tight functions for recovering core and core - valence "
        + "correlation, e.g.cc - pCVTZ");
    gSwitch.addValue("CCQC", "As CCQ, but augmented with tight functions for recovering core and core - valence "
        + "correlation, e.g.cc - pCVQZ");
    gSwitch.addValue("CC5C", "As CC5, but augmented with tight functions for recovering core and core - valence "
        + "correlation, e.g.cc - pCV5Z");
    gSwitch.addValue("CC6C", "As CC6, but augmented with tight functions for recovering core and core - valence "
        + "correlation, e.g.cc - pCV6Z");

    gSwitch.addValue("ACCTC", "As CCT, but augmented with both tight and diffuse functions, e.g.aug - cc - pCVTZ.");
    gSwitch.addValue("ACCDC", "As CCD, but augmented with both tight and diffuse functions, e.g.aug - cc - pCVDZ.");
    gSwitch.addValue("ACCQC", "As CCQ, but augmented with both tight and diffuse functions, e.g.aug - cc - pCVQZ.");
    gSwitch.addValue("ACC5C", "As CC5, but augmented with both tight and diffuse functions, e.g.aug - cc - pCV5Z.");
    gSwitch.addValue("ACC6C", "As CC6, but augmented with both tight and diffuse functions, e.g.aug - cc - pCV6Z.");

    gSwitch.addValue("PC0", "Jensen Polarization Consistent basis sets.\n"
        + "n = 0, 1, 2, 3, 4 indicates the level of polarization. (n = 0 is unpolarized, n = 1 is"
        + "DZP, n = 2 is TZP, etc.)\n"
        + "Available for H, C, N, O, F, Si, P, S, Cl");
    gSwitch.addValue("PC1", "Jensen Polarization Consistent basis sets.\n"
        + "n = 0, 1, 2, 3, 4 indicates the level of polarization. (n = 0 is unpolarized, n = 1 is"
        + "DZP, n = 2 is TZP, etc.)\n"
        + "Available for H, C, N, O, F, Si, P, S, Cl");
    gSwitch.addValue("PC2", "Jensen Polarization Consistent basis sets.\n"
        + "n = 0, 1, 2, 3, 4 indicates the level of polarization. (n = 0 is unpolarized, n = 1 is"
        + "DZP, n = 2 is TZP, etc.)\n"
        + "Available for H, C, N, O, F, Si, P, S, Cl");
    gSwitch.addValue("PC3", "Jensen Polarization Consistent basis sets.\n"
        + "n = 0, 1, 2, 3, 4 indicates the level of polarization. (n = 0 is unpolarized, n = 1 is"
        + "DZP, n = 2 is TZP, etc.)\n"
        + "Available for H, C, N, O, F, Si, P, S, Cl");
    gSwitch.addValue("PC4", "Jensen Polarization Consistent basis sets.\n"
        + "n = 0, 1, 2, 3, 4 indicates the level of polarization. (n = 0 is unpolarized, n = 1 is"
        + "DZP, n = 2 is TZP, etc.)\n"
        + "Available for H, C, N, O, F, Si, P, S, Cl");

    gSwitch.addValue("APC0",
        "As PC0, but augmented with a set of diffuse functions.");
    gSwitch.addValue("APC1",
        "As PC1, but augmented with a set of diffuse functions.");
    gSwitch.addValue("APC2",
        "As PC2, but augmented with a set of diffuse functions.");
    gSwitch.addValue("APC3",
        "As PC3, but augmented with a set of diffuse functions.");
    gSwitch.addValue("APC4",
        "As PC4, but augmented with a set of diffuse functions.");

    gSwitch.addValue("SBKJC",
        "Stevens/Basch/Krauss/Jasien/Cundari valence basis set, for Li-Rn. This choice "
        + "implies an unscaled -31G basis for H-He.");
    gSwitch.addValue("HW", "Hay/Wadt valence basis.\n"
        + "This is a -21 split, available Na-Xe, except for the transition metals.\n"
        + "This implies a 3-21G basis for H-Ne.");

    gSwitch.addValue("MCP-DZP",
        "double zeta quality valence basis set, which are akin to the "
        + "correlation consistent sets, in that these include increasing levels of polarization (and so do not "
        + "require \"supplements\" like NDFUNC or DIFFSP) and must be used as spherical harmonics (see ISPHER).\n"
        + "These are available for main group atoms, Li-Rn.");
    gSwitch.addValue("MCP-TZP",
        "triple zeta quality valence basis set, which are akin to the "
        + "correlation consistent sets, in that these include increasing levels of polarization (and so do not "
        + "require \"supplements\" like NDFUNC or DIFFSP) and must be used as spherical harmonics (see ISPHER).\n"
        + "These are available for main group atoms, Li-Rn.");
    gSwitch.addValue("MCP-QZP",
        "quadruple zeta quality valence basis set, which are akin to the "
        + "correlation consistent sets, in that these include increasing levels of polarization (and so do not "
        + "require \"supplements\" like NDFUNC or DIFFSP) and must be used as spherical harmonics (see ISPHER).\n"
        + "These are available for main group atoms, Li-Rn.");
    gSwitch.addValue("IMCP-SR1", "valence basis set to be used with the improved MCPs with scalar relativistic effects.\n"
        + "These are available for transition metals except La, and the main group elements B-Ne, P-Ar, Ge, Kr, Sb, Xe, Rn.\n"
        + "The 1 refer to addition of first polarization shell, so again don't use any of the \"supplements\" and do use spherical harmonics.");
    gSwitch.addValue("IMCP-SR2", "valence basis set to be used with the improved MCPs with scalar relativistic effects.\n"
        + "These are available for transition metals except La, and the main group elements B-Ne, P-Ar, Ge, Kr, Sb, Xe, Rn.\n"
        + "The 2 refer to addition of second polarization shell, so again don't use any of the \"supplements\" and do use spherical harmonics.");
    gSwitch.addValue("IMCP-NR1",
        "valence basis set, but with nonrelativistic model core potentials.");
    gSwitch.addValue("IMCP-NR2",
        "valence basis set, but with nonrelativistic model core potentials.");
    gSwitch.addValue("MNDO", "selects MNDO model hamiltonian");
    gSwitch.addValue("AM1", "selects AM1 model hamiltonian");
    gSwitch.addValue("PM3", "selects PM3 model hamiltonian");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NGAUSS",
        "the number of Gaussians (N). This parameter pertains only to GBASIS=STO, N21, N31, or N311.");
    gSwitch.setType(new Integer(0));

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NDFUNC",
        "number of heavy atom polarization functions to be used. These are usually d functions, except "
        + "for MINI/MIDI. The term \"heavy\" means Na on up when GBASIS=STO, HW, or N21, and from Li on up "
        + "otherwise. The value may not exceed 3. The variable POLAR selects the actual exponents to "
        + " be used, see also SPLIT2 and SPLIT3. (default=0)");
    gSwitch.setType(new Integer(0));
    gSwitch.addValue("0", "", true);
    gSwitch.addValue("1", "");
    gSwitch.addValue("2", "");
    gSwitch.addValue("3", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NFFUNC", "number of heavy atom f type polarization functions to be used on Li-Cl. This may only "
        + "be input as 0 or 1. (default=0)");
    gSwitch.setType(new Integer(0));
    gSwitch.addValue("0", "", true);
    gSwitch.addValue("1", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NPFUNC", "number of light atom, p type polarization functions to be used on H-He. This may not "
        + "exceed 3, see also POLAR. (default=0)");
    gSwitch.setType(new Integer(0));
    gSwitch.addValue("0", "", true);
    gSwitch.addValue("1", "");
    gSwitch.addValue("2", "");
    gSwitch.addValue("3", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("DIFFSP",
        "flag to add diffuse sp(L) shell to heavy atoms.\n"
        + "Heavy means Li - F, Na - Cl, Ga - Br, In - I, Tl - At.\n"
        + "The default is .FALSE.");
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue(".FALSE.", "", true);
    gSwitch.addValue(".TRUE.", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("DIFFS",
        "flag to add diffuse s shell to hydrogens.\n"
        + "The default is .FALSE.");
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue(".FALSE.", "", true);
    gSwitch.addValue(".TRUE.", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("POLAR", "exponent of polarization functions");
    gSwitch.addValue("COMMON", "(default for GBASIS=STO,N21,HW,SBKJC)");
    gSwitch.addValue("POPN31", "(default for GBASIS=N31)");
    gSwitch.addValue("POPN311", "(default for GBASIS=N311, MC)");
    gSwitch.addValue("DUNNING", "(default for GBASIS=DH, DZV)");
    gSwitch.addValue("HUZINAGA", "(default for GBASIS=MINI, MIDI)");
    gSwitch.addValue("HONDO7", "(default for GBASIS=TZV)");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("SPLIT2",
        "an array of splitting factors used when NDFUNC or NPFUNC is 2. Default=2.0,0.5");
    Float[] split2 = {
      2.0f, 0.5f};
    gSwitch.setType(split2);
    gSwitch.addValue("2.0,0.5", "", true);

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("SPLIT3", "an array of splitting factors used when NDFUNC or NPFUNC is 3. Default=4.00,1.00,0.25");
    Float[] split3 = {
      4.00f, 1.00f, 0.25f};
    gSwitch.setType(split3);
    gSwitch.addValue("4.00,1.00,0.25", "", true);

    basisSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("EXTFIL",
        "a flag to read basis sets from an external file, defined by EXTBAS, rather than from a $DATA group.\n"
        + "(default=.false.)");
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue(".FALSE.", "", true);
    gSwitch.addValue(".TRUE.", "");

    basisSwitches.put(gSwitch.getName(), gSwitch);

    // --- $SYSTEM swithces
    gSwitch = new GamessSwitch("MWORDS",
        "The maximum replicated memory which your job can "
        + "use, on every node. This is given in units of 1,000,000 words (as opposed to 1024*1024 words), "
        + "where a word is defined as 64 bits. (default=1)\n"
        + "(In case finer control over the memory is needed, this value can be given in units of words with "
        + " the old keyword MEMORY instead of MWORDS.)", true);
    gSwitch.setType(new Integer(1));
    gSwitch.addValue("1", "", true);

    gSwitch = new GamessSwitch("MEMORY",
        "The maximum memory in units of words. Obsolete", true);
    gSwitch.setType(new Integer(1));

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("MEMDDI",
        "The grand total memory needed for the distributed data interface (DDI) storage, given in units of "
        + "1,000,000 words.\n"
        + "note: the memory required on each processor for a run using p processors is therefore MEMDDI/p + MWORDS.\n"
        + "The parallel runs that currently require MEMDDI are:\n"
        + "SCFTYP=RHF MPLEVL=2 energy or gradient\n"
        + "SCFTYP=UHF MPLEVL=2 energy or gradient\n"
        + "SCFTYP=ROHF MPLEVL=2 OSPT=ZAPT energy or gradient\n"
        + "SCFTYP=MCSCF MPLEVL=2 energy\n"
        + "SCFTYP=MCSCF using the FULLNR or JACOBI convergers\n"
        + "SCFTYP=MCSCF analytic hessian\n"
        + "SCFTYP=any CITYP=ALDET, GENCI, ORMAS, FSOCI, GUGA\n"
        + "SCFTYP=RHF CCTYP=CCSD or CCSD(T)\n"
        + "All other parallel runs should enter MEMDDI=0, for they use only replicated memory.\n"
        + "Some serial runs execute the parallel code (on just 1 CPU), for there is only a parallel code. These serial runs must "
        + "give MEMDDI as a result:\n"
        + "SCFTYP=ROHF MPLEVL=2 OSPT=ZAPT gradient/property run\n"
        + "SCFTYP=MCSCF analytic hessian", true);
    gSwitch.setType(new Integer(1));
    gSwitch.addValue("1", "", true);

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("TIMLIM",
        "Time limit, in minutes. Set to about 95 percent of the time limit given to the batch job (if you "
        + "use a queueing system) so that GAMESS can stop itself gently. (default=525600.0 minutes)", true);
    gSwitch.setType(new Float(525600.0f));
    gSwitch.addValue("525600", "", true);

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("PARALL",
        "A flag to cause the distributed data parallel MP2 program to execute the parallel algorithm, "
        + "even if you are running on only one node. The main purpose of this is to allow you to "
        + "do EXETYP=CHECK runs to learn what the correct value of MEMDDI needs to be.");
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue(".FALSE.", "", true);

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("KDIAG", "Diagonalization control switch");
    gSwitch.setType(new Integer(0));
    gSwitch.addValue("0",
        "use a vectorized diagonalization routine if one is available on your machine, else use EVVRSP. (default)", true);
    gSwitch.addValue("1",
        "use EVVRSP diagonalization. This may be more accurate than KDIAG=0.");
    gSwitch.addValue("2",
        "use GIVEIS diagonalization (not as fast or reliable as EVVRSP)");
    gSwitch.addValue("3",
        "use JACOBI diagonalization (this is the slowest method)");

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("COREFL", "A flag to indicate whether or not GAMESS should produce a \"core\" file for debugging "
        + "when subroutine ABRT is called to kill a job.This variable pertains only to UNIX operating systems. (default = .FALSE.)");
    gSwitch.setType(new Boolean(false));
    gSwitch.addValue(".FALSE.", "", true);
    gSwitch.addValue(".TRUE.", "");

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("BALTYP", "Parallel load balance scheme", true);
    gSwitch.setType(new Integer(0));

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("BALTYP",
        "Parallel load balance scheme. LOOP uses static load balancing. NXTVAL uses dynamic load balancing(DLB).\n"
        + "(default = NXTVAL)", true);
    gSwitch.setType(new Integer(0));

    systemSwitches.put(gSwitch.getName(), gSwitch);

    gSwitch = new GamessSwitch("NODEXT",
        "Array specifying node extentions in GDDI for each file. "
        + "Non - zero values force no extension.\n"
        + "E.g., NODEXT(40) = 1 forces file 40 (file numbers are unit numbers used in GAMESS, "
        + "see \"rungms\" or PROG.DOC) to have the name of $JOB.F40 on all nodes, rather than $JOB.F40, $JOB.F40 .001, "
        + "$JOB.F40 .002etc.This is convenient for FMO restart jobs, so that the file name need not be "
        + "changed for each node, when copying the restart file. Note that on machines when several CPUs use "
        + "the same directory(e.g., SMP) NODEXT should be zero. (default: all zeros)", true);
    systemSwitches.put(gSwitch.getName(), gSwitch);

    // --- Setup references
    sectionVarsReference.put(controlSection, controlSwitches);
    sectionVarsReference.put(basisSection, basisSwitches);
    sectionVarsReference.put(systemSection, systemSwitches);

  }

  public void getMolecularInterface(MoleculeInterface molec) throws
      Exception {
    if (molec == null) {
      throw new Exception(this.getClass().getCanonicalName()
          + " : molec == null");
    }
    if (atoms.size() < 1) {
      throw new Exception(this.getClass().getCanonicalName()
          + " : no GAMESS atoms");
    }

    molec.addMonomer("GAMESS");

    for (int i = 0; i < atoms.size(); i++) {
      GamessAtom ga = atoms.get(i);
      AtomInterface atom = molec.getNewAtomInstance();
      atom.setName(ga.getName());
      atom.setAtomicNumber(ga.getAtomicNumber());
      atom.setXYZ(ga.getX(), ga.getY(), ga.getZ());
      molec.addAtom(atom);
    }
  }

  /**
   *
   * @param inputfile
   * @throws Exception
   * @deprecated
   *
   */
  @Deprecated
  public void parseGamessInput(String inputfile) throws
      Exception {

    int status = LOOKING_FOR_SECTION;

    try {
      // Create the tokenizer to read from a file
      FileReader rd = new FileReader(inputfile);
      StreamTokenizer st = new StreamTokenizer(rd);

      // Prepare the tokenizer for Java-style tokenizing rules
      //st.parseNumbers();
      st.wordChars('1', '9');
      st.wordChars('0', '0');
      st.wordChars('_', '_');
      st.wordChars('$', '$');
      st.wordChars('.', '.');

      st.eolIsSignificant(true);

      // If whitespace is not to be discarded, make this call
      //st.ordinaryChars(0, ' ');
      st.whitespaceChars('=', '=');
      //st.ordinaryChar('$');

      // These calls caused comments to be discarded
      st.slashSlashComments(true);
      st.slashStarComments(true);

      // Parse the file
      int token = st.nextToken();
      while (token != StreamTokenizer.TT_EOF) {
        switch (token) {

          case StreamTokenizer.TT_EOL:

            // End of line character found
            if (status == READING_COMMENT) {
              status = LOOKING_FOR_SECTION;
            }
            break;

          case StreamTokenizer.TT_NUMBER:

            // A number was found; the value is in nval
            break;
          case StreamTokenizer.TT_WORD:

            if (status == READING_COMMENT) {
              break;
            } else if (status == LOOKING_FOR_SECTION) {
              String word = st.sval.toUpperCase();
              if (!validSections.contains(word)) {
                break;
                //throw new Exception(
                //    "Error parsing GAMESS input file: unknown section: " +
                //    st.sval);
              }
              st.pushBack();
              parseGamessInputSection(st);
            }

            // A word was found; the value is in sval
            break;
          case '"':

            // A double-quoted string was found; sval contains the contents
            break;
          case '\'':

            // A single-quoted string was found; sval contains the contents
            break;

          case StreamTokenizer.TT_EOF:

            // End of file has been reached
            break;
          default:

            // A regular character was found; the value is the token itself
            char ch = (char) st.ttype;
            if (commentSection.startsWith(String.valueOf(ch))) {
              status = READING_COMMENT;
            }

            break;
        }

        token = st.nextToken();

      }
      rd.close();

      logger.info("Number of atoms: " + atoms.size());
    } catch (Exception e) {
      throw new Exception("Error parsing GAMESS input file: " + e.getMessage());
    }
  }

  /**
   *
   * @param inputfile
   * @param fileType
   * @throws Exception
   * @deprecated Use parseData methods instead
   */
  @Deprecated
  public void parseGamessInput(String inputfile, int fileType) throws Exception {

    String line;
    BufferedReader in = null;
    ignoreDataSection = false;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(inputfile));

      } catch (Exception ex) {
        throw ex;
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(inputfile));
    } else {
      throw new Exception("parseGamessInput: INTERNAL ERROR: Unknown file type");
    }
    parseData(in);
  }

  private void printSection(String section, Map controls) {
    logger.info("Section " + section + " variables");
    Set set = controls.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      logger.info(me.getKey().toString() + "="
          + me.getValue().toString());
    }

  }

  private Object parseGamessInputSection(StreamTokenizer st) throws
      Exception {

    st.nextToken();
    String section = st.sval.toUpperCase();

    Map references = sectionVarsReference.get(section);
    Map current = currentVars.get(section);

    if (references != null && current == null
        && sectionVarsReference.containsKey(section)) {
      st.pushBack();
      current = new HashMap();
      parseControlSection(st, references, current);
      currentVars.put(section, current);
      //HashMap controls = getControlSection();
      Map controls = new HashMap(current);
      logger.info("Section " + section + " variables");
      Set set = controls.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        logger.info(me.getKey().toString() + "="
            + me.getValue().toString());
      }

    } else if (section.equalsIgnoreCase(dataSection)) {
      this.parseDataSection(st);
      int cardSequence = getCardSequence();
      if (atoms.size() > 0 && cardSequence == UNIQUE) {
        applySymmetry(this.SymmetrySymbol, this.AxisOrder, atoms);
      }
    } else {
      System.err.println("Warning: skipping section " + section);
      st.pushBack();
      skipSection(st);
    }

    return null;
  }

  private Object parseControlSection(StreamTokenizer st, Map referenceVars, Map sectionVars) throws
      Exception {

    int status = LOOKING_FOR_SWITCH;
    String currentSwitch = "";
    String word = "";

    int token = st.nextToken();
    while (token != StreamTokenizer.TT_EOF) {
      token = st.nextToken();
      switch (token) {
        case StreamTokenizer.TT_NUMBER:

          // A number was found; the value is in nval
          word = String.valueOf(st.nval);
        //double num = st.nval;
        //break;
        case StreamTokenizer.TT_WORD:

          if (token == StreamTokenizer.TT_WORD) {
            word = st.sval.toUpperCase();
          }
          if (word.equalsIgnoreCase(endSection)) {
            if (status == LOOKING_FOR_SWITCH) {
              return null;
            } else {
              System.err.println("unexpected end of a section");
              //throw new Exception(
              //    "Error parsing GAMESS input file: unexpected end of a section..");
              return null;
            }
          }

          if (status == LOOKING_FOR_SWITCH) {
            if (!referenceVars.containsKey(word)) {
              System.err.println("Unknown switch: " + st.sval);
              //throw new Exception(
              //    "Error parsing GAMESS input file: unknown switch in section: " +
              //    st.sval);
            }
            currentSwitch = word;
            status = LOOKING_FOR_SWITCH_VALUE;
            break;
          }

          // --- Looking for value
          GamessSwitch refSwitch = (GamessSwitch) referenceVars.get(
              currentSwitch);
          if (refSwitch == null || (!refSwitch.isValidValue(word))) {
            System.err.println("Check value " + word + " for switch "
                + currentSwitch);
            //throw new Exception(
            //    "Error parsing GAMESS input file: wrong value " + word +
            //    " for switch " +
            //    currentSwitch);
          }
          sectionVars.put(currentSwitch, word);
          status = LOOKING_FOR_SWITCH;
          break;
        case '"':

          // A double-quoted string was found; sval contains the contents
          break;
        case '\'':

          // A single-quoted string was found; sval contains the contents
          break;
        case StreamTokenizer.TT_EOL:

          // End of line character found
          break;
        case StreamTokenizer.TT_EOF:

          // End of file has been reached
          break;
        default:

          // A regular character was found; the value is the token itself
          break;
      }
    }

    return null;
  }

  private Object parseDataSection(BufferedReader in) throws Exception {
    String line;

    try {

      // --- reading comment
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end-of-file while reading a comment in $DATA section");
      }

      Title = line.trim();

      // --- -2- GROUP, NAXIS
      if ((line = in.readLine()) == null) {
        throw new Exception("Unexpected end-of-file while reading -2- GROUP, NAXIS card in $DATA section");
      } else if (line.trim().length() == 0) {
        throw new Exception("Expecting symmetry symbol in $DATA section");
      }

      StringTokenizer st = new StringTokenizer(line, " \t=");

      if (st.countTokens() < 1) {
        throw new Exception("Expecting symmetry symbol in $DATA section");
      }

      this.SymmetrySymbol = st.nextToken();
      if (!validSymmetrySymbols.contains(SymmetrySymbol.toUpperCase())) {
        throw new Exception("Unknown symmetry symbol: " + SymmetrySymbol
            + " Valid symbols: C1, Cs, Ci, Cn, S2n, Cnh, Cnv, Dn, Dnh, Dnd, T, Th, Td, O, Oh");
      }

      if (st.hasMoreTokens()) {
        String token = st.nextToken();
        try {
          AxisOrder = Integer.parseInt(token);
        } catch (Exception ex) {
          throw new Exception("Wrong number for symmetry axis order: " + token);
        }
      }

      // --- Skip or not next line
      if (!SymmetrySymbol.toUpperCase().equals("C1")) {
        if ((line = in.readLine()) == null) {
          throw new Exception("Unexpected end-of-file while reading blank card after symmetry options in $DATA section");
        }
      }

      // --- Start to read molecular geometry
      int cardSequence = getCardSequence();
      switch (cardSequence) {
        case UNIQUE:
          readUniqueCardSequence(in);
          if (atoms.size() > 0 && cardSequence == UNIQUE) {
            applySymmetry(this.SymmetrySymbol, this.AxisOrder, atoms);
            for (int i = 0; i < atoms.size(); i++) {
              GamessAtom atom = atoms.get(i);
              logger.info((i + 1) + " " + atom.getName() + " "
                  + atom.getNuclearCharge() + " "
                  + atom.getX() + " " + atom.getY() + " "
                  + atom.getZ());
            }
          }
          return null;

        case ZMT:
          readZMatrixCardSequence(in);
          return null;

        case CART:
          readUniqueCardSequence(in);
          return null;
      }

    } catch (Exception ex) {
      throw ex;
    }
    return null;
  }

  /**
   * Parse molecular geometry
   */
  private Object parseDataSection(StreamTokenizer st) throws
      Exception {

    boolean readingComment = true;
    boolean readingSymmetry = false;
    boolean readingMasterframe = false;
    boolean readingAtoms = false;
    String comment = "";
    int status = LOOKING_FOR_SWITCH;
    String word = "";

    //static final int UNIQUE = 0;
    //static final int HINT = 0;
    //static final int CART = 1;
    //static final int ZMT = 2;
    //static final int ZMTMPC = 3;
    int cardSequence = getCardSequence();

    int token = st.nextToken();
    while (token != StreamTokenizer.TT_EOF) {
      token = st.nextToken();
      switch (token) {
        case StreamTokenizer.TT_NUMBER:

          if (readingComment) {
            if (comment.length() > 0) {
              comment += " ";
            }
            comment += st.nval;
            break;
          }

          // --- Reading GROUP is the Schoenflies symbol of the symmetry group,
          // you may choose from
          // C1, Cs, Ci, Cn, S2n, Cnh, Cnv, Dn, Dnh, Dnd,
          // T, Th, Td, O, Oh.
          if (readingSymmetry) {
            this.AxisOrder = (int) st.nval;
            break;
          }

          if (readingMasterframe) {
            break;
          }

          // A number was found; the value is in nval
          word = String.valueOf(st.nval);
        //double num = st.nval;
        //break;
        case StreamTokenizer.TT_WORD:

          if (readingComment) {
            if (comment.length() > 0) {
              comment += " ";
            }
            comment += st.sval;
            break;
          }

          if (readingSymmetry) {
            this.SymmetrySymbol = st.sval;
            if (!validSymmetrySymbols.contains(SymmetrySymbol.toUpperCase())) {
              throw new Exception("Unknown symmetry symbol: "
                  + SymmetrySymbol
                  + " Valid symbols: C1, Cs, Ci, Cn, S2n, Cnh, Cnv, Dn, Dnh, Dnd, T, Th, Td, O, Oh");
            }

            if (SymmetrySymbol.toUpperCase().equals("C1")) {
              readingSymmetry = false;
              readingMasterframe = false;
              readingAtoms = true;
              break;
            }

            break;
          }

          if (readingMasterframe) {
            break;
          }

          if (readingAtoms) {
          }

          if (token == StreamTokenizer.TT_WORD) {
            word = st.sval.toUpperCase();
          }
          if (word.equalsIgnoreCase(endSection)) {
            if (status == LOOKING_FOR_SWITCH) {
              return null;
            } else {
              throw new Exception(
                  "Error parsing GAMESS input file: unexpected end of a $CONTROL section..");
            }
          }

          break;
        case '"':

          // A double-quoted string was found; sval contains the contents
          break;
        case '\'':

          // A single-quoted string was found; sval contains the contents
          break;
        case StreamTokenizer.TT_EOL:

          if (readingComment) {
            readingComment = false;
            readingSymmetry = true;
            Title = comment;
            break;
          }

          if (readingSymmetry) {
            readingSymmetry = false;
            readingMasterframe = true;
            break;
          }

          if (readingMasterframe) {
            readingMasterframe = false;
            switch (cardSequence) {
              case UNIQUE:
                readUniqueCardSequence(st);
                return null;
              case ZMT:
                readZMatrixCardSequence(st);
                return null;
            }

            break;
          }

          if (readingAtoms) {
            switch (cardSequence) {
              case UNIQUE:
                readUniqueCardSequence(st);
                return null;
              case ZMT:
                readZMatrixCardSequence(st);
                return null;

            }
            break;
          }

          // End of line character found
          break;
        case StreamTokenizer.TT_EOF:

          // End of file has been reached
          break;
        default:

          // A regular character was found; the value is the token itself
          break;
      }
    }

    return null;
  }

  private Object readUniqueCardSequence(BufferedReader in) throws Exception {

    GamessAtom atom = null;

    double factor = 1;
    int cardSequence = UNIQUE;
    if (currentVars.containsKey(controlSection)) {
      Map csection = currentVars.get(controlSection);

      if (csection.containsKey("UNITS")) {
        String value = (String) csection.get("UNITS");
        value = value.toUpperCase();
        if (value.equals("ANGS")) {
          factor = 1;
        } else if (value.equals("BOHR")) {
          factor = ONE_BOHR;
        }
      }

      if (csection.containsKey("COORD")) {
        String value = (String) csection.get("COORD");
        if (value.toUpperCase().equals("CART")) {
          cardSequence = CART;
        }
      }
    }

    String line, token;
    StringTokenizer st;

    boolean readBasis = !currentVars.containsKey( basisSection);
    if (cardSequence == CART) {
      readBasis = false;
    }

    boolean readingAtom = true;

    try {
      while ((line = in.readLine()) != null) {

        if (line.trim().toUpperCase().startsWith(endSection)) {
          return null;
        }

        if (readingAtom) {
          st = new StringTokenizer(line, " \t");

          if (!st.hasMoreTokens()) {
            throw new Exception("Expecting atom name...");
          }

          atom = new GamessAtom();
          atom.setName(st.nextToken());

          // --- Reading atomic charge
          if (!st.hasMoreTokens()) {
            throw new Exception("Expecting atomic charge...");
          }

          token = st.nextToken();
          try {
            float charge = Float.parseFloat(token);
            atom.setNuclearCharge(charge);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atomic charge " + token
                + " : " + ex.getMessage());
          }

          // --- Reading X
          if (!st.hasMoreTokens()) {
            atom.setXYZ(0, 0, 0);
            atoms.add(atom);
            readingAtom = !readBasis;
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setX(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom X value " + token
                + " : " + ex.getMessage());
          }

          // --- Reading Y
          if (!st.hasMoreTokens()) {
            atom.setY(0);
            atom.setZ(0);
            atoms.add(atom);
            readingAtom = !readBasis;
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setY(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom Y value " + token
                + " : " + ex.getMessage());
          }

          // --- Reading Z
          if (!st.hasMoreTokens()) {
            atom.setZ(0);
            atoms.add(atom);
            readingAtom = !readBasis;
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setZ(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom Y value " + token
                + " : " + ex.getMessage());
          }
          atoms.add(atom);
          readingAtom = !readBasis;
          continue;
        } else if (line.trim().length() == 0) { // Found blank line after basis
          readingAtom = true;
        }
      }
    } catch (Exception ex) {
      throw ex;
    }

    return null;
  }

  private void readFMOXYZSection(BufferedReader in) throws Exception {

    GamessAtom atom = null;

    double factor = 1;
    if (currentVars.containsKey(controlSection)) {
      Map csection = currentVars.get(controlSection);

      if (csection.containsKey("UNITS")) {
        String value = (String) csection.get("UNITS");
        value = value.toUpperCase();
        if (value.equals("ANGS")) {
          factor = 1;
        } else if (value.equals("BOHR")) {
          factor = ONE_BOHR;
        }
      }
    }

    String line, token;
    StringTokenizer st;

    boolean readingAtom = true;

    try {
      while ((line = in.readLine()) != null) {

        if (line.trim().toUpperCase().startsWith(endSection)) {
          return;
        }

        if (readingAtom) {
          st = new StringTokenizer(line, " \t");

          if (!st.hasMoreTokens()) {
            throw new Exception("Expecting dummy atom name in FMOXYZ section...");
          }

          atom = new GamessAtom();
          st.nextToken(); // Skip it

          // --- Reading atomic charge
          if (!st.hasMoreTokens()) {
            throw new Exception("Expecting atomic charge in FMOXYZ section...");
          }

          token = st.nextToken();
          try {
            float charge = Float.parseFloat(token);
            atom.setNuclearCharge(charge);
            atom.setName(ChemicalElements.getElementSymbol((int) charge));
          } catch (Exception ex) {
            // i.e. we got element symbol
            atom.setName(token);
            atom.setNuclearCharge(ChemicalElements.getAtomicNumber(token));
          }

          // --- Reading X
          if (!st.hasMoreTokens()) {
            atom.setXYZ(0, 0, 0);
            atoms.add(atom);
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setX(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom X value in FMOXYZ Section" + token + " : " + ex.getMessage());
          }

          // --- Reading Y
          if (!st.hasMoreTokens()) {
            atom.setY(0);
            atom.setZ(0);
            atoms.add(atom);
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setY(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom Y value in FMOXYZ Section " + token + " : " + ex.getMessage());
          }

          // --- Reading Z
          if (!st.hasMoreTokens()) {
            atom.setZ(0);
            atoms.add(atom);
            continue;
          }

          token = st.nextToken();
          try {
            float coord = Float.parseFloat(token);
            atom.setZ(coord * factor);
          } catch (Exception ex) {
            throw new Exception("Wrong value for atom Y value in FMOXYZ Section " + token + " : " + ex.getMessage());
          }
          atoms.add(atom);
          continue;
        }

      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  private Object readUniqueCardSequence(StreamTokenizer st) throws
      Exception {
    boolean readingNAME = true;
    boolean readingZNUC = false;
    boolean readingX = false, readingY = false, readingZ = false;
    boolean waitingForEOL = false;

    boolean skippingBasis = false;
    boolean readingBasisData = false;

    boolean readBasis = !currentVars.containsKey( basisSection);

    GamessAtom atom = null;

    double factor = 1;
    if (currentVars.containsKey(controlSection)) {
      Map csection = currentVars.get(controlSection);
      if (csection.containsKey("UNITS")) {
        String value = (String) csection.get("UNITS");
        value = value.toUpperCase();
        if (value.equals("ANGS")) {
          factor = 1;
        } else if (value.equals("BOHR")) {
          factor = ONE_BOHR;
        }
      }
    }

    String word = "";

    int token = StreamTokenizer.TT_EOF + 1;

    while (token != StreamTokenizer.TT_EOF) {
      token = st.nextToken();
      switch (token) {
        case StreamTokenizer.TT_NUMBER:
        case StreamTokenizer.TT_WORD:

          if (waitingForEOL) {
            break;
          }

          if (st.sval != null && st.sval.equalsIgnoreCase(endSection)) {
            return null;
          }

          if (readBasis && skippingBasis) {
            readingBasisData = true;
            break;
          }

          // --- Reading atom name
          if (readingNAME) {
            atom = new GamessAtom();
            if (token == StreamTokenizer.TT_WORD) {
              atom.setName(st.sval);
            } else if (token == StreamTokenizer.TT_NUMBER) {
              atom.setName(String.valueOf(st.nval));
            }
            readingNAME = false;
            readingZNUC = true;
            break;
          }

          // --- Reading nuclear charge
          if (readingZNUC) {
            atom.setNuclearCharge(st.nval);
            readingZNUC = false;
            readingX = true;
            break;
          }

          // --- Reading X
          if (readingX) {
            atom.setX(st.nval * factor);
            readingX = false;
            readingY = true;
            break;
          }

          // --- Reading Y
          if (readingY) {
            atom.setY(st.nval * factor);
            readingY = false;
            readingZ = true;
            break;
          }

          // --- Reading Z
          if (readingZ) {
            atom.setZ(st.nval * factor);
            readingZ = false;
            waitingForEOL = true;
            atoms.add(atom);
            break;
          }

          // A number was found; the value is in nval
          word = String.valueOf(st.nval);

          //double num = st.nval;
          //break;
          if (token == StreamTokenizer.TT_WORD) {
            word = st.sval.toUpperCase();
          }
          if (word.equalsIgnoreCase(endSection)) {
            return null;
          }

          break;
        case '"':

          // A double-quoted string was found; sval contains the contents
          //String dquoteVal = st.sval;
          break;
        case '\'':

          // A single-quoted string was found; sval contains the contents
          //String squoteVal = st.sval;
          break;
        case StreamTokenizer.TT_EOL:

          if (readBasis && skippingBasis) {
            if (readingBasisData) {
              readingBasisData = false;
              break;
            } else { // Found empty line
              skippingBasis = false;
              waitingForEOL = false;
              readingNAME = true; // Start to read the next atom
            }
            break;
          }

          if (readingX) {
            atom.setXYZ(0, 0, 0);
            atoms.add(atom);
            readingX = false;
            if (readBasis) {
              skippingBasis = true;
            } else {
              waitingForEOL = false;
              readingNAME = true; // Start to read the next atom
              break;
            }
          }

          if (readingY) {
            atom.setY(0);
            atom.setZ(0);
            atoms.add(atom);
            readingY = false;
            if (readBasis) {
              skippingBasis = true;
            } else {
              waitingForEOL = false;
              readingNAME = true; // Start to read the next atom
              break;
            }

          }

          if (readingZ) {
            atom.setZ(0);
            atoms.add(atom);
            readingZ = false;
            if (readBasis) {
              skippingBasis = true;
            } else {
              waitingForEOL = false;
              readingNAME = true; // Start to read the next atom
              break;
            }

          }

          waitingForEOL = false;
          if (readBasis) {
            skippingBasis = true;
          } else {
            readingNAME = true; // Start to read the next atom
          }

          // End of line character found
          break;
        case StreamTokenizer.TT_EOF:

          // End of file has been reached
          break;
        default:

          // A regular character was found; the value is the token itself
          //char ch = (char) st.ttype;
          break;
      }
    }

    return null;
  }

  private Object readZMatrixCardSequence(BufferedReader in) throws Exception {

    GamessAtom atom = null;

    double factor = 1;
    if (currentVars.containsKey(controlSection)) {
      Map csection = currentVars.get(controlSection);
      if (csection.containsKey("UNITS")) {
        String value = (String) csection.get("UNITS");
        value = value.toUpperCase();
        if (value.equals("ANGS")) {
          factor = 1;
        } else if (value.equals("BOHR")) {
          factor = ONE_BOHR;
        }
      }
    }

    String line, token;
    StringTokenizer st;
    boolean yesSymbolicStrings = false;
    boolean endSectionFound = false;
    Map<String, Integer> atomNames = new HashMap<String, Integer>(100);
    Map<String, Float> symbolicStrings = new HashMap<String, Float>(300);

    try {
      // --- reading atoms
      while ((line = in.readLine()) != null) {
        line = line.trim(); //.toUpperCase();
        st = new StringTokenizer(line, " \t");

        // --- Atom name
        if (!st.hasMoreTokens()) { // Blank line - end of input
          break; //throw new Exception("Expecting atom name");
        }

        token = st.nextToken();

        if (token.equalsIgnoreCase(endSection)) {
          endSectionFound = true;
          break;
        }

        atom = new GamessAtom();
        atom.setName(token);
        atom.setAtomicNumber(getAtomicNumber(atom.getName()));

        // --- check atom name for uniqueness
        if (atomNames.containsKey(atom.getName())) {
          // --- it's not unique
          atomNames.put(atom.getName(), new Integer(-1));
        } else {
          atomNames.put(atom.getName(), new Integer(atoms.size()));
        }

        if (atoms.size() == 0) {
          atoms.add(atom);
          continue;
        }

        // Reading i1
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting i1");
        }
        token = st.nextToken();

        // --- if it's atom's name
        boolean isNumber = true;
        int number = 0;

        try {
          number = Integer.parseInt(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        if (isNumber) { // --- If it's a number
          if (number < 1 || number > atoms.size()) {
            throw new Exception("Wrong i1 value " + number + " for " + (atoms.size() + 1) + " atom : it should be between 1 and "
                + atoms.size());
          }
          atom.set_i1(number - 1);
        } else // --- If it's an atom
        if (atomNames.containsKey(token)) {
          Integer i = atomNames.get(token);
          atom.set_i1(i);
        } else {
          throw new Exception("Wrong i1 value " + token
              + " for " + (atoms.size() + 1)
              + " atom : no atom with such name was previously specified");
        }

        // --- Reading Bond Length
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting bond length value");
        }
        token = st.nextToken();

        isNumber = true;
        float value = 0;

        try {
          value = Float.parseFloat(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        // --- if it's a parameter
        if (!isNumber) {
          String symbolic = token;
          if (token.startsWith("-")) {
            symbolic = token.substring(1);
          }
          if (!symbolicStrings.containsKey(symbolic)) {
            symbolicStrings.put(symbolic, null);
          }

          atom.setBondLength(token);
          yesSymbolicStrings = true;
        } else {
          atom.setBondLength(value);
        }

        if (atoms.size() == 1) {
          atoms.add(atom);
          continue;
        }

        // --- Reading i2
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting i2");
        }
        token = st.nextToken();

        // --- if it's atom's name
        isNumber = true;

        try {
          number = Integer.parseInt(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        if (isNumber) { // --- If it's a number
          if (number < 1 || number > atoms.size()
              || number == atom.get_i1() + 1) {
            throw new Exception("Wrong i2 value " + number + " for "
                + (atoms.size() + 1)
                + " atom : it should be between 1 and "
                + atoms.size() + " and not "
                + (atom.get_i1() + 1));
          }
          atom.set_i2(number - 1);
        } else // --- If it's an atom
        if (atomNames.containsKey(token)) {
          Integer i = atomNames.get(token);
          atom.set_i2(i);
        } else {
          throw new Exception("Wrong i2 value " + token
              + " for " + (atoms.size() + 1)
              + " atom : no atom with such name was previously specified");
        }

        // --- Reading ALPHA
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting alpha value");
        }
        token = st.nextToken();

        isNumber = true;

        try {
          value = Float.parseFloat(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        // --- if it's a parameter
        if (!isNumber) {
          String symbolic = token;
          if (token.startsWith("-")) {
            symbolic = token.substring(1);
          }
          if (!symbolicStrings.containsKey(symbolic)) {
            symbolicStrings.put(symbolic, null);
          }

          atom.setAlpha(token);
          yesSymbolicStrings = true;
        } else {
          atom.setAlpha(value);
        }

        if (atoms.size() == 2) {
          atoms.add(atom);
          continue;
        }

        // --- Reading i3
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting i3");
        }
        token = st.nextToken();

        // --- if it's atom's name
        isNumber = true;

        try {
          number = Integer.parseInt(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        // --- if it's atom's name
        if (!isNumber) {
          if (atomNames.containsKey(token)) {
            Integer i = atomNames.get(token);
            atom.set_i3(i);
          } else {
            throw new Exception("Wrong i3 value " + token
                + " for " + (atoms.size() + 1)
                + " atom : no atom with such name was previously specified");
          }
        } // --- If it's a number
        else {
          if (number < 1 || number > atoms.size()
              || number == atom.get_i1() + 1
              || number == atom.get_i2() + 1) {
            throw new Exception("Wrong i3 value " + number
                + " for " + (atoms.size() + 1)
                + " atom : it should be between 1 and "
                + atoms.size() + " and not "
                + (atom.get_i1() + 1) + " or "
                + (atom.get_i2() + 1));
          }
          atom.set_i3(number - 1);
        }

        // --- Reading BETA
        if (!st.hasMoreTokens()) {
          throw new Exception("Expecting beta value");
        }
        token = st.nextToken();

        isNumber = true;

        try {
          value = Float.parseFloat(token);
        } catch (Exception ex) {
          isNumber = false;
        }

        // --- if it's a parameter
        if (!isNumber) {
          String symbolic = token;
          if (token.startsWith("-")) {
            symbolic = token.substring(1);
          }
          if (!symbolicStrings.containsKey(symbolic)) {
            symbolicStrings.put(symbolic, null);
          }

          atom.setBeta(token);
          yesSymbolicStrings = true;
        } else {
          atom.setBeta(value);
        }

        // --- REading i4 (not implemented yet
        if (st.hasMoreTokens()) {
          token = st.nextToken();
          if (token.startsWith("+")) {
            token = token.substring(1);
          }
          int i4 = 0;
          try {
            i4 = Integer.parseInt(token);
            if (Math.abs(i4) > 1) {
              i4 = i4 > 0 ? 1 : -1;
            }
          } catch (Exception ex) {
            throw new Exception("Cannot parse i4 parameter " + token + " : " + ex.getMessage());
          }

          atom.alternateZmatrixFormat = i4;

          //if (i4 != 0) {
          //  throw new Exception("Rewading i4 is not implemented yet");
          //}
        }

        atoms.add(atom);

      } // --- End of while/ reading atoms

      // --- Resolve Z-Matrix
      if (symbolicStrings.size() > 0) {
        logger.info("Expecting " + symbolicStrings.size()
            + " unique symbolic strings");
        if (endSectionFound) {
          throw new Exception(
              "Unexpected end of $DATA section. Excepting symbolic strings");
        }
      }

      // --- Start to read symbolic strings
      while ((line = in.readLine()) != null) {
        //line = line.trim().toUpperCase();

        st = new StringTokenizer(line, "= \t");

        if (st.countTokens() < 1) {
          continue;
        }

        String currentString = st.nextToken();

        if (currentString.equalsIgnoreCase(endSection)) {
          break;
        }

        if (st.countTokens() < 1) {
          throw new Exception(
              "Expecting at least two values for symbolic strings");
        }

        // --- Read symbol
        if (!symbolicStrings.containsKey(currentString)) {
          System.err.println(
              "Warning: atom string parameters do not have string "
              + currentString);
          continue;
        }

        // --- Read value
        token = st.nextToken();
        try {
          Float f = new Float(token);
          symbolicStrings.put(currentString, f);
        } catch (Exception ex) {
          throw new Exception("Wrong value for symbolic string " + token);
        }
      }
    } catch (Exception ex) {
      throw ex;
    }

    if (symbolicStrings.size() > 0) {
      try {
        resolveSymbolicStrings(symbolicStrings, atoms);
      } catch (Exception ex) {
        throw ex;
      }
    }

    // --- Convert to cartesian coordinates
    fromZMatrixToCartesians(atoms);

    return null;
  }

  private Object readZMatrixCardSequence(StreamTokenizer st) throws
      Exception {
    int natom = 0;
    boolean yesSymbolicStrings = false;
    boolean readingNAME = true;
    boolean reading_i1 = false, reading_i2 = false, reading_i3 = false,
        reading_i4 = false;
    boolean readingBLENGTH = false, readingALPHA = false, readingBETA = false;
    boolean waitingForEOL = false;
    boolean endOfCoordinates = false;
    boolean endOfSection = false;
    GamessAtom atom = null;

    Map<String, Integer> atomNames = new HashMap<String, Integer>(100);
    Map<String, Float> symbolicStrings = new HashMap<String, Float>(300);

    double factor = 1;
    Map controlGroup = currentVars.get(controlSection);
    if (controlGroup != null && controlGroup.containsKey("UNITS")) {
      String value = (String) controlGroup.get("UNITS");
      value = value.toUpperCase();
      if (value.equals("ANGS")) {
        factor = 1;
      } else if (value.equals("BOHR")) {
        factor = ONE_BOHR;
      }
    }

    String word = "";

    int token = StreamTokenizer.TT_EOF + 1;

    while (token != StreamTokenizer.TT_EOF) {
      token = st.nextToken();
      switch (token) {
        case StreamTokenizer.TT_NUMBER:
        case StreamTokenizer.TT_WORD:

          if (token == StreamTokenizer.TT_WORD
              && st.sval.equalsIgnoreCase(endSection)) {
            endOfCoordinates = true;
            endOfSection = true;
            break;
          }

          if (waitingForEOL) {
            break;
          }

          if (st.sval != null && st.sval.equalsIgnoreCase(endSection)) {
            return null;
          }

          // --- Reading atom name
          if (readingNAME) {
            atom = new GamessAtom();
            if (token == StreamTokenizer.TT_WORD) {
              atom.setName(st.sval);
            } else if (token == StreamTokenizer.TT_NUMBER) {
              atom.setName(String.valueOf(st.nval));
            }

            atom.setAtomicNumber(getAtomicNumber(atom.getName()));

            // --- check atom name for uniqueness
            if (atomNames.containsKey(atom.getName())) {
              // --- it's not unique
              atomNames.put(atom.getName(), new Integer(-1));
            } else {
              atomNames.put(atom.getName(), new Integer(natom));
            }

            if (atoms.size() == 0) {
              atoms.add(atom);
              waitingForEOL = true;
              break;
            }

            readingNAME = false;

            reading_i1 = true;
            break;
          }

          // --- Reading i1
          if (reading_i1) {
            // --- if it's atom's name
            if (token == StreamTokenizer.TT_WORD) {
              if (atomNames.containsKey(st.sval)) {
                Integer i = atomNames.get(st.sval);
                atom.set_i1(i);
              } else {
                throw new Exception("Wrong i1 value " + st.sval
                    + " for " + (atoms.size() + 1)
                    + " atom : no atom with such name was previously specified");
              }
            } // --- If it's a number
            else {
              int nat = (int) st.nval;
              if (nat < 1 || nat > atoms.size()) {
                throw new Exception("Wrong i1 value " + nat
                    + " for " + (atoms.size() + 1)
                    + " atom : it should be between 1 and "
                    + atoms.size());
              }
              atom.set_i1(nat - 1);
            }

            reading_i1 = false;
            readingBLENGTH = true;
            break;
          }

          // --- Reading Bond Length
          if (readingBLENGTH) {
            // --- if it's a parameter
            if (token == StreamTokenizer.TT_WORD) {
              String symbolic = st.sval;
              if (st.sval.startsWith("-")) {
                symbolic = st.sval.substring(1);
              }
              if (!symbolicStrings.containsKey(symbolic)) {
                symbolicStrings.put(symbolic, null);
              }

              atom.setBondLength(st.sval);
              yesSymbolicStrings = true;
            } else {
              atom.setBondLength((float) st.nval);
            }

            readingBLENGTH = false;

            if (atoms.size() == 1) {
              atoms.add(atom);
              readingNAME = true;
              waitingForEOL = true;
              break;
            }

            reading_i2 = true;
            break;
          }

          // --- Reading i2
          if (reading_i2) {
            // --- if it's atom's name
            if (token == StreamTokenizer.TT_WORD) {
              if (atomNames.containsKey(st.sval)) {
                Integer i = atomNames.get(st.sval);
                atom.set_i2(i);
              } else {
                throw new Exception("Wrong i2 value " + st.sval
                    + " for " + (atoms.size() + 1)
                    + " atom : no atom with such name was previously specified");
              }
            } // --- If it's a number
            else {
              int nat = (int) st.nval;
              if (nat < 1 || nat > atoms.size()
                  || nat == atom.get_i1() + 1) {
                throw new Exception("Wrong i2 value " + nat
                    + " for " + (atoms.size() + 1)
                    + " atom : it should be between 1 and "
                    + atoms.size() + " and not "
                    + (atom.get_i1() + 1));
              }
              atom.set_i2(nat - 1);
            }

            reading_i2 = false;
            readingALPHA = true;
            break;
          }

          // --- Reading ALPHA
          if (readingALPHA) {
            // --- if it's a parameter
            if (token == StreamTokenizer.TT_WORD) {
              String symbolic = st.sval;
              if (st.sval.startsWith("-")) {
                symbolic = st.sval.substring(1);
              }
              if (!symbolicStrings.containsKey(symbolic)) {
                symbolicStrings.put(symbolic, null);
              }

              atom.setAlpha(st.sval);
              yesSymbolicStrings = true;
            } else {
              atom.setAlpha((float) st.nval);
            }

            readingALPHA = false;

            if (atoms.size() == 2) {
              atoms.add(atom);
              readingNAME = true;
              waitingForEOL = true;
              break;
            }

            reading_i3 = true;
            break;

          }

          // --- Reading i3
          if (reading_i3) {
            // --- if it's atom's name
            if (token == StreamTokenizer.TT_WORD) {
              if (atomNames.containsKey(st.sval)) {
                Integer i = atomNames.get(st.sval);
                atom.set_i3(i);
              } else {
                throw new Exception("Wrong i3 value " + st.sval
                    + " for " + (atoms.size() + 1)
                    + " atom : no atom with such name was previously specified");
              }
            } // --- If it's a number
            else {
              int nat = (int) st.nval;
              if (nat < 1 || nat > atoms.size()
                  || nat == atom.get_i1() + 1
                  || nat == atom.get_i2() + 1) {
                throw new Exception("Wrong i3 value " + nat
                    + " for " + (atoms.size() + 1)
                    + " atom : it should be between 1 and "
                    + atoms.size() + " and not "
                    + (atom.get_i1() + 1) + " or "
                    + (atom.get_i2() + 1));
              }
              atom.set_i3(nat - 1);
            }

            reading_i3 = false;
            readingBETA = true;
            break;
          }

          // --- Reading BETA
          if (readingBETA) {
            // --- if it's a parameter
            if (token == StreamTokenizer.TT_WORD) {
              String symbolic = st.sval;
              if (st.sval.startsWith("-")) {
                symbolic = st.sval.substring(1);
              }
              if (!symbolicStrings.containsKey(symbolic)) {
                symbolicStrings.put(symbolic, null);
              }

              atom.setBeta(st.sval);
              yesSymbolicStrings = true;
            } else {
              atom.setBeta((float) st.nval);
            }

            readingBETA = false;
            reading_i4 = true;
            break;

          }

          // --- REading i4 (not implemented yet
          if (reading_i4) {
            throw new Exception("Rewading i4 is not implemented yet");
          }

          // A number was found; the value is in nval
          word = String.valueOf(st.nval);

          //double num = st.nval;
          //break;
          if (token == StreamTokenizer.TT_WORD) {
            word = st.sval.toUpperCase();
          }
          if (word.equalsIgnoreCase(endSection)) {
            return null;
          }

          break;
        case '"':

          // A double-quoted string was found; sval contains the contents
          //String dquoteVal = st.sval;
          break;
        case '\'':

          // A single-quoted string was found; sval contains the contents
          //String squoteVal = st.sval;
          break;
        case StreamTokenizer.TT_EOL:

          if (reading_i4) {
            atoms.add(atom);
            reading_i4 = false;
            waitingForEOL = false;
            readingNAME = true;
            break;
          }

          if (readingNAME && waitingForEOL) {
            waitingForEOL = false;
            break;
          } else if (readingNAME) { // End of Z-matrix input
            endOfCoordinates = true;
            break;
          }

          waitingForEOL = false;
          readingNAME = true; // Start to read the next atom

          // End of line character found
          break;
        case StreamTokenizer.TT_EOF:

          // End of file has been reached
          break;
        default:

          // A regular character was found; the value is the token itself
          //char ch = (char) st.ttype;
          break;
      }

      if (endOfCoordinates) {
        break;
      }
    }

    // --- Resolve Z-Matrix
    if (symbolicStrings.size() > 0) {
      logger.info("Expecting " + symbolicStrings.size()
          + " unique symbolic strings");

      if (endOfSection) {
        throw new Exception(
            "Unexpected end of $data section. Expecting for string values");
      }

      // --- Start to read symbolic strings
      token = StreamTokenizer.TT_EOF + 1;
      waitingForEOL = false;
      boolean readingSTRING = true, readingVALUE = false;
      boolean endOfInput = false;
      String currentString = "";

      while (token != StreamTokenizer.TT_EOF) {
        token = st.nextToken();

        switch (token) {
          // --- Reading value
          case StreamTokenizer.TT_NUMBER:

            if (waitingForEOL) {
              System.err.println("Warning: expecting EOL, got "
                  + st.nval);
              break;
            }

            if (!symbolicStrings.containsKey(currentString)) {
              System.err.println(
                  "Warning: atom string parameters do not have string "
                  + st.sval);
            } else if (readingVALUE) {
              symbolicStrings.put(currentString, new Float(st.nval));
            }

            readingVALUE = false;
            readingSTRING = true;
            waitingForEOL = true;

            break;

          // -- reading value
          case StreamTokenizer.TT_WORD:

            if (st.sval.equalsIgnoreCase(endSection)) {
              endOfInput = true;
              endOfSection = true;
              break;
            }

            if (waitingForEOL) {
              System.err.println("Warning: expecting EOL, got "
                  + st.sval);
              break;
            }

            if (readingSTRING) {
              currentString = st.sval;
              if (!symbolicStrings.containsKey(currentString)) {
                System.err.println(
                    "Warning: atom string parameters do not have string "
                    + st.sval);
              }
              readingSTRING = false;
              readingVALUE = true;
            }

            break;

          case StreamTokenizer.TT_EOL:

            if (waitingForEOL) {
              readingVALUE = false;
              readingSTRING = true;
              waitingForEOL = false;
              break;
            }

            if (readingSTRING) { // Empty line
              break;
            }

            if (readingVALUE) {
              throw new Exception(
                  "Unexpected end of line while expecting for a value");
            }

            break;
          case StreamTokenizer.TT_EOF:

            break;
          default:

            // A regular character was found; the value is the token itself
            //char ch = (char) st.ttype;
            break;

        }

        if (endOfInput) {
          break;
        }
      }
    }

    if (symbolicStrings.size() > 0) {
      try {
        resolveSymbolicStrings(symbolicStrings, atoms);
      } catch (Exception ex) {
        throw ex;
      }
    }

    // --- Convert to cartesian coordinates
    fromZMatrixToCartesians(atoms);

    return null;
  }

  private void resolveSymbolicStrings(Map<String, Float> symbolicStrings, List<GamessAtom> atoms) throws Exception {

    // --- Error check
    boolean error = false;
    String Errors = "";
    Set set = symbolicStrings.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String string = me.getKey().toString();
      Object value = me.getValue();

      if (value == null) {
        error = true;
        Errors += "No value for string " + string + "\n";
      } else {
        logger.info(me.getKey().toString() + "="
            + me.getValue().toString());
      }
    }

    if (error) {
      throw new Exception(Errors);
    }

    // --- resolve strings
    for (int i = 0; i < atoms.size(); i++) {
      GamessAtom atom = atoms.get(i);

      String string = atom.getBondLengthPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setBondLength(f);
      }

      string = atom.getAlphaPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setAlpha(f);
      }

      string = atom.getBetaPar();
      if (string != null) {
        boolean negative = string.startsWith("-");
        if (negative) {
          string = string.substring(1);
        }
        Float f = symbolicStrings.get(string);
        if (negative) {
          f = -f;
        }
        atom.setBeta(f);
      }
    }

  }

  private Object skipSection(StreamTokenizer st) throws
      Exception {
    String word = "";

    int token = st.nextToken();

    while (token != StreamTokenizer.TT_EOF) {
      token = st.nextToken();
      switch (token) {
        case StreamTokenizer.TT_NUMBER:

          // A number was found; the value is in nval
          word = String.valueOf(st.nval);
        //double num = st.nval;
        //break;
        case StreamTokenizer.TT_WORD:

          if (token == StreamTokenizer.TT_WORD) {
            word = st.sval.toUpperCase();
          }
          if (word.equalsIgnoreCase(endSection)) {
            return null;
          }

          break;
        case '"':

          // A double-quoted string was found; sval contains the contents
          //String dquoteVal = st.sval;
          break;
        case '\'':

          // A single-quoted string was found; sval contains the contents
          //String squoteVal = st.sval;
          break;
        case StreamTokenizer.TT_EOL:

          // End of line character found
          break;
        case StreamTokenizer.TT_EOF:

          // End of file has been reached
          break;
        default:

          // A regular character was found; the value is the token itself
          //char ch = (char) st.ttype;
          break;
      }
    }

    return null;
  }

  public Map getControlSection() {
    if (currentVars.containsKey(controlSection)) {
      return new HashMap(currentVars.get(controlSection));
    }
    return null; //new HashMap();
  }

  public static void main(String[] args) {
    Gamess gamess = new Gamess();
    MoleculeInterface mol = new Molecule();
    try {
      gamess.parseGamessInput(args[0], 0);
      gamess.getMolecularInterface(mol);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }

    logger.info("Number of atoms " + mol.getNumberOfAtoms());
    for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
      AtomInterface atom = mol.getAtomInterface(i);
      logger.info((i + 1) + " " + atom.getName() + " "
          + atom.getX()
          + " " + atom.getY() + " " + atom.getZ());
    }
    Map controls = gamess.getControlSection();
    Set set = controls.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      logger.info(me.getKey().toString() + "="
          + me.getValue().toString());
    }

  }

  public void applySymmetry(String symbol, int axisOrder, List<GamessAtom> points) throws Exception {

    if (symbol.toUpperCase().equals("C1")) {
      return;
    }

    List<GamessAtom> generatedAtoms = new ArrayList<GamessAtom>();

    for (int i = 0; i < points.size(); i++) {
      GamessAtom atom = points.get(i);
      Object[] dots = PointGroup.generatePoints(symbol, axisOrder, atom);
      if (dots == null) {
        continue;
      }
      for (int j = 0; j < dots.length; j++) {
        GamessAtom a = (GamessAtom) dots[j];
        a.setName(atom.getName());
        a.setNuclearCharge(atom.getNuclearCharge());
        generatedAtoms.add(a);
      }
    }

    // --- Add generated atoms to the pool...
    for (int i = 0; i < generatedAtoms.size(); i++) {
      points.add(generatedAtoms.get(i));
    }
  }

  /**
   * Expects bond length in angstroms
   *
   * @param atoms ArrayList
   * @throws Exception
   */
  public static void fromZMatrixToCartesians(List<GamessAtom> atoms) throws
      Exception {
    int i;
    int natoms = atoms.size();
    GamessAtom a, a1, a2, a3;
    //Float xyz[] = new Float[3];
    float xyz[] = new float[3], x1[], x2[], x3[];

    double SIN2, COS2, SIN3, COS3,
        VT[] = new double[3], V1[] = new double[3],
        V2[] = new double[3];
    double R2,
        V3[] = new double[3], VA1, VB1, VC1, R3, V[] = new double[3];

    if (natoms < 1) {
      return;
    }

    float factor = 1.0f;
    //if (bohrs) {
    //   factor = (float) ONE_BOHR;
    //}

    // --- The first atom. Put it into the origin
    GamessAtom atom = atoms.get(0);
    atom.setXYZ(0, 0, 0);

    if (natoms < 2) {
      return;
    }

    // --- ther second atom: put it along the X-axis
    atom = atoms.get(1);
    atom.setXYZ(atom.getBondLength() * factor, 0, 0);

    if (natoms < 3) {
      return;
    }

    // --- Third atom (put it into the XOY plane
    a = atoms.get(2);

    //x1 = (float[]) cartesians.get(a.ijk[0] - 1);
    x1 = new float[3];
    int index = a.get_i1();
    a1 = atoms.get(index);
    x1[0] = a1.getX();
    x1[1] = a1.getY();
    x1[2] = a1.getZ();

    xyz[2] = 0.0f; // Z-coordinate

    //if (a.ijk[0] == 1) { // Connected to the first atom
    if (index == 0) { // Connected to the first atom
      //logger.info("To the 1st");
      xyz[0] = a.getBondLength() * factor
          * (float) Math.cos((double) (a.getAlpha() * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = a.getBondLength() * factor
          * (float) Math.sin((double) (a.getAlpha() * DEGREES_TO_RADIANS));
    } else {
      //logger.info("To the 2nd");
      xyz[0] = x1[0]
          - a.getBondLength() * factor
          * (float) Math.cos((double) (a.getAlpha() * DEGREES_TO_RADIANS)); // So, we put it along X-axis
      xyz[1] = -a.getBondLength() * factor
          * (float) Math.sin((double) (a.getAlpha() * DEGREES_TO_RADIANS));
    }

    //cartesians.add(xyz);
    a.setXYZ(xyz[0], xyz[1], xyz[2]);

    // Other atoms
    for (i = 3; i < natoms; i++) {

      //xyz = new float[3];
      a = atoms.get(i);

      //x1 = (float[]) cartesians.get(a.ijk[0] - 1);
      //x2 = (float[]) cartesians.get(a.ijk[1] - 1);
      //x3 = (float[]) cartesians.get(a.ijk[2] - 1);
      a1 = atoms.get(a.get_i1());
      a2 = atoms.get(a.get_i2());
      a3 = atoms.get(a.get_i3());

      x1 = a1.xyz;
      x2 = a2.xyz;
      x3 = a3.xyz;

      if (a.alternateZmatrixFormat != 0) {
        float[] unit = Geometry3d.getForTwoBondAngles(
            new float[]{x2[0] - x1[0], x2[1] - x1[1], x2[2] - x1[2]},
            new float[]{x3[0] - x1[0], x3[1] - x1[1], x3[2] - x1[2]},
            a.getAlpha() * DEGREES_TO_RADIANS, a.getBeta() * DEGREES_TO_RADIANS, a.alternateZmatrixFormat);
        xyz[0] = x1[0] + a.getBondLength() * unit[0];
        xyz[1] = x1[1] + a.getBondLength() * unit[1];
        xyz[2] = x1[2] + a.getBondLength() * unit[2];
        a.setXYZ(xyz);
        continue;
      }

      //SIN2 = Math.sin( (double) (a.xyz[1] * DEGREES_TO_RADIANS));
      //COS2 = Math.cos( (double) (a.xyz[1] * DEGREES_TO_RADIANS));
      //SIN3 = Math.sin( (double) (a.xyz[2] * DEGREES_TO_RADIANS));
      //COS3 = Math.cos( (double) (a.xyz[2] * DEGREES_TO_RADIANS));
      SIN2 = Math.sin((double) (a.getAlpha() * DEGREES_TO_RADIANS));
      COS2 = Math.cos((double) (a.getAlpha() * DEGREES_TO_RADIANS));
      SIN3 = Math.sin((double) (a.getBeta() * DEGREES_TO_RADIANS));
      COS3 = Math.cos((double) (a.getBeta() * DEGREES_TO_RADIANS));

      VT[0] = a.getBondLength() * factor * COS2;
      VT[1] = a.getBondLength() * factor * SIN2 * SIN3;
      VT[2] = a.getBondLength() * factor * SIN2 * COS3;

      V1[0] = x3[0] - x2[0];
      V1[1] = x3[1] - x2[1];
      V1[2] = x3[2] - x2[2];

      V2[0] = x2[0] - x1[0];
      V2[1] = x2[1] - x1[1];
      V2[2] = x2[2] - x1[2];

      R2 = Math.sqrt(V2[0] * V2[0] + V2[1] * V2[1] + V2[2] * V2[2]);

      V3[0] = V1[1] * V2[2] - V1[2] * V2[1];
      V3[1] = V1[2] * V2[0] - V1[0] * V2[2];
      V3[2] = V1[0] * V2[1] - V1[1] * V2[0];

      R3 = Math.sqrt(V3[0] * V3[0] + V3[1] * V3[1] + V3[2] * V3[2]);

      V2[0] = V2[0] / R2;
      V2[1] = V2[1] / R2;
      V2[2] = V2[2] / R2;

      V3[0] = V3[0] / R3;
      V3[1] = V3[1] / R3;
      V3[2] = V3[2] / R3;

      V[0] = V2[1] * V3[2] - V2[2] * V3[1];
      V[1] = V2[2] * V3[0] - V2[0] * V3[2];
      V[2] = V2[0] * V3[1] - V2[1] * V3[0];

      VA1 = V2[0] * VT[0] + V3[0] * VT[1] + V[0] * VT[2];
      VB1 = V2[1] * VT[0] + V3[1] * VT[1] + V[1] * VT[2];
      VC1 = V2[2] * VT[0] + V3[2] * VT[1] + V[2] * VT[2];

      xyz[0] = x1[0] + (float) VA1;
      xyz[1] = x1[1] + (float) VB1;
      xyz[2] = x1[2] + (float) VC1;

      //cartesians.add(xyz);
      a.setXYZ(xyz);
    }
  }

  private int getCardSequence() throws Exception {
    int cardSequence = UNIQUE;

    if (!currentVars.containsKey(controlSection)) {
      return cardSequence;
    }

    Map controlGroup = currentVars.get(controlSection);

    if (controlGroup.containsKey("COORD")) {
      String value = (String) controlGroup.get("COORD");
      value = value.toUpperCase();
      if (value.equals("UNIQUE")) {
        cardSequence = UNIQUE;
      } else if (value.equals("HINT")) {
        cardSequence = HINT;
      } else if (value.equals("CART")) {
        cardSequence = CART;
      } else if (value.equals("ZMT")) {
        cardSequence = ZMT;
      } else if (value.equals("ZMTMPC")) {
        cardSequence = ZMTMPC;
      } else {
        throw new Exception("Unknown card sequence: COORD=" + value);
      }
    }
    return cardSequence;
  }

  public static int getAtomicNumber(String symbol) {
    byte[] chars = symbol.getBytes();
    String atom = "";

    if (symbol.length() == 1) {
      return ChemicalElements.getAtomicNumber(symbol);
    } else if (chars.length > 1 && chars[1] >= '0' && chars[1] <= '9') {
      atom = symbol.substring(0, 1);
    } else {
      atom = symbol.substring(0, 2);
    }

    return ChemicalElements.getAtomicNumber(atom);
  }

  public int validFormatScore(BufferedReader in) throws Exception {
    Gamess g = new Gamess();
    try {
      g.parseData(in);
    } catch (Exception ex) {
      return 0;
    }

    if (g.getMolecule() == null || g.getMolecule().getNumberOfAtoms() < 1) {
      return 0;
    }
    return 10;
  }

  public void parseData(BufferedReader in) throws Exception {
    MoleculeInterface mol = getMoleculeInterface();
    this.addMolecule(mol);

    String line;
    ignoreDataSection = false;

    String currentSection = "";
    String currentSwitch = "";
    Map currentOptions = null;
    Map references = null;
    boolean readingSection = false;
    boolean readingSwitch = false;

    // --- Start to parse
    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));

      while ((line = in.readLine()) != null) {
        line = line.trim();

        if (line.length() < 1) {
          continue;
        }

        if (line.startsWith("!")) { // Comment
          continue;
        }

        StringTokenizer st = new StringTokenizer(line, " \t=");

        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if (!readingSection && !token.startsWith("$")) { // Looking for a new section
            continue;
          } else if (!readingSection && token.equalsIgnoreCase(endSection)) { // Found (unexpectedly) end section
            System.err.println("Unexpectedly found $END section...");
            readingSection = false;
            readingSwitch = false;
            //throw new Exception("Unexpectedly found $END section...");
            continue;
          } else if (!readingSection && token.startsWith("$")) { // Found a new section
            token = token.toUpperCase();
            logger.info("Starting to read section " + token);
            if (!validSections.contains(token)) {
              logger.warning("Unknown section " + token);
              references = null;
            } else {
              references = sectionVarsReference.get(token);
            }

            // -- Special case for some sections...
            if (token.equalsIgnoreCase(dataSection) && (!ignoreDataSection)) {
              this.parseDataSection(in);
              break;
            }

            if (token.equalsIgnoreCase(fmoxyzSection)) {
              ignoreDataSection = true;
              this.readFMOXYZSection(in);
            }

            readingSwitch = true;
            currentSection = token;
            if (currentVars.containsKey(currentSection)) {
              currentOptions = currentVars.get(currentSection);
            } else {
              currentOptions = new HashMap();
            }
            readingSection = true;
            continue;
          } else if (readingSection && token.equalsIgnoreCase(endSection) && !readingSwitch) { // Found end section
            logger.warning("Unexpected end of section " + currentSection + " while expecting value for switch " + currentSwitch);
            currentOptions.put(currentSection, "");
            currentVars.put(currentSection, currentOptions);
            printSection(currentSection, currentOptions);
            currentSection = "";
            readingSection = false;
          } else if (readingSection && token.equalsIgnoreCase(endSection)) { // Found end section
            logger.info("Finished to read section " + currentSection);
            currentVars.put(currentSection, currentOptions);
            printSection(currentSection, currentOptions);
            currentSection = "";
            readingSection = false;
          } else if (readingSection && readingSwitch) { // Reading switch
            token = token.toUpperCase();
            if (references != null && !references.containsKey(token)) {
              logger.warning("Unknown switch: " + token);
            }
            currentSwitch = token;
            readingSwitch = false;
          } else if (readingSection && !readingSwitch) { // Reading value
            if (references != null) {
              GamessSwitch refSwitch = (GamessSwitch) references.get(currentSwitch);
              if (refSwitch == null || (!refSwitch.isValidValue(token))) {
                logger.warning("Check value " + token + " for switch " + currentSwitch);
              }
            }
            currentOptions.put(currentSwitch, token);
            readingSwitch = true;
          }

        }

      }
    } catch (IOException e) {
      logger.severe("Error parsing gamess input: " + e.getMessage());
      throw e;
    }

    // --- Build molecule 
    getMolecularInterface(mol);
    Molecule.guessCovalentBonds(mol);
    Molecule.guessAtomTypes(mol, AtomInterface.CCT_ATOM_TYPE, CCTAtomTypes.getElementMapping());
  }
}
