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
package cct.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import cct.math.PREDEFINED_FUNCTION;

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
//enum TYPE {

//  VARIABLE, LOCAL_VARIABLE, FUNCTION, OPERATION_ADD, OPERATION_SUB, OPERATION_MUL, OPERATION_DIV, OPERATION_POWER,
//  LEFT_PARANTHESIS,
//  RIGHT_PARANTHESIS, COMMA;
//}

//enum PREDEFINED_FUNCTION {

//  SQRT, sqrt, SIN, sin, COS, cos, TAN, tan, ASIN, asin, ACOS, acos, ATAN, atan, EXP, exp, LOG, log;
//}

//enum OPERATION {

  //ADD, SUBSTRACT, MULTIPLY, DEVIDE, POWER;
//}

//enum PARANTHESIS {

//  LEFT, RIGHT;
//}

//enum SEPARATOR {

//  COMMA;
//}

public class MathExpressionParser {

  static final double DEGREES_TO_RADIANS = Math.PI / 180.0;

  private static final char[] validSeparators = {
    '+', '-', '*', '/', '^', '(', ')', ','};

  private static final Map<String, OPERATION> validOperators = new HashMap<String, OPERATION>();
  private static final Map<String, TYPE> validOperations = new HashMap<String, TYPE>();
  private static final Map<String, PARANTHESIS> Parantheses = new HashMap<String, PARANTHESIS>(2);
  private Map<String, Object> definedFunctions = new HashMap<String, Object>();

  static {
    validOperators.put("+", OPERATION.ADD);
    validOperators.put("-", OPERATION.SUBSTRACT);
    validOperators.put("*", OPERATION.MULTIPLY);
    validOperators.put("/", OPERATION.DEVIDE);
    validOperators.put("^", OPERATION.POWER);

    validOperations.put("+", TYPE.OPERATION_ADD);
    validOperations.put("-", TYPE.OPERATION_SUB);
    validOperations.put("*", TYPE.OPERATION_MUL);
    validOperations.put("/", TYPE.OPERATION_DIV);
    validOperations.put("^", TYPE.OPERATION_POWER);

    Parantheses.put("(", PARANTHESIS.LEFT);
    Parantheses.put(")", PARANTHESIS.RIGHT);
  }

  private String Delimiters = " \t";
  private boolean caseSensitive = false;
  private boolean inRadians = true;

  Map<String, Object> IDs = new HashMap<String, Object>();
  static final Logger logger = Logger.getLogger(MathExpressionParser.class.getCanonicalName());

  //MathExpressionParser hiddenInit = new MathExpressionParser(true); // !!!
  private MathExpressionParser(boolean something) {

  }

  public MathExpressionParser() {
    definedFunctions.put("SQRT_1", new PredefinedFun(PREDEFINED_FUNCTION.SQRT));
    definedFunctions.put("sqrt_1", new PredefinedFun(PREDEFINED_FUNCTION.sqrt));
    definedFunctions.put("SIN_1", new PredefinedFun(PREDEFINED_FUNCTION.SIN));
    definedFunctions.put("sin_1", new PredefinedFun(PREDEFINED_FUNCTION.sin));
    definedFunctions.put("COS_1", new PredefinedFun(PREDEFINED_FUNCTION.COS));
    definedFunctions.put("cos_1", new PredefinedFun(PREDEFINED_FUNCTION.cos));
    definedFunctions.put("TAN_1", new PredefinedFun(PREDEFINED_FUNCTION.TAN));
    definedFunctions.put("tan_1", new PredefinedFun(PREDEFINED_FUNCTION.tan));
    definedFunctions.put("ASIN_1", new PredefinedFun(PREDEFINED_FUNCTION.ASIN));
    definedFunctions.put("asin_1", new PredefinedFun(PREDEFINED_FUNCTION.asin));
    definedFunctions.put("ACOS_1", new PredefinedFun(PREDEFINED_FUNCTION.ACOS));
    definedFunctions.put("acos_1", new PredefinedFun(PREDEFINED_FUNCTION.acos));
    definedFunctions.put("ATAN_1", new PredefinedFun(PREDEFINED_FUNCTION.ATAN));
    definedFunctions.put("atan_1", new PredefinedFun(PREDEFINED_FUNCTION.atan));
    definedFunctions.put("EXP_1", new PredefinedFun(PREDEFINED_FUNCTION.EXP));
    definedFunctions.put("epx_1", new PredefinedFun(PREDEFINED_FUNCTION.exp));
    definedFunctions.put("LOG_1", new PredefinedFun(PREDEFINED_FUNCTION.LOG));
    definedFunctions.put("log_1", new PredefinedFun(PREDEFINED_FUNCTION.log));

    logger.setLevel(Level.WARNING);
  }

