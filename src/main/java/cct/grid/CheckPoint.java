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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

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
public class CheckPoint
    extends HashMap implements CheckPointInterface {

  final static String outputStatusPrefix = "local";
  final static String inputStatusPrefix = "local";
  Map resourcesUsed = null;

  //final static HashMap firstLevelTags = new HashMap();
  //final static HashMap serviceTags = new HashMap();
  //final static HashMap JobSpecificationTags = new HashMap();
  static {
    firstLevelTags.put(jobNameTag, "MyJob");
    firstLevelTags.put(softwareTag, "Unspecified");
    firstLevelTags.put(programTag, "Unspecified");
    firstLevelTags.put(statusTag, "Unknown");
    firstLevelTags.put(overallJobStatusTag, "Unknown");
    firstLevelTags.put(submittedTimeTag, "Unknown");
    firstLevelTags.put(serviceTag, serviceTags);
    firstLevelTags.put(JobSpecificationTag, JobSpecificationTags);

    serviceTags.put(handleTag, "unknown");
    serviceTags.put(providerTag, "unknown");
    serviceTags.put(computerTag, "unknown");

    JobSpecificationTags.put(executableTag, "");
    JobSpecificationTags.put(localExecutableTag, "true");
    JobSpecificationTags.put(stdOutputTag, "/dev/stdout");
    JobSpecificationTags.put(stdErrorTag, "/dev/stderr");
    JobSpecificationTags.put(remoteDirectoryTag, "");
    JobSpecificationTags.put(localDirectoryTag, "");
    JobSpecificationTags.put(outputFilesTag, null);
    JobSpecificationTags.put(inputFilesTag, null);
  }
  static boolean Debug = false;
  boolean outputFilesStatusUpdated = false;
  String Message = "";
  private JobProgressInterface progress = null;
  static final Logger logger = Logger.getLogger(JobProgressInterface.class.getCanonicalName());

  public JobProgressInterface getProgress() {
    return progress;
  }

  public void setProgress(JobProgressInterface progress) {
    this.progress = progress;
  }
  
  public CheckPoint() {
  }

  /**
   * Returns all possible specifications of checkpoint
   * @return HashMap
   */
  public static Map getCheckPointSpecifications() {
    return firstLevelTags;
  }

  /**
   * Returns checkpoint as a HashMap
   * @return HashMap checkpoint
   */
  public Map getHashMap() {
    return this;
  }

  public String getMessage() {
    return Message;
  }

  public Map getResourcesUsed() {
    return resourcesUsed;
  }

  public void setResourcesUsed(Map rused) {
    resourcesUsed = rused;
  }

  public String getJobStatus() {
    return (String) get(statusTag);
  }

  public String getJobName() {
    return (String) get(jobNameTag);
  }

  public String getProgram() {
    return (String) get(programTag);
  }

  public String getJobSubmitTime() {
    return (String) get(CheckPointInterface.submittedTimeTag);
  }

  public void setJobStatus(String status) {
    put(statusTag, status);
  }

  public void setOverallJobStatus(String status) {
    put(overallJobStatusTag, status);
  }

  public String getOverallJobStatus() {
    if (!containsKey(overallJobStatusTag)) {
      return "Unknown";
    }
    return (String) get(overallJobStatusTag);
  }

  public String getJobHandle() {
    Map service = (Map) get(serviceTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(handleTag);
  }

  public String getTaskProvider() {
    Map service = (Map) get(serviceTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(providerTag);
  }

  public String getComputer() {
    Map service = (Map) get(serviceTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(computerTag);
  }

  public String getRemoteDirectory() {
    Map service = (Map) get(JobSpecificationTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(remoteDirectoryTag);
  }

  public String getExecutable() {
    Map service = (Map) get(JobSpecificationTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(CheckPointInterface.executableTag);

  }

  public String getLocalDirectory() {
    Map service = (Map) get(JobSpecificationTag);
    if (service == null) {
      return null;
    }
    return (String) service.get(localDirectoryTag);
  }

  public String updateJobStatus() throws Exception {
    if (!containsKey(statusTag)) {
      return null;
    }
    if (getTaskProvider() == null) {
      return null;
    }

    String status = getJobStatus();
    if (status.equalsIgnoreCase(JOB_STATUS_DONE)) {
      return status;
    }

    String handle = getJobHandle();

    if (handle == null) {
      return JOB_STATUS_UNKNOWN;
    }

    String jobName = this.getJobName();

    String provider = this.getTaskProvider();
    if (provider == null) {
      logger.warning("Task " + jobName + " does not have a provider record");
      throw new Exception("Task " + jobName + " does not have a provider record");
    }

    if (progress != null) {
      progress.setProgressText("Getting job status for " + handle);
    }
    status = TaskProvider.getJobStatus(this);
    /*
    GramJob gj = null;
    try {
    gj = GRAM.getJobStatus(handle);
    }
    catch (Exception e) {
    //throw e;
    System.err.println("Quering handle : " + handle + " : " + e.getMessage());
    }

    if (gj.getStatus() == 0) {
    status = JOB_STATUS_DONE;
    }
    else {
    status = gj.getStatusAsString();
    }
     */

    setJobStatus(status);

    // Update (if needed) overall job status (only if a submitted job completed)


    if (status.equalsIgnoreCase(JOB_STATUS_DONE)) {
      if (hasOutputFiles()) {
        if (areOutputFilesLoaded()) {
          setOverallJobStatus(JOB_STATUS_DONE);
        } else {
          setOverallJobStatus(JOB_STATUS_PENDING);
        }
      } else {
        setOverallJobStatus(JOB_STATUS_DONE);
      }
    }

    return status;
  }

  public void downloadOutputFiles(FileTransferInterface fileTransferHandler) throws
      MalformedCheckPointException {

    outputFilesStatusUpdated = false;

    // Error check
    if (!hasOutputFiles()) {
      logger.info("Warning: attempt to download output file(s) from the checkpoint without ones");
      return;
    }

    if (areOutputFilesLoaded()) {
      logger.info("Warning: downloadOutputFiles: output files are already downloaded");
      return;
    }

    if (getTaskProvider() == null) {
      System.err.println("Error: downloadOutputFiles: no provider name in checkpoint file");
      throw new MalformedCheckPointException("No provider name in checkpoint file");
    }

    // --- Creating valid URLs
    /*
    if (this.getComputer() == null) {
    System.err.println("Error: downloadOutputFiles: no computer name in checkpoint file");
    throw new MalformedCheckPointException("No computer name in checkpoint file");
    }
     */

    if (this.getLocalDirectory() == null) {
      System.err.println("Error: downloadOutputFiles: no local directory in checkpoint file");
      throw new MalformedCheckPointException("No local directory in checkpoint file");
    }

    String localDir = getLocalDirectory();

    if (!localDir.endsWith("/") && !localDir.endsWith("\\")) {
      localDir = localDir + "/";
    }

    //String fromURL = getComputer() + remoteDir;
    String fromURL = "";
    String toURL = localDir;

    String errorMessage = "";

    // --- Go through output files
    Map out = getOutputFilesEntry();
    Set set = out.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String Key = me.getKey().toString();
      String outputFile = me.getValue().toString();
      if (Key.startsWith(outputStatusPrefix)) {
        continue;
      }
      String status_tag = outputStatusPrefix + Key;
      if (!out.containsKey(status_tag)) {
        System.err.println("Internal Error: tag " + Key + " should be complemented with tag " + outputStatusPrefix + Key);
        throw new RuntimeException("Internal Error: tag " + Key + " should be complemented with tag " + outputStatusPrefix
            + Key);
        //continue;
      }

      String status_value = (String) out.get(outputStatusPrefix + Key);

      // --- Download it
      if (status_value.equalsIgnoreCase("false")) {
        logger.info("Copying\n  From: " + fromURL + outputFile);
        logger.info("  To: " + toURL + outputFile);

        if (fileTransferHandler.tranferFile(fromURL + outputFile, toURL + outputFile, this)) {
          out.put(outputStatusPrefix + Key, "true");
          outputFilesStatusUpdated = true;
        } else {
          errorMessage += "Unable to download " + fromURL + outputFile + "\n";
        }

      }
    }

    // --- Update overal job status
    if (errorMessage.length() == 0) {
      setOverallJobStatus(JOB_STATUS_DONE);
      outputFilesStatusUpdated = true;
    }

    if (errorMessage.length() > 0) {
      throw new MalformedCheckPointException(errorMessage);
    }

  }

  public boolean isOutputFilesStatusUpdated() {
    return outputFilesStatusUpdated;
  }

  /**
   * Informs whether remote output files are downloaded on a local computer
   * @return boolean "true" if ALL output files are downloaded on local computer,
   * and "false" otherwise
   */
  public boolean areOutputFilesLoaded() {
    Map out = getOutputFilesEntry();
    Set set = out.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String Key = me.getKey().toString();
      if (Key.startsWith(outputStatusPrefix)) {
        continue;
      }
      String status_tag = outputStatusPrefix + Key;
      if (!out.containsKey(status_tag)) {
        System.err.println("Internal Error: tag " + Key
            + " should be complemented with tag "
            + outputStatusPrefix + Key);
        throw new RuntimeException("Internal Error: tag " + Key
            + " should be complemented with tag "
            + outputStatusPrefix + Key);
        //continue;
      }
      String status_value = (String) out.get(outputStatusPrefix + Key);
      if (status_value.equalsIgnoreCase("false")) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   * @return HashMap Entry for Output Files
   */
  public Map getOutputFilesEntry() {
    if (!containsKey(JobSpecificationTag)) {
      return null;
    }
    Map js = (Map) get(JobSpecificationTag);
    if (!js.containsKey(outputFilesTag)) {
      return null;
    }
    return (Map) js.get(outputFilesTag);
  }

  public boolean hasOutputFiles() {
      return getOutputFilesEntry() != null;
  }

  public boolean hasOutputFile(String fileName) {
    Map output = getOutputFilesEntry();
    if (output == null) {
      return false;
    }

    if (!output.containsValue(fileName)) {
      return false;
    }

    Set set = output.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String tag = me.getKey().toString();
      if (tag.startsWith("local")) {
        continue;
      }
      String fName = me.getValue().toString();
      if (fileName.equals(fName)) {
        return true;
      }
    }
    return false;
  }

  public boolean isOutputFileDownloaded(String fileName) throws Exception {
    Map output = getOutputFilesEntry();
    if (output == null) {
      throw new Exception("No output files in a given checkpoint");
    }

    if (!output.containsValue(fileName)) {
      throw new Exception("No output file " + fileName
          + " in a given checkpoint");
    }

    Set set = output.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String tag = me.getKey().toString();
      if (tag.startsWith("local")) {
        continue;
      }
      String fName = me.getValue().toString();
      if (fileName.equals(fName)) {
        if (output.containsKey("local" + tag)) {
          String state = output.get("local" + tag).toString();
          try {
            boolean local = Boolean.valueOf(state);
            return local;
          } catch (Exception ex) {
            throw ex;
          }
        }
      }
    }
    throw new Exception("No output file " + fileName + " in a given checkpoint");
  }

  public String getFullLocalPath(String fileName) throws Exception {
    Map output = getOutputFilesEntry();
    if (output == null) {
      throw new Exception("No output files in a given checkpoint");
    }

    if (!output.containsValue(fileName)) {
      throw new Exception("No output file " + fileName
          + " in a given checkpoint");
    }

    if (!isOutputFileDownloaded(fileName)) {
      throw new Exception("File " + fileName + " is not downloaded");
    }

    return getLocalDirectory() + fileName;
  }

  public String getOutputFileType(String fileName) throws Exception {
    Map output = getOutputFilesEntry();
    if (output == null) {
      throw new Exception("No output files in a given checkpoint");
    }

    if (!output.containsValue(fileName)) {
      throw new Exception("No output file " + fileName
          + " in a given checkpoint");
    }

    Set keys = output.keySet();
    Iterator iter = keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      if (output.get(key).toString().equals(fileName)) {
        return key;
      }
    }
    return null;
  }

  /**
   *
   * @param tag String
   * @param value String
   * @throws MalformedCheckPointException
   */
  public void setOutputFile(String tag, String value) throws
      Exception {

    try {
      setOutputFile(tag, value, false);
    } catch (Exception ex) {
      throw ex;
    }
  }

  /**
   *
   * @param tag String
   * @param value String
   * @param local_file boolean
   * @throws MalformedCheckPointException
   */
  public void setOutputFile(String tag, String value, boolean local_file) throws
      MalformedCheckPointException {
    if (!this.containsKey(JobSpecificationTag)) {
      throw new MalformedCheckPointException("Error adding " + outputFilesTag + " tag: no " + JobSpecificationTag + " tag");
    }

    if (tag.indexOf(" ") != -1) {
      throw new MalformedCheckPointException(
          "Error adding output file tag: tag must not contain blanks: \"" + tag + "\"");
    }

    Map jsp = (Map) get(JobSpecificationTag);

    if (!jsp.containsKey(outputFilesTag)) {
      Map out = new HashMap();
      jsp.put(outputFilesTag, out);
      if (Debug) {
        logger.info("Creating specific entry: " + outputFilesTag);
      }
    }
    Map out = (Map) jsp.get(outputFilesTag);
    out.put(tag, value);
    // Add a flag
    out.put(outputStatusPrefix + tag, Boolean.toString(local_file));

    if (Debug) {
      logger.info("  Adding " + tag + "=" + value + " inside tag " + outputFilesTag);
    }
  }

  /**
   * Query spesific "service" tags
   * @param tag String - Tag
   * @return String - Value, or null is no such tag
   */
  public String getServiceTag(String tag) {
    if (!this.containsKey(serviceTag)) {
      return null;
    }
    Map service = (Map) this.get(serviceTag);
    return service.get(tag).toString();
  }

  /**
   * Sets service tag which is in general could be some custom value
   * @param tag String - tag
   * @param value String - value
   * @throws MalformedCheckPointException
   */
  public void setServiceTag(String tag, String value, boolean overwrite) throws Exception {

    if (tag.indexOf(" ") != -1) {
      throw new MalformedCheckPointException("Error adding output file tag: tag must not contain blanks: \"" + tag + "\"");
    }

    // --- Get service hashmap

    Map service = null;
    if (!this.containsKey(serviceTag)) {
      service = new HashMap();
      this.put(serviceTag, service);
    } else {
      service = (Map) this.get(serviceTag);
    }

    if (overwrite || (!service.containsKey(tag))) {
      service.put(tag, value);
      if (Debug) {
        logger.info("  Adding service tag: " + tag + "=" + value);
      }
    } else {
      throw new MalformedCheckPointException("Cannot add service tag " + tag + " : it's already exists. Use overwrite option...");
    }
  }

  public void setInputFile(String tag, String value, boolean local_file) throws
      MalformedCheckPointException {

    if (!this.containsKey(JobSpecificationTag)) {
      throw new MalformedCheckPointException("Error adding " + inputFilesTag + " tag: no " + JobSpecificationTag + " tag");
    }

    if (tag.indexOf(" ") != -1) {
      throw new MalformedCheckPointException("Error adding input file tag: tag must not contain blanks: \"" + tag + "\"");
    }

    Map jsp = (Map) get(JobSpecificationTag);

    if (!jsp.containsKey(inputFilesTag)) {
      Map out = new HashMap();
      jsp.put(inputFilesTag, out);
      if (Debug) {
        logger.info("Creating specific entry: " + inputFilesTag);
      }
    }
    Map out = (Map) jsp.get(inputFilesTag);
    out.put(tag, value);
    // Add a flag
    out.put(inputStatusPrefix + tag, Boolean.toString(local_file));

    if (Debug) {
      logger.info("  Adding " + tag + "=" + value + " inside tag "
          + inputFilesTag);
    }
  }

  /**
   *
   * @param options String
   * @param devider String
   * @throws MalformedCheckPointException
   */
  public CheckPoint(String options, String devider) throws
      MalformedCheckPointException {
    super();
    if (options == null || options.trim().length() == 0) {
      throw new MalformedCheckPointException("No parameters");
    }
    String[] tokens = options.split(devider);
    if (tokens.length % 2 == 1) {
      throw new MalformedCheckPointException("Odd number of parameters:");
    }

    for (int i = 0; i < tokens.length; i++) {
      tokens[i] = tokens[i].trim();
    }

    for (int i = 0; i < tokens.length / 2; i++) {
      if (Debug) {
        logger.info("Parsing: " + tokens[2 * i] + " = " + tokens[2 * i + 1]);
      }
      try {
        if (!addElement(tokens[2 * i], tokens[2 * i + 1], this, firstLevelTags)) {
          throw new MalformedCheckPointException("Parsing paramaters: Wrong tag: " + tokens[2 * i]);
        }
      } catch (MalformedCheckPointException e) {
        throw e;
      }
    }

    // Set defaults values

    Set set = firstLevelTags.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String targetKey = me.getKey().toString();
      Object type = me.getValue();
      if (type instanceof Map) {
        continue;
      }

      if (containsKey(targetKey)) {
        continue;
      }

      put(targetKey, type);
    }
  }

  /**
   *
   * @param keyword String
   * @param value String
   * @param target HashMap
   * @param reference HashMap
   * @return boolean
   * @throws MalformedCheckPointException
   */
  private boolean addElement(String keyword, String value, Map target, Map reference) throws
      MalformedCheckPointException {

    // --- Check for "root" tags

    if (reference.containsKey(keyword)) {
      Object obj = reference.get(keyword);
      if (obj instanceof Map) {
        throw new MalformedCheckPointException("Tag " + keyword + " is only a container for tags");
      } else {
        target.put(keyword, value);
        return true;
      }
    }

    // --- Check for inclosed tag containers

    Set set = reference.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Entry me = (Entry) iter.next();
      String targetKey = me.getKey().toString();
      Object type = me.getValue();

      if (!(type instanceof Map)) {
        continue;
      }

      Map new_entry = null;
      if (!target.containsKey(targetKey)) {
        new_entry = new HashMap();
        target.put(targetKey, new_entry);
      }

      new_entry = (Map) target.get(targetKey);
      Map next_ref_entry = (Map) type;

      try {
        if (addElement(keyword, value, new_entry, next_ref_entry)) {
          new_entry.put(keyword, value);
          target.put(targetKey, new_entry);
          return true;
        }
      } catch (MalformedCheckPointException e) {
        throw e;
      }

    }

    return false;
  }

  /**
   *
   * @param file_name String
   * @return int
   */
  public int saveCheckPoint(String file_name) throws Exception {
    List task = new ArrayList();
    task.add(this.getHashMap());
    String xml_string = null;
    try {
      xml_string = CreateCheckpointString(task);
    } catch (Exception ex) {
      throw ex;
    }
    IOUtils.saveStringIntoFile(xml_string, file_name);
    return 0;
  }

  public static String CreateCheckpointString(List Tasks) throws
      MalformedCheckPointException, SAXException {
    //Message = "";
    StringWriter sWriter = new StringWriter();

    StreamResult streamResult = new StreamResult(sWriter);
    SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

    // SAX2.0 ContentHandler.
    TransformerHandler hd = null;
    try {
      hd = tf.newTransformerHandler();
    } catch (TransformerConfigurationException e) {
      //Message = e.getMessage();
      System.err.println(e.getMessage());
      throw new MalformedCheckPointException("Error: " + e.getMessage());
      //return null;
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
      hd.startElement("", "", tasksTag, atts);

      for (int i = 0; i < Tasks.size(); i++) {
        Map Level_1 = (Map) Tasks.get(i);
        atts.clear();
        hd.startElement("", "", taskTag, atts);

        //if (!createElements(hd, firstLevelTags, Level_1)) {
        if (!createElements(hd, CheckPoint.getCheckPointSpecifications(),
            Level_1)) {
          return null;
        }

        hd.endElement("", "", taskTag);
      }

      hd.endElement("", "", tasksTag);
      hd.endDocument();

    } catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println(e.getMessage());
      throw e;
      //return null;
    }

    return sWriter.toString();
  }

  static boolean createElements(TransformerHandler hd, Map list, Map elements) throws
      MalformedCheckPointException, SAXException {
    AttributesImpl atts = new AttributesImpl();

    if (Debug) {
      logger.info("Enter createElements...");
    }

    Set set = elements.entrySet();
    Iterator iter = set.iterator();
    try {
      while (iter.hasNext()) {
        Entry me = (Entry) iter.next();
        String keyWord = me.getKey().toString();
        Object obj = me.getValue();
        if (Debug) {
          logger.info("Parsing: " + keyWord);
        }

        atts.clear();

        hd.startElement("", "", keyWord, atts);

        if (obj instanceof Map) {
          if (Debug) {
            logger.info("  Key " + keyWord + " is an entry...");
          }

          Map child = (Map) obj;
          createElements(hd, child, child);
        } // Process regular entry
        else {
          String value = obj.toString();
          if (Debug) {
            logger.info("  Key is a value: " + value);
          }
          if (value == null) {
            hd.characters("".toCharArray(), 0, 0);
          } else {
            hd.characters(value.toCharArray(), 0, value.length());
          }
        }

        hd.endElement("", "", keyWord);
      }
    } catch (SAXException e) {
      System.err.println(e.getMessage());
      throw e;
    }
    /*
    Set set = list.entrySet();
    Iterator iter = set.iterator();
    //Iterator iter = list.iterator();
    try {
    while (iter.hasNext()) {
    Map.Entry me = (Map.Entry) iter.next();
    String keyWord = me.getKey().toString();
    Object type = me.getValue();
    if (Debug) {
    logger.info("Parsing: " + keyWord);
    }

    if (!elements.containsKey(keyWord)) {
    if (Debug) {
    logger.info("  No such key in elements. Continuing...");
    }
    continue;
    }

    atts.clear();

    hd.startElement("", "", keyWord, atts);

    Object obj = elements.get(keyWord);

    if (type instanceof HashMap) {
    if (Debug) {
    logger.info("  Key is an entry...");
    }

    // --- Error check
    if (! (type instanceof HashMap)) {
    throw new MalformedCheckPointException("Tag " + keyWord + " must be an hashmap entry! Got " +
    type.getClass().getName());
    }

    if (obj instanceof HashMap) {
    HashMap parent = (HashMap) type;
    HashMap child = (HashMap) obj;
    if (!createElements(hd, parent, child)) {
    return false;
    }
    }
    else {
    String mess = "Instance of user's HashMap different from the reference one";
    System.err.println(mess);
    throw new MalformedCheckPointException(mess);
    //return false;
    }
    }

    // Process specific entry
    else if (type == null) {
    if (Debug) {
    logger.info("  Key is a specific entry...");
    }
    HashMap child = (HashMap) obj;
    if (!createElements(hd, child)) {
    return false;
    }

    }

    // Process regular entry
    else {
    Object xxx = elements.get(keyWord);
    if (Debug) {
    logger.info("  Key is a value: " + xxx.toString());
    }
    String value = (String) elements.get(keyWord);
    hd.characters(value.toCharArray(), 0, value.length());
    }

    hd.endElement("", "", keyWord);
    }
    }
    catch (org.xml.sax.SAXException e) {
    //Message = e.getMessage();
    System.err.println(e.getMessage());
    throw e;
    //return false;
    }
     */
    return true;
  }

  /**
   * Prints all tags and their values of "entries"
   * @param hd TransformerHandler
   * @param entries HashMap
   * @return boolean
   */
  static boolean createElements(TransformerHandler hd, Map entries) throws
      SAXException {
    AttributesImpl atts = new AttributesImpl();
    Set set = entries.entrySet();
    Iterator iter = set.iterator();
    //Iterator iter = list.iterator();
    try {
      while (iter.hasNext()) {
        Entry me = (Entry) iter.next();
        String keyWord = me.getKey().toString();
        String value = (String) me.getValue();
        if (Debug) {
          logger.info("Parsing: " + keyWord);
        }

        atts.clear();

        hd.startElement("", "", keyWord, atts);

        hd.characters(value.toCharArray(), 0, value.length());

        hd.endElement("", "", keyWord);
      }
    } catch (SAXException e) {
      //Message = e.getMessage();
      System.err.println(e.getMessage());
      throw e;
      //return false;
    }
    return true;
  }

  /*
  public static void main(String[] args) {
  CheckPoint checkpoint = new CheckPoint();
  }
   */
}
