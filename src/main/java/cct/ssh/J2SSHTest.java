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

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import com.sshtools.common.authentication.PasswordAuthenticationDialog;
import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.KBIAuthenticationClient;
import com.sshtools.j2ssh.authentication.KBIPrompt;
import com.sshtools.j2ssh.authentication.KBIRequestHandler;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.connection.ChannelState;
import com.sshtools.j2ssh.io.IOStreamConnector;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

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
public class J2SSHTest {
   private static BufferedReader reader =
       new BufferedReader(new InputStreamReader(System.in));
   static final Logger logger = Logger.getLogger(J2SSHTest.class.getCanonicalName());
   public J2SSHTest() {

      String username = "vvv900";

      try {
         ConfigurationLoader.initialize(false);

         // Further code will be added here
         SshClient ssh = new SshClient();

         System.out.print("Host to connect: ");

         String hostname = "ac.anu.edu.au";

         // Making the initial connection

         ssh.connect(hostname, new IgnoreHostKeyVerification());

         logger.info("System banner:\n" + ssh.getAuthenticationBanner(500));

         // Retrieving the available authentication Methods

// It is possible at any time after the connection has been established
// and before authentication has been completed to request a list of authentication methods that can be used.
// The getAvailableAuthMethods method returns a list of authentication method names.

         List available = ssh.getAvailableAuthMethods(username);

         logger.info("Available Authentication Methods:");
         for (int i = 0; i < available.size(); i++) {
            logger.info(available.get(i).toString());
         }

         int result = AuthenticationProtocolState.FAILED;

         if (available.contains("password")) {
            PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();

            PasswordAuthenticationDialog dialog = new PasswordAuthenticationDialog(new
                Frame());
            //dialog.setVisible(true);

            pwd.setAuthenticationPrompt(dialog); // ???

            //System.out.print("Username: ");
            //String username = reader.readLine();
            pwd.setUsername(username);

            //System.out.print("Password: ");
            //String password = reader.readLine();
            pwd.setPassword("xxx");

            result = ssh.authenticate(pwd);

         }
         else if (available.contains("keyboard-interactive")) {
            /*
             * Create the keyboard-interactive instance
             */
            KBIAuthenticationClient kbi = new KBIAuthenticationClient();
            // Get the users name
            System.out.print("Username? ");
            // Read the password
            username = reader.readLine();
            kbi.setUsername(username);

            // Set the callback interface
            kbi.setKBIRequestHandler(new KBIRequestHandler() {
               @Override
              public void showPrompts(String name, String instructions,
                                       KBIPrompt[] prompts) {
                  logger.info(name);
                  logger.info(instructions);
                  String response;
                  if (prompts != null) {
                     for (int i = 0; i < prompts.length; i++) {
                        System.out.print(prompts[i].getPrompt() + ": ");
                        try {
                           response = reader.readLine();
                           prompts[i].setResponse(response);
                        }
                        catch (IOException ex) {
                           prompts[i].setResponse("");
                           ex.printStackTrace();
                        }
                     }
                  }
               }
            });

            // Try the authentication
            result = ssh.authenticate(kbi);
         }

//It should be noted that the SSH specification allows the server to return authentication methods that are not valid
//for the user.


         // In the default implementation of the connect method, J2SSH reads the $HOME/.ssh/known_hosts file
         // to determines to which hosts connections may be allowed.
         // This is provided by the class ConsoleKnownHostsKeyVerification and the
         // default behavior can be emulated by the following code:

         //import com.sshtools.j2ssh.transport.ConsoleKnownHostsKeyVerification;


         // ssh.connect("firestar", new ConsoleKnownHostsKeyVerification());

         // When the connect method returns, the protocol has been negotiated and key exchange has taken place,
         // leaving the connection ready for authenticating the user.

         // Authenticating the user

         // Once the connection has been completed the user is required to provide a set of credentials for authentication.
         // All client side authentication methods are implemented using the abstract class:

         // import com.sshtools.j2ssh.authentication.SshAuthenticationClient.

         // To perform authentication, the SshClient class provides the following method:

         // public int authenticate(SshAuthenticationClient auth);

         // There are currently five authentication methods implemented by J2SSH, 'password', 'publickey',
         // 'keyboard-interactive' and 'hostbased'. With an extra agent authentication method
         // that performs public key authentication using the J2SSH key agent.

         /**
          * Create a PasswordAuthenticationClient instance, set the properties
          * and pass to the SessionClient to authenticate
          */


         // The Authentication Result

         // When the authentication method completes it returns the result of the authentication. This integer value can be any
         // of the following three values defined in the class:

         if (result == AuthenticationProtocolState.FAILED) {
            logger.info("The authentication failed");
         }

         if (result == AuthenticationProtocolState.PARTIAL) {
            logger.info("The authentication succeeded but another"
                               + "authentication is required");
         }

         if (result == AuthenticationProtocolState.COMPLETE) {
            logger.info("The authentication is complete");

            SessionChannelClient session = ssh.openSessionChannel();
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
            ssh.disconnect();

         }

         // SFTP

         SftpClient sftp = ssh.openSftpClient();
         List fileList = sftp.ls();

         logger.info("Remote Directory List:");
         for (int i = 0; i < fileList.size(); i++) {
            SftpFile file = (SftpFile) fileList.get(i);

            logger.info(file.getFilename() + " " + file.getAbsolutePath());
         }

         // The session channel provides an inputstream and outputstream for reading/writing,
         // but before we can do this we need to setup the channel for our command.
         // First we open the channel itself by calling the SshClient method:


         SessionChannelClient session = ssh.openSessionChannel();

         // Now that we have a session instance we need to configure it for our command,
         // there are several options that can be set before we invoke one of the methods that will start the session.

         // Setting Environment Variables

         //  public boolean setEnvironmentVariable(String name, String value);

         // Requesting a Pseudo Terminal

         // A pseudo terminal is a device that imitates a terminal. Rather than being connected to an actual terminal,
         // a pseudo-terminal (or pty) is connected to a process. If the command you are executing is expecting a terminal
         // (such as a shell command) you can request that a pseudo terminal be attached to the process by calling the requestPseudoTerminal method.

         logger.info("Opening pseudoterminal...");
         session.requestPseudoTerminal("ansi", 80, 25, 0, 0, "");
         logger.info("Opened pseudoterminal");

         // Invoking a command

         //After the above operations have been performed you can then request that the session
         // either start the user's shell, execute a specific command or start an SSH subsystem (such as SFTP).
         // You should not invoke a subsystem unless you are able to read/write the subsystem protocol,
         // there are many additional utilities within J2SSH that provide for the available subsystems.

         //session.startShell();

         logger.info("Issuing command...");
         session.executeCommand("z01");
         session.executeCommand("date");
         logger.info("Issued command");

         // Handling Session Data

         // Once the session has been configured and a command or shell has been started, you can begin to
         // transfer data to and from the remote computer using the sessions IO streams. These streams provide you with a
         // standardized interface for reading and writing the data.

         // The Session Channel's OutputStream
         // The format of writing data varies according to how you configured the session, for example if you executed the
         // users shell then the data should be written as if the user had entered the commands interactively.


         /** Writing to the session OutputStream */
         /*
                OutputStream out = session.getOutputStream();
                String cmd = "ls\n";
                out.write(cmd.getBytes());
          */

         // The Session Channel's InputStream

         /**
          * Reading from the session InputStream
          */
         InputStream in = session.getInputStream();
         byte buffer[] = new byte[255];
         int read;
         while ( (read = in.read(buffer)) > 0) {
            String outstr = new String(buffer, 0, read);
            logger.info(outstr);
         }

         // Reading from stderr
         // The session also provides the stderr data provided by the remote session. Again an InputStream is provided.

         //public InputStream session.getStderrInputStream();

         // Closing the Session

         // The session can be closed using the following method:

         session.close();

         ssh.disconnect();
      }

      catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static void main(String[] args) {
      J2SSHTest j2sshtest = new J2SSHTest();
   }
}
