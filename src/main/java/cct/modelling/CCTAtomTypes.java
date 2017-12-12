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

package cct.modelling;

import cct.resources.images.ImageResources;

import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class CCTAtomTypes
    extends AtomGeometry implements Comparator {

  public static final String LINEAR_GEOMETRY = "linear";
  public static final String TETRAHEDRAL_GEOMETRY = "tetrahedral";
  public static final String TRIGONAL_GEOMETRY = "trigonal";

  static final String BASE_ELEMENT_KEY = "BASEELEMENT";
  static final String DEFAULT_TYPE_KEY = "DEFAULT";
  static final String COORDINATION_NUMBER_KEY = "COORDNUMBER";
  static final String GEOMETRY_KEY = "GEOMETRY";
  static final String MAX_SINGLE_BONDS_KEY = "MAXSINGLES";
  static final String VDW_DI_KEY = "DI";
  static final String VDW_RI_KEY = "RI";

  static Set atomProperties = new HashSet();
  static final Logger logger = Logger.getLogger(CCTAtomTypes.class.getCanonicalName());

  static final float unsetDiValue = -10000.0f;

  static private boolean debug = false;
  private String typeName;
  private int chemicalElement;
  private float weight;
  //private int coordNumber = 4;
  //private int maxHNumber = coordNumber;
  //private int geometry = TETRAHEDRAL;
  private float covalentRadius = 0.0f;
  private float vdwWellDepth = unsetDiValue;
  private float vdwDistance = unsetDiValue;
  private boolean defaultType = false;

  static String defaultProperiesFile = "cct.CCTAtomTypes";
  private static ResourceBundle resources;
  // Ordinal of next suit to be created
  private static int nextOrdinal = 0;
  // Assign an ordinal to this suit
  private final int ordinal = nextOrdinal++;

  private static Map<String, CCTAtomTypes> allTypes = new LinkedHashMap<String, CCTAtomTypes> ();
  private static Map elementMapping = new LinkedHashMap();

  static ImageIcon emptyIcon = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/emptyTransparent.png"));
  static ImageIcon tetrahedral_4_Icon = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-4.png"));

  static ImageIcon tetrahedral_3_Icon = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-3.png"));

  static ImageIcon tetrahedral_2_Icon = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/tetrahedral-2.png"));

  static ImageIcon trigonal_3_Icon = new ImageIcon(ImageResources.class.getResource(
      "/cct/images/icons48x48/trigonal-3.png"));

  static {
    atomProperties.add(BASE_ELEMENT_KEY);
    atomProperties.add(COORDINATION_NUMBER_KEY);
    atomProperties.add(GEOMETRY_KEY);
    atomProperties.add(MAX_SINGLE_BONDS_KEY);
    atomProperties.add(VDW_DI_KEY);
    atomProperties.add(VDW_RI_KEY);
    atomProperties.add(DEFAULT_TYPE_KEY);
  }

  static CCTAtomTypes defValues = new CCTAtomTypes(defaultProperiesFile);

  /*
      static {

     try {
        type001 = new CCTAtomTypes("H", ChemicalElements.getAtomicNumber("H"),
                                   1, LINEAR, 1);

   type002 = new CCTAtomTypes("C.4", ChemicalElements.getAtomicNumber("C"),
                                   4, TETRAHEDRAL, 4);
   type003 = new CCTAtomTypes("C.3", ChemicalElements.getAtomicNumber("C"),
                                   3, TRIGONAL, 2);
   type004 = new CCTAtomTypes("C.2", ChemicalElements.getAtomicNumber("C"),
                                   2, LINEAR, 1);

   type005 = new CCTAtomTypes("N.4", ChemicalElements.getAtomicNumber("N"),
                                   4, TETRAHEDRAL, 4);
   type006 = new CCTAtomTypes("N.3", ChemicalElements.getAtomicNumber("N"),
                                   3, TETRAHEDRAL, 3);
   type007 = new CCTAtomTypes("N.2", ChemicalElements.getAtomicNumber("N"),
                                   3, TRIGONAL, 2);

   type008 = new CCTAtomTypes("O.4", ChemicalElements.getAtomicNumber("O"),
                                   2, TETRAHEDRAL, 2);
   type009 = new CCTAtomTypes("O.2", ChemicalElements.getAtomicNumber("O"),
                                   1, LINEAR, 0);
     }
     catch (Exception ex) {
        ex.printStackTrace();
     }
      }
   */
  public CCTAtomTypes() {}

  public CCTAtomTypes(String propertiesName) {
    try {
      resources = ResourceBundle.getBundle(propertiesName);
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (resources == null) {
        System.err.println("Resources " + propertiesName +
                           " are not found");
        return;
      }
    }

    // --- Read data for each atom type
    //H:baseElement=H
    //H:coordNumber=1
    //H:geometry=linear
    //H:maxH=1

    CCTAtomTypes newType = null;
    String typesStr = "";
    Enumeration<String> en = resources.getKeys();
    while (en.hasMoreElements()) {
      String key = en.nextElement();

      // --- Divide atom type and properties
      int index = key.indexOf("*");
      if (index == -1 || (index + 1) >= key.length()) {
        System.err.println("Error in atom types file: " + key);
        continue;
      }

      String atomType = key.substring(0, index);
      String propType = key.substring(index + 1);

      /*
                logger.info("Size: "+atomProperties.size());
                Iterator iter = atomProperties.iterator();
                boolean valid = false;
                while(iter.hasNext()) {
         String prop = (String)iter.next();
         if ( prop.equalsIgnoreCase(propType) ) {
            valid = true;
            break;
         }
                }
       */
      if (!atomProperties.contains(propType.toUpperCase())) {
        //if (!valid) {
        System.err.println(
            "Error in atom types file: no such atom property: " + propType);
        continue;
      }

      if (allTypes.containsKey(atomType)) {
        newType = allTypes.get(atomType);
      }
      else {
        try {
          newType = new CCTAtomTypes(atomType, 1);
          allTypes.put(atomType, newType);
        }
        catch (Exception ex) {
          System.err.println("CCTAtomTypes: program error: " +
                             ex.getMessage());
        }
      }

      // Getting base element

      if (propType.equalsIgnoreCase(BASE_ELEMENT_KEY)) {
        try {
          typesStr = resources.getString(key);
          int el = ChemicalElements.getAtomicNumber(typesStr);
          if (el == 0) {
            System.err.println("Warning: suspicious element: " + typesStr +
                               " for atom type " + atomType);
          }
          newType.setElement(el);
          newType.setCovalentRadius(ChemicalElements.getCovalentRadius(el));
        }
        catch (Exception ex) {
          System.err.println("Parsing atom type: " + atomType + " : " +
                             ex.getMessage() +
                             "\n Setting element to dummy");
          newType.setElement(0);
          newType.setCovalentRadius(ChemicalElements.getCovalentRadius(0));
        }
      }

      // --- Getting default type

      else if (propType.equalsIgnoreCase(CCTAtomTypes.DEFAULT_TYPE_KEY)) {
        try {
          typesStr = resources.getString(key);
          newType.setDefaultType(Boolean.parseBoolean(typesStr));
        }
        catch (Exception ex) {
          System.err.println("Parsing atom type: " + atomType + " : " +
                             ex.getMessage() +
                             "\n Trying to parse 'default' option (should be 'true' or 'false'). Got: " +
                             typesStr + "\nSet to 'false'");
        }
      }

      // --- Getting coordination number

      else if (propType.equalsIgnoreCase(CCTAtomTypes.COORDINATION_NUMBER_KEY)) {
        int coordN = 0;
        try {
          typesStr = resources.getString(key);

          try {
            coordN = Integer.parseInt(typesStr);
          }
          catch (NumberFormatException ex) {
            System.err.println(
                "Warning: suspicious coordination number : " +
                typesStr +
                " for atom type " + atomType + "\nSet to 0");
            coordN = 0;
          }
          if (coordN < 0) {
            System.err.println(
                "Warning: suspicious coordination number : " +
                coordN +
                " for atom type " + atomType + "\nSet to 0");
          }
          newType.setCoordinationNumber(coordN);
        }
        catch (Exception ex) {
          System.err.println("Parsing atom type: " + atomType + " : " +
                             ex.getMessage() +
                             "\n Setting coordination number to 0");
          newType.setCoordinationNumber(0);
        }
      }

      // --- Getting geometry

      else if (propType.equalsIgnoreCase(CCTAtomTypes.GEOMETRY_KEY)) {
        try {
          typesStr = resources.getString(key);

          AtomGeometry geom = AtomGeometry.getAtomGeometry(typesStr);
          if (geom == null) {
            System.err.println("Unknown geometry: " + typesStr);
          }
          else {
            newType.setAtomGeometry(geom);
          }
          /*
           if (typesStr.equalsIgnoreCase(LINEAR_GEOMETRY)) {
             newType.setGeometry(LINEAR);
                          }
           else if (typesStr.equalsIgnoreCase(TETRAHEDRAL_GEOMETRY)) {
             newType.setGeometry(TETRAHEDRAL);
                          }
           else if (typesStr.equalsIgnoreCase(TRIGONAL_GEOMETRY)) {
             newType.setGeometry(TRIGONAL);
                          }
                          else {
             int coordN = newType.getCoordinationNumber();
             System.err.println("Warning: wrong geometry : " + typesStr +
                                " for atom type " + atomType);
             if (coordN <= 1) {
                System.err.println("Set to " + LINEAR_GEOMETRY);
                newType.setGeometry(LINEAR);
             }
             else if (coordN <= 2) {
                System.err.println("Set to " + LINEAR_GEOMETRY);
                newType.setGeometry(LINEAR);
             }
             else if (coordN <= 3) {
                System.err.println("Set to " + TRIGONAL_GEOMETRY);
                newType.setGeometry(TRIGONAL);
             }
             else {
                System.err.println("Set to " + TETRAHEDRAL_GEOMETRY);
                newType.setGeometry(TETRAHEDRAL);
             }
                          }
           */
        }
        catch (Exception ex) {
          System.err.println("Parsing atom type: " + atomType + " : " +
                             ex.getMessage());
          /*
                          int coordN = newType.getCoordinationNumber();
                          if (coordN <= 1) {
             System.err.println("Set to " + LINEAR_GEOMETRY);
             newType.setGeometry(LINEAR);
                          }
                          else if (coordN <= 2) {
             System.err.println("Set to " + LINEAR_GEOMETRY);
             newType.setGeometry(LINEAR);
                          }
                          else if (coordN <= 3) {
             System.err.println("Set to " + TRIGONAL_GEOMETRY);
             newType.setGeometry(TRIGONAL);
                          }
                          else {
             System.err.println("Set to " + TETRAHEDRAL_GEOMETRY);
             newType.setGeometry(TETRAHEDRAL);
                          }
           */
        }
      }

      // --- Getting max number of Hydrogens
      /*
                else if (propType.equalsIgnoreCase(this.MAX_SINGLE_BONDS_KEY)) {
         int maxH = 0;
         try {
            typesStr = resources.getString(key);

            try {
               maxH = Integer.parseInt(typesStr);
            }
            catch (NumberFormatException ex) {
               System.err.println("Warning: suspicious max H's number : " +
                                  typesStr +
                                  " for atom type " + atomType +
                                  "\nSet to 0");
               maxH = 0;
            }
            if (maxH < 0) {
               System.err.println("Warning: suspicious max H's number : " +
                                  maxH +
                                  " for atom type " + atomType +
                                  "\nSet to 0");
            }
            newType.setMaxHNumber(maxH);
         }
         catch (Exception ex) {
            System.err.println("Parsing atom type: " + atomType + " : " +
                               ex.getMessage() +
                               "\n Setting max H's number to 0");
            newType.setMaxHNumber(0);
         }
                }
       */
      // --- Getting vdw well depth

      else if (propType.equalsIgnoreCase(CCTAtomTypes.VDW_DI_KEY)) {
        float Dij = 0; // = ChemicalElements.getUFFWellDepth(el);

        try {
          typesStr = resources.getString(key);
          try {
            Dij = Float.parseFloat(typesStr);
          }
          catch (NumberFormatException ex) {
            int el = newType.getElement();
            Dij = ChemicalElements.getUFFWellDepth(el);
            System.err.println(
                "Warning: suspicious van-der-Waals well depth : " +
                typesStr +
                " for atom type " + atomType + "\nSet to " + Dij +
                " for corresponding element");
          }
        }
        catch (Exception ex) {

        }

        newType.setVDWWellDepth(Dij);
      }

      // --- Getting vdw distance

      else if (propType.equalsIgnoreCase(CCTAtomTypes.VDW_RI_KEY)) {
        float Dij = 0; // = ChemicalElements.getUFFRadius(el);

        try {
          typesStr = resources.getString(key);

          try {
            Dij = Float.parseFloat(typesStr);
          }
          catch (NumberFormatException ex) {
            int el = newType.getElement();
            Dij = ChemicalElements.getUFFRadius(el);
            System.err.println(
                "Warning: suspicious van-der-Waals distance : " +
                typesStr +
                " for atom type " + atomType + "\nSet to " + Dij +
                " for corresponding element");
          }
        }
        catch (Exception ex) {

        }

        newType.setVDWDIstance(Dij);
      }

      else {
        System.err.println(
            "Program error: should not be here for property: " + propType);
      }

    }

    // --- Do element mapping and set default values

    Set set = allTypes.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String Key = me.getKey().toString();
      newType = (CCTAtomTypes) me.getValue();

      String elem = ChemicalElements.getElementSymbol(newType.getElement());

      if (newType.getVDWWellDepth() <= unsetDiValue) {
        float Dij = ChemicalElements.getUFFWellDepth(newType.getElement());
        newType.setVDWWellDepth(Dij);
      }
      if (newType.getVDWDIstance() <= unsetDiValue) {
        float Dij = ChemicalElements.getUFFRadius(newType.getElement());
        newType.setVDWDIstance(Dij);
      }

      if (elementMapping.containsKey(elem)) {
        Map map = (Map) elementMapping.get(elem);
        map.put(Key, newType);
        elementMapping.put(elem, map);
      }
      else {
        Map map = new LinkedHashMap();
        map.put(Key, newType);
        elementMapping.put(elem, map);
      }

    }

    // ---Stupid thing... Sort atom types...

    set = elementMapping.entrySet();
    iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String Key = me.getKey().toString();
      Map map = (Map) me.getValue();
      Object toSort[] = new Object[map.size()];
      if (debug) {
        System.out.print("Element: " + Key + ": atom types:");
      }
      Set set2 = map.entrySet();
      Iterator iter2 = set2.iterator();
      int count = 0;
      while (iter2.hasNext()) {
        Map.Entry me2 = (Map.Entry) iter2.next();
        if (debug) {
          System.out.print(" " + me2.getKey().toString());
        }
        toSort[count++] = me2.getValue();
      }
      if (debug) {
        System.out.print("\n");
      }
      if (count == 0) {
        continue;
      }

      Arrays.sort(toSort, this);
      Map newMap = new LinkedHashMap(toSort.length);
      for (int i = 0; i < toSort.length; i++) {
        CCTAtomTypes type = (CCTAtomTypes) toSort[i];
        newMap.put(type.getAtomTypeName(), type);
      }
      elementMapping.put(Key, newMap);
    }

  }

  public boolean isDefaultType() {
    return this.defaultType;
  }

  public void setDefaultType(boolean enable) {
    defaultType = enable;
  }

  public static ImageIcon getImageIcon(CCTAtomTypes atomType) {
    return atomType.getIcon();
    //return getImageIcon(atomType.getGeometry(), atomType.getMaxSingleBonds());
  }

  /*
      public static ImageIcon getImageIcon(int geometry, int maxSingle) {
     if (geometry == TETRAHEDRAL && maxSingle == 4) {
        return tetrahedral_4_Icon;
     }
     else if (geometry == TETRAHEDRAL && maxSingle == 3) {
        return tetrahedral_3_Icon;
     }
     else if (geometry == TETRAHEDRAL && maxSingle == 2) {
        return tetrahedral_2_Icon;
     }


     else if (geometry == TRIGONAL && maxSingle == 3) {
        return trigonal_3_Icon;
     }

     else if (geometry == TRIGONAL && maxSingle == 2) {
        return trigonal_3_Icon;
     }

     return emptyIcon;
      }
   */

  public static int getAtomTypesCount() {
    return allTypes.size();
  }

  public static boolean isValidCCTType(String type) {
    if (allTypes == null) {
      return false;
    }
    return allTypes.containsKey(type);
  }

  public static CCTAtomTypes getAtomTypeInfo(String type) {
    if (allTypes == null) {
      return null;
    }
    if (allTypes.containsKey(type)) {
      CCTAtomTypes info = allTypes.get(type);
      return info;
    }
    return null;
  }

  public static CCTAtomTypes[] getAtomTypesForElement(int elem) {
    return getAtomTypesForElement(ChemicalElements.getElementSymbol(elem));
  }

  public static CCTAtomTypes[] getAtomTypesForElement(String elem) {
    if (!elementMapping.containsKey(elem)) {
      return null;
    }
    Map map = (Map) elementMapping.get(elem);
    if (map.size() == 0) {
      return null;
    }
    CCTAtomTypes[] types = new CCTAtomTypes[map.size()];
    Set set = map.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      types[count] = (CCTAtomTypes) me.getValue();
      ++count;
    }
    return types;
  }

  public void setElement(int elem) {
    chemicalElement = elem;
  }

  public int getElement() {
    return chemicalElement;
  }

  public static Map getElementMapping() {
    return elementMapping;
  }

  public static Map getAtomTypes() {
    return allTypes;
  }

  //public int getGeometry() {
  //   return geometry;
  //}

  //public void setGeometry(int n) {
  //   geometry = n;
  //}



  public float getCovalentRadius() {
    return covalentRadius;
  }

  public void setCovalentRadius(float radius) {
    covalentRadius = radius;
  }

  public void setVDWWellDepth(float d) {
    vdwWellDepth = d;
  }

  public float getVDWWellDepth() {
    return vdwWellDepth;
  }

  public void setVDWDIstance(float d) {
    vdwDistance = d;
  }

  public float getVDWDIstance() {
    return vdwDistance;
  }

  public String getAtomTypeName() {
    return typeName;
  }

  private CCTAtomTypes(String name, int element) throws Exception {
    if (allTypes.containsKey(name)) {
      throw new Exception("Atom type " + name + " is already in the table");
    }
    this.typeName = name;
    this.chemicalElement = element;
    this.weight = ChemicalElements.getAtomicWeight(element);
    //allTypes.put(typeName, this);
  }

  private CCTAtomTypes(String name, int element, int coordNumber // int geometry,
      ) throws Exception {
    if (allTypes.containsKey(name)) {
      throw new Exception("Atom type " + name + " is already in the table");
    }
    //else if (geometry != LINEAR && geometry != TETRAHEDRAL &&
    //         geometry != TRIGONAL) {
    //   throw new Exception("Atom type: " + name + " : wrong geometry");
    //}

    this.typeName = name;
    this.chemicalElement = element;
    this.weight = ChemicalElements.getAtomicWeight(element);
    //this.coordNumber = coordNumber;
    //this.geometry = geometry;
    //this.maxHNumber = maxHNumber;
    allTypes.put(typeName, this);
  }

  public static Map getPictureMapping(int element) {
    String currentType = "Atom";
    Map atomTypes = new LinkedHashMap();
    //atomTypes.put(currentType, CCTAtomTypes.getImageIcon(0, 0));
    atomTypes.put(currentType, AtomGeometry.ATOM_ICON);

    Map mapping = getElementMapping();
    Map map = (Map) mapping.get(ChemicalElements.getElementSymbol(element));

    if (map != null) {
      int count = 0;
      Set set = map.entrySet();
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String Key = me.getKey().toString();
        CCTAtomTypes Type = (CCTAtomTypes) me.getValue();

        atomTypes.put(Key, CCTAtomTypes.getImageIcon(Type));

        ++count;
      }
    }
    return atomTypes;
  }

