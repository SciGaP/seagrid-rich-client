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

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.util.logging.Logger;

import cct.awtdialogs.MessageWindow;

public class AmberSubmitDialog
    extends Frame {
   public String mdin = null, prmtop = null, inpcrd = null;
   public String mdin_dir = null, prmtop_dir = null, inpcrd_dir = null;
   public int numberOfAtoms = 0;
   String actionURL;
   TextArea fileContent;
   Sander8JobControl sanderJC = new Sander8JobControl();
   SanderJobControlDialog resources = null;
   SanderResourcesEval eval = null;
   AmberApplet daddy;
   static final Logger logger = Logger.getLogger(AmberSubmitDialog.class.getCanonicalName());

   public AmberSubmitDialog(AmberApplet parent, String title, String aURL) {
      super(title);

      daddy = parent;
      actionURL = aURL;
      resources = new SanderJobControlDialog(this, "Required Resources", false);
      resources.setVisible(false);

      eval = new SanderResourcesEval();

      AmberSubmitMenuHandler handler = new AmberSubmitMenuHandler(this);

      // create menu bar and add it to frame
      MenuBar mbar = new MenuBar();
      setMenuBar(mbar);

      // --- File Menu

      Menu file = new Menu("File");
      MenuItem openAJCF = new MenuItem("Open Amber Job Control File");
      openAJCF.addActionListener(handler);
      file.add(openAJCF);
      MenuItem attachPrmtop = new MenuItem("Attach Topology File");
      attachPrmtop.addActionListener(handler);
      file.add(attachPrmtop);
      MenuItem attachInpcrd = new MenuItem("Attach Coordinates File");
      attachInpcrd.addActionListener(handler);
      file.add(attachInpcrd);

      mbar.add(file);

      // --- Check Menu

      Menu check = new Menu("Check");
      MenuItem checkInput = new MenuItem("Check Input");
      checkInput.addActionListener(handler);
      check.add(checkInput);

      mbar.add(check);

      Menu Submit = new Menu("Submit");
      MenuItem sendFiles = new MenuItem("Send Files");
      sendFiles.addActionListener(handler);
      Submit.add(sendFiles);

      mbar.add(Submit);

      fileContent = new TextArea("Load Amber Job Control file here");
      add(fileContent);

   }

   public void setTextArea(String newText) {
      fileContent.setText(newText);
   }

   /**
    *
    * @return int
    * =0 - OK, =1 - Warning (OK for checkup, not for submition); =2 - Error
    */
   public int checkDataIntegrity() {
      if (mdin == null) {
         MessageWindow d = new MessageWindow("Error Message",
                                             "Load Amber Job Control File First", true);
         d.setVisible(true);
         return 2;
      }

      String mess = sanderJC.getGeneralParameters(fileContent.getText(), 1);
      //if (!mess.contentEquals("Ok")) { // Java 1.5
      if (!mess.equalsIgnoreCase("Ok")) {
         MessageWindow d = new MessageWindow("Error Message", mess, true);
         d.setVisible(true);
         return 2;
      }

      int natoms = 0;
      if (inpcrd != null) {
         natoms = AmberUtilities.getNAtomsFromCoordFile(inpcrd_dir + inpcrd);
      }

      sanderJC.setNumberOfAtoms(natoms);

      float memory = eval.evaluateMemoryRequirement(sanderJC);
      float time = eval.evaluateTimeRequirement(sanderJC);

      resources.setNumberOfAtoms(eval.getNumberOfAtoms());
      resources.setNumberOfSteps(sanderJC.numberOfEnergySteps());
      resources.setCutoff(sanderJC.getCutoff());
      resources.setMemoryRequirement(memory);
      resources.setTimeRequirement(time);

      resources.setVisible(true);
      return 0; // -- Ok
   }

   public int sendFiles() {
      try {
         java.net.URL url = new java.net.URL(actionURL + "&exec=/opt/amber8/exe/sander" +
                                             "&args=%20-O%20-i%20" + mdin + "%20-o%20mdout%20-p%20" + prmtop + "%20-c%20" + inpcrd +
                                             "&mdin=" + mdin_dir + mdin +
                                             "&inpcrd=" + inpcrd_dir + inpcrd + "&prmtop=" +
                                             prmtop_dir + prmtop + "&step=1a&jobname=amber_calc");

         daddy.getAppletContext().showDocument(url, "_top");
         logger.info(url.toString());
         logger.info("daddy's CodeBase" + daddy.getCodeBase());
         logger.info("daddy's DocumentBase" + daddy.getDocumentBase());
      }
      catch (java.net.MalformedURLException e) {
         e.printStackTrace();
      }

      return 0;
   }
}
