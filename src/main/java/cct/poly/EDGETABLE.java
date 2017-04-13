package cct.poly;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class EDGETABLE {

   static int HASHBIT = 5;
   static int HASHSIZE = (1<< (3 * HASHBIT));

   //vector<EDGELIST> table;		   /* edge and vertex id hash table */
   ArrayList table = new ArrayList(2 * HASHSIZE);

   // EDGETABLE(): table(2*HASHSIZE) {}


   /* setedge: set vertex id for edge */
   void setedge(int i1, int j1, int k1, int i2, int j2, int k2, int vid) {
      int index; //unsigned int index;

      if (i1 > i2 || (i1 == i2 && (j1 > j2 || (j1 == j2 && k1 > k2)))) {
         int t = i1;
         i1 = i2;
         i2 = t;
         t = j1;
         j1 = j2;
         j2 = t;
         t = k1;
         k1 = k2;
         k2 = t;
      }
      index = Polygonizer.HASH(i1, j1, k1) + Polygonizer.HASH(i2, j2, k2);

      EDGEELEMENT new_obj = new EDGEELEMENT();
      new_obj.i1 = i1;
      new_obj.j1 = j1;
      new_obj.k1 = k1;
      new_obj.i2 = i2;
      new_obj.j2 = j2;
      new_obj.k2 = k2;
      new_obj.vid = vid;

      //table[index].push_front(new_obj);
      EDGELIST el = (EDGELIST) table.get(index);

   }

   /* getedge: return vertex id for edge; return -1 if not set */

   int getedge(int i1, int j1, int k1, int i2, int j2, int k2) {

      if (i1 > i2 || (i1 == i2 && (j1 > j2 || (j1 == j2 && k1 > k2)))) {
         int t = i1;
         i1 = i2;
         i2 = t;
         t = j1;
         j1 = j2;
         j2 = t;
         t = k1;
         k1 = k2;
         k2 = t;
      }

      int hashval = Polygonizer.HASH(i1, j1, k1) + Polygonizer.HASH(i2, j2, k2);
      //EDGELIST::const_iterator q = table[hashval].begin();
      EDGELIST xxx = (EDGELIST) table.get(hashval);
      Iterator q = xxx.iterator();
      //for (; q != table[hashval].end(); ++q)
      while (q.hasNext()) {
         EDGEELEMENT ee = (EDGEELEMENT) q.next();
         //if (q.i1 == i1 && q.j1 == j1 && q.k1 == k1 &&
         //    q.i2 == i2 && q.j2 == j2 && q.k2 == k2)
         //  return q.vid;
         if (ee.i1 == i1 && ee.j1 == j1 && ee.k1 == k1 &&
             ee.i2 == i2 && ee.j2 == j2 && ee.k2 == k2) {
            return ee.vid;
         }

      }
      return -1;
   }

   public EDGETABLE() {
      for (int i = 0; i < 2 * HASHSIZE; i++) {
         table.add(new EDGELIST());
      }
   }
}
