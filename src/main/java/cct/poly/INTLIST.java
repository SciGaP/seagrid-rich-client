package cct.poly;

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
public class INTLIST
    extends java.util.ArrayList {
   public INTLIST() {
   }

   public void push_front(int n) {
      this.add(0, new Integer(n));
   }
}
