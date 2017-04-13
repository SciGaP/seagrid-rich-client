/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class ListCommand extends ArrayList implements CommandInterface {

  static public final String ADD_COMMAND = "add";
  static public final String GET_COMMAND = "get";
  static public final String SIZE_COMMAND = "size";
  static public final String TOARRAY_COMMAND = "toArray";
  static private Set<String> listCommands = new HashSet<String>();

  static {
    listCommands.add(ADD_COMMAND);
    listCommands.add(GET_COMMAND);
    listCommands.add(SIZE_COMMAND);
    listCommands.add(TOARRAY_COMMAND);
  }

  public CommandInterface ciInstance() {
    return new ListCommand();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args[0].equals(ADD_COMMAND)) { // add item
      Object obj = args[1];
      if (obj instanceof Variable) {
        obj = ((Variable) args[1]).getValue();
      }
      add(obj);
      return obj;
    } else if (args[0].equals(GET_COMMAND)) { // get item_number
      Object obj = args[1];
      if (CommandProcessor.isIntegerNumber(obj)) {
        int n = CommandProcessor.getIntValue(obj);
        return get(n);
      }
      if (obj instanceof Variable) {
        obj = ((Variable) args[1]).getValue();
        if (CommandProcessor.isIntegerNumber(obj)) {
          int n = CommandProcessor.getIntValue(obj);
          return get(n);
        }
      }
      throw new Exception(this.getClass().getCanonicalName() + ": executing " + ADD_COMMAND
          + ": expected integer argument. Got " + obj.getClass().getCanonicalName());
    } else if (args[0].equals(TOARRAY_COMMAND)) { // toArray
      if (size() == 0) {
        System.out.println(this.getClass().getCanonicalName() + ": " + TOARRAY_COMMAND + ": size of array = 0");
        return null;

      }
      Object item = get(0);
      if (item instanceof Double || item instanceof Float || CommandProcessor.isIntegerNumber(item)) {
        double[] d = new double[size()];
        for (int i = 0; i < size(); i++) {
          d[i] = ((Number) get(i)).doubleValue();
        }
        return d;
      } else {
        return this.toArray();
      }
    }
    return null;
  }

  /**
   * Parses command parameters.
   *
   * @param tokens - arguments. The first argument is a command name, so it's ignored
   * @return parsed and parameters
   * @throws Exception
   */
  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length <= 1) {
      return null; // Initialization
    }
    // ---
    if (!listCommands.contains(tokens[1])) {
      throw new Exception(this.getClass().getCanonicalName() + ": Uknown command: " + tokens[1]);
    }
    if (tokens[1].equals(ADD_COMMAND)) { // add item
      if (tokens.length != 3) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + ADD_COMMAND + " command requires one argument");
      }
      return new Object[]{ADD_COMMAND, tokens[2]};
    } else if (tokens[1].equals(GET_COMMAND)) { // get item_number
      if (tokens.length != 3) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + GET_COMMAND + " command requires one argument");
      }
      if (CommandProcessor.isIntegerNumber(tokens[2])) {
        return new Object[]{GET_COMMAND, Integer.parseInt(tokens[2])};
      } else {
        return new Object[]{GET_COMMAND, tokens[2]};
      }
    } else if (tokens[1].equals(TOARRAY_COMMAND)) { // toArray
      if (tokens.length != 2) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + TOARRAY_COMMAND
            + " command does not require argument(s)");
      }
      return new Object[]{TOARRAY_COMMAND};
    }
    return null;
  }
}
