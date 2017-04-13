/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import static cct.cprocessor.MoleculeCommand.supportedCommands;
import cct.math.Statistics;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class StatisticsCommand implements CommandInterface {

  public static final String LinearCorrelationCoefficientCommand = "linearCorrelationCoefficient";
  public static final Set<String> supportedCommands = new HashSet<String>();

  static {
    supportedCommands.add(LinearCorrelationCoefficientCommand);
  }

  public CommandInterface ciInstance() {
    return new StatisticsCommand();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args == null || args.length == 0) {
      return this; // Returning coomand as object
    }
    // ---
    if (args[0].equals(LinearCorrelationCoefficientCommand)) {
      Object obj1 = args[1];
      if (obj1 == null) {
        throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
            + ": the first argument is null. Expecting data array");
      }
      if (obj1 instanceof Variable) {
        Variable var = (Variable) obj1;
        obj1 = var.getValue();
        if (obj1 == null) {
          throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
              + ": a variable " + var.getName() + " as the first argument is null. Expecting data array");
        }
      }

      if (!obj1.getClass().isArray()) {
        throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
            + ": Expecting data array as athe first arg. Got: " + obj1.getClass().getCanonicalName());
      }
      // -- The second argument
      Object obj2 = args[2];
      if (obj2 == null) {
        throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
            + ": the second argument is null. Expecting data array");
      }
      if (obj2 instanceof Variable) {
        Variable var = (Variable) obj2;
        obj2 = var.getValue();
        if (obj2 == null) {
          throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
              + ": a variable " + var.getName() + " as the second argument is null. Expecting data array");
        }
      }

      if (!obj2.getClass().isArray()) {
        throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
            + ": Expecting data array as athe first arg. Got: " + obj1.getClass().getCanonicalName());
      }
      // --- CAlculating a result

      if (obj1 instanceof double[] && obj2 instanceof double[]) {
        return Statistics.linearCorrelationCoefficient((double[]) obj1, (double[]) obj2);
      } else if (obj1 instanceof float[] && obj1 instanceof float[]) {
        return Statistics.linearCorrelationCoefficient((float[]) obj1, (float[]) obj2);
      } else if (obj1 instanceof Number[] && obj1 instanceof Number[]) {
        return Statistics.linearCorrelationCoefficient((Number[]) obj1, (Number[]) obj2);
      } else {
        throw new Exception(this.getClass().getCanonicalName() + ": Executing " + LinearCorrelationCoefficientCommand
            + ": Expecting double, float or NUmber data arrays. Got: "
            + obj1.getClass().getCanonicalName() + " and " + obj2.getClass().getCanonicalName());
      }
    }
    throw new Exception(this.getClass().getCanonicalName() + " Something is wrong with execution");
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
    if (!supportedCommands.contains(tokens[1])) {
      throw new Exception(this.getClass().getCanonicalName() + ": Unknown command: " + tokens[1]);
    }
    // ---
    if (tokens[1].equals(LinearCorrelationCoefficientCommand)) { // Stat linearCorrelationCoefficient vector_1 vector_2
      if (tokens.length < 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + LinearCorrelationCoefficientCommand
            + " command requires two arguments");
      } else if (tokens.length > 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + LinearCorrelationCoefficientCommand
            + " command requires two arguments");
      }
      return new Object[]{LinearCorrelationCoefficientCommand, tokens[2], tokens[3]};
    }
    return null;
  }

}
