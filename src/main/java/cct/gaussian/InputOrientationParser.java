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

package cct.gaussian;

import java.io.BufferedReader;
import java.util.StringTokenizer;

import cct.interfaces.AtomInterface;
import cct.interfaces.CoordinateParserInterface;
import cct.interfaces.MoleculeInterface;
import cct.modelling.ChemicalElements;

/**
 * <p>Title: Computational Chemistry Tookit</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2010 Dr. Vladislav Vassiliev</p>
 *
 * <p>Company: ANU</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InputOrientationParser
    implements CoordinateParserInterface {
   public InputOrientationParser() {
   }

   public static void main(String[] args) {
      InputOrientationParser simpleparser = new InputOrientationParser();
   }

   @Override
  public void parseCoordinates(BufferedReader in, MoleculeInterface molecule) throws Exception {
      try {
         molecule.addMonomer("GAUSS");
         // --- Reading atoms
         String line;
         while ( (line = in.readLine()) != null) {

            line = line.trim();
            if (line.length() < 1) {
               continue; // Empty line
            }

            AtomInterface atom = molecule.getNewAtomInstance();

            //     1          7             0        0.250464    0.322510   -1.260153

            StringTokenizer st = new StringTokenizer(line, " \t", false);
            if (st.countTokens() < 6) {
               throw new Exception("InputOrientationParser: Expected at least 6 tokens, got: " + line);
            }

            // --- Skip first token

            st.nextToken();

            // --- Getting element

            String token = st.nextToken();
            try {
               int element = Integer.parseInt(token);
               atom.setAtomicNumber(element);
               atom.setName(ChemicalElements.getElementSymbol(element));

            }
            catch (Exception ex) {
               throw new Exception("Cannot parse element number: " + ex.getMessage());
            }

            // --- Skip token

            st.nextToken();

            // --- Getting x,y,z

            float xyz;
            try {
               xyz = Float.parseFloat(st.nextToken());
               atom.setX(xyz);
               xyz = Float.parseFloat(st.nextToken());
               atom.setY(xyz);
               xyz = Float.parseFloat(st.nextToken());
               atom.setZ(xyz);
            }
            catch (Exception ex) {
               throw new Exception("InputOrientationParser: Error while parsing atom's coordinate: " + line);
            }

            molecule.addAtom(atom);
         }

      }
      catch (Exception ex) {
         throw ex;
      }
   }

   @Override
  public double evaluateCompliance(BufferedReader in) throws Exception {
      double score = 0;
      try {
         // --- Reading atoms
         String line;
         while ( (line = in.readLine()) != null) {

            line = line.trim();
            if (line.length() < 1) {
               continue; // Empty line
            }

            score = 1.0;
            //     1          7             0        0.250464    0.322510   -1.260153

            StringTokenizer st = new StringTokenizer(line, " \t", false);
            if (st.countTokens() < 6) {
               return 0;
            }
            else if (st.countTokens() > 6) {
               score -= 0.25;
            }

            // --- The first token should be integer number

            try {
               Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
               score -= 0.25;
            }

            // --- Getting element

            String token = st.nextToken();
            try {
               Integer.parseInt(token);

            }
            catch (Exception ex) {
               return 0;
            }

            // --- token should be a number

            try {
               Integer.parseInt(st.nextToken());
            }
            catch (Exception ex) {
               score -= 0.25;
            }

            // --- Getting x,y,z

            try {
               Float.parseFloat(st.nextToken());
               Float.parseFloat(st.nextToken());
               Float.parseFloat(st.nextToken());
            }
            catch (Exception ex) {
               return 0;
            }

            return score; // i.e. we check only the first line
         }

      }
      catch (Exception ex) {
         throw ex;
      }
      return score;
   }
}
