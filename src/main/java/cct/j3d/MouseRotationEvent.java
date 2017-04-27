package cct.j3d;

import org.scijava.java3d.Transform3D;

/**
 * <p>Title: Jamberoo - Java Molecular Editor</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */
public class MouseRotationEvent {
  private Transform3D transform3D;
  public MouseRotationEvent(Transform3D t3d) {
    transform3D = t3d;
  }

  public Transform3D getLocalToVworld() {
    return transform3D;
  }
}
