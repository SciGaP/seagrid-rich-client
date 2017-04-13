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
// Ordinal-based typesafe enum
public class CCTParserEnum
    implements Comparable {
  private final String name;
  // Ordinal of next suit to be created
  private static int nextOrdinal = 0;
  // Assign an ordinal to this suit
  private final int ordinal = nextOrdinal++;
  private CCTParserEnum(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(Object o) {
    return ordinal - ( (CCTParserEnum) o).ordinal;
  }

  public int compareTo(String str) {
    return this.toString().compareTo(str);
  }

  public int compareToIgnoreCase(String str) {
    return this.toString().compareToIgnoreCase(str);
  }

  public boolean equalsIgnoreCase(String str) {
    return this.toString().equalsIgnoreCase(str);
  }

  public int getEnum() {
    return ordinal;
  }

  // CCT Tags

  public static final CCTParserEnum CCT_TAG = new CCTParserEnum("cct");
  public static final CCTParserEnum MANY_MOLECULES_TAG = new CCTParserEnum("molecules");
  public static final CCTParserEnum MOLECULE_TAG = new CCTParserEnum("molecule");
  public static final CCTParserEnum MANY_MONOMERS_TAG = new CCTParserEnum("monomers");
  public static final CCTParserEnum MONOMER_TAG = new CCTParserEnum("monomer");
  public static final CCTParserEnum MANY_ATOMS_TAG = new CCTParserEnum("atoms");
  public static final CCTParserEnum ATOM_TAG = new CCTParserEnum("atom");
  public static final CCTParserEnum MANY_BONDS_TAG = new CCTParserEnum("bonds");
  public static final CCTParserEnum BOND_TAG = new CCTParserEnum("bond");
  public static final CCTParserEnum PROPERTY_TAG = new CCTParserEnum("property");
  public static final CCTParserEnum ATOM_SET_TAG = new CCTParserEnum("atomSet");

}
