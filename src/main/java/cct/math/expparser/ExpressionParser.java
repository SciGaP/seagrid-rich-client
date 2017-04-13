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
package cct.math.expparser;

import cct.math.*;
import static cct.math.expparser.SYMBOL_TYPE.CLASS;
import static cct.math.expparser.SYMBOL_TYPE.VARIABLE;
import cct.tools.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
//enum PREDEFINED_FUNCTION {
//  SQRT, sqrt, SIN, sin, COS, cos, TAN, tan, ASIN, asin, ACOS, acos, ATAN, atan, EXP, exp, LOG, log;
//}
enum SEPARATOR {

  COMMA
}

public class ExpressionParser {

  protected String EXP_PARSER_PROPERTY_FILE = ExpressionParser.class.getCanonicalName();
  protected String EXP_PARSER_TYPES_PROPERTY_FILE = ExpressionParser.class.getCanonicalName();
  static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
  static private Set<String> stringSeparators = new HashSet<String>();

  protected char[] validSeparators = {'+', '-', '*', '/', '^', '(', ')', ','};

  private Map<String, OPERATION> validOperators = new HashMap<String, OPERATION>();
  private Map<String, SYMBOL_TYPE> validOperations = new HashMap<String, SYMBOL_TYPE>();
  private static final Map<String, PARANTHESIS> Parantheses = new HashMap<String, PARANTHESIS>(2);
  private Map<String, Object> definedFunctions = new HashMap<String, Object>();
  private Map<String, ClassInterface> definedClasses = new HashMap<String, ClassInterface>();
  private Pattern objectTypeDeclaration;
  private Set<String> dataTypes = new HashSet<String>();
  private Stack<Boolean> ifStack = new Stack<Boolean>();
  private int lineNumber = 0;

  static {

    Parantheses.put("(", PARANTHESIS.LEFT);
    Parantheses.put(")", PARANTHESIS.RIGHT);

    stringSeparators.add("\"");
    //stringSeparators.add('\"');
  }

  private static String Delimiters = " \t";
  private boolean caseSensitive = false;
  private boolean allowRedefineVariables = true;
  private boolean inRadians = true;
  private Properties expressionParserProperties = null;

  private int ifStatementDepth = 0;

  Map<String, Object> IDs = new HashMap<String, Object>();
  static final Logger logger = Logger.getLogger(ExpressionParser.class.getCanonicalName());

  //MathExpressionParser hiddenInit = new MathExpressionParser(true); // !!!
  private ExpressionParser(boolean something) {

  }

