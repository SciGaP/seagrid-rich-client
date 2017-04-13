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

package cct.amber;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class SanderVariable {
  boolean enumerated = false;
  int varType = Sander8JobControl.TYPE_INTEGER;
  String Description;
  List Values = new ArrayList();
  int selectedValue = -1; // For enumerated variables
  String newValue = null; // For non-enumerated variables

  boolean defaultIsSet = false;
  boolean currentValueIsNotDefault = false;
  Object nondefaultParsedValue = null;
  String nondefaultValue = null;
  static final Logger logger = Logger.getLogger(SanderVariable.class.getCanonicalName());

  SanderVariable(String descr, boolean isEnum) {
    Description = descr;
    enumerated = isEnum;
    if (enumerated) {
      varType = Sander8JobControl.TYPE_INTEGER;
    }
  }

  public int getNumberOfValues() {
    return Values.size();
  }

  public void addValue(SanderVariableValue value) {
    if (!enumerated && Values.size() > 0) {
      logger.info("INTERNAL ERROR: addValue: Only one value for non-enumerated variables is allowed!");
      return;
    }

    if (defaultIsSet && value.isDefault()) {
      logger.info(
          "INTERNAL ERROR: addValue: Only one value can be default one!");
      return;
    }

    if (value.isDefault()) {
      defaultIsSet = true;
    }

    if (enumerated) {
      try {
        Integer ival = Integer.valueOf(value.getValue());
        value.setParsedValue(ival);
      }
      catch (NumberFormatException e) {
        logger.info(
            "INTERNAL ERROR: addValue: Expected integer value for enumerated variable, got: " +
            value);
        return;
      }

      if (value.isDefault()) {
        selectedValue = Values.size();
        //logger.info("Selected changed: " + selectedValue);
      }
    }

    else if (varType == Sander8JobControl.TYPE_INTEGER) {
      try {
        Integer ival = Integer.valueOf(value.getValue());
        value.setParsedValue(ival);
      }
      catch (NumberFormatException e) {
        logger.info(
            "INTERNAL ERROR: addValue: Expected integer value, got: " +
            value);
        return;
      }
    }

    else if (varType == Sander8JobControl.TYPE_FLOAT) {
      try {
        Float fval = Float.valueOf(value.getValue());
        value.setParsedValue(fval);
      }
      catch (NumberFormatException e) {
        logger.info(
            "INTERNAL ERROR: addValue: Expected float value, got: " +
            value);
        return;
      }

    }

    Values.add(value);

  }

  public int getSelectedIndex() {
    return selectedValue;
  }

  public String getDescription() {
    return this.Description;
  }

  public void setDescription(String newDescription) {
    Description = null;
    Description = newDescription;
  }

  /**
   *
   * @return String
   */
  public String getValue() {
    if (enumerated) {
      SanderVariableValue value = (SanderVariableValue) Values.get(
          selectedValue);
      return value.getValue();
    }

    if (currentValueIsNotDefault) {
      return nondefaultValue;
    }
    SanderVariableValue value = (SanderVariableValue) Values.get(0);
    return value.getValue();
  }

  /**
   *
   * @return Object
   */
  public Object getParsedValue() {
    if (enumerated) {
      SanderVariableValue value = (SanderVariableValue) Values.get(
          selectedValue);
      return value.getParsedValue();
    }

    if (currentValueIsNotDefault) {
      return nondefaultParsedValue;
    }
    SanderVariableValue value = (SanderVariableValue) Values.get(0);
    return value.getParsedValue();
  }

  public SanderVariableValue getValue(int n) {
    if (n < 0 || n >= getNumberOfValues()) {
      return null;
    }
    return (SanderVariableValue) Values.get(n);
  }

  public boolean isEnumerated() {
    return enumerated;
  }

  public boolean isDefaultValue() {
    return!currentValueIsNotDefault;
  }

  public void setNewValue(String new_value) {
    if (enumerated) {
      logger.info(
          "INTERNAL ERROR: setNewValue: Attempt to set enumerated variable!");
      return;
    }
    newValue = new_value;
  }

  /**
   *
   * @param index int
   */
  public void setSelectedIndex(int index) {
    if (!enumerated) {
      logger.info(
          "INTERNAL ERROR: setSelectedIndex: Attempt to set non-enumerated variable!");
      return;
    }

    if (index < 0 || index >= Values.size()) {
      logger.info(
          "INTERNAL ERROR: setSelectedIndex: index < 0 || index >= Values.size()!");
      return;
    }

    SanderVariableValue valid_value = (SanderVariableValue) Values.
        get(index);
      currentValueIsNotDefault = !valid_value.isDefault();

    selectedValue = index;
    //logger.info("Selected changed: setSelectedIndex" + selectedValue);
  }

  void setVarType(int type) {
    varType = type;
  }

  /**
   *
   * @param value String
   * @return String
   */

  public String setValue(String new_value) {

    if (new_value == null) {
      return "INTERNAL ERROR: setValue: Attempt to set null value";
    }
    String val = new_value.trim();
    logger.info("Parsing value: " + val);

    // --- Check enumerated variable

    if (enumerated) {
      Integer ival;
      try {
        ival = Integer.valueOf(val);
      }
      catch (NumberFormatException e) {
        return "Enumerated variable is not integer: " + val;
      }

      for (int i = 0; i < Values.size(); i++) {
        SanderVariableValue valid_value = (SanderVariableValue) Values.
            get(i);
        Integer proper_value = (Integer) valid_value.getParsedValue();
        if (ival.intValue() == proper_value.intValue()) {
          selectedValue = i;
          //logger.info("Selected changed: setValue: " + selectedValue);
          if (valid_value.isDefault()) {
            currentValueIsNotDefault = false;
            logger.info("Value is a default one");
          }
          else {
            currentValueIsNotDefault = true;
            logger.info("Value is not a default one");
          }
          return null;
        }
      }
      return val + " is not a valid enumerated value";

    }

    // --- Check integer value

    else if (varType == Sander8JobControl.TYPE_INTEGER) {
      try {
        Integer ival = Integer.valueOf(val);
        SanderVariableValue valid_value = (SanderVariableValue) Values.
            get(0);

        // --- Is new value equal to default one?

        Integer default_value = (Integer) valid_value.getParsedValue();

        if (default_value.intValue() == ival.intValue()) {
          currentValueIsNotDefault = false;
          logger.info("Value is a default one");
        }
        else {
          currentValueIsNotDefault = true;
          nondefaultParsedValue = ival;
          nondefaultValue = val;
          logger.info("Value is not a default one");
        }
        return null;
      }
      catch (NumberFormatException e) {
        return "Expected integer value, got: " + val;
      }
    }

    // --- Check float value

    else if (varType == Sander8JobControl.TYPE_FLOAT) {
      try {
        Float fval = Float.valueOf(val);
        SanderVariableValue valid_value = (SanderVariableValue) Values.
            get(0);
        // --- Is new value equal to default one?

        Float default_value = (Float) valid_value.getParsedValue();

        if (default_value.floatValue() == fval.floatValue()) {
          currentValueIsNotDefault = false;
          logger.info("Value is a default one");
        }
        else {
          currentValueIsNotDefault = true;
          nondefaultParsedValue = fval;
          nondefaultValue = val;
          logger.info("Value is not a default one");
        }

        return null;
      }
      catch (NumberFormatException e) {
        return "Expected float value, got: " + val;
      }
    }

    return null;
  }

  public int getType() {
    return varType;
  }
}
