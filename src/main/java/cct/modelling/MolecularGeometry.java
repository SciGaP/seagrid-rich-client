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
package cct.modelling;

import java.util.ArrayList;
import java.util.logging.Logger;

import cct.vecmath.Point3f;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004</p>
 *
 * <p>
 * Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MolecularGeometry
    extends ArrayList<Point3f> {

  String Name = "Geometry";
  String Remark = null;
  static final Logger logger = Logger.getLogger(MolecularGeometry.class.getCanonicalName());

  public MolecularGeometry() {
  }

  public MolecularGeometry(float[][] coords) {
    if (coords == null || coords.length < 1) {
      return;
    }
    this.ensureCapacity(coords.length);
    for (int i = 0; i < coords.length; i++) {
      this.add(new Point3f(coords[i][0], coords[i][1], coords[i][2]));
    }
  }

  public boolean addCoordinates(Point3f coord) {
    return add(coord);
  }

  public void addRemark(String rem) {
    Remark.concat(rem);
  }

  public Point3f getCoordinates(int n) {
    if (n < 0 || n >= size()) {
      logger.info("getCoordinates: n < 0 || n>= size()");
      return null;
    }
    return get(n);
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }
}
