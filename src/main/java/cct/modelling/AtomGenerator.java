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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cct.interfaces.AtomInterface;
import cct.interfaces.BondInterface;
import cct.interfaces.MoleculeInterface;
import cct.interfaces.Point3fInterface;
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
public class AtomGenerator {

  static final double RIGHT_ANGLE = 90.0 * Math.PI / 180.0;
  static final double TETRAHEDRAL_ANGLE = 109.47 * Math.PI / 180.0;
  static final float TABULATED_35_3_COS = (float) Math.cos(RIGHT_ANGLE - TETRAHEDRAL_ANGLE / 2.0);

  static final float HALF_TETRAHEDRAL_ANGLE_COS = (float) Math.cos(TETRAHEDRAL_ANGLE / 2.0);

  static final double TRIGONAL_ANGLE = 120.0 * Math.PI / 180.0;
  static double Factor = 0.7;
  static final Logger logger = Logger.getLogger(AtomGenerator.class.getCanonicalName());

  public AtomGenerator() {
  }

  public static Map fillEmptyValences(MoleculeInterface molecule, AtomInterface atom, String atomTypeKey, Map atomTypes) throws
      Exception {

    String refAtomType = (String) atom.getProperty(atomTypeKey);
    if (refAtomType == null) {
      throw new Exception("Atom type of reference atom is not set");
    }
    CCTAtomTypes refType = (CCTAtomTypes) atomTypes.get(refAtomType);
    if (refType.getCoordinationNumber() == 0 ||
        refType.getMaxSingleBonds() == 0) {
      throw new Exception("Atom type of reference atom either does not have single bonds or it's coordination number is 0");
    }
    float refCovRad = refType.getCovalentRadius();
    logger.info("Ref Atom: " + atom.getAtomicNumber() + " covRad: " + refCovRad);

    Map result = new HashMap();
    List newAtoms = new ArrayList();
    List newBonds = new ArrayList();

    // Temporary array
    Point3fInterface p1 = new Point3f();
    Point3fInterface p2 = new Point3f();
    Point3fInterface p3 = new Point3f();
    Point3fInterface p4 = new Point3f();
    Point3fInterface p5 = new Point3f();

    for (int i = 0; i < molecule.getNumberOfAtoms(); i++) {
      AtomInterface a = molecule.getAtomInterface(i);
      logger.info("Atom: " + i + " type: " +
                         a.getProperty(atomTypeKey) + " selected: " +
                         a.isSelected());

      if (!a.isSelected()) {
        continue;
      }

      String atomType = (String) a.getProperty(atomTypeKey);
      if (atomType == null) {
        continue; // Does not have atom type
      }

      if (!atomTypes.containsKey(atomType)) { // No such atom type in the list
        System.err.println("Warning: No such atom type " + atomType +
                           " in the atom types list. Ignored...");
        continue;
      }

      CCTAtomTypes type = (CCTAtomTypes) atomTypes.get(atomType);

      logger.info("  Number of bonds: " + a.getNumberOfBondedAtoms() +
                         " max number singlebonds: " +
                         type.getMaxSingleBonds());
      int nb = type.getMaxSingleBonds();
      if (nb < 1) {
        continue;
      }

      int nb_curr = a.getNumberOfBondedAtoms();
      //if (nb_curr nb  ) {
      //   continue;
      // }

      int atomsToAdd = 0;

      /*
                if (nb_curr < type.getCoordinationNumber() - nb) {
         atomsToAdd = nb;
                }
                else {
         atomsToAdd = nb - nb_curr - (type.getCoordinationNumber() - nb);
                }
       */

      atomsToAdd = type.getCoordinationNumber() - nb_curr;
      if (atomsToAdd > nb) {
        atomsToAdd = nb;
      }

      logger.info("  Atoms to add: " + atomsToAdd);
      if (atomsToAdd < 1) {
        continue;
      }

      float covRadius = type.getCovalentRadius();

      logger.info("Atoms to add: " + atomsToAdd + " geometry: " +
                         type.getGeometry() + " covRadius: " + covRadius);

      // --- Assume that atom can have only two ligands
      if (type.getGeometry() == AtomGeometry.LINEAR) {
        if (nb_curr == 0) {
          // --- Choose arbitrary direction for the first atom
          float dx = (refCovRad + covRadius) * (float) Factor, dy = 0,
              dz = 0;
          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          newAtom.setX(a.getX() + dx);
          newAtom.setY(a.getY() + dy);
          newAtom.setZ(a.getZ() + dz);
          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }

        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        List ligands = a.getBondedToAtoms();
        if (ligands.size() != 1) {
          throw new Exception(
              "Linear: Unexpected error: ligands.size() != 1 : " +
              ligands.size());
        }
        AtomInterface a2 = (AtomInterface) ligands.get(0);
        float dist = (float) a.distanceTo(a2);
        float dx = (a.getX() - a2.getX()) / dist;
        float dy = (a.getY() - a2.getY()) / dist;
        float dz = (a.getZ() - a2.getZ()) / dist;
        AtomInterface newAtom = atom.getNewAtomInstance(atom);
        newAtom.setX(a.getX() + dx);
        newAtom.setY(a.getY() + dy);
        newAtom.setZ(a.getZ() + dz);
        newAtom.setSubstructureNumber(a.getSubstructureNumber());
        newAtoms.add(newAtom);
        BondInterface bond = molecule.getNewBondInstance(a, newAtom);
        newBonds.add(bond);
        //molecule.addBond(bond);
      }

      // --- Assume that atom can have only three ligands
      else if (type.getGeometry() == AtomGeometry.TRIGONAL) {

        if (nb_curr == 0) {
          logger.info("Case TRIGONAL & nb_curr == 0");
          // --- Choose arbitrary direction for the first atom
          float dx = (refCovRad + covRadius) * (float) Factor, dy = 0,
              dz = 0;
          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          newAtom.setX(a.getX() + dx);
          newAtom.setY(a.getY() + dy);
          newAtom.setZ(a.getZ() + dz);
          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }

        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        if (nb_curr == 1) {
          logger.info("Case TRIGONAL & nb_curr == 1");
          // --- Choose arbitrary direction for the second atom
          List ligands = a.getBondedToAtoms();
          if (ligands.size() != 1) {
            throw new Exception(
                "Trigonal: Unexpected error: ligands.size() != 1 : " +
                ligands.size());
          }

          AtomInterface a2 = (AtomInterface) ligands.get(0);

          List ligs = a2.getBondedToAtoms();

          if (ligs.size() == 1) { // General case
            float dist = (float) a.distanceTo(a2);
            float length = (refCovRad + covRadius) * (float) Factor;

            p1.setXYZ( (a2.getX() - a.getX()) / dist,
                      (a2.getY() - a.getY()) / dist,
                      (a2.getZ() - a.getZ()) / dist);

            generateAtom(p1, (float) TRIGONAL_ANGLE, length, p2);

            AtomInterface newAtom = atom.getNewAtomInstance(atom);
            newAtom.setX(a.getX() + p2.getX());
            newAtom.setY(a.getY() + p2.getY());
            newAtom.setZ(a.getZ() + p2.getZ());
            newAtom.setSubstructureNumber(a.getSubstructureNumber());
            newAtoms.add(newAtom);
            BondInterface bond = molecule.getNewBondInstance(a, newAtom);
            newBonds.add(bond);
            //molecule.addBond(bond);
            --atomsToAdd;
            ++nb_curr;

          }
          else {
            AtomInterface a3 = (AtomInterface) ligs.get(0);
            if (a3 == a) {
              a3 = (AtomInterface) ligs.get(1);
            }
            // Place new atom in trans position to a3 in the direction a3->a2
            float dist = (float) a2.distanceTo(a3);
            float v1_x = (a2.getX() - a3.getX()) / dist;
            float v1_y = (a2.getY() - a3.getY()) / dist;
            float v1_z = (a2.getZ() - a3.getZ()) / dist;

            float length = (refCovRad + covRadius) * (float) Factor;

            AtomInterface newAtom = atom.getNewAtomInstance(atom);
            newAtom.setX(a.getX() + v1_x * length);
            newAtom.setY(a.getY() + v1_y * length);
            newAtom.setZ(a.getZ() + v1_z * length);
            newAtom.setSubstructureNumber(a.getSubstructureNumber());
            newAtoms.add(newAtom);
            BondInterface bond = molecule.getNewBondInstance(a, newAtom);
            newBonds.add(bond);
            --atomsToAdd;
            ++nb_curr;

          }

        }

        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        if (nb_curr == 2) {
          logger.info("Case TRIGONAL & nb_curr == 2");
          List ligands = a.getBondedToAtoms();
          if (ligands.size() != 2) {
            throw new Exception(
                "Tetragonal: Unexpected error: ligands.size() != 2 : " +
                ligands.size());
          }

          AtomInterface a2 = (AtomInterface) ligands.get(0);
          AtomInterface a3 = (AtomInterface) ligands.get(1);

          p2.setXYZ(a.getX() - a2.getX(), a.getY() - a2.getY(), a.getZ() - a2.getZ());
          Point3f.normalize(p2);
          p3.setXYZ(a.getX() - a3.getX(), a.getY() - a3.getY(), a.getZ() - a3.getZ());
          Point3f.normalize(p3);

          float length = (refCovRad + covRadius) * (float) Factor;
          //p1.setX(2.0f * a.getX() - a2.getX() - a3.getX());
          //p1.setY(2.0f * a.getY() - a2.getY() - a3.getY());
          //p1.setZ(2.0f * a.getZ() - a2.getZ() - a3.getZ());
          p1.setX(p2.getX() + p3.getX());
          p1.setY(p2.getY() + p3.getY());
          p1.setZ(p2.getZ() + p3.getZ());

          Point3f.normalize(p1);

          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          newAtom.setX(a.getX() + p1.getX() * length);
          newAtom.setY(a.getY() + p1.getY() * length);
          newAtom.setZ(a.getZ() + p1.getZ() * length);
          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          --atomsToAdd;
          ++nb_curr;
        }

      }

      // --- Assume that atom can have only four ligands
      else if (type.getGeometry() == AtomGeometry.TETRAHEDRAL) {
        //if (atomsToAdd == 4) {
        if (nb_curr == 0) {
          logger.info("Case TETRAHEDRAL & nb_curr == 0");
          // --- Choose arbitrary direction for the first atom
          float dx = (refCovRad + covRadius) * (float) Factor, dy = 0,
              dz = 0;
          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          newAtom.setX(a.getX() + dx);
          newAtom.setY(a.getY() + dy);
          newAtom.setZ(a.getZ() + dz);
          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }

        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        //if (atomsToAdd == 3) {
        if (nb_curr == 1) {
          logger.info("Case TETRAHEDRAL & nb_curr == 1");
          // --- Choose arbitrary direction for the second atom
          List ligands = a.getBondedToAtoms();
          if (ligands.size() != 1) {
            throw new Exception(
                "Tetrahedral: Unexpected error: ligands.size() != 1 : " +
                ligands.size());
          }
          AtomInterface a2 = (AtomInterface) ligands.get(0);
          float dist = (float) a.distanceTo(a2);
          float length = (refCovRad + covRadius) * (float) Factor;

          p1.setXYZ( (a2.getX() - a.getX()) / dist,
                    (a2.getY() - a.getY()) / dist,
                    (a2.getZ() - a.getZ()) / dist);

          generateAtom(p1, (float) TETRAHEDRAL_ANGLE, length, p2);

          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          newAtom.setX(a.getX() + p2.getX());
          newAtom.setY(a.getY() + p2.getY());
          newAtom.setZ(a.getZ() + p2.getZ());
          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }

        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        //if (atomsToAdd == 2) {
        if (nb_curr == 2) {
          logger.info("Case TETRAHEDRAL & nb_curr == 2");
          // --- Choose direction for the atom using a sum of three vector
          List ligands = a.getBondedToAtoms();
          if (ligands.size() != 2) {
            throw new Exception(
                "Tetrahedral: Unexpected error: ligands.size() != 2 : " +
                ligands.size());
          }
          AtomInterface a1 = (AtomInterface) ligands.get(0);
          AtomInterface a2 = (AtomInterface) ligands.get(1);
          /*
           p1.setXYZ(a1.getX() - a.getX(), a1.getY() - a.getY(),
                    a1.getZ() - a.getZ());
           p2.setXYZ(a2.getX() - a.getX(), a2.getY() - a.getY(),
                    a2.getZ() - a.getZ());
                          Point3f.crossProduct(p1, p2, p3);
                          Point3f.normalize(p3);
           float factor2 = length * (float) Math.sin(TETRAHEDRAL_ANGLE / 2);
           logger.info("factor2: " + factor2 + " p3" + p3.getX() +
                             " " + p3.getY() + " " + p3.getZ());

                          float dx = 0, dy = 0, dz = 0;

                          dx -= a1.getX() + a2.getX() - 2.0f * a.getX();
                          dy -= a1.getY() + a2.getY() - 2.0f * a.getY();
                          dz -= a1.getZ() + a2.getZ() - 2.0f * a.getZ();
           double scale = Math.sqrt( (double) (dx * dx + dy * dy + dz * dz));
           float factor = length * (float) Math.cos(TETRAHEDRAL_ANGLE / 2);
                          dx /= scale;
                          dy /= scale;
                          dz /= scale;

           logger.info("dx, dy, dz, factor: " + dx + " " + dy + " " +
                             dz + " " + factor);
           */

          p1.setXYZ(a1.getX() - a.getX(), a1.getY() - a.getY(),
                    a1.getZ() - a.getZ());
          Point3f.normalize(p1);
          p2.setXYZ(a2.getX() - a.getX(), a2.getY() - a.getY(),
                    a2.getZ() - a.getZ());
          Point3f.normalize(p2);
          Point3f.crossProduct(p1, p2, p3);
          Point3f.normalize(p3);

          p3.setXYZ(p3.getX() * TABULATED_35_3_COS,
                    p3.getY() * TABULATED_35_3_COS,
                    p3.getZ() * TABULATED_35_3_COS);

          p4.setXYZ( -p1.getX() - p2.getX(), -p1.getY() - p2.getY(),
                    -p1.getZ() - p2.getZ());
          Point3f.normalize(p4);
          p4.setXYZ(p4.getX() * HALF_TETRAHEDRAL_ANGLE_COS,
                    p4.getY() * HALF_TETRAHEDRAL_ANGLE_COS,
                    p4.getZ() * HALF_TETRAHEDRAL_ANGLE_COS);

          p5.setXYZ(p3.getX() + p4.getX(), p3.getY() + p4.getY(),
                    p3.getZ() + p4.getZ());
          Point3f.normalize(p5);

          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          //newAtom.setX(a.getX() + dx * factor + p3.getX() * factor2);
          //newAtom.setY(a.getY() + dy * factor + p3.getY() * factor2);
          //newAtom.setZ(a.getZ() + dz * factor + p3.getZ() * factor2);
          newAtom.setX(a.getX() + p5.getX());
          newAtom.setY(a.getY() + p5.getY());
          newAtom.setZ(a.getZ() + p5.getZ());

          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }
        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

        //if (atomsToAdd == 1) {
        if (nb_curr == 3) {
          logger.info("Case TETRAHEDRAL & nb_curr == 3");
          // --- Choose direction for the atom using a sum of three vector
          List ligands = a.getBondedToAtoms();
          if (ligands.size() != 3) {
            throw new Exception(
                "Tetrahedral: Unexpected error: ligands.size() != 3 : " +
                ligands.size());
          }
          AtomInterface a1 = (AtomInterface) ligands.get(0);
          AtomInterface a2 = (AtomInterface) ligands.get(1);
          AtomInterface a3 = (AtomInterface) ligands.get(2);

          p1.setXYZ(a1.getX() - a.getX(), a1.getY() - a.getY(),
                    a1.getZ() - a.getZ());
          Point3f.normalize(p1);
          p2.setXYZ(a2.getX() - a.getX(), a2.getY() - a.getY(),
                    a2.getZ() - a.getZ());
          Point3f.normalize(p2);
          p3.setXYZ(a3.getX() - a.getX(), a3.getY() - a.getY(),
                    a3.getZ() - a.getZ());
          Point3f.normalize(p3);

          p4.setXYZ( -p1.getX() - p2.getX() - p3.getX(),
                    -p1.getY() - p2.getY() - p3.getY(),
                    -p1.getZ() - p2.getZ() - p3.getZ());
          Point3f.normalize(p4);
          /*
           float dx = 0, dy = 0, dz = 0;

           dx -= a1.getX() + a2.getX() + a3.getX() - 3.0f * a.getX();
           dy -= a1.getY() + a2.getY() + a3.getY() - 3.0f * a.getY();
           dz -= a1.getZ() + a2.getZ() + a3.getZ() - 3.0f * a.getZ();
           // !!! Add test for dx, dy & dz  = 0 !!!

           double scale = Math.sqrt(dx * dx + dy * dy + dz * dz);
           dx /= scale;
           dy /= scale;
           dz /= scale;
           */
          float length = (refCovRad + covRadius) * (float) Factor;

          AtomInterface newAtom = atom.getNewAtomInstance(atom);
          //newAtom.setX(a.getX() + dx * length);
          //newAtom.setY(a.getY() + dy * length);
          //newAtom.setZ(a.getZ() + dz * length);
          newAtom.setX(a.getX() + p4.getX() * length);
          newAtom.setY(a.getY() + p4.getY() * length);
          newAtom.setZ(a.getZ() + p4.getZ() * length);

          newAtom.setSubstructureNumber(a.getSubstructureNumber());
          newAtoms.add(newAtom);
          BondInterface bond = molecule.getNewBondInstance(a, newAtom);
          newBonds.add(bond);
          //molecule.addBond(bond);
          --atomsToAdd;
          ++nb_curr;
        }
        if (type.getCoordinationNumber() == nb_curr) {
          continue;
        }

      }

    }

    logger.info("Atoms generated: " + newAtoms.size());
    //for (int i = 0; i < newAtoms.size(); i++) {
    //   molecule.addAtom( (AtomInterface) newAtoms.get(i));
    //}
    result.put("atoms", newAtoms);
    result.put("bonds", newBonds);
    //return newAtoms.size();
    return result;
  }

