package cct.adf;

import java.util.ArrayList;
import java.util.List;

import cct.vecmath.Point3f;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADFFrequency {
   private double value;
   private List<ADFVector> vectors = new ArrayList<ADFVector> ();

   public ADFFrequency(double value) {
      this.value = value;
   }

   public double getFrequencyValue() {
      return value;
   }

   public boolean hasDisplacementVectors() {
       return !(vectors == null || vectors.size() < 1);
   }

   public float[][] getDisplacementVectors() {
      if (vectors == null || vectors.size() < 1) {
         return null;
      }
      float[][] vect = new float[vectors.size()][3];
      for (int i = 0; i < vectors.size(); i++) {
         ADFVector v = vectors.get(i);
         vect[i][0] = v.vector.getX();
         vect[i][1] = v.vector.getY();
         vect[i][2] = v.vector.getZ();
      }
      return vect;
   }

   public void addVector(String atom_name, float x, float y, float z) {
      ADFVector v = new ADFVector();
      v.atom_name = atom_name;
      v.vector.setXYZ(x, y, z);
      vectors.add(v);
   }

   class ADFVector {
      String atom_name;
      private Point3f vector = new Point3f();
   }
}