  public boolean hasSymbol(String symbol) {
    if (!caseSensitive) {
      symbol = symbol.toUpperCase();
    }
      return IDs.containsKey( symbol ) || definedFunctions.containsKey( symbol );
  }

  public void setInDegrees(boolean in_degrees) {
    inRadians = !in_degrees;
  }

  public void setInRadians(boolean in_radians) {
    inRadians = in_radians;
  }

  public boolean addLine(String line) throws Exception {

    line = line.trim();
    if (line.length() < 1) {
      return true;
    }

    if (!line.contains("=")) {
      throw new Exception("Constant or function should be defined using = (equal) sign. Got " + line);
    }

    if ((line.indexOf("=") == line.length() - 1) || line.substring(line.indexOf("=") + 1).trim().length() == 0) {
      throw new Exception("Should be expression after the = (equal) sign. Got " + line);
    }

    if (!caseSensitive) {
      line = line.toUpperCase();
    }

    // --- Get id name
    String id = line.substring(0, line.indexOf("=")).trim();

    String expression = line.substring(line.indexOf("=") + 1);
    expression = expression.replaceAll("[*][*]", "^");

    if (id.matches(".+[(].+[)]")) { // Function
      parseFunction(id, expression);
      //throw new Exception("Definition of functions is not implemented yet");
    } else if (!id.contains("(") && !id.contains(")") && !id.contains(",")) { // Constant
      if (IDs.containsKey(id)) {
        throw new Exception("Identifier " + id + " is already defined");
      }
      IDs.put(id, null);
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
      function = new Function(definition);
      function.setFunctionBody(expression);
    } catch (Exception ex) {
      throw new Exception(definition + " : " + ex.getMessage());
    }

    if (definedFunctions.containsKey(function.internalName)) {
      throw new Exception("Function " + function.Name + " with " + function.localParams.size()
              + " parameters is already defined");
    }

    definedFunctions.put(function.internalName, function);

  }

  void parseConstant(String name, String expression) throws Exception {
    double value = resolveExpression(expression);
    Type var = new Type(name, TYPE.VARIABLE);
    var.setValue(value);
    IDs.put(name, var);
    logger.info(name + " = " + var.number);
  }

  public void addVariable(String name, double value) {
    Type var = new Type(name, TYPE.VARIABLE);
    var.setValue(value);
    IDs.put(name, var);
    logger.info("Added variable: " + name + " = " + var.number);
  }

