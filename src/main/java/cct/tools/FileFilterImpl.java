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

/**
 * <p>Title: Picking</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileFilterImpl
    extends javax.swing.filechooser.FileFilter {
  protected String Description = "";
  protected List Extensions = new ArrayList();

  //Accept all directories and all specified files.
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }

    String extension = getExtension(f);
    if (extension != null) {
      for (int i = 0; i < Extensions.size(); i++) {
        String ext = (String) Extensions.get(i);
        if (extension.equals(ext)) {
          return true;
        }
      }
    }
    else {
      return false;
    }

    return false;
  }

  //Accept all directories and all specified files.
  public boolean accept(String fileName, boolean isDirectory) {
    if (isDirectory) {
      return true;
    }

    String extension = getExtension(fileName);
    if (extension != null) {
      for (int i = 0; i < Extensions.size(); i++) {
        String ext = (String) Extensions.get(i);
        if (ext.equals("*")) {
          return true;
        }
        if (extension.equals(ext)) {
          return true;
        }
      }
    }
    else {
      return false;
    }

    return false;
  }

//The description of this filter
  @Override
  public String getDescription() {
    return Description;
  }

  public void setDescription(String descr) {
    Description = descr;
  }

  public static String getExtension(File f) {
    return getExtension(f.getName());
  }

  public static String getExtension(String file_name) {
    String ext = null;
    int i = file_name.lastIndexOf('.');

    if (i > 0 && i < file_name.length() - 1) {
      ext = file_name.substring(i + 1).toLowerCase();
    }
    return ext;
  }

  /**
   * Builds file filter implementations using HaspMap
   * @param fileFormats LinkedHashMap
   * @return FileFilterImpl[]
   */
  public static javax.swing.filechooser.FileFilter[] getFileFilters(Map fileFormats) {
    if (fileFormats == null || fileFormats.size() < 1) {
      return null;
    }
    FileFilterImpl[] filters = new FileFilterImpl[fileFormats.size()];

    int count = 0;
    Set set = fileFormats.entrySet();
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Map.Entry me = (Map.Entry) iter.next();
      String description = me.getKey().toString();
      String ext = me.getValue().toString();

      filters[count] = new FileFilterImpl();
      String temp[] = ext.split(";");
      for (int i = 0; i < temp.length; i++) {
        filters[count].addExtension(temp[i]);
      }
      filters[count].setDescription(description);
      ++count;
    }

    //for (int i=0; i<filters.length; i++) {
    //   logger.info("Filter: "+filters[i].getDescription() );
    //}
    return filters;
  }

  public void addExtension(String file_ext) {
    Extensions.add(file_ext);
  }
}
