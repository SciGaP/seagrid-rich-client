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

import java.util.logging.Logger;

import cct.grid.JobDescription;
import cct.tools.filebrowser.SFTPBrowser;

import com.sshtools.j2ssh.SftpClient;

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
public class SFTPFileStaging {
  static final Logger logger = Logger.getLogger(SFTPFileStaging.class.getCanonicalName());

   public SFTPFileStaging() {
   }

   public void fileStageIn(JobDescription job, boolean modal) throws Exception {

      // --- Setting local directory

      String localDirectory = "";
      if (job.getLocalDirectory() != null &&
          job.getLocalDirectory().trim().length() > 0) {
         localDirectory = job.getLocalDirectory();
         if (localDirectory.endsWith("/")) {
            localDirectory = localDirectory.substring(0,
                localDirectory.length() - 1);
         }
      }

      // --- Setting remote directory

      String remoteDirectory = "";
      if (job.getRemoteDirectory() != null &&
          job.getRemoteDirectory().trim().length() > 0) {
         remoteDirectory = job.getRemoteDirectory().trim();
         if (remoteDirectory.endsWith("/")) {
            remoteDirectory = remoteDirectory.substring(0,
                remoteDirectory.length() - 1);
         }
      }

      SFTPBrowser sftp = (SFTPBrowser) job.getTaskProviderObject();
      SftpClient sftp_client = null;
      try {
         sftp_client = sftp.getSftpClient();
      }
      catch (Exception ex) {
         throw new Exception("Failed to open sftp client: " + ex.getMessage());
      }

      try {
         sftp.setRemoteDirectory(remoteDirectory);
      }
      catch (Exception ex) {
         throw new Exception("Failed setting remote directory: " + ex.getMessage());
      }

      // --- Start file staging

      for (int i = 0; i < job.getFileStageInCount(); i++) {
         //fileTranferCompleted = false;
         String[] files = job.getFileStageIn(i);
         String localFile = job.getLocalDirectory() + "/" + files[1];
         logger.info("Transfering: " + localFile);
         try {
            sftp_client.put(localFile);
         }
         catch (Exception ex) {
            throw new Exception(localFile + " : " + ex.getMessage());
         }
      }
   }

}
