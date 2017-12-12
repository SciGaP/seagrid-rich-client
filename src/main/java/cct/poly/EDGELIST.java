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
public class EDGELIST
    extends ArrayList {
   public EDGELIST() {
   }

   public void push_front(Object new_obj) {
      this.add(0, new_obj);
   }
}
