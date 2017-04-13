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

package cct.amber;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import cct.tools.IOUtils;

public class AmberSubmitMenuHandler
    implements ActionListener, ItemListener {
   AmberSubmitDialog menuFrame;
   String fileName, directory;
   static final Logger logger = Logger.getLogger(AmberSubmitMenuHandler.class.getCanonicalName());

   public AmberSubmitMenuHandler(AmberSubmitDialog parent) {
      this.menuFrame = parent;
   }

   // Handle action events
   @Override
  public void actionPerformed(ActionEvent ae) {
      //String msg = "You selected ";
      String arg = ae.getActionCommand();
      if (arg.equals("New...")) {

      }
      else if (arg.equals("Open Amber Job Control File")) {
         FileDialog fd = new FileDialog(menuFrame, "Open File",
                                        FileDialog.LOAD);
//          FilenameFilter x; x.
         fd.setFile("*");
         fd.setVisible(true);
         if (fd.getFile() != null) {
            fileName = new String(fd.getFile());
            directory = new String(fd.getDirectory());
            String fileContents = IOUtils.loadFileIntoString(directory + fileName);
            menuFrame.setTextArea(fileContents);
            menuFrame.mdin = fileName;
            menuFrame.mdin_dir = directory;
         }
      }
      else if (arg.equals("Attach Topology File")) {
         FileDialog fd = new FileDialog(menuFrame, "Specify Molecular Topology File",
                                        FileDialog.LOAD);
         fd.setFile("*");
         fd.setVisible(true);
         if (fd.getFile() != null) {
            fileName = new String(fd.getFile());
            directory = new String(fd.getDirectory());
            menuFrame.prmtop = fileName;
            menuFrame.prmtop_dir = directory;
         }

      }
      else if (arg.equals("Attach Coordinates File")) {
         FileDialog fd = new FileDialog(menuFrame, "Specify Coordinates File",
                                        FileDialog.LOAD);
         fd.setFile("*");
         fd.setVisible(true);
         if (fd.getFile() != null) {
            fileName = fd.getFile();
            directory = fd.getDirectory();
            menuFrame.inpcrd = fileName;
            menuFrame.inpcrd_dir = directory;
            logger.info("Coord file: " + directory + fileName);
         }
      }
      else if (arg.equals("Check Input")) {
         menuFrame.checkDataIntegrity();
      }
      else if (arg.equals("Send Files")) {
         menuFrame.sendFiles();
      }

      menuFrame.repaint();
   }

   // Handle item events
   @Override
  public void itemStateChanged(ItemEvent ie) {
      menuFrame.repaint();
   }
}
