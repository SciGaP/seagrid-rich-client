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

import java.util.prefs.Preferences;

import javax.swing.UIManager;

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
public class SwingLookAndFeel {

   private static String lookAndFeelKey = "preferredLookAndFeel";
   private static Preferences prefs;

   private SwingLookAndFeel() {
   }

   public static String getLookAndFeel() {
      return UIManager.getLookAndFeel().getName();
   }

   public static String[] getAvailableLookAndFeels() {
      UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
      if (inst.length < 1) {
         return null;
      }

      String looks[] = new String[inst.length];

      for (int i = 0; i < inst.length; i++) {
         looks[i] = inst[i].getName();
      }
      return looks;
   }

   public static String retrieveLookAndFeelPrefs(Class c) {
      try {
         prefs = Preferences.userNodeForPackage(c);
      }
      catch (Exception ex) {
         System.err.println("Error retrieving L&F Preferences: " + ex.getMessage());
         return UIManager.getLookAndFeel().getName();
      }

      String look = prefs.get(lookAndFeelKey, UIManager.getLookAndFeel().getName());

      String looks[] = getAvailableLookAndFeels();

      for (int i = 0; i < looks.length; i++) {
         if (look.equalsIgnoreCase(looks[i])) {
            return look;
         }
      }

      //System.err.println("Retrieving Preferences: There is no such L&F in the system: " + look);
      return UIManager.getLookAndFeel().getName();
      //return null;
   }

   public static void saveLookAndFeelPrefs(Class c) {
      try {
         prefs = Preferences.userNodeForPackage(c);
      }
      catch (Exception ex) {
         System.err.println("Error saving L&F: " +
                            ex.getMessage());
         return;
      }

      try {
         prefs.put(lookAndFeelKey, UIManager.getLookAndFeel().getName());
      }
      catch (Exception ex) {
         System.err.println("Cannot save L&F Preferences: " +
                            ex.getMessage());
      }

   }

   public static void setLookAndFeel(String lookAndFeelName) throws Exception {
      UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
      if (inst.length < 1) {
         return;
      }

      for (int i = 0; i < inst.length; i++) {
         if (lookAndFeelName.equalsIgnoreCase(inst[i].getName())) {
            try {
               UIManager.setLookAndFeel(inst[i].getClassName());
            }
            catch (Exception ex) {
               throw ex;
            }
            return ;
         }
      }

   }

   public static void main(String[] args) {
      SwingLookAndFeel swinglookandfeel = new SwingLookAndFeel();
   }
}
