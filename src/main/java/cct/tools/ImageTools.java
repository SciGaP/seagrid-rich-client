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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cct.tools.ui.FileChooserDialog;
import cct.tools.ui.JPEGOptionsDialog;

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
public class ImageTools {

  static boolean Debug = true;
  public static final String IMAGE_PNG = "png";
  public static final String IMAGE_JPEG = "jpg";
  public static final String IMAGE_BMP = "bmp";
  public static final String IMAGE_TIFF = "tiff";

  static final String LastSaveImagePWD = "LastSaveImageDirectory";

  protected static final Set<String> supportedSaveImageFormats = new HashSet();
  protected static Set<String> canSaveForThisPlatform;

  static private boolean jpegProgressiveMode = false;
  static private boolean optimizeHuffmanCode = false;
  static private float jpegImageQuality = 0.99f;

  static final Logger logger = Logger.getLogger(ImageTools.class.getCanonicalName());

  static {
    supportedSaveImageFormats.add(IMAGE_PNG.toUpperCase());
    supportedSaveImageFormats.add(IMAGE_JPEG.toUpperCase());
    supportedSaveImageFormats.add(IMAGE_BMP.toUpperCase());
  }

  private static ImageTools phantom = new ImageTools();

  public ImageTools() {
    if (canSaveForThisPlatform == null) {
      String[] supported = getSaveImageFormatNames();
      canSaveForThisPlatform = new HashSet<String>(supported.length);
      for (int i = 0; i < supported.length; i++) {
        canSaveForThisPlatform.add(supported[i].toUpperCase());
      }
    }
  }

  public static String[] getSaveImageFormatNames() {
    String formats[] = ImageIO.getWriterFormatNames();
    Set<String> tempStore = new HashSet(formats.length);
    if (Debug) {
      logger.info("System available formats");
    }
    for (int i = 0; i < formats.length; i++) {
      tempStore.add(formats[i].toUpperCase());
      if (Debug) {
        logger.info(formats[i]);
      }
    }

    List<String> temp = new ArrayList<String>(supportedSaveImageFormats.size());
    Iterator<String> iter = supportedSaveImageFormats.iterator();
    while (iter.hasNext()) {
      String format = iter.next();
      if (tempStore.contains(format)) {
        temp.add(format);
      }
    }
    String[] supported = new String[temp.size()];
    temp.toArray(supported);
    return supported;
  }

  public static void saveImageAsDialog(BufferedImage bImage, String imageType) throws
      Exception {
    saveImageAsDialog(bImage, imageType, new java.awt.Frame());
  }

  public static void saveImageAsDialog(BufferedImage bImage, String imageType, java.awt.Frame parent) throws Exception {

    FileFilterImpl filters[] = null;
    String formats[] = null;

    if (imageType == null || imageType.length() == 0) {
      formats = ImageTools.getSaveImageFormatNames();
    } else {
      formats = new String[1];
      formats[0] = imageType;
    }

    filters = new FileFilterImpl[formats.length];
    for (int i = 0; i < formats.length; i++) {
      filters[i] = new FileFilterImpl();
      if (formats[i].equalsIgnoreCase("JPG") || formats[i].equalsIgnoreCase("JPEG")) {
        filters[i].addExtension("jpg");
        filters[i].addExtension("JPG");
        filters[i].addExtension("jpeg");
        filters[i].addExtension("JPEG");
        filters[i].setDescription("JPEG files (*.jpg;*.jpeg)");
      } else {
        filters[i].addExtension(formats[i].toLowerCase());
        filters[i].addExtension(formats[i].toUpperCase());
        filters[i].setDescription(formats[i].toUpperCase() + " files (*." + formats[i].toLowerCase() + ")");
      }
    }

    String workingDirectory = Utils.getPreference(phantom, LastSaveImagePWD);

    FileChooserDialog fileChooser = new FileChooserDialog(filters, null,
        "Select file to save", JFileChooser.SAVE_DIALOG);
    fileChooser.setWorkingDirectory(workingDirectory);

    String outFileName = fileChooser.getFile();

    if (outFileName == null) {
      return;
    }

    if (imageType == null || imageType.length() == 0) {
      FileFilter filter = fileChooser.getFileFilter();
      String descr = filter.getDescription();
      if (descr.contains("jpg")) {
        imageType = "jpg";
      } else if (descr.contains("bmp")) {
        imageType = "bmp";
      } else if (descr.contains("png")) {
        imageType = "png";
      } else {
        throw new Exception("Internal error: cannot deduct image file type from " + descr);
      }
    }

    String pwd = fileChooser.getCurrentDirectory().getAbsolutePath();
    Utils.savePreference(phantom, LastSaveImagePWD, pwd);

    JPEGOptionsDialog params = new JPEGOptionsDialog(null, "Choose JPEG Parameters", true);
    params.setLocationByPlatform(true);
    params.setVisible(true);
    if (!params.isOKPressed()) {
      return;
    }
    jpegImageQuality = (params.getJPEGQuality()) / 100.0f;
    optimizeHuffmanCode = params.isOptimizeHuffmanCode();
    jpegProgressiveMode = params.isProgressiveEncoding();

    saveImage(bImage, imageType, outFileName);
  }

