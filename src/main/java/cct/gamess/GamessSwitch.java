/* ***** BEGIN LICENSE BLOCK *****
   Version: Apache 2.0/GPL 3.0/LGPL 3.0

   CCT - Computational Chemistry Tools
   Jamberoo - Java Molecules Editor

   Copyright 2008-2015 Dr. Vladislav Vasilyev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Contributor(s):
     Dr. Vladislav Vasilyev <vvv900@gmail.com>       (original author)

  Alternatively, the contents of this file may be used under the terms of
  either the GNU General Public License Version 2 or later (the "GPL"), or
  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  in which case the provisions of the GPL or the LGPL are applicable instead
  of those above. If you wish to allow use of your version of this file only
  under the terms of either the GPL or the LGPL, and not to allow others to
  use your version of this file under the terms of the Apache 2.0, indicate your
  decision by deleting the provisions above and replace them with the notice
  and other provisions required by the GPL or the LGPL. If you do not delete
  the provisions above, a recipient may use your version of this file under
  the terms of any one of the Apache 2.0, the GPL or the LGPL.

 ***** END LICENSE BLOCK *****/

package cct.gamess;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */

public class GamessSwitch {
  boolean anyValue = false;
  String name;
  String description;
  String defaultValue = null;
  Map values = new HashMap();
  Object variableType = null;

  public GamessSwitch(String _name, String _description) {
    name = _name;
    description = _description;
  }

  public GamessSwitch(String _name, String _description, boolean _anyValue) {
    name = _name;
    description = _description;
    anyValue = _anyValue;
  }

  public void setType(Object type) {
    variableType = type;
  }

  public void addValue(String value, String _description) {
    values.put(value, _description);
  }

  public void addValue(String value, String _description, boolean isDefault) {
    values.put(value, _description);
    if (isDefault) {
      defaultValue = value;
    }
  }

  public boolean isValidValue(String value) {

    // --- first, check value validity
    if (variableType != null) {
      try {
        if (variableType instanceof Boolean) {
          Boolean b = (Boolean) variableType;
          b = Boolean.parseBoolean(value);
          return true;
        }
        else if (variableType instanceof Integer) {
          float num = Float.parseFloat(value);
          Integer i = (Integer) variableType;
          i = (int) num;
          //Integer.parseInt(value);
          return true;
        }
        else if (variableType instanceof Float) {
          Float f = (Float) variableType;
          f = Float.parseFloat(value);
          return true;
        }
        else if (variableType instanceof Double) {
          Double d = (Double) variableType;
          d = Double.parseDouble(value);
          return true;
        }
        else if (variableType instanceof Long) {
          Long l = (Long) variableType;
          l = Long.parseLong(value);
          return true;
        }

      }
      catch (Exception ex) {
        return false;
      }
    }

    // --- Check "any value"

    if (anyValue) {
      return true;
    }

    // Is it in the list

      return values.containsKey( value );
  }

  public String getName() {
    return name;
  }
}
