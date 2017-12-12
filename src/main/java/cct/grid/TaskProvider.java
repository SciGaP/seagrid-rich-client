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

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import cct.interfaces.FileChooserInterface;
import cct.ssh.SSHServiceProvider;

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
public class TaskProvider {

  public static final String CCT_PROVIDER_PROPERTY_FILE = "cct-provider.properties";
  public final static String GLOBUS_2_PROVIDER = "gt2";
  public final static String GLOBUS_4_PROVIDER = "gt4";
  public final static String SSH_PROVIDER = "ssh";
  public final static String LOCAL_PROVIDER = "local";
  private static boolean overwriteStaticProvider = true;
  private static Map<String, GridProviderInterface> availableProviders = new LinkedHashMap<String, GridProviderInterface>();

  static {
    //availableProviders.put(GLOBUS_2_PROVIDER, new GatekeeperPanel());
    //availableProviders.put(GLOBUS_4_PROVIDER, new GT4Panel());
    //availableProviders.put(SSH_PROVIDER, new SSHPanel());
    //availableProviders.put(LOCAL_PROVIDER, new JPanel());
    //availableProviders.put(GLOBUS_2_PROVIDER, new GT2ServiceProvider()); // !!! Temporary
    //availableProviders.put(GLOBUS_4_PROVIDER, new GT4ServiceProvider()); // !!! Temporary
    availableProviders.put(SSH_PROVIDER, new SSHServiceProvider());
    //availableProviders.put(LOCAL_PROVIDER, new LocalServiceProvider());
  }
  private String jobHandle = null;
  private int jobID;
  private static TaskProvider tp = new TaskProvider();
  static final Logger logger = Logger.getLogger(TaskProvider.class.getCanonicalName());

  private TaskProvider() {
    loadProviderProperties();
  }

  private void loadProviderProperties() {
    try {
      Enumeration e = TaskProvider.class.getClassLoader().getResources(CCT_PROVIDER_PROPERTY_FILE);
      while (e.hasMoreElements()) {
        try {
          loadProviderProperties(((URL) e.nextElement()).openStream());
        } catch (Exception ee) {
          System.err.println("Error reading from provider properties: " + ee.getMessage());
        }
      }
    } catch (Exception e) {
      System.err.println("Warning: No " + CCT_PROVIDER_PROPERTY_FILE + " resource found");
    }
  }

  private void loadProviderProperties(InputStream is) {
    Properties props = new Properties();
    try {
      props.load(is);
      String provider = null;
      String providerClass = null;

      Enumeration e = props.propertyNames();
      while (e.hasMoreElements()) {
        String prop = e.nextElement().toString();

        if (prop.equalsIgnoreCase("provider")) {
          provider = props.getProperty(prop);
        } else if (prop.equalsIgnoreCase("taskHandler")) {
          providerClass = props.getProperty(prop);
        }

      }
      if (provider == null || provider.length() < 1) {
        System.err.println("Provider name missing from provider properties file");
        return;
      }
      if (providerClass == null || providerClass.length() < 1) {
        System.err.println("Provider class name missing from provider properties file");
        return;
      }

      logger.info("Loading provider " + provider + " : " + providerClass);
      Class cl = this.getClass().getClassLoader().loadClass(providerClass);
      Object obj = cl.newInstance();
      logger.info("Provider " + provider + " : " + providerClass + " was loaded");

      GridProviderInterface gpi = (GridProviderInterface) obj;

      if (overwriteStaticProvider) {
        availableProviders.put(provider, gpi);
      } else if (availableProviders.containsKey(provider)) {
        System.err.println("Provider " + provider + " is already loaded. Use overwrite option");
        return;
      } else {
        availableProviders.put(provider, gpi);
      }
    } catch (Exception e) {
      System.err.println("Could not load provider: " + e.getMessage());
    }
  }

  public static void setAvailableTaskProviders(Map providers) {
    availableProviders = new LinkedHashMap<String, GridProviderInterface>(providers);
  }

