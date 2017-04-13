/* ***** BEGIN LICENSE BLOCK *****
 Attribution-Noncommercial-Share Alike 3.0 Unported
 http://www.javapractices.com/home/HomeAction.do

 You are free:
 # to Share to copy, distribute and transmit the work
 # to Remix to adapt the work

 Under the following conditions:

 * Attribution. You must attribute the work in the manner specified by the author or licensor (but not in any way that suggests that they endorse you or your use of the work).

  Attribute this work:
  What does "Attribute this work" mean?
  The page you came from contained embedded licensing metadata, including how the creator wishes to be attributed for re-use. You can use the HTML here to cite the work. Doing so will also include metadata on your page so that others can find the original work as well.

 * Noncommercial. You may not use this work for commercial purposes.
 * Share Alike. If you alter, transform, or build upon this work, you may distribute the resulting work only under the same or similar license to this one.

 * For any reuse or distribution, you must make clear to others the license terms of this work. The best way to do this is with a link to this web page.
 * Any of the above conditions can be waived if you get permission from the copyright holder.
 * Nothing in this license impairs or restricts the author's moral rights.

 Modified by Dr. Vladislav Vasilyev <vvv900@gmail.com>
 ***** END LICENSE BLOCK *****/

package cct.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Logger;

public final class TextClipboard
    implements ClipboardOwner {

  static final Logger logger = Logger.getLogger(TextClipboard.class.getCanonicalName());

   public static void main(String ...aArguments) {
      TextClipboard textTransfer = new TextClipboard();

      //display what is currently on the clipboard
      try {
         logger.info("Clipboard contains:" + textTransfer.getClipboardContents());

         //change the contents and then re-display
         textTransfer.setClipboardContents("blah, blah, blah");
         logger.info("Clipboard contains:" + textTransfer.getClipboardContents());
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Empty implementation of the ClipboardOwner interface.
    */
   @Override
  public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
      //do nothing
   }

   /**
    * Place a String on the clipboard, and make this class the
    * owner of the Clipboard's contents.
    */
   public void setClipboardContents(String aString) {
      StringSelection stringSelection = new StringSelection(aString);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, this);
   }

   /**
    * Get the String residing on the clipboard.
    *
    * @return any text found on the Clipboard; if none found, return an
    * empty String.
    */
   public String getClipboardContents() throws Exception {
      String result = "";
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      //odd: the Object param of getContents is not currently used
      Transferable contents = clipboard.getContents(null);
      boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      if (hasTransferableText) {
         try {
            result = (String) contents.getTransferData(DataFlavor.stringFlavor);
         }
         catch (UnsupportedFlavorException ex) {
            //highly unlikely since we are using a standard DataFlavor
            System.err.println(ex.getMessage());
            throw ex;
         }
         catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw ex;
            //ex.printStackTrace();
         }
      }
      return result;
   }
}
