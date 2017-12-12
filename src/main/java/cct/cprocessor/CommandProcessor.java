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
package cct.cprocessor;

import cct.j3d.Java3dUniverse;
import cct.math.MathExpressionParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>
 * Description: Computational Chemistry Toolkit</p>
 *
 * <p>
 * Copyright: Copyright (c) 2007</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class CommandProcessor {

  public enum CONSTANTS {

    PREVIOUS_RESULT
  }

    public static final String COMMANDS_PROPERTY_FILE = "cct/cprocessor/commands.properties";
  private Properties commandsProperties = null;
  private URL cctCommandsURL = null;
  private Map<String, CommandInterface> commandProviders = new HashMap<String, CommandInterface>();
  private Map<String, Variable> variables = new HashMap<String, Variable>();
  private boolean oneVariableInstanceOnly = true;
  private AssignmentOperator assignmentOperator = new AssignmentOperator();
  private Java3dUniverse renderer = null;
  private boolean allowVariableRedifination = true;

  private List<AtomicCommand> compiledCommands = new ArrayList<AtomicCommand>();
  private List<Integer> forLoopTracker = new ArrayList<Integer>();

  Commands currentCommand = null;
  Object commandObject = null;
  Object commandArguments = null;

  static final Logger logger = Logger.getLogger(CommandProcessor.class.getCanonicalName());

  public CommandProcessor() throws Exception {
    commandsProperties = getProperties();

    for (Map.Entry entry : commandsProperties.entrySet()) {
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
        if (!(obj instanceof CommandInterface)) {
          logger.severe("Class " + className + " does not implement " + CommandInterface.class.getName()
              + " interface. Ignored...");
          continue;
        }
        // ----

        commandProviders.put(name, (CommandInterface) obj);
      } catch (Exception ex) {
        logger.severe("Error while loading Class " + className + ex.getMessage() + " Ignored...");
        continue;
      }

    }
    // Setup "inbuilt" commands
    for (String cmd : GUIWrapperCommand.supportedCommands()) {
      commandProviders.put(cmd, new GUIWrapperCommand(renderer));
    }
    ForLoopCommand forLoop = new ForLoopCommand();
    forLoop.setCommandProcessor(this);
    commandProviders.put("for", forLoop);

    EndForLoopCommand endforLoop = new EndForLoopCommand();
    endforLoop.setCommandProcessor(this);
    commandProviders.put("endfor", endforLoop);

    PrintCommand print = new PrintCommand();
    print.setCommandProcessor(this);
    commandProviders.put("print", print);

    print = new PrintCommand();
    print.setCommandProcessor(this);
    commandProviders.put("CreateLog", print);
  }

  public void setLoggerLevel(Level level) {
    logger.setLevel(level);
  }

  public Properties getProperties() throws Exception {

    commandsProperties = getProperties(COMMANDS_PROPERTY_FILE);
    return commandsProperties;
  }

  public Properties getProperties(String propertiesFile) throws Exception {

    commandsProperties = null;
    cctCommandsURL = null;
    try {
      cctCommandsURL = CommandProcessor.class.getClassLoader().getResource(propertiesFile);
      commandsProperties = new Properties();
      commandsProperties.load(cctCommandsURL.openStream());
      logger.info("Loaded commands properties file " + propertiesFile);
    } catch (Exception ex) {
      logger.warning("Cannot open commands properties file " + propertiesFile + ": " + ex.getMessage());
    }
    return commandsProperties;
  }

  static public Number getNumber(String numStr) throws Exception {
    try {
      Integer inum = Integer.parseInt(numStr);
      return inum;
    } catch (Exception ex) {

    }

    Double dnum = null;
    try {
      dnum = Double.parseDouble(numStr);
      return dnum;
    } catch (Exception ex) {
      throw new Exception(numStr + " is not a valid number");
    }
  }

  /**
   * Use this function to add a new variable. DO not use a direct add
   *
   * @param variable
   */
  public void addVariable(Variable variable) throws Exception {
    if (!this.allowVariableRedifination && variables.containsKey(variable.getName())) {
      throw new Exception("Variable " + variable.getName() + " was already defined. Allow Variable Redifinition to avoid this error");
    }
    variables.put(variable.getName(), variable);
  }

  public boolean isHasVariable(String variableName) {
    return variables.containsKey(variableName);
  }

  public Variable getVariable(String variableName) {
    return variables.get(variableName);
  }

  public int numberForLoopsInProcessing() {
    if (forLoopTracker == null) {
      return 0;
    }
    return forLoopTracker.size();
  }

  public void addForLoopForProcessing(int number) {
    forLoopTracker.add(0, number);
  }

  public int extractForLoopStart() {
    if (forLoopTracker == null) {
      return -1;
    }
    int n = forLoopTracker.get(0);
    forLoopTracker.remove(0); // Remove for entry from the stack queue
    return n;
  }

  public AtomicCommand getCompiledCommand(int n) {
    return compiledCommands.get(n);
  }

  public void addCompiledCommand(AtomicCommand command) {
    compiledCommands.add(command);
  }

  public int numberOfCompiledCommands() {
    return compiledCommands.size();
  }

  public Java3dUniverse getRenderer() {
    return renderer;
  }

  public void setRenderer(Java3dUniverse renderer) {
    this.renderer = renderer;
    for (String cmd : GUIWrapperCommand.supportedCommands()) {
      ((GUIWrapperCommand) commandProviders.get(cmd)).setRenderer(renderer);
    }
  }

  public static void main(String[] args) {
    try {
      CommandProcessor commandprocessor = new CommandProcessor();
      Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.WARNING);
      commandprocessor.setLoggerLevel(Level.WARNING);
      commandprocessor.parseScript("x=1\n"
          + "gjh = 1.68765\n"
          + "log = CreateLog\n"
          + "log setLog analysis.log\n"
          + "mol = loadGJF ferrocene-admp-20c.com\n"
          + "traj = TrajectoryManager\n"
          + "traj set mol\n"
          //+ "traj load g09-md.out g09\n"
          + "traj load /home/vvv900/fuji1/Fc/ferrocene-admp-20c.log g09\n"
          + "snaps = traj getNumSnaps\n"
          + "print Number of snapshots: snaps\n"
          + "snaps = snaps - 1 \n"
          + "print Stop value for loop over trajectories: snaps\n"
          + "list1 = List\n"
          + "list2 = List\n"
          + "list3 = List\n"
          + "for iter 0 snaps 2\n"
          + "  print Snapshot: iter\n"
          + "  coords = traj getXYZ iter\n"
          + "  print Coordinates: coords\n"
          + "  Molecule setXYZ mol coords\n"
          + "  centroid_1 = Molecule centroid mol 1-5\n"
          + "  print Centroid 1: centroid_1\n"
          + "  centroid_2 = Molecule centroid mol 6-10\n"
          + "  print Centroid 2: centroid_2\n"
          + "  Fe = Molecule getAtom mol 21\n"
          + "  C_1 = Molecule getAtom mol 1\n"
          + "  C_2 = Molecule getAtom mol 6\n"
          + "  Fe_1 = Geometry distance Fe centroid_1\n"
          + "  print Fe-Centroid 1 distance: Fe_1\n"
          + "  Fe_2 = Geometry distance Fe centroid_2\n"
          + "  C_C = Geometry distance centroid_1 centroid_2\n"
          + "  print Fe-Centroid 2 distance: Fe_2\n"
          + "  dihed = Geometry dihedral C_1 centroid_1 centroid_2 C_2\n"
          + "  log Fe_1 Fe_2 C_C dihed\n"
          + "  list1 add Fe_1\n"
          + "  list2 add Fe_2\n"
          + "endfor\n"
          + "log close\n"
          + "data1 = list1 toArray\n"
          + "data2 = list2 toArray\n"
          + "corr = Stat linearCorrelationCoefficient data1 data2\n"
          + "print Linear correlation coeff for Fe-C_1 and Fe-C_2 distances: corr\n", 1);
      commandprocessor.printCompiledCommands();
      commandprocessor.executeScript();
    } catch (Exception ex) {
      Logger.getLogger(CommandProcessor.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.exit(0);
  }

  public void executeScript() throws Exception {
    executeScript(compiledCommands);
  }

  public void executeScript(List<AtomicCommand> compiled_commands) throws Exception {
    boolean printExecution = false;
    Object previousResult = null;
    //if (false) {
    //  for (int i = 0; i < compiled_commands.size(); i++) {
    //    AtomicCommand command = compiled_commands.get(i);
    //    if (command.isNeedPreviousResult()) {
    //      command.setPreviousResult(previousResult);
    //    }
    //    Object result = command.executeCommand();
    //    previousResult = result;
    //  }
    //}

    int commandCounter = 0;
    while (commandCounter < compiled_commands.size()) {
      AtomicCommand command = compiled_commands.get(commandCounter);
      if (command.isNeedPreviousResult()) {
        command.setPreviousResult(previousResult);
      }
      if (printExecution) {
        System.out.println("\nExecuting command: " + commandCounter + " : "
            + command.getCommand().getClass().getSimpleName()
            + "\nArguments:");
        if (command.getParsedArguments() != null) {
          for (int i = 0; i < command.getParsedArguments().length; i++) {
            System.out.print(i + ": ");
            printArgument(command.getParsedArguments()[i]);
          }
        }
      }
      Object result = command.executeCommand();
      if (printExecution) {
        System.out.println("Result: ");
        printArgument(result);
      }
      previousResult = result;
      if (command.getCommand() instanceof InbuiltCommandInterface) {
        int newCounter = ((InbuiltCommandInterface) command.getCommand()).getNewCommandCounter();
        if (newCounter != -1) {
          commandCounter = newCounter;
          continue;
        }
      }
      ++commandCounter;
    }
  }

  public void printCompiledCommands() {
    printCompiledCommands(compiledCommands);
  }

  public void printCompiledCommands(List<AtomicCommand> compiled_commands) {
    System.out.println("\nCompiled commands: ");
    for (int i = 0; i < compiled_commands.size(); i++) {
      AtomicCommand command = compiled_commands.get(i);
      System.out.printf("%3d %-30s ", i, command.getCommand().getClass().getSimpleName());
      Object[] args = command.getParsedArguments();
      if (args == null) {
        System.out.println(args);
      } else {
        for (Object obj : args) {
          if (obj == null) {
            System.out.print(" " + obj);
          } else {
            System.out.print(" " + obj.toString());
          }

        }
        System.out.print("\n");
      }
    }
  }

  public void parseScript(String filename, int fileType) throws Exception {

    String line;
    BufferedReader in = null;
    StringWriter sWriter = new StringWriter();
    int lineNumber = 0;

    if (fileType == 0) { // Read from file
      try {
        in = new BufferedReader(new FileReader(filename));
      } catch (Exception ex) {
        throw ex;
      }
    } else if (fileType == 1) { // Read from String
      in = new BufferedReader(new StringReader(filename));
    } else {
      throw new Exception(
          "parseScript: INTERNAL ERROR: Unknown file type");
    }

    try {
      //BufferedReader in = new BufferedReader(new FileReader(filename));

      Variable variable = null;
      boolean setValue = false;
      String variableName = "", subLine = "";

      while ((line = in.readLine()) != null) {
        setValue = false;
        subLine = line.trim();
        ++lineNumber;

        if (subLine.length() < 1) { // Blank line
          continue;
        }

        if (subLine.startsWith("!") || subLine.startsWith("#") || subLine.startsWith(";")) {
          continue;
        }

        logger.info("Parsing line " + lineNumber + ": " + line);

        // variable = value(s): assignment operator
        if (subLine.matches("\\w+\\s*=\\s*\\w+.*")) {

          setValue = true;
          variableName = subLine.substring(0, subLine.indexOf("=")).trim();
          if (!this.isHasVariable(variableName)) {
            variable = new Variable(variableName, null);
            this.addVariable(variable);
          }

          logger.info("Found variable: " + variableName + " in line: " + line);

          subLine = subLine.substring(subLine.indexOf("=") + 1).trim();
        }

        String[] tokens = subLine.split("\\s+");

        // --- Now we assume that the first token is a command name or a number
        if (tokens.length == 1 && setValue && !isItCommand(tokens[0])) {
          try {
            Integer iNum = Integer.parseInt(tokens[0]);
            if (!this.isHasVariable(variableName)) {
              variable = new Variable(variableName, iNum);
              this.addVariable(variable);
            } else {
              variable = this.getVariable(variableName);
              variable.setValue(iNum);
            }

            logger.info("Line: " + line + " is an integer number assignment: " + variableName + "=" + iNum);
            continue;
          } catch (Exception ex) {
            // --- Is is a real number?...
            try {
              Double dNum = Double.parseDouble(tokens[0]);
              if (!this.isHasVariable(variableName)) {
                variable = new Variable(variableName, dNum);
                this.addVariable(variable);
              } else {
                variable = this.getVariable(variableName);
                variable.setValue(dNum);
              }
              logger.info("Line: " + line + " is a real number assignment: " + variableName + "=" + dNum);
              continue;
            } catch (Exception e) {
              logger.info("Line: " + line + " is not a number assignment...");
            }
          }
          // --- Check for object
          CommandInterface command = commandProviders.get(tokens[0]);
          if (command != null) {
            if (!this.isHasVariable(variableName)) {
              variable = new Variable(variableName, command);
              this.addVariable(variable);
              logger.info("Line: " + line + " this is variable of type " + variable.getValue().getClass().getCanonicalName());
              continue;
            }

          }
        } // End "if" for ONE token after a "="

        // --- if it's initialization statement
        if (tokens.length == 1 && setValue && isItCommand(tokens[0])) {
          CommandInterface command = this.getCommand(tokens[0]);
          variable = this.getVariable(variableName);
          variable.setValue(command.ciInstance());
          continue;
        }

        // --- Now check for math expression
        if (setValue) {
          CommandInterface command = commandProviders.get(tokens[0]);
          if (isItCommand(tokens[0]) || this.isItVariableCommand(tokens[0])) {

          } else {
            if (isMathExpression(subLine)) {
              // -- Add expression operator
              Object[] args = {subLine};
              this.addCompiledCommand(new AtomicCommand(new MathExpressionOperator(this), args));
              // -- Add assinment operator
              variable = this.getVariable(variableName);
              args = new Object[]{variable, CONSTANTS.PREVIOUS_RESULT};
              this.addCompiledCommand(new AtomicCommand(assignmentOperator, args));
              continue;
            }
          }
        }

        CommandInterface command = commandProviders.get(tokens[0]);
        if (command == null) {
          // --- Check for variable-command
          if (this.isHasVariable(tokens[0])) {
            Variable var = this.getVariable(tokens[0]);
            if (var.getValue() instanceof CommandInterface) {
              command = (CommandInterface) var.getValue();

            } else {
              throw new Exception("Variable-command should be of type " + CommandInterface.class
                  .getCanonicalName()
                  + " Got: " + var.getValue().getClass().getCanonicalName());
            }
          } else {
            throw new Exception("Unknown command " + tokens[0] + " in line " + lineNumber);
          }
        }

        Object[] commandArguments = command.ciParseCommand(tokens);
        if (commandArguments == null) {

        } else {
          for (int i = 0; i < commandArguments.length; i++) {
            Object item = commandArguments[i];
            if (item instanceof String && variables.containsKey(item.toString())) {
              commandArguments[i] = variables.get(item.toString());
              logger.info("Substituting variable " + item.toString());
            } else if (item instanceof Variable) {
              Variable var = (Variable) item;
              if (variables.containsKey(var.getName()) && !(command instanceof InbuiltCommandInterface)) {
                throw new Exception("Variable " + var.getName() + " is already defined");
              } else {
                variables.put(var.getName(), var);
              }
            }
          }
          // --- Record a compiled command
          AtomicCommand atomicCommand = new AtomicCommand(command, commandArguments);
          compiledCommands.add(atomicCommand);
        }

        // --- Extra step in the case of assignment operator
        if (setValue) {
          variable = this.getVariable(variableName);
          Object[] args = {variable, CONSTANTS.PREVIOUS_RESULT};

          AtomicCommand assignCommand = new AtomicCommand(assignmentOperator, args);
          this.addCompiledCommand(assignCommand);
        }
      }

      in.close();

    } catch (Exception e) {
      throw e;
    }

    if (forLoopTracker.size() > 0) {
      sWriter.append("Unmatched for loop\n");
    }

    String errors = sWriter.toString();
    if (errors != null && errors.length() > 0) {
      throw new Exception(errors);
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Parsed " + lineNumber + " lines\n");
    sb.append("Number of variables: " + this.variables.size() + "\n");
    if (variables.size() > 0) {
      sb.append("Variables: \n");
      for (String key : variables.keySet()) {
        sb.append(key + " = " + variables.get(key).getValue() + "\n");
      }
    }
    logger.info(sb.toString());
  }

  public boolean isItVariableCommand(String variable) {
    if (this.isHasVariable(variable)) {
      Variable var = this.getVariable(variable);
      if (var.getValue() instanceof CommandInterface) {
        return true;
      }
    }
    return false;
  }

  public boolean isItCommand(String token) {
    return commandProviders.containsKey(token);
  }

  public CommandInterface getCommand(String commName) {
    return commandProviders.get(commName);
  }

  public boolean isMathExpression(String line) throws Exception {
    MathExpressionParser mathExpressionParser = new MathExpressionParser();
    mathExpressionParser.setCaseSensitive(true);
    for (String varName : variables.keySet()) {
      if (this.isItVariableCommand(varName)) {
        continue;
      }
      mathExpressionParser.addVariable(varName, 0);
    }
    try {
      mathExpressionParser.addLine("x$$$=" + line);
      return true;
    } catch (Exception ex) {
      throw new Exception(line + " : " + ex.getMessage());
    }
  }

  public double evaluateMathExpression(String line) throws Exception {
    MathExpressionParser mathExpressionParser = new MathExpressionParser();
    mathExpressionParser.setCaseSensitive(true);

    for (String varName : variables.keySet()) {
      if (this.isItVariableCommand(varName)) { // Skip variable commands
        continue;
      }
      // --- Some variables is not defined yet at this point
      Variable var = this.getVariable(varName);
      if (var.getValue() == null) {
        continue;
        //throw new Exception("Evaluating \"" + line + "\" : variable " + varName + " is not defined");
      }

      if (isObjectArray(var.getValue())) {
        continue;
        //throw new Exception("Evaluating \"" + line + "\" : variable " + varName + " is not a scalar");
      }

      if (var.getValue() instanceof Number) {
        mathExpressionParser.addVariable(varName, ((Number) var.getValue()).doubleValue());
        continue;
      }

      if (var.getValue() instanceof String) {
        try {
          double value = Double.parseDouble(var.getValue().toString());
          mathExpressionParser.addVariable(varName, value);
        } catch (Exception ex) {
          throw new Exception("Evaluating \"" + line + "\" : cannot convert " + varName + "=" + var.getValue().toString()
              + " to double value");
        }
        // --- Desperate attempt to conver variable into double

        try {
          double value = Double.parseDouble(var.getValue().toString());
          mathExpressionParser.addVariable(varName, value);
          System.err.println("Warning: converted Object value into double...");
        } catch (Exception ex) {
          throw new Exception("Evaluating \"" + line + "\" : cannot convert " + varName + "=" + var.getValue().toString()
              + " to double value");
        }

      }

    }
    // --- Evaluate 
    try {
      return mathExpressionParser.resolveExpression(line);
    } catch (Exception ex) {
      throw new Exception(line + " : " + ex.getMessage());
    }
  }

  public static boolean isObjectArray(Object obj) {
    return obj.getClass().isArray();
  }

  public boolean isOneVariableInstanceOnly() {
    return oneVariableInstanceOnly;
  }

  public void setOneVariableInstanceOnly(boolean oneVariableInstanceOnly) {
    this.oneVariableInstanceOnly = oneVariableInstanceOnly;
  }

  public boolean isAllowVariableRedifination() {
    return allowVariableRedifination;
  }

  public void setAllowVariableRedifination(boolean allowVariableRedifination) {
    this.allowVariableRedifination = allowVariableRedifination;
  }

  public static void printArgument(Object arg) {
    if (arg == null) {

      System.out.print(arg);
    } else if (arg.getClass().isPrimitive()) {
      if (arg instanceof float[]) {
        for (int i = 0; i < ((float[]) arg).length; i++) {
          System.out.print(i + ": " + arg + "\n");
        }
      } else if (arg instanceof float[][]) {
        for (int i = 0; i < ((float[]) arg).length; i++) {
          System.out.print(i + ": ");
          for (int j = 0; j < ((float[][]) arg).length; j++) {
            System.out.print(i + ": " + ((float[][]) arg)[i][j]);
          }
          System.out.print("\n");
        }
      } else if (isObjectArray(arg)) {

      } else {
        Object[] array = (Object[]) arg;
        for (int i = 0; i < array.length; i++) {
          if (array[i] != null) {
            System.out.print(i + ": " + array[i].toString() + "\n");
          } else {
            System.out.print(i + ": " + array[i] + "\n");
          }
        }
      }
    } else {
      System.out.print(arg.toString() + "\n");
    }
  }

  public static Integer getIntValue(Object obj) throws Exception {
    if (obj == null) {
      throw new Exception("Cannot convert null into Integer value");
    }
    if (obj instanceof Number) {
      return ((Number) obj).intValue();
    }

    if (obj instanceof Variable) {
      Variable var = (Variable) obj;
      return var.getIntValue();
    }

    if (obj instanceof String) {
      return getIntValue((String) obj);
    }
    throw new Exception("Cannot convert object " + obj.getClass().getCanonicalName() + " into the scalar Integer value");
  }

  public static Integer getIntValue(String str) throws Exception {
    return Integer.parseInt(str.trim());
  }

  public static boolean isNumber(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Number) {
      return true;
    }
    try {
      Double.parseDouble(obj.toString().trim());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public static boolean isIntegerNumber(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Integer || obj instanceof Long || obj instanceof Short
        || obj instanceof BigInteger || obj instanceof AtomicInteger) {
      return true;
    }
    try {
      Integer.parseInt(obj.toString().trim());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public static Double getDoubleValue(Object obj) throws Exception {
    if (obj == null) {
      throw new Exception("Cannot convert null into double value");
    }
    if (obj instanceof Number) {
      return ((Number) obj).doubleValue();
    }

    if (obj instanceof Variable) {
      Variable var = (Variable) obj;
      if (var.getValue() instanceof Number) {
        return ((Number) var.getValue()).doubleValue();
      }
      if (var.getValue() instanceof String) {
        return getDoubleValue((String) var.getValue());
      }
      throw new Exception("Cannot convert object " + var.getValue().getClass().getCanonicalName() + " of variable "
          + var.getName() + " into the scalar double value");
    }

    if (obj instanceof String) {
      return getDoubleValue((String) obj);
    }
    throw new Exception("Cannot convert object " + obj.getClass().getCanonicalName() + " into the scalar double value");
  }

  public static Double getDoubleValue(String str) throws Exception {
    return Double.parseDouble(str.trim());
  }
}
