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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
public class Experimental {
  static final Logger logger = Logger.getLogger(Experimental.class.getCanonicalName());
   public Experimental() {
   }

   public static void main(String[] args) {
      Experimental experimental = new Experimental();

      try {
         File file = new File("E:/Java/Tests/PDB/pdb1cfm.ent.Z");
         //URL url = file.toURL();
         //URL url = new URL("ftp://ftp.rcsb.org/pub/pdb/data/structures/divided/pdb/cf/pdb1cfm.ent.Z");
         URL url = new URL("ftp://ftp.rcsb.org/pub/pdb/data/structures/divided/pdp/ka/pdb1kas.ent.Z");
         UncompressInputStream uis = new UncompressInputStream(url.openStream());

         //Inflater inf = new Inflater(false);
         //InflaterInputStream iis = new InflaterInputStream(url.openStream(), inf);

         //GZIPInputStream gzip = new GZIPInputStream(url.openStream());
         //ZipInputStream zis = new ZipInputStream(url.openStream());

         //DataInputStream dis = new DataInputStream( iis );
         BufferedReader in = new BufferedReader(new InputStreamReader(uis));

         String line;
         while ( (line = in.readLine()) != null) {

         }

         // crude code to unpack a zip file into element files.
         FileInputStream fis = new FileInputStream(new File(
             "E:/Java/Tests/PDB/pdb1cfm.zip"));
         ZipInputStream zip = new ZipInputStream(fis);

         // loop for each entry
         while (true) {
            ZipEntry entry = zip.getNextEntry();
            // relative name with slashes to separate dirnames.
            String elementName = entry.getName();
            File elementFile = new File("targetdir", elementName);
            // checking that subdirs exist for elementname is not shown.

            // This code won't work if ZipOutputStream
            // was used to create the zip file. ZipEntry.getSize will
            // return -1. You will have to read the element in chunks
            // or estimate a biggest possible size.
            // See http://mindprod.com/products.html#FILETRANSFER
            // Filetransfer.copy will handle the chunking and does
            // not need to know the length in advance.
            int fileLength = (int) entry.getSize();

            byte[] wholeFile = new byte[fileLength];
            int bytesRead = zip.read(wholeFile, 0, fileLength);
            // checking bytesRead, and repeating if you don't get it all is not shown.
            FileOutputStream fos = new FileOutputStream(elementFile);
            fos.write(wholeFile, 0, fileLength);
            fos.close();
            elementFile.setLastModified(entry.getTime());
            zip.closeEntry();
         }

         //zip.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      try {
         URL url = new URL(
             "http://sf.anu.edu.au/~vvv900/cct/appl/jmoleditor/download/JMolEditor.jar");
         URI uri = new URI(
             "http://sf.anu.edu.au/~vvv900/cct/appl/jmoleditor/download/JMolEditor.jar");
         File file = new File(uri);

         logger.info("Last modified " + file.lastModified());
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
