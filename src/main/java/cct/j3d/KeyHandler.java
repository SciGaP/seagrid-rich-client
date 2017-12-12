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
import java.util.Enumeration;
import java.util.logging.Logger;

import org.scijava.java3d.Behavior;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransformGroup;
import org.scijava.java3d.WakeupOnAWTEvent;

public class KeyHandler
    extends Behavior {

  private TransformGroup targetTG;
  private Transform3D rotation = new Transform3D();
  private Transform3D rotationX = new Transform3D();
  private Transform3D rotationY = new Transform3D();
  private double angleX = 0.0;
  private double angleY = 0.0;
  private WakeupOnAWTEvent KeyEvent = new WakeupOnAWTEvent(java.awt.event.KeyEvent.KEY_PRESSED);
  static final Logger logger = Logger.getLogger(KeyHandler.class.getCanonicalName());

  // create SimpleBehavior
  KeyHandler(TransformGroup targetTG) {
    this.targetTG = targetTG;
  }

  // initialize the Behavior
  //     set initial wakeup condition
  //     called when behavior beacomes live
  @Override
  public void initialize() {
    // set initial wakeup condition
    this.wakeupOn(KeyEvent);
  }

  // behave
  // called by Java 3D when appropriate stimulus occures
  @Override
  public void processStimulus(Enumeration criteria) {
    if (KeyEvent.hasTriggered()) {
      AWTEvent events[] = KeyEvent.getAWTEvent();
      //java.awt.event.KeyEvent events[] = KeyEvent.getAWTEvent();

      for (int i = 0; i < events.length; i++) {
        String params = events[i].toString();
        //logger.info("KeyHandler: Id:" + events[i].getID() +
        //                   "  Param: " + params );
        // decode event

        targetTG.getTransform(rotation);
        rotationX.set(Transform3D.IDENTITY);
        rotationY.set(Transform3D.IDENTITY);
        //if ( params.matches("keyCode=38")) {
        if (params.indexOf("keyCode=38") != -1) {
          // do what is necessary
          angleX = -0.1;
          rotationX.rotX(angleX);
          //rotationY.rotY(angleY);
          //rotation.mul( rotationY );
          //rotation.mul( rotationX );
          rotationX.mul(rotation);
          //targetTG.setTransform(rotation);
          targetTG.setTransform(rotationX);
          logger.info("Rotating...");
        }
        else if (params.indexOf("keyCode=40") != -1) {
          // do what is necessary
          angleX = 0.1;
          rotationX.rotX(angleX);
          //rotationY.rotY(angleY);
          //rotation.mul( rotationY );
          //rotation.mul( rotationX );
          //targetTG.setTransform(rotation);
          rotationX.mul(rotation);
          targetTG.setTransform(rotationX);

          logger.info("Rotating...");
        }
        else if (params.indexOf("keyCode=39") != -1) {
          // do what is necessary
          angleY = 0.1;
          //rotationX.rotX(angleX);
          rotationY.rotY(angleY);
          //rotation.mul( rotationY );
          rotationY.mul(rotation);
          //rotation.mul( rotationX );
          targetTG.setTransform(rotationY);

          logger.info("Rotating...");
        }
        else if (params.indexOf("keyCode=37") != -1) {
          // do what is necessary
          angleY = -0.1;
          //rotationX.rotX(angleX);
          rotationY.rotY(angleY);
          //rotation.mul( rotationX );
          //rotation.mul( rotationY );
          //targetTG.setTransform(rotation);
          rotationY.mul(rotation);
          targetTG.setTransform(rotationY);
          /*
                               rotationX.rotX(angleX);
                               rotationY.rotY(angleY);
                               rotationY.mul( rotationX );
                               targetTG.setTransform(rotationY);
           */

          logger.info("Rotating...");
        }

      }

    }
    this.wakeupOn(KeyEvent);
  }

} // end of class SimpleBehavior
