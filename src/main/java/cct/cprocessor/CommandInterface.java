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
public interface CommandInterface {

  CommandInterface ciInstance();

  Object ciExecuteCommand(Object[] args) throws Exception;

  /**
   * Parses command parameters.
   *
   * @param tokens - arguments. The first argument is a command name, so it's ignored
   * @return parsed and parameters
   * @throws Exception
   */
  Object[] ciParseCommand(String[] tokens) throws Exception;
}
