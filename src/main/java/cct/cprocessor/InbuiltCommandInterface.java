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
public interface InbuiltCommandInterface extends CommandInterface {

  int getNewCommandCounter();

  void setCommandProcessor(CommandProcessor commandProcessor);
}
