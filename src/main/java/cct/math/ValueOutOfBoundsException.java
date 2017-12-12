package cct.math;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class ValueOutOfBoundsException
    extends Exception {
  public ValueOutOfBoundsException() {
    super();
  }

  public ValueOutOfBoundsException(String message) {
    super(message);
  }
}
