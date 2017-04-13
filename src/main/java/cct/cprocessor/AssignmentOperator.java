/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import java.util.logging.Logger;

/**
 *
 * @author vvv900
 */
public class AssignmentOperator implements CommandInterface {

  static final Logger logger = Logger.getLogger(AssignmentOperator.class.getCanonicalName());

  public CommandInterface ciInstance() {
    return new AssignmentOperator();
  }

  /**
   * Assumes variable as the first argument and its value as the second one
   *
   * @param args
   * @return
   * @throws Exception
   */
  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args == null || args.length != 2) {
      throw new Exception("Assignment operator expects for two arguments");
    }
    if (!(args[0] instanceof Variable)) {
      throw new Exception("Assignment operator: first argument should be of type "
          + Variable.class.getCanonicalName() + " Got: " + args[0].getClass().getCanonicalName());
    }
    Variable var = (Variable) args[0];
    var.setValue(args[1]);
    return new Boolean(true);
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    throw new Exception("Not implemented yet");
  }
}
