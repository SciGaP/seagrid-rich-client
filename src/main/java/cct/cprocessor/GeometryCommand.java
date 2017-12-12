/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import static cct.cprocessor.MoleculeCommand.supportedCommands;
import cct.vecmath.Point3f;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class GeometryCommand implements CommandInterface {

  public static final String DISTANCE_COMMAND = "distance";
  public static final String ANGLE_COMMAND = "angle";
  public static final String DIHEDRAL_COMMAND = "dihedral";
  private static Set<String> supportedCommands = new HashSet<String>();

  static {
    supportedCommands.add(DISTANCE_COMMAND);
    supportedCommands.add(ANGLE_COMMAND);
    supportedCommands.add(DIHEDRAL_COMMAND);
  }

  public CommandInterface ciInstance() {
    return new GeometryCommand();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args[0].equals(DISTANCE_COMMAND)) {
      Point3f p1 = getPoint3f(args[1]);
      Point3f p2 = getPoint3f(args[2]);
      return Point3f.distance(p1, p2);
    } else if (args[0].equals(ANGLE_COMMAND)) {
      Point3f p1 = getPoint3f(args[1]);
      Point3f p2 = getPoint3f(args[2]);
      Point3f p3 = getPoint3f(args[3]);
      return Point3f.RADIANS_TO_DEGREES * Point3f.angleBetween(p1, p2, p3);
    } else if (args[0].equals(DIHEDRAL_COMMAND)) {
      Point3f p1 = getPoint3f(args[1]);
      Point3f p2 = getPoint3f(args[2]);
      Point3f p3 = getPoint3f(args[3]);
      Point3f p4 = getPoint3f(args[4]);
      return Point3f.RADIANS_TO_DEGREES * Point3f.dihedralAngle(p1, p2, p3, p4);
    }
    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (!supportedCommands.contains(tokens[1])) {
      throw new Exception(this.getClass().getCanonicalName() + ": Unknown command: " + tokens[1]);
    }
    // ---

    if (tokens[1].equals(DISTANCE_COMMAND)) { // Geometry distance Point3f_object Point3f_object
      if (tokens.length != 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + DISTANCE_COMMAND + " command requires three arguments");
      }
      return new Object[]{tokens[1], tokens[2], tokens[3]};
      // -----
    } else if (tokens[1].equals(ANGLE_COMMAND)) { // Geometry angle Point3f_object Point3f_object Point3f_object
      if (tokens.length != 5) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + ANGLE_COMMAND + " command requires three arguments");
      }
      return new Object[]{tokens[1], tokens[2], tokens[3], tokens[4]};
    } // ----
    else if (tokens[1].equals(DIHEDRAL_COMMAND)) { // Geometry angle Point3f_object Point3f_object Point3f_object Point3f_object
      if (tokens.length != 6) {
        throw new Exception(this.getClass().getCanonicalName() + ": " + DIHEDRAL_COMMAND + " command requires three arguments");
      }
      return new Object[]{tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]};
    }
    throw new Exception(this.getClass().getCanonicalName() + ": was unable to parse a command");
  }

  public Point3f getPoint3f(Object obj) throws Exception {
    if (!(obj instanceof Variable)) {
      throw new Exception(this.getClass().getCanonicalName() + ": expected a variable, got "
          + obj.getClass().getCanonicalName());
    }
    Variable var = (Variable) obj;
    if (var.getValue() instanceof Point3f) {
      return (Point3f) var.getValue();
    }
    throw new Exception(this.getClass().getCanonicalName() + ": expected a variable of type Point3f, got "
        + var.getValue().getClass().getCanonicalName());
  }
}