  public ExpressionParser() {
    EXP_PARSER_PROPERTY_FILE = EXP_PARSER_PROPERTY_FILE.replaceAll("[.]", "/") + ".properties";
    EXP_PARSER_TYPES_PROPERTY_FILE = EXP_PARSER_TYPES_PROPERTY_FILE.replaceAll("[.]", "/") + "Types.properties";

    validOperators.put("!", OPERATION.Unary_logical_negation);
    validOperators.put("+", OPERATION.ADD);
    validOperators.put("-", OPERATION.SUBSTRACT);
    validOperators.put("*", OPERATION.MULTIPLY);
    validOperators.put("/", OPERATION.DEVIDE);
    validOperators.put("^", OPERATION.POWER);
    validOperators.put("%", OPERATION.MODULUS);
    validOperators.put("<", OPERATION.Relational_less_than);
    validOperators.put("<=", OPERATION.Relational_less_than_or_equal);
    validOperators.put(">", OPERATION.Relational_greater_than);
    validOperators.put(">=", OPERATION.Relational_greater_than_or_equal);
    validOperators.put("instanceof", OPERATION.Type_comparison);
    validOperators.put("==", OPERATION.Relational_is_equal_to);
    validOperators.put("!=", OPERATION.Relational_is_not_equal_to);
    validOperators.put("&", OPERATION.Bitwise_AND);
    //validOperators.put("^", OPERATION.Bitwise_AND);
    validOperators.put("|", OPERATION.Bitwise_inclusive_OR);
    validOperators.put("&&", OPERATION.Logical_AND);
    validOperators.put("||", OPERATION.Logical_OR);
    validOperators.put("?", OPERATION.Ternary_conditional);

    validOperations.put("+", SYMBOL_TYPE.OPERATION_ADD);
    validOperations.put("-", SYMBOL_TYPE.OPERATION_SUB);
    validOperations.put("*", SYMBOL_TYPE.OPERATION_MUL);
    validOperations.put("/", SYMBOL_TYPE.OPERATION_DIV);
    validOperations.put("^", SYMBOL_TYPE.OPERATION_POWER);

    try {
      this.addSymbol("true", new Variable("true", new Boolean(true), true));
      this.addSymbol("TRUE", new Variable("TRUE", new Boolean(true), true));
      this.addSymbol("false", new Variable("false", new Boolean(false), true));
      this.addSymbol("FALSE", new Variable("FALSE", new Boolean(false), true));
      this.addSymbol("PI", new Variable("PI", new Double(Math.PI), true));
      this.addSymbol("E", new Variable("E", new Double(Math.E), true));
    } catch (Exception ex) {
    }

    logger.setLevel(Level.WARNING);

    try {
      expressionParserProperties = getProperties();
    } catch (Exception ex) {
      logger.log(Level.WARNING, null, ex);
    }

    try {
      addFunctionDefenitions(expressionParserProperties);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    }

    // ----
    try {
      Properties props = Utils.getProperties(EXP_PARSER_TYPES_PROPERTY_FILE);
      addClassesDefenitions(props);
    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  public void addClassesDefenitions(Properties classesProperties) throws Exception {
    if (classesProperties == null) {
      return;
    }
    for (Map.Entry entry : classesProperties.entrySet()) {
      String name = entry.getKey().toString();
      String className = entry.getValue().toString();

      Class builderClass = null;
      ClassLoader loader = null;
      try {
        // Get the Class object associated with builder
        builderClass = Class.forName(className);

        // Get the ClassLoader object associated with this Class.
        loader = builderClass.getClassLoader();

        if (loader == null) {
          logger.severe("loader == null while attempting to load class " + className + " Ignored...");
          continue;
        } else {
          // Verify that this ClassLoader is associated with the builder class.
          Class loaderClass = loader.getClass();

          if (logger.isLoggable(Level.INFO)) {
            logger.info("Class associated with ClassLoader: " + loaderClass.getName());
          }
        }
      } catch (ClassNotFoundException ex) {
        logger.severe("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
        continue;
      }

      try {
        Class cl = loader.loadClass(className);
        Object obj = cl.newInstance();
        if (logger.isLoggable(Level.INFO)) {
          logger.info("Classr " + className + " was loaded");
        }
        // ----
        if (!(obj instanceof ClassInterface)) {
          logger.severe("Class " + className + " does not implement " + ClassInterface.class.getName()
              + " interface. Ignored...");
          continue;
        }
        // ----
        ClassInterface ci = (ClassInterface) obj;
        ci.ciSetName(name);
        ci.ciSetExpressionParser(this);
        definedClasses.put(name, ci);
      } catch (Exception ex) {
        logger.severe("Error while loading Class " + className + ex.getMessage() + " Ignored...");
        continue;
      }
      dataTypes.add(name);
    }
    // ---
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (String type : dataTypes) {
      if (sb.length() > 1) {
        sb.append("|");
      }
      sb.append(type);
    }
    sb.append(")");
    String pattern = "\\s*" + sb.toString() + "\\s*?(\\[\\w+\\])*+\\s+\\w+([(].*[)])*?";
    System.out.println("Type definition pattern: " + pattern);
    objectTypeDeclaration = Pattern.compile(pattern);
  }

  public void addFunctionDefenitions(Properties funProperties) throws Exception {
    if (funProperties == null) {
      return;
    }
    for (Map.Entry entry : funProperties.entrySet()) {
      String name = entry.getKey().toString();
      String className = entry.getValue().toString();

      Class builderClass = null;
      ClassLoader loader = null;
      try {
        // Get the Class object associated with builder
        builderClass = Class.forName(className);

        // Get the ClassLoader object associated with this Class.
        loader = builderClass.getClassLoader();

        if (loader == null) {
          logger.severe("loader == null while attempting to load class " + className + " Ignored...");
          throw new Exception("loader == null while attempting to load class " + className + " Ignored...");
        } else {
          // Verify that this ClassLoader is associated with the builder class.
          Class loaderClass = loader.getClass();

          if (logger.isLoggable(Level.INFO)) {
            logger.info("Class associated with ClassLoader: " + loaderClass.getName());
          }
        }
      } catch (ClassNotFoundException ex) {
        logger.severe("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
        throw new Exception("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
      }

      try {
        Class cl = loader.loadClass(className);
        Object obj = cl.newInstance();
        if (logger.isLoggable(Level.INFO)) {
          logger.info("Classr " + className + " was loaded");
        }
        // ----
        if (!(obj instanceof FunctionInterface)) {
          logger.severe("Class " + className + " does not implement " + FunctionInterface.class.getName()
              + " interface. Ignored...");
          throw new Exception("Class " + className + " does not implement " + FunctionInterface.class.getName()
              + " interface. Ignored...");
        }
        // ----
        FunctionInterface fi = (FunctionInterface) obj;
        fi.fiSetName(name);
        fi.fiSetExpressionParser(this);
        definedFunctions.put(name, fi);
      } catch (Exception ex) {
        logger.severe("Error while loading Class " + className + ex.getMessage() + " Ignored...");
        throw new Exception("Error while loading Class " + className + ex.getMessage() + " Ignored...");
      }

    }
  }

  public boolean isInRadians() {
    return inRadians;
  }

  public boolean isInDegrees() {
    return !inRadians;
  }

  public boolean isNumber(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Number) {
      return true;
    }

    try {
      Integer num = Integer.parseInt(obj.toString());
      return true;
    } catch (Exception ex) {

    }
    try {
      Double num = Double.parseDouble(obj.toString());
      return true;
    } catch (Exception ex) {

    }
    return false;
  }

  public Number getNumber(Object obj) throws Exception {
    if (obj == null) {
      throw new Exception("Cannot convert null object into a number");
    }
    if (obj instanceof Number) {
      return (Number) obj;
    }

    try {
      Integer num = Integer.parseInt(obj.toString());
      return num;
    } catch (Exception ex) {

    }
    try {
      Double num = Double.parseDouble(obj.toString());
      return num;
    } catch (Exception ex) {

    }
    throw new Exception("Object " + obj.toString() + " is not a number");
  }

  public Properties getProperties() throws Exception {

    expressionParserProperties = Utils.getProperties(EXP_PARSER_PROPERTY_FILE);
    return expressionParserProperties;
  }

  public boolean isAllowRedefineVariables() {
    return allowRedefineVariables;
  }

  public void setAllowRedefineVariables(boolean allowRedefineVariables) {
    this.allowRedefineVariables = allowRedefineVariables;
  }

  public boolean hasSymbol(String symbol) {
    if ((symbol != null) && (!caseSensitive)) {
      symbol = symbol.toUpperCase();
    }
      return IDs.containsKey( symbol ) || definedFunctions.containsKey( symbol );
  }

  public boolean hasObjectType(String symbol) {
    if (!caseSensitive) {
      symbol = symbol.toUpperCase();
    }
      return this.definedClasses.containsKey( symbol );
  }

  public Object getObjectTypeInstance(String symbol) throws Exception {
    if (!this.definedClasses.containsKey(symbol)) {
      throw new Exception("Cannot allocate variable of unknown type: " + symbol);
    }
    return definedClasses.get(symbol).ciAllocateClass(null);
  }

  public Object getSymbol(String symbol) {
    if (!caseSensitive) {
      symbol = symbol.toUpperCase();
    }
    if (IDs.containsKey(symbol)) {
      return IDs.get(symbol);
    } else if (definedFunctions.containsKey(symbol)) {
      return definedFunctions.get(symbol);
    }
    return null;
  }

  public void setInDegrees(boolean in_degrees) {
    inRadians = !in_degrees;
  }

  public void setInRadians(boolean in_radians) {
    inRadians = in_radians;
  }

  public boolean isObjectTypeDeclaration(String line) {
    return objectTypeDeclaration.matcher(line).matches();
  }

  public boolean addLine(String line) throws Exception {
    ++lineNumber;

    line = line.trim();
    if (line.length() < 1) {
      return true;
    }

    if (line.matches("\\s*if.*")) {
      if (line.matches("\\s*if\\s*[(].+[)]\\s*then\\s*")) { // If block statement starts
        System.out.println("Start of if block");
        ++ifStatementDepth;
        String expression = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
        Object result = resolveExpression(expression);
        boolean ifTrue = false;
        if (result == null) {
          ifTrue = false;
        } else if (result instanceof Boolean) {
          ifTrue = (Boolean) result;
        } else if (result instanceof Number) {
          ifTrue = !(((Number) result).doubleValue() == 0);
        } else {
          ifTrue = true;
        }
        ifStack.push(ifTrue);
        return true;
      } else if (line.matches("\\s*else\\s*if\\s*[(].+[)]\\s*then\\s*")) { // else if block statement starts

      } else if (line.matches("\\s*else\\s*")) { // else block statement starts

      } else if (line.matches("\\s*endif\\s*")) { // endif of if block statement 

      }

    }

    if (!ifStack.empty() && !ifStack.peek()) {
      System.out.println("Skip line: \"" + line + "\" because of previous if condition");
      return true;
    }

    // --- Check for variable declaration
    if (isObjectTypeDeclaration(line)) {
      System.out.println("Found variable declaration: " + line);
      parseObjectTypeDeclaration(line);
      return true;
    }

    if (!line.matches("\\s*\\w+([(].*[)])?\\s*([+]=|-=|[*]=|/=|[%]=|=)\\s*.*")) {
      throw new Exception("Constant or function should be defined using = (equal) sign. Got: " + line);
    }

    if ((line.indexOf("=") == line.length() - 1) || line.substring(line.indexOf("=") + 1).trim().length() == 0) {
      throw new Exception("Should be expression after the = (equal) sign. Got: " + line);
    }

    if (!caseSensitive) {
      line = line.toUpperCase();
    }

    boolean newOperator = false;
    if (line.matches("\\w+\\s*=\\s*(new|NEW)\\s+\\w+.*")) {
      System.out.println("New Operator: " + line);
      newOperator = true;
    }
    // --- Get id name
    String operator = null;
    String id = line.substring(0, line.indexOf("=")).trim();
    if (id.matches(".*([+]|-|[*]|/|%){1}$")) {
      operator = id.substring(id.length() - 1);
      id = id.substring(0, id.length() - 1);
    }

    String expression = line.substring(line.indexOf("=") + 1).trim();
    expression = expression.replaceAll("[*][*]", "^");

    if (newOperator) {
      System.err.println("New Operator is not implemented yet");
      if (expression.contains("new")) {
        expression = line.substring(line.indexOf("new") + 3).trim();
      } else if (expression.contains("NEW")) {
        expression = line.substring(line.indexOf("NEW") + 3).trim();
      }
      allocateClass(id, expression);
    } else if (id.matches(".+[(].+[)]")) { // Function
      parseFunction(id, expression);
      //throw new Exception("Definition of functions is not implemented yet");
    } else if (!id.contains("(") && !id.contains(")") && !id.contains(",")) { // Constant
      if (this.hasSymbol(id) && (!this.allowRedefineVariables)) {
        throw new Exception("Identifier " + id + " is already defined");
      }
      addSymbol(id, null);
      parseConstant(id, expression);
    } else {
      throw new Exception("Unknown type of identifier. Should be a constant or a function. Got " + line);
    }
    return true;
    //StringTokenizer st = new StringTokenizer(line, " =,");
  }

  void parseFunction(String definition, String expression) throws Exception {

    Function function;
    try {
      function = new Function(definition, this);
      //function.setExpressionParser(this);
      function.setFunctionBody(expression);
    } catch (Exception ex) {
      throw new Exception(definition + " : " + ex.getMessage());
    }

    if (definedFunctions.containsKey(function.getInternalName())) {
      throw new Exception("Function " + function.getName() + " with " + function.getArgsNumber()
          + " parameters is already defined");
    }

    definedFunctions.put(function.getInternalName(), function);

  }

  void allocateClass(String name, String expression) throws Exception {
    Object obj = this.resolveExpression(expression);
    Variable var = new Variable(name, SYMBOL_TYPE.CLASS);
    var.setValue(obj);
    addSymbol(name, var);
    logger.info(name + " = " + (var.getValue() != null ? var.getValue().toString() : var.getValue()));
  }

  void parseConstant(String name, String expression) throws Exception {
    Object obj = resolveExpression(expression);
    Variable var = new Variable(name, SYMBOL_TYPE.VARIABLE);
    var.setValue(obj);
    addSymbol(name, var);
    logger.info(name + " = " + (var.getValue() != null ? var.getValue().toString() : var.getValue()));
  }

  void parseObjectTypeDeclaration(String line) throws Exception {

    List<Variable> Tokens = new ArrayList<Variable>();
    List<SYMBOL_TYPE> tokenTypes = new ArrayList<SYMBOL_TYPE>();

    resolveTokens(line, Tokens, tokenTypes);

    if (Tokens.get(0).getType() != CLASS) {
      throw new Exception("The first token must be a valid data type. Unknown data type: " + Tokens.get(0).getName());
    }

    String type = Tokens.get(0).getName();
    if (!hasObjectType(type)) { // Double check
      throw new Exception("Unknown Object type declaration: " + type);
    }

    // --- For now we don't process arrays...
    if (Tokens.get(1).getType() == VARIABLE) {
      String varName = Tokens.get(1).getName();
      Variable var = new Variable(varName, SYMBOL_TYPE.VARIABLE);
      var.setValue(getObjectTypeInstance(type));
      addSymbol(varName, var);
    } else if (Tokens.get(1).getType() == SYMBOL_TYPE.FUNCTION) {
      //File file("filename") -> file File("filename")
      Variable constructor = Tokens.get(0);
      Variable var = Tokens.get(1);
      Tokens.remove(var);
      Tokens.get(0).setType(SYMBOL_TYPE.FUNCTION);
      Object obj = evaluateFunction(Tokens);
      var.setValue(obj);
      var.setType(SYMBOL_TYPE.VARIABLE);
      addSymbol(var.getName(), var);
    }

    if (false) {
      String[] tokens = line.split("\\s+");
      if (tokens.length < 2) {
        throw new Exception("Object type declaration should cocnsist of at least two tokens. Got: " + line);
      }
      type = tokens[0];
      if (!hasObjectType(type)) {
        throw new Exception("Unknown Object type declaration: " + type);
      }
      String varName = tokens[1];
      Variable var = new Variable(varName, SYMBOL_TYPE.VARIABLE);
      var.setValue(getObjectTypeInstance(type));
      addSymbol(varName, var);
    }
  }

  public void addVariable(String name, double value) throws Exception {
    if (!caseSensitive) {
      name = name.toUpperCase();
    }
    Variable var = new Variable(name, SYMBOL_TYPE.VARIABLE);
    try {
      var.setValue(value);
    } catch (Exception ex) {
    }
    addSymbol(name, var);
    logger.info("Added variable: " + name + " = " + (var.getValue() != null ? var.getValue().toString() : var.getValue()));
  }

  public Object getVariable(String name) throws Exception {
    Object obj = this.getSymbol(name);
    if (obj == null) {
      throw new Exception("No such symbol " + name);
    }
    return obj;
  }

  public double getVariableAsDouble(String name) throws Exception {
    Object obj = getVariable(name);
    if (obj instanceof Number) {
      return ((Number) obj).doubleValue();
    } else if (obj instanceof Variable) {
      Object value = ((Variable) obj).getValue();
      if (value == null) {
        throw new Exception("Variable " + name + " is not set");
      }
      if (value instanceof Number) {
        return ((Number) value).doubleValue();
      }
    }
    throw new Exception("Variable " + name + " cannot be converted into a double value");
  }

  public void addSymbol(String name, Object value) throws Exception {
    if (IDs.containsKey(name) && IDs.get(name) instanceof Variable && ((Variable) IDs.get(name)).isConstant()) {
      throw new Exception(name + " is a reserved constant and cannot be changed");
    }
    IDs.put(name, value);
    logger.info("Added symbol: " + name + " = " + (value != null ? value.toString() : "null"));
  }

  public boolean isDataType(String symbolic) {
    if (symbolic == null) {
      return false;
    }
    return this.definedClasses.containsKey(symbolic);
  }

  private void processSymbolicConstant(String symbolic, List<Variable> Tokens, List<SYMBOL_TYPE> tokenTypes, List tokens)
      throws Exception {
    if (!caseSensitive) {
      symbolic = symbolic.toUpperCase();
    }
    if (isDataType(symbolic)) {
      Tokens.add(new Variable(symbolic, SYMBOL_TYPE.CLASS));
      tokenTypes.add(SYMBOL_TYPE.CLASS);
      tokens.add(symbolic);
      return;
    }
    // ---
    if (this.hasSymbol(symbolic)) { // if it's a defined variable or a function 
      Variable var = (Variable) getSymbol(symbolic);
      Tokens.add(var);
    } else if (this.isNumber(symbolic)) { // Is it a number
      Number num = this.getNumber(symbolic);
      Variable var = new Variable(num);
      Tokens.add(var);
    } else { //Probably it's a local variable
      Tokens.add(new Variable(symbolic, SYMBOL_TYPE.VARIABLE));
      //throw new Exception("Variable " + variable + " is not defined");
    }

    tokenTypes.add(SYMBOL_TYPE.VARIABLE);
    tokens.add(symbolic);
  }

  void resolveTokens(String expression, List<Variable> Tokens, List<SYMBOL_TYPE> tokenTypes) throws Exception {
    Tokens.clear();
    tokenTypes.clear();

    List tokens = new ArrayList();
    int leftParantheses = 0, rightParantheses = 0;
    int leftSquareBrackets = 0;

    boolean buildingID = false;
    boolean buildingString = false;
    String symbol = null;
    StringBuilder variable = new StringBuilder();
    for (int i = 0; i < expression.length(); i++) {
      //expression.charAt(i);
      symbol = expression.substring(i, i + 1);

      if (Delimiters.contains(symbol)) {
        if (buildingString) {
          variable.append(symbol);
        } else if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        continue;
      }

      // --- Math operator
      if (validOperators.containsKey(symbol) && (!buildingString)) {
        if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(validOperators.get(symbol), validOperations.get(symbol)));
        tokenTypes.add(validOperations.get(symbol));
        tokens.add(validOperators.get(symbol));
      } else if (symbol.equals("(") && (!buildingString)) {
        ++leftParantheses;
        if (buildingID) {
          buildingID = false;
          Tokens.add(new Variable(variable.toString(), SYMBOL_TYPE.FUNCTION));
          tokenTypes.add(SYMBOL_TYPE.FUNCTION);
          tokens.add(variable.toString());
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(Parantheses.get(symbol), SYMBOL_TYPE.LEFT_PARANTHESIS));
        tokenTypes.add(SYMBOL_TYPE.LEFT_PARANTHESIS);
        tokens.add(Parantheses.get(symbol));
      } else if (symbol.equals(")") && (!buildingString)) {
        ++rightParantheses;
        if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(Parantheses.get(symbol), SYMBOL_TYPE.RIGHT_PARANTHESIS));
        tokenTypes.add(SYMBOL_TYPE.RIGHT_PARANTHESIS);
        tokens.add(Parantheses.get(symbol));
      } else if (symbol.equals(",") && (!buildingString)) {
        if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(SYMBOL_TYPE.COMMA));
        tokenTypes.add(SYMBOL_TYPE.COMMA);
        tokens.add(SEPARATOR.COMMA);

      } else if (symbol.equals("[") && (!buildingString)) {
        ++leftSquareBrackets;
        if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(SYMBOL_TYPE.LEFT_SQUARE_BRACKET));
        tokenTypes.add(SYMBOL_TYPE.LEFT_SQUARE_BRACKET);
        tokens.add(SYMBOL_TYPE.LEFT_SQUARE_BRACKET);
      } else if (symbol.equals("])") && (!buildingString)) {
        --leftSquareBrackets;
        if (buildingID) {
          buildingID = false;
          processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
          variable.delete(0, variable.length());
        }
        Tokens.add(new Variable(SYMBOL_TYPE.RIGHT_SQUARE_BRACKET));
        tokenTypes.add(SYMBOL_TYPE.RIGHT_SQUARE_BRACKET);
        tokens.add(SYMBOL_TYPE.RIGHT_SQUARE_BRACKET);
      } else if (stringSeparators.contains(symbol) || buildingString) {
        // --- Start to build a string
        if (!buildingString) {
          if (buildingID) {
            throw new Exception("String should be separated by valid separator in expression");
          }
          buildingString = true;
          continue;
        }
        // --- appending a string
        if (i != (expression.length() - 1) && expression.substring(i + 1, i + 2).equals("\"")) {
          variable.append(symbol);
          continue;
        }
        if (!stringSeparators.contains(symbol)) {
          variable.append(symbol);
          continue;
        }
        // --- End of a string definition
        buildingString = false;
        Variable var = new Variable(variable.toString());
        Tokens.add(var);
        tokenTypes.add(var.getType());
        tokens.add(variable.toString());
        variable.delete(0, variable.length());
        continue;
      } // --- Building variable name...
      else {
        buildingID = true;
        variable.append(symbol);
      }

    }

    // --- End of a loop
    if (buildingID) {
      processSymbolicConstant(variable.toString(), Tokens, tokenTypes, tokens);
    } else if (buildingString) {
      //if (!symbol.equals("\"")) {
      //  variable.append(symbol);
      //}
      Tokens.add(new Variable(variable.toString()));
      tokenTypes.add(SYMBOL_TYPE.VARIABLE);
      tokens.add(variable.toString());
    }

    if (rightParantheses != leftParantheses) {
      throw new Exception("Expression: " + expression + "\nLeft and Right Parantheses are not balanced");
    }
    if (leftSquareBrackets != 0) {
      throw new Exception("Expression: " + expression + "\nLeft and Right Square Brackets are not balanced");
    }
  }

