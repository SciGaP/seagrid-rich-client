/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo (former JMolEditor) - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/


package cct.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class GeomFormat {

  private static String lastPWDKey = "lastPWD";

  float[] transformMatrix = null;
  MoleculeInterface molecule = null;

  float defaultAtomicRadius = 0.05f;
  private static float defaulCylinerRadius = 0.01f;

  Preferences prefs = Preferences.userNodeForPackage(getClass());
  File currentWorkingDirectory = null;
  JFileChooser chooser = null;
  FileFilterImpl filter = null;

  public GeomFormat() {
  }

  public void settransformMatrix(float[] matrix) {
    transformMatrix = new float[16];
    for (int i = 0; i < 16; i++) {
      transformMatrix[i] = matrix[i];
    }
  }

  public void setMolecule(MoleculeInterface mol) {
    molecule = mol;
  }

  public static void main(String[] args) {
    GeomFormat geomformat = new GeomFormat();
  }

  public void saveGeomFormatFile() throws Exception {

    if (molecule == null) {
      throw new Exception("saveGeomFormatFile: ERROR: molecule is not set");
    }

    if (chooser == null) {
      chooser = new JFileChooser();
      filter = new FileFilterImpl();

      String temp[] = {
          "gf", "GF"}; // extensions.split(";");
      for (int i = 0; i < temp.length; i++) {
        filter.addExtension(temp[i]);
      }
      filter.setDescription("Geom Format Files (*.gf)");
      chooser.setFileFilter(filter);
    }

    chooser.setDialogTitle("Save as Geom Format File");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);

    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    }
    else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory() &&
            currentWorkingDirectory.exists()) {
          chooser.setCurrentDirectory(currentWorkingDirectory);
        }
      }
    }

    int returnVal = JFileChooser.CANCEL_OPTION;

    returnVal = chooser.showSaveDialog(null);

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String fileName = chooser.getSelectedFile().getPath();
      if (fileName.endsWith(".")) {
        fileName += "gf";
      }
      else if (!fileName.endsWith(".gf") && !fileName.endsWith(".GF")) {
        fileName += ".gf";
      }
      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      }
      catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage() +
                           " Ignored...");
      }
      //logger.info("You chose to open this file: " +
      //                   fileName);

      saveGeomFormatFile(fileName);
    }

  }

  public void saveGeomFormatFile(String filename) throws Exception {
    FileOutputStream out;
    try {
      out = new FileOutputStream(filename);

      out.write("# --- File is generated by Jamberoo. Contact: vvv900@gmail.com\n".getBytes());
      out.write("#\n".getBytes());

      // --- Write atom spheres

      out.write("\n# --- Atomic spheres\n#\n".getBytes());

      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface atom = molecule.getAtomInterface(i);

        Object obj = atom.getProperty(AtomInterface.VISIBLE);
        if (obj != null && obj instanceof Boolean) {
          if (! (Boolean) obj) {
            continue;
          }
        }

        out.write("#\n".getBytes());

        String monomerName = molecule.getMonomerInterface(atom.getSubstructureNumber()).getName();

        try {
          out.write( ("# Atom " + (i + 1) + " " + atom.getName() + "(" + ChemicalElements.getElementName(atom.getAtomicNumber()) +
                      ") " + monomerName + "-" + (atom.getSubstructureNumber() + 1) + "\n").getBytes());
        }
        catch (Exception ex) {}

        // --- Get sphere radius

        obj = atom.getProperty(AtomInterface.GR_RADIUS);
        Float radius = defaultAtomicRadius;
        if (obj != null) {
          if (obj instanceof Float) {
            radius = (Float) obj;
          }
          else if (obj instanceof Double) {
            radius = ( (Double) obj).floatValue();
          }
        }

        // --- get sphere color
        float red = 0.5f, green = 0.5f, blue = 0.5f;
        obj = atom.getProperty(AtomInterface.RGB_COLOR);
        if (obj != null) {
          if (obj instanceof Integer[]) {
            red = (float) ( (Integer[]) obj)[0] / 255.0f;
            green = (float) ( (Integer[]) obj)[1] / 255.0f;
            blue = (float) ( (Integer[]) obj)[2] / 255.0f;
          }
        }

        out.write( ("s " + atom.getX() + " " + atom.getY() + " " + atom.getZ() + " " + radius + " " + red + " " + green + " " +
                    blue + "\n").getBytes());
      }
      out.write("#\n".getBytes());

      // --- Writing bonds
      //cylinder { <  -5.244,    2.407,  -18.493>, <  -4.837,    2.627,  -18.216>,    0.100
      //pigment { color red    0.000 green    1.000 blue    1.000 }
      //finish { ambient 0.3 diffuse 0.7 phong 1 }
      //}

      int bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;

      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface bond = molecule.getBondInterface(i);
        AtomInterface a1 = bond.getIAtomInterface();
        AtomInterface a2 = bond.getJAtomInterface();

        // --- Whether atoms are visible

        Object obj = a1.getProperty(AtomInterface.VISIBLE);
        if (obj != null && obj instanceof Boolean) {
          if (! (Boolean) obj) {
            continue;
          }
        }

        obj = a2.getProperty(AtomInterface.VISIBLE);
        if (obj != null && obj instanceof Boolean) {
          if (! (Boolean) obj) {
            continue;
          }
        }

        obj = bond.getProperty(BondInterface.RENDERING_STYLE);
        int bRendStyle = bondRenderingStyle;
        if (obj instanceof Integer) {
          bRendStyle = ( (Integer) obj).intValue();
        }

        Float R = (Float) bond.getProperty(BondInterface.CYLINDER_RADIUS);
        if (R == null || bRendStyle == BondInterface.LINE_BICOLOR) {
          R = defaulCylinerRadius;
        }

        out.write("#\n".getBytes());

        try {
          out.write( ("# Bond : #" + (molecule.getAtomIndex(a1) + 1) + ":" + a1.getName() + "(" +
                      ChemicalElements.getElementName(a1.getAtomicNumber()) +
                      ") " + molecule.getMonomerInterface(a1.getSubstructureNumber()).getName() + "-" +
                      (a1.getSubstructureNumber() + 1) + " - #" + (molecule.getAtomIndex(a2) + 1) + ":" + a2.getName() + "(" +
                      ChemicalElements.getElementName(a2.getAtomicNumber()) +
                      ") " + molecule.getMonomerInterface(a2.getSubstructureNumber()).getName() + "-" +
                      (a2.getSubstructureNumber() + 1) + "\n").getBytes());
        }
        catch (Exception ex) {}

        if (bRendStyle == BondInterface.CYLINDER_BICOLOR || bRendStyle == BondInterface.LINE_BICOLOR) {

          float red = 0.5f, green = 0.5f, blue = 0.5f;
          obj = a1.getProperty(AtomInterface.RGB_COLOR);
          if (obj != null) {
            if (obj instanceof Integer[]) {
              red = (float) ( (Integer[]) obj)[0] / 255.0f;
              green = (float) ( (Integer[]) obj)[1] / 255.0f;
              blue = (float) ( (Integer[]) obj)[2] / 255.0f;
            }
          }

          // --- Write the first part of a bond

          out.write( ("c " + a1.getX() + " " + a1.getY() + " " + a1.getZ() + " " + (0.5f * (a1.getX() + a2.getX())) + " " +
                      (0.5f * (a1.getY() + a2.getY())) + " " + (0.5f * (a1.getZ() + a2.getZ())) + " " + R + " " + R +
                      " " + red + " " + green + " " + blue + "\n").getBytes());

          // --- Write the second part of a bond

          red = 0.5f;
          green = 0.5f;
          blue = 0.5f;
          obj = a2.getProperty(AtomInterface.RGB_COLOR);
          if (obj != null) {
            if (obj instanceof Integer[]) {
              red = (float) ( (Integer[]) obj)[0] / 255.0f;
              green = (float) ( (Integer[]) obj)[1] / 255.0f;
              blue = (float) ( (Integer[]) obj)[2] / 255.0f;
            }
          }

          out.write( ("c " + (0.5f * (a1.getX() + a2.getX())) + "  " + (0.5f * (a1.getY() + a2.getY())) + "  " +
                      (0.5f * (a1.getZ() + a2.getZ())) + " " + a2.getX() + " " + a2.getY() + " " + a2.getZ() + " " + R +
                      " " + R + " " + red + " " + green + " " + blue + "\n").getBytes());
        }
        else if (bRendStyle == BondInterface.CYLINDER_MONOCOLOR) {

        }
        else if (bRendStyle == BondInterface.LINE_MONOCOLOR) {

        }

      }

      out.close();
    }

    catch (IOException e) {
      throw e;
    }

  }
}
