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
package cct.j3d;

import cct.GlobalSettings;
import cct.j3d.Java3dUniverse;
import cct.povray.Povray;
import cct.tools.IOUtils;
import cct.tools.ImageTools;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dirty class at the moment...
 *
 * @author vvv900
 */
public class ImageSequenceManager {

  public static final String DEFAULT_FILE_PREFIX = "md-";
  public static final String DEFAULT_FILE_NUMBER_FORMAT = "%06d";
  private String filePrefix = DEFAULT_FILE_PREFIX;
  private String fileNumberFormat = DEFAULT_FILE_NUMBER_FORMAT;
  private Map<String, Object> availableFormats = new HashMap<String, Object>();
  private Java3dUniverse java3dUniverse = null;
  private int counter = 0;
  private int imageSequenceNumber = 0;
  private boolean useImageSeqNumber = false;

  private String outputFormat;
  private boolean sequenceInitialized = false;
  static final Logger logger = Logger.getLogger(ImageSequenceManager.class.getCanonicalName());

  public ImageSequenceManager(Java3dUniverse java3d) {
    java3dUniverse = java3d;
    outputFormat = "jpg";
    // --- get Image formats
    String formats[] = ImageTools.getSaveImageFormatNames();
    for (String f : formats) {
      availableFormats.put(f, null);
    }
    availableFormats.put("pov", null);
  }

  public Object[] getAvailableFormats() {
    return availableFormats.keySet().toArray();
  }

  public void initializeImageSequence() {
    sequenceInitialized = true;
    counter = 0;
  }

  public void finalizeImageSequence() {

    sequenceInitialized = false;
    if (this.outputFormat.equalsIgnoreCase("pov")) {

// --- Generate povray image sequence control file
      String povTemplate = GlobalSettings.getProperty(
          Povray.IMAGE_SEQ_POV_TEMPLATE_KEY,
          Povray.DEFAULT_IMAGE_SEQ_POV_TEMPLATE);
      System.out.println("Povray Pov template:\n" + povTemplate);
      povTemplate = povTemplate.replaceAll(Povray.FILE_PREFIX_TEMPLATE, this.getFilePrefix());
      try {
        IOUtils.saveStringIntoFile(povTemplate, getFilePrefix() + Povray.DEFAULT_IMAGE_SEQ_POV_FILENAME);
      } catch (Exception ex) {
        Logger.getLogger(ImageSequenceManager.class.getName()).log(Level.SEVERE, null, ex);
      }

      // --- Generate Povray initemplate
      String iniTemplate = GlobalSettings.getProperty(
          Povray.IMAGE_SEQ_INI_TEMPLATE_KEY,
          Povray.DEFAULT_IMAGE_SEQ_INI_TEMPLATE);
      System.out.println("Povray INI template:\n" + iniTemplate);
      iniTemplate = iniTemplate.replaceAll(Povray.NUMBER_OF_FRAMES_TEMPLATE, String.valueOf(counter)).
          replaceAll(Povray.POVRAY_CONTROL_FILE_NAME_TEMPLATE, getFilePrefix() + Povray.DEFAULT_IMAGE_SEQ_POV_FILENAME);

      try {
        IOUtils.saveStringIntoFile(iniTemplate, getFilePrefix() + Povray.DEFAULT_IMAGE_SEQ_INI_FILENAME);
      } catch (Exception ex) {
        Logger.getLogger(ImageSequenceManager.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    counter = 0;
  }

  public void generateImage(Java3dUniverse java3d, int seqNumber) {
    imageSequenceNumber = seqNumber;
    generateImage(java3d);
  }

  public void generateImage(Java3dUniverse java3d) {
    if (!sequenceInitialized) {
      return;
    }
    if (filePrefix == null) {
      filePrefix = "";
    }
    ++counter;
    int imageSequence = counter;
    if (useImageSeqNumber) {
      imageSequence = imageSequenceNumber;
    }
    String fileName = String.format(filePrefix + fileNumberFormat, imageSequence);

    // --- Save image
    if (this.outputFormat.equalsIgnoreCase("pov")) {
      PovrayJava3d povray = new PovrayJava3d(java3d);
      try {
        povray.savePovrayFile(fileName + ".pov");
      } catch (Exception ex) {
        logger.severe(ex.getLocalizedMessage());
        return;
      }
    } else if (this.outputFormat.equalsIgnoreCase("bmp") || outputFormat.equalsIgnoreCase("jpg")
        || outputFormat.equalsIgnoreCase("png")) {
      ImageToolsJ3D it3d = new ImageToolsJ3D(java3d);
      it3d.setImageType(outputFormat);
      try {
        it3d.saveImage(fileName);
      } catch (Exception ex) {
        logger.severe(ex.getLocalizedMessage());
        return;
      }
    }
  }

  public String getFilePrefix() {
    return filePrefix;
  }

  public void setFilePrefix(String filePrefix) {
    this.filePrefix = filePrefix;
  }

  public String getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  public String getFileNumberFormat() {
    return fileNumberFormat;
  }

  public void setFileNumberFormat(String fileNumberFormat) {
    this.fileNumberFormat = fileNumberFormat;
  }

  public boolean isUseImageSeqNumber() {
    return useImageSeqNumber;
  }

  public void setUseImageSeqNumber(boolean useImageSeqNumber) {
    this.useImageSeqNumber = useImageSeqNumber;
  }
}
