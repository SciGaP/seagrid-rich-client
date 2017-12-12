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

import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.WakeupOnAWTEvent;

import org.scijava.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;

// Uncomplete WRONG class (to delete)

public class myKeyNavigatorBehavior
    extends KeyNavigatorBehavior {
  private TransformGroup targetTG;
  private WakeupOnAWTEvent KeyEvent = new WakeupOnAWTEvent(java.awt.event.KeyEvent.KEY_PRESSED);
  java.awt.event.KeyEvent evt;
  private Transform3D rotation = new Transform3D();
  private double angle = 0.0;
  static final Logger logger = Logger.getLogger(myKeyNavigatorBehavior.class.getCanonicalName());

  public myKeyNavigatorBehavior(TransformGroup targetTG) {
    super(targetTG);
    this.targetTG = targetTG;
  }

  public myKeyNavigatorBehavior(java.awt.Component c, TransformGroup targetTG) {
    super(c, targetTG);
    this.targetTG = targetTG;
  }

  @Override
  public void initialize() {
    // set initial wakeup condition
    this.wakeupOn(KeyEvent);

  }

  @Override
  public void processStimulus(java.util.Enumeration criteria) {
    keyPressed(evt);
    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_KP_UP) {
      angle += 0.1;
      rotation.rotY(angle);
      targetTG.setTransform(rotation);

    }
    logger.info("KeyHandler: Id" + evt.getID() + "  Param: " +
                       evt.toString());
    this.wakeupOn(KeyEvent);
  }
}