  /**
   * Saves buffered image into file
   *
   * @param bImage
   * @param imageType - "jpg", "png" or "bmp"
   * @param outFileName
   * @throws Exception
   */
  public static void saveImage(BufferedImage bImage, String imageType, String outFileName) throws Exception {

    if (!outFileName.endsWith("." + imageType.toUpperCase())
        && !outFileName.endsWith("." + imageType.toLowerCase())) {
      outFileName += "." + imageType.toLowerCase();
    }

    // --- Format support check
    if (!canSaveForThisPlatform.contains(imageType.toUpperCase())) {
      throw new Exception("Image type " + imageType + " is not supported for this platform");
    }

    BufferedImage imageToSave = bImage;
    ImageWriter iWriter = null;

    if (imageType.equalsIgnoreCase("JPG")
        || imageType.equalsIgnoreCase("JPEG")) { //

      Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(IMAGE_JPEG);

      if (iter == null) {
        throw new Exception("Error Saving Jpeg Image: no such image writer on the system");
      }

      while (iter.hasNext()) {
        if (iWriter == null) {
          iWriter = iter.next();
          JPEGImageWriteParam param = (JPEGImageWriteParam) iWriter.getDefaultWriteParam();
          if (param.getCompressionMode() != ImageWriteParam.MODE_EXPLICIT) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
          }
          String[] descr = param.getCompressionQualityDescriptions();

          param.setCompressionQuality(jpegImageQuality);
          param.setOptimizeHuffmanTables(optimizeHuffmanCode);
          int mode = ImageWriteParam.MODE_DISABLED;
          if (jpegProgressiveMode) {
            mode = ImageWriteParam.MODE_DEFAULT;
          }
          param.setProgressiveMode(mode);

          System.out.print("Compress qual descr: ");
          for (int i = 0; i < descr.length; i++) {
            System.out.print(" " + descr[i]);
          }
          System.out.print("\n");
          logger.info("Compression type: " + param.getCompressionType());
          logger.info("Compression quality: " + param.getCompressionQuality());

          if (bImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            imageToSave = convertImage(bImage, BufferedImage.TYPE_INT_RGB);
          }

          FileOutputStream out;
          try {
            out = new FileOutputStream(outFileName);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            iWriter.setOutput(ImageIO.createImageOutputStream(bout));
            iWriter.write(null, new IIOImage(imageToSave, null, null), param);
            byte[] data = bout.toByteArray();
            out.write(data);
            out.close();
            return;
          } catch (Exception e) {
            System.err.println("Error saving " + outFileName + e.getMessage());
            throw new Exception("Error saving " + outFileName + e.getMessage());

          }

        }
        logger.info("Writer: " + iWriter.toString());

      }

    } else if (imageType.equalsIgnoreCase(IMAGE_BMP)) {
      if (bImage.getType() == BufferedImage.TYPE_INT_ARGB) {
        imageToSave = convertImage(bImage, BufferedImage.TYPE_INT_RGB);
      }
      //filters[0].addExtension("bmp");
      //filters[0].addExtension("BMP");
      //filters[0].setDescription("BMP files (*.bmp)");
    } else if (imageType.equalsIgnoreCase(IMAGE_PNG)) {
      //filters[0].addExtension("png");
      //filters[0].addExtension("PNG");
      //filters[0].setDescription("PNG files (*.png)");
    }

    try {
      ImageIO.write(imageToSave, imageType,
          new File(outFileName));
      //new File(workingDirectory + outFileName));
    } catch (Exception ex) {
      throw new Exception("Error Saving Image: " + outFileName + " : " + ex.getMessage());
    }

  }

  public static BufferedImage convertImage(BufferedImage bImage, int imageType) {

    BufferedImage imageToSave = new BufferedImage(bImage.getWidth(),
        bImage.getHeight(),
        imageType);
    Graphics2D graphics2D = imageToSave.createGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics2D.drawImage(bImage, 0, 0, bImage.getWidth(),
        bImage.getHeight(), null);
    return imageToSave;
  }

  public static void main(String[] args) {
    ImageTools imagetools = new ImageTools();
  }

  public boolean isJpegProgressiveMode() {
    return jpegProgressiveMode;
  }

  public void setJpegProgressiveMode(boolean jpegProgressiveMode) {
    ImageTools.jpegProgressiveMode = jpegProgressiveMode;
  }

  public boolean isOptimizeHuffmanCode() {
    return optimizeHuffmanCode;
  }

  public void setOptimizeHuffmanCode(boolean optimizeHuffmanCode) {
    ImageTools.optimizeHuffmanCode = optimizeHuffmanCode;
  }

  public float getJpegImageQuality() {
    return jpegImageQuality;
  }

  public void setJpegImageQuality(float jpegImageQuality) {
    ImageTools.jpegImageQuality = jpegImageQuality;
  }
}
