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

package cct.modelling;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import cct.gaussian.Gaussian;
import cct.gaussian.GaussianMolecule;

public class SaveGJF {
  static final Logger logger = Logger.getLogger(SaveGJF.class.getCanonicalName());

   public SaveGJF() {
   }

   public static void saveGJF(Gaussian g, int job, String filename) {

      FileOutputStream out;
      try {
         out = new FileOutputStream(filename);
      }
      catch (java.io.FileNotFoundException e) {
         logger.info(filename + " not found\n");
         return;
      }
      catch (SecurityException e) {
         logger.info(filename + ": Security Eception\n");
         return;
      }
      catch (IOException e) {
         logger.info(filename + ": Read-only file\n");
         return;
      }

      int start = 0, end = g.getNumberOfMolecules() - 1;
      if (end < 0) {
         logger.info("saveGJF: no molecules");
         return;
      }
      if (job >= 0) {
         start = end = job;
      }

      int count = -1;
      for (int i = start; i <= end; i++) {
         ++count;
         GaussianMolecule gmol = g.getGaussianMolecule(i);

         // --- Write Link section (if any)

         if (count > 0) {
            try {
               out.write( ("--Link1--\n").getBytes());
            }
            catch (IOException e) {
               logger.info("saveGJF: Error writing Link Section");
               return;
            }

         }

         // --- Write Link 0 Commands (% lines)

         if (!gmol.writeLinkZeroCommands(out)) {
            return;
         }

         // --- Write Route Section

         if (!gmol.writeRouteSection(out)) {
            return;
         }

         // --- Write Title Section

         if (!gmol.writeTitleSection(out)) {
            return;
         }

         // --- Write Charge Section

         if (!gmol.writeChargeSection(out)) {
            return;
         }

         // --- Write Atom Coordinates

         if (!gmol.writeAtoms(out)) {
            return;
         }

      }

      // --- Finally, close a stream
      try {
         out.close();
      }
      catch (IOException e) {
         logger.info("Error closing " + filename);
         return;
      }

   }

}
