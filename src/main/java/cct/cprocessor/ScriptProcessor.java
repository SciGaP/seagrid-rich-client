/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import cct.math.expparser.ExpressionParser;
import cct.tools.Utils;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vvv900
 */
public class ScriptProcessor extends ExpressionParser {

  public static final String ScriptProcessorFunsProps_Key = "scriptProcessorFunctionsProps";
  private String ScriptProcessorFunctions_PROPERTY_FILE;

  public static final String ScriptProcessorClassesProps_Key = "scriptProcessorClassesProps";
  private String ScriptProcessorClasses_PROPERTY_FILE;

  private String ScriptProcessor_PROPERTY_FILE = ScriptProcessor.class.getCanonicalName();
  private Properties classProperties, functionProperties, typesProperties;

  static final Logger logger = Logger.getLogger(ScriptProcessor.class.getCanonicalName());

  public ScriptProcessor() throws Exception {
    super();
    ScriptProcessor_PROPERTY_FILE = ScriptProcessor_PROPERTY_FILE.replaceAll("[.]", "/") + ".properties";
    try {
      classProperties = Utils.getProperties(ScriptProcessor_PROPERTY_FILE);
    } catch (Exception ex) {
      Logger.getLogger(ScriptProcessor.class.getName()).log(Level.WARNING, null, ex);
    }
    if (classProperties != null) {
      ScriptProcessorFunctions_PROPERTY_FILE = classProperties.getProperty(ScriptProcessorFunsProps_Key);
      ScriptProcessorClasses_PROPERTY_FILE = classProperties.getProperty(ScriptProcessorClassesProps_Key);
    }

    // ---
    if (ScriptProcessorFunctions_PROPERTY_FILE != null) {
      try {
        functionProperties = Utils.getProperties(ScriptProcessorFunctions_PROPERTY_FILE);
        this.addFunctionDefenitions(functionProperties);
      } catch (Exception ex) {
        logger.log(Level.SEVERE, null, ex);
        throw new Exception("Error while loading function definitions: " + ex.getLocalizedMessage());
      }
    }
    // ---
    if (ScriptProcessorClasses_PROPERTY_FILE != null) {
      try {
        typesProperties = Utils.getProperties(ScriptProcessorClasses_PROPERTY_FILE);
      } catch (Exception ex) {
        logger.log(Level.WARNING, ex.getLocalizedMessage());
      }
      // ---
      try {
        this.addClassesDefenitions(typesProperties);
      } catch (Exception ex) {
        logger.log(Level.SEVERE, null, ex);
        throw new Exception("Error while loading classes/data types definitions: " + ex.getLocalizedMessage());
      }
    }

  }

  public static void main(String[] args) {
    try {
      ScriptProcessor sp = new ScriptProcessor();
    } catch (Exception ex) {
      Logger.getLogger(ScriptProcessor.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(1);
    }
    System.exit(0);
  }
}