  public static void addTaskProviders(Map providers) {
    if (availableProviders == null) {
      availableProviders = new LinkedHashMap<String, GridProviderInterface>(providers);
    } else {
      availableProviders.putAll(providers);
    }
  }

  public static GridProviderInterface getProvider(String provider) {
    return availableProviders.get(provider).newInstance();
  }

  public static Map<String, GridProviderInterface> getAvailableTaskProviders() {
    LinkedHashMap<String, GridProviderInterface> temp = new LinkedHashMap<String, GridProviderInterface>(availableProviders.size());
    Iterator iter = availableProviders.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String key = me.getKey().toString();
      GridProviderInterface pr = (GridProviderInterface) me.getValue();
      if (pr != null) {
        pr = pr.newInstance();
      }
      temp.put(key, pr);
    }
    return new LinkedHashMap(availableProviders);
  }

  public static boolean isValidServiceProvider(String provider) {
      return availableProviders.get( provider ) != null;
  }

  public String submitJob(JobDescription job) throws Exception {

    String provider = job.getTaskProvider();
    if (provider == null) {
      throw new Exception("No service provider specified");
    }

    if (!isValidServiceProvider(provider)) {
      throw new Exception(provider + " is not currently supported");
    }

    // --- Submit task

    GridProviderInterface taskProvider = availableProviders.get(provider);

    try {
      jobHandle = taskProvider.submitTask(job);
    } catch (Exception ex) {
      throw ex;
    }

    return jobHandle;
    /*
    if (provider.equalsIgnoreCase(GLOBUS_2_PROVIDER)) {

    GT2ServiceProvider jobSubmit = new GT2ServiceProvider();
    try {
    jobSubmit.submitJob(job);
    }
    catch (Exception ex) {
    throw ex;
    }

    jobHandle = jobSubmit.getJobHandle();
    }

    else if (provider.equalsIgnoreCase(GLOBUS_4_PROVIDER)) {
    GT4ServiceProvider jobSubmit = new GT4ServiceProvider();
    try {
    jobSubmit.submitJob(job);
    }
    catch (Exception ex) {
    throw ex;
    }

    }

    else if (provider.equalsIgnoreCase(SSH_PROVIDER)) {
    SSHServiceProvider jobSubmit = new SSHServiceProvider();
    try {
    jobSubmit.submitJob(job);
    }
    catch (Exception ex) {
    throw ex;
    }
    jobHandle = jobSubmit.getJobHandle();
    }

    else if (provider.equalsIgnoreCase(LOCAL_PROVIDER)) {
    LocalServiceProvider jobSubmit = new LocalServiceProvider();
    try {
    jobSubmit.submitJob(job);
    }
    catch (Exception ex) {
    throw ex;
    }
    jobHandle = jobSubmit.getJobHandle();

    }
     */
  }

  public String getJobHandle() {
    return jobHandle;
  }

  /**
   * Returns job status as a string for a given checkpoint
   * @param chkp CheckPoint
   * @return String
   * @throws Exception
   */
  public static String getJobStatus(CheckPoint chkp) throws Exception {
    if (!chkp.containsKey(CheckPointInterface.statusTag)) {
      System.err.println("!chkp.containsKey(CheckPointInterface.statusTag)");
      chkp.setJobStatus(CheckPointInterface.JOB_STATUS_UNKNOWN);
    }

    if (chkp.getTaskProvider() == null) {
      System.err.println("chkp.getTaskProvider() == null");
      return CheckPointInterface.JOB_STATUS_UNKNOWN;
    }

    String status = chkp.getJobStatus();
    if (status.equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
      return CheckPointInterface.JOB_STATUS_DONE;
    }

    String handle = chkp.getJobHandle();

    if (handle == null) {
      System.err.println("Job handle == null");
      return CheckPointInterface.JOB_STATUS_UNKNOWN;
    }

    String jobName = chkp.getJobName();

    String provider = chkp.getTaskProvider();
    if (provider == null) {
      System.err.println("Task " + jobName + " does not have a provider record");
      throw new Exception("Task " + jobName + " does not have a provider record");
    }

    if (!isValidServiceProvider(provider)) {
      System.err.println(provider + " is not currently supported/loaded");
      return CheckPointInterface.JOB_STATUS_UNKNOWN;
    }

    // --- Query job status

    GridProviderInterface taskProvider = availableProviders.get(provider);
    status = taskProvider.getJobStatus(chkp);
    /*
    if (provider.equalsIgnoreCase(GLOBUS_2_PROVIDER)) {

    status = GT2ServiceProvider.getJobStatus(chkp);
    return status;
    }

    else if (provider.equalsIgnoreCase(GLOBUS_4_PROVIDER)) {
    }

    else if (provider.equalsIgnoreCase(SSH_PROVIDER)) {
    status = SSHServiceProvider.getJobStatus(chkp);
    return status;
    }

    else if (provider.equalsIgnoreCase(LOCAL_PROVIDER)) {
    }
     */
    return status;
  }

  public static void downloadOutputFiles(CheckPoint chkp) throws Exception {
    boolean outputFilesStatusUpdated = false;
    /*
    // Error check
    if (!chkp.hasOutputFiles()) {
    System.err.println("Warning: attempt to download output file(s) from the checkpoint without ones");
    return;
    }

    if (chkp.areOutputFilesLoaded()) {
    System.err.println("Warning: downloadOutputFiles: output files are already downloaded");
    return;
    }

    if (chkp.getTaskProvider() == null) {
    System.err.println("Error: downloadOutputFiles: no provider name in checkpoint file");
    throw new MalformedCheckPointException("No provider name in checkpoint file");
    }

    // --- Creating valid URLs

    if (this.getComputer() == null) {
    System.err.println(
    "Error: downloadOutputFiles: no computer name in checkpoint file");
    throw new MalformedCheckPointException(
    "No computer name in checkpoint file");
    }

    String remoteDir = "";
    if (this.getRemoteDirectory() == null) {
    //System.err.println(
    //    "Error: downloadOutputFiles: no remote directory in checkpoint file");
    //throw new MalformedCheckPointException(
    //    "No remote directory in checkpoint file");
    remoteDir += "/";
    }
    else {
    remoteDir = getRemoteDirectory();
    if (!remoteDir.endsWith("/")) {
    remoteDir = remoteDir + "/";
    }
    }

    if (chkp.getLocalDirectory() == null) {
    System.err.println(
    "Error: downloadOutputFiles: no local directory in checkpoint file");
    throw new MalformedCheckPointException(
    "No local directory in checkpoint file");
    }

    String localDir = chkp.getLocalDirectory();

    if (!localDir.endsWith("/") && !localDir.endsWith("\\")) {
    localDir = localDir + "/";
    }

    String fromURL = getComputer() + remoteDir;
    String toURL = localDir;

    String errorMessage = "";

    // --- Go through output files
    HashMap out = chkp.getOutputFilesEntry();
    Set set = out.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
    Map.Entry me = (Map.Entry) iter.next();
    String Key = me.getKey().toString();
    String outputFile = me.getValue().toString();
    if (Key.startsWith(chkp.outputStatusPrefix)) {
    continue;
    }
    String status_tag = chkp.outputStatusPrefix + Key;
    if (!out.containsKey(status_tag)) {
    System.err.println("Internal Error: tag " + Key + " should be complemented with tag " + chkp.outputStatusPrefix + Key);
    throw new RuntimeException("Internal Error: tag " + Key + " should be complemented with tag " + chkp.outputStatusPrefix +
    Key);
    //continue;
    }

    String status_value = (String) out.get(chkp.outputStatusPrefix + Key);

    // --- Download it
    if (status_value.equalsIgnoreCase("false")) {
    logger.info("Copying\n  From: " + fromURL + outputFile);
    logger.info("  To: " + toURL + outputFile);

    if (fileTransferHandler.tranferFile(fromURL + outputFile, toURL + outputFile, chkp.getTaskProvider())) {
    out.put(chkp.outputStatusPrefix + Key, "true");
    outputFilesStatusUpdated = true;
    }
    else {
    errorMessage += "Unable to download " + fromURL + outputFile + "\n";
    }

    }
    }

    // --- Update overal job status
    if (errorMessage.length() == 0) {
    chkp.setOverallJobStatus(chkp.JOB_STATUS_DONE);
    outputFilesStatusUpdated = true;
    }

    if (errorMessage.length() > 0) {
    throw new MalformedCheckPointException(errorMessage);
    }
     */
  }

  /*
  public static boolean transferFile(String source, String destination, String provider) throws Exception {

  if (provider == null) {
  throw new Exception("No service provider specified");
  }

  if (!isValidServiceProvider(provider)) {
  throw new Exception(provider + " is not currently supported");
  }

  GridProviderInterface taskProvider = availableProviders.get(provider);

  return taskProvider.transferFile(source, destination);
  }
   */
  public static boolean transferFile(String source, String destination, CheckPoint chkp) throws Exception {

    // Error check
    if (!chkp.hasOutputFiles()) {
      System.err.println("Warning: attempt to download output file(s) from the checkpoint without ones");
      return false;
    }

    if (chkp.areOutputFilesLoaded()) {
      System.err.println("Warning: downloadOutputFiles: output files are already downloaded");
      return false;
    }

    if (chkp.getTaskProvider() == null) {
      System.err.println("Error: downloadOutputFiles: no provider name in checkpoint file");
      throw new Exception("No provider name in checkpoint file");
    }

    String provider = chkp.getTaskProvider();
    if (provider == null) {
      throw new Exception("No service provider specified");
    }

    if (!isValidServiceProvider(provider)) {
      throw new Exception(provider + " is not currently supported");
    }

    GridProviderInterface taskProvider = availableProviders.get(provider);
    return taskProvider.transferFile(source, destination, chkp);
  }

  /**
   * Kills a job
   * @param chkp CheckPoint
   * @return boolean
   * @throws Exception
   */
  public static void killJob(CheckPoint chkp) throws Exception {
    String jobName = chkp.getJobName();

    String provider = chkp.getTaskProvider();
    if (provider == null) {
      System.err.println("Task " + jobName + " does not have a provider record");
      throw new Exception("Task " + jobName + " does not have a provider record");
    }

    if (!isValidServiceProvider(provider)) {
      System.err.println(provider + " is not currently supported/loaded");
      throw new Exception(provider + " is not currently supported/loaded");
    }

    GridProviderInterface taskProvider = availableProviders.get(provider);

    try {
      taskProvider.killJob(chkp);
    } catch (Exception ex) {
      throw ex;
    }

    /*
    if (chkp.getTaskProvider().equalsIgnoreCase(GLOBUS_2_PROVIDER)) {
    try {
    GT2ServiceProvider.killJob(chkp);

    }
    catch (Exception ex) {
    System.err.println(ex.getMessage());
    throw ex;
    }
    }

    else if (chkp.getTaskProvider().equalsIgnoreCase(GLOBUS_4_PROVIDER)) {
    throw new Exception("Killing jobs using " + chkp.getTaskProvider() +
    " is not implemented yet");
    }

    else if (chkp.getTaskProvider().equalsIgnoreCase(SSH_PROVIDER)) {
    try {
    SSHServiceProvider.killJob(chkp);
    }
    catch (Exception ex) {
    System.err.println(ex.getMessage());
    throw ex;
    }
    }

    else if (chkp.getTaskProvider().equalsIgnoreCase(LOCAL_PROVIDER)) {
    throw new Exception("Killing jobs using " + chkp.getTaskProvider() +
    " is not implemented yet");
    }

    else {
    throw new Exception("Unknown task provider: " + chkp.getTaskProvider());
    }
     */

  }

  public static FileChooserInterface getRemoteFileChooser(String provider) {
    if (provider.equalsIgnoreCase(SSH_PROVIDER)) {
    }

    return null;
  }
}
