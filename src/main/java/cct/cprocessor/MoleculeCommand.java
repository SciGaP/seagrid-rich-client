/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vvv900
 */
public class MoleculeCommand extends Molecule implements CommandInterface {

  public static final Set<String> supportedCommands = new HashSet<String>();
  boolean debug = false;

  static {
    supportedCommands.add("setXYZ");
    supportedCommands.add("centroid");
    supportedCommands.add("getAtom");
  }

  public CommandInterface ciInstance() {
    return new MoleculeCommand();
  }

  public Object ciExecuteCommand(Object[] args) throws Exception {
    if (args == null || args.length == 0) {
      return this; // Returning coomand as object
    }
    // --- Execute "set" command
    if (args[0].equals("setXYZ")) {
      MoleculeInterface mol = this.getMolecule(args[1]);
      float[][] coords = null;

      // ---
      if (args[2] instanceof float[][]) {
        coords = (float[][]) args[2];
      } else if (args[2] instanceof Variable) {
        Object obj = ((Variable) args[2]).getValue();
        if (!(obj instanceof float[][])) {
          throw new Exception(this.getClass().getCanonicalName()
              + ": Wrong argument variable type for setXYZ command. Should be float[][], got"
              + obj.getClass().getCanonicalName());
        }
        coords = (float[][]) obj;
      } else {
        throw new Exception(this.getClass().getCanonicalName()
            + ": Wrong argument type for setXYZ command. Should be float[][], got" + args[2].getClass().getCanonicalName());
      }
      if (debug) {
        System.out.println(this.getClass().getSimpleName() + ": Extracted Coordinates from parameters:");
      }
      for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
        AtomInterface atom = mol.getAtomInterface(i);
        atom.setXYZ(coords[i][0], coords[i][1], coords[i][2]);
        if (debug) {
          System.out.printf("%5d %8.2f %8.2f %8.2f\n", i + 1, coords[i][0], coords[i][1], coords[i][2]);
        }
        //System.out.printf("%5d %8.2f %8.2f %8.2f\n",i+1,atom.getX(),atom.getY(),atom.getZ());
      }
    }// --- 
    // --- Centroid command
    else if (args[0].equals("centroid")) {
      MoleculeInterface mol = this.getMolecule(args[1]);
      if (args.length == 2) {
        return Molecule.getCentroid(mol);
      }
      Set<AtomInterface> list = Molecule.getAtomListUsingAtomMaskSelection(mol, args[2].toString());
      System.out.println("List: " + args[2].toString() + " Size: " + list.size());
      return Molecule.getCentroid(list);
    }// --- 
    // --- getAtom command
    else if (args[0].equals("getAtom")) {
      MoleculeInterface mol = this.getMolecule(args[1]);
      int n = this.getIntValue(args[2]);
      return mol.getAtomInterface(n - 1);
    }

    return null;
  }

  public Object[] ciParseCommand(String[] tokens) throws Exception {
    if (tokens == null || tokens.length <= 1) {
      return null; // Initialization
    }
    if (!supportedCommands.contains(tokens[1])) {
      throw new Exception(this.getClass().getCanonicalName() + ": Unknown command: " + tokens[1]);
    }
    // ---
    if (tokens[1].equals("setXYZ")) { // Molecule setXYZ Molecule_object float[][]_object
      if (tokens.length < 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": setXYZ command requires three arguments");
      } else if (tokens.length > 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": setXYZ command requires three arguments");
      }
      return new Object[]{tokens[1], tokens[2], tokens[3]};
    } // --- 
    // --- Centroid command
    else if (tokens[1].equals("centroid")) { // Molecule centroid Molecule_object [atom(s) list]
      if (tokens.length < 3) {
        throw new Exception(this.getClass().getCanonicalName() + ": centroid command requires at least one argument");
      }
      if (tokens.length == 3) {
        return new Object[]{tokens[1], tokens[2]};
      }
      String line = "";
      for (int i = 3; i < tokens.length; i++) {
        line += tokens[i] + " ";
      }
      return new Object[]{tokens[1], tokens[2], line};
    }// --- 
    // --- getAtom command
    else if (tokens[1].equals("getAtom")) { // Molecule getAtom Molecule_object Number_value]
      if (tokens.length != 4) {
        throw new Exception(this.getClass().getCanonicalName() + ": getAtom command requires two arguments");
      }

      Integer atomNumber;
      try {
        atomNumber = Integer.parseInt(tokens[3]);
        return new Object[]{tokens[1], tokens[2], atomNumber};
      } catch (Exception ex) {
        // Do nothing. Probably is's a variable
      }
      return new Object[]{tokens[1], tokens[2], tokens[3]};
    }

    return null;
  }

  public MoleculeInterface getMolecule(Object obj) throws Exception {
    if (obj instanceof MoleculeInterface) {
      return (MoleculeInterface) obj;
    } else if (obj instanceof Variable) {
      Object value = ((Variable) obj).getValue();
      if (!(value instanceof MoleculeInterface)) {
        throw new Exception(this.getClass().getCanonicalName()
            + ": Wrong argument variable type. Should be Molecule, got"
            + value.getClass().getCanonicalName());
      }
      return (MoleculeInterface) value;
    } else {
      throw new Exception(this.getClass().getCanonicalName()
          + ": Wrong argument type. Should be Molecule, got" + obj.getClass().getCanonicalName());
    }
  }

  public int getIntValue(Object obj) throws Exception {
    if (obj instanceof Variable) {
      return ((Variable) obj).getIntValue();
    }
    if (obj instanceof Number) {
      return ((Number) obj).intValue();
    }
    throw new Exception(this.getClass().getCanonicalName() + " argument " + obj.toString() + " cannot be converted into integer value");
  }
}
