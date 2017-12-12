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
import java.util.List;

import org.scijava.java3d.Appearance;
import org.scijava.java3d.Canvas3D;
import org.scijava.java3d.Group;
import org.scijava.java3d.Shape3D;
import org.scijava.java3d.Transform3D;
import org.scijava.java3d.TransparencyAttributes;
import org.scijava.java3d.TriangleArray;
import org.scijava.java3d.View;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Point3d;

import cct.gaussian.Gaussian;
import cct.interfaces.GraphicsObjectInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.Molecule;
import cct.povray.Povray;
import cct.vecmath.MeshObject;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PovrayJava3d
    extends Povray {

  public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

  Canvas3D canvas3D;
  View view;

  public PovrayJava3d(Java3dUniverse j3d) {
    super();

    canvas3D = j3d.getCanvas3D();
    view = canvas3D.getView();
    int pPolicy = view.getProjectionPolicy();

    if (pPolicy == View.PARALLEL_PROJECTION) {
      setProjectionPolicy( PARALLEL_PROJECTION);
    }
    else if (pPolicy == View.PERSPECTIVE_PROJECTION) {
      setProjectionPolicy( PARALLEL_PROJECTION);
    }

    super.setFieldOfView( (float) (view.getFieldOfView() * RADIANS_TO_DEGREES));

    Point3d position = new Point3d();
    canvas3D.getCenterEyeInImagePlate(position);
    Transform3D motion = new Transform3D();
    canvas3D.getImagePlateToVworld(motion);
    motion.transform(position);

    super.setCameraLocation( (float) position.x, (float) position.y,
                            (float) position.z);

    Color3f bc = j3d.getBackgroundColor3f();
    super.setBackgroundColor(bc.x, bc.y, bc.z);

    Transform3D t3d = j3d.getVWTransform();

    float[] matrix = new float[16];
    t3d.get(matrix);

    super.settransformMatrix(matrix);

    super.setMolecule(j3d.getMoleculeInterface());

    List<GraphicsObjectInterface> grObjects = j3d.getGraphicsObjects();

    if (grObjects != null && grObjects.size() > 0) {
      for (int i = 0; i < grObjects.size(); i++) {
        GraphicsObjectInterface object = grObjects.get(i);
        List shape3ds = object.getShape3DElements();
        if (shape3ds == null || shape3ds.size() < 1) {
          continue;
        }

        for (int j = 0; j < shape3ds.size(); j++) {
          Object obj = shape3ds.get(j);

          if (obj instanceof Group) {
            if ( ( (Group) obj).getParent() == null) {
              continue; // Invisible
            }
          }

          Shape3D shape3d = this.getShape3D(obj);

          if (shape3d == null) {
            continue;
          }

          Object geometry = shape3d.getGeometry();
          if (geometry instanceof TriangleArray) {
            TriangleArray triangleArray = (TriangleArray) geometry;

            int n = triangleArray.getVertexCount();

            float[] coordinates = new float[n * 3];
            triangleArray.getCoordinates(0, coordinates);

            float[] normals = new float[n * 3];
            triangleArray.getNormals(0, normals);

            float[] colors = new float[n * 3];
            triangleArray.getColors(0, colors);

            int[] face_indices = new int[n];
            for (int k = 0; k < n; k++) {
              face_indices[k] = k;
            }

            MeshObject mesh = new MeshObject();
            try {
              mesh.setMeshObjectOfTriangles(n, coordinates, face_indices, normals, colors);
            }
            catch (Exception ex) {
              System.err.println(ex.getMessage());
            }

            if (shape3d.getCapability(Shape3D.ALLOW_APPEARANCE_READ)) {
              Appearance app = shape3d.getAppearance();
              if (app.getCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ)) {
                TransparencyAttributes ta = app.getTransparencyAttributes();
                if (ta == null) {
                  mesh.setOpacity(1.0f);
                }
                else {
                  mesh.setTransparency(ta.getTransparency());
                }
              }
              else {
                System.err.println(this.getClass().getCanonicalName() + ": No capability to read TransparencyAttributes...");
              }

            }
            else {
              System.err.println(this.getClass().getCanonicalName() + ": No capability to read Appearance...");
            }

            super.addMeshObject(mesh);
          }
        }
      }
    }
  }

  Shape3D getShape3D(Object obj) {
    if (obj instanceof Shape3D) {
      return (Shape3D) obj;
    }
    else if (obj instanceof Group) {
      Group group = (Group) obj;
      Enumeration children = group.getAllChildren();
      while (children.hasMoreElements()) {
        Object node = children.nextElement();
        if (node instanceof Group) {
          Shape3D shape3d = getShape3D(node);
          if (shape3d != null) {
            return shape3d;
          }
        }
        else if (node instanceof Shape3D) {
          return (Shape3D) node;
        }
      }
    }
    return null;
  }

  public static void main(String[] args) {

    Gaussian gaussianData = new Gaussian();
    int n = gaussianData.parseGJF(args[0], 0);
    MoleculeInterface m = Molecule.getNewInstance();
    m = gaussianData.getMolecule(m, 0);
    Java3dUniverse j3d = new Java3dUniverse();
    j3d.addMolecule(m);

    PovrayJava3d povrayjava3d = new PovrayJava3d(j3d);

    try {
      povrayjava3d.savePovrayFile("povray.pov");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