  public Object resolveExpression(String expression) throws Exception {

    // --- Stupid algorithm
    // --- Resolve functions (if any)
    // --- Resolve expr in parantheses (if any)
    //ArrayList tokens = new ArrayList();
    List<Variable> Tokens = new ArrayList<Variable>();
    List<SYMBOL_TYPE> tokenTypes = new ArrayList<SYMBOL_TYPE>();

    resolveTokens(expression, Tokens, tokenTypes);

    // --- Resolve variables
    for (int i = 0; i < Tokens.size(); i++) {
      logger.info(Tokens.get(i).getName() + " " + Tokens.get(i).getType().toString());
      Variable type = Tokens.get(i);
      switch (type.getType()) {
        case VARIABLE:

          if (type.getName() == null) {
            // --- Temporary variable
          } else if (type.getName() != null && (!this.hasSymbol(type.getName()))) {
            throw new Exception("Expression: " + expression + "\nVariable " + type.getName() + " is not defined");
          }

          //if (type.isNumber()) {
          //  continue;
          //}
          //Variable var = (Variable) this.getSymbol(type.getName());
          //type.setValue(var.getValue());
          break;

        case FUNCTION:
          break;
      }
    }

    Object value;
    try {
      value = evaluateExpression(Tokens, tokenTypes);
    } catch (Exception ex) {
      throw new Exception("Expression: " + expression + "\n" + ex.getMessage());
    }
    return value;
  }