//public static final CCTParserEnum CCT_TAG = new CCTParserEnum("cct");

  public static void main(String[] args) {
    //CCTAtomTypes cctatomtypes = new CCTAtomTypes();
    logger.info("Number of atom types: " +
                       CCTAtomTypes.getAtomTypesCount());
    Integer count[] = {
        0, 1};
    logger.info("Class: " + count.getClass().getCanonicalName() + " " +
                       count.getClass().getSimpleName() + " " +
                       count.getClass().getInterfaces());
    List a = new ArrayList();
    logger.info("Class: " + a.getClass().getCanonicalName() + " " +
                       a.getClass().getSimpleName());
    Class in[] = a.getClass().getInterfaces();
    for (int i = 0; i < in.length; i++) {
      logger.info("Interface: " + i + " : " + in[i].getCanonicalName());
    }
  }

  @Override
  public int compare(Object o1, Object o2) throws ClassCastException {
    if (o1 instanceof CCTAtomTypes && o2 instanceof CCTAtomTypes) {
      CCTAtomTypes a1 = (CCTAtomTypes) o1;
      CCTAtomTypes a2 = (CCTAtomTypes) o2;
      if (a1.getGeometry() != a2.getGeometry()) {
        return a1.getGeometry() - a2.getGeometry();
      }

      if (a1.getCoordinationNumber() != a2.getCoordinationNumber()) {
        return a1.getCoordinationNumber() - a2.getCoordinationNumber();
      }

      return a1.getMaxSingleBonds() - a2.getMaxSingleBonds();
    }
    throw new ClassCastException("CCTAtomTypes: Class Cast Exception");
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }
}
