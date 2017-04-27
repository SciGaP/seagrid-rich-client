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

import java.util.logging.Logger;

import org.scijava.java3d.Bounds;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.Node;
import org.scijava.java3d.PickBounds;
import org.scijava.java3d.PickConeRay;
import org.scijava.java3d.PickCylinder;
import org.scijava.java3d.PickPoint;
import org.scijava.java3d.PickRay;
import org.scijava.java3d.PickSegment;
import org.scijava.java3d.PickShape;
import org.scijava.java3d.SceneGraphPath;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.Point3d;
import org.scijava.vecmath.Vector3d;

import cct.modelling.OperationsOnAtoms;

import org.scijava.java3d.utils.behaviors.mouse.MouseBehaviorCallback;
import org.scijava.java3d.utils.geometry.Cylinder;
import org.scijava.java3d.utils.geometry.Sphere;
import org.scijava.java3d.utils.picking.PickResult;
import org.scijava.java3d.utils.picking.behaviors.PickMouseBehavior;
import org.scijava.java3d.utils.picking.behaviors.PickingCallback;

/**
 *	PickRotateBehavior behavior = new PickRotateBehavior(canvas, root, bounds);
 *      root.addChild(behavior);
 */

public class MousePicking
    extends PickMouseBehavior implements
    MouseBehaviorCallback, OperationsOnAtoms {
   //MouseRotate drag;
   //int pickMode = PickObject.USE_BOUNDS;
   private PickingCallback callback = null;
   private TransformGroup currentTG;
   //private SampleFrame daddy = null;
   private Java3dUniverse parent = null;
   static final Logger logger = Logger.getLogger(MousePicking.class.getCanonicalName());

   /**
    * Creates a pick/rotate behavior that waits for user mouse events for
    * the scene graph. This method has its pickMode set to BOUNDS picking.
    * @param root   Root of your scene graph.
    * @param canvas Java 3D drawing canvas.
    * @param bounds Bounds of your scene.
    **/
   /*
       public MousePicking(SampleFrame dad, BranchGroup root, Canvas3D canvas,
                       Bounds bounds) {
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
      daddy = dad;
       }
    */

   public MousePicking(Java3dUniverse dad, BranchGroup root, Canvas3D canvas,
                       Bounds bounds) {
      super(canvas, root, bounds);
      //drag = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
      //drag.setTransformGroup(currGrp);
      //currGrp.addChild(drag);
      //drag.setSchedulingBounds(bounds);
      this.setSchedulingBounds(bounds);
      parent = dad;
   }

   /**
    * Creates a pick/rotate behavior that waits for user mouse events for
    * the scene graph.
    * @param root   Root of your scene graph.
    * @param canvas Java 3D drawing canvas.
    * @param bounds Bounds of your scene.
    * @param pickMode specifys PickObject.USE_BOUNDS or PickObject.USE_GEOMETRY.
    * Note: If pickMode is set to PickObject.USE_GEOMETRY, all geometry object in
    * the scene graph that allows pickable must have its ALLOW_INTERSECT bit set.
    **/
   /*
       public MousePicking(SampleFrame dad, BranchGroup root, Canvas3D canvas,
                       Bounds bounds,
                       int pickMode) {
      super(canvas, root, bounds);
      //drag = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
      //drag.setTransformGroup(currGrp);
      //currGrp.addChild(drag);
      //drag.setSchedulingBounds(bounds);
      this.setSchedulingBounds(bounds);
      this.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
      Node node = this;
      node.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);
      //this.pickMode = pickMode;
      daddy = dad;
       }
    */

   /**
    * Sets the pickMode component of this PickRotateBehavior to the value of
    * the passed pickMode.
    * @param pickMode the pickMode to be copied.
    **/


   public void setPickMode(int pickMode) {
      //this.pickMode = pickMode;
      super.setMode(pickMode);
   }

   @Override
  public void setTolerance(float tolerance) {
      super.setTolerance(tolerance);
   }

   /**
    * Return the pickMode component of this PickRotateBehavior.
    **/

   public int getPickMode() {
      //return pickMode;
      return getMode();
   }

   /**
    * Update the scene to manipulate any nodes. This is not meant to be
    * called by users. Behavior automatically calls this. You can call
    * this only if you know what you are doing.
    *
    * @param xpos Current mouse X pos.
    * @param ypos Current mouse Y pos.
    **/
   @Override
  public void updateScene(int xpos, int ypos) {

      //if ( !daddy.selectionInProgress ) return;

      TransformGroup tg = null;
      logger.info("MousePicking: Enter updateScene");
      if (!mevent.isMetaDown() && !mevent.isAltDown()) {

         //Transform3D t3d = new Transform3D();

         //canvas.getImagePlateToVworld(t3d);
         //logger.info("ImagePlateToVworld: " + t3d );

         //this.getLocalToVworld(t3d);
         //logger.info("This: " + t3d );

         pickCanvas.setShapeLocation(mevent);

         PickShape shape = pickCanvas.getPickShape();

         Point3d eyePos = pickCanvas.getStartPosition();
         //logger.info("Screen: "+xpos+" "+ypos+" Eye Pos: " + eyePos);
         Point3d origin = null;
         Vector3d dir = null;

         if (shape instanceof PickBounds) {
            logger.info("PickShape: PickBounds");
         }
         else if (shape instanceof PickConeRay) {
            logger.info("PickShape: PickConeRay");
            origin = new Point3d();
            //origin = eyePos;
            PickConeRay pc = (PickConeRay) shape;

            pc.getOrigin(origin);
            logger.info("Origin: " + origin.x + " " + origin.y + " " +
                               origin.z);
            dir = new Vector3d();
            pc.getDirection(dir);
            logger.info("Direction: " + dir.x + " " + dir.y + " " +
                               dir.z + " Spread angle: " +
                               pc.getSpreadAngle() / Math.PI * 180.0);

            pc.set(origin, dir, pc.getSpreadAngle() / 5.0);

            pickCanvas.setShapeRay(origin, dir);
         }
         else if (shape instanceof PickCylinder) {
            logger.info("PickShape: PickCylinder");
         }
         else if (shape instanceof PickPoint) {
            logger.info("PickShape: PickPoint");
         }
         else if (shape instanceof PickRay) {
            logger.info("PickShape: PickRay");
            PickRay pc = (PickRay) shape;
            origin = new Point3d();
            dir = new Vector3d();
            pc.get(origin, dir);
         }
         else if (shape instanceof PickSegment) {
            logger.info("PickShape: PickSegment");
         }
         else {
            origin = new Point3d();
            Canvas3D canvas = pickCanvas.getCanvas();
            Point3d mouse_pos = new Point3d();

            canvas.getCenterEyeInImagePlate(origin);
            canvas.getPixelLocationInImagePlate(mevent.getX(), mevent.getY(),
                                                mouse_pos);

            Transform3D motion = new Transform3D();
            canvas.getImagePlateToVworld(motion);
            motion.transform(origin);
            motion.transform(mouse_pos);

            dir = new Vector3d(mouse_pos);
            dir.sub(origin);
         }

         //currGrp.getTransform(t3d);
         //daddy.handleSpotPicking(origin,dir);

         BranchGroup bgr = pickCanvas.getBranchGroup();
         PickResult[] results = pickCanvas.pickAll();
         if (results != null) {
            logger.info("# of picked objects: " + results.length);
         }
         PickResult result = pickCanvas.pickClosest();

         Sphere sel_sphere = null;

         if (results != null) {
            //logger.info("Array size: " + results.length);
            //PickIntersection pi = result.getClosestIntersection(eyePos);
            //Transform3D tr = result.getLocalToVworld();
            //if ( tr != null )
            //    logger.info("Loc->VW: " + tr);
            //if ( pi != null ) {
            //    Point3d intercept = pi.getPointCoordinatesVW();
            //    logger.info("VW intercept: " + intercept);
            //}
         }

         // --- Call came from Frame (obsolete)
         if (false) {

         }
         /*
                   if (daddy != null) {
          if (daddy.processingSelected == SELECTED_ADD_ATOMS && result != null) {
               daddy.doAtomSelection(bgr, result);
            }
          else if (daddy.processingSelected == SELECTED_ADD_ATOMS && result == null &&
                     mevent.isShiftDown()) {
               //daddy.doAtomSelection(bgr, result);
               daddy.handleSpotPicking(origin, dir);
            }

            else if (mevent.isShiftDown() &&
                     daddy.selectedMode == SELECTION_SPOT_ONLY) { // dead code
               daddy.handleSpotPicking(origin, dir);
            }
            else if (daddy.selectedMode == SELECTION_UNLIMITED ||
                     daddy.selectedMode == SELECTION_ONE_ATOM_ONLY) {
               daddy.doAtomSelection(bgr, result);
            }
                   }
          */

         // --- If call came from class
         else {
            if (parent.getJobType() == SELECTED_ADD_ATOMS && result != null) {
               //parent.doAtomSelection(bgr, result);
               parent.doAtomSelection(origin, dir, bgr, results);
            }
            else if (parent.getJobType() == SELECTED_ADD_ATOMS && result == null &&
                     mevent.isShiftDown()) {
               //daddy.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
            }
            else if (parent.getJobType() == SELECTED_ADD_MOLECULE && result == null &&
                     mevent.isShiftDown()) {
               //daddy.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
            }

            else {
               //parent.doAtomSelection(bgr, result);
               if (dir != null) {
                  parent.doAtomSelection(origin, dir, bgr, results);
               }
            }

            /*
             else if (parent.getJobType() == SELECTED_MODIFY_ATOMS && result != null ) {
               parent.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
                         }
             else if (parent.getJobType() == SELECTED_MODIFY_BONDS && result != null ) {
               parent.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
                         }
             else if (parent.getJobType() == SELECTED_MODIFY_ANGLES && result != null ) {
               parent.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
                         }

             else if (parent.getJobType() == SELECTED_MODIFY_DIHEDRALS && result != null ) {
               parent.doAtomSelection(bgr, result);
               parent.handleSpotPicking(origin, dir);
                         }
             */


            /*
                         else if (mevent.isShiftDown() &&
                     parent.selectedMode == SELECTION_SPOT_ONLY) { // dead code
               parent.handleSpotPicking(origin, dir);
                         }
                         else if (parent.selectedMode == SELECTION_UNLIMITED ||
                     parent.selectedMode == SELECTION_ONE_ATOM_ONLY) {
               parent.doAtomSelection(bgr, result);
                         }
             */

         }
         //bgr.setCapability( BranchGroup.ALLOW_CHILDREN_READ);

         // Make sure the selection exists and is movable.
         if ( (tg != null) &&
             (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ)) &&
             (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))) {
            //drag.setTransformGroup(tg);
            //drag.wakeup();
            currentTG = tg;
         }
         else if (callback != null) {
            callback.transformChanged(PickingCallback.NO_PICK, null);
         }
      }
   }

   /**
    * Callback method from MouseRotate
    * This is used when the Picking callback is enabled
    */
   @Override
  public void transformChanged(int type, Transform3D transform) {
      callback.transformChanged(PickingCallback.ROTATE, currentTG);
   }

   /**
    * Register the class @param callback to be called each
    * time the picked object moves
    */
   /*
      public void setupCallback( PickingCallback callback ) {
      this.callback = callback;
      if (callback==null)
          //drag.setupCallback( null );
      else
          //drag.setupCallback( this );
      }
    */
   public int getSelectedAtomNumber(BranchGroup bgr, Sphere sel_sphere) {
      int selected_atom = -1;
      if (bgr != null) {
         PickShape shape = pickCanvas.getPickShape();
         SceneGraphPath sgp = bgr.pickClosest(shape);
         //logger.info("Got Branch group: # children " +
         //                   bgr.numChildren());
         for (int i = 0; i < bgr.numChildren(); i++) {
            Node n = bgr.getChild(i);
            if (n instanceof BranchGroup) {
               //logger.info("Child: " + i + " :BranchGroup");
               BranchGroup bg = (BranchGroup) n;
               for (int j = 0; j < bg.numChildren(); j++) {
                  Node n2 = bg.getChild(j);
                  if (n2 instanceof TransformGroup) {
                     TransformGroup tg2 = (TransformGroup) n2;
                     //logger.info("SubChild: " + j +
                     //                   " :TransformGroup");
                     for (int k = 0; k < tg2.numChildren(); k++) {
                        Node n3 = tg2.getChild(k);
                        if (n3 instanceof Sphere) {
                           //logger.info("SubSubChild: " + k +
                           //        " :Sphere");
                           if (sel_sphere == n3) {
                              //logger.info(
                              //        ">>> Selected Sphere <<<");
                              return i;
                           }
                        }
                        else if (n3 instanceof Cylinder) {
                           //logger.info("SubSubChild: " + k +
                           //        " :Cylinder");
                        }
                        else {
                           //logger.info("SubSubChild: " + k +
                           //        " :Unknown");
                        }

                     }
                  }
                  else {
                     //logger.info("SubChild: " + j + " :Unknown");
                  }
               }
            }
            else {
               //logger.info("Child: " + i + " :Unknown");
            }
         }
      }

      return selected_atom;
   }
}
