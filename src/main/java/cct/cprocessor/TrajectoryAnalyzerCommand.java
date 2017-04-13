/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import cct.dynamics.SnapshotSequenceAnalyzer;
import cct.interfaces.MoleculeInterface;
import cct.modelling.StructureManager;
import cct.modelling.TRAJECTORY_FILE_FORMAT;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author vvv900
 */
public class TrajectoryAnalyzerCommand extends SnapshotSequenceAnalyzer implements CommandInterface {

  static final Logger logger = Logger.getLogger(TrajectoryAnalyzerCommand.class.getCanonicalName());
  static private Map<String, TRAJECTORY_FILE_FORMAT> trajFormats = new HashMap<String, TRAJECTORY_FILE_FORMAT>();
  static private Set<String> trajCommands = new HashSet<String>();
  boolean debug = false;

  static {
    trajFormats.put("g09", TRAJECTORY_FILE_FORMAT.G03_TRAJECTORY);
    // ---
    trajCommands.add("set");
    trajCommands.add("load");
    trajCommands.add("getXYZ");
    trajCommands.add("getNumSnaps");
  }

  public TrajectoryAnalyzerCommand() {
    super(new StructureManager());
  }

  public CommandInterface ciInstance() {
    return new TrajectoryAnalyzerCommand();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args == null || args.length == 0) {
      return this; // Returning coomand as object
    }
    // --- Execute "set" command
    if (args[0].equals("set")) {
      if (args[1] instanceof MoleculeInterface) {
        this.setReferenceMolecule((MoleculeInterface) args[1]);
      } else if (args[1] instanceof Variable) {
        Object obj = ((Variable) args[1]).getValue();
        if (!(obj instanceof MoleculeInterface)) {
          throw new Exception(this.getClass().getCanonicalName()
              + ": Wrong argument variable type for set command. Should be Molecule, got"
              + obj.getClass().getCanonicalName());
        }
        this.setReferenceMolecule((MoleculeInterface) obj);
      } else {
        throw new Exception(this.getClass().getCanonicalName()
            + ": Wrong argument type for set command. Should be Molecule, got" + args[1].getClass().getCanonicalName());
      }
    } // --- Execute "load" command
    else if (args[0].equals("load")) {
      this.getStructureManager().parseTrajectoryFile(args[1].toString(), (TRAJECTORY_FILE_FORMAT) args[2]);
    } // --- Execute "getXYZ" command
    else if (args[0].equals("getXYZ")) {
      Integer n = null;
      if (args[1] instanceof Variable) {
        n = (Integer) ((Variable) args[1]).getValue();
      } else {
        n = (Integer) args[1];
      }
      float[][] coords = new float[this.getReferenceMolecule().getNumberOfAtoms()][3];
      this.getStructureManager().getStructure(n, coords);
      if (debug) {
        System.out.println(this.getClass().getSimpleName() + ": Extracted coords from the trajectory");
        for (int i = 0; i < coords.length; i++) {
          System.out.printf("%5d %8.2f %8.2f %8.2f\n", i + 1, coords[i][0], coords[i][1], coords[i][2]);
        }
      }
      return coords;
    } else if (args[0].equals("getNumSnaps")) {
      return getStructureManager().getNumberOfSnapshots();
    } else {
      throw new Exception(this.getClass().getCanonicalName() + ": Unknown command to execute: " + args[0].toString());
    }
    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length <= 1) {
      return null; // Initialization
    }
    if (!trajCommands.contains(tokens[1])) {
      throw new Exception(this.getClass().getCanonicalName() + ": Uknown command: " + tokens[1]);
    }
    // --- "set" command
    if (tokens[1].equals("set")) { // set Molecule_object
      if (tokens.length == 1) {
        throw new Exception(this.getClass().getCanonicalName() + ": set command requires an argument");
      } else if (tokens.length > 3) {
        throw new Exception(this.getClass().getCanonicalName() + ": set command requires only one argument");
      }
      return new Object[]{tokens[1], tokens[2]};

    } // --- Load command: "load tajectory-file format", where format = g09
    else if (tokens[1].equals("load")) {
      if (tokens.length < 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": Load command requies 2 arguments");
      }
      if (!trajFormats.containsKey(tokens[3])) {
        throw new Exception(this.getClass().getCanonicalName() + ": Unknown trajectory format: " + tokens[3]);
      }
      return new Object[]{tokens[1], tokens[2], trajFormats.get(tokens[3])};
    } // --- Load command: "getXYZ number", where number - integer number of strcuture in a list (zero-based)
    else if (tokens[1].equals("getXYZ")) {
      if (tokens.length < 3) {
        throw new Exception(this.getClass().getCanonicalName() + ": getXYZ command requies 1 argument");
      }
      Number n;
      Integer num;
      try {
        n = CommandProcessor.getNumber(tokens[2]);
        num = n.intValue();
        return new Object[]{tokens[1], num};
      } catch (Exception ex) {
      }
      return new Object[]{tokens[1], tokens[2]}; // Probably it's variable
    } else if (tokens[1].equals("getNumSnaps")) {
      if (tokens.length > 2) {
        throw new Exception(this.getClass().getCanonicalName() + ": getNumSnaps command requies no argument(s)");
      }
      return new Object[]{tokens[1]};
    } else {
      throw new Exception(this.getClass().getCanonicalName() + ": Unknown command: " + tokens[1]);
    }
    //return null;
  }

}
