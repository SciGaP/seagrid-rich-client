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

import cct.tools.ImageTools;
import java.awt.Frame;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

/**
 *
 * @author vvv900
 */
public class ImageToolsJ3D {

  Java3dUniverse java3d;
  String imageType = "jpg";

  public ImageToolsJ3D(Java3dUniverse java3d) {
    this.java3d = java3d;
  }

  public Java3dUniverse getJava3d() {
    return java3d;
  }

  public void setJava3d(Java3dUniverse java3d) {
    this.java3d = java3d;
  }

  public String getImageType() {
    return imageType;
  }

  public void setImageType(String imageType) {
    this.imageType = imageType.toLowerCase();
  }

  public void saveImage(String fileName) throws Exception {
    if (fileName == null || fileName.trim().length() < 1) {
      throw new Exception("File name is not set");
    }
    if (java3d == null) {
      throw new Exception("Java3dUniverse is not set");
    }
    BufferedImage bImage = java3d.getImageCapture();
    MediaTracker mediaTracker = new MediaTracker(new Frame());
    mediaTracker.addImage(bImage, 0);
    try {
      mediaTracker.waitForID(0);
      ImageTools.saveImage(bImage, imageType, fileName);
    } catch (Exception ex) {
      throw new Exception("Error Saving Image: " + fileName + " : " + ex.getMessage());
    }
  }
}