  /**
   * We assume, no functions inside
   *
   * @param Tokens ArrayList
   * @param tokenTypes ArrayList
   * @return double
   * @throws Exception
   */
  //public Object evaluateFunction(List<Variable> Tokens, List<SYMBOL_TYPE> tokenTypes) throws Exception {
  public Object evaluateFunction(List<Variable> Tokens) throws Exception {
    double value = 0;

    //if (tokenTypes.size() < 3) {
    if (Tokens.size() < 3) {
      throw new Exception("Minimal function definition should be function_name()");
    }

    //if (tokenTypes.get(0) != SYMBOL_TYPE.FUNCTION) {
    if (Tokens.get(0).getType() != SYMBOL_TYPE.FUNCTION) {
      throw new Exception("Function should start from funcion name. Got " + Tokens.get(0).getType().toString());
    }
    //if (tokenTypes.get(1) != SYMBOL_TYPE.LEFT_PARANTHESIS) {
    if (Tokens.get(1).getType() != SYMBOL_TYPE.LEFT_PARANTHESIS) {
      throw new Exception("Left paranthesis should follow the funcion name. Got " + Tokens.get(1).getType().toString());
    }
    //if (tokenTypes.get(tokenTypes.size() - 1) != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
    if (Tokens.get(Tokens.size() - 1).getType() != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
      throw new Exception("Right paranthesis should close funcion definition. Got "
          + Tokens.get(Tokens.size() - 1).getType().toString());
    }

    // --- start to resolve arguments
    //if (tokenTypes.size() == 3 && tokenTypes.get(2) != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
    if (Tokens.size() == 3 && Tokens.get(2).getType() != SYMBOL_TYPE.RIGHT_PARANTHESIS) {
      throw new Exception("Minimal function definition should be function_name()");
    }
    //List<Double> parameters = new ArrayList<Double>();
    List<Object> parameters = new ArrayList<Object>();
    List<Variable> Expression = new ArrayList<Variable>();
    List<SYMBOL_TYPE> exprTypes = new ArrayList<SYMBOL_TYPE>();
    int leftParanthesisDepth = 0;
    //for (int i = 2; i < tokenTypes.size(); i++) {
    for (int i = 2; i < Tokens.size(); i++) {
      //if (tokenTypes.get(i) == SYMBOL_TYPE.LEFT_PARANTHESIS) {
      if (Tokens.get(i).getType() == SYMBOL_TYPE.LEFT_PARANTHESIS) {
        ++leftParanthesisDepth;
      } else if (Tokens.get(i).getType() == SYMBOL_TYPE.RIGHT_PARANTHESIS && leftParanthesisDepth > 0) {
        --leftParanthesisDepth;
      } else if (Tokens.get(i - 1).getType() == SYMBOL_TYPE.LEFT_PARANTHESIS
          && Tokens.get(i).getType() == SYMBOL_TYPE.RIGHT_PARANTHESIS) {
        --leftParanthesisDepth;
      } else if (Tokens.get(i).getType() == SYMBOL_TYPE.COMMA
          || Tokens.get(i).getType() == SYMBOL_TYPE.RIGHT_PARANTHESIS) {
        Object Value = evaluateExpression(Expression, exprTypes);
        parameters.add(Value);
        logger.info("Par: " + parameters.size() + " value: " + Value);
        Expression.clear();
        exprTypes.clear();
        continue;
      }
      Expression.add(Tokens.get(i));
      exprTypes.add(Tokens.get(i).getType());
    }

    Object obj;
    String internalName = Tokens.get(0).getName() + "_" + String.valueOf(parameters.size());
    if (definedFunctions.containsKey(Tokens.get(0).getName())) {
      obj = definedFunctions.get(Tokens.get(0).getName());
    } else if (this.definedClasses.containsKey(Tokens.get(0).getName())) {
      obj = definedClasses.get(Tokens.get(0).getName());
    } else if (Tokens.get(0).getName().matches("\\w+[.]\\w+")) { // Class or variable method
      String[] methodsChain = Tokens.get(0).getName().split("[.]");
      if (!this.hasSymbol(methodsChain[0])) {
        throw new Exception("No such variable-class " + methodsChain[0] + " in method invocation " + Tokens.get(0).getName());
      }
      obj = this.getSymbol(methodsChain[0]);
      return evaluateMethod(obj, methodsChain, parameters.toArray());
    } else if (definedFunctions.containsKey(internalName)) {
      obj = definedFunctions.get(internalName);
    } else {
      throw new Exception("Function " + Tokens.get(0).getName() + " with " + parameters.size() + " parameters is not defined");
    }

    //if (obj instanceof PredefinedFun) {
    //  return ((PredefinedFun) obj).getValue(parameters);
    //} else 
    if (obj instanceof Function) {
      return ((Function) obj).getValue(parameters);
    } else if (obj instanceof FunctionInterface) {
      return ((FunctionInterface) obj).fiEvaluateFunction(parameters.toArray());
    } else if (obj instanceof ClassInterface) {
      return ((ClassInterface) obj).ciAllocateClass(parameters.toArray());
    }

    throw new Exception("Unknown type of Function " + obj.getClass().getCanonicalName());
  }

