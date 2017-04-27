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
package cct.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import cct.j3d.QueryProperties;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * Title: </p>
 *
 * <p>
 * Description: </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006</p>
 *
 * <p>
 * Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class Utils {

  static final String DIVIDER = "@";
  static String CCT_DIRECTORY = ".cct";
  static private Utils utils = new Utils();

  private Utils() {
  }

  static public Properties getProperties(String propertiesFile) throws Exception {

    Properties props = null;
    URL url = null;
    try {
      url = Utils.class.getClassLoader().getResource(propertiesFile);
      props = new Properties();
      props.load(url.openStream());
    } catch (Exception ex) {
      throw new Exception("Cannot open properties file " + propertiesFile + ": " + ex.getMessage());
    }
    return props;
  }

  public static List toSimpleList(Map complexList, List simpleList) {

    Set set = complexList.entrySet();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String dicName = me.getKey().toString();
      Object obj = me.getValue();

      if (obj instanceof Map) {
        simpleList = toSimpleList((Map) obj, simpleList);
      } else {
        simpleList.add(dicName);
      }
    }
    return simpleList;
  }

  public static Map toSimpleList(Map complexList, Map simpleList) {

    Set set = complexList.entrySet();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String dicName = me.getKey().toString();
      Object obj = me.getValue();

      if (obj instanceof Map) {
        simpleList = toSimpleList((Map) obj, simpleList);
      } else {
        simpleList.put(dicName, obj);
      }
    }
    return simpleList;
  }

  /**
   * Checks if the ~/CCT_DIRECTORY (~/.cct) directory exists. If not, it creates one. Returns absolute path of the
   * directory
   */
  public static String getCCTDirectory() throws Exception {

    File cctDir = new File(System.getProperty("user.home"), CCT_DIRECTORY);
    if (cctDir.exists()) {
      if (!cctDir.isDirectory()) {
        /*
         JOptionPane.showMessageDialog(
         null,
         ".globus is not a directory.\nA .globus directory is needed in your home directory by the Java CoG Kit.\nHowever this directory could not be created because a file with that name already exists.\nPlease either remove or rename the .globus file, then restart this wizard",
         "Error", JOptionPane.ERROR_MESSAGE);
         */
        throw new Exception(CCT_DIRECTORY + "is not a directory.\nA "
            + CCT_DIRECTORY
            + " directory is needed in your home directory by the Java CCT Kit.\n"
            + "However this directory could not be created because a file with that name already exists.\n"
            + "Please either remove or rename the "
            + CCT_DIRECTORY
            + " file, then restart this application");
        //return false;
      } else {
        return cctDir.getAbsolutePath() + "/";
      }
    } else {
      try {
        cctDir.mkdir();
      } catch (Exception ex) {
        throw new Exception("Failed to create " + CCT_DIRECTORY + " : "
            + ex.getMessage());
      }
      return cctDir.getAbsolutePath() + "/";
    }
  }

  public static <T> void printVector(T v[]) {
    printVector(v, v.length);
  }

  public static <T> void printVector(T v[], int n) {
    System.out.print("\n");

    int N = 5;
    int IFIRST = 0;
    while (IFIRST < n) {
      int ILAST = Math.min(IFIRST + N - 1, n - 1);
      System.out.print("\n      I  ");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format("  %6d     ", i + 1));
      }
      System.out.print("\n     V(I)");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format(" %12f", v[i]));
      }
      IFIRST = ILAST + 1;
    }
    System.out.print("\n");
  }

  public static void printVector(double v[]) {
    printVector(v, v.length);
  }

  public static void printVector(double v[], int n) {
    System.out.print("\n");

    int N = 5;
    int IFIRST = 0;
    while (IFIRST < n) {
      int ILAST = Math.min(IFIRST + N - 1, n - 1);
      System.out.print("\n      I  ");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format("  %6d     ", i + 1));
      }
      System.out.print("\n     V(I)");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format(" %12f", v[i]));
      }
      IFIRST = ILAST + 1;
    }
    System.out.print("\n");
  }

  public static void printFloatVector(int n, float v[]) {
    System.out.print("\n");

    int N = 5;
    int IFIRST = 0;
    while (IFIRST < n) {
      int ILAST = Math.min(IFIRST + N - 1, n - 1);
      System.out.print("\n      I  ");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format("  %6d     ", i + 1));
      }
      System.out.print("\n     V(I)");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format(" %12f", v[i]));
      }
      IFIRST = ILAST + 1;
    }
    System.out.print("\n");
  }

  public static void printFloatVectors(int n, float v1[], float v2[]) {
    System.out.print("\n");

    int N = 5;
    int IFIRST = 0;
    while (IFIRST < n) {
      int ILAST = Math.min(IFIRST + N - 1, n - 1);
      System.out.print("\n      I  ");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format("  %6d     ", i + 1));
      }
      System.out.print("\n     V1(I)");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format(" %12f", v1[i]));
      }
      System.out.print("\n     V2(I)");
      for (int i = IFIRST; i <= ILAST; i++) {
        System.out.print(String.format(" %12f", v2[i]));
      }

      IFIRST = ILAST + 1;
    }
    System.out.print("\n");
  }

  //static public void printMatrix(double[][] matrix) throws Exception {
  //  printMatrix(matrix, new BufferedWriter(new OutputStreamWriter(System.out)));
  //}
  static public void printMatrix(double[][] matrix, Writer out) throws Exception {
    if (matrix == null) {
      out.write("Matrix to be printed is not defined or of zero size\n");
      return;
    }
    for (int i = 0; i < matrix.length; i++) {
      out.write(String.format("%3d", i + 1));
      for (int j = 0; j < matrix[i].length; j++) {
        out.write(String.format(" %12.4f", matrix[i][j]));
      }
      out.write("\n");
    }
  }

  static public void printMatrix(double[][] matrix) throws Exception {
    if (matrix == null) {
      System.out.println("Matrix to be printed is not defined or of zero size\n");
      return;
    }
    for (int i = 0; i < matrix.length; i++) {
      System.out.print(String.format("%3d", i + 1));
      for (int j = 0; j < matrix[i].length; j++) {
        System.out.print(String.format(" %12.4f", matrix[i][j]));
      }
      System.out.print("\n");
    }
  }

  public static String getPreference(Object object, String Key) {
    String value = null;
    try {
      Preferences prefs = Preferences.userNodeForPackage(object.getClass());
      value = prefs.get(Key, "");
    } catch (Exception ex) {
      System.err.println("getPreference: cannot get preference " + Key
          + " for " + object.getClass().getCanonicalName()
          + " : " + ex.getMessage());
    }
    if (value == null || value.length() < 1) {
      return null;
    }
    return value;
  }

  public static void savePreference(Object object, String Key, String value) {
    try {
      Preferences prefs = Preferences.userNodeForPackage(object.getClass());
      prefs.put(Key, value);
    } catch (Exception ex) {
      System.err.println("savePreference: cannot save preference " + Key
          + " for " + object.getClass().getCanonicalName()
          + " : " + ex.getMessage());
    }

  }

  public static String getResourceAsString(String resource) {
    InputStream is = null;
    try {
      ClassLoader cl = Utils.class.getClassLoader();
      is = cl.getResourceAsStream(resource);
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      StringBuffer sb = new StringBuffer();
      String line = null;
      while ((line = in.readLine()) != null) {
        sb.append(line + "\n");
      }
      return sb.toString();
    } catch (Exception ex) {
      System.err.println("Cannot get resource " + resource + " : " + ex.getMessage());
      return "Cannot get resource " + resource + " : " + ex.getMessage();
    }
  }

  public static String getSystemInfoAsString() {
    //Integer i = new Integer(0);
    ClassLoader classLoader = utils.getClass().getClassLoader();

    try {
      Properties sysProp = System.getProperties();
      StringWriter sWriter = new StringWriter();
      for (Enumeration e = sysProp.propertyNames(); e.hasMoreElements();) {
        String key = e.nextElement().toString();
        String value = sysProp.getProperty(key);
        sWriter.write(key + " : " + value + "\n");
      }

      // --- java3d info
      sWriter.write("\n\n ***** Java3d Properties *****\n\n");

      sWriter.write(QueryProperties.getJava3dPropsAsString());

      sWriter.write(packageInfoAsString(classLoader, "org.scijava.vecmath", "Point3d"));
      sWriter.write(packageInfoAsString(classLoader, "org.scijava.java3d", "SceneGraphObject"));
      sWriter.write(packageInfoAsString(classLoader, "org.scijava.java3d.utils.universe", "SimpleUniverse"));

      return sWriter.toString();
    } catch (SecurityException see) {
      return "Error getting System Info: " + see.getMessage();
    }
  }

  public static String packageInfoAsString(ClassLoader classLoader, String pkgName,
      String className) {
    StringWriter sWriter = new StringWriter();
    try {
      classLoader.loadClass(pkgName + "." + className);

      Package p = Package.getPackage(pkgName);
      if (p == null) {
        sWriter.write("Package " + pkgName + " : no package information is available from the archive or codebase\n");
      } else {
        sWriter.write("\n" + p.toString() + "\n");
        sWriter.write("Specification Title = " + p.getSpecificationTitle() + "\n");
        sWriter.write("Specification Vendor = " + p.getSpecificationVendor() + "\n");
        sWriter.write("Specification Version = " + p.getSpecificationVersion() + "\n");

        sWriter.write("Implementation Vendor = " + p.getImplementationVendor() + "\n");
        sWriter.write("Implementation Version = " + p.getImplementationVersion() + "\n");
      }
    } catch (ClassNotFoundException e) {
      sWriter.write("Unable to load package " + pkgName + "\n");
    }

    return sWriter.toString();
  }

  public static Map reverseLinkedHashMap(Map toReverse) {

    if (toReverse == null) {
      return null;
    }

    Map reversed = new LinkedHashMap(toReverse.size());

    Set set = toReverse.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      reversed.put(me.getValue(), me.getKey());
    }
    return reversed;
  }

  /**
   * Converts time in seconds into time string in hours
   *
   * @param time_in_secs - time in seconds
   * @return - Time string in hours in the form: HH:MM:SS
   */
  public static String timeInHours(float time_in_secs) {
    if (time_in_secs < 0) {
      return "00:00:00";
    }
    int hours = (int) (time_in_secs / 3600.0f);
    float therest = time_in_secs - hours * 3600.0f;
    int minutes = (int) (therest / 60.0f);
    therest = therest - minutes * 60.0f;
    int secs = (int) therest;
    return String.format("%d:%02d:%02d", hours, minutes, secs);
  }

  public static Object loadClass(String className) throws Exception {
    Class builderClass = Class.forName(className);
    // Get the ClassLoader object associated with this Class.
    ClassLoader loader = builderClass.getClassLoader();

    Class cl = loader.loadClass(className);
    Object obj = cl.newInstance();
    return obj;
  }

  /**
   * Converts double into a byte array
   *
   * @param value - double number
   * @return byte array
   */
  public static byte[] toByteArray(double value) {
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putDouble(value);
    return bytes;
  }

  /**
   * Converts byte array into a double number
   *
   * @param bytes
   * @return
   */
  public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
  }

  /**
   * Converts bytes array into a double array
   *
   * @param bytes
   * @return
   */
  public static double[] toDoubleArray(byte[] bytes) {
    double[] doubles = new double[bytes.length / 8];
    ByteBuffer.wrap(bytes).asDoubleBuffer().get(doubles);
    return doubles;
  }

  /**
   * Converts a double array into a byte array
   *
   * @param doubleArray
   * @return
   */
  public static byte[] toByteArray(double[] doubleArray) {
    byte[] bytes = new byte[doubleArray.length * 8];
    ByteBuffer.wrap(bytes).asDoubleBuffer().put(doubleArray);
    return bytes;
  }

  /**
   * Converts an array of string into a byte array while treating strings as a UTF8 ones
   *
   * @param strings - array of strings
   * @return - byte array
   */
  public static byte[] toByteArrayUTF8(String[] strings) {
    int n = 0;
    for (int i = 0; i < strings.length; i++) {
      n += strings[i].length();
    }

    byte[] bytes = new byte[n];
    int index = 0;
    for (int i = 0; i < strings.length; i++) {
      byte[] b = strings[i].getBytes(StandardCharsets.UTF_8);
      for (int j = 0; j < b.length; j++) {
        bytes[index] = b[j];
        ++index;
      }
    }

    return bytes;
  }

  public static byte[] toByteArrayUTF16(String[] strings) {
    int n = 0;
    for (int i = 0; i < strings.length; i++) {
      n += strings[i].length();
    }

    byte[] bytes = new byte[2 * n];
    int index = 0;
    for (int i = 0; i < strings.length; i++) {
      byte[] b = strings[i].getBytes(StandardCharsets.UTF_8);
      for (int j = 0; j < b.length; j++) {
        bytes[index] = b[j];
        ++index;
      }
    }

    return bytes;
  }
}
