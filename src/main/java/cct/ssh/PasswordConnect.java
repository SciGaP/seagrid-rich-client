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
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.connection.ChannelState;
import com.sshtools.j2ssh.io.IOStreamConnector;
import com.sshtools.j2ssh.session.SessionChannelClient;

// JDK > 1.4 ONLY
/*import java.util.logging.FileHandler;
 import java.util.logging.Handler;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import java.util.logging.SimpleFormatter;*/
/**
 * Demonstrates a simple password connection to an SSH server.
 *
 * @author <A HREF="mailto:lee@sshtools.com">Lee David Painter</A>
 * @version $Id: PasswordConnect.java,v 1.12 2003/07/16 10:42:07 t_magicthize Exp $
 *
 * @created 20 December 2002
 */
public class PasswordConnect {

  static final Logger logger = Logger.getLogger(PasswordConnect.class.getCanonicalName());
  /**
    * The main program for the PasswordConnect class
    *
    * @param args The command line arguments
    */
   public static void main(String args[]) {
      try {
         // JDK > 1.4 ONLY
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
         ssh.setSocketTimeout(30000);
         SshConnectionProperties properties = new SshConnectionProperties();
         properties.setHost(hostname);
         properties.setPrefPublicKey("ssh-dss");
         // Connect to the host
         ssh.connect(properties);
         // Create a password authentication instance
         PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
         // Get the users name
         System.out.print("Username? ");
         // Read the password
         String username = reader.readLine();
         pwd.setUsername(username);
         // Get the password
         System.out.print("Password? ");
         String password = reader.readLine();
         pwd.setPassword(password);
         // Try the authentication
         int result = ssh.authenticate(pwd);
         // Evaluate the result
         if (result == AuthenticationProtocolState.COMPLETE) {
            // The connection is authenticated we can now do some real work!
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
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