  Object evaluateMethod(Object obj, String[] tokens, Object[] args) throws Exception {
    if (obj == null) {
      throw new Exception("Variable-class " + tokens[0] + " is not defined");
    }
    Object varC = obj;
    if (obj instanceof Variable) {
      varC = ((Variable) obj).getValue();
    }
    if (varC == null) {
      throw new Exception("Variable-class " + tokens[0] + " is not defined");
    }

    if (!(varC instanceof ClassInterface)) {
      throw new Exception("Variable-class " + tokens[0] + " is not an instanceof ClassInterface");
    }

    ClassInterface ci = (ClassInterface) varC;
    if (!ci.ciHasMethod(tokens[1])) {
      throw new Exception("Variable-class " + tokens[0] + " does not have method " + tokens[1]);
    }
    return ci.ciExecuteMethod(tokens[1], args);
  }

  Object evaluateExpression(List<Variable> Tokens, List<SYMBOL_TYPE> tokenTypes) throws Exception {
    //Object evaluateExpression(List<Variable> Tokens ) throws Exception {
    Object value = null;

    if (Tokens.size() == 1) {
      if (Tokens.get(0).getType() == SYMBOL_TYPE.VARIABLE || Tokens.get(0).getType() == SYMBOL_TYPE.LOCAL_VARIABLE) {
        return Tokens.get(0).getValue();
      }
      throw new Exception("Single token must be either a variable or a number");
    }

    while (Tokens.size() > 1) {

      if (tokenTypes.contains(SYMBOL_TYPE.FUNCTION)) {
        List<Variable> Expression = new ArrayList<Variable>();
        List<SYMBOL_TYPE> exprTypes = new ArrayList<SYMBOL_TYPE>();
        int index = tokenTypes.lastIndexOf(SYMBOL_TYPE.FUNCTION);
        int end, leftParanthesisDepth = -1;

        logger.info("Resolving function " + Tokens.get(index).getName());

        for (end = index; end < tokenTypes.size(); end++) {
          logger.info(end + " " + Tokens.get(end).getType());
          Expression.add(Tokens.get(end));
          exprTypes.add(Tokens.get(end).getType());
          if (tokenTypes.get(end) == SYMBOL_TYPE.LEFT_PARANTHESIS) {
            ++leftParanthesisDepth;
            continue;
          } else if (tokenTypes.get(end) == SYMBOL_TYPE.RIGHT_PARANTHESIS) {
            if (leftParanthesisDepth > 0) {
              --leftParanthesisDepth;
              continue;
            }
          }

          if (tokenTypes.get(end) == SYMBOL_TYPE.RIGHT_PARANTHESIS) {
            //value = evaluateFunction(Expression, exprTypes);
            value = evaluateFunction(Expression);
            logger.info("Function value: " + (value != null ? value.toString() : value));
            Variable result = new Variable(value);
            for (int i = index; i <= end; i++) {
              Tokens.remove(index);
              tokenTypes.remove(index);
            }
            Tokens.add(index, result);
            tokenTypes.add(index, result.getType());
            break;
          }

        }
      } else if (tokenTypes.contains(SYMBOL_TYPE.LEFT_PARANTHESIS)) {
        List<Variable> Expression = new ArrayList<Variable>();
        List<SYMBOL_TYPE> exprTypes = new ArrayList<SYMBOL_TYPE>();
        ((ArrayList) Expression).ensureCapacity(Tokens.size());
        ((ArrayList) exprTypes).ensureCapacity(Tokens.size());

        int indexEnd;
        while (true) {
          int indexStart = tokenTypes.lastIndexOf(SYMBOL_TYPE.LEFT_PARANTHESIS);
          if (indexStart == -1) {
            break;
          }

          Expression.clear();
          exprTypes.clear();

          for (indexEnd = indexStart + 1; indexEnd < Tokens.size(); indexEnd++) {
            if (Tokens.get(indexEnd).getType() == SYMBOL_TYPE.RIGHT_PARANTHESIS) {
              break;
            }
            Expression.add(Tokens.get(indexEnd));
            exprTypes.add(Tokens.get(indexEnd).getType());
          }
          if (indexEnd == Tokens.size()) {
            throw new Exception("Unmatched (");
          }
          if (indexEnd - indexStart == 1) {
            throw new Exception("No expression within parantheses");
          }

          //value = evaluateExpression(Expression, exprTypes);
          Object obj = evaluateExpression(Expression, exprTypes);
          Variable result = new Variable(obj);
          for (int i = indexStart; i <= indexEnd; i++) {
            Tokens.remove(indexStart);
            tokenTypes.remove(indexStart);
          }
          Tokens.add(indexStart, result);
          tokenTypes.add(indexStart, result.getType());

        }
      } else {
        int index;
        while (Tokens.size() > 1) {
          // --- Power operation
          index = tokenTypes.indexOf(SYMBOL_TYPE.OPERATION_POWER);
          if (index != -1) {
            Variable first = getToken(Tokens, index - 1);
            Variable second = getToken(Tokens, index + 1);
            value = Math.pow(first.getValueAsDouble(), second.getValueAsDouble());
            Variable result = new Variable(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.getType());
            continue;
          }

          // --- Division
          index = tokenTypes.indexOf(SYMBOL_TYPE.OPERATION_DIV);
          if (index != -1) {
            Variable first = getToken(Tokens, index - 1);
            Variable second = getToken(Tokens, index + 1);
            value = first.getValueAsDouble() / second.getValueAsDouble();
            Variable result = new Variable(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.getType());
            continue;
          }

          // --- Multiplication
          index = tokenTypes.indexOf(SYMBOL_TYPE.OPERATION_MUL);
          if (index != -1) {
            Variable first = getToken(Tokens, index - 1);
            Variable second = getToken(Tokens, index + 1);
            value = first.getValueAsDouble() * second.getValueAsDouble();
            Variable result = new Variable(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.getType());
            continue;
          }

          // --- Sub
          index = tokenTypes.indexOf(SYMBOL_TYPE.OPERATION_SUB);
          if (index != -1) {
            Variable first;
            try {
              first = getToken(Tokens, index - 1);
              Variable second = getToken(Tokens, index + 1);
              value = first.getValueAsDouble() - second.getValueAsDouble();
              Variable result = new Variable(value);

              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              Tokens.remove(index - 1);

              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);

              Tokens.add(index - 1, result);
              tokenTypes.add(index - 1, result.getType());
            } catch (Exception ex) {
              Variable second = getToken(Tokens, index + 1);
              value = -second.getValueAsDouble();
              Variable result = new Variable(value);

              Tokens.remove(index);
              Tokens.remove(index);
              tokenTypes.remove(index);
              tokenTypes.remove(index);

              Tokens.add(index, result);
              tokenTypes.add(index, result.getType());
            }
            continue;
          }

          // --- addition
          index = tokenTypes.indexOf(SYMBOL_TYPE.OPERATION_ADD);
          if (index != -1) {
            Variable first;
            try {
              first = getToken(Tokens, index - 1);
              Variable second = getToken(Tokens, index + 1);
              if (first.isString() || second.isString()) {
                value = this.concatAsStrings(first.getValue(), second.getValue());
              } else {
                value = first.getValueAsDouble() + second.getValueAsDouble();
              }

              Variable result = new Variable(value);

              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);

              Tokens.add(index - 1, result);
              tokenTypes.add(index - 1, result.getType());
            } catch (Exception ex) {
              Variable second = getToken(Tokens, index + 1);
              value = second.getValueAsDouble();
              Variable result = new Variable(value);

              Tokens.remove(index);
              Tokens.remove(index);
              tokenTypes.remove(index);
              tokenTypes.remove(index);

              Tokens.add(index, result);
              tokenTypes.add(index, result.getType());
            }
            continue;
          }
          // --- Then should be << , >> and >>> operators

          throw new Exception("INTERNAL ERROR: we should not be here");
        }
        if (Tokens.get(0) != null && Tokens.get(0).isNumber()) {
          return Tokens.get(0).getValueAsDouble();
        }
      }
    }

    return value;
  }

  String concatAsStrings(Object first, Object second) {
    String s1 = null;
    if (first instanceof String) {
      s1 = (String) first;
    } else if (first instanceof Number) {
      if (first instanceof Double || first instanceof Float || first instanceof java.math.BigDecimal) {
        s1 = String.valueOf(((Number) first).doubleValue());
      } else {
        s1 = String.valueOf(((Number) first).intValue());
      }
    } else if (first != null) {
      s1 = first.toString();
    }
    String s2 = null;
    if (second instanceof String) {
      s2 = (String) second;
    } else if (second instanceof Number) {
      if (second instanceof Double || second instanceof Float || second instanceof java.math.BigDecimal) {
        s2 = String.valueOf(((Number) second).doubleValue());
      } else {
        s2 = String.valueOf(((Number) second).intValue());
      }
    } else if (second != null) {
      s2 = second.toString();
    }
    return s1 + s2;
  }

  Variable getToken(List<Variable> Tokens, int index) throws Exception {
    if (index < 0) {
      throw new Exception("No first argument in expression");
    } else if (index >= Tokens.size()) {
      throw new Exception("No second argument in expression");
    }
    return Tokens.get(index);
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public static void main(String[] args) {
    ExpressionParser expressionparser = new ExpressionParser();
    expressionparser.setInDegrees(false);
    expressionparser.caseSensitive = true;
    try {
      String express = null;

      express = "if (false) then";
      expressionparser.addLine(express);

      express = "List list";
      expressionparser.addLine(express);

      express = "Writer log(\"test.log\")";
      expressionparser.addLine(express);
      express = "log";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "println(\"Printing a Writer class object: \"+log)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "log.write(\"This is the first line: \"+log)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "log.newLine()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "log.write(\"This is the second line\")";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "log.close()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "Reader reader(\"test.log\")";
      expressionparser.addLine(express);
      express = "res = reader.readLine()";
      expressionparser.addLine(express);
      express = "res";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "res = reader.readLine()";
      expressionparser.addLine(express);
      express = "res";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "reader.close()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "true";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "TRUE";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "false";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "FALSE";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "PI";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "E";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      double var1 = 10.0, var2 = 20.;
      expressionparser.addVariable("var1", var1);
      expressionparser.addVariable("var2", var2);
      express = "var1+var2";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + (var1 + var2));

      express = "sqr = sqrt(4*5)";
      expressionparser.addLine(express);
      express = "sqr";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.sqrt(4 * 5));

      double RCH = 1.085;
      express = "RCH = 1.085";
      expressionparser.addLine(express);
      express = "RCH";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + RCH);

      double XCH = Math.sqrt(3) * (RCH / 3);
      express = "XCH = sqrt(3)*(RCH/3)";
      expressionparser.addLine(express);
      express = "XCH";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + XCH);

      express = "atan2(6,7)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.atan2(6, 7));

      express = "abs(6)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.abs(6));

      express = "abs(-6)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.abs(-6));

      express = "cbrt(27)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.cbrt(27));

      express = "cbrt(-27)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.cbrt(-27));

      express = "exp(1)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + Math.exp(1));

      express = "list.add(5)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.add(7)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.add(0,100)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.size()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.isEmpty()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.get(0)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.get(1)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.get(2)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.remove(1)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.size()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.set(0,1006)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.get(0)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.clear()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.size()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "list.isEmpty()";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "exp(1+1-1)";
      expressionparser.addLine("ab = 1");

      expressionparser.addLine("func(x,y,z) = x*ab+y**2-y*z");
      expressionparser.addLine("x = 5");
      expressionparser.addLine("y=func((3+4)*(x-2),4,5*6)+sqrt(4*5)");

      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "func(6,7,8)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "XCH";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "-XCH";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "0+1";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "sqr+1";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      express = "sin(10.0) + SIN(20.0)";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express)
          + " Ref: " + (Math.sin(10) + Math.sin(20.0)));
      // ---
      express = "str = \"This is a string";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"This is a string\"";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"This is a string\" + \" plus another string";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"This is a string plus a number: \" + 1001";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = 1001 +\" - This is a string appended to a number: \"";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"This is a string plus a numerical variable (sqr): \" + sqr";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"sqr = \" + sqr";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));

      express = "str = \"sqrt(900) = \" + sqrt(900)";
      expressionparser.addLine(express);
      express = "str";
      System.out.println("Evaluating: " + express + " : " + expressionparser.resolveExpression(express));
      // ---
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String line;
      while (!(line = in.readLine()).equals("quit")) {
        try {
          line = line.trim();
          if (line.matches("\\s*\\w+\\s*=\\s*.+") || expressionparser.isObjectTypeDeclaration(line)) {
            expressionparser.addLine(line);
          } else {
            System.out.println(expressionparser.resolveExpression(line));
          }
        } catch (Exception ex) {
          System.err.println(ex.getMessage());
        }
      }
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
    System.exit(0);

  }

  private class OpStack
      extends ArrayList {

    void push(Object token) {
      this.add(0, token);
    }

    Object pop() {
      if (this.size() == 0) {
        return null;
      }
      Object obj = this.get(0);
      this.remove(0);
      return obj;
    }

  }

  /**
   * private class PredefinedFun {
   *
   * PREDEFINED_FUNCTION Type;
   *
   * public PredefinedFun(PREDEFINED_FUNCTION type) { Type = type; }
   *
   * public double getValue(List<Double> parameters) throws Exception { double value; switch (Type) { case SQRT: case
   * sqrt: if (parameters.size() != 1) { throw new Exception("Predefined function " + Type.toString() + " should have
   * only one parameter"); } value = parameters.get(0); return Math.sqrt(value);
   *
   * case TAN: case tan: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.TAN.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.tan(value);
   *
   * case ATAN: case atan: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.ATAN.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.atan(value);
   *
   * case EXP: case exp: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.EXP.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.exp(value);
   *
   * case ASIN: case asin: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.ASIN.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.asin(value);
   *
   * case ACOS: case acos: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.ACOS.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.acos(value);
   *
   * case LOG: case log: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.LOG.toString() + " should have only one parameter"); } value = parameters.get(0); return
   * Math.log(value);
   *
   * case SIN: case sin: if (parameters.size() != 1) { throw new Exception("Predefined function " +
   * PREDEFINED_FUNCTION.SIN.toString() + " should have only one parameter"); } value = parameters.get(0);
   *
   * if (inRadians) { return Math.sin(value); } else { return Math.sin(value * DEGREES_TO_RADIANS); } case COS: case
   * cos: if (parameters.size() != 1) { throw new Exception("Predefined function " + PREDEFINED_FUNCTION.COS.toString()
   * + " should have only one parameter"); } value = parameters.get(0);
   *
   * if (inRadians) { return Math.cos(value); } else { return Math.cos(value * DEGREES_TO_RADIANS); }
   *
   * }
   * throw new Exception("INTERNAL ERROR: Unknown predefined function");
   *
   * }
   *
   * public double getValue(double value) throws Exception { switch (Type) { case SQRT: return Math.sqrt(value); case
   * TAN: return Math.tan(value); case ATAN: return Math.atan(value); case EXP: return Math.exp(value); case ASIN:
   * return Math.asin(value); case ACOS: return Math.acos(value); case LOG: return Math.log(value);
   *
   * case SIN: if (inRadians) { return Math.sin(value); } else { return Math.sin(value * DEGREES_TO_RADIANS); } case
   * COS: if (inRadians) { return Math.cos(value); } else { return Math.cos(value * DEGREES_TO_RADIANS); }
   *
   * }
   * throw new Exception("INTERNAL ERROR: Unknown predefined function"); } }
   */
}
