/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import cct.GlobalSettings;
import cct.interfaces.MoleculeInterface;
import cct.j3d.Java3dUniverse;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class GUIWrapperCommand implements CommandInterface {

  public static final Set<String> supportedCommands = new HashSet<String>();

  static {
    supportedCommands.add("show");
  }
  private String command;
  private Java3dUniverse renderer;

  public GUIWrapperCommand(Java3dUniverse renderer) {
    this.renderer = renderer;
  }

  public CommandInterface ciInstance() {
    return new GUIWrapperCommand(renderer);
  }

  static public String[] supportedCommands() {
    return supportedCommands.toArray(new String[supportedCommands.size()]);
  }

  public Java3dUniverse getRenderer() {
    return renderer;
  }

  public void setRenderer(Java3dUniverse renderer) {
    this.renderer = renderer;
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (renderer == null) {
      throw new Exception("3d Renderer is not set");
    }
    // 
    MoleculeInterface m = null;
    if (args[0] instanceof Variable) {
      Object obj = ((Variable) args[0]).getValue();
      if (!(obj instanceof MoleculeInterface)) {
        throw new Exception("Execute Show molecule command: variable as the first argument should of type Molecule. Got "
            + obj.getClass().getCanonicalName());
      }
      m = (MoleculeInterface) obj;
    } else if (!(args[0] instanceof MoleculeInterface)) {
      throw new Exception("Execute Show molecule command: the first argument should of type Molecule. Got "
          + args[0].getClass().getCanonicalName());
    } else {
      m = (MoleculeInterface) args[0];
    }

    GlobalSettings.ADD_MOLECULE_MODE mode = GlobalSettings.ADD_MOLECULE_MODE.SET;

    if (args.length > 1 && (!(args[1] instanceof GlobalSettings.ADD_MOLECULE_MODE))) {
      throw new Exception("Execute Show molecule command: the first argument should of type "
          + GlobalSettings.ADD_MOLECULE_MODE.class.getCanonicalName() + " Got "
          + args[1].getClass().getCanonicalName());
    }

    if (args.length > 1) {
      mode = (GlobalSettings.ADD_MOLECULE_MODE) args[1];
    }

    renderer.setMolecule(m, mode);
    return null;
  }

  /**
   * Wrapper for GUI related commands. Cuurently available ones are: show Molecule_object - shows molecule in 3r
   * renderer (if it's set)
   *
   * @param tokens - arguments. The first argument is a command name, so it's ignored
   * @return parsed and parameters
   * @throws Exception
   */
  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (!supportedCommands.contains(tokens[0])) {
      throw new Exception("No such GUI related command: " + tokens[0]);
    }
    if (tokens.length < 2) {
      throw new Exception("Show molecule command requires at least one argument (molecule object)");
    }
    Object[] parsedArgs = new Object[tokens.length > 2 ? 2 : 1];
    parsedArgs[0] = tokens[1];

    if (tokens.length > 2) {
      if (tokens[2].endsWith("a") || tokens[2].equals("append")) {
        parsedArgs[1] = GlobalSettings.ADD_MOLECULE_MODE.APPEND;
      } else if (tokens[2].endsWith("s") || tokens[2].equals("set")) {
        parsedArgs[1] = GlobalSettings.ADD_MOLECULE_MODE.SET;
      } else {
        throw new Exception("Show molecule command: unknown second argument: " + tokens[2]
            + " Expected: a, append or s, set");
      }
    }

    return parsedArgs;
  }

}
