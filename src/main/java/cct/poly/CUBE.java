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
public class CUBE {
   /* partitioning cell (cube) */
   public int i, j, k; /* lattice location of cube */
   public CORNER corners[] = new CORNER[8]; /* eight corners */

   public CUBE() {
   }
}
