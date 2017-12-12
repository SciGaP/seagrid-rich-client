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

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Dr. Vladislav Vasyliev
 * Multiplies half-cycle frames into a long predefined sequence
 */
public class MultiplyFrames implements Comparator<File> {

  void doInteractively() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    int result = fileChooser.showOpenDialog(null);

    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File[] files = fileChooser.getSelectedFiles();
    if (files.length < 1) {
      System.err.println("No files selected");
      return;
    }
    System.out.println(files.length + " files selected");
    Arrays.sort(files, this);
    //
    File currentDir = fileChooser.getCurrentDirectory();
    System.out.println("Current working directory: " + currentDir.getAbsolutePath());
    System.setProperty("user.dir", fileChooser.getCurrentDirectory().getAbsolutePath());

    Object obj = JOptionPane.showInputDialog(new Frame(), "Number of repeats", "", JOptionPane.INFORMATION_MESSAGE,
            null, null, "1");
    if (obj == null) {
      return;
    }
    int repeat = 1;
    try {
      repeat = Integer.parseInt(obj.toString());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Wrong number "
              + obj.toString() + " : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (repeat < 1) {
      System.err.println("Number of repeats should be positive number");
      return;
    }
    // ----
    Path[] sources = new Path[files.length];
    for (int i = 0; i < files.length; i++) {
      Path source = FileSystems.getDefault().getPath("./", files[i].getName());
      sources[i] = source;
      Path target = FileSystems.getDefault().getPath("./", String.format("%06d", i) + ".jpg");
      try {
        Files.copy(source, target,StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException ex) {
        Logger.getLogger(MultiplyFrames.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    // ---
    int pointer = files.length - 1;
    int sign = -1;
    for (int frame = files.length; frame < files.length * repeat; frame++) {
      if (pointer == files.length - 1) {
        sign = -1;
      } else if (pointer == 0) {
        sign = 1;
      }
      pointer += sign;
      Path target = FileSystems.getDefault().getPath("./", String.format("%06d", frame) + ".jpg");

      try {
        Files.copy(sources[pointer], target,StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException ex) {
        Logger.getLogger(MultiplyFrames.class.getName()).log(Level.SEVERE, null, ex);
      }

    }

  }

  public static void main(String[] args) {
    boolean interactively = true;
    MultiplyFrames mf = new MultiplyFrames();
    for (String arg : args) {
      if (arg.equalsIgnoreCase("-gui")) {
        interactively = true;
        continue;
      }
    }
    // ---

    if (interactively) {
      mf.doInteractively();
    } else {

    }
    System.exit(0);
  }

  public int compare(File o1, File o2) {
    return o1.getName().compareTo(o2.getName());
  }

  public boolean equals(Object obj) {
    return this == obj;
  }
}
