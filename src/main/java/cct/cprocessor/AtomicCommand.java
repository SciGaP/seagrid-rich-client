/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

/**
 *
 * @author vvv900
 */
public class AtomicCommand {

  private CommandInterface command;
  private Object[] parsedArguments;
  private boolean[] setPrevResult;
  private boolean needPreviousResult = false;

  public AtomicCommand(CommandInterface command, Object[] parsedArguments) {
    this.command = command;
    this.parsedArguments = parsedArguments;
    if (parsedArguments != null) {
      setPrevResult = new boolean[parsedArguments.length];
      int i = 0;
      for (Object obj : parsedArguments) {
        if (obj instanceof CommandProcessor.CONSTANTS
            && obj == CommandProcessor.CONSTANTS.PREVIOUS_RESULT) {
          needPreviousResult = true;
          setPrevResult[i] = true;
        } else {
          setPrevResult[i] = false;
        }
        ++i;
      }
    }
  }

  public Object executeCommand() throws Exception {
    return command.ciExecuteCommand(parsedArguments);
  }

  public Object[] getParsedArguments() {
    return parsedArguments;
  }

  public boolean isNeedPreviousResult() {
    return needPreviousResult;
  }

  public void setPreviousResult(Object result) {
    for (int i = 0; i < parsedArguments.length; i++) {
      if (setPrevResult[i]) {
        parsedArguments[i] = result;
      }
    }
  }

  public CommandInterface getCommand() {
    return command;
  }
}
