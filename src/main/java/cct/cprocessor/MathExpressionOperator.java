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
public class MathExpressionOperator implements InbuiltCommandInterface {

  private CommandProcessor commandProcessor;

  public MathExpressionOperator() {
  }

  public MathExpressionOperator(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }

  public CommandInterface ciInstance() {
    return new MathExpressionOperator(commandProcessor);
  }

  /**
   *
   * @param args - A singe argument - string with expression
   * @return - double value (result of evaluation of expression)
   * @throws Exception
   */
  public Object ciExecuteCommand(Object[] args) throws Exception {
    return commandProcessor.evaluateMathExpression(args[0].toString());
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    return null;
  }

  public int getNewCommandCounter() {
    return -1;
  }

  public void setCommandProcessor(CommandProcessor commandProcessor) {
    this.commandProcessor = commandProcessor;
  }
}
