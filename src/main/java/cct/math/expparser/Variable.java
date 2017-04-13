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
package cct.math.expparser;

/**
 *
 * @author vvv900
 */
public class Variable {

  private SYMBOL_TYPE type;
  private OPERATION operation;
  private PARANTHESIS paranthesis;
  private String name;
  private double number;
  private boolean is_number = false;
  private boolean constant = false;

  private Object value;

  public Variable(double value) {
    //this.name = String.valueOf(value);
    this.type = SYMBOL_TYPE.VARIABLE;
    try {
      setValue(value);
    } catch (Exception ex) {
    }
  }

  public Variable(double value, boolean constant) {
    //this.name = String.valueOf(value);
    this.type = SYMBOL_TYPE.VARIABLE;
    try {
      setValue(value);
    } catch (Exception ex) {
    }
    this.constant = constant;
  }

  public Variable(Object value) {
    this.type = SYMBOL_TYPE.VARIABLE;
    try {
      this.setValue(value);
    } catch (Exception ex) {
    }
  }

  public Variable(String name, Object value, boolean constant) {
    this.name = name;
    this.type = SYMBOL_TYPE.VARIABLE;
    try {
      this.setValue(value);
    } catch (Exception ex) {
    }
    this.constant = constant;
  }

  public Variable(Object value, boolean constant) {
    this.type = SYMBOL_TYPE.VARIABLE;
    try {
      this.setValue(value);
    } catch (Exception ex) {
    }
    this.constant = constant;
  }

  public Variable(String name, SYMBOL_TYPE type) {
    this.name = name;
    this.type = type;
  }

  public Variable(OPERATION op, SYMBOL_TYPE type) {
    operation = op;
    this.type = type;
  }

  public Variable(PARANTHESIS par, SYMBOL_TYPE type) {
    paranthesis = par;
    this.type = type;
  }

  public Variable(SYMBOL_TYPE type) throws Exception {
    if (type != SYMBOL_TYPE.COMMA) {
      throw new Exception("INTERNAL ERROR: Not comma");
    }
    this.type = type;
  }

  public void setValue(double value) throws Exception {
    if (constant) {
      throw new Exception(name + " is a Constant and its value cannot be changed");
    }
    number = value;
    this.value = new Double(value);
    is_number = true;
  }

  public void setValue(Object value) throws Exception {
    if (constant) {
      throw new Exception(name + " is a Constant and its value cannot be changed");
    }
    this.value = value;
    if (value instanceof Number) {
      is_number = true;
      number = ((Number) value).doubleValue();
    } else {
      is_number = false;
    }
  }

  public Object getValue() {
    return value;
  }

  public double getValueAsDouble() throws Exception {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    throw new Exception("Variable " + name + " cannot be converted into a double value. Got: " + value.getClass().getName());
  }

  public String getName() {
    return name;
  }

  public SYMBOL_TYPE getType() {
    return type;
  }

  public void setType(SYMBOL_TYPE type) {
    this.type = type;
  }

  public boolean isNumber() {
    return is_number;
  }

  public boolean isString() {
    if (value == null) {
      return true;
    }
    return value instanceof String;
  }

  public boolean isConstant() {
    return constant;
  }
}
