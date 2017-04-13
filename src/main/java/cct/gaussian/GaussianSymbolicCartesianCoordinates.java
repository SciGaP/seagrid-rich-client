/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.gaussian;

import cct.Constants;
import cct.interfaces.AtomInterface;
import cct.interfaces.CoordinateBuilderInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Vlad
 */
public class GaussianSymbolicCartesianCoordinates implements CoordinateBuilderInterface {

  public void getCoordinates(MoleculeInterface mol, boolean inAngstroms, Writer writer) throws Exception {
    if (mol == null || mol.getNumberOfAtoms() < 1) {
      throw new Exception("Molecule has no atoms");
    }
    float factor = 1.0f;
    if (!inAngstroms) {
      factor /= Constants.ONE_BOHR_FLOAT;
    }

    String x, y, z;
    Map<String, Float> variables = new LinkedHashMap<String, Float>();
    for (int i = 0, j = 1; i < mol.getNumberOfAtoms(); i++, j++) {
      AtomInterface atom = mol.getAtomInterface(i);
      int element = atom.getAtomicNumber();
      String symbol = ChemicalElements.getElementSymbol(element);
      if (symbol == null) {
        symbol = " X";
      }
      x = String.format("x%03d", j);
      y = String.format("y%03d", j);
      z = String.format("z%03d", j);
      variables.put(x, atom.getX() * factor);
      variables.put(y, atom.getY() * factor);
      variables.put(z, atom.getZ() * factor);

      writer.write(String.format("%-2s 0 %s %s %s\n", symbol, x, y, z));
    }
    // ---
    writer.write("  Variables:\n");
    for (Entry<String, Float> c : variables.entrySet()) {
      writer.write(String.format("%s %8.4f\n", c.getKey(), c.getValue()));
    }
    writer.write("\n");
  }

  public String getCoordinatesAsString(MoleculeInterface molec, boolean inAngstroms) throws Exception {
    StringWriter sw = new StringWriter();
    getCoordinates(molec, inAngstroms, sw);
    return sw.toString();
  }
}
