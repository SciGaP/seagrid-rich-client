package cct.modelling;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cct.GlobalSettings;
import cct.interfaces.CoordinateBuilderInterface;
import cct.interfaces.CoordinateParserInterface;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FormatManager {

  public static final String SIMPLE_COORDINATE_BUILDERS_TAG = "simpleCoordinateBuilders";
  public static final String SIMPLE_COORDINATE_BUILDER_TAG = "simpleCoordinateBuilder";
  public static final String SIMPLE_COORDINATE_PARSER_TAG = "simpleCoordinateParser";
  public static final String SIMPLE_COORDINATE_NAME_TAG = "name";
  public static final String SIMPLE_COORDINATE_DESC_TAG = "desc";
  public static final String SIMPLE_COORDINATE_CLASS_TAG = "class";

  public static boolean debug = true;
  static final Logger logger = Logger.getLogger(FormatManager.class.getCanonicalName());

  private static Map<String, CoordBuilder> coordBuilders = null;
  private static Map<String, CoordParser> coordParsers = null;

  public FormatManager() {
  }

  public static void main(String[] args) {
    FormatManager formatmanager = new FormatManager();
  }

  public static CoordinateBuilderInterface getCoordinateBuilder(String builder) throws Exception {
    if (coordBuilders == null || coordBuilders.size() < 1) {
      try {
        getSimpleCoordinateBuilders();
      }
      catch (Exception ex) {
        System.err.println("Encountered some errors while getting builder list: " + ex.getMessage());
      }
    }

    if (coordBuilders == null || coordBuilders.size() < 1) {
      throw new Exception("Cannot get list of coordinate builders");
    }

    if (!coordBuilders.containsKey(builder)) {
      throw new Exception("No such coordinate builder: " + builder);
    }

    CoordBuilder cBuilder = coordBuilders.get(builder);
    CoordinateBuilderInterface cbi = (CoordinateBuilderInterface) cBuilder.builder;
    return cbi;
  }

  public static CoordinateParserInterface getCoordinateParser(String builder) throws Exception {
    if (coordParsers == null || coordParsers.size() < 1) {
      try {
        getSimpleCoordinateParsers();
      }
      catch (Exception ex) {
        System.err.println("Encountered some errors while getting parsers list: " + ex.getMessage());
      }
    }

    if (coordParsers == null || coordParsers.size() < 1) {
      throw new Exception("Cannot get list of coordinate Parsers");
    }

    if (!coordParsers.containsKey(builder)) {
      throw new Exception("No such coordinate parser: " + builder);
    }

    CoordParser cParser = coordParsers.get(builder);
    CoordinateParserInterface cpi = (CoordinateParserInterface) cParser.parser;
    return cpi;
  }

  public static String[] getCoordinateBuilders() {
    if (coordBuilders == null || coordBuilders.size() < 1) {
      try {
        getSimpleCoordinateBuilders();
      }
      catch (Exception ex) {
        System.err.println("Encountered some errors while getting builder list: " + ex.getMessage());
      }
    }

    if (coordBuilders == null || coordBuilders.size() < 1) {
      return null;
    }
    String[] builders = new String[coordBuilders.size()];
    coordBuilders.keySet().toArray(builders);
    return builders;
  }

  public static String[] getCoordinateParsers() {
    if (coordParsers == null || coordParsers.size() < 1) {
      try {
        getSimpleCoordinateParsers();
      }
      catch (Exception ex) {
        System.err.println("Encountered some errors while getting parser list: " + ex.getMessage());
      }
    }

    if (coordParsers == null || coordParsers.size() < 1) {
      return null;
    }
    String[] parsers = new String[coordParsers.size()];
    coordParsers.keySet().toArray(parsers);
    return parsers;
  }

  public static void getSimpleCoordinateBuilders() throws Exception {
    getSimpleCoordinateBuilders(GlobalSettings.FORMATS_XML_FILE);
  }

  public static void getSimpleCoordinateParsers() throws Exception {
    getSimpleCoordinateParsers(GlobalSettings.FORMATS_XML_FILE);
  }

  public static void getSimpleCoordinateBuilders(String file_name) throws Exception {

    if (coordBuilders == null) {
      coordBuilders = new HashMap<String, CoordBuilder> ();
    }

    //URL builderURL = null;
    InputStream is = null;
    try {
      ClassLoader cl = FormatManager.class.getClassLoader();
      //logger.info("Class loader: " + cl.toString());
      //builderURL = cl.getResource(file_name);
      is = cl.getResourceAsStream(file_name);
    }
    catch (Exception ex) {
      throw new Exception("getSimpleCoordinateBuilders: cannot get resource : " + file_name + " : " + ex.getMessage());
    }

    /*
           File file = new File(builderURL.getFile());
           if (!file.exists()) {
       throw new Exception("Error: file " + file_name + " with description of simple coordinate builders is not exists");
           }
     */

    /*
     <?xml version="1.0" encoding="UTF-8"?>
     <simpleCoordinateBuilders>
       <simpleCoordinateBuilder name="XMol XYZ" desc="XMol XYZ Format" class="cct.siesta.SiestaInput" />
     </simpleCoordinateBuilders>
     */
    // ---  Using DOM
    Document doc = null;
    try {
      // Create a factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Use the factory to create a builder
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(is);
    }

    catch (Exception ex) {
      throw new Exception("Error parsing file " + file_name + " with description of simple coordinate builders: " +
                          ex.getMessage());
    }

    NodeList list = doc.getElementsByTagName(SIMPLE_COORDINATE_BUILDER_TAG);
    for (int i = 0; i < list.getLength(); i++) {

      // Get element
      Node node = list.item(i);
      if (debug) {
        logger.info(node.getNodeName() + " : " + node.getNodeValue());
      }

      NamedNodeMap attr = node.getAttributes();

      if (attr == null || attr.getLength() < 1) {
        continue;
      }

      // --- Get Name

      Node nameN = attr.getNamedItem(SIMPLE_COORDINATE_NAME_TAG);

      if (nameN == null) {
        System.err.println("getSimpleCoordinateBuilders: no name for builder. Ignored...");
        continue;
      }

      if (nameN.getNodeType() != Node.ATTRIBUTE_NODE) {
        System.err.println("getSimpleCoordinateBuilders: name is not attribute. Ignored...");
        continue;
      }

      String name = "";
      try {
        name = nameN.getNodeValue();
      }
      catch (Exception ex) {
        System.err.println("getSimpleCoordinateBuilders: error getting name: " + ex.getMessage() + " Ignored...");
        continue;
      }

      if (coordBuilders.containsKey(name)) {
        System.err.println("Coordinate Builder " + name + " is already in Table. Ignored...");
        continue;
      }

      // --- Get Description

      String desc = "";
      Node descN = attr.getNamedItem(SIMPLE_COORDINATE_DESC_TAG);

      if (descN != null) {
        if (descN.getNodeType() != Node.ATTRIBUTE_NODE) {
          System.err.println("getSimpleCoordinateBuilders: description is not an attribute. Ignored...");
        }
        else {
          try {
            desc = descN.getNodeValue();
          }
          catch (Exception ex) {
            System.err.println("getSimpleCoordinateBuilders: error getting description: " + ex.getMessage() +
                               " Ignored...");
          }
        }
      }

      // --- Get class

      Node classN = attr.getNamedItem(SIMPLE_COORDINATE_CLASS_TAG);

      if (classN == null) {
        System.err.println("getSimpleCoordinateBuilders: no class for builder. Ignored...");
        continue;
      }

      if (classN.getNodeType() != Node.ATTRIBUTE_NODE) {
        System.err.println("getSimpleCoordinateBuilders: class is not attribute. Ignored...");
        continue;
      }

      // -- Load class

      String className = "";
      try {
        className = classN.getNodeValue();
      }
      catch (Exception ex) {
        System.err.println("getSimpleCoordinateBuilders: error getting class name: " + ex.getMessage() + " Ignored...");
        continue;
      }

      if (debug) {
        logger.info("Loading class " + className);
      }

      Class builderClass = null;
      ClassLoader loader = null;
      try {
        // Get the Class object associated with builder
        builderClass = Class.forName(className);

        // Get the ClassLoader object associated with this Class.
        loader = builderClass.getClassLoader();

        if (loader == null) {
          System.err.println("loader == null. Ignored...");
          continue;
        }
        else if (debug) {
          // Verify that this ClassLoader is associated with the builder class.
          Class loaderClass = loader.getClass();

          logger.info("Class associated with ClassLoader: " + loaderClass.getName());
        }
      }
      catch (ClassNotFoundException ex) {
        System.err.println("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
        continue;
      }

      Class cl = loader.loadClass(className);
      Object obj = cl.newInstance();
      if (debug) {
        logger.info("Classr " + className + " was loaded");
      }

      if (! (obj instanceof CoordinateBuilderInterface)) {
        System.err.println("Class " + className + " does not implement " + CoordinateBuilderInterface.class.getName() +
                           " interface. Ignored...");
        continue;
      }

      CoordBuilder cBuilder = new CoordBuilder(name, desc, obj);
      coordBuilders.put(name, cBuilder);
    }

  }

  public static void getSimpleCoordinateParsers(String file_name) throws Exception {

    if (coordParsers == null) {
      coordParsers = new HashMap<String, CoordParser> ();
    }

    //URL builderURL = null;
    InputStream is = null;
    try {
      ClassLoader cl = FormatManager.class.getClassLoader();
      //logger.info("Class loader: " + cl.toString());
      //builderURL = cl.getResource(file_name);
      is = cl.getResourceAsStream(file_name);
    }
    catch (Exception ex) {
      throw new Exception("getSimpleCoordinateParser: cannot get resource : " + file_name + " : " + ex.getMessage());
    }

    /*
           File file = new File(builderURL.getFile());
           if (!file.exists()) {
       throw new Exception("Error: file " + file_name + " with description of simple coordinate builders is not exists");
           }
     */

    /*
     <?xml version="1.0" encoding="UTF-8"?>
     <simpleCoordinateBuilders>
       <simpleCoordinateBuilder name="XMol XYZ" desc="XMol XYZ Format" class="cct.siesta.SiestaInput" />
     </simpleCoordinateBuilders>
     */
    // ---  Using DOM
    Document doc = null;
    try {
      // Create a factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Use the factory to create a builder
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(is);
    }

    catch (Exception ex) {
      throw new Exception("Error parsing file " + file_name + " with description of simple coordinate parsers: " +
                          ex.getMessage());
    }

    NodeList list = doc.getElementsByTagName(SIMPLE_COORDINATE_PARSER_TAG);
    for (int i = 0; i < list.getLength(); i++) {

      // Get element
      Node node = list.item(i);
      if (debug) {
        logger.info(node.getNodeName() + " : " + node.getNodeValue());
      }

      NamedNodeMap attr = node.getAttributes();

      if (attr == null || attr.getLength() < 1) {
        continue;
      }

      // --- Get Name

      Node nameN = attr.getNamedItem(SIMPLE_COORDINATE_NAME_TAG);

      if (nameN == null) {
        System.err.println("getSimpleCoordinateParsers: no name for Parser. Ignored...");
        continue;
      }

      if (nameN.getNodeType() != Node.ATTRIBUTE_NODE) {
        System.err.println("getSimpleCoordinateParsers: name is not attribute. Ignored...");
        continue;
      }

      String name = "";
      try {
        name = nameN.getNodeValue();
      }
      catch (Exception ex) {
        System.err.println("getSimpleCoordinateParsers: error getting name: " + ex.getMessage() + " Ignored...");
        continue;
      }

      if (coordParsers.containsKey(name)) {
        System.err.println("Coordinate Parser " + name + " is already in Table. Ignored...");
        continue;
      }

      // --- Get Description

      String desc = "";
      Node descN = attr.getNamedItem(SIMPLE_COORDINATE_DESC_TAG);

      if (descN != null) {
        if (descN.getNodeType() != Node.ATTRIBUTE_NODE) {
          System.err.println("getSimpleCoordinateParsers: description is not an attribute. Ignored...");
        }
        else {
          try {
            desc = descN.getNodeValue();
          }
          catch (Exception ex) {
            System.err.println("getSimpleCoordinateParsers: error getting description: " + ex.getMessage() +
                               " Ignored...");
          }
        }
      }

      // --- Get class

      Node classN = attr.getNamedItem(SIMPLE_COORDINATE_CLASS_TAG);

      if (classN == null) {
        System.err.println("getSimpleCoordinateParsers: no class for builder. Ignored...");
        continue;
      }

      if (classN.getNodeType() != Node.ATTRIBUTE_NODE) {
        System.err.println("getSimpleCoordinateBuilders: class is not attribute. Ignored...");
        continue;
      }

      // -- Load class

      String className = "";
      try {
        className = classN.getNodeValue();
      }
      catch (Exception ex) {
        System.err.println("getSimpleCoordinateParsers: error getting class name: " + ex.getMessage() + " Ignored...");
        continue;
      }

      if (debug) {
        logger.info("Loading class " + className);
      }

      Class builderClass = null;
      ClassLoader loader = null;
      try {
        // Get the Class object associated with builder
        builderClass = Class.forName(className);

        // Get the ClassLoader object associated with this Class.
        loader = builderClass.getClassLoader();

        if (loader == null) {
          System.err.println("loader == null. Ignored...");
          continue;
        }
        else if (debug) {
          // Verify that this ClassLoader is associated with the builder class.
          Class loaderClass = loader.getClass();

          logger.info("Class associated with ClassLoader: " + loaderClass.getName());
        }
      }
      catch (ClassNotFoundException ex) {
        System.err.println("Cannot load class " + className + " : " + ex.getMessage() + " Ignored...");
        continue;
      }

      Class cl = loader.loadClass(className);
      Object obj = cl.newInstance();
      if (debug) {
        logger.info("Class " + className + " was loaded");
      }

      if (! (obj instanceof CoordinateParserInterface)) {
        System.err.println("Class " + className + " does not implement " + CoordinateParserInterface.class.getName() +
                           " interface. Ignored...");
        continue;
      }

      CoordParser cBuilder = new CoordParser(name, desc, obj);
      coordParsers.put(name, cBuilder);
    }
  }

  public static String guessSimpleCoordinateParser(String coordinates) {
    if (coordParsers == null || coordParsers.size() < 1) {
      try {
        getSimpleCoordinateParsers();
      }
      catch (Exception ex) {
        return null;
      }
    }

    if (coordParsers == null || coordParsers.size() < 1) {
      return null;
    }

    ParserScore[] parsers = new ParserScore[coordParsers.size()];
    Set set = coordParsers.entrySet();
    Iterator iter = set.iterator();
    int count = 0;
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      CoordParser parser = (CoordParser) me.getValue();
      BufferedReader in = new BufferedReader(new StringReader(coordinates));
      CoordinateParserInterface cpi = (CoordinateParserInterface) parser.parser;
      double score = 0;
      try {
        score = cpi.evaluateCompliance(in);
      }
      catch (Exception ex) {}

      parsers[count] = new ParserScore(parser, score);
      count++;
    }

    Arrays.sort(parsers, new ParserComparator());

    return parsers[0].parser.name;
  }

  static class ParserScore {
    CoordParser parser;
    double score;
    public ParserScore(CoordParser parser, double score) {
      this.parser = parser;
      this.score = score;
    }
  }

  static class ParserComparator
      implements Comparator {
    /**
     * Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     * @param o1 Object - ParserScore object
     * @param o2 Object - ParserScore object
     * @return int
     */
    @Override
    public int compare(Object o1, Object o2) {
      if ( ( (ParserScore) o1).score == ( (ParserScore) o2).score) {
        return 0;
      }
      if ( ( (ParserScore) o1).score < ( (ParserScore) o2).score) {
        return 1;
      }
      return -1;
    }
  }
}
