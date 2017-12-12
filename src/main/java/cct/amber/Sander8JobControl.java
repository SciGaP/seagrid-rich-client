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
package cct.amber;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import cct.tools.FortranNamelist;

public class Sander8JobControl {

  FortranNamelist cntrl_namelist = new FortranNamelist();
  String ControlData = "mdin";
  String OutpusFile = "mdout";
  String LatestEnergyInfo = "mdinfo";
  String MolecularTopology = "prmtop";
  String InitialCoord = "inpcrd";
  String PositionRestraints = "refc";
  String CoordOverTrajectory = "mdcrd";
  String VelocitiesOverTrajectory = "mdvel";
  String EnergyOverTrajectory = "mden";
  String Restart = "restrt";
  String PolarDipoleInput = "inpdip";
  String PolarDipoleOutput = "rstdip";
  String ProtonationStateDefs = "cpin";
  String ProtonationStateDefsRestart = "cprestrt";
  String ProtonationStateOverTraj = "cpout";
  String MMTSB_Server = "mmtsb_setup.job";
  String Message = null;
  static final public int TYPE_INTEGER = 0;
  static final public int TYPE_FLOAT = 1;
  static final public int TYPE_STRING = 2;
  static final public int TYPE_UNDEFINED = 3;
  int numberOfAtoms = 0;
  Map FileUsage = new HashMap();
  static final Logger logger = Logger.getLogger(Sander8JobControl.class.getCanonicalName());
  // --- Some most common options
  // They set to their default values according to Sander-8 Manual
  int IMIN = 0; // Flag to run minimization
  int NTB = 1; // Periodic boundary. If NTB .EQ. 0 then a boundary is NOT applied
  float CUT = 8.0f; // This is used to specify the nonbonded cutoff, in Angstroms.
  float SCNB = 2.0f; // 1-4 vdw interactions are divided by SCNB. Default 2.0.
  float SCEE = 1.2f; // 1-4 electrostatic interactions are divided by SCEE;
  int NSNB = 25; // Determines the frequency of nonbonded list updates when igb=0 and nbflag=0;
  int IPOL = 0; // Inclusion of polarizabilities in the force field
  int IGB = 0; // generalized Born solvation model
  int MAXCYC = 1; // The maximum number of cycles of minimization.
  int NCYC = 10; // If NTMIN is 1 then the method of minimization will be switched from steepest
  //descent to conjugate gradient after NCYC cycles.
  int NTMIN = 1; // Flag for the method of minimization.
  int NSTLIM = 1; // Number of MD-steps to be performed. Default 1.
  float DT = 0.001f; // The time step (psec).
  int NTT = 1; // Switch for temperature scaling
  float TEMP0 = 300.0f; // Reference temperature at which the system is to be kept, if ntt > 0.
  float TEMPI = 0.0f; // Initial temperature. For the initial dynamics run,
  float TAUTP = 1.0f; // Time constant, in ps, for heat bath coupling for the system, if ntt = 1.
  int NTP = 0; // Flag for constant pressure dynamics.
  float PRES0 = 1.0f; // Reference pressure (in units of bars, where 1 bar  1 atm)
  float TAUP = 1.0f; // Pressure relaxation time (in ps), when NTP > 0.
  int NTC = 1; // Flag for SHAKE to perform bond length constraints
  Map ImportantVars = new HashMap();
  static Map allVars = new HashMap();
  static Map ewaldVars = new HashMap();

  public Sander8JobControl() {
    FileUsage.put("-i", ControlData);
    FileUsage.put("-o", OutpusFile);
    // --- Default values for the most important variables
    ImportantVars.put("IMIN", new Integer(IMIN));
    ImportantVars.put("NTB", new Integer(NTB));
    ImportantVars.put("CUT", new Float(CUT));
    ImportantVars.put("SCNB", new Float(SCNB));
    ImportantVars.put("SCEE", new Float(SCEE));
    ImportantVars.put("NSNB", new Integer(NSNB));
    ImportantVars.put("IPOL", new Integer(IPOL));
    ImportantVars.put("IGB", new Integer(IGB));
    ImportantVars.put("MAXCYC", new Integer(MAXCYC));
    ImportantVars.put("NCYC", new Integer(NCYC));
    ImportantVars.put("NTMIN", new Integer(NTMIN));
    ImportantVars.put("NSTLIM", new Integer(NSTLIM));
    ImportantVars.put("DT", new Float(DT));
    ImportantVars.put("NTT", new Integer(NTT));
    ImportantVars.put("TEMP0", new Float(TEMP0));
    ImportantVars.put("TEMPI", new Float(TEMPI));
    ImportantVars.put("TAUTP", new Float(TAUTP));
    ImportantVars.put("NTP", new Integer(NTP));
    ImportantVars.put("PRES0", new Float(PRES0));
    ImportantVars.put("TAUP", new Float(TAUP));
    ImportantVars.put("NTC", new Integer(NTC));

    SanderVariable var;

    // IMIN
    var = new SanderVariable("Flag to run minimization", true);
    var.addValue(new SanderVariableValue("0",
            "No minimization (only do molecular dynamics)", true));
    var.addValue(new SanderVariableValue("1",
            "Perform minimization (and no molecular dynamics)", false));
    var.addValue(new SanderVariableValue("5",
            "Read in a trajectory for analysis", false));

    allVars.put("IMIN", var);

    // IREST
    var = new SanderVariable("Flag to restart the run.", true);
    var.addValue(new SanderVariableValue("0",
            "No effect", true));
    var.addValue(new SanderVariableValue("1",
            "Restart calculation. Requires velocities in coordinate input file", false));
    allVars.put("IREST", var);

    // NMROPT

    var = new SanderVariable("", true);
    var.addValue(new SanderVariableValue("0",
            "No nmr-type analysis will be done", true));
    var.addValue(new SanderVariableValue("1",
            "NOESY volume restraints or chemical shift restraints"
            + " will be read as well", false));
    var.addValue(new SanderVariableValue("2",
            "NOESY volume restraints or chemical shift restraints"
            + " will be read as well", false));
    allVars.put("NMROPT", var);

    // --- Energy minimization. ***********************

    // MAXCYC

    var = new SanderVariable(
            "The maximum number of cycles of minimization", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1",
            "", true));
    allVars.put("MAXCYC", var);

