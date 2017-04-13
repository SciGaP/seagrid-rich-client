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

package cct.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class FortranNamelist {
  static final int READY_FOR_NEW_OPTION = 0;
  static final int WAITING_FOR_VALUE = 1;
  protected Map Vars = null;
  String Name = null;
  String Key = null, Value = null;
  int Status = READY_FOR_NEW_OPTION;
  static final Logger logger = Logger.getLogger(FortranNamelist.class.getCanonicalName());

  public FortranNamelist() {
  }

  void clear() {
    if (Vars == null) {
      Vars = new HashMap();
    }
    if (!Vars.isEmpty()) {
      Vars.clear();
    }
    if (Name != null) {
      Name = null;
    }
    Status = READY_FOR_NEW_OPTION;
  }

  public Map getVariables() {
    return Vars;
  }

  /**
   *
   * @param filename String
   * @param nm_name String
   * @return String
   */
  public String findAndParseFortranNamelist(String filename, String nm_name) {
    clear();
    String line, bufer;
    String namelist = "&" + nm_name.toUpperCase();
    logger.info("Looking for namelist: " + namelist);
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      // --- Look for beginning of namelist input
      while ( (line = in.readLine()) != null) {
        //line.trim();
        bufer = (line.trim()).toUpperCase();
        logger.info("Reading line: #" + bufer + "#");
        if (bufer.compareToIgnoreCase(namelist) == 0) {
          String message = parseFortranNamelist(in, bufer);
          in.close();
          return message;
        }

      } // --- End of while
      in.close();

    }
    catch (IOException e) {
      String error = "parseFortranNamelist: " + e.getMessage();
      logger.info(error);
      return error;
    }

    return "Ok";
  }

  /**
   *
   * @param in BufferedReader
   * @param nm_name String
   * @return String
   */
  public String findAndParseFortranNamelist(BufferedReader in, String nm_name) {
    clear();
    String line, bufer;
    String namelist = "&" + nm_name.toUpperCase();
    try {
      // --- Look for end of namelist input
      while ( (line = in.readLine()) != null) {
        bufer = (line.trim()).toUpperCase();
        logger.info("Reading line: #" + bufer + "#");
        if (bufer.startsWith(namelist)) {
          return parseFortranNamelist(in, bufer);
        }

      } // --- End of while

    }
    catch (IOException e) {
      String error = "parseFortranNamelist: " + e.getMessage();
      logger.info(error);
      return error;
    }

    return "Ok";
  }

  /**
   *
   * @param filename String
   * @return String
   */
  public String parseFortranNamelist(String filename) {
    clear();
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      // --- Look for beginning of namelist input
      while ( (line = in.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("&") || line.startsWith("$")) {
          return parseFortranNamelist(in, line);
        }

      } // --- End of while

    }
    catch (IOException e) {
      String error = "parseFortranNamelist: " + e.getMessage();
      logger.info(error);
      return error;
    }

    return "Ok";
  }

  /**
   *
   * @param in BufferedReader
   * @param line String
   * @return String
   */
  public String parseFortranNamelist(BufferedReader in, String line) {
    clear();
    String message = parseLine(line);
    //if (message != null && message.contentEquals("end")) { // Java 1.5
    if (message != null && message.equalsIgnoreCase("end")) {
      return "Ok";
    }
    else if (message != null) {
      return message;
    }

    try {
      // --- Look for end of namelist input
      while ( (line = in.readLine()) != null) {
        line = line.trim();
        message = parseLine(line);
        //if (message != null && message.contentEquals("end")) { // Java 1.5
        if (message != null && message.equalsIgnoreCase("end")) {
          logger.info("End-of-namelist");
          return "Ok";
        }
        else if (message != null) {
          return message;
        }
      } // --- End of while

    }
    catch (IOException e) {
      String error = "parseFortranNamelist: " + e.getMessage();
      logger.info(error);
      return error;
    }

    return "Ok";
  }

  /**
   *
   * @param line String
   * @return String
   */
  public String parseLine(String line) {
    String token, opts = line.toUpperCase();
    opts = opts.trim();
    logger.info("Parsing line: " + opts);
    if (opts.length() == 0) {
      return null;
    }

    StringTokenizer subtoken, tokens = new StringTokenizer(opts, " ,");

    // --- Special case for the first line in namelist input

    if (opts.startsWith("&") || opts.startsWith("$")) {
      if (opts.startsWith("&END") || opts.startsWith("$END")) {
        return "end";
      }
      if (Name != null) {
        return "Start of a new namelist";
      }
      token = tokens.nextToken();
      subtoken = new StringTokenizer(token, "&$");
      token = subtoken.nextToken();
      Name = token;
      logger.info("Namelist name: " + Name);
    }

    // --- Go through tokens

    while (tokens.hasMoreTokens()) {
      token = tokens.nextToken();
      if (token == null) {
        return null;
      }
      logger.info("Parsing token: " + token);

      // --- Simple cases of end-of-namelist

      if (token.startsWith("&END") || token.startsWith("$END")) {
        return "end";
      }
      else if (token.startsWith("/") && token.length() == 1) {
        return "end";
      }

      // --- Case: taup=0.2

      //if (Status == READY_FOR_NEW_OPTION && token.contains("=") && // Java 1.5
      if (Status == READY_FOR_NEW_OPTION && token.indexOf("=") != -1 &&
          (token.indexOf('=') < token.length() - 1)) {
        subtoken = new StringTokenizer(token, "=");
        Key = subtoken.nextToken();
        if (Vars.containsKey(Key)) {
          return "Already contains key " + Key;
        }
        Value = subtoken.nextToken();
        Vars.put(Key, Value);
        Status = READY_FOR_NEW_OPTION;
      }

      // --- Case: taup=            0.2

      //else if (Status == READY_FOR_NEW_OPTION && token.contains("=") && // Java 1.5
      else if (Status == READY_FOR_NEW_OPTION && token.indexOf("=") != -1 &&
               (token.indexOf('=') == token.length() - 1)) {
        subtoken = new StringTokenizer(token, "=");
        Key = subtoken.nextToken();
        if (Vars.containsKey(Key)) {
          return "Already contains key " + Key;
        }
        Status = WAITING_FOR_VALUE;
      }

      // --- Case: taup            =0.2

      //else if (Status == READY_FOR_NEW_OPTION && !token.contains("=")) { // Java 1.5
      else if (Status == READY_FOR_NEW_OPTION && token.indexOf("=") == -1) {
        Key = token;
        if (Vars.containsKey(Key)) {
          return "Already contains key " + Key;
        }
        Status = WAITING_FOR_VALUE;
      }

      // --- Case: =0.2

      else if (Status == WAITING_FOR_VALUE && token.indexOf("=") == 0 &&
               token.length() > 1) {
        subtoken = new StringTokenizer(token, "=");
        Value = subtoken.nextToken();
        Vars.put(Key, Value);
        Status = READY_FOR_NEW_OPTION;
      }

      // --- Case: =

      else if (Status == WAITING_FOR_VALUE && token.indexOf("=") == 0 &&
               token.length() == 1) {
        Status = WAITING_FOR_VALUE;
      }

      // --- Case: 0.2

      //else if (Status == WAITING_FOR_VALUE && !token.contains("=")) { // Java 1.5
      else if (Status == WAITING_FOR_VALUE && token.indexOf("=") == -1) {
        Value = token;
        Vars.put(Key, Value);
        Status = READY_FOR_NEW_OPTION;
      }

    }

    return null;
  }

  /**
   *
   * @param line String
   * @param nm_name String
   * @return boolean
   */
  static public boolean hasNamelistStart(String line, String nm_name) {
    String namelist = "&" + nm_name.toUpperCase();
    String bufer = (line.trim()).toUpperCase();

      return bufer.startsWith( namelist );

  }

  /**
   *
   * @param line String
   * @return boolean
   */
  static public boolean hasNamelistEnd(String line) {
    String bufer = (line.trim()).toUpperCase();
      return bufer.endsWith( "&END" ) || bufer.endsWith( "$END" ) || bufer.endsWith( " /" );
  }

  static public void writeNamelistBody(StringWriter sWriter, String namelistName, Map Vars) {

    sWriter.write(" &" + namelistName + "\n");

    Set set = Vars.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String option = me.getKey().toString();
      String value = me.getValue().toString();

      sWriter.write("     " + option + " = " + value + "\n");
    }

    sWriter.write(" &end\n");
  }

}
