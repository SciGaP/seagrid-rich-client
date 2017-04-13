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

import java.util.List;

import cct.interfaces.AtomInterface;
import cct.interfaces.MoleculeInterface;
import cct.vecmath.Point3f;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class TorsionEnergy {
  private TorsionEnergy() {
  }

  public static double TorsionEnergy(MoleculeInterface molec, List torsionPars) {
    if (torsionPars == null) {
      return 0;
    }

    double energy = 0;

    for (int i = 0; i < torsionPars.size(); i++) {
      TorsionParameter tp = (TorsionParameter) torsionPars.get(i);
      AtomInterface a1 = molec.getAtomInterface(tp.getI());
      AtomInterface a2 = molec.getAtomInterface(tp.getJ());
      AtomInterface a3 = molec.getAtomInterface(tp.getK());
      AtomInterface a4 = molec.getAtomInterface(tp.getL());

      double angle = Point3f.dihedralAngle(a1, a2, a3, a4);

      List serie = tp.getSerie();
      for (int j = 0; j < serie.size(); j++) {
        TorsionTerm tt = (TorsionTerm) serie.get(j);
        energy += tt.V *
            (1.0 + Math.cos(tt.Periodicity * (float) angle - tt.Phase));
        /*
         if (p->n > 0) { // A negative n flags a 180 degree phase shift
           etor += p->u * (1.0 + cos(p->n * angle));
                     }
                     else {
           etor += p->u * (1.0 - cos(p->n * angle));
                     }
         */
      }
    }

    return energy;
  }

  public static double TorsionEnergy(MoleculeInterface molec, List torsionPars, Point3f[] Gradients) {
    double energy = 0;

    /*
          //integer ii,jj,kk,ll, k;
          //real f[3],g[3],h[3], a[3],b[3], sign[3];

     real dih, A, A2, B, B2, ab, G, fg, hg, coeff;

     dihedral_energy = 0.0;
     if ( ndihed <= 0 ) return dihedral_energy;

     for(integer i=0; i<ndihed; i++ ) {
      ii = dihedrals[i].i;
      jj = dihedrals[i].j;
      kk = dihedrals[i].k;
      ll = dihedrals[i].l;
      for( k=0; k<3; k++ ) {
        f[k] = cartes[ii].x[k] - cartes[jj].x[k];
        g[k] = cartes[jj].x[k] - cartes[kk].x[k];
        h[k] = cartes[ll].x[k] - cartes[kk].x[k];
        }

      VectorProduct(f,g,a);
      VectorProduct(h,g,b);
      A2 = VectorSquare(a);
      B2 = VectorSquare(b);
      A = sqrt(A2);
      B = sqrt(B2);
      ab = ScalarProduct(a,b);
      G = VectorLength(g);
      fg = ScalarProduct(f,g);
      hg = ScalarProduct(h,g);

      dih =  ArcCos(ab/(A*B));

      VectorProduct(a,b,sign);
      if ( ScalarProduct(sign,g) > 0.0 ) dih = - dih;

      dihedral_energy += dihedrals[i].Pk * (1.0 + cos(dihedrals[i].Pn*
                dih -  dihedrals[i].Phase) );

      coeff = - dihedrals[i].Pk * dihedrals[i].Pn *
                sin(dihedrals[i].Pn*dih - dihedrals[i].Phase);

      for(k=0; k<3; k++) {
        dxyz[ii].x[k] += coeff * ( - G / A2 * a[k] );
     dxyz[jj].x[k] += coeff * ( G/A2*a[k] + fg/(A2*G)*a[k] - hg/(B2*G)*b[k] );
     dxyz[kk].x[k] += coeff * ( hg/(B2*G)*b[k] - fg/(A2*G)*a[k] - G/B2*b[k] );
        dxyz[ll].x[k] += coeff *  G / B2 * b[k];
        }

      }
     */

    if (torsionPars.size() < 1) {
      return 0;
    }

    Point3f a = new Point3f();
    Point3f b = new Point3f();
    Point3f sign = new Point3f();

    Point3f f = new Point3f();
    Point3f g = new Point3f();
    Point3f h = new Point3f();

    float A, A2, B, B2, ab, G, fg, hg, coeff;
    double dih;

    for (int i = 0; i < torsionPars.size(); i++) {
      TorsionParameter tp = (TorsionParameter) torsionPars.get(i);
      AtomInterface a1 = molec.getAtomInterface(tp.getI());
      AtomInterface a2 = molec.getAtomInterface(tp.getJ());
      AtomInterface a3 = molec.getAtomInterface(tp.getK());
      AtomInterface a4 = molec.getAtomInterface(tp.getL());

      /*
                for( k=0; k<3; k++ ) {
                 f[k] = cartes[ii].x[k] - cartes[jj].x[k];
                 g[k] = cartes[jj].x[k] - cartes[kk].x[k];
                 h[k] = cartes[ll].x[k] - cartes[kk].x[k];
                 }

       */
      f.subtract(a1, a2);
      g.subtract(a2, a3);
      h.subtract(a4, a3);

      //VectorProduct(f, g, a);
      a.crossProduct(f, g);

      //VectorProduct(h, g, b);
      b.crossProduct(h, g);

      //A2 = VectorSquare(a);
      A2 = a.squaredNorm();

      //B2 = VectorSquare(b);
      B2 = b.squaredNorm();

      //A = sqrt(A2);
      A = (float) Math.sqrt(A2);

      //B = sqrt(B2);
      B = (float) Math.sqrt(B2);

      //ab = ScalarProduct(a, b);
      ab = a.product(b);

      //G = VectorLength(g);
      G = g.vectorNorm();

      //fg = ScalarProduct(f, g);
      fg = f.product(g);

      //hg = ScalarProduct(h, g);
      hg = h.product(g);

      dih = ArcCos(ab / (A * B));

      //VectorProduct(a, b, sign);
      sign.crossProduct(a, b);

      //if (ScalarProduct(sign, g) > 0.0) dih = -dih;
      if (sign.product(g) > 0.0) {
        dih = -dih;
      }

      //double angle = Point3f.dihedralAngle(a1, a2, a3, a4);
      //logger.info("Torsion: "+dih*Point3f.RADIANS_TO_DEGREES+" Angle: "+angle*Point3f.RADIANS_TO_DEGREES);

      List serie = tp.getSerie();
      for (int j = 0; j < serie.size(); j++) {
        TorsionTerm tt = (TorsionTerm) serie.get(j);

        //dihedral_energy += dihedrals[i].Pk * (1.0 + cos(dihedrals[i].Pn *
        //    dih - dihedrals[i].Phase));
        energy += tt.V * (1.0 + Math.cos(tt.Periodicity * dih - tt.Phase));

        //coeff = -dihedrals[i].Pk * dihedrals[i].Pn *
        //    sin(dihedrals[i].Pn * dih - dihedrals[i].Phase);

        coeff = -tt.V * tt.Periodicity *
            (float) Math.sin(tt.Periodicity * dih - tt.Phase);

        //for (k = 0; k < 3; k++) {
        //   dxyz[ii].x[k] += coeff * ( -G / A2 * a[k]);
        Gradients[tp.getI()].add(coeff * ( -G / A2 * a.getX()),
                                 coeff * ( -G / A2 * a.getY()),
                                 coeff * ( -G / A2 * a.getZ()));

        //   dxyz[jj].x[k] += coeff *
        //       (G / A2 * a[k] + fg / (A2 * G) * a[k] - hg / (B2 * G) * b[k]);

        Gradients[tp.getJ()].add(coeff *
                                 (G / A2 * a.getX() +
                                  fg / (A2 * G) * a.getX() -
                                  hg / (B2 * G) * b.getX()),
                                 coeff *
                                 (G / A2 * a.getY() +
                                  fg / (A2 * G) * a.getY() -
                                  hg / (B2 * G) * b.getY()),
                                 coeff *
                                 (G / A2 * a.getZ() +
                                  fg / (A2 * G) * a.getZ() -
                                  hg / (B2 * G) * b.getZ()));

        //   dxyz[kk].x[k] += coeff *
        //       (hg / (B2 * G) * b[k] - fg / (A2 * G) * a[k] - G / B2 * b[k]);

        Gradients[tp.getK()].add(coeff *
                                 (hg / (B2 * G) * b.getX() -
                                  fg / (A2 * G) * a.getX() -
                                  G / B2 * b.getX()),
                                 coeff *
                                 (hg / (B2 * G) * b.getY() -
                                  fg / (A2 * G) * a.getY() -
                                  G / B2 * b.getY()),
                                 coeff *
                                 (hg / (B2 * G) * b.getZ() -
                                  fg / (A2 * G) * a.getZ() -
                                  G / B2 * b.getZ()));

        //   dxyz[ll].x[k] += coeff * G / B2 * b[k];

        Gradients[tp.getL()].add(coeff * G / B2 * b.getX(),
                                 coeff * G / B2 * b.getY(),
                                 coeff * G / B2 * b.getZ());
        // }


      }
    }

    return energy;
  }

  static double ArcCos(double A) {
    return Math.acos(Math.abs(A) > 1.0 ? SIGN(1.0, A) : A);
  }

  static float SIGN(float A, float B) {
    if (B >= 0.0f) {
      return A > 0.0f ? A : -A;
    }
    else {
      return A < 0.0f ? A : -A;
    }
  }

  static double SIGN(double A, double B) {
    if (B >= 0.0) {
      return A > 0.0 ? A : -A;
    }
    else {
      return A < 0.0 ? A : -A;
    }
  }

}
