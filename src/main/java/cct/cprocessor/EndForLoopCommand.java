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
public class EndForLoopCommand implements InbuiltCommandInterface {

  private int switchToCommand = -1;
  private CommandProcessor commandProcessor;

  public EndForLoopCommand() {
  }

  public EndForLoopCommand(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public CommandInterface ciInstance() {
    return new EndForLoopCommand(commandProcessor);
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    int forStart = (Integer) args[0];
    AtomicCommand atomicCommand = commandProcessor.getCompiledCommand(forStart); // Get for loop parameters

    Object[] forArgs = atomicCommand.getParsedArguments();
    Variable controlLoopVar = (Variable) forArgs[0];
    Number clv = (Number) controlLoopVar.getValue();
    if (controlLoopVar.getValue() instanceof Double || controlLoopVar.getValue() instanceof Float) {
      Double i = ((Number) controlLoopVar.getValue()).doubleValue();
      Double step = 1.0;
      //if (forArgs[2] != null) {
      step = CommandProcessor.getDoubleValue(forArgs[2]);
      //}
      double newi = i.doubleValue() + step.doubleValue();
      if (clv instanceof Double) {
        controlLoopVar.setValue(new Double(newi));
      } else {
        controlLoopVar.setValue(new Float(newi));
      }
    } else {
      Integer i = ((Number) controlLoopVar.getValue()).intValue();
      Integer step = 1;
      //if (forArgs[2] != null) {
      step = CommandProcessor.getIntValue(forArgs[2]);
      //}
      int newi = i.intValue() + step.intValue();
      controlLoopVar.setValue(new Integer(newi));
    }

    switchToCommand = forStart;
    return null;
  }

  public int getNewCommandCounter() {
    return switchToCommand;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length == 0) {
      throw new Exception(this.getClass().getCanonicalName() + ": for loop command: parameters are not set");
    }

    if (!tokens[0].equals("endfor")) {
      throw new Exception(this.getClass().getCanonicalName()
          + ": expected endfor loop command, got: " + tokens[0]);
    }
    // for i start stop [step]
    if (tokens.length > 1) {
      throw new Exception(this.getClass().getCanonicalName() + ": endfor loop command does not require any arguments");
    }

    if (commandProcessor.numberForLoopsInProcessing() == 0) {
      throw new Exception(this.getClass().getCanonicalName() + ": endfor loop command: matching \"for\" loop operator is missing");
    }
    // --- at the endfor we need to make increment of loop control variable ("i") using a "step" variable
    Integer forStart = commandProcessor.extractForLoopStart();
    AtomicCommand forCommand = commandProcessor.getCompiledCommand(forStart); // Get for loop parameters

    AtomicCommand endforCommand = new AtomicCommand(this, new Object[]{forStart});
    commandProcessor.addCompiledCommand(endforCommand);

    // --- Update the last argument for for command
    Object[] args = forCommand.getParsedArguments();
    args[args.length - 1]
        = new Integer(commandProcessor.numberOfCompiledCommands()); // i.e. point to the next command after endfor

    commandProcessor.addCompiledCommand(new AtomicCommand(new EmptyCommand(), null));

    return null;
  }

  public void setCommandProcessor(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

}
