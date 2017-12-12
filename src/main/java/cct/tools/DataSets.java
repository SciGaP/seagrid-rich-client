/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Vlad
 */
public abstract class DataSets {

  public static final String DATA_ROW_NUMBER = "Row Number";

  private Set<String> descriptors = new LinkedHashSet<String>();
  private Map<String, List> data = new LinkedHashMap<String, List>();
  static final Logger logger = Logger.getLogger(DataSets.class.getCanonicalName());

  public DataSets() {
    descriptors.add(DATA_ROW_NUMBER);
  }

  public abstract void parseData(BufferedReader in) throws Exception;

  public void parseData(String filename) throws Exception {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(filename));
    } catch (Exception e) {
      throw new Exception("Error opening file " + filename + " : " + e.getMessage());
    }

    try {
      parseData(in);
    } catch (Exception e) {
      try {
        in.close();
      } catch (Exception ex) {
      }
      throw new Exception("Error while parsing data in file " + filename + " : " + e.getMessage());
    }
    try {
      in.close();
    } catch (Exception ex) {
    }
  }

  public void addData(String dataLabel, Object object) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return;
    }
    if (!data.containsKey(dataLabel)) {
      List<Object> list = new ArrayList<Object>();
      data.put(dataLabel, list);
      descriptors.add(dataLabel);
    }
    data.get(dataLabel).add(object);
  }

  public void addData(String dataLabel, String string) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return;
    }
    if (!data.containsKey(dataLabel)) {
      List<String> list = new ArrayList<String>();
      data.put(dataLabel, list);
      descriptors.add(dataLabel);
    }
    data.get(dataLabel).add(string);
  }

  public void addData(String dataLabel, double number) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return;
    }
    if (!data.containsKey(dataLabel)) {
      List<Double> list = new ArrayList<Double>();
      data.put(dataLabel, list);
      descriptors.add(dataLabel);
    }
    data.get(dataLabel).add(number);
  }

  public void addData(String dataLabel, float number) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return;
    }
    if (!data.containsKey(dataLabel)) {
      List<Float> list = new ArrayList<Float>();
      data.put(dataLabel, list);
      descriptors.add(dataLabel);
    }
    data.get(dataLabel).add(number);
  }

  public void addData(String dataLabel, int number) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return;
    }
    if (!data.containsKey(dataLabel)) {
      List<Integer> list = new ArrayList<Integer>();
      data.put(dataLabel, list);
      descriptors.add(dataLabel);
    }
    data.get(dataLabel).add(number);
  }

  public Object getData(String dataLabel, int index) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return getData(index);
    }
    if (!data.containsKey(dataLabel)) {
      return null;
    }

    List list = data.get(dataLabel);
    if (index < 0 || index >= list.size()) {
      return null;
    }
    return list.get(index);
  }

  private Object getData(int index) {
    if (index < 0 || index >= this.getDataSize()) {
      return null;
    }
    return new Integer(index + 1);
  }

  public String getDataAsString(String dataLabel, int index) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return getDataAsString(index);
    }
    if (!data.containsKey(dataLabel)) {
      return null;
    }

    List list = data.get(dataLabel);
    if (index < 0 || index >= list.size()) {
      return null;
    }
    return list.get(index).toString();
  }

  private String getDataAsString(int index) {
    if (index < 0 || index >= this.getDataSize()) {
      return null;
    }
    return String.valueOf(index + 1);
  }

  public Double getDataAsDouble(String dataLabel, int index) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return getDataAsDouble(index);
    }
    if (!data.containsKey(dataLabel)) {
      return null;
    }

    List list = data.get(dataLabel);
    if (index < 0 || index >= list.size()) {
      return null;
    }
    if (list.get(index) instanceof Number) {
      return ((Number) list.get(index)).doubleValue();
    }
    if (list.get(index) instanceof String) {
      try {
        return Double.parseDouble(list.get(index).toString());
      } catch (Exception ex) {
        logger.severe("Error parsing " + list.get(index).toString() + " as a double value");
        return null;
      }
    }
    return null;
  }

  private Double getDataAsDouble(int index) {
    if (index < 0 || index >= this.getDataSize()) {
      return null;
    }
    return new Double(index + 1);
  }

  public Float getDataAsFloat(String dataLabel, int index) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return getDataAsFloat(index);
    }
    if (!data.containsKey(dataLabel)) {
      return null;
    }

    List list = data.get(dataLabel);
    if (index < 0 || index >= list.size()) {
      return null;
    }
    if (list.get(index) instanceof Number) {
      return ((Number) list.get(index)).floatValue();
    }
    if (list.get(index) instanceof String) {
      try {
        return Float.parseFloat(list.get(index).toString());
      } catch (Exception ex) {
        logger.severe("Error parsing " + list.get(index).toString() + " as a float value");
        return null;
      }
    }
    return null;
  }

  private Float getDataAsFloat(int index) {
    if (index < 0 || index >= this.getDataSize()) {
      return null;
    }
    return new Float(index + 1);
  }

  public Integer getDataAsInteger(String dataLabel, int index) {
    if (dataLabel.equals(DATA_ROW_NUMBER)) {
      return getDataAsInteger(index);
    }
    if (!data.containsKey(dataLabel)) {
      return null;
    }

    List list = data.get(dataLabel);
    if (index < 0 || index >= list.size()) {
      return null;
    }
    if (list.get(index) instanceof Number) {
      return ((Number) list.get(index)).intValue();
    }
    if (list.get(index) instanceof String) {
      try {
        return Integer.parseInt(list.get(index).toString());
      } catch (Exception ex) {
        logger.severe("Error parsing " + list.get(index).toString() + " as a integer value");
        return null;
      }
    }
    return null;
  }

  private Integer getDataAsInteger(int index) {
    if (index < 0 || index >= this.getDataSize()) {
      return null;
    }
    return new Integer(index + 1);
  }

  public Object getLastData(String dataLabel) {
    if (!data.containsKey(dataLabel)) {
      return null;
    }
    return this.getData(dataLabel, data.get(dataLabel).size() - 1);
  }

  public String geLastDataAsString(String dataLabel) {
    if (!data.containsKey(dataLabel)) {
      return null;
    }
    return this.getDataAsString(dataLabel, data.get(dataLabel).size() - 1);
  }

  public Double getLastDataAsDouble(String dataLabel) {
    if (!data.containsKey(dataLabel)) {
      return null;
    }
    return this.getDataAsDouble(dataLabel, data.get(dataLabel).size() - 1);
  }

  public Float getLastDataAsFloat(String dataLabel) {
    if (!data.containsKey(dataLabel)) {
      return null;
    }
    return this.getDataAsFloat(dataLabel, data.get(dataLabel).size() - 1);
  }

  public Integer getLastDataAsInteger(String dataLabel) {
    if (!data.containsKey(dataLabel)) {
      return null;
    }
    return this.getDataAsInteger(dataLabel, data.get(dataLabel).size() - 1);
  }

  /**
   * Clears dataset
   */
  public void clearData() {
    for (String key : data.keySet()) {
      data.get(key).clear();
      //data.put(key, null);
    }
    data.clear();
  }

  /**
   * Returns A COPY of original Map
   *
   * @return A COPY of original Map
   */
  public Map<String, List> getData() {
    return new LinkedHashMap<String, List>(data);
  }

  public Set<String> getDescriptors() {
    return new LinkedHashSet<String>(descriptors);
  }

  public boolean hasDescriptor(String descr) {
    return descriptors.contains(descr);
  }

  public void addDescriptor(String descr) {
    descriptors.add(descr);
  }

  public void clearDescriptors() {
    descriptors.clear();
    descriptors.add(DATA_ROW_NUMBER);
    this.clearData();
  }

  public int getDataSize() {
    if (descriptors.size() < 1) {
      return 0;
    }
    int size = 0;
    for (String key : descriptors) {
      if (data.get(key) == null) {
        continue;
      }
      if (data.get(key).size() > size) {
        size = data.get(key).size();
      }
    }
    return size;
  }

  public double[] getRowNumberAsDouble() {
    double[] dbl = new double[this.getDataSize()];
    double value = 1.0;
    for (int i = 0; i < getDataSize(); i++, value += 1.0) {
      dbl[i] = value;
    }
    return dbl;
  }

  public float[] getRowNumberAsFloat() {
    float[] dbl = new float[this.getDataSize()];
    float value = 1.0f;
    for (int i = 0; i < getDataSize(); i++, value += 1.0) {
      dbl[i] = value;
    }
    return dbl;
  }

  public double[] getDataAsDouble(String descriptor) {
    if (descriptor.equals(DATA_ROW_NUMBER)) {
      return getRowNumberAsDouble();
    }
    List list = data.get(descriptor);
    if (list == null) {
      return null;
    }

    double[] dbl = new double[list.size()];

    if (list.get(0) instanceof Double) {
      List<Double> dblList = list;
      if (list.get(0) instanceof Double) {
        for (int i = 0; i < dbl.length; i++) {
          dbl[i] = dblList.get(i);
        }
        return dbl;
      }
    }

    // ---
    if (list.get(0) instanceof Float) {
      List<Float> fltList = list;
      for (int i = 0; i < dbl.length; i++) {
        dbl[i] = fltList.get(i).doubleValue();
      }
      return dbl;
    }
    // ---

    if (list.get(0) instanceof Integer) {
      List<Integer> intList = list;
      for (int i = 0; i < dbl.length; i++) {
        dbl[i] = intList.get(i).doubleValue();
      }
      return dbl;
    }

    // ---
    dbl = null;
    return dbl;
  }

  public float[] getDataAsFloat(String descriptor) {
    if (descriptor.equals(DATA_ROW_NUMBER)) {
      return getRowNumberAsFloat();
    }
    List list = data.get(descriptor);
    if (list == null) {
      return null;
    }

    float[] dbl = new float[list.size()];
    for (int i = 0; i < dbl.length; i++) {
      dbl[i] = (Float) list.get(i);
    }
    return dbl;
  }

  public int[] getRowNumberAsInteger() {
    int[] dbl = new int[this.getDataSize()];

    for (int i = 0; i < getDataSize(); i++) {
      dbl[i] = i + 1;
    }
    return dbl;
  }

  public int[] getDataAsInteger(String descriptor) {
    if (descriptor.equals(DATA_ROW_NUMBER)) {
      return getRowNumberAsInteger();
    }
    List list = data.get(descriptor);
    if (list == null) {
      return null;
    }

    int[] dbl = new int[list.size()];
    for (int i = 0; i < dbl.length; i++) {
      dbl[i] = (Integer) list.get(i);
    }
    return dbl;
  }

  public String[] getRowNumberAsString() {
    String[] dbl = new String[this.getDataSize()];

    for (int i = 0; i < getDataSize(); i++) {
      dbl[i] = String.valueOf(i + 1);
    }
    return dbl;
  }

  public String[] getDataAsString(String descriptor) {
    if (descriptor.equals(DATA_ROW_NUMBER)) {
      return getRowNumberAsString();
    }
    List list = data.get(descriptor);
    if (list == null) {
      return null;
    }

    String[] dbl = new String[list.size()];
    for (int i = 0; i < dbl.length; i++) {
      dbl[i] = list.get(i).toString();
    }
    return dbl;
  }

  public Object[] getDataAsObject(String descriptor) {
    List list = data.get(descriptor);
    if (list == null) {
      return null;
    }
    return list.toArray();
  }

  public void printDataSets() {
    System.out.println("\nData Set(s) Contents:\nDescriptor(s) and Data:\n");
    int count = 0, n = 0;
    for (String d : descriptors) {
      System.out.println(String.format("%6d", ++count) + " " + d + ":");
      n = 0;
      if (!d.equals(DATA_ROW_NUMBER)) {
        for (Object o : data.get(d)) {
          System.out.println(String.format("%6d", ++n) + " " + o.toString());
        }
      }
    }
    System.out.println("===== End of Data Set(s) =====\n");
  }
}
