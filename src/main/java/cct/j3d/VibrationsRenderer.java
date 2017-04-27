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
package cct.j3d;

import cct.GlobalSettings;
import org.scijava.java3d.Alpha;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Group;
import org.scijava.java3d.Node;
import org.scijava.java3d.Switch;
import org.scijava.java3d.SwitchValueInterpolator;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.OutputResultsInterface;
import cct.interfaces.VibrationsRendererProvider;
import cct.modelling.ChemicalElements;
import cct.modelling.Molecule;
import cct.povray.Povray;
import cct.tools.IOUtils;
import java.awt.Frame;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * <p>
 * Title: Computational Chemistry Tookit</p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. Vladislav Vassiliev
 * @version 1.0
 */
public class VibrationsRenderer
        implements VibrationsRendererProvider, RenderingListener {

  public static final String DEFAULT_IMAGE_SEQ_POV_FILENAME = "anim-img-seq.pov";
  public static final String DEFAULT_FILE_PREFIX = "vib-";

  private MoleculeInterface referenceMolecule = null;
  private OutputResultsInterface vibProvider = null;
  private Java3dUniverse j3d = null;
  private DVObject[] dvObjects = null;
  private float scale_length = 2.5f;
  private float arrowRadius = 0.05f;
  private int framesPerHalfCycle = 11; // It's better to use odd number
  private long halfCycleDuration = 500;
  private long minHalfCycleDuration = 100;
  private long maxHalfCycleDuration = 1500;
  private long durationStep = 100;
  private Node moleculeParentNode = null;
  private BranchGroup moleculeBranchGroup = null;
  private String filePrefix = DEFAULT_FILE_PREFIX;

  private int currentFrequency = -1;
  private boolean animationInProgress = false;

  public VibrationsRenderer(MoleculeInterface molecule, OutputResultsInterface provider, Java3dUniverse renderer) throws Exception {
    referenceMolecule = molecule;
    vibProvider = provider;
    j3d = renderer;
    dvObjects = new DVObject[molecule.getNumberOfAtoms() * 3];

    if (j3d != null) {
      j3d.addRenderingListener(this);

      moleculeBranchGroup = j3d.getMoleculeBranchGroup();
      try {
        moleculeParentNode = moleculeBranchGroup.getParent();
        if (moleculeParentNode == null) {
          System.err.println("Molecule BranchGroup does not have a parent");
        }

      } catch (Exception ex) {
        System.err.println("Cannot get parent for molecule BranchGroup: " + ex.getMessage());
      }
    }

  }

  public static void main(String[] args) {
    //VibrationsRenderer vibrationsrenderer = new VibrationsRenderer();
  }

  @Override
  public void showDisplacementVectors(int frequency, boolean show) {
    float[][] vectors = vibProvider.getDisplacementVectors(frequency);
    if (vectors == null) {
      System.err.println("showDisplacementVectors: vectors == null");
      return;
    }
    if (vectors.length != referenceMolecule.getNumberOfAtoms()) {
      System.err.println("showDisplacementVectors: vectors.length != referenceMolecule.getNumberOfAtoms()");
      return;
    }

    if (!show && dvObjects[frequency] == null) {
      System.err.println("showDisplacementVectors: !show && dvObjects[frequency] == null");
      return;
    }
    currentFrequency = frequency;

    if (show) {

      if (dvObjects[frequency] == null || dvObjects[frequency].rootBranch == null) {
        BranchGroup rootBranch = new BranchGroup();
        rootBranch.setCapability(Group.ALLOW_CHILDREN_READ);
        rootBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        rootBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        rootBranch.setCapability(BranchGroup.ALLOW_DETACH);

        for (int i = 0; i < referenceMolecule.getNumberOfAtoms(); i++) {
          AtomInterface atom = referenceMolecule.getAtomInterface(i);
          float[] origin = {
            atom.getX(), atom.getY(), atom.getZ()};
          float length = (float) Math.sqrt(vectors[i][0] * vectors[i][0] + vectors[i][1] * vectors[i][1]
                  + vectors[i][2] * vectors[i][2]) * scale_length;
          if (length < 0.001f) {
            continue;
          }
          ArrowNode arrow = new ArrowNode(origin, vectors[i]);
          arrow.setArrowRadius(arrowRadius);
          arrow.setArrowLength(length);
          arrow.setArrowHeadRadius(arrowRadius + 0.02f);
          arrow.setArrowHeadLength(0.2f);
          arrow.createArrow();
          arrow.setNodeName(String.format("DVECTOR_%03d_%03d_%s", (currentFrequency + 1), (i + 1),
                  ChemicalElements.getElementSymbol(atom.getAtomicNumber())));
          rootBranch.addChild(arrow);
        }
        if (dvObjects[frequency] == null) {
          dvObjects[frequency] = new DVObject();
        }
        dvObjects[frequency].rootBranch = rootBranch;
      }

      //j3d.addGraphicsToMolecule(dvObjects[frequency].rootBranch, true);
      if (moleculeParentNode != null) {
        addBranchGroupToNode(moleculeParentNode, dvObjects[frequency].rootBranch, true);
      }
    } else {
      //j3d.addGraphicsToMolecule(dvObjects[frequency].rootBranch, false);
      if (moleculeParentNode != null) {
        addBranchGroupToNode(moleculeParentNode, dvObjects[frequency].rootBranch, false);
      }

    }
  }

  private void addBranchGroupToNode(Node node, Node child, boolean enable) {
    if (node instanceof TransformGroup) {
      TransformGroup tg = (TransformGroup) node;
      if (enable) {
        try {
          tg.addChild(child);
        } catch (Exception ex) {
          System.err.println("addBranchGroupToNode: cannot add a node to TransformGroup: " + ex.getMessage());
        }
      } else {
        try {
          tg.removeChild(child);
        } catch (Exception ex) {
          System.err.println("addBranchGroupToNode: cannot remove child of TransformGroup: " + ex.getMessage());
        }

      }
    }
  }

  @Override
  public void saveScene(int frequency, String format) throws Exception {

    if (format.equalsIgnoreCase("povray")) {
      generatePovrayImageSequence(frequency);
      return;
    }

    if (!format.equalsIgnoreCase("VRML")) {
      throw new Exception(this.getClass().getCanonicalName() + ": currently support only VRML format for saving scene");
    }

    if (j3d == null) {
      throw new Exception(this.getClass().getCanonicalName() + ": Java3dUniverse == null");
    }
    GroupToVRML vrml = new GroupToVRML(j3d.getLocale());
    vrml.enableColorsPerVertex(false); // Don't write colors per vertex (to be compatible with Acrobat 9,xx)
    vrml.setCanvas3D(j3d.getCanvas3D());
    try {
      vrml.saveVRMLFile();
    } catch (Exception ex) {
      throw new Exception(this.getClass().getCanonicalName() + ": Error Saving VRML file: " + ex.getMessage());
    }

  }

  @Override
  public int getMinimumDuration() {
    return 0;
  }

  @Override
  public int getMaximumDuration() {
    return (int) ((maxHalfCycleDuration - minHalfCycleDuration) / durationStep);
  }

  @Override
  public int getDuration() {
    return (int) ((halfCycleDuration - minHalfCycleDuration) / durationStep);
  }

  @Override
  public void setDuration(int duration) {
    if (duration < 0) {
      duration = 0;
    }
    int max = (int) ((maxHalfCycleDuration - minHalfCycleDuration) / durationStep);
    if (duration > max) {
      duration = max;
    }
    halfCycleDuration = durationStep * duration + minHalfCycleDuration;
    if (currentFrequency != -1 && dvObjects[currentFrequency] != null && dvObjects[currentFrequency].alpha != null) {
      dvObjects[currentFrequency].alpha.setDecreasingAlphaDuration(halfCycleDuration);
      dvObjects[currentFrequency].alpha.setIncreasingAlphaDuration(halfCycleDuration);
    }
  }

  @Override
  public void animateVibrations(int frequency, boolean show) {
    float[][] vectors = vibProvider.getDisplacementVectors(frequency);
    if (vectors == null) {
      System.err.println("animateFrequency: vectors == null");
      return;
    }
    if (vectors.length != referenceMolecule.getNumberOfAtoms()) {
      System.err.println("animateFrequency: vectors.length != referenceMolecule.getNumberOfAtoms()");
      return;
    }

    if (!show && dvObjects[frequency] == null) {
      System.err.println("animateFrequency: !show && dvObjects[frequency] == null");
      return;
    }

    currentFrequency = frequency;

    if (show) {
      if (dvObjects[frequency] == null || dvObjects[frequency].animationBranch == null || dvObjects[frequency].renderingChanged) {

        BranchGroup rootBranch = new BranchGroup();
        rootBranch.setCapability(Group.ALLOW_CHILDREN_READ);
        rootBranch.setCapability(Group.ALLOW_CHILDREN_WRITE);
        rootBranch.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        rootBranch.setCapability(BranchGroup.ALLOW_DETACH);

        Switch objSwitch = new Switch();
        objSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

        // create Alpha
        /*
         public Alpha(int loopCount,
         int mode,
         long triggerTime,
         long phaseDelayDuration,
         long increasingAlphaDuration,
         long increasingAlphaRampDuration,
         long alphaAtOneDuration,
         long decreasingAlphaDuration,
         long decreasingAlphaRampDuration,
         long alphaAtZeroDuration)

         This constructor takes all of the Alpha user-definable parameters.

         Parameters:
         loopCount - number of times to run this alpha; a value of -1 specifies that the alpha loops indefinitely
         mode - indicates whether the increasing alpha parameters or the decreasing alpha parameters or both are active. This parameter accepts the following values, INCREASING_ENABLE or DECREASING_ENABLE, which may be ORed together to specify that both are active. The increasing alpha parameters are increasingAlphaDuration, increasingAlphaRampDuration, and alphaAtOneDuration. The decreasing alpha parameters are decreasingAlphaDuration, decreasingAlphaRampDuration, and alphaAtZeroDuration.
         triggerTime - time in milliseconds since the start time that this object first triggers
         phaseDelayDuration - number of milliseconds to wait after triggerTime before actually starting this alpha
         increasingAlphaDuration - period of time during which alpha goes from zero to one
         increasingAlphaRampDuration - period of time during which the alpha step size increases at the beginning of the increasingAlphaDuration and, correspondingly, decreases at the end of the increasingAlphaDuration. This value is clamped to half of increasingAlphaDuration. NOTE: a value of zero means that the alpha step size remains constant during the entire increasingAlphaDuration.
         alphaAtOneDuration - period of time that alpha stays at one
         decreasingAlphaDuration - period of time during which alpha goes from one to zero
         decreasingAlphaRampDuration - period of time during which the alpha step size increases at the beginning of the decreasingAlphaDuration and, correspondingly, decreases at the end of the decreasingAlphaDuration. This value is clamped to half of decreasingAlphaDuration. NOTE: a value of zero means that the alpha step size remains constant during the entire decreasingAlphaDuration.
         alphaAtZeroDuration - period of time that alpha stays at zero


         */
        Alpha alpha = new Alpha(-1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, halfCycleDuration, 10, 10,
                halfCycleDuration, 10, 0);
        // create switch value interpolator
        SwitchValueInterpolator swiInt = new SwitchValueInterpolator(alpha, objSwitch);
        swiInt.setSchedulingBounds(j3d.getBoundingSphere());

        Transform3D t3d = new Transform3D();
        TransformGroup objSwitchPos = new TransformGroup(t3d);
        objSwitchPos.addChild(objSwitch);

        rootBranch.addChild(objSwitchPos);
        rootBranch.addChild(swiInt);

        float[] lengths = new float[referenceMolecule.getNumberOfAtoms()];
        float[] steps = new float[referenceMolecule.getNumberOfAtoms()];
        float[][] origins = new float[referenceMolecule.getNumberOfAtoms()][3];
        float[][] dirs = new float[referenceMolecule.getNumberOfAtoms()][3];
        float[][] pos = new float[referenceMolecule.getNumberOfAtoms()][3];
        for (int i = 0; i < referenceMolecule.getNumberOfAtoms(); i++) {
          AtomInterface atom = referenceMolecule.getAtomInterface(i);
          lengths[i] = (float) Math.sqrt(vectors[i][0] * vectors[i][0] + vectors[i][1] * vectors[i][1]
                  + vectors[i][2] * vectors[i][2]);
          if (lengths[i] > 0.001) {
            dirs[i][0] = vectors[i][0] / lengths[i];
            dirs[i][1] = vectors[i][1] / lengths[i];
            dirs[i][2] = vectors[i][2] / lengths[i];
          }

          lengths[i] *= scale_length;
          steps[i] = lengths[i] / (framesPerHalfCycle - 1);

          origins[i][0] = atom.getX() - dirs[i][0] * lengths[i] / 2.0f;
          origins[i][1] = atom.getY() - dirs[i][1] * lengths[i] / 2.0f;
          origins[i][2] = atom.getZ() - dirs[i][2] * lengths[i] / 2.0f;
        }

        float factor = 1;

        for (int frame = 0; frame < framesPerHalfCycle; frame++) {
          factor = frame;
          BranchGroup molecule = new BranchGroup();
          UserData.setUserData(molecule, UserData.NODE_NAME, String.format("%03d_FRAME", (frame + 1)));
          //molecule.setName(String.format("Frame_%04d", frame));

          for (int i = 0; i < referenceMolecule.getNumberOfAtoms(); i++) {
            AtomInterface atom = referenceMolecule.getAtomInterface(i);
            Object obj = atom.getProperty(Java3dUniverse.ATOM_NODE);
            if (obj == null) {
              continue;
            }
            AtomNode at = (AtomNode) obj;
            AtomNode new_atom = null;
            try {
              new_atom = (AtomNode) at.clone();
            } catch (Exception ex) {
            }

            if (new_atom == null) {
              continue;
            }

            pos[i][0] = origins[i][0] + dirs[i][0] * steps[i] * factor;
            pos[i][1] = origins[i][1] + dirs[i][1] * steps[i] * factor;
            pos[i][2] = origins[i][2] + dirs[i][2] * steps[i] * factor;

            new_atom.setAtomicCoordinates(pos[i]);
            UserData.setUserData(new_atom, UserData.NODE_NAME,
                    String.format("%03d_FRAME_Atom_%s", (frame + 1),
                            ChemicalElements.getElementSymbol(atom.getAtomicNumber())));
            molecule.addChild(new_atom);
          }

          for (int i = 0; i < referenceMolecule.getNumberOfBonds(); i++) {
            BondInterface b = referenceMolecule.getBondInterface(i);
            Object obj = b.getProperty(Java3dUniverse.BOND_NODE);
            if (obj == null) {
              continue;
            }
            BondNode bn = (BondNode) obj;
            BondNode new_bn = null;
            try {
              new_bn = (BondNode) bn.clone();
            } catch (Exception ex) {
              continue;
            }
            AtomInterface a1 = b.getIAtomInterface();
            AtomInterface a2 = b.getJAtomInterface();
            int ind_1 = referenceMolecule.getAtomIndex(a1);
            int ind_2 = referenceMolecule.getAtomIndex(a2);

            new_bn.updateBond(pos[ind_1], pos[ind_2]);
            new_bn.setNodeName(String.format("%03d_FRAME_Bond_%s_%s", (frame + 1),
                    ChemicalElements.getElementSymbol(a1.getAtomicNumber()),
                    ChemicalElements.getElementSymbol(a2.getAtomicNumber())));

            molecule.addChild(new_bn);
          }

          objSwitch.addChild(molecule);

        }

        swiInt.setLastChildIndex(objSwitch.numChildren() - 1); // since switch made after interpolator

        if (dvObjects[frequency] == null) {
          dvObjects[frequency] = new DVObject();
        }

        if (dvObjects[frequency].animationBranch != null) {
          addBranchGroupToNode(moleculeParentNode, dvObjects[frequency].animationBranch, false);
        }

        dvObjects[frequency].animationBranch = rootBranch;
        dvObjects[frequency].alpha = alpha;
        dvObjects[frequency].renderingChanged = false;

      }

      //j3d.addGraphicsToMolecule(dvObjects[frequency].animationBranch, true);
      if (moleculeParentNode != null) {
        addBranchGroupToNode(moleculeParentNode, dvObjects[frequency].animationBranch, true);
        addBranchGroupToNode(moleculeParentNode, moleculeBranchGroup, false);
      }
      animationInProgress = true;
    } else {
      animationInProgress = false;
      if (dvObjects[frequency].animationBranch != null) {
        //j3d.addGraphicsToMolecule(dvObjects[frequency].animationBranch, false);
        if (moleculeParentNode != null) {
          addBranchGroupToNode(moleculeParentNode, moleculeBranchGroup, true);
          addBranchGroupToNode(moleculeParentNode, dvObjects[frequency].animationBranch, false);
        }

      }
    }

  }

  @Override
  public void renderingChanged(RenderingObject e) {
    if (!animationInProgress || currentFrequency == -1) {
      return;
    }

    for (int i = 0; i < dvObjects.length; i++) {
      if (dvObjects[i] == null) {
        continue;
      }
      dvObjects[i].renderingChanged = true;
    }

    animateVibrations(currentFrequency, true);

  }

  public void generatePovrayImageSequence(int frequency) {
    float[][] vectors = vibProvider.getDisplacementVectors(frequency);
    if (vectors == null) {
      System.err.println("animateFrequency: vectors == null");
      return;
    }
    if (vectors.length != referenceMolecule.getNumberOfAtoms()) {
      System.err.println("animateFrequency: vectors.length != referenceMolecule.getNumberOfAtoms()");
      return;
    }

    framesPerHalfCycle = 30;

    // --- Generating animation control file
    String name = "vib-animation-cntrl.pov";
    name = (String) JOptionPane.showInputDialog(null, "Animation control file name?", "Animation control file name input",
            JOptionPane.QUESTION_MESSAGE, null, new Object[]{name}, name );
    if (name == null || name.trim().length() < 1) {
      return;
    }
    if (!name.endsWith(".pov") && !name.endsWith(".POV")) {
      name += ".pov";
    }

    Object obj = JOptionPane.showInputDialog(new Frame(),"Number of repeats", "", JOptionPane.INFORMATION_MESSAGE,
            null, null, "1");
    if (obj == null) {
      return;
    }
    int repeat = 1;
    try {
      repeat = Integer.parseInt(obj.toString());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Wrong number "
              + obj.toString() + " : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int n = JOptionPane.showConfirmDialog(null, "Would you like to rebond for each step?", "Yes - to rebond each time",
            JOptionPane.YES_NO_OPTION);
    boolean recalculate = true;
      recalculate = n == JOptionPane.YES_OPTION;

    String povTemplate = GlobalSettings.getProperty(Povray.IMAGE_SEQ_POV_TEMPLATE_KEY,
            Povray.DEFAULT_IMAGE_SEQ_POV_TEMPLATE);
    System.out.println("Povray Pov template:\n" + povTemplate);
    povTemplate = povTemplate.replaceAll(Povray.FILE_PREFIX_TEMPLATE, DEFAULT_FILE_PREFIX);
    try {
      IOUtils.saveStringIntoFile(povTemplate, DEFAULT_FILE_PREFIX + Povray.DEFAULT_IMAGE_SEQ_POV_FILENAME);
    } catch (Exception ex) {
      Logger.getLogger(ImageSequenceManager.class.getName()).log(Level.SEVERE, null, ex);
    }

    // --- Generate Povray initemplate
    String iniTemplate = GlobalSettings.getProperty(
            Povray.IMAGE_SEQ_INI_TEMPLATE_KEY,
            Povray.DEFAULT_IMAGE_SEQ_INI_TEMPLATE);
    System.out.println("Povray INI template:\n" + iniTemplate);
    iniTemplate = iniTemplate.replaceAll(Povray.NUMBER_OF_FRAMES_TEMPLATE, String.valueOf(framesPerHalfCycle * repeat)).
            replaceAll(Povray.POVRAY_CONTROL_FILE_NAME_TEMPLATE, DEFAULT_FILE_PREFIX + Povray.DEFAULT_IMAGE_SEQ_POV_FILENAME);

    try {
      IOUtils.saveStringIntoFile(iniTemplate, DEFAULT_FILE_PREFIX + Povray.DEFAULT_IMAGE_SEQ_INI_FILENAME);
    } catch (Exception ex) {
      Logger.getLogger(ImageSequenceManager.class.getName()).log(Level.SEVERE, null, ex);
    }

    /*
     FileOutputStream out;
     try {
     out = new FileOutputStream(name);
     out.write("// --- File is generated by Jamberoo\n".getBytes());
     out.write("#declare _DEBUG_          = true;\n".getBytes());
     out.write("#declare AnimationLength        = 20.0;\n".getBytes());
     out.write("#declare AnimationClock = clock * AnimationLength;\n".getBytes());
     out.write("#declare file = concat(\"vib-\",str(frame_number,-6,0),\".pov\")\n".getBytes());
     out.write("#ifdef (_DEBUG_)\n".getBytes());
     out.write("#debug \"\\n\"\n".getBytes());
     out.write("#debug concat(\"clock          = \", str(clock,7,3),\" Frame number: \",str(frame_number,0,0) , \"\\n\")\n".getBytes());
     out.write("#debug concat(\"AnimationClock = \", str(AnimationClock,7,3), \"\\n\")\n".getBytes());
     out.write("#debug concat(\"Rendering \", file, \"\\n\")\n".getBytes());
     out.write("#end\n".getBytes());
     out.write("#include file\n".getBytes());
     } catch (Exception ex) {
     return;
     }
     */
    currentFrequency = frequency;
    List<MoleculeInterface> mols = new ArrayList<MoleculeInterface>();

    float[] lengths = new float[referenceMolecule.getNumberOfAtoms()];
    float[] steps = new float[referenceMolecule.getNumberOfAtoms()];
    float[][] origins = new float[referenceMolecule.getNumberOfAtoms()][3];
    float[][] dirs = new float[referenceMolecule.getNumberOfAtoms()][3];
    double[][] pos = new double[referenceMolecule.getNumberOfAtoms()][3];

    for (int i = 0; i < referenceMolecule.getNumberOfAtoms(); i++) {

      AtomInterface atom = referenceMolecule.getAtomInterface(i);
      lengths[i] = (float) Math.sqrt(vectors[i][0] * vectors[i][0] + vectors[i][1] * vectors[i][1]
              + vectors[i][2] * vectors[i][2]);
      if (lengths[i] > 0.001) {
        dirs[i][0] = vectors[i][0] / lengths[i];
        dirs[i][1] = vectors[i][1] / lengths[i];
        dirs[i][2] = vectors[i][2] / lengths[i];
      }

      lengths[i] *= scale_length;
      steps[i] = lengths[i] / (framesPerHalfCycle - 1);

      origins[i][0] = atom.getX() - dirs[i][0] * lengths[i] / 2.0f;
      origins[i][1] = atom.getY() - dirs[i][1] * lengths[i] / 2.0f;
      origins[i][2] = atom.getZ() - dirs[i][2] * lengths[i] / 2.0f;
    }

    float factor = 1;

    for (int frame = 0; frame < framesPerHalfCycle; frame++) {
      factor = frame;

      MoleculeInterface mol = referenceMolecule.getInstance();
      mol.appendMolecule(referenceMolecule);

      for (int i = 0; i < mol.getNumberOfAtoms(); i++) {
        AtomInterface atom = mol.getAtomInterface(i);

        pos[i][0] = origins[i][0] + dirs[i][0] * steps[i] * factor;
        pos[i][1] = origins[i][1] + dirs[i][1] * steps[i] * factor;
        pos[i][2] = origins[i][2] + dirs[i][2] * steps[i] * factor;

        atom.setXYZ(pos[i][0], pos[i][1], pos[i][2]);
      }

      if (recalculate) {
        Molecule.redoConnectivity(mol);
      }
      mols.add(mol);
      j3d.setMolecule(mol);  
      
      PovrayJava3d povray = new PovrayJava3d(j3d);
      try {
        povray.savePovrayFile(String.format("vib-%06d", frame) + ".pov");
      } catch (Exception ex) {
        System.err.println(ex.getLocalizedMessage());
        return;
      }

    }

    // ---
    int pointer = framesPerHalfCycle - 1;
    int sign = -1;
    for (int frame = framesPerHalfCycle; frame < framesPerHalfCycle * repeat; frame++) {
      if (pointer == framesPerHalfCycle - 1) {
        sign = -1;
      } else if (pointer == 0) {
        sign = 1;
      }
      pointer += sign;

      MoleculeInterface mol = mols.get(pointer);
      j3d.setMolecule(mol);
      PovrayJava3d povray = new PovrayJava3d(j3d);
      try {
        povray.savePovrayFile(String.format("vib-%06d", frame) + ".pov");
      } catch (Exception ex) {
        System.err.println(ex.getLocalizedMessage());
        return;
      }
    }

    j3d.setMolecule(this.referenceMolecule);
  }
  class DVObject {

    BranchGroup rootBranch;
    BranchGroup animationBranch;
    Alpha alpha;
    long halfCycleDuration;
    boolean renderingChanged = false;

    public DVObject() {
    }
  }
}
