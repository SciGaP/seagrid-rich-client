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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class JobDescription
        extends HashMap {

  // Local  Declarations
  final static String transferPrefix = "transfer";
  final static String PREPROCESS_DIRECTIVES = "preProcessDirectives";
  final static String POSTPROCESS_DIRECTIVES = "postProcessDirectives";
  final static String SERVICE_PROVIDER = "serviceProvider";
  final static String LOCAL_DIRECTORY = "localDirectory";
  final static String LOCAL_EXECUTABLE = "localExecutable";
  final static String ABS_PATH_EXECUTABLE = "absPathExecutable";
  final static String JOBFS = "jobfs";
  final static String SOFTWARE = "software";
  final static String PROVIDER_OBJECT = "providerObject";
  //  Global Declarations
  public final static String PBS_SCHEDULER = "pbs";
  final static String ARGUMENT = "argument";
  final static String COUNT = "count";
  final static String DIRECTORY = "directory";
  final static String ENVIRONMENT = "environment";
  final static String EXECUTABLE = "executable";
  final static String EXTENSIONS = "extensions";
  final static String FACTORYENDPOINT = "factoryEndpoint";
  final static String FILECLEANUP = "fileCleanUp";
  final static String FILESTAGEIN = "fileStageIn";
  final static String FILESTAGEOUT = "fileStageOut";
  final static String HOSTCOUNT = "hostCount";
  final static String JOB = "job";
  final static String JOBCREDENTIAL = "jobCredentialEndpoint";
  final static String JOBTYPE = "jobType";
  final static String LIBRARYPATH = "libraryPath";
  final static String MAXCPUTIME = "maxCpuTime";
  final static String MAXMEMORY = "maxMemory";
  final static String MAXTIME = "maxTime";
  final static String MAXWALLTIME = "maxWallTime";
  final static String MINMEMORY = "minMemory";
  final static String MULTIJOB = "multiJob";
  final static String PROJECT = "project";
  final static String QUEUE = "queue";
  final static String SERVICELEVEL = "serviceLevelAgreement";
  final static String STAGINGCREDENTIAL = "stagingCredentialEndpoint";
  final static String STDERR = "stderr";
  final static String STDIN = "stdin";
  final static String STDOUT = "stdout";
  // Other
  final static String RESOURCE_ID = "ResourceID";
  final static String ADDRESS = "Address";
  final static String CLUSTER_ADDRESS = "clusterAddress";
  final static String SHELL = "shell";
  final static String ALLLORNONE = "allOrNone";
  final static String SOURCE_URL = "sourceUrl";
  final static String DESTINATION_URL = "destinationUrl";
  final static String USE_GASS = "useGASS";
  final static String PBS_JOB_SCRIPT = "pbsjobscript";
  final static String PBS_OTHER = "pbsother";
  // Transfer Request Type
  final static String TRANSFER = "transfer";
  final static String MUL_PATTERN = ".*?@@MUL[(]\\d+?[.]{0,1}?\\d*?,\\d+?[.]{0,1}?\\d*?[)]@@.*?";
  final static String MUL_PATTERN_REPLACE = "@@MUL[(]\\d+?[.]{0,1}?\\d*?,\\d+?[.]{0,1}?\\d*?[)]@@";

  public JobDescription() {
  }

  public void setExecutable(String executable) {
    put(EXECUTABLE, executable);
  }

  public String getExecutable() {
    Object obj = get(EXECUTABLE);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setPBSOther(String other) {
    put(PBS_OTHER, other);
  }

  public String getShell() {
    Object obj = get(SHELL);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setShell(String other) {
    put(SHELL, other);
  }

  public String getPBSOther() {
    Object obj = get(PBS_OTHER);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setPbsJobScript(boolean enable) {
    put(PBS_JOB_SCRIPT, new Boolean(enable));
  }

  public boolean isPbsJobScript() {
    Object obj = get(PBS_JOB_SCRIPT);
    if (obj == null) {
      return false;
    }
    return ((Boolean) obj).booleanValue();
  }

  public void setTaskProvider(String provider) {
    put(SERVICE_PROVIDER, provider);
  }

  public String getTaskProvider() {
    Object obj = get(SERVICE_PROVIDER);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setTaskProviderObject(Object provider) {
    put(PROVIDER_OBJECT, provider);
  }

  public Object getTaskProviderObject() {
    Object obj = get(PROVIDER_OBJECT);
    if (obj == null) {
      return null;
    }
    return obj;
  }

  public void setRemoteDirectory(String dir) {
    put( DIRECTORY, dir);
  }

  public String getRemoteDirectory() {
    Object obj = get(DIRECTORY);
    if (obj == null) {
      return null;
    }
    return (String) obj;

  }

  public void setStdout(String dir) {
    put(STDOUT, dir);
  }

  public String getStdout() {
    Object obj = get(STDOUT);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setStderr(String dir) {
    put(STDERR, dir);
  }

  public String getStderr() {
    Object obj = get(STDERR);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setJobName(String dir) {
    put(JOB, dir);
  }

  public String getJobName() {
    Object obj = get(JOB);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setQueue(String dir) {
    put(QUEUE, dir);
  }

  public String getQueue() {
    Object obj = get(QUEUE);
    if (obj == null) {
      return null;
    }
    return (String) obj;

  }

  public void setLocalDirectory(String dir) {
    put( LOCAL_DIRECTORY, dir);
  }

  public String getLocalDirectory() {
    Object obj = get(LOCAL_DIRECTORY);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setNCPU(int ncpu) {
    put(COUNT, new Integer(ncpu));
  }

  public int getNCPU() {
    Object obj = get(COUNT);
    if (obj == null) {
      return 1;
    }
    return ((Integer) obj).intValue();
  }

  /**
   * Time is in seconds
   * @param time float
   */
  public void setMaxWallTime(float time) {
    int t = (int) time;
    put(MAXWALLTIME, new Integer(t));
  }

  /**
   * Time is in seconds
   * @param time float
   */
  public void setMaxWallTime(int time) {
    put(MAXWALLTIME, new Integer(time));
  }

  public int getMaxWallTime() {
    Object obj = get(MAXWALLTIME);
    if (obj == null) {
      return 0;
    }
    return ((Integer) obj).intValue();
  }

  /**
   * Returns time in the format [[hours:]minutes:]seconds
   * @return String
   */
  public String getMaxWallTimeFormatted() {
    Object obj = get(MAXWALLTIME);
    if (obj == null) {
      return "0";
    }

    int value = ((Integer) obj).intValue();
    String time = "";

    if ((value / 3600) > 0) {
      time += String.valueOf((value / 3600)) + ":";
      value -= (value / 3600) * 3600; // What left in minutes
      if ((value / 60) > 0) {
        String minutes = String.valueOf((value / 60));
        if (minutes.length() == 1) {
          minutes = "0" + minutes;
        }
        time += minutes + ":";
        value -= (value / 60) * 60; // What left in seconds
        time += value;
      } else {
        if (value >= 10) {
          time += "00:" + value;
        } else {
          time += "00:0" + value;
        }
      }
    } // --- Time is in minutes (and seconds)
    else if ((value / 60) > 0) {
      String minutes = String.valueOf((value / 60));
      if (minutes.length() == 1) {
        minutes = "0" + minutes;
      }
      time += minutes + ":";
      value -= (value / 60) * 60; // What left in seconds
      if (value >= 10) {
        time += value;
      } else {
        time += "0" + value;
      }

    } // --- Time in seconds only
    else {
      if (value >= 10) {
        time += value;
      } else {
        time += "0" + value;
      }
    }

    return time;
  }

  public void setMaxCpuTime(float time) {
    put(MAXCPUTIME, new Float(time));
  }

  public float getMaxCpuTime() {
    Object obj = get(MAXCPUTIME);
    if (obj == null) {
      return 0;
    }
    return ((Float) obj).floatValue();
  }

  public void setMaxTime(float time) {
    put(MAXTIME, new Float(time));
  }

  public float getMaxTime() {
    Object obj = get(MAXTIME);
    if (obj == null) {
      return 0;
    }
    return ((Float) obj).floatValue();
  }

  /**
   * Memory is in MB
   * @param mem int
   */
  public void setMaxMemory(int mem) {
    put(MAXMEMORY, new Integer(mem));
  }

  public int getMaxMemory() {
    Object obj = get(MAXMEMORY);
    if (obj == null) {
      return 0;
    }
    return ((Integer) obj).intValue();
  }

  public void setProject(String proj) {
    put(PROJECT, proj);
  }

  public String getProject() {
    Object obj = get(PROJECT);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setJobFS(int jobfs) {
    put(JOBFS, new Integer(jobfs));
  }

  public int getJobFS() {
    Object obj = get(JOBFS);
    if (obj == null) {
      return 0;
    }
    return ((Integer) obj).intValue();
  }

  public void setSoftware(String soft) {
    put(SOFTWARE, soft);
  }

  public String getSoftware() {
    Object obj = get(SOFTWARE);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  /**
   *
   * @param source String - Remote file
   * @param destination String - Local file
   */
  public void setFileStageIn(String source, String destination) {
    Map stageIn = null;
    if (!this.containsKey(FILESTAGEIN)) {
      stageIn = new HashMap();
      put(FILESTAGEIN, stageIn);
    }

    stageIn = (Map) get(FILESTAGEIN);

    Map transfer = new HashMap(2);

    transfer.put(SOURCE_URL, source);
    transfer.put(DESTINATION_URL, destination);

    stageIn.put(transferPrefix + stageIn.size(), transfer);

    put(FILESTAGEIN, stageIn);
  }

  /**
   *
   * @param source String - Local file
   * @param destination String - Remote file
   */
  public void setFileStageOut(String source, String destination) {
    Map stageOut = null;
    if (!this.containsKey(FILESTAGEOUT)) {
      stageOut = new HashMap();
      put(FILESTAGEOUT, stageOut);
    }

    stageOut = (Map) get(FILESTAGEOUT);

    Map transfer = new HashMap(2);

    transfer.put(SOURCE_URL, source);
    transfer.put(DESTINATION_URL, destination);

    stageOut.put(transferPrefix + stageOut.size(), transfer);

    put(FILESTAGEOUT, stageOut);
  }

  public void setScheduler(String scheduler) {
    put(RESOURCE_ID, scheduler);
  }

  public String getScheduler() {
    Object obj = get(RESOURCE_ID);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void setRemoteHost(String host) {
    put(ADDRESS, host);
  }

  public void setClusterAddress(String host) {
    put(CLUSTER_ADDRESS, host);
  }

  public String getRemoteHost() {
    Object obj = get(ADDRESS);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public String getClusterAddress() {
    Object obj = get(CLUSTER_ADDRESS);
    if (obj == null) {
      return null;
    }
    return (String) obj;
  }

  public void enableGASS(boolean enable) {
    put(USE_GASS, new Boolean(enable));
  }

  public boolean isGASSEnabled() {
    Object obj = get(USE_GASS);
    if (obj == null) {
      return false;
    }
    return ((Boolean) obj).booleanValue();
  }

  /**
   * Returns number of files for staging in
   * @return int - number of files for staging in
   */
  public int getFileStageInCount() {
    if (!this.containsKey(FILESTAGEIN)) {
      return 0;
    }

    Map stageIn = (Map) get(FILESTAGEIN);

    int count = 0;
    Set set = stageIn.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String targetKey = me.getKey().toString();
      if (targetKey.startsWith(transferPrefix)) {
        ++count;
      }
    }

    return count;
  }

  /**
   * Returns source and destination files
   * @param n int - 0 <= n < getFileStageInCount()
   * @return String[] - index 0 - destination file (remote)
   *                    index 1 - source file (local)
   */
  public String[] getFileStageIn(int n) {
    if (n < 0 || n >= getFileStageInCount()) {
      System.err.println("getFileStageIn: Internal Error: n < 0 || n >= getFileStageInCount() - Ignored...");
      return null;
    }

    Map stageIn = (Map) get(FILESTAGEIN);

    String key = transferPrefix + n;
    if (!stageIn.containsKey(key)) {
      System.err.println("getFileStageIn: Internal Error: !stageIn.containsKey(" + key + ") - Ignored...");
      return null;
    }

    Map transfer = (Map) stageIn.get(key);
    String[] files = new String[2];
    files[0] = transfer.get(SOURCE_URL).toString();
    files[1] = transfer.get(DESTINATION_URL).toString();

    return files;
  }

  /**
   * Adds argument for the executable
   * @param argument String
   */
  public void addArgument(String argument) {
    List arguments = null;
    if (!this.containsKey(ARGUMENT)) {
      arguments = new ArrayList();
      put(ARGUMENT, arguments);
    }

    arguments = (List) get(ARGUMENT);

    arguments.add(argument);

    put(ARGUMENT, arguments);
  }

  /**
   * Returns number of arguments
   * @return int
   */
  public int getArgumentCount() {
    if (!this.containsKey(ARGUMENT)) {
      return 0;
    }

    List arguments = (List) get(ARGUMENT);

    return arguments.size();
  }

  /**
   * Returns n-th argument
   * @param n int
   * @return String
   */
  public String getArgument(int n) {
    if (n < 0 || n >= getArgumentCount()) {
      System.err.println(
              "getArgument: Internal Error: n < 0 || n >= getArgumentCount() - Ignored...");
      return null;
    }

    List arguments = (List) get(ARGUMENT);

    return (String) arguments.get(n);
  }

  public void setLocalExecutable(boolean local) {
    put(LOCAL_EXECUTABLE, new Boolean(local));
  }

  public void setAbsPathExecutable(boolean local) {
    put(ABS_PATH_EXECUTABLE, new Boolean(local));
  }

  public boolean isAbsPathExecutable() {
    Object obj = get(ABS_PATH_EXECUTABLE);
    if (obj == null) {
      return false;
    }
    return ((Boolean) obj).booleanValue();
  }

  public boolean isLocalExecutable() {
    Object obj = get(LOCAL_EXECUTABLE);
    if (obj == null) {
      return false;
    }
    return ((Boolean) obj).booleanValue();
  }

  public void addPreprocessDirective(String argument) {
    List arguments = null;
    if (!this.containsKey(PREPROCESS_DIRECTIVES)) {
      arguments = new ArrayList();
      put(PREPROCESS_DIRECTIVES, arguments);
    }

    arguments = (List) get(PREPROCESS_DIRECTIVES);

    arguments.add(argument);

    put(PREPROCESS_DIRECTIVES, arguments);
  }

  /**
   * Returns number of preprocess directives
   * @return int
   */
  public int getPreprocessDirCount() {
    if (!this.containsKey(PREPROCESS_DIRECTIVES)) {
      return 0;
    }

    List arguments = (List) get(PREPROCESS_DIRECTIVES);

    return arguments.size();
  }

  public String getPreprocessDir(int n) {
    if (n < 0 || n >= getPreprocessDirCount()) {
      System.err.println(
              "getPreprocessDir: Internal Error: n < 0 || n >= getPreprocessDirCount() - Ignored...");
      return null;
    }

    List arguments = (List) get(PREPROCESS_DIRECTIVES);

    return (String) arguments.get(n);
  }

  public static String createRunScript(GridProviderInterface provider, SchedulerInterface scheduler,
          ClientProgramInterface program) throws Exception {

    StringWriter sWriter = new StringWriter();

    // --- Get command(s) from task provider

    sWriter.write(provider.getCommandsAsString());

    // --- Get scheduler-specific commands

    sWriter.write(scheduler.getCommandsAsString());

    // --- Get program-specific commands

    sWriter.write(program.getCommandsAsString());

    sWriter.close();
    return sWriter.toString();
  }

  /**
   *
   * @param job JobDescription
   * @throws Exception
   */
  public static String createUnixScript(JobDescription job, SchedulerInterface scheduler) throws Exception {
    StringWriter sWriter = new StringWriter();

    if (job.getShell() != null) {
      sWriter.write("#!" + job.getShell() + "\n"); // ---  Shell
    }

    // --- Get scheduler-specific commands

    sWriter.write(scheduler.getCommandsAsString());

    // --- Commands to be executed before main job

    if (job.getPreprocessDirCount() > 0) {
      for (int i = 0; i < job.getPreprocessDirCount(); i++) {
        sWriter.write(job.getPreprocessDir(i) + "\n\n");
      }
    }

    // --- Main command

    sWriter.write("echo 'Running " + job.getExecutable() + "'\n");

    if (job.getExecutable() != null) {
      sWriter.write(job.getExecutable());
    }

    // --- its arguments

    if (job.getArgumentCount() > 0) {
      sWriter.write("     " + job.getArgument(0));
      for (int i = 1; i < job.getArgumentCount(); i++) {
        sWriter.write("  \\\n          " + job.getArgument(i));
      }
      sWriter.write("\n\n");
    } else {
      sWriter.write("\n\n");
    }

    sWriter.write("echo 'Finish Running " + job.getExecutable() + "'\n");

    sWriter.close();
    return sWriter.toString();
  }

  public static String createUnixScript(JobDescription job) throws Exception {
    StringWriter sWriter = new StringWriter();

    if (job.getShell() != null) {
      sWriter.write("#!" + job.getShell() + "\n"); // ---  Shell
    }

    if (job.getScheduler() != null && job.getScheduler().equalsIgnoreCase("pbs")) {

      if (job.getProject() != null && job.getProject().length() > 0) {
        sWriter.write("#PBS -P " + job.getProject() + "\n");
      }
      if (job.getQueue() != null) {
        sWriter.write("#PBS -q " + job.getQueue() + "\n");
      }
      if (job.getMaxWallTime() != 0) {
        sWriter.write("#PBS -l walltime=" + job.getMaxWallTimeFormatted()
                + "\n");
      }
      if (job.getMaxMemory() != 0) {
        sWriter.write("#PBS -l vmem=" + job.getMaxMemory() + "MB\n");
      }
      if (job.getSoftware() != null) {
        sWriter.write("#PBS -l software=" + job.getSoftware() + "\n");
      }
      if (job.getJobFS() != 0) {
        sWriter.write("#PBS -l jobfs=" + job.getJobFS() + "Gb\n");
      }

      if (job.getPBSOther() != null) {
        sWriter.write("#PBS -l other=" + job.getPBSOther() + "\n");
      }
      if (job.getNCPU() != 0) {
        sWriter.write("#PBS -l ncpus=" + job.getNCPU() + "\n");
      }
      if (job.getStdout() != null) {
        sWriter.write("#PBS -o " + job.getStdout() + "\n");
      }
      if (job.getStderr() != null) {
        sWriter.write("#PBS -e " + job.getStderr() + "\n");
      }

      sWriter.write("#PBS -wd\n\n");

    }

    // --- Commands to be executed before main job

    if (job.getPreprocessDirCount() > 0) {
      for (int i = 0; i < job.getPreprocessDirCount(); i++) {
        sWriter.write(job.getPreprocessDir(i) + "\n\n");
      }
    }

    // --- Main command

    sWriter.write("echo 'Running " + job.getExecutable() + "'\n");

    if (job.getExecutable() != null) {
      sWriter.write(job.getExecutable());
    }

    // --- its arguments

    if (job.getArgumentCount() > 0) {
      sWriter.write("     " + job.getArgument(0));
      for (int i = 1; i < job.getArgumentCount(); i++) {
        sWriter.write("  \\\n          " + job.getArgument(i));
      }
      sWriter.write("\n\n");
    } else {
      sWriter.write("\n\n");
    }

    sWriter.write("echo 'Finish Running " + job.getExecutable() + "'\n");

    sWriter.close();
    return sWriter.toString();
  }

  public static String createUnixScript(JobDescription job, String template, Map<String, String> patterns) throws Exception {
    StringWriter sWriter = new StringWriter();
    BufferedReader in = new BufferedReader(new StringReader(template));
    String line;
    while ((line = in.readLine()) != null) {

      // --- First replace static constants, if any...

      if (line.matches(".*?@@.+?@@.*?")) {
        Iterator<String> it = patterns.keySet().iterator();
        while (it.hasNext()) {
          String key = it.next();
          String val = patterns.get(key);
          line = line.replaceAll(key, val);
        }
      }

      // --- Now replace math operators, if any

      while (line.matches(MUL_PATTERN)) {
        line = doMul(line);
      }

      sWriter.write(line + "\n");
    }
    sWriter.close();
    return sWriter.toString();
  }

  public static String doMul(String line) throws Exception {
    String result = null;
    int start = line.indexOf("@@MUL(");
    if (start == -1) {
      return result;
    }
    int end = line.indexOf(")@@");
    if (end == -1) {
      throw new Exception("Didn't find end of expression )@@");
    }

    String args = line.substring(start + "@@MUL(".length(), end);
    String twoArgs[] = args.split("[ ,\t]");
    if (twoArgs.length != 2) {
      throw new Exception("Expected two args divided by comma, got " + args);
    }

    boolean isFirstInteger = false;
    int firstInt = 0;
    try {
      firstInt = Integer.parseInt(twoArgs[0]);
      isFirstInteger = true;
    } catch (Exception ex) {
    }


    double first = 0.0;
    if (!isFirstInteger) {
      try {
        first = Double.parseDouble(twoArgs[0]);
      } catch (Exception ex) {
        throw new Exception("Expected the float or int type for the first argument, got " + args);
      }
    }

    boolean isSecondInteger = false;
    int secondInt = 0;
    try {
      secondInt = Integer.parseInt(twoArgs[1]);
      isSecondInteger = true;
    } catch (Exception ex) {
    }

    double second = 0.0;
    if (!isSecondInteger) {
      try {
        second = Double.parseDouble(twoArgs[1]);
      } catch (Exception ex) {
        throw new Exception("Expected the float or int type for the second argument, got " + args);
      }
    }

    if (isFirstInteger && isSecondInteger) {
      result = String.valueOf(firstInt * secondInt);
    } else if (isFirstInteger) {
      result = String.valueOf((double) firstInt * second);
    } else if (isSecondInteger) {
      result = String.valueOf(first * (double) secondInt);
    } else {
      result = String.valueOf(first * second);
    }

    result = line.replaceFirst(MUL_PATTERN_REPLACE, result);

    return result;
  }
}
