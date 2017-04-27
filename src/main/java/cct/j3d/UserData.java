package cct.j3d;

import java.util.HashMap;

import org.scijava.java3d.SceneGraphObject;

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
public class UserData
    extends HashMap<String, Object> {

  public static final String NODE_NAME = "node_name";

  public UserData() {
    super();
  }

  public static void setUserData(SceneGraphObject target, String name, Object data) {
    UserData ud = null;
    Object obj = target.getUserData();
    if (obj == null) {
      ud = new UserData();
    }
    else if (obj instanceof UserData) {
      ud = (UserData) obj;
    }
    else {
      System.err.println("Expected UserData class, got " + obj.getClass().getCanonicalName() + " Overwitten...");
      ud = new UserData();
    }
    ud.put(name, data);
    target.setUserData(ud);
  }

  public static String getUserDataAsString(SceneGraphObject target, String name) {
    UserData ud = null;
    Object obj = target.getUserData();
    if (obj == null) {
      return "";
    }
    else if (obj instanceof UserData) {
      ud = (UserData) obj;
    }
    else {
      System.err.println("getUserDataAsString: Expected UserData class, got " + obj.getClass().getCanonicalName() + " Ignored...");
      return "";
    }
    return ud.get(name).toString();
  }
}