    // NCYC

    var = new SanderVariable(
            "If NTMIN is 1 then the method of minimization will be switched from steepest "
            + " descent to conjugate gradient after NCYC cycles", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("10",
            "", true));
    allVars.put("NCYC", var);

    // NTMIN

    var = new SanderVariable(
            "Flag for the method of minimization", true);
    var.addValue(new SanderVariableValue("0",
            "Full conjugate gradient minimization. The first 4 cycles are steepest"
            + " descent at the start of the run and after every nonbonded pairlist"
            + " update.", false));
    var.addValue(new SanderVariableValue("1",
            "For NCYC cycles the steepest descent method is used then conjugate"
            + " gradient is switched on", true));
    var.addValue(new SanderVariableValue("2",
            "Only the steepest descent method is used", false));
    var.addValue(new SanderVariableValue("3",
            "The XMIN method is used, see amber8/doc/lmod.pdf", false));
    var.addValue(new SanderVariableValue("4",
            "The LMOD method is used, see amber8/doc/lmod.pdf", false));
    allVars.put("NTMIN", var);

    // DX0

    var = new SanderVariable(
            "The initial step length. If the initial step length is big then the minimizer will"
            + " try to leap the energy surface and sometimes the first few cycles will give a"
            + " huge energy, howev er the minimizer is smart enough to adjust itself", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.01",
            "", true));
    allVars.put("DX0", var);

    // DRMS

    var = new SanderVariable(
            "The convergence criterion for the energy gradient: minimization will halt"
            + " when the root-mean-square of the Cartesian elements of the gradient is less"
            + " than DRMS. Default 1.0E-4 kcal/mole A�", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0E-4",
            "", true));
    allVars.put("DRMS", var);

    // --- Potential function options **************************

    // NTF

    var = new SanderVariable(
            "Force evaluation. Note: If SHAKE is used (see NTC), it is not necessary to "
            + "calculate forces for the constrained bonds", true);
    var.addValue(new SanderVariableValue("1",
            "Complete interaction is calculated", true));
    var.addValue(new SanderVariableValue("2",
            "Bond interactions involving H-atoms omitted (use with NTC=2)", false));
    var.addValue(new SanderVariableValue("3",
            "All the bond interactions are omitted (use with NTC=3)", false));
    var.addValue(new SanderVariableValue("4",
            "Angle involving H-atoms and all bonds are omitted", false));
    var.addValue(new SanderVariableValue("5",
            "All bond and angle interactions are omitted", false));
    var.addValue(new SanderVariableValue("6",
            "Dihedrals involving H-atoms and all bonds and all angle interactions are omitted", false));
    var.addValue(new SanderVariableValue("7",
            "All bond, angle and dihedral interactions are omitted", false));
    var.addValue(new SanderVariableValue("8",
            "All bond, angle, dihedral and non-bonded interactions are omitted", false));

    allVars.put("NTF", var);

    // NTB

    var = new SanderVariable(
            "Periodic boundary. If NTB .EQ. 0 then a boundary is NOT applied regardless"
            + " of any boundary condition information in the topology file. The value of NTB "
            + "specifies whether constant volume or constant pressure dynamics will be used", true);

    var.addValue(new SanderVariableValue("0",
            "No periodicity is applied and PME is off", false));
    var.addValue(new SanderVariableValue("1",
            "Constant volume", true));
    var.addValue(new SanderVariableValue("2",
            "Constant Pressure", false));

    allVars.put("NTB", var);

    // DIELC

    var = new SanderVariable(
            "Dielectric multiplicative constant for the electrostatic interactions. "
            + "Please note this is NOT related to dielectric constants for generalized "
            + "Born simulations.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0",
            "", true));
    allVars.put("DIELC", var);

    // CUT

    var = new SanderVariable(
            "This is used to specify the nonbonded cutoff, in Angstroms. For PME, the "
            + "cutoff is used to limit direct space sum, and the default value of 8.0 is usually "
            + "a good value. When igb>0, the cutoff is used to truncate nonbonded pairs (on "
            + "an atom-by-atom basis.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);

    var.addValue(new SanderVariableValue("8.0",
            "", true));
    allVars.put("CUT", var);

    // SCNB

    var = new SanderVariable(
            "1-4 vdw interactions are divided by SCNB", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);

    var.addValue(new SanderVariableValue("2.0", "", true));
    allVars.put("SCNB", var);

    // SCEE

    var = new SanderVariable(
            "1-4 electrostatic interactions are divided by SCEE; the 1991 and previous "
            + "force fields used 2.0, while the 1994 force field uses 1.2", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.2", "", true));
    allVars.put("SCEE", var);

    // NSNB

    var = new SanderVariable(
            "Determines the frequency of nonbonded list updates when igb=0 and "
            + "nbflag=0; see the description of nbflag for more information", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("25", "", true));
    allVars.put("NSNB", var);

    // IPOL

    var = new SanderVariable(
            "Inclusion of polarizabilities in the force field.\n= 0 non polar calc (default)\n"
            + "= 1 turn on polarization calculation. Polarizabilities must be present in prmtop.", true);

    var.addValue(new SanderVariableValue("0",
            "Nonpolar calc", true));
    var.addValue(new SanderVariableValue("1",
            "Polar calc", false));
    allVars.put("IPOL", var);

    // --- General flags describing the calculation

    // NTX

    var = new SanderVariable(
            "Option to read the initial coordinates, velocities and box size from the"
            + " \"inpcrd\" file. The options 1-2 must be used when one is starting from minimized"
            + " or model-built coordinates. If an MD restrt file is used as inpcrd, then"
            + "options 4-7 may be used.", true);

    var.addValue(new SanderVariableValue("1",
            "Formatted X", true));
    var.addValue(new SanderVariableValue("2",
            "Unformatted X", false));
    var.addValue(new SanderVariableValue("4",
            "Unformatted X and V", false));
    var.addValue(new SanderVariableValue("5",
            "Formatted X and V", false));
    var.addValue(new SanderVariableValue("6",
            "Unformatted X, V and BOX(1..3)", false));
    var.addValue(new SanderVariableValue("7",
            "Unformatted X, V and BOX(1..3)", false));
    allVars.put("NTX", var);

    // NTRX

    var = new SanderVariable(
            "Format of the Cartesian coordinates for restraint from file \"refc\". Note: the"
            + " program expects file \"refc\" to contain coordinates for all the atoms in the system."
            + " A subset for the actual restraints is selected by restraintmask in the control"
            + "namelist.", true);

    var.addValue(new SanderVariableValue("0",
            "Unformatted (binary) form)", false));
    var.addValue(new SanderVariableValue("1",
            "Formatted (ascii) form", true));
    allVars.put("NTRX", var);

    // --- Nature and format of the output.

    // NTXO

    var = new SanderVariable(
            "Format of the final coordinates, velocities, and box size (if constant volume or"
            + " pressure run) written to file \"restrt\".", true);

    var.addValue(new SanderVariableValue("0",
            "Unformatted)", false));
    var.addValue(new SanderVariableValue("1",
            "Formatted", true));
    allVars.put("NTXO", var);

    // NTPR

    var = new SanderVariable(
            "Every NTPR steps energy information will be printed in human-readable form"
            + " to files \"mdout\" and \"mdinfo\". \"mdinfo\" is closed and reopened each time, so"
            + " it always contains the most recent energy and temperature.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("50",
            "", true));
    allVars.put("NTPR", var);

    // NTAVE

    var = new SanderVariable(
            "Every NTAVE steps of dynamics, running averages of average energies and"
            + " fluctuations over the last NTAVE steps will be printed out. Default value of 0"
            + " disables this printout.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("NTAVE", var);

    // NTWR

    var = new SanderVariable(
            "Every NTWR steps during dynamics, the \"restrt\" file will be written, ensuring"
            + " that recovery from a crash will not be so painful. If"
            + " NTWR<0, a unique copy of the file, restrt_nstep, is written every abs(NTWR)"
            + "steps. Default 500.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("500",
            "", true));
    allVars.put("NTWR", var);

    // IWRAP

    var = new SanderVariable(
            "If set to 1, the coordinates written to the restart and trajectory files will be"
            + " \"wrapped\" into a primary box. This often makes the resulting structures"
            + " look better visually, but has no effect on the energy or forces. Performing"
            + " such wrapping, however, can mess up diffusion and other calculations. The"
            + " default (when iwrap=0) is to not perform any such manipulations", true);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "No \"Wrapping\" coordinates into a primary box", true));
    var.addValue(new SanderVariableValue("1",
            "\"Wrap\" coordinates into a primary box", false));
    allVars.put("IWRAP", var);

    // NTWX

    var = new SanderVariable(
            "Every NTWX steps the coordinates will be written to file \"mdcrd\". NTWX=0"
            + " inhibits all output. Default 0.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("NTWX", var);

    // NTWV

    var = new SanderVariable(
            "Every NTWV steps the velocities will be written to file \"mdvel\". NTWV=0"
            + " inhibits all output. Default 0.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("NTWV", var);

    // NTWE

    var = new SanderVariable(
            "Every NTWE steps the energies and temperatures will be written to file"
            + " \"mden\" in compact form. NTWE=0 inhibits all output. Default 0.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("NTWE", var);

    // IOUTFM - obsolete

    var = new SanderVariable(
            "Format of velocity, coordinate, and energy sets. Note: these values are \"backwards\""
            + " compared to NTRX and NTXO; this is an ancient mistake that we are"
            + " reluctant to change, since it would break existing scripts.", true);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "Formatted (default)", true));
    var.addValue(new SanderVariableValue("1",
            "Binary", false));
    allVars.put("IOUTFM", var);

    // NTWPRT

    var = new SanderVariable(
            "Coordinate/velocity archive limit flag. This flag can be used to decrease the"
            + " size of the coordinate / velocity archive files, by only including that portion of"
            + " the system of greatest interest. The Coord/velocity archives will include:\n"
            + "= 0 all atoms of the system (default)\n"
            + "> 0 only atoms 1->NTWPRT", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("NTWPRT", var);

    // IDECOMP

    var = new SanderVariable(
            "This option is only really useful in conjunction with mm_pbsa, where it is"
            + " turned on automatically if required.", true);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "Do nothing (default).", true));
    var.addValue(new SanderVariableValue("1",
            "Decompose energies on a per-residue basis; 1-4 EEL + 1-4 VDW are"
            + " added to internal (bond, angle, dihedral) energies.", false));
    var.addValue(new SanderVariableValue("2",
            "Decompose energies on a per-residue basis; 1-4 EEL + 1-4 VDW are"
            + " added to EEL and VDW.", false));
    var.addValue(new SanderVariableValue("3",
            "Decompose energies on a pairwise per-residue basis; the rest is equal"
            + " to \"1\".", false));
    var.addValue(new SanderVariableValue("4",
            "Decompose energies on a pairwise per-residue basis; the rest is"
            + " equal to \"2\".", false));
    allVars.put("IDECOMP", var);

    // --- Generalized Born/Surface Area options.

    // IGB

    var = new SanderVariable(
            "The generalized Born solvation model can be used instead of explicit water for non-polarizable"
            + " force fields such as ff94 or ff99. Users should understand that all"
            + " (current) GB models have limitations and should proceed with caution. Generalized Born simulations"
            + " can only be run for non-periodic systems The nonbonded cutoff for GB"
            + " calculations should be greater than that for PME calculations, perhaps cut=16.", true);

    var.addValue(new SanderVariableValue("0",
            "No GB", true));
    var.addValue(new SanderVariableValue("1",
            "Hawkins et all model", false));
    var.addValue(new SanderVariableValue("2",
            "Onufriev et all model", false));
    var.addValue(new SanderVariableValue("5",
            "Onufriev et all model II", false));
    var.addValue(new SanderVariableValue("10",
            "Poisson-Boltzmann solver", false));
    allVars.put("IGB", var);

    // INTDIEL

    var = new SanderVariable(
            "Sets the interior dielectric constant of the molecule of interest. Default is 1.0."
            + " Other values have not been extensively tested.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0",
            "", true));
    allVars.put("INTDIEL", var);

    // EXTDIEL

    var = new SanderVariable(
            "Sets the exterior or solvent dielectric constant. Default is 78.5.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("78.5",
            "", true));
    allVars.put("EXTDIEL", var);

    // SALTCON

    var = new SanderVariable(
            "Sets the concentration (M) of 1-1 mobile counterions in solution, using a"
            + " modified generalized Born theory based on the Debye-H�ckel limiting law for"
            + " ion screening of interactions. Default is 0.0 M (i.e. no Debye-H�ckel"
            + " screening.) Setting saltcon to a non-zero value does result in some increase in"
            + "computation time.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.0",
            "", true));
    allVars.put("SALTCON", var);

    // RGBMAX

    var = new SanderVariable(
            "This parameter controls the maximum distance between atom pairs that will"
            + " be considered in carrying out the pairwise summation involved in calculating"
            + " the effective Born radii. Atoms whose associated spheres are farther way than"
            + " rgbmax from given atom will not contribute to that atom�s effective Born"
            + " radius. The default is 25 �, which is usually plenty for single-domain proteins of a few hundred residues.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("25.0",
            "", true));
    allVars.put("RGBMAX", var);

    // RBORNSTAT

    var = new SanderVariable(
            "If rbornstat = 1, the statistics of the effective Born radii for each atom of the"
            + " molecule throughout the molecular dynamics simulation are reported in the"
            + " output file. Default is 0.", true);
    var.addValue(new SanderVariableValue("0",
            "No output of the statistics of the effective Born radii for each atom", true));
    var.addValue(new SanderVariableValue("1",
            "The statistics of the effective Born radii for each atom of the"
            + " molecule throughout the molecular dynamics simulation are reported in the"
            + " output file.", false));
    allVars.put("RBORNSTAT", var);

    // OFFSET

    var = new SanderVariable(
            "The dielectric radii for generalized Born calculations are decreased by a uniform"
            + " value \"offset\" to give the \"intrinsic radii\" used to obtain effective Born"
            + " radii. Default 0.09 �.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.09",
            "", true));
    allVars.put("OFFSET", var);

    // SURFTEN

    var = new SanderVariable(
            "Surface tension used to calculate the nonpolar contribution to the free energy"
            + " of solvation (when gbsa = 1), as Enp = surften*SA. The default is 0.005"
            + " kcal/mol-�2", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.005",
            "", true));
    allVars.put("SURFTEN", var);

    // RDT

    var = new SanderVariable(
            "This parameter is only used for GB simulations with LES (Locally Enhanced"
            + " Sampling). In GB+LES simulations, non-LES atoms require multiple effective"
            + " Born radii due to alternate descreening effects of different LES copies."
            + " Default 0.01 �.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.01",
            "", true));
    allVars.put("RDT", var);

    // GBSA

    var = new SanderVariable(
            "Option to carry out GB/SA (generalized Born/surface area) simulations. For"
            + " the default value of 0, surface area will not be computed and included in the"
            + " solvation term. If gbsa = 1, surface area will be computed using the LCPO"
            + " model. If gbsa = 2, surface area will be computed by recursively approximating"
            + " a sphere around an atom, starting from an icosahedra.", true);

    var.addValue(new SanderVariableValue("0",
            "No surface area will not be computed and included in the solvation term.", true));
    var.addValue(new SanderVariableValue("1",
            "Surface area will be computed using the LCPO model.", false));
    var.addValue(new SanderVariableValue("2",
            "Surface area will be computed by recursively approximating a sphere around an atom, starting from an icosahedra.", false));
    allVars.put("GBSA", var);

    // --- Frozen or restrained atoms.

    // --- Molecular dynamics.

    // NSTLIM

    var = new SanderVariable(
            "Number of MD-steps to be performed. Default 1.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1",
            "", true));
    allVars.put("NSTLIM", var);

    // NSCM

    var = new SanderVariable(
            "Flag for the removal of translational and rotational center-of-mass (COM)"
            + " motion at regular intervals. For non-periodic simulations, after every NSCM"
            + " steps, translational and rotational motion will be removed. For periodic systems,"
            + " just the translational center-of-mass motion will be removed. This flag"
            + " is ignored for belly simulations. Default 1000.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1000",
            "", true));
    allVars.put("NSCM", var);

    // T

    var = new SanderVariable(
            "The time at the start (psec) this is for your own reference and is not critical."
            + " Start time is taken from the coordinate input file if IREST=1. Default 0.0.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.0",
            "", true));
    allVars.put("T", var);

    // DT

    var = new SanderVariable(
            "The time step (psec). Recommended MAXIMUM is .002 if SHAKE is used,"
            + " or .001 if it isn�t. Note that for temperatures above 300K, the step size should"
            + " be reduced since greater temperatures mean increased velocities and longer"
            + " distance traveled between each force evaluation, which can lead to anomalously"
            + " high energies and system blowup. Default 0.001.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.001",
            "", true));
    allVars.put("DT", var);

    // NRESPA

    var = new SanderVariable(
            "This variable allows the user to evaluate slowly-varying terms in the force"
            + " field less frequently. If NRESPA>1 these slowly-varying forces are evaluated every nrespa steps."
            + " The forces are adjusted appropriately, leading to an impulse at that step. If"
            + " nrespa*dt is less than or equal to 4 fs the energy conservation is not seriously"
            + " compromised. However if nrespa*dt > 4 fs the simulation becomes less stable.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1",
            "", true));
    allVars.put("NRESPA", var);

    // --- Temperature regulation

    // NTT

    var = new SanderVariable(
            "Switch for temperature scaling. Note that setting ntt=0 corresponds to the"
            + " microcanonical (NVE) ensemble (which should approach the canonical one"
            + " for large numbers of degrees of freedom). Some aspects of the \"weak-coupling"
            + " ensemble\" (ntt=1) have been examined, and roughly interpolate between"
            + " the microcanonical and canonical ensembles. The ntt=2 and 3 options"
            + " correspond to the canonical (constant T) ensemble. The ntt=4 option is"
            + " included for historical reasons, but does not correspond to any of the traditional"
            + " ensembles.", true);

    var.addValue(new SanderVariableValue("0",
            "Constant total energy classical dynamics (assuming that ntb<2, as"
            + " should probably always be the case when ntt=0).", true));
    var.addValue(new SanderVariableValue("1",
            "Constant temperature, using the weak-coupling algorithm. A"
            + " single scaling factor is used for all atoms.", false));
    var.addValue(new SanderVariableValue("2",
            "Andersen temperature coupling scheme, in which imaginary"
            + " \"collisions\" randomize the velocities to a distribution corresponding"
            + " to temp0 every vrand steps.", false));
    var.addValue(new SanderVariableValue("3",
            "Use Langevin dynamics with the collision frequency gamma given by"
            + " gamma_ln. Note that when gamma has its default value"
            + " of zero, this is the same as setting ntt = 0.", false));

    allVars.put("NTT", var);

    // TEMP0

    var = new SanderVariable(
            "Reference temperature at which the system is to be kept, if ntt > 0. Note that"
            + " for temperatures above 300K, the step size should be reduced since increased"
            + " distance traveled between evaluations can lead to SHAKE and other problems."
            + " Default 300.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("300",
            "", true));
    allVars.put("TEMP0", var);

    // TEMP0LES

    var = new SanderVariable(
            "This is the target temperature for all LES particles. If"
            + " temp0les<0, a single temperature bath is used for all atoms, otherwise separate"
            + " thermostats are used for LES and non-LES particles. Default is -1, corresponding"
            + " to a single (weak-coupling) temperature bath.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("-1",
            "", true));
    allVars.put("TEMP0LES", var);

    // TEMPI

    var = new SanderVariable(
            "Initial temperature. For the initial dynamics run, (NTX .lt. 3) the velocities"
            + " are assigned from a Maxwellian distribution at TEMPI K. If TEMPI = 0.0,"
            + " the velocities will be calculated from the forces instead. TEMPI has no effect"
            + " if NTX .gt. 3. Default 0.0.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.0",
            "", true));
    allVars.put("TEMPI", var);

    // IG

    var = new SanderVariable(
            "The seed for the random number generator. The MD starting velocity is"
            + " dependent on the random number generator seed if NTX .lt. 3 .and. TEMPI"
            + " .ne. 0.0. Default 71277.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("71277",
            "", true));
    allVars.put("IG", var);

    // TAUTP

    var = new SanderVariable(
            "Time constant, in ps, for heat bath coupling for the system, if ntt = 1. Default"
            + " is 1.0. Generally, values for TAUTP should be in the range of 0.5-5.0 ps, with"
            + " a smaller value providing tighter coupling to the heat bath and, thus, faster"
            + " heating and a less natural trajectory. Smaller values of TAUTP result in"
            + " smaller fluctuations in kinetic energy, but larger fluctuations in the total"
            + " energy. Values much larger than the length of the simulation result in a return"
            + "to constant energy conditions.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0",
            "", true));
    allVars.put("TAUTP", var);

    // GAMMA_LN

    var = new SanderVariable(
            "The collision frequency gamma , in ps-1, when ntt = 3. A simple Leapfrog integrator"
            + " is used to propagate the dynamics, with the kinetic energy adjusted to be correct"
            + " for the harmonic oscillator case. Note that it is not necessary that"
            + " gamma approximate the physical collision frequency. In fact, it is often advantageous,"
            + " in terms of sampling or stability of integration, to use much smaller"
            + " values. Default is 0", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("GAMMA_LN", var);

    // VRAND

    var = new SanderVariable(
            "If vrand>0 and ntt=2, the velocities will be randomized to temperature"
            + " TEMP0 every vrand steps.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0",
            "", true));
    allVars.put("VRAND", var);

    // VLIMIT

    var = new SanderVariable(
            "If not equal to 0.0, then any component of the velocity that is greater than"
            + " abs(VLIMIT) will be reduced to VLIMIT (preserving the sign). This can be"
            + " used to avoid occasional instabilities in molecular dynamics runs. VLIMIT"
            + " should generally be set to a value like 20 (the default), which is well above the"
            + " most probable velocity in a Maxwell-Boltzmann distribution at room temperature.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("20",
            "", true));
    allVars.put("VLIMIT", var);

    // --- Pressure regulation.

    // NTP

    var = new SanderVariable(
            "Flag for constant pressure dynamics. This option should be set to 1 or 2 when"
            + " Constant Pressure periodic boundary conditions are used (NTB = 2).", true);

    var.addValue(new SanderVariableValue("0",
            "No pressure scaling", true));
    var.addValue(new SanderVariableValue("1",
            "Isotropic scaling", false));
    var.addValue(new SanderVariableValue("2",
            "MD with anisotropic (x-,y-,z-) pressure scaling: this should only be"
            + " used with orthogonal boxes (i.e. with all angles set to 90 degrees)."
            + " Anisotropic scaling is primarily intended for non-isotropic systems,"
            + " such as membrane simulations, where the surface tensions are different"
            + " in different directions; it is generally not appropriate for solutes"
            + " dissolved in water.", false));
    allVars.put("NTP", var);

    // PRES0

    var = new SanderVariable(
            "Reference pressure (in units of bars, where 1 bar � 1 atm) at which the system"
            + " is maintained ( when NTP > 0). Default 1.0.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1",
            "", true));
    allVars.put("PRES0", var);

    // COMP

    var = new SanderVariable(
            "Compressibility of the system when NTP > 0. The units are in 1.0E-06/bar; a"
            + " value of 44.6 (default) is appropriate for water.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("44.6",
            "", true));
    allVars.put("COMP", var);

    // TAUP

    var = new SanderVariable(
            "Pressure relaxation time (in ps), when NTP > 0. The recommended value is"
            + " between 1.0 and 5.0 psec. Default value is 1.0, but larger values may sometimes"
            + " be necessary (if your trajectories seem unstable).", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0",
            "", true));
    allVars.put("TAUP", var);

    // --- Frozen or restrained atoms.

    // IBELLY

    var = new SanderVariable(
            "Flag for belly type dynamics.", true);

    var.addValue(new SanderVariableValue("0",
            "No belly run (default).", true));
    var.addValue(new SanderVariableValue("1",
            "Belly run. A subset of the atoms in the system will be allowed to"
            + " move, and the coordinates of the rest will be frozen. The moving"
            + " atoms are specified bellymask. This option is not available when"
            + " igb>0. Note also that this option does not provide any significant"
            + " speed advantage, and is maintained primarily for backwards compatibility"
            + " with older version of Amber. Most applications should use the"
            + "ntr variable instead", false));
    allVars.put("IBELLY", var);

    // NTR

    var = new SanderVariable(
            "Flag for restraining specified atoms in Cartesian space using a harmonic"
            + " potential. The restrained atoms are determined by the restraintmask string."
            + " The force constant is given by restraint_wt. The coordinates are read in"
            + " \"restrt\" format from the \"refc\" file", true);

    var.addValue(new SanderVariableValue("0",
            "No position restraints (default)", true));
    var.addValue(new SanderVariableValue("1",
            "MD with restraint of specified atoms", false));
    allVars.put("NTR", var);

    // RESTRAINT_WT

    var = new SanderVariable(
            "The weight (in kcal/mol \u2212 �2) for the positional restraints. The restraint is of"
            + " the form k(\u0394x)2, where k is the value given by this variable, and \u0394x is the difference"
            + " between one of the Cartesian coordinates of a restrained atom and its"
            + " reference position. There is a term like this for each Cartesian coordinate of"
            + " each restrainted atom.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1.0",
            "", true));
    allVars.put("RESTRAINT_WT", var);

    // RESTRAINTMASK

    var = new SanderVariable(
            "String that specifies the restrained atoms when ntr=1.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue(" ",
            "", true));
    allVars.put("RESTRAINTMASK", var);

    // BELLYMASK

    var = new SanderVariable(
            "String that specifies the moving atoms when ibelly=1. Note that these mask"
            + " strings are limited to a maximum of 80 characters.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue(" ",
            "", true));
    allVars.put("BELLYMASK", var);

    // --- SHAKE bond length constraints.

    // NTC

    var = new SanderVariable(
            "Flag for SHAKE to perform bond length constraints. (See also NTF in"
            + " the Potential function section. In particular, typically NTF = NTC.) The"
            + " SHAKE option should be used for most MD calculations. Since SHAKE is an "
            + " algorithm based on dynamics, the minimizer is not aware of what SHAKE is doing;"
            + " for this reason, minimizations generally should be carried out without SHAKE."
            + " One exception is short minimizations whose purpose is to remove bad contacts"
            + " before dynamics can begin.", true);

    var.addValue(new SanderVariableValue("1",
            "SHAKE is not performed (default)", true));
    var.addValue(new SanderVariableValue("2",
            "Bonds involving hydrogen are constrained", false));
    var.addValue(new SanderVariableValue("3",
            "All bonds are constrained (not available for parallel runs in sander)", false));
    allVars.put("NTC", var);

    // TOL

    var = new SanderVariable(
            "Relative geometrical tolerance for coordinate resetting in shake. Recommended"
            + " maximum: <0.00005 Angstrom Default 0.00001.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.00001",
            "", true));
    allVars.put("TOL", var);

    // JFASTW

    var = new SanderVariable(
            "Fast water definition flag. By default, the system is searched for water"
            + " residues, and special routines are used to SHAKE these systems", true);

    var.addValue(new SanderVariableValue("0",
            "Normal operation. Waters are identified by the default names (given"
            + " below), unless they are redefined, as described below.", true));
    var.addValue(new SanderVariableValue("4",
            "Do not use the fast SHAKE routines for waters.", false));
    allVars.put("JFASTW", var);

    // WATNAM

    var = new SanderVariable(
            "The residue name the program expects for water. Default �WAT �.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue("WAT ",
            "", true));
    allVars.put("WATNAM", var);

    // OWTNM

    var = new SanderVariable(
            "The atom name the program expects for the oxygen of water. Default �O   �.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue("O   ",
            "", true));
    allVars.put("OWTNM", var);

    // HWTNM1

    var = new SanderVariable(
            "The atom name the program expects for the 1st H of water. Default �H1  �.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue("H1  ",
            "", true));
    allVars.put("HWTNM1", var);

    // HWTNM2

    var = new SanderVariable(
            "The atom name the program expects for the 2nd H of water. Default �H2  �.", false);
    var.setVarType(Sander8JobControl.TYPE_STRING);
    var.addValue(new SanderVariableValue("H2  ",
            "", true));
    allVars.put("HWTNM2", var);

    // --- Water cap.

    // IVCAP

    var = new SanderVariable(
            "Flag to control cap option. The \"cap\" refers to a spherical portion of water"
            + " centered on a point in the solute and restrained by a soft half-harmonic potential."
            + " For the best physical realism, this option should be combined with"
            + " igb=10, in order to include the reaction field of waters that are beyond the cap"
            + "radius.", true);

    var.addValue(new SanderVariableValue("0",
            "Cap will be in effect if it is in the prmtop file (default).", true));
    var.addValue(new SanderVariableValue("2",
            "Cap will be inactivated, even if parameters are present in the prmtop file.", false));
    allVars.put("IVCAP", var);

    // FCAP

    var = new SanderVariable(
            "The force constant for the cap restraint potential.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("1", "", true));
    allVars.put("FCAP", var);

    // Variables WHICH ARE NOT PARSED !!!

    // ISCALE

    var = new SanderVariable(
            "Number of additional variables to optimize beyond the 3N structural parameters."
            + " (Default = 0). At present, this is only used with residual dipolar coupling"
            + "restraints", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("0", "", true));
    allVars.put("ISCALE", var);

    // NOESKP --- !!! should be enumerated
    var = new SanderVariable(
            "The NOESY volumes will only be evaluated if mod(nstep, noeskp) = 0; otherwise"
            + " the last computed values for intensities and derivatives will be used."
            + " (default = 1, i.e. evaluate volumes at every step)", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1", "", true));
    allVars.put("NOESKP", var);

    // IPNLTY --- !!! should be enumerated
    var = new SanderVariable(
            "", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1", "", true));
    allVars.put("IPNLTY", var);

    // MXSUB

    var = new SanderVariable(
            "Maximum number of submolecules that will be used. This is used to determine"
            + " how much space to allocate for the NOESY calculations. Default 1.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("1", "", true));
    allVars.put("MXSUB", var);

    // SCALM

    var = new SanderVariable(
            "\"Mass\" for the additional scaling parameters. Right now they are restricted to"
            + " all have the same value. The larger this value, the slower these extra variables"
            + " will respond to their environment. Default 100 amu.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("100", "", true));
    allVars.put("SCALM", var);

    // PENCUT

    var = new SanderVariable(
            "In the summaries of the constraint deviations, entries will only be made if the"
            + " penalty for that term is greater than PENCUT. Default 0.1.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.1", "", true));
    allVars.put("PENCUT", var);

    // TAUSW

    var = new SanderVariable(
            "For noesy volume calculations (NMROPT = 2), intensities with mixing times"
            + " less that TAUSW (in seconds) will be computed using perturbation theory,"
            + " whereas those greater than TAUSW will use a more exact theory."
            + " To always use the \"exact\" intensities and"
            + " derivatives, set TAUSW = 0.0; to always use perturbation theory, set TAUSW"
            + " to a value larger than the largest mixing time in the input. Default is TAUSW"
            + " of 0.1 second, which should work pretty well for most systems.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.1", "", true));
    allVars.put("TAUSW", var);

    // ---- Variables for ewald namelist

    // ORDER

    var = new SanderVariable(
            "The order of the B-spline interpolation. The higher the order, the better the"
            + " accuracy (unless the charge grid is too coarse). The minimum order is 3. An"
            + " order of 4 (the default) implies a cubic spline approximation which is a good"
            + " standard value. Note that the cost of the PME goes as roughly the order to the"
            + " third power.", false);
    var.setVarType(Sander8JobControl.TYPE_INTEGER);
    var.addValue(new SanderVariableValue("4", "", true));
    ewaldVars.put("ORDER", var);

    // VERBOSE

    var = new SanderVariable(
            "Standard use is to have VERBOSE = 0. Setting VERBOSE to higher values"
            + " (up to a maximum of 3) leads to voluminous output of information about the"
            + " PME run.", true);

    var.addValue(new SanderVariableValue("0",
            "", true));
    var.addValue(new SanderVariableValue("1",
            "", false));
    var.addValue(new SanderVariableValue("2",
            "", false));
    var.addValue(new SanderVariableValue("3",
            "", false));
    ewaldVars.put("VERBOSE", var);

    // EW_TYPE

    var = new SanderVariable(
            "Standard use is to have EW_TYPE = 0 which turns on the particle mesh"
            + " ewald (PME) method. When EW_TYPE = 1, instead of the approximate,"
            + " interpolated PME, a regular Ewald calculation is run. The number of reciprocal"
            + " vectors used depends upon RSUM_TOL, or can be set by the user. The"
            + " exact Ewald summation is present mainly to serve as an accuracy check"
            + " allowing users to determine if the PME grid spacing, order and direct sum tolerance"
            + " lead to acceptable results.", true);

    var.addValue(new SanderVariableValue("0",
            "Turns on the particle mesh ewald (PME) method", true));
    var.addValue(new SanderVariableValue("1",
            "A regular Ewald calculation is run.", false));
    ewaldVars.put("EW_TYPE", var);

    // DSUM_TOL

    var = new SanderVariable(
            "This relates to the width of the direct sum part of the Ewald sum, requiring"
            + " that the value of the direct sum at the Lennard-Jones cutoff value (specified in"
            + " CUT as during standard dynamics) be less than DSUM_TOL. In practice it"
            + " has been found that the relative error in the Ewald forces (RMS) due to cutting"
            + " off the direct sum at CUT is between 10.0 and 50.0 times DSUM_TOL.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.00001", "", true));
    ewaldVars.put("DSUM_TOL", var);

    // RSUM_TOL

    var = new SanderVariable(
            "This serves as a way to generate the number of reciprocal vectors used in an"
            + " Ewald sum. Typically the relative RMS reciprocal sum error is about 5-10"
            + " times RSUM_TOL. Default is 5 x 10\u22125.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    var.addValue(new SanderVariableValue("0.00005", "", true));
    ewaldVars.put("RSUM_TOL", var);

    // --- MLIMIT(1,2,3) - !!! not implemented yet

    // EW_COEFF

    var = new SanderVariable(
            "Ewald coefficient, in �\u22121. Default is determined by dsum_tol and cutoff. If it"
            + " is explicitly inputed then that value is used, and dsum_tol is computed from"
            + " ew_coeff and cutoff.", false);
    var.setVarType(Sander8JobControl.TYPE_FLOAT);
    //var.addValue(new SanderVariableValue("0.00005", "", true));
    ewaldVars.put("EW_COEFF", var);

  }

  public float getCutoff() {
    Float f = (Float) ImportantVars.get("CUT");
    return f.floatValue();
  }

  public Map getAllVariablesInfo() {
    return allVars;
  }

  public int getNumberOfAtoms() {
    return numberOfAtoms;
  }

  public void setNumberOfAtoms(int n) {
    numberOfAtoms = n;
  }

  public int numberOfEnergySteps() {
    Integer i = (Integer) ImportantVars.get("NSTLIM");
    //logger.info("NSTLIM: "+i.toString());
    return i.intValue();
  }

  public String getGeneralParameters(String filename, int fileType) {
    String mess = null;

    if (fileType == 0) { // --- Read in from file
      mess = cntrl_namelist.findAndParseFortranNamelist(filename, "cntrl");
    } else if (fileType == 1) { // --- Read in from string bufer
      BufferedReader in = new BufferedReader(new StringReader(filename));
      //logger.info("Internal Text: "+filename);
      mess = cntrl_namelist.findAndParseFortranNamelist(in, "cntrl");
      //in.close();
      //logger.info("After reading text bufer: "+mess);
    } else {
      return "getGeneralParameters: Internal Error: Unknown file type";
    }

    if (mess.compareTo("Ok") != 0) {
      return mess;
    }
    Map vars = cntrl_namelist.getVariables();
    Set set = vars.entrySet();
    Iterator i = set.iterator();

    // --- Debug print out

    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      String key = (String) me.getKey();
      System.out.print(key + " : ");
      key = (String) me.getValue();
      logger.info(key);
    }

    // --- End of debug

    // Get the most common options

    set = ImportantVars.entrySet();
    i = set.iterator();
    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      String key = (String) me.getKey();
      if (!vars.containsKey(key)) {
        continue;
      }
      String value = (String) vars.get(key);
      Number num = (Number) me.getValue();
      // --- Parse integer value
      if (num instanceof Integer) {
        try {
          Integer ival = Integer.valueOf(value);
          ImportantVars.put(key, ival);
          logger.info("Key " + key + " is updated: "
                  + ival.toString());
        } catch (NumberFormatException e) {
          logger.info("Not integer value for " + key + " : "
                  + value);
          return "Not integer value for " + key + " : " + value;
        }
      } // Parse float value
      else if (num instanceof Float) {
        try {
          Float fval = Float.valueOf(value);
          ImportantVars.put(key, fval);
          logger.info("Key " + key + " is updated: "
                  + fval.toString());
        } catch (NumberFormatException e) {
          logger.info("Not float value for " + key + " : "
                  + value);
          return "Not float value for " + key + " : " + value;
        }
      }
    }

    return "Ok";
  }

  static public Map getGeneralParameters(String filename, int fileType,
          FortranNamelist namelist,
          String nm_name) {
    String mess = null;

    if (fileType == 0) { // --- Read in from file
      mess = namelist.findAndParseFortranNamelist(filename, nm_name);
    } else if (fileType == 1) { // --- Read in from string bufer
      BufferedReader in = new BufferedReader(new StringReader(filename));
      //logger.info("Internal Text: "+filename);
      mess = namelist.findAndParseFortranNamelist(in, nm_name);
      //in.close();
      //logger.info("After reading text bufer: "+mess);
    } else {
      logger.info(
              "getGeneralParameters: Internal Error: Unknown file type");
      return null;
    }

    if (mess.compareTo("Ok") != 0) {
      return null;
    }
    Map vars = namelist.getVariables();
    Set set = vars.entrySet();
    Iterator i = set.iterator();

    // --- Debug print out

    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      String key = (String) me.getKey();
      System.out.print(key + " : ");
      key = (String) me.getValue();
      logger.info(key);
    }
    return vars;
  }

  /**
   *
   * @param vars Map
   * @return boolean
   */
  public boolean setCntrlVariables(Map vars) {
    Message = "";
    boolean no_errors = true;

    Set set = vars.entrySet();
    Iterator i = set.iterator();
    while (i.hasNext()) {
      Map.Entry me = (Map.Entry) i.next();
      String key = me.getKey().toString();
      if (!allVars.containsKey(key)) {
        no_errors = false;
        Message += "Error: Unknown variable: " + key
                + " in cntrl namelist\n";
        continue;
      }

      String value = me.getValue().toString();

      logger.info("Setting " + key + " = " + value);

      SanderVariable variable = (SanderVariable) allVars.get(key);

      String mess = variable.setValue(value);
      if (mess != null) {
        Message += "Error: " + key + " " + mess + "\n";
        no_errors = false;
      }

    }
    return no_errors;
  }

  public String getErrorMessage() {
    return Message;
  }

  /**
   *
   * @param namelistVars Map
   * @param s8jcVars Map
   * @return String
   */
  public static String updateNamelistVariables(Map namelistVars, Map s8jcVars) {
    SanderVariable var;
    String message = "";

    Set set = s8jcVars.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String option = me.getKey().toString();
      var = (SanderVariable) me.getValue();

      // If it's default value we remove it from the namelist
      if (var.isDefaultValue()) {
        if (namelistVars.containsKey(option)) {
          namelistVars.remove(option);
        }
      } else {
        namelistVars.put(option, var.getValue());
      }
    }

    // -- Now check again all namelist variables and remove those which has
    // no mapping

    Map newNamelistVars = new HashMap();

    set = namelistVars.entrySet();
    iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String option = me.getKey().toString();

      if (s8jcVars.containsKey(option)) {
        newNamelistVars.put(option, me.getValue().toString());
        continue;
      }
      message += "Warning: No variable " + option
              + " in Sander-8 job control list\n";
      //namelistVars.remove(option);
    }

    namelistVars.clear();
    namelistVars.putAll(newNamelistVars);

    if (message.length() == 0) {
      return null;
    }
    return message;
  }
}
