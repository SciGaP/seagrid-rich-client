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
package cct.ssh;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import cct.grid.CheckPoint;
import cct.grid.CheckPointInterface;
import cct.grid.GridProviderInterface;
import cct.grid.JobDescription;
import cct.grid.OperationalSystems;
import cct.grid.PBSJobStatus;
import cct.grid.TaskProvider;
import cct.interfaces.FileChooserInterface;
import cct.tools.filebrowser.SFTPBrowser;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.connection.ChannelInputStream;
import com.sshtools.j2ssh.io.IOStreamConnector;
import com.sshtools.j2ssh.io.IOStreamConnectorState;
import com.sshtools.j2ssh.session.SessionChannelClient;

/**
 * <p>Title: SSH Service Provider</p>
 *
 * <p>Description: How to use SSH Service Provider</p>
 * Could be customized using custom.properties file:
 * cct.ssh.SSHServiceProvider@jobKillCommand = bash --login -c 'qdel -F  %s'
 * cct.ssh.SSHServiceProvider@jobSubmitCommand = bash --login -c 'qsub %s'
 * cct.ssh.SSHServiceProvider@jobStatusCommand = bash --login -c 'qstat -f %s'
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class SSHServiceProvider
    implements GridProviderInterface {

  static final String SSH2_HOST = "ssh2Host";
  static final String SSH2_PORT = "ssh2Port";
  static final String SSH2_USER_NAME = "ssh2UserName";
  static final String DIVIDER = "@";
  static final String JOB_KILL_COMMAND = "jobKillCommand";
  static final String JOB_SUBMIT_COMMAND = "jobSubmitCommand";
  static final String JOB_STATUS_COMMAND = "jobStatusCommand";
  //cct.ssh.SSHServiceProvider@jobKillCommand = bash --login -c 'qdel -F  %s'
  //cct.ssh.SSHServiceProvider@jobSubmitCommand = bash --login -c 'qsub %s'
  //cct.ssh.SSHServiceProvider@jobStatusCommand = bash --login -c 'qstat -f %s'
  Preferences prefs = Preferences.userNodeForPackage(getClass());
  private String jobHandle;
  private int jobID = -1;
  private static Map hosts = new HashMap();
  protected SSHPanel nativeDialog = new SSHPanel();
  static String jobKillCommand = "bash --login -c 'qdel -F %s'";
  static String jobSubmitCommand = "bash --login -c 'qsub %s'";
  static String jobStatusCommand = "bash --login -c 'qstat -f %s'";
  static final Logger logger = Logger.getLogger(SSHServiceProvider.class.getCanonicalName());

  public SSHServiceProvider() {

    String className = this.getClass().getCanonicalName();
    try {
      Properties props = cct.GlobalSettings.getCustomProperties();

      if (props != null) {

        //--- Get custom kill command

        String customCommand = props.getProperty(className + DIVIDER + JOB_KILL_COMMAND);
        if (customCommand != null) {
          jobKillCommand = customCommand;
        }

        //--- Get custom submit command

        customCommand = props.getProperty(className + DIVIDER + JOB_SUBMIT_COMMAND);
        if (customCommand != null) {
          jobSubmitCommand = customCommand;
        }

        //--- Get custom status command

        customCommand = props.getProperty(className + DIVIDER
            + JOB_STATUS_COMMAND);
        if (customCommand != null) {
          jobStatusCommand = customCommand;
        }
      }
    } catch (Exception ex) {
    }

  }

  /**
   * Submits job using ssh
   * @param job JobDescription
   * @throws Exception in case of any error(s)
   */
  public void submitJob(JobDescription job) throws Exception {

    // Error check

    if (job.getLocalDirectory() == null) {
      throw new Exception("Local directory is not set");
    } else if (job.getExecutable() == null) {
      throw new Exception("Executable is not set");
    }

    //job.getExecutable());
    //job.getRemoteDirectory());


    /*
    if (job.getRemoteDirectory() != null && job.getJobName() != null) {
    sWriter.write("<stdout>");
    sWriter.write(job.getRemoteDirectory() + "/" + job.getJobName() +
    ".stdout");
    sWriter.write("</stdout>\n");
    }

    if (job.getRemoteDirectory() != null && job.getJobName() != null) {
    sWriter.write("<stderr>");
    sWriter.write(job.getRemoteDirectory() + "/" + job.getJobName() +
    ".stderr");
    sWriter.write("</stderr>\n");
    }
     */

    //SFTPBrowser sftp = (SFTPBrowser) job.getTaskProviderObject();
    SFTPBrowser sftp = (SFTPBrowser) nativeDialog.getSSHProvider();

    if (job.getFileStageInCount() > 0) {
      job.setTaskProviderObject(sftp);
      SFTPFileStaging fileTransfer = new SFTPFileStaging();
      try {
        fileTransfer.fileStageIn(job, true);
      } catch (Exception ex) {
        throw new Exception("File staging-in failed: " + ex.getMessage());
      }

    }

    // Remember the host and username

    String host = sftp.getHost();
    //String user = sftp.getUsername();
    if (!hosts.containsKey(host)) {
      hosts.put(host, sftp);
    }

    // --- Create SSH client

    SshClient ssh_client = sftp.getSshClient();

    // Submitting job

    SessionChannelClient session = ssh_client.openSessionChannel();

    //StringWriter outWriter = new StringWriter();
    //StringWriter errWriter = new StringWriter();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    IOStreamConnector output = new IOStreamConnector();
    IOStreamConnector error = new IOStreamConnector();
    output.setCloseOutput(false);
    error.setCloseOutput(false);

    output.connect(session.getInputStream(), outStream);
    //output.connect(session.getInputStream(), System.out);

    //error.connect(session.getStderrInputStream(), System.out);
    error.connect(session.getStderrInputStream(), errStream);

    /*     try {
    logger.info("Executing: cd " + job.getRemoteDirectory());
    session.executeCommand("cd " + job.getRemoteDirectory());
    String out = errStream.toString();
    if (out != null && out.length() > 0) {
    throw new Exception("Failed to switch to remote directory: " + out);
    }
    logger.info("Out: "+outStream.toString());
    }
    catch (Exception ex) {
    throw new Exception("Failed to switch to remote directory: " +
    ex.getMessage());
    }
     */

    /*
    outStream.reset();
    errStream.reset();

    output = new IOStreamConnector();
    error = new IOStreamConnector();
    output.setCloseOutput(false);
    error.setCloseOutput(false);

    //output.connect(session.getInputStream(), System.out );
    output.connect(session.getInputStream(), outStream);
    error.connect(session.getStderrInputStream(), errStream);
     */

    try {
      String cmd = "cd " + job.getRemoteDirectory() + "; "
          + String.format(jobSubmitCommand, job.getExecutable());
      //"; bash --login -c 'qsub " +
      //job.getExecutable() + "'";
      //String cmd = "qsub " + job.getExecutable();

      logger.info("Executing: " + cmd);
      ChannelInputStream in = session.getInputStream();
      session.executeCommand(cmd);
      //session.getState().waitForState(ChannelState.CHANNEL_CLOSED);

      output.getState().waitForState(IOStreamConnectorState.CLOSED);
      error.getState().waitForState(IOStreamConnectorState.CLOSED);

      //InputStream err = session.getStderrInputStream();
      //session.close();
      String out = "";

      out = errStream.toString();
      if (out != null && out.length() > 0) {
        throw new Exception("Failed to submit a job: " + out);
      }

      byte buffer[] = new byte[255];
      int read;

      /*
      String errors = "";
      while ( (read = err.read(buffer)) > 0) {
      out = new String(buffer, 0, read);
      errors += out;
      System.err.println(out);
      }

      if (errors.length() > 0) {
      throw new Exception("Failed to submit a job:\n" + errors);
      }
       */

      /*
      while ( (read = in.read(buffer)) > 0) {
      out = new String(buffer, 0, read);
      logger.info(out);
      StringTokenizer st = new StringTokenizer(out, " .", true);
      // Look for a number
      while (st.hasMoreTokens()) {
      try {
      int number = Integer.parseInt(st.nextToken());
      jobHandle = String.valueOf(number);
      jobID = number;
      logger.info("Job handle: " + jobHandle);
      return;
      }
      catch (Exception ex) {}
      }

      throw new Exception("Cannot get job handle");
      }
       */



      session.close();
      logger.info("Output state: " + output.getState());
      logger.info("Output bytes: " + output.getBytes());
      logger.info("Out: Size: " + outStream.size() + " : "
          + outStream.toString());

      jobHandle = outStream.toString().trim();
      logger.info("Job handle: " + jobHandle);
      // Look for a number

      /*
      try {
      int number = Integer.parseInt(jobHandle);
      jobID = number;

      return;
      }
      catch (Exception ex) {}
       */
    } catch (Exception ex) {
      throw new Exception("Failed to submit job: " + ex.getMessage());
    }

    /*
    if (!session.requestPseudoTerminal("vt100", 80, 24, 0, 0, "")) {
    logger.info("Failed to allocate a pseudo terminal");
    }
    if (session.startShell()) {
    IOStreamConnector input =
    new IOStreamConnector();
    IOStreamConnector output =
    new IOStreamConnector();
    IOStreamConnector error =
    new IOStreamConnector();
    output.setCloseOutput(false);
    input.setCloseInput(false);
    error.setCloseOutput(false);
    input.connect(System.in, session.getOutputStream());
    output.connect(session.getInputStream(), System.out);
    error.connect(session.getStderrInputStream(), System.out);
    session.getState().waitForState(ChannelState.CHANNEL_CLOSED);
    }
    else {
    logger.info("Failed to start the users shell");
    }
     */
    //ssh.disconnect();
  }

  /**
   * Returns job handle of a submitted job (in the case of success)
   * @return String
   */
  public String getJobHandle() {
    return jobHandle;
  }

  /**
   * Returns current job status of a job
   * @param chkp CheckPoint
   * @return String
   * @throws Exception
   */
  public String getJobStatus(CheckPoint chkp) throws Exception {
    if (!chkp.containsKey(CheckPointInterface.statusTag)) {
      return null;
    }

    /*
    if (!chkp.containsKey(providerTag)) {
    return null;
    }
     */

    String status = chkp.getJobStatus();
    if (status.equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
      return status;
    }

    String handle = chkp.getJobHandle();
    if (handle == null) {
      throw new Exception("No job handle in the checkpoint");
    }

    String host = chkp.getServiceTag(SSH2_HOST);
    if (host == null) {
      throw new Exception("No host name in the checkpoint");
    }

    SFTPBrowser sftp;
    try {
      logger.log(Level.INFO, "Connecting to {0}...", host);
      sftp = getConnection(host);
      logger.log(Level.INFO, "Connection to {0} established", host);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, "Error connecting to {0} : {1}", new Object[]{host, ex.getMessage()});
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return CheckPointInterface.JOB_STATUS_UNKNOWN;
    }

    // --- Check whether we still connected

    if (!sftp.isAuthenticated()) {
      try {
        logger.log(Level.INFO, "Authenticating to host {0}...", host);
        sftp.reconnect();
      } catch (Exception ex) {
        logger.severe("Unable to reconnect to host " + sftp.getHost() + " as " + sftp.getUsername() + " : " + ex.getMessage());
        return CheckPointInterface.JOB_STATUS_UNKNOWN;
      }
    } else {
      logger.info("Connection to host " + host + " is already authenticated");
    }

    // --- Send a command

    SshClient ssh_client = sftp.getSshClient();
    SessionChannelClient session = ssh_client.openSessionChannel();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    IOStreamConnector output = new IOStreamConnector();
    IOStreamConnector error = new IOStreamConnector();
    output.setCloseOutput(false);
    error.setCloseOutput(false);

    output.connect(session.getInputStream(), outStream);
    error.connect(session.getStderrInputStream(), errStream);

    try {
      String cmd = String.format(jobStatusCommand, handle);
      //"bash --login -c 'qstat -f " + handle + "'";

      logger.info("Executing command '" + cmd + "' on host " + host + "...");
      session.executeCommand(cmd);

      output.getState().waitForState(IOStreamConnectorState.CLOSED);
      error.getState().waitForState(IOStreamConnectorState.CLOSED);
      session.close();

      String out = "";

      out = errStream.toString();
      logger.info("Stderr from the host " + host + ":\n" + out);
      //logger.info("Stderr: " + out);
      if (out != null && out.length() > 0) {
        //System.err.println("Failed to query a job: " +handle+" : " + out);
        //throw new Exception("Failed to submit a job: " + out);
        return CheckPointInterface.JOB_STATUS_DONE;
      }

      logger.info("Stdout from the host " + host + ":\n" + outStream.toString());
      //logger.info("Stdout: " + outStream.toString());
      BufferedReader in = new BufferedReader(new StringReader(outStream.toString()));

      PBSJobStatus pbs_status = new PBSJobStatus();

      try {
        status = pbs_status.parsePBSJobStatus(in);
        Map rused = pbs_status.getResourcesUsed();
        chkp.setResourcesUsed(rused);
        if (status == null) {
          System.err.println("Unable to get job statusfor job: " + handle
              + " : qstat output is empty");
          return CheckPointInterface.JOB_STATUS_UNKNOWN;
        }

        if (status.equalsIgnoreCase("E")) {
          return "EXITING AFTER HAVING RUN";
        } else if (status.equalsIgnoreCase("H")) {
          return "HELD";
        } else if (status.equalsIgnoreCase("Q")) {
          return "QUEUED";
        } else if (status.equalsIgnoreCase("R")) {
          return "ACTIVE";
        } else if (status.equalsIgnoreCase("T")) {
          return "BEING MOVED";
        } else if (status.equalsIgnoreCase("W")) {
          return "WAITING";
        } else if (status.equalsIgnoreCase("S")) {
          return "SUSPENDED";
        }

        return status;

      } catch (Exception ex) {
        logger.severe("Unable to get job status for a job: " + handle
            + " from the host " + host + ": " + ex.getMessage());
        return CheckPointInterface.JOB_STATUS_UNKNOWN;
      }

    } catch (Exception ex) {
      //throw new Exception("Failed to submit job: " + ex.getMessage());
      logger.severe("Failed to query a job: " + handle + " on the host " + host + ": "
          + ex.getMessage());
      return CheckPointInterface.JOB_STATUS_UNKNOWN;
    }

    //return JOB_STATUS_UNKNOWN;
  }

  /*
  public boolean transferFile(String source, String destination) throws Exception {

  // --- Define direction of the file transfer

  if (source.indexOf("/") == -1) {
  throw new Exception("Wrong Source " + source);
  }
  String host = source.substring(0, source.indexOf("/"));
  String remoteFile = source.substring(source.indexOf("/"));

  // --- Now we need to process a situation whether file is in root directory
  // (//File) or in default user's one (/File)

  if (remoteFile.startsWith("//")) {
  remoteFile = remoteFile.substring(1);
  }
  else if (remoteFile.substring(1).indexOf("/") == -1) {
  remoteFile = remoteFile.substring(1);
  }

  SFTPBrowser sftp = null;
  try {
  sftp = getConnection(host);
  }
  catch (Exception ex) {
  throw ex;
  }

  SftpClient sftp_client = null;
  try {
  sftp_client = sftp.getSftpClient();
  }
  catch (Exception ex) {
  throw new Exception("Failed to open sftp client: " + ex.getMessage());
  }

  logger.info("Transfering: " + source + " -> " + destination);
  try {
  sftp_client.get(remoteFile, destination);
  }
  catch (Exception ex) {
  throw new Exception(ex.getMessage());
  }

  return true;
  }
   */
  /**
   * Transfers file assuming that "source" is a remote file while "destination" is a local
   * @param source String
   * @param destination String
   * @param chkp CheckPoint
   * @return boolean
   * @throws Exception
   */
  public boolean transferFile(String source, String destination, CheckPoint chkp) throws Exception {
    // --- Define direction of the file transfer

    String host = chkp.getComputer();
    if (host == null || host.length() < 1) {
      throw new Exception("SSH Service provider: host is not set: " + source);
    }

    String remoteDir = "";
    if (chkp.getRemoteDirectory() == null) {
      remoteDir += "/";
    } else {
      remoteDir = chkp.getRemoteDirectory();
      if (!remoteDir.endsWith("/")) {
        remoteDir += "/";
      }
    }

    String remoteFile = remoteDir + source;

    // --- Now we need to process a situation whether file is in root directory
    // (//File) or in default user's one (/File)

    if (remoteFile.startsWith("//")) {
      remoteFile = remoteFile.substring(1);
    } else if (remoteFile.substring(1).indexOf("/") == -1) {
      remoteFile = remoteFile.substring(1);
    }

    SFTPBrowser sftp = null;
    try {
      sftp = getConnection(host);
    } catch (Exception ex) {
      throw ex;
    }

    SftpClient sftp_client = null;
    try {
      sftp_client = sftp.getSftpClient();
    } catch (Exception ex) {
      throw new Exception("Failed to open sftp client: " + ex.getMessage());
    }

    logger.info("Transfering: " + source + " -> " + destination);
    try {
      sftp_client.get(remoteFile, destination);
    } catch (Exception ex) {
      throw new Exception(ex.getMessage());
    }

    return true;

  }

  public static SFTPBrowser getConnection(String host) throws Exception {
    SFTPBrowser sftp = null;
    if (!hosts.containsKey(host)) { // Make a connection
      sftp = new SFTPBrowser();
      hosts.put(host, sftp);
      try {
        logger.info("Connecting to " + host);
        sftp.connect(host);
      } catch (Exception ex) {
        logger.severe("Cannot connect to " + host + ": " + ex.getMessage());
        throw new Exception("Unable to connect to Host " + host + " as " + sftp.getUsername() + " : " + ex.getMessage());
      }
    } else { // Get connection
      logger.info(host + " is already in the hosts pool... Retrieving...");
      sftp = (SFTPBrowser) hosts.get(host);
    }
    return sftp;
  }

  public void killJob(CheckPoint chkp) throws Exception {

    // --- Error check

    String host = chkp.getComputer();
    if (host == null) {
      throw new Exception("No Host in checkpoint file");
    }

    if (chkp.getTaskProvider() != null
        && !chkp.getTaskProvider().equalsIgnoreCase(TaskProvider.SSH_PROVIDER)) {
      throw new Exception("Checkpoint file is not for "
          + TaskProvider.SSH_PROVIDER + " task provider: "
          + chkp.getTaskProvider());
    }

    if (chkp.getJobStatus() != null
        && chkp.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
      return; // It's already finished
    }

    String handle = chkp.getJobHandle();

    if (handle == null) {
      throw new Exception("Checkpoint file does not have job handle");
    }

    // --- Getting provider

    SFTPBrowser sftp = null;
    try {
      sftp = getConnection(host);
    } catch (Exception ex) {
      throw ex;
    }

    // --- Send a command

    SshClient ssh_client = sftp.getSshClient();
    SessionChannelClient session = ssh_client.openSessionChannel();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    IOStreamConnector output = new IOStreamConnector();
    IOStreamConnector error = new IOStreamConnector();
    output.setCloseOutput(false);
    error.setCloseOutput(false);

    output.connect(session.getInputStream(), outStream);
    error.connect(session.getStderrInputStream(), errStream);

    try {
      //String cmd = "bash --login -c 'qdel -F " + handle + "'";
      String cmd = String.format(jobKillCommand, handle);

      logger.info("Executing: " + cmd);
      session.executeCommand(cmd);

      output.getState().waitForState(IOStreamConnectorState.CLOSED);
      error.getState().waitForState(IOStreamConnectorState.CLOSED);
      session.close();

      String out = "";

      out = errStream.toString();
      logger.info("Stderr: " + out);
      if (out != null && out.length() > 0) {
        if (out.indexOf("Unknown") != -1 && out.indexOf("Job") != -1) {
          return;
        }
        System.err.println("Failed to kill a job: " + handle + " : " + out);
        throw new Exception("Failed to kill a job: " + handle + " on "
            + host
            + " : " + out);
      }

      logger.info("Stdout: " + outStream.toString());
    } catch (Exception ex) {
      System.err.println("Failed to kill a job: " + handle + " on " + host
          + " : "
          + ex.getMessage());
      throw new Exception("Failed to kill a job: " + handle + " on " + host
          + " : "
          + ex.getMessage());
    }

  }

  public static void addHost(String host, SFTPBrowser sftp) {
    hosts.put(host, sftp);
  }

  /**
   *
   * @param sftp SFTPBrowser
   * @param command String
   * @return String[]
   * @throws Exception
   */
  public String[] executeCommand(SFTPBrowser sftp, String command) throws
      Exception {

    // Error check

    if (command == null || command.length() < 1) {
      throw new Exception("Command is not set");
    }

    // --- Create SSH client

    SshClient ssh_client = sftp.getSshClient();

    // Submitting job

    SessionChannelClient session = ssh_client.openSessionChannel();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    IOStreamConnector output = new IOStreamConnector();
    IOStreamConnector error = new IOStreamConnector();
    output.setCloseOutput(false);
    error.setCloseOutput(false);

    output.connect(session.getInputStream(), outStream);

    error.connect(session.getStderrInputStream(), errStream);

    try {
      logger.info("Executing: " + command);
      ChannelInputStream in = session.getInputStream();
      session.executeCommand(command);

      output.getState().waitForState(IOStreamConnectorState.CLOSED);
      error.getState().waitForState(IOStreamConnectorState.CLOSED);

      String[] return_value = new String[2];
      return_value[0] = outStream.toString();
      return_value[1] = errStream.toString();

      return return_value;
    } catch (Exception ex) {
      throw new Exception("Failed to execute command: " + ex.getMessage());
    }

  }

  //  implementation of GridProviderInterface functions
  public String getName() {
    return "ssh";
  }

  public Component getVisualComponent() {
    return nativeDialog;
  }

  public FileChooserInterface getRemoteFileChooser() {
    return nativeDialog;
  }

  public String getRemoteHost() {
    return nativeDialog.getHost();
  }

  public boolean isPassOptionsToScheduler() {
    return true;
  }

  public String submitTask(JobDescription job) throws Exception {
    nativeDialog.checkConnection();
    submitJob(job);
    return jobHandle;
  }

  public String getCommandsAsString() {
    return "#!sh\n";
  }

  public OperationalSystems getOS() {
    return OperationalSystems.Linux; // !!!
  }

  public String getGatekeeper() {
    return null;
  }

  public boolean isRemoteDirectorySelectable() {
    return true;
  }

  public CheckPointInterface setCheckPoint(CheckPointInterface checkp) {
    try {
      checkp.setServiceTag(CheckPointInterface.providerTag, getName(), true);
      checkp.setServiceTag(SSH2_HOST, nativeDialog.getHost(), true);
      checkp.setServiceTag(SSH2_PORT, String.valueOf(nativeDialog.getPort()), true);
      checkp.setServiceTag(SSH2_USER_NAME, nativeDialog.getUserName(), true);
    } catch (Exception ex) {
    }
    return checkp;
  }

  public GridProviderInterface newInstance() {
    return new SSHServiceProvider();
  }
}