  void resolveTokens(String expression, List<Type> Tokens, List<TYPE> tokenTypes) throws Exception {
    Tokens.clear();
    tokenTypes.clear();

    List tokens = new ArrayList();
    int leftParantheses = 0, rightParantheses = 0;

    boolean buildingID = false;
    String variable = "";
    for (int i = 0; i < expression.length(); i++) {
      //expression.charAt(i);
      String symbol = expression.substring(i, i + 1);

      if (Delimiters.contains(symbol)) {
        continue;
      }

      // --- Math operator
      if (validOperators.containsKey(symbol)) {
        if (buildingID) {
          buildingID = false;
          Tokens.add(new Type(variable, TYPE.VARIABLE));
          tokenTypes.add(TYPE.VARIABLE);
          tokens.add(variable);
          variable = "";
        }
        Tokens.add(new Type(validOperators.get(symbol), validOperations.get(symbol)));
        tokenTypes.add(validOperations.get(symbol));
        tokens.add(validOperators.get(symbol));
      } else if (symbol.equals("(")) {
        ++leftParantheses;
        if (buildingID) {
          buildingID = false;
          Tokens.add(new Type(variable, TYPE.FUNCTION));
          tokenTypes.add(TYPE.FUNCTION);
          tokens.add(variable);
          variable = "";
        }
        Tokens.add(new Type(Parantheses.get(symbol), TYPE.LEFT_PARANTHESIS));
        tokenTypes.add(TYPE.LEFT_PARANTHESIS);
        tokens.add(Parantheses.get(symbol));
      } else if (symbol.equals(")")) {
        ++rightParantheses;
        if (buildingID) {
          buildingID = false;
          Tokens.add(new Type(variable, TYPE.VARIABLE));
          tokenTypes.add(TYPE.VARIABLE);
          tokens.add(variable);
          variable = "";
        }
        Tokens.add(new Type(Parantheses.get(symbol), TYPE.RIGHT_PARANTHESIS));
        tokenTypes.add(TYPE.RIGHT_PARANTHESIS);
        tokens.add(Parantheses.get(symbol));
      } else if (symbol.equals(",")) {
        if (buildingID) {
          buildingID = false;
          Tokens.add(new Type(variable, TYPE.VARIABLE));
          tokenTypes.add(TYPE.VARIABLE);
          tokens.add(variable);
          variable = "";
        }
        Tokens.add(new Type(TYPE.COMMA));
        tokenTypes.add(TYPE.COMMA);
        tokens.add(SEPARATOR.COMMA);
      } else {
        buildingID = true;
        variable += symbol;
      }

    }

    if (buildingID) {
      Tokens.add(new Type(variable, TYPE.VARIABLE));
      tokenTypes.add(TYPE.VARIABLE);
      tokens.add(variable);
    }

    if (rightParantheses != leftParantheses) {
      throw new Exception("Expression: " + expression + "\nLeft and Right Parantheses are not balanced");
    }

  }

