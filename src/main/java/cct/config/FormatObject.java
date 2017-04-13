/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.config;

import cct.modelling.AbstractDataParser;
import cct.tools.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vvv900
 */
public class FormatObject {

  static final String NAME = "name";
  static final String DESCRIPTION = "desc";
  static final String EXTENSIONS = "ext";
  static final String PARSER = "parser";
  public static final String FORMATS_TAG = "formats";
  public static final String FORMAT_TAG = "format";
  static final String FORMAT_NODES_PATH = FORMATS_TAG + "/" + FORMAT_TAG;
  static final Logger logger = Logger.getLogger(FormatObject.class.getCanonicalName());
  private String name, description, extensions;
  private Object parser;
  private static final String formatsPathExpr = "//" + FORMATS_TAG + "/" + FORMAT_TAG;
  private static final String nameXPath = "@" + NAME;
  private static final String descXPath = "@" + DESCRIPTION;
  private static final String extXPath = "@" + EXTENSIONS;
  private static final String parserXPath = "@" + PARSER;
  private static XPath xPath;
  private static XPathExpression formatsPath, namePath, descPath, extPath, parserPath;

  public FormatObject(String name, String description, String extensions, String parserClass) {
    this.name = name;
    this.description = description;
    this.extensions = extensions;
    if (parserClass != null && parserClass.length() > 0) {
      try {
        logger.info("Loading class " + parserClass + "...");
        parser = Utils.loadClass(parserClass);
        logger.info("Loaded class " + parserClass + "...");
        if (parser instanceof AbstractDataParser) {
          if (name != null && name.trim().length() > 0) {
            ((AbstractDataParser) parser).setName(name);
          }
          if (description != null && description.trim().length() > 0) {
            ((AbstractDataParser) parser).setDescription(description);
          }
          if (extensions != null && extensions.trim().length() > 0) {
            ((AbstractDataParser) parser).setExtensions(extensions);
          }
        }
      } catch (Exception ex) {
        logger.severe("Cannot load class " + parserClass + " : " + ex.getMessage());
      }
    }
  }

  public AbstractDataParser newParserInstance() throws Exception {
    AbstractDataParser prsr = (AbstractDataParser) parser.getClass().newInstance();
    prsr.setDescription(((AbstractDataParser) parser).getDescription());
    prsr.setExtensions(((AbstractDataParser) parser).getExtensions());
    prsr.setName(((AbstractDataParser) parser).getName());
    prsr.setUseDialog(((AbstractDataParser) parser).isUseDialog());
    return prsr;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExtensions() {
    return extensions;
  }

  public void setExtensions(String extensions) {
    this.extensions = extensions;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getParser() {
    return parser;
  }

  public void setParser(Object parser) {
    this.parser = parser;
  }

  public static Map<String, FormatObject> getFormatObjects(Document doc) throws Exception {
    if (doc == null) {
      throw new Exception("doc == null");
    }

    if (xPath == null) {
      xPath = XPathFactory.newInstance().newXPath();
      formatsPath = xPath.compile(formatsPathExpr);
      namePath = xPath.compile(nameXPath);
      descPath = xPath.compile(descXPath);
      extPath = xPath.compile(extXPath);
      parserPath = xPath.compile(parserXPath);
    }

    NodeList list = (NodeList) formatsPath.evaluate(doc, XPathConstants.NODESET);
    //doc.getElementsByTagName("./" + FORMAT_NODES_PATH);
    if (list.getLength() < 1) {
      throw new Exception("Document does not contain path " + formatsPathExpr);
    }

    Map<String, FormatObject> forms = new HashMap<String, FormatObject>();

    for (int i = 0; i < list.getLength(); i++) {

      // Get element
      Node node = list.item(i);

      logger.info(node.getNodeName() + " : " + node.getNodeValue());

      NamedNodeMap attr = node.getAttributes();

      if (attr == null || attr.getLength() < 1) {
        continue;
      }

      // --- Get Name

      Node nameN = attr.getNamedItem(NAME);

      if (nameN == null) {
        logger.warning("No name for format. Ignored...");
        continue;
      }

      if (nameN.getNodeType() != Node.ATTRIBUTE_NODE) {
        logger.warning("Name is not an attribute. Ignored...");
        continue;
      }

      String name = "";
      try {
        name = nameN.getNodeValue();
      } catch (Exception ex) {
        logger.warning("Error getting name: " + ex.getMessage() + " Ignored...");
        continue;
      }

      if (forms.containsKey(name.toUpperCase())) {
        logger.warning("Coordinate Builder " + name + " is already in Table. Ignored...");
        continue;
      }

      // --- Get Description

      String desc = "";
      Node descN = attr.getNamedItem(DESCRIPTION);

      if (descN != null) {
        if (descN.getNodeType() != Node.ATTRIBUTE_NODE) {
          logger.warning("Description is not an attribute. Ignored...");
        } else {
          try {
            desc = descN.getNodeValue();
          } catch (Exception ex) {
            logger.warning("Error getting description: " + ex.getMessage() + " Ignored...");
          }
        }
      }

      // --- Get Extensions

      String exts = "";
      Node extN = attr.getNamedItem(EXTENSIONS);

      if (extN != null) {
        if (extN.getNodeType() != Node.ATTRIBUTE_NODE) {
          logger.warning("Extensions is not an attribute. Ignored...");
        } else {
          try {
            exts = extN.getNodeValue();
          } catch (Exception ex) {
            logger.warning("Error getting extenstion: " + ex.getMessage() + " Ignored...");
          }
        }
      }

      // --- Get parser

      String parser = "";
      Node parN = attr.getNamedItem(PARSER);

      if (parN != null) {
        if (parN.getNodeType() != Node.ATTRIBUTE_NODE) {
          logger.warning("Parser is not an attribute. Ignored...");
        } else {
          try {
            parser = parN.getNodeValue();
          } catch (Exception ex) {
            logger.warning("Error getting parser: " + ex.getMessage() + " Ignored...");
          }
        }
      }

      FormatObject fo = new FormatObject(name, desc, exts, parser);
      forms.put(name.toUpperCase(), fo);
    }

    return forms;
  }
}
