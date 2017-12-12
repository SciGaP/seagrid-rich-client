package cct.ssh;

/*
 * Sshtools - Java SSH2 API
 *
 * Copyright (C) 2002 Lee David Painter.
 *
 * Written by: 2002 Lee David Painter <lee@sshtools.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.io.IOStreamConnector;
import com.sshtools.j2ssh.io.IOStreamConnectorState;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

/*import java.util.logging.FileHandler;
 import java.util.logging.Handler;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import java.util.logging.SimpleFormatter;*/
/**
 * Demonstrates a public key authentication connection to an SSH server.
 *
 * @author <A HREF="mailto:lee@sshtools.com">Lee David Painter</A>
 * @version $Id: PublicKeyConnect.java,v 1.8 2003/07/16 10:42:08 t_magicthize Exp $
 *
 * @created 20 December 2002
 */
public class PublicKeyConnect {

  static final Logger logger = Logger.getLogger(PublicKeyConnect.class.getCanonicalName());
   /**
    * The main program for the PublicKeyConnect class
    *
    * @param args The command line arguments
    */
   public static void main(String args[]) {
      try {
         // Setup a logfile
         /*Handler fh = new FileHandler("example.log");
                fh.setFormatter(new SimpleFormatter());
                Logger.getLogger("com.sshtools").setUseParentHandlers(false);
                Logger.getLogger("com.sshtools").addHandler(fh);
                Logger.getLogger("com.sshtools").setLevel(Level.ALL);*/
         // Configure J2SSH (This will attempt to install the bouncycastle provider
         // under jdk 1.3.1)
         ConfigurationLoader.initialize(false);
         BufferedReader reader =
             new BufferedReader(new InputStreamReader(System.in));
         System.out.print("Connect to host? ");
         String hostname = reader.readLine();
         // Make a client connection
         SshClient ssh = new SshClient();
         // Connect to the host
         ssh.connect(hostname);
         PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient();
         // Get the users name
         System.out.print("Username? ");
         String username = reader.readLine();
         pk.setUsername(username);
         // Get the private key file
         System.out.print("Path to private key file? ");
         String filename = reader.readLine();
         // Open up the private key file
         SshPrivateKeyFile file =
             SshPrivateKeyFile.parse(new File(filename));
         // If the private key is passphrase protected then ask for the passphrase
         String passphrase = null;
         if (file.isPassphraseProtected()) {
            System.out.print("Enter passphrase? ");
            passphrase = reader.readLine();
         }
         // Get the key
         SshPrivateKey key = file.toPrivateKey(passphrase);
         pk.setKey(key);
         // Try the authentication
         int result = ssh.authenticate(pk);
         // Evaluate the result
         if (result == AuthenticationProtocolState.COMPLETE) {
            // The connection is authenticated we can now do some real work!
            SessionChannelClient session = ssh.openSessionChannel();
            if (!session.requestPseudoTerminal("vt100", 80, 24, 0, 0, "")) {
               logger.info("Failed to allocate a pseudo terminal");
            }
            if (session.startShell()) {
               InputStream in = session.getInputStream();
               OutputStream out = session.getOutputStream();
               IOStreamConnector input =
                   new IOStreamConnector(System.in, session.getOutputStream());
               IOStreamConnector output =
                   new IOStreamConnector(session.getInputStream(), System.out);
               output.getState().waitForState(IOStreamConnectorState.CLOSED);
            }
            else {
               logger.info("Failed to start the users shell");
            }
            ssh.disconnect();
         }
         if (result == AuthenticationProtocolState.PARTIAL) {
            logger.info("Further authentication requried!");
         }
         if (result == AuthenticationProtocolState.FAILED) {
            logger.info("Authentication failed!");
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
