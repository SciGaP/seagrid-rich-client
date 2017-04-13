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

package cct.help;

import java.net.URL;
import java.util.logging.Logger;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;

import cct.interfaces.HelperInterface;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class JavaHelp
    implements HelperInterface {
   HelpSet helpSet = null;
   HelpBroker helpBroker = null;
   static final Logger logger = Logger.getLogger(JavaHelp.class.getCanonicalName());

   boolean ok = false;

   public JavaHelp(String helpsetName) throws Exception {

      try {
         Class javaH = this.getClass().getClassLoader().loadClass(
             "javax.help.HelpSet");
      }
      catch (Exception ex) {
         System.err.println(ex.getMessage());
         throw ex;
      }


      try {
         ClassLoader cl = JavaHelp.class.getClassLoader();
         URL url = HelpSet.findHelpSet(cl, helpsetName);
         helpSet = new HelpSet(cl, url);
      }
      catch (Exception ee) {
         logger.info("Help Set " + helpsetName + " not found");
         throw ee;
         //return;
      }
      catch (ExceptionInInitializerError ex) {
         System.err.println("initialization error:");
         ex.getException().printStackTrace();
         throw ex;
         //return;
      }
      helpBroker = helpSet.createHelpBroker();
      ok = true;
   }

   /*
       public JavaHelp() {
      // Find the HelpSet file and create the HelpSet object:

      ClassLoader cl = JavaHelp.class.getClassLoader();
      //String path = "file:" + IOUtils.getRootDirectory(this) +
      //"cct/resources/JavaHelp/jmoleditor/" + helpHS;
      //logger.info("Help Set Path: " + path);
      try {
         URL hsURL = HelpSet.findHelpSet(cl, helpHS);
         //URL hsURL = new URL(path);
         hs = new HelpSet(null, hsURL);
      }
      catch (Exception ee) {
// Say what the exception really is
         logger.info("HelpSet " + ee.getMessage());
         logger.info("HelpSet " + helpHS + " not found");
         return;
      }
// Create a HelpBroker object:
      hb = hs.createHelpBroker();

       }

       public void showHelp() {

      try {
         hb.setCurrentID("toplevelfolder");
         hb.setDisplayed(true);
      }
      catch (Exception ee) {
         System.err.println("trouble with visiting id; " + ee);
      }


      try {
         Popup popup = (Popup) Popup.getPresentation(hs, null);
         popup.setInvoker(null);
         popup.setCurrentID("toplevelfolder");
         popup.setDisplayed(true);
      }
      catch (Exception ee) {
         System.err.println("trouble with visiting id; " + ee);
      }

      }
    */

   public static void main(String[] args) {
      String helpHS = "JMolEditorHelpSet.hs";
      try {
         JavaHelp javahelp = new JavaHelp(helpHS);
      }
      catch (Exception ex) {}
   }

   @Override
  public void setHelpIDString(java.awt.Component comp, String helpID) {
      if (!ok) {
         return;
      }
      CSH.setHelpIDString(comp, helpID);
   }

   @Override
  public void enableHelpKey(java.awt.Component comp, String id) {
      if (!ok) {
         return;
      }
      helpBroker.enableHelpKey(comp, id, helpSet);
   }

   @Override
  public void enableHelpKey(java.awt.Component comp, String id, String presentation, String presentationName) {
      if (!ok) {
         return;
      }
      helpBroker.enableHelpKey(comp, id, helpSet, presentation, presentationName);
   }

   @Override
  public void addActionListenerForTracking(javax.swing.AbstractButton comp) {
      if (!ok) {
         return;
      }
      comp.addActionListener(new CSH.DisplayHelpAfterTracking(helpBroker));
   }

   @Override
  public void displayHelpFromSourceActionListener(javax.swing.AbstractButton comp) {
      if (!ok) {
         return;
      }
      comp.addActionListener(new CSH.DisplayHelpFromSource(helpBroker));
   }
}
