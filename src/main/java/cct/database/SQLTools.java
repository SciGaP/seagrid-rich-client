/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 *
 * @author vvv900
 */
public class SQLTools {
  
  public enum SQL_ENGINE {
    
    MySQL, PostgresSQL, GenericSQL, OracleSQL, SQLite
  }

    private static Logger logger = Logger.getLogger(SQLTools.class.getCanonicalName());

  /**
   * Executes arbitrary SQL script
   *
   * @param script - collection of SQL commands
   * @param connection - Connection
   * @throws Exception
   */
  public static void executeSQLScript(String script, Connection connection) throws Exception {
    executeSQLScript(script, connection, SQL_ENGINE.GenericSQL);
  }
  
  public static void executeSQLScript(String script, Connection connection, String sqlEngine) throws Exception {
    SQL_ENGINE engine = SQL_ENGINE.GenericSQL;
    SQL_ENGINE[] values = SQL_ENGINE.values();
    for (SQL_ENGINE iter : values) {
      if (iter.toString().equalsIgnoreCase(sqlEngine)) {
        engine = iter;
        break;
      }
    }
    executeSQLScript(script, connection, engine);
  }
  
  public static void executeSQLScript(String script, Connection connection, SQL_ENGINE sqlEngine) throws Exception {
    
    Statement statement = connection.createStatement();
    
    BufferedReader in = new BufferedReader(new FileReader(script));
    String line;
    StringBuilder query = new StringBuilder(1024);
    while ((line = in.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("-- ") || line.startsWith("#")) {
        continue;
      } else if (line.length() < 1) {
        continue;
      }
      query.append(line + "\n");
      if (line.endsWith(";")) {
        try {
          String sqlQuery = query.toString().trim();
          if (sqlEngine == SQL_ENGINE.SQLite) {
            // -- drop these lines
            if (sqlQuery.matches("(?s)(DROP|drop)\\s+(DATABASE|database).*")
                || sqlQuery.matches("(?s)(CREATE|create)\\s+(DATABASE|database).*")
                || sqlQuery.matches("(?s)(USE|use)\\s+.*")
                || sqlQuery.matches("(?s)(SET|set)\\s+(FOREIGN_KEY_CHECKS|foreign_key_checks).*")) {
              System.out.println("Skiping command for " + SQL_ENGINE.SQLite.toString() + ": " + sqlQuery);
              query.delete(0, query.length());
              continue;
            }

            // --- Modify string for SQLite
            sqlQuery = modifySQLStringForSQLite(sqlQuery);
          }
          statement.executeUpdate(sqlQuery);
          query.delete(0, query.length());
        } catch (Exception ex) {
          logger.severe("Cannot execute query =====\n" + query.toString() + "\n======================\nReason: "
              + ex.getMessage());
          throw new Exception("Cannot execute query =====\n" + query.toString() + "\n======================\nReason: "
              + ex.getMessage());
        }
      }
    }
    
    System.out.println("SQL Script " + script + " was successfuly executed");
  }
  
  static public String modifySQLStringForSQLite(String sql) {
    sql = sql.replaceAll("(?s)(CHARACTER|character)", "");
    sql = sql.replaceAll("(?s)(AUTO_INCREMENT|auto_increment)", "");
    sql = sql.replaceAll("(?s)(SET|set)\\s+\\w+", "");
    sql = sql.replaceAll("(?s)(COLLATE|collate)\\s+utf8\\w+", "collate NOCASE");
    sql = sql.replaceAll("(?s)(INT|int)[(]\\d+[)]", "INTEGER");
    sql = sql.replaceAll("(?s)(DOUBLE|double)([(]\\d+[)]|[(]\\d+[,]\\d+[)])", "REAL");
    sql = sql.replaceAll("(?s)(FLOAT|float)([(]\\d+[)]|[(]\\d+[,]\\d+[)])", "REAL");
    sql = sql.replaceAll("(?s)(ENGINE|engine)\\s*=\\s*\\w+", "");
    return sql;
  }

  /**
   * Checks a table named for existence
   *
   * @param con - Connection
   * @param tableName - table name
   * @return - true if table exists, false otherwise
   * @throws Exception
   */
  public static boolean isTableExists(Connection con, String tableName) throws Exception {
    DatabaseMetaData dbm = con.getMetaData();
    ResultSet tables = dbm.getTables(null, null, tableName, null);
    if (tables.next()) {
      // Table exists
      logger.info("Table " + tableName + " exists");
      return true;
    } else {
      // Table does not exist
      logger.info("Table " + tableName + " does not exist");
      return false;
    }
  }

  /**
   * Tries to delete table in DB
   *
   * @param con - Connection
   * @param tableName - table name
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements
   * that return nothing
   * @throws Exception
   */
  public static int deleteTable(Connection con, String tableName) throws Exception {
    
    Statement st = con.createStatement();
    int res = st.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
    logger.info("Deleting Table: " + res);
    return res;
  }

  /**
   * Execute update query
   *
   * @param con - Connection
   * @param updateQuery - SQL update query
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements
   * that return nothing
   * @throws Exception
   */
  public static int executeUpdate(Connection con, String updateQuery) throws Exception {
    
    Statement st = con.createStatement();
    logger.info("Executing update: " + updateQuery);
    int res = st.executeUpdate(updateQuery);
    logger.info("Executed update: " + res);
    return res;
  }
}
