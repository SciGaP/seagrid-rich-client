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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SQLDatabaseAccess
    implements DatabaseAccess {
  static boolean SuccessLoadDriver = false;
  Connection Conn = null;
  Statement stmt = null;
  PreparedStatement pstmt = null;
  ResultSet rs = null;
  boolean errorHappened = false;
  String errorMessage = "No error messages yet";
  static final Logger logger = Logger.getLogger(SQLDatabaseAccess.class.getCanonicalName());

  public SQLDatabaseAccess() {
    try {
      // The newInstance() call is a work around for some
      // broken Java implementations
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      SuccessLoadDriver = true;
    }
    catch (Exception ex) {
      errorHappened = true;
      errorMessage = "Cannot load driver: " + ex.getMessage();
      logger.info(errorMessage);
      // handle the error
      SuccessLoadDriver = false;
    }

  }

  public boolean isDriverLoaded() {
    return SuccessLoadDriver;
  }

  public boolean wasError() {
    return errorHappened;
  }

  public boolean getConnection(String host, String user, String pass) {

    if (!SuccessLoadDriver) {
      return false;
    }

    try {
      logger.info("Available Drivers");
      for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
        logger.info(e.nextElement().toString());
      }

      //conn = DriverManager.getConnection(
      //        "jdbc:mysql://localhost/test?user=vlad&password=victor");
      String hostName = "jdbc:mysql://" + host; // !!!
      if (!hostName.endsWith("/")) {
        hostName = hostName + "/";
      }

      logger.info("Connecting to " + hostName + " as " + user);
      Conn = DriverManager.getConnection(hostName, user, pass);
      //"jdbc:mysql://localhost/test?user=vlad&password=victor");
      // Do something with the Connection

    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "Cannot connect to database: SQLException: " +
          ex.getMessage();
      logger.info(errorMessage);
      //logger.info("SQLException: " + ex.getMessage());
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());

      try {
        Driver dr = DriverManager.getDriver(host);
        DriverManager.registerDriver(dr);
        Conn = DriverManager.getConnection(host, user, pass);
      }
      catch (SQLException exep) {
        logger.info(
            "Cannot  locate a driver that understands the given URL" +
            " " + host);
      }
      return false;
    }
    return true;
  }

  /**
   *
   * @return Connection
   */
  public Connection getConnection() {
    return Conn;
  }

  public boolean closeConnection() {
    if (!SuccessLoadDriver || Conn == null) {
      return true; // Connection is already closed
    }

    try {
      Conn.close();
    }
    catch (SQLException er) {

    }
    return true;
  }

  /**
   *
   * @return String
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   *
   * @param database String
   * @return boolean
   */
  public boolean selectDatabase(String database) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str;
    try {
      str = "use " + database;
      logger.info("SQL Query: " + str);
      stmt = Conn.createStatement();
      stmt.executeUpdate(str);
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return true;
  }

  /**
   *
   * @param tableName String
   * @return ArrayList
   */
  public List getTableMetaData(String tableName) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    ResultSetMetaData rsmd;
    String str = null;

    List tableMD = null;

    try {
      stmt = Conn.createStatement();
      str = "select * from " + tableName;
      logger.info("SQL Query: " + str);
      if (!stmt.execute(str)) {
        return null;
      }
      rs = stmt.getResultSet();
      rsmd = rs.getMetaData();

      if (rsmd.getColumnCount() > 0) {
        tableMD = new ArrayList();
      }
      else {
        return null;
      }

      for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        Map info = new HashMap();
        info.put(COLUMN_NAME, rsmd.getColumnName(i));
        logger.info("Column name: " + rsmd.getColumnName(i));
        info.put(COLUMN_TYPE, rsmd.getColumnTypeName(i));
        logger.info("Column SQL Type: " +
                           rsmd.getColumnTypeName(i));
        tableMD.add(info);
      }
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return tableMD;
  }

  /**
   *
   * @param tableName String
   * @return ArrayList
   */
  public List getColumnsFromTable(String tableName, String Columns) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    ResultSetMetaData rsmd;
    String str = null;

    List columns = null;

    try {
      stmt = Conn.createStatement();
      str = "select " + Columns + " from " + tableName;
      logger.info("SQL Query: " + str);
      if (!stmt.execute(str)) {
        return null;
      }
      rs = stmt.getResultSet();
      rsmd = rs.getMetaData();
      if (rsmd.getColumnCount() < 1) {
        return null;
      }

      columns = new ArrayList();

      while (rs.next()) {
        Map info = new HashMap();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("CHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("VARCHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("TEXT") ==
              0) {
            info.put(rsmd.getColumnName(i), rs.getString(i));
          }
          else
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("FLOAT") ==
              0) {
            info.put(rsmd.getColumnName(i), new Float(rs.getFloat(i)));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase("INTEGER") ==
                   0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase(
              "INTEGER UNSIGNED") == 0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }

          // --- General case
          else {
            info.put(rsmd.getColumnName(i), rs.getString(i));
          }

        }
        columns.add(info);
        //logger.info(rs.getString(1));
      }

    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return columns;
  }

  /**
   *
   * @param tableName String
   * @param Columns String
   * @param condition String - Should be prepared by user
   * @return ArrayList
   */
  public List getSelectedColumnsFromTable(String tableName,
                                          String Columns,
                                          String condition) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    ResultSetMetaData rsmd;
    String str = null;

    List columns = null;

    try {
      stmt = Conn.createStatement();
      str = "select " + Columns + " from " + tableName + " where " +
          condition;
      logger.info("getSelectedColumnsFromTable: SQL Query: " + str);
      if (!stmt.execute(str)) {
        return null;
      }
      rs = stmt.getResultSet();
      rsmd = rs.getMetaData();
      if (rsmd.getColumnCount() < 1) {
        return null;
      }

      columns = new ArrayList();

      while (rs.next()) {
        Map info = new HashMap();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("CHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("VARCHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("TEXT") ==
              0) {
            info.put(rsmd.getColumnName(i), rs.getString(i));
            logger.info(rsmd.getColumnName(i) + " " +
                               rs.getString(i));
          }
          else
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("FLOAT") ==
              0) {
            info.put(rsmd.getColumnName(i), new Float(rs.getFloat(i)));
            logger.info(rsmd.getColumnName(i) + " " +
                               rs.getFloat(i));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase("INTEGER") ==
                   0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase(
              "INTEGER UNSIGNED") == 0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase("INT") ==
                   0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
            logger.info(rsmd.getColumnName(i) + " " +
                               rs.getString(i));
          }

          // --- General case
          else {
            info.put(rsmd.getColumnName(i), rs.getString(i));
            logger.info(rsmd.getColumnName(i) + " " +
                               rs.getString(i));
          }

        }
        columns.add(info);
        //logger.info(rs.getString(1));
      }
      errorHappened = false;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "getSelectedColumnsFromTable: SQLException: " +
          ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return columns;
  }

  public boolean updateItem(String Table, String columnName, String oldValue,
                            String newValue) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = null;
    try {
      stmt = Conn.createStatement();
      str = "UPDATE " + Table + " SET " + columnName +
          " = '" +
          newValue + "' WHERE " + columnName + " LIKE '" +
          oldValue + "'";

      logger.info("SQLState: " + str);
      stmt.executeUpdate(str);
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return true;
  }

  public boolean updateItemsUsingCondition(String Table, String Values,
                                           String condition) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = null;
    try {
      stmt = Conn.createStatement();
      str = "UPDATE " + Table + " SET " + Values + " WHERE " + condition;

      logger.info("SQLState: " + str);
      stmt.executeUpdate(str);
      errorHappened = false;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return true;
  }

  public boolean addStringItem(String Table, String columnName,
                               String newValue) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str;
    try {
      stmt = Conn.createStatement();
      str = "INSERT INTO " + Table + " (" + columnName +
          ") " +
          " VALUES ('" + newValue + "')";
      logger.info("SQL Query: " + str);
      stmt.executeUpdate(str);
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return true;
  }

  /**
   *
   * @param Table String
   * @param columnNames String
   * @param newValues String
   * @return boolean
   */
  public boolean addManyStringItems(String Table, String columnNames,
                                    String newValues) {
    if (Conn == null) {
      return false;
    }

    String columns[] = columnNames.split(",");
    String values[] = newValues.split(",");
    return addManyStringItems(Table, columns, values);

  }

  /**
   *
   * @param Table String
   * @param columnNames String[]
   * @param newValues String[]
   * @return boolean
   */
  public boolean addManyStringItems(String Table, String columnNames[],
                                    String newValues[]) {
    if (Conn == null) {
      return false;
    }

    // Error check

    if ( (columnNames.length != newValues.length) || columnNames.length == 0) {
      errorHappened = true;
      errorMessage =
          "addManyStringItems: Input parameters Error: (columnNames.length != newValues.length ) || columnNames.length == 0";
      return false;
    }
    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "INSERT INTO " + Table + " (";

    // Form columns names
    str += columnNames[0];
    for (int i = 1; i < columnNames.length; i++) {
      str += "," + columnNames[i];
    }

    str += ")  VALUES (";

    // Form values

    str += "'" + newValues[0] + "'";
    for (int i = 1; i < columnNames.length; i++) {
      str += ",'" + newValues[i] + "'";
    }

    str += ")";

    try {
      stmt = Conn.createStatement();

      logger.info("addManyStringItems: SQL Query: " + str);
      stmt.executeUpdate(str);
      errorHappened = false;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "addManyStringItems: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return true;
  }

  public int deleteRows(String Table, String columnNames, String newValues) {
    if (Conn == null) {
      return 0;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "DELETE FROM " + Table + " WHERE ";
    try {
      stmt = Conn.createStatement();
      String temp[] = newValues.split(",");
      String temp2[] = columnNames.split(",");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        return 0;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      logger.info("deleteRows: SQL Query: " + str);
      return stmt.executeUpdate(str);
      //rs = stmt.getResultSet();
      //rs.next();
      //return 0;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "deleteRows: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return 0;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    //return null;
  }

  public Blob getBlob(String Table, String blobColumnNname,
                      String columnNames, String newValues) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "SELECT " + blobColumnNname + " FROM " + Table + " WHERE ";
    try {
      stmt = Conn.createStatement();
      String temp[] = newValues.split(" ");
      String temp2[] = columnNames.split(" ");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        return null;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      logger.info("getBlob: SQL Query: " + str);
      stmt.executeQuery(str);
      rs = stmt.getResultSet();
      rs.next();
      return rs.getBlob(blobColumnNname);
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "getBlob: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    /*finally {
     // it is a good idea to release
     // resources in a finally{} block
     // in reverse-order of their creation
     // if they are no-longer needed
     if (rs != null) {
         try {
             rs.close();
         } catch (SQLException sqlEx) { // ignore }
             rs = null;
         }
         if (stmt != null) {
             try {
                 stmt.close();
             } catch (SQLException sqlEx) { // ignore }
                 stmt = null;
             }
         }
     }
                }
     */
    //return null;
  }

  public byte[] getBytes(String Table, String blobColumnNname,
                         String columnNames, String newValues) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "SELECT " + blobColumnNname + " FROM " + Table + " WHERE ";
    try {
      stmt = Conn.createStatement();
      String temp[] = newValues.split(" ");
      String temp2[] = columnNames.split(" ");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        return null;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      logger.info("getBytes: SQL Query: " + str);
      stmt.executeQuery(str);
      rs = stmt.getResultSet();
      rs.next();
      Blob blob = rs.getBlob(blobColumnNname);
      return blob.getBytes(1L, (int) blob.length());
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "getBytes: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
  }

  /*
           public boolean setBlob(String Table, String blobColumnNname, String columnNames, String newValues, Blob myBlob ) {
      if (Conn == null) {
          return false;
      }

      // assume conn is an already created JDBC connection
      stmt = null;
      rs = null;
   String str = "UPDATE " + Table + " SET " + blobColumnNname + " = ? WHERE ";
      try {
          stmt = Conn.createStatement();
          String temp[] = newValues.split(" ");
          String temp2[] = columnNames.split(" ");
   if ( (temp.length != temp2.length) || temp.length < 1 ) return false;
          str += temp2[0] + "='" + temp[0] + "'";
          for (int i=1; i<temp.length; i++)
              str += " && " + temp2[i] + "='" + temp[i] + "'";
          logger.info("setBlob: Query: " + str);
          PreparedStatement pstmt = Conn.prepareStatement( str );
          pstmt.setBlob( 3, myBlob); // !!!
          pstmt.executeUpdate();

      } catch (SQLException ex) {
          // handle any errors
          errorMessage = "SQLException: " + ex.getMessage();
          logger.info(errorMessage);
          logger.info("SQLState: " + ex.getSQLState());
          logger.info("VendorError: " + ex.getErrorCode());
          return false;
      } finally {
          // it is a good idea to release
          // resources in a finally{} block
          // in reverse-order of their creation
          // if they are no-longer needed
          if (rs != null) {
              try {
                  rs.close();
              } catch (SQLException sqlEx) { // ignore }
                  rs = null;
              }
              if (stmt != null) {
                  try {
                      stmt.close();
                  } catch (SQLException sqlEx) { // ignore }
                      stmt = null;
                  }
              }
          }
      }
      return true;
           }
   */
  public boolean setBlob(String Table, String blobColumnNname,
                         String columnNames, String newValues, Blob myBlob) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    //String str = "SELECT " + blobColumnNname + " FROM " + Table + " WHERE ";
    String str = "UPDATE " + Table + " SET " + blobColumnNname +
        " = ? WHERE ";
    try {
      String temp[] = newValues.split(" ");
      String temp2[] = columnNames.split(" ");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        return false;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      /*
       stmt = Conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                  ResultSet.CONCUR_UPDATABLE );
                     logger.info("setBlob: Query: " + str);
                      rs = stmt.executeQuery( str );
       if ( rs.getConcurrency() != ResultSet.CONCUR_UPDATABLE ) {
          logger.info("setBlob: ResultSet is not updatable...");
          return false;
                      }
                      //rs = stmt.getResultSet();
                      rs.next();
                      Blob xxx = rs.getBlob(blobColumnNname);
                      byte bytes[] = ";wlehfkwjefbk;wjef".getBytes();
                      xxx.setBytes(1,bytes);
       logger.info("setBlob: xxx Blob length: " + xxx.length() );

                      rs.updateBlob(blobColumnNname, xxx);
                      rs.updateRow();
                      //rs.updateBlob( blobColumnNname, myBlob);
                      //return rs.getBlob( blobColumnNname );
       */
      logger.info("setBlob: myBlob Blob length: " + myBlob.length());
      logger.info("setBlob: Query: " + str);
      pstmt = Conn.prepareStatement(str);
      pstmt.setBlob(1, myBlob);
      pstmt.executeUpdate();
      return true;
    }
    catch (SQLException ex) {
      // handle any errors
      errorMessage = "setBlob: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    //return null;
    //return false;
  }

  public Clob getClob(String Table, String clobColumnNname,
                      String columnNames, String newValues) {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "SELECT " + clobColumnNname + " FROM " + Table + " WHERE ";
    try {
      stmt = Conn.createStatement();
      String temp[] = newValues.split(",");
      String temp2[] = columnNames.split(",");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        errorHappened = true;
        errorMessage =
            "getClob: Internal Error: (temp.length != temp2.length) || temp.length < 1 ";
        logger.info(
            "getClob: Internal Error: (temp.length != temp2.length) || temp.length < 1 ");
        return null;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      logger.info("getClob: SQL Query: " + str);
      stmt.executeQuery(str);
      rs = stmt.getResultSet();
      rs.next();
      Clob cl = rs.getClob(clobColumnNname);
      str = cl.getSubString(1L, cl.length() > 255 ? 255 : (int) cl.length());
      logger.info("getClob: substring: " + str);
      errorHappened = false;
      return rs.getClob(clobColumnNname);
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "getClob: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    /*finally {
     // it is a good idea to release
     // resources in a finally{} block
     // in reverse-order of their creation
     // if they are no-longer needed
     if (rs != null) {
         try {
             rs.close();
         } catch (SQLException sqlEx) { // ignore }
             rs = null;
         }
         if (stmt != null) {
             try {
                 stmt.close();
             } catch (SQLException sqlEx) { // ignore }
                 stmt = null;
             }
         }
     }
                }
     */
    //return null;
  }

  public boolean setClob(String Table, String clobColumnNname,
                         String columnNames, String newValues, Clob myClob) {
    if (Conn == null) {
      return false;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    //String str = "SELECT " + blobColumnNname + " FROM " + Table + " WHERE ";
    String str = "UPDATE " + Table + " SET " + clobColumnNname +
        " = ? WHERE ";
    try {
      String temp[] = newValues.split(",");
      String temp2[] = columnNames.split(",");
      if ( (temp.length != temp2.length) || temp.length < 1) {
        return false;
      }
      str += temp2[0] + "='" + temp[0] + "'";
      for (int i = 1; i < temp.length; i++) {
        str += " && " + temp2[i] + "='" + temp[i] + "'";
      }
      /*
       stmt = Conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                  ResultSet.CONCUR_UPDATABLE );
                     logger.info("setBlob: Query: " + str);
                      rs = stmt.executeQuery( str );
       if ( rs.getConcurrency() != ResultSet.CONCUR_UPDATABLE ) {
          logger.info("setBlob: ResultSet is not updatable...");
          return false;
                      }
                      //rs = stmt.getResultSet();
                      rs.next();
                      Blob xxx = rs.getBlob(blobColumnNname);
                      byte bytes[] = ";wlehfkwjefbk;wjef".getBytes();
                      xxx.setBytes(1,bytes);
       logger.info("setBlob: xxx Blob length: " + xxx.length() );

                      rs.updateBlob(blobColumnNname, xxx);
                      rs.updateRow();
                      //rs.updateBlob( blobColumnNname, myBlob);
                      //return rs.getBlob( blobColumnNname );
       */
      logger.info("setClob: myClob Clob length: " + myClob.length());
      logger.info("setBlob: SQL Query: " + str);
      pstmt = Conn.prepareStatement(str);
      pstmt.setClob(1, myClob);
      pstmt.executeUpdate();
      errorHappened = false;
      return true;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "setBlob: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return false;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    //return null;
    //return false;
  }

  public String getUser() {
    if (Conn == null) {
      return null;
    }

    // assume conn is an already created JDBC connection
    stmt = null;
    rs = null;
    String str = "SELECT CURRENT_USER";
    try {
      stmt = Conn.createStatement();
      logger.info("getUser: SQL Query: " + str);
      stmt.executeQuery(str);
      rs = stmt.getResultSet();
      rs.next();
      String user = rs.getString(1);
      logger.info("getUser: User: " + user);
      errorHappened = false;
      return user;
    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "getUser: SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return null;
    }
    /*finally {
     // it is a good idea to release
     // resources in a finally{} block
     // in reverse-order of their creation
     // if they are no-longer needed
     if (rs != null) {
         try {
             rs.close();
         } catch (SQLException sqlEx) { // ignore }
             rs = null;
         }
         if (stmt != null) {
             try {
                 stmt.close();
             } catch (SQLException sqlEx) { // ignore }
                 stmt = null;
             }
         }
     }
                }
     */
    //return null;

  }

  public int getLastInsertId(String tableName) {
    if (Conn == null) {
      errorHappened = true;
      errorMessage = "getLastInsertId: No connection to database";
      return -1;
    }

    // assume conn is an already created JDBC connection
    int insertedId = -1;
    stmt = null;
    rs = null;
    ResultSetMetaData rsmd;
    String str = null;

    // ArrayList columns = null;

    try {
      stmt = Conn.createStatement();
      str = "SELECT LAST_INSERT_ID() FROM " + tableName + " LIMIT 1";
      logger.info("SQL Query: " + str);
      if (!stmt.execute(str)) {
        errorHappened = true;
        errorMessage = "getLastInsertId: cannot execute statement";
        return -1;
      }
      rs = stmt.getResultSet();
      rsmd = rs.getMetaData();
      if (rsmd.getColumnCount() < 1) {
        return -1;
      }

      rs.next();

      insertedId = rs.getInt(1);
      errorHappened = false;
      /*
           columns = new ArrayList();

           while (rs.next()) {
        HashMap info = new HashMap();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("CHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("VARCHAR") ==
              0 ||
              rsmd.getColumnTypeName(i).compareToIgnoreCase("TEXT") ==
              0) {
            info.put(rsmd.getColumnName(i), rs.getString(i));
          }
          else
          if (rsmd.getColumnTypeName(i).compareToIgnoreCase("FLOAT") ==
              0) {
            info.put(rsmd.getColumnName(i), new Float(rs.getFloat(i)));
          }
       else if (rsmd.getColumnTypeName(i).compareToIgnoreCase("INTEGER") ==
                   0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }
          else if (rsmd.getColumnTypeName(i).compareToIgnoreCase(
              "INTEGER UNSIGNED") == 0) {
            info.put(rsmd.getColumnName(i), new Integer(rs.getInt(i)));
          }

          // --- General case
          else {
            info.put(rsmd.getColumnName(i), rs.getString(i));
          }

        }
        columns.add(info);
        //logger.info(rs.getString(1));
           }
       */

    }
    catch (SQLException ex) {
      // handle any errors
      errorHappened = true;
      errorMessage = "SQLException: " + ex.getMessage();
      logger.info(errorMessage);
      logger.info("SQLState: " + ex.getSQLState());
      logger.info("VendorError: " + ex.getErrorCode());
      return -1;
    }
    finally {
      // it is a good idea to release
      // resources in a finally{} block
      // in reverse-order of their creation
      // if they are no-longer needed
      if (rs != null) {
        try {
          rs.close();
        }
        catch (SQLException sqlEx) { // ignore }
          rs = null;
        }
        if (stmt != null) {
          try {
            stmt.close();
          }
          catch (SQLException sqlEx) { // ignore }
            stmt = null;
          }
        }
      }
    }
    return insertedId;
  }

}
