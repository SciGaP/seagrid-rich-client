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

package cct.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PBSJobStatus {

  String Job_Name = null;
  String Job_Owner = null;
  String job_state = null;
  String queue = null;
  String server = null;
  String Account_Name = null;
  Map resourcesUsed = new HashMap();

  public PBSJobStatus() {
  }

  /**
   * Parses  a full status display in (qstat -f xxx) the form:
   * > qstat -f 598103.ac-pbs
   Job Id: 598103.ac-pbs
    Job_Name = pbs.eq36smd
    Job_Owner = pcc562@ac
    job_state = H
    queue = normal
    server = ac-pbs
    Account_Name = f91
    Checkpoint = u
    ctime = Mon Jul 31 14:24:56 2006
    depend = afterok:598101.ac-pbs@ac-pbs
    Error_Path = ac:/short/f91/poker/toxSMD/pbs.eq36smd.e598103
    group_list = f91
    Hold_Types = s
    Join_Path = oe
    Keep_Files = n
    Mail_Points = a
    mtime = Mon Jul 31 14:24:56 2006
    Output_Path = ac:/short/f91/poker/toxSMD/pbs.eq36smd.o598103
    Priority = 25
    qtime = Mon Jul 31 14:24:56 2006
    Rerunable = False
    Resource_List.jobfs = 100mb
    Resource_List.ncpus = 32
    Resource_List.nodect = 32
    Resource_List.nodes = 32:ppn=1
    Resource_List.other = mpi
    Resource_List.software = namd
    Resource_List.vmem = 6000mb
    Resource_List.walltime = 08:29:00
    Shell_Path_List = /bin/bash
    Variable_List = PBS_O_HOME=/home/562/pcc562,PBS_O_LOGNAME=pcc562,
        PBS_O_PATH=/opt/grace-5.1.18/grace/bin:/opt/nedit-5.5/bin:/opt/vmd-1.8
        .2/bin:/opt/netpbm-10.26.26/bin:/opt/intel-cc/8.1.035/bin:/opt/intel-fc
        /8.1.031/bin:/opt/anumpirun/2.1.14/bin:/opt/mpt-1.12/bin:/opt/pbs/bin:/
        opt/rash/bin:/usr/bin:/bin:/usr/sbin:/sbin:/opt/bin:.:/opt/lesstif-0.93
        .94/gcc-3.3.3/bin:/home/562/pcc562/scripts:./,
        PBS_O_MAIL=/var/mail/pcc562,PBS_O_SHELL=/bin/bash,PBS_O_HOST=ac,
        PBS_O_WORKDIR=/short/f91/poker/toxSMD,PROJECT=f91,PBS_O_QUEUE=normal
    cwd = 1


   * @return PBSJobStatus
   */
  public String parsePBSJobStatus(BufferedReader in) throws Exception {
    String line;
    String value = "Unknown";
    try {
      while ( (line = in.readLine()) != null) {
        if (line.matches("(?i).*job_state.*=.*")) {
          value = getValue(line);

        }
        else if (line.matches("(?i).*resources_used.*=.*")) {
          String key = line.substring(0, line.indexOf("=")).trim();
          String temp = line.substring(line.indexOf("=") + 1, line.length()).trim();
          resourcesUsed.put(key, temp);
        }
      } // --- End of while
      return value;
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      throw e;
      //return error;
    }
    //return null;
  }

  /**
   * Parsing line of a PBS full display output, for example: job_state = R and returning "R"
   * @param line String
   * @return String
   */
  public String getValue(String line) {
    if (line.indexOf("=") == -1) {
      return null;
    }
    else if (line.indexOf("=") >= line.length()) {
      return null;
    }
    return line.substring(line.indexOf("=") + 1).trim();
  }

  public Map getResourcesUsed() {
    return resourcesUsed;
  }

  public static void main(String[] args) {
    PBSJobStatus pbsjobstatus = new PBSJobStatus();
  }
}
