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

import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

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
public class HelpPanel
    extends JScrollPane implements HyperlinkListener {

   JEditorPane editorPane = new JEditorPane();

   public HelpPanel() {
      try {
         jbInit();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   private void jbInit() throws Exception {
      editorPane.setEditable(false);
      editorPane.addHyperlinkListener(this);
      editorPane.setText("jEditorPane1");
      this.getViewport().add(editorPane);
   }

   public void setText(URL helpURL) {
      if (helpURL != null) {
         try {
            editorPane.setPage(helpURL);
         }
         catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + helpURL);
         }
      }
      else {
         System.err.println("Couldn't find file: TextSamplerDemoHelp.html");
      }

   }

   @Override
  public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
         JEditorPane pane = (JEditorPane) e.getSource();
         if (e instanceof HTMLFrameHyperlinkEvent) {
            HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
            HTMLDocument doc = (HTMLDocument) pane.getDocument();
            doc.processHTMLFrameHyperlinkEvent(evt);
         }
         else {
            try {
               pane.setPage(e.getURL());
            }
            catch (Throwable t) {
               t.printStackTrace();
            }
         }
      }
   }

}
