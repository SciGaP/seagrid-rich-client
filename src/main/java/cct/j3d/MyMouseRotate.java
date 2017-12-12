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

import java.util.Enumeration;

import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.vecmath.Matrix3d;

import org.scijava.java3d.utils.behaviors.mouse.MouseBehaviorCallback;
import org.scijava.java3d.utils.behaviors.mouse.MouseRotate;

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
public class MyMouseRotate
    extends MouseRotate {

   //private SampleFrame daddy = null;
   private Java3dUniverse dad = null;
   protected Transform3D inverseTrans = null;
   protected Matrix3d rotation = null;

   private MouseBehaviorCallback callback = null;

   /**
    * Creates a rotate behavior given the transform group.
    * @param transformGroup The transformGroup to operate on.
    */
   public MyMouseRotate(TransformGroup transformGroup) {
      super(transformGroup);
   }

   /**
    * Creates a default mouse rotate behavior.
    **/
   public MyMouseRotate() {
      super();
   }

   /**
    * Creates a rotate behavior.
    * Note that this behavior still needs a transform
    * group to work on (use setTransformGroup(tg)) and
    * the transform group must add this behavior.
    * @param flags interesting flags (wakeup conditions).
    */
   public MyMouseRotate(int flags) {
      super(flags);
   }

   @Override
  public void initialize() {
      super.initialize();
   }

   /**
    * Return the x-axis movement multipler.
    **/

   @Override
  public double getXFactor() {
      return super.getXFactor();
   }

   /**
    * Return the y-axis movement multipler.
    **/

   @Override
  public double getYFactor() {
      return super.getYFactor();
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

      super.processStimulus(criteria);

      //logger.info("processStimulus: labels; "+ daddy.areLabels());

      /*
             if (daddy != null && daddy.areLabels()) {
         daddy.updateLabels();
             }
             else if (dad != null && dad.areLabels()) {
         dad.updateLabels();
             }
       */

      if (dad != null && dad.areLabels()) {
         dad.updateLabels();
         //this.notifyAll();
      }

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

   public MyMouseRotate(Java3dUniverse parent, TransformGroup transformGroup) {
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
