package cct.j3d;

/**
 * <p>Title: Jamberoo - Computational Chemistry Toolkit</p>
 *
 * <p>Description: Collection of Computational Chemistry related code</p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vasilyev</p>
 *
 * <p>Company: The Australian National University</p>
 *
 * @author Dr. Vladislav Vasilyev
 * @version 1.0
 */

import org.scijava.java3d.BranchGroup;
import org.scijava.java3d.Group;
import org.scijava.vecmath.Color3f;

public class XYZAxes
    extends BranchGroup {

  ArrowNode xAxis = new ArrowNode(new float[] {-10, 0, 0}, new float[] {20, 0, 0});
  ArrowNode yAxis = new ArrowNode(new float[] {0, -10, 0}, new float[] {0, 20, 0});
  ArrowNode zAxis = new ArrowNode(new float[] {0, 0, -10}, new float[] {0, 0, 20});

  public XYZAxes() {

    this.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    this.setCapability(Group.ALLOW_CHILDREN_READ);
    this.setCapability(Group.ALLOW_CHILDREN_WRITE);
    this.setCapability(BranchGroup.ALLOW_DETACH);

    xAxis.createArrow();
    yAxis.createArrow();
    zAxis.createArrow();

    xAxis.setColor(new Color3f(1, 0, 0));
    yAxis.setColor(new Color3f(0, 1, 0));
    zAxis.setColor(new Color3f(0, 0, 1));

    this.addChild(xAxis);
    this.addChild(yAxis);
    this.addChild(zAxis);
  }
}
