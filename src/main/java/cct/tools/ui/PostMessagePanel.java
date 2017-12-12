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

package cct.tools.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import cct.tools.Utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ANU</p>
 *
 * @author Dr. V. Vasilyev
 * @version 1.0
 */
public class PostMessagePanel
    extends JPanel {

  static final String nameKey = "name";
  static final String emailKey = "email";

  private JLabel jLabel1 = new JLabel();
  private JTextField nameTextField = new JTextField();
  private JTextArea messageTextArea = new JTextArea();
  private JLabel jLabel2 = new JLabel();
  private JTextField emailTextField = new JTextField();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();

  boolean initialized = false;
  private MyMouseAdapter mouseAdapter = null;
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JPanel jPanel1 = new JPanel();
  private BorderLayout borderLayout1 = new BorderLayout();

  private boolean nameIsOptional = true;

  public PostMessagePanel() {
    try {
      jbInit();
    }
    catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(gridBagLayout1);
    jLabel1.setToolTipText("");
    jLabel1.setText("Your Name " + (nameIsOptional ? "(Optional)" : ""));
    jLabel2.setToolTipText("");
    jLabel2.setText("Your e-mail (Optional)");
    nameTextField.setToolTipText("Your Name");
    emailTextField.setToolTipText("Your e-mail address");
    messageTextArea.setMinimumSize(new Dimension(20, 20));
    messageTextArea.setColumns(50);
    messageTextArea.setLineWrap(true);
    messageTextArea.setRows(10);
    jPanel1.setLayout(borderLayout1);
    jPanel1.setBorder(BorderFactory.createEmptyBorder());
    jScrollPane1.setBorder(new TitledBorder(BorderFactory.createLineBorder(new Color(127, 157, 185), 1), "Your Message"));
    this.add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.add(jLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                             , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.add(emailTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    jScrollPane1.getViewport().add(jPanel1);
    this.add(jScrollPane1, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
                                                  , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0,
                                                  0));
    jPanel1.add(messageTextArea, BorderLayout.CENTER);
    Font currentFont = messageTextArea.getFont();
    int size = currentFont.getSize();
    int style = currentFont.getStyle();
    String fontName = currentFont.getFontName();
    if (size < 12) {
      size = 12;
      Font newFont = new Font(fontName, style, size);
      messageTextArea.setFont(newFont);
    }

    String name = Utils.getPreference(this, nameKey);
    name = Utils.getPreference(this, emailKey);
  }

  public void savePreferences() {
    Utils.savePreference(this, emailKey, emailTextField.getText());
    Utils.savePreference(this, nameKey, nameTextField.getText());
  }

  public void setMessage(String text) {
    initialized = true;
    if (mouseAdapter != null) {
      messageTextArea.removeMouseListener(mouseAdapter);
    }
    messageTextArea.setText(text);
  }

  public void appendMessage(String text) {
    initialized = true;
    if (mouseAdapter != null) {
      messageTextArea.removeMouseListener(mouseAdapter);
    }
    messageTextArea.append(text);
  }

  public void setInitialText(String text) {
    if (initialized) {
      return;
    }
    messageTextArea.setText(text);
    mouseAdapter = new MyMouseAdapter(this);
    messageTextArea.addMouseListener(mouseAdapter);
  }

  public String getMessage() {

    if (!initialized) {
      return "";
    }
    return messageTextArea.getText();
  }

  public void messageTextArea_mouseClicked(MouseEvent e) {
    initialized = true;
    messageTextArea.setText("");
    messageTextArea.removeMouseListener(mouseAdapter);
  }

  public String getEmailAddress() {
    return emailTextField.getText().trim();
  }

  public String getUserName() {
    return nameTextField.getText().trim();
  }

  class MyMouseAdapter
      extends MouseAdapter {
    private PostMessagePanel adaptee;
    MyMouseAdapter(PostMessagePanel adaptee) {
      this.adaptee = adaptee;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      adaptee.messageTextArea_mouseClicked(e);
    }
  }

}