  public double resolveExpression(String expression) throws Exception {

    // --- Stupid algorithm
    // --- Resolve functions (if any)
    // --- Resolve expr in parantheses (if any)
    //ArrayList tokens = new ArrayList();
    List<Type> Tokens = new ArrayList<Type>();
    List<TYPE> tokenTypes = new ArrayList<TYPE>();

    resolveTokens(expression, Tokens, tokenTypes);

    // --- Resolve variables
    for (int i = 0; i < Tokens.size(); i++) {
      logger.info(Tokens.get(i).name + " " + Tokens.get(i).type.toString());
      Type type = Tokens.get(i);
      switch (type.type) {
        case VARIABLE:

          if (!IDs.containsKey(type.name) && !type.isNumber()) {
            throw new Exception("Expression: " + expression + "\nVariable " + type.name + " is not defined");
          }

          if (type.isNumber()) {
            continue;
          }

          Type var = (Type) IDs.get(type.name);
          type.setValue(var.number);
          break;

        case FUNCTION:
          break;
      }
    }

    double value = 0;
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
  double evaluateFunction(List<Type> Tokens, List<TYPE> tokenTypes) throws Exception {
    double value = 0;

    if (tokenTypes.size() < 3) {
      throw new Exception("Minimal function definition should be function_name()");
    }

    if (tokenTypes.get(0) != TYPE.FUNCTION) {
      throw new Exception("Function should start from funcion name. Got " + tokenTypes.get(0).toString());
    }
    if (tokenTypes.get(1) != TYPE.LEFT_PARANTHESIS) {
      throw new Exception("Left paranthesis should follow the funcion name. Got " + tokenTypes.get(1).toString());
    }
    if (tokenTypes.get(tokenTypes.size() - 1) != TYPE.RIGHT_PARANTHESIS) {
      throw new Exception("Right paranthesis should close funcion definition. Got "
              + tokenTypes.get(tokenTypes.size() - 1).toString());
    }

    // --- start to resolve arguments
    if (tokenTypes.size() == 3 && tokenTypes.get(1) != TYPE.RIGHT_PARANTHESIS) {
      throw new Exception("Minimal function definition should be function_name()");
    }
    List<Double> parameters = new ArrayList<Double>();
    List<Type> Expression = new ArrayList<Type>();
    List<TYPE> exprTypes = new ArrayList<TYPE>();
    int leftParanthesisDepth = 0;
    for (int i = 2; i < tokenTypes.size(); i++) {
      if (tokenTypes.get(i) == TYPE.LEFT_PARANTHESIS) {
        ++leftParanthesisDepth;
      } else if (tokenTypes.get(i) == TYPE.RIGHT_PARANTHESIS && leftParanthesisDepth > 0) {
        --leftParanthesisDepth;
      } else if (tokenTypes.get(i) == TYPE.COMMA || tokenTypes.get(i) == TYPE.RIGHT_PARANTHESIS) {
        Double Value = evaluateExpression(Expression, exprTypes);
        parameters.add(Value);
        logger.info("Par: " + parameters.size() + " value: " + Value);
        Expression.clear();
        exprTypes.clear();
        continue;
      }
      Expression.add(Tokens.get(i));
      exprTypes.add(Tokens.get(i).type);
    }

    String internalName = Tokens.get(0).name + "_" + String.valueOf(parameters.size());

    if (!definedFunctions.containsKey(internalName)) {
      throw new Exception("Function " + Tokens.get(0).name + " with " + parameters.size() + " parameters is not defined");
    }

    Object obj = definedFunctions.get(internalName);
    if (obj instanceof PredefinedFun) {
      return ((PredefinedFun) obj).getValue(parameters);
    } else if (obj instanceof Function) {
      return ((Function) obj).getValue(parameters);

    }

    throw new Exception("Unknown type of Function " + obj.getClass().getCanonicalName());
  }

  double evaluateExpression(List<Type> Tokens, List<TYPE> tokenTypes) throws Exception {
    double value = 0;

    if (Tokens.size() == 1) {
      if (Tokens.get(0).type == TYPE.VARIABLE || Tokens.get(0).type == TYPE.LOCAL_VARIABLE) {
        return Tokens.get(0).number;
      }
      throw new Exception("Single token must be either a variable or a number");
    }

    while (Tokens.size() > 1) {

      if (tokenTypes.contains(TYPE.FUNCTION)) {
        List<Type> Expression = new ArrayList<Type>();
        List<TYPE> exprTypes = new ArrayList<TYPE>();
        int index = tokenTypes.lastIndexOf(TYPE.FUNCTION);
        int end, leftParanthesisDepth = -1;

        logger.info("Resolving function " + Tokens.get(index).name);

        for (end = index; end < tokenTypes.size(); end++) {
          logger.info(end + " " + Tokens.get(end).type);
          Expression.add(Tokens.get(end));
          exprTypes.add(Tokens.get(end).type);
          if (tokenTypes.get(end) == TYPE.LEFT_PARANTHESIS) {
            ++leftParanthesisDepth;
            continue;
          } else if (tokenTypes.get(end) == TYPE.RIGHT_PARANTHESIS) {
            if (leftParanthesisDepth > 0) {
              --leftParanthesisDepth;
              continue;
            }
          }

          if (tokenTypes.get(end) == TYPE.RIGHT_PARANTHESIS) {
            value = evaluateFunction(Expression, exprTypes);
            logger.info("Function value: " + value);
            Type result = new Type(value);
            for (int i = index; i <= end; i++) {
              Tokens.remove(index);
              tokenTypes.remove(index);
            }
            Tokens.add(index, result);
            tokenTypes.add(index, result.type);
            break;
          }

        }
      } else if (tokenTypes.contains(TYPE.LEFT_PARANTHESIS)) {
        List<Type> Expression = new ArrayList<Type>();
        List<TYPE> exprTypes = new ArrayList<TYPE>();
        ((ArrayList) Expression).ensureCapacity(Tokens.size());
        ((ArrayList) exprTypes).ensureCapacity(Tokens.size());

        int indexEnd;
        while (true) {
          int indexStart = tokenTypes.lastIndexOf(TYPE.LEFT_PARANTHESIS);
          if (indexStart == -1) {
            break;
          }

          Expression.clear();
          exprTypes.clear();

          for (indexEnd = indexStart + 1; indexEnd < Tokens.size(); indexEnd++) {
            if (Tokens.get(indexEnd).type == TYPE.RIGHT_PARANTHESIS) {
              break;
            }
            Expression.add(Tokens.get(indexEnd));
            exprTypes.add(Tokens.get(indexEnd).type);
          }
          if (indexEnd == Tokens.size()) {
            throw new Exception("Unmatched (");
          }
          if (indexEnd - indexStart == 1) {
            throw new Exception("No expression within parantheses");
          }

          value = evaluateExpression(Expression, exprTypes);
          Type result = new Type(value);
          for (int i = indexStart; i <= indexEnd; i++) {
            Tokens.remove(indexStart);
            tokenTypes.remove(indexStart);
          }
          Tokens.add(indexStart, result);
          tokenTypes.add(indexStart, result.type);

        }
      } else {
        int index;
        while (Tokens.size() > 1) {
          // --- Power operation
          index = tokenTypes.indexOf(TYPE.OPERATION_POWER);
          if (index != -1) {
            Type first = getToken(Tokens, index - 1);
            Type second = getToken(Tokens, index + 1);
            value = Math.pow(first.number, second.number);
            Type result = new Type(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.type);
            continue;
          }

          // --- Division
          index = tokenTypes.indexOf(TYPE.OPERATION_DIV);
          if (index != -1) {
            Type first = getToken(Tokens, index - 1);
            Type second = getToken(Tokens, index + 1);
            value = first.number / second.number;
            Type result = new Type(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.type);
            continue;
          }

          // --- Multiplication
          index = tokenTypes.indexOf(TYPE.OPERATION_MUL);
          if (index != -1) {
            Type first = getToken(Tokens, index - 1);
            Type second = getToken(Tokens, index + 1);
            value = first.number * second.number;
            Type result = new Type(value);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            Tokens.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);
            tokenTypes.remove(index - 1);

            Tokens.add(index - 1, result);
            tokenTypes.add(index - 1, result.type);
            continue;
          }

          // --- Sub
          index = tokenTypes.indexOf(TYPE.OPERATION_SUB);
          if (index != -1) {
            Type first;
            try {
              first = getToken(Tokens, index - 1);
              Type second = getToken(Tokens, index + 1);
              value = first.number - second.number;
              Type result = new Type(value);

              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              Tokens.remove(index - 1);

              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);

              Tokens.add(index - 1, result);
              tokenTypes.add(index - 1, result.type);
            } catch (Exception ex) {
              Type second = getToken(Tokens, index + 1);
              value = -second.number;
              Type result = new Type(value);

              Tokens.remove(index);
              Tokens.remove(index);
              tokenTypes.remove(index);
              tokenTypes.remove(index);

              Tokens.add(index, result);
              tokenTypes.add(index, result.type);
            }
            continue;
          }

          // --- addition
          index = tokenTypes.indexOf(TYPE.OPERATION_ADD);
          if (index != -1) {
            Type first;
            try {
              first = getToken(Tokens, index - 1);
              Type second = getToken(Tokens, index + 1);
              value = first.number + second.number;
              Type result = new Type(value);

              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              Tokens.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);
              tokenTypes.remove(index - 1);

              Tokens.add(index - 1, result);
              tokenTypes.add(index - 1, result.type);
            } catch (Exception ex) {
              Type second = getToken(Tokens, index + 1);
              value = second.number;
              Type result = new Type(value);

              Tokens.remove(index);
              Tokens.remove(index);
              tokenTypes.remove(index);
              tokenTypes.remove(index);

              Tokens.add(index, result);
              tokenTypes.add(index, result.type);
            }
            continue;
          }

          throw new Exception("INTERNAL ERROR: we should not be here");
        }
        return Tokens.get(0).number;
      }
    }

    return value;
  }

  Type getToken(List<Type> Tokens, int index) throws Exception {
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
    MathExpressionParser mathexpressionparser = new MathExpressionParser();
    mathexpressionparser.setInDegrees(true);
    try {
      String express = null;
      mathexpressionparser.addVariable("var1", 10.0);
      mathexpressionparser.addVariable("var2", 20.0);
      express = "var1+var2";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));

      mathexpressionparser.caseSensitive = true;
      mathexpressionparser.addLine("sqr = sqrt(4*5)");
      mathexpressionparser.addLine("ab = 1");
      mathexpressionparser.addLine("func(x,y,z) = x*ab+y**2-y*z");
      mathexpressionparser.addLine("x = 5");
      mathexpressionparser.addLine("y=func((3+4)*(x-2),4,5*6)+sqrt(4*5)");

      mathexpressionparser.addLine("RCH = 1.085");
      mathexpressionparser.addLine("XCH = sqrt(3)*(RCH/3)");

      express = "XCH";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));
      express = "-XCH";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));

      express = "0+1";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));
      express = "sqr+1";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));
      express = "sin(10.0) + SIN(20.0)";
      System.out.println("Evaluating: " + express + " : " + mathexpressionparser.resolveExpression(express));
      // --  
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      String line;
      while(!(line=in.readLine()).equals("quit")) {
        if (line.matches("\\s*\\w+\\s*=\\s*.+")) {
          mathexpressionparser.addLine(line);
        } else {
        System.out.println(mathexpressionparser.resolveExpression(line));
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

  private class Type {

    TYPE type;
    OPERATION operation;
    PARANTHESIS paranthesis;
    String name;
    double number;
    boolean is_number = false;

    public Type(double value) {
      this.name = String.valueOf(value);
      this.type = TYPE.VARIABLE;
      number = value;
      is_number = true;
    }

    public Type(String name, TYPE type) {
      this.name = name;
      this.type = type;
    }

    public Type(OPERATION op, TYPE type) {
      operation = op;
      this.type = type;
    }

    public Type(PARANTHESIS par, TYPE type) {
      paranthesis = par;
      this.type = type;
    }

    public Type(TYPE type) throws Exception {
      if (type != TYPE.COMMA) {
        throw new Exception("INTERNAL ERROR: Not comma");
      }
      this.type = type;
    }

    public void setValue(double value) {
      number = value;
      is_number = true;
    }

    public boolean isNumber() {
      try {
        number = Double.parseDouble(name);
        is_number = true;
      } catch (Exception ex) {
        return false;
      }
      return true;
    }

  }

  private class Function {

    String Name;
    String internalName;
    Map<String, Object> locParams = new HashMap<String, Object>();
    List localParams = new ArrayList();
    List<Type> functionTokens;
    List<TYPE> functionTokenTypes;

    public Function(String definition) throws Exception {
      defineFunction(definition);
    }

    void defineFunction(String definition) throws Exception {
      List<Type> Tokens = new ArrayList<Type>();
      List<TYPE> tokenTypes = new ArrayList<TYPE>();
      resolveTokens(definition, Tokens, tokenTypes);

      if (tokenTypes.size() < 3) {
        throw new Exception("Minimal Function definition should be: function_name()");
      }

      if (tokenTypes.size() % 2 == 1) {
        throw new Exception("Wrong Function definition");
      }

      if (tokenTypes.get(0) != TYPE.FUNCTION) {
        throw new Exception("Function definition should start from function_name(");
      }

      if (tokenTypes.get(1) != TYPE.LEFT_PARANTHESIS) {
        throw new Exception("Left paranthesis should follow function name");
      }

      Name = Tokens.get(0).name;

      int index = 2;
      while (tokenTypes.get(index) != TYPE.RIGHT_PARANTHESIS) {
        if (index >= tokenTypes.size() - 1) {
          break;
        }
        if (tokenTypes.get(index) != TYPE.VARIABLE) {
          throw new Exception("Expecting parameter in Function definition, got " + tokenTypes.get(index).toString());
        }

        if (tokenTypes.get(index + 1) != TYPE.COMMA && tokenTypes.get(index + 1) != TYPE.RIGHT_PARANTHESIS) {
          throw new Exception("Expecting comma or right paranthesis in Function definition, got "
                  + tokenTypes.get(index).toString());
        }

        if (locParams.containsKey(Tokens.get(index).name)) {
          throw new Exception("Duplicate definition of parameter " + Tokens.get(index).name);
        }

        if (Tokens.get(index).isNumber()) {
          throw new Exception("Parameter should be symbolic variable. Got " + Tokens.get(index).name);
        }

        locParams.put(Tokens.get(index).name, null);
        localParams.add(Tokens.get(index).name);

        index += 2;
        if (index >= tokenTypes.size() - 1) {
          break;
        }

      }

      internalName = Name + "_" + String.valueOf(localParams.size());
      logger.info("Fun name: " + Name + " Internal name: " + internalName + " N params: " + localParams.size());
      for (int i = 0; i < localParams.size(); i++) {
        logger.info((i + 1) + " : " + localParams.get(i).toString());

      }
    }

    void setFunctionBody(String body) throws Exception {
      List<Type> Tokens = new ArrayList<Type>();
      List<TYPE> tokenTypes = new ArrayList<TYPE>();
      resolveTokens(body, Tokens, tokenTypes);
      if (Tokens.size() < 1) {
        throw new Exception("Empty function body");
      }
      for (int i = 0; i < Tokens.size(); i++) {
        if (tokenTypes.get(i) == TYPE.VARIABLE && locParams.containsKey(Tokens.get(i).name)) {
          Tokens.get(i).type = TYPE.LOCAL_VARIABLE;
          tokenTypes.set(i, TYPE.LOCAL_VARIABLE);
        } else if (tokenTypes.get(i) == TYPE.VARIABLE) {
          if (!IDs.containsKey(Tokens.get(i).name) && !Tokens.get(i).isNumber()) {
            throw new Exception("Function body: " + body + "\nVariable " + Tokens.get(i).name + " is not defined");
          } else if (IDs.containsKey(Tokens.get(i).name)) {
            Type type = (Type) IDs.get(Tokens.get(i).name);
            Tokens.set(i, type);
          }
        }
      }

      functionTokens = Tokens;
        functionTokenTypes = tokenTypes;
    }

    public double getValue(List<Double> arguments) throws Exception {
      double value = 0;
      for (int i = 0; i < functionTokens.size(); i++) {
        if (functionTokenTypes.get(i) == TYPE.LOCAL_VARIABLE) {
          int index = localParams.indexOf(functionTokens.get(i).name);
          if (index == -1) {
            throw new Exception("Internal Error: index == -1");
          }
          functionTokens.get(i).number = arguments.get(index);
        }
      }

      List<Type> Tokens = new ArrayList<Type>(functionTokens);
      List<TYPE> tokenTypes = new ArrayList<TYPE>(functionTokenTypes);

      value = evaluateExpression(Tokens, tokenTypes);
      return value;
    }
  }

  private class PredefinedFun {

    PREDEFINED_FUNCTION Type;

    public PredefinedFun(PREDEFINED_FUNCTION type) {
      Type = type;
    }

    public double getValue(List<Double> parameters) throws Exception {
      double value;
      switch (Type) {
        case SQRT:
        case sqrt:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + Type.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.sqrt(value);

        case TAN:
        case tan:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.TAN.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.tan(value);

        case ATAN:
        case atan:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.ATAN.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.atan(value);

        case EXP:
        case exp:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.EXP.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.exp(value);

        case ASIN:
        case asin:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.ASIN.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.asin(value);

        case ACOS:
        case acos:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.ACOS.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.acos(value);

        case LOG:
        case log:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.LOG.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);
          return Math.log(value);

        case SIN:
        case sin:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.SIN.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);

          if (inRadians) {
            return Math.sin(value);
          } else {
            return Math.sin(value * DEGREES_TO_RADIANS);
          }
        case COS:
        case cos:
          if (parameters.size() != 1) {
            throw new Exception("Predefined function " + PREDEFINED_FUNCTION.COS.toString()
                    + " should have only one parameter");
          }
          value = parameters.get(0);

          if (inRadians) {
            return Math.cos(value);
          } else {
            return Math.cos(value * DEGREES_TO_RADIANS);
          }

      }
      throw new Exception("INTERNAL ERROR: Unknown predefined function");

    }

    public double getValue(double value) throws Exception {
      switch (Type) {
        case SQRT:
          return Math.sqrt(value);
        case TAN:
          return Math.tan(value);
        case ATAN:
          return Math.atan(value);
        case EXP:
          return Math.exp(value);
        case ASIN:
          return Math.asin(value);
        case ACOS:
          return Math.acos(value);
        case LOG:
          return Math.log(value);

        case SIN:
          if (inRadians) {
            return Math.sin(value);
          } else {
            return Math.sin(value * DEGREES_TO_RADIANS);
          }
        case COS:
          if (inRadians) {
            return Math.cos(value);
          } else {
            return Math.cos(value * DEGREES_TO_RADIANS);
          }

      }
      throw new Exception("INTERNAL ERROR: Unknown predefined function");
    }
  }

}