  public static void generateAtom(Point3fInterface dir, float theta,
                                  float length, Point3fInterface atom) throws
      Exception {

    float projection = length * (float) Math.cos(theta);

    float v1_x = dir.getX() * projection;
    float v1_y = dir.getY() * projection;
    float v1_z = dir.getZ() * projection;

    float v_normal_length = (float) Math.sqrt( (length *
        length - projection * projection));
    float vx_normal, vy_normal, vz_normal;
    // --- Choose arbitrary direction for vx

    // x & y != 0
    if (Math.abs(v1_x) > 0.0001 && Math.abs(v1_y) > 0.0001) {
      vx_normal = -v1_y / v1_x;
      vy_normal = 1;
      vz_normal = 0;
    }
    // x & z != 0
    else if (Math.abs(v1_x) > 0.0001 && Math.abs(v1_z) > 0.0001) {
      vx_normal = -v1_z / v1_x;
      vy_normal = 0;
      vz_normal = 1;
    }
    // y & z != 0
    else if (Math.abs(v1_y) > 0.0001 && Math.abs(v1_z) > 0.0001) {
      vx_normal = 0;
      vy_normal = -v1_z / v1_y;
      vz_normal = 1;
    }
    else if (Math.abs(v1_x) > 0.0001) {
      vx_normal = 0;
      vy_normal = 0;
      vz_normal = 1;
    }
    else if (Math.abs(v1_y) > 0.0001) {
      vx_normal = 0;
      vy_normal = 0;
      vz_normal = 1;
    }
    else if (Math.abs(v1_z) > 0.0001) {
      vx_normal = 1;
      vy_normal = 0;
      vz_normal = 0;
    }

    else {
      throw new Exception(
          "Unexpected error: x, y & z are all zero:" +
          v1_x + " " + v1_y + " " + v1_z);
    }

    // --- Form (arbitrary) vector perpendicular to v1

    double scale = Math.sqrt( (vx_normal * vx_normal +
                                        vy_normal * vy_normal +
                                        vz_normal * vz_normal));
    vx_normal /= scale;
    vy_normal /= scale;
    vz_normal /= scale;

    atom.setX(v1_x + vx_normal * v_normal_length);
    atom.setY(v1_y + vy_normal * v_normal_length);
    atom.setZ(v1_z + vz_normal * v_normal_length);
  }

  public static void main(String[] args) {
    AtomGenerator atomgenerator = new AtomGenerator();
  }
}
