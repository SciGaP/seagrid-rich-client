/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import java.util.List;
import java.util.Map;

/**
 *
 * @author vvv900
 */
public class ForLoopCommand implements InbuiltCommandInterface {

  private CommandProcessor commandProcessor;
  private int switchToCommand = -1;

  public ForLoopCommand() {
  }

  public ForLoopCommand(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public CommandInterface ciInstance() {
    return new ForLoopCommand(commandProcessor);
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args.length != 4) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop execution command requires 4 arguments");
    }
    // --- Get Control loop variable
    Variable iVar = (Variable) args[0];
    Number i = (Number) iVar.getValue();
    // --- Get "stop variable
    Number stop;
    if (args[1] instanceof Variable) {
      Variable stopVar = (Variable) args[1];
      stop = (Number) stopVar.getValue();
    } else {
      stop = (Number) args[1];
    }

    // --- Get "step"
    Number step;
    if (args[2] instanceof Variable) {
      Variable stepVar = (Variable) args[2];
      step = (Number) stepVar.getValue();
    } else {
      step = (Number) args[2];
    }

    // -- Get jump value
    Number jump = (Number) args[3];
    // ---
    if (step.doubleValue() == 0) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop execution command: step cannot be 0");
    } else if (step.doubleValue() > 0) {
      if (i.doubleValue() <= stop.doubleValue()) {
        switchToCommand = -1;
      } else {
        switchToCommand = jump.intValue();
      }
    } else {
      if (i.doubleValue() >= stop.doubleValue()) {
        switchToCommand = -1;
      } else {
        switchToCommand = jump.intValue();
      }
    }
    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length == 0) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop command: parameters are not set");
    }

    if (!tokens[0].equals("for")) {
      throw new Exception(this.getClass().getCanonicalName()
          + ": expected for loop command, got: " + tokens[0]);
    }
    // for i start stop [step]
    if (tokens.length < 4) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop command requires at least three arguments");
    } else if (tokens.length > 5) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop command does not require more than 4 arguments");
    }
    // --- Process variable "i"
    Variable var = null;
    if (commandProcessor.isHasVariable(tokens[1])) {
      var = commandProcessor.getVariable(tokens[1]);
    } else {
      var = new Variable(tokens[1], null);
      commandProcessor.addVariable(var);
    }

    // --- Now decide what is a type of a "start" variable and add an assignment command
    if (commandProcessor.isHasVariable(tokens[2])) {
      Variable var2 = commandProcessor.getVariable(tokens[2]);
      AtomicCommand atomicCommand = new AtomicCommand(new AssignmentOperator(), new Object[]{var, var2});
      commandProcessor.addCompiledCommand(atomicCommand);
    } else { // It's not an existing variable
      Number num = CommandProcessor.getNumber(tokens[2]);
      AtomicCommand atomicCommand = new AtomicCommand(new AssignmentOperator(), new Object[]{var, num});
      commandProcessor.addCompiledCommand(atomicCommand);
    }

    // --- Now decide what is a type of a "stop" variable
    Object stop;
    if (commandProcessor.isHasVariable(tokens[3])) {
      stop = commandProcessor.getVariable(tokens[3]);
    } else { // It's not an existing variable
      Number num = CommandProcessor.getNumber(tokens[3]);
      stop = num;
    }

    Object[] compiledArgs = null;
    AtomicCommand atomicCommand = null;
    if (tokens.length == 4) {
      compiledArgs = new Object[]{var, stop, new Integer(1), null};
      atomicCommand = new AtomicCommand(this, compiledArgs); // reserve last value to be filled in by endfor
    } else if (tokens.length == 5) {
      // --- Now decide what is a type of a "step" variable
      Object step;
      if (commandProcessor.isHasVariable(tokens[4])) {
        step = commandProcessor.getVariable(tokens[4]);
      } else { // It's not an existing variable
        Number num = CommandProcessor.getNumber(tokens[4]);
        step = num;
      }
      compiledArgs = new Object[]{var, stop, step, null};
      atomicCommand = new AtomicCommand(this, compiledArgs); // reserve last value to be filled in by endfor
    }
    // ---

    //compiledCommands.add(atomicCommand);
    commandProcessor.addForLoopForProcessing(commandProcessor.numberOfCompiledCommands()); // i.e. adds at the top of the list

    return compiledArgs;
  }

  public int getNewCommandCounter() {
    return switchToCommand;
  }

  public void setCommandProcessor(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }
}
