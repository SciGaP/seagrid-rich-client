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

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.View;
import org.scijava.java3d.WakeupCriterion;
import org.scijava.java3d.WakeupOnAWTEvent;
import org.scijava.vecmath.Matrix3d;

import org.scijava.java3d.utils.behaviors.mouse.MouseBehaviorCallback;
import org.scijava.java3d.utils.behaviors.mouse.MouseZoom;

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MyMouseZoom
    extends MouseZoom {

   //private SampleFrame daddy = null;
   private Java3dUniverse dad = null;
   protected Transform3D inverseTrans = null;
   protected Matrix3d rotation = null;

   private MouseBehaviorCallback callback = null;

   /**
    * Creates a rotate behavior given the transform group.
    * @param transformGroup The transformGroup to operate on.
    */
   public MyMouseZoom(TransformGroup transformGroup) {
      super(transformGroup);
   }

   /**
    * Creates a default mouse rotate behavior.
    **/
   public MyMouseZoom() {
      super();
   }

   /**
    * Creates a rotate behavior.
    * Note that this behavior still needs a transform
    * group to work on (use setTransformGroup(tg)) and
    * the transform group must add this behavior.
    * @param flags interesting flags (wakeup conditions).
    */
   public MyMouseZoom(int flags) {
      super(flags);
   }

   @Override
  public void initialize() {
      super.initialize();
   }

   /**
    * Set the x-axis amd y-axis movement multipler with factor.
    **/

   @Override
  public void setFactor(double factor) {
      super.setFactor(factor);

   }

   @Override
  public void processStimulus(Enumeration criteria) {

      if ( getView().getProjectionPolicy() == View.PARALLEL_PROJECTION) {
         WakeupCriterion wakeup;
         AWTEvent[] event;
         int id;
         int dx, dy;

         while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
               event = ( (WakeupOnAWTEvent) wakeup).getAWTEvent();
               for (int i = 0; i < event.length; i++) {
                  processMouseEvent( (MouseEvent) event[i]);

                  if ( ( (buttonPress) && ( (flags & MANUAL_WAKEUP) == 0)) ||
                      ( (wakeUp) && ( (flags & MANUAL_WAKEUP) != 0))) {
                     id = event[i].getID();
                     if ( (id == MouseEvent.MOUSE_DRAGGED) &&
                         ( (MouseEvent) event[i]).isAltDown() &&
                         ! ( (MouseEvent) event[i]).isMetaDown()) {

                        x = ( (MouseEvent) event[i]).getX();
                        y = ( (MouseEvent) event[i]).getY();

                        dx = x - x_last;
                        dy = y - y_last;

                        double scale = getView().getScreenScale();
                        scale += dy * getFactor()*0.01;
                        if (scale < 0.0001) {
                           scale = 0.0001;
                        }
                        getView().setScreenScale(scale);

                        if (!reset) {
                           //transformGroup.getTransform(currXform);

                           /*
                                                       translation.z = dy * getFactor();

                                                       transformX.set(translation);

                                                       if (invert) {
                              currXform.mul(currXform, transformX);
                                                       }
                                                       else {
                              currXform.mul(transformX, currXform);
                                                       }

                                                       transformGroup.setTransform(currXform);

                                                       transformChanged(currXform);

                                                       if (callback != null) {
                              callback.transformChanged(MouseBehaviorCallback.TRANSLATE,
                                  currXform);
                                                       }
                            */
                        }
                        else {
                           reset = false;
                        }

                        x_last = x;
                        y_last = y;
                     }
                     else if (id == MouseEvent.MOUSE_PRESSED) {
                        x_last = ( (MouseEvent) event[i]).getX();
                        y_last = ( (MouseEvent) event[i]).getY();
                     }
                  }
               }
            }
         }

         wakeupOn(mouseCriterion);

      }
      else {
         super.processStimulus(criteria);
      }
      //logger.info("processStimulus: labels; "+ daddy.areLabels());
   }

   /**
    * Users can overload this method  which is called every time
    * the Behavior updates the transform
    *
    * Default implementation does nothing
    */
   @Override
  public void transformChanged(Transform3D transform) {

   }

   /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
   @Override
  public void setupCallback(MouseBehaviorCallback callback) {
      this.callback = callback;
   }

   // --- And now, custom functions...

   /*
       public MyMouseRotate(SampleFrame parent, TransformGroup transformGroup) {
      super(transformGroup);
      daddy = parent;
       }
    */

   public MyMouseZoom(Java3dUniverse parent, TransformGroup transformGroup) {
      super(transformGroup);
      dad = parent;
   }

   /*
       public void setParentFrame(SampleFrame dad) {
      daddy = dad;
       }
    */

   public void setParentFrame(Java3dUniverse parent) {
      dad = parent;
   }

}
