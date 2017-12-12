/* ***** BEGIN LICENSE BLOCK *****
 Version: Apache 2.0/GPL 3.0/LGPL 3.0

 CCT - Computational Chemistry Tools
 Jamberoo - Java Molecules Editor

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
package cct.povray;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.Point3fInterface;
import cct.modelling.ChemicalElements;
import cct.tools.FileFilterImpl;
import cct.vecmath.FaceIndices;
import cct.vecmath.MeshObject;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2007</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Povray {

  public static final String IMAGE_SEQ_INI_TEMPLATE_KEY = "povray.template.movie.ini";
  public static final String DEFAULT_IMAGE_SEQ_INI_FILENAME = "gen-img-seq.ini";
  public static final String DEFAULT_IMAGE_SEQ_INI_TEMPLATE
          = "; Persistence Of Vision raytracer version 3.7 image sequence template file.\n"
          + "Antialias=On\n"
          + "\n"
          + "Antialias_Threshold=0.1\n"
          + "Antialias_Depth=2\n"
          + "Input_File_Name=@@POVRAY-CONTROL-FILE@@\n"
          + "\n"
          + "Initial_Frame=1\n"
          + "Final_Frame=@@FRAMES@@\n"
          + "Initial_Clock=1\n"
          + "Final_Clock=@@FRAMES@@\n"
          + "\n"
          + "Cyclic_Animation=on\n"
          + "Pause_when_Done=off\n"
          + "\n"
          + "Output_File_Type=J\n"
          + "Width=800\n"
          + "Height=600\n";

  public static final String IMAGE_SEQ_POV_TEMPLATE_KEY = "povray.template.movie.pov";
  public static final String DEFAULT_IMAGE_SEQ_POV_FILENAME = "gen-img-seq.pov";
  public static final String DEFAULT_IMAGE_SEQ_POV_TEMPLATE
          = "#declare _DEBUG_          = true;\n"
          + "#declare AnimationLength        = 20.0;\n"
          + "#declare AnimationClock = clock * AnimationLength;\n"
          + "#declare file = concat(\"@@PREFIX@@\",str(frame_number,-6,0),\".pov\")\n"
          + "#ifdef (_DEBUG_)\n"
          + "#debug \"\\n\"\n"
          + "#debug concat(\"clock          = \", str(clock,7,3),\" Frame number: \",str(frame_number,0,0) , \"\\n\")\n"
          + "#debug concat(\"AnimationClock = \", str(AnimationClock,7,3), \"\\n\")\n"
          + "#debug concat(\"Rendering \", file, \"\\n\")\n"
          + "#end\n"
          + "#include file\n";

  public static final String NUMBER_OF_FRAMES_TEMPLATE = "@@FRAMES@@";
  public static final String FILE_PREFIX_TEMPLATE = "@@PREFIX@@";
  public static final String POVRAY_CONTROL_FILE_NAME_TEMPLATE = "@@POVRAY-CONTROL-FILE@@";

  private static String lastPWDKey = "lastPWD";
  private static String phongID = "phongValue";
  private static String meshPhongID = "meshPhongValue";
  private static String phongSizeID = "phongSizeValue";
  private static String meshPhongSizeID = "meshPhongSizeValue";
  private static String diffuseID = "diffuseValue";
  private static String meshDiffuseID = "meshDiffuseValue";
  private static String meshOpacityID = "meshOpacity";

  private static float defaultPhongValue = 0.6f;
  private static float defaultMeshPhongValue = 0.6f;
  private static float defaultPhongSizeValue = 5.0f;
  private static float defaultMeshPhongSizeValue = 5.0f;
  private static float defaultDiffuseValue = 0.7f;
  private static float defaultMeshDiffuseValue = 0.7f;
  private static float defaulCylinerRadius = 0.01f;

  public static final int PARALLEL_PROJECTION = 0;
  public static final int PERSPECTIVE_PROJECTION = 1;

  public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

  float defaultAtomicRadius = 0.05f;

  int projectionPolicy = PERSPECTIVE_PROJECTION;
  float fieldOfView = 60.0f;
  float[] cameraLocation = {
    0, 0, 0};
  float[] transformMatrix = null;
  MoleculeInterface molecule = null;
  float[] backgroundColor = {
    0.0f, 0.0f, 0.0f};

  Preferences prefs = Preferences.userNodeForPackage(getClass());
  File currentWorkingDirectory = null;
  JFileChooser chooser = null;
  FileFilterImpl filter = null;

  List<MeshObject> meshObjects = null;

  public Povray() {
  }

  public void settransformMatrix(float[] matrix) {
    transformMatrix = new float[16];
    for (int i = 0; i < 16; i++) {
      transformMatrix[i] = matrix[i];
    }
  }

  public void setCameraLocation(float x, float y, float z) {
    cameraLocation[0] = x;
    cameraLocation[1] = y;
    cameraLocation[2] = z;
  }

  public void setMolecule(MoleculeInterface mol) {
    molecule = mol;
  }

  public void setFieldOfView(float angle) {
    fieldOfView = angle;
  }

  public void setProjectionPolicy(int policy) {
    if (policy != PARALLEL_PROJECTION && policy != PERSPECTIVE_PROJECTION) {
      System.err.println("setProjectionPolicy: wrong projection policy: set to perspective...");
      policy = PERSPECTIVE_PROJECTION;
    }
    projectionPolicy = policy;
  }

  public static void main(String[] args) {
    Povray povray = new Povray();
  }

  public void savePovrayFile() throws Exception {
    if (chooser == null) {
      chooser = new JFileChooser();
      filter = new FileFilterImpl();

      String temp[] = {
        "pov", "POV"}; // extensions.split(";");
      for (int i = 0; i < temp.length; i++) {
        filter.addExtension(temp[i]);
      }
      filter.setDescription("Pov-Ray Files (*.pov)");
      chooser.setFileFilter(filter);
    }

    chooser.setDialogTitle("Save as Pov-Ray File");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);

    if (currentWorkingDirectory != null) {
      chooser.setCurrentDirectory(currentWorkingDirectory);
    } else {
      String lastPWD = prefs.get(lastPWDKey, "");
      if (lastPWD.length() > 0) {
        currentWorkingDirectory = new File(lastPWD);
        if (currentWorkingDirectory.isDirectory()
                && currentWorkingDirectory.exists()) {
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
        fileName += "pov";
      } else if (!fileName.endsWith(".pov") && !fileName.endsWith(".POV")) {
        fileName += ".pov";
      }
      currentWorkingDirectory = chooser.getCurrentDirectory();
      try {
        prefs.put(lastPWDKey, currentWorkingDirectory.getAbsolutePath());
      } catch (Exception ex) {
        System.err.println("Cannot save cwd: " + ex.getMessage()
                + " Ignored...");
      }
      //logger.info("You chose to open this file: " +
      //                   fileName);

      savePovrayFile(fileName);
    }

  }

  public void setBackgroundColor(float red, float green, float blue) {
    backgroundColor[0] = red;
    backgroundColor[1] = green;
    backgroundColor[2] = blue;
  }

  public void savePovrayFile(String filename) throws Exception {
    FileOutputStream out;
    try {
      out = new FileOutputStream(filename);

      out.write("// --- File is generated by Jamberoo (former JMolEditor)\n".getBytes());
      out.write("\n".getBytes());

      out.write("global_settings { assumed_gamma 2.2 }\n".getBytes());
      out.write("\n".getBytes());
      out.write("#include \"colors.inc\"\n".getBytes());
      out.write("\n".getBytes());

      // --- Writing camera parameters
      out.write("camera {\n".getBytes());

      if (projectionPolicy == PERSPECTIVE_PROJECTION) {
        out.write("  perspective\n".getBytes());
      }
      //else {
      //   out.write("  parallel\n".getBytes());
      //}

      //out.write("  perspective\n".getBytes());
      out.write(("  location <" + String.valueOf(cameraLocation[0]) + ", " + String.valueOf(cameraLocation[1]) + ", "
              + String.valueOf(cameraLocation[2]) + ">\n").getBytes());
      /*
       perspective
       location  <0, 0, 0>
       right <4/3, 0, 0>
       up <0, 1, 0>
       direction <0, -1, 0>
       look_at <0, 0, -1>
       angle 60
       */
      out.write("  right <4/3, 0, 0>\n".getBytes());
      out.write("  up <0, -1, 0>\n".getBytes());
      //out.write( "direction <0, -1, 0>\n".getBytes());
      out.write(("  look_at <0, 0, -1>\n").getBytes());

      out.write(("  angle " + String.valueOf(fieldOfView) + "\n").getBytes());
      out.write("  }\n".getBytes());

      // --- Light and background
      out.write("\n".getBytes());
      out.write("light_source { <  50.000,   50.000,   50.000> colour Gray70 }\n".getBytes());
      out.write(("\n\n// --- Background color\n\n"
              + "background { color red " + backgroundColor[0] + " green " + backgroundColor[1] + " blue " + backgroundColor[2]
              + " }\n").getBytes());

      // --- Setup transform matrix
      if (transformMatrix != null) {
        out.write("\n#declare transformMatrix =\n".getBytes());
        out.write("  transform {\n".getBytes());
        out.write("    matrix <\n".getBytes());
        out.write(("      " + String.valueOf(transformMatrix[0]) + ", " + String.valueOf(transformMatrix[4]) + ", "
                + String.valueOf(transformMatrix[8]) + ",\n").getBytes());
        out.write(("      " + transformMatrix[1] + ", " + transformMatrix[5] + ", " + transformMatrix[9] + ",\n").getBytes());
        out.write(("      " + transformMatrix[2] + ", " + transformMatrix[6] + ", " + transformMatrix[10] + ",\n").getBytes());
        out.write(("      " + transformMatrix[3] + ", " + transformMatrix[7] + ", " + transformMatrix[11] + "\n").getBytes());
        out.write("    >\n".getBytes());
        out.write("  }\n".getBytes());
      }

      // --- Writing atoms
      //sphere { <  13.354,  -14.637,  -11.758>,    1.450
      //pigment { color red    0.000 green    0.000 blue    1.000 }
      //finish { ambient 0.3 diffuse 0.7 phong 1 }
      //}
      Map<AtomInterface, float[]> Colors = new HashMap<AtomInterface, float[]>(molecule.getNumberOfAtoms());

      Map<Float, String> uniqueRadii = new LinkedHashMap<Float, String>(molecule.getNumberOfAtoms());
      Map<Integer, Float[]> uniqueColors = new LinkedHashMap<Integer, Float[]>(molecule.getNumberOfAtoms());
      Map<Integer, Float[]> uniqueAmbientColors = new LinkedHashMap<Integer, Float[]>(molecule.getNumberOfAtoms());
      Map<Integer, String> uniqueColorID = new HashMap<Integer, String>(molecule.getNumberOfAtoms());
      Map<Integer, String> uniqueAmbientColorID = new HashMap<Integer, String>(molecule.getNumberOfAtoms());
      List<String> atomicRadii = new ArrayList<String>(molecule.getNumberOfAtoms());
      List<Integer> atomicColors = new ArrayList<Integer>(molecule.getNumberOfAtoms());
      List<Integer> atomicAmbientColors = new ArrayList<Integer>(molecule.getNumberOfAtoms());

      // --- First, find unique properties for atoms
      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface atom = molecule.getAtomInterface(i);

        Object obj = atom.getProperty(AtomInterface.VISIBLE);
        if (obj != null && obj instanceof Boolean) {
          if (!(Boolean) obj) {
            continue;
          }
        }

        float red = 0.5f, green = 0.5f, blue = 0.5f;
        obj = atom.getProperty(AtomInterface.RGB_COLOR);
        if (obj != null) {
          if (obj instanceof Integer[]) {
            red = (float) ((Integer[]) obj)[0] / 255.0f;
            green = (float) ((Integer[]) obj)[1] / 255.0f;
            blue = (float) ((Integer[]) obj)[2] / 255.0f;

          }
        }

        float[] colors = {
          red, green, blue};
        Colors.put(atom, colors);

        // --- Process Atomic Radii
        obj = atom.getProperty(AtomInterface.GR_RADIUS);
        Float radius = defaultAtomicRadius;
        if (obj != null) {
          if (obj instanceof Float) {
            radius = (Float) obj;
          } else if (obj instanceof Double) {
            radius = ((Double) obj).floatValue();
          }
        }

        String radiusID = "";
        if (!uniqueRadii.containsKey(radius)) {
          radiusID = "atomicRadius_" + String.valueOf(uniqueRadii.size() + 1);
          uniqueRadii.put(radius, radiusID);
        } else {
          radiusID = uniqueRadii.get(radius);
        }
        atomicRadii.add(radiusID);

        // --- Process atomic colors
        processAtomColor(atom, AtomInterface.RGB_COLOR, uniqueColors, atomicColors, new float[]{0.5f, 0.5f, 0.5f});

        // --- Process atomic Ambient colors (if any)
        processAtomColor(atom, AtomInterface.AMBIENT_RGB_COLOR, uniqueAmbientColors, atomicAmbientColors, new float[]{0.3f,
          0.3f, 0.3f});
      }

      // --- Write unique atomic radii
      out.write("\n// --- Unique atomic radii\n\n".getBytes());

      Set set = uniqueRadii.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        Float value = (Float) me.getKey();
        String id = me.getValue().toString();
        out.write(("#declare " + id + "=" + value.floatValue() + ";\n").getBytes());
      }

      // --- Write unique atomic colors
      out.write("\n// --- Unique atomic colors\n\n".getBytes());

      int count = 0;
      set = uniqueColors.entrySet();
      iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        Integer value = (Integer) me.getKey();
        Float[] color = (Float[]) me.getValue();
        ++count;
        String id = "atomColor_" + count;
        uniqueColorID.put(value, id);
        out.write(("#declare " + id + " = " + "pigment { color red " + color[0] + " green " + color[1] + " blue " + color[2]
                + " }\n").getBytes());
      }

      // --- Write unique atomic Ambient colors
      out.write("\n// --- Unique atomic Ambient colors\n\n".getBytes());

      count = 0;
      set = uniqueAmbientColors.entrySet();
      iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        Integer value = (Integer) me.getKey();
        Float[] color = (Float[]) me.getValue();
        ++count;
        String id = "atomAmbientColor_" + count;
        uniqueAmbientColorID.put(value, id);
        out.write(("#declare " + id + " = " + "< " + color[0] + ", " + color[1] + ", " + color[2] + " >;\n").getBytes());
      }

      // --- Second, find unique properties for bonds
      Map<Float, String> uniqueBondRadii = new LinkedHashMap<Float, String>(molecule.getNumberOfBonds());
      Map<Integer, Float[]> uniqueBondColors = new LinkedHashMap<Integer, Float[]>(molecule.getNumberOfBonds());
      Map<BondInterface, Object> bondColorRef = new LinkedHashMap<BondInterface, Object>(molecule.getNumberOfBonds());
      List<Integer> bondColors = new ArrayList<Integer>(molecule.getNumberOfBonds());
      List<String> bondRadii = new ArrayList<String>(molecule.getNumberOfBonds());
      Map<Integer, String> uniqueBondColorID = new HashMap<Integer, String>(molecule.getNumberOfAtoms());
      float[] defaultBondColor = new float[]{
        0.5f, 0.5f, 0.5f};

      int bondRenderingStyle = BondInterface.CYLINDER_BICOLOR;
      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface bond = molecule.getBondInterface(i);
        AtomInterface a1 = bond.getIAtomInterface();
        AtomInterface a2 = bond.getJAtomInterface();

        float[] a1_color = Colors.get(a1);
        float[] a2_color = Colors.get(a2);

        processColor(a1_color, uniqueBondColors, bondColors, defaultBondColor);
        processColor(a2_color, uniqueBondColors, bondColors, defaultBondColor);

        Object obj = bond.getProperty(BondInterface.RENDERING_STYLE);
        int bRendStyle = bondRenderingStyle;
        if (obj instanceof Integer) {
          bRendStyle = ((Integer) obj).intValue();
        }

        if (bRendStyle == BondInterface.CYLINDER_BICOLOR || bRendStyle == BondInterface.LINE_BICOLOR) {
          bondColorRef.put(bond, new Integer[]{bondColors.get(2 * i), bondColors.get(2 * i + 1)});
        }

        // --- Process radius
        Float R = (Float) bond.getProperty(BondInterface.CYLINDER_RADIUS);
        if (R == null || bRendStyle == BondInterface.LINE_BICOLOR) {
          R = defaulCylinerRadius;
        }

        String radiusID = "";
        if (!uniqueBondRadii.containsKey(R)) {
          radiusID = "bondRadius_" + String.valueOf(uniqueBondRadii.size() + 1);
          uniqueBondRadii.put(R, radiusID);
        } else {
          radiusID = uniqueBondRadii.get(R);
        }
        bondRadii.add(radiusID);

      }

      // --- Write unique bond radii
      out.write("\n// --- Unique bond radii\n\n".getBytes());

      set = uniqueBondRadii.entrySet();
      iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        Float value = (Float) me.getKey();
        String id = me.getValue().toString();
        out.write(("#declare " + id + "=" + value.floatValue() + ";\n").getBytes());
      }

      // --- Write unique bond colors
      out.write("\n// --- Unique bond colors\n\n".getBytes());

      count = 0;
      set = uniqueBondColors.entrySet();
      iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        Integer value = (Integer) me.getKey();
        Float[] color = (Float[]) me.getValue();
        ++count;
        String id = "bondColor_" + count;
        uniqueBondColorID.put(value, id);
        out.write(("#declare " + id + " = " + "pigment { color red " + color[0] + " green " + color[1] + " blue " + color[2]
                + " }\n").getBytes());
      }

      // --- Write other finishing parameters
      out.write("\n// --- Finishing parameters\n\n".getBytes());

      out.write(("// The " + phongID + " keyword controls the amount of the bright shiny spots\n").getBytes());
      out.write(("// on the object that are the color of the light source being reflected.\n").getBytes());
      out.write(("// " + phongID + "'s value is typically from 0.0 to 1.0,\n").getBytes());
      out.write(("// where 1.0 causes complete saturation to the light source's color\n").getBytes());
      out.write(("// at the brightest area (center) of the highlight. The value 0.0 gives no highlight.\n").getBytes());
      out.write("\n".getBytes());
      out.write(("#declare " + phongID + " = " + defaultPhongValue + ";\n").getBytes());

      out.write("\n".getBytes());

      out.write(("// The size of the highlight spot is defined by the " + phongSizeID + " value.\n").getBytes());
      out.write(("// The larger the phong size the tighter, or smaller, the highlight and the shinier the appearance.\n").
              getBytes());
      out.write(("// The smaller the phong size the looser, or larger, the highlight and the less glossy the appearance.\n").
              getBytes());
      out.write(("// Typical values range from 1.0 (very dull) to 250 (highly polished)\n").getBytes());
      out.write("\n".getBytes());
      out.write(("#declare " + phongSizeID + " = " + defaultPhongSizeValue + ";\n").getBytes());

      out.write("\n".getBytes());

      out.write(("// The value of " + diffuseID + " is used in a \"diffuse\" statement\n").getBytes());
      out.write(("// to control how much of the light coming directly from any light sources\n").getBytes());
      out.write(("// is reflected via diffuse reflection. For example, the value 0.7 means that 70% of the light seen\n").
              getBytes());
      out.write(("// comes from direct illumination from light sources.\n").getBytes());
      out.write("\n".getBytes());
      out.write(("#declare " + diffuseID + " = " + defaultDiffuseValue + ";\n").getBytes());

      // --- Parameters for mesh objects
      List<String> meshTransparency = null;
      if (meshObjects != null && meshObjects.size() > 0) {

        out.write("\n".getBytes());

        out.write(("// Mesh parameters\n").getBytes());

        out.write(("#declare " + meshDiffuseID + " = " + defaultMeshDiffuseValue + ";\n").getBytes());
        out.write(("#declare " + meshPhongID + " = " + defaultMeshPhongValue + ";\n").getBytes());
        out.write(("#declare " + meshPhongSizeID + " = " + defaultMeshPhongSizeValue + ";\n").getBytes());

        out.write("\n".getBytes());
        meshTransparency = new ArrayList<String>(meshObjects.size());
        for (int i = 0; i < meshObjects.size(); i++) {
          String id = meshOpacityID + "_" + String.valueOf(i + 1);
          meshTransparency.add(id);
          out.write(("#declare " + id + " = " + meshObjects.get(i).getTransparency() + ";\n").getBytes());
        }
        out.write("\n".getBytes());
      }

      // --- Finally start to write atoms
      out.write("\n// --- Atomic spheres\n\n".getBytes());

      out.write("union {\n".getBytes());

      for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
        AtomInterface atom = molecule.getAtomInterface(i);

        Object obj = atom.getProperty(AtomInterface.VISIBLE);
        if (obj != null && obj instanceof Boolean) {
          if (!(Boolean) obj) {
            continue;
          }
        }

        out.write("\n".getBytes());

        String monomerName = molecule.getMonomerInterface(atom.getSubstructureNumber()).getName();

        try {
          out.write(("// Atom " + (i + 1) + " " + atom.getName() + "(" + ChemicalElements.getElementName(atom.getAtomicNumber())
                  + ") " + monomerName + "-" + (atom.getSubstructureNumber() + 1) + "\n").getBytes());
        } catch (Exception ex) {
        }

        out.write(("sphere { < " + atom.getX() + ", " + atom.getY() + ", " + atom.getZ() + ">, " + atomicRadii.get(i) + "\n").
                getBytes());
        //if (transformMatrix != null) {
        //   out.write("  transform { transformMatrix }\n".getBytes());
        //}
        Integer colorHash = atomicColors.get(i);
        String colorID = uniqueColorID.get(colorHash);
        out.write(("  texture { " + colorID + " }\n").getBytes());
        out.write("  finish { ".getBytes());

        colorHash = atomicAmbientColors.get(i);
        colorID = uniqueAmbientColorID.get(colorHash);
        out.write(("  ambient rgb " + colorID + " diffuse " + diffuseID + " phong " + phongID + " phong_size " + phongSizeID).
                getBytes());
        out.write(" }\n".getBytes());

        out.write("}\n".getBytes());
      }

      if (transformMatrix != null) {
        out.write("\n  transform { transformMatrix }\n".getBytes());
      }
      out.write("}\n".getBytes());

      // --- Writing bonds
      //cylinder { <  -5.244,    2.407,  -18.493>, <  -4.837,    2.627,  -18.216>,    0.100
      //pigment { color red    0.000 green    1.000 blue    1.000 }
      //finish { ambient 0.3 diffuse 0.7 phong 1 }
      //}
      for (int i = 0; i < molecule.getNumberOfBonds(); i++) {
        BondInterface bond = molecule.getBondInterface(i);
        AtomInterface a1 = bond.getIAtomInterface();
        AtomInterface a2 = bond.getJAtomInterface();

        Object obj = bond.getProperty(BondInterface.RENDERING_STYLE);
        int bRendStyle = bondRenderingStyle;
        if (obj instanceof Integer) {
          bRendStyle = ((Integer) obj).intValue();
        }

        Float R = (Float) bond.getProperty(BondInterface.CYLINDER_RADIUS);
        if (R == null || bRendStyle == BondInterface.LINE_BICOLOR) {
          R = defaulCylinerRadius;
        }

        out.write("\n".getBytes());

        try {
          out.write(("// Bond : #" + (molecule.getAtomIndex(a1) + 1) + ":" + a1.getName() + "("
                  + ChemicalElements.getElementName(a1.getAtomicNumber())
                  + ") " + molecule.getMonomerInterface(a1.getSubstructureNumber()).getName() + "-"
                  + (a1.getSubstructureNumber() + 1) + " - #" + (molecule.getAtomIndex(a2) + 1) + ":" + a2.getName() + "("
                  + ChemicalElements.getElementName(a2.getAtomicNumber())
                  + ") " + molecule.getMonomerInterface(a2.getSubstructureNumber()).getName() + "-"
                  + (a2.getSubstructureNumber() + 1) + "\n").getBytes());
        } catch (Exception ex) {
        }

        if (bRendStyle == BondInterface.CYLINDER_BICOLOR || bRendStyle == BondInterface.LINE_BICOLOR) {

          obj = bondColorRef.get(bond);
          Integer[] colors = (Integer[]) obj;

          out.write(("cylinder { < " + a1.getX() + ", " + a1.getY() + ", " + a1.getZ() + ">,\n").getBytes());
          out.write((" < " + (0.5f * (a1.getX() + a2.getX())) + ", "
                  + (0.5f * (a1.getY() + a2.getY())) + ", " + (0.5f * (a1.getZ() + a2.getZ())) + ">, " + bondRadii.get(i)
                  + "\n").getBytes());
          if (transformMatrix != null) {
            out.write("  transform { transformMatrix }\n".getBytes());
          }
          String colorID = uniqueBondColorID.get(colors[0]);
          out.write(("  texture { " + colorID + " }\n").getBytes());
          //out.write( ("  pigment { color red " + a1_color[0] + " green " + a1_color[1] + " blue " + a1_color[2] + "}\n").
          //          getBytes());
          out.write(("finish { ambient 0.3 diffuse " + diffuseID + " phong " + phongID + "  phong_size " + phongSizeID
                  + " }\n").getBytes());
          out.write("}\n".getBytes());

          out.write("\n".getBytes());

          out.write(("cylinder { < " + (0.5f * (a1.getX() + a2.getX())) + ", " + (0.5f * (a1.getY() + a2.getY())) + ", "
                  + (0.5f * (a1.getZ() + a2.getZ())) + ">,\n").getBytes());
          out.write((" < " + a2.getX() + ", " + a2.getY() + ", " + a2.getZ() + ">, " + bondRadii.get(i)
                  + "\n").getBytes());
          if (transformMatrix != null) {
            out.write("  transform { transformMatrix }\n".getBytes());
          }

          colorID = uniqueBondColorID.get(colors[1]);
          out.write(("  texture { " + colorID + " }\n").getBytes());
          //out.write( ("  pigment { color red " + a2_color[0] + " green " + a2_color[1] + " blue " + a2_color[2] + "}\n").
          //          getBytes());
          out.write(("finish { ambient 0.3 diffuse " + diffuseID + " phong " + phongID + "  phong_size " + phongSizeID
                  + " }\n").getBytes());
          out.write("}\n".getBytes());

        } else if (bRendStyle == BondInterface.CYLINDER_MONOCOLOR) {

        } else if (bRendStyle == BondInterface.LINE_MONOCOLOR) {

        }

      }

      if (meshObjects != null && meshObjects.size() > 0) {
        for (int i = 0; i < meshObjects.size(); i++) {
          MeshObject mesh = meshObjects.get(i);
          if (mesh.getObjectType() == MeshObject.TRIANGLES_MESH_OBJECT) {
            out.write("mesh2 {\n".getBytes());

            // --- Write vertices
            Point3fInterface[] vertices = mesh.getVerticesAsArray();
            out.write("  vertex_vectors {\n".getBytes());
            out.write(("    " + vertices.length + ",\n").getBytes());
            for (int j = 0; j < vertices.length / 3; j++) {
              if (j != 0) {
                out.write(",\n".getBytes());
              }
              out.write(("    <" + vertices[j * 3].getX() + ", " + vertices[j * 3].getY() + ", " + vertices[j * 3].getZ()
                      + ">, ").getBytes());
              out.write(("<" + vertices[j * 3 + 1].getX() + ", " + vertices[j * 3 + 1].getY() + ", " + vertices[j * 3
                      + 1].getZ() + ">, ").getBytes());
              out.write(("<" + vertices[j * 3 + 2].getX() + ", " + vertices[j * 3 + 2].getY() + ", " + vertices[j * 3
                      + 2].getZ() + ">").getBytes());
            }
            out.write("\n  }\n".getBytes());

            // --- Write normals
            List<Point3fInterface> normals = mesh.getNormals();
            out.write("  normal_vectors {\n".getBytes());
            out.write(("    " + normals.size() + ",\n").getBytes());
            for (int j = 0; j < normals.size() / 3; j++) {
              if (j != 0) {
                out.write(",\n".getBytes());
              }
              out.write(("    <" + normals.get(j * 3).getX() + ", " + normals.get(j * 3).getY() + ", "
                      + normals.get(j * 3).getZ()
                      + ">, ").getBytes());
              out.write(("<" + normals.get(j * 3 + 1).getX() + ", " + normals.get(j * 3 + 1).getY() + ", "
                      + normals.get(j * 3 + 1).getZ() + ">, ").getBytes());
              out.write(("<" + normals.get(j * 3 + 2).getX() + ", " + normals.get(j * 3 + 2).getY() + ", "
                      + normals.get(j * 3 + 2).getZ() + ">").getBytes());
            }
            out.write("\n  }\n".getBytes());

            // -- Write colors
            Color[] colors = mesh.getColorsAsArray();
            out.write("  texture_list {\n".getBytes());
            out.write(("    " + colors.length + ",\n").getBytes());
            for (int j = 0; j < colors.length; j++) {
              float red = ((float) colors[j].getRed()) / 255.0f;
              float green = ((float) colors[j].getGreen()) / 255.0f;
              float blue = ((float) colors[j].getBlue()) / 255.0f;
              String ambient = " < " + (red * 0.2f) + ", " + (green * 0.2f) + ", " + (blue * 0.2f) + "> ";
              out.write(("    texture { pigment { rgbt <" + red + ", " + green + ", " + blue + ", "
                      + meshTransparency.get(i) + "> }\n").getBytes());
              out.write(("              finish { ambient " + ambient + " diffuse " + meshDiffuseID
                      + " phong " + meshPhongID + " phong_size " + meshPhongSizeID + " }}\n").getBytes());
            }
            out.write("\n  }\n".getBytes());

            // --- Write face indices
            List<FaceIndices> faces = mesh.getFaceIndices();
            out.write("  face_indices {\n".getBytes());
            out.write(("    " + faces.size() + ",\n").getBytes());
            for (int j = 0; j < faces.size(); j++) {
              if (j != 0) {
                out.write(",\n".getBytes());
              }

              FaceIndices face = faces.get(j);
              out.write(("    < " + face.i + ", " + face.j + ", " + face.k + ">, " + face.i + ", " + face.j + ", " + face.k).
                      getBytes());
            }
            out.write("\n  }\n".getBytes());

            if (transformMatrix != null) {
              out.write("\n  transform { transformMatrix }\n".getBytes());
            }

            out.write("}\n".getBytes());
          }

        }
      }

      out.close();
    } catch (IOException e) {
      throw e;
    }

  }

  private void processColor(Object colorInfo, Map uniqueColors, List atomicColors, float[] defaultColor) {
    float red = defaultColor[0], green = defaultColor[1], blue = defaultColor[2];
    Integer colorHash = -1;
    if (colorInfo != null) {
      if (colorInfo instanceof Integer[]) {
        Integer[] c = (Integer[]) colorInfo;
        red = (float) c[0] / 255.0f;
        green = (float) c[1] / 255.0f;
        blue = (float) c[2] / 255.0f;
        colorHash = this.getColorHash(c[0], c[1], c[2]);
      } else if (colorInfo instanceof int[]) {
        int[] c = (int[]) colorInfo;
        red = (float) c[0] / 255.0f;
        green = (float) c[1] / 255.0f;
        blue = (float) c[2] / 255.0f;
        colorHash = this.getColorHash(c[0], c[1], c[2]);
      } else if (colorInfo instanceof Float[]) {
        Float[] c = (Float[]) colorInfo;
        red = c[0];
        green = c[1];
        blue = c[2];
      } else if (colorInfo instanceof float[]) {
        float[] c = (float[]) colorInfo;
        red = c[0];
        green = c[1];
        blue = c[2];
      }

    }

    if (colorHash < 0) {
      colorHash = getColorHash(red, green, blue);
    }

    //String pigment = "";
    if (!uniqueColors.containsKey(colorHash)) {
      uniqueColors.put(colorHash, new Float[]{red, green, blue});
      //pigment = "pigment { color red "+red+" green "+green+" blue "+blue+" }";
    }
    atomicColors.add(colorHash);
  }

  private void processAtomColor(AtomInterface atom, String colorLabel, Map uniqueColors, List atomicColors,
          float[] defaultColor) {
    Object obj = atom.getProperty(colorLabel);
    processColor(obj, uniqueColors, atomicColors, defaultColor);

  }

  Integer getColorHash(Integer red, Integer green, Integer blue) {
    Integer colorHash = -1;
    colorHash = red << 16 | green << 8 | blue;
    return colorHash;
  }

  Integer getColorHash(int red, int green, int blue) {
    Integer colorHash = -1;
    colorHash = red << 16 | green << 8 | blue;
    return colorHash;
  }

  Integer getColorHash(float red, float green, float blue) {
    Integer colorHash = -1;
    int r = (int) (red * 255.0f);
    int g = (int) (green * 255.0f);
    int b = (int) (blue * 255.0f);
    colorHash = r << 16 | g << 8 | b;
    return colorHash;
  }

  Integer getColorHash(Float red, Float green, Float blue) {
    Integer colorHash = -1;
    int r = (int) (red * 255.0f);
    int g = (int) (green * 255.0f);
    int b = (int) (blue * 255.0f);
    colorHash = r << 16 | g << 8 | b;
    return colorHash;
  }

  public void addMeshObject(MeshObject mesh) {
    if (meshObjects == null) {
      meshObjects = new ArrayList<MeshObject>();
    }
    meshObjects.add(mesh);
  }
}
