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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import cct.tools.FileFilterImpl;
import java.util.HashMap;
import java.util.Properties;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MolecularFileFormats {

  public static final String extension_All_Formats = "*";
  public static final String extension_ADF_Input = "in;inp;dat;run";
  public static final String extension_ADF_Output = "out;log";
  public static final String extension_G03_GJF = "gjf;com;inp;dat;in;g03";
  public static final String extension_G03_Output = "log;out";
  public static final String extension_G03_Cube = "cub;cube";
  public static final String extension_G03_Fragment = "frg";
  public static final String extension_G03_Trajectory = "trj;7;log;out";
  public static final String extension_G03_FormCheckpoint = "fchk;fch";
  public static final String extension_GAMESS_Input = "inp";
  public static final String extension_GAMESS_Output = "out;log";
  public static final String extension_MOPAC_Input = "dat;inp;mop";
  public static final String extension_MOPAC_Output = "out";
  public static final String extension_MOPAC_Log = "log";
  public static final String extension_PDB = "pdb;ent;Z";
  public static final String extension_Geom = "gf";
  public static final String extension_VRML = "wrl";
  public static final String extension_Mol2 = "mol2";
  public static final String extension_CCT = "cct";
  public static final String extension_XMol_XYZ = "xyz";
  public static final String extension_MDL_Molfile = "mol";
  public static final String extension_GRO = "gro";
  public static final String extension_Prmtop = "top;prmtop";
  public static final String extension_Prep = "in;prepin;prep";
  public static final String extension_AmberLib = "off;lib";
  public static final String extension_Ambercrd = "crd;rst;inpcrd";
  public static final String extension_AmberTraj = "crd;rst;mdcrd";
  public static final String extension_Povray = "pov";
  public static final String extension_VASP_POSCAR = "dat;inp;poscar";
  public static final String extension_VASP_Vasprun = "xml";
  public static final String extension_QChem_Input = "in";
  public static final String extension_QChem_Output = "out;log";
  public static final String extension_GULP_Input = "in;inp;gin;grs";
  public static final String extension_GULP_Trajectory = "trg;trj";
  public static final String extension_SIESTA_Input = "fdf;dat;inp;in";
  public static final String extension_CPMD_Output = "out";
  public static final String extension_CPMD_Trajectory = "xyz";
  public static final String allFormats = "All Chemistry Format Files";
  public static final String adfInput = "ADF Input";
  public static final String adfOutput = "ADF Output";
  public static final String gaussian03GJF = "Gaussian G03 Input";
  public static final String gaussian03Output = "Gaussian G03 Output";
  public static final String gaussian03Cube = "Geometry from G03 cube";
  public static final String gaussianFragment = "Gaussian G03 Fragment";
  public static final String gaussianTrajectory = "Gaussian G03 Trajectory";
  public static final String gaussianFormCheckpoint = "Gaussian G03 Formatted Checkpoint";
  public static final String gamessInput = "GAMESS Input";
  public static final String gamessOutput = "GAMESS Output";
  public static final String geomFormat = "Geom Format File";
  public static final String vrmlFormat = "VRML Format File";
  public static final String mopacInput = "Mopac Input File";
  public static final String mopacLog = "Mopac2002 Log File";
  public static final String mopacOutput = "Mopac Output File";
  public static final String pdbFile = "PDB";
  public static final String triposMol2 = "Tripos Mol2";
  public static final String cctFileFormat = "CCT File";
  public static final String xmolXYZFileFormat = "XMol XYZ File";
  public static final String mdlMolfileFormat = "MDL Molfile File";
  public static final String gromacsGROFormat = "Gromacs coordinate file";
  public static final String amberPrmtopFormat = "Amber prmtop file";
  public static final String amberPrepFormat = "Amber PREP file";
  public static final String amberLibFormat = "Amber Lib file";
  public static final String amberCoordFormat = "Amber coordinate/restart file";
  public static final String amberMdcrdFormat = "Amber trajectory file";
  public static final String povrayFormat = "Povray file";
  public static final String vaspPoscarFormat = "VASP Poscar file";
  public static final String vaspVasprunFormat = "VASP Vasprun file";
  public static final String qchemFormat = "QChem Input file";
  public static final String qchemOutputFormat = "QChem Output file";
  public static final String gulpInputFormat = "GULP Input file";
  public static final String gulpTrajectoryFormat = "GULP Trajectory file";
  public static final String siestaInputFormat = "SIESTA Input file (in development)";
  public static final String CPMDOutputFormat = "CPMD Output file (in development)";
  public static final String CPMDTrajectoryFormat = "CPMD MD Trajectory file (in development)";
  public static final int formatUnknown = 0;
  public static final int formatG03_GJF = 1;
  public static final int formatG03_Output = 2;
  public static final int formatPDB = 3;
  public static final int formatTripos_Mol2 = 4;
  public static final int formatCCT = 5;
  public static final int formatG03_Fragment = 6;
  public static final int format_XMol_XYZ = 7;
  public static final int format_MDL_Molfile = 8;
  public static final int format_GRO = 9;
  public static final int format_Prmtop = 10;
  public static final int format_AmberCrd = 11;
  public static final int format_GAMESS_Input = 12;
  public static final int formatG03_Cube = 13;
  public static final int format_GAMESS_Output = 14;
  public static final int format_MOPAC_Log = 15;
  public static final int format_MOPAC_Output = 16;
  public static final int format_Povray = 17;
  public static final int format_VASP_Poscar = 18;
  public static final int format_QChem_Input = 19;
  public static final int format_QChem_Output = 20;
  public static final int format_MOPAC_Input = 21;
  public static final int format_ADF_Input = 22;
  public static final int format_G03_Trajectory = 23;
  public static final int format_GULP_Input = 24;
  public static final int format_GULP_Trajectory = 25;
  public static final int format_VASP_Vasprun = 26;
  public static final int format_ADF_Output = 27;
  public static final int format_SIESTA_Input = 28;
  public static final int format_G03_FormCheckpoint = 29;
  public static final int format_CPMD_Output = 30;
  public static final int format_CPMD_Trajectory = 31;
  private static Map<CHEMISTRY_FILE_FORMAT, String> openFileExtensions = new LinkedHashMap<CHEMISTRY_FILE_FORMAT, String>();
  private static Map<CHEMISTRY_FILE_FORMAT, String> openFileFormats = new LinkedHashMap<CHEMISTRY_FILE_FORMAT, String>();
  public static final Map readFormats = new LinkedHashMap();
  public static final Map<CHEMISTRY_FILE_FORMAT, String> remoteReadFormats = new LinkedHashMap<CHEMISTRY_FILE_FORMAT, String>();
  public static final Map<CHEMISTRY_FILE_FORMAT, String> commonReadFormats = new LinkedHashMap<CHEMISTRY_FILE_FORMAT, String>();
  public static final Map<CHEMISTRY_FILE_FORMAT, String> remoteReadFormatDescription = new LinkedHashMap<CHEMISTRY_FILE_FORMAT, String>();
  private static Map<CHEMISTRY_FILE_FORMAT, String> commonFileFormatDescription = new HashMap<CHEMISTRY_FILE_FORMAT, String>();
  static final Map readFormatsTypes = new LinkedHashMap();
  static final Map saveFormats = new LinkedHashMap();
  static final Map updateCoordFormats = new LinkedHashMap();
  protected static final Map<TRAJECTORY_FILE_FORMAT, String> trajectoryReadFormats = new LinkedHashMap<TRAJECTORY_FILE_FORMAT, String>();
  protected static final Map<TRAJECTORY_FILE_FORMAT, String> trajectoryReadFormatDescription = new LinkedHashMap<TRAJECTORY_FILE_FORMAT, String>();
  // --- General case - all Molecular file formats
  public static final String allMolecularFormats = "Molecular File Formats";
  public static final String allMolecularExtensions =
          "gjf;log;out;pdb;ent;mol2;cct;mol";

  static {
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_GJF, extension_G03_GJF);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, extension_G03_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, extension_G03_Cube);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_FRAGMENT, extension_G03_Fragment);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, extension_G03_Trajectory);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.G03_FORM_CHECKPOINT, extension_G03_FormCheckpoint);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, extension_GAMESS_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, extension_GAMESS_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, extension_MOPAC_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, extension_MOPAC_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, extension_MOPAC_Log);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.ADF_INPUT, extension_ADF_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.ADF_OUTPUT, extension_ADF_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.PDB, extension_PDB);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.MOL2, extension_Mol2);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.CCT, extension_CCT);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, extension_XMol_XYZ);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, extension_MDL_Molfile);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, extension_GRO);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.AMBER_PRMTOP, extension_Prmtop);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.AMBER_PREP, extension_Prep);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.AMBER_LIB, extension_AmberLib);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.SIESTA_INPUT, extension_SIESTA_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.VASP_POSCAR, extension_VASP_POSCAR);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.VASP_VASPRUN, extension_VASP_Vasprun);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, extension_QChem_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, extension_QChem_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, extension_GULP_Input);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, extension_CPMD_Output);
    openFileExtensions.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, extension_CPMD_Trajectory);


    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_GJF, extension_G03_GJF);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, extension_G03_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, extension_G03_Cube);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_FRAGMENT, extension_G03_Fragment);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, extension_G03_Trajectory);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.G03_FORM_CHECKPOINT, extension_G03_FormCheckpoint);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, extension_GAMESS_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, extension_GAMESS_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, extension_MOPAC_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, extension_MOPAC_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, extension_MOPAC_Log);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.ADF_INPUT, extension_ADF_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.ADF_OUTPUT, extension_ADF_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.PDB, extension_PDB);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.MOL2, extension_Mol2);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.CCT, extension_CCT);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, extension_XMol_XYZ);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, extension_MDL_Molfile);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, extension_GRO);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.AMBER_PRMTOP, extension_Prmtop);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.AMBER_PREP, extension_Prep);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.AMBER_LIB, extension_AmberLib);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.SIESTA_INPUT, extension_SIESTA_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.VASP_POSCAR, extension_VASP_POSCAR);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.VASP_VASPRUN, extension_VASP_Vasprun);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, extension_QChem_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, extension_QChem_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, extension_GULP_Input);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, extension_CPMD_Output);
    openFileFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, extension_CPMD_Trajectory);
  }

  static {
    readFormats.put(gaussian03GJF, extension_G03_GJF);
    readFormats.put(gaussian03Output, extension_G03_Output);
    readFormats.put(gaussian03Cube, extension_G03_Cube);
    readFormats.put(gaussianFragment, extension_G03_Fragment);
    readFormats.put(gaussianTrajectory, extension_G03_Trajectory);
    readFormats.put(gaussianFormCheckpoint, extension_G03_FormCheckpoint);
    readFormats.put(gamessInput, extension_GAMESS_Input);
    readFormats.put(gamessOutput, extension_GAMESS_Output);
    readFormats.put(mopacInput, extension_MOPAC_Input);
    readFormats.put(mopacOutput, extension_MOPAC_Output);
    readFormats.put(mopacLog, extension_MOPAC_Log);
    readFormats.put(adfInput, extension_ADF_Input);
    readFormats.put(adfOutput, extension_ADF_Output);
    readFormats.put(pdbFile, extension_PDB);
    readFormats.put(triposMol2, extension_Mol2);
    readFormats.put(cctFileFormat, extension_CCT);
    readFormats.put(xmolXYZFileFormat, extension_XMol_XYZ);
    readFormats.put(mdlMolfileFormat, extension_MDL_Molfile);
    readFormats.put(gromacsGROFormat, extension_GRO);
    readFormats.put(amberPrmtopFormat, extension_Prmtop);
    readFormats.put(amberPrepFormat, extension_Prep);
    readFormats.put(amberLibFormat, extension_AmberLib);
    readFormats.put(siestaInputFormat, extension_SIESTA_Input);
    readFormats.put(vaspPoscarFormat, extension_VASP_POSCAR);
    readFormats.put(vaspVasprunFormat, extension_VASP_Vasprun);
    readFormats.put(qchemFormat, extension_QChem_Input);
    readFormats.put(qchemOutputFormat, extension_QChem_Output);
    readFormats.put(gulpInputFormat, extension_GULP_Input);
    readFormats.put(CPMDOutputFormat, extension_CPMD_Output);
    readFormats.put(CPMDTrajectoryFormat, extension_CPMD_Trajectory);

    //remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.ALL_FORMATS, extension_All_Formats);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_GJF, extension_G03_GJF);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, extension_G03_Output);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, extension_G03_Cube);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, extension_G03_Trajectory);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, extension_GAMESS_Input);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, extension_GAMESS_Output);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, extension_MOPAC_Input);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, extension_MOPAC_Output);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, extension_MOPAC_Log);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.PDB, extension_PDB);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.MOL2, extension_Mol2);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.CCT, extension_CCT);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, extension_XMol_XYZ);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, extension_MDL_Molfile);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, extension_GRO);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, extension_QChem_Input);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, extension_QChem_Output);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, extension_GULP_Input);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.VASP_VASPRUN, extension_VASP_Vasprun);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, extension_CPMD_Output);
    remoteReadFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, extension_CPMD_Trajectory);

    //remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.ALL_FORMATS, allFormats);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_GJF, gaussian03GJF);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, gaussian03Output);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, gaussian03Cube);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, gaussianTrajectory);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, gamessInput);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, gamessOutput);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, mopacInput);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, mopacOutput);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, mopacLog);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.PDB, pdbFile);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOL2, triposMol2);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.CCT, cctFileFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, xmolXYZFileFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, mdlMolfileFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, gromacsGROFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, qchemFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, qchemOutputFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, gulpInputFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.VASP_VASPRUN, vaspVasprunFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, CPMDOutputFormat);
    remoteReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, CPMDTrajectoryFormat);

    //commonReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_GJF, extension_G03_GJF);
    //commonReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, extension_G03_Output);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, extension_G03_Cube);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, extension_G03_Trajectory);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, extension_GAMESS_Input);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, extension_GAMESS_Output);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, extension_MOPAC_Input);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, extension_MOPAC_Output);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, extension_MOPAC_Log);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.PDB, extension_PDB);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.MOL2, extension_Mol2);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.CCT, extension_CCT);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, extension_XMol_XYZ);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, extension_MDL_Molfile);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, extension_GRO);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, extension_QChem_Input);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, extension_QChem_Output);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, extension_GULP_Input);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, extension_CPMD_Output);
    commonReadFormats.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, extension_CPMD_Trajectory);

    //commonReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_GJF, gaussian03GJF);
    //commonReadFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_OUTPUT, gaussian03Output);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_CUBE, gaussian03Cube);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.G03_TRAJECTORY, gaussianTrajectory);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.GAMESS_INPUT, gamessInput);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.GAMESS_OUTPUT, gamessOutput);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC_INPUT, mopacInput);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC_OUTPUT, mopacOutput);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOPAC2002_LOG, mopacLog);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.PDB, pdbFile);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.MOL2, triposMol2);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.CCT, cctFileFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.XMOL_XYZ, xmolXYZFileFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.MDL_MOLFILE, mdlMolfileFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.GROMACS_GRO, gromacsGROFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.QCHEM_INPUT, qchemFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.QCHEM_OUTPUT, qchemOutputFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.GULP_INPUT, gulpInputFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.CPMD_OUTPUT, CPMDOutputFormat);
    commonFileFormatDescription.put(CHEMISTRY_FILE_FORMAT.CPMD_TRAJECTORY, CPMDTrajectoryFormat);

    readFormatsTypes.put(adfInput, new Integer(format_ADF_Input));
    readFormatsTypes.put(adfOutput, new Integer(format_ADF_Output));
    readFormatsTypes.put(gaussian03GJF, new Integer(formatG03_GJF));
    readFormatsTypes.put(gaussian03Output, new Integer(formatG03_Output));
    readFormatsTypes.put(gaussian03Cube, new Integer(formatG03_Cube));
    readFormatsTypes.put(gaussianFragment, new Integer(formatG03_Fragment));
    readFormatsTypes.put(gaussianTrajectory, new Integer(format_G03_Trajectory));
    readFormatsTypes.put(gaussianFormCheckpoint, new Integer(format_G03_FormCheckpoint));
    readFormatsTypes.put(gamessInput, new Integer(format_GAMESS_Input));
    readFormatsTypes.put(gamessOutput, new Integer(format_GAMESS_Output));
    readFormatsTypes.put(mopacInput, new Integer(format_MOPAC_Input));
    readFormatsTypes.put(mopacOutput, new Integer(format_MOPAC_Output));
    readFormatsTypes.put(mopacLog, new Integer(format_MOPAC_Log));
    readFormatsTypes.put(pdbFile, new Integer(formatPDB));
    readFormatsTypes.put(triposMol2, new Integer(formatTripos_Mol2));
    readFormatsTypes.put(cctFileFormat, new Integer(formatCCT));
    readFormatsTypes.put(xmolXYZFileFormat, new Integer(format_XMol_XYZ));
    readFormatsTypes.put(mdlMolfileFormat, new Integer(format_MDL_Molfile));
    readFormatsTypes.put(gromacsGROFormat, new Integer(format_GRO));
    readFormatsTypes.put(amberPrmtopFormat, new Integer(format_Prmtop));
    readFormatsTypes.put(siestaInputFormat, new Integer(format_SIESTA_Input));
    readFormatsTypes.put(vaspPoscarFormat, new Integer(format_VASP_Poscar));
    readFormatsTypes.put(vaspVasprunFormat, new Integer(format_VASP_Vasprun));
    readFormatsTypes.put(qchemFormat, new Integer(format_QChem_Input));
    readFormatsTypes.put(qchemOutputFormat, new Integer(format_QChem_Output));
    readFormatsTypes.put(gulpInputFormat, new Integer(format_GULP_Input));
    readFormatsTypes.put(CPMDOutputFormat, new Integer(format_CPMD_Output));
    readFormatsTypes.put(CPMDTrajectoryFormat, new Integer(format_CPMD_Trajectory));

    saveFormats.put(pdbFile, extension_PDB);
    saveFormats.put(triposMol2, extension_Mol2);
    saveFormats.put(xmolXYZFileFormat, extension_XMol_XYZ);
    saveFormats.put(gaussian03GJF, extension_G03_GJF);
    saveFormats.put(siestaInputFormat, extension_SIESTA_Input);
    saveFormats.put(cctFileFormat, extension_CCT);
    saveFormats.put(povrayFormat, extension_Povray);
    saveFormats.put(geomFormat, extension_Geom);
    saveFormats.put(vrmlFormat, extension_VRML);

    updateCoordFormats.put(amberCoordFormat, extension_Ambercrd);
    updateCoordFormats.put(pdbFile, extension_PDB);

    // --- File descriptions for trajectory extensions

    trajectoryReadFormats.put(TRAJECTORY_FILE_FORMAT.AMBER, extension_AmberTraj);
    trajectoryReadFormats.put(TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY, extension_G03_Trajectory);
    trajectoryReadFormats.put(TRAJECTORY_FILE_FORMAT.GULP_TRAJECTORY, extension_GULP_Trajectory);

    trajectoryReadFormatDescription.put(TRAJECTORY_FILE_FORMAT.AMBER, amberMdcrdFormat);
    trajectoryReadFormatDescription.put(TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY, gaussianTrajectory);
    trajectoryReadFormatDescription.put(TRAJECTORY_FILE_FORMAT.GULP_TRAJECTORY, gulpTrajectoryFormat);
  }

  private MolecularFileFormats() {
    Properties cctProperties;

  }

  /**
   * Currently very simple implementation for guessing molecular file format based on file extension
   * @param fileName String
   * @param Ext String
   * @return int
   */
  public static int guessMolecularFileFormat(String fileName, String Ext) {

    Set set = readFormats.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String description = me.getKey().toString();
      String ext = me.getValue().toString();

      String temp[] = ext.split(";");
      for (int i = 0; i < temp.length; i++) {
        if (Ext.equalsIgnoreCase(temp[i])) {

          if (!readFormatsTypes.containsKey(description)) {
            System.err.println(
                    "INTERNAL ERROR: guessMolecularFileFormat: file type is not set"
                    + description);
            return formatUnknown;
          }

          Integer type = (Integer) readFormatsTypes.get(description);
          return type.intValue();
        }
      }
    }

    if (extension_G03_GJF.contains(Ext)) {
      return formatG03_GJF;
    } else if (extension_G03_Output.contains(Ext)) {
      return formatG03_Output;
    } else if (extension_PDB.contains(Ext)) {
      return formatPDB;
    } else if (extension_Mol2.contains(Ext)) {
      return formatTripos_Mol2;
    } else if (extension_G03_Fragment.contains(Ext)) {
      return formatG03_Fragment;
    } else if (extension_CCT.contains(Ext)) {
      return formatCCT;
    } else if (extension_XMol_XYZ.contains(Ext)) {
      return format_XMol_XYZ;
    } else if (extension_MDL_Molfile.contains(Ext)) {
      return format_MDL_Molfile;
    } else if (extension_GRO.contains(Ext)) {
      return format_GRO;
    } else if (extension_Prmtop.contains(Ext)) {
      return format_Prmtop;
    }

    return formatUnknown;
  }

  public static Map getUpdateCoordFormats() {
    Map temp = new LinkedHashMap(updateCoordFormats);
    return temp;
  }

  @Deprecated
  public static Map getReadCoordFormats() {
    Map temp = new LinkedHashMap(readFormats);
    //LinkedHashMap temp = (LinkedHashMap)readFormats.clone();
    return temp;
  }

  public static Map<CHEMISTRY_FILE_FORMAT, String> getOpenFileFormats() {
    Map<CHEMISTRY_FILE_FORMAT, String> temp = new LinkedHashMap(openFileExtensions);
    //LinkedHashMap temp = (LinkedHashMap)readFormats.clone();
    return temp;
  }

  public static Map getSaveCoordFormats() {
    Map temp = new LinkedHashMap(saveFormats);
    return temp;
  }

  public static String getOpenFileExtensions(CHEMISTRY_FILE_FORMAT format) {
    return openFileExtensions.get(format);
  }

  public static String getFileFormatDescription(CHEMISTRY_FILE_FORMAT format) {
    return commonFileFormatDescription.get(format);
  }

  public static int getReadFileType(String description) {
    if (readFormatsTypes.containsKey(description)) {
      Integer type = (Integer) readFormatsTypes.get(description);
      return type.intValue();
    }
    return formatUnknown;
  }

  public static Map<TRAJECTORY_FILE_FORMAT, String> getTrajectoryReadFormats() {
    return new LinkedHashMap<TRAJECTORY_FILE_FORMAT, String>(trajectoryReadFormats);
  }

  public static Map<TRAJECTORY_FILE_FORMAT, String> getTrajectoryReadFormatDescription() {
    return new LinkedHashMap<TRAJECTORY_FILE_FORMAT, String>(trajectoryReadFormatDescription);
  }

  public static FileFilterImpl[] formTrajectoryFileFilters() {
    Map<TRAJECTORY_FILE_FORMAT, String> trReadFormats = MolecularFileFormats.getTrajectoryReadFormats();
    Map<TRAJECTORY_FILE_FORMAT, String> trReadFormatsDescr = MolecularFileFormats.getTrajectoryReadFormatDescription();
    int count = 0;
    FileFilterImpl[] filter = new FileFilterImpl[trReadFormats.size()];

    //filter[count] = new FileFilterImpl();
    //filter[count].setDescription("All files");
    //++count;
    Set set = trReadFormats.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      TRAJECTORY_FILE_FORMAT format = (TRAJECTORY_FILE_FORMAT) me.getKey();
      String extensions = trReadFormats.get(format);
      String temp[] = extensions.split(";");
      filter[count] = new FileFilterImpl();
      String descr = trReadFormatsDescr.get(format);

      for (int i = 0; i < temp.length; i++) {
        if (i == 0) {
          descr += " (";
        } else {
          descr += ";";
        }
        filter[count].addExtension(temp[i]);
        descr += "*." + temp[i];
        if (i == temp.length - 1) {
          descr += ")";
        }
      }
      filter[count].setDescription(descr);
      ++count;
    }

    return filter;
  }

  public static Map<String, TRAJECTORY_FILE_FORMAT> formTrajectoryFileRef() {
    Map<TRAJECTORY_FILE_FORMAT, String> trReadFormats = MolecularFileFormats.getTrajectoryReadFormats();
    Map<TRAJECTORY_FILE_FORMAT, String> trReadFormatsDescr = MolecularFileFormats.getTrajectoryReadFormatDescription();

    Map<String, TRAJECTORY_FILE_FORMAT> reference = new LinkedHashMap<String, TRAJECTORY_FILE_FORMAT>(trReadFormats.size());

    Set set = trReadFormats.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      TRAJECTORY_FILE_FORMAT format = (TRAJECTORY_FILE_FORMAT) me.getKey();
      String extensions = trReadFormats.get(format);
      String temp[] = extensions.split(";");
      String descr = trReadFormatsDescr.get(format);

      for (int i = 0; i < temp.length; i++) {
        if (i == 0) {
          descr += " (";
        } else {
          descr += ";";
        }
        descr += "*." + temp[i];
        if (i == temp.length - 1) {
          descr += ")";
        }
      }
      reference.put(descr, format);
    }

    return reference;
  }
}
