/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.database;

import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class DatabaseDriver {

  private final Logger logger = Logger.getLogger(DatabaseDriver.class.getName());
  private Level infoLevel = Level.INFO;

  private String dbType;

  private String className;

  private Connection connection = null;
  //
  private boolean usePort = true;
  private int defaultPort = 3306;
  //
  private boolean useUsername = true;
  private String defaultUsername = "admin";
  private String defaultDatabase = "database";
  //
  private boolean usePassword = true;
  private String defaultPassword = "";
  //
  private boolean useHostname = true;
  private String defaultHostname = "localhost";

  public void initializeDatabase(String dbEntry, String sqlScript) {

  }

  public String getDefaultDatabase() {
    return defaultDatabase;
  }

  public void setDefaultDatabase(String defaultDatabase) {
    this.defaultDatabase = defaultDatabase;
  }

  public boolean isUsePort() {
    return usePort;
  }

  public void setUsePort(boolean usePort) {
    this.usePort = usePort;
  }

  public int getDefaultPort() {
    return defaultPort;
  }

  public void setDefaultPort(int defaultPort) {
    this.defaultPort = defaultPort;
  }

  public boolean isUseUsername() {
    return useUsername;
  }

  public void setUseUsername(boolean useUsername) {
    this.useUsername = useUsername;
  }

  public String getDefaultUsername() {
    return defaultUsername;
  }

  public void setDefaultUsername(String defaultUsername) {
    this.defaultUsername = defaultUsername;
  }

  public boolean isUsePassword() {
    return usePassword;
  }

  public void setUsePassword(boolean usePassword) {
    this.usePassword = usePassword;
  }

  public String getDefaultPassword() {
    return defaultPassword;
  }

  public void setDefaultPassword(String defaultPassword) {
    this.defaultPassword = defaultPassword;
  }

  public boolean isUseHostname() {
    return useHostname;
  }

  public void setUseHostname(boolean useHostname) {
    this.useHostname = useHostname;
  }

  public String getDefaultHostname() {
    return defaultHostname;
  }

  public void setDefaultHostname(String defaultHostname) {
    this.defaultHostname = defaultHostname;
  }

  public Level getInfoLevel() {
    return infoLevel;
  }

  public void setInfoLevel(Level infoLevel) {
    infoLevel = infoLevel;
  }

  public DatabaseDriver(String className) throws Exception {
    this.className = className;
    Class.forName(className).newInstance();
  }

  public String getDbType() {
    return dbType;
  }

  public void setDbType(String dbType) {
    this.dbType = dbType;
  }

  public String getClassName() {
    return className;
  }

  public Connection getConnection() {
    return connection;
  }

  public Connection makeConnection(String databaseName) throws Exception {
    return makeConnection(databaseName, null, null, null);
  }


  public Connection makeConnection(String databaseName, String hostname, String username, char[] password) throws Exception {
   return makeConnection(databaseName, hostname, username, password, null);
  }

  public Connection makeConnection(String databaseName, String hostname, String username, char[] password, String params) throws Exception {
    //connection = DriverManager.getConnection("jdbc:sqlite:test.db");
    //String hostName = "jdbc:mysql://" + host; // !!!
    //Conn = DriverManager.getConnection(hostName, user, pass);
    String connStr = "jdbc:" + dbType + ":";
    if (useHostname) {
      connStr += "//" + hostname + "/";
    } else {
      connStr += ":" + databaseName;
    }
    if (params != null) {
      params = params.trim();
      connStr += "?" + params;
    }
    // ---
    try {
      if (params != null) {
        params = params.trim();
      }
      if (params != null && params.length() > 0) {
        connStr += "?" + params;
      }
      if (useHostname) {
        connection = DriverManager.getConnection(connStr, username, String.valueOf(password));
      } else {
        connection = DriverManager.getConnection(connStr);
      }
    } catch (Exception ex) {
      throw new Exception("Unable establish connection " + connStr + " : " + ex.getLocalizedMessage());
    }
    if (logger.isLoggable(infoLevel)) {
      logger.log(infoLevel, "Connection to " + connStr + " was successfully established");
    }
    // --- 
    if (useHostname) {
      try {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("use " + databaseName);
      } catch (Exception ex) {
        throw new Exception("Unable to select database " + databaseName + " on a host " + connStr + " : "
            + ex.getLocalizedMessage());
      }
      if (logger.isLoggable(infoLevel)) {
        logger.log(infoLevel, "Database " + databaseName + " on a host " + connStr + " was successfully selected");
      }
    }

    return connection;
  }

  public void executeSQLScript(String scriptFile) throws Exception {

    Statement statement = connection.createStatement();

    BufferedReader in = new BufferedReader(new FileReader(scriptFile));
    String line;
    StringBuilder query = new StringBuilder(1024);
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("-- ") || line.startsWith("#")) {
        continue;
      }
      query.append(line + "\n");
      if (line.endsWith(";")) {
        try {
          statement.execute(query.toString());
          query.delete(0, query.length());
        } catch (Exception ex) {
          throw new Exception("Cannot execute query =====\n" + query.toString() + "\n======================\nReason: "
              + ex.getMessage());
        }
      }
    }

    System.out.println("SQL Script " + scriptFile + " is successfuly executed");
  }

  public static void main(String[] args) {
    String driverClass = "com.mysql.jdbc.Driver";
    String db = "jamberoo";
    String user = "jamberoo";
    String pass = "jamberoo";
    String host = "localhost";
    DatabaseDriver dd = null;
    Connection conn = null;
    try {
      dd = new DatabaseDriver(driverClass);
      dd.setDbType("mysql");
      System.out.println(driverClass + " was successfully loaded...");
    } catch (Exception ex) {
      Logger.getLogger(DatabaseDriver.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(1);
    }
    Object[] options = {"Yes", "No"};
    int n = JOptionPane.showOptionDialog(null, "Do you want to use the defaults for DB", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
        null, options, options[0]);
    if (n == JOptionPane.YES_OPTION) {
      System.out.println("Default DB parameters will be used...");

    } else {
      System.out.println("No was selected");
    }

    try {
      conn = dd.makeConnection(db, host, user, pass.toCharArray());
      System.out.println("Connection to db " + db + " on " + host + " as " + user + " was created...");
    } catch (Exception ex) {
      Logger.getLogger(DatabaseDriver.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(1);
    }

    n = JOptionPane.showOptionDialog(null, "Reinitialize DB?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
        null, options, options[0]);
    if (n == JOptionPane.YES_OPTION) {
      System.out.println("Reinitializing DB...");
      try {
        dd.executeSQLScript("/home/vvv900/SVN/Jamberoo/trunk/current/etc/sql/jamberoo.sql");
      } catch (Exception ex) {
        Logger.getLogger(DatabaseDriver.class.getName()).log(Level.SEVERE, null, ex);
        System.exit(1);
      }

    }

    n = JOptionPane.showOptionDialog(null, "Add Molecule?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
        null, options, options[0]);
    if (n == JOptionPane.NO_OPTION) {
      System.exit(0);
    }

    System.exit(0);
  }
}
