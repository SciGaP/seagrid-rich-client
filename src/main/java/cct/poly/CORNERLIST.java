package cct.poly;

import java.util.ArrayList;

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
public class CORNERLIST
    extends ArrayList {
   public CORNERLIST() {
   }

   public void push_front(Object elem) {
      this.add(0, elem);
   }
}
