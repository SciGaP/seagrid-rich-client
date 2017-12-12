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
public class EmptyCommand implements CommandInterface {

  public CommandInterface ciInstance() {
    return new EmptyCommand();
  }
  
  public Object ciExecuteCommand(Object[] args) throws Exception {
    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    return null;
  }
}
