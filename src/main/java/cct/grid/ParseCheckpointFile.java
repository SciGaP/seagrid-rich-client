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
package cct.grid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import cct.tools.IOUtils;
import cct.tools.ui.JobProgressInterface;

/**
 * <p>Title: Molecular Structure Viewer/Editor</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class ParseCheckpointFile {

  static String checkPointFile = "checkpoint.xml";
  static final String CHECKPOINT_FILE = "checkpointFile";
  /*
  final static HashMap firstLevelTags = CheckPoint.getCheckPointSpecifications();
  final static HashMap serviceTags = (HashMap)firstLevelTags.get(serviceTag);
  final static HashMap JobSpecificationTags = (HashMap)firstLevelTags.get(JobSpecificationTag);
   */
  static boolean debug = false;
  boolean finish_parsing = false;
  boolean yes_tasks = false;
  boolean yes_parsing_task = false;
  int parseLevel = 0;
  static String Message = "";
  String currentTag = null;
  List tasks = new ArrayList();
  CheckPoint checkPoint = null;
  //HashMap taskEntries = null;
  Map serviceEntries = null;
  Map jobSpecificationEntries = null;
  Map outputFilesEntries = null;
  static private Writer out;
  private JobProgressInterface progress = null;
  private StatusUpdateTask statusUpdateTask;
  private int queryTime = 60000;
  static final Logger logger = Logger.getLogger(JobProgressInterface.class.getCanonicalName());

  public void setJobProgressInterface(JobProgressInterface jpi) {
    progress = jpi;
  }

  public int getQueryTime() {
    return queryTime;
  }

  public void setQueryTime(int queryTime) {
    this.queryTime = queryTime;
  }

  public JobProgressInterface getProgress() {
    return progress;
  }

  public void setProgress(JobProgressInterface progress) {
    this.progress = progress;
  }

  public ParseCheckpointFile() {
  }

  public void parseCheckpointFile() throws Exception {
    String checkpf = "";
    try {
      checkpf = getCheckPointFile();
    } catch (Exception ex) {
      throw ex;
    }
    parseCheckpointFile(checkpf);
  }

  public List getTasks() {
    return tasks;
  }

  public void parseCheckpointFile(String file_name) {
    Message = "";

    File chk = new File(file_name);
    if (!chk.exists()) {
      List temp = new ArrayList();
      try {
        saveCheckPointFile(temp, file_name);
      } catch (Exception ex) {
        System.err.println("Error: Cannot create checkpoint file: " + file_name
            + " : " + ex.getMessage());
      }
    }

    // ---  Using DOM
    try {
      // Create a factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Use the factory to create a builder
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(chk);
      // Get a list of all elements in the document
      NodeList list = doc.getElementsByTagName(CheckPointInterface.taskTag);
      for (int i = 0; i < list.getLength(); i++) {
        checkPoint = new CheckPoint();
        serviceEntries = new HashMap();
        jobSpecificationEntries = new HashMap();
        outputFilesEntries = new HashMap();

        // Get element
        Node node = list.item(i);
        if (debug) {
          logger.info(node.getNodeName() + " : " + node.getNodeValue());
        }

        NodeList taskNodes = node.getChildNodes();
        for (int j = 0; j < taskNodes.getLength(); j++) {
          Node taskNode = taskNodes.item(j);
          if (debug) {
            logger.info("Node: " + taskNode.getNodeName() + " Text content: " + taskNode.getTextContent());
          }
          if (taskNode.getNodeName().startsWith("#")) {
            continue;
          }

          if (taskNode.getNodeName().equals(CheckPointInterface.serviceTag)) { // Service tag
            NodeList serviceNodes = taskNode.getChildNodes();
            for (int k = 0; k < serviceNodes.getLength(); k++) {
              Node serviceNode = serviceNodes.item(k);
              if (serviceNode.getNodeName().startsWith("#")) {
                continue;
              }

              serviceEntries.put(serviceNode.getNodeName(), serviceNode.getTextContent());
              if (debug) {
                logger.info(" " + CheckPointInterface.serviceTag + ": " + serviceNode.getNodeName() + " : "
                    + serviceNode.getTextContent());
              }
            }
          } else if (taskNode.getNodeName().equals(CheckPointInterface.JobSpecificationTag)) { // JobSpecification tag
            NodeList jsNodes = taskNode.getChildNodes();
            for (int k = 0; k < jsNodes.getLength(); k++) {
              Node jsNode = jsNodes.item(k);
              if (jsNode.getNodeName().startsWith("#")) {
                continue;
              }

              if (jsNode.getNodeName().equals(CheckPointInterface.outputFilesTag)) { // outputFiles tag
                NodeList outFilesNodes = jsNode.getChildNodes();
                for (int kk = 0; kk < outFilesNodes.getLength(); kk++) {
                  Node outNode = outFilesNodes.item(kk);
                  if (outNode.getNodeName().startsWith("#")) {
                    continue;
                  }
                  outputFilesEntries.put(outNode.getNodeName(), outNode.getTextContent());
                  if (debug) {
                    logger.info("  " + CheckPointInterface.outputFilesTag + ": " + outNode.getNodeName() + " : "
                        + outNode.getTextContent());
                  }
                }
              } else {
                jobSpecificationEntries.put(jsNode.getNodeName(), jsNode.getTextContent());
                if (debug) {
                  logger.info(" " + CheckPointInterface.JobSpecificationTag + ": " + jsNode.getNodeName() + " : "
                      + jsNode.getTextContent());
                }
              }
            }
          } else {
            checkPoint.put(taskNode.getNodeName(), taskNode.getTextContent());
            if (debug) {
              logger.info(CheckPointInterface.taskTag + ": " + taskNode.getNodeName() + " : "
                  + taskNode.getTextContent());
            }
          }
        }

        /*
        Element element = (Element) list.item(i);
        logger.info(element.getNodeName());
        NodeList childNodes = element.getChildNodes();

        NodeList serviceNode = element.getElementsByTagName(serviceTag);
        if (serviceNode.getLength() > 0) {
        Element childElement = (Element) serviceNode.item(0);
        logger.info(childElement.getNodeName());
        NodeList serviceNodes = childElement.getElementsByTagName(handleTag);
        if (serviceNodes.getLength() > 0) {
        childElement = (Element) serviceNodes.item(0);
        serviceEntries.put(childElement.getNodeName(), childElement.getNodeValue());
        }
        }

        for (int j = 0; j < childNodes.getLength(); j++) {
        Object obj = childNodes.item(j);
        logger.info(obj.getClass().getCanonicalName());
        Element childElement = (Element) childNodes.item(j);
        logger.info(childElement.getNodeName());

        if (childElement.getNodeName().equals(serviceTag)) { // "service" tag
        NodeList serviceNodes = childElement.getChildNodes();
        for (int k = 0; k < serviceNodes.getLength(); k++) {
        Element serviceElement = (Element) serviceNodes.item(k);
        logger.info(serviceElement.getNodeName());
        serviceEntries.put(serviceElement.getNodeName(), serviceElement.getNodeValue());
        }
        }
        }
         */

        checkPoint.put(CheckPointInterface.serviceTag, serviceEntries);
        jobSpecificationEntries.put(CheckPointInterface.outputFilesTag, outputFilesEntries);
        checkPoint.put(CheckPointInterface.JobSpecificationTag, jobSpecificationEntries);
        tasks.add(checkPoint);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    if (true) {
      return;
    }

    // --- Using SAX...

    if (file_name.endsWith(".xml") || file_name.endsWith(".XML")) {
      // Use an instance of ourselves as the SAX event handler
      DefaultHandler handler = new HandleSAXEvents();
      // Use the default (non-validating) parser
      SAXParserFactory factory = SAXParserFactory.newInstance();

      try {
        // Set up output stream
        out = new OutputStreamWriter(System.out, "UTF8");

        // Parse the input
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(new File(file_name), handler);

      } catch (Throwable t) {
        //logger.error("Parsing error " + t.getMessage());
        t.printStackTrace();
      }
    }
  }

  public static String CreateCheckpointString(List Tasks) {
    String Content = "";
    Message = "";
    StringWriter sWriter = new StringWriter();

    StreamResult streamResult = new StreamResult(sWriter);
    SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

    // SAX2.0 ContentHandler.
    TransformerHandler hd = null;
    try {
      hd = tf.newTransformerHandler();
    } catch (TransformerConfigurationException e) {
      Message = e.getMessage();
      System.err.println(e.getMessage());
      return null;
    }
    Transformer serializer = hd.getTransformer();
    //serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
    //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "users.dtd");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    hd.setResult(streamResult);
    try {
      hd.startDocument();
      AttributesImpl atts = new AttributesImpl();
      //atts.addAttribute("","","ID","CDATA",id[i]);
      //atts.addAttribute("","","TYPE","CDATA",type[i]);

      // TASKS tag.
      hd.startElement("", "", CheckPointInterface.tasksTag, atts);

      for (int i = 0; i < Tasks.size(); i++) {
        Map Level_1 = (Map) Tasks.get(i);
        atts.clear();
        hd.startElement("", "", CheckPointInterface.taskTag, atts);

        //if (!createElements(hd, firstLevelTags, Level_1)) {
        if (!createElements(hd, CheckPoint.getCheckPointSpecifications(), Level_1)) {
          return null;
        }

        hd.endElement("", "", CheckPointInterface.taskTag);
      }

      hd.endElement("", "", CheckPointInterface.tasksTag);
      hd.endDocument();

    } catch (SAXException e) {
      Message = e.getMessage();
      System.err.println(e.getMessage());
      return null;
    }

    return sWriter.toString();
  }

  static public boolean createElements(TransformerHandler hd, Map list, Map elements) {
    AttributesImpl atts = new AttributesImpl();
    Set set = list.entrySet();
    Iterator iter = set.iterator();
    //Iterator iter = list.iterator();
    try {
      while (iter.hasNext()) {
        Map.Entry me = (Map.Entry) iter.next();
        String keyWord = me.getKey().toString();
        Object type = me.getValue();
        if (debug) {
          logger.info("Parsing: " + keyWord);
        }

        if (!elements.containsKey(keyWord)) {
          if (debug) {
            logger.info("  No such key in elements. Continuing...");
          }
          continue;
        }

        atts.clear();

        hd.startElement("", "", keyWord, atts);

        Object obj = elements.get(keyWord);

        if (type instanceof Map) {
          if (debug) {
            logger.info("  Key is an entry...");
          }
          if (obj instanceof Map) {
            Map parent = (Map) type;
            Map child = (Map) obj;
            if (!createElements(hd, parent, child)) {
              return false;
            }
          } else {
            Message +=
                "Instance of user's HashMap different from the reference one";
            System.err.println(Message);
            return false;
          }
        } else {
          Object xxx = elements.get(keyWord);
          if (debug) {
            logger.info("  Key is a value: " + xxx.toString());
          }
          String value = (String) elements.get(keyWord);
          hd.characters(value.toCharArray(), 0, value.length());
        }

        hd.endElement("", "", keyWord);
      }
    } catch (SAXException e) {
      Message = e.getMessage();
      System.err.println(e.getMessage());
      return false;
    }
    return true;
  }

  public static String getCheckPointFile() throws Exception {

    // ---

    try {
      Properties props = cct.GlobalSettings.getCustomProperties();

      //--- Checkpoint file location

      if (props != null) {
        String checkpoint = props.getProperty(CHECKPOINT_FILE);
        if (checkpoint != null) {
          if (checkpoint.endsWith("/")) {
            return checkpoint + checkPointFile;
          }
          return checkpoint;
        }
      }
    } catch (Exception ex) {
      logger.warning("Cannot get checkpoint file location from the custom properties file : " + ex.getMessage());
    }

    String baseDir = "";
    try {
      baseDir = cct.tools.Utils.getCCTDirectory();
    } catch (Exception ex) {
      throw ex;
    }

    //String pathS = File.separator;
    String pathS = "/";
    if (!baseDir.endsWith(pathS)) {
      // it means that path contains checkpoint file name
      return baseDir;
    }





    return baseDir + checkPointFile;
  }

  public void addCheckPoint(CheckPoint checkPoint) throws
      MalformedCheckPointException {
    if (checkPoint == null) {
      throw new MalformedCheckPointException("checkPoint == null");


    }

    String checkpf = "";


    try {
      checkpf = getCheckPointFile();


    } catch (Exception ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }
    parseCheckpointFile(checkpf);



    if (debug) {
      logger.info("there are " + tasks.size() + " tasks in checkpoint file");


    }

    tasks.add(checkPoint);



    try {
      saveCheckPointFile(tasks, checkpf);


    } catch (MalformedCheckPointException ex) {
      throw ex;


    }
  }

  public static void saveCheckPointFile(List jobs, String file_name) throws
      MalformedCheckPointException {
    String xml_string = null;


    try {
      xml_string = CheckPoint.CreateCheckpointString(jobs);


    } catch (SAXException ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }
    try {
      IOUtils.saveStringIntoFile(xml_string, file_name);


    } catch (Exception ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }

  }

  public static void saveCheckPointFile(List jobs) throws
      MalformedCheckPointException {
    String xml_string = null;


    try {
      xml_string = CheckPoint.CreateCheckpointString(jobs);


    } catch (SAXException ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }

    try {
      String file_name = getCheckPointFile();
      IOUtils.saveStringIntoFile(xml_string, file_name);


    } catch (Exception ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }

  }

  public void updateSelectedCheckPoints(List checkpoints) throws
      MalformedCheckPointException {

    String file_name = "";


    try {
      file_name = getCheckPointFile();


    } catch (Exception ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }
    updateSelectedCheckPoints(checkpoints, file_name);


  }

  public void updateSelectedCheckPoints(List checkpoints, String fileName) throws
      MalformedCheckPointException {

    if (checkpoints == null || checkpoints.size() == 0) {
      return;


    }

    try {
      parseCheckpointFile(fileName);


    } catch (Exception ex) {
    }

    if (tasks.size() == 0) {
      throw new MalformedCheckPointException("Checkpoint file is empty");


    }

    Map current = new HashMap(tasks.size());


    for (int i = 0; i
        < tasks.size(); i++) {
      CheckPoint chk = (CheckPoint) tasks.get(i);
      String handle = chk.getJobHandle();
      current.put(handle, new Integer(i));


    }

    Map update = new HashMap(checkpoints.size());


    for (int i = 0; i
        < checkpoints.size(); i++) {
      CheckPoint chk = (CheckPoint) checkpoints.get(i);
      String handle = chk.getJobHandle();
      update.put(handle, new Integer(i));


    }

    Set set = update.entrySet();
    Iterator iter = set.iterator();
    //Iterator iter = list.iterator();



    boolean no_update = true;


    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String handle = me.getKey().toString();
      Integer index = (Integer) me.getValue();


      if (!current.containsKey(handle)) {
        System.err.println("Checkpoint file does not contain handle " + handle
            + ". Ignored...");


      }
      Integer index_2 = (Integer) current.get(handle);
      tasks.remove(index_2.intValue());
      CheckPoint chk = (CheckPoint) checkpoints.get(index.intValue());
      tasks.add(index_2.intValue(), chk);
      no_update = false;


    }

    if (no_update) {
      return;


    }

    saveCheckPointFile(tasks);


  }

  public void getJobStatuses() throws MalformedCheckPointException {
    try {
      parseCheckpointFile();


    } catch (Exception ex) {
      throw new MalformedCheckPointException(ex.getMessage());


    }
    for (int i = 0; i
        < tasks.size(); i++) {
      CheckPoint chk = (CheckPoint) tasks.get(i);


      if (chk.getJobStatus().equalsIgnoreCase(CheckPointInterface.JOB_STATUS_DONE)) {
        continue;
      }


      try {
        //chk.updateJobStatus();

        statusUpdateTask = new StatusUpdateTask(chk, queryTime);
        statusUpdateTask.execute();


        while (!(statusUpdateTask.isDone() || statusUpdateTask.isCancelled())) {
          try {
            Thread.sleep(500);


          } catch (Exception ex) {
          }
        }


        if (statusUpdateTask.isDone()) {
          logger.info("Job status query completed successfully");


        } else if (statusUpdateTask.isCancelled()) {
          logger.info("Job status query was cancelled");


        } else {
          logger.info("Job status query was not finished after " + queryTime + " msec. Force to finish...");


          if (statusUpdateTask.cancel(true)) {
            logger.info("Job status query was cancelled");


          } else {
            logger.info("Job status query could not be cancelled (maybe it has already completed normally)");


          }
        }

        if (progress != null) {
          progress.setProgress((int) ((float) i / (float) tasks.size() * 100.0f));


        }

      } catch (Exception e) {
        //throw new MalformedCheckPointException("Quering job statuses: "+e.getMessage());
        System.err.println("getJobStatuses: Quering job statuses: " + e.getMessage());


      }
    }

    if (progress != null) {
      progress.setProgress(100);
      progress.setProgressText("Job status query completed");


    }

    try {
      saveCheckPointFile(tasks);


    } catch (MalformedCheckPointException ex) {
      throw ex;


    }
  }

  public static void main(String[] args) {
    ParseCheckpointFile parsecheckpointfile = new ParseCheckpointFile();
    parsecheckpointfile.parseCheckpointFile(args[0]);






  }

  class HandleSAXEvents
      extends DefaultHandler {

    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================
    @Override
    public void startDocument() throws SAXException {
      if (debug) {
        emit("<?xml version='1.0' encoding='UTF-8'?>");
        nl();
      }
    }

    @Override
    public void endDocument() throws SAXException {
      try {
        nl();
        out.flush();
      } catch (IOException e) {
        throw new SAXException("I/O error", e);
      }
    }

    @Override
    public void startElement(String namespaceURI,
        String lName, // local name
        String qName, // qualified name
        Attributes attrs) throws SAXException {

      if (finish_parsing) {
        return;
      }

      String eName = lName; // element name
      if ("".equals(eName)) {
        eName = qName; // namespaceAware = false
      }
      if (debug) {
        emit("<" + eName);
      }
      if (attrs != null) {
        for (int i = 0; i < attrs.getLength(); i++) {
          String aName = attrs.getLocalName(i); // Attr name
          if ("".equals(aName)) {
            aName = attrs.getQName(i);
          }
          if (debug) {
            emit(" ");
            emit(aName + "=\"" + attrs.getValue(i) + "\"");
          }
        }
      }
      if (debug) {
        emit(">");
      }
      // Parse elements...

      currentTag = eName;

      if (eName.equalsIgnoreCase(CheckPointInterface.tasksTag)) {
        yes_tasks = true;
        return;
      }
      if (!yes_tasks) {
        return; // Don't find <tasks> tag yet...
      }

      if (eName.equalsIgnoreCase(CheckPointInterface.taskTag)) {
        if (debug) {
          logger.info("Starting parsing new task...");
        }
        parseLevel = CheckPointInterface.PARSE_TASK_TAGS;
        yes_parsing_task = true;
        checkPoint = new CheckPoint();
        //taskEntries = new HashMap();
        serviceEntries = new HashMap();
        jobSpecificationEntries = new HashMap();
        outputFilesEntries = new HashMap();
        return;
      }

      if (!yes_parsing_task) {
        return; // Don't find <tasks> tag yet...
      }

      switch (parseLevel) {
        case CheckPointInterface.PARSE_TASK_TAGS:
          if (!CheckPointInterface.firstLevelTags.containsKey(eName)) {
            Message += "No such first level tag :" + eName + "\n";
            //logger.error("No such first level tag :" + eName);
            return;
          }
          if (eName.equalsIgnoreCase(CheckPointInterface.serviceTag)) {
            parseLevel = CheckPointInterface.PARSE_SERVICE_TAGS;
          } else if (eName.equalsIgnoreCase(CheckPointInterface.JobSpecificationTag)) {
            parseLevel = CheckPointInterface.PARSE_JOB_SPECIFICATION_TAGS;
          }

          break;
        case CheckPointInterface.PARSE_SERVICE_TAGS:
          if (!CheckPointInterface.serviceTags.containsKey(eName)) {
            Message += "No such service tag :" + eName + "\n";
            //logger.error("No such service tag :" + eName);
            return;
          }
          break;
        case CheckPointInterface.PARSE_JOB_SPECIFICATION_TAGS:
          if (!CheckPointInterface.JobSpecificationTags.containsKey(eName)) {
            Message += "No such JobSpecification tag :" + eName + "\n";
            //logger.error("No such JobSpecification Tag :" + eName);
            return;
          }
          if (eName.equalsIgnoreCase(CheckPointInterface.outputFilesTag)) {
            parseLevel = CheckPointInterface.PARSE_OUTPUT_FILES_TAGS;
          }

          break;
        case CheckPointInterface.PARSE_OUTPUT_FILES_TAGS:
          break;
      }

    }

    @Override
    public void endElement(String namespaceURI,
        String sName, // simple name
        String qName // qualified name
        ) throws SAXException {

      if (finish_parsing) {
        return;
      }

      String eName = sName; // element name
      if ("".equals(eName)) {
        eName = qName; // namespaceAware = false
      }

      if (debug) {
        emit("</" + eName + ">");
      }
      // --- Parsing

      if (eName.equalsIgnoreCase(CheckPointInterface.tasksTag)) {
        finish_parsing = true;
        //throw new SAXException("OK");
        return;
      }

      if (yes_parsing_task && eName.equalsIgnoreCase(CheckPointInterface.taskTag)) {
        if (debug) {
          logger.info("Finish parsing " + (tasks.size() + 1) + " task");
        }
        yes_parsing_task = false;
        checkPoint.put(CheckPointInterface.serviceTag, serviceEntries);
        //taskEntries.put(serviceTag, serviceEntries);
        jobSpecificationEntries.put(CheckPointInterface.outputFilesTag, outputFilesEntries);
        //taskEntries.put(JobSpecificationTag, jobSpecificationEntries);
        checkPoint.put(CheckPointInterface.JobSpecificationTag, jobSpecificationEntries);
        tasks.add(checkPoint);
        //tasks.add(taskEntries);
        return;
      }

      if (parseLevel == CheckPointInterface.PARSE_SERVICE_TAGS
          && eName.equalsIgnoreCase(CheckPointInterface.serviceTag)) {
        parseLevel = CheckPointInterface.PARSE_TASK_TAGS;
        return;
      }

      if (parseLevel == CheckPointInterface.PARSE_JOB_SPECIFICATION_TAGS
          && eName.equalsIgnoreCase(CheckPointInterface.JobSpecificationTag)) {
        parseLevel = CheckPointInterface.PARSE_TASK_TAGS;
        return;
      }

      if (parseLevel == CheckPointInterface.PARSE_OUTPUT_FILES_TAGS
          && eName.equalsIgnoreCase(CheckPointInterface.outputFilesTag)) {
        parseLevel = CheckPointInterface.PARSE_JOB_SPECIFICATION_TAGS;
        return;
      }

    }

    @Override
    public void characters(char buf[], int offset, int len) throws
        SAXException {
      String s = new String(buf, offset, len);
      if (debug) {
        emit(s);
      }
      s = s.trim();
      if (s.length() == 0) {
        return;
      }

      //logger.info("Characters: Tag: " + currentTag);

      switch (parseLevel) {
        case CheckPointInterface.PARSE_TASK_TAGS:
          checkPoint.put(currentTag, s);

          //taskEntries.put(currentTag, s);
          //logger.info("Task Entries: " + taskEntries);
          break;
        case CheckPointInterface.PARSE_SERVICE_TAGS:
          serviceEntries.put(currentTag, s);

          //logger.info("Service Entries: " + serviceEntries);
          break;
        case CheckPointInterface.PARSE_JOB_SPECIFICATION_TAGS:
          jobSpecificationEntries.put(currentTag, s);

          //logger.info("Job Specs Entries: " + jobSpecificationEntries);
          break;
        case CheckPointInterface.PARSE_OUTPUT_FILES_TAGS:
          outputFilesEntries.put(currentTag, s);

          //logger.info("Output files Entries: " + outputFilesEntries);
          break;

      }
    }

    //===========================================================
    // Utility Methods ...
    //===========================================================
    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void emit(String s) throws SAXException {
      try {
        out.write(s);
        out.flush();
      } catch (IOException e) {
        throw new SAXException("I/O error", e);
      }
    }

    /*
    private void emit(String s) throws SAXException {
    try {
    out.write(s);
    out.flush();
    }
    catch (IOException e) {
    throw new SAXException("I/O error", e);
    }
    }
     */
    // Start a new line
    private void nl() throws SAXException {
      String lineEnd = System.getProperty("line.separator");
      try {
        out.write(lineEnd);
      } catch (IOException e) {
        throw new SAXException("I/O error", e);
      }
    }
    /*
    class tasksEndException
    extends Exception {

    }
     */
  }
}

class StatusUpdateTask extends javax.swing.SwingWorker<Void, Void> implements ActionListener {
  /*
   * Main task. Executed in background thread.
   */

  CheckPoint chk;
  private Timer timer;

  public StatusUpdateTask(CheckPoint chk, int maxWorkTime) {
    this.chk = chk;
    timer = new Timer(maxWorkTime, this);
    timer.setRepeats(false);
  }

  public void actionPerformed(ActionEvent e) {
    this.cancel(true);
  }

  @Override
  public Void doInBackground() {
    timer.start();
    try {
      chk.updateJobStatus();
    } catch (Exception ex) {
      //logger.severe("Error querying Job status: " + ex.getMessage());
    }
    return null;
  }

  /*
   * Executed in event dispatching thread
   */
  @Override
  public void done() {
    //taskOutput.append("Done!\n");
  }
}
