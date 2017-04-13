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

import cct.Constants;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

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
public class GaussianKeywords {

  public static final int JOB_UNKNOWN = 0;

  static final int KEYWORD_UNKNOWN = 0;
  static final int KEYWORD_JOB_TYPE = 1;
  static final int KEYWORD_METHOD = 2;
  static final int KEYWORD_BASIS_SET = 3;
  static final int KEYWORD_GEOM = 4;

  static final int WAVEFUNCTION_R = 0;
  static final int WAVEFUNCTION_U = 1;
  static final int WAVEFUNCTION_RO = 2;

  static final Map jobTypes = new HashMap();
  static final Map allKeywords = new HashMap();
  static final Set freqOptions = new HashSet();
  static final Set geomOptions = new HashSet();

  protected int Wavefunction_type = WAVEFUNCTION_R;
  protected String Method = "HF";
  protected String jobType = "SP";
  protected String basisSet = "STO-3G";
  protected String Options = null;
  protected double distanceUnits = 1.0;

  protected boolean skipTitleSection = false;
  protected boolean skipChargeSection = false;
  protected boolean skipMoleculeSpecification = false;
  protected boolean isONIOM = false;
  static final Logger logger = Logger.getLogger(GaussianKeywords.class.getCanonicalName());

  static {
    final int JOB_SINGLE_POINT_ENERGY = 1;
    final int JOB_GEOM_OPTIMIZATION = 2;
    final int JOB_FREQUENCY_ANALYSIS = 3;
    final int JOB_REACTION_PATH = 4;
    final int JOB_MAXIMUM_ENERGY = 5;
    final int JOB_SURFACE_SCAN = 6;
    final int JOB_POLARIZABILITIES = 7;
    final int JOB_DYNAMICS_ADMP = 8;
    final int JOB_DYNAMICS_BOMD = 9;
    final int JOB_COMPUTE_FORCES = 10;
    final int JOB_TEST_WAVEFUNCTION = 11;
    final int JOB_COMPUTE_VOLUME = 12;
    final int JOB_RECOMPUTE_POP_ANALYSIS = 13;
    final int JOB_INITIAL_GUESS = 14;
    final int JOB_EXTRACT_ARCHIVE = 15;

    // 1) Not all MM options are here
    // 2) no Frozen Core Options

    // --- Method keywords
    allKeywords.put("AM1", new Integer(KEYWORD_METHOD));
    allKeywords.put("BD", new Integer(KEYWORD_METHOD));
    allKeywords.put("CASSCF", new Integer(KEYWORD_METHOD));
    allKeywords.put("CAS", new Integer(KEYWORD_METHOD));
    allKeywords.put("CBS-4M", new Integer(KEYWORD_METHOD));
    allKeywords.put("CBS-LQ", new Integer(KEYWORD_METHOD));
    allKeywords.put("CBS-Q", new Integer(KEYWORD_METHOD));
    allKeywords.put("CBS-QB3", new Integer(KEYWORD_METHOD));
    allKeywords.put("CBS-APNO", new Integer(KEYWORD_METHOD));
    allKeywords.put("CC", new Integer(KEYWORD_METHOD));
    allKeywords.put("CCD", new Integer(KEYWORD_METHOD));
    allKeywords.put("CCSD", new Integer(KEYWORD_METHOD));
    allKeywords.put("CIS", new Integer(KEYWORD_METHOD));
    allKeywords.put("CNDO", new Integer(KEYWORD_METHOD));
    allKeywords.put("DFT", new Integer(KEYWORD_METHOD));
    allKeywords.put("G1", new Integer(KEYWORD_METHOD));
    allKeywords.put("G2", new Integer(KEYWORD_METHOD));
    allKeywords.put("G2MP2", new Integer(KEYWORD_METHOD));
    allKeywords.put("G3", new Integer(KEYWORD_METHOD));
    allKeywords.put("G3MP2", new Integer(KEYWORD_METHOD));
    allKeywords.put("G3B3", new Integer(KEYWORD_METHOD));
    allKeywords.put("G3MP2B3", new Integer(KEYWORD_METHOD));
    allKeywords.put("GVB", new Integer(KEYWORD_METHOD));
    allKeywords.put("HF", new Integer(KEYWORD_METHOD));
    allKeywords.put("INDO", new Integer(KEYWORD_METHOD));
    allKeywords.put("MINDO3", new Integer(KEYWORD_METHOD));
    allKeywords.put("MNDO", new Integer(KEYWORD_METHOD));
    allKeywords.put("MP2", new Integer(KEYWORD_METHOD));
    allKeywords.put("MP3", new Integer(KEYWORD_METHOD));
    allKeywords.put("MP4", new Integer(KEYWORD_METHOD));
    allKeywords.put("MP5", new Integer(KEYWORD_METHOD));
    allKeywords.put("ONIOM", new Integer(KEYWORD_METHOD));
    allKeywords.put("OVGF", new Integer(KEYWORD_METHOD));
    allKeywords.put("PM3", new Integer(KEYWORD_METHOD));
    allKeywords.put("QCID", new Integer(KEYWORD_METHOD));
    allKeywords.put("QCISD", new Integer(KEYWORD_METHOD));
    allKeywords.put("TD", new Integer(KEYWORD_METHOD));
    allKeywords.put("W1U", new Integer(KEYWORD_METHOD));
    allKeywords.put("W1BD", new Integer(KEYWORD_METHOD));
    allKeywords.put("ZINDO", new Integer(KEYWORD_METHOD));

    // --- Basis set (AUG -?)
    allKeywords.put("STO-3G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("3-21G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("6-21G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("4-31G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("6-31G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("6-311G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("6-311+G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("MC-311G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("D95V", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("D95", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SHC", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SEC", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CEP-4G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CEP-31G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CEP-121G", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("LANL2MB", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("LANL2DZ", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SDD", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SDDALL", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CC-PVDZ", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CC-PVTZ", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CC-PVQZ", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CC-PV5Z", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("CC-PV6Z", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SV", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("SVP", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("TZV", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("TZVP", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("MIDIX", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("EPR-II", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("EPR-III", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("UGBS", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("UGBS1P", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("UGBS2P", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("UGBS3P", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("MTSMALL", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("DGDZVP", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("DGDZVP2", new Integer(KEYWORD_BASIS_SET));
    allKeywords.put("DGTZVP", new Integer(KEYWORD_BASIS_SET));

    // --- Job types
    allKeywords.put("SP", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("OPT", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("FREQ", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("IRC", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("IRCMAX", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("SCAN", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("POLAR", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("ADMP", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("BOMD", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("FORCE", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("STABLE", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("VOLUME", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("DENSITY", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("GUESS", new Integer(KEYWORD_JOB_TYPE));
    allKeywords.put("REARCHIVE", new Integer(KEYWORD_JOB_TYPE));

    allKeywords.put("#", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("ARCHIVE", new Integer(KEYWORD_UNKNOWN));

    allKeywords.put("CHARGE", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("CPHF", new Integer(KEYWORD_UNKNOWN));
    // --- MM Options
    allKeywords.put("AMBER", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("DREIDING", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("GEOM", new Integer(KEYWORD_GEOM));
    allKeywords.put("UFF", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("QEQ", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("UNTYPED", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("UNCHARGED", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("HARDFIRST", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("SOFTFIRST", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("SOFTONLY", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("CHKPARAMETERS", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("NEWPARAMETERS", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("MODIFY", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("FIRSTEQUIV", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("LASTEQUIV", new Integer(KEYWORD_UNKNOWN));

    allKeywords.put("FREQ", new Integer(KEYWORD_UNKNOWN));

    allKeywords.put("ARCHIVE", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("ARCHIVE", new Integer(KEYWORD_UNKNOWN));
    allKeywords.put("ARCHIVE", new Integer(KEYWORD_UNKNOWN));

    // --- Job Types
    jobTypes.put("SP", new Integer(JOB_SINGLE_POINT_ENERGY));
    jobTypes.put("OPT", new Integer(JOB_GEOM_OPTIMIZATION));
    jobTypes.put("FREQ", new Integer(JOB_FREQUENCY_ANALYSIS));
    jobTypes.put("IRC", new Integer(JOB_REACTION_PATH));
    jobTypes.put("IRCMAX", new Integer(JOB_MAXIMUM_ENERGY));
    jobTypes.put("SCAN", new Integer(JOB_SURFACE_SCAN));
    jobTypes.put("POLAR", new Integer(JOB_POLARIZABILITIES));
    jobTypes.put("ADMP", new Integer(JOB_DYNAMICS_ADMP));
    jobTypes.put("BOMD", new Integer(JOB_DYNAMICS_BOMD));
    jobTypes.put("FORCE", new Integer(JOB_COMPUTE_FORCES));
    jobTypes.put("STABLE", new Integer(JOB_TEST_WAVEFUNCTION));
    jobTypes.put("VOLUME", new Integer(JOB_COMPUTE_VOLUME));
    jobTypes.put("DENSITY", new Integer(JOB_RECOMPUTE_POP_ANALYSIS));
    jobTypes.put("GUESS", new Integer(JOB_INITIAL_GUESS));
    jobTypes.put("REARCHIVE", new Integer(JOB_EXTRACT_ARCHIVE));

    // --- frequency options
    freqOptions.add("VCD");
    freqOptions.add("RAMAN");
    freqOptions.add("NRAMAN");
    freqOptions.add("NNRAMAN");
    freqOptions.add("NORAMAN");
    freqOptions.add("VIBROT");
    freqOptions.add("ANHARMONIC");
    freqOptions.add("READANHARM");
    freqOptions.add("READFC");
    freqOptions.add("HPMODES");
    freqOptions.add("INTERNALMODES");
    freqOptions.add("ANALYTIC");
    freqOptions.add("NUMERICAL");
    freqOptions.add("ENONLY");
    freqOptions.add("CUBIC");
    freqOptions.add("STEP");
    freqOptions.add("RESTART");
    freqOptions.add("PROJECTED");
    freqOptions.add("HINDEREDROTOR");
    freqOptions.add("MODREDUNDANT");
    freqOptions.add("READISOTOPES");

    // GEOM options
    geomOptions.add("CHECKPOINT");
    geomOptions.add("ALLCHECK");
    geomOptions.add("STEP");
    geomOptions.add("MODREDUNDANT");
    geomOptions.add("MODIFY");
    geomOptions.add("CONNECT");
    geomOptions.add("MODCONNECT");
    geomOptions.add("ZMCONNECT");
    geomOptions.add("IHARMONIC");
    geomOptions.add("CHKHARMONIC");
    geomOptions.add("READHARMONIC");
    geomOptions.add("OLDREDUNDANT");
    geomOptions.add("DISTANCE");
    geomOptions.add("NODISTANCE");
  }

  //***********************************************************
   public void addOptions(String opt) {
     if (opt == null) {
       return;
     }
     if (Options != null) {
       Options += " ";
       Options += opt;
     }
     else {
       Options = opt;
     }
   }

  //public    Wavefunction_type = WAVEFUNCTION_R;
  public String getMethod() {
    return Method;
  }

  public String getJobType() {
    return jobType;
  }

  public String getBasisSet() {
    return basisSet;
  }

  public String getOptions() {
    return Options;
  }

  //***********************************************************
   public void parseOptions() {
     String opts = Options.toUpperCase();
     StringTokenizer subtok, tokens = new StringTokenizer(opts, " ,/");
     String token, option, RO, subtoken;
     Integer keyword_type;
     boolean suboptions = false, one_option = false, many_options = false;

     if (opts.matches(".*?UNITS\\s*?[=]\\s*?AU.*?")) {
       distanceUnits = Constants.ONE_BOHR;
     }
     
     logger.info("Starting Parsing keywords...");
     while (tokens.hasMoreTokens()) {
       token = tokens.nextToken();
       if (token == null) {
         return;
       }
       logger.info("Parsing token: " + token);

       /*
                     if ( one_option ) {
           logger.info("Parsing token: " + token + " - assuming it's the only option");
           one_option = false;
           continue;
                     }
        */
       if (many_options) {
         if (token.indexOf(')') != -1) {
           logger.info("      - last in many options");
           many_options = false;
         }
         else {
           logger.info("      - continue to skip options");
         }
         continue;
       }

       // --- Try to determine wheather we have option(s) with a keyword
       suboptions = false;
       subtok = new StringTokenizer(token, "=(");
       subtoken = subtok.nextToken();

       // Try to determine number of options
       if (subtoken.length() != token.length()) {
         // keyword=(option) or keyword(option)
         if (token.indexOf('(') != -1 && token.indexOf(')') != -1) {
           one_option = true;
         }
         // keyword=(option,
         else if (token.indexOf('(') != -1) {
           many_options = true;
         }
         // keyword=option
         else if (token.indexOf('=') != -1) {
           one_option = true;
         }

       }

       if (token.startsWith("ONIOM")) {
         isONIOM = true;
         continue;
       }

       // --- Go through all keywords
       Set set = allKeywords.entrySet();
       Iterator iter = set.iterator();
       while (iter.hasNext()) {
         Map.Entry me = (Map.Entry) iter.next();
         option = me.getKey().toString();
         keyword_type = (Integer) me.getValue();

         if (token.startsWith(option) &&
             subtoken.compareTo(option) == 0) { // We got a keyword
           logger.info("Find keyword: " + subtoken);
           if (keyword_type.intValue() == KEYWORD_METHOD) {
             Method = subtoken;
           }
           else if (keyword_type.intValue() == KEYWORD_BASIS_SET) {
             basisSet = subtoken;
           }
           else if (keyword_type.intValue() == KEYWORD_JOB_TYPE) {
             jobType = subtoken;
           }
           else if (keyword_type.intValue() == KEYWORD_GEOM) {
             parseGEOMKeyword(token, tokens);
           }

         }
         else if (keyword_type.intValue() == KEYWORD_METHOD) {
           if (subtoken.startsWith("R")) {
             RO = "R" + option;
             if (subtoken.compareTo(RO) == 0) {
               logger.info("Find keyword: " + subtoken);
               Method = subtoken;
               Wavefunction_type = WAVEFUNCTION_R;
             }
           }
           else if (subtoken.startsWith("U")) {
             RO = "U" + option;
             if (subtoken.compareTo(RO) == 0) {
               logger.info("Find keyword: " + subtoken);
               Method = subtoken;
               Wavefunction_type = WAVEFUNCTION_U;
             }
           }
           else if (subtoken.startsWith("RO")) {
             RO = "RO" + option;
             if (subtoken.compareTo(RO) == 0) {
               logger.info("Find keyword: " + subtoken);
               Method = subtoken;
               Wavefunction_type = WAVEFUNCTION_RO;
             }
           }
         }
       } // --- End of while ( iter.hasNext() )

     }

   }

  /**
   * Returns true in the case of success, false otherwise
   * @param token String
   * @param tokens StringTokenizer
   * @return boolean
   */
  public boolean parseGEOMKeyword(String token, StringTokenizer tokens) {
    if ( (token = stripKeyword(token, "GEOM")) == null) {
      return false;
    }

    boolean manyOptions = false;
    String option = getOption(token);
    logger.info("parseGEOMKeyword: Parsing token: " + token);

    if (option.startsWith("CHECKPOINT") || option.startsWith("CHECK")) {
      skipMoleculeSpecification = true;
      logger.info("parseGEOMKeyword: Found option: " + option);
    }
    else if (option.startsWith("ALLCHECK")) {
      skipMoleculeSpecification = true;
      skipTitleSection = true;
      skipChargeSection = true;
      logger.info("parseGEOMKeyword: Found option: " + option);
    }
    else if (option.startsWith("MODIFY")) {
      skipMoleculeSpecification = true;
      logger.info("parseGEOMKeyword: Found option: " + option);
    }

    if (lastOption(token, manyOptions)) {
      return true;
    }
    else {
      manyOptions = true;
    }

    // If we are here we have many options...

    while (tokens.hasMoreTokens()) {
      token = tokens.nextToken();
      if (token == null) {
        return false;
      }
      logger.info("parseGEOMKeyword: Parsing token: " + token);

      option = getOption(token);
      logger.info("parseGEOMKeyword: Parsing token: " + token);

      if (option.startsWith("CHECKPOINT") || option.startsWith("CHECK")) {
        skipMoleculeSpecification = true;
        logger.info("parseGEOMKeyword: Found option: " + option);
      }
      else if (option.startsWith("ALLCHECK")) {
        skipMoleculeSpecification = true;
        skipTitleSection = true;
        skipChargeSection = true;
        logger.info("parseGEOMKeyword: Found option: " + option);
      }
      else if (option.startsWith("MODIFY")) {
        skipMoleculeSpecification = true;
        logger.info("parseGEOMKeyword: Found option: " + option);
      }

      if (lastOption(token, manyOptions)) {
        return true;
      }

    }

    return true;
  }

  /**
   * Strips a keyword and returns the rest of the string
   *
   * @param keyword String
   * @return String
   */
  String stripKeyword(String str, String keyword) {
    if (str.length() <= keyword.length()) {
      logger.info("stripKeyword: ERROR: cannot strip keyword " +
                         keyword + "from string " + str);
      return null;
    }
    else if (str.startsWith(keyword)) {
      return str.substring(keyword.length());
    }
    logger.info("stripKeyword: ERROR: string " + str +
                       " does not start from keyword " + keyword);
    return null;
  }

  String getOption(String str) {
    if (str == null) {
      return null;
    }
    String option = str;
    if (str.startsWith("=")) {
      option = option.substring(1);
    }
    if (str.startsWith("(")) {
      option = option.substring(1);
    }
    return option;
  }

  boolean lastOption(String option, boolean manyOptions) {
    if (option == null) {
      logger.info("lastOption: ERROR: trying to test zero string");
      return true;
    }

    // keyword=(option) or keyword(option)
    if (option.indexOf('(') != -1 && option.indexOf(')') != -1) {
      return true;
    }
    // keyword=(option, or keyword(option,
    else if (option.indexOf('(') != -1) {
      return false;
    }
    // ,option)
    else if (option.indexOf(')') != -1) {
      return true;
    }

    // keyword=option
    else if (option.startsWith("=") && (!manyOptions)) {
      return true;
    }

    return false;
  }

  public boolean isSkipTitleSection() {
    return skipTitleSection;
  }

  public boolean isSkipChargeSection() {
    return skipChargeSection;
  }

  public boolean isSkipMoleculeSpecification() {
    return skipMoleculeSpecification;
  }

  public boolean isONIOMCalculation() {
    return isONIOM;
  }
  
  public double getDistanceUnits() {
    return distanceUnits;
  }

  public void setDistanceUnits(double distanceUnits) {
    this.distanceUnits = distanceUnits;
  }
}
