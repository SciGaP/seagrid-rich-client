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
package cct.database;

import cct.GlobalSettings;
import cct.awtdialogs.ConnectSQLServer;
import cct.awtdialogs.MessageWindow;
import cct.j3d.Java3dUniverse;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class DatabaseManager implements ActionListener {
  
  static String JDBC_KEY = "jdbc";
  static String DRIVER_KEY = "driver";
  static String HOST_KEY = "default_host";
  static String USE_HOST_KEY = "use_host";
  static String PORT_KEY = "default_port";
  static String USE_PORT_KEY = "use_port";
  static String USER_KEY = "default_user";
  static String USE_USER_KEY = "use_user";
  static String PASSWORD_KEY = "default_password";
  static String USE_PASSWORD_KEY = "use_password";
  static String DATABASE_KEY = "default_database";
  static String JDBC_DRIVERS_KEY = JDBC_KEY + ".drivers";
  static String DATABASE_CREATE_SCRIPT_KEY = "database.create.script";

  // ---
  private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
  private static Level infoLevel = Level.INFO;
  
  static protected Map<String, DabaseEntry> databaseEntries = new HashMap<String, DabaseEntry>();
  static protected Map<String, DatabaseDriver> databaseDrivers = new HashMap<String, DatabaseDriver>();

  // -------------------
  static protected ConnectSQLServer sqlServerDialog = null;
  protected SQLDatabaseAccess databaseAccess = null;
  
  public new_SQLChemistryDatabase new_sqlChemistryDatabase = null;
  public new_ChemistryDatabaseDialog new_chemistryDatabase = null;
  
  public SQLChemistryDatabase sqlChemistryDatabase = null;
  public ChemistryDatabaseDialog chemistryDatabase = null;
  
  protected Java3dUniverse target = null;
  
  protected boolean connectedToDB = false;
  private String databaseCreateScript;
  private URL databaseCreateScriptURL;
  
  static protected JMenuItem jMenuDBLogin = null;
  static protected JMenuItem jMenuDBLogout = null;
  static protected JButton jDBLogin = new JButton();

  //static private DatabaseManager dummy = new DatabaseManager();
  public DatabaseManager() {
    if (jMenuDBLogin == null) {
      jMenuDBLogin = new JMenuItem("Login");
      jMenuDBLogin.setToolTipText("Login Database");
      jMenuDBLogin.addActionListener(this);
    }
    // --- 
    if (jMenuDBLogout == null) {
      jMenuDBLogout = new JMenuItem("Logout");
      jMenuDBLogout.setEnabled(false);
      jMenuDBLogout.addActionListener(this);
    }
    
    if (jDBLogin == null) {
      jDBLogin = new JButton();
      //jDBLogin.setIcon(dbImage);
      jDBLogin.setToolTipText("Login/Logout Database");
      jDBLogin.addActionListener(this);
    }
    
    if (logger.isLoggable(infoLevel)) {
      logger.log(infoLevel, "Available Drivers");
      for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
        logger.log(infoLevel, e.nextElement().toString());
      }
    }
  }
  
  public void resetDBEntry(String dbEntry) throws Exception {
    DabaseEntry entry = databaseEntries.get(dbEntry);
    if (entry == null) {
      throw new Exception("No such database entry: " + dbEntry);
    }
    if (this.databaseCreateScript == null) {
      throw new Exception("Database creation script is not defined");
    }
    if (databaseCreateScriptURL == null) {
      throw new Exception("No valid URL to the Database creation script");
    }
    String path = this.databaseCreateScriptURL.getPath();
    SQLTools.executeSQLScript(path, entry.getConnection(), entry.getDatabaseDriver().getDbType());
  }
  
  public String[] getDBEntriesAsArray() {
    Object[] obj = databaseEntries.keySet().toArray();
    String[] entries = new String[obj.length];
    for (int i = 0; i < obj.length; i++) {
      entries[i] = obj[i].toString();
    }
    return entries;
  }
  
  public String[] getDBEntriesAsSortedArray() {
    String[] entries = getDBEntriesAsArray();
    Arrays.sort(entries);
    return entries;
  }
  
  public String makeAndRegisterConnection(DatabaseDriver dbDriver, String databaseName, String hostname,
      String username, char[] password) throws Exception {
    Connection conn = dbDriver.makeConnection(databaseName, hostname, username, password);
    DabaseEntry dabaseEntry = new DabaseEntry(dbDriver, databaseName, hostname, username, password);
    databaseEntries.put(dabaseEntry.getDbEntryName(), dabaseEntry);
    return dabaseEntry.getDbEntryName();
  }
  
  public Connection getConnection(String connectionName) {
    return databaseEntries.get(connectionName).getConnection();
  }
  
  public Map<String, DatabaseDriver> getDatabaseDrivers() {
    return databaseDrivers;
  }
  
  public DatabaseDriver getDatabaseDriver(String driverName) {
    return databaseDrivers.get(driverName);
  }
  
  public void setDatabaseDrivers(Map<String, DatabaseDriver> databaseDrivers) {
    DatabaseManager.databaseDrivers = databaseDrivers;
  }
  
  public void initiateFromProperties(Properties properties) throws Exception {
    String driversSt = properties.getProperty(JDBC_DRIVERS_KEY);
    if (driversSt == null) {
      throw new Exception("No jdbc driver(s) information in properties file");
    }
    String[] drivers = driversSt.trim().split("\\s+");
    
    Set<String> drs = new HashSet<String>();
    for (String i : drivers) {
      drs.add(i);
    }
    
    if (logger.isLoggable(infoLevel)) {
      logger.log(infoLevel, "Number of DB driver(s) in properties: " + drivers.length + " Unique drivers: " + drs.size());
    }
    
    for (String i : drivers) {
      String key = JDBC_KEY + "." + i + "." + DRIVER_KEY;
      String className = properties.getProperty(key);
      if (className == null) {
        logger.severe("No key " + key + " in properties");
        continue;
      }
      // ---
      try {
        DatabaseDriver driver = loadDriver(className);
        if (logger.isLoggable(infoLevel)) {
          logger.log(infoLevel, "Driver class " + className + " was successfully loaded");
        }
        driver.setDbType(i);
        // ---
        key = JDBC_KEY + "." + i + "." + HOST_KEY;
        className = properties.getProperty(key, "");
        driver.setDefaultHostname(className);
        // ---
        key = JDBC_KEY + "." + i + "." + USE_HOST_KEY;
        className = properties.getProperty(key, "true");
        try {
          boolean bool = Boolean.parseBoolean(className);
          driver.setUseHostname(bool);
        } catch (Exception ex) {
          
        }
        // ---   

        key = JDBC_KEY + "." + i + "." + PORT_KEY;
        className = properties.getProperty(key, "3306");
        try {
          int port = Integer.parseInt(className);
          driver.setDefaultPort(port);
        } catch (Exception ex) {
          
        }
        // ---
        key = JDBC_KEY + "." + i + "." + USE_PORT_KEY;
        className = properties.getProperty(key, "true");
        try {
          boolean bool = Boolean.parseBoolean(className);
          driver.setUsePort(bool);
        } catch (Exception ex) {
          
        }
        // ---
        key = JDBC_KEY + "." + i + "." + USER_KEY;
        className = properties.getProperty(key, "");
        driver.setDefaultUsername(className);
        // ---
        key = JDBC_KEY + "." + i + "." + USE_USER_KEY;
        className = properties.getProperty(key, "true");
        try {
          boolean bool = Boolean.parseBoolean(className);
          driver.setUseUsername(bool);
        } catch (Exception ex) {
          
        }
        // ---
        key = JDBC_KEY + "." + i + "." + PASSWORD_KEY;
        className = properties.getProperty(key, "");
        driver.setDefaultPassword(className);
        // ---
        key = JDBC_KEY + "." + i + "." + USE_PASSWORD_KEY;
        className = properties.getProperty(key, "true");
        try {
          boolean bool = Boolean.parseBoolean(className);
          driver.setUsePassword(bool);
        } catch (Exception ex) {
          
        }
        // ---
        key = JDBC_KEY + "." + i + "." + DATABASE_KEY;
        className = properties.getProperty(key, "jamberoo");
        driver.setDefaultDatabase(className);
        // ---
        databaseDrivers.put(i, driver);
      } catch (Exception ex) {
        logger.severe("Error loading driver class " + className + " : " + ex.getMessage());
        continue;
      }
      
      databaseCreateScript = properties.getProperty(DATABASE_CREATE_SCRIPT_KEY);
      if (this.databaseCreateScript == null) {
        if (logger.isLoggable(Level.WARNING)) {
          logger.log(Level.WARNING, "Database Creation script is not defined");
        }
      } else {
        try {
          if (databaseCreateScript.matches("\\w+[.]sql")) {
            databaseCreateScriptURL = this.getClass().getResource(databaseCreateScript);
          } else {
            databaseCreateScriptURL = new URL(databaseCreateScript);
          }
        } catch (Exception ex) {
          logger.severe("Unable to open Database Creation script as " + databaseCreateScriptURL.getPath() + " : " + ex.getMessage());
        }
      }
      
    }
    
  }
  
  public DatabaseDriver loadDriver(String name) throws Exception {
    return new DatabaseDriver(name);
  }
  
  public boolean connectToDatabase() {

    /*
     // Invoke a dialog to get hostname, user name, etc...
     //ConnectSQLServer server = new ConnectSQLServer(menuFrame,
     // So, now we can open only one database during a session
     if (sqlServerDialog == null) {
     sqlServerDialog = new ConnectSQLServer("Connect to Database Server", true);
     if (sqlServerDialog.getDatabase().length() == 0) {
     sqlServerDialog.setDatabase("chemistry");
     }

     //server.dispose();
     //menuFrame.sqlServerDialog.setVisible(false);
     }

     if (!connectedToDB) {
     sqlServerDialog.setVisible(true);
     }

     if (!sqlServerDialog.pressedOK()) {
     return false;
     }

     String host = sqlServerDialog.getHostname();
     String user = sqlServerDialog.getUsername();
     String pass = sqlServerDialog.getPassword();
     String database = sqlServerDialog.getDatabase();

     // --- Connect to the SQL Server
     // --- Load connector and make connection
     if (databaseAccess == null) {
     databaseAccess = new SQLDatabaseAccess();
     if (databaseAccess.wasError()) {
     JOptionPane.showMessageDialog(new JFrame(),
     databaseAccess.getErrorMessage(),
     "Error",
     JOptionPane.ERROR_MESSAGE);
     return false;

     }
     }
     if (!databaseAccess.isDriverLoaded()) {
     return false;
     }
     if (!databaseAccess.getConnection(host, user, pass)) {
     return false;
     }
     if (!databaseAccess.selectDatabase(database)) {
     return false;
     }

     connectedToDB = true;

     //SQLChemistryDatabase sql_data = new SQLChemistryDatabase(host,
     // --- Testing new database
     if (cct.GlobalSettings.isNewDatabase()) {
     if (new_sqlChemistryDatabase == null) {
     new_sqlChemistryDatabase = new new_SQLChemistryDatabase(
     databaseAccess, host,
     user, pass, database);
     //new_sqlChemistryDatabase.setParentFrame(parentFrame);
     }
     if (new_sqlChemistryDatabase.isError()) {
     logger.info("connectToDatabase: " + new_sqlChemistryDatabase.getErrorMessage());
     MessageWindow er = new MessageWindow("ERROR",
     new_sqlChemistryDatabase.getErrorMessage(), true);
     er.setVisible(true);
     } else {
     //ConnectSQLServer sql = new ConnectSQLServer( menuFrame, "Connect to SQL Server", true );
     if (new_chemistryDatabase == null) {
     new_chemistryDatabase = new new_ChemistryDatabaseDialog(target, new_sqlChemistryDatabase,
     "Chemical Database", false);
     }
     new_chemistryDatabase.setVisible(true);

     }

     } else {
     // --- Old Database Design
     if (sqlChemistryDatabase == null) {
     sqlChemistryDatabase = new SQLChemistryDatabase(
     databaseAccess, host,
     user, pass, database);
     }

     if (sqlChemistryDatabase.isError()) {
     logger.info(": "
     + sqlChemistryDatabase.getErrorMessage());
     MessageWindow er = new MessageWindow("ERROR",
     sqlChemistryDatabase.getErrorMessage(), true);
     er.setVisible(true);
     } else {
     //ConnectSQLServer sql = new ConnectSQLServer( menuFrame, "Connect to SQL Server", true );
     if (chemistryDatabase == null) {
     chemistryDatabase = new ChemistryDatabaseDialog(target, sqlChemistryDatabase,
     "Chemical Database", false);
     }
     chemistryDatabase.setVisible(true);

     }
     }
     */
    return true;
  }
  
  public Java3dUniverse getTarget() {
    return target;
  }
  
  public void setTarget(Java3dUniverse target) {
    this.target = target;
  }
  
  public List<JMenuItem> getMenuItems() {
    List<JMenuItem> items = new ArrayList<JMenuItem>();
    items.add(jMenuDBLogin);
    items.add(jMenuDBLogout);
    return items;
  }
  
  public void actionPerformed(ActionEvent actionEvent) {
    if (jMenuDBLogin == actionEvent.getSource() || jDBLogin == actionEvent.getSource()) {
      
      if (connectToDatabase()) {
        jMenuDBLogin.setText("Save/Retrieve Data");
        jMenuDBLogin.setToolTipText(
            "Save/Retrieve Data to/from Database");
        jMenuDBLogout.setEnabled(true);
      }
    } else if (jMenuDBLogout == actionEvent.getSource()) {
      //java3dUniverse.logoutDatabase();
      jMenuDBLogin.setText("Login Database");
      jMenuDBLogin.setToolTipText("Login Database");
      jMenuDBLogout.setEnabled(false);
    }
  }
  
  public static void main(String[] args) {
    DatabaseManager dbm = new DatabaseManager();
    try {
      dbm.initiateFromProperties(GlobalSettings.getProperties());
      
    } catch (Exception ex) {
      Logger.getLogger(DatabaseDriver.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.exit(0);
    
  }
  
}

class DabaseEntry {
  
  private DatabaseDriver databaseDriver;
  private String databaseName, hostname, username;
  private char[] password;
  private String dbEntryName = "";

  // ---
  private Connection connection = null;
  
  public DabaseEntry(DatabaseDriver databaseDriver, String databaseName, String hostname, String username, char[] password) {
    this.databaseDriver = databaseDriver;
    this.databaseName = databaseName;
    this.hostname = hostname;
    this.username = username;
    this.password = password;
    connection = databaseDriver.getConnection();
    dbEntryName = databaseDriver.getDbType() + ":" + databaseName
        + ((hostname != null && hostname.length() > 0) ? " on " + hostname : "")
        + ((username != null && username.length() > 0) ? " as " + username : "");
  }
  
  public DatabaseDriver getDatabaseDriver() {
    return databaseDriver;
  }
  
  public String getDbEntryName() {
    return dbEntryName;
  }
  
  public Connection getConnection() {
    //connection.isValid(1);
    return connection;
  }
  
}
