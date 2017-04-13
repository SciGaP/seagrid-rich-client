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

import java.applet.Applet;
import java.awt.BorderLayout;

//import cct.amber.*;

public class AmberApplet
    extends Applet {
   boolean isStandalone = false;
   BorderLayout borderLayout1 = new BorderLayout();
   AmberSubmitDialog AmberSubmit;
   //String var0;

   //Get a parameter value
   public String getParameter(String key, String def) {
      return isStandalone ? System.getProperty(key, def) :
          (getParameter(key) != null ? getParameter(key) : def);
   }

   //Construct the applet
   public AmberApplet() {
   }

   //Initialize the applet
   @Override
  public void init() {
      /*
           try {
        var0 = this.getParameter("param0", "");
           }
           catch (Exception e) {
        e.printStackTrace();
           }
       */
      try {
         jbInit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   //Component initialization
   private void jbInit() throws Exception {
      String actionURL;
      actionURL = getParameter("actionURL");

      AmberSubmit = new AmberSubmitDialog(this, "Submit Amber Job", actionURL);
      AmberSubmit.setSize(600, 400);
      AmberSubmit.setVisible(true);

   }

   //Get Applet information
   @Override
  public String getAppletInfo() {
      return "Applet Information";
   }

   //Get parameter info
   @Override
  public String[][] getParameterInfo() {
      String[][] pinfo = {
          {
          "param0", "String", ""},
      };
      return pinfo;
   }
}
