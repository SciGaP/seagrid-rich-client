/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.cprocessor;

/**
 *
 * @author vvv900
 */
public class Variable {

  private String name;
  private Object value;

  public Variable(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public int getIntValue() throws Exception {
    if (value == null) {
      throw new Exception("Variable is not defined");
    }
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    if (value instanceof String) {
      try {
        return Integer.parseInt(value.toString());
      } catch (Exception ex) {

      }
    }
    throw new Exception("Variable value " + value.toString() + " cannot be converted into integer value");
  }

  public String toString() {
    if (getValue() == null) {
      return getName() + " = null";
    } else {
      return getName() + " = " + getValue().toString();
    }
  }
}
