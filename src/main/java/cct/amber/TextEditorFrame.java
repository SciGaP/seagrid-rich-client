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

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import cct.tools.IOUtils;

/**
 * <p>Title: Preparation of input file for Sander 8 program</p>
 *
 * <p>Description: Computational Chemistry Toolkit</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class TextEditorFrame
    extends JFrame {

   JPanel contentPane;
   JMenuBar jMenuBar1 = new JMenuBar();
   JEditorPane jEditorPane1 = new JEditorPane();
   JMenu jMenu1 = new JMenu();
   JMenuItem jMenuItem1 = new JMenuItem();
   JMenuItem jMenuItem2 = new JMenuItem();

   String fileName = null;
   String workingDirectory = null;
   BorderLayout borderLayout1 = new BorderLayout();
   String editedText = null;

   SanderInputParserInterface Parent;
   JScrollPane jScrollPane1 = new JScrollPane();

   public TextEditorFrame() {
      try {
         jbInit();
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public TextEditorFrame(SanderInputParserInterface parent, String Title, String Text) {
      try {
         Parent = parent;
         jbInit();
         this.setTitle(Title);
         jEditorPane1.setText(Text);
      }
      catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   private void jbInit() throws Exception {
      contentPane = (JPanel) getContentPane();
      contentPane.setLayout(borderLayout1);

      jEditorPane1.setText("Text to edit...");
      jMenu1.setText("File");
      jMenuItem1.setText("Save");
      jMenuItem1.addActionListener(new TextEditorFrame_jMenuItem1_actionAdapter(this));
      jMenuItem2.setText("Return");
      jMenuItem2.addActionListener(new TextEditorFrame_jMenuItem2_actionAdapter(this));
      this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      jMenuBar1.add(jMenu1);
      jMenu1.add(jMenuItem1);
      jMenu1.add(jMenuItem2);
      contentPane.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(jEditorPane1);
      setJMenuBar(jMenuBar1);
   }

   public void jMenuItem1_actionPerformed(ActionEvent e) {
      if (fileName == null) {
         fileName = "sander8.in";
      }
      if (workingDirectory == null) {
         workingDirectory = "./";
      }

      FileDialog fd = new FileDialog(this, "Save Sander 8 Job Control File",
                                     FileDialog.SAVE);
      fd.setFile(fileName);
      fd.setDirectory(workingDirectory);
      fd.setVisible(true);
      if (fd.getFile() != null) {
         fileName = new String(fd.getFile());
         workingDirectory = new String(fd.getDirectory());
         try {
            IOUtils.saveStringIntoFile(jEditorPane1.getText(),
                                       workingDirectory + fileName);
         }
         catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                                          ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);

         }
      }

   }

   public void jMenuItem2_actionPerformed(ActionEvent e) {
      setVisible(false);
      Parent.setEnabled(true);
      Parent.parseInputData(jEditorPane1.getText());
      //jEditorPane1.is
   }

   public void setTextToEdit(String text) {
      editedText = text;
      jEditorPane1.setText(text);
   }
}

class TextEditorFrame_jMenuItem2_actionAdapter
    implements ActionListener {
   private TextEditorFrame adaptee;
   TextEditorFrame_jMenuItem2_actionAdapter(TextEditorFrame adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItem2_actionPerformed(e);
   }
}

class TextEditorFrame_jMenuItem1_actionAdapter
    implements ActionListener {
   private TextEditorFrame adaptee;
   TextEditorFrame_jMenuItem1_actionAdapter(TextEditorFrame adaptee) {
      this.adaptee = adaptee;
   }

   @Override
  public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItem1_actionPerformed(e);
   }
}
