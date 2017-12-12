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

import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.scijava.java3d.Bounds;
import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.WakeupCriterion;
import org.scijava.java3d.WakeupOnAWTEvent;
import org.scijava.java3d.WakeupOr;

import org.scijava.java3d.utils.picking.behaviors.PickMouseBehavior;

public class MousePickingHandler
    extends PickMouseBehavior {
   WakeupCriterion[] conditions = {
       new WakeupOnAWTEvent(java.awt.event.
                            MouseEvent.MOUSE_CLICKED),
       new WakeupOnAWTEvent(java.awt.event.
                            MouseEvent.MOUSE_PRESSED),
       new WakeupOnAWTEvent(java.awt.event.
                            MouseEvent.MOUSE_RELEASED)
   };
   WakeupOr MouseEvent = new WakeupOr(conditions);

   static final Logger logger = Logger.getLogger(MousePickingHandler.class.getCanonicalName());

   public MousePickingHandler(Canvas3D canvas, BranchGroup root, Bounds bounds) {
      super(canvas, root, bounds);
   }

   @Override
  public void initialize() {
      // set initial wakeup condition
      wakeupOn(MouseEvent);
   }

   private void processMouseEvent(MouseEvent evt) {
      buttonPress = false;
      logger.info("processMouseEvent: Mouse Clicked");
      if (evt.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED |
          evt.getID() == java.awt.event.MouseEvent.MOUSE_CLICKED) {
         buttonPress = true;
         return;
      }
      else if (evt.getID() == java.awt.event.MouseEvent.MOUSE_MOVED) {
         // Process mouse move event
      }
   }

   @Override
  public void processStimulus(Enumeration criteria) {
      logger.info("MouseHandler: Mouse Clicked");
   }

   @Override
  public void updateScene(int xpos, int ypos) {

   }

}
