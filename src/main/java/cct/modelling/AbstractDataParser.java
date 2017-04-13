/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cct.modelling;

import cct.tools.UncompressInputStream;
import org.itadaki.bzip2.BZip2InputStream;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author vvv900
 */
abstract public class AbstractDataParser {

  private String name, description, extensions;
  private boolean useDialog = false;
  static final Logger logger = Logger.getLogger(AbstractDataParser.class.getCanonicalName());

  /**
   * Parses the data
   * @param in
   * @throws Exception 
   */
  abstract public void parseData(BufferedReader in) throws Exception;

  /**
   * Tries to determine whether it's a valid format.
   * @param in
   * @return 0 - not at all, 10 - 100%
   * @throws Exception 
   */
 

  public void parseData(File file) throws Exception {

    BufferedReader in = null;
    if (file.getName().endsWith(".Z")) {
      in = new BufferedReader(new InputStreamReader(new UncompressInputStream(new FileInputStream(file))));
    } else if (file.getName().endsWith(".gz")) {
      GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
      in = new BufferedReader(new InputStreamReader(gzip));
    } else if (file.getName().endsWith(".bz2")) {
      BZip2InputStream bzip2 = new BZip2InputStream(new FileInputStream(file), false);
      //CBZip2InputStream bzip2 = new CBZip2InputStream(new FileInputStream(filename));
      in = new BufferedReader(new InputStreamReader(bzip2));
      in.mark(5);
      char[] buf = new char[3];
      if (in.read(buf, 0, 2) == -1) {
        throw new Exception("Unexpected end of file");
      }
      if (!(buf[0] == 'B' && buf[0] == 'Z')) {
        in.reset();
      }
    } else if (file.getName().endsWith(".zip")) {
      ZipFile zipFile = new ZipFile(file);
      if (zipFile.size() < 1) {
        String msg = "Zip file " + file.getName() + " has no entries";
        logger.warning(msg);
        throw new Exception(msg);
      } else if (zipFile.size() > 1) {
        logger.warning("Zip file has " + zipFile.size() + " entries. Only the first will be used");
      }
      Enumeration e = zipFile.entries();
      ZipEntry zipEntry = (ZipEntry) e.nextElement();
      in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
    } else if (file.getName().endsWith(".jar")) {
      JarFile jarFile = new JarFile(file);
      if (jarFile.size() < 1) {
        String msg = "Jar file " + file.getName() + " has no entries";
        logger.warning(msg);
        throw new Exception(msg);
      } else if (jarFile.size() > 1) {
        logger.warning("Jar file has " + jarFile.size() + " entries. Only the first will be used");
      }
      JarEntry jarEntry = null;
      Enumeration e = jarFile.entries();
      while (e.hasMoreElements()) {
        JarEntry entry = (JarEntry) e.nextElement();
        if (entry.isDirectory() || entry.getName().equals("META-INF/MANIFEST.MF")) {
          continue;
        }
        System.out.println(entry.getName() + " " + entry.getSize() + " " + entry.getExtra() + " " + entry.getComment());
        jarEntry = entry;
        break;
      }

      if (jarEntry == null) {
        throw new Exception("Didn't find data file in jar");
      }

      in = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
    } else {
      in = new BufferedReader(new FileReader(file));
    }
    parseData(in);
  }

  public void parseData(String filename) throws Exception {
    File file = new File(filename);
    parseData(file);
  }

  public void parseDataAsString(String data) throws Exception {
    BufferedReader in = new BufferedReader(new StringReader(data));
    parseData(in);
  }

  public void parseDataAsURL(String urlString) throws Exception {
    URL url = new URL(urlString);
    parseDataAsURL(url);
  }

  public void parseDataAsURL(URL url) throws Exception {

    BufferedReader in = null;
    if (url.getFile().endsWith(".Z")) {
      in = new BufferedReader(new InputStreamReader(new UncompressInputStream(url.openStream())));
    } else if (url.getFile().endsWith(".gz")) {
      GZIPInputStream gzip = new GZIPInputStream(url.openStream());
      in = new BufferedReader(new InputStreamReader(gzip));
    } else if (url.getFile().endsWith(".bz2")) {
      BZip2InputStream bzip2 = new BZip2InputStream(url.openStream(), false);
      //CBZip2InputStream bzip2 = new CBZip2InputStream(new FileInputStream(filename));
      in = new BufferedReader(new InputStreamReader(bzip2));
      in.mark(5);
      char[] buf = new char[3];
      if (in.read(buf, 0, 2) == -1) {
        throw new Exception("Unexpected end of file");
      }
      if (!(buf[0] == 'B' && buf[0] == 'Z')) {
        in.reset();
      }
    } /*
    else if (url.getFile().endsWith(".zip")) {
    ZipFile zipFile = new ZipFile(file);
    if (zipFile.size() < 1) {
    String msg = "Zip file " + url.getFile() + " has no entries";
    logger.warning(msg);
    throw new Exception(msg);
    } else if (zipFile.size() > 1) {
    logger.warning("Zip file has " + zipFile.size() + " entries. Only the first will be used");
    }
    Enumeration e = zipFile.entries();
    ZipEntry zipEntry = (ZipEntry) e.nextElement();
    in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
    
    }
     */ else if (url.getFile().endsWith(".jar")) {
      //local archive
      //  url=new URL("jar:file:/C:/Program%20Files/Java/jdk1.5.0/jre/lib/jsse.jar!/");
      //remote archive
      //url=new URL("jar:http://.../archive.jar!/");
      JarURLConnection jarURL = (JarURLConnection) url.openConnection();
      JarFile jarFile = jarURL.getJarFile();
      if (jarFile.size() < 1) {
        String msg = "Jar file " + url.getFile() + " has no entries";
        logger.warning(msg);
        throw new Exception(msg);
      } else if (jarFile.size() > 1) {
        logger.warning("Jar file has " + jarFile.size() + " entries. Only the first will be used");
      }
      JarEntry jarEntry = null;
      Enumeration e = jarFile.entries();
      while (e.hasMoreElements()) {
        JarEntry entry = (JarEntry) e.nextElement();
        if (entry.isDirectory() || entry.getName().equals("META-INF/MANIFEST.MF")) {
          continue;
        }
        System.out.println(entry.getName() + " " + entry.getSize() + " " + entry.getExtra() + " " + entry.getComment());
        jarEntry = entry;
        break;
      }

      if (jarEntry == null) {
        throw new Exception("Didn't find data file in jar");
      }

      in = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
    } else {
      in = new BufferedReader(new InputStreamReader(url.openStream()));
    }
    parseData(in);
  }

  public AbstractDataParser getParserObject() {
    return this;
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

  public boolean isUseDialog() {
    return useDialog;
  }

  public void setUseDialog(boolean useDialog) {
    this.useDialog = useDialog;
  }
}
