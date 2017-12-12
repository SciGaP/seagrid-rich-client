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

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JPanel;

import cct.interfaces.FileChooserInterface;

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
public class LocalServiceProvider
    implements GridProviderInterface {

  protected JPanel nativeDialog = new JPanel();
  String osName = "Linux";
  boolean windowsOS = false;
  static final Logger logger = Logger.getLogger(LocalServiceProvider.class.getCanonicalName());

  public LocalServiceProvider() {
    try {
      osName = System.getProperty("os.name");
      if (osName.toUpperCase().contains("WINDOWS")) {
        windowsOS = true;
      }
    } catch (SecurityException e) {
      System.err.println("Cannot get OS type : " + e.getMessage()
          + " Assuming Linux");
      // Ignore it
    }

  }
  private boolean interactive = true;
  private boolean overwiteAlways = true;
  private String jobHandle;
  private int jobID = -1;

  public void submitJob(JobDescription job) throws Exception {

    // --- Checking whether executable exists

    String path = null;
    if (job.isAbsPathExecutable()) {
      path = job.getExecutable();
    } else if (job.isLocalExecutable()) {
      //path = job.getLocalDirectory() + File.separator + job.getExecutable();
      path = job.getLocalDirectory() + "/" + job.getExecutable();
    } else {
      //path = job.getRemoteDirectory() + File.separator + job.getExecutable();
      path = job.getRemoteDirectory() + "/" + job.getExecutable();
    }

    File exec = new File(path);

    // --- Check for it's existence

    if (!exec.exists()) {
      throw new Exception("Executable " + path + " does not exists");
    }

    // --- Check existence of a remote directory

    File remoteDir = new File(job.getRemoteDirectory());

    if (!remoteDir.exists()) {
      throw new Exception("Working directory " + job.getRemoteDirectory()
          + " does not exists");
    }

    boolean isDir = remoteDir.isDirectory();
    if (!isDir) {
      throw new Exception(job.getRemoteDirectory()
          + " should be a directory");
    }

    // --- Check staging in (copying files to remote directory

    for (int i = 0; i < job.getFileStageInCount(); i++) {
      String[] files = job.getFileStageIn(i);
      String inPath = job.getLocalDirectory() + "/" + files[1];
      File in = new File(inPath);
      if (!in.exists()) {
        throw new Exception("Input file " + inPath + " does not exists");
      }

      String destPath = job.getRemoteDirectory() + "/" + files[0];
      File dest = new File(destPath);

      // --- Skip check in remote directory for the file with the same name

      try {
        copy(in, dest);
      } catch (Exception ex) {
        throw new Exception("Error transfering " + inPath + " to "
            + destPath
            + " : " + ex.getMessage());
      }
    }

    try {
      // Execute a command without arguments
      String command = null;
      if (job.getScheduler() != null
          && job.getScheduler().equalsIgnoreCase(JobDescription.PBS_SCHEDULER)) {
        command = "qsub " + job.getExecutable();
      } else {
        command = job.getExecutable();
      }
      //Process child = Runtime.getRuntime().exec(command, null, remoteDir);
      String[] cmdarray = new String[2];
      cmdarray[0] = "C:/MOPAC2000/mopac2002.exe";
      cmdarray[1] = "bpti.dat";
      Process child = Runtime.getRuntime().exec(cmdarray, null, remoteDir);
      child.waitFor();
      logger.info("Exit code: " + child.exitValue());
      // Get the output stream and read from it


      InputStream err = child.getErrorStream();

      // Get the input stream and read from it
      InputStream in = child.getInputStream();
      int c;
      StringWriter outWriter = new StringWriter();
      logger.info("Output stream:");
      while ((c = in.read()) != -1) {
        outWriter.write(c);
        System.out.print((char) c);
      }
      in.close();
      outWriter.close();

      logger.info("Stderr stream:");
      StringWriter errWriter = new StringWriter();
      while ((c = err.read()) != -1) {
        errWriter.write(c);
        System.err.print((char) c);
      }
      err.close();
      errWriter.close();
      String stderr = errWriter.toString();
      if (stderr.length() > 0) {
        throw new Exception(stderr);
      }

      // Inspect stdout for the job handle in the case of PBS
      if (job.getScheduler() != null && job.getScheduler().equalsIgnoreCase(JobDescription.PBS_SCHEDULER)) {
        String stdout = outWriter.toString();
        if (stdout.length() < 1) {
          throw new Exception("No job identifier was returned");
        }

        StringTokenizer st = new StringTokenizer(stdout, " .", true);
        // Look for a number
        while (st.hasMoreTokens()) {
          try {
            int number = Integer.parseInt(st.nextToken());
            jobHandle = String.valueOf(number);
            jobID = number;
            return;
          } catch (Exception ex) {
          }
        }
      }

    } catch (IOException e) {
      throw e;
    }

    // --- If we are here - there is an error
    if (job.getScheduler() != null && job.getScheduler().equalsIgnoreCase(JobDescription.PBS_SCHEDULER)) {
      throw new Exception("No job identifier was returned");
    }
  }

  public String getJobHandle() {
    return jobHandle;
  }

  // Copies src file to dst file.
  // If the dst file does not exist, it is created
  private void copy(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }

  //  implementation of GridProviderInterface functions
  @Override
  public String getName() {
    return "local";
  }

  @Override
  public Component getVisualComponent() {
    return nativeDialog;
  }

  @Override
  public FileChooserInterface getRemoteFileChooser() {
    return null;
  }

  @Override
  public String getRemoteHost() {
    return null;
  }

  @Override
  public boolean isPassOptionsToScheduler() {
    return true;
  }

  @Override
  public String submitTask(JobDescription job) throws Exception {
    submitJob(job);
    System.err.println(this.getClass().getName()
        + ": Warning : Job Handle is not set...");
    return null;
  }

  @Override
  public String getCommandsAsString() {
    if (windowsOS) {
      return "";
    }
    return "#!sh\n";
  }

  @Override
  public OperationalSystems getOS() {
    if (windowsOS) {
      return OperationalSystems.WindowsOS;
    }
    return OperationalSystems.Linux; // !!!
  }

  @Override
  public String getGatekeeper() {
    return null;
  }

  @Override
  public CheckPointInterface setCheckPoint(CheckPointInterface checkp) {
    return checkp;
  }

  @Override
  public String getJobStatus(CheckPoint chkp) throws Exception {
    return "getJobStatus is not implemented yet for Local provider";
  }

  @Override
  public void killJob(CheckPoint chkp) throws Exception {
    throw new Exception("Kill Job is not implemented for local provider");
  }

  @Override
  public boolean isRemoteDirectorySelectable() {
    return true;
  }

  public boolean transferFile(String source, String destination) throws Exception {
    throw new Exception("transferFile is not implemented for local provider");
  }

  @Override
  public boolean transferFile(String source, String destination, CheckPoint chkp) throws Exception {
    throw new Exception("transferFile is not implemented for local provider");
  }

  public static void main(String[] args) {
    JobDescription job = new JobDescription();
    job.setExecutable("C:/MOPAC2000/mopac2002.exe");
    job.setAbsPathExecutable(true);
    job.setLocalDirectory("C:/MOPAC2000");
    job.setRemoteDirectory("C:/TEMP");
    job.setFileStageIn("bpti.dat", "bpti.dat");

    LocalServiceProvider provider = new LocalServiceProvider();
    try {
      provider.submitJob(job);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public GridProviderInterface newInstance() {
    return new LocalServiceProvider();
  }
}
