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

package cct.awtdialogs;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ConnectSQLServer
    extends Dialog implements ActionListener {
   TextField host, user, pass, port, database, db_type;
   boolean OKpressed = false;
   static final Logger logger = Logger.getLogger(ConnectSQLServer.class.getCanonicalName());
   public ConnectSQLServer(String Title, boolean modal) {
      super(new Frame(), Title, modal);

      GridLayout sizer = new GridLayout(0, 1, 2, 2);
      setLayout(sizer);

      Panel P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label hostname = new Label("Hostname:", Label.RIGHT);
      //String host_str = "jdbc:mysql://localhost/"; //parent.getSQLHostName();
      String host_str = "localhost"; //parent.getSQLHostName();
      host = new TextField(20);
      host.setText(host_str); //, host_str.length() > 100 ? host_str.length() : 100 ) ;

      P.add(hostname);
      P.add(host);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label username = new Label("Username:", Label.RIGHT);
      user = new TextField(20);

      P.add(username);
      P.add(user);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label password = new Label("Password:", Label.RIGHT);
      pass = new TextField(20);
      pass.setEchoChar('@');
      P.add(password);
      P.add(pass);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      database = new TextField(20);

      P.add(new Label("Database:", Label.RIGHT));
      P.add(database);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label port_l = new Label("Port:", Label.RIGHT);
      port = new TextField("3306", 10);
      port.setEnabled(false);
      P.add(port_l);
      P.add(port);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout(FlowLayout.RIGHT));

      Label type_l = new Label("Type:", Label.RIGHT);
      db_type = new TextField("mySQL", 10);
      db_type.setEnabled(false);

      P.add(type_l);
      P.add(db_type);
      add(P);

      P = new Panel();
      P.setLayout(new FlowLayout());

      Button OK = new Button("OK");
      Button Cancel = new Button("Cancel");

      P.add(OK);
      P.add(Cancel);
      add(P);

      /*
               add( hostname );
               add(host);

               add( username );
               add(user);

               add(password);
               add(pass);

               add( new Label( "Database:", Label.RIGHT ));
               add( database);

               add( port_l);
               add(port);

               add( type_l );

               add( OK );
       */

      host.addActionListener(this);
      user.addActionListener(this);
      pass.addActionListener(this);
      port.addActionListener(this);
      OK.addActionListener(this);
      Cancel.addActionListener(this);

      setSize(300, 250);
   }

   @Override
  public void actionPerformed(ActionEvent ae) {
      String arg = ae.getActionCommand();
      if (arg.equals("OK")) {
         OKpressed = true;
         setVisible(false);
         //dispose();
      }
      else if (arg.equals("Cancel")) {
         OKpressed = false;
         setVisible(false);
         //dispose();
      }
      else {
         logger.info("Event: " + arg);
      }
   }

   public String getDatabase() {
      return database.getText();
   }

   public String getHostname() {
      return host.getText();
   }

   public String getUsername() {
      return user.getText();
   }

   public String getPassword() {
      return pass.getText();
   }

   public boolean pressedOK() {
      return OKpressed;
   }

   public void setDatabase(String dbase) {
      database.setText(dbase);
   }

   public void setHostname(String hname) {
      host.setText(hname);
   }

   public void setUsername(String usern) {
      user.setText(usern);
   }

   public void setPassword(String password) {
      pass.setText(password);
   }

}
